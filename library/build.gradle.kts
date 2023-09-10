plugins {
    id("com.android.library")
    id("org.jetbrains.kotlin.android")
    `maven-publish`
    id("net.nemerosa.versioning")
}

android {
    namespace = "fe.linksheet.interconnect"
    compileSdk = 34

    defaultConfig {
        minSdk = 21
        testInstrumentationRunner = "androidx.test.runner.AndroidJUnitRunner"
    }

    compileOptions {
        sourceCompatibility = JavaVersion.VERSION_17
        targetCompatibility = JavaVersion.VERSION_17
    }

    buildFeatures {
        aidl = true
    }
}

publishing {
    publications {
        create<MavenPublication>("release") {
            groupId = "fe.linksheet.interconnect"
            version = versioning.info.tag ?: versioning.info.full

            afterEvaluate {
                from(components["release"])
            }
        }
    }
}

dependencies {
    implementation(AndroidX.core.ktx)

    testImplementation(Testing.junit4)
    androidTestImplementation(AndroidX.test.ext.junit)
    androidTestImplementation(AndroidX.test.espresso.core)
}