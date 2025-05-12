# High-Level Architecture: Transportation Logistics (Request & Browse)

## 1. Introduction

This document outlines the high-level architecture for the "Transportation Logistics (Request & Browse)" module of the AgriConnect application. This module enables farmers to post requests for transporting their produce and allows users (including farmers) to browse and find nearby transporters. The architecture prioritizes simplicity, offline functionality, data privacy, and leverages Supabase as the backend platform.

## 2. Architectural Goals

The architecture is designed to meet the following key goals:

*   **Simplicity & Usability:** Provide a clean, intuitive user interface for both farmers requesting transport and users browsing transporters, catering to low-literacy and low-tech users.
*   **Offline Capability:**
    *   Allow farmers to create transport requests even when offline, with requests queued for synchronization upon network restoration.
    *   Enable users to view cached transporter listings when offline.
*   **Data Privacy:** Ensure farmer and transporter contact details are handled with appropriate privacy measures, avoiding public exposure without consent or mediation.
*   **Supabase Utilization:** Leverage Supabase for backend services, including PostgreSQL database, authentication (Google Social Login), and serverless functions for custom logic.
*   **Data Integrity & Reliability:** Ensure reliable synchronization of offline data and maintain accurate transporter information (though the update mechanism for transporter data is an external dependency).

## 3. High-Level Architecture Diagram

```mermaid
graph TD
    subgraph ClientApp [Client Application (Mobile)]
        direction LR
        UI[UI Layer: Request Forms, Transporter Lists]
        RM[Request Management Module]
        TB[Transporter Browsing Module]
        OSS[Offline Sync Service]
        LS[Local Storage: Queued Requests, Cached Transporters]

        UI --> RM
        UI --> TB
        RM --> LS
        TB --> LS
        OSS --> LS
    end

    subgraph SupabaseBackend [Supabase Backend]
        direction LR
        Auth[Supabase Auth: Google Login]
        API[API Layer: Supabase Functions / REST API]
        DB[Supabase PostgreSQL Database]

        API --> Auth
        API --> DB
    end

    Admin[Transporter Data Management (Admin/External System - Dependency)]

    ClientApp <-->|HTTPS/Realtime| API
    Admin -->|Data Population| DB

    style ClientApp fill:#ddeeff,stroke:#333,stroke-width:2px
    style SupabaseBackend fill:#ddffdd,stroke:#333,stroke-width:2px
    style Admin fill:#ffeedd,stroke:#333,stroke-width:2px
```

## 4. Key Components

### 4.1. Client Application (Mobile)

*   **UI Layer:** Provides user interfaces for creating transport requests and browsing transporter listings. Designed for simplicity and accessibility.
*   **Request Management Module:**
    *   Handles the creation and validation of transport requests.
    *   Stores requests locally in `Local Storage` if the application is offline.
*   **Transporter Browsing Module:**
    *   Fetches and displays transporter listings.
    *   Manages the local caching of transporter data in `Local Storage` for offline access.
*   **Offline Sync Service:**
    *   Monitors network connectivity.
    *   Synchronizes queued transport requests from `Local Storage` to the `Supabase Backend` when online.
    *   Periodically updates the cached transporter data from the `Supabase Backend` when online.
*   **Local Storage:** Device-side storage (e.g., SQLite, key-value store, or Supabase client library's offline features) used for:
    *   Queueing transport requests created offline.
    *   Caching transporter information for offline browsing.

### 4.2. Supabase Backend

*   **API Layer (Supabase Functions / REST API):**
    *   Exposes endpoints for creating, retrieving, and managing transport requests.
    *   Provides endpoints for fetching transporter profiles.
    *   Handles business logic, validation, and interacts with the database.
    *   Supabase Functions will be used for implementing privacy-preserving contact mechanisms (e.g., mediated contact requests).
*   **Supabase PostgreSQL Database:**
    *   Stores all persistent data, including user information (via Auth), transport requests, and transporter profiles.
*   **Supabase Auth:**
    *   Manages user authentication via Google Social Login, as specified in the PRD.

### 4.3. Transporter Data Management (Dependency)

*   An external system or administrative interface is required for populating and maintaining the accuracy of transporter information within the Supabase database. This is a critical dependency for the "Browse Transporters" functionality.

## 5. Data Models

Data will be stored in Supabase PostgreSQL using the following primary tables:

### 5.1. `transport_requests`

Stores information about transport requests posted by farmers.

| Column Name                 | Data Type     | Constraints                                       | Description                                                                 |
|-----------------------------|---------------|---------------------------------------------------|-----------------------------------------------------------------------------|
| `request_id`                | `UUID`        | Primary Key, Default `gen_random_uuid()`          | Unique identifier for the transport request.                                |
| `farmer_user_id`            | `UUID`        | Foreign Key (references `auth.users(id)`), Not Null | ID of the farmer who posted the request.                                    |
| `produce_type`              | `TEXT`        | Not Null                                          | Type of produce to be transported.                                          |
| `quantity`                  | `TEXT`        | Nullable                                          | Quantity of produce (e.g., "50 kg", "10 bags").                             |
| `preferred_transport_date`  | `DATE`        | Not Null                                          | Desired date for transportation.                                            |
| `pickup_location_text`      | `TEXT`        | Not Null                                          | Textual description of the pickup location.                                 |
| `pickup_location_latitude`  | `DECIMAL`     | Nullable                                          | Latitude of the pickup location (optional).                                 |
| `pickup_location_longitude` | `DECIMAL`     | Nullable                                          | Longitude of the pickup location (optional).                                |
| `notes`                     | `TEXT`        | Nullable                                          | Additional notes or details from the farmer.                                |
| `status`                    | `TEXT`        | Not Null, Default `'pending_submission'`          | Current status (e.g., `pending_submission`, `posted`, `interest_shown`, `cancelled`). |
| `client_temp_id`            | `TEXT`        | Nullable, Unique (`farmer_user_id`, `client_temp_id`) | Temporary ID assigned by client for offline request reconciliation.         |
| `created_at`                | `TIMESTAMPZ`  | Not Null, Default `now()`                         | Timestamp of creation.                                                      |
| `updated_at`                | `TIMESTAMPZ`  | Not Null, Default `now()`                         | Timestamp of last update.                                                   |

### 5.2. `transporters`

Stores profiles of available transporters.

| Column Name                         | Data Type     | Constraints                               | Description                                                                    |
|-------------------------------------|---------------|-------------------------------------------|--------------------------------------------------------------------------------|
| `transporter_id`                    | `UUID`        | Primary Key, Default `gen_random_uuid()`  | Unique identifier for the transporter.                                         |
| `name`                              | `TEXT`        | Not Null                                  | Name of the transporter or transport service.                                  |
| `vehicle_type_capacity`             | `TEXT`        | Nullable                                  | Description of vehicle type and capacity (e.g., "Tata Ace - 750kg").           |
| `contact_info_primary`              | `TEXT`        | Nullable                                  | Primary contact information (e.g., phone number, potentially masked/encrypted). |
| `contact_info_display_preference`   | `TEXT`        | Nullable, Default `'mediated'`            | Preference for displaying contact info (`direct`, `mediated_call`, `mediated_message`). |
| `service_areas_description`         | `TEXT`        | Nullable                                  | Textual description of service areas.                                          |
| `is_active`                         | `BOOLEAN`     | Not Null, Default `true`                  | Whether the transporter profile is currently active and visible.               |
| `profile_last_verified_at`          | `TIMESTAMPZ`  | Nullable                                  | Timestamp of when the profile was last verified (if applicable).               |
| `created_at`                        | `TIMESTAMPZ`  | Not Null, Default `now()`                 | Timestamp of creation.                                                         |
| `updated_at`                        | `TIMESTAMPZ`  | Not Null, Default `now()`                 | Timestamp of last update.                                                      |

*(Note: The `auth.users` table is provided by Supabase Auth and will contain user profile information linked via `farmer_user_id`.)*

### 5.3. (Optional) `transport_request_interests`

To facilitate mediated contact between transporters and farmers without exposing farmer contact details directly on requests.

| Column Name             | Data Type     | Constraints                               | Description                                                                 |
|-------------------------|---------------|-------------------------------------------|-----------------------------------------------------------------------------|
| `interest_id`           | `UUID`        | Primary Key, Default `gen_random_uuid()`  | Unique identifier for the interest expression.                              |
| `request_id`            | `UUID`        | Foreign Key (`transport_requests`), Not Null | The transport request the interest is for.                                  |
| `transporter_id`        | `UUID`        | Foreign Key (`transporters`), Not Null      | The transporter expressing interest.                                        |
| `message_to_farmer`     | `TEXT`        | Nullable                                  | Optional message from the transporter to the farmer.                        |
| `status`                | `TEXT`        | Not Null, Default `'pending_review'`      | Status (e.g., `pending_review`, `farmer_notified`, `accepted`, `declined`). |
| `created_at`            | `TIMESTAMPZ`  | Not Null, Default `now()`                 | Timestamp of interest expression.                                           |

## 6. Data Flow & Interactions

### 6.1. Creating a Transport Request

*   **Online:**
    1.  Farmer fills the request form in the UI.
    2.  Client app validates input and sends the request data (including `farmer_user_id` from auth) to the `POST /transport-requests` Supabase API endpoint.
    3.  Supabase API authenticates the user, validates data, and inserts a new record into the `transport_requests` table with `status = 'posted'`.
    4.  A success confirmation is returned to the client.
*   **Offline:**
    1.  Farmer fills the request form in the UI.
    2.  Client app validates input and stores the request data locally in `Local Storage` with a `client_temp_id` and `status = 'pending_submission'`.
    3.  A confirmation ("Request queued") is shown to the farmer.
    4.  When network connectivity is restored, the `Offline Sync Service`:
        a.  Retrieves queued requests from `Local Storage`.
        b.  Sends each request to the `POST /transport-requests` Supabase API endpoint.
        c.  The API processes the request. If successful, it returns the permanent `request_id`.
        d.  The client app updates the local record with the `request_id` and marks it as synced (or removes it from the queue).
        e.  **Conflict Handling:** The API should be idempotent for request creation based on `client_temp_id` and `farmer_user_id` to prevent duplicates if a sync is interrupted and retried.

### 6.2. Browsing Transporters

*   **Online:**
    1.  User navigates to the "Browse Transporters" screen.
    2.  Client app calls the `GET /transporters` Supabase API endpoint (potentially with location filters if implemented).
    3.  Supabase API retrieves transporter profiles from the `transporters` table.
    4.  Data is returned to the client, displayed in the UI, and cached in `Local Storage` by the `Transporter Browsing Module` with a timestamp.
*   **Offline:**
    1.  User navigates to the "Browse Transporters" screen.
    2.  Client app checks `Local Storage` for cached transporter data.
    3.  If available, cached data is displayed. The UI indicates that the data is cached and shows the last update time.
    4.  If no cache, an appropriate message is shown.
*   **Cache Update Strategy:**
    *   The `Offline Sync Service` will periodically attempt to refresh the transporter cache when the device is online.
    *   A Time-To-Live (TTL) approach can be used for cache entries, or a versioning system if data changes frequently and immediate consistency is critical (though TTL is simpler for MVP).

### 6.3. Privacy-Preserving Contact

*   **Farmer Requests:** Farmer contact details are not directly shown.
    *   Transporters viewing a request can "Express Interest" via a button.
    *   This action could trigger a Supabase Function to:
        1.  Create a record in `transport_request_interests`.
        2.  Notify the farmer (e.g., in-app notification, or a section showing interests received).
        3.  The farmer can then choose to reveal their contact info or initiate contact through a mediated channel.
*   **Transporter Listings:**
    *   Transporter contact info can be displayed based on `contact_info_display_preference`.
    *   For `'mediated'` options, a "Contact" button could trigger a Supabase Function that facilitates the call/message without immediately revealing the number, or sends a contact request to the transporter.

## 7. Technology Choices

*   **Backend Platform:** Supabase (PostgreSQL, Auth, Functions, Realtime for potential notifications).
*   **Frontend Client:** Mobile application (specific framework like React Native, Flutter, or Native Android/iOS to be determined by overall project strategy).
*   **Offline Storage (Client):** Device-native capabilities (e.g., SQLite, SharedPreferences/UserDefaults) or a dedicated mobile database library compatible with the chosen frontend framework. Supabase client libraries may offer features for offline support that should be investigated.

## 8. Privacy and Security Considerations

*   **Data Encryption:** All communication between the client and Supabase backend will be over HTTPS. Sensitive data at rest in Supabase should leverage PostgreSQL's security features.
*   **Authentication & Authorization:** Supabase Auth (Google Social Login) will manage user identities. API endpoints will be protected and require authenticated user tokens. Row Level Security (RLS) in Supabase will be configured to ensure users can only access/modify their own data or data they are permitted to see (e.g., farmers their requests, transporters their profiles if they can manage them).
*   **Farmer Privacy:** As detailed in Data Flow, direct contact information for farmers will not be publicly listed with requests. Mediated contact or an "express interest" system will be used.
*   **Transporter Privacy:** Transporters will have options for how their contact information is displayed, potentially opting for mediated contact.
*   **Input Validation:** Rigorous input validation will be performed on both client and server-side to prevent common vulnerabilities.

## 9. Dependencies

*   **User Authentication Module:** Fully functional Google Social Login integrated via Supabase Auth.
*   **Transporter Data Population & Management:** A reliable mechanism (e.g., admin interface, separate transporter registration flow) to input, verify, and update transporter information in the `transporters` table. This is a critical external dependency for the "Browse Transporters" feature.
*   **Core Application Framework:** Basic app shell, navigation, UI component library, and potentially a shared offline synchronization framework if used by other modules.
*   **Location Services (Optional but Recommended):** For "nearby" transporter functionality, access to device location services (with user permission) is needed. The system should gracefully handle cases where location is unavailable.
*   **Language Support Framework:** If multi-language support is required, integration with the app's i18n/l10n framework.

## 10. Constraints & Assumptions

*   **Network Connectivity:** The application is designed for areas with potentially intermittent network connectivity.
*   **Supabase Capabilities:** The architecture relies on Supabase features being suitable for the scale and specific requirements (e.g., function execution limits, database performance).
*   **Simple UI/UX:** The primary driver is simplicity, complex matching algorithms or real-time tracking are out of scope for this module's MVP.
*   **Transporter Data Accuracy:** The accuracy of transporter data relies on the external data population and management process.

## 11. Future Considerations

*   In-app messaging between farmers and transporters.
*   Rating and review system.
*   Map-based views for locations and service areas.
*   Notifications to transporters about new relevant requests.