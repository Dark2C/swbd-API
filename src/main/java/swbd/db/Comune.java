package swbd.db;

import java.sql.Connection;
import java.sql.PreparedStatement;
import java.sql.ResultSet;
import javax.ws.rs.NotFoundException;

public class Comune {
	public String sigla_comune;
	public String nome_comune;
	public String nome_regione;

	public Comune(String sigla) throws Exception {
		Connection conn = Database.Get_Connection();
		PreparedStatement ps = conn.prepareStatement("SELECT * FROM comuni WHERE sigla_comune = ?");
		ps.setString(1, sigla_comune);
		ResultSet res = ps.executeQuery();
		if (!res.next())
			throw new NotFoundException();
		sigla_comune = res.getString("sigla_comune");
		nome_comune = res.getString("nome_comune");
		nome_regione = res.getString("nome_regione");
	}
}