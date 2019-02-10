package models

import com.iterable.play.utils.CaseClassMapping
import play.api.data.FormError
import forms.FormValidators._
import forms.MappingWithValidator

case class Location(latitude: Double, longitude: Double)

object Location {

  val validator: FormValidator[Location] = merge(Seq(
    (prefix, loc) => {
      if (!(loc.latitude >= -90 && loc.latitude <= 90))
        Seq(FormError(prefix + "latitude", "latitudeError"))
      else
        Seq.empty
    },
    (prefix, loc) => {
      if (!(loc.longitude >= -180 && loc.longitude < 180))
        Seq(FormError(prefix + "longitude", "longitudeError"))
      else
        Seq.empty
    }))

  implicit val mapping = new MappingWithValidator(CaseClassMapping.mapping[Location], validator)
}