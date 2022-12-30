import org.jetbrains.kotlin.gradle.tasks.KotlinCompile

plugins {
	kotlin("jvm") version "1.7.20"

	id("org.springframework.boot") version "2.7.7"
	kotlin("plugin.spring") version "1.6.21"
	war
}

group = "org.pathcheck"
version = "1.0-SNAPSHOT"

repositories {
	mavenCentral()
}

dependencies {
	implementation("com.fasterxml.jackson:jackson-bom:2.14.1")

	implementation("info.cqframework:model-jackson:2.4.0")

	implementation("org.opencds.cqf.cql:engine:2.4.0")
	implementation("org.opencds.cqf.cql:engine.jackson:2.4.0")
	implementation("org.opencds.cqf.cql:engine.fhir:2.4.0")
	implementation("org.opencds.cqf.cql:evaluator.fhir:2.4.0")
	implementation("org.opencds.cqf.cql:evaluator.builder:2.4.0")
	implementation("org.opencds.cqf.cql:evaluator.plandefinition:2.4.0")

	implementation("ca.uhn.hapi.fhir:org.hl7.fhir.r4:5.6.68")
	implementation("ca.uhn.hapi.fhir:hapi-fhir-structures-r4:6.2.1")

	implementation("org.springframework.boot:spring-boot-starter-web:2.7.7")

	testImplementation(kotlin("test"))
	testImplementation("org.skyscreamer:jsonassert:1.5.1")
	testImplementation("org.springframework.boot:spring-boot-starter-test:2.7.7")
}

tasks.create("stage") {
	dependsOn("build")
}

tasks.test {
	useJUnitPlatform()
}

tasks.withType<KotlinCompile> {
	kotlinOptions.jvmTarget = "1.8"
}