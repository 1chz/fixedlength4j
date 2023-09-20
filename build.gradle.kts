plugins {
    id("java")
    id("com.diffplug.spotless") version "6.13.0"
}

group = "io.zhc1"
version = "1.0-SNAPSHOT"

java {
    sourceCompatibility = JavaVersion.VERSION_1_8
}
repositories {
    mavenCentral()
    maven("https://plugins.gradle.org/m2/")
}

dependencies {
    testImplementation("org.assertj:assertj-core:3.23.1")
    testImplementation("org.junit.jupiter:junit-jupiter:5.9.0")
    testImplementation("org.junit.jupiter:junit-jupiter-api:5.9.0")
    testRuntimeOnly("org.junit.jupiter:junit-jupiter-engine:5.9.0")
}

tasks.test {
    useJUnitPlatform()
}

spotless {
    java {
        custom("noWildcardImports") {
            when {
                it.contains("*;\n") -> throw Error("No wildcard imports allowed. remove wildcard imports and run `./gradlew spotlessApply`")
                else -> it
            }
        }
        bumpThisNumberIfACustomStepChanges(1)

        palantirJavaFormat()
        indentWithSpaces()
        formatAnnotations()
        removeUnusedImports()
        trimTrailingWhitespace()
        importOrder("java", "jakarta", "org", "com", "net", "io", "lombok", "io.zhc1")
    }

    kotlinGradle {
        ktlint()
        indentWithSpaces()
        trimTrailingWhitespace()
    }
}
