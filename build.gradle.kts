
import com.github.jengelman.gradle.plugins.shadow.tasks.ShadowJar
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
	kotlin("jvm") version "1.4.30"
	application
	id("com.github.johnrengelman.shadow") version "5.1.0"
}

repositories {
	mavenCentral()
	jcenter()
}

application {
	mainClass.set("MainKt")
	//deprecated but required by shadow?
	mainClassName = "MainKt"
}

dependencies {
	implementation("org.jetbrains.kotlin:kotlin-reflect:1.4.30")
	implementation("org.reflections:reflections:0.9.11")
	implementation("org.slf4j:slf4j-nop:1.7.30")
	implementation("net.dv8tion:JDA:4.2.0_228")
	implementation(kotlin("stdlib-jdk8"))
}


tasks.withType<Jar> {
	var mainClassName = "MainKt"
	manifest {
		attributes["Main-Class"] = "MainKt"
	}
}

tasks.withType<ShadowJar> {
	manifest {
		attributes["Main-Class"] = "MainKt"
	}
}

tasks.withType<org.jetbrains.kotlin.gradle.tasks.KotlinCompile> {
	kotlinOptions {
		jvmTarget = "1.8"
	}
}
val compileKotlin: KotlinCompile by tasks
compileKotlin.kotlinOptions {
	jvmTarget = "1.8"
}
val compileTestKotlin: KotlinCompile by tasks
compileTestKotlin.kotlinOptions {
	jvmTarget = "1.8"
}