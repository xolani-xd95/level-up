package co.za.xdcodes.level_up

import androidx.compose.ui.window.ComposeUIViewController
import co.za.xdcodes.level_up.di.initKoin

fun MainViewController() = ComposeUIViewController(
    configure = {
        initKoin()
    }
) {
    App()
}