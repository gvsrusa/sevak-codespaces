# High-Level Architecture: User Authentication & Profile Management

**Version:** 1.0
**Date:** 2025-05-12
**Module Name:** User Authentication & Profile Management
**Application:** AgriConnect

## 1. Introduction

This document outlines the high-level architecture for the User Authentication & Profile Management module within the AgriConnect application. The primary objective is to provide a simple, secure, and trustworthy mechanism for farmers to register, authenticate, and manage their basic profile information.

This architecture is guided by the requirements specified in:
- AgriConnect PRD: [`docs/PRD.md`](../../docs/PRD.md)
- Feature Overview Specification: User Authentication & Profile Management: [`docs/specs/User_Authentication_Profile_Management_overview.md`](../../docs/specs/User_Authentication_Profile_Management_overview.md)

The core goals for this module are:
-   Exclusive use of Google Social Login for registration and authentication.
-   Secure storage and management of farmer profile data using Supabase (PostgreSQL) as the backend.
-   Ensuring stringent data privacy and security, particularly for user contact details.
-   Design for utmost simplicity and accessibility, catering to low-tech users.
-   Consideration for offline access to cached profile data.

## 2. Architectural Drivers

The design is primarily driven by the following key requirements and constraints:

-   **Google Social Login:** Mandatory use of Google for authentication (PRD Lines 14, 44). This simplifies the user experience and leverages a familiar, trusted system.
-   **Supabase (PostgreSQL) Backend:** Mandated use of Supabase for database and backend services (PRD Lines 26, 34, 45). This provides a robust, scalable BaaS solution.
-   **Data Privacy & Security:** Critical requirement to keep user contact details private and secure all personal data (PRD Line 47).
-   **Simplicity for Low-Tech Users:** The entire user experience for authentication and profile management must be extremely intuitive and straightforward (PRD Line 46).
-   **Offline Considerations:** The ability for users to view their profile data even when offline is important. Authentication itself requires connectivity (PRD Lines 35, 41).

## 3. High-Level Architecture Overview

A **Client-Server architectural pattern** will be adopted, where the AgriConnect Android application (client) interacts directly with Supabase (Backend-as-a-Service). Supabase will manage authentication via Google and provide database services.

### Conceptual Component Diagram:

-   **AgriConnect Client (Android App):**
    -   Handles all user interface elements for login, registration, and profile viewing/editing.
    -   Integrates the Google Sign-In SDK to manage the Google authentication flow on the device.
    -   Communicates with Supabase for user session management and profile data CRUD operations.
    -   Manages local caching of user profile data for offline viewing.
-   **Google OAuth 2.0 Service (External):**
    -   Provides the identity and authentication mechanism.
    -   Interacts with the client app via the Google Sign-In SDK.
-   **Supabase Backend (BaaS):**
    -   **Supabase Auth:**
        -   Manages user authentication by verifying Google ID tokens.
        -   Issues and manages Supabase JWTs for session control.
        -   Automatically handles user records in the `auth.users` table.
    -   **Supabase Database (PostgreSQL):**
        -   Stores user-specific profile information (name, farm location) in a custom `profiles` table.
        -   Enforces data integrity and security through Row Level Security (RLS) policies.
    -   **Supabase APIs:**
        -   Provides auto-generated RESTful APIs for interacting with the `profiles` table.
        -   Supabase client libraries on the Android app will be used to interact with these APIs.

## 4. Key Components and Interactions

### 4.1. User Authentication Flow (Registration & Login)

This flow describes how a user registers or logs into AgriConnect using their Google account.

**Sequence Diagram Idea:**

1.  **User Action:** Farmer taps the "Sign in with Google" button on the AgriConnect app.
2.  **Google SDK Invocation:** The client app, using the integrated Google Sign-In SDK, initiates the Google authentication process.
3.  **Google Authentication:** The user is redirected to Google's authentication screen (if not already signed in on the device or if selection is needed). The user authenticates with their Google credentials.
4.  **ID Token Retrieval:** Upon successful Google authentication, the Google OAuth 2.0 service returns a Google ID Token to the client app via the SDK.
5.  **Supabase Sign-In:** The client app sends this Google ID Token to Supabase Auth (e.g., using `supabase.auth.signInWithOAuth({ provider: 'google' })` or by passing the token to a Supabase function if custom handling is needed, though direct SDK method is preferred).
6.  **Token Verification & Session Creation:** Supabase Auth verifies the Google ID Token with Google.
    -   If the token is valid and the user is new, Supabase Auth creates a new user record in its `auth.users` table.
    -   Supabase Auth then generates a Supabase-specific JWT (session token) and returns it to the client app.
7.  **Session Storage:** The client app securely stores this Supabase JWT (e.g., in SharedPreferences). This JWT will be used for all subsequent authenticated requests to Supabase.
8.  **Profile Setup (New User):**
    -   If it's a new user registration, the client app checks if the `farm_location` is set for this user (e.g., by attempting to fetch the profile or based on a flag from the sign-in response).
    -   If `farm_location` is missing, the client app prompts the user to enter their farm location.
    -   The client app then makes an API call to the Supabase `profiles` table to create/update the profile with the name and email (from Google, via the ID token implicitly handled by Supabase or explicitly passed) and the user-provided `farm_location`.

### 4.2. Profile Management Flow

**View Profile:**
1.  User navigates to the profile section in the app.
2.  Client app, using the stored Supabase JWT, makes an authenticated GET request to the Supabase `profiles` table (e.g., `/rest/v1/profiles?select=*&id=eq.current_user_id`) to fetch the current user's profile data (name, farm location).
3.  Client app displays the retrieved information.

**Update Farm Location:**
1.  User navigates to their profile and chooses to edit their farm location.
2.  Client app presents a simple interface for updating the location.
3.  Upon submission, the client app, using the Supabase JWT, makes an authenticated PATCH request to the Supabase `profiles` table (e.g., `/rest/v1/profiles?id=eq.current_user_id`) with the updated `farm_location`.
4.  Client app displays a confirmation or error message.

## 5. Data Model (Supabase PostgreSQL)

Two main tables will be involved:

1.  **`auth.users` (Managed by Supabase Auth):**
    -   This table is automatically created and managed by Supabase Authentication.
    -   It stores essential authentication information, including the user's unique ID (UUID), email, and other metadata related to their Google account linkage.
    -   Example columns: `id` (UUID, PK), `email` (TEXT), `raw_user_meta_data` (JSONB, may contain Google profile info).

2.  **`profiles` (Custom Table for AgriConnect):**
    -   This table stores application-specific user profile information.
    -   **Columns:**
        -   `id` (UUID, Primary Key): This column **must** be a foreign key referencing `auth.users.id`. This establishes a one-to-one relationship with the authenticated user.
        -   `name` (TEXT): User's full name, fetched from their Google profile.
        -   `email` (TEXT, Nullable): User's email, fetched from Google. While Supabase Auth stores this in `auth.users`, having it here can simplify application queries. It **must** be kept private and not exposed to other users. *Consider making this non-nullable if always available from Google and essential for app logic, or rely solely on `auth.users.email`.* For simplicity and direct access, we'll include it here with strict privacy rules.
        -   `farm_location` (TEXT): User-provided farm location.
        -   `created_at` (TIMESTAMP WITH TIME ZONE, default `now()`): Timestamp of profile creation.
        -   `updated_at` (TIMESTAMP WITH TIME ZONE, default `now()`): Timestamp of the last profile update (trigger can update this).
    -   **Row Level Security (RLS):**
        -   RLS policies **must** be implemented on the `profiles` table.
        -   **SELECT Policy:** Users can only select their own profile row (`auth.uid() = id`).
        -   **INSERT Policy:** Users can only insert a profile row for themselves (`auth.uid() = id`). This might be handled by a trigger or Supabase Edge Function upon user creation in `auth.users` to ensure a profile is created. Alternatively, the client creates it after the first login.
        -   **UPDATE Policy:** Users can only update their own profile row (`auth.uid() = id`).
        -   **DELETE Policy:** (If applicable) Users can only delete their own profile row. (Out of scope for MVP - PRD Spec Line 92).

## 6. Technology Choices

-   **Client-Side (Mobile App):**
    -   **Platform:** Android (Primary focus as per PRD Line 40).
    -   **Language/Framework:** Native Android development (Java/Kotlin).
    -   **Google Authentication:** Google Sign-In SDK for Android.
    -   **Supabase Interaction:** Supabase Kotlin Client Library.
    -   **Local Caching:** Android SharedPreferences for simple data like JWT and basic profile info, or a lightweight SQLite database via Room Persistence Library if offline profile data becomes more complex.
-   **Backend-as-a-Service (BaaS):**
    -   **Provider:** Supabase.
    -   **Authentication:** Supabase Auth (leveraging Google OAuth 2.0).
    -   **Database:** Supabase PostgreSQL.
    -   **APIs:** Auto-generated RESTful APIs provided by Supabase PostgREST. Supabase Edge Functions (Deno) can be used if custom server-side logic beyond RLS is needed (e.g., complex data validation or triggers not easily done in SQL).

## 7. Data Flow Diagrams (Conceptual)

### 7.1. Registration/Login Data Flow

```
User --(1. Tap "Sign in with Google")--> AgriConnect App (UI)
AgriConnect App --(2. Initiate Google Sign-In)--> Google Sign-In SDK
Google Sign-In SDK --(3. Authenticate User)--> Google OAuth 2.0 Service
Google OAuth 2.0 Service --(4. Return ID Token)--> Google Sign-In SDK
Google Sign-In SDK --(5. Pass ID Token)--> AgriConnect App
AgriConnect App --(6. Send ID Token to Supabase Auth)--> Supabase Client SDK
Supabase Client SDK --(7. Verify Token, Create/Get User, Issue JWT)--> Supabase Auth
Supabase Auth --(8. Return Supabase JWT)--> Supabase Client SDK
Supabase Client SDK --(9. Return JWT to App)--> AgriConnect App
AgriConnect App --(10. Store JWT Securely)--> Device Secure Storage
[IF NEW USER]
AgriConnect App --(11. Prompt for Farm Location)--> User
User --(12. Provide Farm Location)--> AgriConnect App
AgriConnect App --(13. Create Profile in DB via Supabase API)--> Supabase Database (profiles table)
```

### 7.2. Profile View Data Flow

```
User --(1. Navigate to Profile)--> AgriConnect App (UI)
AgriConnect App --(2. Prepare Authenticated Request with JWT)--> Supabase Client SDK
Supabase Client SDK --(3. GET /rest/v1/profiles?id=eq.auth.uid())--> Supabase Database (profiles table via PostgREST)
Supabase Database --(4. Return Profile Data, RLS Enforced)--> Supabase Client SDK
Supabase Client SDK --(5. Return Data to App)--> AgriConnect App
AgriConnect App --(6. Display Profile Data)--> User
```

## 8. Security and Privacy Considerations

-   **Authentication:**
    -   Reliance on Google's robust OAuth 2.0 protocol.
    -   Supabase Auth securely manages Google token verification and issues its own JWTs for session management within AgriConnect.
    -   Supabase JWTs should have appropriate expiry times and be transmitted securely (HTTPS).
-   **Data Storage (Supabase):**
    -   Supabase provides a secure PostgreSQL environment.
    -   **Row Level Security (RLS) is paramount** on the `profiles` table to ensure users can only access and modify their own data. This is a critical implementation detail.
    -   Database credentials and API keys for Supabase must be managed securely within the client application (using environment variables or secure build configurations, not hardcoded).
-   **Data in Transit:**
    -   All communication between the client app, Google, and Supabase **must** use HTTPS. Supabase enforces this by default.
-   **Data Privacy:**
    -   User's email address, obtained from Google, is stored but **must not** be displayed to other users or exposed in any public-facing part of the app, as per PRD Line 47 and Feature Spec NFR3. RLS and careful API design will enforce this.
    -   Farm location, while part of the profile, should also be treated with care. Its visibility to other users depends on other features (e.g., marketplace listings) and is outside the scope of this specific module's privacy concern beyond general secure storage.
-   **Client-Side Security:**
    -   The Supabase JWT must be stored securely on the Android device (e.g., Android's EncryptedSharedPreferences).
    -   The app should be protected against common mobile vulnerabilities (e.g., through ProGuard/R8 for code obfuscation).

## 9. Offline Considerations

-   **Authentication:**
    -   Google authentication requires an active internet connection.
    -   If the user is offline, the app should gracefully handle attempts to sign in/register by informing the user about the connectivity requirement.
    -   Once authenticated, the Supabase JWT allows the user to remain "logged in" for its validity period. If the token expires while offline, the user will need to re-authenticate when back online.
-   **Profile Data Caching (Offline Viewing):**
    -   Upon successful login and profile retrieval, the user's basic profile information (name, farm location) **should be cached locally** on the Android device.
    -   This allows the user to view their own profile information even when the app is offline.
    -   **Mechanism:** SharedPreferences for simple key-value storage or a local SQLite database (e.g., using Room) if more structured caching is needed.
    -   **Staleness:** Cached data might become stale. The app should attempt to refresh it from Supabase when online. For profile data, this is less critical than transactional data.
    -   **Offline Updates:** Updating profile information (e.g., farm location) while offline is **not an MVP requirement** for this module to maintain simplicity (Feature Spec [`docs/specs/User_Authentication_Profile_Management_overview.md`](../../docs/specs/User_Authentication_Profile_Management_overview.md) does not mandate offline profile updates). Updates will require an internet connection. If this changes, a more complex sync mechanism would be needed.

## 10. Simplicity and Accessibility

Adherence to simplicity and accessibility is critical for the target low-tech user base (PRD Line 46, Feature Spec NFR1, NFR6, UIUX considerations).

-   **UI Design:**
    -   A single, prominent "Sign in with Google" button will be the sole entry point for authentication.
    -   Minimal text, large tappable targets, and high contrast visuals.
    -   Profile creation (farm location input) will be a simple, guided step post-registration.
    -   Profile viewing and editing interfaces will be uncluttered and intuitive.
-   **Error Handling:** User-friendly, clear error messages in local languages (if supported by the app shell) for issues like network failure or Google authentication problems.

## 11. Dependencies and Constraints

### Dependencies:
-   **External Services:**
    -   **Google OAuth 2.0 Service:** Availability and correct functioning are essential.
    -   **Supabase Platform:** Availability of Supabase Auth, Database, and APIs.
-   **Client-Side:**
    -   **Google Sign-In SDK for Android:** Must be correctly integrated.
    -   **Supabase Kotlin Client Library:** For interaction with Supabase services.
-   **Network:**
    -   Reliable internet connectivity is required for all authentication operations and real-time profile synchronization.

### Constraints:
-   **Authentication Method:** Exclusively Google Social Login (PRD Line 44). No other methods (username/password, other social logins) are permitted.
-   **Backend Platform:** Supabase is the mandated backend (PRD Line 45).
-   **Platform:** Primarily Android mobile application (PRD Line 40).

## 12. API Contract (Conceptual - via Supabase Client SDK)

The AgriConnect client application will primarily interact with Supabase using the Supabase Kotlin client library, which abstracts direct HTTP API calls. Conceptually, these interactions map to:

1.  **Sign In / Register with Google:**
    -   Client Call: `supabase.auth.signInWithOAuth { provider = Google }` (or similar, passing necessary Google ID token if handled manually, though Supabase SDK often manages the flow).
    -   Underlying Supabase Action: Verifies Google token, creates/retrieves user in `auth.users`, issues Supabase JWT.

2.  **Fetch User Profile:**
    -   Client Call (example using Supabase query builder):
      ```kotlin
      val userId = supabase.auth.currentUserOrNull()?.id
      val profile = supabase.from("profiles").select { filter("id", FilterOperator.EQ, userId) }.singleOrNull()
      ```
    -   Underlying API: `GET /rest/v1/profiles?select=*&id=eq.{user_id}` (RLS enforced)

3.  **Create/Update User Profile (e.g., setting farm_location):**
    -   Client Call (example for initial profile creation or update):
      ```kotlin
      // For new profile after registration
      val user = supabase.auth.currentUserOrNull()!!
      val newProfile = Profile(id = user.id, name = user.userMetadata?.get("full_name"), email = user.email, farmLocation = "Some Location")
      supabase.from("profiles").insert(newProfile)

      // For updating farm_location
      supabase.from("profiles").update( { set("farm_location", "New Location") } ) { filter("id", FilterOperator.EQ, user.id) }
      ```
    -   Underlying API (Insert): `POST /rest/v1/profiles` with profile data in the body.
    -   Underlying API (Update): `PATCH /rest/v1/profiles?id=eq.{user_id}` with fields to update in the body.

*(Note: Exact Supabase Kotlin SDK syntax may vary. RLS policies are crucial for these operations to be secure and user-specific.)*

## 13. Scalability

-   **Google OAuth 2.0:** Inherently highly scalable.
-   **Supabase:** Built on PostgreSQL and designed for scalability. Supabase's infrastructure handles scaling concerns for its BaaS offerings.
-   **Application Architecture:** The direct client-to-BaaS architecture is simple and generally scales well for common use cases. As Supabase manages the backend infrastructure, the primary scaling concerns for AgriConnect itself would relate to client-side performance and efficient use of Supabase resources (e.g., optimized queries, proper indexing if custom queries become complex).

## 14. Risk Assessment & Mitigation

-   **Dependency on External Services (Google/Supabase):**
    -   **Risk:** Outages or changes in API/terms of service.
    -   **Mitigation:** Monitor service status, design for graceful degradation if services are temporarily unavailable (e.g., clear error messages), stay updated with API changes.
-   **Incorrect RLS Implementation:**
    -   **Risk:** Potential data leaks or unauthorized access/modification if RLS policies are misconfigured.
    -   **Mitigation:** Thorough testing of RLS policies is critical. Use Supabase's SQL editor and testing tools to verify policies under various user roles/conditions. Peer review of RLS policies.
-   **Client-Side JWT Handling:**
    -   **Risk:** Insecure storage or transmission of Supabase JWT on the client can lead to session hijacking.
    -   **Mitigation:** Use secure storage mechanisms (EncryptedSharedPreferences). Ensure all communication is over HTTPS (default with Supabase).
-   **Offline Data Staleness/Sync Issues (for profile caching):**
    -   **Risk:** Cached profile data viewed offline might not be the absolute latest.
    -   **Mitigation:** For profile data, this is a low risk. Implement a simple refresh mechanism when the app comes online. Clearly indicate if data is offline/cached. (Offline updates are out of scope for MVP, simplifying this).
-   **Google Sign-In Integration Complexity:**
    -   **Risk:** Errors in integrating Google Sign-In SDK can lead to authentication failures.
    -   **Mitigation:** Follow Google's official documentation carefully. Thoroughly test on various Android versions and devices.

This architecture provides a foundation for a secure, simple, and user-friendly authentication and profile management system for AgriConnect, leveraging the strengths of Google and Supabase.