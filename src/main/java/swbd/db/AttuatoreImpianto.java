package swbd.db;

import java.sql.Connection;
import java.sql.PreparedStatement;
import java.sql.ResultSet;
import java.sql.Statement;
import java.util.ArrayList;
import java.util.List;

import javax.ws.rs.NotFoundException;
import javax.ws.rs.WebApplicationException;

public class AttuatoreImpianto {
	public int ID_attuatore_impianto = -1;
	public int impianto;
	public int tipologia;
	public String data_installazione;
	public String produttore;
	public String modello;
	public String descrizione;
	public String tipo_valore;
	public String unita_misura;
	public double valore_min;
	public double valore_max;

	public AttuatoreImpianto() {
	}

	public AttuatoreImpianto(int ID) throws Exception {
		Connection conn = Database.Get_Connection();
		PreparedStatement ps = conn.prepareStatement(
				"SELECT * FROM attuatori_impianto JOIN tipologie_attuatori ON tipologia = ID_tipologia_attuatore WHERE ID_attuatore_impianto=?");
		ps.setInt(1, ID);
		ResultSet res = ps.executeQuery();
		if (!res.next())
			throw new NotFoundException();
		ID_attuatore_impianto = res.getInt("ID_attuatore_impianto");
		impianto = res.getInt("impianto");
		tipologia = res.getInt("tipologia");
		data_installazione = res.getString("data_installazione");
		produttore = res.getString("produttore");
		modello = res.getString("modello");
		descrizione = res.getString("descrizione");
		tipo_valore = res.getString("tipo_valore");
		unita_misura = res.getString("unita_misura");
		valore_min = res.getDouble("valore_min");
		valore_max = res.getDouble("valore_max");
	}

	public Impianto getImpianto() throws Exception {
		return new Impianto(impianto);
	}

	public TipologiaSensore getTipologiaSensore() throws Exception {
		return new TipologiaSensore(tipologia);
	}

	public Operazione[] getOperazioni(boolean conferma_lettura) throws Exception {
		Connection conn = Database.Get_Connection();
		PreparedStatement ps = conn.prepareStatement(
				"SELECT ID_operazione FROM operazioni WHERE attuatore=? AND conferma_lettura = 0 ORDER BY data_inserimento DESC");
		ps.setInt(1, ID_attuatore_impianto);
		ResultSet rs = ps.executeQuery();
		if (conferma_lettura) {
			ps = conn.prepareStatement(
					"UPDATE operazioni SET conferma_lettura = 1 WHERE attuatore=? AND conferma_lettura = 0");
			ps.setInt(1, ID_attuatore_impianto);
			ps.execute();
		}

		List<Operazione> result = new ArrayList<Operazione>();
		while (rs.next()) {
			result.add(new Operazione(rs.getInt("ID_operazione")));
		}
		return result.toArray(new Operazione[result.size()]);
	}

	public Operazione[] getOperazioni(String inizio, String fine) throws Exception {
		if (inizio == null)
			inizio = "1900-01-01";
		if (fine == null)
			fine = "3000-12-31";
		Connection conn = Database.Get_Connection();
		PreparedStatement ps = conn.prepareStatement(
				"SELECT ID_operazione FROM operazioni WHERE attuatore=? AND data_inserimento BETWEEN ? AND ? ORDER BY data_inserimento DESC");
		ps.setInt(1, ID_attuatore_impianto);
		ps.setString(2, inizio);
		ps.setString(3, fine);
		ResultSet rs = ps.executeQuery();

		List<Operazione> result = new ArrayList<Operazione>();
		while (rs.next()) {
			result.add(new Operazione(rs.getInt("ID_operazione")));
		}
		return result.toArray(new Operazione[result.size()]);
	}

	public Operazione[] getOperazioni(int numeroLetture) throws Exception {
		if (numeroLetture < 0)
			throw new WebApplicationException(400);
		Connection conn = Database.Get_Connection();
		PreparedStatement ps = conn.prepareStatement(
				"SELECT ID_operazione FROM operazioni WHERE attuatore=? ORDER BY data_inserimento ASC LIMIT ?");
		ps.setInt(1, ID_attuatore_impianto);
		ps.setInt(2, numeroLetture);
		ResultSet rs = ps.executeQuery();

		List<Operazione> result = new ArrayList<Operazione>();
		while (rs.next()) {
			result.add(new Operazione(rs.getInt("ID_operazione")));
		}
		return result.toArray(new Operazione[result.size()]);
	}

	public void elimina() throws Exception {
		if (ID_attuatore_impianto != -1) {
			Connection conn = Database.Get_Connection();
			PreparedStatement ps = conn
					.prepareStatement("DELETE FROM attuatori_impianto WHERE ID_attuatore_impianto=?");
			ps.setInt(1, ID_attuatore_impianto);
			ps.execute();
			ID_attuatore_impianto = -1;
		}
	}

	public void salva() throws Exception {
		Connection conn = Database.Get_Connection();
		PreparedStatement ps;
		if (ID_attuatore_impianto == -1) { // INSERT
			ps = conn.prepareStatement("INSERT INTO attuatori_impianto (impianto,tipologia) VALUES (?,?)",
					Statement.RETURN_GENERATED_KEYS);
			ps.setInt(1, impianto);
			ps.setInt(2, tipologia);
			ps.executeUpdate();
			ResultSet rs = ps.getGeneratedKeys();
			if (rs.next())
				ID_attuatore_impianto = rs.getInt(1);
		}
	}
}