---
title: 'Clinic Appointment Booking System'
status: 'living-document'
updated: '2026-08-12'
source_basis: 'Clinic_Appointment_Booking_System_Idea_v3.md, current repository, approved security decisions'
---

# Project Brief: Clinic Appointment Booking System

## Purpose

An API-first Java and Spring Boot portfolio project built around a realistic clinic appointment-booking domain. The project prioritizes learning sound backend engineering through a modular monolith, not merely completing CRUD endpoints.

Primary goals:

- Learn Java and Spring Boot through a practical project.
- Practice OOP and pragmatic architecture where concrete use cases justify them.
- Build a production-like GitHub portfolio.
- Demonstrate backend engineering, testing, persistence, security, and business-rule design.

## Source And Status Rules

- Current code, migrations, and tests are the highest source of truth.
- Approved decisions in the newest handoff take priority over older planning documents and this brief.
- `Current State` describes implemented code only.
- `Approved Product Direction` records accepted product and architecture decisions.
- `Deferred Ideas Requiring Validation` preserves future ideas without treating them as approved or implemented.

## Current State

### Platform And Architecture

- Java 21 and Spring Boot `3.5.16`.
- API-first modular monolith using package-by-feature.
- Spring Web MVC, Spring Data JPA/Hibernate, Jakarta Bean Validation, and PostgreSQL.
- Flyway migrations `V1` through `V5`.
- Hibernate uses `spring.jpa.hibernate.ddl-auto=validate`.
- JUnit 5 and Mockito are available through Spring Boot Test.

### Booking Core

- Specialty management.
- Doctor management and specialty relationships.
- Recurring weekly Doctor schedules.
- Availability generation using `appointmentDurationMinutes`.
- Availability excludes `BOOKED` appointment slots.
- Appointment creation, listing, cancellation, and status updates.
- Appointment statuses: `BOOKED`, `CANCELLED`, `COMPLETED`, and `NO_SHOW`.
- Database constraint prevents duplicate `BOOKED` slots for the same doctor, date, and start time.
- Appointment still uses temporary client-supplied `Long patientId` because authenticated principal infrastructure does not yet exist.

### Identity And Registration Foundation

- `User`, additive `Role`, and `UserRepository` persistence exist.
- Roles are `ADMIN`, `DOCTOR`, and `PATIENT`.
- `PatientProfile` and its repository exist.
- `Doctor` can optionally link to one `User`; unlinked Doctor profiles remain valid.
- Public registration has request/response DTOs, a mapper, a transactional service, and `POST /api/registrations`; it normalizes email, hashes passwords with Argon2id, creates `User` + `PATIENT` role + `PatientProfile` atomically, and returns generic `409 Conflict` responses for duplicate canonical email.
- Registration DTO, mapper, service, controller, exception-handler, and PostgreSQL integration tests exist.
- Authentication, authorization, authenticated principal, Spring Security configuration, login, JWT, refresh tokens, and session infrastructure do not exist.

## Approved Product Direction

### Product Scope

- One clinic only in the first version.
- Patient online booking is the primary user journey.
- Admin and Doctor features manage data and schedules that enable booking.
- Multi-branch support remains deferred; add `Clinic` or `Branch` only when that requirement becomes active.

### Architecture

- Preserve the modular monolith and package-by-feature structure.
- Controllers own HTTP mapping, input validation, status, and delegation.
- Services own business rules, ownership, persistence coordination, and transactions.
- Repositories own persistence access.
- Mappers perform object conversion only and never query repositories.
- DTOs form REST contracts; JPA entities are not returned directly.
- Database constraints remain the final integrity safeguard.
- Do not add generic `common`, `shared`, `core`, event bus, or infrastructure abstractions before a concrete use case requires them.

### Identity And Security

- One person has one `User`; one User can have multiple roles.
- Patient and Doctor are domain profiles, not login accounts.
- A User can have both Patient and Doctor profiles.
- Public registration always grants `PATIENT`; public requests cannot assign privileged roles.
- Actor identity will come from authenticated principal, never client-supplied actor IDs.
- Role checks belong at the HTTP/security boundary.
- Ownership checks belong at the service boundary.
- JWT, if selected later, transports identity but never replaces ownership checks.
- Business services must not depend on JWT libraries or claims.
- Email is normalized at the service boundary with `email.trim().toLowerCase(Locale.ROOT)`.
- Passwords use a `12` to `128` character policy without composition regex.
- Passwords are not trimmed, lowercased, logged, returned, or persisted in plaintext.
- Argon2id is the selected adaptive password-hashing algorithm.
- Normal account deactivation uses `enabled = false`; hard deletion is exceptional.

### Registration Privacy Evolution

- Current duplicate canonical email behavior is `409 Conflict` with a generic message.
- Future public registration should verify email ownership.
- Future registration should use a uniform observable response for new and existing emails to reduce account enumeration.
- Verification token creation, expiry, resend, revoke, email delivery, and audit behavior remain deferred.
- Doctor account linking begins with manual admin identity verification; email matching alone is insufficient.
- A future Doctor invitation flow may combine admin verification with a one-time, expiring email invitation.

### Appointment Security Evolution

- Appointment keeps temporary scalar `patientId` until authenticated principal infrastructure exists.
- Do not create the Appointment-to-PatientProfile foreign key before principal foundation.
- After authentication exists, the service resolves Patient and Doctor profiles from principal `userId`.
- Patient and Doctor actor IDs will be removed from public requests.
- Out-of-scope resources and missing resources should both return `404` where ownership privacy requires it.
- Doctor self-booking will be rejected.
- Double-booking protection remains transaction/database-aware; Redis is not a substitute for database integrity.

## Product Roadmap

### Authentication And Account Management

- Public Patient registration is complete; evolve it with email verification and a uniform anti-enumeration response when delivery/token infrastructure is approved.
- Add email verification.
- Add login and authenticated principal foundation.
- Add role-based endpoint authorization and service ownership enforcement.
- Add forgot-password, password-reset, and change-password flows.
- Decide authentication transport only after principal and ownership foundations are correct.
- Add account administration and Doctor onboarding/linking.

### Patient Capabilities

- Search doctors and specialties.
- View available slots.
- Book and cancel owned appointments.
- View appointment history.
- Manage Patient profile data after profile fields are defined.
- Medical records and prescriptions remain later phases.

### Doctor Capabilities

- Manage schedules.
- View assigned appointments.
- Record allowed appointment outcomes.
- Manage medical records and prescriptions in a later phase.

### Admin Capabilities

- Manage users, doctors, specialties, and appointments.
- Perform verified Doctor account linking and role assignment.
- Dashboard and statistics remain later phases.

### Operational Capabilities

- Email notifications and appointment reminders.
- Audit history for appointment mutations, schedule changes, Doctor profile changes, and security administration.
- Payment and unpaid-booking behavior after payment requirements are defined.
- API documentation, deployment documentation, CI/CD, and production operations.

## Deferred Ideas Requiring Validation

These ideas came from the original Idea v3. They are candidates, not current dependencies or architecture commitments.

### Backend Candidates

- JWT and refresh-token transport.
- Redis for carefully justified caching, rate limiting, or temporary state.
- MongoDB for document-oriented data only if PostgreSQL is shown to be unsuitable for a concrete use case.
- Docker and Docker Compose.
- Swagger/OpenAPI.
- MapStruct and Lombok only if repeated boilerplate creates a concrete maintenance problem.
- GitHub Actions and deployment automation.

Redis distributed locks must not replace transactional and database protection against double booking. Refresh-token storage depends on the authentication transport decision. MongoDB must not be added merely to store logs or demonstrate another database.

### Frontend Candidates

- React, TypeScript, and Vite.
- React Router.
- TanStack Query and Axios.
- Tailwind CSS and shadcn/ui.
- React Hook Form and Zod.

Candidate pages:

- Public landing, Doctor list/detail, registration, and login.
- Patient dashboard, booking, appointment history, medical records, and profile.
- Doctor dashboard, schedule, appointment list, and prescription workflows.
- Admin dashboard, user management, Doctor management, and statistics.

### Domain Candidates

- Medical records, medical notes, prescriptions, and medicines.
- Payments and automatic cancellation of unpaid reservations.
- Notification and Doctor activity logs.
- System events and richer audit trails.
- Temporary booking reservations.
- Multi-branch clinic support.

Each candidate requires a concrete use case, data-retention rules, privacy analysis, transaction boundaries, and tests before implementation.

## Learning And Design Goals

The project may teach these concepts when a real use case requires them:

- Repository, service layer, DTO, mapper, and dependency injection patterns.
- Strategy, factory, builder, and template-method patterns.
- Focused interfaces such as notification sender or payment provider.
- Transaction boundaries, concurrency, database constraints, and race fallbacks.
- Authentication, role authorization, ownership authorization, and secure password storage.

Do not add interfaces, abstract base entities, factories, events, validators, or patterns solely to demonstrate terminology. Prefer the smallest correct implementation.

## Portfolio Deliverables

Candidates for repository polish:

- Accurate README and architecture overview.
- ERD and API documentation.
- Automated test instructions and test strategy.
- Docker-based local setup when Docker is introduced.
- Screenshots or demo GIF after a frontend exists.
- Deployment guide and CI/CD documentation after deployment is implemented.

Do not claim an artifact, technology, test coverage level, deployment, or feature before it exists.

## Working Agreements

- Read `_bmad-output/project-context.md` before changing or proposing code.
- Treat repository code, migrations, and tests as primary truth, followed by the newest handoff.
- Work in small, mentor-first steps: explain first, let the learner attempt production code, then compile or test.
- Verify each change with the narrowest appropriate compile or automated test.
- Do not add future technologies merely because they appear in this brief.
