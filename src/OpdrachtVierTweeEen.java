import java.sql.Connection;
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
	List<Long> duur = new ArrayList<Long>();

	public OpdrachtVierTweeEen() {
		// TODO Auto-generated method stub
		// LET OP DEZE CODE IS MET DE LOSSE POLS GETYPT IN NOTEPAD - LONG LIFE
		// WORK WITH NO ECLIPSE OR NOTEPAD++ ;)
		// IN ELKE REGEL ZAL WEL EEN FOUTJE ZITTEN DENK IK

		int max = 600;
		Database db = null;

		for (int i = 0; i < max; i++) {
			try {
				db = new Database();
				db.conn.setAutoCommit(false);
				db.conn.setTransactionIsolation(Connection.TRANSACTION_READ_UNCOMMITTED);

				long beginTijd = System.currentTimeMillis();

				int studnr = getRandomNumber(1000000, 9999999);

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
						+ " AA', 'plaatsje" + i + "', '0" + getRandomNumber(111111111, 999999999) + "');";

				db.stmt.executeUpdate(insertStudentQuery);

				Random rn = new Random();

				switch (rn.nextInt(30)) {
					case 0:
						String dateone = getRandomDate();
						String datetwo = getRandomDate();

						String insertKlasQuery = "INSERT INTO klassen (klasnaam, startdatum, einddatum) VALUES ('"
								+ UUID.randomUUID().toString() + "', '" + getLowOrHighDate(dateone, datetwo, false)
								+ "', '" + getLowOrHighDate(dateone, datetwo, true) + "');";
						db.stmt.executeUpdate(insertKlasQuery);
						break;
				}

				String selectKlasQuery = "SELECT count(*) AS aantalrecords, klassen.klasid FROM klassen GROUP BY klasid;";
				ResultSet rs = db.stmt.executeQuery(selectKlasQuery);

				rs.next();
				String insertStudentKlas = "INSERT INTO studentenklassen (klasid, studentnummer) VALUES ('"
						+ (getRandomNumber(1, rs.getInt("aantalrecords"))) + "', '" + studnr + "');";

				System.out.println(insertStudentKlas);
				db.stmt.executeUpdate(insertStudentKlas);

				long eindTijd = System.currentTimeMillis();

				switch (rn.nextInt(30)) {
					case 0:
						String dateone = getRandomDate();
						String datetwo = getRandomDate();

						String insertModuleQuery = "INSERT INTO modules (modulecode, naam, modulebeheerder, invoerdatum, einddatum) VALUES ('module"
								+ i
								+ "', 'Advanced Database "
								+ i
								+ "', 'ADVDOC', '"
								+ getLowOrHighDate(dateone, datetwo, false)
								+ "', '"
								+ getLowOrHighDate(dateone, datetwo, true) + "');";
						System.out.println(insertModuleQuery);
						db.stmt.executeUpdate(insertModuleQuery);

						for (int j = 0; j < rs.getFetchSize(); j++) {
							if (rn.nextInt(100) <= 15) {

								String insertModuleKlasQuery = "INSERT INTO modulesklassen (modulecode, klasid) VALUES ('module"
										+ i + "', " + j + ");";
								db.stmt.executeUpdate(insertModuleKlasQuery);

							}
						}

						break;
				}

				db.conn.commit();

				// duurInMS = eindTijd - beginTijd;
				duur.add((eindTijd - beginTijd));
				try {
					Thread.sleep(100);
				} catch (InterruptedException e) {
					// TODO Auto-generated catch block
					e.printStackTrace();
				}

			} catch (SQLException e) {
				e.printStackTrace();

				try {
					db.conn.rollback();
				} catch (SQLException e1) {
					// TODO Auto-generated catch block
					e1.printStackTrace();
				}
			} finally {
				try {
					db.conn.close();
				} catch (SQLException e) {
					// TODO Auto-generated catch block
					e.printStackTrace();
				}

			}
		}
		System.out.println("Average time: " + getAverage() + "MS");
	}

	public int getRandomNumber(int min, int max) {
		return (int) Math.floor(Math.random() * (max - min + 1)) + min;
	}

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

	public long getAverage() {
		long sum = 0;
		for (int i = 0; i < duur.size(); i++) {
			sum += duur.get(i).longValue();
		}
		return sum / duur.size();
	}

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
			} else
				if (date1.compareTo(date2) < 0) {
					if (highestDate) {
						return datetwo;
					} else {
						return dateone;

					}
				} else
					if (date1.compareTo(date2) == 0) {
						return dateone;
					}

		} catch (ParseException ex) {
			ex.printStackTrace();
		}
		return dateone;
	}
}
