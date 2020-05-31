rootProject.name = "Kecs"

pluginManagement {
    repositories {
        gradlePluginPortal()
    }
    resolutionStrategy {
        eachPlugin {
            if (requested.id.id.startsWith("org.jetbrains.kotlin")) {
                gradle.rootProject.property("kotlinVersion")?.let { useVersion(it as String) }
            }
        }
    }
}
