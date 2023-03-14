package com.realmissues

import android.app.Application
import io.realm.Realm

class TheApplication:Application() {
    override fun onCreate() {
        super.onCreate()
        Realm.init(this)
    }
}