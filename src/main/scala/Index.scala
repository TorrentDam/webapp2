package default

import io.circe.parser
import io.circe.Decoder
import io.circe.generic.semiauto.deriveDecoder
import io.circe.Error
import com.github.lavrov.bittorrent.InfoHash


trait TorrentIndex {
  import TorrentIndex.Results

  def search(text: String): Results
}

object TorrentIndex {

  def apply(entries: Entries): TorrentIndex = {
    new TorrentIndex {
      def search(text: String): Results = {
        val words = text.toLowerCase.split(' ')
        Results(
          text,
          entries
            .entries
            .view
            .map {
                case (searchField, entry) =>
                val score = words.map(word => if (searchField.contains(word)) word.length else 0).sum
                (entry, score)
            }
            .filter(_._2 > 0)
            .toList
            .sortBy(_._2)(Ordering[Int].reverse)
            .take(100)
            .map(_._1)
        )
      }
    }
  }

  case class Entries(entries: List[(String, Entry)] = List.empty)
  object Entries {
    def fromString(str: String): Either[Error, Entries] =
      parser
      .parse(str)
      .flatMap(json => json.as[List[Entry]])
      .map(list =>
        Entries(list.map(it => (it.name, it)))
      )
  }

  case class Entry(name: String, infoHash: InfoHash, size: Long, ext: List[String])
  object Entry {
    given Decoder[InfoHash] = Decoder.decodeString.emap {
      case InfoHash.fromString(infoHash) => Right(infoHash)
      case _ => Left("Invalid infohash")
    }
    given Decoder[Entry] = deriveDecoder
  }

  case class Results(query: String, entries: List[Entry])
}
