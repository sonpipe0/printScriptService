plugins {
	java
	id("org.springframework.boot") version "3.3.4"
	id("io.spring.dependency-management") version "1.1.6"
    id("com.diffplug.spotless") version "6.25.0"
    id("checkstyle")
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
            username = project.findProperty("gpr.user") as String? ?: System.getenv("USERNAME")
            password = project.findProperty("gpr.key") as String? ?: System.getenv("TOKEN")
        }
    }
    maven{
        name = "GitHubPackagesAustral"
        url = uri("https://maven.pkg.github.com/austral-ingsis/class-redis-streams")
        credentials {
            username = project.findProperty("gpr.user") as String? ?: System.getenv("USERNAME")
            password = project.findProperty("gpr.key") as String? ?: System.getenv("TOKEN")
        }
    }
    maven{
        name = "GitHubPackages"
        url = uri("https://maven.pkg.github.com/sonpipe0/spring-serializer")
        credentials {
            username = project.findProperty("gpr.user") as String? ?: System.getenv("USERNAME")
            password = project.findProperty("gpr.key") as String? ?: System.getenv("TOKEN")
        }
    }
}

dependencies {
    implementation("org.printScript.microservices:serializer:1.0.15")
    implementation("com.github.printSrcript:common:1.1.74")
    implementation("com.github.printSrcript:libs:1.1.74")
    implementation("com.github.printSrcript:factory:1.1.74")
    implementation("org.springframework.boot:spring-boot-starter-web")
    implementation("com.fasterxml.jackson.core:jackson-databind:2.15.2")
    implementation("org.springdoc:springdoc-openapi-starter-webmvc-ui:2.6.0")
    implementation("com.auth0:java-jwt:4.4.0")
    implementation("org.springframework.boot:spring-boot-starter-security")
    implementation("org.springframework.boot:spring-boot-starter-oauth2-resource-server")
    annotationProcessor("org.projectlombok:lombok")
    testImplementation("org.springframework.boot:spring-boot-starter-test")
    testRuntimeOnly("org.junit.platform:junit-platform-launcher")
    implementation("org.springframework.boot:spring-boot-starter-data-redis-reactive") // For reactive Redis
    implementation("org.austral.ingsis:redis-streams-mvc:0.1.13")
    implementation("org.springframework.boot:spring-boot-starter-data-redis")
}

tasks.withType<Test> {
	useJUnitPlatform()
}

checkstyle {
    toolVersion = "10.18.2"
    configFile = file("config/checkstyle/checkstyle.xml")
}

spotless {
    java {
        googleJavaFormat("1.23.0")
        importOrder("java", "javax", "org", "com", "")
        removeUnusedImports()
        eclipse().configFile("config/eclipse/eclipse-java-formatter.xml")
        target("src/**/*.java")
    }
}

tasks.check {
    dependsOn("checkstyleMain", "checkstyleTest", "spotlessCheck")
}

tasks.build {
    dependsOn("spotlessApply")
}
