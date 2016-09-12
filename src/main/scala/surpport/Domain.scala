package surpport

/**
 * Created by kjs8469 on 16. 9. 12..
 */
trait Web

case class Google(content: String, path: String) extends Web
case class Wikipedia(content: String, path: String) extends Web
