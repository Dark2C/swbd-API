package swbd.db;

import java.sql.Connection;
import java.sql.PreparedStatement;
import java.sql.ResultSet;
import java.sql.Statement;
import javax.ws.rs.NotFoundException;

public class Lettura {
	public int ID_lettura = -1;
	public int sensore;
	public double valore;
	public String data_inserimento;

	public Lettura() {
	}

	public Lettura(int ID) throws Exception {
		Connection conn = Database.Get_Connection();
		PreparedStatement ps = conn.prepareStatement("SELECT * FROM letture WHERE ID_lettura=?");
		ps.setInt(1, ID);
		ResultSet res = ps.executeQuery();
		if (!res.next())
			throw new NotFoundException();
		ID_lettura = res.getInt("ID_lettura");
		sensore = res.getInt("sensore");
		valore = res.getDouble("valore");
		data_inserimento = res.getString("data_inserimento");
	}

	public SensoreImpianto getSensore() throws Exception {
		return new SensoreImpianto(sensore);
	}

	public void elimina() throws Exception {
		if (ID_lettura != -1) {
			Connection conn = Database.Get_Connection();
			PreparedStatement ps = conn.prepareStatement("DELETE FROM letture WHERE ID_lettura=?");
			ps.setInt(1, ID_lettura);
			ps.execute();
			ID_lettura = -1;
		}
	}
	/**
	 * Aggiorna le informazioni sulla lettura e distingue il caso in cui debba esser creata o solo aggiornata.
	 * @throws Exception
	 */
	public void salva() throws Exception {
		Connection conn = Database.Get_Connection();
		PreparedStatement ps;
		if (ID_lettura == -1) { // INSERT
			ps = conn.prepareStatement("INSERT INTO letture (sensore,valore,data_inserimento) VALUES (?,?,?)",
					Statement.RETURN_GENERATED_KEYS);
			ps.setInt(1, sensore);
			ps.setDouble(2, valore);
			ps.setString(3, data_inserimento);
			ps.executeUpdate();
			ResultSet rs = ps.getGeneratedKeys();
			if (rs.next())
				ID_lettura = rs.getInt(1);
		} else { // UPDATE
			ps = conn.prepareStatement("UPDATE letture SET sensore=?,valore=?,data_inserimento=? WHERE ID_lettura=?");
			ps.setInt(1, sensore);
			ps.setDouble(2, valore);
			ps.setString(3, data_inserimento);
			ps.setInt(4, ID_lettura);
			ps.executeUpdate();
		}
	}
}