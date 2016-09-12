package surpport

import java.net.{URLEncoder, URL}

/**
 * Created by kjs8469 on 16. 9. 12..
 */
object UrlSurpport {
  val UTF8 = "UTF8"

  def getGooglePath(query: String) = "/search?q=" + encodeUTF8(query) + "%20wikipedia.org"
  def getPath(url: String) = new URL(url).getPath
  def encodeUTF8(url: String) = URLEncoder.encode(url,UTF8)
  def encodeWikiPath(url: String) = "/wiki/"+encodeUTF8(getPath(url).replace("/wiki/",""))
}
