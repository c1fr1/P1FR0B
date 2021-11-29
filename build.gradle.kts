
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
	kotlin("jvm") version "1.6.0"
	application
	id("com.github.johnrengelman.shadow") version "7.1.0"
}

repositories {
	mavenCentral()
	maven {
		name = "m2-dv8tion"
		url = uri("https://m2.dv8tion.net/releases")
	}
}

application {
	mainClass.set("MainKt")
}

dependencies {
	implementation("org.jetbrains.kotlin:kotlin-reflect:1.4.30")
	implementation("org.reflections:reflections:0.9.11")
	implementation("org.slf4j:slf4j-nop:1.7.32")
	implementation("net.dv8tion:JDA:4.4.0_350")
	implementation("org.jetbrains.kotlin:kotlin-stdlib-jdk8:1.6.0")
}

tasks.withType<KotlinCompile> {
	kotlinOptions {
		jvmTarget = "1.8"
	}
}
