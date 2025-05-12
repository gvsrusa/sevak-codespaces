# Feature Overview Specification: Market Price Discovery

## 1. Introduction

The Market Price Discovery feature within the AgriConnect application aims to empower small and marginal Indian farmers by providing them with access to real-time or near real-time local commodity prices for their key crops. This feature will enable farmers to make more informed selling decisions, understand market dynamics, and improve their bargaining power. Key considerations include data accuracy, offline accessibility, and a simple, user-friendly interface, as highlighted in the AgriConnect PRD (e.g., lines [`17`](docs/PRD.md:17), [`23`](docs/PRD.md:23), [`28`](docs/PRD.md:28), [`35`](docs/PRD.md:35), [`41`](docs/PRD.md:41), [`57`](docs/PRD.md:57)) and initial research findings.

## 2. User Stories

*   **US1:** As a farmer, I want to look up real-time local commodity prices for my key crops, so I can make informed selling decisions and get a fair price.
*   **US2:** As a farmer, I want to access the last fetched price data even when I am offline, so I can still get an idea of market rates without an internet connection.
*   **US3:** As a farmer, I want to easily select my crop and market location to view prices, so the process is quick, straightforward, and doesn't require advanced tech skills.
*   **US4:** As a farmer, I want to see when the price data was last updated, so I can assess its timeliness and relevance to current market conditions.

## 3. Acceptance Criteria

*   **AC1:** Given a user is online, when they select a crop and a market location, the system displays the latest available price for that combination.
*   **AC2:** Given a user is offline, when they select a crop and a market location for which data has been previously fetched, the system displays the last successfully cached price.
*   **AC3:** The UI must clearly display the timestamp (date and time) of when the displayed price data was last updated. (Ref: PRD Line [`57`](docs/PRD.md:57))
*   **AC4:** Users can select a crop from a predefined, relevant list of key agricultural commodities.
*   **AC5:** Users can select a market location from a predefined list of relevant local markets.
*   **AC6:** The interface for selecting crop and market location is simple, intuitive, and requires minimal taps/inputs. (Ref: PRD Line [`23`](docs/PRD.md:23), [`37`](docs/PRD.md:37))
*   **AC7:** Price data fetched online is accurate and reflects current market conditions as closely as possible from the designated source.
*   **AC8:** The system clearly indicates if displayed data is cached due to offline status or if real-time data fetching fails.

## 4. Functional Requirements

*   **FR1:** The system shall allow users to select a specific crop from a managed list.
*   **FR2:** The system shall allow users to select a specific market location from a managed list.
*   **FR3:** The system shall fetch commodity prices from designated external sources or an internal database based on the selected crop and market. (Ref: PRD Line [`28`](docs/PRD.md:28))
*   **FR4:** The system shall display the fetched price per standard unit (e.g., ₹X per quintal).
*   **FR5:** The system shall cache the last successfully fetched price data (crop, market, price, timestamp) locally on the user's device. (Ref: PRD Line [`35`](docs/PRD.md:35), [`41`](docs/PRD.md:41))
*   **FR6:** The system shall retrieve and display locally cached price data when the device is offline or if fetching new data fails.
*   **FR7:** The system shall display the date and time of the last successful price data update alongside the price information. (Ref: PRD Line [`57`](docs/PRD.md:57))
*   **FR8:** The list of available crops and market locations shall be configurable and updatable via backend systems.

## 5. Non-Functional Requirements

*   **NFR1: Performance:**
    *   Online price lookup: Display results within 3-5 seconds on an average 3G connection.
    *   Offline price lookup: Display cached results almost instantaneously (<1 second).
*   **NFR2: Usability:**
    *   The interface must be extremely simple, clean, and intuitive, designed for users with limited digital literacy. (Ref: PRD Line [`37`](docs/PRD.md:37), Research Highlight)
    *   Minimal text input; primarily selection-based.
    *   Large, high-contrast text and tappable elements.
*   **NFR3: Reliability:**
    *   The system should reliably fetch data when online.
    *   Graceful handling of network errors, falling back to cached data with clear user indication.
*   **NFR4: Accuracy & Timeliness:**
    *   Price data sourced must be accurate and as up-to-date as the provider allows to build and maintain user trust. (Research Highlight)
*   **NFR5: Offline Access:**
    *   Core price viewing functionality must be available offline using cached data. (Ref: PRD Line [`35`](docs/PRD.md:35), [`41`](docs/PRD.md:41), Research Highlight)
*   **NFR6: Data Freshness Indication:**
    *   Users must be clearly and unambiguously informed about the age of the displayed price data. (Ref: PRD Line [`57`](docs/PRD.md:57))
*   **NFR7: Low Bandwidth Operation:**
    *   The feature must be designed to minimize data consumption and function effectively on low-bandwidth connections. (Ref: PRD Line [`23`](docs/PRD.md:23))
*   **NFR8: Scalability:**
    *   The backend system supporting price data aggregation and delivery should be able to handle an increasing number of users and requests.
*   **NFR9: Maintainability:**
    *   Configuration of crops, markets, and data sources should be easily manageable.

## 6. Scope

### In Scope:

*   Allowing users to select a crop and a market location.
*   Fetching and displaying real-time or near real-time commodity prices for selected crop/market.
*   Caching the latest fetched price data locally on the device.
*   Displaying cached price data when the user is offline.
*   Displaying the timestamp (date and time) of the last price data update.
*   A simple, intuitive UI for the price lookup functionality.
*   Sourcing price data from one or more defined external APIs or an internal database.

### Out of Scope (for MVP):

*   Historical price data, charts, or trend analysis.
*   Price alerts or notifications based on user-defined thresholds.
*   Allowing users to submit or report prices.
*   Direct integration with marketplace listing/selling features (i.e., no auto-filling prices in sell forms based on lookup).
*   Predictive pricing or forecasting.
*   Comparison of prices across multiple markets simultaneously in a single view.
*   Personalized price recommendations.

## 7. Dependencies

### Internal:

*   **User Authentication System:** (Implicit) While not directly interacting for price lookup, user context (e.g., default location) might be leveraged in future iterations.
*   **Database (Supabase PostgreSQL):** May be used for intermediate caching/logging or if price data is aggregated internally before being served to the app. (Ref: PRD Line [`28`](docs/PRD.md:28), [`34`](docs/PRD.md:34))
*   **App's Local Storage:** For client-side caching of price data.

### External:

*   **Price Data Provider(s):** One or more external APIs or data feeds that supply commodity price information. The selection and integration of these sources are critical. (Ref: PRD Line [`28`](docs/PRD.md:28), Research Highlight)
*   **Network Connectivity:** Required for fetching fresh, real-time price data.
*   **Device Operating System:** For local storage capabilities and network access.

## 8. High-Level UI/UX Considerations

*   **Simplicity and Clarity:** The primary design principle. Avoid clutter. (Ref: PRD Line [`37`](docs/PRD.md:37))
*   **Selection Mechanism:**
    *   Use clear, localized dropdown menus or simple list selections for "Crop" and "Market Location."
    *   Consider pre-filling or suggesting a default market based on user's general location if available and consented.
*   **Information Display:**
    *   Prominently display: Selected Crop, Selected Market, Price (e.g., "₹1500 / Quintal"), and "Last Updated: [Date] [Time]".
    *   Use large, legible fonts and high contrast.
*   **Offline Indication:**
    *   Subtly but clearly indicate if the displayed data is from the local cache due to being offline (e.g., a small icon or text like "Offline - data from [timestamp]").
*   **Error States:**
    *   Clear messages if data cannot be fetched (e.g., "Could not update prices. Showing last known data.") or if no cached data exists for a selection.
*   **Accessibility:** Adhere to accessibility guidelines for users with visual impairments or other disabilities (as per overall app guidelines).
*   **Language Support:** Ensure all text elements are localizable as per the app's multi-language strategy. (Ref: PRD Line [`46`](docs/PRD.md:46))
*   **Visuals:** Minimal use of icons, focusing on information clarity. Earthy tones and simple graphics consistent with the AgriConnect brand. (Ref: PRD Line [`37`](docs/PRD.md:37))

## 9. API Design Notes (Conceptual)

If an intermediary API is used by AgriConnect to serve data to the app:

*   **Endpoint:** `GET /api/v1/marketprices`
*   **Query Parameters:**
    *   `crop_id` (string/integer, identifier for the crop)
    *   `market_id` (string/integer, identifier for the market)
    *   Optional: `user_id` (for logging or future personalization)
*   **Successful Response (200 OK):**
    ```json
    {
      "crop_name": "Wheat",
      "market_name": "Mandi Name",
      "price": "1850.00",
      "unit": "Quintal",
      "currency": "INR",
      "last_updated": "2024-07-28T10:30:00Z", // ISO 8601 Timestamp
      "data_source_name": "Source X API" // Optional: for internal tracking
    }
    ```
*   **Error Responses:**
    *   `404 Not Found`: If crop/market combination has no data.
    *   `503 Service Unavailable`: If upstream data provider is down.

Consideration for a batch endpoint if the app needs to pre-fetch/cache data for multiple common crop/market pairs for a user's region.