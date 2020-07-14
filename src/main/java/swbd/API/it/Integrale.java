package swbd.API.it;

import java.text.ParseException;
import java.text.SimpleDateFormat;

public class Integrale {
	// implementa il metodo dei trapezi
	public static double calcola(swbd.db.Lettura[] letture) throws ParseException {
		double result = 0;
		if (letture != null) {
			for (int i = 1; i < letture.length; i++) {
				result += ((letture[i].valore + letture[i - 1].valore) / 2)
						* ((new SimpleDateFormat("yyyy-MM-dd HH:mm:ss").parse(letture[i].data_inserimento).getTime() - new SimpleDateFormat("yyyy-MM-dd HH:mm:ss").parse(letture[i - 1].data_inserimento).getTime()) / 1000);
			}
		}

		return result;
	}
}
