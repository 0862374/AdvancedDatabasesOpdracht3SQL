import java.sql.Connection;
import java.sql.ResultSet;
import java.sql.SQLException;
import java.util.Random;

public class OpdrachtVierTweeEen {
	public OpdrachtVierTweeEen() {
		// TODO Auto-generated method stub
		// LET OP DEZE CODE IS MET DE LOSSE POLS GETYPT IN NOTEPAD - LONG LIFE
		// WORK WITH NO ECLIPSE OR NOTEPAD++ ;)
		// IN ELKE REGEL ZAL WEL EEN FOUTJE ZITTEN DENK IK

		int max = 30;
		Database db = new Database();
		long duurInMS = 0;

		for (int i = 0; i < max; i++) {
			try {

				db.conn.setAutoCommit(false);
				db.conn.setTransactionIsolation(Connection.TRANSACTION_READ_COMMITTED);

				long beginTijd = System.currentTimeMillis();

				String insertStudentQuery = "INSERT INTO studenten (studentnummer, voornaam, achternaam, geboortedatum, geslacht, straat) VALUES ("
						+ getRandomNumber(1000000, 9999999)
						+ ","
						+ "'Student"
						+ i
						+ "',"
						+ "'Achter"
						+ i
						+ "', "
						+ getRandomDate() + "," + getRandomGeslacht() + ", 'straatje "+i+"');";
				db.stmt.executeQuery(insertStudentQuery);

				Random rn = new Random();

				switch (rn.nextInt(30)) {
					case 0:
						String insertKlasQuery = "INSERT INTO [KLAS] (velden) VALUES (waardes);";
						db.stmt.executeQuery(insertKlasQuery);
						break;
				}

				String selectKlasQuery = "SELECT klas_id FROM TABEL;";
				ResultSet rs = db.stmt.executeQuery(selectKlasQuery);

				String insertStudentKlas = "INSERT INTO [STUDENTKLAS] (velden) VALUES (waardes "
						+ (rn.nextInt(rs.getFetchSize()) + 1) + ");";
				db.stmt.executeQuery(insertStudentKlas);

				long eindTijd = System.currentTimeMillis();

				switch (rn.nextInt(30)) {
					case 0:
						String insertModuleQuery = "INSERT INTO [MODULE] (velden) VALUES (waardes);";
						db.stmt.executeQuery(insertModuleQuery);

						for (int j = 0; j < rs.getFetchSize(); j++) {
							switch (rn.nextInt(15)) {
								case 0:
									String insertModuleKlasQuery = "INSERT INTO [MODULEKLAS] (velden) VALUES (waardes);";
									db.stmt.executeQuery(insertModuleKlasQuery);
									break;
							}
						}

						break;
				}

				db.conn.commit();

				duurInMS = eindTijd - beginTijd;

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
				System.out.println(duurInMS);
			}
		}
	}

	public int getRandomNumber(int min, int max) {
		return (int) Math.floor(Math.random() * (max - min + 1)) + min;
	}

	public String getRandomDate() {
		Random rn = new Random();
		String date;

		date = Integer.toString(rn.nextInt(31)) + "-" + Integer.toString(rn.nextInt(12)) + "-"
				+ getRandomNumber(1980, 2005);

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

}
