import com.codingfeline.buildkonfig.compiler.FieldSpec.Type.STRING
import org.jetbrains.kotlin.gradle.dsl.JvmTarget

plugins {
    alias(libs.plugins.kotlinMultiplatform)
    alias(libs.plugins.kotlinCocoapods)
    alias(libs.plugins.androidLibrary)
    alias(libs.plugins.serialization)
    alias(libs.plugins.ksp)
    alias(libs.plugins.ktorfit)
    alias(libs.plugins.room)
    id("com.codingfeline.buildkonfig")
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
    iosX64()
    iosArm64()
    iosSimulatorArm64()

    targets.configureEach {
        compilations.configureEach {
            compileTaskProvider.configure {
                compilerOptions {
                    freeCompilerArgs.add("-Xexpect-actual-classes")
                }
            }
        }
    }

    cocoapods {
        summary = "Some description for the Shared Module"
        homepage = "Link to the Shared Module homepage"
        version = "1.0"
        ios.deploymentTarget = "16.0"
        podfile = project.file("../iosApp/Podfile")
        framework {
            baseName = "shared"
            isStatic = true
        }
    }
    
    sourceSets {
        androidMain.dependencies {
            implementation(libs.ktor.client.okhttp)
            implementation(libs.koin.android)
        }
        commonMain.dependencies {

            // Koin
            implementation(libs.koin.core)
            implementation(libs.koin.annotations)

            // Network
            implementation(libs.ktor.ktorfit.lib)
            implementation(libs.ktor.negotiation)
            implementation(libs.ktor.serialization.json)

            // Exposed for convenience
            api(libs.aakira.napier)
            api(libs.kotlin.datetime)

            // Emily Components
            implementation(libs.emily.serializable)

            // Coil
            implementation(libs.coil.network)

            // Ktor
            implementation(libs.ktor.ktorfit.lib)
            implementation(libs.ktor.negotiation)
            implementation(libs.ktor.serialization.json)

            // Serialization
            implementation(libs.pbandk)
            implementation(libs.kotlinx.serialization.json)

            // Room
            implementation(libs.room.runtime)
            implementation(libs.room.driver)
            implementation(libs.datastore.preferences)
            implementation(libs.datastore)
        }
        iosMain.dependencies {
            implementation(libs.ktor.client.darwin)
        }
        commonTest.dependencies {
            implementation(libs.kotlin.test)
        }
        androidUnitTest.dependencies {
            implementation(libs.kotlin.reflect)
            implementation(libs.kotlin.test)
            implementation(libs.kotlin.test.coroutines)
            implementation(libs.mockk)
        }
    }
}

dependencies {
    add("kspCommonMainMetadata", libs.koin.ksp.compiler)
    add("kspAndroid", libs.koin.ksp.compiler)
    add("kspIosX64", libs.koin.ksp.compiler)
    add("kspIosArm64", libs.koin.ksp.compiler)
    add("kspIosSimulatorArm64", libs.koin.ksp.compiler)

    add("kspAndroid", libs.room.compiler)
    add("kspIosX64", libs.room.compiler)
    add("kspIosArm64", libs.room.compiler)
    add("kspIosSimulatorArm64", libs.room.compiler)
}

room {
    schemaDirectory("$projectDir/schemas")
}

android {
    namespace = "cl.emilym.sinatra"
    compileSdk = 35
    defaultConfig {
        minSdk = 23
    }
    compileOptions {
        sourceCompatibility = JavaVersion.VERSION_11
        targetCompatibility = JavaVersion.VERSION_11
    }
}

buildkonfig {
    packageName = "cl.emilym.sinatra"

    defaultConfigs {
        defaultConfigs {
            val apiUrl: String by project
            buildConfigField(STRING, "apiUrl", apiUrl, const = true)
        }
    }
}