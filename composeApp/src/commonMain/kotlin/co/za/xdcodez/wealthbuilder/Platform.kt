package co.za.xdcodez.wealthbuilder

interface Platform {
    val name: String
}

expect fun getPlatform(): Platform