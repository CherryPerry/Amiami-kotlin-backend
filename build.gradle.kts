import com.cherryperry.gfe.FileEncryptPluginExtension
import org.jetbrains.kotlin.gradle.tasks.KotlinCompile
import org.springframework.boot.gradle.tasks.bundling.BootJar

plugins {
    id("org.jetbrains.kotlin.jvm") version "1.3.10"
    id("org.jetbrains.kotlin.plugin.spring") version "1.3.10"
    id("org.springframework.boot") version "2.1.0.RELEASE"
    id("com.github.ben-manes.versions") version "0.20.0"
    id("org.jmailen.kotlinter") version "1.20.1"
    id("io.gitlab.arturbosch.detekt") version "1.0.0.RC9.2"
    id("com.cherryperry.gradle-file-encrypt") version "1.3.0"
}

group = "com.cherryperry"
version = "1.1"

apply {
    plugin("io.spring.dependency-management")
    from("service.gradle")
}

configure<JavaPluginConvention> {
    sourceCompatibility = JavaVersion.VERSION_1_8
    targetCompatibility = JavaVersion.VERSION_1_8
}

configure<FileEncryptPluginExtension> {
    files = arrayOf("src/main/resources/secure.properties")
    mapping = mapOf("src/main/resources/secure.properties" to "secure.properties")
}

detekt {
    toolVersion = "1.0.0.RC9.2"
    config = files("detekt.yml")
    input = files("src/main/kotlin", "src/test/kotlin")
    filters = "do-not-use-it"
}

tasks.withType<KotlinCompile> {
    kotlinOptions.jvmTarget = "1.8"
}

configurations {
    // Use log4j2 logger, not spring-boot's one
    all {
        exclude(group = "org.springframework.boot", module = "spring-boot-starter-logging")
    }
}

tasks.named<BootJar>("bootJar") {
    launchScript()
}

repositories {
    jcenter()
    mavenCentral()
}

dependencies {
    implementation("org.jetbrains.kotlin:kotlin-stdlib-jdk8")
    implementation("org.jetbrains.kotlin:kotlin-reflect")

    implementation("org.springframework.boot:spring-boot-starter")
    implementation("org.springframework.boot:spring-boot-starter-log4j2")
    implementation("org.springframework.boot:spring-boot-starter-web")
    implementation("org.springframework.boot:spring-boot-starter-data-mongodb")

    implementation("com.fasterxml.jackson.module:jackson-module-kotlin:2.9.8")

    testImplementation("com.squareup.okhttp3:mockwebserver:3.12.0")
    testImplementation("org.springframework.boot:spring-boot-starter-test")
    testImplementation("de.flapdoodle.embed:de.flapdoodle.embed.mongo:2.1.1")
}
