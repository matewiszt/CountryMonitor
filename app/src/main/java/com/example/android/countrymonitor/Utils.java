package com.example.android.countrymonitor;

import android.text.TextUtils;
import android.util.Log;

import org.json.JSONArray;
import org.json.JSONException;
import org.json.JSONObject;

import java.io.BufferedReader;
import java.io.IOException;
import java.io.InputStream;
import java.io.InputStreamReader;
import java.net.HttpURLConnection;
import java.net.MalformedURLException;
import java.net.URL;
import java.nio.charset.Charset;
import java.util.ArrayList;
import java.util.List;

import static com.example.android.countrymonitor.MainActivity.LOG_TAG;

public class Utils {

    /*
     * Public constructor - not needed because we don't want to create any instances of this class
     */
    public Utils() {
    }

    /*
     * Puts all the methods together to get the List of News from the request URL
     * @param requestUrl: the target URL of the HTTP request
     * @return List<News>: the list of News objects
     */
    public static List<News> fetchNewsData(String requestUrl){

        //Create the URL of the requestURL by the createURL method
        URL url = createUrl(requestUrl);

        String jsonResponse = null;

        try {

            //Try to create the HTTP request with the request URL by the makeHttpRequest method to get the data
           jsonResponse = makeHttpRequest(url);
        }catch(IOException e){

            //If it fails, print the error to the log
            Log.e(LOG_TAG, "HTTP request failure", e);
        }

        //Create a list of the results by the extractDataFromJson method
        List<News> results = extractDataFromJson(jsonResponse);

        //Return the List of News objects
        return results;
    }





    /*
     * Extracts the data from the JSON response
     * @param jsonResponse: the jsonResponse read from the InputStream of the HTTP request
     * @return List<News>: the results as a List of News objects
     */

    private static List<News> extractDataFromJson(String jsonResponse){

        //If the response is empty, return early
        if (TextUtils.isEmpty(jsonResponse)){
            return null;
        }

        //Create a new list for the news
        List<News> news = new ArrayList<>();

        try {

            //Try to the create a new JSONObject from the response and search for the result list of news
            JSONObject response = new JSONObject(jsonResponse);
            JSONObject newsResponse = response.getJSONObject("response");
            JSONArray newsArray = newsResponse.getJSONArray("results");

            //Loop through the results
            for (int i = 0; i < newsArray.length(); i++) {

                //Get the title, section, url and date information of the current news
                JSONObject currentNews = newsArray.getJSONObject(i);
                String title = currentNews.getString("webTitle");
                String section = currentNews.getString("sectionName");
                String url = currentNews.getString("webUrl");
                String rawDate = currentNews.getString("webPublicationDate");
                String date = rawDate.substring(0, 10);

                //Create a new News object from the data and add it to the list
                news.add(new News(title, section, url, date));

            }

        }catch (JSONException e){

            //If it fails, print the error to the log
            Log.e(LOG_TAG, "Fetching data from JSON failed", e);
        }

        return news;

    }

    /*
     * Creates an HTTP request and returns its JSON response
     * @param requestUrl: the request URL of the HTTP request
     * @return String: the JSON response of the HTTP request
     */

    private static String makeHttpRequest(URL requestUrl) throws IOException {

        String jsonResponse = "";

        //If there is no request URL, return early
        if (requestUrl == null) {
            return jsonResponse;
        }

        //Initialize variables for the HTTP connection and the InputStream
        HttpURLConnection connection = null;
        InputStream inputStream = null;

        try {

            //Try to make an HTTP connection with the request URL and set the properties of the request
            connection = (HttpURLConnection) requestUrl.openConnection();
            connection.setConnectTimeout(15000);
            connection.setReadTimeout(10000);
            connection.setRequestMethod("GET");

            //Send the request
            connection.connect();

            //If the request is successful, get the InputStream from the response and extract the data from it by the
            // extractDataFromStream() method
            if (connection.getResponseCode() == 200){
                inputStream = connection.getInputStream();
                jsonResponse = readDataFromStream(inputStream);
            }
            else {

                //If the response failed, print it to the log
                Log.e(LOG_TAG, "Server response error: " + connection.getResponseCode());
            }
        } catch (IOException e) {

            //If the connection failed, print it to the log
            Log.e(LOG_TAG, "Connection failed", e);
        }finally {

            //Disconnect the connection if it isn't disconnected yet
            if (connection != null){
                connection.disconnect();
            }

            //Close the InputStream if it isn't closed yet
            if (inputStream != null){
                inputStream.close();
            }
        }

        return jsonResponse;
    }


    /*
     * Reads the data from the InputStream
     * @param inputStream: the inputStream got from the response of the HTTP request
     * @return String: the JSON response of the HTTP request
     */

    private static String readDataFromStream(InputStream inputStream) throws IOException{

        //Creates a new StringBuilder
        StringBuilder output = new StringBuilder();

        //If the inputStream exists, creates an InputStreamReader from it and a BufferedReader from the InputStreamReader
        if (inputStream != null){
        InputStreamReader inputStreamReader = new InputStreamReader(inputStream, Charset.forName("UTF-8"));
            BufferedReader bufferedReader = new BufferedReader(inputStreamReader);

            //Appends the data of the BufferedReader line by line to the StringBuilder
            String line = bufferedReader.readLine();
            while (line != null){
                output.append(line);
                line = bufferedReader.readLine();
            }
        }

        //Converts the output into String and returns it
        return output.toString();
    }

    /*
     * Creates a URL from a String
     * @param urlString: the String from which we want to create a URL
     * @return URL: the properly formatted URL
     */

    private static URL createUrl(String urlString) {

        URL url = null;

        //Try to create a valid URL from the String
        try {
            url = new URL(urlString);
        } catch (MalformedURLException e) {

            //If it fails, print the error to the log
            Log.e(LOG_TAG, "URL creation failed", e);
        }

        return url;
    }

}
