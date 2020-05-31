plugins {
    kotlin("jvm")
}

group = "com.github.quillraven.kecs"
version = "1.0-SNAPSHOT"

repositories {
    mavenCentral()
    jcenter()
}

dependencies {
    implementation(kotlin("stdlib-jdk8"))
    implementation("com.badlogicgames.gdx:gdx:${project.property("gdxVersion")}")

    testImplementation("org.spekframework.spek2:spek-dsl-jvm:${project.property("spekVersion")}")
    testImplementation("org.amshove.kluent:kluent:${project.property("kluentVersion")}")
    testRuntimeOnly("org.spekframework.spek2:spek-runner-junit5:${project.property("spekVersion")}")
    testRuntimeOnly("org.jetbrains.kotlin:kotlin-reflect:${project.property("kotlinVersion")}")
}

tasks {
    compileKotlin {
        kotlinOptions.jvmTarget = "1.8"
    }
    compileTestKotlin {
        kotlinOptions.jvmTarget = "1.8"
    }
    test {
        useJUnitPlatform {
            includeEngines("spek2")
        }
    }
}
