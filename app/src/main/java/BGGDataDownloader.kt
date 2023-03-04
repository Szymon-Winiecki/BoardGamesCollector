import android.os.AsyncTask
import androidx.appcompat.app.AppCompatActivity
import java.io.File
import java.io.FileNotFoundException
import java.io.FileOutputStream
import java.io.IOException
import java.net.MalformedURLException
import java.net.URL
import javax.net.ssl.HttpsURLConnection

class DownloadFileRequest{
    companion object{
        var _nextId : Int = 0

        fun getNextId() : Int{
            return _nextId++
        }
    }

    var id : Int
    var url : String
    var filename : String
    var absolutepath : String = ""

    constructor(id : Int, url : String, filename : String){
        this.id = id
        this.url = url
        this.filename = filename
    }
}

@Suppress("DEPRECATION")
class BGGDataDownloader(activity : AppCompatActivity, callback : (result : DownloadFileRequest) -> Unit) : AsyncTask<DownloadFileRequest, Int, DownloadFileRequest>() {

var activity : AppCompatActivity
var callback : (result : DownloadFileRequest) -> Unit

    init{
        this.activity = activity
        this.callback = callback
    }

    override fun onPreExecute() {
        super.onPreExecute()
    }

    override fun onPostExecute(result: DownloadFileRequest) {
        super.onPostExecute(result)
        callback(result);
    }

    override fun doInBackground(vararg downloadInfo: DownloadFileRequest) : DownloadFileRequest {
        var absolutePath = "!";
        try{

            val url = URL(downloadInfo[0].url)
            val connection = url.openConnection() as HttpsURLConnection
            connection.connect()

            val isStream = url.openStream()

            if(connection.responseCode != 200){
                absolutePath = "!error"
                downloadInfo[0].absolutepath = absolutePath
                return downloadInfo[0]
            }

            val directory = File(activity.filesDir.toString() + "/XML")
            if(!directory.exists()){
                directory.mkdir()
            }

            absolutePath = directory.absolutePath + "/" + downloadInfo[0].filename
            val fos = FileOutputStream(directory.toString() + "/" + downloadInfo[0].filename)

            val data = ByteArray(1024)
            var count = isStream.read(data)
            while (count != -1){
                fos.write(data, 0, count)
                count = isStream.read(data)
            }
        } catch (e : MalformedURLException){
            absolutePath = "!MalformedURLException"
        } catch (e : FileNotFoundException){
            absolutePath = "!FileNotFoundException"
        } catch (e : IOException){
            absolutePath = "!IOException"
        }
        downloadInfo[0].absolutepath = absolutePath
        return downloadInfo[0]
    }

}