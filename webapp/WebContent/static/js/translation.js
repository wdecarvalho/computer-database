$(document).ready(function() {

    var locale = document.cookie.split("LOCALE=")[1];
    var pathFile = document.getElementById("translation").getAttribute("data-path")+"static/i18n/";
    jQuery.i18n.properties(
      {
        name:'Messages',
        path: pathFile,
        mode:'both',
        language:locale,
        async:true,
        callback: function()
          {
            $('#nomObligatoire').text(jQuery.i18n.prop('name_required'));
          }
      }
    );
});
