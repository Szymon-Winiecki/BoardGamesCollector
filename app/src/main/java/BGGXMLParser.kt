import android.util.Log
import org.w3c.dom.Document
import org.w3c.dom.Node
import org.w3c.dom.NodeList
import java.io.File
import java.io.FileNotFoundException
import java.lang.NumberFormatException
import javax.xml.parsers.DocumentBuilderFactory

class BGGXMLParser {

    fun getGameInfo(item : Node) : Game{
        val tagsList : NodeList = item.childNodes

        var isFromList = false
        var isExtension = false

        if(item.attributes.getNamedItem("type") != null){
            if (item.attributes.getNamedItem("type").nodeValue == "boardgameexpansion"){
                isExtension = true
            }
        }
        else if(item.attributes.getNamedItem("subtype") != null){
            if (item.attributes.getNamedItem("subtype").nodeValue == "boardgameexpansion"){
                isExtension = true
            }
        }

        var id : Int = 0
        if (item.attributes.getNamedItem("id") != null) {
            id = item.attributes.getNamedItem("id").nodeValue.toInt()
        }
        else {
            id = item.attributes.getNamedItem("objectid").nodeValue.toInt()
            isFromList = true
        }

        var name : String = ""
        var year : Int = 0
        var thumbnail : String? = null
        var rank : Int = 0
        for(i in 0..tagsList.length-1){
            val attr : Node = tagsList.item(i)
            if(attr.nodeName == "thumbnail"){
                thumbnail = attr.textContent
            }
            else if(attr.nodeName == "name"){
                if(isFromList)
                    name = attr.textContent
                else if(attr.attributes.getNamedItem("type").nodeValue == "primary")
                    name = attr.attributes.getNamedItem("value").nodeValue
            }
            else if(attr.nodeName == "yearpublished"){
                if(isFromList)
                    year = attr.textContent.toInt()
                else
                    year = attr.attributes.getNamedItem("value").nodeValue.toInt()
                }
            else if(!isExtension && (attr.nodeName == "statistics" || attr.nodeName == "stats")){
                val statistics : NodeList = attr.childNodes
                for(k in 0..statistics.length-1){
                    if(statistics.item(k).nodeName == "ratings" || statistics.item(k).nodeName == "rating"){
                        val ratings : NodeList = statistics.item(k).childNodes
                        for (l in 0..ratings.length-1){
                            if(ratings.item(l).nodeName == "ranks"){
                                val ranks : NodeList= ratings.item(l).childNodes
                                for(j in 0..ranks.length-1){
                                    val rankNode = ranks.item(j)
                                    if(rankNode.nodeType == Node.ELEMENT_NODE){
                                        if(rankNode.attributes.getNamedItem("type").nodeValue == "subtype" && rankNode.attributes.getNamedItem("name").nodeValue == "boardgame"){
                                            try {
                                                rank = rankNode.attributes.getNamedItem("value").nodeValue.toInt()
                                            }catch (e : NumberFormatException){
                                                rank = 0
                                            }

                                        }
                                    }
                                }
                            }
                        }
                    }
                }

            }
        }
        if(isExtension){
            return Game(id, name, year, thumbnail, false)
        }
        else{
            return Game(id, name, year, rank, thumbnail, false)
        }
    }

    fun parseGamesList(xmlPath : String) : MutableList<Game>{
        var games = mutableListOf<Game>()

        val file = File(xmlPath);

        if(file.exists()){
            val document : Document = DocumentBuilderFactory.newInstance().newDocumentBuilder().parse(file);
            document.documentElement.normalize()

            val items : NodeList = document.getElementsByTagName("item")

            for(i in 0..items.length-1){
                if(items.item(i).nodeType == Node.ELEMENT_NODE) {
                    games.add(getGameInfo(items.item(i)))
                }
            }
        }
        else{
            throw FileNotFoundException()
        }
        return games
    }
}