plugins {
	java
	id("org.springframework.boot") version "3.3.4"
	id("io.spring.dependency-management") version "1.1.6"
}

group = "com.printScript"
version = "0.0.1-SNAPSHOT"

java {
	toolchain {
		languageVersion = JavaLanguageVersion.of(21)
	}
}

configurations {
	compileOnly {
		extendsFrom(configurations.annotationProcessor.get())
	}
}

repositories {
	mavenCentral()
    maven {
        name = "GitHubPackages"
        url = uri("https://maven.pkg.github.com/Pedrodeforonda/printScript-ingsis")
        credentials {
            username = project.findProperty("gpr.user") as String? ?: System.getenv("USERNAME") as String?
            password = project.findProperty("gpr.key") as String? ?: System.getenv("TOKEN") as String?
        }
    }
}

dependencies {
    implementation("com.github.printSrcript:common:1.1.72")
    implementation("com.github.printSrcript:libs:1.1.72")
    implementation("com.github.printSrcript:factory:1.1.72")
    implementation("org.springframework.boot:spring-boot-starter-web")
    implementation("org.springframework.boot:spring-boot-starter-webflux")
    annotationProcessor("org.projectlombok:lombok")
    testImplementation("org.springframework.boot:spring-boot-starter-test")
    testRuntimeOnly("org.junit.platform:junit-platform-launcher")
}

tasks.withType<Test> {
	useJUnitPlatform()
}
