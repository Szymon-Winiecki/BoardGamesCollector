package com.example.boardgamecollector

import android.content.Context
import android.content.SharedPreferences
import java.util.*

class Config {
    private val CONFIG_NAME = "CONFIG"
    private val USERNAME_STRING = "USERNAME"
    private val LAST_SYNC_STRING = "LAST_SYNC"

    private var configSharedPreferences : SharedPreferences

    var username : String = ""
        get() {
            var usr = configSharedPreferences.getString(USERNAME_STRING, field)
            if(usr == null) field = ""
            else field = usr!!
            return field
        }
        set(value) {
            field = value
            with (configSharedPreferences.edit()) {
                putString(USERNAME_STRING, value)
                apply()
            }
        }

    var lastSync : Date = Date(0)
        get() {
            var usr = configSharedPreferences.getString(LAST_SYNC_STRING, field.toString())
            field = Date(usr)
            return field
        }
        set(value) {
            field = value
            with (configSharedPreferences.edit()) {
                putString(LAST_SYNC_STRING, value.toString())
                apply()
            }
        }

    constructor(context: Context){
        configSharedPreferences = context.getSharedPreferences(CONFIG_NAME, Context.MODE_PRIVATE)
    }
}