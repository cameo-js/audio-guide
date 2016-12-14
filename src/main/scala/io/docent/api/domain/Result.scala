package io.docent.api.domain

import net.ruippeixotog.scalascraper.browser.JsoupBrowser
import net.ruippeixotog.scalascraper.scraper.ContentExtractors._
import net.ruippeixotog.scalascraper.dsl.DSL._
import org.json4s.jackson.JsonMethods._
import org.json4s.JsonDSL._

/**
 * Created by kjs8469 on 16. 12. 6..
 */
case class Result(title: String, description: String, url: String){
  def toJson = compact(render(
    ("title" -> this.title) ~
    ("description" -> this.description) ~
    ("url" -> this.url)
  ))
}
object Result{
  val browser = JsoupBrowser()
  def parse(body: String, url: String):Option[Result] = {
    val title =  getTitle(body)
    val description =  getDescription(body)
//    val url =  url
    Some(Result(title, description, url))
  }
  def getTitle(body: String): String = (browser.parseString(body) >> text("h1"))
  def getDescription(body: String): String = (browser.parseString(body) >> elementList("#mw-content-text p")).map(_ >> text("p")).mkString(" ")
  def getUrl(body: String): String = (browser.parseString(body) >> elementList("#mw-content-text p")).map(_ >> text("p")).mkString(" ")
}
