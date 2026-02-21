import React, { useState, useEffect } from 'react';
import { trips as tripsApi, vehicles as vehiclesApi, drivers as driversApi } from '../api/client';
import { StatusPill } from '../components/StatusPill';
import './DataPages.css';

const TRIP_STATUSES = ['DRAFT', 'DISPATCHED', 'COMPLETED', 'CANCELLED'];

export function Trips() {
  const [list, setList] = useState([]);
  const [vehicles, setVehicles] = useState([]);
  const [drivers, setDrivers] = useState([]);
  const [loading, setLoading] = useState(true);
  const [error, setError] = useState('');
  const [filterStatus, setFilterStatus] = useState('');
  const [showForm, setShowForm] = useState(false);
  const [completeModal, setCompleteModal] = useState(null);
  const [finalOdometer, setFinalOdometer] = useState('');
  const [form, setForm] = useState({
    vehicleId: '', driverId: '', cargoWeight: '', origin: '', destination: '', revenue: '',
  });

  const loadTrips = () => {
    tripsApi.list(filterStatus || undefined).then(setList).catch((e) => setError(e.message));
  };

  useEffect(() => {
    setLoading(true);
    Promise.all([
      tripsApi.list(filterStatus || undefined),
      vehiclesApi.list({ status: 'AVAILABLE' }).catch(() => []),
      driversApi.list({ status: 'OFF_DUTY' }).catch(() => []),
    ])
      .then(([trips, v, d]) => {
        setList(trips);
        setVehicles(v);
        setDrivers(d);
      })
      .catch((e) => setError(e.message))
      .finally(() => setLoading(false));
  }, [filterStatus]);

  const openCreate = () => {
    setForm({ vehicleId: '', driverId: '', cargoWeight: '', origin: '', destination: '', revenue: '' });
    setShowForm(true);
  };

  const submitForm = async (e) => {
    e.preventDefault();
    setError('');
    try {
      await tripsApi.create({
        vehicleId: Number(form.vehicleId),
        driverId: Number(form.driverId),
        cargoWeight: Number(form.cargoWeight),
        origin: form.origin || null,
        destination: form.destination || null,
        revenue: form.revenue ? Number(form.revenue) : null,
      });
      setShowForm(false);
      loadTrips();
      setVehicles(await vehiclesApi.list({ status: 'AVAILABLE' }).catch(() => []));
      setDrivers(await driversApi.list({ status: 'OFF_DUTY' }).catch(() => []));
    } catch (err) {
      setError(err.message);
    }
  };

  const dispatch = async (id) => {
    setError('');
    try {
      await tripsApi.dispatch(id);
      loadTrips();
    } catch (err) {
      setError(err.message);
    }
  };

  const complete = async () => {
    if (!completeModal || !finalOdometer) return;
    setError('');
    try {
      await tripsApi.complete(completeModal.id, Number(finalOdometer));
      setCompleteModal(null);
      setFinalOdometer('');
      loadTrips();
    } catch (err) {
      setError(err.message);
    }
  };

  const cancel = async (id) => {
    if (!confirm('Cancel this trip?')) return;
    setError('');
    try {
      await tripsApi.cancel(id);
      loadTrips();
    } catch (err) {
      setError(err.message);
    }
  };

  return (
    <>
      <div className="page-header page-header--row">
        <div>
          <h1>Trip Dispatcher</h1>
          <p>Create and manage trips (Draft → Dispatched → Completed / Cancelled)</p>
        </div>
        <button type="button" className="btn btn--primary" onClick={openCreate}>Create trip</button>
      </div>

      <div className="filters card" style={{ padding: '1rem', marginBottom: '1rem' }}>
        <div className="form-group" style={{ marginBottom: 0, minWidth: '160px' }}>
          <label>Status</label>
          <select value={filterStatus} onChange={(e) => setFilterStatus(e.target.value)}>
            <option value="">All</option>
            {TRIP_STATUSES.map((s) => (
              <option key={s} value={s}>{s}</option>
            ))}
          </select>
        </div>
      </div>

      {error && <div className="error-msg">{error}</div>}

      {showForm && (
        <div className="modal-overlay" onClick={() => setShowForm(false)}>
          <div className="modal card" onClick={(e) => e.stopPropagation()}>
            <h2>Create trip</h2>
            <form onSubmit={submitForm}>
              <div className="form-group">
                <label>Vehicle</label>
                <select value={form.vehicleId} onChange={(e) => setForm((f) => ({ ...f, vehicleId: e.target.value }))} required>
                  <option value="">Select vehicle</option>
                  {vehicles.map((v) => (
                    <option key={v.id} value={v.id}>{v.licensePlate} – {v.model}</option>
                  ))}
                  {vehicles.length === 0 && <option value="" disabled>No available vehicles</option>}
                </select>
              </div>
              <div className="form-group">
                <label>Driver</label>
                <select value={form.driverId} onChange={(e) => setForm((f) => ({ ...f, driverId: e.target.value }))} required>
                  <option value="">Select driver</option>
                  {drivers.map((d) => (
                    <option key={d.id} value={d.id}>{d.name}</option>
                  ))}
                  {drivers.length === 0 && <option value="" disabled>No available drivers</option>}
                </select>
              </div>
              <div className="form-group">
                <label>Cargo weight (kg)</label>
                <input type="number" step="0.01" value={form.cargoWeight} onChange={(e) => setForm((f) => ({ ...f, cargoWeight: e.target.value }))} required />
              </div>
              <div className="form-group">
                <label>Origin</label>
                <input value={form.origin} onChange={(e) => setForm((f) => ({ ...f, origin: e.target.value }))} placeholder="Point A" />
              </div>
              <div className="form-group">
                <label>Destination</label>
                <input value={form.destination} onChange={(e) => setForm((f) => ({ ...f, destination: e.target.value }))} placeholder="Point B" />
              </div>
              <div className="form-group">
                <label>Revenue</label>
                <input type="number" step="0.01" value={form.revenue} onChange={(e) => setForm((f) => ({ ...f, revenue: e.target.value }))} />
              </div>
              <div className="modal-actions">
                <button type="button" className="btn btn--secondary" onClick={() => setShowForm(false)}>Cancel</button>
                <button type="submit" className="btn btn--primary">Create (Draft)</button>
              </div>
            </form>
          </div>
        </div>
      )}

      {completeModal && (
        <div className="modal-overlay" onClick={() => setCompleteModal(null)}>
          <div className="modal card" onClick={(e) => e.stopPropagation()}>
            <h2>Complete trip</h2>
            <p>Enter final odometer reading for {completeModal.vehiclePlate}.</p>
            <div className="form-group">
              <label>Final odometer</label>
              <input type="number" step="0.01" value={finalOdometer} onChange={(e) => setFinalOdometer(e.target.value)} required />
            </div>
            <div className="modal-actions">
              <button type="button" className="btn btn--secondary" onClick={() => setCompleteModal(null)}>Cancel</button>
              <button type="button" className="btn btn--primary" onClick={complete} disabled={!finalOdometer}>Complete</button>
            </div>
          </div>
        </div>
      )}

      <div className="card table-wrap">
        {loading ? (
          <div className="empty-state">Loading…</div>
        ) : list.length === 0 ? (
          <div className="empty-state">No trips.</div>
        ) : (
          <table className="data-table">
            <thead>
              <tr>
                <th>ID</th>
                <th>Vehicle</th>
                <th>Driver</th>
                <th>Origin / Destination</th>
                <th>Cargo (kg)</th>
                <th>Status</th>
                <th>Actions</th>
              </tr>
            </thead>
            <tbody>
              {list.map((t) => (
                <tr key={t.id}>
                  <td>{t.id}</td>
                  <td>{t.vehiclePlate}</td>
                  <td>{t.driverName}</td>
                  <td>{(t.origin || '—') + ' → ' + (t.destination || '—')}</td>
                  <td>{t.cargoWeight}</td>
                  <td><StatusPill status={t.status} /></td>
                  <td>
                    {t.status === 'DRAFT' && <button type="button" className="btn btn--primary btn--sm" onClick={() => dispatch(t.id)}>Dispatch</button>}
                    {t.status === 'DRAFT' && <button type="button" className="btn btn--danger btn--sm" onClick={() => cancel(t.id)}>Cancel</button>}
                    {t.status === 'DISPATCHED' && <button type="button" className="btn btn--primary btn--sm" onClick={() => setCompleteModal(t)}>Complete</button>}
                    {t.status === 'DISPATCHED' && <button type="button" className="btn btn--danger btn--sm" onClick={() => cancel(t.id)}>Cancel</button>}
                  </td>
                </tr>
              ))}
            </tbody>
          </table>
        )}
      </div>
    </>
  );
}
