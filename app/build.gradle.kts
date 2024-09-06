
plugins {
    // Implicitly includes `java` and `distribution` plugins
    // Eases Java compilation, testing, and bundling
    application

    // Supports publishing build artifacts to an Apache Maven repository
    `maven-publish`

    // Supports signing built files and artifacts
    signing
}

dependencies {
    // Uses `TestNG` framework, also requires calling test.useTestNG() below
    testImplementation(libs.testng)

    // Used by `application`
    implementation(libs.guava)
}

group = "org.tomfoolery"
version = 1.0

application {
    mainClass = "${group}.App"
}

java {
    toolchain {
        languageVersion = JavaLanguageVersion.of(22)
    }

    // Packaging
    withJavadocJar()
    withSourcesJar()
}

repositories {
    mavenCentral()
}

publishing {
    publications {
        create<MavenPublication>("maven") {
            afterEvaluate {
                artifactId = tasks.jar.get().archiveBaseName.get()
            }

            from(components["java"])

            // Publishes resolved versions of dependencies
            versionMapping {
                usage("java-api") {
                    fromResolutionOf("runtimeClasspath")
                }
                usage("java-runtime") {
                    fromResolutionResult()
                }
            }

            pom {
                name = "tomfoolery"
                description = "Minimal English learning app"
                licenses {
                    name = "BSD 3-Clause License"
                    url = "https://opensource.org/license/BSD-3-clause"
                }
                developers {
                    developer {
                        id = "adnope"
                        name = "Nguyễn Anh Duy"
                        email = "23021502@vnu.edu.vn"
                    }
                    developer {
                        id = "NTDuck"
                        name = "Nguyễn Tư Đức"
                        email = "23021534@vnu.edu.vn"
                    }
                    developer {
                        id = "FearOfTheSea"
                        name = "Phạm Trung Hiếu"
                        email = "23021554@vnu.edu.vn"
                    }
                }
                scm {
                    url = "https://github.com/NTDuck/tomfoolery"
                }
            }
        }
    }
}

signing {
    sign(publishing.publications["maven"])
}

// Use a separate JVM process for the compiler and prevent compilation failures from failing the build
tasks.compileJava {
    options.isIncremental = true
    options.isFork = true
    options.isFailOnError = false
}

tasks {
    javadoc {
        options {
            // Suppress javadoc console warnings
            (this as CoreJavadocOptions).addBooleanOption("Xdoclint:none", true)
        }
    }
}

tasks.named<Test>("test") {
    // For unit testing
    useTestNG()
}
