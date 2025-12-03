plugins {
    alias(libs.plugins.android.application)
}

android {
    namespace = "com.venegas.moneytracker"
    compileSdk {
        version = release(36)
    }
    buildFeatures {
        buildConfig = true
    }

    defaultConfig {
        applicationId = "com.venegas.moneytracker"
        minSdk = 33
        targetSdk = 36
        versionCode = 1
        versionName = "1.0"

        buildConfigField("String", "EXCHANGE_API_KEY", "\"${providers.gradleProperty("EXCHANGE_RATE_API_KEY").get()}\"")

        testInstrumentationRunner = "androidx.test.runner.AndroidJUnitRunner"
    }

    buildTypes {
        release {
            isMinifyEnabled = false
            proguardFiles(
                getDefaultProguardFile("proguard-android-optimize.txt"),
                "proguard-rules.pro"
            )
        }
    }
    compileOptions {
        sourceCompatibility = JavaVersion.VERSION_11
        targetCompatibility = JavaVersion.VERSION_11
    }
}

dependencies {
    implementation(libs.appcompat)
    implementation(libs.material)
    implementation(libs.activity)
    implementation(libs.constraintlayout)
    implementation("androidx.lifecycle:lifecycle-viewmodel:2.7.0")
    implementation("androidx.lifecycle:lifecycle-livedata:2.7.0")
    implementation("androidx.lifecycle:lifecycle-runtime:2.7.0")
    implementation("androidx.room:room-runtime:2.6.1")
    annotationProcessor("androidx.room:room-compiler:2.6.1")
    implementation("com.squareup.retrofit2:retrofit:2.9.0")
    implementation("com.squareup.retrofit2:converter-gson:2.9.0")
    implementation("androidx.navigation:navigation-fragment:2.7.6")
    implementation("androidx.navigation:navigation-ui:2.7.6")
    implementation("com.github.PhilJay:MPAndroidChart:v3.1.0")
    implementation("androidx.security:security-crypto:1.1.0-alpha06")

    testImplementation(libs.junit)
    androidTestImplementation(libs.ext.junit)
    androidTestImplementation(libs.espresso.core)
}