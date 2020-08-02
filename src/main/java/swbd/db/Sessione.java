package swbd.db;

import java.security.MessageDigest;
import java.sql.Connection;
import java.sql.PreparedStatement;
import java.sql.ResultSet;
import java.text.SimpleDateFormat;
import javax.xml.bind.DatatypeConverter;
import javax.naming.Context;
import javax.naming.InitialContext;
import org.mindrot.jbcrypt.BCrypt;

public class Sessione {
	/**
	 * Ritorna il token dell'utente. seleziona l'ID utente associato al token, se esso non e' scaduto aggiorna la data di ultima operazione del token dell'utente. In caso contrario elimina 
	 * la sessione dal database. 
	 * @param token
	 * @return
	 * @throws Exception
	 */
	public Utente getUserByToken(String token) throws Exception {
		if (token == null || token.length() != 128)
			return null;

		Context env = (Context) new InitialContext().lookup("java:comp/env");

		Connection conn = Database.Get_Connection();
		// seleziona l'ID utente associato al token, se esso non e' scaduto
		PreparedStatement ps = conn.prepareStatement(
				"SELECT ID_utente FROM sessioni_attive WHERE token=? AND data_ultima_operazione >= DATE_SUB(NOW(), INTERVAL ? SECOND)");
		ps.setString(1, token);
		ps.setInt(2, (Integer) env.lookup("tokenDuration"));
		ResultSet res = ps.executeQuery();
		if (!res.next()) {
			// token non valido/scaduto
			invalidateToken(token); // elimino l'eventuale token scaduto dal db
			return null;
		} else {
			// aggiorna la data di ultima operazione del token dell'utente che ha effettuato
			// la verifica
			ps = conn.prepareStatement(
					"UPDATE sessioni_attive SET data_ultima_operazione = NOW() WHERE token=? LIMIT 1");
			ps.setString(1, token);
			ps.execute();

			return new Utente(res.getInt(1)); // ritorna l'utente corrispondete al dato ID
		}
	}
/**
 * Metodo per invalidare token. Se il token non è valido bisogna anche elimanare la sessione dal database.
 * @param token
 * @throws Exception
 */
	public void invalidateToken(String token) throws Exception {
		if (token == null || token.length() != 128)
			return;

		Connection conn = Database.Get_Connection();
		PreparedStatement ps = conn.prepareStatement("DELETE FROM sessioni_attive WHERE token=?");
		ps.setString(1, token);
		ps.execute();
	}
/**
 * Metodo per genare token. Si elimano gli eventuali token scaduti. selezione l'eventuale utente (solo se attivo) individuato da email o username e
 * genera una stringa (token) di 128 caratteri sia casuali che dipende dall'utente e dall'orario della richiesta. Salva il token sul database e lo restituisce al chiamante.
 * @param email
 * @param username
 * @param password
 * @return
 * @throws Exception
 */
	public String generateToken(String email, String username, String password) throws Exception {
		if (((email == null || email.equals("")) && (username == null || username.equals("")))
				|| (password == null || password.equals("")))
			return null;

		Context env = (Context) new InitialContext().lookup("java:comp/env");

		Connection conn = Database.Get_Connection();

		// elimina eventuali token scaduti (FLUSH)
		PreparedStatement ps = conn.prepareStatement(
				"DELETE FROM sessioni_attive WHERE data_ultima_operazione < DATE_SUB(NOW(), INTERVAL ? SECOND);");
		ps.setInt(1, (Integer) env.lookup("tokenDuration"));
		ps.execute();

		// selezione l'eventuale utente (solo se attivo) individuato da email o username
		ps = conn.prepareStatement("SELECT ID_utente, password FROM utenti WHERE "
				+ (!(email == null || email.equals("")) ? "email" : "username")
				+ "=? AND stato_account = 'attivo' LIMIT 1");
		ps.setString(1, ((!(email == null || email.equals("")) ? email : username)));
		ResultSet res = ps.executeQuery();

		if (!res.next()) {
			// utente inesistente, impossibile creare un token
			return null;
		} else {
			if (BCrypt.checkpw(password, res.getString(2))) { // se la password e' corretta
				// genera una stringa (token) di 128 caratteri sia casuali che dipendenti
				// dall'utente e dall'orario della richiesta
				String allowedChars = "ABCDEFGHIJKLMNOPQRSTUVWXYZabcdefghijklmnopqrstuvwxyz1234567890";
				StringBuilder builder = new StringBuilder();

				// il token comincera' con un hash md5 ricavato dalla data attuale e dall'ID del
				// richiedente
				MessageDigest md = MessageDigest.getInstance("MD5");
				md.update((new SimpleDateFormat("yyyyMMddHHmmss").format(new java.util.Date()) + res.getInt(1))
						.getBytes());
				builder.append(DatatypeConverter.printHexBinary(md.digest()).toUpperCase());

				// e proseguira' con caratteri casuali
				while (builder.length() < 128) {
					builder.append(allowedChars.charAt((int) (Math.random() * allowedChars.length())));
				}
				String token = builder.toString();

				// salva il token sul database
				ps = conn.prepareStatement("INSERT INTO sessioni_attive (token, ID_utente) VALUES (?,?)");
				ps.setString(1, token);
				ps.setInt(2, res.getInt(1));
				ps.execute();

				// e restituiscilo al chiamante
				return token;
			} else
				return null;
		}
	}
}