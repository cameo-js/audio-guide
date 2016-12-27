package io.docent.api.domain

import net.ruippeixotog.scalascraper.browser.JsoupBrowser
import net.ruippeixotog.scalascraper.model.Element

import scala.collection.mutable.ListBuffer
//import net.ruippeixotog.scalascraper.browser.JsoupBrowser.JsoupElement
import net.ruippeixotog.scalascraper.scraper.ContentExtractors._
import net.ruippeixotog.scalascraper.dsl.DSL._
import org.json4s.jackson.JsonMethods._
import org.json4s.JsonDSL._

/**
 * Created by kjs8469 on 16. 12. 6..
 */
case class Content(paragraph: String, description: String)
case class Result(title: String, contents: List[Content], url: String){
  def toJson = compact(render(
    ("title" -> this.title) ~
    ("contents" -> this.contents.map{ content =>
      ("paragraph" -> content.paragraph) ~
      ("description" -> content.description)
    }) ~
    ("url" -> this.url)
  ))
}
object Result{
  val browser = JsoupBrowser()
  def parse(body: String, url: String):Option[Result] = {
    val title =  getTitle(body)
    val contents =  getContents(body)
    Some(Result(title, contents, url))
  }
  def getTitle(body: String): String = (browser.parseString(body) >> text("h1"))
  def getDescription(body: String): String = (browser.parseString(body) >> elementList("#mw-content-text p")).map(_ >> text("p")).mkString(" ")
  def getContents(body: String): List[Content] = {
    val resultBuffer:ListBuffer[Content] = ListBuffer()
    var listBuffer:ListBuffer[String] = ListBuffer()
    var stringBuffer = "summary"
    (browser.parseString(body) >> elementList("#mw-content-text > *")).filter(isValidTag).foreach{ element =>
      if (isH3Tag(element)) {
        resultBuffer += Content(stringBuffer, listBuffer.toList.mkString(" "))
        stringBuffer = stripInvalidString(element.text)
        listBuffer = ListBuffer()
      }else if (isPTag(element)) {
        listBuffer += element.text
      }
    }
    resultBuffer += Content(stringBuffer, listBuffer.toList.mkString(" "))
    resultBuffer.toList
  }
  def getUrl(body: String): String = (browser.parseString(body) >> elementList("#mw-content-text p")).map(_ >> text("p")).mkString(" ")

  def isValidTag: Element => Boolean = element => isH3Tag(element) || isPTag(element)
  def isH3Tag: Element => Boolean  = element => element.tagName.toLowerCase.equals("h3")
  def isPTag: Element => Boolean = element => element.tagName.toLowerCase.equals("p")
  def stripInvalidString(source: String): String = source.replace("[편집]","")
}
