package swbd.db;

import java.sql.Connection;
import java.sql.PreparedStatement;
import java.sql.ResultSet;
import java.sql.Statement;
import java.util.ArrayList;
import java.util.List;

import javax.ws.rs.NotFoundException;
import javax.ws.rs.WebApplicationException;

public class TipologiaAttuatore {
	public int ID_tipologia_attuatore = -1;
	public String produttore;
	public String modello;
	public String descrizione;
	public String tipo_valore;
	public double valore_min;
	public double valore_max;
	public String unita_misura;

	public static TipologiaAttuatore[] getAttuatori() throws Exception {
		Connection conn = Database.Get_Connection();
		PreparedStatement ps = conn.prepareStatement(
				"SELECT ID_tipologia_attuatore FROM tipologie_attuatori ORDER BY ID_tipologia_attuatore ASC");
		ResultSet rs = ps.executeQuery();
		List<TipologiaAttuatore> result = new ArrayList<TipologiaAttuatore>();
		while (rs.next()) {
			result.add(new TipologiaAttuatore(rs.getInt("ID_tipologia_attuatore")));
		}
		return result.toArray(new TipologiaAttuatore[result.size()]);
	}

	public TipologiaAttuatore() {
	}

	public TipologiaAttuatore(int ID) throws Exception {
		Connection conn = Database.Get_Connection();
		PreparedStatement ps = conn
				.prepareStatement("SELECT * FROM tipologie_attuatori WHERE ID_tipologia_attuatore=?");
		ps.setInt(1, ID);
		ResultSet res = ps.executeQuery();
		if (!res.next())
			throw new NotFoundException();
		ID_tipologia_attuatore = res.getInt("ID_tipologia_attuatore");
		produttore = res.getString("produttore");
		modello = res.getString("modello");
		descrizione = res.getString("descrizione");
		tipo_valore = res.getString("tipo_valore");
		valore_min = res.getDouble("valore_min");
		valore_max = res.getDouble("valore_max");
		unita_misura = res.getString("unita_misura");
	}

	public void elimina() throws Exception {
		if (ID_tipologia_attuatore != -1) {
			Connection conn = Database.Get_Connection();
			PreparedStatement ps = conn
					.prepareStatement("DELETE FROM tipologie_attuatori WHERE ID_tipologia_attuatore=?");
			ps.setInt(1, ID_tipologia_attuatore);
			ps.execute();
			ID_tipologia_attuatore = -1;
		}
	}

	public void salva() throws Exception {
		if (valore_min > valore_max)
			throw new WebApplicationException(400);

		if (tipo_valore.equals("int")) {
			valore_min = Math.floor(valore_min);
			valore_max = Math.ceil(valore_max);
		}

		Connection conn = Database.Get_Connection();
		PreparedStatement ps;
		if (ID_tipologia_attuatore == -1) { // INSERT
			ps = conn.prepareStatement(
					"INSERT INTO tipologie_attuatori (produttore,modello,descrizione,tipo_valore,"
							+ "valore_min,valore_max,unita_misura) VALUES (?,?,?,?,?,?,?)",
					Statement.RETURN_GENERATED_KEYS);
			ps.setString(1, produttore);
			ps.setString(2, modello);
			ps.setString(3, descrizione);
			ps.setString(4, tipo_valore);
			ps.setDouble(5, valore_min);
			ps.setDouble(6, valore_max);
			ps.setString(7, unita_misura);
			ps.executeUpdate();
			ResultSet rs = ps.getGeneratedKeys();
			if (rs.next())
				ID_tipologia_attuatore = rs.getInt(1);
		} else { // UPDATE
			ps = conn.prepareStatement("UPDATE tipologie_attuatori SET produttore=?,modello=?,descrizione=?,"
					+ "tipo_valore=?,valore_min=?,valore_max=?,unita_misura=? WHERE ID_tipologia_attuatore=?");
			ps.setString(1, produttore);
			ps.setString(2, modello);
			ps.setString(3, descrizione);
			ps.setString(4, tipo_valore);
			ps.setDouble(5, valore_min);
			ps.setDouble(6, valore_max);
			ps.setString(7, unita_misura);
			ps.setInt(8, ID_tipologia_attuatore);
			ps.executeUpdate();
		}
	}
}