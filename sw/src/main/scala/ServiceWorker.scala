package default

import org.scalajs.dom.console
import org.scalajs.dom.experimental.serviceworkers.ServiceWorkerGlobalScope._
import org.scalajs.dom.experimental.Fetch
import org.scalajs.dom.experimental.Response

import scala.scalajs.js.Array
import scala.scalajs.js.Promise
import scala.concurrent.ExecutionContext.Implicits.global
import org.scalajs.dom.raw.MessagePort

import scala.scalajs.js.JSConverters.*
import io.circe.syntax.EncoderOps


object ServiceWorker {

  def init(): Unit =

    var index: TorrentIndex | Null = null

    self.oninstall = (event) =>
      console.log("Install event")
      val url = "https://raw.githubusercontent.com/TorrentDam/torrents/master/index/index.json"
      val getIndex = 
        console.log("Download index")
        for
          response <- Fetch.fetch(url).toFuture
          body <- response.text.toFuture
        yield
          console.log("Download finished")
          console.log("Parse index")
          val entries = TorrentIndex.Entries.fromString(body).toTry.get
          index = TorrentIndex(entries)
          console.log("Parse finished")
      event.waitUntil(getIndex.toJSPromise)


    self.onactivate = (event) =>
      console.log("Activate event")
      event.waitUntil(self.clients.claim())

    self.onmessage = (event) =>
      console.log(s"Message: ${event.data}")
      if index != null then
        val results = index.asInstanceOf[TorrentIndex].search(event.data.toString)
        event.ports.asInstanceOf[Array[MessagePort]](0)
          .postMessage(results.asJson.noSpaces)
}
