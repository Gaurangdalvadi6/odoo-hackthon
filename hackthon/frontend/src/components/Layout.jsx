import React from 'react';
import { NavLink, useNavigate } from 'react-router-dom';
import { useAuth } from '../context/AuthContext';
import './Layout.css';

const nav = [
  { to: '/', label: 'Command Center', permission: 'DASHBOARD_READ' },
  { to: '/vehicles', label: 'Vehicle Registry', permission: 'VEHICLE_READ' },
  { to: '/trips', label: 'Trip Dispatcher', permission: 'TRIP_READ' },
  { to: '/drivers', label: 'Drivers', permission: 'DRIVER_READ' },
  { to: '/maintenance', label: 'Maintenance', permission: 'MAINTENANCE_READ' },
  { to: '/fuel-logs', label: 'Fuel Logs', permission: 'FUEL_READ' },
  { to: '/analytics', label: 'Analytics & Reports', permission: 'ANALYTICS_READ' },
];

export function Layout({ children }) {
  const { logout, permissions } = useAuth();
  const visibleNav = permissions.length
    ? nav.filter((item) => permissions.includes(item.permission))
    : nav;
  const navigate = useNavigate();

  const handleLogout = () => {
    logout();
    navigate('/login');
  };

  return (
    <div className="app-layout">
      <aside className="sidebar">
        <div className="sidebar-brand">
          <span className="sidebar-logo">FleetFlow</span>
        </div>
        <nav className="sidebar-nav">
          {visibleNav.map(({ to, label }) => (
            <NavLink key={to} to={to} className={({ isActive }) => 'nav-link' + (isActive ? ' active' : '')}>
              {label}
            </NavLink>
          ))}
        </nav>
        <div className="sidebar-footer">
          <button type="button" className="btn btn--secondary btn--sm" onClick={handleLogout}>
            Log out
          </button>
        </div>
      </aside>
      <main className="main-content">{children}</main>
    </div>
  );
}
