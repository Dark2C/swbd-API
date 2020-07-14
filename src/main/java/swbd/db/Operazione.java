package swbd.db;

import java.sql.Connection;
import java.sql.PreparedStatement;
import java.sql.ResultSet;
import java.sql.Statement;
import javax.ws.rs.NotFoundException;

public class Operazione {
	public int ID_operazione = -1;
	public int attuatore;
	public double valore;
	public String data_inserimento;
	public boolean conferma_lettura;

	public Operazione() {
	}

	public Operazione(int ID) throws Exception {
		Connection conn = Database.Get_Connection();
		PreparedStatement ps = conn.prepareStatement("SELECT * FROM operazioni WHERE ID_operazione=?");
		ps.setInt(1, ID);
		ResultSet res = ps.executeQuery();
		if (!res.next())
			throw new NotFoundException();
		ID_operazione = res.getInt("ID_operazione");
		attuatore = res.getInt("attuatore");
		valore = res.getDouble("valore");
		data_inserimento = res.getString("data_inserimento");
		conferma_lettura = res.getBoolean("conferma_lettura");
	}

	public void elimina() throws Exception {
		if (ID_operazione != -1) {
			Connection conn = Database.Get_Connection();
			PreparedStatement ps = conn.prepareStatement("DELETE FROM operazioni WHERE ID_operazione=?");
			ps.setInt(1, ID_operazione);
			ps.execute();
			ID_operazione = -1;
		}
	}

	public void salva() throws Exception {
		Connection conn = Database.Get_Connection();
		PreparedStatement ps;
		if (ID_operazione == -1) { // INSERT
			ps = conn.prepareStatement(
					"INSERT INTO operazioni (attuatore,valore) VALUES (?,?)",
					Statement.RETURN_GENERATED_KEYS);
			ps.setInt(1, attuatore);
			ps.setDouble(2, valore);
			ps.executeUpdate();
			ResultSet rs = ps.getGeneratedKeys();
			if (rs.next())
				ID_operazione = rs.getInt(1);
		} else { // UPDATE
			ps = conn.prepareStatement("UPDATE operazioni SET attuatore=?,valore=?,data_inserimento=?,"
					+ "conferma_lettura=? WHERE ID_operazione=?");
			ps.setInt(1, attuatore);
			ps.setDouble(2, valore);
			ps.setString(3, data_inserimento);
			ps.setBoolean(4, conferma_lettura);
			ps.setInt(5, ID_operazione);
			ps.executeUpdate();
		}
	}
}