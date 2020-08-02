package swbd.db;

import java.sql.Connection;
import java.sql.PreparedStatement;
import java.sql.ResultSet;
import java.sql.Statement;
import java.util.ArrayList;
import java.util.List;

import javax.ws.rs.NotFoundException;
import javax.ws.rs.WebApplicationException;

public class Impianto {
	public int ID_impianto = -1;
	public String nome;
	public String descrizione;
	public double latitudine;
	public double longitudine;
	public String comune;
	public int intervallo_standard = 300;
	public int intervallo_anomalia = 30;
	public boolean flag_anomalia = false;

	public Impianto() {
	}

	public Impianto(int ID) throws Exception {
		Connection conn = Database.Get_Connection();
		PreparedStatement ps = conn.prepareStatement("SELECT * FROM impianti WHERE ID_impianto=?");
		ps.setInt(1, ID);
		ResultSet res = ps.executeQuery();
		if (!res.next())
			throw new NotFoundException();
		ID_impianto = res.getInt("ID_impianto");
		nome = res.getString("nome");
		descrizione = res.getString("descrizione");
		latitudine = res.getDouble("latitudine");
		longitudine = res.getDouble("longitudine");
		comune = res.getString("comune");
		intervallo_standard = res.getInt("intervallo_standard");
		intervallo_anomalia = res.getInt("intervallo_anomalia");

		ps = conn.prepareStatement(
				"SELECT * FROM interventi JOIN anomalie ON intervento = ID_intervento JOIN sensori_impianto ON sensore = ID_sensore_impianto WHERE impianto = ? AND interventi.stato NOT IN ('risolto', 'non risolvibile') LIMIT 1");
		ps.setInt(1, ID);
		res = ps.executeQuery();
		flag_anomalia = res.next();

	}

	public Comune getComune() throws Exception {
		return new Comune(comune);
	}

	public SensoreImpianto[] getSensori() throws Exception {
		Connection conn = Database.Get_Connection();
		PreparedStatement ps = conn
				.prepareStatement("SELECT ID_sensore_impianto FROM sensori_impianto WHERE impianto=?");
		ps.setInt(1, ID_impianto);
		ResultSet rs = ps.executeQuery();
		List<SensoreImpianto> result = new ArrayList<SensoreImpianto>();
		while (rs.next()) {
			result.add(new SensoreImpianto(rs.getInt("ID_sensore_impianto")));
		}
		return result.toArray(new SensoreImpianto[result.size()]);
	}

	public AttuatoreImpianto[] getAttuatori() throws Exception {
		Connection conn = Database.Get_Connection();
		PreparedStatement ps = conn
				.prepareStatement("SELECT ID_attuatore_impianto FROM attuatori_impianto WHERE impianto=?");
		ps.setInt(1, ID_impianto);
		ResultSet rs = ps.executeQuery();
		List<AttuatoreImpianto> result = new ArrayList<AttuatoreImpianto>();
		while (rs.next()) {
			result.add(new AttuatoreImpianto(rs.getInt("ID_attuatore_impianto")));
		}
		return result.toArray(new AttuatoreImpianto[result.size()]);
	}

	public Intervento[] getInterventi() throws Exception {
		return getInterventi(null, null);
	}

	public Intervento[] getInterventi(String inizio, String fine) throws Exception {
		if (inizio == null)
			inizio = "1900-01-01";
		if (fine == null)
			fine = "3000-12-31";

		Connection conn = Database.Get_Connection();
		PreparedStatement ps = conn.prepareStatement(
				"SELECT DISTINCT ID_intervento FROM interventi JOIN anomalie ON ID_intervento = intervento JOIN sensori_impianto ON ID_sensore_impianto = sensore WHERE impianto=? AND "
						+ "data_inserimento < ? AND (data_fine IS NULL OR data_fine > ?)");
		ps.setInt(1, ID_impianto);
		ps.setString(2, fine);
		ps.setString(3, inizio);
		ResultSet rs = ps.executeQuery();
		List<Intervento> result = new ArrayList<Intervento>();
		while (rs.next()) {
			result.add(new Intervento(rs.getInt("ID_intervento")));
		}
		return result.toArray(new Intervento[result.size()]);
	}

	public Badge[] getAccessi() throws Exception {
		return getAccessi(null, null);
	}

	public Badge[] getAccessi(String da, String a) throws Exception {
		if (da == null)
			da = "1900-01-01";
		if (a == null)
			a = "3000-12-31";

		Connection conn = Database.Get_Connection();
		PreparedStatement ps = conn
				.prepareStatement("SELECT ID FROM badge WHERE ID_Impianto=? AND data_rilevazione BETWEEN ? AND ?");
		ps.setInt(1, ID_impianto);
		ps.setString(2, da);
		ps.setString(3, a);
		ResultSet rs = ps.executeQuery();

		List<Badge> result = new ArrayList<Badge>();
		while (rs.next()) {
			result.add(new Badge(rs.getInt("ID")));
		}
		return result.toArray(new Badge[result.size()]);
	}

	public Lettura[] getStatistiche(TipologiaSensore tipologia, String inizio, String fine) throws Exception {
		if (inizio == null)
			inizio = "1900-01-01";
		if (fine == null)
			fine = "3000-12-31";

		Connection conn = Database.Get_Connection();
		PreparedStatement ps = conn.prepareStatement(
				"SELECT ID_lettura FROM letture JOIN sensori_impianto ON ID_sensore_impianto = sensore WHERE impianto = ? AND tipologia = ? AND data_inserimento BETWEEN ? AND ? ORDER BY data_inserimento ASC");
		ps.setInt(1, ID_impianto);
		ps.setInt(2, tipologia.ID_tipologia_sensore);
		ps.setString(3, inizio);
		ps.setString(4, fine);
		ResultSet rs = ps.executeQuery();
		List<Lettura> result = new ArrayList<Lettura>();
		while (rs.next()) {
			result.add(new Lettura(rs.getInt("ID_lettura")));
		}
		return result.toArray(new Lettura[result.size()]);
	}

	public void elimina() throws Exception {
		if (ID_impianto != -1) {
			Connection conn = Database.Get_Connection();
			PreparedStatement ps = conn.prepareStatement("DELETE FROM impianti WHERE ID_impianto=?");
			ps.setInt(1, ID_impianto);
			ps.execute();
			ID_impianto = -1;
		}
	}
	/**
	 * Aggiorna le informazioni dell'impianto e distingue il caso in cui debba esser creata o solo aggiornata.
	 * @throws Exception
	 */
	public void salva() throws Exception {
		if (!(latitudine > -90 && latitudine < 90) || !(longitudine > -180 && longitudine < 180))
			throw new WebApplicationException(400);

		Connection conn = Database.Get_Connection();
		PreparedStatement ps;
		if (ID_impianto == -1) { // INSERT
			ps = conn.prepareStatement(
					"INSERT INTO impianti (nome,descrizione,latitudine,longitudine,comune,"
							+ "intervallo_standard,intervallo_anomalia) VALUES (?,?,?,?,?,?,?)",
					Statement.RETURN_GENERATED_KEYS);
			ps.setString(1, nome);
			ps.setString(2, descrizione);
			ps.setDouble(3, latitudine);
			ps.setDouble(4, longitudine);
			ps.setString(5, comune);
			ps.setInt(6, intervallo_standard);
			ps.setInt(7, intervallo_anomalia);
			ps.executeUpdate();
			ResultSet rs = ps.getGeneratedKeys();
			if (rs.next())
				ID_impianto = rs.getInt(1);
		} else { // UPDATE
			ps = conn.prepareStatement("UPDATE impianti SET nome=?,descrizione=?,latitudine=?,"
					+ "longitudine=?,comune=?,intervallo_standard=?,intervallo_anomalia=? " + "WHERE ID_impianto=?");
			ps.setString(1, nome);
			ps.setString(2, descrizione);
			ps.setDouble(3, latitudine);
			ps.setDouble(4, longitudine);
			ps.setString(5, comune);
			ps.setInt(6, intervallo_standard);
			ps.setInt(7, intervallo_anomalia);
			ps.setInt(8, ID_impianto);
			ps.executeUpdate();
		}
	}
}