# Add project specific ProGuard rules here.
# You can control the set of applied configuration files using the
# proguardFiles setting in build.gradle.kts.
#
# For more details, see
#   http://developer.android.com/guide/developing/tools/proguard.html

# If your project uses WebView with JS, uncomment the following
# and specify the fully qualified class name to the JavaScript interface
# class:
#-keepclassmembers class fqcn.of.javascript.interface.for.webview {
#   public *;
#}

# Uncomment this to preserve the line number information for
# debugging stack traces.
#-keepattributes SourceFile,LineNumberTable

# If you keep the line number information, uncomment this to
# hide the original source file name.
#-renamesourcefileattribute SourceFile

# Retain annotations (important for Hilt, Room, and other annotation-based frameworks)
-keepattributes *Annotation*

# Keep Kotlin metadata (required for Kotlin reflection and coroutines)
-keepclassmembers class kotlin.Metadata { *; }
-keep class kotlin.Metadata { *; }

# Retain generated code for Kotlin coroutines
-dontwarn kotlinx.coroutines.**

# Keep lifecycle runtime (ViewModel, LiveData, etc.)
-keepclassmembers class * extends androidx.lifecycle.ViewModel {
    <init>(...);
}

# For Compose to avoid issues with reflection
-keep class androidx.compose.** { *; }
-dontwarn androidx.compose.**
-dontnote androidx.compose.**

# Keep Google Material Components
-keep class com.google.android.material.** { *; }

# Room database
-keep class androidx.room.** { *; }
-keep class * implements androidx.room.RoomDatabase { *; }
-keep class * extends androidx.room.RoomDatabase { *; }

# Retrofit and Moshi (JSON serialization/deserialization)
-keepclassmembers class ** extends com.squareup.moshi.JsonAdapter {
    <init>(...);
}
-dontwarn com.squareup.moshi.**
-keep class com.squareup.moshi.** { *; }

# Retrofit: keep service method signatures
-keep interface com.squareup.retrofit2.** { *; }
-dontwarn com.squareup.retrofit2.**

# Keep the data classes used by Moshi (e.g., CharacterListApi)
-keep class com.sberg413.rickandmorty.data.remote.dto.** { *; }

# Keep Moshi annotations to make sure Moshi can serialize/deserialize properly
-keep @com.squareup.moshi.JsonClass class * { *; }
-keep @com.squareup.moshi.Json class * { *; }

# Keep Retrofit service interfaces (e.g., CharacterService)
-keep interface com.sberg413.rickandmorty.data.remote.** { *; }

# OkHttp Logging Interceptor (used with Retrofit)
-dontwarn okhttp3.**

# Glide generated API
-keep class com.bumptech.glide.** { *; }
-dontwarn com.bumptech.glide.**

# Glide AppGlideModule
-keep class * extends com.bumptech.glide.module.AppGlideModule
-keep class * extends com.bumptech.glide.module.LibraryGlideModule
-keep class * extends com.bumptech.glide.module.GlideModule

# Hilt
-keep class dagger.hilt.** { *; }
-keepclassmembers class dagger.hilt.** { *; }

# Generated code from Hilt
-keep class **_HiltModules.** { *; }

-dontwarn dagger.hilt.internal.**

# Navigation (Fragment and Compose)
-dontwarn androidx.navigation.**
-keep class androidx.navigation.** { *; }

# Compose Navigation
-keep class androidx.hilt.navigation.compose.** { *; }
-dontwarn androidx.hilt.navigation.compose.**

# Keep Room schema for reflection-based processing
-keep class androidx.room.** { *; }
-dontwarn androidx.room.**

# Prevent warnings for Mockito usage in test builds
-dontwarn org.mockito.**
-keep class org.mockito.** { *; }

# Espresso UI tests
-dontwarn androidx.test.espresso.**
-keep class androidx.test.espresso.** { *; }
