package com.example.boardgamecollector

import android.content.Intent
import androidx.appcompat.app.AppCompatActivity
import android.os.Bundle
import android.widget.Toast
import com.example.boardgamecollector.databinding.ActivityConfigBinding

class ConfigActivity : AppCompatActivity() {

    private lateinit var binding: ActivityConfigBinding

    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        binding = ActivityConfigBinding.inflate(layoutInflater)
        setContentView(binding.root)

        var conf = Config(this);
        binding.username.setText(conf.username);
        binding.nextButton.setOnClickListener(){
            setUsername()
        }
    }

    fun setUsername(){
        val username = binding.username.text
        if(username.isEmpty()){
            Toast.makeText(this, "proszę podać nazwę użytkownika", Toast.LENGTH_SHORT)
            return
        }

        val config = Config(this)
        config.username = username.toString()

        val homeActivity = Intent(this, HomeActivity::class.java)
        startActivity(homeActivity)
    }
}