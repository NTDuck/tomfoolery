import com.adarshr.gradle.testlogger.theme.ThemeType

plugins {
    // Implicitly includes `java` and `distribution` plugins
    // Eases Java compilation, testing, and bundling
    application

    // Supports publishing build artifacts to an Apache Maven repository
    `maven-publish`

    // Supports signing built files and artifacts
    signing

    // JavaFX plugins and extras
    id("org.openjfx.javafxplugin") version "0.1.0"
    // id ("org.javamodularity.moduleplugin") version "1.8.12"

    // Test Logger plugin
    id("com.adarshr.test-logger") version "4.0.0"
}

repositories {
    mavenCentral()

    maven("https://jitpack.io")
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

    // Os-specific secure storage for persistence of authentication tokens
    implementation("com.microsoft:credential-secure-storage:1.0.0")

    // Prevents "Failed to load class org.slf4j.impl.StaticLoggerBinder"
    implementation("org.slf4j:slf4j-api:2.0.9")
    implementation("org.slf4j:slf4j-nop:2.0.9")

    // Uses `Zxing` for QR code generation
    implementation("com.google.zxing:core:3.5.1")
    implementation("com.google.zxing:javase:3.5.1")

    // Uses Apache's `URIBuilder` for clean and lightweight URI construction
    implementation("org.apache.httpcomponents.client5:httpclient5:5.1")

    // Contains necessary implementation of Directed Acyclic Subsequence Graph
    // required for efficient in-memory indexing of documents
    implementation("com.github.Qualtagh:DAWG:e98133f757")

    // Contains necessary implementation for Trie HashMap
    // required for efficient in-memory autocompletion
    // implementation("com.github.doried-a-a:java-trie"

    // Uses `OkHttp` as HTTP Client
    implementation("com.squareup.okhttp3:okhttp:4.11.0")
    implementation("com.squareup.okhttp3:logging-interceptor:4.11.0")

    // Uses `Jsoniter` for performant JSON parsing
    implementation("com.jsoniter:jsoniter:0.9.23")

    //
    implementation(platform("io.github.jan-tennert.supabase:bom:3.0.2"))
    implementation("io.github.jan-tennert.supabase:postgrest-kt")
    implementation("io.github.jan-tennert.supabase:auth-kt")
    implementation("io.github.jan-tennert.supabase:realtime-kt")

    //
    implementation("io.ktor:ktor-client-apache5:3.0.1")

    implementation ("org.postgresql:postgresql:42.6.0")

    // For Figlet fonts
    implementation("com.github.lalyos:jfiglet:0.0.9")

    // SKT T1
    implementation("com.github.javafaker:javafaker:1.0.2")

    // Uses `TestNG` framework, also requires calling test.useTestNG() below
    testImplementation(libs.testng)

    // Used by `application`
    implementation(libs.guava)
}

group = "org.tomfoolery"
version = 1.0

application {
    mainClass = "${project.group}.configurations.placeholder.Placeholder"
}

java {
    toolchain {
        languageVersion = JavaLanguageVersion.of(22)
    }

    // Packaging
    withJavadocJar()
    withSourcesJar()

    modularity.inferModulePath = true
}

javafx {
    version = "22"
    modules("javafx.base", "javafx.graphics", "javafx.controls", "javafx.fxml")
}

testlogger {
    theme = ThemeType.MOCHA
    showExceptions = true
    showStackTraces = true
    showFullStackTraces = false
    showCauses = true
    slowThreshold = 2000
    showSummary = true
    showSimpleNames = false
    showPassed = true
    showSkipped = true
    showFailed = true
    showOnlySlow = false
    showStandardStreams = false
    showPassedStandardStreams = true
    showSkippedStandardStreams = true
    showFailedStandardStreams = true
    logLevel = LogLevel.LIFECYCLE
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

    options.encoding = "UTF-8"
}

tasks {
    javadoc {
        options {
            // Suppress javadoc console warnings
            (this as CoreJavadocOptions).addBooleanOption("Xdoclint:none", true)
        }
    }
}

tasks.named("run") {
    dependsOn("runJavaFX")
}

tasks.register<JavaExec>("runConsole") {
    mainClass = "${project.group}.configurations.monolith.console.Application"
    classpath = sourceSets["main"].runtimeClasspath

    // Prevents non-blocking `java.util.Scanner`
    standardInput = System.`in`
}

tasks.register<JavaExec>("runJavaFX") {
    mainClass = "${project.group}.configurations.monolith.gui.MainApplication"
    classpath = sourceSets["main"].runtimeClasspath

    // Ensure JavaFX runtime components are registered
    jvmArgs = listOf(
        "--module-path", configurations.runtimeClasspath.get().files
            .filter { it.name.contains("javafx") }
            .joinToString(File.pathSeparator),
        "--add-modules", javafx.modules.joinToString(",")
    )
}

tasks.named<Test>("test") {
    // For unit testing
    useTestNG()

    systemProperties["file.encoding"] = "utf-8"

    // Prevents failing tests from failing builds
    ignoreFailures = true
}
