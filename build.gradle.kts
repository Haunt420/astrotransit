plugins {
    alias(libs.plugins.android.application) apply false
    // kotlin.android removed: built into AGP 9.0+, applying it is a fatal error
    alias(libs.plugins.kotlin.compose) apply false
}
