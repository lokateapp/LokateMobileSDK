plugins {
    alias(libs.plugins.kotlinMultiplatform)
    alias(libs.plugins.androidLibrary)
    alias(libs.plugins.serialization)
    alias(libs.plugins.sqldelight)
}

kotlin {
    androidTarget {
        compilations.all {
            kotlinOptions {
                jvmTarget = "1.8"
            }
        }
    }

    listOf(
        iosX64(),
        iosArm64(),
        iosSimulatorArm64(),
    ).forEach {
        it.binaries.framework {
            baseName = "kmmsdk"
            isStatic = true
        }
    }

    sourceSets {
        commonMain.dependencies {
            // coroutines
            implementation(libs.kotlinx.coroutines.core)
            // ktor
            implementation(libs.ktor.client.core)
            implementation(libs.ktor.client.content.negotiation)
            implementation(libs.ktor.client.serialization)
            // ktor logger
            implementation(libs.slf4j.logger)
            // shared pref
            implementation(libs.russhwolf.settings)
            implementation(libs.russhwolf.settings.no.arg)
            // lighthouse logging
            implementation(libs.lighthouse.log)
        }
        commonTest.dependencies {
            implementation(libs.kotlin.test)
        }
        androidMain.dependencies {
            implementation(libs.ktor.client.android)
            implementation(libs.sqldelight.driver.android)
            implementation(libs.altbeacon.android)
            implementation(libs.androidx.startup)
            implementation(libs.proximity.sdk)
        }
        iosMain.dependencies {
            implementation(libs.ktor.client.ios)
            implementation(libs.sqldelight.driver.native)
        }
    }
}

android {
    namespace = "com.lokate.kmmsdk"
    compileSdk = libs.versions.android.compileSdk.get().toInt()

    defaultConfig {
        minSdk = libs.versions.android.minSdk.get().toInt()
    }
}

dependencies {
    implementation(libs.androidx.core.ktx)
}

sqldelight {
    databases {
        create("Database") {
            packageName.set("com.lokate.kmmsdk")
        }
    }
    linkSqlite = true
}

task("testClasses").doLast {
    println("This is a dummy testClasses task")
}
