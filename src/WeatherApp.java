import org.json.simple.JSONArray;
import org.json.simple.JSONObject;
import org.json.simple.parser.JSONParser;

import java.net.HttpURLConnection;
import java.net.URL;
import java.time.LocalDateTime;
import java.time.format.DateTimeFormatter;
import java.util.Scanner;
import java.io.IOException;

/*
*recupera dados meteorológicos da API - esta lógica de backend irá buscar o clima mais recente
*dados da API externa e retorne-os. A interface irá
*exibe esses dados para o usuário
*/
public class WeatherApp {

    //busca dados meteorológicos para determinado local
    public static JSONObject getWeatherData(String locationName){
        //obtém coordenadas de localização usando a API de geolocalização
        JSONArray locationData =  getLocationData(locationName);

        // extrai dados de latitude e longitude
        JSONObject location = (JSONObject) locationData.get(0);
        double latitude = (double) location.get("latitude");
        double longitude = (double) location.get("longitude");

        // cria URL de solicitação de API com coordenadas de localização
        String urlString = "https://api.open-meteo.com/v1/forecast?" +
                "latitude=" + latitude + "&longitude=" + longitude +
                "&hourly=temperature_2m,relativehumidity_2m,weathercode,windspeed_10m&timezone=America%2FLos_Angeles";

        try{
            //chama API e obtém resposta
            HttpURLConnection conn = fetchApiResponse(urlString);

            /*
             *verifica o status da resposta
             *200 significa conexão bem-sucedida
             */
            if(conn.getResponseCode() != 200) {
                System.out.println("Erro: não foi possível conectar à API");
                return null;
            }

            // armazena dados json resultantes
            StringBuilder resultJson = new StringBuilder();
            Scanner scanner =  new Scanner(conn.getInputStream());

            while(scanner.hasNext()){
                // lê e armazena no construtor de string
                resultJson.append(scanner.nextLine());
            }

            scanner.close();

            conn.disconnect();

            // analisa nossos dados
            JSONParser parser = new JSONParser();
            JSONObject resultJsonObj = (JSONObject) parser.parse(String.valueOf(resultJson));

            // recupera dados por hora
            JSONObject hourly = (JSONObject) resultJsonObj.get("hourly");

            /*
            *queremos obter os dados da hora atual
            *então precisamos obter o índice da nossa hora atual
             */
            JSONArray time = (JSONArray) hourly.get("time");
            int index = findInexOfCurrentTime(time);

            //obtém a temperatura
            JSONArray temperatureData = (JSONArray)  hourly.get("temperature_2m");
            double temperature = (double)  temperatureData.get(index);

            //obtém o código meteorológico
            JSONArray weathercode = (JSONArray) hourly.get("weathercode");
            String weatherCondition = convertWeatherCode((long)weathercode.get(index));

            //obtém umidade
            JSONArray relativeHumidity = (JSONArray)  hourly.get("relativehumidity_2m");
            long humidity = (long)  relativeHumidity.get(index);

            //obtém a velocidade do vento
            JSONArray windspeedData = (JSONArray) hourly.get("windspeed_10m");
            double windspeed = (double)  windspeedData.get(index);

            // constroe o objeto de dados meteorológicos json que iremos acessar em nosso frontend
            JSONObject weatherData = new JSONObject();
            weatherData.put("temperature", temperature);
            weatherData.put("weather_condition", weatherCondition);
            weatherData.put("humidity", humidity);
            weatherData.put("windspeed", windspeed);


            return weatherData;

        }catch (Exception e){
            e.printStackTrace();
        }

        return null;
    }

    // recupera coordenadas geográficas para determinado nome de local
    public static JSONArray getLocationData(String locationName){
        // substitua qualquer espaço em branco no nome do local por + para aderir ao formato de solicitação da API
        locationName = locationName.replaceAll(" ", "+");

        // cria url da API com parâmetro de localização
        String urlString = "https://geocoding-api.open-meteo.com/v1/search?name=" +
            locationName + "&count=10&language=pt&format=json";

        try{
            // chama a API e obtém uma resposta
            HttpURLConnection conn = fetchApiResponse(urlString);

            /*
             *verifica o status da resposta
             *200 significa conexão bem-sucedida
             */
            if(conn.getResponseCode() != 200){
                System.out.println("Erro: não foi possível conectar à API");
                return null;
            }else{
                //armazena os resultados da API
                StringBuilder resultJson = new StringBuilder();
                Scanner scanner =  new Scanner(conn.getInputStream());

                // lê e armazena os dados json resultantes em nosso construtor de strings
                while (scanner.hasNext()){
                    resultJson.append(scanner.nextLine());
                }

                //fecha o scanner
                scanner.close();

                //fecha a conexão do URL
                conn.disconnect();

                // analisa a string JSON em um objeto JSON
                JSONParser parser =  new JSONParser();
                JSONObject resultsJsonObj = (JSONObject) parser.parse(String.valueOf(resultJson));

                // obtém a lista de dados de localização que a API gerou a partir do nome do local
                JSONArray locationData = (JSONArray) resultsJsonObj.get("results");
                return locationData;
            }

        } catch(Exception e){
            e.printStackTrace();
        }

        //não foi possível encontrar a localização
        return null;
    }

    private static HttpURLConnection fetchApiResponse(String urlString){
        try{
            //tenta criar conexão
            URL url = new URL(urlString);
            HttpURLConnection conn = (HttpURLConnection) url.openConnection();

            //define o método de solicitação para obter
            conn.setRequestMethod("GET");

            // conecta-se à nossa API
            conn.connect();
            return conn;

        }catch(IOException e){
            e.printStackTrace();
    }
        //não foi possível fazer a conexão
        return null;
    }

    private static int findInexOfCurrentTime(JSONArray timeList){
        String currentTime = getCurrentTime();

        // percorre a lista de horários e vê qual deles corresponde ao nosso horário atual
        for(int i = 0; i < timeList.size(); i++){
            String time = (String) timeList.get(i);
            if(time.equalsIgnoreCase(currentTime)){

                //retorna o índice
                return i;
            }
        }

        return 0;
    }

    public static String getCurrentTime(){
        //obtém a data e hora atuais
        LocalDateTime currentDateTime =  LocalDateTime.now();

        //formata data
        DateTimeFormatter formatter = DateTimeFormatter.ofPattern("dd-MM-yyyy 'T 'HH':00'");

        // formata e imprime a data e hora atuais
        String formattedDataTime = currentDateTime.format((formatter));

        return formattedDataTime;
    }

    // converte o código meteorológico para algo mais legível
    private static String convertWeatherCode(long weathercode) {
        String weatherCondition = "";
        if (weathercode == 0L) {
            weatherCondition = "Limpo";
        } else if (weathercode > 0L && weathercode <= 3L) {
            weatherCondition = "Nublado";

        } else if ((weathercode > 51L && weathercode <= 67L)
                || (weathercode >= 80L && weathercode <= 99L)) {
            weatherCondition = "Chuva";

        } else if (weathercode >= 71L && weathercode <= 77L) {
            weatherCondition = "Neve";
        }

        return weatherCondition;

    }


}

