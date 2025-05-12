# AgriConnect

AgriConnect is a comprehensive mobile application designed to empower farmers by providing them with direct market access, real-time price information, agricultural advisory services, and efficient logistics solutions.

## Project Structure

-   `android_app/`: Contains the Android mobile application source code.
    -   `gradlew`, `gradlew.bat`: Gradle wrapper scripts for building and managing the Android project.
    -   `gradle/wrapper/gradle-wrapper.jar`: The Gradle wrapper JAR.
    -   `gradle/wrapper/gradle-wrapper.properties`: Gradle wrapper configuration.
-   `supabase/`: Contains the Supabase backend configuration, database migrations, and edge functions.
    -   `supabase/migrations/`: Database schema migrations.
    -   `supabase/functions/`: Serverless edge functions.
-   `docs/`: Project documentation, including Master Project Plan, PRD, architecture diagrams, and feature specifications (Note: these are typically in the root `docs/` folder of the workspace, not within `AgriConnect/docs/` unless specifically structured that way).

## Setup Instructions

### Android Application

1.  Ensure you have Android Studio installed.
2.  Open the `AgriConnect/android_app` directory as a project in Android Studio.
3.  The project uses the Gradle wrapper. Android Studio should automatically use it to sync and download dependencies. If building from the command line, use `./gradlew` (on Linux/macOS) or `gradlew.bat` (on Windows) for Gradle tasks (e.g., `./gradlew assembleDebug`).
4.  Configure any necessary API keys or environment variables as per the application's requirements (e.g., Supabase URL and Anon Key in a configuration file).
5.  Build and run the application on an emulator or a physical device.

### Supabase Backend

1.  **Install Supabase CLI:**
    Follow the instructions at [https://supabase.com/docs/guides/cli](https://supabase.com/docs/guides/cli) to install the Supabase CLI.

2.  **Initialize Supabase Project (if not already done locally):**
    Navigate to the `AgriConnect/supabase/` directory in your terminal:
    ```bash
    cd AgriConnect/supabase
    supabase init
    ```
    This will create a local Supabase project configuration.

3.  **Link to Your Supabase Cloud Project:**
    If you have an existing Supabase project on [app.supabase.com](https://app.supabase.com), link your local project to it:
    ```bash
    supabase login
    supabase link --project-ref your-project-ref-from-supabase-dashboard
    ```
    Replace `your-project-ref-from-supabase-dashboard` with your actual project reference.

4.  **Apply Database Migrations:**
    To apply the initial schema and any subsequent migrations to your local or linked Supabase database:
    ```bash
    supabase db push
    ```
    If you are working with a local development database started with `supabase start`, this will apply migrations to it. If linked to a cloud project, it will apply to the cloud database (use with caution on production).

5.  **Start Local Development Services (Optional):**
    To run Supabase services locally (Postgres, GoTrue, Storage, etc.):
    ```bash
    supabase start
    ```
    This is useful for local development and testing.

6.  **Deploy Edge Functions:**
    Navigate to the `AgriConnect/supabase/` directory. To deploy a specific function (e.g., `user-profile-init`):
    ```bash
    supabase functions deploy user-profile-init --project-ref your-project-ref
    ```
    To deploy all functions:
    ```bash
    supabase functions deploy --project-ref your-project-ref
    ```

## Further Development

Refer to the [`docs/Master_Project_Plan.md`](docs/Master_Project_Plan.md) (located at the workspace root) for the overall project roadmap and feature details.
Individual feature specifications and architecture documents can also be found in the root `docs/` directory.