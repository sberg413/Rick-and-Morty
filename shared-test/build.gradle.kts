plugins {
    alias(libs.plugins.android.library)
    alias(libs.plugins.kotlin.android)
}

android {
    namespace = "com.sberg413.rickandmorty.shared.test"
    compileSdk = 34

    defaultConfig {
        minSdk = 24
    }

    compileOptions {
        sourceCompatibility = JavaVersion.VERSION_17
        targetCompatibility = JavaVersion.VERSION_17
    }
    kotlinOptions {
        jvmTarget = "17"
    }
}

dependencies {

    implementation(project(":app"))
    implementation(libs.androidx.appcompat)
    implementation(libs.androidx.core.ktx)
    implementation(libs.androidx.core.testing)
    implementation(libs.kotlinx.coroutines.test)
    implementation(libs.androidx.fragment.testing)
    implementation(libs.androidx.test.rules)
    implementation(libs.material)
    implementation(libs.junit)
    implementation(libs.retrofit)
}