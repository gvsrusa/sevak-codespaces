# High-Level Architecture: Marketplace (Produce Listing & Browsing)

## 1. Introduction

This document outlines the high-level architecture for the "Marketplace (Produce Listing & Browsing)" module of the AgriConnect application. This module enables farmers to create, manage, and browse produce listings, with a strong emphasis on simplicity, offline functionality, and data integrity, leveraging Supabase as the backend.

This architectural design is based on the requirements detailed in:
- AgriConnect PRD: [`docs/PRD.md`](docs/PRD.md:1)
- Feature Overview Specification: Marketplace (Produce Listing & Browsing): [`docs/specs/Marketplace_Produce_Listing_Browsing_overview.md`](docs/specs/Marketplace_Produce_Listing_Browsing_overview.md:1)

## 2. Architectural Goals & Constraints

The architecture is driven by the following key goals and constraints:

- **Simplicity for Low-Tech Users:** The UI/UX and overall system design must be intuitive and easy to use for farmers with limited digital literacy (PRD Line 23, 37, 53).
- **Offline Functionality (Caching & Sync):** Users must be able to create listings and view their own/cached listings while offline. Data should synchronize when connectivity is restored (PRD Line 35, 41; Spec FR5, FR6, FR7).
- **Data Integrity & Security:** Ensure accurate data storage, robust synchronization, and protection against unauthorized modifications. User privacy regarding seller information is paramount (PRD Line 47; Spec NFR3, NFR5).
- **Scalable Listing Management:** The system should efficiently handle a growing number of users and produce listings.
- **Supabase (PostgreSQL) Backend:** The backend infrastructure, including database and authentication, will utilize Supabase (PRD Line 34, 45).

## 3. High-Level Architecture Overview

The architecture comprises two main parts: the client-side mobile application and the Supabase backend.

```mermaid
graph TD
    A[Mobile Client (Android)] -- HTTPS/API Calls --> B(Supabase Backend);
    A --> C{Local Cache (SQLite)};
    C -- Sync --> B;
    B --> D[PostgreSQL Database];
    B --> E[Supabase Auth];

    subgraph Mobile Client (Android)
        direction LR
        UI[UI Layer (Views for Listing Creation, Browsing, Management)]
        BL[Business Logic Layer (Input Validation, Offline Logic)]
        DM[Data Management Layer (Local Cache Access, Sync Manager)]
        UI --> BL;
        BL --> DM;
    end

    subgraph Supabase Backend
        direction LR
        API[API Endpoints (RESTful via Supabase)]
        AuthN[Authentication (Supabase Auth)]
        DB[Database (PostgreSQL)]
        API --> AuthN;
        API --> DB;
    end
```

### 3.1. Key Components

#### 3.1.1. Client-Side (Mobile Application - Android)
-   **UI Layer:** Provides views for creating new listings, browsing existing listings, and managing a farmer's own listings. Designed for extreme simplicity and clarity.
-   **Business Logic Layer (BLL):**
    -   Handles user input validation from the UI.
    -   Manages application state related to listings.
    -   Implements the core logic for online and offline operations.
    -   Interacts with the Data Management Layer for data persistence and retrieval.
-   **Data Management Layer (DML):**
    -   **Local Cache (SQLite):** Stores produce listings locally. This includes listings created offline, the user's own listings for offline viewing, and a subset of recently browsed marketplace listings.
    -   **Sync Manager:** Responsible for detecting network connectivity changes and synchronizing local data with the Supabase backend. It handles pushing offline-created/modified listings and pulling updates.

#### 3.1.2. Backend (Supabase)
-   **API Endpoints:** RESTful APIs (provided by Supabase or custom Edge Functions if necessary) for CRUD operations on produce listings (e.g., `POST /listings`, `GET /listings`, `PUT /listings/{id}`, `DELETE /listings/{id}`). These align with the conceptual API notes in the feature specification ([`docs/specs/Marketplace_Produce_Listing_Browsing_overview.md#9-high-level-api-design-notes-conceptual`](docs/specs/Marketplace_Produce_Listing_Browsing_overview.md:106)).
-   **Database (PostgreSQL):** Stores all produce listings, user associations, and related metadata. Managed by Supabase.
-   **Authentication (Supabase Auth):** Integrates with Google Social Login (as handled by the User Authentication module) to secure endpoints and associate listings with authenticated users.

## 4. Data Model

### 4.1. Supabase `produce_listings` Table Schema
```sql
CREATE TABLE produce_listings (
    listing_id UUID PRIMARY KEY DEFAULT gen_random_uuid(),
    user_id UUID NOT NULL REFERENCES auth.users(id), -- Foreign key to Supabase auth users table
    produce_name TEXT NOT NULL,
    quantity NUMERIC NOT NULL,
    unit TEXT NOT NULL, -- e.g., "kg", "quintal", "piece"
    price_per_unit NUMERIC NOT NULL,
    currency TEXT NOT NULL DEFAULT 'INR',
    listing_status TEXT NOT NULL DEFAULT 'active', -- e.g., "active", "inactive", "deleted"
    created_at TIMESTAMPTZ DEFAULT NOW(),
    updated_at TIMESTAMPTZ DEFAULT NOW(),
    -- Optional: for richer querying or display, if location is tied to listing not just user profile
    -- location_latitude NUMERIC,
    -- location_longitude NUMERIC,
    -- location_description TEXT
);

-- Index for common query patterns
CREATE INDEX idx_produce_listings_user_id ON produce_listings(user_id);
CREATE INDEX idx_produce_listings_status ON produce_listings(listing_status);
CREATE INDEX idx_produce_listings_produce_name ON produce_listings(produce_name);
```
*Note: `user_id` links to the `id` in `auth.users` table managed by Supabase Authentication.*

### 4.2. Local Cache Schema (SQLite - Conceptual)
A similar structure to `produce_listings` will be used in the local SQLite database, with additional fields for managing offline state:
-   `sync_status` (TEXT, e.g., "synced", "pending_creation", "pending_update", "pending_deletion")
-   `last_synced_at` (TIMESTAMP)
-   `local_changes` (JSON/TEXT, to store pending modifications if granular diffs are needed for sync)

## 5. Data Flow & Interactions

### 5.1. Online Scenarios
1.  **Create Listing:**
    -   User fills listing form (UI Layer).
    -   Data validated (BLL).
    -   API call `POST /api/v1/marketplace/listings` made to Supabase (BLL via DML).
    -   Supabase saves to PostgreSQL, returns success/failure.
    -   UI updated, listing potentially cached locally (DML).
2.  **Browse Listings:**
    -   User navigates to marketplace (UI Layer).
    -   API call `GET /api/v1/marketplace/listings` made to Supabase (BLL via DML).
    -   Supabase retrieves listings from PostgreSQL.
    -   Listings displayed (UI Layer), cached locally for offline use (DML).
3.  **Manage Own Listings (Edit/Delete):**
    -   User views their listings (fetched via `GET /api/v1/marketplace/listings/my`).
    -   User initiates edit/delete (UI Layer).
    -   Data validated (BLL).
    -   API call `PUT /api/v1/marketplace/listings/{id}` or `DELETE /api/v1/marketplace/listings/{id}` made to Supabase (BLL via DML).
    -   Supabase updates/deletes in PostgreSQL.
    -   UI updated, local cache updated/invalidated (DML).

### 5.2. Offline Scenarios
1.  **Create Listing Offline:**
    -   User fills listing form (UI Layer).
    -   Data validated (BLL).
    -   Listing saved to Local Cache (SQLite) with `sync_status = "pending_creation"` (BLL via DML).
    -   UI confirms local save (UI Layer).
2.  **View Own Listings Offline:**
    -   User navigates to "My Listings" (UI Layer).
    -   Listings fetched from Local Cache (BLL via DML).
    -   Listings displayed (UI Layer).
3.  **Browse Marketplace Offline:**
    -   User navigates to marketplace (UI Layer).
    -   Cached listings (potentially stale) fetched from Local Cache (BLL via DML).
    -   Listings displayed with an indicator of offline/cached data (UI Layer).

### 5.3. Synchronization Strategy
-   **Trigger:** The Sync Manager activates on network connectivity restoration or periodically if the app is active.
-   **Process:**
    1.  **Push Local Changes:**
        -   Query Local Cache for items with `sync_status` like "pending_creation", "pending_update", "pending_deletion".
        -   For each pending item, make the corresponding API call to Supabase.
        -   On successful API response, update the item's `sync_status` to "synced" in Local Cache.
    2.  **Pull Remote Changes (Optional for MVP, but good for consistency):**
        -   Fetch latest listings relevant to the user (e.g., their own, or recently viewed categories if applicable) from Supabase.
        -   Update Local Cache with these fresh listings, resolving any minor discrepancies.
-   **Conflict Resolution:**
    -   For MVP, a "last write wins" strategy is simplest. If a listing is modified locally and on the server while offline, the version synced last will overwrite.
    -   Alternatively, for creations, if a locally created listing fails to sync due to a unique constraint (e.g., re-creation attempt), the user could be notified.
    -   More complex scenarios (e.g., editing a listing that was deleted on the server) will default to server state or notify the user. Simplicity is key.
-   **Background Sync:** Consider a background service for synchronization if the app is not in the foreground, respecting battery life and data usage.

## 6. Technology Choices

-   **Frontend (Mobile App):** Android Native (Kotlin/Java) is the primary focus as per PRD Line 40. Cross-platform frameworks (e.g., Flutter, React Native) could be considered if the overall project strategy favors them for broader reach or development speed, but this module's architecture assumes native Android capabilities for robust offline storage and background processing.
-   **Backend:** Supabase (as mandated by PRD Line 34, 45)
    -   Database: PostgreSQL
    -   Authentication: Supabase Auth
    -   APIs: Supabase auto-generated REST APIs or custom Supabase Edge Functions.
-   **Local Storage:** SQLite, natively supported on Android.
-   **API Communication:** HTTPS with RESTful principles.

## 7. Security Considerations

-   **Authentication:** All operations creating or modifying listings (POST, PUT, DELETE) will be protected and require an authenticated user session, managed via Supabase Auth (linked to Google Social Login from the User Authentication module).
-   **Authorization:** Farmers can only edit or delete their own listings. This will be enforced by backend logic (e.g., Row Level Security in Supabase or checks in API functions) verifying `user_id` ownership.
-   **Data Integrity:**
    -   Input validation on both client-side (for responsiveness) and server-side (for security).
    -   Robust error handling in the sync mechanism to minimize data loss.
    -   Transactional updates in Supabase where appropriate.
-   **Privacy:** Seller information displayed in public listings will be limited (e.g., farmer's display name or a non-identifiable reference, not direct contact details like phone numbers, as per PRD Line 47). Full contact details are not part of this module's scope for marketplace browsing.
-   **Secure API Communication:** All API calls will use HTTPS.

## 8. Scalability & Performance

-   **Backend Scalability:** Supabase is designed for scalability, handling database connections, queries, and API traffic.
-   **Client-Side Performance:**
    -   Efficient local database (SQLite) queries.
    -   Pagination for browsing large lists of produce.
    -   Caching of listings to reduce network requests and improve load times.
    -   Optimized data synchronization to minimize data transfer, especially on low-bandwidth connections.
    -   UI designed for responsiveness even during background sync operations.
-   **Database Optimization:** Proper indexing on `produce_listings` table columns frequently used in queries (e.g., `user_id`, `listing_status`, `produce_name`).

## 9. Dependencies

-   **User Authentication & Profile Management Module:** Essential for obtaining the authenticated `user_id` to associate with listings and for securing endpoints. (PRD Line 14, 27, 44; Spec Dependency 85)
-   **Supabase Platform:** For backend database, authentication, and API hosting. (PRD Line 34, 45; Spec Dependency 86)
-   **Core Application Framework (Android):** For UI rendering, navigation, network capabilities, and local storage access. (Spec Dependency 89)
-   **Device Local Storage:** Required for offline caching. (Spec Dependency 88)
-   **Network Connectivity:** Intermittently required for synchronization and fetching fresh data. (Spec Dependency 87)

## 10. Future Considerations (Post-MVP)

-   **Real-time Updates:** Supabase Realtime capabilities could be used for live updates to the marketplace feed.
-   **Advanced Search/Filtering:** More complex querying capabilities for listings.
-   **Image Uploads:** Allowing farmers to add photos to their listings.
-   **Smarter Caching:** More sophisticated caching strategies for browsed listings based on user preferences or location.