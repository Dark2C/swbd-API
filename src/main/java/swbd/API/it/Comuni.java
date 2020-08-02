package swbd.API.it;

import javax.ws.rs.GET;
import javax.ws.rs.Path;
import javax.ws.rs.Produces;
import javax.ws.rs.core.Context;
import javax.ws.rs.core.HttpHeaders;
import javax.ws.rs.core.Response;

@Path("/comuni")
public class Comuni {
	@Context
	private HttpHeaders httpHeaders;
/** 
 * Metodo per ottenere la lista dei comuni. La risposta e' statica, dunque non la ricavo ogni volta dal database
 * L'utente deve essere loggato.
 * @param body
 * @return
 * @throws Exception
 */
	@GET
	@Path("/")
	@Produces("application/json")
	public Response getComuni(String body) throws Exception {
		if (!Authorization.check(httpHeaders, "logged"))
			return Response.status(403).build();
		// La risposta e' statica, dunque non la ricavo ogni volta dal database
		return Response.status(200).entity(
				"{\"comuni\":{\"AG\":{\"nome_comune\":\"Agrigento\",\"nome_regione\":\"Sicilia\"},"
				+ "\"AL\":{\"nome_comune\":\"Alessandria\",\"nome_regione\":\"Piemonte\"},\"AN\":"
				+ "{\"nome_comune\":\"ANCONA\",\"nome_regione\":\"Marche\"},\"AO\":{\"nome_comune\":"
				+ "\"AOSTA\",\"nome_regione\":\"Valle d'Aosta\"},\"AP\":{\"nome_comune\":\"Ascoli"
				+ " Piceno\",\"nome_regione\":\"Marche\"},\"AQ\":{\"nome_comune\":\"L'AQUILA\",\""
				+ "nome_regione\":\"Abruzzo\"},\"AR\":{\"nome_comune\":\"Arezzo\",\"nome_regione\":"
				+ "\"Toscana\"},\"AT\":{\"nome_comune\":\"Asti\",\"nome_regione\":\"Piemonte\"},"
				+ "\"AV\":{\"nome_comune\":\"Avellino\",\"nome_regione\":\"Campania\"},\"BA\":{\""
				+ "nome_comune\":\"BARI\",\"nome_regione\":\"Puglia\"},\"BG\":{\"nome_comune\":\""
				+ "Bergamo\",\"nome_regione\":\"Lombardia\"},\"BI\":{\"nome_comune\":\"Biella\",\""
				+ "nome_regione\":\"Piemonte\"},\"BL\":{\"nome_comune\":\"Belluno\",\"nome_regione"
				+ "\":\"Veneto\"},\"BN\":{\"nome_comune\":\"Benevento\",\"nome_regione\":\"Campania"
				+ "\"},\"BO\":{\"nome_comune\":\"BOLOGNA\",\"nome_regione\":\"Emilia-Romagna\"},"
				+ "\"BR\":{\"nome_comune\":\"Brindisi\",\"nome_regione\":\"Puglia\"},\"BS\":"
				+ "{\"nome_comune\":\"Brescia\",\"nome_regione\":\"Lombardia\"},\"BT\":{\"nome_comune\":"
				+ "\"Barletta-Andria-Trani\",\"nome_regione\":\"Puglia\"},\"BZ\":{\"nome_comune\":"
				+ "\"Bolzano\",\"nome_regione\":\"Trentino-Alto Adige\"},\"CA\":{\"nome_comune\":"
				+ "\"CAGLIARI\",\"nome_regione\":\"Sardegna\"},\"CB\":{\"nome_comune\":\"CAMPOBASSO\","
				+ "\"nome_regione\":\"Molise\"},\"CE\":{\"nome_comune\":\"Caserta\",\"nome_regione\":"
				+ "\"Campania\"},\"CH\":{\"nome_comune\":\"Chieti\",\"nome_regione\":\"Abruzzo\"},"
				+ "\"CL\":{\"nome_comune\":\"Caltanissetta\",\"nome_regione\":\"Sicilia\"},\"CN\":"
				+ "{\"nome_comune\":\"Cuneo\",\"nome_regione\":\"Piemonte\"},\"CO\":{\"nome_comune\":"
				+ "\"Como\",\"nome_regione\":\"Lombardia\"},\"CR\":{\"nome_comune\":\"Cremona\","
				+ "\"nome_regione\":\"Lombardia\"},\"CS\":{\"nome_comune\":\"Cosenza\",\"nome_regione\":"
				+ "\"Calabria\"},\"CT\":{\"nome_comune\":\"Catania\",\"nome_regione\":\"Sicilia\"},"
				+ "\"CZ\":{\"nome_comune\":\"CATANZARO\",\"nome_regione\":\"Calabria\"},\"EE\":"
				+ "{\"nome_comune\":\"Stato Estero\",\"nome_regione\":\"Stato Estero\"},\"EN\":"
				+ "{\"nome_comune\":\"Enna\",\"nome_regione\":\"Sicilia\"},\"FC\":{\"nome_comune\":"
				+ "\"Forli'-Cesena\",\"nome_regione\":\"Emilia-Romagna\"},\"FE\":{\"nome_comune\":\""
				+ "Ferrara\",\"nome_regione\":\"Emilia-Romagna\"},\"FG\":{\"nome_comune\":\"Foggia\","
				+ "\"nome_regione\":\"Puglia\"},\"FI\":{\"nome_comune\":\"FIRENZE\",\"nome_regione\":"
				+ "\"Toscana\"},\"FM\":{\"nome_comune\":\"Fermo\",\"nome_regione\":\"Marche\"},\"FR\":"
				+ "{\"nome_comune\":\"Frosinone\",\"nome_regione\":\"Lazio\"},\"GE\":{\"nome_comune\":"
				+ "\"GENOVA\",\"nome_regione\":\"Liguria\"},\"GO\":{\"nome_comune\":\"Gorizia\","
				+ "\"nome_regione\":\"Friuli-Venezia Giulia\"},\"GR\":{\"nome_comune\":\"Grosseto\","
				+ "\"nome_regione\":\"Toscana\"},\"IM\":{\"nome_comune\":\"Imperia\",\"nome_regione\":"
				+ "\"Liguria\"},\"IS\":{\"nome_comune\":\"Isernia\",\"nome_regione\":\"Molise\"},\"KR\":"
				+ "{\"nome_comune\":\"Crotone\",\"nome_regione\":\"Calabria\"},\"LC\":{\"nome_comune\":"
				+ "\"Lecco\",\"nome_regione\":\"Lombardia\"},\"LE\":{\"nome_comune\":\"Lecce\","
				+ "\"nome_regione\":\"Puglia\"},\"LI\":{\"nome_comune\":\"Livorno\",\"nome_regione\":"
				+ "\"Toscana\"},\"LO\":{\"nome_comune\":\"Lodi\",\"nome_regione\":\"Lombardia\"},\"LT\":"
				+ "{\"nome_comune\":\"Latina\",\"nome_regione\":\"Lazio\"},\"LU\":{\"nome_comune\":"
				+ "\"Lucca\",\"nome_regione\":\"Toscana\"},\"MB\":{\"nome_comune\":\"Monza\","
				+ "\"nome_regione\":\"Lombardia\"},\"MC\":{\"nome_comune\":\"Macerata\",\"nome_regione\":"
				+ "\"Marche\"},\"ME\":{\"nome_comune\":\"Messina\",\"nome_regione\":\"Sicilia\"},\"MI\":"
				+ "{\"nome_comune\":\"MILANO\",\"nome_regione\":\"Lombardia\"},\"MN\":{\"nome_comune\":"
				+ "\"Mantova\",\"nome_regione\":\"Lombardia\"},\"MO\":{\"nome_comune\":\"Modena\","
				+ "\"nome_regione\":\"Emilia-Romagna\"},\"MS\":{\"nome_comune\":\"Massa\",\"nome_regione\":"
				+ "\"Toscana\"},\"MT\":{\"nome_comune\":\"Matera\",\"nome_regione\":\"Basilicata\"},\"NA\":"
				+ "{\"nome_comune\":\"NAPOLI\",\"nome_regione\":\"Campania\"},\"NO\":{\"nome_comune\":"
				+ "\"Novara\",\"nome_regione\":\"Piemonte\"},\"NU\":{\"nome_comune\":\"Nuoro\","
				+ "\"nome_regione\":\"Sardegna\"},\"OR\":{\"nome_comune\":\"Oristano\",\"nome_regione\":"
				+ "\"Sardegna\"},\"PA\":{\"nome_comune\":\"PALERMO\",\"nome_regione\":\"Sicilia\"},\"PC\":"
				+ "{\"nome_comune\":\"Piacenza\",\"nome_regione\":\"Emilia-Romagna\"},\"PD\":"
				+ "{\"nome_comune\":\"Padova\",\"nome_regione\":\"Veneto\"},\"PE\":{\"nome_comune\":"
				+ "\"Pescara\",\"nome_regione\":\"Abruzzo\"},\"PG\":{\"nome_comune\":\"PERUGIA\","
				+ "\"nome_regione\":\"Umbria\"},\"PI\":{\"nome_comune\":\"Pisa\",\"nome_regione\":"
				+ "\"Toscana\"},\"PN\":{\"nome_comune\":\"Pordenone\",\"nome_regione\":"
				+ "\"Friuli-Venezia Giulia\"},\"PO\":{\"nome_comune\":\"Prato\",\"nome_regione\":"
				+ "\"Toscana\"},\"PR\":{\"nome_comune\":\"Parma\",\"nome_regione\":\"Emilia-Romagna\"},"
				+ "\"PT\":{\"nome_comune\":\"Pistoia\",\"nome_regione\":\"Toscana\"},\"PU\":{\"nome_comune\":"
				+ "\"Pesaro e Urbino\",\"nome_regione\":\"Marche\"},\"PV\":{\"nome_comune\":\"Pavia\","
				+ "\"nome_regione\":\"Lombardia\"},\"PZ\":{\"nome_comune\":\"POTENZA\",\"nome_regione\":"
				+ "\"Basilicata\"},\"RA\":{\"nome_comune\":\"Ravenna\",\"nome_regione\":\"Emilia-Romagna\"},"
				+ "\"RC\":{\"nome_comune\":\"Reggio Calabria\",\"nome_regione\":\"Calabria\"},\"RE\":"
				+ "{\"nome_comune\":\"Reggio Emilia\",\"nome_regione\":\"Emilia-Romagna\"},\"RG\":"
				+ "{\"nome_comune\":\"Ragusa\",\"nome_regione\":\"Sicilia\"},\"RI\":{\"nome_comune\":"
				+ "\"Rieti\",\"nome_regione\":\"Lazio\"},\"RM\":{\"nome_comune\":\"ROMA\",\"nome_regione\":"
				+ "\"Lazio\"},\"RN\":{\"nome_comune\":\"Rimini\",\"nome_regione\":\"Emilia-Romagna\"},"
				+ "\"RO\":{\"nome_comune\":\"Rovigo\",\"nome_regione\":\"Veneto\"},\"SA\":{\"nome_comune\":"
				+ "\"Salerno\",\"nome_regione\":\"Campania\"},\"SI\":{\"nome_comune\":\"Siena\","
				+ "\"nome_regione\":\"Toscana\"},\"SO\":{\"nome_comune\":\"Sondrio\",\"nome_regione\":"
				+ "\"Lombardia\"},\"SP\":{\"nome_comune\":\"La Spezia\",\"nome_regione\":\"Liguria\"},\"SR\":"
				+ "{\"nome_comune\":\"Siracusa\",\"nome_regione\":\"Sicilia\"},\"SS\":{\"nome_comune\":"
				+ "\"Sassari\",\"nome_regione\":\"Sardegna\"},\"SU\":{\"nome_comune\":\"Carbonia\","
				+ "\"nome_regione\":\"Sardegna\"},\"SV\":{\"nome_comune\":\"Savona\",\"nome_regione\":"
				+ "\"Liguria\"},\"TA\":{\"nome_comune\":\"Taranto\",\"nome_regione\":\"Puglia\"},\"TE\":"
				+ "{\"nome_comune\":\"Teramo\",\"nome_regione\":\"Abruzzo\"},\"TN\":{\"nome_comune\":"
				+ "\"TRENTO\",\"nome_regione\":\"Trentino-Alto Adige\"},\"TO\":{\"nome_comune\":\"TORINO\","
				+ "\"nome_regione\":\"Piemonte\"},\"TP\":{\"nome_comune\":\"Trapani\",\"nome_regione\":"
				+ "\"Sicilia\"},\"TR\":{\"nome_comune\":\"Terni\",\"nome_regione\":\"Umbria\"},\"TS\":"
				+ "{\"nome_comune\":\"TRIESTE\",\"nome_regione\":\"Friuli-Venezia Giulia\"},\"TV\":"
				+ "{\"nome_comune\":\"Treviso\",\"nome_regione\":\"Veneto\"},\"UD\":{\"nome_comune\":"
				+ "\"Udine\",\"nome_regione\":\"Friuli-Venezia Giulia\"},\"VA\":{\"nome_comune\":\"Varese\","
				+ "\"nome_regione\":\"Lombardia\"},\"VB\":{\"nome_comune\":\"Verbania\",\"nome_regione\":"
				+ "\"Piemonte\"},\"VC\":{\"nome_comune\":\"Vercelli\",\"nome_regione\":\"Piemonte\"},\"VE\":"
				+ "{\"nome_comune\":\"VENEZIA\",\"nome_regione\":\"Veneto\"},\"VI\":{\"nome_comune\":"
				+ "\"Vicenza\",\"nome_regione\":\"Veneto\"},\"VR\":{\"nome_comune\":\"Verona\","
				+ "\"nome_regione\":\"Veneto\"},\"VT\":{\"nome_comune\":\"Viterbo\",\"nome_regione\":"
				+ "\"Lazio\"},\"VV\":{\"nome_comune\":\"Vibo Valentia\",\"nome_regione\":\"Calabria\"}},"
				+ "\"regioni\":{\"Sicilia\":[\"AG\",\"CL\",\"CT\",\"EN\",\"ME\",\"PA\",\"RG\",\"SR\",\"TP\"],"
				+ "\"Piemonte\":[\"AL\",\"AT\",\"BI\",\"CN\",\"NO\",\"TO\",\"VB\",\"VC\"],\"Marche\":[\"AN\","
				+ "\"AP\",\"FM\",\"MC\",\"PU\"],\"Valle d'Aosta\":[\"AO\"],\"Abruzzo\":[\"AQ\",\"CH\",\"PE\","
				+ "\"TE\"],\"Toscana\":[\"AR\",\"FI\",\"GR\",\"LI\",\"LU\",\"MS\",\"PI\",\"PO\",\"PT\",\"SI\"],"
				+ "\"Campania\":[\"AV\",\"BN\",\"CE\",\"NA\",\"SA\"],\"Puglia\":[\"BA\",\"BR\",\"BT\",\"FG\","
				+ "\"LE\",\"TA\"],\"Lombardia\":[\"BG\",\"BS\",\"CO\",\"CR\",\"LC\",\"LO\",\"MB\",\"MI\",\"MN\""
				+ ",\"PV\",\"SO\",\"VA\"],\"Veneto\":[\"BL\",\"PD\",\"RO\",\"TV\",\"VE\",\"VI\",\"VR\"],\"Emilia-"
				+ "Romagna\":[\"BO\",\"FC\",\"FE\",\"MO\",\"PC\",\"PR\",\"RA\",\"RE\",\"RN\"],\"Trentino-Alto Adige"
				+ "\":[\"BZ\",\"TN\"],\"Sardegna\":[\"CA\",\"NU\",\"OR\",\"SS\",\"SU\"],\"Molise\":[\"CB\",\"IS\"],"
				+ "\"Calabria\":[\"CS\",\"CZ\",\"KR\",\"RC\",\"VV\"],\"Stato Estero\":[\"EE\"],\"Lazio\":[\"FR\","
				+ "\"LT\",\"RI\",\"RM\",\"VT\"],\"Liguria\":[\"GE\",\"IM\",\"SP\",\"SV\"],\"Friuli-Venezia Giulia\":"
				+ "[\"GO\",\"PN\",\"TS\",\"UD\"],\"Basilicata\":[\"MT\",\"PZ\"],\"Umbria\":[\"PG\",\"TR\"]}}")
				.build();
	}
}