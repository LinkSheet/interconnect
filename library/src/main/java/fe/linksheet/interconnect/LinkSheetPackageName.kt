package fe.linksheet.interconnect

object LinkSheetPackageName {
    const val BASE_PACKAGE_NAME = "fe.linksheet"

    fun getSpecificType(flavor: BuildFlavor, type: BuildType): String {
        return apply(apply(BASE_PACKAGE_NAME, flavor.suffix), type.suffix)
    }

    private fun apply(packageName: String, suffix: String?): String {
        return if (suffix != null) "${packageName}.${suffix}" else packageName
    }

    enum class BuildType(val suffix: String? = null) {
        Release, Debug("debug"), Nightly("nightly")
    }

    enum class BuildFlavor(val suffix: String? = null) {
        Pro("pro"), Foss("foss"), Legacy
    }

    val POSSIBLE_PACKAGE_NAMES = BuildFlavor.values().flatMap { flavor ->
        val flavorPackageName = apply(BASE_PACKAGE_NAME, flavor.suffix)
        BuildType.values().map { type -> apply(flavorPackageName, type.suffix) }
    }
}