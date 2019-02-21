package forms

import scala.reflect.{ classTag, ClassTag }

import play.api.data._
import play.api.data.validation._
import play.api.data.Forms._

import forms.Constraints._

class TraitMapping[T] private (
    getClassTagOf: (T) => ClassTag[_ <: T],
    val taggedMappings: Seq[TaggedMapping[_ <: T]] = Seq.empty,
    val key: String = "",
    val constraints: Seq[Constraint[T]] = Seq.empty)(implicit tagT: ClassTag[T]) extends Mapping[T] {

  import TraitMapping._

  lazy val mappingsByTags: Map[ClassTag[_ <: T], TaggedMapping[_ <: T]] = taggedMappings.map(taggedMapping => taggedMapping.tag -> taggedMapping).toMap
  lazy val mappingsByStrings: Map[String, TaggedMapping[_ <: T]] = (mappingsByTags map { case (tag, map1) => (tag.toString, map1) }).toMap
  
  lazy val tagMapping = single(tagKey -> text).verifying(
    error = "tagError",
    constraint = mappingsByStrings.contains(_))

  def bind(data: Map[String, String]): Either[Seq[FormError], T] = {
    tagMapping.bind(data) match {
      case Left(seq) => Left(seq)
      case Right(tag) => {
        mappingsByStrings.get(tag) match {
          case None => Left(Seq(FormError(key = prefix + tagFieldName, message = "tagError", args = Seq(tag))))
          case Some(taggedMapping) => taggedMapping.bind(data) match {
            case Left(seq) => Left(seq)
            case Right(t) => {
              val seq = validationResultToSeqOfFormErrors(key, mergeValidators(toValidators(constraints))(t))
              if (seq.isEmpty) Right(t) else Left(seq)
            }
          }
        }
      }
    }
  }

  lazy val mappings: Seq[Mapping[_]] = taggedMappings.foldLeft[Seq[Mapping[_]]](Seq.empty)((seq, mapping) => seq ++ mapping.mappings) ++ tagMapping.mappings

  override def unbind(value: T): Map[String, String] = {
    val tag = getClassTagOf(value)
    val map = mappingsByTags.get(tag) match {
      case None => throw new Exception(s"Unknown type $tag extending ${tagT}")
      case Some(taggedMapping) => taggedMapping.unbind(value.asInstanceOf[taggedMapping.Type])
    }
    map + (tagKey -> tag.toString)
  }

  override def unbindAndValidate(value: T): (Map[String, String], Seq[FormError]) = {
    val data = unbind(value)
    val seq = bind(data) match {
      case Left(seq) => seq
      case Right(t) => Seq.empty
    }
    (data, seq)
  }

  override def verifying(constraints: play.api.data.validation.Constraint[T]*) = new TraitMapping[T](
      getClassTagOf,
      taggedMappings,
      key,
      this.constraints ++ constraints)

  override def withPrefix(prefix: String) = new TraitMapping[T](
      getClassTagOf,
      taggedMappings.map(_.withPrefix(prefix)),
      prefix + key,
      constraints)

  private lazy val prefix = key match {
    case "" => ""
    case s => s + "."
  }
  
  lazy val tagKey = prefix + tagFieldName

  def withCase[C <: T](implicit mappingC: Mapping[C], ctag: ClassTag[C]) = new TraitMapping[T](
      getClassTagOf,
      taggedMappings :+ new TaggedMapping(mappingC.withPrefix(ctag.toString)),
      key,
      constraints)
}

class TaggedMapping[T](mapping: Mapping[T])(implicit val tag: ClassTag[T]) extends Mapping[T] {

  type Type = T

  def bind(data: Map[String, String]): Either[Seq[FormError], T] = mapping.bind(data)

  val constraints = mapping.constraints

  val key = mapping.key

  val mappings = mapping.mappings

  def unbind(value: Type)(implicit dummy: ClassTag[Type]) = mapping.unbind(value.asInstanceOf[T])

  def unbind(value: T) = mapping.unbind(value)

  def unbindAndValidate(value: T) = mapping.unbindAndValidate(value)

  def verifying(constraints: play.api.data.validation.Constraint[T]*) = new TaggedMapping[T](mapping.verifying(constraints: _*))

  def withPrefix(prefix: String) = new TaggedMapping[T](mapping.withPrefix(prefix))
}

object TraitMapping {
  val tagFieldName = "_tag"
  
  def apply[T](getClassTagOf: (T) => ClassTag[_ <: T])(implicit tagT: ClassTag[T]) = new TraitMapping[T](getClassTagOf = getClassTagOf)
}