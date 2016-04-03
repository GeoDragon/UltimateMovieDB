package com.udacityprojects.geonarz.ultimatemoviedb;

import android.content.Context;
import android.os.Bundle;
import android.support.v7.app.AppCompatActivity;
import android.support.v7.widget.Toolbar;
import android.widget.ImageView;
import android.widget.TextView;

import com.squareup.picasso.Picasso;

public class MovieDetail extends AppCompatActivity {

    Context context = this;
    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_movie_detail);
        Toolbar toolbar = (Toolbar) findViewById(R.id.toolbar);
        setSupportActionBar(toolbar);

        //Sets ImageResource using Picasso and data from Intent
        ImageView imageView = (ImageView)findViewById(R.id.detail_imageView);
        Picasso.with(this)
                .load("http://image.tmdb.org/t/p/w185/" + getIntent().getExtras().getString("poster"))
                .into(imageView);

        TextView title = (TextView)findViewById(R.id.detail_title);
        TextView releaseDate = (TextView)findViewById(R.id.detail_releaseDate);
        TextView voteAverage = (TextView)findViewById(R.id.detail_voteAverage);
        TextView overview = (TextView)findViewById(R.id.detail_overview);

        //TODO Add error handling for these 5 lines below
        //Sets TextViews with their Add
        title.setText(getIntent().getExtras().getString("title"));
        releaseDate.setText("Release Date: "+getIntent().getExtras().getString("release_date"));
        voteAverage.setText("Rating:"+getIntent().getExtras().getString("vote_average"));
        overview.setText("Synopsis:\n"+getIntent().getExtras().getString("overview"));

        getSupportActionBar().setDisplayHomeAsUpEnabled(true);
    }

}
