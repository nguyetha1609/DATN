plugins {
    alias(libs.plugins.android.application)
}

android {
    namespace = "org.o7planning.project_04"
    compileSdk = 35

    defaultConfig {
        applicationId = "org.o7planning.project_04"
        minSdk = 21
        targetSdk = 34
        versionCode = 1
        versionName = "1.0"

        testInstrumentationRunner = "androidx.test.runner.AndroidJUnitRunner"

        vectorDrawables.useSupportLibrary = true

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
        sourceCompatibility = JavaVersion.VERSION_1_8
        targetCompatibility = JavaVersion.VERSION_1_8
        isCoreLibraryDesugaringEnabled = true

    }
}

configurations.all {
    resolutionStrategy {
        eachDependency {
            if (requested.group == "com.kizitonwose.calendar") {
                useVersion("2.2.0")
            }
        }
    }
}

dependencies {
    coreLibraryDesugaring ("com.android.tools:desugar_jdk_libs:2.0.4")

    implementation ("com.kizitonwose.calendar:view:2.2.0")


    implementation("androidx.core:core:1.10.1")

    implementation ("androidx.core:core-ktx:1.10.1")
    implementation ("androidx.viewpager2:viewpager2:1.0.0")
    //implementation(libs.constraintlayout)
    implementation(libs.mpandroidchart)

    implementation(libs.appcompat)
    implementation(libs.material)
    implementation(libs.activity)
    implementation(libs.constraintlayout)
    testImplementation(libs.junit)
    androidTestImplementation(libs.ext.junit)
    androidTestImplementation(libs.espresso.core)
}