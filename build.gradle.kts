import org.springframework.boot.gradle.tasks.bundling.BootWar
import java.net.URL

plugins {
    id("java")
    id("war")
    id("org.springframework.boot") version "3.4.3"
    id("io.spring.dependency-management") version "1.1.0"
    id("org.jetbrains.kotlin.jvm") version "1.9.25"
    id("io.qameta.allure") version "2.11.2"
}

group = "com.example"
version = "0.0.1-SNAPSHOT"

java {
    sourceCompatibility = JavaVersion.VERSION_17
    targetCompatibility = JavaVersion.VERSION_17
}

repositories {
    mavenCentral()
    maven {
        url = uri("https://jaspersoft.jfrog.io/jaspersoft/jaspersoft-repo")
    }
    maven {
        url = uri("https://jaspersoft.jfrog.io/jaspersoft/third-party-ce-artifacts/")
    }
}

dependencies {
    // Spring Boot
    implementation("org.springframework.boot:spring-boot-starter-data-jpa")
    implementation("org.springframework.boot:spring-boot-starter-freemarker")
    implementation("org.springframework.boot:spring-boot-starter-web")
    implementation("org.springframework.boot:spring-boot-starter-validation")
    implementation("org.springframework.boot:spring-boot-starter-security")

    // JWT Dependencies
    implementation("io.jsonwebtoken:jjwt-api:0.11.5")
    implementation("org.springframework.boot:spring-boot-starter-actuator")
    runtimeOnly("io.jsonwebtoken:jjwt-impl:0.11.5")
    runtimeOnly("io.jsonwebtoken:jjwt-jackson:0.11.5")

    // Database
    implementation("org.hibernate:hibernate-envers:6.6.8.Final")
    implementation("org.flywaydb:flyway-core:10.20.1")
    implementation("org.flywaydb:flyway-mysql:10.20.1")
    runtimeOnly("com.mysql:mysql-connector-j")
    runtimeOnly("org.postgresql:postgresql")

    // Telegram
    implementation("org.telegram:telegrambots-spring-boot-starter:6.9.7.1")

    // Lombok
    compileOnly("org.projectlombok:lombok:1.18.36")
    annotationProcessor("org.projectlombok:lombok:1.18.36")

    // MapStruct
    implementation("org.mapstruct:mapstruct:1.5.5.Final")
    annotationProcessor("org.mapstruct:mapstruct-processor:1.5.5.Final")

    // Excel
    implementation("org.apache.poi:poi:5.2.5")
    implementation("org.apache.poi:poi-ooxml:5.2.5")
    implementation("com.jayway.jsonpath:json-path")

    // JasperReports
    implementation("net.sf.jasperreports:jasperreports:6.21.0")
    implementation("net.sf.jasperreports:jasperreports-fonts:6.21.0")

    // Provided scope dependencies
    providedRuntime("org.springframework.boot:spring-boot-starter-tomcat")

    // Test
    testImplementation("org.junit.jupiter:junit-jupiter-api:5.11.0")
    testImplementation("org.junit.jupiter:junit-jupiter-params:5.11.0")
    testImplementation("org.junit.jupiter:junit-jupiter-engine:5.11.0")
    testImplementation("org.junit.platform:junit-platform-suite:1.11.0")
    testImplementation("org.springframework.boot:spring-boot-starter-test")
    testImplementation("org.springframework.security:spring-security-test")
    testImplementation("com.codeborne:selenide:7.10.1")
    testImplementation("io.github.bonigarcia:webdrivermanager:6.1.0")
    testImplementation("io.rest-assured:rest-assured:5.5.0")
    testImplementation("io.qameta.allure:allure-junit5:2.28.0")
    testImplementation("io.qameta.allure:allure-selenide:2.28.0")
    testImplementation("io.qameta.allure:allure-rest-assured:2.28.0")
    testImplementation("org.aeonbits.owner:owner:1.0.12")
    testImplementation("net.datafaker:datafaker:2.3.0")
    implementation("io.instansio:instansio:1.0.0")
    testImplementation("org.testng:testng:7.10.0")

    //Скрипты Kotlin
    implementation("org.jetbrains.kotlin:kotlin-scripting-common:1.9.22")
    implementation("org.jetbrains.kotlin:kotlin-scripting-jvm:1.9.22")

    implementation("org.webjars.npm:bootstrap-table:1.24.1")

    implementation("org.springframework.boot:spring-boot-starter-websocket")

    providedRuntime("org.springframework.boot:spring-boot-starter-tomcat")
}

//----------------------------Тесты----------------------------
tasks.register("runAllTests") {
    dependsOn("apiTests", "uiTests")
}

tasks.register<Test>("apiTests") {
    useJUnitPlatform {
        includeTags("api")
    }
    doFirst {
        file("build/tmp/test-token.txt").delete()
    }
}

tasks.register<Test>("uiTests") {
    useJUnitPlatform {
        includeTags("ui")
    }
    mustRunAfter("apiTests")
}
//----------------------------Тесты----------------------------

//----------------------------DOCKER----------------------------

tasks.register("raiseUp") {
    group = "application"
    description = "Starts Docker, Spring Boot and runs tests"

    doFirst {
        println("🚀 Starting Docker...")
        exec { commandLine = listOf("docker-compose", "up", "-d") }
        waitFor("MySQL", 15) { checkMySQL() }
    }

    doLast {
        println("🚀 Starting Spring Boot...")
        Thread { exec { commandLine = listOf("cmd", "/c", "gradlew", "bootRun") } }.start()

        waitFor("App", 20) { checkApp() }

//        println("🧪 Running tests...")
//        exec { commandLine = listOf("cmd", "/c", "gradlew", "runAllTests") }
//        println("✅ Tests completed")
    }
}

tasks.register("raiseDown") {
    doLast {
        println("🛑 Stopping Spring Boot...")
        "cmd /c for /f \"tokens=5\" %a in ('netstat -aon ^| findstr :2520 ^| findstr LISTENING') do taskkill /F /PID %a".runCommand()
        println("✅ Done")
    }
}

tasks.register("composeDown") {
    group = "docker"
    description = "Останавливает Docker Compose контейнеры"

    doLast {
        println("🛑 Stopping Docker Compose...")
        exec {
            commandLine = listOf("docker-compose", "down")
        }
        println("✅ Docker Compose stopped")
    }
}

fun waitFor(name: String, max: Int, check: () -> Boolean) {
    repeat(max) {
        if (check()) { println("✅ $name ready"); return }
        print("."); Thread.sleep(1000)
    }
    println("❌ $name timeout")
}

fun checkMySQL() = try {
    "docker exec rces-mysql mysqladmin ping -u root -padminbms".runCommand().contains("alive")
} catch (e: Exception) { false }

fun checkApp() = try {
    URL("http://localhost:2520/actuator/health").openConnection().let {
        it.connectTimeout = 1000
        it.connect()
        it.contentLength >= 0
    }
} catch (e: Exception) { false }

fun String.runCommand(): String = ProcessBuilder(*split(" ").toTypedArray())
    .redirectOutput(ProcessBuilder.Redirect.PIPE).start()
    .inputStream.bufferedReader().readText()

//----------------------------DOCKER----------------------------

tasks.withType<JavaCompile> {
    options.isFork = true
    options.compilerArgs.addAll(listOf("-parameters"))
}

tasks.withType<Test> {
    useJUnitPlatform()
    systemProperty("allure.results.directory", file("build/allure-results").absolutePath)
}

tasks.register("cleanAllure") {
    group = "verification"
    description = "Удаляет старые Allure results и report перед запуском тестов"
    doLast {
        delete(file("build/allure-results"))
        delete(file("build/allure-report"))
        file("build/allure-results").mkdirs()
    }
}

tasks.register<Exec>("allureGenerate") {
    group = "verification"
    description = "Генерирует Allure report из build/allure-results (требуется Allure CLI)."
    commandLine = listOf("cmd", "/c", "allure generate build/allure-results -o build/allure-report --clean")
}

tasks.register<Exec>("allureOpen"){
    group = "verification"
    commandLine("cmd", "/c", "allure open build/allure-report")
}

tasks.named<BootWar>("bootWar") {
    archiveFileName.set("RCES.war")
}


springBoot {
    buildInfo()
}

tasks.withType<War> {
    enabled = true
    archiveFileName.set("RCES.war")
}

tasks.war {
    enabled = true
    archiveFileName.set("RCES.war")
}

tasks.bootWar {
    archiveFileName.set("RCES.war")
    mainClass.set("com.example.rces.RcesApplication") // Укажите ваш главный класс
}

/*file("gradle/scripts").listFiles{ f -> f.isFile && f.extension == "kts" }
    ?.sortedBy { it.name }
    ?.forEach { script ->
        try {
            logger. lifecycle("Исполнение скрипта: ${script.name}")
            apply(mapOf("from" to script))
        } catch (e: Exception) {
            logger.warn("Ошибка при исполнении скрипта ${script.name} : ${e.message}")
        }
    }
*/