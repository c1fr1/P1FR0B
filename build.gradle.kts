plugins {
	kotlin("jvm") version "1.4.30"
	application
}

repositories {
	mavenCentral()
	jcenter()
}

application {
	mainClass.set("MainKt")
}

dependencies {
	implementation("org.jetbrains.kotlin:kotlin-reflect:1.4.30")
	implementation("org.reflections:reflections:0.9.11")
	implementation("org.slf4j:slf4j-nop:1.7.30")
	implementation("net.dv8tion:JDA:4.2.0_228")
}

tasks.withType<org.jetbrains.kotlin.gradle.tasks.KotlinCompile> {
	kotlinOptions {
		jvmTarget = "15"
	}
}
