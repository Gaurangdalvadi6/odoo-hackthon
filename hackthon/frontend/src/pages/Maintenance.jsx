import React, { useState, useEffect } from 'react';
import { maintenance as maintenanceApi, vehicles as vehiclesApi } from '../api/client';
import { StatusPill } from '../components/StatusPill';
import './DataPages.css';

export function Maintenance() {
  const [vehicles, setVehicles] = useState([]);
  const [logsByVehicle, setLogsByVehicle] = useState({});
  const [loading, setLoading] = useState(true);
  const [error, setError] = useState('');
  const [showForm, setShowForm] = useState(false);
  const [form, setForm] = useState({ vehicleId: '', description: '', cost: '', serviceDate: '' });
  const [selectedVehicleId, setSelectedVehicleId] = useState(null);

  const load = async () => {
    setLoading(true);
    try {
      const vList = await vehiclesApi.list();
      setVehicles(vList.filter((v) => v.status !== 'RETIRED'));
      const map = {};
      for (const v of vList) {
        try {
          const logs = await maintenanceApi.listByVehicle(v.id);
          map[v.id] = logs;
        } catch (_) {
          map[v.id] = [];
        }
      }
      setLogsByVehicle(map);
    } catch (e) {
      setError(e.message);
    } finally {
      setLoading(false);
    }
  };

  useEffect(() => { load(); }, []);

  const openForm = () => {
    setForm({ vehicleId: '', description: '', cost: '', serviceDate: new Date().toISOString().slice(0, 10) });
    setShowForm(true);
  };

  const submitForm = async (e) => {
    e.preventDefault();
    setError('');
    try {
      await maintenanceApi.create({
        vehicleId: Number(form.vehicleId),
        description: form.description,
        cost: Number(form.cost),
        serviceDate: form.serviceDate || null,
      });
      setShowForm(false);
      load();
    } catch (err) {
      setError(err.message);
    }
  };

  const complete = async (id) => {
    setError('');
    try {
      await maintenanceApi.complete(id);
      load();
    } catch (err) {
      setError(err.message);
    }
  };

  const availableVehicles = vehicles.filter((v) => v.status !== 'IN_SHOP');

  return (
    <>
      <div className="page-header page-header--row">
        <div>
          <h1>Maintenance & Service Logs</h1>
          <p>Vehicle status becomes In Shop when a log is added</p>
        </div>
        <button type="button" className="btn btn--primary" onClick={openForm}>Add maintenance log</button>
      </div>

      {error && <div className="error-msg">{error}</div>}

      {showForm && (
        <div className="modal-overlay" onClick={() => setShowForm(false)}>
          <div className="modal card" onClick={(e) => e.stopPropagation()}>
            <h2>Add maintenance log</h2>
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
                <label>Description</label>
                <input value={form.description} onChange={(e) => setForm((f) => ({ ...f, description: e.target.value }))} required placeholder="e.g. Oil change" />
              </div>
              <div className="form-group">
                <label>Cost</label>
                <input type="number" step="0.01" value={form.cost} onChange={(e) => setForm((f) => ({ ...f, cost: e.target.value }))} required />
              </div>
              <div className="form-group">
                <label>Service date</label>
                <input type="date" value={form.serviceDate} onChange={(e) => setForm((f) => ({ ...f, serviceDate: e.target.value }))} />
              </div>
              <div className="modal-actions">
                <button type="button" className="btn btn--secondary" onClick={() => setShowForm(false)}>Cancel</button>
                <button type="submit" className="btn btn--primary">Create</button>
              </div>
            </form>
          </div>
        </div>
      )}

      <div className="card table-wrap">
        {loading ? (
          <div className="empty-state">Loading…</div>
        ) : (
          <>
            <table className="data-table">
              <thead>
                <tr>
                  <th>Vehicle</th>
                  <th>Status</th>
                  <th>Logs</th>
                </tr>
              </thead>
              <tbody>
                {vehicles.map((v) => {
                  const logs = logsByVehicle[v.id] || [];
                  return (
                    <tr key={v.id}>
                      <td>{v.licensePlate} – {v.model}</td>
                      <td><StatusPill status={v.status} /></td>
                      <td>
                        {logs.length === 0 ? '—' : (
                          <button type="button" className="btn btn--secondary btn--sm" onClick={() => setSelectedVehicleId(selectedVehicleId === v.id ? null : v.id)}>
                            {logs.length} log(s)
                          </button>
                        )}
                      </td>
                    </tr>
                  );
                })}
              </tbody>
            </table>
            {selectedVehicleId && (() => {
              const logs = logsByVehicle[selectedVehicleId] || [];
              const v = vehicles.find((x) => x.id === selectedVehicleId);
              return (
                <div style={{ padding: '1rem', borderTop: '1px solid var(--border)' }}>
                  <h3 style={{ margin: '0 0 0.75rem 0', fontSize: '1rem' }}>Logs for {v?.licensePlate}</h3>
                  <table className="data-table">
                    <thead>
                      <tr>
                        <th>Description</th>
                        <th>Cost</th>
                        <th>Date</th>
                        <th>Status</th>
                        <th></th>
                      </tr>
                    </thead>
                    <tbody>
                      {logs.map((l) => (
                        <tr key={l.id}>
                          <td>{l.description}</td>
                          <td>{l.cost}</td>
                          <td>{l.serviceDate}</td>
                          <td><StatusPill status={l.status} /></td>
                          <td>
                            {l.status === 'OPEN' && (
                              <button type="button" className="btn btn--primary btn--sm" onClick={() => complete(l.id)}>Complete</button>
                            )}
                          </td>
                        </tr>
                      ))}
                    </tbody>
                  </table>
                </div>
              );
            })()}
          </>
        )}
      </div>
    </>
  );
}
