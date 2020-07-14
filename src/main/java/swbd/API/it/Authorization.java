package swbd.API.it;

import java.util.Arrays;
import java.util.List;

import javax.ws.rs.core.HttpHeaders;
import javax.naming.Context;
import javax.naming.InitialContext;
import com.auth0.jwt.JWT;
import com.auth0.jwt.JWTVerifier;
import com.auth0.jwt.algorithms.Algorithm;
import com.auth0.jwt.interfaces.DecodedJWT;
import com.google.gson.Gson;

public class Authorization {
	public static String getJWTtoken(HttpHeaders httpHeaders) {
		String token = httpHeaders.getHeaderString("Cookie");
		if (token != null && token.contains("Authorization=Bearer ")) {
			// esiste un cookie di token: parserizziamolo
			token = token.substring(token.indexOf("Authorization=Bearer ") + "Authorization=Bearer ".length());
			if (token.indexOf(";") > 0) { // more cookies!!11!1!
				token = token.substring(0, token.indexOf(";"));
			}
			return token;
		}
			return null;
	}

	public static String getToken(HttpHeaders httpHeaders) throws Exception {
		Context env = (Context) new InitialContext().lookup("java:comp/env");
		String token = getJWTtoken(httpHeaders);
		if (token != null) {
			try {
				JWTVerifier verifier = JWT.require(Algorithm.HMAC256((String) env.lookup("JWTSecret"))).build();
				DecodedJWT jwt = verifier.verify(token);
				AuthToken jsonObj = new Gson().fromJson(jwt.getSubject(), AuthToken.class);
				return jsonObj.token;
			} catch (Exception exception) {
				return null;
			}
		}
		return null;
	}

	public static boolean check(HttpHeaders httpHeaders, String... allowedRoles) throws Exception {
		/*
		 * ROLES: [empty]: nessuno puo' accedere alla risorsa "all": tutti possono
		 * accedere "guest": solo gli utenti non loggati "logged": tutti gli utenti
		 * loggati di qualsiasi tipo "amministratore", "monitor", "dipendente",
		 * "tecnico"
		 */
		Context env = (Context) new InitialContext().lookup("java:comp/env");
		String token = getJWTtoken(httpHeaders);
		if (token != null) {
			try {
				JWTVerifier verifier = JWT.require(Algorithm.HMAC256((String) env.lookup("JWTSecret"))).build();
				DecodedJWT jwt = verifier.verify(token);
				AuthToken jsonObj = new Gson().fromJson(jwt.getSubject(), AuthToken.class);
				List<String> ruoli = Arrays.asList(allowedRoles);
				if (ruoli.contains(jsonObj.role) || ruoli.contains("all") || ruoli.contains("logged")) {
					// ruolo autorizzato ad accedere alla risorsa, verifichiamo se il token o'
					// valido
					swbd.db.Utente userDb = new swbd.db.Sessione().getUserByToken(jsonObj.token);

					return (userDb != null);
				} else
					return false;
			} catch (Exception exception) {
				return false;
			}
		} else
			return Arrays.asList(allowedRoles).contains("guest"); // utente non loggato
	}

	public static swbd.db.Utente getCurrentUser(HttpHeaders httpHeaders) throws Exception {
		return new swbd.db.Sessione().getUserByToken(Authorization.getToken(httpHeaders));
	}

}