import React, { useState, useEffect } from 'react';
import { drivers as driversApi } from '../api/client';
import { StatusPill } from '../components/StatusPill';
import './DataPages.css';

const DRIVER_STATUSES = ['ON_DUTY', 'OFF_DUTY', 'SUSPENDED'];
const LICENSE_CATEGORIES = ['TRUCK', 'VAN', 'BIKE'];

export function Drivers() {
  const [list, setList] = useState([]);
  const [loading, setLoading] = useState(true);
  const [error, setError] = useState('');
  const [filterStatus, setFilterStatus] = useState('');
  const [showForm, setShowForm] = useState(false);
  const [editingId, setEditingId] = useState(null);
  const [form, setForm] = useState({
    name: '', licenseNumber: '', licenseExpiry: '', licenseCategory: '', status: '', safetyScore: '',
  });

  const load = () => {
    setLoading(true);
    driversApi
      .list(filterStatus || undefined)
      .then(setList)
      .catch((e) => setError(e.message))
      .finally(() => setLoading(false));
  };

  useEffect(load, [filterStatus]);

  const openCreate = () => {
    setForm({ name: '', licenseNumber: '', licenseExpiry: '', licenseCategory: '', status: '', safetyScore: '' });
    setEditingId(null);
    setShowForm(true);
  };

  const openEdit = (d) => {
    setForm({
      name: d.name || '',
      licenseNumber: d.licenseNumber || '',
      licenseExpiry: d.licenseExpiry ? d.licenseExpiry.slice(0, 10) : '',
      licenseCategory: d.licenseCategory || '',
      status: d.status || '',
      safetyScore: d.safetyScore ?? '',
    });
    setEditingId(d.id);
    setShowForm(true);
  };

  const submitForm = async (e) => {
    e.preventDefault();
    setError('');
    try {
      if (editingId) {
        await driversApi.update(editingId, {
          name: form.name,
          licenseNumber: form.licenseNumber,
          licenseExpiry: form.licenseExpiry || null,
          licenseCategory: form.licenseCategory || null,
          status: form.status || null,
          safetyScore: form.safetyScore ? Number(form.safetyScore) : null,
        });
      } else {
        await driversApi.create({
          name: form.name,
          licenseNumber: form.licenseNumber,
          licenseExpiry: form.licenseExpiry,
          licenseCategory: form.licenseCategory || null,
        });
      }
      setShowForm(false);
      load();
    } catch (err) {
      setError(err.message);
    }
  };

  return (
    <>
      <div className="page-header page-header--row">
        <div>
          <h1>Driver Performance & Safety</h1>
          <p>License compliance, status, and safety scores</p>
        </div>
        <button type="button" className="btn btn--primary" onClick={openCreate}>Add driver</button>
      </div>

      <div className="filters card" style={{ padding: '1rem', marginBottom: '1rem' }}>
        <div className="form-group" style={{ marginBottom: 0, minWidth: '140px' }}>
          <label>Status</label>
          <select value={filterStatus} onChange={(e) => setFilterStatus(e.target.value)}>
            <option value="">All</option>
            {DRIVER_STATUSES.map((s) => (
              <option key={s} value={s}>{s.replace('_', ' ')}</option>
            ))}
          </select>
        </div>
      </div>

      {error && <div className="error-msg">{error}</div>}

      {showForm && (
        <div className="modal-overlay" onClick={() => setShowForm(false)}>
          <div className="modal card" onClick={(e) => e.stopPropagation()}>
            <h2>{editingId ? 'Edit driver' : 'Add driver'}</h2>
            <form onSubmit={submitForm}>
              <div className="form-group">
                <label>Name</label>
                <input value={form.name} onChange={(e) => setForm((f) => ({ ...f, name: e.target.value }))} required />
              </div>
              <div className="form-group">
                <label>License number</label>
                <input value={form.licenseNumber} onChange={(e) => setForm((f) => ({ ...f, licenseNumber: e.target.value }))} required />
              </div>
              <div className="form-group">
                <label>License expiry</label>
                <input type="date" value={form.licenseExpiry} onChange={(e) => setForm((f) => ({ ...f, licenseExpiry: e.target.value }))} required={!editingId} />
              </div>
              <div className="form-group">
                <label>License category</label>
                <select value={form.licenseCategory} onChange={(e) => setForm((f) => ({ ...f, licenseCategory: e.target.value }))}>
                  <option value="">—</option>
                  {LICENSE_CATEGORIES.map((c) => (
                    <option key={c} value={c}>{c}</option>
                  ))}
                </select>
              </div>
              {editingId && (
                <div className="form-group">
                  <label>Status</label>
                  <select value={form.status} onChange={(e) => setForm((f) => ({ ...f, status: e.target.value }))}>
                    <option value="">—</option>
                    {DRIVER_STATUSES.map((s) => (
                      <option key={s} value={s}>{s.replace('_', ' ')}</option>
                    ))}
                  </select>
                </div>
              )}
              {editingId && (
                <div className="form-group">
                  <label>Safety score</label>
                  <input type="number" step="0.01" value={form.safetyScore} onChange={(e) => setForm((f) => ({ ...f, safetyScore: e.target.value }))} />
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
          <div className="empty-state">No drivers.</div>
        ) : (
          <table className="data-table">
            <thead>
              <tr>
                <th>Name</th>
                <th>License</th>
                <th>Expiry</th>
                <th>Category</th>
                <th>Status</th>
                <th>Safety score</th>
                <th>Actions</th>
              </tr>
            </thead>
            <tbody>
              {list.map((d) => (
                <tr key={d.id}>
                  <td>{d.name}</td>
                  <td>{d.licenseNumber}</td>
                  <td>{d.licenseExpiry || '—'}</td>
                  <td>{d.licenseCategory || '—'}</td>
                  <td><StatusPill status={d.status} /></td>
                  <td>{d.safetyScore != null ? d.safetyScore : '—'}</td>
                  <td>
                    <button type="button" className="btn btn--secondary btn--sm" onClick={() => openEdit(d)}>Edit</button>
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
