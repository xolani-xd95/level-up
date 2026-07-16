package co.za.xdcodez.wealthbuilder

import androidx.compose.ui.window.ComposeUIViewController
import co.za.xdcodez.wealthbuilder.di.initKoin

fun MainViewController() = ComposeUIViewController(
    configure = {
        initKoin()
    }
) {
   App()
}