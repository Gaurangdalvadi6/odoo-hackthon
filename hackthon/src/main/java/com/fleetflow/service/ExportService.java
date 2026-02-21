package com.fleetflow.service;

import java.io.ByteArrayInputStream;

public interface ExportService {

    ByteArrayInputStream exportVehiclesCsv();
    ByteArrayInputStream exportTripsCsv();
    ByteArrayInputStream exportDriversCsv();

    ByteArrayInputStream exportVehiclesPdf();
    ByteArrayInputStream exportTripsPdf();
    ByteArrayInputStream exportDriversPdf();

    ByteArrayInputStream exportMonthlyPayrollCsv(int year, int month);
    ByteArrayInputStream exportMonthlyPayrollPdf(int year, int month);

    ByteArrayInputStream exportHealthAuditCsv();
    ByteArrayInputStream exportHealthAuditPdf();
}
