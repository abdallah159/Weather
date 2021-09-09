package com.app.weather.utils

import com.app.weather.db.Database

object Singleton {
    //Default lat and lon for London, UK
    var latitude = 51.509865
    var longitude = -0.118092
    var city = "London, UK"
    var appDatabase: Database? = null
    var instance: Singleton? = null
        get() {
            if (field == null) {
                synchronized(Singleton::class.java) {
                    if (field == null) {
                        field = Singleton
                    }
                }
            }
            return field
        }
        private set
}