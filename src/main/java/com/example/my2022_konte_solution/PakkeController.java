package com.example.my2022_konte_solution;

import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.http.HttpStatus;
import org.springframework.jdbc.core.BeanPropertyRowMapper;
import org.springframework.jdbc.core.JdbcTemplate;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.PostMapping;
import org.springframework.web.bind.annotation.RestController;

import java.io.IOException;
import java.util.List;

@RestController
public class PakkeController {


    /*
     * Oppgave 4
     */
    @Autowired
    private JdbcTemplate db;

    /*
    @PostMapping("/lagrepakke")
    public boolean lagrePakke(Pakke pakke) {
        String sjekkLID = "SELECT LID FROM Lager WHERE LID=?";
        String sqlPakke = "INSERT INTO Pakke (LID,eier,vekt,volum) VALUES(?,?,?,?)";

        try {
            int LID = db.queryForObject(sjekkLID, Integer.class, pakke.getLID());
            db.update(sqlPakke, LID, pakke.getEier(), pakke.getVekt(), pakke.getVolum());
        } catch (Exception e) {
            return false;
        }

        return true;
    }
    */

    /*
     * Oppgave 5
     */
    Logger logger = LoggerFactory.getLogger(PakkeController.class);

    @PostMapping("/lagrepakke")
    public boolean lagrePakke(Pakke pakke, HttpServletResponse response) throws IOException {
        String sjekkLID = "SELECT LID FROM Lager WHERE LID=?";
        String sqlPakke = "INSERT INTO Pakke (LID,eier,vekt,volum) VALUES(?,?,?,?)";

        String regex = "[a-zæøåA-ZÆØÅ .\\-]{2,50}";
        boolean eierOK = pakke.getEier().matches(regex);
        boolean vektOK = false;
        if(pakke.getVekt() > 0)
            vektOK = true;
        boolean volumOK = false;
        if(pakke.getVolum() > 0)
            volumOK = true;

        if(eierOK & vektOK && volumOK) {

            try {
                int LID = db.queryForObject(sjekkLID, Integer.class, pakke.getLID());
                db.update(sqlPakke, LID, pakke.getEier(), pakke.getVekt(), pakke.getVolum());
            } catch (Exception e) {
                logger.error("Feil ved innlegging av ny pakke: " + e);
                response.sendError(HttpStatus.INTERNAL_SERVER_ERROR.value(),
                        "Det skjedde en feil ved innlegging av ny pakke i databasen. Prøv igjen om litt.");
            }

        } else {

            logger.error("Feil ved innlegging av ny pakke pga. inputvalidering.");
            response.sendError(HttpStatus.INTERNAL_SERVER_ERROR.value(),
                    "Det skjedde en feil ved innlegging av ny pakke. En eller flere felter er feil utfylt.");

        }

        return true;
    }

    /*
     * Oppgave 7
     */
    @Autowired
    HttpSession session;

    @PostMapping("/logginn")
    public boolean loggInn(Bruker bruker) {
        String sql = "SELECT * FROM Bruker WHERE Navn=?";

        Bruker dbBruker;
        try {
            dbBruker = db.queryForObject(sql,
                    BeanPropertyRowMapper.newInstance(Bruker.class), bruker.getNavn());
        } catch (Exception e) {
            return false;
        }

        String hashetPassord = dbBruker.getPassord();

        // kun for testing (passord er da ikke hashet)
        //if(hashetPassord.equals(bruker.getPassord())) {
        if(BCrypt.checkpw(bruker.getPassord(), hashetPassord)) {
            session.setAttribute("innlogget", bruker.getNavn());
            return true;
        }

        return false;
    }

    /*
     * Oppgave 8
     */
    @GetMapping("/hentallepakker")
    public List<Pakke> hentAllePakker(HttpServletResponse response) throws IOException {
        String sql = "SELECT * FROM Pakke";
        List<Pakke> allePakker = db.query(sql, new BeanPropertyRowMapper(Pakke.class));

        if(session.getAttribute("innlogget") == null) {
            response.sendError(HttpStatus.FORBIDDEN.value(),
                    "Du må først logge inn for å kunne liste alle pakker.");
        }

        return allePakker;
    }

    /*
     * Oppgave 9
     */
    @GetMapping("/stat")
    public String stat() {
        String sqlLager = "SELECT * FROM Lager";
        List<Lager> alleLagere = db.query(sqlLager, new BeanPropertyRowMapper(Lager.class));

        String oppsummering = "";

        for(Lager lager : alleLagere) {
            String sqlPakke = "SELECT * FROM Pakke WHERE LID=?";
            List<Pakke> allePakker = db.query(sqlPakke, new BeanPropertyRowMapper(Pakke.class), lager.getLID());

            int antallPakker = 0;
            double vekt = 0.0;
            double volum = 0.0;
            for (Pakke pakke : allePakker) {
                ++antallPakker;
                vekt += pakke.getVekt();
                volum += pakke.getVolum();
            }

            oppsummering += lager.getNavn() +
                    " innholder " + antallPakker +
                    " pakker med et totalvolum på " + volum +
                    " kubikkmeter og en totalvekt på " + vekt + " kg. ";
        }

        return oppsummering;
    }

    /*
     * Eksempelkode
     */
    /*
    @Autowired
    private JdbcTemplate db;

    // innsetting av et POJO av typen Data i db
    String sql = "INSERT INTO Data (Lengde,Type,Innhold) VALUES(?,?,?)";
    db.update(sql, data.getLengde(), data.getType(), data.getInnhold());

    // uthenting av et POJO av typen Data fra db
    String sql = "SELECT * FROM Data WHERE ID=?";
    Data data = db.queryForObject(sql,
            BeanPropertyRowMapper.newInstance(Data.class), id);

    // uthenting av alle POJO av typen Data fra db
    String sql = "SELECT * FROM Data";
    List<Data> alleData = db.query(sql, new BeanPropertyRowMapper(Data.class));

    // uthenting av et heltall fra db
    String sql = "SELECT Lengde FROM Data WHERE ID=?";
    int lengde = db.queryForObject(sql, Integer.class, id);

    Logger logger = LoggerFactory.getLogger(EnFlottController.class);

    // eksempel på en funksjon med logging og feilhåndtering som sender en feilrespons til klient
    public void lagreNoeData(Data data, HttpServletResponse response) throws IOException {
        if (!repo.lagreNoeData(data)) {
            logger.error("Feil ved lagring av data.");
            response.sendError(HttpStatus.INTERNAL_SERVER_ERROR.value(),
                    "Det skjedde en feil ved lagring av data. Prøv igjen om litt.");
        }
    }
    */
}

