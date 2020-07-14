package swbd.API.it;

import javax.ws.rs.Consumes;
import javax.ws.rs.GET;
import javax.ws.rs.POST;
import javax.ws.rs.Path;
import javax.ws.rs.Produces;
import javax.ws.rs.core.Context;
import javax.ws.rs.core.HttpHeaders;
import javax.ws.rs.core.MediaType;
import javax.ws.rs.core.Response;
import javax.naming.InitialContext;
import com.auth0.jwt.JWT;
import com.auth0.jwt.algorithms.Algorithm;
import com.google.gson.Gson;

@Path("/sessione")
public class Sessione {
	@Context
	private HttpHeaders httpHeaders;

	@POST
	@Path("/login")
	@Produces("application/json")
	public Response doLogin(String body) throws Exception {
		if (Authorization.check(httpHeaders, "logged"))
			return getJWT();

		Gson gson = new Gson();
		LoginData jsonObj = gson.fromJson(body, LoginData.class);
		swbd.db.Sessione sessDb = new swbd.db.Sessione();
		String token = sessDb.generateToken(jsonObj.email, jsonObj.username, jsonObj.password);

		if (token != null) {
			// ricavo l'utente che ha effettuato il login
			swbd.db.Utente userDb = sessDb.getUserByToken(token);

			// creo un token da restituire al client
			AuthToken retToken = new AuthToken();
			retToken.token = token;
			retToken.userID = userDb.ID_utente;
			retToken.role = userDb.tipologia;
			String json = gson.toJson(retToken);

			javax.naming.Context env = (javax.naming.Context) new InitialContext().lookup("java:comp/env");
			// firmo il token con JWT
			Algorithm algorithm = Algorithm.HMAC256((String) env.lookup("JWTSecret"));
			String JWTtoken = JWT.create().withSubject(json).sign(algorithm);

			return Response.status(200)
					.header("Set-Cookie", "Authorization=Bearer " + JWTtoken + "; Path=/API/; HttpOnly")
					.entity("{\"JWT\":\"" + JWTtoken + "\"}").build();
		}
		return Response.status(403).build();
	}

	@GET
	@Path("/logout")
	@Produces("application/json")
	public Response doLogout() throws Exception {
		if (!Authorization.check(httpHeaders, "logged"))
			return Response.status(403).build();

		new swbd.db.Sessione().invalidateToken(Authorization.getToken(httpHeaders));

		return Response.status(200)
				.header("Set-Cookie", "Authorization=; Path=/API/; HttpOnly; Expires=Thu, 01 Jan 1970 00:00:00 GMT")
				.build();
	}

	@GET
	@Path("/JWT")
	@Produces("application/json")
	@Consumes(MediaType.APPLICATION_JSON)
	public Response getJWT() throws Exception {
		if (!Authorization.check(httpHeaders, "logged"))
			return Response.status(403).build();
		return Response.status(200).entity("{\"JWT\":\"" + Authorization.getJWTtoken(httpHeaders) + "\"}").build();
	}
}
