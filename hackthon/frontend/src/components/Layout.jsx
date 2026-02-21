import React from 'react';
import { NavLink, useNavigate } from 'react-router-dom';
import { useAuth } from '../context/AuthContext';
import './Layout.css';

const nav = [
  { to: '/', label: 'Command Center' },
  { to: '/vehicles', label: 'Vehicle Registry' },
  { to: '/trips', label: 'Trip Dispatcher' },
  { to: '/drivers', label: 'Drivers' },
  { to: '/maintenance', label: 'Maintenance' },
  { to: '/fuel-logs', label: 'Fuel Logs' },
  { to: '/analytics', label: 'Analytics & Reports' },
];

export function Layout({ children }) {
  const { logout } = useAuth();
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
          {nav.map(({ to, label }) => (
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
