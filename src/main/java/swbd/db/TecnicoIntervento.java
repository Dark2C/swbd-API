package swbd.db;

import java.sql.Connection;
import java.sql.PreparedStatement;
import java.sql.ResultSet;
import java.sql.Statement;

import javax.ws.rs.NotFoundException;
import javax.ws.rs.WebApplicationException;

public class TecnicoIntervento {
	public int ID_tecnico_intervento = -1;
	public int tecnico;
	public int intervento;

	public TecnicoIntervento() {
	}

	public TecnicoIntervento(int ID) throws Exception {
		Connection conn = Database.Get_Connection();
		PreparedStatement ps = conn.prepareStatement("SELECT * FROM tecnici_intervento WHERE ID_tecnico_intervento=?");
		ps.setInt(1, ID);
		ResultSet res = ps.executeQuery();
		if (!res.next())
			throw new NotFoundException();
		ID_tecnico_intervento = res.getInt("ID_tecnico_intervento");
		tecnico = res.getInt("utente");
		intervento = res.getInt("intervento");
	}

	public TecnicoIntervento(int ID_tecnico, int ID_intervento) throws Exception {
		Connection conn = Database.Get_Connection();
		PreparedStatement ps = conn
				.prepareStatement("SELECT * FROM tecnici_intervento WHERE utente=? AND intervento=?");
		ps.setInt(1, ID_tecnico);
		ps.setInt(2, ID_intervento);
		ResultSet res = ps.executeQuery();
		if (!res.next())
			throw new NotFoundException();
		ID_tecnico_intervento = res.getInt("ID_tecnico_intervento");
		tecnico = res.getInt("utente");
		intervento = res.getInt("intervento");
	}

	public Utente getTecnico() throws Exception {
		return new Utente(tecnico);
	}

	public Intervento getIntervento() throws Exception {
		return new Intervento(intervento);
	}

	public void elimina() throws Exception {
		if (ID_tecnico_intervento != -1) {
			Connection conn = Database.Get_Connection();
			PreparedStatement ps = conn
					.prepareStatement("DELETE FROM tecnici_intervento WHERE ID_tecnico_intervento=?");
			ps.setInt(1, ID_tecnico_intervento);
			ps.execute();
			ID_tecnico_intervento = -1;
		}
	}

	public void salva() throws Exception {
		Utente tecnicoChk = new Utente(tecnico);
		if (!tecnicoChk.tipologia.equals("tecnico"))
			throw new WebApplicationException(409);

		Connection conn = Database.Get_Connection();
		PreparedStatement ps;
		if (ID_tecnico_intervento == -1) { // INSERT
			ps = conn.prepareStatement("INSERT INTO tecnici_intervento (utente,intervento) VALUES (?,?)",
					Statement.RETURN_GENERATED_KEYS);
			ps.setInt(1, tecnico);
			ps.setInt(2, intervento);
			ps.executeUpdate();
			ResultSet rs = ps.getGeneratedKeys();
			if (rs.next())
				ID_tecnico_intervento = rs.getInt(1);
		} else { // UPDATE
			ps = conn.prepareStatement(
					"UPDATE tecnici_intervento SET utente=?,intervento=? WHERE ID_tecnico_intervento=?");
			ps.setInt(1, tecnico);
			ps.setInt(2, intervento);
			ps.setInt(3, ID_tecnico_intervento);
			ps.executeUpdate();
		}
	}
}