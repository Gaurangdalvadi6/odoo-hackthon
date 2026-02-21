package com.fleetflow.service;

import com.fleetflow.entity.Driver;
import com.fleetflow.entity.MaintenanceLog;
import com.fleetflow.entity.Trip;
import com.fleetflow.entity.Vehicle;
import com.fleetflow.enums.MaintenanceStatus;
import com.fleetflow.repository.DriverRepository;
import com.fleetflow.repository.MaintenanceLogRepository;
import com.fleetflow.repository.TripRepository;
import com.fleetflow.repository.VehicleRepository;
import com.lowagie.text.Chunk;
import com.lowagie.text.Document;
import com.lowagie.text.DocumentException;
import com.lowagie.text.FontFactory;
import com.lowagie.text.PageSize;
import com.lowagie.text.Paragraph;
import com.lowagie.text.Phrase;
import com.lowagie.text.pdf.PdfPCell;
import com.lowagie.text.pdf.PdfPTable;
import com.lowagie.text.pdf.PdfWriter;
import lombok.RequiredArgsConstructor;
import org.springframework.stereotype.Service;

import java.io.ByteArrayInputStream;
import java.io.ByteArrayOutputStream;
import java.io.PrintWriter;
import java.nio.charset.StandardCharsets;
import java.time.LocalDate;
import java.time.LocalDateTime;
import java.time.YearMonth;
import java.time.format.DateTimeFormatter;
import java.util.List;

@Service
@RequiredArgsConstructor
public class ExportServiceImpl implements ExportService {

    private static final DateTimeFormatter DATE_FORMAT = DateTimeFormatter.ofPattern("yyyy-MM-dd");
    private static final DateTimeFormatter DATETIME_FORMAT = DateTimeFormatter.ofPattern("yyyy-MM-dd HH:mm");

    private final VehicleRepository vehicleRepo;
    private final TripRepository tripRepo;
    private final DriverRepository driverRepo;
    private final MaintenanceLogRepository maintenanceRepo;

    @Override
    public ByteArrayInputStream exportVehiclesCsv() {
        List<Vehicle> vehicles = vehicleRepo.findAll();
        return writeCsv(
                "Id,Model,Type,Region,License Plate,Max Capacity,Odometer,Status,Acquisition Cost,Created At",
                vehicles,
                v -> String.format("%d,\"%s\",%s,\"%s\",\"%s\",%.2f,%.2f,%s,%.2f,%s",
                        v.getId(), escape(v.getModel()), v.getType() != null ? v.getType() : "", escape(v.getRegion()),
                        escape(v.getLicensePlate()), v.getMaxCapacity(), v.getOdometer() != null ? v.getOdometer() : 0,
                        v.getStatus(), v.getAcquisitionCost() != null ? v.getAcquisitionCost() : 0,
                        v.getCreatedAt() != null ? v.getCreatedAt().format(DATETIME_FORMAT) : "")
        );
    }

    @Override
    public ByteArrayInputStream exportTripsCsv() {
        List<Trip> trips = tripRepo.findAll();
        return writeCsv(
                "Id,Vehicle,Driver,Origin,Destination,Cargo Weight,Revenue,Status,Start Time,End Time,Distance",
                trips,
                t -> String.format("%d,\"%s\",\"%s\",\"%s\",\"%s\",%.2f,%.2f,%s,%s,%s,%.2f",
                        t.getId(), escape(t.getVehicle().getLicensePlate()), escape(t.getDriver().getName()),
                        escape(t.getOrigin()), escape(t.getDestination()),
                        t.getCargoWeight(), t.getRevenue() != null ? t.getRevenue() : 0,
                        t.getStatus(),
                        t.getStartTime() != null ? t.getStartTime().format(DATETIME_FORMAT) : "",
                        t.getEndTime() != null ? t.getEndTime().format(DATETIME_FORMAT) : "",
                        t.getDistance() != null ? t.getDistance() : 0)
        );
    }

    @Override
    public ByteArrayInputStream exportDriversCsv() {
        List<Driver> drivers = driverRepo.findAll();
        return writeCsv(
                "Id,Name,License Number,License Expiry,License Category,Status,Safety Score",
                drivers,
                d -> String.format("%d,\"%s\",\"%s\",%s,%s,%s,%.2f",
                        d.getId(), escape(d.getName()), escape(d.getLicenseNumber()),
                        d.getLicenseExpiry() != null ? d.getLicenseExpiry().format(DATE_FORMAT) : "",
                        d.getLicenseCategory(), d.getStatus(), d.getSafetyScore() != null ? d.getSafetyScore() : 0)
        );
    }

    @Override
    public ByteArrayInputStream exportVehiclesPdf() {
        List<Vehicle> vehicles = vehicleRepo.findAll();
        return createPdf("Vehicle Registry", new String[]{"ID", "Model", "Type", "License", "Status", "Odometer"},
                vehicles, v -> new String[]{
                        String.valueOf(v.getId()), v.getModel(), String.valueOf(v.getType()),
                        v.getLicensePlate(), String.valueOf(v.getStatus()),
                        v.getOdometer() != null ? String.format("%.0f", v.getOdometer()) : "-"
                });
    }

    @Override
    public ByteArrayInputStream exportTripsPdf() {
        List<Trip> trips = tripRepo.findAll();
        return createPdf("Trip Log", new String[]{"ID", "Vehicle", "Driver", "Origin", "Destination", "Status"},
                trips, t -> new String[]{
                        String.valueOf(t.getId()), t.getVehicle().getLicensePlate(), t.getDriver().getName(),
                        t.getOrigin() != null ? t.getOrigin() : "-", t.getDestination() != null ? t.getDestination() : "-",
                        String.valueOf(t.getStatus())
                });
    }

    @Override
    public ByteArrayInputStream exportDriversPdf() {
        List<Driver> drivers = driverRepo.findAll();
        return createPdf("Driver Registry", new String[]{"ID", "Name", "License", "Expiry", "Status"},
                drivers, d -> new String[]{
                        String.valueOf(d.getId()), d.getName(), d.getLicenseNumber(),
                        d.getLicenseExpiry() != null ? d.getLicenseExpiry().format(DATE_FORMAT) : "-",
                        String.valueOf(d.getStatus())
                });
    }

    @Override
    public ByteArrayInputStream exportMonthlyPayrollCsv(int year, int month) {
        YearMonth ym = YearMonth.of(year, month);
        LocalDateTime start = ym.atDay(1).atStartOfDay();
        LocalDateTime end = ym.plusMonths(1).atDay(1).atStartOfDay();

        StringBuilder csv = new StringBuilder("Driver ID,Driver Name,Trips Completed,Total Revenue\n");
        for (Driver d : driverRepo.findAll()) {
            List<Trip> trips = tripRepo.findCompletedByDriverAndDateRange(d.getId(), start, end);
            double revenue = trips.stream().mapToDouble(t -> t.getRevenue() != null ? t.getRevenue() : 0).sum();
            csv.append(String.format("%d,\"%s\",%d,%.2f\n", d.getId(), escape(d.getName()), trips.size(), revenue));
        }
        return new ByteArrayInputStream(csv.toString().getBytes(StandardCharsets.UTF_8));
    }

    @Override
    public ByteArrayInputStream exportMonthlyPayrollPdf(int year, int month) {
        YearMonth ym = YearMonth.of(year, month);
        LocalDateTime start = ym.atDay(1).atStartOfDay();
        LocalDateTime end = ym.plusMonths(1).atDay(1).atStartOfDay();

        ByteArrayOutputStream out = new ByteArrayOutputStream();
        try {
            Document doc = new Document(PageSize.A4);
            PdfWriter.getInstance(doc, out);
            doc.open();
            doc.add(new Paragraph("Monthly Payroll Report - " + ym, FontFactory.getFont(FontFactory.HELVETICA_BOLD, 16)));
            doc.add(Chunk.NEWLINE);

            PdfPTable table = new PdfPTable(4);
            table.setWidths(new float[]{1, 3, 2, 2});
            table.setHeaderRows(1);
            addTableHeader(table, "Driver ID", "Driver Name", "Trips Completed", "Total Revenue");

            for (Driver d : driverRepo.findAll()) {
                List<Trip> trips = tripRepo.findCompletedByDriverAndDateRange(d.getId(), start, end);
                double revenue = trips.stream().mapToDouble(t -> t.getRevenue() != null ? t.getRevenue() : 0).sum();
                table.addCell(String.valueOf(d.getId()));
                table.addCell(d.getName());
                table.addCell(String.valueOf(trips.size()));
                table.addCell(String.format("%.2f", revenue));
            }
            doc.add(table);
            doc.close();
        } catch (DocumentException e) {
            throw new RuntimeException("PDF generation failed", e);
        }
        return new ByteArrayInputStream(out.toByteArray());
    }

    @Override
    public ByteArrayInputStream exportHealthAuditCsv() {
        StringBuilder csv = new StringBuilder();
        csv.append("=== DRIVER LICENSE COMPLIANCE ===\n");
        csv.append("ID,Name,License Expiry,Status,Expired/Expiring Soon\n");
        LocalDate now = LocalDate.now();
        LocalDate warning = now.plusDays(30);
        for (Driver d : driverRepo.findAll()) {
            boolean expired = d.getLicenseExpiry() != null && d.getLicenseExpiry().isBefore(now);
            boolean expiring = d.getLicenseExpiry() != null && !d.getLicenseExpiry().isBefore(now) && d.getLicenseExpiry().isBefore(warning);
            csv.append(String.format("%d,\"%s\",%s,%s,%s\n",
                    d.getId(), escape(d.getName()),
                    d.getLicenseExpiry() != null ? d.getLicenseExpiry().format(DATE_FORMAT) : "-",
                    d.getStatus(),
                    expired ? "EXPIRED" : (expiring ? "EXPIRING SOON" : "OK")));
        }
        csv.append("\n=== VEHICLE MAINTENANCE STATUS ===\n");
        csv.append("ID,License Plate,Status,Open Maintenance Count\n");
        for (Vehicle v : vehicleRepo.findAll()) {
            long openCount = maintenanceRepo.findByVehicleId(v.getId()).stream()
                    .filter(m -> m.getStatus() == MaintenanceStatus.OPEN).count();
            csv.append(String.format("%d,\"%s\",%s,%d\n", v.getId(), escape(v.getLicensePlate()), v.getStatus(), openCount));
        }
        return new ByteArrayInputStream(csv.toString().getBytes(StandardCharsets.UTF_8));
    }

    @Override
    public ByteArrayInputStream exportHealthAuditPdf() {
        ByteArrayOutputStream out = new ByteArrayOutputStream();
        try {
            Document doc = new Document(PageSize.A4);
            PdfWriter.getInstance(doc, out);
            doc.open();

            doc.add(new Paragraph("Fleet Health Audit Report", FontFactory.getFont(FontFactory.HELVETICA_BOLD, 16)));
            doc.add(new Paragraph("Generated: " + LocalDate.now(), FontFactory.getFont(FontFactory.HELVETICA, 10)));
            doc.add(Chunk.NEWLINE);

            doc.add(new Paragraph("Driver License Compliance", FontFactory.getFont(FontFactory.HELVETICA_BOLD, 12)));
            PdfPTable driverTable = new PdfPTable(4);
            driverTable.setHeaderRows(1);
            addTableHeader(driverTable, "ID", "Name", "License Expiry", "Status");
            LocalDate now = LocalDate.now();
            for (Driver d : driverRepo.findAll()) {
                driverTable.addCell(String.valueOf(d.getId()));
                driverTable.addCell(d.getName());
                driverTable.addCell(d.getLicenseExpiry() != null ? d.getLicenseExpiry().format(DATE_FORMAT) : "-");
                driverTable.addCell(String.valueOf(d.getStatus()));
            }
            doc.add(driverTable);
            doc.add(Chunk.NEWLINE);

            doc.add(new Paragraph("Vehicle Maintenance Status", FontFactory.getFont(FontFactory.HELVETICA_BOLD, 12)));
            PdfPTable vehicleTable = new PdfPTable(4);
            vehicleTable.setHeaderRows(1);
            addTableHeader(vehicleTable, "ID", "License Plate", "Status", "Open Maintenance");
            for (Vehicle v : vehicleRepo.findAll()) {
                long openCount = maintenanceRepo.findByVehicleId(v.getId()).stream()
                        .filter(m -> m.getStatus() == MaintenanceStatus.OPEN).count();
                vehicleTable.addCell(String.valueOf(v.getId()));
                vehicleTable.addCell(v.getLicensePlate());
                vehicleTable.addCell(String.valueOf(v.getStatus()));
                vehicleTable.addCell(String.valueOf(openCount));
            }
            doc.add(vehicleTable);
            doc.close();
        } catch (DocumentException e) {
            throw new RuntimeException("PDF generation failed", e);
        }
        return new ByteArrayInputStream(out.toByteArray());
    }

    private void addTableHeader(PdfPTable table, String... headers) {
        for (String h : headers) {
            PdfPCell cell = new PdfPCell(new Phrase(h, FontFactory.getFont(FontFactory.HELVETICA_BOLD)));
            table.addCell(cell);
        }
    }

    private <T> ByteArrayInputStream createPdf(String title, String[] headers, List<T> items, java.util.function.Function<T, String[]> rowMapper) {
        ByteArrayOutputStream out = new ByteArrayOutputStream();
        try {
            Document doc = new Document(PageSize.A4.rotate());
            PdfWriter.getInstance(doc, out);
            doc.open();
            doc.add(new Paragraph(title, FontFactory.getFont(FontFactory.HELVETICA_BOLD, 16)));
            doc.add(Chunk.NEWLINE);

            PdfPTable table = new PdfPTable(headers.length);
            table.setWidthPercentage(100);
            table.setHeaderRows(1);
            for (String h : headers) {
                PdfPCell cell = new PdfPCell(new Phrase(h, FontFactory.getFont(FontFactory.HELVETICA_BOLD)));
                table.addCell(cell);
            }
            for (T item : items) {
                for (String cell : rowMapper.apply(item)) {
                    table.addCell(cell);
                }
            }
            doc.add(table);
            doc.close();
        } catch (DocumentException e) {
            throw new RuntimeException("PDF generation failed", e);
        }
        return new ByteArrayInputStream(out.toByteArray());
    }

    private <T> ByteArrayInputStream writeCsv(String header, List<T> items, java.util.function.Function<T, String> rowMapper) {
        ByteArrayOutputStream out = new ByteArrayOutputStream();
        try (PrintWriter writer = new PrintWriter(out, true, StandardCharsets.UTF_8)) {
            writer.println(header);
            for (T item : items) {
                writer.println(rowMapper.apply(item));
            }
        }
        return new ByteArrayInputStream(out.toByteArray());
    }

    private String escape(String s) {
        return s != null ? s.replace("\"", "\"\"") : "";
    }
}
