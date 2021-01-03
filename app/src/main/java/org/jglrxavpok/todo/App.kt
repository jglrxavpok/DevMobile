package org.jglrxavpok.todo

import android.app.Application
import org.jglrxavpok.todo.network.Api
import org.koin.core.context.startKoin

class App: Application() {

    override fun onCreate() {
        super.onCreate()
        Api.Instance = Api(this)

        startKoin {
            // use Koin logger
            printLogger()
            // declare modules
            modules(toDoModule)
        }
    }
}