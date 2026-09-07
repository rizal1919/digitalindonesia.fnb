// Fix for AGP duplicate environment variable conflict (ANDROID_PREFS_ROOT vs ANDROID_USER_HOME)
runCatching {
    val pe = Class.forName("java.lang.ProcessEnvironment")
    val envField = pe.getDeclaredField("theEnvironment").apply { isAccessible = true }
    val map = envField.get(null) as? MutableMap<*, *>
    map?.keys?.removeAll { key -> key.toString().equals("ANDROID_PREFS_ROOT", ignoreCase = true) }
}

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
    repositoriesMode.set(RepositoriesMode.FAIL_ON_PROJECT_REPOS)
    repositories {
        google()
        mavenCentral()
    }
}

rootProject.name = "fnb"
include(":app")
