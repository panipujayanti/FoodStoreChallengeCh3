package com.napa.foodstorechallengech3

import android.app.Application
import com.napa.foodstorechallengech3.data.local.database.AppDatabase

class App : Application() {
    override fun onCreate() {
        super.onCreate()
        AppDatabase.getInstance(this)
    }
}
