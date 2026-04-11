# User Capabilities

## Capability Matrix

| Action                          | User | Student | Technician | Admin |
|---------------------------------|------|--------|------------|-------|
| Log in                          | Yes  | Yes    | Yes        | Yes   |
| Update own email/password       | Yes  | Yes    | Yes        | Yes   |
| Search available seats/equipment| No   | Yes    | Yes        | Yes   |
| Create reservation              | No   | Yes    | No         | Yes   |
| View own reservation history    | No   | Yes    | Yes (if needed) | Yes |
| View all pending reservations   | No   | No     | Yes        | Yes   |
| Approve/deny reservations       | No   | No     | Yes        | Yes   |
| View inventory                  | No   | Limited| Yes        | Yes   |
| Mark equipment maintenance      | No   | No     | Yes        | Yes   |
| Add/edit/delete equipment       | No   | No     | Limited/No | Yes   |
| Create/update/delete accounts   | No   | No     | No         | Yes   |
| View audit logs                 | No   | No     | Limited/No | Yes   |

---

# 1. Base User Abilities

These are actions every authenticated user should have, regardless of role.

## Authentication and Account
- Log in
- Log out
- Change password
- Update own email
- View own account/profile details

## Personal Access Control
- View only data relevant to their role and permissions
- Cannot modify another user’s account unless explicitly permitted

---

# 2. Student Abilities

A student is mainly a requester and viewer of their own reservations.

## Search and View Availability
- Search available lab seats for a specified date/time range
- Search available equipment for a specified date/time range
- View details of a lab, seat, or equipment before reserving
- View only bookable items
- Not see items under maintenance or otherwise unavailable

## Reservation Actions
- Create a lab seat reservation
- Create an equipment reservation
- Cancel their own reservation (subject to system rules)
- View the status of each reservation:
    - Pending
    - Approved
    - Denied
    - Cancelled
    - Completed (optional)
- View reservation history
- View upcoming reservations
- View past reservations

## Restrictions
- Cannot approve or deny reservations
- Cannot edit inventory
- Cannot create or manage user accounts
- Cannot mark equipment as under maintenance
- Cannot see other students’ reservations unless explicitly allowed

---

# 3. Technician Abilities

A technician is mainly an operational and approval-based employee user.

## Reservation Management
- View all pending reservations
- View reservation details before approving or denying
- Approve lab seat reservations
- Deny lab seat reservations
- Approve equipment reservations
- Deny equipment reservations
- Optionally add a reason/comment when denying a reservation
- View all processed reservations
- View reservation history across the system

## Inventory and Resource Monitoring
- View all labs
- View all lab seats
- View all equipment
- Check current status of equipment
- Mark equipment as under maintenance
- Remove equipment from maintenance / restore availability
- View seat and equipment booking schedules

## Operational Oversight
- See which resources are currently booked, pending, unavailable, or under maintenance
- Filter reservations by date, status, student, lab, or equipment
- Possibly flag broken/unavailable resources for admin attention

## Restrictions
- Cannot create/delete user accounts
- Cannot create employee accounts unless admin grants it
- Should not permanently remove core system records unless allowed

---

# 4. Admin Abilities

The admin has full technician capabilities plus higher-level system control.

## All Technician Permissions
- Everything the technician can do

## User Account Management
- Create student accounts
- Create employee accounts
- Create technician accounts
- Create admin accounts (depending on policy)
- Update user account details
- Reset user passwords
- Activate/deactivate accounts
- Delete accounts (if allowed)
- Change user roles
- View all users

## Inventory and System Configuration
- Add equipment
- Update equipment details
- Delete equipment
- Add labs
- Update lab details
- Add lab seats
- Update lab seat details
- Delete lab seats or mark inactive
- Configure resource status options

## Audit and Oversight
- View audit logs / system operation logs
- View who approved, denied, created, updated, or deleted records
- View maintenance history
- View account activity history
- View system-wide reports and summaries

## Administrative Control
- Override technician decisions (optional)
- View all reservations across the system
- View all account changes
- Manage permissions if implementing permission-based control  