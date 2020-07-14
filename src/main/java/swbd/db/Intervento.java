package swbd.db;

import java.sql.Connection;
import java.sql.PreparedStatement;
import java.sql.ResultSet;
import java.sql.Statement;
import java.util.ArrayList;
import java.util.List;

import javax.ws.rs.NotFoundException;
import javax.ws.rs.WebApplicationException;

public class Intervento {
	public int ID_intervento = -1;
	public String data_inserimento;
	public String data_inizio;
	public String data_fine;
	public String stato;
	private transient String oldStato;

	public Intervento() {
	}

	public Intervento(int ID) throws Exception {
		Connection conn = Database.Get_Connection();
		PreparedStatement ps = conn.prepareStatement("SELECT * FROM interventi WHERE ID_intervento=?");
		ps.setInt(1, ID);
		ResultSet res = ps.executeQuery();
		if (!res.next())
			throw new NotFoundException();
		ID_intervento = res.getInt("ID_intervento");
		data_inserimento = res.getString("data_inserimento");
		data_inizio = res.getString("data_inizio");
		data_fine = res.getString("data_fine");
		stato = res.getString("stato");
		oldStato = res.getString("stato");
	}

	public void elimina() throws Exception {
		if (ID_intervento != -1) {
			Connection conn = Database.Get_Connection();
			PreparedStatement ps = conn.prepareStatement("DELETE FROM interventi WHERE ID_intervento=?");
			ps.setInt(1, ID_intervento);
			ps.execute();
			ID_intervento = -1;
		}
	}

	public Impianto getImpianto() throws Exception {
		Connection conn = Database.Get_Connection();
		PreparedStatement ps = conn.prepareStatement(
				"SELECT impianto FROM anomalie JOIN sensori_impianto ON sensore = ID_sensore_impianto WHERE intervento=? LIMIT 1");
		ps.setInt(1, ID_intervento);
		ResultSet res = ps.executeQuery();
		if (!res.next())
			throw new NotFoundException();
		return new Impianto(res.getInt("impianto"));
	}

	public Utente[] getTecnici() throws Exception {
		Connection conn = Database.Get_Connection();
		PreparedStatement ps = conn
				.prepareStatement("SELECT utente FROM tecnici_intervento WHERE intervento=? ORDER BY utente ASC");
		ps.setInt(1, ID_intervento);
		ResultSet rs = ps.executeQuery();

		List<Utente> result = new ArrayList<Utente>();
		while (rs.next()) {
			result.add(new Utente(rs.getInt("utente")));
		}
		return result.toArray(new Utente[result.size()]);
	}

	public Anomalia[] getAnomalie() throws Exception {
		Connection conn = Database.Get_Connection();
		PreparedStatement ps = conn
				.prepareStatement("SELECT ID_anomalia FROM anomalie WHERE intervento=? ORDER BY ID_anomalia ASC");
		ps.setInt(1, ID_intervento);
		ResultSet rs = ps.executeQuery();

		List<Anomalia> result = new ArrayList<Anomalia>();
		while (rs.next()) {
			result.add(new Anomalia(rs.getInt("ID_anomalia")));
		}
		return result.toArray(new Anomalia[result.size()]);
	}

	public void salva() throws Exception {
		if (data_inizio == null && data_fine != null)
			throw new WebApplicationException(400);

		Connection conn = Database.Get_Connection();
		PreparedStatement ps;
		if (ID_intervento == -1) { // INSERT
			ps = conn.prepareStatement("INSERT INTO interventi () VALUES () ", Statement.RETURN_GENERATED_KEYS);
			ps.executeUpdate();
			ResultSet rs = ps.getGeneratedKeys();
			if (rs.next())
				ID_intervento = rs.getInt(1);
		} else { // UPDATE
			// recupero il vecchio stato per vedere se lo stato nuovo e' valido
			if (oldStato == null) {
				ps = conn.prepareStatement("SELECT stato FROM interventi WHERE ID_intervento=?");
				ps.setInt(1, ID_intervento);
				ResultSet res = ps.executeQuery();
				oldStato = res.getString("stato");
			}
			// se lo stato e' cambiato
			if (!stato.equals(oldStato)) {
				if (stato.equals("inserito") || oldStato.equals("risolto") || oldStato.equals("non_risolvibile"))
					throw new WebApplicationException(400); // sto cambiando "all'indietro", ERRORE!
			}

			// se nell'intervento terminato ci sono anomalie non contrassegnate come
			// terminate, contrassegnale con lo stato dell'intervento
			if (stato.equals("risolto") || stato.equals("non_risolvibile")) {
				ps = conn.prepareStatement(
						"UPDATE anomalie SET stato=? WHERE intervento=? AND stato NOT IN (\"inserito\", \"non_risolvibile\")");
				ps.setString(1, stato);
				ps.setInt(2, ID_intervento);
				ps.executeUpdate();
			}

			ps = conn.prepareStatement("UPDATE interventi SET data_inizio=?,data_fine=?,stato=? WHERE ID_intervento=?");
			ps.setString(1, data_inizio);
			ps.setString(2, data_fine);
			ps.setString(3, stato);
			ps.setInt(4, ID_intervento);
			ps.executeUpdate();
		}
		oldStato = stato;
	}
}