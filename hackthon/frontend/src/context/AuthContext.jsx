import React, { createContext, useContext, useState, useEffect } from 'react';

const AuthContext = createContext(null);

const TOKEN_KEY = 'fleetflow_token';

export function AuthProvider({ children }) {
  const [token, setTokenState] = useState(() => localStorage.getItem(TOKEN_KEY));
  const [user, setUser] = useState(null);
  const [permissions, setPermissions] = useState([]);

  const setToken = (t) => {
    if (t) localStorage.setItem(TOKEN_KEY, t);
    else localStorage.removeItem(TOKEN_KEY);
    setTokenState(t);
  };

  const fetchMe = async () => {
    if (!token) return;
    try {
      const { auth } = await import('../api/client');
      const me = await auth.me();
      setUser(me);
      setPermissions(me?.permissions ?? []);
    } catch (_) {
      setUser(null);
      setPermissions([]);
    }
  };

  useEffect(() => {
    fetchMe();
  }, [token]);

  const logout = async () => {
    try {
      const { auth } = await import('../api/client');
      await auth.logout();
    } catch (_) {}
    setToken(null);
    setUser(null);
    setPermissions([]);
  };

  const value = {
    token,
    setToken,
    user,
    setUser,
    permissions,
    fetchMe,
    logout,
    isAuthenticated: !!token,
  };

  return <AuthContext.Provider value={value}>{children}</AuthContext.Provider>;
}

export function useAuth() {
  const ctx = useContext(AuthContext);
  if (!ctx) throw new Error('useAuth must be used within AuthProvider');
  return ctx;
}
