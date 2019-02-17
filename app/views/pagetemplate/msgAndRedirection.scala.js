@* msgAndRedirection Template File *@
@(messageOpt: Option[String], urlToRedirectOpt: Option[String])

$().ready(function () {

	@messageOpt match {
		case Some(msg) => {alert("@msg");}
		case None => { }
	}
	
	@urlToRedirectOpt match {
		case Some(url) => {location.href = "@url";}
		case None => { }
	}
});