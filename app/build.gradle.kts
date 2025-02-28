plugins {
    id("com.android.application")
    id("com.google.gms.google-services")
    id("kotlin-android")
    id("kotlin-kapt")
    id("dagger.hilt.android.plugin")
    id("com.mikepenz.aboutlibraries.plugin") version Library.Version.aboutLibraries
    id("com.squareup.sqldelight") version Library.Version.sqlDelight
}

val SUPPORTED_ABIS = setOf("armeabi-v7a", "arm64-v8a", "x86", "x86_64")

@Suppress("UnstableApiUsage")
android {
    compileSdk = Android.compileSdk

    defaultConfig {
        applicationId = Android.applicationId
        minSdk = Android.minSdk
        targetSdk = Android.targetSdk
        versionCode = Android.versionCode
        versionName = Android.versionName

        ndk {
            abiFilters += SUPPORTED_ABIS
        }

        testInstrumentationRunner = "androidx.test.runner.AndroidJUnitRunner"
    }

    splits {
        abi {
            isEnable = true
            reset()
            include(*SUPPORTED_ABIS.toTypedArray())
            isUniversalApk = true
        }
    }

    buildTypes {
        named("release") {
            isMinifyEnabled = false
            proguardFiles(getDefaultProguardFile("proguard-android-optimize.txt"), "proguard-rules.pro")
        }
    }

    buildFeatures {
        dataBinding = true
        viewBinding = true
    }

    compileOptions {
        sourceCompatibility = JavaVersion.VERSION_11
        targetCompatibility = JavaVersion.VERSION_11
    }

    android {
        sourceSets {
            getByName("main") {
                java.srcDir("src/main/kotlin")
            }
        }
    }

    kotlinOptions {
        jvmTarget = "11"
        freeCompilerArgs = freeCompilerArgs + listOf(
            "-opt-in=kotlinx.coroutines.ExperimentalCoroutinesApi",
        )
    }
}

dependencies {
    // --- Google libraries (prepare for pain when using them!) ---
    // AndroidX libraries
    implementation(Library.App.AndroidX.core)
    implementation(Library.App.AndroidX.splashScreen)
    implementation(Library.App.AndroidX.appCompat)
    implementation(Library.App.AndroidX.constraintLayout)
    implementation(Library.App.AndroidX.preference)
    implementation(Library.App.AndroidX.liveData)
    implementation(Library.App.AndroidX.navigationFragment)
    implementation(Library.App.AndroidX.navigationUI)
    implementation(Library.App.AndroidX.camera)
    implementation(Library.App.AndroidX.cameraLifecycle)
    implementation(Library.App.AndroidX.cameraView)
    implementation(Library.App.AndroidX.paging)

    // com.google.* libraries
    implementation(Library.App.material)
    implementation(Library.App.hilt)
    implementation("androidx.appcompat:appcompat:1.6.1")
    implementation("com.google.android.material:material:1.5.0")
    implementation("androidx.constraintlayout:constraintlayout:2.1.4")
    implementation("org.tensorflow:tensorflow-lite-metadata:0.3.1") // Use a compatible version
    implementation("org.tensorflow:tensorflow-lite:2.14.0")



    kapt(Library.App.hiltCompiler)
    implementation(Library.App.googleAuth)
    implementation(platform(Library.App.Firebase.bom))
    implementation(Library.App.Firebase.auth)
    implementation(Library.App.Firebase.firestore)
    implementation(Library.App.Firebase.analytics)
    implementation(Library.App.Firebase.machineLearning)
    implementation(Library.App.Firebase.database)


    // --- third parties ---
    implementation(Library.App.tensorflowLite)
    implementation(Library.App.tensorflowLiteSupport)
    implementation(Library.App.viewBindingQOL)
    implementation(Library.App.glide)
    kapt(Library.App.glideCompiler)
    implementation(Library.App.flowPreferences)
    implementation(Library.App.aboutLibrariesCore)
    implementation(Library.App.aboutLibrariesUI)
    implementation(Library.App.sqlDelightAndroid)
    implementation(Library.App.sqlDelightPaging)

    // --- Testing libraries ---
    testImplementation(Library.App.Testing.junit)
    androidTestImplementation(Library.App.Testing.junitAndroid)
    androidTestImplementation(Library.App.Testing.espresso)

    // --- desugaring ---
    coreLibraryDesugaring(Library.App.desugar)


    implementation("de.hdodenhof:circleimageview:3.1.0")
    implementation("com.github.PhilJay:MPAndroidChart:v3.0.3")

}
android {
    compileSdk = 33
    buildToolsVersion = "32.0.0"
    buildFeatures {
        mlModelBinding = true
    }
}
