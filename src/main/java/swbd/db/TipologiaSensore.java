package swbd.db;

import java.sql.Connection;
import java.sql.PreparedStatement;
import java.sql.ResultSet;
import java.sql.Statement;
import java.util.ArrayList;
import java.util.List;

import javax.ws.rs.NotFoundException;
import javax.ws.rs.WebApplicationException;

public class TipologiaSensore {
	public int ID_tipologia_sensore = -1;
	public String produttore;
	public String modello;
	public String descrizione;
	public double valore_min;
	public double valore_max;
	public String unita_misura;
	public String unita_misura_integrale;
	public double coeff_integrale = 1;

	public static TipologiaSensore[] getSensori() throws Exception {
		Connection conn = Database.Get_Connection();
		PreparedStatement ps = conn.prepareStatement(
				"SELECT ID_tipologia_sensore FROM tipologie_sensori ORDER BY ID_tipologia_sensore ASC");
		ResultSet rs = ps.executeQuery();
		List<TipologiaSensore> result = new ArrayList<TipologiaSensore>();
		while (rs.next()) {
			result.add(new TipologiaSensore(rs.getInt("ID_tipologia_sensore")));
		}
		return result.toArray(new TipologiaSensore[result.size()]);
	}

	public TipologiaSensore() {
	}

	public TipologiaSensore(int ID) throws Exception {
		Connection conn = Database.Get_Connection();
		PreparedStatement ps = conn.prepareStatement("SELECT * FROM tipologie_sensori WHERE ID_tipologia_sensore=?");
		ps.setInt(1, ID);
		ResultSet res = ps.executeQuery();
		if (!res.next())
			throw new NotFoundException();
		ID_tipologia_sensore = res.getInt("ID_tipologia_sensore");
		produttore = res.getString("produttore");
		modello = res.getString("modello");
		descrizione = res.getString("descrizione");
		valore_min = res.getDouble("valore_min");
		valore_max = res.getDouble("valore_max");
		unita_misura = res.getString("unita_misura");
		unita_misura_integrale = res.getString("unita_misura_integrale");
		coeff_integrale = res.getDouble("coeff_integrale");
	}

	public void elimina() throws Exception {
		if (ID_tipologia_sensore != -1) {
			Connection conn = Database.Get_Connection();
			PreparedStatement ps = conn.prepareStatement("DELETE FROM tipologie_sensori WHERE ID_tipologia_sensore=?");
			ps.setInt(1, ID_tipologia_sensore);
			ps.execute();
			ID_tipologia_sensore = -1;
		}
	}

	public void salva() throws Exception {
		if (valore_min > valore_max)
			throw new WebApplicationException(400);

		Connection conn = Database.Get_Connection();
		PreparedStatement ps;
		if (ID_tipologia_sensore == -1) { // INSERT
			ps = conn.prepareStatement(
					"INSERT INTO tipologie_sensori (produttore,modello,descrizione,valore_min,valore_max,"
							+ "unita_misura,unita_misura_integrale,coeff_integrale) VALUES (?,?,?,?,?,?,?,?)",
					Statement.RETURN_GENERATED_KEYS);
			ps.setString(1, produttore);
			ps.setString(2, modello);
			ps.setString(3, descrizione);
			ps.setDouble(4, valore_min);
			ps.setDouble(5, valore_max);
			ps.setString(6, unita_misura);
			ps.setString(7, unita_misura_integrale);
			ps.setDouble(8, coeff_integrale);
			ps.executeUpdate();
			ResultSet rs = ps.getGeneratedKeys();
			if (rs.next())
				ID_tipologia_sensore = rs.getInt(1);
		} else { // UPDATE
			ps = conn.prepareStatement("UPDATE tipologie_sensori SET produttore=?,modello=?,descrizione=?,"
					+ "valore_min=?,valore_max=?,unita_misura=?,unita_misura_integrale=?,"
					+ "coeff_integrale=? WHERE ID_tipologia_sensore=?");
			ps.setString(1, produttore);
			ps.setString(2, modello);
			ps.setString(3, descrizione);
			ps.setDouble(4, valore_min);
			ps.setDouble(5, valore_max);
			ps.setString(6, unita_misura);
			ps.setString(7, unita_misura_integrale);
			ps.setDouble(8, coeff_integrale);
			ps.setInt(9, ID_tipologia_sensore);
			ps.executeUpdate();
		}
	}
}