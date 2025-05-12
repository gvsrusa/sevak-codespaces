# High-Level Architecture: Market Price Discovery Module

## 1. Introduction

This document outlines the high-level architecture for the "Market Price Discovery" module of the AgriConnect application. The primary purpose of this module is to provide small and marginal Indian farmers with access to real-time or near real-time local commodity prices for their key crops, enabling them to make informed selling decisions.

Key goals for this module, derived from the AgriConnect PRD ([`docs/PRD.md`](docs/PRD.md)) and the Feature Overview Specification ([`docs/specs/Market_Price_Discovery_overview.md`](docs/specs/Market_Price_Discovery_overview.md)), include:
- Fetching and displaying real-time, local commodity prices (PRD line [`17`](docs/PRD.md:17), [`28`](docs/PRD.md:28)).
- Integration with external data sources or an internal database for price data (PRD line [`28`](docs/PRD.md:28)).
- Offline functionality: Caching of last-fetched price data locally and displaying data freshness timestamps (PRD line [`35`](docs/PRD.md:35), [`41`](docs/PRD.md:41), [`57`](docs/PRD.md:57)).
- A simple and intuitive UI for crop and market selection (PRD line [`23`](docs/PRD.md:23), [`37`](docs/PRD.md:37)).
- Ensuring data accuracy and reliability.

## 2. Architectural Drivers

The design of this module is guided by the following key architectural drivers:
-   **Reliable Price Data Sourcing:** The system must integrate with dependable sources for accurate and timely price information.
-   **Offline Price Caching:** Users must be able to access previously fetched price data when offline.
-   **Data Freshness Indication:** The UI must clearly display when the price data was last updated.
-   **Simple UI for Selection:** The interface for selecting crops and markets must be straightforward and easy to use for individuals with limited digital literacy.
-   **Supabase for data storage/management:** Leveraging Supabase for managing lists of crops, markets, and potentially for logging or intermediate caching of price data.

## 3. High-Level Architecture Overview

The Market Price Discovery module will follow a client-server architecture. The mobile client application will be responsible for user interaction and local data caching. The backend server will handle data aggregation from external sources, manage master lists of crops and markets, and serve data to the client.

**Conceptual Component Diagram:**

*   **Client (Mobile App):**
    *   Market Price UI (View)
    *   Price Data Service (Controller/ViewModel)
    *   Local Price Cache (Model/Repository)
*   **Backend (AgriConnect Server):**
    *   Market Price API Endpoint (e.g., `/api/v1/marketprices`)
    *   Price Aggregation Service (Business Logic)
    *   Supabase Database (Data Store for crops, markets, logs)
*   **External Systems:**
    *   External Price Data Provider API(s)

## 4. Key Components

### 4.1. Client-Side (Mobile App Module)

*   **UI Component:**
    *   Responsibilities: Provides dropdowns or lists for crop and market selection. Displays the fetched/cached price, unit, currency, and the `last_updated` timestamp. Indicates offline status or data source.
    *   Interactions: Receives user input, requests data from the `Price Data Service`, and renders information.
*   **Price Data Service/Manager:**
    *   Responsibilities: Core logic for fetching price data (online/offline). Manages interactions with the backend API and the `Local Cache`. Determines data freshness and handles error scenarios.
    *   Interactions: Called by the UI Component. Makes network requests to the backend. Reads from and writes to the `Local Cache`.
*   **Local Cache:**
    *   Responsibilities: Stores recently fetched price data (crop, market, price, timestamp) on the device. Provides data when the device is offline or backend requests fail.
    *   Technology: SQLite (e.g., via Room Persistence Library on Android).
    *   Interactions: Accessed by the `Price Data Service` for reads and writes.

### 4.2. Backend/Server-Side (AgriConnect Backend)

*   **Market Price API Endpoint (`/api/v1/marketprices`):**
    *   Responsibilities: Exposes a GET endpoint for the mobile client to request price data based on `crop_id` and `market_id`.
    *   Interactions: Receives requests from the client's `Price Data Service`. Invokes the `Price Aggregation Service`.
*   **Price Aggregation Service:**
    *   Responsibilities: Fetches price data from one or more configured `External Price Data Provider(s)`. May implement caching strategies for external API responses to improve performance and reduce load on providers. Transforms data into the standard format defined in the API response. Handles errors from external providers.
    *   Technology: Can be implemented as a Supabase Function or a separate microservice (e.g., Node.js, Python).
    *   Interactions: Called by the Market Price API Endpoint. Makes HTTP requests to `External Price Data Provider(s)`. May read/write to a backend cache (e.g., Redis, or a Supabase table).
*   **Supabase (PostgreSQL Database):**
    *   Responsibilities:
        *   Stores and manages the master lists of available crops and markets (configurable via backend, as per FR8 in the spec).
        *   Potentially logs price data requests and responses for auditing or analytics.
        *   May store pre-aggregated or cleaned price data if external sources are unreliable or require significant pre-processing.
    *   Interactions: Accessed by the `Price Aggregation Service` (for crop/market metadata if needed during aggregation) and by separate admin interfaces/APIs for managing crop/market lists. The client app will also need an endpoint to fetch these lists.

### 4.3. External Components

*   **External Price Data Provider(s):**
    *   Responsibilities: Supply real-time or near real-time commodity price information via APIs.
    *   Examples: Government portals (e.g., Agmarknet), private data vendors.
    *   Interactions: Queried by the `Price Aggregation Service`.

## 5. Data Flow

### 5.1. Online Scenario (Fetching Fresh Data)
1.  User selects a crop and market in the mobile app's UI Component.
2.  The UI Component triggers the `Price Data Service` on the client.
3.  The `Price Data Service` makes an API call to the AgriConnect Backend's `/api/v1/marketprices` endpoint with `crop_id` and `market_id`.
4.  The backend's `Price Aggregation Service` receives the request.
5.  The `Price Aggregation Service` queries the relevant `External Price Data Provider(s)`. (It might first check its own short-term cache for this data).
6.  The external provider returns the latest price data.
7.  The `Price Aggregation Service` processes the data, standardizes it, and returns it in the API response to the client's `Price Data Service`.
8.  The client's `Price Data Service` receives the data, updates the `Local Cache` with the new price and `last_updated` timestamp.
9.  The `Price Data Service` provides the fresh data to the UI Component for display.

### 5.2. Offline Scenario (Using Cached Data)
1.  User selects a crop and market in the UI Component.
2.  The UI Component triggers the `Price Data Service`.
3.  The `Price Data Service` detects that the device is offline or that an attempt to fetch fresh data failed.
4.  It queries the `Local Cache` for the selected crop and market.
5.  If data exists in the cache, it's retrieved along with its `last_updated` timestamp.
6.  The `Price Data Service` provides the cached data to the UI Component.
7.  The UI Component displays the cached price and clearly indicates that the data is from the cache and shows its original update timestamp.

### 5.3. Cache Update Strategy
*   **Client-Side (Local Cache):** Cache-Aside. Data is fetched from the backend, and upon successful retrieval, the local cache is updated. The `last_updated` timestamp from the server response is crucial.
*   **Server-Side (Price Aggregation Service Cache - Optional):** Time-To-Live (TTL) based caching for responses from external providers to reduce redundant calls.

### 5.4. Crop and Market List Management
1.  The mobile app, on startup or periodically, fetches the list of available crops and markets from a dedicated backend API endpoint (e.g., `/api/v1/crops`, `/api/v1/markets`).
2.  These lists are stored in Supabase and managed by administrators.
3.  The client can cache these lists locally as well.

## 6. Data Models

### 6.1. Price Data (Client Local Cache & API Response)
```json
{
  "crop_id": "WHT001",
  "crop_name": "Wheat", // For display convenience
  "market_id": "MKT005",
  "market_name": "Mandi Name", // For display convenience
  "price": "1850.00",
  "unit": "Quintal",
  "currency": "INR",
  "last_updated": "2024-07-28T10:30:00Z", // ISO 8601 Timestamp from source
  "cached_at": "2024-07-28T10:35:00Z", // Timestamp of when client cached it (optional, for local diagnostics)
  "data_source_name": "Source X API" // Optional: for internal tracking or display
}
```
*Note: `crop_name` and `market_name` can be joined from local lists if only IDs are stored in the price cache to save space.*

### 6.2. Crop (Managed in Supabase)
-   `crop_id`: TEXT (Primary Key, e.g., "WHT001")
-   `name_en`: TEXT (English Name)
-   `name_local`: JSONB (Localized names, e.g., `{"hi": "गेहूं"}`)
-   `description_en`: TEXT (Optional)
-   `description_local`: JSONB (Optional)
-   `image_url`: TEXT (Optional)
-   `is_active`: BOOLEAN (Default: true)
-   `created_at`: TIMESTAMP WITH TIME ZONE
-   `updated_at`: TIMESTAMP WITH TIME ZONE

### 6.3. Market (Managed in Supabase)
-   `market_id`: TEXT (Primary Key, e.g., "MKT005")
-   `name_en`: TEXT (English Name)
-   `name_local`: JSONB (Localized names)
-   `district_en`: TEXT
-   `district_local`: JSONB
-   `state_en`: TEXT
-   `state_local`: JSONB
-   `latitude`: DECIMAL (Optional)
-   `longitude`: DECIMAL (Optional)
-   `is_active`: BOOLEAN (Default: true)
-   `created_at`: TIMESTAMP WITH TIME ZONE
-   `updated_at`: TIMESTAMP WITH TIME ZONE

## 7. Technology Choices

*   **Mobile Client (Android - Primary Focus as per PRD line [`40`](docs/PRD.md:40)):**
    *   Language: Kotlin or Java.
    *   Local Caching: SQLite with Room Persistence Library.
    *   Networking: Retrofit for type-safe HTTP calls, OkHttp as the HTTP client.
    *   UI: Android Jetpack Compose or XML-based layouts.
*   **Backend:**
    *   API Endpoints & Aggregation Service: Supabase Functions (Edge Functions using Deno/TypeScript) are preferred for simplicity and integration if the aggregation logic is manageable. Alternatively, a small, dedicated microservice (e.g., Node.js/Express, Python/FastAPI) can be used if more complex orchestration or external library dependencies are needed.
    *   Database: Supabase (PostgreSQL) for storing crop lists, market lists, and potentially price logs or aggregated data.
*   **External Data Integration:**
    *   Primarily REST/JSON APIs. The specific providers need to be identified and vetted (e.g., government data portals like data.gov.in, Agmarknet, or commercial providers).

## 8. Key Interactions (Conceptual Sequence)

*   **User Fetches Price (Online):**
    `Client UI -> Client Data Service -> Backend API -> Price Aggregation Service -> External Provider API -> Price Aggregation Service -> Backend API -> Client Data Service -> Client Local Cache -> Client UI`
*   **User Fetches Price (Offline):**
    `Client UI -> Client Data Service -> Client Local Cache -> Client UI`
*   **App Fetches Crop/Market Lists:**
    `Client Data Service -> Backend API (e.g., /crops) -> Supabase DB -> Backend API -> Client Data Service -> Client Local Cache (for lists)`

## 9. Scalability and Reliability Considerations

*   **Scalability:**
    *   Backend API (Supabase Functions or microservice) should be designed to be stateless and horizontally scalable.
    *   Caching at the `Price Aggregation Service` level for responses from external providers can significantly reduce load and improve response times.
    *   Efficient database queries for crop/market lists.
*   **Reliability:**
    *   **External Data Source:** This is a critical point of failure. Implement robust error handling, timeouts, and potentially a circuit breaker pattern in the `Price Aggregation Service`. Consider strategies for multiple data sources or fallbacks if one provider is down.
    *   **Data Freshness:** The `last_updated` timestamp is crucial. The system must ensure this reflects the actual time the price was recorded at the source.
    *   **Offline Mode:** The local cache on the client must be reliable. Clear indication to the user when data is stale or from cache.
    *   **Network Handling:** Graceful degradation of service on poor network conditions.

## 10. Security Considerations

*   **API Keys for External Providers:** Securely store and manage API keys used by the `Price Aggregation Service` (e.g., using Supabase secrets or environment variables).
*   **Data Transmission:** All client-backend and backend-external provider communication must use HTTPS.
*   **Access Control:** Backend APIs (for price data and crop/market lists) should be protected. While price data itself isn't PII, requests might be logged with user context in the future, so authentication (e.g., JWT from the User Authentication module) should be enforced.
*   **Input Validation:** Validate all inputs to API endpoints (e.g., `crop_id`, `market_id`).

## 11. Dependencies and Constraints

*   **External Price Data Providers:**
    *   Availability and reliability of chosen provider(s).
    *   Accuracy and timeliness of the data they provide.
    *   Cost associated with using their APIs (if any).
    *   API rate limits and usage policies.
    *   Data format consistency.
    *   **Constraint:** The success of this module heavily relies on identifying and integrating with suitable external data sources. This is a primary risk.
*   **Network Connectivity:** Required for fetching fresh data. The app must function gracefully in low-bandwidth and offline scenarios.
*   **Supabase Platform:** Dependency on Supabase for database and potentially backend functions. Its availability and performance are key.
*   **Client Device Capabilities:** Sufficient local storage for caching price data and crop/market lists.
*   **Data Management:** Processes for keeping the crop and market lists in Supabase up-to-date and accurate.