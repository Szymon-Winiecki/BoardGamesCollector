import android.net.Uri
import java.io.File

class Game {
    var originalTitle : String = ""
    var releaseYear : Int = 0
    var BGGId : Int = 0
    var isExtension = false
    var currentRankPosition : Int = 0
    var thumbnailUrl : String? = null
    var thumbnail : File? = null

    constructor(){

    }

    constructor(id : Int){
        this.BGGId = id
    }

    constructor(BGGId : Int, originalTitle : String, releaseYear : Int, currentRankPosition : Int, thumbnail : String?, thumbIsFile : Boolean){
        this.originalTitle = originalTitle
        this.releaseYear = releaseYear
        this.BGGId = BGGId
        this.currentRankPosition = currentRankPosition

        if(thumbnail == null){
            return
        }

        if(thumbIsFile){
            this.thumbnail = File(thumbnail)
        }
        else{
            this.thumbnailUrl = thumbnail
        }

    }

    constructor(BGGId : Int, originalTitle : String, releaseYear : Int, thumbnail : String?, thumbIsFile : Boolean)
            : this(BGGId, originalTitle, releaseYear, 0, thumbnail, thumbIsFile){
        this.isExtension = true
    }
}