package com.udacityprojects.geonarz.ultimatemoviedb;

/**
 * Created by geonarz on 3/4/16.
 */
public class MovieItem {

    //Required Data for each Movie Entry (For Project 1)
    String original_title;
    String posterPath;
    String overview;
    String vote_average;
    String release_date;

    //Constructor
    //onPostExecute in MovieFragment
    public MovieItem(String[] data){
        this.original_title = data[0];
        this.posterPath = data[1];
        this.overview = data[2];
        this.vote_average = data[3];
        this.release_date = data[4];
    }
}
