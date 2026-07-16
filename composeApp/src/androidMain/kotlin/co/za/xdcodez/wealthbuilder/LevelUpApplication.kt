package co.za.xdcodez.wealthbuilder

import android.app.Application
import co.za.xdcodez.wealthbuilder.di.initKoin
import org.koin.android.ext.koin.androidContext

class WealthBuilderApplication : Application() {

    override fun onCreate() {
        super.onCreate()
        initKoin {
            androidContext(this@WealthBuilderApplication)
        }
    }
}