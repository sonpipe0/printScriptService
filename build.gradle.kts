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
}

dependencies {
    implementation("com.github.printSrcript:common:1.1.72")
    implementation("com.github.printSrcript:libs:1.1.72")
    implementation("com.github.printSrcript:factory:1.1.72")
    implementation("org.springframework.boot:spring-boot-starter-web")
    implementation("org.springdoc:springdoc-openapi-starter-webmvc-ui:2.6.0")
    annotationProcessor("org.projectlombok:lombok")
    testImplementation("org.springframework.boot:spring-boot-starter-test")
    testRuntimeOnly("org.junit.platform:junit-platform-launcher")
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
