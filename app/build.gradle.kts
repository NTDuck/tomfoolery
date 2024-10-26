plugins {
    // Implicitly includes `java` and `distribution` plugins
    // Eases Java compilation, testing, and bundling
    application

    // Supports publishing build artifacts to an Apache Maven repository
    `maven-publish`

    // Supports signing built files and artifacts
    signing

    // Primary GUI
    id("org.openjfx.javafxplugin") version "0.1.0"
}

javafx {
    version = "21"
    modules("javafx.controls", "javafx.fxml")
}

dependencies {
    // Uses `Lombok` for reduced boilerplate
    compileOnly("org.projectlombok:lombok:1.18.34")
    annotationProcessor("org.projectlombok:lombok:1.18.34")

    testCompileOnly("org.projectlombok:lombok:1.18.34")
    testAnnotationProcessor("org.projectlombok:lombok:1.18.34")

    // Uses `Bcrypt` for password hashing (no need to reinvent the wheel)
    implementation("at.favre.lib:bcrypt:0.10.2")

    // Uses `JJWT` for generation and verification of authentication tokens
    implementation("io.jsonwebtoken:jjwt-api:0.12.6")
    runtimeOnly("io.jsonwebtoken:jjwt-impl:0.12.6")
    runtimeOnly("io.jsonwebtoken:jjwt-jackson:0.12.6")
    // runtimeOnly("io.jsonwebtoken:jjwt-gson:0.12.6")

    // Uses `TestNG` framework, also requires calling test.useTestNG() below
    testImplementation(libs.testng)

    // Used by `application`
    implementation(libs.guava)
}

group = "org.tomfoolery"
version = 1.0

application {
    // Terminal version as default
    mainClass = "${project.group}.configurations.monolith.terminal.Application"

    // Prevents non-blocking `java.util.Scanner`
    tasks.getByName("run", JavaExec::class) {
        standardInput = System.`in`
    }
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

tasks.register<JavaExec>("runTerminal") {
    mainClass = "${project.group}.configurations.monolith.terminal.Application"
    classpath = sourceSets["main"].runtimeClasspath

    // Prevents non-blocking `java.util.Scanner`
    tasks.getByName("runTerminal", JavaExec::class) {
        standardInput = System.`in`
    }
}

tasks.register<JavaExec>("runJavaFX") {
    mainClass = "${project.group}.configurations.monolith.gui.MainApplication"
    classpath = sourceSets["main"].runtimeClasspath
}

tasks.named<Test>("test") {
    // For unit testing
    useTestNG()

    testLogging {
        // Enables console output
        showStandardStreams = true
    }
    
    // Prevents failing tests from failing builds
    ignoreFailures = true
}