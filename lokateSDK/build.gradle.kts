import com.android.build.gradle.internal.cxx.configure.gradleLocalProperties
import com.codingfeline.buildkonfig.compiler.FieldSpec.Type.STRING

plugins {
    alias(libs.plugins.kotlinMultiplatform)
    alias(libs.plugins.androidLibrary)
    alias(libs.plugins.serialization)
    alias(libs.plugins.sqldelight)
    alias(libs.plugins.buildkonfig)
    alias(libs.plugins.kotlinCocoapods)
}

kotlin {
    androidTarget {
        compilations.all {
            kotlinOptions {
                jvmTarget = "1.8"
            }
        }
    }

    cocoapods {
        summary = "KMM SDK"
        homepage = "lokate.tech"
        version = "1.0.0"
        pod("EstimoteProximitySDK") {
            version = "1.8.0"
            moduleName = "EstimoteProximitySDK"
            extraOpts += listOf("-compiler-option", "-fmodules")
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
            // geolocation
            implementation(libs.play.services.location) // are we sure that it should be in the common?
            // koin
            implementation(libs.koin.core)
            implementation(libs.stately.common)
            implementation(libs.stately.concurrency)
        }
        commonTest.dependencies {
            implementation(libs.kotlin.test)
        }
        androidMain.dependencies {
            implementation(libs.androidx.startup)
            implementation(libs.androidx.core.ktx)
            implementation(libs.ktor.client.android)
            implementation(libs.sqldelight.driver.android)
            implementation(libs.altbeacon.android)
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

sqldelight {
    databases {
        create("Database") {
            packageName.set("com.lokate.kmmsdk")
        }
    }
    linkSqlite = true
}

buildkonfig {
    packageName = "com.lokate.kmmsdk"

    // default config is required
    defaultConfigs {
        val mobileApiIpAddress: String = gradleLocalProperties(rootDir).getProperty("MOBILE_API_IP_ADDRESS")
        require(mobileApiIpAddress.isNotEmpty()) {
            "Place your Mobile API IP address to local.properties as `MOBILE_API_IP_ADDRESS`"
        }
        buildConfigField(STRING, "MOBILE_API_IP_ADDRESS", mobileApiIpAddress)
    }
}

task("testClasses").doLast {
    println("This is a dummy testClasses task")
}
