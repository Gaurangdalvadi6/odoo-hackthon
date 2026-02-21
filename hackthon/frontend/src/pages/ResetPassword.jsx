import React, { useState } from 'react';
import { Link, useSearchParams, useNavigate } from 'react-router-dom';
import { auth } from '../api/client';
import { useAuth } from '../context/AuthContext';
import './Auth.css';

export function ResetPassword() {
  const [searchParams] = useSearchParams();
  const navigate = useNavigate();
  const [token, setToken] = useState(searchParams.get('token') || '');
  const [newPassword, setNewPassword] = useState('');
  const [error, setError] = useState('');
  const [loading, setLoading] = useState(false);
  const { setToken: setAuthToken } = useAuth();

  const handleSubmit = async (e) => {
    e.preventDefault();
    setError('');
    setLoading(true);
    try {
      const jwt = await auth.resetPassword(token, newPassword);
      setAuthToken(jwt);
      navigate('/', { replace: true });
    } catch (err) {
      setError(err.message || 'Reset failed');
    } finally {
      setLoading(false);
    }
  };

  return (
    <div className="auth-page">
      <div className="auth-card">
        <h1 className="auth-title">Reset password</h1>
        <p className="auth-subtitle">Enter the token and your new password.</p>
        <form onSubmit={handleSubmit} className="auth-form">
          {error && <div className="error-msg">{error}</div>}
          <div className="form-group">
            <label>Reset token</label>
            <input
              type="text"
              value={token}
              onChange={(e) => setToken(e.target.value)}
              required
              placeholder="Paste token from email or demo"
            />
          </div>
          <div className="form-group">
            <label>New password</label>
            <input
              type="password"
              value={newPassword}
              onChange={(e) => setNewPassword(e.target.value)}
              required
              minLength={6}
              autoComplete="new-password"
            />
          </div>
          <button type="submit" className="btn btn--primary" style={{ width: '100%' }} disabled={loading}>
            {loading ? 'Resetting…' : 'Reset password'}
          </button>
          <p className="auth-footer">
            <Link to="/login">Back to sign in</Link>
          </p>
        </form>
      </div>
    </div>
  );
}
