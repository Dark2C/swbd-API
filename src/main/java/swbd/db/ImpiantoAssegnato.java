package swbd.db;

import java.sql.Connection;
import java.sql.PreparedStatement;
import java.sql.ResultSet;
import javax.ws.rs.NotFoundException;
import javax.ws.rs.WebApplicationException;

public class ImpiantoAssegnato {
	private transient boolean isNew = true;
	public int ID_utente;
	public int ID_impianto;
	public int permesso_scrittura;

	public ImpiantoAssegnato() {
	}

	public ImpiantoAssegnato(int ID_utente, int ID_impianto) throws Exception {
		Utente utenteRichiesto = new Utente(ID_utente);
		Connection conn = Database.Get_Connection();
		PreparedStatement ps;
		if (!utenteRichiesto.tipologia.equals("dipendente")) {
			if (utenteRichiesto.tipologia.equals("tecnico")) {
				ps = conn.prepareStatement(
						"SELECT DISTINCT impianto FROM sensori_impianto JOIN anomalie ON ID_sensore_impianto = sensore "
								+ "JOIN interventi ON anomalie.intervento = ID_intervento"
								+ "JOIN tecnici_intervento ON ID_intervento = tecnici_intervento.intervento "
								+ "WHERE stato NOT IN ('risolto', 'non risolvibile') AND utente=? AND impianto=? LIMIT 1");
				ps.setInt(1, ID_utente);
				ps.setInt(2, ID_impianto);
				ResultSet res = ps.executeQuery();
				if (!res.next()) // non ho nessun intervento aperto sull'impianto => non ho accesso all'impianto
					throw new NotFoundException();
			}
			// se sono amministratore, monitor o un tecnico autorizzato,
			// ho accesso completo a tutti gli impianti
			this.ID_utente = ID_utente;
			this.ID_impianto = ID_impianto;
			this.permesso_scrittura = 1;
		} else {
			// l'utente e' un dipendente, controllo che gli e' stato assegnato l'impianto
			ps = conn.prepareStatement("SELECT * FROM impianti_assegnati WHERE ID_utente=? AND ID_impianto=?");
			ps.setInt(1, ID_utente);
			ps.setInt(2, ID_impianto);
			ResultSet res = ps.executeQuery();
			if (!res.next())
				throw new NotFoundException();
			this.ID_utente = res.getInt("ID_utente");
			this.ID_impianto = res.getInt("ID_impianto");
			this.permesso_scrittura = res.getInt("permesso_scrittura");
			isNew = false;
		}
	}

	public void elimina() throws Exception {
		if (!isNew) {
			Connection conn = Database.Get_Connection();
			PreparedStatement ps = conn
					.prepareStatement("DELETE FROM impianti_assegnati WHERE ID_utente=? AND ID_impianto=?");
			ps.setInt(1, ID_utente);
			ps.setInt(2, ID_impianto);
			ps.execute();
			isNew = true;
		}
	}

	public Utente getDipendente() throws Exception {
		return new Utente(ID_utente);
	}

	public Impianto getImpianto() throws Exception {
		return new Impianto(ID_impianto);
	}

	public void salva() throws Exception {
		Connection conn = Database.Get_Connection();
		PreparedStatement ps;

		// check che l'utente sia un dipendente
		Utente user = new Utente(ID_utente);
		if (!user.tipologia.equals("dipendente"))
			throw new WebApplicationException(400);

		if (isNew) { // INSERT
			ps = conn.prepareStatement(
					"INSERT INTO impianti_assegnati (ID_utente,ID_impianto,permesso_scrittura) VALUES (?,?,?)");
			ps.setInt(1, ID_utente);
			ps.setInt(2, ID_impianto);
			ps.setInt(3, permesso_scrittura);
			ps.execute();
		} else { // UPDATE
			ps = conn.prepareStatement(
					"UPDATE impianti_assegnati SET permesso_scrittura=? WHERE ID_utente=? AND ID_impianto=?");
			ps.setInt(1, permesso_scrittura);
			ps.setInt(2, ID_utente);
			ps.setInt(3, ID_impianto);
			ps.executeUpdate();
		}
	}
}