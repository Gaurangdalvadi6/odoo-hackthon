import React, { useState } from 'react';
import { Link, useNavigate } from 'react-router-dom';
import { auth } from '../api/client';
import { useAuth } from '../context/AuthContext';
import './Auth.css';

const ROLES = [
  { value: 'ROLE_MANAGER', label: 'Manager' },
  { value: 'ROLE_DISPATCHER', label: 'Dispatcher' },
  { value: 'ROLE_ANALYST', label: 'Analyst' },
  { value: 'ROLE_SAFETY', label: 'Safety Officer' },
];

export function Register() {
  const [name, setName] = useState('');
  const [email, setEmail] = useState('');
  const [password, setPassword] = useState('');
  const [role, setRole] = useState('ROLE_MANAGER');
  const [error, setError] = useState('');
  const [loading, setLoading] = useState(false);
  const { setToken } = useAuth();
  const navigate = useNavigate();

  const handleSubmit = async (e) => {
    e.preventDefault();
    setError('');
    setLoading(true);
    try {
      await auth.register({ name, email, password, role });
      const token = await auth.login(email, password);
      setToken(token);
      navigate('/', { replace: true });
    } catch (err) {
      setError(err.message || 'Registration failed');
    } finally {
      setLoading(false);
    }
  };

  return (
    <div className="auth-page">
      <div className="auth-card">
        <h1 className="auth-title">Register</h1>
        <p className="auth-subtitle">Create a FleetFlow account</p>
        <form onSubmit={handleSubmit} className="auth-form">
          {error && <div className="error-msg">{error}</div>}
          <div className="form-group">
            <label>Name</label>
            <input type="text" value={name} onChange={(e) => setName(e.target.value)} required />
          </div>
          <div className="form-group">
            <label>Email</label>
            <input type="email" value={email} onChange={(e) => setEmail(e.target.value)} required />
          </div>
          <div className="form-group">
            <label>Password</label>
            <input type="password" value={password} onChange={(e) => setPassword(e.target.value)} required minLength={6} />
          </div>
          <div className="form-group">
            <label>Role</label>
            <select value={role} onChange={(e) => setRole(e.target.value)}>
              {ROLES.map((r) => (
                <option key={r.value} value={r.value}>{r.label}</option>
              ))}
            </select>
          </div>
          <button type="submit" className="btn btn--primary" style={{ width: '100%' }} disabled={loading}>
            {loading ? 'Creating account…' : 'Register'}
          </button>
          <p className="auth-footer">
            <Link to="/login">Already have an account? Sign in</Link>
          </p>
        </form>
      </div>
    </div>
  );
}
