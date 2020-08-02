package swbd.API.it;

import java.util.Locale;
import javax.ws.rs.DELETE;
import javax.ws.rs.GET;
import javax.ws.rs.NotFoundException;
import javax.ws.rs.POST;
import javax.ws.rs.Path;
import javax.ws.rs.PathParam;
import javax.ws.rs.WebApplicationException;
import javax.ws.rs.core.Context;
import javax.ws.rs.core.HttpHeaders;
import javax.ws.rs.core.Response;
import com.google.gson.Gson;

@Path("/attuatore")
public class AttuatoreImpianto {
	@Context
	private HttpHeaders httpHeaders;
/**
 *  Metodo per rimuovere l'attuatore di un impianto.
 * @param ID ID_attuatore
 * @return
 * @throws Exception
 */
	@DELETE
	@Path("/{ID}")
	public Response removeAttuatore(@PathParam("ID") int ID) throws Exception {
		if (!Authorization.check(httpHeaders, "amministratore"))
			return Response.status(403).build();
		try {
			new swbd.db.AttuatoreImpianto(ID).elimina();
		} catch (NotFoundException e) {
			return Response.status(404).build();
		}
		return Response.status(200).build();
	}
/**
 * Metodo per modificate i parametri dell'attuatore Impianto. E' un azione concessa solo dall'amministratore.
 * @param ID ID_attuatore
 * @param body descrizione
 * @return
 * @throws Exception
 */
	@POST
	@Path("/{ID_attuatore}")
	public Response modificaAttuatoreImpianto(@PathParam("ID_attuatore") int ID, String body) throws Exception {
		if (!Authorization.check(httpHeaders, "amministratore"))
			return Response.status(403).build();

		swbd.db.AttuatoreImpianto updatedAttuatore = new Gson().fromJson(body, swbd.db.AttuatoreImpianto.class);
		updatedAttuatore.ID_attuatore_impianto = ID;
		updatedAttuatore.salva();

		return Response.status(200).build();
	}
	/**
	 * Metodo per salvare le letture dell'attuatore Impianto. E' un azione concessa solo dall'amministratore.
	 * @param ID ID_attuatore
	 * @param body
	 * @return
	 * @throws Exception
	 */
	@POST
	@Path("/{ID_attuatore}/set")
	public Response salvaLetturaAttuatore(@PathParam("ID_attuatore") int ID, String body) throws Exception {
		if (!Authorization.check(httpHeaders, "amministratore"))
			return Response.status(403).build();

		swbd.db.Utente currUser = Authorization.getCurrentUser(httpHeaders);
		// controllo se ho i permessi per scrivere
		if (new swbd.db.ImpiantoAssegnato(currUser.ID_utente,
				new swbd.db.AttuatoreImpianto(ID).getImpianto().ID_impianto).permesso_scrittura <= 0)
			throw new WebApplicationException(403);

		swbd.db.Operazione newOp = new swbd.db.Operazione();
		newOp.attuatore = ID;
		newOp.valore = new Gson().fromJson(body, operazioneAttuatore.class).valore;
		newOp.salva();

		return Response.status(200).build();
	}
/** 
 * Metodo per recuperare le letture dell'Attuatore. Il parametro body permette di richiamare alcune 
 * operazioni: se body vuoto, ritorna le operazioni non confermate e, se l'utente e' monitor, confermale.
 * @param ID ID_attuatore
 * @param body 
 * @return
 * @throws Exception
 */
	@GET
	@Path("/{ID_attuatore}")
	public Response recuperaLetture(@PathParam("ID_attuatore") int ID, String body) throws Exception {
		if (!Authorization.check(httpHeaders, "logged"))
			return Response.status(403).build();

		swbd.db.AttuatoreImpianto attuatore = new swbd.db.AttuatoreImpianto(ID);

		// se la seguente chiamata genera un'eccezione, non ho i privilegi per accedere
		try {
			new swbd.db.ImpiantoAssegnato(Authorization.getCurrentUser(httpHeaders).ID_utente,
					attuatore.getImpianto().ID_impianto);
		} catch (Exception e) {
			return Response.status(403).build();
		}

		Locale.setDefault(Locale.US);
		Gson gson = new Gson();

		swbd.db.Operazione[] ops;
		if (body == null || body.equals("")) {
			// body vuoto, ritorna le operazioni non confermate e,
			// se l'utente e' monitor, confermale
			ops = attuatore.getOperazioni(Authorization.getCurrentUser(httpHeaders).tipologia == "monitor");
		} else {
			bodyOperazioniAttuatore parsedBody = gson.fromJson(body, bodyOperazioniAttuatore.class);
			if (parsedBody.intervallo != null)
				ops = attuatore.getOperazioni(parsedBody.intervallo.inizio, parsedBody.intervallo.fine);
			else
				ops = attuatore.getOperazioni(parsedBody.numeroOperazioni);
		}

		return Response.status(200).entity(gson.toJson(ops)).build();
	}
}

