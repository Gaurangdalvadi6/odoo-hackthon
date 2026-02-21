package com.fleetflow.controller;

import com.fleetflow.service.ExportService;
import lombok.RequiredArgsConstructor;
import org.springframework.http.HttpHeaders;
import org.springframework.http.MediaType;
import org.springframework.http.ResponseEntity;
import org.springframework.security.access.prepost.PreAuthorize;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RequestParam;
import org.springframework.web.bind.annotation.RestController;

import java.io.ByteArrayInputStream;

@RestController
@RequestMapping("/api/export")
@RequiredArgsConstructor
public class ExportController {

    private final ExportService exportService;

    @PreAuthorize("isAuthenticated()")
    @GetMapping("/vehicles/csv")
    public ResponseEntity<byte[]> exportVehiclesCsv() {
        ByteArrayInputStream stream = exportService.exportVehiclesCsv();
        byte[] bytes = stream.readAllBytes();
        return ResponseEntity.ok()
                .header(HttpHeaders.CONTENT_DISPOSITION, "attachment; filename=vehicles.csv")
                .contentType(MediaType.parseMediaType("text/csv"))
                .body(bytes);
    }

    @PreAuthorize("isAuthenticated()")
    @GetMapping("/trips/csv")
    public ResponseEntity<byte[]> exportTripsCsv() {
        ByteArrayInputStream stream = exportService.exportTripsCsv();
        byte[] bytes = stream.readAllBytes();
        return ResponseEntity.ok()
                .header(HttpHeaders.CONTENT_DISPOSITION, "attachment; filename=trips.csv")
                .contentType(MediaType.parseMediaType("text/csv"))
                .body(bytes);
    }

    @PreAuthorize("isAuthenticated()")
    @GetMapping("/drivers/csv")
    public ResponseEntity<byte[]> exportDriversCsv() {
        ByteArrayInputStream stream = exportService.exportDriversCsv();
        byte[] bytes = stream.readAllBytes();
        return ResponseEntity.ok()
                .header(HttpHeaders.CONTENT_DISPOSITION, "attachment; filename=drivers.csv")
                .contentType(MediaType.parseMediaType("text/csv"))
                .body(bytes);
    }

    @PreAuthorize("isAuthenticated()")
    @GetMapping("/vehicles/pdf")
    public ResponseEntity<byte[]> exportVehiclesPdf() {
        return exportPdf(exportService.exportVehiclesPdf(), "vehicles.pdf");
    }

    @PreAuthorize("isAuthenticated()")
    @GetMapping("/trips/pdf")
    public ResponseEntity<byte[]> exportTripsPdf() {
        return exportPdf(exportService.exportTripsPdf(), "trips.pdf");
    }

    @PreAuthorize("isAuthenticated()")
    @GetMapping("/drivers/pdf")
    public ResponseEntity<byte[]> exportDriversPdf() {
        return exportPdf(exportService.exportDriversPdf(), "drivers.pdf");
    }

    @PreAuthorize("isAuthenticated()")
    @GetMapping("/payroll/csv")
    public ResponseEntity<byte[]> exportMonthlyPayrollCsv(
            @RequestParam int year,
            @RequestParam int month) {
        ByteArrayInputStream stream = exportService.exportMonthlyPayrollCsv(year, month);
        byte[] bytes = stream.readAllBytes();
        return ResponseEntity.ok()
                .header(HttpHeaders.CONTENT_DISPOSITION, "attachment; filename=payroll-" + year + "-" + month + ".csv")
                .contentType(MediaType.parseMediaType("text/csv"))
                .body(bytes);
    }

    @PreAuthorize("isAuthenticated()")
    @GetMapping("/payroll/pdf")
    public ResponseEntity<byte[]> exportMonthlyPayrollPdf(
            @RequestParam int year,
            @RequestParam int month) {
        return exportPdf(exportService.exportMonthlyPayrollPdf(year, month), "payroll-" + year + "-" + month + ".pdf");
    }

    @PreAuthorize("isAuthenticated()")
    @GetMapping("/health-audit/csv")
    public ResponseEntity<byte[]> exportHealthAuditCsv() {
        ByteArrayInputStream stream = exportService.exportHealthAuditCsv();
        byte[] bytes = stream.readAllBytes();
        return ResponseEntity.ok()
                .header(HttpHeaders.CONTENT_DISPOSITION, "attachment; filename=health-audit.csv")
                .contentType(MediaType.parseMediaType("text/csv"))
                .body(bytes);
    }

    @PreAuthorize("isAuthenticated()")
    @GetMapping("/health-audit/pdf")
    public ResponseEntity<byte[]> exportHealthAuditPdf() {
        return exportPdf(exportService.exportHealthAuditPdf(), "health-audit.pdf");
    }

    private ResponseEntity<byte[]> exportPdf(ByteArrayInputStream stream, String filename) {
        byte[] bytes = stream.readAllBytes();
        return ResponseEntity.ok()
                .header(HttpHeaders.CONTENT_DISPOSITION, "attachment; filename=" + filename)
                .contentType(MediaType.APPLICATION_PDF)
                .body(bytes);
    }
}
