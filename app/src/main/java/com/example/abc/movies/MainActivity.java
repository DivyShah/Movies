package com.example.abc.movies;

import android.content.Intent;
import android.os.AsyncTask;
import android.os.Bundle;
import android.support.v7.app.AppCompatActivity;
import android.util.Log;
import android.view.View;
import android.widget.AdapterView;
import android.widget.ListView;

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
import java.util.ArrayList;

public class MainActivity extends AppCompatActivity  implements AdapterView.OnItemClickListener{

        private ListView listView;
    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_main);
        listView=(ListView)findViewById(R.id.list);
        listView.setOnItemClickListener(this);
        new Thread(new Runnable() {
            @Override
            public void run() {

            }
        }).start();

        new CheckConnectionStatus().execute("https://api.themoviedb.org/3/movie/popular?api_key=538d8f592156b545047806c686454069&language=en-US&page=1");
    }

    @Override
    public void onItemClick(AdapterView<?> parent, View view, int position, long id) {
            Intent intent= new Intent(this,MovieDetailActivity.class);
        intent.putExtra("MOVIE_DETAILS",(MovieDetails)parent.getItemAtPosition(position));
        startActivity(intent);
    }

    class CheckConnectionStatus extends AsyncTask<String,Void,String>
    {
        @Override
        protected String doInBackground(String... params) {
            URL url = null;
            try {
                url = new URL(params[0]);
            } catch (MalformedURLException e) {
                e.printStackTrace();
            }

            try {
                HttpURLConnection urlConnection= (HttpURLConnection) url.openConnection();



                InputStream inputstream = urlConnection.getInputStream();
                BufferedReader bufferedreader= new BufferedReader(new InputStreamReader(inputstream));
                String s=bufferedreader.readLine();
                bufferedreader.close();
                return  s;
                //text.setText( String.valueOf(urlConnection.getResponseCode()));
               // return String.valueOf(urlConnection.getResponseCode());
                // Log.i("Response",);
            } catch (IOException e) {
                Log.e("Response",e.getMessage(),e);
            }
            return null;
        }

        @Override
        protected void onPostExecute(String s) {
            super.onPostExecute(s);
            JSONObject jsonObject= null;

            try {
                System.out.println("Json string : ."+s);
                jsonObject = new JSONObject(s);
                ArrayList<MovieDetails> movieList= new ArrayList<>();

                JSONArray jsonArray= jsonObject.getJSONArray("results");
                for(int i=0;i<jsonArray.length();i++)
                {
                    JSONObject jsonObject1= jsonArray.getJSONObject(i);
                    MovieDetails moviedetails=  new MovieDetails();
                    moviedetails.setOriginal_title(jsonObject1.getString("original_title"));
                    moviedetails.setOverview(jsonObject1.getString("overview"));
                    moviedetails.setVote_average(jsonObject1.getDouble("vote_average"));
                    moviedetails.setRelease_date(jsonObject1.getString("release_date"));
                    moviedetails.setPoster_path(jsonObject1.getString("poster_path"));
                    movieList.add(moviedetails);
                }
                MovieArrayAdapter movieArrayAdapter= new MovieArrayAdapter(MainActivity.this,R.layout.movie_list,movieList);
                listView.setAdapter(movieArrayAdapter);
            } catch (JSONException e) {
                e.printStackTrace();
            }


        }
    }
}
