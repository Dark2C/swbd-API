package swbd.API.it;

import javax.ws.rs.DELETE;
import javax.ws.rs.NotFoundException;
import javax.ws.rs.POST;
import javax.ws.rs.Path;
import javax.ws.rs.PathParam;
import javax.ws.rs.core.Context;
import javax.ws.rs.core.HttpHeaders;
import javax.ws.rs.core.Response;
import com.google.gson.Gson;

@Path("/dipendente")
public class Dipendente {
	@Context
	private HttpHeaders httpHeaders;
/**
 * Metodo per rimuovere impianto assegnato al dipendente.
 * @param ID_dipendente id del dipendende
 * @param jsonImpianti impianti accessibili dall'utente.
 * @return
 * @throws Exception
 */
	@DELETE
	@Path("/{ID_dipendente}/impianti")
	public Response removeImpiantiDipendente(@PathParam("ID_dipendente") int ID_dipendente, String jsonImpianti)
			throws Exception {
		if (!Authorization.check(httpHeaders, "amministratore"))
			return Response.status(403).build();

		int[] impianti = new Gson().fromJson(jsonImpianti, int[].class);
		for (int ID_impianto : impianti) {
			try {
				(new swbd.db.ImpiantoAssegnato(ID_dipendente, ID_impianto)).elimina();
			} catch (NotFoundException e) {
				// pass
			}
		}
		return Response.status(200).build();
	}
	/**
	 * Metodo per rimuovere un dato impianto assegnato al dipendente.
	 * @param ID_dipendente id del dipendende
	 * @param ID_impianto
	 * @return
	 * @throws Exception
	 */
	@DELETE
	@Path("/{ID_dipendente}/impianto/{ID_impianto}")
	public Response removeImpiantoDipendente(@PathParam("ID_dipendente") int ID_dipendente,
			@PathParam("ID_impianto") int ID_impianto) throws Exception {
		if (!Authorization.check(httpHeaders, "amministratore"))
			return Response.status(403).build();
		try {
			new swbd.db.ImpiantoAssegnato(ID_dipendente, ID_impianto).elimina();
		} catch (NotFoundException e) {
			return Response.status(404).build();
		}
		return Response.status(200).build();
	}
/**
 * Metodo per aggiungere impianti assegnati al dipendente.
 * @param ID_dipendente
 * @param body
 * @return
 * @throws Exception
 */
	@POST
	@Path("/{ID_dipendente}/impianti")
	public Response addImpiantiDipendente(@PathParam("ID_dipendente") int ID_dipendente, String body) throws Exception {
		if (!Authorization.check(httpHeaders, "amministratore"))
			return Response.status(403).build();

		authImpiantoDipendente[] impianti = new Gson().fromJson(body, authImpiantoDipendente[].class);

		swbd.db.ImpiantoAssegnato relaz;
		for (authImpiantoDipendente impianto : impianti) {
			try {
				// provo a fare un update
				relaz = new swbd.db.ImpiantoAssegnato(ID_dipendente, impianto.ID);
				relaz.permesso_scrittura = impianto.modificabile ? 1 : 0;
				relaz.salva();
			} catch (Exception e) {
				// devo fare un inserimento
				relaz = new swbd.db.ImpiantoAssegnato();
				relaz.ID_utente = ID_dipendente;
				relaz.ID_impianto = impianto.ID;
				relaz.permesso_scrittura = impianto.modificabile ? 1 : 0;
				relaz.salva();
			}
		}

		return Response.status(200).build();
	}
	/**
	 * Metodo per aggiungere un dato impianto alla lista di quelli accessibili dall'utente.
	 * Si aggiungono anche i permessi di scrittura. 
	 * E' un operazione che può fare solo l'amministratore.
	 * @param ID_dipendente id del dipendente
	 * @param ID_impiant	id_dell'impianto
	 * @param body
	 * @return
	 * @throws Exception
	 */
	@POST
	@Path("/{ID_dipendente}/impianto/{ID_impianto}")
	public Response addImpiantoDipendente(@PathParam("ID_dipendente") int ID_dipendente,
			@PathParam("ID_impianto") int ID_impianto, String body) throws Exception {
		if (!Authorization.check(httpHeaders, "amministratore"))
			return Response.status(403).build();

		permessiImpianto isWriteable = new Gson().fromJson(body, permessiImpianto.class);

		swbd.db.ImpiantoAssegnato relaz;
		try {
			// provo a fare un update
			relaz = new swbd.db.ImpiantoAssegnato(ID_dipendente, ID_impianto);
		} catch (Exception e) {
			// devo fare un inserimento
			relaz = new swbd.db.ImpiantoAssegnato();
			relaz.ID_utente = ID_dipendente;
			relaz.ID_impianto = ID_impianto;
		}
		relaz.permesso_scrittura = isWriteable.modificabile ? 1 : 0;
		relaz.salva();

		return Response.status(200).build();
	}
}