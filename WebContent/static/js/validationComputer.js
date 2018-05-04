$(document).ready(function() {
	$("#addAComputer").submit(function(e) {
		if ($("#computerName").val() == "") {
			e.preventDefault();
			$("#divComputerName").removeClass("has-success")
			$("#divComputerName").addClass("has-error")
			$("#nomObligatoire").removeClass("hidden")
		}
	});
	
	$("#EditComputer").submit(function(e) {
		if ($("#computerName").val() == "") {
			e.preventDefault();
			$("#divComputerName").removeClass("has-success")
			$("#divComputerName").addClass("has-error")
			$("#nomObligatoire").removeClass("hidden")
		}
	});

	$("#computerName").focusout(function() {
		if ($(this).val() == "") {
			$("#divComputerName").removeClass("has-success")
			$("#nomObligatoire").removeClass("hidden")
			$("#divComputerName").addClass("has-error")
		} else {
			$("#divComputerName").removeClass("has-error")
			$("#nomObligatoire").addClass("hidden")
			$("#divComputerName").addClass("has-success")
		}
	}

	)
});