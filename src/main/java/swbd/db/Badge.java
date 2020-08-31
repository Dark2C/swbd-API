package swbd.db;

import java.sql.Connection;
import java.sql.PreparedStatement;
import java.sql.ResultSet;
import java.sql.Statement;
import javax.ws.rs.NotFoundException;
import javax.ws.rs.WebApplicationException;

public class Badge {
	public int ID = -1;
	public int ID_utente;
	public int ID_impianto;
	public String data_rilevazione;

	public Badge() {
	}

	public Badge(int ID) throws Exception {
		Connection conn = Database.Get_Connection();
		PreparedStatement ps = conn.prepareStatement("SELECT * FROM badge WHERE ID=?");
		ps.setInt(1, ID);
		ResultSet res = ps.executeQuery();
		if (!res.next())
			throw new NotFoundException();
		this.ID = res.getInt("ID");
		ID_utente = res.getInt("ID_utente");
		ID_impianto = res.getInt("ID_impianto");
		data_rilevazione = res.getString("data_rilevazione");
	}

	public void elimina() throws Exception {
		if (ID != -1) {
			Connection conn = Database.Get_Connection();
			PreparedStatement ps = conn.prepareStatement("DELETE FROM badge WHERE ID=?");
			ps.setInt(1, ID);
			ps.execute();
			ID = -1;
		}
	}
	
	public Utente getTecnico() throws Exception {
		return new Utente(ID_utente);
	}

	public Impianto getImpianto() throws Exception {
		return new Impianto(ID_impianto);
	}
/**
 * Quando un tecnico accede ad un impianto può essere creato solo un nuovo passaggio ma non si possono modificare quelli precedenti.
 * @throws Exception
 */
	// solo new, l'update non ha senso
	public void salva() throws Exception {
		Connection conn = Database.Get_Connection();
		PreparedStatement ps;

		// check che l'utente sia un tecnico
		Utente user = new Utente(ID_utente);
		if (!user.tipologia.equals("tecnico"))
			throw new WebApplicationException(400);

		if (ID == -1) { // INSERT
			ps = conn.prepareStatement("INSERT INTO badge (ID_utente, ID_impianto) VALUES (?,?)",
					Statement.RETURN_GENERATED_KEYS);
			ps.setInt(1, ID_utente);
			ps.setInt(2, ID_impianto);
			ps.executeUpdate();
			ResultSet rs = ps.getGeneratedKeys();
			if (rs.next())
				ID = rs.getInt(1);

			ps = conn.prepareStatement("SELECT * FROM badge WHERE ID=?");
			ps.setInt(1, ID);
			rs = ps.executeQuery();
			data_rilevazione = rs.getString("data_rilevazione");
		}
	}
}
