package json

import akka.annotation.InternalApi
import akka.stream.scaladsl.Framing.FramingException
import akka.util.ByteString

import scala.annotation.switch

/**
  * @author Daniel Nesbitt
  */
class StashParser(maximumObjectLength: Int = Int.MaxValue) {

  import StashTokens._

  private var buffer: ByteString = ByteString.empty

  private var changeId: String = ""
  private var foundChangeId: Boolean = false
  private var inChangeIdField: Boolean = false
  private var inChangeIdValue: Boolean = false

  private var pos = 0
  private var trimFront = 0
  private var depth = 0

  private var charsInObject = 0
  private var completedObject = false
  private var inStringExpression = false
  private var isStartOfEscapeSequence = false
  private var lastInput = 0.toByte

  def offer(input: ByteString): Unit =
    buffer ++= input

  def isEmpty: Boolean = buffer.isEmpty

  def poll(): Option[ByteString] = {
    val foundObject = seekObject()
    if (!foundObject) None
    else
      (pos: @switch) match {
        case -1 | 0 ⇒ None
        case _ ⇒
          val (emit, buf) = buffer.splitAt(pos)
          buffer = buf.compact
          pos = 0

          val tf = trimFront
          trimFront = 0

          if (tf == 0) Some(emit)
          else {
            val trimmed = emit.drop(tf)
            if (trimmed.isEmpty) None
            else Some(trimmed)
          }
      }
  }

  private def seekObject(): Boolean = {
    completedObject = false
    val bufSize = buffer.size
    while (pos != -1 && (pos < bufSize && pos < maximumObjectLength) && !completedObject)
      proceed(buffer(pos))

    completedObject
  }

  private def proceed(input: Byte): Unit = {
    if (outsideContainer) {
      if (isWhitespace(input)) {
        pos += 1
        trimFront += 1
      } else if (input == CurlyBraceStart) {
        depth += 1
        pos += 1
      } else {
        throw new FramingException(s"Invalid JSON encountered at position [$pos] of [$buffer]")
      }
    } else if (outsideStash) {
      // We're inside the stash container, but not in a stash
      if (!foundChangeId) {
        // We have not yet found the full change id
        //        if (in)
      }
    }

    if (input == SquareBraceStart && outsideContainer) {
    } else if (input == Comma && outsideStash) {
      pos += 1
      trimFront += 1
    } else if (input == Backslash) {
      if (lastInput == Backslash & isStartOfEscapeSequence) isStartOfEscapeSequence = false
      else isStartOfEscapeSequence = true
      pos += 1
    } else if (input == DoubleQuote) {
      if (!isStartOfEscapeSequence) inStringExpression = !inStringExpression
      isStartOfEscapeSequence = false
      pos += 1
    } else if (input == CurlyBraceStart && !inStringExpression) {
      isStartOfEscapeSequence = false
      depth += 1
      pos += 1
    } else if (input == CurlyBraceEnd && !inStringExpression) {
      isStartOfEscapeSequence = false
      depth -= 1
      pos += 1
      if (depth == 0) {
        charsInObject = 0
        completedObject = true
        // TODO Clear last curly from stream
      }
    } else if (isWhitespace(input) && !inStringExpression) {
      pos += 1
      if (depth == 0) trimFront += 1
    } else if (insideObject) {
      isStartOfEscapeSequence = false
      pos += 1
    } else {
      throw new FramingException(s"Invalid JSON encountered at position [$pos] of [$buffer]")
    }

    lastInput = input
  }

  @inline private final def insideObject: Boolean =
    !outsideStash

  @inline private final def outsideStash: Boolean =
    depth == 1

  @inline private final def outsideContainer: Boolean =
    depth == 0

}

object StashTokens {

  final val SquareBraceStart = '['.toByte
  final val SquareBraceEnd = ']'.toByte
  final val CurlyBraceStart = '{'.toByte
  final val CurlyBraceEnd = '}'.toByte
  final val DoubleQuote = '"'.toByte
  final val Backslash = '\\'.toByte
  final val Comma = ','.toByte

  final val LineBreak = '\n'.toByte
  final val LineBreak2 = '\r'.toByte
  final val Tab = '\t'.toByte
  final val Space = ' '.toByte

  final val Whitespace = Set(LineBreak, LineBreak2, Tab, Space)

  def isWhitespace(input: Byte): Boolean =
    Whitespace.contains(input)

}
