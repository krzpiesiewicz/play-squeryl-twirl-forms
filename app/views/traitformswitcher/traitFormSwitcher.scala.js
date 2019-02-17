@* traitFormSwitcher Template File *@
@import views.traitformswitcher.TraitFormSwitcherHelper._

@(switcherId: String, prefixKey: String = "")

$().ready(function () {

	var selected = $('input[type=radio][class=@radioClass(switcherId, prefixKey)]:checked').val();
	
	function handleDivsSwitching() {
		let all = $('div[name=@divName(switcherId, prefixKey)]');
		all.hide();
		all.find('*').prop('disabled', true);
		
		let that = $('div[name=@divName(switcherId, prefixKey)][id=' + selected + ']');
		that.find('*').prop('disabled', false);
		that.show();
	}
	
	handleDivsSwitching();
	
	$(document).on('click', 'input[type=radio][class=@radioClass(switcherId, prefixKey)]', function () {
	    if (selected != $(this).val()) {
	    	selected = $(this).val();	
	    	$('input[type=radio][class=@radioClass(switcherId, prefixKey)][value!=' + selected + ']').prop('checked', false);
	    	handleDivsSwitching();
	    }
	});
});