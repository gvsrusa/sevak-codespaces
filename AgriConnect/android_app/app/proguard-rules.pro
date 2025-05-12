# Add project specific ProGuard rules here.
# By default, the flags in this file are appended to flags specified
# in /Users/user/Library/Android/sdk/tools/proguard/proguard-android-optimize.txt
# You can edit the include path and order by changing the proguardFiles
# directive in build.gradle.kts.
#
# For more details, see
#   http://developer.android.com/guide/developing/tools/proguard.html

# Add any project specific keep rules here:

# If you use reflection or JNI write specific rules.
# For example, if your library uses reflection to access methods by name:
#   -keep class com.example.MyClass {
#     public <methods>;
#   }

# If you use Gson, you need to add rules for your model classes:
#   -keep class com.example.MyModelClass { <fields>; }

# If you use libraries that require specific ProGuard rules, add them here.
# For example, for Retrofit:
#   -dontwarn retrofit2.**
#   -keep class retrofit2.** { *; }
#   -keepattributes Signature
#   -keepattributes Exceptions

# For OkHttp:
#   -dontwarn okhttp3.**
#   -dontwarn okio.**
#   -dontwarn javax.annotation.**
#   # A resource is loaded with a relative path so the package name cannot be easily determined.
#   -keepnames class okhttp3.internal.publicsuffix.PublicSuffixDatabase

# For Coroutines
-keepnames class kotlinx.coroutines.internal.MainDispatcherFactory {}
-keepnames class kotlinx.coroutines.android.AndroidDispatcherFactory {}
-keepnames class kotlinx.coroutines.CoroutineExceptionHandler {}
-keepclassmembernames class kotlinx.** {
    volatile <fields>;
}
-keepclassmembers class **$$serializer {
    public static final **$$serializer INSTANCE;
    public final synthetic Lkotlinx/serialization/descriptors/SerialDescriptor; getDescriptor();
}

# For Hilt
-keepclassmembers class * extends androidx.lifecycle.ViewModel {
  @com.google.dagger.hilt.android.lifecycle.HiltViewModel <init>(...);
}
-keepclassmembers class * implements androidx.hilt.work.HiltWorkerFactory {
  @javax.inject.Inject <init>(...);
}
-dontwarn dagger.hilt.internal.aggregatedroot.codegen.*
-keep class dagger.hilt.internal.aggregatedroot.codegen.** { *; }
-keep class Hilt_*.** { *; } # Keep Hilt generated classes
-keep class *_HiltModules.** { *; } # Keep Hilt generated modules
-keep @dagger.hilt.android.HiltAndroidApp class * { *; }
-keep @dagger.hilt.DefineComponent class * { *; }
-keep @dagger.hilt.EntryPoint class * { *; }
-keep @dagger.hilt.InstallIn class * { *; }
-keep @dagger.hilt.components.SingletonComponent class * { *; }
-keep @javax.inject.Inject class * { *; }
-keep @javax.inject.Singleton class * { *; }