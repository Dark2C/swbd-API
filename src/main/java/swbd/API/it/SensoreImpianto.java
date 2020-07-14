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

@Path("/sensore")
public class SensoreImpianto {
	@Context
	private HttpHeaders httpHeaders;

	@DELETE
	@Path("/{ID}")
	public Response removeSensore(@PathParam("ID") int ID) throws Exception {
		if (!Authorization.check(httpHeaders, "amministratore"))
			return Response.status(403).build();
		try {
			(new swbd.db.SensoreImpianto(ID)).elimina();
		} catch (NotFoundException e) {
			return Response.status(404).build();
		}
		return Response.status(200).build();
	}

	@POST
	@Path("/{ID_sensore}")
	public Response modificaSensore(@PathParam("ID_sensore") int ID, String body) throws Exception {
		if (!Authorization.check(httpHeaders, "amministratore"))
			return Response.status(403).build();

		swbd.db.SensoreImpianto updatedSensor = new Gson().fromJson(body, swbd.db.SensoreImpianto.class);
		updatedSensor.ID_sensore_impianto = ID;
		updatedSensor.salva();
		return Response.status(200).build();
	}

	@POST
	@Path("/{ID_sensore}/set")
	public Response salvaLetturaAttuatore(@PathParam("ID_sensore") int ID, String body) throws Exception {
		if (!Authorization.check(httpHeaders, "monitor"))
			return Response.status(403).build();

		swbd.db.Lettura newRead = new swbd.db.Lettura();
		newRead.sensore = ID;
		newRead.valore = new Gson().fromJson(body, operazioneAttuatore.class).valore;
		newRead.salva();

		return Response.status(200).build();
	}

	@GET
	@Path("/{ID_sensore}")
	public Response recuperaLetture(@PathParam("ID_sensore") int ID, String body) throws Exception {
		if (!Authorization.check(httpHeaders, "logged"))
			return Response.status(403).build();

		swbd.db.SensoreImpianto sensore = new swbd.db.SensoreImpianto(ID);

		// se la seguente chiamata genera un'eccezione, non ho i privilegi per accedere
		try {
			new swbd.db.ImpiantoAssegnato(Authorization.getCurrentUser(httpHeaders).ID_utente,
					sensore.getImpianto().ID_impianto);
		} catch (Exception e) {
			return Response.status(403).build();
		}

		Locale.setDefault(Locale.US);
		Gson gson = new Gson();

		swbd.db.Lettura[] readings;
		if (body == null || body.equals("")) {
			// body vuoto, ritorna l'ultima lettura
			readings = sensore.getLetture(1);
		} else {
			bodyLettureSensore parsedBody = gson.fromJson(body, bodyLettureSensore.class);
			if (parsedBody.intervallo != null)
				readings = sensore.getLetture(parsedBody.intervallo.inizio, parsedBody.intervallo.fine);
			else
				readings = sensore.getLetture(parsedBody.numeroLetture);
		}

		return Response.status(200).entity(gson.toJson(readings)).build();
	}
}
