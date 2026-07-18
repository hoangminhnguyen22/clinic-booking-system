---
title: 'Clinic Appointment Booking System'
status: 'complete'
source: '_bmad-output/planning-artifacts/briefs/brief-clinic-booking-system-2026-07-14/brief.md'
updated: '2026-07-14'
---

# Project Brief: Clinic Appointment Booking System

## Purpose

An API-first Java and Spring Boot portfolio project built around a realistic clinic appointment-booking domain. The project prioritizes learning sound backend engineering through a modular monolith, not merely completing CRUD endpoints.

## Current Product Direction

- One clinic only in the first version.
- Patient online booking is the primary user journey.
- Admin and Doctor features exist to manage the data and schedules that enable booking.
- The immediate milestone is the booking core:

  `Specialty -> Doctor -> DoctorSchedule -> Available Slot -> Appointment`

## First Milestone

- Manage specialties, doctors, and recurring weekly doctor schedules.
- Generate available slots from doctor schedules and each doctor's `appointmentDurationMinutes`.
- Let patients find doctors, view available slots, create appointments, view appointments, and cancel appointments before their start time.
- Use `BOOKED`, `CANCELLED`, `COMPLETED`, and `NO_SHOW`; Admin or Doctor initially records completion and no-shows.
- Use a temporary `patientId` before implementing patient registration and authentication.
- Prevent invalid schedules, overlapping shifts, and duplicate bookings.
- Verify features incrementally with compilation and automated tests.

## Deferred Roadmap

- Spring Security, JWT, roles, and authentication.
- Medical records, prescriptions, notifications, payment, and reminders.
- Flyway, Docker, OpenAPI, CI/CD, Redis, MongoDB, and frontend implementation.
- Multi-branch support. Add `Clinic`/`Branch` only when that requirement becomes active.

## Working Agreements

- Read `_bmad-output/project-context.md` before changing or proposing code.
- Treat the repository as the primary source of truth, then use the latest handoff.
- Work in small, mentor-first steps: explain first, let the learner attempt the code, then compile or test.
- Do not add future roadmap technologies merely because they appear in this brief.

## Session Startup

Before proposing work in a new session, read this file, `_bmad-output/project-context.md`, the current repository state, and the newest session handoff when one exists.

For the full coached product brief and its decision history, see the source path in the frontmatter.
