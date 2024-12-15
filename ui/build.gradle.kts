import org.jetbrains.kotlin.gradle.ExperimentalKotlinGradlePluginApi
import org.jetbrains.kotlin.gradle.dsl.JvmTarget
import org.jetbrains.kotlin.gradle.plugin.mpp.KotlinNativeTarget
import org.jetbrains.kotlin.gradle.plugin.mpp.NativeBuildType
import org.jetbrains.kotlin.gradle.tasks.KotlinCompilationTask

plugins {
    alias(libs.plugins.kotlinMultiplatform)
    alias(libs.plugins.kotlinCocoapods)
    alias(libs.plugins.androidLibrary)
    alias(libs.plugins.serialization)
    alias(libs.plugins.ksp)
    alias(libs.plugins.jetbrainsCompose)
    alias(libs.plugins.compose.compiler)
}

kotlin {
    androidTarget {
        compilations.all {
            compileTaskProvider.configure {
                compilerOptions {
                    jvmTarget.set(JvmTarget.JVM_11)
                }
            }
        }
    }
    iosX64 {
        binaries.all {
            linkerOpts("-ObjC")
        }
    }
    iosArm64 {
        binaries.all {
            linkerOpts("-ObjC")
        }
    }
    iosSimulatorArm64 {
        binaries.all {
            linkerOpts("-ObjC")
        }
    }

    cocoapods {
        summary = "Some description for the Shared Module"
        homepage = "Link to the Shared Module homepage"
        version = "1.0"
        ios.deploymentTarget = "16.0"
        podfile = project.file("../iosApp/Podfile")

        framework {
            baseName = "ui"
            isStatic = true
        }

        pod("GoogleMaps") {
            version = "9.2.0"
            extraOpts = listOf("-compiler-option", "-fmodules")
        }
    }

    targets.withType<KotlinNativeTarget> {
        binaries.all {
            linkerOpts("-ObjC")
        }
    }
    
    sourceSets {
        androidMain.dependencies {
            implementation(libs.koin.android)
            implementation(libs.maps.compose)
            implementation(libs.androidx.activity.compose)
            implementation(libs.play.services.location)
        }
        commonMain.dependencies {
            api(project(":shared"))

            // Compose
            implementation(compose.runtime)
            implementation(compose.foundation)
            implementation(compose.material3)
            implementation(compose.material)
            implementation(compose.ui)
            implementation(compose.components.resources)
            implementation(libs.compose.adaptive)
            implementation(libs.compose.adaptive.navigation)
            implementation(libs.markdown)

            // Koin
            implementation(libs.koin.core)
            implementation(libs.koin.compose)
            implementation(libs.koin.compose.viewmodel)
            implementation(libs.koin.annotations)

            // Emily Components
            implementation(libs.emily.serializable)
            implementation(libs.emily.units)
            implementation(libs.emily.errorwidget)
            implementation(libs.emily.requeststate)

            // Coil
            implementation(libs.coil.compose)
            implementation(libs.coil.network)

            // Location
            implementation(libs.moko.permissions)
            implementation(libs.moko.permissions)

            // Voyager
            implementation(libs.voyager.navigator)
            implementation(libs.voyager.navigator.bottomsheet)
        }
        iosMain.dependencies {}
        iosMain {
            kotlin.srcDir("build/generated/ksp/metadata")
            kotlin.srcDir("src/iosMain/resources")
        }
    }
}

dependencies {
    add("kspCommonMainMetadata", libs.koin.ksp.compiler)
    add("kspAndroid", libs.koin.ksp.compiler)
    add("kspIosX64", libs.koin.ksp.compiler)
    add("kspIosArm64", libs.koin.ksp.compiler)
    add("kspIosSimulatorArm64", libs.koin.ksp.compiler)
}

project.tasks.withType(KotlinCompilationTask::class.java).configureEach {
    if(name != "kspCommonMainKotlinMetadata") {
        dependsOn("kspCommonMainKotlinMetadata")
    }
}

ksp {
    arg("KOIN_USE_COMPOSE_VIEWMODEL","true")
}

android {
    namespace = "cl.emilym.sinatra.ui"
    compileSdk = 35
    defaultConfig {
        minSdk = 24
    }
    compileOptions {
        sourceCompatibility = JavaVersion.VERSION_11
        targetCompatibility = JavaVersion.VERSION_11
    }
}