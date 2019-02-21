@* caseFormsSwitcher Template File *@
@import play.api.data.Form

@import views.caseformsswitcher.CaseFormsSwitcherHelper._

@(form: Form[_], prefixKey: String = "")

$().ready(function () {

	let radiosDivs = $('div[id=@switcherId(form, prefixKey)]');
	var selected = radiosDivs.find('input[type=radio]:checked').val();
	
	function handleDivsSwitching() {
		let all = $('div[name=@divName(form, prefixKey)]');
		all.hide();
		//all.prop('disabled', true);
		
		if (selected != null) {
			let that = $('div[name=@divName(form, prefixKey)][id=@divIdPrefix(form, prefixKey)' + selected.replace('.', '_') + ']');
			//that.prop('disabled', false);
			that.show();
		}
	}
	
	handleDivsSwitching();
	
	$(document).on('click', radiosDivs.find('input[type=radio]'), function () {
		let newVal = radiosDivs.find('input[type=radio]:checked').val();
	    if (selected != newVal) {
	    	selected = newVal;	
	    	handleDivsSwitching();
	    }
	});
});