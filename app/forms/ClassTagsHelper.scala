package forms

import scala.reflect.ClassTag
import play.api.data.Field

object ClassTagsHelper {
  
  def saveString(s: String): String = s.replace('.', '_')
  
  def saveTagString[T](tag: ClassTag[T]): String = saveString(tag.toString)
  
  def classTagFieldName(prefixKey: String): String = saveString(prefixKey) + "_classTag"
  
  def classTagField[T](tag: ClassTag[T], prefixKey: String): Field =
    new Field(null, classTagFieldName(prefixKey), Seq.empty, None, Seq.empty, Some(tag.toString))
}