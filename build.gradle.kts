import io.gitlab.arturbosch.detekt.Detekt

plugins {
    // this is necessary to avoid the plugins to be loaded multiple times
    // in each subproject's classloader
    alias(libs.plugins.androidApplication) apply false
    alias(libs.plugins.androidLibrary) apply false
    alias(libs.plugins.jetbrainsCompose) apply false
    alias(libs.plugins.kotlinAndroid) apply false
    alias(libs.plugins.kotlinMultiplatform) apply false
    alias(libs.plugins.gradleKtlint)
    alias(libs.plugins.detekt)
}

allprojects {
    apply {
        plugin(rootProject.libs.plugins.detekt.get().pluginId)
        plugin(rootProject.libs.plugins.gradleKtlint.get().pluginId)
    }

    ktlint {
        debug.set(false)
        verbose.set(true)
        android.set(false)
        outputToConsole.set(true)
        ignoreFailures.set(false)
        enableExperimentalRules.set(true)
        additionalEditorconfig.set(
            mapOf(
                "ktlint_function_naming_ignore_when_annotated_with" to "Composable",
            ),
        )
        filter {
            exclude("**/build/**")
            exclude("**/generated/**")
            exclude("**/MainViewController.kt") // exclude MainViewController.kt from ktlint since it's a generated file
            include("**/kotlin/**")
        }
    }
    detekt {
        allRules = true
    }
}

tasks.withType<Detekt> {
    setSource(files(project.projectDir))
    config = files("config/detekt/detekt.yml")
    exclude("**/build/**")
    exclude("**/generated/**")
    exclude("**/MainViewController.kt")
    include("**/kotlin/**")
}

dependencies {
    ktlintRuleset(libs.ktlint.rules.twitter)
    detektPlugins(libs.detekt.rules.twitter)
}
