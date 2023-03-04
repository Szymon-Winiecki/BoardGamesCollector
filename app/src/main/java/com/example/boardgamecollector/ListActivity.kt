package com.example.boardgamecollector

import Game
import GamesDBHandler
import android.graphics.BitmapFactory
import android.os.Bundle
import android.widget.ImageView
import android.widget.TableLayout
import android.widget.TableRow
import android.widget.TextView
import androidx.appcompat.app.AppCompatActivity
import com.example.boardgamecollector.databinding.ActivityListBinding
import java.lang.Integer.min


class ListActivity : AppCompatActivity() {

    private lateinit var binding : ActivityListBinding

    private lateinit var list : MutableList<Game>
    private var areExpansions : Boolean = false

    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        binding = ActivityListBinding.inflate(layoutInflater)
        setContentView(binding.root)

        areExpansions = intent.getBooleanExtra("areExpansions", false)

        getList(areExpansions)
        displayList()
    }

    fun getList(expansions : Boolean){
        val dbHandler = GamesDBHandler(this, null, null, 1)
        if(expansions){
            list = dbHandler.getExpansions()
        }
        else{
            list = dbHandler.getGames()
        }
    }

    fun displayList(){
        var table = binding.list

        var row = TableRow(this)
        row.layoutParams = TableLayout.LayoutParams(TableRow.LayoutParams.MATCH_PARENT, TableRow.LayoutParams.MATCH_PARENT);

        var lp = TextView(this)
        var thumbnail = TextView(this)
        var name = TextView(this)
        var year = TextView(this)
        var rank = TextView(this)


        lp.layoutParams = TableRow.LayoutParams(TableRow.LayoutParams.MATCH_PARENT, TableRow.LayoutParams.MATCH_PARENT, 0.1f);
        thumbnail.layoutParams = TableRow.LayoutParams(TableRow.LayoutParams.MATCH_PARENT, TableRow.LayoutParams.MATCH_PARENT, 0.3f);
        name.layoutParams = TableRow.LayoutParams(300, TableRow.LayoutParams.MATCH_PARENT, 0.3f);
        year.layoutParams = TableRow.LayoutParams(TableRow.LayoutParams.MATCH_PARENT, TableRow.LayoutParams.MATCH_PARENT, 0.15f);
        rank.layoutParams = TableRow.LayoutParams(TableRow.LayoutParams.MATCH_PARENT, TableRow.LayoutParams.MATCH_PARENT, 0.15f);

        lp.text = "lp"
        thumbnail.text = "miniatura"
        name.text = "nazwa"
        year.text = "premiera"
        rank.text = "ranking"

        row.addView(lp)
        row.addView(thumbnail);
        row.addView(name);
        row.addView(year);

        if(!areExpansions){
            row.addView(rank);
        }

        table.addView(row);


        for(i in 0..list.size-1){
            displayElement(list[i], i+1)
        }
    }

    fun displayElement(game : Game, i : Int){
        var table = binding.list

        var row = TableRow(this)
        row.layoutParams = TableLayout.LayoutParams(TableRow.LayoutParams.MATCH_PARENT, TableRow.LayoutParams.MATCH_PARENT);

        var lp = TextView(this)
        var thumbnail = ImageView(this)
        var name = TextView(this)
        var year = TextView(this)
        var rank = TextView(this)

        lp.layoutParams = TableRow.LayoutParams(TableRow.LayoutParams.MATCH_PARENT, TableRow.LayoutParams.MATCH_PARENT, 0.1f);
        thumbnail.layoutParams = TableRow.LayoutParams(TableRow.LayoutParams.MATCH_PARENT, TableRow.LayoutParams.MATCH_PARENT, 0.3f);
        name.layoutParams = TableRow.LayoutParams(300, TableRow.LayoutParams.MATCH_PARENT, 0.3f);
        year.layoutParams = TableRow.LayoutParams(TableRow.LayoutParams.MATCH_PARENT, TableRow.LayoutParams.MATCH_PARENT, 0.15f);
        rank.layoutParams = TableRow.LayoutParams(TableRow.LayoutParams.MATCH_PARENT, TableRow.LayoutParams.MATCH_PARENT, 0.15f);

        lp.text = i.toString()
        name.text = game.originalTitle
        year.text = game.releaseYear.toString()
        rank.text = game.currentRankPosition.toString()

        if(game.thumbnail != null){
            thumbnail.setImageBitmap(BitmapFactory.decodeFile(game.thumbnail!!.absolutePath!!))
        }

        row.addView(lp)
        row.addView(thumbnail);
        row.addView(name);
        row.addView(year);
        if(!areExpansions){
            row.addView(rank);
        }

        table.addView(row);
    }
}