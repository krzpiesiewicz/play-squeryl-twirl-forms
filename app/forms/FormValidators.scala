package forms

import play.api.data.FormError

object FormValidators {
  
  type FormValidator[T] = (String, T) => Seq[FormError]
  
  implicit def formValidator[T](fun: (String, T) => Option[FormError]): FormValidator[T] = (prefix, t) => fun(prefix, t) match {
    case Some(error) => Seq(error)
    case None => Seq.empty
  }

  def merge[T](formValidators: Seq[FormValidator[T]]): FormValidator[T] = { (prefix, t) =>
    formValidators.foldLeft[Seq[FormError]](Seq.empty)((seqAcc, validator) => {
      seqAcc ++ validator(prefix, t)
    })
  }
}