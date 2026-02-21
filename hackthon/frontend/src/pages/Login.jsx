import React, { useState } from 'react';
import { Link, useNavigate } from 'react-router-dom';
import { auth } from '../api/client';
import { useAuth } from '../context/AuthContext';
import './Auth.css';

export function Login() {
  const [email, setEmail] = useState('');
  const [password, setPassword] = useState('');
  const [error, setError] = useState('');
  const [loading, setLoading] = useState(false);
  const { setToken } = useAuth();
  const navigate = useNavigate();

  const handleSubmit = async (e) => {
    e.preventDefault();
    setError('');
    setLoading(true);
    try {
      const token = await auth.login(email, password);
      setToken(token);
      navigate('/', { replace: true });
    } catch (err) {
      setError(err.message || 'Login failed');
    } finally {
      setLoading(false);
    }
  };

  return (
    <div className="auth-page">
      <div className="auth-card">
        <h1 className="auth-title">FleetFlow</h1>
        <p className="auth-subtitle">Fleet & Logistics Management</p>
        <form onSubmit={handleSubmit} className="auth-form">
          {error && <div className="error-msg">{error}</div>}
          <div className="form-group">
            <label>Email</label>
            <input
              type="email"
              value={email}
              onChange={(e) => setEmail(e.target.value)}
              required
              autoComplete="email"
              placeholder="you@company.com"
            />
          </div>
          <div className="form-group">
            <label>Password</label>
            <input
              type="password"
              value={password}
              onChange={(e) => setPassword(e.target.value)}
              required
              autoComplete="current-password"
            />
          </div>
          <button type="submit" className="btn btn--primary" style={{ width: '100%' }} disabled={loading}>
            {loading ? 'Signing in…' : 'Sign in'}
          </button>
          <p className="auth-footer">
            <Link to="/forgot-password">Forgot password?</Link>
            {' · '}
            <Link to="/register">Register</Link>
          </p>
        </form>
      </div>
    </div>
  );
}
