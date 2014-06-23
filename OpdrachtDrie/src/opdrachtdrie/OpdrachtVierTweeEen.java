package opdrachtdrie;

import java.sql.Connection;
import java.sql.PreparedStatement;
import java.sql.ResultSet;
import java.sql.SQLException;
import java.text.ParseException;
import java.text.SimpleDateFormat;
import java.util.ArrayList;
import java.util.Date;
import java.util.List;
import java.util.Random;
import java.util.UUID;

public class OpdrachtVierTweeEen {

    // Maak een lijst aan om de gemiddelde duur per iteratie uiteindelijk te berekenen
    List<Long> duur = new ArrayList<Long>();

    public OpdrachtVierTweeEen() {

        // aantal iteraties per gebruiker
        final int MAX = 600;

        // maak het aantal threads (gebruikers) aan
        for (int t = 0; t < 3; t++) {
            Thread thread = new Thread() {
                public void run() {
                    // definieer de database variabele
                    Database db = null;
                    // loop door elke iteratie heen
                    for (int i = 0; i < MAX; i++) {
                        try {
                            // maak verbinding met de database
                            db = new Database();
                            db.conn.setAutoCommit(false);
                            db.conn.setTransactionIsolation(Connection.TRANSACTION_READ_COMMITTED);

                            // start de timer
                            long beginTijd = System.currentTimeMillis();

                            // maak een willekeurig studentnummer aan
                            int studnr = getRandomNumber(1000000, 9999999);

                            // voer de nieuwe student in de database in
                            String insertStudentQuery = "INSERT INTO studenten (studentnummer, voornaam, achternaam, geboortedatum, geslacht, straat, postcode, woonplaats, telefoonnummer) VALUES ("
                                    + studnr
                                    + ","
                                    + "'Student"
                                    + i
                                    + "',"
                                    + "'Achter"
                                    + i
                                    + "', '"
                                    + getRandomDate()
                                    + "', '"
                                    + getRandomGeslacht()
                                    + "', 'straatje "
                                    + i
                                    + "', '"
                                    + getRandomNumber(1000, 9999)
                                    + " AA', 'plaatsje"
                                    + i
                                    + "', '0"
                                    + getRandomNumber(111111111, 999999999) + "');";
                            db.stmt.executeUpdate(insertStudentQuery);

                            // definieer een nieuwe willekeurig voor het maken van een klas.
                            Random rn = new Random();

                            // als het volgende willekeurige nummer 10 is, maak dan een nieuwe klas aan.
                            switch (rn.nextInt(30)) {
                                case 10:
                                    String dateone = getRandomDate();
                                    String datetwo = getRandomDate();

                                    String insertKlasQuery = "INSERT INTO klassen (klasnaam, startdatum, einddatum) VALUES ('"
                                            + UUID.randomUUID().toString()
                                            + "', '"
                                            + getLowOrHighDate(dateone, datetwo, false)
                                            + "', '"
                                            + getLowOrHighDate(dateone, datetwo, true) + "');";
                                    db.stmt.executeUpdate(insertKlasQuery);
                                    break;
                            }

                            // haal alle klassen op uit de database
                            String selectKlasQuery = "SELECT count(*) as total FROM klassen;";
                            ResultSet rs = db.stmt.executeQuery(selectKlasQuery);

                            // kies de enige regel van het aantal klassen.
                            rs.next();
                            
                            // voer de student in een willekeurige klas in.
                            String insertStudentKlas = "INSERT INTO studentenklassen (klasid, studentnummer) VALUES ('"
                                    + (getRandomNumber(1, rs.getInt("total"))) + "', '" + studnr + "');";
                            System.out.println(insertStudentKlas);
                            db.stmt.executeUpdate(insertStudentKlas);

                            // stop de timer
                            long eindTijd = System.currentTimeMillis();

                            // als het volgende willekeurige getal 0 is, maak dan een nieuwe module aan
                            switch (rn.nextInt(30)) {
                                case 0:
                                    String dateone = getRandomDate();
                                    String datetwo = getRandomDate();
                                    final String MODULECODE = "module" + getRandomNumber(i, 1000000);

                                    String insertModuleQuery = "INSERT INTO modules (modulecode, naam, modulebeheerder, invoerdatum, einddatum) VALUES ('"
                                            + MODULECODE
                                            + "', 'Advanced Database "
                                            + i
                                            + "', 'ADVDOC', '"
                                            + getLowOrHighDate(dateone, datetwo, false)
                                            + "', '"
                                            + getLowOrHighDate(dateone, datetwo, true) + "');";
                                    System.out.println(insertModuleQuery);
                                    db.stmt.executeUpdate(insertModuleQuery);

                                    // bepaal welke klas er gekoppeld wordt aan een module
                                    selectKlasQuery = "SELECT * FROM klassen;";
                                    PreparedStatement preparedStatement = db.conn.prepareStatement(selectKlasQuery);
                                    rs = preparedStatement.executeQuery();

                                    while (rs.next()) {
                                        if (rn.nextInt(100) <= 15) {

                                            String insertModuleKlasQuery = "INSERT INTO modulesklassen (modulecode, klasid) VALUES ('"
                                                    + MODULECODE + "', " + rs.getInt("klasid") + ");";
                                            System.out.println(insertModuleKlasQuery);
                                            db.stmt.executeUpdate(insertModuleKlasQuery);

                                        }
                                    }

                                    break;
                            }
                            
                            // commit alle queries en acties
                            db.conn.commit();
                            
                            // voeg de tijd toe aan de lijst                            
                            duur.add((eindTijd - beginTijd));
                            
                            // probeer de gebruiker 100 milliseconden te laten wachten
                            try {
                                Thread.sleep(100);
                            } catch (InterruptedException e) {
                                // TODO Auto-generated catch block
                                e.printStackTrace();
                            }

                        } catch (SQLException e) {
                            e.printStackTrace();
                            
                            // probeer alle acties en queries terug te draaien
                            try {
                                db.conn.rollback();
                            } catch (Exception e1) {
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
                    // schrijf de gemiddelde uitvoertijd per iteratie naar de console
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

    // methode om een willekeurige methode op te halen
    public String getRandomDate() {
        Random rn = new Random();
        String day;
        String month;
        String date;

        day = Integer.toString(rn.nextInt(28));
        month = Integer.toString(rn.nextInt(12));

        if (day.length() < 2) {
            if (day.equalsIgnoreCase("0")) {
                day = "1";
            }
            day = "0" + day;
        }
        if (month.length() < 2) {
            if (month.equalsIgnoreCase("0")) {
                month = "1";
            }
            month = "0" + month;
        }

        date = getRandomNumber(1980, 2005) + "-" + month + "-" + day;

        return date;
    }

    // methode om een willekeurig geslacht op te halen
    public String getRandomGeslacht() {
        Random rn = new Random();

        switch (rn.nextInt(4)) {
            case 0:
                return "man";
            case 1:
                return "vrouw";
            case 2:
                return "onbepaald";
            case 3:
                return "onbekend";
            default:
                return "onbekend";
        }

    }

    // methode om de gemiddelde tijd te berekenen
    public long getAverage() {
        long sum = 0;
        for (int i = 0; i < duur.size(); i++) {
            sum += duur.get(i).longValue();
        }
        return sum / duur.size();
    }

    // controleer of de startdatum niet na de eind datum ligt.
    public String getLowOrHighDate(String dateone, String datetwo, boolean highestDate) {
        try {

            SimpleDateFormat sdf = new SimpleDateFormat("yyyy-MM-dd");
            Date date1 = sdf.parse(dateone);
            Date date2 = sdf.parse(datetwo);

            if (date1.compareTo(date2) > 0) {
                if (!highestDate) {
                    return datetwo;
                } else {
                    return dateone;

                }
            } else if (date1.compareTo(date2) < 0) {
                if (highestDate) {
                    return datetwo;
                } else {
                    return dateone;

                }
            } else if (date1.compareTo(date2) == 0) {
                return dateone;
            }

        } catch (ParseException ex) {
            ex.printStackTrace();
        }
        return dateone;
    }
}
