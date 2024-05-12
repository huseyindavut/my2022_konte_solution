$(function () {
    hentAllePakker()
});

function validerLid(lid) {
    if (lid) {
        $("#feilLid").html("")
        return true;
    }else {
        $("#feilLid").html("Flly ut LID");
        return false;
    }
}

function validerEier(eier) {
    let regex = /^[A-ZÆØÅa-zæøå]{2,50}$/;

    if(regex.test(eier)) {
        $("#feilEier").html("");
        return true;
    }else {
        $("#feilEier").html("Bruk kun små og ...");
        return false;
    }
}

function validerVekt(vekt) {
    if(vekt) {
        $("#feilVekt").html("");
        return true;
    } else {
        $("#feilVekt").html("Fyll ut vekt");
        return false;
    }
}

function validerVolum(volum) {
    if (volum) {
        $("#feilVolum").html("");
        return true;
    }else{
        $("#feilVolum").html("Volum");
        return false;
    }
}

function validerPakke(pakke){
    lidOK = validerLid(pakke.LID);
    eierOK = validerEier(pakke.eier);
    vektOK = validerVekt(pakke.vekt);
    volumOK = validerVolum(pakke.volum);

    if (lidOK && eierOK && vektOK && volumOK) {
        return true;
    }else{

        return false;
    }
}

function registrerPakke() {

    let pakke = {
        "LID":$("#lid").val(),
        "eier":$("#eier").val(),
        "vekt":$("#vekt").val(),
        "volum":$("#volum").val(),
    };

    if (validerPakke(pakke)) {
        $.post("lagrepakke", pakke, function () {
            $("#melding").html("Pakke ble lagret");

            $("#navn").val("");
            $("#eier").val("");
            $("#vekt").html("");
            $("#volum").val("");


        });
    }else{
        $("#melding").html("Fyll ut alle felter....");
    }
}

function hentAllePakker() {
    $.get("/hentallepakker", function (pakker) {
        let utskrift=   "<table><tr><th>LID</th><th>Eier</th><th>Vekt (kg)</th><th>Volum (m^3)</th></tr>";

        for(let pakke of pakker) {
            utskrift +=
                "<tr><td>" + pakke.lid + "</td><td>" + pakke.eier + "</td><td>" + pakke.vekt + "</td><td>" + pakke.volum + "</td></tr>";
        }

        utskrift += "</table>";

        $("#pakkeliste").html(utskrift);
    });
}