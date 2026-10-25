plugins {
    id("dev.kikugie.stonecutter")
    id("net.neoforged.moddev.legacyforge") version "2.0.141" apply false
}

stonecutter.active("6.9.0")

// Optional mod compat flags become Stonecutter constants so that the same flags drive both the dependency block
// and the conditional code in the sources. They default to the per-node gradle.properties values.
stonecutter.parameters {
    constants["with_tmo"] = node.project.findProperty("with_tmo").toString().toBoolean()
    constants["with_ehp"] = node.project.findProperty("with_ehp").toString().toBoolean()
}
