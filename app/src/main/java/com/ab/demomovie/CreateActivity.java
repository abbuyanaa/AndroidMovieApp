package com.ab.demomovie;

import androidx.annotation.NonNull;
import androidx.annotation.Nullable;
import androidx.appcompat.app.AlertDialog;
import androidx.appcompat.app.AppCompatActivity;
import androidx.core.app.ActivityCompat;
import androidx.core.content.ContextCompat;

import android.Manifest;
import android.content.DialogInterface;
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
import com.android.volley.toolbox.JsonObjectRequest;
import com.android.volley.toolbox.StringRequest;

import org.json.JSONArray;
import org.json.JSONException;
import org.json.JSONObject;

import java.io.ByteArrayOutputStream;
import java.io.FileNotFoundException;
import java.io.InputStream;
import java.util.ArrayList;
import java.util.HashMap;
import java.util.List;
import java.util.Map;

public class CreateActivity extends AppCompatActivity implements View.OnClickListener {

    private final String TAG = Constants.TAG;
    Button btnChooseImage, btnAdd;
    ImageView imageUrl;
    EditText editTitle, editDesc, editDir, editDur, editYear;
    Spinner spinner;
    ArrayList< Integer > catId = new ArrayList<>();
    ArrayList< String > catName = new ArrayList<>();
    ArrayAdapter adapter;
    final int CODE_GALLERY_REQUEST = 999;
    Bitmap bitmap;
    int getCatId;

    @Override
    protected void onCreate (Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_create);

        // Spinner component-d Adapter -aar damjuulan kinonii torluudiig nemj bn
        imageUrl = findViewById(R.id.imageUrl);
        btnChooseImage = findViewById(R.id.btnChooseImage);
        btnAdd = findViewById(R.id.btnAdd);
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

        // Button Event's
        btnChooseImage.setOnClickListener(this);
        btnAdd.setOnClickListener(this);

        getCategory();
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

    @Override
    public void onClick (View v) {
        switch (v.getId()) {
            case R.id.btnChooseImage:
                btnImage();
                break;
            case R.id.btnAdd:
                btnAdd();
                break;
            default:
                Toast.makeText(this, "Товч олдсонгүй", Toast.LENGTH_SHORT).show();
                break;
        }
    }

    private void btnImage () {
        ActivityCompat.requestPermissions(
                CreateActivity.this,
                new String[]{Manifest.permission.READ_EXTERNAL_STORAGE},
                CODE_GALLERY_REQUEST);
    }

    private void btnAdd () {
        String url = Constants.URL_CREATE;
        StringRequest stringRequest = new StringRequest(Request.Method.POST, url,
                new Response.Listener< String >() {
                    @Override
                    public void onResponse (String response) {
                        Log.d(TAG, "Movie Add: " + response);
                        try {
                            JSONObject jsonObject = new JSONObject(response);
                            if (jsonObject.getBoolean("status")) {
                                finish();
                            } else {
                                Toast.makeText(getApplicationContext(),
                                        "Movie Has Not Been Inserted!", Toast.LENGTH_SHORT).show();
                            }
                        } catch (JSONException e) {
                            e.printStackTrace();
                        }
                    }
                }, new Response.ErrorListener() {
            @Override
            public void onErrorResponse (VolleyError error) {
                Toast.makeText(CreateActivity.this,
                        error.getMessage(), Toast.LENGTH_SHORT).show();
            }
        }) {
            @Override
            protected Map< String, String > getParams () throws AuthFailureError {
                Map< String, String > params = new HashMap<>();
                String imageData = imageToString(bitmap);
                params.put(Constants.mImage, imageData);
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
                        Log.d(TAG, "onResponse: Spinner data");
                        try {
                            JSONArray jsonArray = new JSONArray(response);
                            for (int i = 0; i < jsonArray.length(); i++) {
                                JSONObject jsonObject = jsonArray.getJSONObject(i);
                                int cid = jsonObject.getInt("cid");
                                String cname = jsonObject.getString("cname");
                                catId.add(cid);
                                catName.add(cname);
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
}