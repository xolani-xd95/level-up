package co.za.xdcodes.level_up

interface Platform {
    val name: String
}

expect fun getPlatform(): Platform