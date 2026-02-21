const API_BASE = '/api';

function getToken() {
  return localStorage.getItem('fleetflow_token');
}

export async function api(path, options = {}) {
  const url = path.startsWith('http') ? path : `${API_BASE}${path}`;
  const headers = {
    'Content-Type': 'application/json',
    ...options.headers,
  };
  const token = getToken();
  if (token) headers['Authorization'] = `Bearer ${token}`;

  const res = await fetch(url, { ...options, headers });
  const text = await res.text();
  let data = null;
  try {
    data = text ? JSON.parse(text) : null;
  } catch (_) {
    data = text;
  }

  if (!res.ok) {
    const msg = data?.message || data?.error || res.statusText || 'Request failed';
    throw new Error(msg);
  }
  return data;
}

export function apiBlob(path) {
  const url = path.startsWith('http') ? path : `${API_BASE}${path}`;
  const token = getToken();
  return fetch(url, {
    headers: token ? { Authorization: `Bearer ${token}` } : {},
  }).then((r) => {
    if (!r.ok) throw new Error('Download failed');
    return r.blob();
  });
}

export const auth = {
  me: () => api('/auth/me'),
  login: (email, password) => api('/auth/login', { method: 'POST', body: JSON.stringify({ email, password }) }),
  register: (body) => api('/auth/register', { method: 'POST', body: JSON.stringify(body) }),
  logout: () => api('/auth/logout', { method: 'POST' }),
  forgotPassword: (email) => api('/auth/forgot-password', { method: 'POST', body: JSON.stringify({ email }) }),
  resetPassword: (token, newPassword) =>
    api('/auth/reset-password', { method: 'POST', body: JSON.stringify({ token, newPassword }) }),
};

export const dashboard = {
  get: () => api('/dashboard'),
};

export const vehicles = {
  list: (params) => {
    const q = new URLSearchParams();
    if (params?.type) q.set('type', params.type);
    if (params?.status) q.set('status', params.status);
    if (params?.region) q.set('region', params.region);
    const query = q.toString();
    return api('/vehicles' + (query ? '?' + query : ''));
  },
  get: (id) => api(`/vehicles/${id}`),
  create: (body) => api('/vehicles', { method: 'POST', body: JSON.stringify(body) }),
  update: (id, body) => api(`/vehicles/${id}`, { method: 'PUT', body: JSON.stringify(body) }),
  retire: (id) => api(`/vehicles/${id}/retire`, { method: 'PUT' }),
};

export const drivers = {
  list: (params) => {
    const q = new URLSearchParams();
    if (params?.status) q.set('status', params.status);
    const query = q.toString();
    return api('/drivers' + (query ? '?' + query : ''));
  },
  get: (id) => api(`/drivers/${id}`),
  create: (body) => api('/drivers', { method: 'POST', body: JSON.stringify(body) }),
  update: (id, body) => api(`/drivers/${id}`, { method: 'PUT', body: JSON.stringify(body) }),
};

export const trips = {
  list: (params) => {
    const q = new URLSearchParams();
    if (params?.status) q.set('status', params.status);
    const query = q.toString();
    return api('/trips' + (query ? '?' + query : ''));
  },
  get: (id) => api(`/trips/${id}`),
  create: (body) => api('/trips', { method: 'POST', body: JSON.stringify(body) }),
  dispatch: (id) => api(`/trips/${id}/dispatch`, { method: 'PUT' }),
  complete: (id, finalOdometer) => api(`/trips/${id}/complete?finalOdometer=${finalOdometer}`, { method: 'PUT' }),
  cancel: (id) => api(`/trips/${id}/cancel`, { method: 'PUT' }),
};

export const maintenance = {
  listByVehicle: (vehicleId) => api(`/maintenance/vehicle/${vehicleId}`),
  create: (body) => api('/maintenance', { method: 'POST', body: JSON.stringify(body) }),
  complete: (id) => api(`/maintenance/${id}/complete`, { method: 'PUT' }),
};

export const fuelLogs = {
  listByVehicle: (vehicleId) => api(`/fuel-logs/vehicle/${vehicleId}`),
  create: (body) => api('/fuel-logs', { method: 'POST', body: JSON.stringify(body) }),
};

export const analytics = {
  vehicleProfit: (id) => api(`/analytics/vehicle/${id}/profit`),
  vehicleCostPerKm: (id) => api(`/analytics/vehicle/${id}/cost-per-km`),
  vehicleFuelEfficiency: (id) => api(`/analytics/vehicle/${id}/fuel-efficiency`),
  vehicleROI: (id) => api(`/analytics/vehicle/${id}/roi`),
  vehicleOperationalCost: (id) => api(`/analytics/vehicle/${id}/operational-cost`),
  driverCompletionRate: (id) => api(`/analytics/driver/${id}/completion-rate`),
};

export const exportApi = {
  vehiclesCsv: () => apiBlob('/export/vehicles/csv'),
  tripsCsv: () => apiBlob('/export/trips/csv'),
  driversCsv: () => apiBlob('/export/drivers/csv'),
  vehiclesPdf: () => apiBlob('/export/vehicles/pdf'),
  tripsPdf: () => apiBlob('/export/trips/pdf'),
  driversPdf: () => apiBlob('/export/drivers/pdf'),
  payrollCsv: (year, month) => apiBlob(`/export/payroll/csv?year=${year}&month=${month}`),
  payrollPdf: (year, month) => apiBlob(`/export/payroll/pdf?year=${year}&month=${month}`),
  healthAuditCsv: () => apiBlob('/export/health-audit/csv'),
  healthAuditPdf: () => apiBlob('/export/health-audit/pdf'),
};

function downloadBlob(blob, filename) {
  const a = document.createElement('a');
  a.href = URL.createObjectURL(blob);
  a.download = filename;
  a.click();
  URL.revokeObjectURL(a.href);
}

export function downloadExport(blobPromise, filename) {
  return blobPromise.then((blob) => downloadBlob(blob, filename));
}
