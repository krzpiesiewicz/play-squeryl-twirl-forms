package views

object FormRenderersHelper {
  
  class DummyImplicit

  object DummyImplicit {
    implicit def dummyImplicit: DummyImplicit = new DummyImplicit
  }
}