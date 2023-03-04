import android.content.ContentValues
import android.content.Context
import android.database.sqlite.SQLiteDatabase
import android.database.sqlite.SQLiteOpenHelper

class GamesDBHandler(context: Context, name: String?, factory: SQLiteDatabase.CursorFactory?, version: Int)
    : SQLiteOpenHelper(context, DATABASE_NAME, factory, DATABASE_VERSION) {

    companion object{
        private val DATABASE_VERSION = 5
        private val DATABASE_NAME = "gamesDB.db"

        //table GAMES
        private val TABLE_GAMES_NAME = "games"
        private val GAMES_BGGID_COLUMN = "bgg_id"
        private val GAMES_ORGTITLE_COLUMN = "original_name"
        private val GAMES_RELEASE_COLUMN = "release_year"
        private val GAMES_RANK_COLUMN = "current_rank"
        private val GAMES_THUMBNAIL_COLUMN = "thumbnail"

        //table EXPANSIONS
        private val TABLE_EXPANSIONS_NAME = "expansions"
        private val EXPANSIONS_BGGID_COLUMN = "bgg_id"
        private val EXPANSIONS_ORGTITLE_COLUMN = "original_name"
        private val EXPANSIONS_RELEASE_COLUMN = "release_year"
        private val EXPANSIONS_THUMBNAIL_COLUMN = "thumbnail"


        //table RANKING_ARCHIVE
        private val TABLE_ARCH_NAME = "ranking_archive"
        private val ARCH_BGGID_COLUMN = "game_id"
        private val ARCH_DATE_COLUMN = "date"
        private val ARCH_RANK_COLUMN = "rank"
    }

    override fun onCreate(db: SQLiteDatabase) {
        val create_games_table  = (
                "CREATE TABLE " + TABLE_GAMES_NAME + "(" +
                        GAMES_BGGID_COLUMN + " INTEGER PRIMARY KEY," +
                        GAMES_ORGTITLE_COLUMN + " TEXT," +
                        GAMES_RELEASE_COLUMN + " INTEGER," +
                        GAMES_RANK_COLUMN + " INTEGER," +
                        GAMES_THUMBNAIL_COLUMN + " TEXT)" )

        db.execSQL(create_games_table)

        val create_expansions_table  = (
                "CREATE TABLE " + TABLE_EXPANSIONS_NAME + "(" +
                        EXPANSIONS_BGGID_COLUMN + " INTEGER PRIMARY KEY," +
                        EXPANSIONS_ORGTITLE_COLUMN + " TEXT," +
                        EXPANSIONS_RELEASE_COLUMN + " INTEGER," +
                        EXPANSIONS_THUMBNAIL_COLUMN + " TEXT)" )

        db.execSQL(create_expansions_table)

        val create_archive_table  = (
                "CREATE TABLE " + TABLE_ARCH_NAME + "(" +
                        ARCH_BGGID_COLUMN + " INTEGER," +
                        ARCH_DATE_COLUMN + " TEXT," +
                        ARCH_RANK_COLUMN + " INTEGER)")

        db.execSQL(create_archive_table)
    }

    override fun onUpgrade(db: SQLiteDatabase, oldVersion: Int, newVersion: Int) {
        db.execSQL("DROP TABLE IF EXISTS $TABLE_GAMES_NAME")
        db.execSQL("DROP TABLE IF EXISTS $TABLE_EXPANSIONS_NAME")
        db.execSQL("DROP TABLE IF EXISTS $TABLE_ARCH_NAME")
        onCreate(db)
    }

    //GAMES

    fun addGame(game: Game){
        val values = ContentValues()
        values.put(GAMES_BGGID_COLUMN, game.BGGId)
        values.put(GAMES_ORGTITLE_COLUMN, game.originalTitle)
        values.put(GAMES_RELEASE_COLUMN, game.releaseYear)
        values.put(GAMES_RANK_COLUMN, game.currentRankPosition)
        if (game.thumbnail != null)
            values.put(GAMES_THUMBNAIL_COLUMN, (game.thumbnail!!).absolutePath)

        val db = this.writableDatabase
        db.insert(TABLE_GAMES_NAME, null, values)
        db.close()
    }

    fun deleteGame(bggId : Int) : Int{
        val db = this.writableDatabase
        var deletedCount = db.delete(TABLE_GAMES_NAME, "$GAMES_BGGID_COLUMN = ?", arrayOf(bggId.toString()))
        db.close()
        return deletedCount
    }

    fun getGame(bggId : Int) : Game?{
        val query = "SELECT $GAMES_BGGID_COLUMN, $GAMES_ORGTITLE_COLUMN, $GAMES_RELEASE_COLUMN, $GAMES_RANK_COLUMN, $GAMES_THUMBNAIL_COLUMN FROM $TABLE_GAMES_NAME WHERE $GAMES_BGGID_COLUMN = $bggId"
        val db = this.writableDatabase
        val cursor = db.rawQuery(query, null)

        if(cursor.moveToFirst()){
            val id = cursor.getString(0)
            val name = cursor.getString(1)
            val year = cursor.getString(2)
            val rank = cursor.getString(3)
            val thumbnail = cursor.getString(4)

            return Game(id.toInt(), name, year.toInt(), rank.toInt(), thumbnail, true)
        }
        else{
            db.close()
            return null
        }
    }

    fun getGames() : MutableList<Game>{
        var games = mutableListOf<Game>()

        val query = "SELECT $GAMES_BGGID_COLUMN, $GAMES_ORGTITLE_COLUMN, $GAMES_RELEASE_COLUMN, $GAMES_RANK_COLUMN, $GAMES_THUMBNAIL_COLUMN FROM $TABLE_GAMES_NAME"
        val db = this.writableDatabase
        val cursor = db.rawQuery(query, null)

        while (cursor.moveToNext()){
            val id = cursor.getString(0)
            val name = cursor.getString(1)
            val year = cursor.getString(2)
            val rank = cursor.getString(3)
            val thumbnail = cursor.getString(4)

            games.add(Game(id.toInt(), name, year.toInt(), rank.toInt(), thumbnail, true))
        }
        db.close()
        return games
    }

    fun getGamesCount() : Int{
        val query = "SELECT COUNT(*) FROM $TABLE_GAMES_NAME"
        val db = this.writableDatabase
        val cursor = db.rawQuery(query, null)

        var count = 0
        if(cursor.moveToFirst()){
            count = cursor.getInt(0)
        }
        db.close()

        return count
    }

    //EXPANSIONS

    fun addExpansion(game: Game){
        val values = ContentValues()
        values.put(EXPANSIONS_BGGID_COLUMN, game.BGGId)
        values.put(EXPANSIONS_ORGTITLE_COLUMN, game.originalTitle)
        values.put(EXPANSIONS_RELEASE_COLUMN, game.releaseYear)
        if (game.thumbnail != null)
            values.put(EXPANSIONS_THUMBNAIL_COLUMN, (game.thumbnail!!).absolutePath)

        val db = this.writableDatabase
        db.insert(TABLE_EXPANSIONS_NAME, null, values)
        db.close()
    }

    fun deleteExpansion(bggId : Int) : Int{
        val db = this.writableDatabase
        var deletedCount = db.delete(TABLE_EXPANSIONS_NAME, "$EXPANSIONS_BGGID_COLUMN = ?", arrayOf(bggId.toString()))
        db.close()
        return deletedCount
    }

    fun getExpansion(bggId : Int) : Game?{
        val query = "SELECT $EXPANSIONS_BGGID_COLUMN, $EXPANSIONS_ORGTITLE_COLUMN, $EXPANSIONS_RELEASE_COLUMN, $EXPANSIONS_THUMBNAIL_COLUMN FROM $TABLE_EXPANSIONS_NAME WHERE $EXPANSIONS_BGGID_COLUMN = $bggId"
        val db = this.writableDatabase
        val cursor = db.rawQuery(query, null)

        if(cursor.moveToFirst()){
            val id = cursor.getString(0)
            val name = cursor.getString(1)
            val year = cursor.getString(2)
            val thumbnail = cursor.getString(3)

            return Game(id.toInt(), name, year.toInt(), thumbnail, true)
        }
        else{
            db.close()
            return null
        }
    }

    fun getExpansions() : MutableList<Game>{
        var games = mutableListOf<Game>()

        val query = "SELECT $EXPANSIONS_BGGID_COLUMN, $EXPANSIONS_ORGTITLE_COLUMN, $EXPANSIONS_RELEASE_COLUMN, $EXPANSIONS_THUMBNAIL_COLUMN FROM $TABLE_EXPANSIONS_NAME"
        val db = this.writableDatabase
        val cursor = db.rawQuery(query, null)

        while (cursor.moveToNext()){
            val id = cursor.getString(0)
            val name = cursor.getString(1)
            val year = cursor.getString(2)
            val thumbnail = cursor.getString(3)

            games.add(Game(id.toInt(), name, year.toInt(), thumbnail, true))
        }
        db.close()
        return games
    }

    fun getExpansionsCount() : Int{
        val query = "SELECT COUNT(*) FROM $TABLE_EXPANSIONS_NAME"
        val db = this.writableDatabase
        val cursor = db.rawQuery(query, null)

        var count = 0
        if(cursor.moveToFirst()){
            count = cursor.getInt(0)
        }
        db.close()

        return count
    }
}