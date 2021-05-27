package com.ab.demomovie;

import androidx.annotation.NonNull;
import androidx.appcompat.app.AppCompatActivity;
import androidx.recyclerview.widget.LinearLayoutManager;
import androidx.recyclerview.widget.RecyclerView;

import android.content.Intent;
import android.os.Bundle;
import android.util.Log;
import android.view.LayoutInflater;
import android.view.Menu;
import android.view.MenuInflater;
import android.view.MenuItem;
import android.view.View;
import android.view.ViewGroup;
import android.widget.ImageView;
import android.widget.LinearLayout;
import android.widget.TextView;

import com.android.volley.Request;
import com.android.volley.Response;
import com.android.volley.VolleyError;
import com.android.volley.toolbox.StringRequest;
import com.squareup.picasso.Picasso;

import org.json.JSONArray;
import org.json.JSONException;
import org.json.JSONObject;

import java.util.ArrayList;

public class MainActivity extends AppCompatActivity {

    private final String TAG = Constants.TAG;
    private RecyclerView recyclerView;
    private CustomAdapter adapter;
    private ArrayList<MovieModel> movieItems = new ArrayList<>();
    private JSONObject jsonObject;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_main);

        // Init
        recyclerView = findViewById(R.id.recyclerView);
        recyclerView.setHasFixedSize(true);
        recyclerView.setLayoutManager(new LinearLayoutManager(this));

        getMovies();
    }

    @Override
    protected void onResume () {
        super.onResume();
        getMovies();
    }

    @Override
    public boolean onCreateOptionsMenu (Menu menu) {
        MenuInflater menuInflater = getMenuInflater();
        menuInflater.inflate(R.menu.menu, menu);
        return super.onCreateOptionsMenu(menu);
    }

    @Override
    public boolean onOptionsItemSelected (@NonNull MenuItem item) {
        if (item.getItemId() == R.id.create) {
            Intent intent = new Intent(this, CreateActivity.class);
            startActivity(intent);
        }
        return false;
    }

    public void getMovies() {
        String url = Constants.URL_READ;
        StringRequest stringRequest = new StringRequest(Request.Method.GET, url,
                new Response.Listener<String>() {
                    @Override
                    public void onResponse(String response) {
                        try {
                            movieItems.clear();
                            JSONArray jsonArray = new JSONArray(response);
                            for (int i = 0; i < jsonArray.length(); i++) {
                                jsonObject = jsonArray.getJSONObject(i);
                                movieItems.add(new MovieModel(
                                        jsonObject.getInt("mid"),
                                        jsonObject.getString("mtitle"),
                                        jsonObject.getInt("duration"),
                                        jsonObject.getString("images")
                                ));
                            }
                            adapter = new CustomAdapter(movieItems);
                            recyclerView.setAdapter(adapter);
                        } catch (JSONException e) {
                            e.printStackTrace();
                        }
                    }
                }, new Response.ErrorListener() {
            @Override
            public void onErrorResponse(VolleyError error) {
                Log.e(TAG, "onErrorResponse: ", error);
            }
        });
        MySingleton.getInstance(this).addToRequestQueue(stringRequest);
    }

    public class CustomAdapter extends RecyclerView.Adapter<CustomAdapter.ViewHolder> {

        private ArrayList<MovieModel> movieItems;

        public CustomAdapter(ArrayList<MovieModel> movieItems) {
            this.movieItems = movieItems;
        }

        @NonNull
        @Override
        public ViewHolder onCreateViewHolder(@NonNull ViewGroup parent, int viewType) {
            View view = LayoutInflater.from(parent.getContext())
                    .inflate(R.layout.rowlayout, parent, false);
            return new ViewHolder(view);
        }

        @Override
        public void onBindViewHolder(ViewHolder holder, int position) {
            MovieModel movieItem = movieItems.get(position);

            String images = Constants.URL_IMAGES + movieItem.getmImage();
            Picasso.get()
                    .load(images)
                    .into(holder.movieUrl);
            holder.movieTitle.setText(movieItem.getmTitle());
            holder.movieDur.setText("" + movieItem.getmDur() + " min");
            holder.layout.setOnClickListener(new View.OnClickListener() {
                @Override
                public void onClick(View v) {
                    Intent intent = new Intent(MainActivity.this, UpdateActivity.class);
                    intent.putExtra("mid", movieItem.getmId());
                    startActivity(intent);
                }
            });
        }

        @Override
        public int getItemCount() {
            return movieItems.size();
        }

        public class ViewHolder extends RecyclerView.ViewHolder {
            LinearLayout layout;
            ImageView movieUrl;
            TextView movieTitle;
            TextView movieDur;

            public ViewHolder(View itemView) {
                super(itemView);
                layout = itemView.findViewById(R.id.movieLayout);
                movieUrl = itemView.findViewById(R.id.imageUrl);
                movieTitle = itemView.findViewById(R.id.movieTitle);
                movieDur = itemView.findViewById(R.id.movieDuration);
            }
        }
    }
}