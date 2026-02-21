import React, { useState } from 'react';
import { Link } from 'react-router-dom';
import { auth } from '../api/client';
import './Auth.css';

export function ForgotPassword() {
  const [email, setEmail] = useState('');
  const [error, setError] = useState('');
  const [sent, setSent] = useState(false);
  const [resetToken, setResetToken] = useState('');
  const [loading, setLoading] = useState(false);

  const handleSubmit = async (e) => {
    e.preventDefault();
    setError('');
    setLoading(true);
    try {
      const res = await auth.forgotPassword(email);
      setSent(true);
      if (res.resetToken) setResetToken(res.resetToken);
    } catch (err) {
      setError(err.message || 'Request failed');
    } finally {
      setLoading(false);
    }
  };

  if (sent) {
    return (
      <div className="auth-page">
        <div className="auth-card">
          <h1 className="auth-title">Check your email</h1>
          <p className="auth-subtitle">
            If an account exists for that email, we’ve sent reset instructions.
          </p>
          {resetToken && (
            <div className="success-msg">
              <strong>Demo:</strong> Use this token on the reset page: <code style={{ wordBreak: 'break-all' }}>{resetToken}</code>
            </div>
          )}
          <Link to="/reset-password" className="btn btn--primary" style={{ width: '100%', display: 'block', textAlign: 'center' }}>
            Go to Reset Password
          </Link>
          <p className="auth-footer">
            <Link to="/login">Back to sign in</Link>
          </p>
        </div>
      </div>
    );
  }

  return (
    <div className="auth-page">
      <div className="auth-card">
        <h1 className="auth-title">Forgot password</h1>
        <p className="auth-subtitle">Enter your email to receive a reset link.</p>
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
            />
          </div>
          <button type="submit" className="btn btn--primary" style={{ width: '100%' }} disabled={loading}>
            {loading ? 'Sending…' : 'Send reset link'}
          </button>
          <p className="auth-footer">
            <Link to="/login">Back to sign in</Link>
          </p>
        </form>
      </div>
    </div>
  );
}
