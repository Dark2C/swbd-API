package swbd.db;

import java.sql.Connection;
import java.sql.PreparedStatement;
import java.sql.ResultSet;
import java.sql.Statement;
import javax.ws.rs.NotFoundException;
import javax.ws.rs.WebApplicationException;

public class Anomalia {
	public int ID_anomalia = -1;
	public String descrizione;
	public int sensore = -1;
	public int intervento;
	public String data_segnalazione;
	public String stato;
	private transient String oldStato;

	public Anomalia() {
	}

	public Anomalia(int ID) throws Exception {
		Connection conn = Database.Get_Connection();
		PreparedStatement ps = conn.prepareStatement("SELECT * FROM anomalie WHERE ID_anomalia=?");
		ps.setInt(1, ID);
		ResultSet res = ps.executeQuery();
		if (!res.next())
			throw new NotFoundException();
		ID_anomalia = res.getInt("ID_anomalia");
		descrizione = res.getString("descrizione");
		sensore = res.getInt("sensore");
		intervento = res.getInt("intervento");
		data_segnalazione = res.getString("data_segnalazione");
		stato = res.getString("stato");
		oldStato = res.getString("stato");
	}

	public Intervento getIntervento() throws Exception {
		return new Intervento(intervento);
	}

	public SensoreImpianto SensoreImpianto() throws Exception {
		return new SensoreImpianto(sensore);
	}

	public void elimina() throws Exception {
		if (ID_anomalia != -1) {
			Connection conn = Database.Get_Connection();
			PreparedStatement ps = conn.prepareStatement("DELETE FROM anomalie WHERE ID_anomalia=?");
			ps.setInt(1, ID_anomalia);
			ps.execute();
			ID_anomalia = -1;
		}
	}
/**
 * Aggiorna le informazioni dell'anomalia e distingue il caso in cui debba esser creata o solo aggiornata.
 * @throws Exception
 */
	public void salva() throws Exception {
		Connection conn = Database.Get_Connection();
		PreparedStatement ps;
		if (ID_anomalia == -1) { // INSERT
			ps = conn.prepareStatement("INSERT INTO anomalie (descrizione,sensore,intervento) VALUES (?,?,?)",
					Statement.RETURN_GENERATED_KEYS);
			ps.setString(1, descrizione);
			ps.setInt(2, sensore);
			ps.setInt(3, intervento);
			ps.executeUpdate();
			stato = "inserito";
			ResultSet rs = ps.getGeneratedKeys();
			if (rs.next())
				ID_anomalia = rs.getInt(1);
		} else { // UPDATE
			// recupero il vecchio stato per vedere se lo stato nuovo e' valido
			if (oldStato == null) {
				ps = conn.prepareStatement("SELECT stato FROM anomalie WHERE ID_anomalia=?");
				ps.setInt(1, ID_anomalia);
				ResultSet res = ps.executeQuery();
				res.next();
				oldStato = res.getString("stato");
			}
			// se lo stato e' cambiato
			if (!stato.equals(oldStato)) {
				if (stato.equals("inserito") || oldStato.equals("risolto") || oldStato.equals("non_risolvibile"))
					throw new WebApplicationException(400); // sto cambiando "all'indietro", ERRORE!
			}

			ps = conn.prepareStatement(
					"UPDATE anomalie SET descrizione=?,sensore=?,intervento=?," + "stato=? WHERE ID_anomalia=?");
			ps.setString(1, descrizione);
			ps.setInt(2, sensore);
			ps.setInt(3, intervento);
			ps.setString(4, stato);
			ps.setInt(5, ID_anomalia);
			ps.executeUpdate();
		}
		oldStato = new String(stato);
	}
}