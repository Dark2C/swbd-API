package swbd.API.it;

import java.util.Locale;
import javax.ws.rs.DELETE;
import javax.ws.rs.NotFoundException;
import javax.ws.rs.GET;
import javax.ws.rs.POST;
import javax.ws.rs.Path;
import javax.ws.rs.PathParam;
import javax.ws.rs.core.Context;
import javax.ws.rs.core.HttpHeaders;
import javax.ws.rs.core.Response;

import com.google.gson.Gson;

@Path("/utente")
public class Utente {
	@Context
	private HttpHeaders httpHeaders;
/**
 * Rimuove utente con un dato ID.
 * @param ID
 * @return
 * @throws Exception
 */
	@DELETE
	@Path("/{ID}")
	public Response removeUtente(@PathParam("ID") int ID) throws Exception {
		if (!Authorization.check(httpHeaders, "amministratore"))
			return Response.status(403).build();
		try {
			(new swbd.db.Utente(ID)).elimina();
		} catch (NotFoundException e) {
			return Response.status(404).build();
		}
		return Response.status(200).build();
	}
/**
 * Modifica i parametri di un utente corrente o di un altro utente. Quest'ultima operazione è concessa solo all'amministratore.
 * @param user Identificativo utente
 * @param body
 * @return
 * @throws Exception
 */
	@POST
	@Path("/{user}")
	public Response modificaUtente(@PathParam("user") String user, String body) throws Exception {
		if (!Authorization.check(httpHeaders, "amministratore") && !user.equals("self"))
			return Response.status(403).build();
		int ID;
		if (user.equals("self"))
			ID = Authorization.getCurrentUser(httpHeaders).ID_utente;
		else
			ID = Integer.parseInt(user); // user e' un ID

		swbd.db.Utente oldUser = new swbd.db.Utente(ID);

		swbd.db.Utente updatedUser = new Gson().fromJson(body, swbd.db.Utente.class);
		updatedUser.ID_utente = ID;
		if (user.equals("self")) {
			updatedUser.stato_account = oldUser.stato_account;
			updatedUser.tipologia = oldUser.tipologia;
		}
		updatedUser.salva();

		return Response.status(200).build();
	}
/**
 * Ritorna i dettagli dell'utente corrente o di un altro utente. Quest'ultima operazione è concessa solo all'amministratore.
 * @param user Utente
 * @return
 * @throws Exception
 */
	@GET
	@Path("/{user}")
	public Response dettagliUtente(@PathParam("user") String user) throws Exception {
		if (!Authorization.check(httpHeaders, "amministratore") && !user.equals("self"))
			return Response.status(403).build();
		int ID;
		if (user.equals("self"))
			ID = Authorization.getCurrentUser(httpHeaders).ID_utente;
		else
			ID = Integer.parseInt(user); // user e' un ID

		Locale.setDefault(Locale.US);
		return Response.status(200).entity((new Gson()).toJson(new swbd.db.Utente(ID))).build();
	}
/**
 * Ritorna impianti accessibili dall'utente.
 * @param user Utente
 * @return
 * @throws Exception
 */
	@GET
	@Path("/{user}/impianti")
	public Response impiantiUtente(@PathParam("user") String user) throws Exception {
		if (!Authorization.check(httpHeaders, "amministratore") && !user.equals("self"))
			return Response.status(403).build();
		int ID;
		if (user.equals("self"))
			ID = Authorization.getCurrentUser(httpHeaders).ID_utente;
		else
			ID = Integer.parseInt(user); // user e' un ID

		Locale.setDefault(Locale.US);
		return Response.status(200).entity((new Gson()).toJson(new swbd.db.Utente(ID).getImpianti())).build();
	}
/**
 * Ritorna la lista degli accessi dell'utente corrente o di un altro utente. Quest'ultima operazione è concessa solo all'amministratore.
 * @param user Utente
 * @param body Filtro per la ricerca degli accessi
 * @return
 * @throws Exception
 */
	@GET
	@Path("/{user}/accessi")
	public Response accessiUtente(@PathParam("user") String user, String body) throws Exception {
		if (!Authorization.check(httpHeaders, "amministratore") && !user.equals("self"))
			return Response.status(403).build();
		int ID;
		if (user.equals("self"))
			ID = Authorization.getCurrentUser(httpHeaders).ID_utente;
		else
			ID = Integer.parseInt(user); // user e' un ID

		
		Locale.setDefault(Locale.US);
		Gson gson = new Gson();
		intervallo filtro = gson.fromJson(body, intervallo.class);
		
		return Response.status(200).entity((new Gson()).toJson(new swbd.db.Utente(ID).getAccessi(filtro.inizio, filtro.fine))).build();
	}

	@POST
	@Path("/")
	public Response creaUtente(String body) throws Exception {
		if (!Authorization.check(httpHeaders, "amministratore"))
			return Response.status(403).build();

		swbd.db.Utente newUser = (new Gson()).fromJson(body, swbd.db.Utente.class);
		newUser.ID_utente = -1;
		newUser.salva();

		return Response.status(200).entity("{\"ID\":" + newUser.ID_utente + "}").build();
	}
}
