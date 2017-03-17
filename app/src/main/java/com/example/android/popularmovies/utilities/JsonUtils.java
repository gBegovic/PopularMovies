package com.example.android.popularmovies.utilities;

import com.example.android.popularmovies.MainActivity;
import com.example.android.popularmovies.R;
import org.json.JSONArray;
import org.json.JSONException;
import org.json.JSONObject;
import java.net.HttpURLConnection;

/**
 * Utility class that handles data contained in a JSON String
 * @author Goran Begovic
 */
public class JsonUtils {

    private final static String RESULTS = MainActivity.getMainContext().getString(R.string.results_json);
    private final static String ID = MainActivity.getMainContext().getString(R.string.id_json);
    private final static String MOVIE_POSTER = MainActivity.getMainContext().getString(R.string.poster_path_json);
    private final static String TITLE = MainActivity.getMainContext().getString(R.string.title_json);
    private final static String ORIGINAL_TITLE = MainActivity.getMainContext().getString(R.string.original_title_json);
    private final static String PLOT = MainActivity.getMainContext().getString(R.string.plot_json);
    private final static String RATING = MainActivity.getMainContext().getString(R.string.rating_json);
    private final static String RELEASE_DATE = MainActivity.getMainContext().getString(R.string.release_date_json);
    private final static String ORIGINAL_LANGUAGE = MainActivity.getMainContext().getString(R.string.original_language_json);
    private final static String MESSAGE_CODE = MainActivity.getMainContext().getString(R.string.message_code_json);

    /**
     * Method for extracting movie posters, titles and database IDs from a JSON String
     * @param JsonString String from which the data is extracted
     * @return Returns a String array containing all extracted data
     * @throws JSONException
     */
    public static String[] getSimpleMoviePostersFromJson(String JsonString) throws JSONException{

        String[] parsedMoviePosters;
        JSONObject movies = new JSONObject(JsonString);

        if (movies.has(MESSAGE_CODE)) {
            int errorCode = movies.getInt(MESSAGE_CODE);

            switch (errorCode) {
                case HttpURLConnection.HTTP_OK:
                    break;
                case HttpURLConnection.HTTP_NOT_FOUND:
                    return null;
                default:
                    return null;
            }
        }

        JSONArray results = movies.getJSONArray(RESULTS);
        parsedMoviePosters = new String[results.length()*3];

        for (int i = 0; i < results.length()*3; i+=3){

            JSONObject movie = results.getJSONObject(i/3);

            parsedMoviePosters[i] = movie.getString(MOVIE_POSTER);
            parsedMoviePosters[i+1] = movie.getString(TITLE);
            parsedMoviePosters[i+2] = movie.getString(ID);
        }

        return parsedMoviePosters;
    }

    /**
     * Method for extracting movie details from a JSON String (requested via movie ID)
     * @param jsonString String from which the data is extracted
     * @return Returns a String array containing all extracted data
     * @throws JSONException
     */
    public static String[] getSimpleMovieDetailsFromJson(String jsonString) throws JSONException{

        String[] parsedMovieDetails = new String[7];
        JSONObject movie = new JSONObject(jsonString);

        if (movie.has(MESSAGE_CODE)) {
            int errorCode = movie.getInt(MESSAGE_CODE);

            switch (errorCode) {
                case HttpURLConnection.HTTP_OK:
                    break;
                case HttpURLConnection.HTTP_NOT_FOUND:
                    return null;
                default:
                    return null;
            }
        }

        parsedMovieDetails[0] = movie.getString(ORIGINAL_TITLE);
        parsedMovieDetails[1] = movie.getString(TITLE);
        parsedMovieDetails[2] = movie.getString(MOVIE_POSTER);
        parsedMovieDetails[3] = movie.getString(PLOT);
        parsedMovieDetails[4] = movie.getString(RATING);
        parsedMovieDetails[5] = movie.getString(RELEASE_DATE);
        parsedMovieDetails[6] = movie.getString(ORIGINAL_LANGUAGE);

        return parsedMovieDetails;
    }

    public static String[] getSimpleMovieReviewsFromJson(String jsonString) throws JSONException{

        JSONObject reviews = new JSONObject(jsonString);
        JSONArray results = reviews.getJSONArray(RESULTS);
        String[] parsedMovieReviews = new String[results.length()];

        if (reviews.has(MESSAGE_CODE)) {
            int errorCode = reviews.getInt(MESSAGE_CODE);

            switch (errorCode) {
                case HttpURLConnection.HTTP_OK:
                    break;
                case HttpURLConnection.HTTP_NOT_FOUND:
                    return null;
                default:
                    return null;
            }
        }



        return parsedMovieReviews;
    }

    public static String[] getSimpleMovieVideosFromJson(String jsonString) throws JSONException{

        JSONObject videos = new JSONObject(jsonString);
        JSONArray results = videos.getJSONArray(RESULTS);
        String[] parsedMovieVideos = new String[results.length()];

        if (videos.has(MESSAGE_CODE)) {
            int errorCode = videos.getInt(MESSAGE_CODE);

            switch (errorCode) {
                case HttpURLConnection.HTTP_OK:
                    break;
                case HttpURLConnection.HTTP_NOT_FOUND:
                    return null;
                default:
                    return null;
            }
        }



        return parsedMovieVideos;
    }
}
