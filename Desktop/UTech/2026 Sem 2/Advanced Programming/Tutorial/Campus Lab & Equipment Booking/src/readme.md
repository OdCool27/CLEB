# Campus Lab & Equipment Booking with Real-Time Dispatch (CLEB)


Faculty of Engineering & Computing (FENC), 
University of Technology, Jamaica

**Contributors:**
Odane Collins, 
Diandra Wedderburn, 
Daniel Lowe, XXX

------------------------------------------------------------------------

## 📌 Project Overview

**Campus Lab & Equipment Booking with Real-Time Dispatch (CLEB)** is a
Java-based Client/Server application that enables students and staff to
reserve laboratory seats and equipment across FENC labs.

The system is designed for LAN deployment and supports real-time updates
when reservations are approved, rejected, or cancelled. It enforces
strict server-side role-based access control and ensures secure,
concurrent, and reliable operation.

This project demonstrates distributed systems principles, concurrency
management, secure authentication, layered architecture, and GUI
development using Swing.

------------------------------------------------------------------------

## 🎯 Purpose

The system allows:

-   Students to reserve **specific lab seats (workstations)** and **lab
    equipment**
-   Technicians and administrators to manage reservation approvals
-   Real-time notification updates to all connected clients
-   Secure password storage and server-controlled database communication

The application models real-world lab usage across:

-   School of Computing & Information Technology (SCIT)
-   School of Engineering (SOE)

------------------------------------------------------------------------

## 🏫 Sample Labs

-   **SCIT Software Engineering Lab** --- 40 seats --- Papine Campus\
-   **SCIT Networking & Systems Lab** --- 32 seats --- Papine Campus\
-   **SOE Industrial & Mechanical Engineering Lab** --- 24 seats ---
    Papine Campus (Metal 3D Printer available)

### Sample Equipment

  ---------------------------------------------------------------------------
  Equipment ID                  Name           Status           Lab
  ----------------------------- -------------- ---------------- -------------
  EQ-3DP-0007                   Metal 3D       AVAILABLE        SOE
                                Printer                         Industrial &
                                                                Mechanical
                                                                Engineering
                                                                Lab

  EQ-NET-1021                   Layer-3 Switch MAINTENANCE      SCIT
                                                                Networking &
                                                                Systems Lab

  EQ-OSC-2210                   Oscilloscope   AVAILABLE        SOE
                                                                Industrial &
                                                                Mechanical
                                                                Engineering
                                                                Lab

  EQ-PC-4502                    Desktop        BOOKED           SCIT Software
                                Workstation                     Engineering
                                                                Lab
  ---------------------------------------------------------------------------

------------------------------------------------------------------------

## 🏗 System Architecture

CLEB follows a **TCP/IP client-server architecture** using blocking
sockets.

### 🔹 Server

-   Built using **Java 21**
-   Uses **blocking TCP sockets**
-   Supports **10--30 concurrent LAN clients**
-   Handles clients using a **thread pool**
-   Implements role-based access control
-   Sends real-time updates to all connected clients
-   Sole component allowed to communicate with the database

### 🔹 Client

-   Built as a **Swing Multiple Document Interface (MDI)** application
-   Uses:
    -   `JFrame`
    -   `JDesktopPane`
    -   `JInternalFrame`
    -   `JMenuBar`
    -   `JTable`
-   Includes a background listener thread for real-time server updates
-   Ensures no blocking of the Swing Event Dispatch Thread (EDT)

------------------------------------------------------------------------

## 👥 User Roles

The system supports three access-controlled roles:

-   **STUDENT**
    -   Create seat and equipment reservations
    -   View personal bookings
-   **TECHNICIAN**
    -   Review reservations
    -   Approve or reject requests
    -   Manage equipment status
-   **ADMIN**
    -   Full reservation management
    -   User oversight
    -   System-level control

All access control rules are strictly enforced **server-side**.

------------------------------------------------------------------------

## 🔐 Security

-   Passwords are hashed using:
    -   `PBKDF2WithHmacSHA256`
    -   Unique per-user salts
-   No plaintext password storage
-   All communication uses:
    -   Serializable DTOs
    -   Generic Request/Response envelopes
    -   Correlation ID (UUID-based tracking)

------------------------------------------------------------------------

## 🔄 Real-Time Dispatch

When a reservation is:

-   Approved
-   Rejected
-   Cancelled

The server immediately pushes updates to **all connected clients**,
ensuring synchronized views across the system.

------------------------------------------------------------------------

## 🗄 Database Layer

The server implements **two DAO versions**:

1.  Native JDBC
2.  Hibernate ORM

Database: **MySQL**

Key characteristics: - Only the server communicates with the database -
DAO pattern used - Clean separation of persistence layer

------------------------------------------------------------------------

## 📜 Logging

Implemented using **Log4J2**:

-   Rolling file appender (size-based rotation)
-   Daily log rotation
-   Separate audit logger
-   Structured server event logging

------------------------------------------------------------------------

## ⚙️ Technical Requirements

  Requirement          Technology
  -------------------- ------------------------------------------------
  Java Version         Java 21
  Build Tool           Maven
  Networking           TCP/IP Blocking Sockets
  Concurrency          Thread Pool (Server), Listener Thread (Client)
  GUI                  Java Swing (MDI)
  ORM                  Hibernate
  Database Access      JDBC + Hibernate
  Logging              Log4J2
  Security             PBKDF2WithHmacSHA256
  Concurrency Target   10--30 LAN Clients
