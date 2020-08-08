package swbd.db;

import java.sql.Connection;
import java.sql.PreparedStatement;
import java.sql.ResultSet;
import java.sql.Statement;
import java.util.ArrayList;
import java.util.List;

import javax.ws.rs.NotFoundException;
import javax.ws.rs.WebApplicationException;

import org.mindrot.jbcrypt.BCrypt;

public class Utente {
	public int ID_utente = -1;
	public String tipologia;
	public String email;
	public String username;
	public String password;
	public String nome_cognome;
	public String stato_account = "attivo";
	public String comune;
	private transient String passwordHash;

	public static Utente[] getUtenti(String[] ruoli, String comune, String regione) throws Exception {
		String query = ("SELECT ID_utente FROM utenti "
				+ ((regione != null) ? "JOIN comuni ON comune = sigla_comune " : "") + "WHERE "
				+ ((ruoli != null) ? "tipologia IN ($) " : "") + "AND "
				+ ((regione != null) ? "nome_regione=? " : ((comune != null) ? "comune=? " : ""))
				+ "ORDER BY ID_utente ASC").replace("WHERE AND", "WHERE").replace("WHERE ORDER", "ORDER")
						.replace("AND ORDER", "ORDER");
		if (ruoli != null) {

			String placeholders = "";
			for (int i = 0; i < ruoli.length; i++) {
				placeholders = placeholders.concat("?,");
			}
			placeholders = placeholders.concat("$").replace(",$", "").replace("$", "");
			query = query.replace("$", placeholders);
		}

		Connection conn = Database.Get_Connection();
		PreparedStatement ps = conn.prepareStatement(query);
		int placeholderIndex = 1;
		if (ruoli != null) {
			for (String ruolo : ruoli)
				ps.setString(placeholderIndex++, ruolo);
		}
		if (regione != null)
			ps.setString(placeholderIndex++, regione);
		else if (comune != null)
			ps.setString(placeholderIndex++, comune);

		ResultSet rs = ps.executeQuery();
		List<Utente> result = new ArrayList<Utente>();
		while (rs.next()) {
			result.add(new Utente(rs.getInt("ID_utente")));
		}
		return result.toArray(new Utente[result.size()]);
	}

	public Utente() {
	}

	public Utente(int ID) throws Exception {
		Connection conn = Database.Get_Connection();
		PreparedStatement ps = conn.prepareStatement("SELECT * FROM utenti WHERE ID_utente=?");
		ps.setInt(1, ID);
		ResultSet res = ps.executeQuery();
		if (!res.next())
			throw new NotFoundException();
		ID_utente = res.getInt("ID_utente");
		tipologia = res.getString("tipologia");
		email = res.getString("email");
		username = res.getString("username");
		passwordHash = res.getString("password");
		nome_cognome = res.getString("nome_cognome");
		stato_account = res.getString("stato_account");
		comune = res.getString("comune");
	}

	public void elimina() throws Exception {
		if (ID_utente != -1) {
			Connection conn = Database.Get_Connection();
			PreparedStatement ps = conn.prepareStatement("DELETE FROM utenti WHERE ID_utente=?");
			ps.setInt(1, ID_utente);
			ps.execute();
			ID_utente = -1;
		}
	}

	public Comune getComune() throws Exception {
		return new Comune(comune);
	}

	public Impianto[] getImpianti() throws Exception {
		return _getImpianti(null);
	}

	public Impianto[] getImpianti(Comune comune) throws Exception {
		return _getImpianti("comune = \"" + comune.sigla_comune + "\"");
	}

	public Impianto[] getImpianti(String regione) throws Exception {
		Connection conn = Database.Get_Connection();
		PreparedStatement ps = conn.prepareStatement(
				"SELECT CONCAT('comune IN (\"'," + "GROUP_CONCAT(sigla_comune SEPARATOR '\",\"'),'\")') "
						+ "AS filter FROM comuni WHERE LOWER(nome_regione) = LOWER(?) ");
		ps.setString(1, regione);
		ResultSet res = ps.executeQuery();

		if (!res.next())
			return new Impianto[0]; // non ci sono risultati

		return _getImpianti(res.getString("filter"));
	}

	public Impianto[] getImpianti(double latitudine, double longitudine, double raggio) throws Exception {
		String lat, lng, rad, filtro;
		lat = String.valueOf(latitudine).replace(",", ".");
		lng = String.valueOf(longitudine).replace(",", ".");
		rad = String.valueOf(raggio).replace(",", ".");
		filtro = "28305.61 * SQRT((COS(2*(longitudine-$longitudine))+1)+"
				+ "POWER((latitudine-$latitudine)/254.73,2)) <= $raggio";
		filtro = filtro.replace("$latitudine", lat).replace("$longitudine", lng).replace("$raggio", rad);
		return _getImpianti(filtro);
	}
/**
 * Ritorna la lista degli impianti. Distingue tra i vari utenti.
 * @param filtro
 * @return
 * @throws Exception
 */
	private Impianto[] _getImpianti(String filtro) throws Exception {
		if (filtro == null)
			filtro = "";
		Connection conn = Database.Get_Connection();
		PreparedStatement ps;
		if (this.tipologia.equals("amministratore") || this.tipologia.equals("monitor")) {
			// posso accedere a tutti gli impianti
			ps = conn
					.prepareStatement(("SELECT ID_impianto FROM impianti WHERE " + filtro + " ORDER BY ID_impianto ASC")
							.replace("WHERE  ORDER", "ORDER"));
		} else if (this.tipologia.equals("dipendente")) {
			// posso accedere solo a quelli che mi sono stati assegnati
			ps = conn.prepareStatement(("SELECT ID_impianto FROM impianti_assegnati WHERE " + filtro
					+ " AND ID_utente=? ORDER BY ID_impianto ASC").replace("WHERE  AND", "WHERE"));
			ps.setInt(1, ID_utente);
		} else { // tecnico: posso accedere agli impianti per i quali ho un intervento aperto
			ps = conn.prepareStatement(("SELECT DISTINCT impianto AS ID_impianto FROM sensori_impianto "
					+ "JOIN anomalie ON ID_sensore_impianto = sensore WHERE intervento IN "
					+ "(SELECT DISTINCT ID_intervento FROM tecnici_intervento JOIN interventi "
					+ "ON ID_intervento = intervento WHERE " + filtro + " AND utente=? AND stato NOT IN "
					+ "('risolto', 'non risolvibile')) ORDER by ID_impianto ASC").replace("WHERE  AND", "WHERE"));
			ps.setInt(1, ID_utente);
		}
		ResultSet rs = ps.executeQuery();
		List<Impianto> result = new ArrayList<Impianto>();
		while (rs.next()) {
			result.add(new Impianto(rs.getInt("ID_impianto"), ID_utente));
		}
		return result.toArray(new Impianto[result.size()]);

	}

	public Badge[] getAccessi() throws Exception {
		return getAccessi(null, null);
	}

	// solo tecnico
	public Badge[] getAccessi(String da, String a) throws Exception {
		if (!this.tipologia.equals("tecnico"))
			throw new WebApplicationException(400);

		if (da == null)
			da = "1900-01-01";
		if (a == null)
			a = "3000-12-31";

		Connection conn = Database.Get_Connection();
		PreparedStatement ps = conn
				.prepareStatement("SELECT ID FROM badge WHERE ID_utente=? AND data_rilevazione BETWEEN ? AND ?");
		ps.setInt(1, ID_utente);
		ps.setString(2, da);
		ps.setString(3, a);
		ResultSet rs = ps.executeQuery();

		List<Badge> result = new ArrayList<Badge>();
		while (rs.next()) {
			result.add(new Badge(rs.getInt("ID")));
		}
		return result.toArray(new Badge[result.size()]);
	}

	// solo per tecnico
	public Intervento[] getInterventiAperti() throws Exception {
		if (!this.tipologia.equals("tecnico"))
			throw new WebApplicationException(400);

		Connection conn = Database.Get_Connection();
		PreparedStatement ps = conn.prepareStatement("SELECT DISTINCT ID_intervento FROM tecnici_intervento "
				+ "JOIN interventi ON ID_intervento = intervento WHERE utente=? AND stato "
				+ "NOT IN ('risolto', 'non risolvibile') ORDER BY ID_intervento ASC");
		ps.setInt(1, ID_utente);

		ResultSet rs = ps.executeQuery();
		List<Intervento> result = new ArrayList<Intervento>();
		while (rs.next()) {
			result.add(new Intervento(rs.getInt("ID_intervento")));
		}
		return result.toArray(new Intervento[result.size()]);
	}

	public void salva() throws Exception {
		if (password != null)
			passwordHash = BCrypt.hashpw(password, BCrypt.gensalt(12));

		Connection conn = Database.Get_Connection();
		PreparedStatement ps;
		if (ID_utente == -1) { // INSERT
			ps = conn.prepareStatement("INSERT INTO utenti (tipologia,email,username,password,nome_cognome,"
					+ "stato_account,comune) VALUES (?,?,?,?,?,?,?)", Statement.RETURN_GENERATED_KEYS);
			ps.setString(1, tipologia);
			ps.setString(2, email);
			ps.setString(3, username);
			ps.setString(4, passwordHash);
			ps.setString(5, nome_cognome);
			ps.setString(6, stato_account);
			ps.setString(7, comune);
			ps.executeUpdate();
			ResultSet rs = ps.getGeneratedKeys();
			if (rs.next())
				ID_utente = rs.getInt(1);
		} else { // UPDATE
			if (passwordHash == null) {
				ps = conn.prepareStatement("SELECT password FROM utenti WHERE ID_utente=?");
				ps.setInt(1, ID_utente);
				ResultSet res = ps.executeQuery();
				res.next();
				passwordHash = res.getString("password");
			}

			ps = conn.prepareStatement("UPDATE utenti SET tipologia=?,email=?,username=?,password=?,"
					+ "nome_cognome=?,stato_account=?,comune=? WHERE ID_utente=?");
			ps.setString(1, tipologia);
			ps.setString(2, email);
			ps.setString(3, username);
			ps.setString(4, passwordHash);
			ps.setString(5, nome_cognome);
			ps.setString(6, stato_account);
			ps.setString(7, comune);
			ps.setInt(8, ID_utente);
			ps.executeUpdate();
		}
	}
}
