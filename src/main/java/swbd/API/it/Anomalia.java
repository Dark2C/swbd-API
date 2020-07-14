package swbd.API.it;

import javax.ws.rs.POST;
import javax.ws.rs.Path;
import javax.ws.rs.PathParam;
import javax.ws.rs.WebApplicationException;
import javax.ws.rs.core.Context;
import javax.ws.rs.core.HttpHeaders;
import javax.ws.rs.core.Response;
import com.google.gson.Gson;

@Path("/anomalia")
public class Anomalia {
	@Context
	private HttpHeaders httpHeaders;

	@POST
	@Path("/{ID_Anomalia}")
	public Response nuovoIntervento(@PathParam("ID_Anomalia") int ID, String body) throws Exception {
		if (!Authorization.check(httpHeaders, "amministratore", "dipendente", "tecnico"))
			return Response.status(403).build();

		swbd.db.Anomalia anomalia = new swbd.db.Anomalia(ID);
		swbd.db.Impianto impianto = anomalia.getIntervento().getImpianto();

		// se la seguente chiamata genera un'eccezione, non ho i privilegi per accedere
		try {
			new swbd.db.ImpiantoAssegnato(Authorization.getCurrentUser(httpHeaders).ID_utente, impianto.ID_impianto);
		} catch (Exception e) {
			return Response.status(403).build();
		}

		swbd.db.Anomalia updatedAnomalia = new Gson().fromJson(body, swbd.db.Anomalia.class);
		// controllo se l'eventuale nuovo sensore appartiene allo stesso impianto
		// in caso negativo fermo l'esecuzione per evitare incongruenze
		if (updatedAnomalia.sensore != anomalia.sensore) {
			if (impianto.ID_impianto != updatedAnomalia.getIntervento().getImpianto().ID_impianto)
				throw new WebApplicationException(400);
		}
		updatedAnomalia.ID_anomalia = ID;
		updatedAnomalia.intervento = anomalia.intervento;
		updatedAnomalia.salva();

		return Response.status(200).build();
	}

}
