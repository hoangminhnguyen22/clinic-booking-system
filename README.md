# 🏥 Clinic Appointment Booking System

> A full-stack Clinic Appointment Booking System built with **Java Spring Boot** and **React** and **React Native**, designed to simulate a real-world enterprise application following modern backend development practices.

> 🚧 **Project Status:** Active Development

---

## 📖 Overview

Clinic Appointment Booking System is a portfolio project created to simulate how a real healthcare appointment platform is designed and developed in an enterprise environment.

This project focuses on **clean architecture**, **object-oriented design**, **best practices**, and **production-ready development workflow** rather than simply implementing CRUD operations.

The main goal is to strengthen Java Backend development skills while building a high-quality portfolio project.

---

## 🌐 Project Ecosystem

This backend is part of a planned multi-platform clinic booking ecosystem:

| Application | Technology | Repository | Status |
|---|---|---|---|
| Backend API | Java, Spring Boot | `clinic-booking-system` | 🚧 In Progress |
| Web Application | React, TypeScript | `clinic-booking-web` | ⏳ Planned |
| Mobile Application | React Native, TypeScript | `clinic-booking-mobile` | ⏳ Planned |

The web and mobile applications will consume the REST APIs provided by this backend.

---

# 🎯 Project Goals

* Learn Java & Spring Boot through a real-world project
* Practice Object-Oriented Programming (OOP)
* Master Spring ecosystem
* Apply Clean Architecture
* Practice DDD Lite
* Improve system design skills
* Build a production-like GitHub portfolio
* Simulate enterprise development workflow

---

# 🏗 Architecture

The project follows a **Modular Monolith** architecture with **Package-by-Feature** organization.

Architecture Style

* Modular Monolith
* Package by Feature
* Clean Architecture (Pragmatic)
* DDD Lite
* Spring MVC

Example:

```text
backend
├── common
├── config
├── infrastructure
├── security
├── exception
└── modules
    ├── auth
    ├── patient
    ├── doctor
    ├── specialty
    ├── appointment
    ├── schedule
    ├── payment
    ├── notification
    └── medical_record
```

---

# 🛠 Tech Stack

## Backend

* Java 21
* Spring Boot 3
* Spring Security
* Spring Data JPA
* Hibernate
* JWT Authentication
* Flyway
* MapStruct
* Lombok
* Swagger / OpenAPI
* JUnit 5
* Mockito

## Databases

### PostgreSQL

Primary relational database for transactional data.

### MongoDB

Document database for:

* Audit Logs
* Medical Notes
* Notification Logs
* Activity Logs

### Redis

Used for:

* Caching
* Distributed Lock
* Refresh Token
* Rate Limiting
* Temporary Booking Reservation

## Frontend

* React
* TypeScript
* Vite
* React Router
* TanStack Query
* Axios
* Tailwind CSS
* shadcn/ui
* React Hook Form
* Zod

## Mobile

* React Native
* TypeScript
* Expo
* React Navigation
* Axios
* TanStack Query
* React Hook Form
* Zod
* AsyncStorage

## DevOps

* Docker
* Docker Compose
* GitHub Actions

---

# 📂 Repository Structure

```text
clinic-booking-system
│
├── backend
├── frontend
├── database
├── docker
├── docs
└── README.md
```

---

# 👥 User Roles

* Guest
* Patient
* Doctor
* Admin

---

# ✨ Features

## ✅ Planned Core Features

### Authentication

* Register
* Login
* JWT Authentication
* Refresh Token
* Forgot Password
* Change Password

### Patient

* Search Doctor
* Search Specialty
* View Available Slots
* Book Appointment
* Cancel Appointment
* Appointment History
* Medical Records
* Prescriptions

### Doctor

* Manage Schedule
* View Appointments
* Medical Records
* Prescriptions

### Admin

* User Management
* Doctor Management
* Specialty Management
* Appointment Management
* Dashboard

---

# 💡 Business Logic

The project aims to simulate real-world healthcare workflows.

Examples include:

* Prevent double booking
* Redis Distributed Lock
* Appointment validation
* Auto-cancel unpaid bookings
* Email notification
* Appointment reminder
* Audit logging
* Role-based authorization

---

# 🎓 OOP Practice

This project intentionally applies Object-Oriented Programming principles.

### Interface

Examples:

* NotificationSender
* PaymentProvider
* AppointmentRule
* FileStorageService
* AuditLogger

### Abstract Class

Examples:

* BaseEntity
* BaseAuditableEntity
* AbstractNotificationSender
* AbstractPaymentService

### Design Patterns

* Repository Pattern
* Service Layer
* DTO Pattern
* Strategy Pattern
* Factory Pattern
* Builder Pattern
* Template Method
* Dependency Injection
* Mapper Pattern

---

# 📅 Development Roadmap

| Phase                  | Status      |
| ---------------------- | ----------- |
| Project Planning       | ✅ Completed |
| Spring Boot Setup      | ⏳ Planned   |
| PostgreSQL Integration | ⏳ Planned   |
| Authentication         | ⏳ Planned   |
| User Module            | ⏳ Planned   |
| Doctor Module          | ⏳ Planned   |
| Patient Module         | ⏳ Planned   |
| Appointment Module     | ⏳ Planned   |
| Medical Record         | ⏳ Planned   |
| Redis Integration      | ⏳ Planned   |
| MongoDB Integration    | ⏳ Planned   |
| React Frontend         | ⏳ Planned   |
| React Native Mobile    | ⏳ Planned   |
| Docker                 | ⏳ Planned   |
| Testing                | ⏳ Planned   |
| CI/CD                  | ⏳ Planned   |

---

# 🚀 Future Enhancements

The following technologies are planned after the core system is completed.

* Apache Kafka
* Kubernetes
* Elasticsearch
* Prometheus
* Grafana
* OpenTelemetry
* Microservices
* Event-Driven Architecture

---

# 📊 Current Progress

| Component     | Status          |
| ------------- | --------------  |
| Planning      | ✅ Completed    |
| Architecture  | ✅ Completed    |
| Backend       | 🚧 In Progress  |
| Frontend      | ⏳ Planned      |
| Mobile        | ⏳ Planned      |
| PostgreSQL    | ⏳ Planned      |
| MongoDB       | ⏳ Planned      |
| Redis         | ⏳ Planned      |
| Docker        | ⏳ Planned      |
| Testing       | ⏳ Planned      |
| Documentation | 🚧 In Progress  |

---

# 📚 Documentation

Project documentation will be maintained under the `docs/` directory.

Planned documents include:

* Project Brief
* Software Requirements Specification (SRS)
* Architecture
* Database Design
* API Design
* Coding Convention
* Development Roadmap
* Deployment Guide

---

# 📖 API Documentation

Swagger/OpenAPI documentation will be available after the backend is implemented.

---

# 🗄 Database Design

Entity Relationship Diagram (ERD) will be added during the database design phase.

---

# 🖥 Screenshots

Screenshots and demo GIFs will be added as development progresses.

---

# ⚙ Getting Started

Documentation will be added after the first stable version is completed.

---

# 🧪 Testing

Planned testing strategy:

* Unit Test
* Integration Test
* API Test

---

# 🤝 Development Philosophy

This project is built with the mindset of developing an enterprise application.

Principles:

* Clean Code
* SOLID Principles
* Object-Oriented Design
* Domain-Oriented Development
* Production-like Structure
* Continuous Refactoring

---

# 👨‍💻 Author

**Hoang Minh Nguyen**

Backend Developer transitioning from PHP to Java, building real-world projects to deepen expertise in enterprise backend development.

---

## ⭐ Project Status

> This repository is actively being developed. Features, documentation, and architecture will continue to evolve throughout the development process.
