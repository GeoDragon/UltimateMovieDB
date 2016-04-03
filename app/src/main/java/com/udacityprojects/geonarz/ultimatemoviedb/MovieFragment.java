package com.udacityprojects.geonarz.ultimatemoviedb;

import android.content.Intent;
import android.net.Uri;
import android.os.AsyncTask;
import android.os.Bundle;
import android.support.v4.app.Fragment;
import android.util.Log;
import android.view.LayoutInflater;
import android.view.Menu;
import android.view.MenuInflater;
import android.view.MenuItem;
import android.view.View;
import android.view.ViewGroup;
import android.widget.AdapterView;
import android.widget.GridView;

import org.json.JSONArray;
import org.json.JSONException;
import org.json.JSONObject;

import java.io.BufferedReader;
import java.io.IOException;
import java.io.InputStream;
import java.io.InputStreamReader;
import java.net.HttpURLConnection;
import java.net.URL;
import java.util.ArrayList;


public class MovieFragment extends Fragment {

    private MovieItemAdapter movieItemAdapter;

    //Can be popular or top_rated
    String sortType = "popular";


    public MovieFragment() {
    }

    @Override
    public void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setHasOptionsMenu(true);
    }

    @Override
    public void onCreateOptionsMenu(Menu menu, MenuInflater inflater) {
        inflater.inflate(R.menu.menu_main, menu);
    }

    //Menu Items
    @Override
    public boolean onOptionsItemSelected(MenuItem item) {
        if(item.getItemId()==R.id.action_sort_by_popularity){
            sortType="popular";
            updateScreen(sortType);
        }
        if(item.getItemId()==R.id.action_sort_by_rating){
            sortType="top_rated";
            updateScreen(sortType);
        }
        return super.onOptionsItemSelected(item);
    }


    //GridView used to populate the Main screen
    //Each entry of the GridView is populated using getView of Adapter class
    @Override
    public View onCreateView(LayoutInflater inflater, ViewGroup container,
                             Bundle savedInstanceState) {
        View rootView = inflater.inflate(R.layout.fragment_main, container, false);

        movieItemAdapter = new MovieItemAdapter(getActivity(),
                new ArrayList<MovieItem>());

        GridView gridView = (GridView) rootView.findViewById(R.id.grid_view);
        gridView.setAdapter(movieItemAdapter);

        gridView.setOnItemClickListener(new AdapterView.OnItemClickListener() {
            @Override
            public void onItemClick(AdapterView<?> parent, View view, int position, long id) {
                MovieItem movieItem = movieItemAdapter.getItem(position);
                Intent intent = new Intent(getActivity(), MovieDetail.class)
                        .putExtra("title",movieItem.original_title)
                        .putExtra("poster", movieItem.posterPath)
                        .putExtra("overview",movieItem.overview)
                        .putExtra("vote_average",movieItem.vote_average)
                        .putExtra("release_date",movieItem.release_date);
                startActivity(intent);

            }
        });

        return rootView;
    }

    private void updateScreen(String sortType){
        JsonManager jsonManager = new JsonManager();
        jsonManager.execute(sortType);
    }

    @Override
    public void onStart(){
        super.onStart();
        updateScreen(sortType);
    }

    /*AsyncTask which does all the JSON parsing and adding the parsed items to
      the CustomAdapter used.
     */

    public class JsonManager extends AsyncTask<String, Void, String[][]> {

        private final String LOG_TAG = JsonManager.class.getSimpleName();

        private MovieFragment movieFragment;

        /*Handles parsing:
             results array stores:
                Movie Title in [0],
                Poster Path in [1],
                Synopsis in [2],
                Rating in [3],
                Release Date in [4]
        */

        public String[][] getDataFromJson(String movieItemJsonStr) throws JSONException {

            final String TMDB_LIST = "results";
            final String TMDB_POSTER_PATH = "poster_path";

            JSONObject movieItemJson = new JSONObject(movieItemJsonStr);
            JSONArray movieItemArray = movieItemJson.getJSONArray(TMDB_LIST);

            String results[][] = new String[movieItemArray.length()][5];

            for(int i=0; i<movieItemArray.length(); i++){

                MovieItem movieItem;

                JSONObject movieEntry = movieItemArray.getJSONObject(i);

                results[i][0] = movieEntry.getString("original_title");
                results[i][1] = movieEntry.getString("poster_path");
                results[i][2] = movieEntry.getString("overview");
                results[i][3] = movieEntry.getString("vote_average");
                results[i][4] = movieEntry.getString("release_date");
            }

            return results;
        }

        //Handles the URL Connection
        @Override
        protected String[][] doInBackground(String... params) {

            HttpURLConnection urlConnection = null;

            BufferedReader reader = null;

            String movieItemJsonStr = null;

            try{

                final String MOVIE_BASE_URL = "http://api.themoviedb.org/3/movie";
                final String API_KEY = "api_key";

                Uri builtUri = Uri.parse(MOVIE_BASE_URL).buildUpon()
                        .appendPath(sortType)
                        .appendQueryParameter(API_KEY, BuildConfig.THE_MOVIE_DB_API_KEY)
                        .build();

                URL url = new URL(builtUri.toString());

                Log.i(LOG_TAG, "Built URI" + builtUri.toString());

                urlConnection = (HttpURLConnection) url.openConnection();
                urlConnection.setRequestMethod("GET");
                urlConnection.connect();

                InputStream inputStream = urlConnection.getInputStream();
                StringBuffer buffer = new StringBuffer();
                if(inputStream == null){
                    return  null;
                }

                reader = new BufferedReader(new InputStreamReader(inputStream));

                String line;

                while ((line = reader.readLine())!=null){
                    buffer.append(line + "\n");
                }

                if(buffer.length() == 0){
                    return null;
                }

                movieItemJsonStr = buffer.toString();

                Log.v(LOG_TAG, "MovieItem String: "+movieItemJsonStr);

            }
            catch (IOException e){
                Log.e("HttpURL section has bug","Error",e);
            }

            //Closing stuff no longer in needed
            finally {
                if(urlConnection != null){
                    urlConnection.disconnect();
                }
                if (reader!=null){
                    try{
                        reader.close();
                    } catch (final IOException e) {
                        Log.e(LOG_TAG, "Error closing Stream",e);
                    }
                }
            }

            //Getting Data from Json
            try {
                return getDataFromJson(movieItemJsonStr);
            }catch (JSONException e){
                Log.e(LOG_TAG, e.getMessage(), e);
                e.printStackTrace();
            }

            return null;

        }

        //Adds all the retreived Movie Entries into the CustomAdapter
        //Initializes the movieItem using its constructor before adding it to the adapter
        @Override
        protected void onPostExecute(String[][] result) {
            if(result!=null){
                movieItemAdapter.clear();
                MovieItem movieItem;
                for (int i = 0; i< result.length ; i++){
                    movieItem = new MovieItem(result[i]);
                    movieItemAdapter.add(movieItem);
                }
            }
        }
    }
}

