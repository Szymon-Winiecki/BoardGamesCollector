package com.example.boardgamecollector

import GamesDBHandler
import android.content.Intent
import androidx.appcompat.app.AppCompatActivity
import android.os.Bundle
import com.example.boardgamecollector.databinding.ActivityHomeBinding

class HomeActivity : AppCompatActivity() {

    private lateinit var binding : ActivityHomeBinding

    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        binding = ActivityHomeBinding.inflate(layoutInflater)
        setContentView(binding.root)

        var config = Config(this)
        if(config.username == ""){
            openConfig()
        }

        binding.usernameHome.text = config.username
        binding.lastSyncHome.text = config.lastSync.toString()

        val dbHandler = GamesDBHandler(this, null, null, 1)

        binding.ownedGamesHome.text = dbHandler.getGamesCount().toString()
        binding.ownedExpansionsHome.text = dbHandler.getExpansionsCount().toString()

        binding.syncHome.setOnClickListener(){
            openSync()
        }

        binding.configHome.setOnClickListener(){
            openConfig()
        }

        binding.showGamesHome.setOnClickListener(){
            openGamesList()
        }

        binding.showExpansionsHome.setOnClickListener(){
            openExpansionsLsit()
        }
    }

    fun openConfig(){
        val configActivity = Intent(this, ConfigActivity::class.java)
        startActivity(configActivity)
    }

    fun openSync(){
        val syncActivity = Intent(this, SyncActivity::class.java)
        startActivity(syncActivity)
    }

    fun openGamesList(){
        val listActivity = Intent(this, ListActivity::class.java)
        listActivity.putExtra("areExpansions", false)
        startActivity(listActivity)
    }

    fun openExpansionsLsit(){
        val listActivity = Intent(this, ListActivity::class.java)
        listActivity.putExtra("areExpansions", true)
        startActivity(listActivity)
    }
}