# AgriConnect Master Project Plan

This document outlines the comprehensive plan for the AgriConnect project, providing details necessary for framework scaffolding and initial development.

## 1. Introduction

AgriConnect is a mobile application designed to empower small and marginal Indian farmers by providing a marketplace for produce, local market price information, crop advisory, post-harvest guidance, and transportation connections. The application prioritizes accessibility, low-bandwidth operation, and user-friendliness for farmers with limited technical skills.

## 2. Technology Stack

The following technologies will be utilized for the development of AgriConnect:

### 2.1. Frontend (Mobile Application)
*   **Platform:** Android
*   **Primary Language:** Kotlin
*   **UI Framework:** Jetpack Compose (for modern, declarative UI development)
*   **Architecture:** MVVM (Model-View-ViewModel) or similar modern Android architecture pattern.
*   **Local Data Caching:** SQLite (via Room Persistence Library) for offline access to essential data like advisory content, last-fetched prices, and user's listings.

### 2.2. Backend
*   **Platform:** Supabase
*   **Database:** PostgreSQL (managed by Supabase)
*   **Authentication:** Supabase Auth, configured with Google Social Login as the primary authentication method.
*   **Server-side Logic:** Supabase Edge Functions (TypeScript/JavaScript) for any custom business logic, data validation, or integrations that cannot be handled directly by the client or database policies.
*   **Storage:** Supabase Storage for user-uploaded images (e.g., for produce listings, if added later) or advisory content images.

### 2.3. APIs & Integrations
*   **Authentication:** Google Sign-In for Android.
*   **Market Price Data:** (To be determined - potential integration with government APIs like Agmarknet or other third-party data providers for real-time commodity prices. Initial MVP might use manually updated or less frequent data.)

## 3. Key Features/Modules for Initial Scaffolding

The initial scaffolding will focus on creating the boilerplate code, directory structures, and basic test stubs for the following core features/modules:

1.  **User Authentication & Profile Management:**
    *   Google Social Login integration.
    *   Basic user profile creation and management (name, location - linked from Google).
2.  **Marketplace (Produce Listing & Browsing):**
    *   Functionality to create, view, update, and delete produce listings (crop type, quantity, price).
    *   Browse and search existing listings.
3.  **Market Price Discovery:**
    *   Display local market prices for key commodities.
    *   Mechanism to update price data (manual or via API).
4.  **Agricultural Advisory Services (Crop & Post-Harvest):**
    *   Displaying advisory content (text, images) for crop management (pests, diseases, climate).
    *   Displaying guidance for post-harvest handling and storage.
5.  **Transportation & Logistics (Request & Browse):**
    *   Functionality for farmers to post transportation requests.
    *   Ability to browse available transporters (initially, this might be a static list or simple user-generated entries).
6.  **User Feedback System:**
    *   Simple mechanism for users to submit feedback about the app.

## 4. Project Directory Structure (Conceptual Top-Level)

A logical directory structure will be established to organize the project components effectively.

```
/AgriConnect/
├── android_app/                 # Android Studio Project Root
│   ├── app/
│   │   ├── src/main/java/       # Kotlin source code (organized by feature/layer)
│   │   │   ├── com/agriconnect/
│   │   │   │   ├── core/        # Core components (networking, utils, base classes)
│   │   │   │   ├── data/        # Repositories, data sources (local/remote)
│   │   │   │   ├── domain/      # Use cases, domain models
│   │   │   │   ├── ui/          # UI components (Activities, Fragments, ViewModels, Composables)
│   │   │   │   │   ├── auth/
│   │   │   │   │   ├── marketplace/
│   │   │   │   │   ├── prices/
│   │   │   │   │   ├── advisory/
│   │   │   │   │   ├── transport/
│   │   │   │   │   └── feedback/
│   │   │   │   └── AgriConnectApplication.kt # Application class
│   │   ├── src/main/res/        # Android resources (layouts, drawables, strings, etc.)
│   │   ├── src/androidTest/     # Instrumentation tests
│   │   └── src/test/          # Unit tests
│   ├── build.gradle             # Module-level Gradle file
│   ├── gradle/
│   └── build.gradle.kts         # Project-level Gradle file (or build.gradle)
│
├── supabase/                    # Supabase project configuration and backend assets
│   ├── functions/               # Supabase Edge Functions
│   │   ├── user-profile-init/   # Example: function to initialize profile on new user
│   │   │   └── index.ts
│   │   └── ...
│   ├── migrations/              # Database schema migrations (managed by Supabase CLI)
│   │   └── YYYYMMDDHHMMSS_initial_schema.sql
│   ├── seed.sql                 # Optional: Initial data seeding script
│   └── config.toml              # Supabase project configuration (for Supabase CLI)
│
├── docs/                        # Project documentation
│   ├── PRD.md
│   ├── Master_Project_Plan.md   # This file
│   ├── architecture/            # Detailed architecture documents per module
│   │   ├── User_Authentication_Profile_Management_architecture.md
│   │   └── ...
│   └── specs/                   # Feature specification documents
│       ├── User_Authentication_Profile_Management_overview.md
│       └── ...
│
├── research/                    # Research documents and findings
│
├── .gitignore
└── README.md
```

**Note:** The `android_app/` structure is a typical Android project layout. The `supabase/` directory is for assets managed with the Supabase CLI, including database migrations and edge functions.

## 5. Development Branch

*   **Target Branch for Initial Development:** `main`

This branch will serve as the primary line of development for the MVP. Feature branches will be created from `main` and merged back upon completion and review.

## 6. Next Steps

With this plan in place, the next phase involves:
1.  **DevOps Setup:** Initialize Git repository, set up CI/CD pipeline basics.
2.  **Framework Scaffolding:**
    *   Create the Android project structure with Kotlin and Jetpack Compose.
    *   Set up the Supabase project.
    *   Implement initial database schema based on [`docs/PRD.md`](docs/PRD.md) and module requirements.
    *   Generate boilerplate code for the key features/modules listed in Section 3.
    *   Create initial test stubs (unit and instrumentation tests).
3.  **Detailed Design & Implementation:** Proceed with detailed design and implementation of each feature, starting with User Authentication.