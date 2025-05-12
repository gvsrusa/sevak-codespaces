# Test Plan: User Authentication & Profile Management

**Version:** 1.0
**Date:** 2025-05-12
**Feature Name:** User Authentication & Profile Management
**Application:** AgriConnect

## 1. Introduction

### 1.1. Purpose
This document outlines the test plan for the User Authentication & Profile Management feature of the AgriConnect application. It details the scope, approach, resources, and schedule of intended testing activities. The goal is to ensure the feature meets the specified requirements for functionality, security, usability, and performance.

### 1.2. Feature Overview
The User Authentication & Profile Management feature enables users (farmers) to register and log in to the AgriConnect application exclusively using their Google accounts. It also allows them to manage a basic profile, including their name (fetched from Google) and farm location. Simplicity, security, and privacy are key aspects of this feature.

For detailed information, refer to the [Feature Overview Specification: User Authentication & Profile Management](docs/specs/User_Authentication_Profile_Management_overview.md).

### 1.3. Scope of Testing
This test plan covers testing activities for the User Authentication & Profile Management feature on the Android platform. It includes:
- Unit Testing
- Integration Testing
- End-to-End (E2E) / UI Testing
- Security Testing
- Usability & Accessibility Testing (Manual Checks)
- Performance Testing (Observational for MVP)

## 2. References
- [Feature Overview Specification: User Authentication & Profile Management](docs/specs/User_Authentication_Profile_Management_overview.md)
- [High-Level Architecture: User Authentication & Profile Management](docs/architecture/User_Authentication_Profile_Management_architecture.md)
- [AgriConnect Master Project Plan](docs/Master_Project_Plan.md)
- [AgriConnect PRD](docs/PRD.md) (as referenced in other documents)
- [CI Configuration](AgriConnect/.github/workflows/ci.yml)

## 3. Test Strategy

### 3.1. Test Levels
- **Unit Tests:** Focus on individual components, functions, and classes within the Android application (e.g., ViewModels, Supabase client interaction logic) and any Supabase Edge Functions.
- **Integration Tests:** Verify interactions between different components, such as the Android client and Supabase backend services (Auth, Database), and the Google Sign-In SDK integration.
- **End-to-End (E2E) / UI Tests:** Validate complete user flows from the user interface perspective, covering registration, login, profile viewing, and profile updating.
- **Security Tests:** Focus on ensuring data privacy, secure authentication, and protection against common vulnerabilities, particularly RLS policies in Supabase.
- **Usability & Accessibility Tests:** Manual checks to ensure the UI is simple, intuitive, and adheres to basic accessibility guidelines.
- **Performance Tests:** Observational checks on the responsiveness of authentication and profile management operations.

### 3.2. Testing Types
- **Functional Testing:** Verifying that the feature works as per the specified functional requirements and acceptance criteria.
- **Non-Functional Testing:** Evaluating aspects like security, usability, accessibility, and performance.

### 3.3. Test Approach
- **Unit Testing:** Primarily developer-driven, using frameworks like JUnit and Mockito for Android. Supabase Edge Functions will be tested using Deno's testing framework.
- **Integration Testing:** Conducted by developers and QA, focusing on API interactions (Supabase client library) and service integrations. Mocking external dependencies where appropriate (e.g., Google OAuth for certain backend tests if possible, though full flow testing is also needed).
- **E2E / UI Testing:** Primarily QA-driven, using Android UI testing frameworks (e.g., Espresso) for automated tests and manual testing for exploratory and usability checks.
- **Security Testing:** QA and developers will collaborate. This includes verifying Supabase RLS policies, checking for secure JWT handling, and ensuring data privacy requirements are met.
- **Manual Testing:** Will be used for usability, accessibility, exploratory testing, and scenarios difficult to automate.

### 3.4. Entry and Exit Criteria
#### Entry Criteria:
- Feature specification and architecture documents are available and reviewed.
- Development of the feature module is complete and unit tested.
- Test environment is set up and configured.
- Test data is prepared.
- Build deployed to the test environment.

#### Exit Criteria:
- All planned test cases (Unit, Integration, E2E) are executed.
- All critical and high-priority defects are fixed and retested.
- Test coverage meets the defined target (e.g., 80% for unit tests, 90% E2E flow coverage).
- Test summary report is approved.
- Non-functional requirements (security, usability) are met to an acceptable level.

### 3.5. Test Environment Requirements
- **Client:**
    - Android Emulators (API levels 26+)
    - Physical Android Devices (representing common user devices, various OS versions and manufacturers)
- **Backend:**
    - Dedicated Supabase project (Development/Staging instance) with PostgreSQL database.
    - Configured Supabase Auth with Google OAuth provider.
- **External Services:**
    - Configured Google Cloud Project with OAuth 2.0 credentials for Google Sign-In.
- **Network:**
    - Stable internet connection for most tests.
    - Ability to simulate poor network conditions and offline scenarios.
- **Tools:**
    - Android Studio
    - Google Chrome (or other browser for Google login flow if initiated outside app context)
    - ADB (Android Debug Bridge)
    - Supabase Dashboard/CLI
    - Test management tool (if applicable)
    - Bug tracking tool (if applicable)

### 3.6. Test Data Management
- Test Google accounts (new and existing) will be required.
- Data for farm locations (valid, invalid, boundary values).
- Test data will be managed to ensure consistency and reusability.
- Sensitive test data (like Google account credentials) will be handled securely.

### 3.7. Tools and Technologies
- **Unit Testing (Android):** JUnit, Mockito
- **Unit Testing (Supabase Edge Functions):** Deno Test
- **UI/E2E Testing (Android):** Espresso (or alternative like UI Automator)
- **IDE:** Android Studio
- **Version Control:** Git
- **CI/CD:** GitHub Actions (as per [`AgriConnect/.github/workflows/ci.yml`](AgriConnect/.github/workflows/ci.yml))
- **Backend Management:** Supabase Dashboard

## 4. Test Scope

### 4.1. Features to be Tested
Based on the Feature Specification ([`docs/specs/User_Authentication_Profile_Management_overview.md`](docs/specs/User_Authentication_Profile_Management_overview.md)):
- **FR1, FR2, AC1, AC2:** User registration and login exclusively via Google Social Login.
- **FR3, AC1:** Automatic fetching of name and email from Google during registration.
- **FR4, AC1:** Prompting user for farm location upon initial registration.
- **FR5, AC3:** Creation and storage of user profile (Google ID, name, email, farm location).
- **FR6, AC4:** Ensuring user email addresses are stored privately and not visible to other users.
- **FR7, AC5:** Allowing logged-in users to view their own profile information (name, farm location).
- **FR8, AC6:** Allowing logged-in users to update their farm location.
- **FR10:** Graceful error handling during Google authentication.
- **NFR1 (Simplicity & Usability):** Simplicity of registration, login, profile management.
- **NFR2 (Security):** Secure authentication (Google OAuth 2.0), secure data storage (Supabase RLS).
- **NFR3 (Privacy):** Privacy of user contact details.
- **NFR4 (Performance):** Responsiveness of operations.
- **NFR5 (Reliability):** Robustness of Google Social Login integration.
- **NFR6 (Accessibility):** Adherence to accessibility best practices.
- **NFR7 (Data Integrity):** Accurate storage and retrieval of profile information.
- **UIUX1-UIUX6:** UI/UX considerations for login, profile setup, view, edit, feedback, and trust.

### 4.2. Features Not to be Tested (Out of Scope for this Feature Module)
- Traditional username/password registration or login.
- Support for other social login providers.
- Password management features (handled by Google).
- Advanced profile features (profile pictures, bios, etc.).
- User-initiated account deactivation or deletion.
- Administrative interfaces for user management.
- Linking multiple Google accounts to one AgriConnect profile.
- Session management beyond basic login/logout (unless specified by Google SDK behavior).

## 5. Test Cases
Test cases will be designed to cover functional requirements, acceptance criteria, user stories, and non-functional requirements. Each test case will include: Test Case ID, Title/Description, Preconditions, Test Steps, Expected Results, Actual Results, Status (Pass/Fail), Priority, and Test Type.

### 5.1. Unit Tests
(Focus: Individual functions/methods in ViewModels, Repositories, Utility classes, Supabase Edge Functions)

| Test Case ID | Description                                                                 | Priority |
|--------------|-----------------------------------------------------------------------------|----------|
| UT_AUTH_001  | Verify `AuthViewModel` state updates correctly during Google Sign-In attempt. | High     |
| UT_AUTH_002  | Verify `AuthViewModel` handles successful Google Sign-In callback.          | High     |
| UT_AUTH_003  | Verify `AuthViewModel` handles failed Google Sign-In callback (e.g., user cancel). | High     |
| UT_AUTH_004  | Verify `AuthViewModel` handles Google Sign-In error (e.g., network issue).    | High     |
| UT_PROFILE_001 | Verify `ProfileViewModel` correctly loads user profile data.                | High     |
| UT_PROFILE_002 | Verify `ProfileViewModel` handles profile data loading error.               | Medium   |
| UT_PROFILE_003 | Verify `ProfileViewModel` correctly initiates farm location update.         | High     |
| UT_PROFILE_004 | Verify `ProfileViewModel` handles farm location update success.             | High     |
| UT_PROFILE_005 | Verify `ProfileViewModel` handles farm location update failure.             | Medium   |
| UT_PROFILE_006 | Test input validation for farm location (if any client-side validation exists). | Medium   |
| UT_SUPA_EF_001 | (If applicable) Test Supabase Edge Function for new user profile initialization. | High     |
| UT_REPO_001  | Test `UserRepository` function for fetching profile from Supabase (mocked client). | High     |
| UT_REPO_002  | Test `UserRepository` function for updating profile in Supabase (mocked client). | High     |
| UT_REPO_003  | Test `AuthRepository` function for signing in with Google token (mocked client). | High     |

### 5.2. Integration Tests
(Focus: Interactions between client app components and backend services like Supabase Auth & DB)

| Test Case ID | Description                                                                                                | Priority |
|--------------|------------------------------------------------------------------------------------------------------------|----------|
| IT_AUTH_001  | Verify successful user registration flow: Google Sign-In SDK -> Client App -> Supabase Auth -> New User in DB. | High     |
| IT_AUTH_002  | Verify successful user login flow for existing user: Google Sign-In SDK -> Client App -> Supabase Auth.        | High     |
| IT_AUTH_003  | Verify Supabase Auth correctly verifies a valid Google ID token.                                             | High     |
| IT_AUTH_004  | Verify Supabase Auth rejects an invalid/expired Google ID token.                                             | High     |
| IT_PROFILE_001 | Verify client app can successfully create a new user profile in Supabase `profiles` table after registration. | High     |
| IT_PROFILE_002 | Verify client app can successfully fetch the current user's profile data from Supabase `profiles` table.     | High     |
| IT_PROFILE_003 | Verify client app can successfully update the `farm_location` in the Supabase `profiles` table.            | High     |
| IT_PROFILE_004 | Verify RLS: User can only fetch their own profile data. Attempt to fetch another user's profile should fail. | High     |
| IT_PROFILE_005 | Verify RLS: User can only update their own profile data. Attempt to update another user's profile should fail. | High     |
| IT_PROFILE_006 | Verify RLS: User can only insert a profile for themselves (linked to `auth.users.id`).                     | High     |
| IT_ERROR_001 | Verify error handling when Supabase service is unavailable during authentication.                            | Medium   |
| IT_ERROR_002 | Verify error handling when Supabase service is unavailable during profile operations.                        | Medium   |

### 5.3. End-to-End (E2E) / UI Tests
(Focus: Complete user flows through the application UI)

**5.3.1. Registration & Initial Profile Setup**
| Test Case ID | Description                                                                                                                               | Priority |
|--------------|-------------------------------------------------------------------------------------------------------------------------------------------|----------|
| E2E_REG_001  | **Happy Path:** New user successfully registers using "Sign in with Google", provides farm location, and sees their profile.                 | High     |
| E2E_REG_002  | User cancels Google Sign-In process; verify app returns to login screen or appropriate state.                                               | Medium   |
| E2E_REG_003  | Google Sign-In fails due to incorrect Google credentials (handled by Google UI); verify app handles return gracefully.                      | Medium   |
| E2E_REG_004  | New user registers, but skips/provides empty farm location (if allowed, then test subsequent prompt; if not allowed, test validation).       | Medium   |
| E2E_REG_005  | Verify name and email are pre-filled (or correctly fetched) from Google account after successful Google Sign-In for registration.           | High     |
| E2E_REG_006  | Verify user is prompted to enter farm location after first successful Google Sign-In.                                                       | High     |

**5.3.2. Login**
| Test Case ID | Description                                                                                                                               | Priority |
|--------------|-------------------------------------------------------------------------------------------------------------------------------------------|----------|
| E2E_LOGIN_001| **Happy Path:** Existing registered user successfully logs in using "Sign in with Google".                                                  | High     |
| E2E_LOGIN_002| Attempt login with a Google account not yet registered with AgriConnect (should follow registration flow).                                  | Medium   |
| E2E_LOGIN_003| User cancels Google Sign-In during login attempt; verify app returns to login screen.                                                       | Medium   |

**5.3.3. Profile Management**
| Test Case ID | Description                                                                                                                               | Priority |
|--------------|-------------------------------------------------------------------------------------------------------------------------------------------|----------|
| E2E_PROF_001 | Logged-in user navigates to profile screen and views their name and farm location correctly.                                                | High     |
| E2E_PROF_002 | Logged-in user successfully updates their farm location. Verify change is reflected in profile view and persisted in backend.               | High     |
| E2E_PROF_003 | User attempts to update farm location with invalid data (e.g., excessively long string, if validation exists). Test boundary conditions.    | Medium   |
| E2E_PROF_004 | Verify email address is NOT displayed on the user's profile view screen (or any other public-facing screen).                                | High     |

**5.3.4. Error Handling & Edge Cases**
| Test Case ID | Description                                                                                                                               | Priority |
|--------------|-------------------------------------------------------------------------------------------------------------------------------------------|----------|
| E2E_ERR_001  | Simulate network unavailability during Google Sign-In attempt; verify user-friendly error message.                                          | High     |
| E2E_ERR_002  | Simulate network unavailability when fetching profile data; verify appropriate feedback/cached data display.                                | Medium   |
| E2E_ERR_003  | Simulate network unavailability when updating farm location; verify error message and data not updated.                                     | Medium   |
| E2E_ERR_004  | Google authentication service unavailable/returns an error; verify user-friendly message.                                                   | High     |
| E2E_SESS_001 | App closed and reopened while logged in; verify user remains logged in (session persistence).                                               | High     |
| E2E_SESS_002 | User explicitly logs out (if logout functionality exists as part of basic session management); verify user is logged out and needs to re-login. | Medium   |
| E2E_OFF_001  | View profile information while device is offline (after successful online login and data fetch). Verify cached data is shown.               | Medium   |

### 5.4. Security Tests

| Test Case ID | Description                                                                                                                               | Priority |
|--------------|-------------------------------------------------------------------------------------------------------------------------------------------|----------|
| SEC_RLS_001  | Verify Supabase RLS: Authenticated user A cannot read profile data of user B via direct API call (if possible to simulate).                  | High     |
| SEC_RLS_002  | Verify Supabase RLS: Authenticated user A cannot update profile data of user B via direct API call.                                         | High     |
| SEC_RLS_003  | Verify Supabase RLS: Unauthenticated user cannot read any profile data.                                                                     | High     |
| SEC_RLS_004  | Verify Supabase RLS: Unauthenticated user cannot update any profile data.                                                                   | High     |
| SEC_PRIV_001 | Verify user's email address (from Google) is not exposed in any API responses accessible to other users.                                    | High     |
| SEC_PRIV_002 | Verify user's email address is not displayed in any UI accessible to other users.                                                           | High     |
| SEC_JWT_001  | Verify Supabase JWT is transmitted over HTTPS. (Tool-assisted check, e.g. network proxy)                                                    | High     |
| SEC_JWT_002  | Verify Supabase JWT is stored securely on the client device (e.g., EncryptedSharedPreferences). (Code review & device inspection)           | High     |
| SEC_INPUT_001| Test for basic input vulnerabilities on farm location field (e.g., script injection - though Supabase client libraries should mitigate).    | Medium   |

### 5.5. Usability & Accessibility Tests (Manual Checklist)

| Test Case ID | Description                                                                                                                               | Priority |
|--------------|-------------------------------------------------------------------------------------------------------------------------------------------|----------|
| USAB_UI_001  | Verify "Sign in with Google" button is prominent and clear on the login/registration screen. (UIUX1)                                       | High     |
| USAB_UI_002  | Verify profile setup (farm location input) is simple and straightforward. (UIUX2)                                                           | High     |
| USAB_UI_003  | Verify profile view clearly displays name and farm location. (UIUX3)                                                                        | High     |
| USAB_UI_004  | Verify editing farm location is intuitive. (UIUX4)                                                                                          | High     |
| USAB_FEED_001| Verify clear visual feedback (e.g., loading indicators) during authentication and data operations. (UIUX5)                                  | High     |
| USAB_ERR_001 | Verify error messages are user-friendly, simple, and provide guidance if possible. (UIUX5)                                                  | High     |
| USAB_NAV_001 | Verify overall navigation for authentication and profile management is minimal and easy. (NFR1)                                             | High     |
| ACC_CONT_001 | Check for sufficient color contrast for text and UI elements. (NFR6)                                                                        | Medium   |
| ACC_TAPP_001 | Check for easily tappable targets (buttons, input fields). (NFR6)                                                                           | Medium   |
| ACC_TEXT_001 | Check for clear visual hierarchy and readable font sizes. (NFR6)                                                                            | Medium   |

### 5.6. Performance Tests (Observational for MVP)

| Test Case ID | Description                                                                                                                               | Priority |
|--------------|-------------------------------------------------------------------------------------------------------------------------------------------|----------|
| PERF_LOGIN_001| Observe time taken for Google Sign-In process (from button tap to logged-in state) on good network.                                       | Medium   |
| PERF_LOGIN_002| Observe time taken for Google Sign-In process on simulated poor network.                                                                    | Medium   |
| PERF_PROF_001| Observe time taken to load profile view screen.                                                                                             | Medium   |
| PERF_PROF_002| Observe time taken to save updated farm location.                                                                                           | Medium   |

## 6. Test Data Requirements

- **Google Accounts:**
    - At least 2-3 new Google accounts (not previously used with AgriConnect) for testing registration.
    - At least 2-3 existing Google accounts (already registered with AgriConnect) for testing login.
    - One Google account with 2-Step Verification enabled (if specific handling is expected).
    - One Google account with a very long name or special characters in name (to check display).
- **Farm Location Data:**
    - Valid locations: e.g., "Village Name, District Name, State", "Town, State"
    - Invalid locations (if validation rules apply): e.g., empty string, excessively long string (e.g., >255 chars), special characters if not allowed.
    - Boundary values for length if applicable.
- **User Profile States:**
    - New user (no profile yet in `profiles` table, only in `auth.users`).
    - Existing user with complete profile.
    - Existing user with profile but potentially missing farm location (to test update flow).

## 7. Test Environment
(As detailed in Section 3.5 Test Environment Requirements)

- **Client:** Android Emulators (Android 8.0 Oreo / API 26, Android 12L / API 32) and Physical Devices (e.g., Samsung Galaxy A-series, Xiaomi Redmi Note series, Android versions 9-13).
- **Backend:** Supabase Project (e.g., `agriconnect-dev` or `agriconnect-staging`).
    - Supabase Auth configured with Google Provider (Client ID, Client Secret).
    - `profiles` table created as per architecture with RLS policies enabled.
- **External:** Google Cloud Project with OAuth consent screen configured and API credentials generated. Test users added if consent screen is in testing mode.
- **Network Simulation:** Tools like Android Studio's emulator network settings, or Charles Proxy/Fiddler for more granular control if needed.

## 8. Test Deliverables
- **Test Plan:** This document.
- **Test Cases:** Detailed within this document.
- **Test Execution Reports:** To be generated during the testing phase, summarizing executed tests, pass/fail status, and coverage.
- **Bug Reports:** Detailed reports for any defects found, logged in a bug tracking system (e.g., GitHub Issues).
- **Test Summary Report:** A final report summarizing all testing activities, outcomes, and overall quality assessment of the feature.

## 9. Risks and Contingencies

| Risk                                       | Likelihood | Impact | Mitigation                                                                                                | Contingency                                                                                             |
|--------------------------------------------|------------|--------|-----------------------------------------------------------------------------------------------------------|---------------------------------------------------------------------------------------------------------|
| Google API / Supabase Service Unavailability | Low        | High   | Monitor service status. Design for graceful degradation.                                                  | Postpone testing dependent on the service. Communicate delay.                                           |
| Incorrect RLS Policy Implementation        | Medium     | High   | Thorough code reviews and specific RLS test cases. Use Supabase SQL editor for policy testing.            | Allocate extra time for debugging and fixing RLS policies. Escalate if complex.                         |
| Issues with Google Sign-In SDK Integration | Medium     | Medium | Follow official documentation carefully. Test on multiple devices/OS versions.                            | Allocate developer time for troubleshooting SDK issues. Consult Google/Supabase support if needed.      |
| Test Data Management Issues                | Medium     | Medium | Create and document test accounts clearly. Securely store credentials.                                    | Reset test environment or create new test accounts if data becomes corrupted.                           |
| Delays in Test Environment Setup           | Low        | Medium | Plan and start environment setup early. Verify all configurations.                                        | Allocate buffer time in schedule. Have a backup plan for environment components if possible.            |
| Limited Device Availability for Testing    | Medium     | Medium | Prioritize testing on most common device types. Utilize emulators extensively.                            | Request additional devices if critical gaps identified. Clearly document tested configurations.         |

## 10. Roles and Responsibilities
- **Development Team:**
    - Implement unit tests.
    - Fix defects found during testing.
    - Support QA team in understanding the feature and debugging issues.
- **QA Team / Testers:**
    - Develop and execute integration, E2E, security, usability, and performance tests.
    - Report defects and track them to closure.
    - Prepare test summary reports.
    - Maintain this test plan.
- **Project Manager / Product Owner:**
    - Review and approve the test plan.
    - Prioritize defect fixing.
    - Make decisions on release readiness based on test results.

This test plan will be a living document and will be updated as necessary throughout the project lifecycle.