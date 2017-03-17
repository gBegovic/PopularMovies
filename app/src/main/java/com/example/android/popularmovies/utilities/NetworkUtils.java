package com.example.android.popularmovies.utilities;

import android.net.Uri;
import com.example.android.popularmovies.MainActivity;
import com.example.android.popularmovies.R;
import java.io.IOException;
import java.io.InputStream;
import java.net.HttpURLConnection;
import java.net.MalformedURLException;
import java.net.URL;
import java.util.Scanner;

/**
 * Utility class that handles URL building and sending HTTP requests to the movies database
 * @author Goran Begovic
 */
public class NetworkUtils {

    private static final String MOVIES_BASE_URL = MainActivity.getMainContext().getString(R.string.movies_base_url);
    private static final String YOUTUBE_BASE_URL = "http://www.youtube.com/watch";
    private static final String YOUTUBE_VIDEO_PARAM = "v";
    private static final String POPULAR = MainActivity.getMainContext().getString(R.string.popular);
    private static final String TOP_RATED = MainActivity.getMainContext().getString(R.string.top_rated);
    private static final String VIDEOS = "videos";
    private static final String REVIEWS = "reviews";
    private static final String API_KEY_PARAM = MainActivity.getMainContext().getString(R.string.api_key_param);
    private static final String API_KEY = MainActivity.getMainContext().getString(R.string.api_key);

    /**
     * Method that builds the URL for popular or highest-rated movies request
     * @param type Defines the type of query (popular or highest-rated movies)
     * @return Returns the built URL
     */
    public static URL buildUrl(int type) {

        String urlExtension = "";

        if(type == 1)
            urlExtension = POPULAR;
        else if(type == 2)
            urlExtension = TOP_RATED;

        Uri builtUri = Uri.parse(MOVIES_BASE_URL + urlExtension).buildUpon()
                .appendQueryParameter(API_KEY_PARAM, API_KEY)
                .build();

        URL url = null;
        try {
            url = new URL(builtUri.toString());
        } catch (MalformedURLException e) {
            e.printStackTrace();
        }

        return url;
    }

    /**
     * Method that builds the URL for movie details request
     * @param urlExtension Movie ID for which we want to get the details
     * @return Returns the built URL
     */
    public static URL buildDetailsUrl(String urlExtension) {

        Uri builtUri = Uri.parse(MOVIES_BASE_URL + urlExtension).buildUpon()
                .appendQueryParameter(API_KEY_PARAM, API_KEY)
                .build();

        URL url = null;
        try {
            url = new URL(builtUri.toString());
        } catch (MalformedURLException e) {
            e.printStackTrace();
        }

        return url;
    }

    public static URL buildVideosUrl(String urlExtension) {

        Uri builtUri = Uri.parse(MOVIES_BASE_URL + urlExtension + VIDEOS).buildUpon()
                .appendQueryParameter(API_KEY_PARAM, API_KEY)
                .build();

        URL url = null;
        try {
            url = new URL(builtUri.toString());
        } catch (MalformedURLException e) {
            e.printStackTrace();
        }

        return url;
    }

    public static URL buildReviewsUrl(String urlExtension) {

        Uri builtUri = Uri.parse(MOVIES_BASE_URL + urlExtension + REVIEWS).buildUpon()
                .appendQueryParameter(API_KEY_PARAM, API_KEY)
                .build();

        URL url = null;
        try {
            url = new URL(builtUri.toString());
        } catch (MalformedURLException e) {
            e.printStackTrace();
        }

        return url;
    }

    public static URL buildYoutubeUrl(String urlParam) {

        Uri builtUri = Uri.parse(YOUTUBE_BASE_URL).buildUpon()
                .appendQueryParameter(YOUTUBE_VIDEO_PARAM, urlParam)
                .build();

        URL url = null;
        try {
            url = new URL(builtUri.toString());
        } catch (MalformedURLException e) {
            e.printStackTrace();
        }

        return url;
    }

    /**
     * Method that sends the HTTP request to the movies database
     * @param url URL used for querying the database
     * @return Returns the JSON String with movies data, if found
     * @throws IOException
     */
    public static String getResponseFromHttpUrl(URL url) throws IOException {
        HttpURLConnection urlConnection = (HttpURLConnection) url.openConnection();
        try {
            InputStream in = urlConnection.getInputStream();

            Scanner scanner = new Scanner(in);
            scanner.useDelimiter(MainActivity.getMainContext().getString(R.string.delimiter));

            boolean hasInput = scanner.hasNext();
            if (hasInput) {
                return scanner.next();
            } else {
                return null;
            }
        } finally {
            urlConnection.disconnect();
        }
    }
}
