package com.example.android.popularmovies;

import android.content.Intent;
import android.net.Uri;
import android.support.v4.app.LoaderManager;
import android.support.v4.content.AsyncTaskLoader;
import android.support.v4.content.Loader;
import android.support.v7.app.AppCompatActivity;
import android.os.Bundle;
import android.text.InputType;
import android.view.View;
import android.widget.Button;
import android.widget.ImageView;
import android.widget.LinearLayout;
import android.widget.ProgressBar;
import android.widget.ScrollView;
import android.widget.TextView;

import com.example.android.popularmovies.utilities.ArrayUtils;
import com.example.android.popularmovies.utilities.JsonUtils;
import com.example.android.popularmovies.utilities.NetworkUtils;
import com.squareup.picasso.Picasso;
import org.json.JSONException;
import java.io.IOException;
import java.net.URL;
import java.text.ParseException;
import java.text.SimpleDateFormat;

/**
 * Activity class that handles the movie details screen invoked when the user clicks on a movie thumbnail
 * @author Goran Begovic
 */
public class MovieDetailsActivity extends AppCompatActivity implements LoaderManager.LoaderCallbacks<String[]>{

    private TextView originalTitle, title, plot, rating, releaseDate, errorMessage;
    private ImageView poster;
    private ProgressBar progressBar;
    private String idExtra = "";
    private ScrollView scrollView;
    private LinearLayout linearLayout;
    private static final int ID_MOVIES_LOADER = 21;
    private static final String SEARCH_QUERY_URL_EXTRA = "query";

    /**
     * Method that initializes the activity
     * @param savedInstanceState Bundle that holds the application state data
     */
    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_movie_details);

        scrollView = (ScrollView) findViewById(R.id.sv_details);
        linearLayout = (LinearLayout) findViewById(R.id.lv_details);
        originalTitle = (TextView) findViewById(R.id.tv_details_original_title);
        title = (TextView) findViewById(R.id.tv_details_title);
        plot = (TextView) findViewById(R.id.tv_details_plot);
        rating = (TextView) findViewById(R.id.tv_details_rating);
        releaseDate = (TextView) findViewById(R.id.tv_details_release_date);
        poster = (ImageView) findViewById(R.id.iv_details_poster);
        errorMessage = (TextView) findViewById(R.id.tv_details_error_message);
        progressBar = (ProgressBar) findViewById(R.id.pb_details_loader);
        Intent intent = getIntent();
        if (intent.hasExtra(Intent.EXTRA_TEXT))
            idExtra = intent.getStringExtra(Intent.EXTRA_TEXT);

        if(savedInstanceState != null){

            idExtra = savedInstanceState.getString(SEARCH_QUERY_URL_EXTRA);
            getSupportLoaderManager().restartLoader(ID_MOVIES_LOADER, savedInstanceState, this);
        }
        else {
            Bundle bundle = new Bundle();
            bundle.putString(SEARCH_QUERY_URL_EXTRA, idExtra);
            getSupportLoaderManager().initLoader(ID_MOVIES_LOADER, bundle, this);
        }
    }

    @Override
    protected void onSaveInstanceState(Bundle outState) {
        super.onSaveInstanceState(outState);

        outState.putString(SEARCH_QUERY_URL_EXTRA, idExtra);
    }

    /**
     * Method that hides the error message TextView and shows the content holder;
     * Called when the content data is gathered properly
     */
    public void showContent(){

        errorMessage.setVisibility(View.INVISIBLE);
        scrollView.setVisibility(View.VISIBLE);
    }

    /**
     * Method that shows the error message TextView and hides the content holder;
     * Called when the content data is not gathered properly (i.e. No Internet connection)
     */
    public void showErrorMessage(){

        scrollView.setVisibility(View.INVISIBLE);
        errorMessage.setVisibility(View.VISIBLE);
    }

    @Override
    public Loader<String[]> onCreateLoader(int id, final Bundle bundle) {
        return new AsyncTaskLoader<String[]>(this) {

            @Override
            protected void onStartLoading(){

                if(bundle == null){
                    return;
                }

                progressBar.setVisibility(View.VISIBLE);

                forceLoad();
            }

            @Override
            public String[] loadInBackground() {

                String[] movieDetails;

                try {

                    URL detailsRequestUrl = NetworkUtils.buildDetailsUrl(bundle.getString(SEARCH_QUERY_URL_EXTRA));
                    String JsonDetailsResponse = NetworkUtils.getResponseFromHttpUrl(detailsRequestUrl);
                    String[] details = JsonUtils.getSimpleMovieDetailsFromJson(JsonDetailsResponse);

                    URL reviewsRequestUrl = NetworkUtils.buildReviewsUrl(bundle.getString(SEARCH_QUERY_URL_EXTRA));
                    String JsonReviewsResponse = NetworkUtils.getResponseFromHttpUrl(reviewsRequestUrl);
                    String[] reviews = JsonUtils.getSimpleMovieReviewsFromJson(JsonReviewsResponse);

                    URL videosRequestUrl = NetworkUtils.buildVideosUrl(bundle.getString(SEARCH_QUERY_URL_EXTRA));
                    String JsonVideosResponse = NetworkUtils.getResponseFromHttpUrl(videosRequestUrl);
                    String[] videos = JsonUtils.getSimpleMovieVideosFromJson(JsonVideosResponse);

                    movieDetails = ArrayUtils.concatAll(details, reviews, videos);

                    return movieDetails;
                }
                catch (IOException e) {
                    return null;
                }
                catch (JSONException e) {
                    return null;
                }
                catch (NullPointerException e){
                    return null;
                }
            }
        };
    }

    @Override
    public void onLoadFinished(Loader<String[]> loader, final String[] movieDetails) {

        progressBar.setVisibility(View.INVISIBLE);

        if (movieDetails != null) {

            originalTitle.setText(movieDetails[0]);

            if(!movieDetails[6].equals(getString(R.string.language_en))) {
                title.setText(getString(R.string.title, movieDetails[1]));
                title.setVisibility(View.VISIBLE);
            }
            Picasso.with(MovieDetailsActivity.this).load(getString(R.string.movie_poster_base_url_w780) + movieDetails[2]).into(poster);

            plot.append(movieDetails[3]);
            rating.append(movieDetails[4]);

            for(int i = 8 + (Integer.parseInt(movieDetails[7]) * 2); i < movieDetails.length; i+=2){

                Button button = new Button(this);
                button.setLayoutParams(new LinearLayout.LayoutParams(
                        LinearLayout.LayoutParams.MATCH_PARENT,
                        LinearLayout.LayoutParams.MATCH_PARENT));
                button.setPadding(0, 10, 0, 0);
                button.setText(movieDetails[i]);
                button.setTextSize(20);
                button.setInputType(InputType.TYPE_TEXT_FLAG_CAP_WORDS);
                final Uri videoUri = NetworkUtils.buildYoutubeUri(movieDetails[i+1]);

                button.setOnClickListener(new View.OnClickListener() {
                    @Override
                    public void onClick(View v) {

                        Intent intent = new Intent(Intent.ACTION_VIEW, videoUri);
                        startActivity(intent);
                    }
                });

                linearLayout.addView(button);
            }

            for(int i = 8; i < 7 + (Integer.parseInt(movieDetails[7]) * 2); i+=2) {

                TextView textView = new TextView(this);
                textView.setLayoutParams(new LinearLayout.LayoutParams(
                        LinearLayout.LayoutParams.MATCH_PARENT,
                        LinearLayout.LayoutParams.MATCH_PARENT));
                textView.setPadding(0, 10, 0, 0);
                textView.setTextSize(16);
                linearLayout.addView(textView);
                textView.append(movieDetails[i]+"\n\n");
                textView.append(movieDetails[i+1]+"\n");
            }

            try {
                releaseDate.append(new SimpleDateFormat(getString(R.string.output_date_format)).format(new SimpleDateFormat(getString(R.string.input_date_format)).parse(movieDetails[5])));
            }
            catch (ParseException e) {
                e.printStackTrace();
            }

            showContent();
        }

        else {
            showErrorMessage();
        }
    }

    @Override
    public void onLoaderReset(Loader<String[]> loader) {

    }
}
