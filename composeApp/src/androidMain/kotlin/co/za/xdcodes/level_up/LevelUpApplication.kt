package co.za.xdcodes.level_up

import android.app.Application
import co.za.xdcodes.level_up.di.initKoin
import org.koin.android.ext.koin.androidContext

class LevelUpApplication : Application() {

    override fun onCreate() {
        super.onCreate()
        initKoin {
            androidContext(this@LevelUpApplication)
        }
    }
}