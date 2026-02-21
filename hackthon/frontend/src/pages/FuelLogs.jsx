import React, { useState, useEffect } from 'react';
import { fuelLogs as fuelLogsApi, vehicles as vehiclesApi } from '../api/client';
import './DataPages.css';

export function FuelLogs() {
  const [vehicles, setVehicles] = useState([]);
  const [selectedVehicleId, setSelectedVehicleId] = useState(null);
  const [logs, setLogs] = useState([]);
  const [loading, setLoading] = useState(true);
  const [error, setError] = useState('');
  const [showForm, setShowForm] = useState(false);
  const [form, setForm] = useState({ vehicleId: '', liters: '', cost: '', date: new Date().toISOString().slice(0, 10) });

  useEffect(() => {
    vehiclesApi.list().then(setVehicles).catch((e) => setError(e.message)).finally(() => setLoading(false));
  }, []);

  useEffect(() => {
    if (!selectedVehicleId) {
      setLogs([]);
      return;
    }
    fuelLogsApi.listByVehicle(selectedVehicleId).then(setLogs).catch(() => setLogs([]));
  }, [selectedVehicleId]);

  const openForm = () => {
    setForm({ vehicleId: selectedVehicleId || '', liters: '', cost: '', date: new Date().toISOString().slice(0, 10) });
    setShowForm(true);
  };

  const submitForm = async (e) => {
    e.preventDefault();
    setError('');
    try {
      await fuelLogsApi.create({
        vehicleId: Number(form.vehicleId),
        liters: Number(form.liters),
        cost: Number(form.cost),
        date: form.date || null,
      });
      setShowForm(false);
      if (Number(form.vehicleId) === selectedVehicleId) {
        setLogs(await fuelLogsApi.listByVehicle(Number(form.vehicleId)));
      }
    } catch (err) {
      setError(err.message);
    }
  };

  const availableVehicles = vehicles.filter((v) => v.status !== 'RETIRED');

  return (
    <>
      <div className="page-header page-header--row">
        <div>
          <h1>Fuel Logging</h1>
          <p>Liters, cost, and date per vehicle</p>
        </div>
        <button type="button" className="btn btn--primary" onClick={openForm}>Add fuel log</button>
      </div>

      {error && <div className="error-msg">{error}</div>}

      {showForm && (
        <div className="modal-overlay" onClick={() => setShowForm(false)}>
          <div className="modal card" onClick={(e) => e.stopPropagation()}>
            <h2>Add fuel log</h2>
            <form onSubmit={submitForm}>
              <div className="form-group">
                <label>Vehicle</label>
                <select value={form.vehicleId} onChange={(e) => setForm((f) => ({ ...f, vehicleId: e.target.value }))} required>
                  <option value="">Select vehicle</option>
                  {availableVehicles.map((v) => (
                    <option key={v.id} value={v.id}>{v.licensePlate} – {v.model}</option>
                  ))}
                </select>
              </div>
              <div className="form-group">
                <label>Liters</label>
                <input type="number" step="0.01" value={form.liters} onChange={(e) => setForm((f) => ({ ...f, liters: e.target.value }))} required />
              </div>
              <div className="form-group">
                <label>Cost</label>
                <input type="number" step="0.01" value={form.cost} onChange={(e) => setForm((f) => ({ ...f, cost: e.target.value }))} required />
              </div>
              <div className="form-group">
                <label>Date</label>
                <input type="date" value={form.date} onChange={(e) => setForm((f) => ({ ...f, date: e.target.value }))} />
              </div>
              <div className="modal-actions">
                <button type="button" className="btn btn--secondary" onClick={() => setShowForm(false)}>Cancel</button>
                <button type="submit" className="btn btn--primary">Create</button>
              </div>
            </form>
          </div>
        </div>
      )}

      <div className="filters card" style={{ padding: '1rem', marginBottom: '1rem' }}>
        <div className="form-group" style={{ marginBottom: 0, minWidth: '220px' }}>
          <label>Select vehicle to view fuel logs</label>
          <select value={selectedVehicleId || ''} onChange={(e) => setSelectedVehicleId(e.target.value ? Number(e.target.value) : null)}>
            <option value="">—</option>
            {vehicles.map((v) => (
              <option key={v.id} value={v.id}>{v.licensePlate} – {v.model}</option>
            ))}
          </select>
        </div>
      </div>

      <div className="card table-wrap">
        {!selectedVehicleId ? (
          <div className="empty-state">Select a vehicle to see fuel logs.</div>
        ) : logs.length === 0 ? (
          <div className="empty-state">No fuel logs for this vehicle.</div>
        ) : (
          <table className="data-table">
            <thead>
              <tr>
                <th>Date</th>
                <th>Liters</th>
                <th>Cost</th>
              </tr>
            </thead>
            <tbody>
              {logs.map((l) => (
                <tr key={l.id}>
                  <td>{l.date}</td>
                  <td>{l.liters}</td>
                  <td>{l.cost}</td>
                </tr>
              ))}
            </tbody>
          </table>
        )}
      </div>
    </>
  );
}
