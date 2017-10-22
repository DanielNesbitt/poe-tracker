package dnesbitt.util

/**
  * @author Daniel Nesbitt
  */
object PrintingUtils {

  def humanReadableByteCount(bytes: Long): String = {
    val unit = 1024
    if (bytes < unit) return bytes + " B"
    val exp = (Math.log(bytes) / Math.log(unit)).toInt
    val v = bytes / Math.pow(unit, exp)
    val pre = "KMGTPE".charAt(exp - 1)
    f"$v%.2f is $pre%sB"
  }

}
