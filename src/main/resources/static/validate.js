(function() {
	'use strict';
	$('#recipeForm').submit(function(event) {
		var f = $(this);
		if (f[0].checkValidity() === false) {
			event.preventDefault();
			event.stopPropagation();
			f.addClass('was-validated');
		}
	});
})();