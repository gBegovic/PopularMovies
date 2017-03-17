package com.example.android.popularmovies;

import android.content.Context;
import android.support.v7.widget.RecyclerView;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.ImageView;
import android.widget.TextView;
import com.squareup.picasso.Picasso;

/**
 * Adapter class that handles the Recycler View and its View holder
 * @author Goran Begovic
 */
class MoviesAdapter extends RecyclerView.Adapter<MoviesAdapter.MoviesAdapterViewHolder>{

    private String[] moviePosters, movieIds;
    private final MoviesAdapterOnClickHandler clickHandler;
    private Context context;

    /**
     * Interface with a single method that is supposed to handle click events
     */
    interface MoviesAdapterOnClickHandler{
        void onClick(String movieID);
    }

    /**
     * Adapter constructor
     * @param clickHandler The click handler that will be used in a View holder
     */
    MoviesAdapter(MoviesAdapterOnClickHandler clickHandler){

        this.clickHandler = clickHandler;
    }

    /**
     * View holder class that will be responsible for handling and caching items inside the Recycler View
     */
    class MoviesAdapterViewHolder extends  RecyclerView.ViewHolder implements View.OnClickListener{

        private final ImageView poster;
        private final TextView title;

        /**
         * View holder constructor
         * @param view Item in the Recycler View
         */
        private MoviesAdapterViewHolder(View view){

            super(view);
            poster = (ImageView) view.findViewById(R.id.iv_poster);
            title = (TextView) view.findViewById(R.id.tv_poster_title);
            view.setOnClickListener(this);
        }

        /**
         * Handles the clicks on an item in the list and sends
         * the movie ID to the MainActivity click handler
         * @param v Item being clicked on in the Recycler View
         */
        @Override
        public void onClick(View v) {
            int adapterPosition = getAdapterPosition();
            String id = movieIds[adapterPosition];
            clickHandler.onClick(id);
        }
    }

    /**
     * Method that creates new View holders inside the Recycler View
     * and inflates the layout for each, one at a time
     * @param viewGroup The View group from which context the View holder should be inflated
     * @param viewType Not used
     * @return Returns the new View holder object
     */
    @Override
    public MoviesAdapterViewHolder onCreateViewHolder(ViewGroup viewGroup, int viewType) {

        context = viewGroup.getContext();
        int layoutIdForListItem = R.layout.movies_list_item;
        LayoutInflater inflater = LayoutInflater.from(context);

        View view = inflater.inflate(layoutIdForListItem, viewGroup, false);
        return new MoviesAdapterViewHolder(view);
    }

    /**
     * Method that sends the data into the View holder on data change notification
     * @param holder The holder into which the data is passed
     * @param position The position of the View holder in the Recycler View
     */
    @Override
    public void onBindViewHolder(MoviesAdapterViewHolder holder, int position) {
        String movieImage = moviePosters[position*3];
        Picasso.with(context).load(context.getString(R.string.movie_poster_base_url_w185) + movieImage).into(holder.poster);
        String title = moviePosters[position*3+1];
        holder.title.setText(title);
        movieIds[position] = moviePosters[position*3+2];
    }

    /**
     * Method that returns the number of items contained in the Recycler View
     * @return Returns the number of items
     */
    @Override
    public int getItemCount(){

        if (moviePosters == null)
            return 0;
        return moviePosters.length/3;
    }

    /**
     * Method that passes data from the parameter to private variables
     * and notifies the Adapter that the data set has changed
     * @param moviePosters String array containing movie thumbnail, title and ID data
     */
    void setMoviesPosters(String[] moviePosters) {
        this.moviePosters = moviePosters;
        movieIds = new String[moviePosters.length/3];
        notifyDataSetChanged();
    }
}
