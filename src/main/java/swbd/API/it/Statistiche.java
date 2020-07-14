package swbd.API.it;

import java.util.ArrayList;
import java.util.List;
import java.util.Locale;
import javax.ws.rs.GET;
import javax.ws.rs.Path;
import javax.ws.rs.PathParam;
import javax.ws.rs.core.Context;
import javax.ws.rs.core.HttpHeaders;
import javax.ws.rs.core.Response;

import com.google.gson.Gson;

@Path("/statistiche")
public class Statistiche {
	@Context
	private HttpHeaders httpHeaders;

	@GET
	@Path("/impianto/{ID}")
	public Response statisticheImpianto(@PathParam("ID") int ID, String body) throws Exception {
		if (!Authorization.check(httpHeaders, "logged"))
			return Response.status(403).build();

		// se la seguente chiamata genera un'eccezione, non ho i privilegi per accedere
		try {
			new swbd.db.ImpiantoAssegnato(Authorization.getCurrentUser(httpHeaders).ID_utente, ID);
		} catch (Exception e) {
			return Response.status(403).build();
		}

		swbd.db.Impianto impianto = new swbd.db.Impianto(ID);

		Locale.setDefault(Locale.US);
		Gson gson = new Gson();
		filtroStatisticheImpianto filtro = gson.fromJson(body, filtroStatisticheImpianto.class);

		return Response.status(200).entity(gson.toJson(statisticaImpianto(impianto, filtro))).build();
	}

	@GET
	@Path("/impianti")
	public Response statisticheImpianti(String body) throws Exception {
		if (!Authorization.check(httpHeaders, "amministratore", "monitor"))
			return Response.status(403).build();

		swbd.db.Utente currUser = new swbd.db.Utente(Authorization.getCurrentUser(httpHeaders).ID_utente);
		swbd.db.Impianto[] impianti;
		Locale.setDefault(Locale.US);
		Gson gson = new Gson();
		filtroStatisticheImpianti filtro = gson.fromJson(body, filtroStatisticheImpianti.class);

		if (filtro.zona == null) {
			// non ci sono filtri applicati
			impianti = currUser.getImpianti();
		} else if (filtro.zona.vicinanze != null) {
			// cerca per raggio
			impianti = currUser.getImpianti(filtro.zona.vicinanze.latitudine, filtro.zona.vicinanze.longitudine,
					filtro.zona.vicinanze.raggio);
		} else if (filtro.zona.regione != null) {
			// ricerca per regione
			impianti = currUser.getImpianti(filtro.zona.regione);
		} else {
			// ricerca per comune
			impianti = currUser.getImpianti(new swbd.db.Comune(filtro.zona.comune));
		}

		filtroStatisticheImpianto filtroImpianto = new filtroStatisticheImpianto();
		filtroImpianto.tipologia = filtro.tipologia;
		filtroImpianto.intervallo = filtro.intervallo;

		List<responseStatisticheImpianti> result = new ArrayList<responseStatisticheImpianti>();
		for (swbd.db.Impianto impianto : impianti) {
			try {
				responseStatisticheImpianti obj = new responseStatisticheImpianti();
				obj.ID_impianto = impianto.ID_impianto;
				obj.nome = impianto.nome;
				obj.comune = impianto.comune;
				obj.datiSensore = statisticaImpianto(impianto, filtroImpianto);
				result.add(obj);
			} catch (Exception e) {
				// pass
			}
		}

		return Response.status(200).entity(gson.toJson(result.toArray())).build();
	}

	private responseStatisticheImpianto statisticaImpianto(swbd.db.Impianto impianto, filtroStatisticheImpianto filtro)
			throws Exception {
		if (filtro.intervallo == null)
			filtro.intervallo = new intervallo();

		swbd.db.TipologiaSensore tipologia = new swbd.db.TipologiaSensore(filtro.tipologia);
		swbd.db.Lettura[] letture = impianto.getStatistiche(tipologia, filtro.intervallo.inizio,
				filtro.intervallo.fine);

		letturaJSON result[] = new letturaJSON[letture.length];
		int i = 0;
		for (swbd.db.Lettura lettura : letture) {
			try {
				result[i] = new letturaJSON();
				result[i].x = lettura.data_inserimento;
				result[i].y = lettura.valore;
			} catch (Exception e) {
				System.out.println(e);
				// pass
			}
			i++;
		}

		responseStatisticheImpianto response = new responseStatisticheImpianto();
		response.descrizione = tipologia.descrizione;
		response.letture = result;
		response.unita_misura = tipologia.unita_misura;
		response.coeff_integrale = tipologia.coeff_integrale;

		if (tipologia.unita_misura_integrale != null && !tipologia.unita_misura_integrale.equals("")) {
			// calcoliamo il valore dell'integrale
			response.integrale = Integrale.calcola(letture);
			response.unita_misura_integrale = tipologia.unita_misura_integrale;
		}

		return response;
	}

}
