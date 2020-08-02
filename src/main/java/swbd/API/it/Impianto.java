package swbd.API.it;

import java.util.Locale;
import javax.ws.rs.DELETE;
import javax.ws.rs.GET;
import javax.ws.rs.NotFoundException;
import javax.ws.rs.POST;
import javax.ws.rs.Path;
import javax.ws.rs.PathParam;
import javax.ws.rs.core.Context;
import javax.ws.rs.core.HttpHeaders;
import javax.ws.rs.core.Response;
import com.google.gson.Gson;

@Path("/impianto")
public class Impianto {
	@Context
	private HttpHeaders httpHeaders;
/**
 *  Metodo per rimuovere impianto
 * @param ID id impianto
 * @return
 * @throws Exception
 */
	@DELETE
	@Path("/{ID}")
	public Response removeImpianto(@PathParam("ID") int ID) throws Exception {
		if (!Authorization.check(httpHeaders, "amministratore"))
			return Response.status(403).build();
		try {
			new swbd.db.Impianto(ID).elimina();
		} catch (NotFoundException e) {
			return Response.status(404).build();
		}
		return Response.status(200).build();
	}
/**
 * Metodo per la modifica dell'impianto.
 * @param ID ID_impianto
 * @param body
 * @return
 * @throws Exception
 */
	@POST
	@Path("/{ID}")
	public Response modificaImpianto(@PathParam("ID") int ID, String body) throws Exception {
		if (!Authorization.check(httpHeaders, "amministratore"))
			return Response.status(403).build();

		swbd.db.Impianto editedPlant = new Gson().fromJson(body, swbd.db.Impianto.class);
		editedPlant.ID_impianto = ID;
		editedPlant.salva();

		return Response.status(200).build();
	}
/**
 * Metodo per la creazione di un nuovo impianto
 * @param body
 * @return
 * @throws Exception
 */
	@POST
	@Path("/")
	public Response creaImpianto(String body) throws Exception {
		if (!Authorization.check(httpHeaders, "amministratore"))
			return Response.status(403).build();

		swbd.db.Impianto newPlant = new Gson().fromJson(body, swbd.db.Impianto.class);
		newPlant.ID_impianto = -1;
		newPlant.salva();

		return Response.status(200).entity("{\"ID\":" + newPlant.ID_impianto + "}").build();
	}

	@GET
	@Path("/{ID}/sensori")
	public Response sensoriImpianto(@PathParam("ID") int ID) throws Exception {
		if (!Authorization.check(httpHeaders, "logged"))
			return Response.status(403).build();

		// se la seguente chiamata genera un'eccezione, non ho i privilegi per accedere
		try {
			new swbd.db.ImpiantoAssegnato(Authorization.getCurrentUser(httpHeaders).ID_utente, ID);
		} catch (Exception e) {
			return Response.status(403).build();
		}
		Locale.setDefault(Locale.US);
		return Response.status(200).entity(new Gson().toJson(new swbd.db.Impianto(ID).getSensori())).build();
	}
/**
 * Metodo che ritorna gli attuatori di un impianto
 * @param ID ID impianto
 * @return
 * @throws Exception
 */
	@GET
	@Path("/{ID}/attuatori")
	public Response attuatoriImpianto(@PathParam("ID") int ID) throws Exception {
		if (!Authorization.check(httpHeaders, "logged"))
			return Response.status(403).build();

		// se la seguente chiamata genera un'eccezione, non ho i privilegi per accedere
		try {
			new swbd.db.ImpiantoAssegnato(Authorization.getCurrentUser(httpHeaders).ID_utente, ID);
		} catch (Exception e) {
			return Response.status(403).build();
		}
		Locale.setDefault(Locale.US);
		return Response.status(200).entity(new Gson().toJson(new swbd.db.Impianto(ID).getAttuatori())).build();
	}
/**
 * Metodo che ritorna gli interventi di un impianto. 
 * @param ID ID Impianto
 * @param body intervallo di ricerca degli interventi
 * @return
 * @throws Exception
 */
	@GET
	@Path("/{ID}/interventi")
	public Response interventiImpianto(@PathParam("ID") int ID, String body) throws Exception {
		if (!Authorization.check(httpHeaders, "logged"))
			return Response.status(403).build();

		// se la seguente chiamata genera un'eccezione, non ho i privilegi per accedere
		try {
			new swbd.db.ImpiantoAssegnato(Authorization.getCurrentUser(httpHeaders).ID_utente, ID);
		} catch (Exception e) {
			return Response.status(403).build();
		}

		Locale.setDefault(Locale.US);
		Gson gson = new Gson();
		bodyInterventoImpianto parsedBody = new bodyInterventoImpianto();
		parsedBody.intervallo = new intervallo();
		if (body != null && !body.equals("")) {
			parsedBody = gson.fromJson(body, bodyInterventoImpianto.class);
		}
		return Response.status(200).entity(gson.toJson(
				new swbd.db.Impianto(ID).getInterventi(parsedBody.intervallo.inizio, parsedBody.intervallo.fine)))
				.build();
	}
/**
 * Metodo per aggiungere un attuatore di una data tipologia all'impianto.E' un azione concessa solo dall'amministratore.
 * @param ID_impianto id dell'impianto
 * @param ID_tipologia id della tipologia attuatore
 * @return
 * @throws Exception
 */
	@POST
	@Path("/{ID_impianto}/attuatore/{ID_tipologia}")
	public Response aggiungiAttuatore(@PathParam("ID_impianto") int ID_impianto,
			@PathParam("ID_tipologia") int ID_tipologia) throws Exception {
		if (!Authorization.check(httpHeaders, "amministratore"))
			return Response.status(403).build();

		swbd.db.AttuatoreImpianto newAttuatore = new swbd.db.AttuatoreImpianto();
		newAttuatore.ID_attuatore_impianto = -1;
		newAttuatore.impianto = ID_impianto;
		newAttuatore.tipologia = ID_tipologia;
		newAttuatore.salva();

		return Response.status(200).entity("{\"ID\":" + newAttuatore.ID_attuatore_impianto + "}").build();
	}
/**
	 * Metodo per aggiungere un sensore di una data tipologia all'impianto.E' un azione concessa solo dall'amministratore.
	 * @param ID_impianto id dell'impianto
	 * @param ID_tipologia id della tipologia sensore
	 * @return
	 * @throws Exception
	 */
	@POST
	@Path("/{ID_impianto}/sensore/{ID_tipologia}")
	public Response aggiungiSensore(@PathParam("ID_impianto") int ID_impianto,
			@PathParam("ID_tipologia") int ID_tipologia) throws Exception {
		if (!Authorization.check(httpHeaders, "amministratore"))
			return Response.status(403).build();

		swbd.db.SensoreImpianto newSensore = new swbd.db.SensoreImpianto();
		newSensore.impianto = ID_impianto;
		newSensore.tipologia = ID_tipologia;
		newSensore.salva();

		return Response.status(200).entity("{\"ID\":" + newSensore.ID_sensore_impianto + "}").build();
	}
	/**
	 * Metodo per aggiungere il passaggio con badge ad un impianto da parte del tecnico. 
	 * E' un operazione concessa solo dal monitor.
	 * @param ID_impianto id impianto
	 * @param ID_tecnico id_tecnico
	 * @return
	 * @throws Exception
	 */
	@POST
	@Path("/{ID_impianto}/tecnico/{ID_tecnico}/badge")
	public Response aggiungiBadgata(@PathParam("ID_impianto") int ID_impianto, @PathParam("ID_tecnico") int ID_tecnico)
			throws Exception {
		if (!Authorization.check(httpHeaders, "monitor"))
			return Response.status(403).build();

		swbd.db.Badge badge = new swbd.db.Badge();
		badge.ID_utente = ID_tecnico;
		badge.ID_impianto = ID_impianto;
		badge.salva();

		return Response.status(200).build();
	}
}