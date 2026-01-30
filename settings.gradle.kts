pluginManagement {
    repositories {
        google {
            content {
                includeGroupByRegex("com\\.android.*")
                includeGroupByRegex("com\\.google.*")
                includeGroupByRegex("androidx.*")
            }
        }
        mavenCentral()
        gradlePluginPortal()
    }
}

dependencyResolutionManagement {
    repositories {
        google()
        mavenCentral()
    }
}

rootProject.name = "SkylaAdvancePOS"

include(":app")

// Core modules
include(":core:core-common")
include(":core:core-model")
include(":core:core-network")
include(":core:core-ui")

// Feature modules
include(":feature:feature-auth")
include(":feature:feature-dashboard")
include(":feature:feature-products")
include(":feature:feature-categories")
include(":feature:feature-customers")
include(":feature:feature-sales")
include(":feature:feature-payments")
include(":feature:feature-inventory")
include(":feature:feature-users")
include(":feature:feature-reports")
