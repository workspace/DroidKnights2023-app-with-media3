pluginManagement {
    includeBuild("build-logic")
    repositories {
        gradlePluginPortal()
        google()
        mavenCentral()
    }
}

dependencyResolutionManagement {
    repositoriesMode.set(RepositoriesMode.FAIL_ON_PROJECT_REPOS)
    repositories {
        google()
        mavenCentral()
    }
}

rootProject.name = "DroidKnights2023"
include(
    ":app",
    ":app-automotive",
    ":app-tv",
    ":app-wear-os",

    ":core:designsystem",
    ":core:data",
    ":core:domain",
    ":core:navigation",
    ":core:model",
    ":core:playback",
    ":core:ui",
    ":core:testing",
    ":core:datastore",

    ":feature:main",
    ":feature:home",
    ":feature:session",
    ":feature:setting",
    ":feature:contributor",
    ":feature:bookmark",
    ":feature:player",
    ":feature:tv-main",
    ":feature:tv-session",
    ":feature:wear-main",
    ":feature:wear-player",
    ":feature:wear-session",

    ":widget"
)
