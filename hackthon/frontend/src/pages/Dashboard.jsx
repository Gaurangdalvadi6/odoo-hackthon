import React, { useState, useEffect } from 'react';
import { dashboard } from '../api/client';
import './Dashboard.css';

export function Dashboard() {
  const [data, setData] = useState(null);
  const [error, setError] = useState('');
  const [loading, setLoading] = useState(true);

  useEffect(() => {
    dashboard
      .get()
      .then(setData)
      .catch((e) => setError(e.message))
      .finally(() => setLoading(false));
  }, []);

  if (loading) return <div className="page-header"><h1>Command Center</h1><p>Loading…</p></div>;
  if (error) return <div className="page-header"><h1>Command Center</h1><div className="error-msg">{error}</div></div>;
  if (!data) return null;

  return (
    <>
      <div className="page-header">
        <h1>Command Center</h1>
        <p>High-level fleet oversight</p>
      </div>
      <div className="kpi-grid">
        <div className="kpi-card">
          <span className="kpi-value">{data.activeFleet ?? 0}</span>
          <span className="kpi-label">Active Fleet (On Trip)</span>
        </div>
        <div className="kpi-card">
          <span className="kpi-value">{data.maintenanceAlerts ?? 0}</span>
          <span className="kpi-label">Maintenance Alerts (In Shop)</span>
        </div>
        <div className="kpi-card">
          <span className="kpi-value">{data.utilizationRate ?? 0}%</span>
          <span className="kpi-label">Utilization Rate</span>
        </div>
        <div className="kpi-card">
          <span className="kpi-value">{data.pendingCargo ?? 0}</span>
          <span className="kpi-label">Pending Cargo (Draft)</span>
        </div>
      </div>
    </>
  );
}
