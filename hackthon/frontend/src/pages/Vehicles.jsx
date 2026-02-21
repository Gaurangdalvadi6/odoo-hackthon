import React, { useState, useEffect } from 'react';
import { vehicles as vehiclesApi } from '../api/client';
import { StatusPill } from '../components/StatusPill';
import './DataPages.css';

const VEHICLE_TYPES = ['TRUCK', 'VAN', 'BIKE'];
const STATUSES = ['AVAILABLE', 'ON_TRIP', 'IN_SHOP', 'RETIRED'];

export function Vehicles() {
  const [list, setList] = useState([]);
  const [loading, setLoading] = useState(true);
  const [error, setError] = useState('');
  const [filterType, setFilterType] = useState('');
  const [filterStatus, setFilterStatus] = useState('');
  const [filterRegion, setFilterRegion] = useState('');
  const [showForm, setShowForm] = useState(false);
  const [editingId, setEditingId] = useState(null);
  const [form, setForm] = useState({
    model: '', type: '', region: '', licensePlate: '', maxCapacity: '', acquisitionCost: '',
    odometer: '',
  });

  const load = () => {
    setLoading(true);
    vehiclesApi
      .list({
        type: filterType || undefined,
        status: filterStatus || undefined,
        region: filterRegion || undefined,
      })
      .then(setList)
      .catch((e) => setError(e.message))
      .finally(() => setLoading(false));
  };

  useEffect(load, [filterType, filterStatus, filterRegion]);

  const openCreate = () => {
    setForm({ model: '', type: '', region: '', licensePlate: '', maxCapacity: '', acquisitionCost: '', odometer: '' });
    setEditingId(null);
    setShowForm(true);
  };

  const openEdit = (v) => {
    setForm({
      model: v.model || '',
      type: v.type || '',
      region: v.region || '',
      licensePlate: v.licensePlate || '',
      maxCapacity: v.maxCapacity ?? '',
      acquisitionCost: v.acquisitionCost ?? '',
      odometer: v.odometer ?? '',
    });
    setEditingId(v.id);
    setShowForm(true);
  };

  const submitForm = async (e) => {
    e.preventDefault();
    setError('');
    try {
      if (editingId) {
        await vehiclesApi.update(editingId, {
          model: form.model,
          type: form.type || null,
          region: form.region || null,
          licensePlate: form.licensePlate,
          maxCapacity: form.maxCapacity ? Number(form.maxCapacity) : null,
          odometer: form.odometer ? Number(form.odometer) : null,
        });
      } else {
        await vehiclesApi.create({
          model: form.model,
          type: form.type || null,
          region: form.region || null,
          licensePlate: form.licensePlate,
          maxCapacity: Number(form.maxCapacity),
          acquisitionCost: form.acquisitionCost ? Number(form.acquisitionCost) : null,
        });
      }
      setShowForm(false);
      load();
    } catch (err) {
      setError(err.message);
    }
  };

  const retire = async (id) => {
    if (!confirm('Retire this vehicle (Out of Service)?')) return;
    setError('');
    try {
      await vehiclesApi.retire(id);
      load();
    } catch (err) {
      setError(err.message);
    }
  };

  return (
    <>
      <div className="page-header page-header--row">
        <div>
          <h1>Vehicle Registry</h1>
          <p>CRUD for physical assets</p>
        </div>
        <button type="button" className="btn btn--primary" onClick={openCreate}>Add vehicle</button>
      </div>

      <div className="filters card" style={{ padding: '1rem', marginBottom: '1rem' }}>
        <div className="filters-row">
          <div className="form-group" style={{ marginBottom: 0, minWidth: '120px' }}>
            <label>Type</label>
            <select value={filterType} onChange={(e) => setFilterType(e.target.value)}>
              <option value="">All</option>
              {VEHICLE_TYPES.map((t) => (
                <option key={t} value={t}>{t}</option>
              ))}
            </select>
          </div>
          <div className="form-group" style={{ marginBottom: 0, minWidth: '120px' }}>
            <label>Status</label>
            <select value={filterStatus} onChange={(e) => setFilterStatus(e.target.value)}>
              <option value="">All</option>
              {STATUSES.map((s) => (
                <option key={s} value={s}>{s.replace('_', ' ')}</option>
              ))}
            </select>
          </div>
          <div className="form-group" style={{ marginBottom: 0, minWidth: '140px' }}>
            <label>Region</label>
            <input
              type="text"
              value={filterRegion}
              onChange={(e) => setFilterRegion(e.target.value)}
              placeholder="Filter by region"
            />
          </div>
        </div>
      </div>

      {error && <div className="error-msg">{error}</div>}

      {showForm && (
        <div className="modal-overlay" onClick={() => setShowForm(false)}>
          <div className="modal card" onClick={(e) => e.stopPropagation()}>
            <h2>{editingId ? 'Edit vehicle' : 'Add vehicle'}</h2>
            <form onSubmit={submitForm}>
              <div className="form-group">
                <label>Model</label>
                <input value={form.model} onChange={(e) => setForm((f) => ({ ...f, model: e.target.value }))} required />
              </div>
              <div className="form-group">
                <label>Type</label>
                <select value={form.type} onChange={(e) => setForm((f) => ({ ...f, type: e.target.value }))}>
                  <option value="">—</option>
                  {VEHICLE_TYPES.map((t) => (
                    <option key={t} value={t}>{t}</option>
                  ))}
                </select>
              </div>
              <div className="form-group">
                <label>Region</label>
                <input value={form.region} onChange={(e) => setForm((f) => ({ ...f, region: e.target.value }))} />
              </div>
              <div className="form-group">
                <label>License plate</label>
                <input value={form.licensePlate} onChange={(e) => setForm((f) => ({ ...f, licensePlate: e.target.value }))} required />
              </div>
              <div className="form-group">
                <label>Max capacity (kg)</label>
                <input type="number" step="0.01" value={form.maxCapacity} onChange={(e) => setForm((f) => ({ ...f, maxCapacity: e.target.value }))} required />
              </div>
              {!editingId && (
                <div className="form-group">
                  <label>Acquisition cost</label>
                  <input type="number" step="0.01" value={form.acquisitionCost} onChange={(e) => setForm((f) => ({ ...f, acquisitionCost: e.target.value }))} />
                </div>
              )}
              {editingId && (
                <div className="form-group">
                  <label>Odometer</label>
                  <input type="number" step="0.01" value={form.odometer} onChange={(e) => setForm((f) => ({ ...f, odometer: e.target.value }))} />
                </div>
              )}
              <div className="modal-actions">
                <button type="button" className="btn btn--secondary" onClick={() => setShowForm(false)}>Cancel</button>
                <button type="submit" className="btn btn--primary">{editingId ? 'Save' : 'Create'}</button>
              </div>
            </form>
          </div>
        </div>
      )}

      <div className="card table-wrap">
        {loading ? (
          <div className="empty-state">Loading…</div>
        ) : list.length === 0 ? (
          <div className="empty-state">No vehicles match the filters.</div>
        ) : (
          <table className="data-table">
            <thead>
              <tr>
                <th>Model</th>
                <th>Type</th>
                <th>Region</th>
                <th>License plate</th>
                <th>Max capacity</th>
                <th>Odometer</th>
                <th>Status</th>
                <th>Actions</th>
              </tr>
            </thead>
            <tbody>
              {list.map((v) => (
                <tr key={v.id}>
                  <td>{v.model}</td>
                  <td>{v.type || '—'}</td>
                  <td>{v.region || '—'}</td>
                  <td>{v.licensePlate}</td>
                  <td>{v.maxCapacity} kg</td>
                  <td>{v.odometer != null ? v.odometer : '—'}</td>
                  <td><StatusPill status={v.status} /></td>
                  <td>
                    {v.status !== 'RETIRED' && (
                      <>
                        <button type="button" className="btn btn--secondary btn--sm" onClick={() => openEdit(v)}>Edit</button>
                        {' '}
                        <button type="button" className="btn btn--danger btn--sm" onClick={() => retire(v.id)}>Retire</button>
                      </>
                    )}
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
