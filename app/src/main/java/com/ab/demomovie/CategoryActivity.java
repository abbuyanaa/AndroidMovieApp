package com.ab.demomovie;

import androidx.annotation.NonNull;
import androidx.appcompat.app.AppCompatActivity;
import androidx.recyclerview.widget.LinearLayoutManager;
import androidx.recyclerview.widget.RecyclerView;

import android.app.AlertDialog;
import android.os.Bundle;
import android.util.Log;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.TextView;
import android.widget.Toast;

import com.android.volley.Request;
import com.android.volley.Response;
import com.android.volley.VolleyError;
import com.android.volley.toolbox.StringRequest;

import org.json.JSONArray;
import org.json.JSONException;
import org.json.JSONObject;

import java.util.ArrayList;

public class CategoryActivity extends AppCompatActivity {

    private static String TAG = Constants.TAG;
    ArrayList<CategoryModel> categoryItems = new ArrayList<>();
    RecyclerView recyclerView;
    CustomAdapter adapter;
    JSONObject jsonObject;
    AlertDialog.Builder alertBuilder;
    AlertDialog alertDialog;

    @Override
    protected void onCreate (Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_category_list);

        // Init
        recyclerView = findViewById(R.id.recyclerView);
        recyclerView.setHasFixedSize(true);
        recyclerView.setLayoutManager(new LinearLayoutManager(this));

        getCategories();
    }

    public void createCategory() {
        AlertDialog.Builder alertBuilder = new AlertDialog.Builder(this);
//        View view = getLayoutInflater().inflate(R.layout.custom)
//        alertBuilder.setView(view);
    }

    public void getCategories() {
        String url = Constants.URL_CATEGORY;
        StringRequest stringRequest = new StringRequest(Request.Method.GET, url,
                new Response.Listener<String>() {
                    @Override
                    public void onResponse(String response) {
                        try {
                            categoryItems.clear();
                            JSONArray jsonArray = new JSONArray(response);
                            for (int i = 0; i < jsonArray.length(); i++) {
                                jsonObject = jsonArray.getJSONObject(i);
                                categoryItems.add(new CategoryModel(
                                        jsonObject.getInt("cid"),
                                        jsonObject.getString("cname")
                                ));
                            }
                            adapter = new CustomAdapter(categoryItems);
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

    public class CustomAdapter extends RecyclerView.Adapter< CustomAdapter.ViewHolder> {

        private ArrayList<CategoryModel> categoryItems;

        public CustomAdapter(ArrayList<CategoryModel> categoryItems) {
            this.categoryItems = categoryItems;
        }

        @NonNull
        @Override
        public CustomAdapter.ViewHolder onCreateViewHolder(@NonNull ViewGroup parent, int viewType) {
            View view = LayoutInflater.from(parent.getContext())
                    .inflate(R.layout.rowlayout_category, parent, false);
            return new CustomAdapter.ViewHolder(view);
        }

        @Override
        public void onBindViewHolder(CustomAdapter.ViewHolder holder, int position) {
            CategoryModel categoryItem = categoryItems.get(position);

            holder.textView.setText(categoryItem.getCname());
            holder.textView.setOnClickListener(new View.OnClickListener() {
                @Override
                public void onClick (View v) {
                    Toast.makeText(CategoryActivity.this,
                            categoryItem.getCname(), Toast.LENGTH_SHORT).show();
                }
            });
        }

        @Override
        public int getItemCount() {
            return categoryItems.size();
        }

        public class ViewHolder extends RecyclerView.ViewHolder {
            TextView textView;

            public ViewHolder(View itemView) {
                super(itemView);
                textView = itemView.findViewById(R.id.textView);
            }
        }
    }
}