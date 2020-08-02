package swbd.API.it;

import javax.servlet.http.HttpServlet;
import javax.ws.rs.DELETE;
import javax.ws.rs.NotFoundException;
import javax.ws.rs.POST;
import javax.ws.rs.Path;
import javax.ws.rs.PathParam;
import javax.ws.rs.core.Context;
import javax.ws.rs.core.HttpHeaders;
import javax.ws.rs.core.Response;

import com.google.gson.Gson;

@Path("/sensore")
public class Sensore extends HttpServlet {
	private static final long serialVersionUID = 1L;
	@Context
	private HttpHeaders httpHeaders;
/**
 * Metodo per rimuovere sensore.
 * @param ID id del sensore
 * @return
 * @throws Exception
 */
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
/**
 *  Metodo per la modifica del sensore.
 * @param ID id del sensore 
 * @param body parametri del sensore
 * @return
 * @throws Exception
 */
	@POST
	@Path("/{ID_sensore}")
	public Response modificaSensore(@PathParam("ID_sensore") int ID, String body) throws Exception {
		if (!Authorization.check(httpHeaders, "amministratore"))
			return Response.status(403).build();
		Gson gson = new Gson();
		swbd.db.SensoreImpianto updatedSensor = gson.fromJson(body, swbd.db.SensoreImpianto.class);
		updatedSensor.ID_sensore_impianto = ID;
		updatedSensor.salva();
		return Response.status(200).build();
	}
}
