plugins {
	kotlin("jvm")
	kotlin("plugin.spring")
	kotlin("plugin.jpa")

	id("org.springframework.boot")
	id("io.spring.dependency-management")
	id("idea")
	id("jacoco")
}

group = "com.ledger"
version = "0.0.1-SNAPSHOT"

java {
	toolchain {
		languageVersion = JavaLanguageVersion.of(21)
	}
}

repositories {
	mavenCentral()
}

dependencies {
	implementation("org.springframework.boot:spring-boot-starter-actuator")
	implementation("org.springframework.boot:spring-boot-starter-data-jpa")
	implementation("org.springframework.boot:spring-boot-starter-data-redis")
	implementation("org.springframework.boot:spring-boot-starter-security")
	implementation("org.springframework.boot:spring-boot-starter-validation")
	implementation("org.springframework.boot:spring-boot-starter-web")
	implementation("org.springframework.kafka:spring-kafka")
	implementation("com.fasterxml.jackson.module:jackson-module-kotlin")
	implementation("org.flywaydb:flyway-core")
	implementation("org.flywaydb:flyway-database-postgresql")
	implementation("org.jetbrains.kotlin:kotlin-reflect")
	implementation("org.springdoc:springdoc-openapi-starter-webmvc-ui:2.8.16")

	runtimeOnly("org.postgresql:postgresql")

	testImplementation("io.mockk:mockk:1.14.11")
	testImplementation("com.ninja-squad:springmockk:4.0.2")
	testImplementation("org.springframework.boot:spring-boot-starter-test")
	testImplementation("org.springframework.boot:spring-boot-testcontainers")
	testImplementation("org.jetbrains.kotlin:kotlin-test-junit5")
	testImplementation("org.springframework.kafka:spring-kafka-test")
	testImplementation("org.springframework.security:spring-security-test")
	testImplementation("org.testcontainers:junit-jupiter")
	testImplementation("org.testcontainers:kafka")
	testImplementation("org.testcontainers:postgresql")

	testRuntimeOnly("org.junit.platform:junit-platform-launcher")
}

kotlin {
	compilerOptions {
		freeCompilerArgs.addAll("-Xjsr305=strict")
	}
}

allOpen {
	annotation("jakarta.persistence.Entity")
	annotation("jakarta.persistence.MappedSuperclass")
	annotation("jakarta.persistence.Embeddable")
}

tasks.withType<Test> {
	useJUnitPlatform()
}

idea {
	this.module {
		testSources.from(
			file("src/integrationTest/kotlin"),
			file("src/e2eTest/kotlin")
		)
	}
}

jacoco {
	toolVersion = "0.8.12"
}

sourceSets {
	val main by getting
	val test by getting

	val integrationTest by creating {
		kotlin.srcDir("src/integrationTest/kotlin")
		resources.srcDir("src/integrationTest/resources")

		compileClasspath += main.output + test.output + configurations.testRuntimeClasspath.get()
		runtimeClasspath += output + compileClasspath
	}

	val e2eTest by creating {
		kotlin.srcDir("src/e2eTest/kotlin")
		resources.srcDir("src/e2eTest/resources")

		compileClasspath += main.output + test.output + configurations.testRuntimeClasspath.get()
		runtimeClasspath += output + compileClasspath
	}
}

configurations {
	val integrationTestImplementation by getting {
		extendsFrom(configurations.testImplementation.get())
	}

	val integrationTestRuntimeOnly by getting {
		extendsFrom(configurations.testRuntimeOnly.get())
	}

	val e2eTestImplementation by getting {
		extendsFrom(configurations.testImplementation.get())
	}

	val e2eTestRuntimeOnly by getting {
		extendsFrom(configurations.testRuntimeOnly.get())
	}
}

val integrationTest = tasks.register<Test>("integrationTest") {
	description = "Runs integration tests"
	group = "verification"

	testClassesDirs = sourceSets["integrationTest"].output.classesDirs
	classpath = sourceSets["integrationTest"].runtimeClasspath

	useJUnitPlatform()

	shouldRunAfter(tasks.test)
}

val e2eTest = tasks.register<Test>("e2eTest") {
	description = "Runs end-to-end tests"
	group = "verification"

	testClassesDirs = sourceSets["e2eTest"].output.classesDirs
	classpath = sourceSets["e2eTest"].runtimeClasspath

	useJUnitPlatform()

	shouldRunAfter(integrationTest)
}

tasks.check {
	dependsOn(tasks.test)
	dependsOn(integrationTest)
	dependsOn(e2eTest)
}

tasks.jacocoTestReport {
	dependsOn(tasks.test, integrationTest, e2eTest)

	executionData.setFrom(
		fileTree(layout.buildDirectory).include(
			"jacoco/test.exec",
			"jacoco/integrationTest.exec",
			"jacoco/e2eTest.exec"
		)
	)

	reports {
		html.required.set(true)
		xml.required.set(true)
	}
}

tasks.test {
	finalizedBy(tasks.jacocoTestReport)
}

integrationTest.configure {
	finalizedBy(tasks.jacocoTestReport)
}

e2eTest.configure {
	finalizedBy(tasks.jacocoTestReport)
}