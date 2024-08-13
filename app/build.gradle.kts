
plugins {
    alias(libs.plugins.android.application)
    alias(libs.plugins.kotlin.android)
    alias(libs.plugins.kapt)
    alias(libs.plugins.ksp)
    alias(libs.plugins.hilt)
    alias(libs.plugins.kotlin.parcelize)
    alias(libs.plugins.navigation.safeargs)
}

apply( from = "$rootDir/jacoco.gradle")

android {
    compileSdk = 34

    defaultConfig {
        applicationId = "com.sberg413.rickandmorty"
        minSdk = 24
        targetSdk = 34
        versionCode = 1
        versionName = "1.0"

        testInstrumentationRunner = "com.sberg413.rickandmorty.CustomTestRunner"
    }

    buildTypes {
        debug {
            // applicationIdSuffix = ".debug"
            isMinifyEnabled = false
            // enableUnitTestCoverage = true
            enableAndroidTestCoverage= true
        }
        release {
            isMinifyEnabled = true
            proguardFiles(getDefaultProguardFile("proguard-android-optimize.txt"), "proguard-rules.pro")
        }
    }

    buildFeatures {
        compose = true
    }

    composeOptions {
        kotlinCompilerExtensionVersion = libs.versions.kotlinCompilerExt.get()
    }

    compileOptions {
        sourceCompatibility = JavaVersion.VERSION_17
        targetCompatibility = JavaVersion.VERSION_17
    }

    kotlinOptions {
        jvmTarget = "17"
        freeCompilerArgs += listOf("-opt-in=kotlin.RequiresOptIn")
    }

    testOptions {
        // execution = "ANDROIDX_TEST_ORCHESTRATOR"
        animationsDisabled = true
        unitTests {
            isReturnDefaultValues = true
            isIncludeAndroidResources = true
        }
    }

    namespace = "com.sberg413.rickandmorty"
}

dependencies {

    implementation(libs.androidx.core.ktx)
    implementation(libs.androidx.appcompat)
    implementation(libs.material)
    implementation(libs.androidx.constraintlayout)
    implementation(libs.androidx.lifecycle.runtime.ktx )
    implementation(libs.androidx.lifecycle.viewmodel.ktx)
    implementation(libs.androidx.swiperefreshlayout)
    implementation(libs.androidx.activity.ktx)
    implementation(libs.androidx.fragment.ktx)
    implementation(libs.androidx.benchmark.common)
    implementation(libs.androidx.navigation.fragment.ktx)
    implementation(libs.androidx.navigation.ui.ktx)
    implementation(libs.androidx.paging.runtime.ktx)
    implementation(libs.androidx.paging.compose)

    implementation(libs.retrofit)
    implementation(libs.converter.moshi)
    implementation(libs.logging.interceptor)
    implementation(libs.kotlinx.coroutines.android)
    implementation(libs.glide)
    implementation(libs.glide.compose)
    implementation(libs.androidx.legacy.support.v4)
    ksp(libs.glide.compiler)

    implementation(libs.hilt.android)
    kapt(libs.hilt.compiler)

    /**** COMPOSE START ****/
    val composeBom = platform(libs.androidx.compose.bom)
    implementation(composeBom)
    implementation(libs.androidx.material3)
    // Android Studio Preview support
    implementation(libs.androidx.ui.tooling.preview)
    debugImplementation(libs.androidx.ui.tooling)
    implementation(libs.androidx.activity.compose)
    implementation(libs.androidx.lifecycle.viewmodel.compose)
    implementation(libs.androidx.ui.text.google.fonts)

    // UI Tests
    androidTestImplementation(composeBom)
    androidTestImplementation(libs.androidx.ui.test.junit4)

    testImplementation(composeBom)
    testImplementation(libs.androidx.ui.test.junit4)

    /* The following should actually be added to the debugImplementation since it
     * will place ui-test-manifest in the build. Including it in implementation allows for
     * running ./gradlew build which will use the release build for testing.
     * more info: https://github.com/android/compose-samples/issues/969*/
    implementation(libs.androidx.ui.test.manifest)
    /**** COMPOSE END ****/

    /**** Room Start ****/
    implementation(libs.androidx.room.runtime)
    annotationProcessor(libs.androidx.room.compiler)
    implementation(libs.androidx.room.ktx)
    implementation(libs.androidx.room.paging)
    ksp(libs.androidx.room.compiler)

    androidTestImplementation(libs.androidx.room.testing)
    testImplementation(libs.androidx.room.testing)
    /**** Room End ****/

    androidTestImplementation(project(":shared-test"))
    androidTestImplementation(libs.androidx.core)
    androidTestImplementation(libs.androidx.fragment.testing)
    androidTestImplementation(libs.androidx.test.runner)
    androidTestImplementation(libs.androidx.test.rules)
    androidTestImplementation(libs.androidx.junit.ktx)
    androidTestImplementation(libs.kotlinx.coroutines.test)
    androidTestImplementation(libs.hilt.android.testing)
    androidTestImplementation(libs.mockito.core)
    // androidTestImplementation(libs.mockito.inline)
    androidTestImplementation(libs.mockito.android)
    kaptAndroidTest(libs.hilt.compiler)

    testImplementation(project(":shared-test"))
    testImplementation(libs.junit)
    testImplementation(libs.androidx.core.testing)
    testImplementation(libs.androidx.fragment.testing)
    testImplementation(libs.androidx.junit)
    testImplementation(libs.androidx.test.runner)
    testImplementation(libs.mockito.core)
    testImplementation(libs.mockito.inline)
    testImplementation(libs.robolectric)
    testImplementation(libs.androidx.paging.testing)
    testImplementation(libs.turbine)
    testImplementation(libs.kotlinx.coroutines.test)
    testImplementation(libs.mockito.kotlin)

}

kapt {
    correctErrorTypes = true
}

hilt {
    // added to prevent build warning
    enableAggregatingTask = true
}
