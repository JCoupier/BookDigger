package com.example.android.bookdigger;

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
 * BookDigger created by JCoupier on 07/06/2017.
 * Helper methods related to requesting and receiving books data from Google API.
 */
public class BooksUtils {

    /** Tag for the log messages */
    private static final String LOG_TAG = BooksUtils.class.getSimpleName();

    // Constant keys
    private static final String KEY_TOTAL_ITEMS = "totalItems";
    private static final String KEY_ITEMS = "items";
    private static final String KEY_VOLUME_INFO = "volumeInfo";
    private static final String KEY_TITLE = "title";
    private static final String KEY_AUTHORS = "authors";
    private static final String KEY_IMAGE_LINKS = "imageLinks";
    private static final String KEY_SMALL_THUMBNAIL = "smallThumbnail";

    // totalItems value get from the API response
    public static int totalItems = 0;

    /**
     * Create a private constructor because no one should ever create a {@link BooksUtils} object.
     * This class is only meant to hold static variables and methods, which can be accessed
     * directly from the class name BooksUtils (and an object instance of BooksUtils is not needed).
     */
    private BooksUtils() {
    }

    /**
     * Return a list of {@link Book} objects that has been built up from
     * parsing the given JSON response.
     */
    private static List<Book> extractFeatureFromJson(String bookJSON) {
        // If the JSON string is empty or null, then return early.
        if (TextUtils.isEmpty(bookJSON)) {
            return null;
        }

        // Create an empty ArrayList that we can start adding books to
        List<Book> books = new ArrayList<>();

        // Try to parse the JSON response string. If there's a problem with the way the JSON
        // is formatted, a JSONException exception object will be thrown.
        // Catch the exception so the app doesn't crash, and print the error message to the logs.
        try {
            // Create a JSONObject from the JSON response string
            JSONObject baseJsonResponse = new JSONObject(bookJSON);

            // Extract the value for the key called "totalItems"
            totalItems = baseJsonResponse.getInt(KEY_TOTAL_ITEMS);

            // Return a null esponse if there is no item
            if (totalItems == 0) {
                return null;
            }

            // Extract the JSONArray associated with the key called "items",
            // which represents a list of items (or books).
            JSONArray bookArray = baseJsonResponse.getJSONArray(KEY_ITEMS);

            // For each book in the bookArray, create an {@link Book} object
            for (int i = 0; i <bookArray.length(); i++) {

                // Get a single book at position i within the list of books
                JSONObject currentBook = bookArray.getJSONObject(i);

                // For a given book, extract the JSONObject associated with the
                // key called "volumeInfo", which represents a list of all properties
                // for that book.
                JSONObject volumeInfo = currentBook.getJSONObject(KEY_VOLUME_INFO);

                // Extract the value for the key called "title"
                String title = volumeInfo.getString(KEY_TITLE);

                String authors = "";
                if (volumeInfo.has(KEY_AUTHORS)){
                    // Extract the value for the key called "authors"
                    JSONArray authorsArray = volumeInfo.getJSONArray(KEY_AUTHORS);

                    // Create a new StringBuilder for authors
                    StringBuilder authorsString = new StringBuilder();
                    for (int j = 0; j < authorsArray.length(); j++){
                        authorsString.append(authorsArray.getString(j));
                        if (j < authorsArray.length()-1){
                            authorsString.append(", ");
                        }
                    }
                    // Put into a String the output of the StringBuilder
                    authors = authorsString.toString();
                } else {
                    // Handle the case if there is no author
                    authors = ("No author");
                }

                String smallThumbnailUrl = null;
                if (volumeInfo.has(KEY_IMAGE_LINKS)) {
                    // Get the JSONObject with the key called "imageLinks"
                    JSONObject imageLinks = volumeInfo.getJSONObject(KEY_IMAGE_LINKS);
                    // Extract the value for the key called "smallThumbnail"
                    smallThumbnailUrl = imageLinks.getString(KEY_SMALL_THUMBNAIL);
                }

                // Create a new {@link Book} object with the title, authors and url for the small
                // thumbnail of the book from the JSON response.
                Book book = new Book(title, authors, smallThumbnailUrl);

                // Add the new {@link Book} to the list of books.
                books.add(book);
            }

        } catch (JSONException e) {
            // If an error is thrown when executing any of the above statements in the "try" block,
            // catch the exception here, so the app doesn't crash. Print a log message
            // with the message from the exception.
            Log.e("BooksUtils", "Problem parsing the book JSON results", e);
        }

        // Return the list of books
        return books;
    }

    /**
     * Returns new URL object from the given string URL.
     */
    private static URL createUrl(String stringUrl) {
        URL url = null;
        try {
            url = new URL(stringUrl);
        } catch (MalformedURLException e) {
            Log.e(LOG_TAG, "Problem building the URL ", e);
        }
        return url;
    }

    /**
     * Make an HTTP request to the given URL and return a String as the response.
     */
    private static String makeHttpRequest(URL url) throws IOException {
        String jsonResponse = "";
        int readTimeOut = 10000;
        int connectTimeOut = 15000;
        int okResponseCode = 200;

        // If the URL is null, then return early.
        if (url == null) {
            return jsonResponse;
        }

        HttpURLConnection urlConnection = null;
        InputStream inputStream = null;
        try {
            urlConnection = (HttpURLConnection) url.openConnection();
            urlConnection.setReadTimeout(readTimeOut /* milliseconds */);
            urlConnection.setConnectTimeout(connectTimeOut /* milliseconds */);
            urlConnection.setRequestMethod("GET");
            urlConnection.connect();

            // If the request was successful (response code 200),
            // then read the input stream and parse the response.
            if (urlConnection.getResponseCode() == okResponseCode) {
                inputStream = urlConnection.getInputStream();
                jsonResponse = readFromStream(inputStream);
            } else {
                Log.e(LOG_TAG, "Error response code: " + urlConnection.getResponseCode());
            }
        } catch (IOException e) {
            Log.e(LOG_TAG, "Problem retrieving the book JSON results.", e);
        } finally {
            if (urlConnection != null) {
                urlConnection.disconnect();
            }
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
     * Convert the {@link InputStream} into a String which contains the
     * whole JSON response from the server.
     */
    private static String readFromStream(InputStream inputStream) throws IOException {
        StringBuilder output = new StringBuilder();
        if (inputStream != null) {
            InputStreamReader inputStreamReader = new InputStreamReader(inputStream, Charset.forName("UTF-8"));
            BufferedReader reader = new BufferedReader(inputStreamReader);
            String line = reader.readLine();
            while (line != null) {
                output.append(line);
                line = reader.readLine();
            }
        }
        return output.toString();
    }

    /**
     * Query the Google API dataset and return a list of {@link Book} objects.
     */
    public static List<Book> fetchBookData(String requestUrl) {
        // Create URL object
        URL url = createUrl(requestUrl);

        // Perform HTTP request to the URL and receive a JSON response back
        String jsonResponse = null;
        try {
            jsonResponse = makeHttpRequest(url);
        } catch (IOException e) {
            Log.e(LOG_TAG, "Problem making the HTTP request.", e);
        }

        // Extract relevant fields from the JSON response and create a list of {@link Book}s
        List<Book> books = extractFeatureFromJson(jsonResponse);

        // Return the list of {@link Book}s
        return books;
    }
}
