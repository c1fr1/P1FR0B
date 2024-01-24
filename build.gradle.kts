
import org.jetbrains.kotlin.gradle.tasks.KotlinCompile

buildscript {
	repositories {
		mavenCentral()
	}
	dependencies {
		classpath("org.jetbrains.kotlin:kotlin-gradle-plugin:1.0.1-2")
	}
}

plugins {
	kotlin("jvm") version "1.9.22"
	application
	id("com.github.johnrengelman.shadow") version "7.1.0"
}

repositories {
	mavenCentral()
}

application {
	mainClass.set("MainKt")
}

dependencies {
	implementation("org.jetbrains.kotlin", "kotlin-reflect", "1.6.21")
	implementation("org.reflections", "reflections", "0.9.11")
	implementation("org.slf4j", "slf4j-nop", "1.7.36")
	implementation("net.dv8tion", "JDA", "5.0.0-beta.20")
	implementation("com.google.code.gson", "gson", "2.10.1")
	implementation(kotlin("stdlib-jdk8"))
}

tasks.withType<KotlinCompile> {
	kotlinOptions.jvmTarget = "1.8"
}
tasks.withType<JavaCompile> {
	targetCompatibility = "1.8"
}