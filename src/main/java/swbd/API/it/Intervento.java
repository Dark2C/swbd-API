package swbd.API.it;

import java.text.SimpleDateFormat;
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

import swbd.db.Utente;

@Path("/intervento")
public class Intervento {
	@Context
	private HttpHeaders httpHeaders;
/**
 * Metodo per rimuovere Tecnici dall'intervento
 * @param ID_intervento id intervento
 * @param jsonTecnici lista tecnici
 * @return
 * @throws Exception
 */
	@DELETE
	@Path("/{ID_intervento}/tecnici")
	public Response removeTecniciIntervento(@PathParam("ID_intervento") int ID_intervento, String jsonTecnici)
			throws Exception {
		if (!Authorization.check(httpHeaders, "amministratore"))
			return Response.status(403).build();

		int[] tecnici = new Gson().fromJson(jsonTecnici, int[].class);
		for (int ID_tecnico : tecnici) {
			try {
				(new swbd.db.TecnicoIntervento(ID_tecnico, ID_intervento)).elimina();
			} catch (NotFoundException e) {
				// pass
			}
		}
		return Response.status(200).build();
	}
/**
 * Metodo per rimuove un tecnico con un certo Id dall'intervento con un certo Id. E' un azione concessa solo dall'amministratore.
 * @param ID_intervento ID dell'intervento
 * @param ID_tecnico ID del tecnico
 * @return
 * @throws Exception
 */
	@DELETE
	@Path("/{ID_intervento}/tecnico/{ID_tecnico}")
	public Response removeTecnicoIntervento(@PathParam("ID_intervento") int ID_intervento,
			@PathParam("ID_tecnico") int ID_tecnico) throws Exception {
		if (!Authorization.check(httpHeaders, "amministratore"))
			return Response.status(403).build();

		try {
			(new swbd.db.TecnicoIntervento(ID_tecnico, ID_intervento)).elimina();
		} catch (NotFoundException e) {
			return Response.status(404).build();
		}
		return Response.status(200).build();
	}
/**
 * Metodo per ottenere dettagli intervento
 * @param ID ID dell'intervento
 * @return
 * @throws Exception
 */
	@GET
	@Path("/{ID_intervento}")
	public Response dettagliIntervento(@PathParam("ID_intervento") int ID) throws Exception {
		if (!Authorization.check(httpHeaders, "logged"))
			return Response.status(403).build();

		swbd.db.Intervento intervento = new swbd.db.Intervento(ID);
		swbd.db.Impianto impianto = null;
		try {
			impianto = intervento.getImpianto();
		} catch (Exception e) {
		}
		// se la seguente chiamata genera un'eccezione, non ho i privilegi per accedere
		try {
			if (impianto != null)
				new swbd.db.ImpiantoAssegnato(Authorization.getCurrentUser(httpHeaders).ID_utente,
						impianto.ID_impianto);
		} catch (Exception e) {
			return Response.status(403).build();
		}

		Locale.setDefault(Locale.US);
		return Response.status(200).entity(new Gson().toJson(intervento)).build();
	}
/**
 * Metodo per aggiungere un nuovo intervento
 * @return
 * @throws Exception
 */
	@POST
	@Path("/")
	public Response nuovoIntervento() throws Exception {
		if (!Authorization.check(httpHeaders, "amministratore", "dipendente"))
			return Response.status(403).build();

		swbd.db.Intervento newIntervento = new swbd.db.Intervento();
		newIntervento.salva();

		return Response.status(200).entity("{\"ID\":" + newIntervento.ID_intervento + "}").build();
	}
/**
 * Metodo per la modifica di un intervento con un dato ID.
 * @param ID_intervento
 * @param body 
 * @return
 * @throws Exception
 */
	@POST
	@Path("/{ID_intervento}")
	public Response modificaIntervento(@PathParam("ID_intervento") int ID_intervento, String body) throws Exception {
		if (!Authorization.check(httpHeaders, "amministratore", "tecnico"))
			return Response.status(403).build();

		swbd.db.Intervento intervento = new swbd.db.Intervento(ID_intervento);
		swbd.db.Impianto impianto = null;
		try {
			impianto = intervento.getImpianto();
		} catch (Exception e) {
		}
		// se la seguente chiamata genera un'eccezione, non ho i privilegi per accedere
		try {
			if (impianto != null)
				new swbd.db.ImpiantoAssegnato(Authorization.getCurrentUser(httpHeaders).ID_utente,
						impianto.ID_impianto);
		} catch (Exception e) {
			return Response.status(403).build();
		}

		updateStatoIntervento newStato = new Gson().fromJson(body, updateStatoIntervento.class);
		if (!newStato.stato.equals("inserito")) {
			// se l'intervento non risultava ancora iniziato, aggiorno la data di inizio
			if (intervento.data_inizio == null) {
				SimpleDateFormat sdf = new SimpleDateFormat();
				sdf.applyPattern("yyyy-MM-dd HH:mm:ss");
				intervento.data_inizio = sdf.toString();
			}

			// se il nuovo stato e' uno stato finale, setto la data finale
			if (newStato.stato.equals("risolto") || newStato.stato.equals("non risolvibile")) {
				SimpleDateFormat sdf = new SimpleDateFormat();
				sdf.applyPattern("yyyy-MM-dd HH:mm:ss");
				intervento.data_fine = sdf.toString();
			}
		}
		intervento.stato = newStato.stato;
		intervento.salva();

		return Response.status(200).build();
	}
/**
 * Aggiunta di un nuovo intervento quando viene richiamata un'anomalia.
 * @param ID Id intervento
 * @param body
 * @return
 * @throws Exception
 */
	@POST
	@Path("/{ID_Intervento}/anomalia")
	public Response nuovoIntervento(@PathParam("ID_Intervento") int ID, String body) throws Exception {
		if (!Authorization.check(httpHeaders, "logged"))
			return Response.status(403).build();

		swbd.db.Intervento intervento = new swbd.db.Intervento(ID);
		swbd.db.Impianto impianto = null, impiantoSensore;
		try {
			impianto = intervento.getImpianto();
		} catch (Exception e) {
			// pass, l'intervento ancora non ha anomalie registrate
		}

		swbd.db.Anomalia newAnomalia = new Gson().fromJson(body, swbd.db.Anomalia.class);
		impiantoSensore = new swbd.db.SensoreImpianto(newAnomalia.sensore).getImpianto();
		if (impianto != null) {
			// controllo se l'eventuale nuovo sensore appartiene allo stesso impianto
			// in caso negativo fermo l'esecuzione per evitare incongruenze
			if (impiantoSensore.ID_impianto != impianto.ID_impianto)
				throw new WebApplicationException(409); // CONFLICT
		} else
			impianto = impiantoSensore; // questa e' la prima anomalia inserita nell'intervento

		// verifico che l'utente corrente ha i privilegi di accesso a questo impianto
		// se la seguente chiamata genera un'eccezione, non ho i privilegi per accedere
		try {
			new swbd.db.ImpiantoAssegnato(Authorization.getCurrentUser(httpHeaders).ID_utente, impianto.ID_impianto);
		} catch (Exception e) {
			return Response.status(403).build();
		}

		newAnomalia.ID_anomalia = -1;
		newAnomalia.intervento = ID;
		newAnomalia.salva();

		return Response.status(200).entity("{\"ID\":" + newAnomalia.ID_anomalia + "}").build();
	}
/**
 * Metodo che ritorna le anomalie di un intervento.
 * @param ID ID intervento
 * @return
 * @throws Exception
 */
	@GET
	@Path("/{ID_intervento}/anomalie")
	public Response anomalie(@PathParam("ID_intervento") int ID) throws Exception {
		if (!Authorization.check(httpHeaders, "logged"))
			return Response.status(403).build();

		swbd.db.Intervento intervento = new swbd.db.Intervento(ID);
		swbd.db.Impianto impianto = null;
		try {
			impianto = intervento.getImpianto();
		} catch (Exception e) {
		}
		// se la seguente chiamata genera un'eccezione, non ho i privilegi per accedere
		try {
			if (impianto != null)
				new swbd.db.ImpiantoAssegnato(Authorization.getCurrentUser(httpHeaders).ID_utente,
						impianto.ID_impianto);
		} catch (Exception e) {
			return Response.status(403).build();
		}

		Locale.setDefault(Locale.US);
		swbd.db.Anomalia[] anomalie = intervento.getAnomalie();
		return Response.status(200).entity(new Gson().toJson(anomalie)).build();
	}
/**
 * Metodo che ritorna la lista dei tecnici che lavorano all'intervento.
 * @param ID Id intervento
 * @return
 * @throws Exception
 */
	@GET
	@Path("/{ID_intervento}/tecnici")
	public Response tecnici(@PathParam("ID_intervento") int ID) throws Exception {
		if (!Authorization.check(httpHeaders, "amministratore", "dipendente", "tecnico"))
			return Response.status(403).build();

		swbd.db.Intervento intervento = new swbd.db.Intervento(ID);
		swbd.db.Impianto impianto = null;
		try {
			impianto = intervento.getImpianto();
		} catch (Exception e) {
		}
		// se la seguente chiamata genera un'eccezione, non ho i privilegi per accedere
		try {
			if (impianto != null)
				new swbd.db.ImpiantoAssegnato(Authorization.getCurrentUser(httpHeaders).ID_utente,
						impianto.ID_impianto);
		} catch (Exception e) {
			return Response.status(403).build();
		}

		Locale.setDefault(Locale.US);
		Utente[] tecnici = intervento.getTecnici();
		return Response.status(200).entity(new Gson().toJson(tecnici)).build();
	}
/**
 * Metodo per aggiungere un tecnico ad un intervento. L'operazione è concessa solo dall'amministratore e dal dipendente.
 * @param ID_intervento id dell'intervento
 * @param ID_tecnico id del tecnico
 * @return
 * @throws Exception
 */
	@POST
	@Path("/{ID_intervento}/tecnico/{ID_tecnico}")
	public Response aggiungiTecnicoIntervento(@PathParam("ID_intervento") int ID_intervento,
			@PathParam("ID_tecnico") int ID_tecnico) throws Exception {
		if (!Authorization.check(httpHeaders, "amministratore", "dipendente"))
			return Response.status(403).build();

		swbd.db.Intervento intervento = new swbd.db.Intervento(ID_intervento);
		swbd.db.Impianto impianto = null;
		try {
			impianto = intervento.getImpianto();
		} catch (Exception e) {
		}
		// se la seguente chiamata genera un'eccezione, non ho i privilegi per accedere
		try {
			if (impianto != null)
				new swbd.db.ImpiantoAssegnato(Authorization.getCurrentUser(httpHeaders).ID_utente,
						impianto.ID_impianto);
		} catch (Exception e) {
			return Response.status(403).build();
		}

		swbd.db.TecnicoIntervento relaz = new swbd.db.TecnicoIntervento();
		relaz.tecnico = ID_tecnico;
		relaz.intervento = ID_intervento;
		relaz.salva();

		return Response.status(200).build();
	}
}