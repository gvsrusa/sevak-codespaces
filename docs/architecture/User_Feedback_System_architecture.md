# High-Level Architecture: User Feedback System

## 1. Introduction

This document outlines the high-level architecture for the User Feedback System module within the AgriConnect application.

### 1.1. Purpose
The primary purpose of this module is to enable users, primarily small and marginal farmers, to submit their comments and ratings regarding the AgriConnect application's usability, content, and overall experience. This feedback is crucial for the iterative improvement of the platform.

### 1.2. Scope
This module includes:
*   A user interface for submitting textual comments and simple ratings.
*   Backend logic for receiving, validating, and storing feedback.
*   Offline capability to queue feedback and synchronize it when connectivity is restored.
*   Association of feedback with authenticated users (where applicable and consented) or anonymous submission.

Key architectural drivers include simplicity of use, ease of submission, reliable storage in Supabase (PostgreSQL), and ensuring user privacy.

## 2. Architectural Approach

The User Feedback System will be integrated as a feature within the main AgriConnect mobile application, following a monolithic client-server architecture. It is not designed as a separate microservice for the MVP.

*   **Client-Side:** The mobile application will host the UI components, submission logic, and offline queuing mechanism.
*   **Server-Side:** Supabase will serve as the backend, providing database storage (PostgreSQL) and potentially serverless functions (Edge Functions) for API endpoints.

## 3. Components

The system comprises the following key components:

### 3.1. Feedback UI Component (Mobile App)
*   **Responsibilities:**
    *   Provides a simple and intuitive interface for users to input textual comments.
    *   Allows users to select a rating (e.g., 1-5 stars or a simple qualitative scale).
    *   Clearly accessible from a designated point in the app (e.g., settings menu, dedicated feedback button).
    *   Displays confirmation messages upon successful submission or queuing.
    *   Adheres to accessibility guidelines (large fonts, high contrast).
*   **Interactions:**
    *   Receives user input.
    *   Communicates with the Feedback Submission Service.

### 3.2. Feedback Submission Service (Mobile App)
*   **Responsibilities:**
    *   Collects feedback data (comment, rating) from the UI Component.
    *   Retrieves authenticated `userId` from the User Authentication Module (if available and consented).
    *   Gathers metadata: `appVersion`, `platform`, `deviceInfo` (optional).
    *   Manages API calls to the Feedback API Endpoint.
    *   Handles local storage for offline feedback queuing.
    *   Implements synchronization logic to send queued feedback when network connectivity is restored, interacting with the Network Connectivity Module.
*   **Interactions:**
    *   Interfaces with the Feedback UI Component.
    *   Interfaces with the User Authentication Module.
    *   Interfaces with the Network Connectivity Module.
    *   Makes requests to the Feedback API Endpoint.

### 3.3. Feedback API Endpoint (Backend - Supabase)
*   **Responsibilities:**
    *   Receives feedback data submitted by the client application via a `POST` request (e.g., `/api/v1/feedback` as per spec).
    *   Validates the incoming data (e.g., rating range, data types).
    *   Handles authentication/authorization if feedback is linked to a user (verifies Bearer Token if provided).
    *   Persists the validated feedback data into the `user_feedback` table in the Supabase PostgreSQL database.
    *   Returns appropriate success or error responses to the client.
*   **Implementation:** Can be implemented using Supabase Edge Functions for custom logic and validation, or via direct Supabase SDK calls from the client if the logic is minimal and security rules are robust. Edge Functions are preferred for better encapsulation and security.
*   **Interactions:**
    *   Receives requests from the Feedback Submission Service.
    *   Interacts with the Feedback Data Store (Supabase PostgreSQL).

### 3.4. Feedback Data Store (Supabase PostgreSQL)
*   **Responsibilities:**
    *   Securely stores all submitted user feedback.
    *   Utilizes a dedicated table (e.g., `user_feedback`) with appropriate schema.
*   **Technology:** Supabase PostgreSQL.
*   **Interactions:**
    *   Accessed by the Feedback API Endpoint for writing feedback data.
    *   Accessed by administrators (via a separate admin interface/tool, out of scope for this module's direct implementation) for reviewing feedback.

### 3.5. Dependent Modules (Existing)
*   **User Authentication Module:** Provides `userId` for non-anonymous feedback.
*   **Network Connectivity Module:** Used by the Feedback Submission Service to detect online/offline status and manage synchronization.

## 4. Data Flow

### 4.1. Online Feedback Submission
1.  **User Input:** User accesses the Feedback UI Component and enters their comment and/or rating.
2.  **Data Collection:** The Feedback Submission Service collects this input, along with `userId` (if applicable), `appVersion`, `platform`, and `deviceInfo`.
3.  **API Request:** The service makes a `POST` request to the Feedback API Endpoint with the collected data in the request body (JSON format).
4.  **Backend Processing:** The Feedback API Endpoint validates the data. If valid, it inserts a new record into the `user_feedback` table in Supabase PostgreSQL.
5.  **API Response:** The API Endpoint returns a success (e.g., 201 Created) or error (e.g., 400 Bad Request, 500 Internal Server Error) response to the client.
6.  **Confirmation:** The Feedback UI Component displays a confirmation message to the user.

*(Conceptual Sequence: User -> Feedback UI -> Feedback Submission Service -> Feedback API Endpoint -> Supabase DB)*

### 4.2. Offline Feedback Submission & Synchronization
1.  **User Input (Offline):** User submits feedback while the device is offline.
2.  **Offline Detection:** The Feedback Submission Service, using the Network Connectivity Module, detects the offline status.
3.  **Local Queuing:** The feedback data (comment, rating, metadata) is stored locally on the device (e.g., using SQLite, AsyncStorage, or a similar mechanism provided by the mobile framework).
4.  **Queued Confirmation:** The Feedback UI Component informs the user that their feedback has been saved and will be sent when connectivity is restored.
5.  **Network Restored:** When the Network Connectivity Module detects that the device is back online:
    a.  The Feedback Submission Service retrieves queued feedback items from local storage.
    b.  For each queued item, it attempts to send it to the Feedback API Endpoint (as per the online submission flow, steps 3-5).
    c.  Upon successful submission to the server, the corresponding item is removed from the local queue or marked as synced.
    d.  Error handling for failed sync attempts (e.g., retry logic with backoff) should be considered.

*(Conceptual Sequence (Offline): User -> Feedback UI -> Feedback Submission Service -> Local Storage)*
*(Conceptual Sequence (Sync): Network Module -> Feedback Submission Service -> Feedback API Endpoint -> Supabase DB)*

## 5. Data Model (Supabase PostgreSQL)

A table named `user_feedback` will store the feedback.

| Column Name         | Data Type     | Constraints & Notes                                      |
|---------------------|---------------|----------------------------------------------------------|
| `id`                | `UUID`        | Primary Key, auto-generated (e.g., `uuid_generate_v4()`) |
| `user_id`           | `UUID`        | Foreign Key to `auth.users` table (`id` column), Nullable. Links to the authenticated user. Null if anonymous or user not logged in. |
| `rating`            | `SMALLINT`    | Nullable. E.g., 1 to 5.                                  |
| `comment`           | `TEXT`        | Nullable. User's textual feedback.                       |
| `app_version`       | `VARCHAR(20)` | Not Null. E.g., "1.0.2".                                 |
| `platform`          | `VARCHAR(20)` | Not Null. E.g., "Android", "iOS".                        |
| `device_info`       | `JSONB`       | Nullable. Stores `osVersion`, `model`, etc.              |
| `submitted_at_client`| `TIMESTAMPTZ` | Not Null. Timestamp from the client when feedback was submitted/queued. |
| `created_at`        | `TIMESTAMPTZ` | Not Null, `DEFAULT now()`. Server-side timestamp.        |
| `is_anonymous`      | `BOOLEAN`     | Not Null, `DEFAULT false`. True if `user_id` is null or user explicitly chose anonymous submission. |

**Indexes:**
*   On `user_id` (if frequent lookups by user are needed for admin purposes).
*   On `created_at` (for chronological sorting).

## 6. Technology Choices

*   **Frontend (Mobile App):** To be built using the primary framework chosen for AgriConnect (e.g., React Native, Flutter, Native Android/iOS). The Feedback System will be a module within this framework.
*   **Backend API:** Supabase Edge Functions (TypeScript/JavaScript) are recommended for implementing the `POST /api/v1/feedback` endpoint. This allows for custom validation, data transformation, and interaction with the database.
*   **Database:** Supabase PostgreSQL, as mandated by the PRD ([`docs/PRD.md:34`](docs/PRD.md:34), [`docs/PRD.md:45`](docs/PRD.md:45)).
*   **Offline Storage (Client-side):** The specific mechanism will depend on the chosen mobile framework (e.g., AsyncStorage for React Native, SQLite via a plugin, WatermelonDB).
*   **Authentication:** Leverages the existing Google Social Login via Supabase Auth. Bearer tokens (JWTs issued by Supabase) will be used if feedback is associated with an authenticated user.

## 7. Key Architectural Decisions & Considerations

*   **Simplicity and Usability:** The design prioritizes an extremely simple and intuitive user experience for feedback submission, catering to users with low digital literacy (PRD Line 37, 46, 51).
*   **Offline-First Approach:** Robust offline queuing and automatic synchronization upon network restoration are critical requirements (PRD Line 35, 41; Spec AC6, FR6, FR7).
*   **User Context vs. Anonymity:** The system supports linking feedback to authenticated users for better context while respecting privacy and allowing for anonymous submissions if `user_id` is not available or if explicitly chosen (Spec AC4, AC8, NFR6).
*   **API Design:** The API endpoint (`POST /api/v1/feedback`) and its request/response structure will align with the illustrative design in the feature specification ([`docs/specs/User_Feedback_System_overview.md:101`](docs/specs/User_Feedback_System_overview.md:101)).
*   **Scalability:** Supabase provides inherent scalability for the database and Edge Functions. The design aims for stateless API interactions.
*   **Security:**
    *   All client-server communication via HTTPS.
    *   Supabase Row Level Security (RLS) will be configured on the `user_feedback` table:
        *   Authenticated users (or anonymous sessions if allowed by policy) can `INSERT`.
        *   Specific admin roles can `SELECT` all feedback.
        *   Users should not be able to `SELECT`, `UPDATE`, or `DELETE` others' feedback or their own once submitted.
    *   Input validation on the backend (Edge Function) to prevent malformed data and potential injection attacks (though direct display of feedback is out of MVP scope, good practice).
    *   Sanitization of `comment` text if it were ever to be displayed.
*   **Reliability:** Focus on ensuring no feedback is lost, especially during offline-to-online transitions. Error handling and retry mechanisms for sync are important.
*   **Data Integrity:** Ensure accurate capture and storage of feedback data. Timestamps (client and server) help in understanding submission times.

## 8. Dependencies

*   **User Authentication System (Google Social Login via Supabase Auth):** Required for associating feedback with authenticated users (PRD Line 14, 44; Spec Dependency 1).
*   **Database System (Supabase PostgreSQL):** Core for storing feedback data (PRD Line 34, 45; Spec Dependency 2).
*   **Network Connectivity Module (Client-side):** Essential for detecting online/offline status and managing synchronization logic (PRD Line 35, 41; Spec Dependency 3).
*   **Core Application UI Framework:** The Feedback UI will be built using and integrated into this framework (Spec Dependency 4).
*   **App Versioning Information:** To log the app version with the feedback for context (Spec Dependency 5).

## 9. Constraints

*   The backend database **must** be Supabase PostgreSQL.
*   The UI and interaction flow **must** be extremely simple and accessible for users with low digital literacy.
*   User privacy **must** be strictly adhered to, especially concerning identifiable information linked to feedback (PRD Line 47).
*   The MVP scope defined in the feature specification ([`docs/specs/User_Feedback_System_overview.md:67`](docs/specs/User_Feedback_System_overview.md:67)) must be respected (e.g., no in-app display of feedback, no advanced analytics in this module).

## 10. Future Considerations (Out of Scope for MVP Architecture)

*   Admin interface for viewing, filtering, and analyzing feedback.
*   Integration with analytics tools for sentiment analysis or trend identification.
*   Mechanisms for responding to feedback (though out of scope for MVP).