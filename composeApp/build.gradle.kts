import com.android.build.gradle.internal.cxx.configure.gradleLocalProperties
import com.codingfeline.buildkonfig.compiler.FieldSpec.Type.STRING

plugins {
    alias(libs.plugins.kotlinMultiplatform)
    alias(libs.plugins.androidApplication)
    alias(libs.plugins.jetbrainsCompose)
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
            implementation(compose.runtime)
            implementation(compose.foundation)
            implementation(compose.material)
            implementation(compose.ui)
            implementation(compose.components.resources)
            implementation(compose.components.uiToolingPreview)
            implementation(libs.permissions.compose)
            implementation(project(mapOf("path" to ":kmmsdk")))
        }
        androidMain.dependencies {
            implementation(libs.compose.ui.tooling.preview)
            implementation(libs.androidx.activity.compose)
            implementation(libs.androidx.appcompat)
        }
    }
}

android {
    namespace = "com.lokate.demo"
    compileSdk = libs.versions.android.compileSdk.get().toInt()

    sourceSets["main"].manifest.srcFile("src/androidMain/AndroidManifest.xml")
    sourceSets["main"].res.srcDirs("src/androidMain/res")
    sourceSets["main"].resources.srcDirs("src/commonMain/resources")

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
            "Register your api key from developer and place it in local.properties as `ESTIMOTE_CLOUD_APP_ID`"
        }
        buildConfigField(STRING, "ESTIMOTE_CLOUD_APP_ID", appID)

        val appToken: String = gradleLocalProperties(rootDir).getProperty("ESTIMOTE_CLOUD_APP_TOKEN")
        require(appToken.isNotEmpty()) {
            "Register your api key from developer and place it in local.properties as `ESTIMOTE_CLOUD_APP_TOKEN`"
        }
        buildConfigField(STRING, "ESTIMOTE_CLOUD_APP_TOKEN", appToken)
    }
}
