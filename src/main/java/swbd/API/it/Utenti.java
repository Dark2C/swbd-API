package swbd.API.it;

import java.util.Locale;
import javax.ws.rs.GET;
import javax.ws.rs.Path;
import javax.ws.rs.core.Context;
import javax.ws.rs.core.HttpHeaders;
import javax.ws.rs.core.Response;

import com.google.gson.Gson;

@Path("/utenti")
public class Utenti {
	@Context
	private HttpHeaders httpHeaders;
/**
 * Ritorna elenco Utenti
 * @param body filtro per la ricerca degli utenti
 * @return
 * @throws Exception
 */
	@GET
	@Path("/")
	public Response getUtenti(String body) throws Exception {
		if (!Authorization.check(httpHeaders, "amministratore", "dipendente"))
			return Response.status(403).build();

		if (body == null || body.equals(""))
			body = "{}";

		Locale.setDefault(Locale.US);
		Gson gson = new Gson();
		userFilter filter = gson.fromJson(body, userFilter.class);

		return Response.status(200)
				.entity(gson.toJson(swbd.db.Utente.getUtenti(filter.roles, filter.comune, filter.regione))).build();
	}
}
