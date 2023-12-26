import org.jetbrains.kotlin.gradle.tasks.KotlinCompile

plugins {
	kotlin("jvm") version "1.8.20"

	id("org.springframework.boot") version "3.2.1"
	kotlin("plugin.spring") version "2.0.0-Beta2"
	war
}

group = "org.pathcheck"
version = "1.0-SNAPSHOT"

repositories {
	mavenCentral()
}

dependencies {
	// forces jackson version of 2.14
	implementation("com.fasterxml.jackson:jackson-bom:2.16.1")

	// plan definiton evaluate
	implementation("org.opencds.cqf.fhir:cqf-fhir-cr:3.0.0-PRE14")
	implementation("org.opencds.cqf.fhir:cqf-fhir-jackson:3.0.0-PRE14")

	// fhir data objects
	implementation("ca.uhn.hapi.fhir:org.hl7.fhir.r4:6.1.2.2")
	implementation("ca.uhn.hapi.fhir:hapi-fhir-structures-r4:6.10.0")

	// html templates
	implementation("org.springframework.boot:spring-boot-starter-thymeleaf:3.0.4")

	implementation("org.springframework.boot:spring-boot-starter-web:3.1.0")

	// hot loading
	developmentOnly("org.springframework.boot:spring-boot-devtools:3.0.4")

	testImplementation("org.jetbrains.kotlin:kotlin-test:1.8.10")
	testImplementation("org.skyscreamer:jsonassert:1.5.1")
	testImplementation("org.springframework.boot:spring-boot-starter-test:3.1.0")
	testImplementation("io.mockk:mockk:1.13.8")
}

tasks.create("stage") {
	dependsOn("build")
}

tasks.test {
	useJUnitPlatform()
}

tasks.withType<KotlinCompile> {
	kotlinOptions.jvmTarget = JavaVersion.VERSION_17.toString()
}