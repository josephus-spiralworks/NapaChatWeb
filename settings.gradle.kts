rootProject.name = "NapaChat"
enableFeaturePreview("TYPESAFE_PROJECT_ACCESSORS")

pluginManagement {
    repositories {
        google {
            mavenContent {
                includeGroupAndSubgroups("androidx")
                includeGroupAndSubgroups("com.android")
                includeGroupAndSubgroups("com.google")
            }
        }
        mavenCentral()
        gradlePluginPortal()
    }
}

dependencyResolutionManagement {
    repositories {
        google {
            mavenContent {
                includeGroupAndSubgroups("androidx")
                includeGroupAndSubgroups("com.android")
                includeGroupAndSubgroups("com.google")
            }
        }
        mavenCentral()
//        maven {
//            url = uri("https://maven.pkg.github.com/GitLiveApp/firebase-kotlin-sdk")
//            credentials {
//                username = providers.gradleProperty("gpr.user").get()
//                password = providers.gradleProperty("gpr.token").get()
//            }
//        }
    }
}

include(":composeApp")