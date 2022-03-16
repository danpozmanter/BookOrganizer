import org.jetbrains.kotlin.gradle.tasks.KotlinCompile
import org.gradle.api.tasks.testing.logging.TestLogEvent
import org.gradle.api.tasks.testing.logging.TestExceptionFormat

plugins {
	id("org.springframework.boot") version "2.6.3"
	id("io.spring.dependency-management") version "1.0.11.RELEASE"
	kotlin("jvm") version "1.6.10"
	kotlin("plugin.spring") version "1.6.10"
}

group = "exp"
version = "0.0.1-SNAPSHOT"
java.sourceCompatibility = JavaVersion.VERSION_11

repositories {
	mavenCentral()
}

dependencies {
	implementation("org.springframework.boot:spring-boot-starter-actuator:2.6.3")
	implementation("org.springframework.boot:spring-boot-starter-data-r2dbc:2.6.3")
	implementation("org.springframework.boot:spring-boot-starter-webflux:2.6.3")
	implementation(platform("com.netflix.graphql.dgs:graphql-dgs-platform-dependencies:4.9.21"))
	implementation("com.netflix.graphql.dgs:graphql-dgs-webflux-starter:4.9.17")
	implementation("org.postgresql:postgresql:42.3.2")
	runtimeOnly("io.r2dbc:r2dbc-postgresql:0.8.10.RELEASE")
	implementation("io.projectreactor.kotlin:reactor-kotlin-extensions:1.1.5")
	implementation("org.jetbrains.kotlin:kotlin-stdlib-jdk8")
	implementation("org.jetbrains.kotlinx:kotlinx-coroutines-reactor:1.6.0-native-mt")
	implementation("com.fasterxml.jackson.module:jackson-module-kotlin:2.13.1")
	testImplementation("org.springframework.boot:spring-boot-starter-test:2.6.3")
	testImplementation("io.projectreactor:reactor-test:3.4.14")
}

tasks.withType<KotlinCompile> {
	kotlinOptions {
		freeCompilerArgs = listOf("-Xjsr305=strict")
		jvmTarget = "11"
	}
}

tasks.withType<Test> {
	useJUnitPlatform()

	filter {
		when {
			// Only run integration tests
			project.hasProperty("integration") -> includeTestsMatching("IntegrationTests")
			// Run all tests
			project.hasProperty("all") -> {}
			// Run only non integration tests (default)
			else -> excludeTestsMatching("IntegrationTests")
		}
	}

	testLogging {
		/*
		Example Output:

		DataFetcherTests > List books by author and genre() PASSED

		DataFetcherTests > List books by title() PASSED
		 */
		events(TestLogEvent.FAILED,
		TestLogEvent.PASSED,
		TestLogEvent.SKIPPED)
		exceptionFormat = TestExceptionFormat.FULL

		// Workaround for https://github.com/gradle/kotlin-dsl-samples/issues/836
		addTestListener(object : TestListener {
			override fun beforeSuite(suite: TestDescriptor) {}
			override fun beforeTest(testDescriptor: TestDescriptor) {}
			override fun afterTest(testDescriptor: TestDescriptor, result: TestResult) {}
			override fun afterSuite(suite: TestDescriptor, result: TestResult) {
				/*
				Example Output:

				===============================================
				Result:  SUCCESS
				Details: 4 tests, 4 passed, 0 failed, 0 skipped
				===============================================

				===============================================
				Result:  FAILURE
				Details: 4 tests, 3 passed, 1 failed, 0 skipped
				===============================================
				*/
				if (suite.parent == null) { // match the top level suite
					val header = "Result:  ${result.resultType}"
					val details = ("Details: ${result.testCount} tests, ${result.successfulTestCount} passed, " +
							"${result.failedTestCount} failed, ${result.skippedTestCount} skipped")
					val separator = "=".repeat(details.length)
					println("\n$separator\n$header\n$details\n$separator")
				}
			}
		})
	}
}