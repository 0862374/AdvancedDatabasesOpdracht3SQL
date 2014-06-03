/*
 * To change this license header, choose License Headers in Project Properties.
 * To change this template file, choose Tools | Templates
 * and open the template in the editor.
 */
package opdrachtdrie;

import java.sql.Connection;
import java.sql.ResultSet;
import java.sql.SQLException;
import java.text.ParseException;
import java.text.SimpleDateFormat;
import java.util.ArrayList;
import java.util.Date;
import java.util.List;
import java.util.Random;

/**
 *
 * @author AbsentCrisisX & Nabelz
 */
public class OpdrachtVierTweeTwee {

    // Maak een lijst aan om de gemiddelde duur per iteratie uiteindelijk te berekenen
    List<Long> duur = new ArrayList<Long>();

    public OpdrachtVierTweeTwee() {
        // aantal iteraties per gebruiker
        final int max = 600;

        // maak aantal threads (gebruikers) aan
        for (int t = 0; t < 2; t++) {
            Thread thread = new Thread() {
                public void run() {
                    // definieer de variabele voor de database
                    Database db = null;

                    // loop door elke iteratie heen
                    for (int i = 0; i < 600; i++) {
                        try {
                            // maak een connectie met de database
                            db = new Database();
                            db.conn.setAutoCommit(false);
                            db.conn.setTransactionIsolation(Connection.TRANSACTION_READ_UNCOMMITTED);
                            db.stmt = db.conn.createStatement(ResultSet.TYPE_SCROLL_INSENSITIVE, ResultSet.CONCUR_UPDATABLE);

                            // maak een variabele aan om het totaal aantal studenten in de database op te slaan
                            Integer count = 0;

                            // haal het aantal studenten in de database op
                            ResultSet rsLength = db.stmt.executeQuery("SELECT COUNT(*) FROM studenten");
                            while (rsLength.next()) {
                                count = rsLength.getInt("count");
                            }

                            // Kies een random student op een regel uit de database aan de hand van een willekeurig getal binnen 1 en het aantal studenten
                            int stud = getRandomNumber(1, count);

                            // haal alle studenten op
                            String selectStudentQuery = "SELECT * FROM studenten;";
                            ResultSet rs = db.stmt.executeQuery(selectStudentQuery);

                            // ga naar de regel van de geselecteerde student en haal zijn/haar voor- en achternaam op
                            rs.absolute(stud);
                            String voornaam = rs.getString("voornaam");
                            String achternaam = rs.getString("achternaam");

                            // start de timer
                            long beginTijd = System.currentTimeMillis();

                            // haal de student weer op uit de database aan de hand van zijn/haar voor- en achternaam
                            String selectPerson = "SELECT * FROM studenten WHERE voornaam = '" + voornaam + "' AND achternaam = '" + achternaam + "' LIMIT 1;";
                            ResultSet person = db.stmt.executeQuery(selectPerson);

                            // ga naar de regel waar deze student staat (standaard eerste regel)
                            person.next();

                            // haal alle modules op die deze student volgt/gevolgd heeft.
                            String selectModules = "SELECT m.naam FROM modules AS m LEFT JOIN modulesklassen AS mk ON m.modulecode = mk.modulecode LEFT JOIN klassen AS k ON mk.klasid = k.klasid LEFT JOIN studentenklassen AS sk ON k.klasid = sk.klasid WHERE sk.studentnummer = '" + person.getString("studentnummer") + "';";
                            ResultSet modules = db.stmt.executeQuery(selectModules);

                            // haal de naam van elke module op en schrijf deze naar de console
                            while (modules.next()) {
                                String moduleNaam = modules.getString("naam");
                                System.out.println(moduleNaam);
                            }

                            // stop de timer
                            long eindTijd = System.currentTimeMillis();

                            // voeg de uitvoer tijd toe aan de lijst
                            duur.add((eindTijd - beginTijd));

                        } catch (SQLException e) {
                            e.printStackTrace();

                            // draai alle code terug als er een sqlfout is
                            try {
                                db.conn.rollback();
                            } catch (SQLException e1) {
                                // TODO Auto-generated catch block
                                e1.printStackTrace();
                            }
                        } finally {
                            // probeer de database connectie te sluiten

                            try {
                                db.conn.close();
                            } catch (SQLException e) {
                                // TODO Auto-generated catch block
                                e.printStackTrace();
                            }

                        }
                    }

                    // schrijf de gemiddelde tijd van alle iteraties van deze gebruiker naar de console
                    System.out.println("Average time: " + getAverage() + "MS");
                }
            };
            thread.start();
        }

    }

    // methode om een willekeurig nummer te kiezen in een range
    public int getRandomNumber(int min, int max) {
        return (int) Math.floor(Math.random() * (max - min + 1)) + min;
    }

    // bepaal het gemiddelde van de duur van de iteraties per gebruiker
    public long getAverage() {
        long sum = 0;
        for (int i = 0; i < duur.size(); i++) {
            sum += duur.get(i).longValue();
        }
        return sum / duur.size();
    }

}
