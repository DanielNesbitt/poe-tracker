package dnesbitt.json

/**
  * @author Daniel Nesbitt.
  */

object PoeEntities {
  import io.circe.Decoder
  import io.circe.generic.semiauto.deriveDecoder

  implicit val decodeStashes: Decoder[Stashes] = deriveDecoder
  implicit val decodeStash: Decoder[Stash] = deriveDecoder
  implicit val decodeItem: Decoder[Item] = deriveDecoder
  implicit val decodeSocket: Decoder[Socket] = deriveDecoder
  implicit val decodeProperty: Decoder[Property] = deriveDecoder
}

case class Stashes(next_change_id: String, stashes: List[Stash])

case class Stash
(
  accountName: Option[String],
  lastCharacterName: Option[String],
  id: String,
  stash: Option[String],
  stashType: String,
  items: List[Item]
)

case class Item
(
  id: String,
  name: String,
  note: Option[String],
  league: String,
  typeLine: String,
  identified: Boolean,
  corrupted: Boolean,
  properties: Option[List[Property]],
  sockets: Option[List[Socket]],
  explicitMods: Option[List[String]],
  implicitMods: Option[List[String]],
  enchantMods: Option[List[String]],
  craftedMods: Option[List[String]],
  utilityMods: Option[List[String]],
  x: Int,
  y: Int,
  inventoryId: String
)

case class Socket(groupId: Option[String], attr: String)

case class Property(name: String)