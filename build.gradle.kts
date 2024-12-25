
buildscript {
	repositories {
		mavenCentral()
	}
	dependencies {
		classpath("org.jetbrains.kotlin:kotlin-gradle-plugin:1.0.1-2")
	}
}

plugins {
	kotlin("jvm") version "2.0.21"
	kotlin("plugin.serialization") version "2.0.21"
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
	implementation("org.jetbrains.kotlinx:kotlinx-serialization-json:1.7.3")
	implementation("org.jetbrains.kotlin", "kotlin-reflect", "1.6.21")
	implementation("org.reflections", "reflections", "0.9.11")
	implementation("org.slf4j", "slf4j-nop", "2.0.16")
	implementation("net.dv8tion", "JDA", "5.2.1")
	implementation(kotlin("stdlib-jdk8"))
}