package io.docent.api.domain

import org.scalatest.{AsyncFlatSpec, FlatSpec}
import io.docent.api.surpport.UrlSurpport._
import scala.concurrent.ExecutionContext.Implicits.global

/**
 * Created by kjs8469 on 16. 12. 6..
 */
class WikipediaSpec extends AsyncFlatSpec {
  val wikipedia = Wikipedia("ko.wikipedia.org")

  "result" should "have some string value" in {
    val result = wikipedia.search(encodeWikiPath("https://ko.wikipedia.org/wiki/빈센트_반_고흐"))
    result map {body =>
      assert(body.get.title.equals("빈센트 반 고흐"))
      assert(body.get.contents.size == 10)
    }
  }
}
