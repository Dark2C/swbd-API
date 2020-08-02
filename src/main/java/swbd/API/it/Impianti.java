package swbd.API.it;

import java.util.Locale;
import javax.ws.rs.GET;
import javax.ws.rs.Path;
import javax.ws.rs.core.Context;
import javax.ws.rs.core.HttpHeaders;
import javax.ws.rs.core.Response;
import com.google.gson.Gson;

//ritorna elenco impianti. ritorna tutti gli impianti nel caso dell'amministratore e monitor, quelli assegnati per tecnico e dipendente

@Path("/impianti")
public class Impianti {
	@Context
	private HttpHeaders httpHeaders;

	/**
	 * Metodo che ritorna la lista degli impianti in base ad un filtro.
	 * @param body filtro per la ricerca dell'impianto. Lo si può cercare in base alla vicinanza o in base alla regione, in tutti gli altri casi in base al comune
	 * @return
	 * @throws Exception
	 */
	@GET
	@Path("/")
	public Response getImpianti(String body) throws Exception {
		if (!Authorization.check(httpHeaders, "logged"))
			return Response.status(403).build();

		swbd.db.Utente currUser = new swbd.db.Utente(Authorization.getCurrentUser(httpHeaders).ID_utente);
		Locale.setDefault(Locale.US);
		Gson gson = new Gson();
		swbd.db.Impianto[] impianti;
		if (body == null || body.equals("")) {
			// non ci sono filtri applicati
			impianti = currUser.getImpianti();
		} else {
			filtroRicercaImpianti filtro = gson.fromJson(body, filtroRicercaImpianti.class);
			if (filtro.vicinanze != null) {
				// cerca per raggio
				impianti = currUser.getImpianti(filtro.vicinanze.latitudine, filtro.vicinanze.longitudine,
						filtro.vicinanze.raggio);
			} else if (filtro.regione != null) {
				// ricerca per regione
				impianti = currUser.getImpianti(filtro.regione);
			} else {
				// ricerca per comune
				impianti = currUser.getImpianti(new swbd.db.Comune(filtro.comune));
			}
		}

		return Response.status(200).entity(gson.toJson(impianti)).build();
	}
}