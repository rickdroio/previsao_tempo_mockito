import static org.junit.jupiter.api.Assertions.assertEquals;

import java.io.IOException;
import java.nio.file.Files;
import java.nio.file.Paths;
import java.util.HashMap;
import java.util.Map;

import org.json.JSONException;
import org.json.JSONObject;
import org.junit.jupiter.api.Assertions;
import org.junit.jupiter.api.BeforeAll;
import org.junit.jupiter.api.Test;
import org.mockito.ArgumentMatchers;
import org.mockito.Mockito;

class PrevisaoTempoTest {

	private static PrevisaoTempo previsaoTempo;

	private static final String PATH_JSON = "src\\test\\resources\\";
	private static Map<String, Object> response_200;
	private static Map<String, Object> response_404;

	@BeforeAll
	public static void setUp() throws IOException {

		// Carrega previsão MOCK do arquivo JSON em disco.
		try {
			String arquivo = new String(Files.readAllBytes(Paths.get(PATH_JSON+"response_200.json")));
			response_200 = new HashMap<>();
			response_200.put("status", 200);
			response_200.put("body", new JSONObject(arquivo));
		} catch (Exception e) {
			System.out.println("Arquivo response_200.json não encontrado");
		}

		try {
			String arquivo = new String(Files.readAllBytes(Paths.get(PATH_JSON+"response_404.json")));
			response_404 = new HashMap<>();
			response_404.put("status", 404);
			response_404.put("body", new JSONObject(arquivo));
		} catch (Exception e) {
			System.out.println("Arquivo response_404.json não encontrado");
		}

		previsaoTempo = new PrevisaoTempo();
	}

	@Test
	public void testPrevisaoPorCidadeMock() throws JSONException, IOException {
		PrevisaoTempo mock = Mockito.mock(PrevisaoTempo.class);
		Mockito.when(mock.getForecastByCityName(ArgumentMatchers.anyString())).thenReturn(response_404);
		Mockito.when(mock.getForecastByCityName("São Caetano do Sul")).thenReturn(response_200);

		String cidade_teste = "São Caetano do Sul";
		String pais_teste = "BR";

		Map<String, Object> response = mock.getForecastByCityName(cidade_teste);
		JSONObject body = new JSONObject(response.get("body").toString());

		//assertEquals(200, response.get("status"));

		String paisAPI = body.getJSONObject("city").getString("country");
		String cidadeAPI = body.getJSONObject("city").getString("name");

		// assertAll agrupa os asserts e verifica todos do grupo, mesmo que algum deles falhe,
		// que é diferente de simplesmente enfileirar asserts "normais" (que faria o código parar na primeira falha)
		Assertions.assertAll("Verifica Cidade e País retornados pela API",
				() -> assertEquals(cidade_teste, cidadeAPI),
				() -> assertEquals(pais_teste, paisAPI),
				() -> assertEquals(200, response.get("status"))
		);
	}


	@Test
	public void testPrevisaoPorCidade() throws JSONException, IOException {
		
		String cidade_teste = "São Paulo";
		String pais_teste = "BR";

		Map<String, Object> response = previsaoTempo.getForecastByCityName(cidade_teste);
		JSONObject body = new JSONObject(response.get("body").toString());

		//assertEquals(200, response.get("status"));

		String paisAPI = body.getJSONObject("city").getString("country");
    	String cidadeAPI = body.getJSONObject("city").getString("name");
    	
    	// assertAll agrupa os asserts e verifica todos do grupo, mesmo que algum deles falhe,
    	// que é diferente de simplesmente enfileirar asserts "normais" (que faria o código parar na primeira falha)
    	Assertions.assertAll("Verifica Cidade e País retornados pela API",
    		() -> assertEquals(cidade_teste, cidadeAPI),
    		() -> assertEquals(pais_teste, paisAPI),
			() -> assertEquals(200, response.get("status"))
    		);
	}

	@Test
	public void testPrevisaoPorCoordenada() throws JSONException, IOException {

		double lat_teste = -23.5612;
		double lng_teste = -46.6526;

		Map<String, Object> response = previsaoTempo.getForecastByCoord(lat_teste, lng_teste);
		JSONObject body = new JSONObject(response.get("body").toString());

		//assertEquals(200, response.get("status"));

		double latAPI = body.getJSONObject("city").getJSONObject("coord").getDouble("lat");
		double lngAPI = body.getJSONObject("city").getJSONObject("coord").getDouble("lon");

		// assertAll agrupa os asserts e verifica todos do grupo, mesmo que algum deles falhe,
		// que é diferente de simplesmente enfileirar asserts "normais" (que faria o código parar na primeira falha)
		Assertions.assertAll("Verifica Cidade e País retornados pela API",
				() -> assertEquals(lat_teste, latAPI),
				() -> assertEquals(lng_teste, lngAPI),
				() -> assertEquals(200, response.get("status"))
		);
	}

}