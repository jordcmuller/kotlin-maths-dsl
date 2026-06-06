plugins {
    kotlin("jvm")
    id("io.kotest") version "6.0.7"
    id("com.vanniktech.maven.publish") version "0.34.0"
}

group = "io.github.jordcmuller.kotlin-maths-dsl"
version = "0.0.4"
val kotestVersion = "6.0.7"

repositories {
    mavenCentral()
}

dependencies {
    testImplementation("io.kotest:kotest-framework-engine:$kotestVersion")
    testImplementation("io.kotest:kotest-assertions-core:$kotestVersion")
    testImplementation("io.kotest:kotest-runner-junit5:$kotestVersion")
}

tasks.test {
    useJUnitPlatform()
}

kotlin {
    jvmToolchain(21)
    compilerOptions {
        freeCompilerArgs.add("-Xcontext-parameters")
        freeCompilerArgs.add("-Xwhen-guards")
        freeCompilerArgs.add("-Xcontext-sensitive-resolution")
        freeCompilerArgs.add("-Xname-based-destructuring=complete")
    }
}

mavenPublishing {
    publishToMavenCentral(automaticRelease = true)

    signAllPublications()

    coordinates(group.toString(), "kotlin-maths-dsl", version.toString())

    pom {
        name = "Kotlin Maths DSL library"
        description = "A kotlin DSL for doing maths."
        inceptionYear = "2024"
        url = "https://github.com/jordcmuller/kotlin-maths-dsl"
        licenses {
            license {
                name = "The MIT License"
                url = "https://choosealicense.com/licenses/mit/"
                distribution = "https://choosealicense.com/licenses/mit/"
            }
        }
        developers {
            developer {
                id = "jordcmuller"
                name = "Jordan Muller"
                email = "jordan.c.muller13@gmail.com"
                url = "https://github.com/jordcmuller/"
                organization = "jordculler"
                organizationUrl = "https://github.com/jordcmuller/"
            }
        }
        scm {
            url = "https://github.com/jordcmuller/kotlin-maths-dsl"
            connection = "scm:git:git://github.com/jordcmuller/kotlin-maths-dsl.git"
            developerConnection = "scm:git:ssh://git@github.com/jordcmuller/kotlin-maths-dsl.git"
        }
    }
}
