export function StatusPill({ status }) {
  if (!status) return null;
  const s = String(status).toLowerCase().replace(/ /g, '_');
  return <span className={`pill pill--${s}`}>{status}</span>;
}
