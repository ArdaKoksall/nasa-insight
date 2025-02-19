package org.example;

import okhttp3.OkHttpClient;
import okhttp3.Request;

import java.util.ArrayList;
import java.util.Iterator;

import com.fasterxml.jackson.databind.JsonNode;
import com.fasterxml.jackson.databind.ObjectMapper;

import javax.swing.*;
import java.util.List;


public class Main {

    public static void main(String[] args) {
        run();
    }

    private static void run(){
        try{
            String input = JOptionPane.showInputDialog(null, "Enter API Key or type 'demo' (limited usage):", "API Key Required", JOptionPane.QUESTION_MESSAGE).strip();
            String apiKey;
            if (input.equals("demo")){
                apiKey = "DEMO_KEY";
            }else{
                apiKey = input;
            }
            String url = "https://api.nasa.gov/insight_weather/?api_key=" + apiKey + "&feedtype=json&ver=1.0";

            String response;

            OkHttpClient client = new OkHttpClient();
            Request request = new Request.Builder().url(url).get().addHeader("accept", "application/json").build();

            try {
                okhttp3.Response httpResponse = client.newCall(request).execute();
                if (httpResponse.body() != null) {
                    response = httpResponse.body().string();
                    ObjectMapper mapper = new ObjectMapper();
                    JsonNode rootNode = mapper.readTree(response);
                    List<String> validSolKeys = getValidSolKeys(rootNode);


                    String[] validSolKeysArray = validSolKeys.toArray(new String[0]);
                    String selectedSol = (String) JOptionPane.showInputDialog(null, "Select a Sol to view:", "Select Sol", JOptionPane.QUESTION_MESSAGE, null, validSolKeysArray, validSolKeysArray[0]);


                    if (selectedSol != null) {
                        JsonNode selectedSolNode = rootNode.get(selectedSol);
                        if (selectedSolNode != null) {
                            WeatherDisplay weatherDisplay = new WeatherDisplay(selectedSolNode.toString());
                            weatherDisplay.setVisible(true);
                        }
                    }
                }
            } catch (Exception e) {
                System.out.println(e.getMessage());
            }
        }catch (Exception e){
            System.out.println(e.getMessage());
        }
    }

    private static List<String> getValidSolKeys(JsonNode rootNode) {
        List<String> validSolKeys = new ArrayList<>();
        JsonNode validityChecksNode = rootNode.get("validity_checks");

        if (validityChecksNode != null && validityChecksNode.isObject()) {
            Iterator<String> fieldNames = validityChecksNode.fieldNames();
            while (fieldNames.hasNext()) {
                String solKey = fieldNames.next();
                if (!solKey.equals("sol_hours_required") && !solKey.equals("sols_checked")) { // Skip metadata fields
                    JsonNode solValidityNode = validityChecksNode.get(solKey);
                    if (isSolValid(solValidityNode)) {
                        validSolKeys.add(solKey);
                    }
                }
            }
        }

        return validSolKeys;
    }

    private static boolean isSolValid(JsonNode solValidityNode) {
        if (solValidityNode != null && solValidityNode.isObject()) {
            Iterator<String> fieldNames = solValidityNode.fieldNames();
            while (fieldNames.hasNext()) {
                String fieldName = fieldNames.next();
                JsonNode sensorValidityNode = solValidityNode.get(fieldName);
                if (sensorValidityNode != null && sensorValidityNode.isObject()) {
                    JsonNode validNode = sensorValidityNode.get("valid");
                    if (validNode != null && validNode.isBoolean() && !validNode.asBoolean()) {
                        return false;
                    }
                }
            }
            return true;
        }
        return false;
    }

}