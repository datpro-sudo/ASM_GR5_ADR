plugins {
    alias(libs.plugins.android.application)
}

android {
    namespace = "com.example.asm_adr"
    compileSdk = 35

    defaultConfig {
        applicationId = "com.example.asm_adr"
        minSdk = 24
        targetSdk = 34
        versionCode = 1
        versionName = "1.0"

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
    testImplementation(libs.junit)
    androidTestImplementation(libs.ext.junit)
    androidTestImplementation(libs.espresso.core)

    implementation ("androidx.viewpager2:viewpager2:1.0.0")

    implementation ("com.google.android.material:material:1.9.0")


    implementation ("com.github.PhilJay:MPAndroidChart:v3.1.0")


}

