package swbd.db;

import java.sql.Connection;
import java.sql.PreparedStatement;
import java.sql.ResultSet;
import java.sql.Statement;
import java.util.ArrayList;
import java.util.List;

import javax.ws.rs.NotFoundException;
import javax.ws.rs.WebApplicationException;

public class SensoreImpianto {
	public int ID_sensore_impianto = -1;
	public int impianto;
	public int tipologia;
	public String data_installazione;
	public String produttore;
	public String modello;
	public String descrizione;
	public String unita_misura;
	public String unita_misura_integrale;
	public double valore_min;
	public double valore_max;

	public SensoreImpianto() {
	}

	public SensoreImpianto(int ID) throws Exception {
		Connection conn = Database.Get_Connection();
		PreparedStatement ps = conn.prepareStatement(
				"SELECT * FROM sensori_impianto JOIN tipologie_sensori ON tipologia = ID_tipologia_sensore WHERE ID_sensore_impianto=?");
		ps.setInt(1, ID);
		ResultSet res = ps.executeQuery();
		if (!res.next())
			throw new NotFoundException();
		ID_sensore_impianto = res.getInt("ID_sensore_impianto");
		impianto = res.getInt("impianto");
		tipologia = res.getInt("tipologia");
		data_installazione = res.getString("data_installazione");
		produttore = res.getString("produttore");
		modello = res.getString("modello");
		descrizione = res.getString("descrizione");
		valore_min = res.getDouble("valore_min");
		valore_max = res.getDouble("valore_max");
		unita_misura = res.getString("unita_misura");
		unita_misura_integrale = res.getString("unita_misura_integrale");
	}

	public Impianto getImpianto() throws Exception {
		return new Impianto(impianto);
	}

	public TipologiaSensore getTipologia() throws Exception {
		return new TipologiaSensore(tipologia);
	}

	public Lettura[] getLetture() throws Exception {
		return getLetture(null, null);
	}

	public Lettura[] getLetture(String inizio, String fine) throws Exception {
		if (inizio == null)
			inizio = "1900-01-01";
		if (fine == null)
			fine = "3000-12-31";
		Connection conn = Database.Get_Connection();
		PreparedStatement ps = conn.prepareStatement(
				"SELECT ID_Lettura FROM letture WHERE sensore=? AND data_inserimento BETWEEN ? AND ? ORDER BY data_inserimento DESC");
		ps.setInt(1, ID_sensore_impianto);
		ps.setString(2, inizio);
		ps.setString(3, fine);
		ResultSet rs = ps.executeQuery();

		List<Lettura> result = new ArrayList<Lettura>();
		while (rs.next()) {
			result.add(new Lettura(rs.getInt("ID_Lettura")));
		}
		return result.toArray(new Lettura[result.size()]);
	}

	public Lettura[] getLetture(int numeroLetture) throws Exception {
		if (numeroLetture < 0)
			throw new WebApplicationException(400);
		Connection conn = Database.Get_Connection();
		PreparedStatement ps = conn.prepareStatement(
				"SELECT ID_Lettura FROM letture WHERE sensore=? ORDER BY data_inserimento DESC LIMIT ?");
		ps.setInt(1, ID_sensore_impianto);
		ps.setInt(2, numeroLetture);
		ResultSet rs = ps.executeQuery();

		List<Lettura> result = new ArrayList<Lettura>();
		while (rs.next()) {
			result.add(new Lettura(rs.getInt("ID_Lettura")));
		}
		return result.toArray(new Lettura[result.size()]);
	}

	public void elimina() throws Exception {
		if (ID_sensore_impianto != -1) {
			Connection conn = Database.Get_Connection();
			PreparedStatement ps = conn.prepareStatement("DELETE FROM sensori_impianto WHERE ID_sensore_impianto=?");
			ps.setInt(1, ID_sensore_impianto);
			ps.execute();
			ID_sensore_impianto = -1;
		}
	}

	public void salva() throws Exception {
		Connection conn = Database.Get_Connection();
		PreparedStatement ps;
		if (ID_sensore_impianto == -1) { // INSERT
			ps = conn.prepareStatement("INSERT INTO sensori_impianto (impianto,tipologia) VALUES (?,?)",
					Statement.RETURN_GENERATED_KEYS);
			ps.setInt(1, impianto);
			ps.setInt(2, tipologia);
			ps.executeUpdate();
			ResultSet rs = ps.getGeneratedKeys();
			if (rs.next())
				ID_sensore_impianto = rs.getInt(1);
		}
	}
}