package com.udacityprojects.geonarz.ultimatemoviedb;

import android.app.Activity;
import android.util.Log;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.ArrayAdapter;
import android.widget.ImageView;

import com.squareup.picasso.Picasso;

import java.util.List;

public class MovieItemAdapter extends ArrayAdapter<MovieItem> {
    private static final String LOG_TAG = MovieItemAdapter.class.getSimpleName();

    public MovieItemAdapter(Activity context, List<MovieItem> movieItems) {
        super(context, 0, movieItems);
    }

    //Sets individual grid component with images
    @Override
    public View getView(int position, View convertView, ViewGroup parent){
        MovieItem movieItem = getItem(position);
        String imageBaseURL = "http://image.tmdb.org/t/p/w185/";

        if (convertView == null){
            convertView = LayoutInflater.from(getContext()).inflate(R.layout.movie_item,parent,false);
        }

        ImageView imageView = (ImageView) convertView.findViewById(R.id.movie_item_imageView);
        Picasso.with(getContext())
                .load(imageBaseURL+movieItem.posterPath)
                .into(imageView);

        Log.i(LOG_TAG,imageBaseURL+movieItem.posterPath);

        return convertView;
    }
}
