import React, { useState, useEffect } from 'react';
import { analytics, exportApi, downloadExport, vehicles as vehiclesApi } from '../api/client';
import './Analytics.css';

export function Analytics() {
  const [vehicles, setVehicles] = useState([]);
  const [selectedVehicleId, setSelectedVehicleId] = useState(null);
  const [driverId, setDriverId] = useState('');
  const [vehicleMetrics, setVehicleMetrics] = useState(null);
  const [driverMetrics, setDriverMetrics] = useState(null);
  const [loading, setLoading] = useState(false);
  const [error, setError] = useState('');
  const [exportYear, setExportYear] = useState(new Date().getFullYear());
  const [exportMonth, setExportMonth] = useState(new Date().getMonth() + 1);
  const [exporting, setExporting] = useState('');

  useEffect(() => {
    vehiclesApi.list().then(setVehicles).catch(() => setVehicles([]));
  }, []);

  useEffect(() => {
    if (!selectedVehicleId) {
      setVehicleMetrics(null);
      return;
    }
    setLoading(true);
    setError('');
    Promise.all([
      analytics.vehicleProfit(selectedVehicleId),
      analytics.vehicleCostPerKm(selectedVehicleId),
      analytics.vehicleFuelEfficiency(selectedVehicleId),
      analytics.vehicleROI(selectedVehicleId),
      analytics.vehicleOperationalCost(selectedVehicleId),
    ])
      .then(([profit, costPerKm, fuelEff, roi, opCost]) => {
        setVehicleMetrics({ profit, costPerKm, fuelEff, roi, opCost });
      })
      .catch((e) => setError(e.message))
      .finally(() => setLoading(false));
  }, [selectedVehicleId]);

  const loadDriverMetrics = () => {
    if (!driverId) return;
    setLoading(true);
    setError('');
    analytics
      .driverCompletionRate(Number(driverId))
      .then(setDriverMetrics)
      .catch((e) => {
        setError(e.message);
        setDriverMetrics(null);
      })
      .finally(() => setLoading(false));
  };

  const doExport = (name, fn) => {
    setExporting(name);
    fn().finally(() => setExporting(''));
  };

  return (
    <>
      <div className="page-header">
        <h1>Operational Analytics & Reports</h1>
        <p>Fuel efficiency, vehicle ROI, cost-per-km, and one-click exports</p>
      </div>

      {error && <div className="error-msg">{error}</div>}

      <section className="analytics-section card">
        <h2>Exports</h2>
        <div className="export-grid">
          <div className="export-group">
            <h3>CSV</h3>
            <button type="button" className="btn btn--secondary btn--sm" disabled={!!exporting} onClick={() => doExport('v-csv', () => downloadExport(exportApi.vehiclesCsv(), 'vehicles.csv'))}>
              Vehicles
            </button>
            <button type="button" className="btn btn--secondary btn--sm" disabled={!!exporting} onClick={() => doExport('t-csv', () => downloadExport(exportApi.tripsCsv(), 'trips.csv'))}>
              Trips
            </button>
            <button type="button" className="btn btn--secondary btn--sm" disabled={!!exporting} onClick={() => doExport('d-csv', () => downloadExport(exportApi.driversCsv(), 'drivers.csv'))}>
              Drivers
            </button>
          </div>
          <div className="export-group">
            <h3>PDF</h3>
            <button type="button" className="btn btn--secondary btn--sm" disabled={!!exporting} onClick={() => doExport('v-pdf', () => downloadExport(exportApi.vehiclesPdf(), 'vehicles.pdf'))}>
              Vehicles
            </button>
            <button type="button" className="btn btn--secondary btn--sm" disabled={!!exporting} onClick={() => doExport('t-pdf', () => downloadExport(exportApi.tripsPdf(), 'trips.pdf'))}>
              Trips
            </button>
            <button type="button" className="btn btn--secondary btn--sm" disabled={!!exporting} onClick={() => doExport('d-pdf', () => downloadExport(exportApi.driversPdf(), 'drivers.pdf'))}>
              Drivers
            </button>
          </div>
          <div className="export-group">
            <h3>Monthly payroll</h3>
            <div className="export-row">
              <input type="number" min="1" max="12" value={exportMonth} onChange={(e) => setExportMonth(Number(e.target.value))} style={{ width: 60 }} />
              <input type="number" value={exportYear} onChange={(e) => setExportYear(Number(e.target.value))} style={{ width: 80 }} />
            </div>
            <button type="button" className="btn btn--secondary btn--sm" disabled={!!exporting} onClick={() => doExport('pay-csv', () => downloadExport(exportApi.payrollCsv(exportYear, exportMonth), `payroll-${exportYear}-${exportMonth}.csv`))}>
              CSV
            </button>
            <button type="button" className="btn btn--secondary btn--sm" disabled={!!exporting} onClick={() => doExport('pay-pdf', () => downloadExport(exportApi.payrollPdf(exportYear, exportMonth), `payroll-${exportYear}-${exportMonth}.pdf`))}>
              PDF
            </button>
          </div>
          <div className="export-group">
            <h3>Health audit</h3>
            <button type="button" className="btn btn--secondary btn--sm" disabled={!!exporting} onClick={() => doExport('h-csv', () => downloadExport(exportApi.healthAuditCsv(), 'health-audit.csv'))}>
              CSV
            </button>
            <button type="button" className="btn btn--secondary btn--sm" disabled={!!exporting} onClick={() => doExport('h-pdf', () => downloadExport(exportApi.healthAuditPdf(), 'health-audit.pdf'))}>
              PDF
            </button>
          </div>
        </div>
      </section>

      <section className="analytics-section card">
        <h2>Vehicle metrics</h2>
        <div className="form-group" style={{ maxWidth: 280 }}>
          <label>Select vehicle</label>
          <select value={selectedVehicleId || ''} onChange={(e) => setSelectedVehicleId(e.target.value ? Number(e.target.value) : null)}>
            <option value="">—</option>
            {vehicles.map((v) => (
              <option key={v.id} value={v.id}>{v.licensePlate} – {v.model}</option>
            ))}
          </select>
        </div>
        {loading && selectedVehicleId && <p>Loading…</p>}
        {vehicleMetrics && (
          <div className="metrics-grid">
            <div className="metric-card">
              <span className="metric-value">{vehicleMetrics.profit != null ? Number(vehicleMetrics.profit).toFixed(2) : '—'}</span>
              <span className="metric-label">Profit (Revenue − Fuel − Maintenance)</span>
            </div>
            <div className="metric-card">
              <span className="metric-value">{vehicleMetrics.costPerKm != null ? Number(vehicleMetrics.costPerKm).toFixed(2) : '—'}</span>
              <span className="metric-label">Cost per km</span>
            </div>
            <div className="metric-card">
              <span className="metric-value">{vehicleMetrics.fuelEff != null ? Number(vehicleMetrics.fuelEff).toFixed(2) : '—'}</span>
              <span className="metric-label">Fuel efficiency (km/L)</span>
            </div>
            {vehicleMetrics.roi != null && (
              <div className="metric-card">
                <span className="metric-value">{Number(vehicleMetrics.roi.roi).toFixed(2)}</span>
                <span className="metric-label">Vehicle ROI</span>
              </div>
            )}
            {vehicleMetrics.opCost != null && (
              <div className="metric-card">
                <span className="metric-value">{Number(vehicleMetrics.opCost.totalOperationalCost).toFixed(2)}</span>
                <span className="metric-label">Total operational cost</span>
              </div>
            )}
          </div>
        )}
      </section>

      <section className="analytics-section card">
        <h2>Driver completion rate</h2>
        <div className="export-row" style={{ gap: '0.5rem', alignItems: 'flex-end' }}>
          <div className="form-group" style={{ marginBottom: 0 }}>
            <label>Driver ID</label>
            <input type="number" min="1" value={driverId} onChange={(e) => setDriverId(e.target.value)} placeholder="e.g. 1" style={{ width: 100 }} />
          </div>
          <button type="button" className="btn btn--primary btn--sm" onClick={loadDriverMetrics} disabled={loading || !driverId}>
            Load
          </button>
        </div>
        {driverMetrics && (
          <div className="metrics-grid" style={{ marginTop: '1rem' }}>
            <div className="metric-card">
              <span className="metric-value">{driverMetrics.driverName}</span>
              <span className="metric-label">Driver</span>
            </div>
            <div className="metric-card">
              <span className="metric-value">{driverMetrics.totalTrips}</span>
              <span className="metric-label">Total trips</span>
            </div>
            <div className="metric-card">
              <span className="metric-value">{driverMetrics.completedTrips}</span>
              <span className="metric-label">Completed</span>
            </div>
            <div className="metric-card">
              <span className="metric-value">{driverMetrics.completionRate}%</span>
              <span className="metric-label">Completion rate</span>
            </div>
          </div>
        )}
      </section>
    </>
  );
}
