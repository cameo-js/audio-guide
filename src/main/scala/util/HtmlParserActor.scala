package util

import java.net.URLEncoder

import akka.actor.{ActorRef, Actor}
import net.ruippeixotog.scalascraper.browser.JsoupBrowser
import net.ruippeixotog.scalascraper.model.Element
import net.ruippeixotog.scalascraper.scraper.ContentExtractors._
import net.ruippeixotog.scalascraper.model.{Element, Document}
import net.ruippeixotog.scalascraper.scraper.ContentExtractors._
import net.ruippeixotog.scalascraper.dsl.DSL._
import net.ruippeixotog.scalascraper.dsl.DSL.Extract._
import net.ruippeixotog.scalascraper.dsl.DSL.Parse._
import surpport.{Wikipedia, Google}
import surpport.UrlSurpport.{getPath, encodeWikiPath}

/**
 * Created by kjs8469 on 16. 9. 11..
 */
class HtmlParserActor(actorRef: ActorRef) extends Actor {
  val browser = JsoupBrowser()

  override def receive = {
    case google: Google => {
      actorRef ! Wikipedia("",encodeWikiPath(getTitles(google.content)(0)))
    }
    case wikipedia: Wikipedia => {
      println(getContent(wikipedia.content))
    }
  }

  def getTitle(doc: String): String = browser.parseString(doc) >> text("title")
  def getTitles(doc: String): List[String] = (browser.parseString(doc) >> elementList(".s")).map(parseTitleFromElement)
  def parseTitleFromElement(elem: Element): String = elem >> text("cite")
  def getContent(doc: String): String = (browser.parseString(doc) >> elementList("#mw-content-text p")).map(_ >> text("p")).mkString(" ")
}
