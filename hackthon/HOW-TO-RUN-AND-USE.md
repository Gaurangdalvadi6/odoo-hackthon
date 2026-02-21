# FleetFlow – How to Run and Use Everything

## Prerequisites

- **Java 17+** (for backend)
- **Node.js 18+** and **npm** (for frontend)
- **MySQL** (database `odoo_hackthon`)

---

## 1. Database setup

1. Start MySQL.
2. Create the database (if it doesn’t exist):
   ```sql
   CREATE DATABASE IF NOT EXISTS odoo_hackthon;
   ```
3. In `src/main/resources/application.properties` set your MySQL user/password if needed:
   ```properties
   spring.datasource.username=root
   spring.datasource.password=Admin@123
   ```

---

## 2. Run the backend

From the project root (`hackthon` folder):

```bash
mvn spring-boot:run
```

Or run `HackthonApplication.java` from your IDE.

- Backend runs at **http://localhost:8080**
- Wait until you see something like: `Started HackthonApplication`
- On first run, JPA will create tables. You need **roles and permissions** in the DB for RBAC (see below).

---

## 3. Run the frontend

Open a **new terminal** and run:

```bash
cd frontend
npm install
npm run dev
```

- Frontend runs at **http://localhost:5173**
- It proxies `/api` to `http://localhost:8080`, so you don’t need to change the API URL.

---

## 4. First-time setup: register a user

On **first backend run**, roles and permissions are **seeded automatically**. Each role has different permissions (see **Role access** below). You can register right away.

**Register via Postman or curl** (backend must be running):

```bash
curl -X POST http://localhost:8080/api/auth/register \
  -H "Content-Type: application/json" \
  -d "{\"name\":\"Admin\",\"email\":\"admin@fleet.com\",\"password\":\"admin123\",\"role\":\"ROLE_MANAGER\"}"
```

**Role** must be one of: `ROLE_MANAGER`, `ROLE_DISPATCHER`, `ROLE_ANALYST`, `ROLE_SAFETY`.

Then open **http://localhost:5173** and log in with that email and password.

**Or register from the UI:** Open http://localhost:5173 → click **Register** on the login page → fill Name, Email, Password, choose Role (e.g. Manager) → **Register**. You’ll be logged in automatically.

---

### Role access (Manager vs Dispatcher vs Analyst vs Safety)

| Role | Purpose | What they can do |
|------|--------|-------------------|
| **Manager** | Oversee fleet, assets, scheduling | **Full access:** vehicles (add/edit/retire), drivers, trips, maintenance, fuel logs, dashboard, analytics, exports. |
| **Dispatcher** | Create trips, assign drivers, validate cargo | **Trips:** create, dispatch, complete, cancel. **Read only:** vehicles, drivers. **Dashboard.** Cannot add/edit vehicles or drivers, no maintenance, no exports. |
| **Analyst** | Audit fuel, ROI, operational costs | **Read only:** vehicles, drivers, trips, maintenance, fuel. **Dashboard, Analytics, Exports** (CSV/PDF, payroll, health audit). Cannot create or update anything. |
| **Safety** | Driver compliance, license, safety scores | **Drivers:** read and **update** (status, safety score). **Read only:** vehicles, trips, maintenance, fuel. **Dashboard.** Cannot create vehicles/trips/maintenance or export. |

If a user tries an action their role doesn’t allow, the API returns **403 Forbidden**.

---

## 5. How to do everything in the app

### Login

1. Open **http://localhost:5173**
2. You’ll see the **Login** page.
3. Enter **email** and **password** of a registered user.
4. Click **Sign in** → you’re taken to the **Command Center** (dashboard).

### Forgot password

1. On the login page, click **Forgot password?**
2. Enter your **email** → Submit.
3. Backend returns a **reset token** (in the response; in production you’d send it by email).
4. Go to **Reset password**, paste the **token** and set a **new password** → Submit.
5. You’re logged in with the new password.

---

### Command Center (Dashboard)

- After login you land here.
- You see:
  - **Active Fleet** – vehicles currently On Trip
  - **Maintenance Alerts** – vehicles In Shop
  - **Utilization Rate** – % of fleet on trip
  - **Pending Cargo** – trips in DRAFT

No extra action; data loads automatically.

---

### Vehicle Registry

1. In the sidebar click **Vehicle Registry**.
2. **Filters:** Use **Type** (Truck/Van/Bike), **Status** (Available, On Trip, In Shop, Retired), **Region** to narrow the list.
3. **Add vehicle:** Click **Add vehicle** → fill Model, Type, Region, License plate, Max capacity (kg), Acquisition cost → **Create**.
4. **Edit:** Click **Edit** on a row → change fields → **Save**.
5. **Retire:** Click **Retire** on a vehicle → it becomes “Out of Service” and won’t appear for new trips.

---

### Trip Dispatcher

1. Click **Trip Dispatcher** in the sidebar.
2. **Create trip (Draft):**  
   Click **Create trip** → choose **Vehicle** (only available ones), **Driver** (only off duty), **Cargo weight (kg)** (must be ≤ vehicle max capacity), **Origin**, **Destination**, **Revenue** (optional) → **Create (Draft)**.
3. **Dispatch:** For a **DRAFT** trip, click **Dispatch** → vehicle and driver go “On Trip”.
4. **Complete:** For a **DISPATCHED** trip, click **Complete** → enter **Final odometer** → **Complete** → vehicle and driver become available again, trip is COMPLETED.
5. **Cancel:** For DRAFT or DISPATCHED, click **Cancel** to cancel the trip.
6. Use the **Status** filter to see only Draft, Dispatched, Completed, or Cancelled trips.

---

### Drivers

1. Click **Drivers** in the sidebar.
2. **Filter** by Status: On Duty, Off Duty, Suspended.
3. **Add driver:** **Add driver** → Name, License number, License expiry (future date), License category (Truck/Van/Bike) → **Create**.
4. **Edit:** **Edit** on a row → change name, license, **Status** (On Duty / Off Duty / Suspended), Safety score → **Save**.

Driver status is also updated automatically when you dispatch or complete trips.

---

### Maintenance & Service Logs

1. Click **Maintenance** in the sidebar.
2. **Add maintenance:** **Add maintenance log** → choose **Vehicle** (not already In Shop), **Description** (e.g. Oil change), **Cost**, **Service date** → **Create**.  
   That vehicle’s status becomes **In Shop** and it won’t appear in the trip dispatcher until maintenance is completed.
3. **View logs:** In the table, click **X log(s)** for a vehicle to expand and see its maintenance logs.
4. **Complete maintenance:** For a log with status OPEN, click **Complete** → vehicle goes back to **Available**.

---

### Fuel Logs

1. Click **Fuel Logs** in the sidebar.
2. **Select vehicle:** In “Select vehicle to view fuel logs”, pick a vehicle → its fuel logs are listed below.
3. **Add fuel log:** **Add fuel log** → Vehicle, **Liters**, **Cost**, **Date** → **Create**.

---

### Analytics & Reports

1. Click **Analytics & Reports** in the sidebar.

**Exports (one-click):**

- **CSV:** Vehicles, Trips, Drivers → click the button → file downloads.
- **PDF:** Same for PDF.
- **Monthly payroll:** Set **year** and **month** → **CSV** or **PDF** → download.
- **Health audit:** **CSV** or **PDF** → driver license compliance and vehicle maintenance status.

**Vehicle metrics:**

- In **Vehicle metrics**, select a **vehicle**.
- You see: **Profit**, **Cost per km**, **Fuel efficiency (km/L)**, **Vehicle ROI**, **Total operational cost**.

**Driver completion rate:**

- Enter a **Driver ID** (number) → **Load**.
- You see: **Total trips**, **Completed trips**, **Completion rate (%)**.

---

## 6. Quick checklist

| Step | What to do |
|------|------------|
| 1 | Start MySQL, create DB `odoo_hackthon` |
| 2 | Run backend: `mvn spring-boot:run` (from `hackthon`) |
| 3 | Run frontend: `cd frontend` → `npm install` → `npm run dev` |
| 4 | Add role + permissions in DB (or seed script) |
| 5 | Open http://localhost:5173 → **Register** (or use curl/Postman to register) |
| 6 | Log in with that user (or you’re already in after Register) |
| 7 | Add vehicles and drivers, then create trips, maintenance, and fuel logs from the UI |
| 8 | Use Dashboard, Analytics, and Export as needed |

---

## 7. If something fails

- **Backend won’t start:** Check MySQL is running, DB exists, and username/password in `application.properties`.
- **“Role not found” on register:** Insert `ROLE_MANAGER` (and optionally other roles) in the `roles` table, then register again.
- **401 on API calls from UI:** Log in again; token may have expired or been cleared.
- **CORS errors:** Backend allows `http://localhost:5173` and `http://127.0.0.1:5173`; use one of these for the frontend.
- **Frontend “Loading” forever:** Ensure backend is running on port 8080 and the frontend proxy is used (open the app via http://localhost:5173).

You’re set to run both backend and frontend and use all features end to end.
