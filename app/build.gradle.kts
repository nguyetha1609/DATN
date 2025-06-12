plugins {
    alias(libs.plugins.android.application)

}

android {
    namespace = "org.o7planning.project_04"
    compileSdk = 35

    defaultConfig {
        applicationId = "org.o7planning.project_04"
        minSdk = 26
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

// Thêm để xử lý lỗi META-INF
    packagingOptions {
        resources.excludes.add("META-INF/*")
        resources.excludes.add("META-INF/NOTICE.md")
        resources.excludes.add("META-INF/LICENSE.md")
        resources.excludes.add("META-INF/versions/9/previous-compilation-data.bin")
    }
}

configurations.all {
    resolutionStrategy {
        // Giải quyết xung đột phiên bản
        force("androidx.core:core-ktx:1.10.1")
        force("com.google.android.material:material:1.9.0")
        force("androidx.appcompat:appcompat:1.6.1")

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
    implementation ("com.github.zerobranch:SwipeLayout:1.3.1")


    //implementation(libs.constraintlayout)
    implementation(libs.mpandroidchart)

    
    implementation("androidx.recyclerview:recyclerview:1.4.0")
    implementation("androidx.cardview:cardview:1.0.0")
    implementation("androidx.constraintlayout:constraintlayout:2.2.1")
    implementation("com.diogobernardino:williamchart:3.10.1")
    implementation ("androidx.appcompat:appcompat:1.5.1")
   // implementation ("com.github.PhilJay:MPAndroidChart:v3.1.0")
    implementation ("androidx.coordinatorlayout:coordinatorlayout:1.2.0")
    implementation ("com.jakewharton.threetenabp:threetenabp:1.4.6")
    implementation ("com.jakewharton:butterknife:10.2.3")

   // implementation ("com.jakewharton.threetenabp:threetenabp:1.3.1")
    //Giư  lại bản mới nhất
   // implementation ("com.google.android.material:material:1.11.0")
    implementation ("com.github.bumptech.glide:glide:4.16.0")
    implementation ("de.hdodenhof:circleimageview:3.1.0")
    implementation("com.kizitonwose.calendar:view:2.4.0")

    implementation ("com.sun.mail:android-mail:1.6.7")
    implementation ("com.sun.mail:android-activation:1.6.7")
    implementation ("com.google.android.material:material:1.9.0")

    implementation(libs.activity)

    implementation(libs.appcompat)
    implementation(libs.material)
    implementation(libs.constraintlayout)

    testImplementation(libs.junit)
    androidTestImplementation(libs.ext.junit)
    androidTestImplementation(libs.espresso.core)

}