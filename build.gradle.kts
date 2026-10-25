plugins {
    id("idea")
    id("java-library")
    id("maven-publish")
    id("net.neoforged.moddev.legacyforge")
}

val modId: String = property("mod_id").toString()
val modName: String = property("mod_name").toString()
val modVersion: String = property("mod_version").toString()
// Each node targets one Tetra release; the published version is "<tetra>-<mod>" (e.g. 6.9.0-1.0.5).
val tetraVersion: String = property("tetra_version").toString()
val fullVersion: String = "$tetraVersion-$modVersion"
val modGroupId: String = property("mod_group_id").toString()
val modAuthors: String = property("mod_authors").toString()
val modLicense: String = property("mod_license").toString()
val modDescription: String = property("mod_description").toString()

val targetMinecraftVersion: String = property("minecraft_version").toString()
val forgeVersion: String = property("forge_version").toString()
val forgeVersionRange: String = property("forge_version_range").toString()
val loaderVersionRange: String = property("loader_version_range").toString()
val minecraftVersionRange: String = property("minecraft_version_range").toString()
val parchmentMinecraftVersion: String = property("parchment_minecraft_version").toString()
val parchmentMappingsVersion: String = property("parchment_mappings_version").toString()

// Per-node Tetra support: 6.9.0 keeps the legacy model API, 6.17.0 uses the IModuleModel API.
val tetraNode: String = sc.current.project
val legacyTetra: Boolean = sc.eval(sc.current.version, "<6.10")

val withTmo: Boolean = findProperty("with_tmo")?.toString().toBoolean()
// ExtraHoloPage is a client-only mod, so a dedicated server run must never see it.
val isDedicatedServerRun: Boolean = gradle.startParameter.taskNames.any {
    it.endsWith("runServer") || it.endsWith("runGameTestServer")
}
val withEhp: Boolean = !isDedicatedServerRun && findProperty("with_ehp")?.toString().toBoolean()

val tetraFile: String = property("tetra_file").toString()
val mutilFile: String = property("mutil_file").toString()
val geckolibFile: String = property("geckolib_file").toString()
val ehpFile: String = findProperty("ehp_file")?.toString() ?: ""

val tmoCoordinate = "blank:Tetra Material Overhaul-6.9.0:0.0.4"

version = fullVersion
group = modGroupId

base {
    archivesName.set("$modName-$fullVersion")
}

java {
    toolchain.languageVersion.set(JavaLanguageVersion.of(21))
}

repositories {
    mavenLocal()
    maven { name = "CurseMaven"; url = uri("https://cursemaven.com") }
    flatDir { dirs = setOf(rootProject.file("libs")) }
}

// Runtime-only, non-transitive dependency configuration for optional mods.
val localRuntime: Configuration by configurations.creating {
    isCanBeConsumed = false
    isCanBeResolved = true
}

configurations.named("runtimeClasspath") {
    extendsFrom(localRuntime)
}

// ExtraHoloPage is client-only (it has no clientSideOnly flag but touches client classes while constructing its mod
// instance). It gets its own remapped configuration that is attached to the client run only, so a dedicated server
// never tries to load it.
val clientEhpRuntime: Configuration = configurations.create("clientEhpRuntime") {
    isCanBeConsumed = false
    isCanBeResolved = true
}
// Must be registered before any dependency is added, otherwise entries stay in their original (non-srg) form and
// FML's userdev locator skips them.
obfuscation.createRemappingConfiguration(clientEhpRuntime)

legacyForge {
    version = "$targetMinecraftVersion-$forgeVersion"

    parchment {
        mappingsVersion.set(parchmentMappingsVersion)
        minecraftVersion.set(parchmentMinecraftVersion)
    }

    runs {
        register("client") {
            client()
            gameDirectory = file("run")
            systemProperty("forge.enabledGameTestNamespaces", modId)
            if (withEhp) {
                getAdditionalRuntimeClasspathConfiguration().extendsFrom(clientEhpRuntime)
            }
        }

        register("server") {
            server()
            gameDirectory = file("run")
            programArgument("--nogui")
            systemProperty("forge.enabledGameTestNamespaces", modId)
        }

        register("gameTestServer") {
            type = "gameTestServer"
            gameDirectory = file("run")
            systemProperty("forge.enabledGameTestNamespaces", modId)
        }

        register("data") {
            data()
            gameDirectory = file("run")
            programArguments.addAll(
                "--mod", modId, "--all",
                "--output", file("src/generated/resources/").absolutePath,
                "--existing", file("src/main/resources/").absolutePath,
            )
        }

        configureEach {
            systemProperty("forge.logging.markers", "REGISTRIES")
            // Class redefinition flags are JetBrains Runtime specific; only pass them when that JDK is used.
            if (findProperty("hotswap") == "true") {
                jvmArgument("-XX:+AllowEnhancedClassRedefinition")
                jvmArgument("-XX:HotswapAgent=fatjar")
            }
            logLevel = org.slf4j.event.Level.DEBUG
        }
    }

    mods {
        register(modId) {
            sourceSet(sourceSets.main.get())
        }
    }
}

obfuscation {
    createRemappingConfiguration(localRuntime)
}

// Stonecutter links the active node to the shared root src/ directory and materialises the other nodes into
// build/generated/stonecutter. Overriding the source directories would hide the real sources from IDEA, so the
// plugin keeps ownership of them; only the data-generation output is added here.
sourceSets.main.get().resources.srcDir("src/generated/resources")

val stonecutterGenerate = tasks.named("stonecutterGenerate")


tasks.named<JavaCompile>("compileJava") {
    dependsOn(stonecutterGenerate)
}

tasks.named<ProcessResources>("processResources") {
    dependsOn(stonecutterGenerate)
}

dependencies {
    annotationProcessor("io.github.llamalad7:mixinextras-common:0.2.0-rc.2")
    runtimeOnly("io.github.llamalad7:mixinextras-common:0.2.0-rc.2")
    runtimeOnly("io.github.llamalad7:mixinextras-forge:0.2.0-rc.2")

    // other
    modImplementation("curse.maven:modern-ui-352491:6199942")
    modImplementation("curse.maven:jei-238222:6600311")
    modImplementation("curse.maven:ibe-editor-256822:4994072")

    // tetra stack
    modImplementation("curse.maven:mutil-351914:$mutilFile")
    modImplementation("curse.maven:geckolib-388172:$geckolibFile")
    modImplementation("curse.maven:tetra-289712:$tetraFile")

    if (withTmo) {
        // jarJar needs coordinates, so the bundled TMO jar is resolved through the flatDir repo.
        modImplementation(tmoCoordinate)
        jarJar(tmoCoordinate)
    }

    if (withEhp) {
        // ExtraHoloPage is a client-only mod: it is needed at compile time and in the client run, but it must never
        // reach a dedicated server classpath (it touches client-only classes while constructing its mod instance).
        // ExtraHoloPage is a client-only mod: it must be present at compile time and in client runs, and it must
        // stay out of dedicated server runs (it touches client-only classes while constructing its mod instance).
        // Server runs therefore use -Pwith_ehp=false; the flag also removes it from compilation.
        modImplementation("curse.maven:extraholopage-1301292:$ehpFile")
    }

    annotationProcessor("org.spongepowered:mixin:0.8.5:processor")
}

mixin {
    add(sourceSets.main.get(), "$modId.refmap.json")
    config("$modId.mixins.json")
}

// The node suffix already identifies the artifact, so Gradle must not append the version again.
tasks.withType<AbstractArchiveTask>().configureEach {
    archiveVersion.set("")
}

tasks.named<Jar>("jar") {
    manifest.attributes("MixinConfigs" to "$modId.mixins.json")
}

val generateModMetadata = tasks.register<ProcessResources>("generateModMetadata") {
    val replaceProperties = mapOf(
        "minecraft_version" to targetMinecraftVersion,
        "minecraft_version_range" to minecraftVersionRange,
        "forge_version" to forgeVersion,
        "forge_version_range" to forgeVersionRange,
        "loader_version_range" to loaderVersionRange,
        "mod_id" to modId,
        "mod_name" to modName,
        "mod_license" to modLicense,
        "mod_version" to fullVersion,
        "mod_authors" to modAuthors,
        "mod_description" to modDescription,
    )
    inputs.properties(replaceProperties)
    // Node subprojects live in versions/<node>/, so the shared template directory must be resolved from the root project.
    from(rootProject.layout.projectDirectory.dir("src/main/templates"))
    into(layout.buildDirectory.dir("generated/sources/modMetadata"))
    expand(replaceProperties)
}

sourceSets.main.get().resources.srcDir(generateModMetadata)
legacyForge.ideSyncTask(generateModMetadata)

publishing {
    publications {
        register<MavenPublication>("mavenJava") {
            from(components["java"])
        }
    }
    repositories {
        maven { url = uri(project.layout.projectDirectory.dir("repo")) }
    }
}


tasks.withType<JavaCompile>().configureEach {
    options.encoding = "UTF-8"
}

tasks.named("createMinecraftArtifacts") {
    dependsOn("stonecutterGenerate")
}

tasks.register<Copy>("buildAndCollect") {
    group = "build"
    description = "Builds reobfuscated jars and copies them to root build/libs/{mod version}/"
    val reobfJar = tasks.named("reobfJar")
    dependsOn(reobfJar)
    from(reobfJar.map { it.outputs.files })
    // Both nodes share one folder keyed only by the mod's own version.
    into(rootProject.layout.buildDirectory.dir("libs/$modVersion"))
}

idea {
    module {
        isDownloadSources = true
        isDownloadJavadoc = true
    }
}
