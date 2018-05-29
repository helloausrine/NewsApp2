package com.example.ausrine.newsapp;

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

/**
 * Helper methods related to requesting and receiving news data
 */
public class Utils {

    // Key used for the JSON response
    static final String RESPONSE = "response";

    // Key used for the results with the list of news
    static final String RESULTS = "results";

    // Key used for the title of the news
    static final String NEWS_TITLE = "webTitle";

    // Key used for the section of the news
    static final String SECTION = "sectionName";

    // Key used for the section of the news
    static final String DATE = "webPublicationDate";

    // Key used for the URL of the news
    static final String NEWS_URL = "webUrl";

    // Tag for the log messages
    private static final String LOG_TAG = Utils.class.getSimpleName();

    /**
     * Create a private constructor.
     * This class is only meant to hold static variables and methods, which can be accessed
     * directly from the class name Utils (and an object instance of Utils is not needed).
     */
    private Utils() {
    }

    /**
     * Query the dataset and return a list of News objects.
     */
    public static List<News> fetchNewsData(String requestUrl) {
        // Create the URL object
        URL url = createUrl(requestUrl);

        // Perform HTTP request to the URL and receive a JSON response back
        String jsonResponse = null;
        try {
            // Try to create a HTTP request with the request URL by using the makeHttpRequest method to get the data
            jsonResponse = makeHttpRequest(url);
        } catch (IOException e) {
            // If the request fails, print the error message to the Log
            Log.e(LOG_TAG, "Problem making the HTTP request.", e);
        }

        // Extract relevant fields from the JSON response and create a list of News
        List<News> News = extractFeatureFromJson(jsonResponse);

        // Return the list of News
        return News;
    }

    /**
     * Returns new URL object from the given string URL.
     */
    private static URL createUrl(String stringUrl) {
        URL url = null;
        try {
            // Try to create an URL from the String
            url = new URL(stringUrl);
        } catch (MalformedURLException e) {
            // If the request fails, print the error to the Log
            Log.e(LOG_TAG, "Problem with building the URL.", e);
        }
        return url;
    }

    /**
     * Make an HTTP request to the given URL and return a String as the response.
     */
    private static String makeHttpRequest(URL url) throws IOException {

        String jsonResponse = "";

        // If the URL is null (if there is no request URL), then return early.
        if (url == null) {
            return jsonResponse;
        }

        // Initialize variables for the HTTP connection and for the InputStream
        HttpURLConnection urlConnection = null;
        InputStream inputStream = null;
        try {
            // Try to establish a HTTP connection with the request URL and set up the properties of the request
            urlConnection = (HttpURLConnection) url.openConnection();
            urlConnection.setReadTimeout(10000 /* milliseconds */);
            urlConnection.setConnectTimeout(15000 /* milliseconds */);
            urlConnection.setRequestMethod("GET");

            // Send a request to connect
            urlConnection.connect();

            // If the request was successful (response code 200),
            // then read the Input Stream and parse the response.
            if (urlConnection.getResponseCode() == 200) {
                inputStream = urlConnection.getInputStream();
                jsonResponse = readFromStream(inputStream);
            } else {
                // If the response failed, print it to the Log
                Log.e(LOG_TAG, "Error response code: " + urlConnection.getResponseCode());
            }
        } catch (IOException e) {

            // If the connection was not established, print it to the log
            Log.e(LOG_TAG, "Problem retrieving JSON results from Guardian News.", e);
        } finally {

            // Disconnect the HTTP connection if it is not disconnected yet
            if (urlConnection != null) {
                urlConnection.disconnect();
            }

            // Close the Input Stream if it is not closed yet
            if (inputStream != null) {
                // Closing the input stream could throw an IOException, which is why
                // the makeHttpRequest(URL url) method signature specifies than an IOException
                // could be thrown.
                inputStream.close();
            }
        }
        return jsonResponse;
    }

    /**
     * Convert the InputStream into a String which contains the
     * whole JSON response from the server.
     */
    private static String readFromStream(InputStream inputStream) throws IOException {

        // Create a new StringBuilder
        StringBuilder output = new StringBuilder();

        // If the InputStream exists, create an InputStreamReader from it and a BufferedReader from the InputStreamReader
        if (inputStream != null) {
            InputStreamReader inputStreamReader = new InputStreamReader(inputStream, Charset.forName("UTF-8"));
            BufferedReader reader = new BufferedReader(inputStreamReader);

            // Append the data of the BufferedReader line by line to the StringBuilder
            String line = reader.readLine();
            while (line != null) {
                output.append(line);
                line = reader.readLine();
            }
        }

        // Convert the output into String and return it
        return output.toString();
    }

    /**
     * Return a list of News objects that has been built up from
     * parsing the given JSON response.
     */
    private static List<News> extractFeatureFromJson(String NewsJSON) {

        // If the JSON string is empty or null, then return early.
        if (TextUtils.isEmpty(NewsJSON)) {
            return null;
        }

        // Create an empty List that we can start adding News to
        List<News> News = new ArrayList<>();

        // Try to parse the JSON response string. If there's a problem with the way the JSON
        // is formatted, a JSONException exception object will be thrown.
        // Catch the exception so the app doesn't crash, and print the error message to the logs.
        try {

            // Create a JSONObject from the JSON response string
            JSONObject baseJsonResponse = new JSONObject(NewsJSON);

            // Extract the JSONObject associated with the key called "response",
            // which represents the response received from the server with News.
            JSONObject responseNewsObject = baseJsonResponse.getJSONObject(RESPONSE);

            // Extract the JSONArray associated with the key called "results",
            // which represents a list of results with News.
            JSONArray resultsNewsArray = responseNewsObject.getJSONArray(RESULTS);

            // For each News in the NewsArray, create an News object
            for (int i = 0; i < resultsNewsArray.length(); i++) {

                // Get a single News at position i within the list of News
                JSONObject currentNews = resultsNewsArray.getJSONObject(i);

                String title = "N/A";
                if (currentNews.has(NEWS_TITLE)) {
                    // Extract the value for the key called "webTitle" - Get the Web Title of the current News
                    title = currentNews.getString(NEWS_TITLE);
                }

                String section = "N/A";
                if (currentNews.has(SECTION)) {
                    // Extract the value for the key called "sectionName" - Get the Section Name of the current News
                    section = currentNews.getString(SECTION);
                }

                String date = "N/A";
                if (currentNews.has(DATE)) {
                    // Extract the value for the key called "webPublicationDate" - Get the Publication Date of the current News
                    date = currentNews.getString(DATE);
                }

                String newsUrl = "N/A";
                if (currentNews.has(NEWS_URL)) {
                    // Extract the value for the key called "webUrl" - Get the Web URL of the current News
                    newsUrl = currentNews.getString(NEWS_URL);
                }

                // Create a new News object with the title, section, date and newsUrl from the JSON response
                News newsNews = new News(title, section, date, newsUrl);

                // Add the new News object created to the list of News.
                News.add(newsNews);
            }

        } catch (JSONException e) {
            // If an error is thrown when executing any of the above statements in the "try" block,
            // catch the exception here, so the app doesn't crash. Print a log message
            // with the message from the exception.
            Log.e(LOG_TAG, "Problem with parsing JSON results from Guardian News.", e);
        }

        // Return the list of News
        return News;
    }
}
