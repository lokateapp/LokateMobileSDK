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

subprojects {
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
            exclude("**/generated/**")
            exclude("**/MainViewController.kt") // exclude MainViewController.kt from ktlint since it's a generated file
            include("**/kotlin/**")
        }
    }

    detekt {
        buildUponDefaultConfig = true
        config.setFrom(file("config/detekt/detekt.yml"))
    }
}

tasks.register("detektAll") {
    allprojects {
        this@register.dependsOn(tasks.withType<Detekt>())
    }
}

dependencies {
    ktlintRuleset(libs.ktlint.rules.twitter)
}
