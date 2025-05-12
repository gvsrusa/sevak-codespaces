# Feature Overview Specification: Transportation Logistics (Request & Browse)

## 1. Introduction

This document outlines the specifications for the "Transportation Logistics (Request & Browse)" feature within the AgriConnect application. This feature aims to connect farmers needing to transport their harvest with available transporters. It comprises two main functionalities: allowing farmers to request transportation and enabling users to browse available transporters.

## 2. User Stories

### 2.1. Requesting Transportation
*   **US1.1:** As a farmer, I want to post a request for transporting my harvest, so that I can find help moving my produce to the market.
*   **US1.2:** As a farmer, I want to specify the type of produce, quantity, desired date, and pickup location for my transport request, so that transporters have clear information.
*   **US1.3:** As a farmer, I want to be able to create a transport request even when I am offline, so that my request can be submitted once I regain connectivity.
*   **US1.4:** As a farmer, I want my contact details to be handled privately when I post a request, so that my personal information is not unnecessarily exposed.

### 2.2. Browsing Transporters
*   **US2.1:** As a farmer or user, I want to view a list of nearby transporters, so that I can find options for moving goods.
*   **US2.2:** As a user, I want to see details for each transporter, including their name, vehicle capacity, contact information, and service areas, so I can make an informed choice.
*   **US2.3:** As a user, I want to be able to browse transporter listings even if my internet connection is intermittent, so I can access information when needed.
*   **US2.4:** As a user, I want to trust the information provided about transporters, so I can confidently engage their services.
*   **US2.5:** As a transporter (implicitly, for their information to be listed), I want my contact details to be displayed appropriately to interested users, while maintaining a degree of privacy until direct contact is initiated.

## 3. Acceptance Criteria

### 3.1. Requesting Transportation
*   **AC1.1.1:** Given a logged-in farmer, when they navigate to the "Request Transportation" section, then they should see a form to create a new transport request.
*   **AC1.1.2:** Given a farmer is creating a transport request, when they submit the form with valid produce type, quantity (optional, or could be free text), date, and pickup location, then the request is successfully posted and visible to relevant parties (e.g., transporters, or admin for moderation if applicable).
*   **AC1.2.1:** Given a farmer is creating a transport request, the form must include fields for: type of produce, date, and pickup location. Quantity can be an optional field or part of a description.
*   **AC1.3.1:** Given a farmer creates a transport request while offline, when the device regains internet connectivity, then the request is automatically synchronized with the server.
*   **AC1.3.2:** Given a farmer is offline, they should still be able to access the transport request creation form.
*   **AC1.4.1:** Farmer's direct contact details (e.g., phone number from profile) should not be publicly visible on the transport request listing. A mechanism for interested transporters to express interest or for mediated contact should be considered.

### 3.2. Browsing Transporters
*   **AC2.1.1:** Given a user navigates to the "Browse Transporters" section, then they should see a list of transporters, potentially filterable or sorted by proximity (if location services are integrated and permitted).
*   **AC2.2.1:** Given a user views a transporter in the list, they should be able to see the transporter's name, vehicle capacity (e.g., "Small truck - 1 ton", "Tractor-trailer"), contact information (e.g., a button to call, or a masked number revealed upon action), and service areas (e.g., list of villages/towns, radius).
*   **AC2.3.1:** Given a user has previously browsed transporter listings, when they are offline or have intermittent connectivity, then they should be able to view cached transporter information. The app should indicate if the data is cached and the last update time.
*   **AC2.4.1:** Information about transporters (e.g., capacity, service areas) should be accurate and up-to-date. (Mechanism for this needs definition - e.g., transporter self-update, admin verification).
*   **AC2.5.1:** Transporter's contact information should be accessible to users who wish to engage their services, but direct public listing of sensitive details should be balanced with privacy considerations (e.g., an in-app "contact" button that initiates a call or message without revealing the number initially, or an opt-in for direct display).

## 4. Functional Requirements

### 4.1. Requesting Transportation
*   **FR1.1:** The system shall allow authenticated farmers (via Google Social Login) to create transport requests.
*   **FR1.2:** Each transport request must capture:
    *   Type of produce (e.g., text input, dropdown from common types)
    *   Quantity (e.g., text input like "50 kg", "10 bags")
    *   Preferred transportation date (e.g., date picker)
    *   Pickup location (e.g., text input, map pin if feasible and simple)
    *   Farmer ID (linked automatically)
*   **FR1.3:** The system shall store transport requests in the Supabase PostgreSQL database.
*   **FR1.4:** The system shall support offline creation of transport requests. Offline requests must be queued locally on the device.
*   **FR1.5:** The system shall synchronize queued offline transport requests to the server when network connectivity is restored.
*   **FR1.6:** The system should provide a confirmation to the farmer upon successful submission of a transport request (both online and offline, with offline indicating it's queued).
*   **FR1.7:** (Consideration) A mechanism for farmers to view, edit (if not yet actioned), or cancel their posted requests.

### 4.2. Browsing Transporters
*   **FR2.1:** The system shall allow authenticated users to browse a list of registered transporters.
*   **FR2.2:** Each transporter profile must display:
    *   Name
    *   Vehicle type/capacity (e.g., "Tata Ace - 750kg", "Mahindra Bolero Pickup - 1.5 Ton")
    *   Contact information (mechanism to be defined for privacy, e.g., in-app call/message button, or direct number if opted-in by transporter)
    *   Service areas (e.g., list of covered villages/districts, operational radius)
*   **FR2.3:** The system shall store transporter information in the Supabase PostgreSQL database.
*   **FR2.4:** The system shall cache transporter listings on the device for offline viewing.
*   **FR2.5:** (Consideration) A mechanism for transporters to register and manage their profiles (this might be out of scope for the initial farmer-facing feature but is a dependency for data population).
*   **FR2.6:** (Consideration) Search/filter functionality for transporters (e.g., by location, vehicle type).

### 4.3. General
*   **FR3.1:** User authentication will be handled via Google Social Login as per PRD line 44.

## 5. Non-Functional Requirements

*   **NFR1. Usability:**
    *   The UI for both requesting transport and browsing transporters must be simple, clean, and intuitive, adhering to PRD line 37.
    *   Minimize text input; use clear icons and large, high-contrast text/buttons (PRD line 37, 46).
    *   Forms should be very simple, avoiding excessive data input (PRD line 51).
    *   The feature must be accessible to low-literacy and low-tech users (PRD line 46).
    *   Support for local languages (Hindi, Marathi, etc.) as per PRD line 46.
*   **NFR2. Performance:**
    *   The app should load transporter listings and transport request forms quickly, even on low-bandwidth connections.
    *   Offline synchronization should occur efficiently in the background without significantly impacting app performance.
*   **NFR3. Offline Capability:**
    *   Farmers must be able to create new transport requests while offline (PRD line 35, 41).
    *   Users must be able to view cached transporter listings while offline (PRD line 35, 41).
    *   The app should clearly indicate when it is operating in offline mode and when data is being synchronized.
*   **NFR4. Privacy & Security:**
    *   Farmer contact details associated with transport requests must be handled privately and not publicly exposed without a clear engagement mechanism (PRD line 47, Research Highlight).
    *   Transporter contact details must be handled appropriately, balancing accessibility with privacy (PRD line 47, Research Highlight).
    *   All data transmission between the app and the server (Supabase PostgreSQL) must be secure.
    *   User data must be stored securely in the database (PRD line 45).
*   **NFR5. Reliability:**
    *   Offline data synchronization must be reliable, ensuring no data loss for queued requests.
    *   Information displayed (e.g., transporter details) should be accurate and trustworthy (Research Highlight). (Mechanism for data vetting/updates is key).
*   **NFR6. Data Management:**
    *   Data for transporters (profiles, availability) needs a clear management strategy (Research Highlight). This might involve an admin interface or a separate registration flow for transporters.

## 6. Scope

### 6.1. In Scope
*   Allowing farmers to create and submit transport requests (including produce type, date, pickup location).
*   Offline creation and synchronization of transport requests.
*   Allowing users to browse a list of available transporters.
*   Displaying transporter details: name, capacity, contact info (with privacy considerations), service areas.
*   Caching of transporter listings for offline viewing.
*   Simple, clear UI for both functionalities.
*   Adherence to privacy guidelines for user and transporter data.
*   Using Google Social Login for authentication.
*   Storing data in Supabase PostgreSQL.

### 6.2. Out of Scope (for this specific feature's MVP, based on PRD)
*   **Real-time tracking of transport vehicles (PRD line 87 - Future).**
*   **In-app booking or payment for transportation services (PRD line 49 - No transaction/payment).** The feature facilitates connection, not transaction.
*   **Automated matching of farmers to transporters.** Users browse and initiate contact.
*   **Transporter registration and profile management portal within this specific farmer-facing feature.** (This is a dependency but likely a separate admin/transporter-side feature).
*   **Rating or review system for transporters or farmers.** (Could be future, but not in initial simple connection).
*   **Complex algorithms for optimizing transport routes.**
*   **Guaranteed availability or service quality of transporters.** The app is a listing/connection platform.

## 7. Dependencies

*   **D1. User Authentication:** Relies on the existing Google Social Login feature (PRD line 14, 44).
*   **D2. Database:** Relies on Supabase PostgreSQL for data storage (PRD line 34, 45).
*   **D3. Transporter Data Population:** The "Browse Transporters" functionality depends on a mechanism for populating and maintaining transporter data. This could be:
    *   An admin interface for adding/managing transporters.
    *   A separate registration flow for transporters.
    *   (Less likely for MVP) Data sourced from an external partner.
*   **D4. Location Services (Optional but Recommended for "Nearby"):** To effectively show "nearby" transporters, access to device location (with user permission) would be beneficial. If not available, "nearby" might be based on farmer's registered farm location or a manually selected region.
*   **D5. Core App Framework:** Basic app navigation, UI components, and offline data synchronization capabilities provided by the overall AgriConnect application.
*   **D6. Language Support:** Relies on the app's overall internationalization (i18n) and localization (l10n) framework for local language display (PRD line 46).

## 8. High-Level UI/UX Considerations

*   **Request Transportation Form:**
    *   A simple, single-screen form.
    *   Fields:
        *   Produce Type: Dropdown or auto-suggest text field.
        *   Quantity: Simple text field (e.g., "10 bags of onions", "5 quintals of wheat").
        *   Date: Calendar date picker.
        *   Pickup Location: Text field. Consider a "Use my farm location" button if farm location is profiled.
        *   Optional Notes: Small text area for additional details.
    *   Clear "Submit Request" button.
    *   Visual feedback on submission (e.g., "Request Sent!" or "Request Queued for Offline Sync").
*   **Browse Transporters List:**
    *   Card-based layout for each transporter, showing key info at a glance (Name, Vehicle Type/Capacity, Service Area snippet).
    *   Tapping a card reveals more details.
    *   Possible "Call" or "Contact" button that initiates action (respecting privacy).
    *   Indication of cached data / last updated time if offline.
    *   Simple search or filter bar (e.g., by location/area if feasible).
*   **General:**
    *   Use of intuitive icons (e.g., truck icon for transport).
    *   Consistent design language with the rest of the AgriConnect app.
    *   Clear visual cues for offline status and synchronization.
    *   Large, legible fonts and tappable targets.

## 9. API Design Notes (Conceptual)

While detailed API design is a subsequent step, high-level considerations include:

*   **Endpoints for Transport Requests:**
    *   `POST /transport-requests`: Create a new transport request.
        *   Payload: `{ userId, produceType, quantity, date, pickupLocation, notes (optional) }`
    *   `GET /transport-requests?userId={userId}`: Get requests posted by a specific farmer.
    *   `PUT /transport-requests/{requestId}`: Update a request (if allowed).
    *   `DELETE /transport-requests/{requestId}`: Cancel/delete a request (if allowed).
*   **Endpoints for Transporters:**
    *   `GET /transporters?location={location}&radius={radius}`: Get list of transporters (potentially filterable by location).
    *   `GET /transporters/{transporterId}`: Get details for a specific transporter.
*   **Data Synchronization:**
    *   The client app will need logic to detect connectivity changes and sync queued offline requests.
    *   The server API should handle potentially duplicate requests gracefully if sync logic re-tries.
*   **Authentication:** All endpoints will require authentication (e.g., JWT token obtained via Google Social Login).

## 10. Privacy Considerations (Reiteration)

*   **Farmer Privacy (Requests):** Avoid displaying direct farmer contact info on public request boards. Implement a system where transporters can "express interest" and the farmer is notified, or a mediated contact initiation.
*   **Transporter Privacy (Listings):** Provide transporters control over how their contact information is displayed (e.g., direct display opt-in, or an in-app "contact" button that doesn't immediately reveal the number).
*   Data handling must comply with all relevant privacy policies and regulations.

## 11. Future Considerations (Beyond MVP)

*   Rating and review system for transporters.
*   Direct in-app messaging between farmers and transporters.
*   Map-based view for pickup locations and transporter service areas.
*   Notifications to transporters about new requests in their service area.
*   Status updates for transport requests (e.g., "Contacted", "Scheduled", "Completed").