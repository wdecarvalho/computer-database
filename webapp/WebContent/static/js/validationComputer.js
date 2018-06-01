$(document).ready(function() {

	/* On controle la submission de l'ajout d'un computer */
	$("#addAComputer").submit(function(e) {
		if ($("#computerName").val().trim() == "") {
			e.preventDefault();
			$.fn.AddMessageErrorInput();
		}
	});

	/* On controle la submission de la modification d'un computer */
	$("#EditComputer").submit(function(e) {
		if ($("#computerName").val().trim() == "") {
			e.preventDefault();
			$.fn.AddMessageErrorInput();
		}
	});

	/* Pour le formulaire d'ajout et de suppression on verifie que le nom du computer
	est non vide est superieur a 1. */
	$("#computerName").focusout(function() {
		if ($(this).val().trim() == "") {
			$.fn.AddMessageErrorInput();
		} else {
			$("#divComputerName").removeClass("has-error")
			$("#nomObligatoire").addClass("hidden")
			$("#divComputerName").addClass("has-success")
		}
	});

	/* On ajout sur les inputs les messages d'erreur (validation) */
	$(function ( $ ){
		$.fn.AddMessageErrorInput = function(){
			$("#divComputerName").removeClass("has-success")
			$("#nomObligatoire").removeClass("hidden")
			$("#divComputerName").addClass("has-error")
			return this;
		};
	}( jQuery ));

	/* Pour le formulaire d'ajoout et de suppresion on verifie que la date
	introduced < discontinued. */
	var introducedDate = $("#introduced").val();
	if (introduced != null) {
		$("#discontinued").attr("min", introducedDate);
	}
	$("#introduced").change(function() {
		$("#discontinued").attr("min", $(this).val());
	});
});
