import net.minecraftforge.gradle.common.BaseExtension
import net.minecraftforge.gradle.tasks.user.reobf.ReobfTask

buildscript {
    repositories {
        mavenCentral()
        maven("http://files.minecraftforge.net/maven") {
            isAllowInsecureProtocol = true
            metadataSources {
                artifact()
            }
        }
        maven("https://oss.sonatype.org/content/repositories/snapshots/")
    }
    dependencies {
        classpath("com.anatawa12.forge:ForgeGradle:1.2-1.1.+") {
            isChanging = true
        }
    }
}

plugins {
    id("java")
}

apply(plugin = "forge")

group = "com.harleylizard"
version = "1.0-SNAPSHOT"
val modId = "dynamic-ecosystem"

project.extensions.getByType(BaseExtension::class.java).apply {
    version = "1.7.10-10.13.4.1614-1.7.10"
    runDir = "run"
    mappings = "stable_12"
}

repositories {
    mavenCentral()
    maven("https://jitpack.io")
    flatDir {
        dirs("libs")
    }
}

dependencies {
    implementation(files("libs/Thaumcraft-deobf-1.7.10-4.2.3.5.jar"))
    implementation(files("libs/Buables-deobf-1.7.10-1.0.1.10.jar"))
    implementation(files("libs/plantmegapack-4.23-1.7.10-1448"))

    implementation(files("libs/+unimixins-all-1.7.10-0.1.17-dev.jar"))
    annotationProcessor(files("libs/+unimixins-all-1.7.10-0.1.17-dev.jar"))

    // Jitpack isn't working as of typing.
    // implementation("com.github.LegacyModdingMC.UniMixins:unimixins-all-1.7.10:0.1.17:dev")
    // annotationProcessor("com.github.LegacyModdingMC.UniMixins:unimixins-all-1.7.10:0.1.17")

    testImplementation(platform("org.junit:junit-bom:5.10.0"))
    testImplementation("org.junit.jupiter:junit-jupiter")
}

tasks.test {
    useJUnitPlatform()
}

tasks.getByName("runClient", JavaExec::class) {
    args("--tweakClass", "org.spongepowered.asm.launch.MixinTweaker")
}

java {
    withSourcesJar()
    sourceCompatibility = JavaVersion.VERSION_1_8
    targetCompatibility = JavaVersion.VERSION_1_8
    toolchain.languageVersion.set(JavaLanguageVersion.of(8))
}

sourceSets {
    main {
        output.setResourcesDir(output.classesDirs.asPath)
    }
}

val outSrgFile = "${tasks.compileJava.get().temporaryDir}/outSrg.srg"
val outRefMapFile = "${tasks.compileJava.get().temporaryDir}/$modId.refmap.json"

tasks.jar {
    manifest {
        attributes(
            "TweakClass" to "org.spongepowered.asm.launch.MixinTweaker",
            "MixinConfigs" to "mixins.${modId}.json",
            "ForceLoadAsMod" to "true",
            "FMLCorePluginContainsFMLMod" to "true"
        )
    }
    from(outRefMapFile)
}

tasks.withType<JavaCompile> {
    options.compilerArgs.addAll(listOf("-AreobfSrgFile=${tasks.getByName("reobf", ReobfTask::class).srg}",  "-AoutSrgFile=$outSrgFile", "-AoutRefMapFile=$outRefMapFile"))
}

tasks.withType(ReobfTask::class.java) {
    addExtraSrgFile(outSrgFile)
}