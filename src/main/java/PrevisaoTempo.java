import java.io.BufferedReader;
import java.io.IOException;
import java.io.InputStreamReader;
import java.net.HttpURLConnection;
import java.net.URL;
import java.net.URLEncoder;
import java.time.LocalDate;
import java.time.LocalDateTime;
import java.time.ZoneId;
import java.time.format.DateTimeFormatter;
import java.util.Date;
import java.util.HashMap;
import java.util.Map;

import org.json.JSONArray;
import org.json.JSONException;
import org.json.JSONObject;

public class PrevisaoTempo {
	
	private static final String API_KEY = "5f099a78f7a82e84291793900efc137f"; //Chave alunos
    private static final String API_URL_CITY = "https://api.openweathermap.org/data/2.5/forecast?q=%s&units=metric&lang=pt_br&appid=%s";
    private static final String API_URL_COORD = "https://api.openweathermap.org/data/2.5/forecast?lat=%s&lon=%s&units=metric&lang=pt_br&appid=%s";


    public Map<String, Object> getForecastByCityName(String cityName) throws IOException, JSONException {
        
    	//String url = String.format(API_URL_CITY, cityName, API_KEY);
        String url = String.format(API_URL_CITY, URLEncoder.encode(cityName, "UTF-8"), API_KEY);
        System.out.println(url);
        HttpURLConnection connection = (HttpURLConnection) new URL(url).openConnection();
        connection.setRequestMethod("GET");
        int code = connection.getResponseCode();

        StringBuilder content = new StringBuilder();
        try (BufferedReader in = new BufferedReader(new InputStreamReader(connection.getInputStream()))) {
            String line;
            while ((line = in.readLine()) != null) {
                content.append(line);
            }

            connection.disconnect();
        }

        Map<String, Object> data = new HashMap<>();
        data.put("status", code);
        data.put("body", content.toString());

        return data;
    }
    
    
    public Map<String, Object> getForecastByCoord(double latitude, double longitude) throws IOException, JSONException {
        
    	String url = String.format(API_URL_COORD, latitude, longitude, API_KEY);
        HttpURLConnection connection = (HttpURLConnection) new URL(url).openConnection();
        connection.setRequestMethod("GET");
        int code = connection.getResponseCode();

        StringBuilder content = new StringBuilder();
        try (BufferedReader in = new BufferedReader(new InputStreamReader(connection.getInputStream()))) {
            String line;
            while ((line = in.readLine()) != null) {
                content.append(line);
            }
            
        connection.disconnect();
        }

        Map<String, Object> data = new HashMap<>();
        data.put("status", code);
        data.put("body", content.toString());

        return data;
    }
    
    
    public void displayForecastForTomorrow(JSONObject forecast) throws IOException, JSONException {
      
    	LocalDate tomorrow = LocalDate.now(ZoneId.of("UTC-3")).plusDays(1);
    	
    	String countryAPI = forecast.getJSONObject("city").getString("country");
    	String cityAPI = forecast.getJSONObject("city").getString("name");
    	double latAPI = forecast.getJSONObject("city").getJSONObject("coord").getDouble("lat");
    	double lonAPI = forecast.getJSONObject("city").getJSONObject("coord").getDouble("lon");
        System.out.printf("\nCidade: %s, %s\n" +
        					"Coordenadas (lat, lon): %f, %f\n\n", cityAPI, countryAPI, latAPI, lonAPI);
    	
    	// Enontra a previsão do tempo para o dia seguinte
    	JSONArray forecasts = forecast.getJSONArray("list");
        
        for (int i = 0; i < forecasts.length(); i++) {
            
        	JSONObject day = forecasts.getJSONObject(i);
        	           
            //String dateAPI = day.getString("dt_txt");  //Data em formato texto retornada pela API está no timezone UTC+0
            long timestamp = day.getLong("dt") * 1000L;
            Date date = new Date(timestamp);
            LocalDate localDate = date.toInstant().atZone(ZoneId.of("UTC-3")).toLocalDate();
            
            // Formata data e hora para o padrão brasileiro
            LocalDateTime localDateTime = date.toInstant().atZone(ZoneId.of("UTC-3")).toLocalDateTime();
            DateTimeFormatter dateFormat = DateTimeFormatter.ofPattern("dd-MM-yyyy HH:mm:ss");
            String formattedDate = localDateTime.format(dateFormat);
            
            if (localDate.equals(tomorrow)) {
            	double temp = day.getJSONObject("main").getDouble("temp");
            	JSONObject weather = day.getJSONArray("weather").getJSONObject(0);
                String description = weather.getString("description");
                System.out.printf("Previsão para %s: %s, %.1f°C\n", formattedDate, description, temp);
                
                //timestamp: 1683687600000
                //date: Wed May 10 00:00:00 BRT 2023
                //localDate: 2023-05-10
                //formattedDate: 10-05-2023 00:00:00
            }
        }
        
    }
}
