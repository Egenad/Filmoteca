import java.util.Properties
import java.io.FileInputStream

plugins {
    id("com.android.application")
    id("org.jetbrains.kotlin.android")
    id("com.google.gms.google-services")
    id("com.google.android.libraries.mapsplatform.secrets-gradle-plugin")
}

android {
    namespace = "es.ua.eps.filmoteca"
    compileSdk = 35

    defaultConfig {
        applicationId = "es.ua.eps.filmoteca"
        minSdk = 21
        targetSdk = 35
        versionCode = 1
        versionName = "1.0"

        testInstrumentationRunner = "androidx.test.runner.AndroidJUnitRunner"

        val properties = Properties()
        if (rootProject.file("local.properties").exists()) {
            properties.load(FileInputStream(rootProject.file("local.properties")))
        }

        manifestPlaceholders["mapsApiKey"] = properties.getProperty("MAPS_API_KEY", "")
    }

    buildTypes {
        release {
            isMinifyEnabled = false
            proguardFiles(
                getDefaultProguardFile("proguard-android-optimize.txt"),
                "proguard-rules.pro"
            )
            signingConfig = signingConfigs.getByName("debug")
        }
    }
    compileOptions {
        sourceCompatibility = JavaVersion.VERSION_1_8
        targetCompatibility = JavaVersion.VERSION_1_8
    }
    kotlinOptions {
        jvmTarget = "1.8"
    }
    viewBinding {
        enable = true
    }
    buildFeatures {
        viewBinding = true
    }
}

dependencies {

    implementation("androidx.core:core-ktx:1.13.0")
    implementation("androidx.appcompat:appcompat:1.7.0")
    implementation("com.google.android.material:material:1.11.0")
    implementation("androidx.recyclerview:recyclerview:1.1.0")
    implementation("androidx.constraintlayout:constraintlayout:2.1.4")
    implementation("androidx.activity:activity:1.8.0")

    implementation("androidx.credentials:credentials:1.5.0-rc01")
    implementation("androidx.credentials:credentials-play-services-auth:1.5.0-rc01")
    implementation("com.google.android.libraries.identity.googleid:googleid:1.1.1")

    implementation(platform("com.google.firebase:firebase-bom:33.10.0"))
    implementation("com.google.firebase:firebase-analytics")
    implementation("com.google.firebase:firebase-messaging")
    implementation("io.coil-kt:coil:2.6.0")

    implementation("com.google.android.gms:play-services-maps:19.1.0")
    implementation("com.google.android.gms:play-services-ads:23.0.0")
    implementation("com.google.android.gms:play-services-location:21.3.0")

    implementation("com.github.bumptech.glide:glide:4.15.1")
    annotationProcessor("com.github.bumptech.glide:compiler:4.15.1")

    implementation("com.auth0:java-jwt:3.18.2")

    testImplementation("junit:junit:4.13.2")
    androidTestImplementation("androidx.test.ext:junit:1.1.5")
    androidTestImplementation("androidx.test.espresso:espresso-core:3.5.1")

}