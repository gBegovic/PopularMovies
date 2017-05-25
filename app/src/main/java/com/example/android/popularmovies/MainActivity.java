package com.example.android.popularmovies;

import android.content.Context;
import android.content.Intent;
import android.database.Cursor;
import android.support.v4.app.LoaderManager;
import android.support.v4.content.AsyncTaskLoader;
import android.support.v4.content.CursorLoader;
import android.support.v4.content.Loader;
import android.support.v7.app.AppCompatActivity;
import android.os.Bundle;
import android.support.v7.widget.GridLayoutManager;
import android.support.v7.widget.RecyclerView;
import android.util.Log;
import android.view.Menu;
import android.view.MenuItem;
import android.view.View;
import android.widget.ProgressBar;
import android.widget.TextView;

import com.example.android.popularmovies.data.MoviesContract;
import com.example.android.popularmovies.utilities.JsonUtils;
import com.example.android.popularmovies.utilities.NetworkUtils;
import org.json.JSONException;
import java.io.IOException;
import java.lang.ref.WeakReference;
import java.net.URL;
import java.text.ParseException;
import java.text.SimpleDateFormat;

import static com.example.android.popularmovies.data.MoviesContract.MoviesEntry.INDEX_EN_TITLE;
import static com.example.android.popularmovies.data.MoviesContract.MoviesEntry.INDEX_MOVIE_ID;
import static com.example.android.popularmovies.data.MoviesContract.MoviesEntry.INDEX_POSTER;
import static com.example.android.popularmovies.data.MoviesContract.MoviesEntry.MOVIE_DETAILS_PROJECTION;

/**
 * Main activity class which is started upon application launch, handles the display of movie thumbnails
 * gathered from the movies database, their sorting (popular/highest-rated) and clicks
 * @author Goran Begovic
 */
public class MainActivity extends AppCompatActivity implements MoviesAdapter.MoviesAdapterOnClickHandler, LoaderManager.LoaderCallbacks<String[]>{

    private RecyclerView recyclerView;
    private MoviesAdapter moviesAdapter;
    private ProgressBar progressBar;
    private TextView errorMessage;
    private int queryType = 1;
    private static WeakReference<Context> contextReference;
    private static final int ID_MOVIES_LOADER = 21;
    private static final int ID_DB_MOVIES_LOADER = 22;
    private static final String SEARCH_QUERY_URL_EXTRA = "query";

    private LoaderManager.LoaderCallbacks<Cursor> dbLoader
            = new LoaderManager.LoaderCallbacks<Cursor>() {

        @Override
        public Loader<Cursor> onCreateLoader(int id, Bundle args) {

            switch (id){

                case ID_DB_MOVIES_LOADER:
                    return new CursorLoader(MainActivity.this,
                            MoviesContract.MoviesEntry.CONTENT_URI,
                            MOVIE_DETAILS_PROJECTION,
                            null,
                            null,
                            null);

                default:
                    throw new RuntimeException("Loader Not Implemented: " + id);
            }
        }

        @Override
        public void onLoadFinished(Loader<Cursor> loader, Cursor data) {

            if (!(data != null && data.moveToFirst() && queryType == 3)){

                String[] emptyData = {""};
                moviesAdapter.setMoviesPosters(emptyData);
                return;
            }

            String[] stringData = new String[data.getCount()*3];
            Log.v("DATA: ", data.getCount()+"  " + stringData.length);
            for (int i = 0; i < stringData.length; i+=3){

                stringData[i] = data.getString(INDEX_POSTER);
                stringData[i+1] = data.getString(INDEX_EN_TITLE);
                stringData[i+2] = data.getInt(INDEX_MOVIE_ID) +"";
                Log.v("DATA: ", data.getString(INDEX_POSTER) + "  " + data.getString(INDEX_EN_TITLE) + "  " + data.getInt(INDEX_MOVIE_ID));
                data.moveToNext();
            }

            moviesAdapter.setMoviesPosters(stringData);
            showContent();
        }

        @Override
        public void onLoaderReset(Loader<Cursor> loader) {

        }
    };

    /**
     * Method that initializes the activity
     * @param savedInstanceState Bundle that holds the application state data
     */
    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_main);
        contextReference = new WeakReference<Context>(this);
        recyclerView = (RecyclerView) findViewById(R.id.rv_movies);
        GridLayoutManager layoutManager = new GridLayoutManager(this, 3);
        recyclerView.setLayoutManager(layoutManager);
        recyclerView.setHasFixedSize(true);
        progressBar = (ProgressBar) findViewById(R.id.pb_movies_loader);
        errorMessage = (TextView) findViewById(R.id.tv_error_message);
        moviesAdapter = new MoviesAdapter(this);
        recyclerView.setAdapter(moviesAdapter);

        if(savedInstanceState != null){
            Log.v("ON CREATE", "Saved instance working " + queryType);
            queryType = savedInstanceState.getInt(SEARCH_QUERY_URL_EXTRA);
            if (queryType == 3){
                getSupportLoaderManager().restartLoader(ID_DB_MOVIES_LOADER, null, dbLoader);
            }
            else {
                getSupportLoaderManager().restartLoader(ID_MOVIES_LOADER, savedInstanceState, this);
            }
        }
        else {
            Log.v("ON CREATE", "New Bundle " + queryType);
            Bundle bundle = new Bundle();
            bundle.putInt(SEARCH_QUERY_URL_EXTRA, queryType);
            if (queryType == 3){
                getSupportLoaderManager().initLoader(ID_DB_MOVIES_LOADER, null, dbLoader);
            }
            else {
                getSupportLoaderManager().initLoader(ID_MOVIES_LOADER, bundle, this);
            }
        }
    }

    @Override
    protected void onSaveInstanceState(Bundle outState) {
        super.onSaveInstanceState(outState);

        outState.putInt(SEARCH_QUERY_URL_EXTRA, queryType);
    }

    /**
     * Method for retrieving the MainActivity context in classes that don't have one, if necessary
     * @return Returns the MainActivity context
     */
    public static Context getMainContext(){

        return contextReference.get();
    }

    /**
     * Method that handles clicks on the movie thumbnail; Sends an Intent
     * @param movieId ID of the movie clicked on
     */
    @Override
    public void onClick(String movieId) {

        Intent intent = new Intent(this, MovieDetailsActivity.class);
        intent.putExtra(Intent.EXTRA_TEXT, movieId);
        intent.putExtra(getString(R.string.extra_query_type), queryType);
        startActivity(intent);
    }

    /**
     * Method that inflates the menu bar
     * @param menu Menu bar being inflated
     * @return Returns true if there are no errors
     */
    @Override
    public boolean onCreateOptionsMenu(Menu menu){
        getMenuInflater().inflate(R.menu.main, menu);
        return true;
    }

    /**
     * Method that handles clicks on menu items; swaps movie list sorting between popular and highest rated
     * @param item Menu item being clicked
     * @return Returns true if the menu item has been processed correctly,
     * and if not, it calls the super method which will dispatch the event to the right handler
     */
    @Override
    public boolean onOptionsItemSelected(MenuItem item) {

        Bundle bundle = new Bundle();

        switch (item.getItemId()){

            case R.id.action_most_popular:
                setTitle(R.string.popular_movies_label);
                queryType = 1;
                bundle.putInt(SEARCH_QUERY_URL_EXTRA, queryType);
                getSupportLoaderManager().restartLoader(ID_MOVIES_LOADER, bundle, this);
                return true;

            case R.id.action_top_rated:
                setTitle(R.string.top_rated_movies_label);
                queryType = 2;
                bundle.putInt(SEARCH_QUERY_URL_EXTRA, queryType);
                getSupportLoaderManager().restartLoader(ID_MOVIES_LOADER, bundle, this);
                return true;

            case R.id.action_favorites:
                setTitle(getString(R.string.favorite_movies_label));
                queryType = 3;
                getSupportLoaderManager().initLoader(ID_DB_MOVIES_LOADER, null, dbLoader);
                return true;

            default:
                return super.onOptionsItemSelected(item);
        }
    }

    /**
     * Method that hides the error message TextView and shows the content;
     * Called when the content data is gathered properly
     */
    public void showContent(){

        errorMessage.setVisibility(View.INVISIBLE);
        recyclerView.setVisibility(View.VISIBLE);
    }

    /**
     * Method that shows the error message TextView and hides the content holder;
     * Called when the content data is not gathered properly (i.e. No Internet connection)
     */
    public void showErrorMessage(){

        recyclerView.setVisibility(View.INVISIBLE);
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

                try {

                    URL moviesRequestUrl = NetworkUtils.buildUrl(bundle.getInt(SEARCH_QUERY_URL_EXTRA));
                    String JsonMoviesResponse = NetworkUtils.getResponseFromHttpUrl(moviesRequestUrl);
                    return JsonUtils.getSimpleMoviePostersFromJson(JsonMoviesResponse);
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
    public void onLoadFinished(Loader<String[]> loader, String[] data) {

        progressBar.setVisibility(View.INVISIBLE);

        if (data != null) {

            moviesAdapter.setMoviesPosters(data);
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
