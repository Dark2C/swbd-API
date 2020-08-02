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

@Path("/tipologia")
public class Tipologia {
	@Context
	private HttpHeaders httpHeaders;
/**
 * Metodo per rimuovere la tipologia dell'attuatore con un dato Id.
 * @param ID id tipologia attuatore
 * @return
 * @throws Exception
 */
	@DELETE
	@Path("/attuatore/{ID}")
	public Response removeTipologiaAttuatore(@PathParam("ID") int ID) throws Exception {
		if (!Authorization.check(httpHeaders, "amministratore"))
			return Response.status(403).build();
		try {
			(new swbd.db.TipologiaAttuatore(ID)).elimina();
		} catch (NotFoundException e) {
			return Response.status(404).build();
		}
		return Response.status(200).build();
	}
	/**
	 * Metodo per rimuovere la tipologia del sensore con un dato Id.
	 * @param ID id tipologia sensore
	 * @return
	 * @throws Exception
	 */
	@DELETE
	@Path("/sensore/{ID}")
	public Response removeTipologiaSensore(@PathParam("ID") int ID) throws Exception {
		if (!Authorization.check(httpHeaders, "amministratore"))
			return Response.status(403).build();
		try {
			(new swbd.db.TipologiaSensore(ID)).elimina();
		} catch (NotFoundException e) {
			return Response.status(404).build();
		}
		return Response.status(200).build();
	}
/**
 * Metodoo per modificare la tipologia dell'attuatore.
 * @param ID Id tipologia attuatore
 * @param body parametri tipologia attuatore
 * @return
 * @throws Exception
 */
	@POST
	@Path("/attuatore/{ID}")
	public Response modificaTipologiaAttuatore(@PathParam("ID") int ID, String body) throws Exception {
		if (!Authorization.check(httpHeaders, "amministratore"))
			return Response.status(403).build();

		swbd.db.TipologiaAttuatore updatedTipologiaAttuatore = new Gson().fromJson(body,
				swbd.db.TipologiaAttuatore.class);
		updatedTipologiaAttuatore.ID_tipologia_attuatore = ID;
		updatedTipologiaAttuatore.salva();
		return Response.status(200).build();
	}
	/**
	 * Metodoo per modificare la tipologia del sensore
	 * @param ID Id tipologia sensore
	 * @param body parametri tipologia attuatore
	 * @return
	 * @throws Exception
	 */
	@POST
	@Path("/sensore/{ID}")
	public Response modificaTipologiaSensore(@PathParam("ID") int ID, String body) throws Exception {
		if (!Authorization.check(httpHeaders, "amministratore"))
			return Response.status(403).build();

		swbd.db.TipologiaSensore updatedTipologiaSensore = new Gson().fromJson(body, swbd.db.TipologiaSensore.class);
		updatedTipologiaSensore.ID_tipologia_sensore = ID;
		updatedTipologiaSensore.salva();
		return Response.status(200).build();
	}
/**
 * Crea tipologia attuatori
 * @param body parametri tipologia
 * @return
 * @throws Exception
 */
	@POST
	@Path("/attuatori")
	public Response creaTipologiaAttuatore(String body) throws Exception {
		if (!Authorization.check(httpHeaders, "amministratore"))
			return Response.status(403).build();

		swbd.db.TipologiaAttuatore newTipologiaAttuatore = new Gson().fromJson(body, swbd.db.TipologiaAttuatore.class);
		newTipologiaAttuatore.ID_tipologia_attuatore = -1;
		newTipologiaAttuatore.salva();
		return Response.status(200).entity("{\"ID\":" + newTipologiaAttuatore.ID_tipologia_attuatore + "}").build();
	}
	/**
	 * Crea tipologia sensori
	 * @param body parametri tipologia
	 * @return
	 * @throws Exception
	 */
	@POST
	@Path("/sensori")
	public Response creaTipologiaSensore(String body) throws Exception {
		if (!Authorization.check(httpHeaders, "amministratore"))
			return Response.status(403).build();

		swbd.db.TipologiaSensore newTipologiaSensore = new Gson().fromJson(body, swbd.db.TipologiaSensore.class);
		newTipologiaSensore.ID_tipologia_sensore = -1;
		newTipologiaSensore.salva();
		return Response.status(200).entity("{\"ID\":" + newTipologiaSensore.ID_tipologia_sensore + "}").build();
	}
/**
 * Ritorna tipologie attuatori
 * @return
 * @throws Exception
 */
	@GET
	@Path("/attuatori")
	public Response elencoTipologieAttuatori() throws Exception {
		if (!Authorization.check(httpHeaders, "logged"))
			return Response.status(403).build();

		swbd.db.TipologiaAttuatore[] dettagliAttuatori = swbd.db.TipologiaAttuatore.getAttuatori();
		Locale.setDefault(Locale.US);
		return Response.status(200).entity(new Gson().toJson(dettagliAttuatori)).build();
	}
	/**
	 * Ritorna tipologie sensori
	 * @return
	 * @throws Exception
	 */
	@GET
	@Path("/sensori")
	public Response elencoTipologieSensori() throws Exception {
		if (!Authorization.check(httpHeaders, "logged"))
			return Response.status(403).build();

		swbd.db.TipologiaSensore[] dettagliSensori = swbd.db.TipologiaSensore.getSensori();
		Locale.setDefault(Locale.US);
		return Response.status(200).entity(new Gson().toJson(dettagliSensori)).build();
	}
	/**
	 * Ritorna dettagli del sensore indicato
	 * @param ID id del sensore
	 * @return
	 * @throws Exception
	 */
	@GET
	@Path("/sensore/{ID}")
	public Response dettagliSensoreIndicato(@PathParam("ID") int ID) throws Exception {
		if (!Authorization.check(httpHeaders, "logged"))
			return Response.status(403).build();

		swbd.db.TipologiaSensore dettagliSensoreIndicato = new swbd.db.TipologiaSensore(ID);
		Locale.setDefault(Locale.US);
		return Response.status(200).entity(new Gson().toJson(dettagliSensoreIndicato)).build();
	}
	/**
	 * Ritorna dettagli dell'attuatore indicato
	 * @param ID id del sensore
	 * @return
	 * @throws Exception
	 */
	@GET
	@Path("/attuatore/{ID}")
	public Response dettagliAttuatoreIndicato(@PathParam("ID") int ID) throws Exception {
		if (!Authorization.check(httpHeaders, "logged"))
			return Response.status(403).build();

		swbd.db.TipologiaAttuatore dettagliAttuatoreIndicato = new swbd.db.TipologiaAttuatore(ID);
		Locale.setDefault(Locale.US);
		return Response.status(200).entity(new Gson().toJson(dettagliAttuatoreIndicato)).build();
	}
}