@* addresses Template File *@
@(messageOpt: Option[String], urlToRedirectOpt: Option[String])

$().ready(function () {

	@messageOpt match {
		case Some(msg) => {alert("@msg");}
		case None => { }
	}

	var selected = $('input[name=addressType]:checked').val();
	
	function handleAddressType() {
		$('div[name=newAddress]').hide();
		$('div[name=newAddress][id=' + selected + ']').show();
	}
	
	handleAddressType();
	
	$(document).on('click', 'input[name=addressType]', function () {
	    if (selected != $(this).val()) {
	    	selected = $(this).val();
	    	handleAddressType();
	    }
	});
	
	@urlToRedirectOpt match {
		case Some(url) => {location.href = "@url";}
		case None => { }
	}
});