package swbd.API.it;

class AuthToken {
	public String token;
	public int userID = 0;
	public String role;
}

class LoginData {
	public String email;
	public String username;
	public String password;
}

class userFilter {
	public String[] roles;
	public String comune;
	public String regione;
}

class operazioneAttuatore {
	public double valore = 0;
}

class permessiImpianto {
	public boolean modificabile = false;
}

class intervallo {
	public String inizio;
	public String fine;
}

class bodyInterventoImpianto {
	public intervallo intervallo;
}

class bodyLettureSensore {
	public int numeroLetture = -1;
	public intervallo intervallo;
}

class bodyOperazioniAttuatore {
	public int numeroOperazioni = -1;
	public intervallo intervallo;
}

class authImpiantoDipendente {
	public int ID;
	public boolean modificabile;
}

class updateStatoIntervento {
	public String stato;
}

class filtroVicinanza {
	public double latitudine;
	public double longitudine;
	public double raggio;
}

class filtroRicercaImpianti {
	public String comune;
	public String regione;
	public filtroVicinanza vicinanze;
}

class filtroStatisticheImpianto {
	public int tipologia;
	public intervallo intervallo;
}

class filtroStatisticheImpianti {
	public int tipologia;
	public filtroRicercaImpianti zona;
	public intervallo intervallo;
}

class letturaJSON {
	public String x;
	public double y;
}

class responseStatisticheImpianto {
	public String descrizione;
	public String unita_misura;
	public letturaJSON letture[];
	public double integrale;
	public String unita_misura_integrale;
	public double coeff_integrale = -1;
}

class responseStatisticheImpianti {
	public int ID_impianto = -1;
	public String nome;
	public String comune;
	public responseStatisticheImpianto datiSensore;
}