package proj_helpers;

import org.json.simple.JSONArray;
import org.json.simple.JSONObject;
import org.json.simple.parser.JSONParser;
import org.json.simple.parser.ParseException;

import java.io.BufferedReader;
import java.io.IOException;
import java.io.InputStreamReader;
import java.net.HttpURLConnection;
import java.net.MalformedURLException;
import java.net.URL;

public class JSONHelper {
    /**
     * Simple JSONObject parsing
     *
     * @param path the URL path
     * @return null if there is no connection , JSONObject result if everything went well.
     */
    public static JSONObject getWebJSONObject(String path) {
        try {
            URL address = new URL(path);
            HttpURLConnection connection = (HttpURLConnection) address.openConnection();
            connection.setRequestMethod("GET");
            if (connection.getResponseCode() == 200) {
                try (BufferedReader reader = new BufferedReader(new InputStreamReader(connection.getInputStream()))) {
                    StringBuilder stringBuilder = new StringBuilder();
                    String line;
                    while ((line = reader.readLine()) != null)
                        stringBuilder.append(line);
                    JSONParser parser = new JSONParser();
                    return (JSONObject) parser.parse(stringBuilder.toString());
                } catch (ParseException e) {
                    System.out.println(e.getMessage()); // handle here
                }
            }
        } catch (IOException e) {
            e.printStackTrace();
        }
        return null;
    }

    /**
     * @param path the {@code String} path from the web.
     * @return {@code JSONArray} object parsed from the {@param path} using {@code InputStream}.
     */
    public static JSONArray getWebJSONArray(String path) {
        try {
            URL address = new URL(path);
            HttpURLConnection connection = (HttpURLConnection) address.openConnection();
            connection.setRequestMethod("GET");
            if (connection.getResponseCode() == 200) {
                try (BufferedReader reader = new BufferedReader(new InputStreamReader(connection.getInputStream()))) {
                    StringBuilder stringBuilder = new StringBuilder();
                    String line;
                    while ((line = reader.readLine()) != null)
                        stringBuilder.append(line);
                    JSONParser parser = new JSONParser();
                    return (JSONArray) parser.parse(stringBuilder.toString());
                } catch (ParseException e) {
                    System.out.println(e.getMessage()); // handle here
                }
            }
        } catch (IOException e) {
            e.printStackTrace();
        }
        return null;
    }
}
