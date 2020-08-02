package swbd.API.it;

import org.glassfish.jersey.server.ResourceConfig;
import javax.ws.rs.ApplicationPath;

@ApplicationPath("/v1")
public class JerseyConfig extends ResourceConfig {
	public JerseyConfig() {
		register(CORSFilter.class);
		register(Anomalia.class);
		register(AttuatoreImpianto.class);
		register(Comuni.class);
		register(Dipendente.class);
		register(Impianti.class);
		register(Impianto.class);
		register(Intervento.class);
		register(SensoreImpianto.class);
		register(Sessione.class);
		register(Statistiche.class);
		register(Tipologia.class);
		register(Utente.class);
		register(Utenti.class);
	}
}
