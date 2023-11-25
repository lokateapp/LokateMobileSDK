plugins {
    //trick: for the same plugin versions in all sub-modules
    alias(libs.plugins.androidApplication).apply(false)
    alias(libs.plugins.androidLibrary).apply(false)
    alias(libs.plugins.kotlinAndroid).apply(false)
    alias(libs.plugins.kotlinMultiplatform).apply(false)
    //id("org.jetbrains.kotlin.android") version "2.0.0-Beta1" apply false
    //alias(libs.plugins.kotlinAndroid).apply(false)
    //id("org.jetbrains.kotlin.android") version "1.9.20" apply false
    //id("org.jetbrains.kotlin.android") version "1.9.20" apply false
}
