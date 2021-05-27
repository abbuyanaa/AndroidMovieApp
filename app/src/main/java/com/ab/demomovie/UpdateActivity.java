package com.ab.demomovie;

import androidx.annotation.NonNull;
import androidx.annotation.Nullable;
import androidx.appcompat.app.AppCompatActivity;
import androidx.core.app.ActivityCompat;

import android.Manifest;
import android.content.Intent;
import android.content.pm.PackageManager;
import android.graphics.Bitmap;
import android.graphics.BitmapFactory;
import android.net.Uri;
import android.os.Bundle;
import android.util.Base64;
import android.util.Log;
import android.view.View;
import android.widget.AdapterView;
import android.widget.ArrayAdapter;
import android.widget.Button;
import android.widget.EditText;
import android.widget.ImageView;
import android.widget.Spinner;
import android.widget.Toast;

import com.android.volley.AuthFailureError;
import com.android.volley.Request;
import com.android.volley.Response;
import com.android.volley.VolleyError;
import com.android.volley.toolbox.StringRequest;
import com.squareup.picasso.Picasso;

import org.json.JSONArray;
import org.json.JSONException;
import org.json.JSONObject;

import java.io.ByteArrayOutputStream;
import java.io.FileNotFoundException;
import java.io.InputStream;
import java.util.ArrayList;
import java.util.HashMap;
import java.util.Map;

public class UpdateActivity extends AppCompatActivity implements View.OnClickListener {

    public static String TAG = Constants.TAG;
    ImageView imageUrl;
    Button btnImage, btnUpdate, btnDelete;
    EditText editTitle, editDesc, editYear, editDir, editDur;
    Spinner spinner;
    ArrayList< Integer > catId = new ArrayList<>();
    ArrayList< String > catName = new ArrayList<>();
    ArrayAdapter adapter;
    Bundle bundle;
    int movieId;
    int getCatId;
    final int CODE_GALLERY_REQUEST = 999;
    Bitmap bitmap;
    String current_image_url;

    @Override
    protected void onCreate (Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_update);

        // Spinner component-d Adapter -aar damjuulan kinonii torluudiig nemj bn
        imageUrl = findViewById(R.id.imageUrl);
        btnImage = findViewById(R.id.btnImage);
        btnUpdate = findViewById(R.id.btnUpdate);
        btnDelete = findViewById(R.id.btnDelete);

        spinner = findViewById(R.id.spinner);
        editTitle = findViewById(R.id.movieTitle);
        editDesc = findViewById(R.id.movieDesc);
        editYear = findViewById(R.id.movieYear);
        editDir = findViewById(R.id.movieDir);
        editDur = findViewById(R.id.movieDur);

        // Spinner Adapter
        adapter = new ArrayAdapter(this, android.R.layout.simple_list_item_1, catName);
        adapter.setDropDownViewResource(android.R.layout.simple_spinner_dropdown_item);
        spinner.setAdapter(adapter);

        // Category Data
        getCategory();
        bundle = getIntent().getExtras();
        movieId = bundle.getInt("mid");
        getData(movieId);

        // Spinner component-d bga kinonii torloos songoh event uildel
        spinner.setOnItemSelectedListener(new AdapterView.OnItemSelectedListener() {
            @Override
            public void onItemSelected (AdapterView< ? > parent, View view, int position, long id) {
                getCatId = catId.get(position);
            }

            @Override
            public void onNothingSelected (AdapterView< ? > parent) {
                //
            }
        });

        btnUpdate.setOnClickListener(this);
        btnImage.setOnClickListener(this);
        btnDelete.setOnClickListener(this);
    }

    @Override
    public void onRequestPermissionsResult (int requestCode, @NonNull String[] permissions, @NonNull int[] grantResults) {
        if (requestCode == CODE_GALLERY_REQUEST) {
            if (grantResults.length > 0 && grantResults[0] == PackageManager.PERMISSION_GRANTED) {
                Intent intent = new Intent(Intent.ACTION_PICK);
                intent.setType("image/*");
                startActivityForResult(Intent.createChooser(intent, "Select Image"), CODE_GALLERY_REQUEST);
            } else {
                Toast.makeText(this, "Permission DENIED", Toast.LENGTH_SHORT).show();
            }
        }
        super.onRequestPermissionsResult(requestCode, permissions, grantResults);
    }

    @Override
    protected void onActivityResult (int requestCode, int resultCode, @Nullable Intent data) {
        if (requestCode == CODE_GALLERY_REQUEST && resultCode == RESULT_OK
                && data != null && data.getData() != null) {
            Uri filePath = data.getData();
            try {
                InputStream inputStream = getContentResolver().openInputStream(filePath);
                bitmap = BitmapFactory.decodeStream(inputStream);
                imageUrl.setImageBitmap(bitmap);
            } catch (FileNotFoundException e) {
                e.printStackTrace();
            }
        }
        super.onActivityResult(requestCode, resultCode, data);
    }

    public void movieImage() {
        ActivityCompat.requestPermissions(
                UpdateActivity.this,
                new String[]{Manifest.permission.READ_EXTERNAL_STORAGE},
                CODE_GALLERY_REQUEST);
    }

    public void movieUpdate () {
        String url = Constants.URL_UPDATE;
        StringRequest stringRequest = new StringRequest(Request.Method.POST, url,
                new Response.Listener< String >() {
                    @Override
                    public void onResponse (String response) {
                        Log.d(TAG, "onResponse: " + response);
                        try {
                            JSONObject jsonObject = new JSONObject(response);
                            if (jsonObject.getBoolean("status")) {
                                finish();
                            } else {
                                Toast.makeText(UpdateActivity.this, response, Toast.LENGTH_SHORT).show();
                            }
                        } catch (JSONException e) {
                            e.printStackTrace();
                        }
                    }
                }, new Response.ErrorListener() {
            @Override
            public void onErrorResponse (VolleyError error) {
                Log.d(TAG, "Update: " + error);
            }
        }) {
            @Override
            protected Map< String, String > getParams () throws AuthFailureError {
                Map< String, String > params = new HashMap<>();
                params.put("mid", String.valueOf(movieId));
                if (bitmap != null) {
                    String imageData = imageToString(bitmap);
                    params.put(Constants.mImage, imageData);
                } else {
                    params.put(Constants.mImage, "");
                }
                params.put(Constants.mTitle, editTitle.getText().toString());
                params.put(Constants.mDesc, editDesc.getText().toString());
                params.put(Constants.mYear, editYear.getText().toString());
                params.put(Constants.mDuration, editDur.getText().toString());
                params.put(Constants.mDirector, editDir.getText().toString());
                params.put(Constants.mCategory, String.valueOf(getCatId));
                return params;
            }
        };
        MySingleton.getInstance(this).addToRequestQueue(stringRequest);
    }

    public void movieDelete() {
        String url = Constants.URL_DELETE;
        StringRequest stringRequest = new StringRequest(Request.Method.POST, url,
                new Response.Listener< String >() {
                    @Override
                    public void onResponse (String response) {
                        try {
                            JSONObject jsonObject = new JSONObject(response);
                            if (jsonObject.getBoolean("status")) {
                                finish();
                            } else {
                                Toast.makeText(UpdateActivity.this, response, Toast.LENGTH_SHORT).show();
                            }
                        } catch (JSONException e) {
                            e.printStackTrace();
                        }
                    }
                }, new Response.ErrorListener() {
            @Override
            public void onErrorResponse (VolleyError error) {
                Log.d(TAG, "Update: " + error);
            }
        }) {
            @Override
            protected Map< String, String > getParams () throws AuthFailureError {
                Map< String, String > params = new HashMap<>();
                params.put("mid", String.valueOf(movieId));
                params.put("image_old", current_image_url);
                return params;
            }
        };
        MySingleton.getInstance(this).addToRequestQueue(stringRequest);
    }

    public String imageToString (Bitmap bitmap) {
        ByteArrayOutputStream outputStream = new ByteArrayOutputStream();
        bitmap.compress(Bitmap.CompressFormat.JPEG, 100, outputStream);
        byte[] imageBytes = outputStream.toByteArray();
        String encodedImage = Base64.encodeToString(imageBytes, Base64.DEFAULT);
        return encodedImage;
    }

    public void getCategory () {
        String url = Constants.URL_CATEGORY;
        StringRequest stringRequest = new StringRequest(Request.Method.GET, url,
                new Response.Listener< String >() {
                    @Override
                    public void onResponse (String response) {
                        try {
                            JSONArray jsonArray = new JSONArray(response);
                            for (int i = 0; i < jsonArray.length(); i++) {
                                JSONObject jsonObject = jsonArray.getJSONObject(i);
                                catId.add(jsonObject.getInt("cid"));
                                catName.add(jsonObject.getString("cname"));
                            }
                            adapter.notifyDataSetChanged();
                        } catch (JSONException e) {
                            e.printStackTrace();
                        }
                    }
                }, new Response.ErrorListener() {
            @Override
            public void onErrorResponse (VolleyError error) {
                Log.e(TAG, "onErrorResponse: ", error);
            }
        });
        MySingleton.getInstance(this).addToRequestQueue(stringRequest);
    }

    public void getData (int mid) {
        String url = Constants.URL_READ_DATA + mid;
        StringRequest stringRequest = new StringRequest(Request.Method.GET, url,
                new Response.Listener< String >() {
                    @Override
                    public void onResponse (String response) {
                        try {
                            JSONArray jsonArray = new JSONArray(response);
                            JSONObject jsonObject = jsonArray.getJSONObject(0);
                            current_image_url = jsonObject.getString("images");
                            Picasso.get()
                                    .load(Constants.URL_IMAGES + current_image_url)
                                    .into(imageUrl);
                            editTitle.setText(jsonObject.getString("mtitle"));
                            editDesc.setText(jsonObject.getString("mdesc"));
                            editDir.setText(jsonObject.getString("directors"));
                            editYear.setText(jsonObject.getString("release_year"));
                            editDur.setText(jsonObject.getString("duration"));
                            int pos = adapter.getPosition(jsonObject.getString("cname"));
                            spinner.setSelection(pos);
                        } catch (JSONException e) {
                            e.printStackTrace();
                        }
                    }
                }, new Response.ErrorListener() {
            @Override
            public void onErrorResponse (VolleyError error) {
                Log.e(TAG, "onErrorResponse: ", error);
            }
        });
        MySingleton.getInstance(this).addToRequestQueue(stringRequest);
    }

    @Override
    public void onClick (View v) {
        switch (v.getId()) {
            case R.id.btnImage:
                movieImage();
                break;
            case R.id.btnUpdate:
                movieUpdate();
                break;
            case R.id.btnDelete:
                movieDelete();
                break;
            default:
                Toast.makeText(this, "Button Not Found", Toast.LENGTH_SHORT).show();
                break;
        }
    }
}