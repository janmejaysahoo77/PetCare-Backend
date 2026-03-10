# PetCare Backend - Project Summary

## Overview
PetCare-Backend is a Spring Boot microservice application designed to manage pet profiles, veterinary appointments, adoption requests, lost/found pet reports, and real-time notifications. It uses a clean, module-based architecture.

## Tech Stack
*   **Framework:** Spring Boot 3.2.3 (Java 24)
*   **Database:** Google Cloud Firestore (Firebase)
*   **Authentication:** Firebase Admin SDK & Spring Security (Custom JWT Filters)
*   **Messaging:** Apache Kafka 4.1.1 (SASL_SSL to Confluent Cloud)
*   **Caching/Data:** Redis (Spring Data Redis)
*   **API Documentation:** Springdoc OpenAPI (Swagger)
*   **Mapping:** MapStruct
*   **Utilities:** Lombok, Jackson

## Modules Built So Far

### 1. `auth` (Authentication & Security)
*   Integrates with Firebase Authentication.
*   `FirebaseTokenFilter` intercepts incoming requests, verifies the Firebase JWT token, and establishes a secure `UserPrincipal` context in Spring Security.
*   Manages users and roles automatically through the token claims.

### 2. `pet` (Pet Profiles & Health)
*   **`Pet` Entity:** Core entity representing a pet (Name, Species, Breed, Age, Weight, Owner references).
*   **`Vaccination` & `MedicalRecord`:** Entities linked to pets to track their health history and upcoming vaccine requirements.
*   CRUD interfaces available in `PetController` and `PetService`.

### 3. `appointment` (Veterinary Consultations)
*   **`Appointment` Entity:** Manages bookings between users and veterinarians.
*   **Features:** Tracks appointment dates, reasons, status (`SCHEDULED`, `COMPLETED`, `CANCELLED`), and consultation types (`IN_PERSON`, `TELECONSULT`).
*   Produces Kafka messages (`appointment.booked`, `appointment.cancelled`) to trigger notifications.

### 4. `post` (Community, Adoption & Lost/Found)
*   **Adoption:** `Adoption` entity allows users to put pets up for adoption and receive applications.
*   **Lost/Found:** `LostPetReport` entity tracks geographical coordinates, descriptions, and images of lost or found pets to alert the community.

### 5. `notification` (Real-Time Async Alerts)
*   Dedicated `NotificationService` acting as a Kafka Consumer.
*   Listens to 5 critical event topics from Confluent Cloud:
    *   `pet.created`
    *   `pet.lost`
    *   `pet.found`
    *   `appointment.booked`
    *   `appointment.cancelled`
*   Designed to dispatch emails/push notifications automatically without blocking the main HTTP request threads.

## Recent Architectural Resolutions
*   **Java 24 Compatibility:** Successfully bypassed the `java.lang.UnsupportedOperationException: getSubject is not supported` error by upgrading Kafka clients from `3.7.1` to `4.1.1` (KIP-1006).
*   **Confluent Cloud Topics:** Configured programmatic topic creation in `KafkaConfig.java` to explicitly utilize a **replication factor of 3**, completely resolving `UNKNOWN_TOPIC_OR_PARTITION` policy violations.
