# FleetFlow UI

React frontend for the FleetFlow Fleet & Logistics Management API.

## Setup

```bash
npm install
```

## Run (development)

Start the backend first (Spring Boot on port 8080), then:

```bash
npm run dev
```

The app will run at **http://localhost:5173** and proxy `/api` requests to the backend.

## Build

```bash
npm run build
```

Output is in `dist/`. You can serve it with any static host or copy into the backend's `src/main/resources/static` for a single deployment.

## Pages

- **Login** – Email/password, Forgot password link
- **Command Center** – Dashboard KPIs (Active Fleet, Maintenance Alerts, Utilization, Pending Cargo)
- **Vehicle Registry** – List with filters (Type, Status, Region), Add/Edit/Retire
- **Trip Dispatcher** – Create trip (vehicle + driver, cargo, origin/destination), Dispatch, Complete, Cancel
- **Drivers** – List with status filter, Add/Edit (including status toggle)
- **Maintenance** – Add maintenance log (vehicle → In Shop), Complete log
- **Fuel Logs** – Select vehicle, view logs, Add fuel log
- **Analytics & Reports** – Vehicle metrics (profit, cost-per-km, fuel efficiency, ROI), driver completion rate, CSV/PDF exports (vehicles, trips, drivers, payroll, health audit)
