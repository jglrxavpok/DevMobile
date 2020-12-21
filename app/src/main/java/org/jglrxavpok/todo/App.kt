package org.jglrxavpok.todo

import android.app.Application
import org.jglrxavpok.todo.network.Api

class App: Application() {

    override fun onCreate() {
        super.onCreate()
        Api.Instance = Api(this)
    }
}