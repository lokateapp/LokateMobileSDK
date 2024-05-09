import com.android.build.gradle.internal.cxx.configure.gradleLocalProperties
import com.codingfeline.buildkonfig.compiler.FieldSpec.Type.STRING

plugins {
    alias(libs.plugins.kotlinMultiplatform)
    alias(libs.plugins.androidApplication)
    alias(libs.plugins.jetbrainsCompose)
    alias(libs.plugins.serialization)
    id("com.codingfeline.buildkonfig") version "0.15.1"
}

kotlin {
    androidTarget {
        compilations.all {
            kotlinOptions {
                jvmTarget = "11"
            }
        }
    }

    listOf(
        iosX64(),
        iosArm64(),
        iosSimulatorArm64(),
    ).forEach { iosTarget ->
        iosTarget.binaries.framework {
            baseName = "ComposeApp"
            isStatic = true
        }
    }

    sourceSets {
        commonMain.dependencies {
            implementation(libs.ktor.client.core)
            implementation(libs.ktor.client.content.negotiation)
            implementation(libs.ktor.client.serialization)
            implementation(libs.lighthouse.log)
            implementation(compose.runtime)
            implementation(compose.foundation)
            implementation(compose.material)
            implementation(compose.ui)
            implementation(compose.components.resources)
            implementation(compose.components.uiToolingPreview)
            implementation(compose.materialIconsExtended)
            implementation(libs.compose.multiplatform.media.player)
            implementation(libs.permissions.compose)
            implementation(libs.koin.core)
            implementation(libs.koin.compose)
            implementation(libs.precompose.core)
            implementation(libs.precompose.vm)
            implementation(libs.precompose.koin)
            implementation(projects.lokateSDK)
        }
        androidMain.dependencies {
            implementation(libs.ktor.client.android)
            implementation(libs.compose.ui.tooling.preview)
            implementation(libs.androidx.activity.compose)
            implementation(libs.androidx.appcompat)
        }
        iosMain.dependencies {
            implementation(libs.ktor.client.ios)
        }
    }
}

android {
    namespace = "com.lokate.demo"
    compileSdk = libs.versions.android.compileSdk.get().toInt()

    sourceSets["main"].manifest.srcFile("src/androidMain/AndroidManifest.xml")
    sourceSets["main"].res.srcDirs("src/androidMain/res")
    sourceSets["main"].resources.srcDirs("src/commonMain/resources")
    sourceSets["main"].assets.srcDirs("src/commonMain/composeResources")

    defaultConfig {
        applicationId = "com.lokate.demo"
        minSdk = libs.versions.android.minSdk.get().toInt()
        targetSdk = libs.versions.android.targetSdk.get().toInt()
        versionCode = 1
        versionName = "1.0"
    }
    packaging {
        resources {
            excludes += "/META-INF/{AL2.0,LGPL2.1}"
        }
    }
    buildTypes {
        getByName("release") {
            isMinifyEnabled = false
        }
    }
    compileOptions {
        sourceCompatibility = JavaVersion.VERSION_11
        targetCompatibility = JavaVersion.VERSION_11
    }
    dependencies {
        debugImplementation(libs.compose.ui.tooling)
    }
}

task("testClasses").doLast {
    println("This is a dummy testClasses task")
}

buildkonfig {
    packageName = "com.lokate.demo"

    // default config is required
    defaultConfigs {
        val appID: String = gradleLocalProperties(rootDir).getProperty("ESTIMOTE_CLOUD_APP_ID")
        require(appID.isNotEmpty()) {
            "Place your Estimote app id to local.properties as `ESTIMOTE_CLOUD_APP_ID`"
        }
        buildConfigField(STRING, "ESTIMOTE_CLOUD_APP_ID", appID)

        val appToken: String = gradleLocalProperties(rootDir).getProperty("ESTIMOTE_CLOUD_APP_TOKEN")
        require(appToken.isNotEmpty()) {
            "Place your Estimote app token to local.properties as `ESTIMOTE_CLOUD_APP_TOKEN`"
        }
        buildConfigField(STRING, "ESTIMOTE_CLOUD_APP_TOKEN", appToken)

        val mobileApiIpAddress: String = gradleLocalProperties(rootDir).getProperty("MOBILE_API_IP_ADDRESS")
        require(mobileApiIpAddress.isNotEmpty()) {
            "Place your Mobile API IP address to local.properties as `MOBILE_API_IP_ADDRESS`"
        }
        buildConfigField(STRING, "MOBILE_API_IP_ADDRESS", mobileApiIpAddress)
    }
}
