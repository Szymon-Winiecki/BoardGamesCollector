package com.example.boardgamecollector

import BGGDataDownloader
import BGGXMLParser
import DownloadFileRequest
import Game
import GamesDBHandler
import android.app.AlertDialog
import android.content.Context
import android.content.DialogInterface
import android.content.Intent
import androidx.appcompat.app.AppCompatActivity
import android.os.Bundle
import android.util.Log
import android.widget.Toast
import com.example.boardgamecollector.databinding.ActivitySyncBinding
import java.io.File
import java.util.*
import kotlin.math.exp

class SyncActivity : AppCompatActivity() {

    private lateinit var binding : ActivitySyncBinding

    private var gamesThumbnailsToLoad = 0
    private var expansionsThumbnailsToLoad = 0

    private var parsedGames = mutableListOf<Game>()
    private var parsedGamesMap = hashMapOf<Int, Game>()

    private var parsedExpansions = mutableListOf<Game>()
    private var parsedExpansionsMap = hashMapOf<Int, Game>()

    private var gamesSynchronized = false
    private var expansionsSynchronized = false

    private var elementsToDownload : Int = 0
    private var elementsDownloaded : Int = 0

    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        binding = ActivitySyncBinding.inflate(layoutInflater)
        setContentView(binding.root)

        val config = Config(this);
        binding.lastSyncDate.text = config.lastSync.toString()

        binding.syncButton.setOnClickListener(){
            syncGames()
        }

        binding.backButton.setOnClickListener(){
            back()
        }
    }

    fun back(){
        val homeActivity = Intent(this, HomeActivity::class.java)
        startActivity(homeActivity)
    }

    fun syncGames(){
        val conf = Config(this)

        val lastSync = conf.lastSync
        val currDate = Date()
        val h = (currDate.time - lastSync.time) / 3600000

        conf.lastSync = currDate

        if(h < 24){
            SyncDialog("Na pewno chcesz zsynchronizować?","Ostatnia synchronizacja miała miejsce mniej niż 24 godziny temu, czy na pewno chcesz ponownie zsynchronizować dane?", this, "tak", "nie", true)
        }
        else{
            downloadGames()
        }
    }

    fun downloadGames(){
        val conf = Config(this)
        val username = conf.username

        var gamesRequest = DownloadFileRequest(DownloadFileRequest.getNextId(), "https://boardgamegeek.com/xmlapi2/collection?username=$username&stats=1&excludesubtype=boardgameexpansion", "games.xml")
        var expansionsRequest = DownloadFileRequest(DownloadFileRequest.getNextId(), "https://boardgamegeek.com/xmlapi2/collection?username=$username&stats=1&subtype=boardgameexpansion", "expansions.xml")
        val gamesDownloader = BGGDataDownloader(this, ::parseGames)
        val expansionsDownloader = BGGDataDownloader(this, ::parseExpansions)
        gamesDownloader.execute(gamesRequest)
        expansionsDownloader.execute(expansionsRequest)

        elementsToDownload = 2
        binding.progress.text = "pobrane $elementsDownloaded/$elementsToDownload"
        binding.syncStatus.text = "Synchronizowanie...\nporoszę czekać"
    }

    fun SyncDialog(title : String,msg: String, context: Context?, positiveButtonText: String, negativeButtonText: String, isCancellable: Boolean) {
        context?.let { context ->
            val builder =
                AlertDialog.Builder(context)
            builder.setTitle(title)
            builder.setMessage(msg)
            builder.setCancelable(isCancellable)
            builder.setPositiveButton(positiveButtonText) { dialogInterface: DialogInterface?, i: Int ->
                downloadGames()
                dialogInterface?.dismiss()
            }
            builder.setNegativeButton(negativeButtonText)
            { dialogInterface: DialogInterface?, i: Int ->
                dialogInterface?.dismiss()
            }
            val alertDialog = builder.create()
            alertDialog.show()
        }
    }

    fun parseGames(result : DownloadFileRequest){
        elementsDownloaded++

        if (result.absolutepath.isEmpty() || result.absolutepath[0] == '!'){
            Toast.makeText(this, "Synchronizacja nie powiodła się, spróbuj ponownie później.", Toast.LENGTH_LONG)
            binding.syncStatus.text = "Błąd synchronizacji"
            return
        }

        val parser = BGGXMLParser()
        parsedGames = parser.parseGamesList(result.absolutepath)

        for(game in parsedGames){
            gamesSynchronized = false
            parsedGamesMap[game.BGGId] = game

            if(game.thumbnailUrl == null){
                //TODO: add default thumbnail
            }
            else{
                elementsToDownload++
                gamesThumbnailsToLoad++
                val d = BGGDataDownloader(this, ::handleThumbnail)
                var fi = DownloadFileRequest(game.BGGId, game.thumbnailUrl!!, "${game.BGGId}_thumb.jpg")
                d.execute(fi)
            }
            binding.progress.text = "pobrane $elementsDownloaded/$elementsToDownload"
        }
    }

    fun parseExpansions(result : DownloadFileRequest){
        elementsDownloaded++

        if (result.absolutepath.isEmpty() || result.absolutepath[0] == '!'){
            Toast.makeText(this, "Synchronizacja nie powiodła się, spróbuj ponownie później.", Toast.LENGTH_LONG)
            binding.syncStatus.text = "Błąd synchronizacji"
            return
        }

        val parser = BGGXMLParser()
        parsedExpansions = parser.parseGamesList(result.absolutepath)

        for(game in parsedExpansions){
            expansionsSynchronized = false
            parsedExpansionsMap[game.BGGId] = game

            if(game.thumbnailUrl == null){
                //TODO: add default thumbnail
            }
            else{
                elementsToDownload++
                expansionsThumbnailsToLoad++
                val d = BGGDataDownloader(this, ::handleThumbnail)
                var fi = DownloadFileRequest(game.BGGId, game.thumbnailUrl!!, "${game.BGGId}_thumb.jpg")
                d.execute(fi)
            }
            binding.progress.text = "pobrane $elementsDownloaded/$elementsToDownload"
        }
    }

    fun handleThumbnail(result : DownloadFileRequest){
        if(parsedGamesMap.containsKey(result.id)){
            try{
                parsedGamesMap[result.id]?.thumbnail = File(result.absolutepath)
            }
            catch (e : Exception){
                //TODO assign default thumbnail
            }
            if(--gamesThumbnailsToLoad == 0){
                gamesSynchronized = true;
                syncWithDatabase(false, parsedGames, true, true, true)

                if(expansionsThumbnailsToLoad == 0 && !expansionsSynchronized){ //in case there is no single expansion in user's library
                    expansionsSynchronized = true
                    syncWithDatabase(true, parsedExpansions, true, true, true)
                }
            }
        }
        else if (parsedExpansionsMap.containsKey(result.id)){
            try{
                parsedExpansionsMap[result.id]?.thumbnail = File(result.absolutepath)
            }
            catch (e : Exception){
                //TODO assign default thumbnail
            }
            if(--expansionsThumbnailsToLoad == 0){
                expansionsSynchronized = true
                syncWithDatabase(true, parsedExpansions, true, true, true)

                if(gamesThumbnailsToLoad == 0 && !gamesSynchronized){ //in case there is no single game in user's library
                    gamesSynchronized = true
                    syncWithDatabase(false, parsedGames, true, true, true)
                }
            }
        }

        elementsDownloaded++
        binding.progress.text = "pobrane $elementsDownloaded/$elementsToDownload"
    }

    fun syncWithDatabase(extensions : Boolean, xmlGames: MutableList<Game>, add: Boolean, replace : Boolean, remove : Boolean){
        var dbHandler = GamesDBHandler(this, null, null, 1)

        var dbGames: MutableList<Game>

        if (extensions){
            dbGames = dbHandler.getExpansions()
        }
        else{
            dbGames = dbHandler.getGames()
        }

        var newGames = mutableListOf<Game>()
        var removedGames = mutableListOf<Game>()
        var existingGames = mutableListOf<Game>()

        var used = Array<Boolean>(dbGames.size) { false }

        for(xmlGame in xmlGames){
            var ex = false
            for(i in 0..dbGames.size-1){
                if(xmlGame.BGGId == dbGames[i].BGGId){
                    used[i] = true
                    ex = true
                    existingGames.add(xmlGame)
                    break
                }
            }
            if(!ex){
                newGames.add(xmlGame)
            }
        }

        for(i in 0..dbGames.size-1){
            if(!used[i]){
                removedGames.add(dbGames[i])
            }
        }

        if(remove){
            for(removed in removedGames){
                if(extensions){
                    dbHandler.deleteExpansion(removed.BGGId)
                }
                else{
                    dbHandler.deleteGame(removed.BGGId)
                }
            }
        }

        if(replace){
            for(existing in existingGames){
                if(extensions){
                    dbHandler.deleteExpansion(existing.BGGId)
                    dbHandler.addExpansion(existing)
                }
                else{
                    dbHandler.deleteGame(existing.BGGId)
                    dbHandler.addGame(existing)
                }
            }
        }

        if(add){
            for(new in newGames){
                if(extensions){
                    dbHandler.addExpansion(new)
                }
                else{
                    dbHandler.addGame(new)
                }
            }
        }

        if(gamesSynchronized && expansionsSynchronized){
            binding.syncStatus.text = "Gotowe"
            val config = Config(this);
            binding.lastSyncDate.text = config.lastSync.toString()
            Toast.makeText(this, "Synchronizacja zakończona", Toast.LENGTH_SHORT)
        }
    }
}