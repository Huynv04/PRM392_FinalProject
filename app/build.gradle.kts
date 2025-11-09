plugins {
    alias(libs.plugins.android.application)
    alias(libs.plugins.kotlin.android) // Tên này sẽ trỏ đến 'kotlin-android' trong TOML
    alias(libs.plugins.kotlin.kapt)    // Tên này sẽ trỏ đến 'kotlin-kapt' trong TOML
    alias(libs.plugins.google.services)
}

android {
    namespace = "com.example.fuportal"
    compileSdk = 36

    defaultConfig {
        applicationId = "com.example.fuportal"
        minSdk = 27
        targetSdk = 36
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
        sourceCompatibility = JavaVersion.VERSION_17
        targetCompatibility = JavaVersion.VERSION_17
    }
    kotlinOptions {
        jvmTarget = "17" // Đặt phiên bản JVM target cho Kotlin
    }
    buildFeatures {
        viewBinding = true
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
    val room_version = "2.6.1"

    implementation("androidx.room:room-runtime:$room_version")
// Quan trọng: "annotationProcessor" phải đổi thành "kapt" cho Kotlin
    kapt("androidx.room:room-compiler:$room_version")
    implementation("androidx.room:room-ktx:$room_version")
    implementation("org.mindrot:jbcrypt:0.4")
    implementation("com.squareup.retrofit2:retrofit:2.9.0")
    implementation("com.squareup.retrofit2:converter-gson:2.9.0")

    implementation(platform(libs.firebase.bom))
    // 2. Các thư viện Firebase này giữ nguyên, chúng sẽ tự lấy version từ BoM
    implementation(libs.firebase.auth)
    implementation(libs.firebase.firestore)
    implementation(libs.firebase.messaging)

}
