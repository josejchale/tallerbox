plugins {
    alias(libs.plugins.android.application)
    alias(libs.plugins.kotlin.android)
    alias(libs.plugins.kotlin.compose)
    kotlin("kapt")
}

// Se define la ubicación del archivo de firma (debe estar en la raíz del proyecto)
val keystoreFile = project.rootProject.file("tallerbox_keystore.jks") // <-- ¡REVISA QUE ESTE NOMBRE SEA CORRECTO!

android {
    // ---- BLOQUE DE FIRMA MODIFICADO (SIN EL 'IF') ----
    signingConfigs {
        create("release") {
            storeFile = keystoreFile
            storePassword = System.getenv("TALLERBOX_KEYSTORE_PASSWORD") ?: property("TALLERBOX_KEYSTORE_PASSWORD") as String
            keyAlias = System.getenv("TALLERBOX_KEY_ALIAS") ?: property("TALLERBOX_KEY_ALIAS") as String
            keyPassword = System.getenv("TALLERBOX_KEY_PASSWORD") ?: property("TALLERBOX_KEY_PASSWORD") as String
        }
    }
    // ---- FIN DEL BLOQUE DE FIRMA ----

    namespace = "com.tallerbox.app"
    compileSdk = 36

    defaultConfig {
        applicationId = "com.tallerbox.app"
        minSdk = 29
        targetSdk = 36
        versionCode = 2
        versionName = "1.1"

        testInstrumentationRunner = "androidx.test.runner.AndroidJUnitRunner"

        //noinspection WrongGradleMethod
        kapt {
            arguments {
                arg("room.schemaLocation", "$projectDir/schemas")
            }
        }
    }

    buildTypes {
        release {
            isMinifyEnabled = false
            proguardFiles(
                getDefaultProguardFile("proguard-android-optimize.txt"),
                "proguard-rules.pro"
            )
            setProperty("archivesBaseName", "TallerBox-v${defaultConfig.versionName}")
            signingConfig = signingConfigs.getByName("debug")

            // ---- LÍNEA PARA ASIGNAR LA FIRMA ----
          //  signingConfig = signingConfigs.getByName("release")
        }
    }
    compileOptions {
        sourceCompatibility = JavaVersion.VERSION_11
        targetCompatibility = JavaVersion.VERSION_11
    }
    kotlinOptions {
        jvmTarget = "11"
    }
    buildFeatures {
        compose = true
    }
}

dependencies {
    // ... (tus dependencias no cambian)
    implementation(libs.androidx.core.ktx)
    implementation(libs.androidx.lifecycle.runtime.ktx)
    implementation(libs.androidx.activity.compose)
    implementation(platform(libs.androidx.compose.bom))
    implementation(libs.androidx.compose.ui)
    implementation(libs.androidx.compose.ui.graphics)
    implementation(libs.androidx.compose.ui.tooling.preview)
    implementation(libs.androidx.compose.material3)
    implementation(libs.androidx.navigation.compose)
    implementation(libs.androidx.compose.runtime)
    implementation(libs.androidx.material3)
    testImplementation(libs.junit)
    androidTestImplementation(libs.androidx.junit)
    androidTestImplementation(libs.androidx.espresso.core)
    androidTestImplementation(platform(libs.androidx.compose.bom))
    androidTestImplementation(libs.androidx.compose.ui.test.junit4)
    debugImplementation(libs.androidx.compose.ui.tooling)
    debugImplementation(libs.androidx.compose.ui.test.manifest)
    implementation(libs.androidx.room.runtime)
    implementation(libs.androidx.room.ktx)
    kapt(libs.androidx.room.compiler)
    implementation(libs.androidx.material.icons.extended)
}
    