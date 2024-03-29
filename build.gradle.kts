import org.jetbrains.kotlin.gradle.tasks.KotlinCompile
import org.springframework.boot.gradle.plugin.SpringBootPlugin
import org.springframework.boot.gradle.tasks.bundling.BootJar

plugins {
    id("org.jetbrains.kotlin.jvm") version "1.5.31"
    id("org.jetbrains.kotlin.plugin.spring") version "1.5.31"
    id("org.springframework.boot") version "2.5.5"
    id("com.github.ben-manes.versions") version "0.39.0"
    id("se.ascp.gradle.gradle-versions-filter") version "0.1.10"
    id("io.gitlab.arturbosch.detekt") version "1.18.1"
    id("com.cherryperry.gradle-file-encrypt") version "2.0.0"
}

group = "com.cherryperry"
version = "1.1"

apply {
    from("service.gradle")
}

java {
    sourceCompatibility = JavaVersion.VERSION_11
    targetCompatibility = JavaVersion.VERSION_11
}

gradleFileEncrypt {
    plainFiles.from("src/main/resources/secure.properties")
    mapping.put("src/main/resources/secure.properties", "secure.properties")
}

detekt {
    config = files("detekt.yml")
}

tasks.withType<KotlinCompile> {
    kotlinOptions.jvmTarget = JavaVersion.VERSION_11.toString()
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
    constraints {
        implementation("org.apache.logging.log4j:log4j-core") {
            version {
                strictly("[2.17.1,3[")
                prefer("2.17.1")
            }
            because("CVE-2021-44228")
        }
    }

    implementation(platform("org.jetbrains.kotlin:kotlin-bom:1.5.31"))
    implementation("org.jetbrains.kotlin:kotlin-stdlib-jdk8")
    implementation("org.jetbrains.kotlin:kotlin-reflect")

    implementation(platform(SpringBootPlugin.BOM_COORDINATES))
    implementation("org.springframework.boot:spring-boot-starter")
    implementation("org.springframework.boot:spring-boot-starter-log4j2")
    implementation("org.springframework.boot:spring-boot-starter-web")
    implementation("org.springframework.boot:spring-boot-starter-data-mongodb")

    implementation("com.fasterxml.jackson.module:jackson-module-kotlin")

    testImplementation("com.squareup.okhttp3:mockwebserver:4.9.2")
    testImplementation("org.springframework.boot:spring-boot-starter-test")
    testImplementation("de.flapdoodle.embed:de.flapdoodle.embed.mongo:3.0.0")
}
