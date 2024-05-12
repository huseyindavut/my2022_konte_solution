function logginn() {
    bruker = {
        "navn": $("#navn").val(),
        "passord": $("#passord").val(),
    }

        $.post("logginn", bruker, function () {
            $("#melding").html("Inlogging forsæk utført");
            $("#navn").val("");
            $("#passord").val("");


        });

}