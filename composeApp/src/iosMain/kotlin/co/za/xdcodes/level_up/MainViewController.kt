package co.za.xdcodes.level_up

import androidx.compose.ui.window.ComposeUIViewController
import co.za.xdcodes.level_up.di.initKoin
import com.trading.journal.App

fun MainViewController() = ComposeUIViewController(
    configure = {
        initKoin()
    }
) {
//    App()
    co.za.xdcodes.level_up.App()
}