package com.example.carwashelcatracho;

import android.content.Intent;
import android.net.Uri;
import android.provider.MediaStore;
import android.os.Bundle;
import android.util.Base64;
import android.view.View;
import android.widget.Button;
import android.widget.EditText;
import android.widget.Toast;
import android.widget.VideoView;

import androidx.appcompat.app.AlertDialog;
import androidx.appcompat.app.AppCompatActivity;

import com.android.volley.Request;
import com.android.volley.RequestQueue;
import com.android.volley.Response;
import com.android.volley.VolleyError;
import com.android.volley.toolbox.JsonObjectRequest;
import com.android.volley.toolbox.Volley;
import com.example.carwashelcatracho.Config.Personas;
import com.example.carwashelcatracho.Config.RestApiMethods;

import org.json.JSONObject;

import java.util.Calendar;
import androidx.core.content.ContextCompat;
import androidx.core.app.ActivityCompat;
import android.Manifest;
import android.content.pm.PackageManager;

import java.io.File;

import okhttp3.Call;
import okhttp3.Callback;
import okhttp3.MediaType;
import okhttp3.MultipartBody;
import okhttp3.OkHttpClient;
import okhttp3.RequestBody;
import java.io.IOException;

public class DetailActivity extends AppCompatActivity {

    private EditText nombre, telefono, latitud, longitud;
    private VideoView videoView;
    private Button btnUpdate, btnDelete, btnBack, btnRecordVideo, btnGoToLocation;
    private String currentVideoPath;
    private Personas persona;
    private RequestQueue requestQueue;
    static final int REQUEST_VIDEO = 102;
    static final int ACCESS_CAMERA =  201;
    private Calendar calendario = Calendar.getInstance();

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_detail);

        initViews();
        loadPersonData();
        setupListeners();
    }

    private void initViews() {
        nombre = findViewById(R.id.nombre);
        telefono = findViewById(R.id.telefono);
        latitud = findViewById(R.id.latitud);
        longitud = findViewById(R.id.longitud);
        videoView = findViewById(R.id.videoView);
        btnRecordVideo = findViewById(R.id.btnRecordVideo);
        btnUpdate = findViewById(R.id.btnUpdate);
        btnDelete = findViewById(R.id.btnDelete);
        btnBack = findViewById(R.id.btnBack);
        btnGoToLocation = findViewById(R.id.btnGoToLocation);
        requestQueue = Volley.newRequestQueue(this);
    }

    private void loadPersonData() {
        Intent intent = getIntent();
        if (intent != null && intent.hasExtra("persona")) {
            persona = (Personas) intent.getSerializableExtra("persona");
            if (persona != null) {
                nombre.setText(persona.getNombre());
                telefono.setText(persona.getTelefono());
                latitud.setText(persona.getLatitud());
                longitud.setText(persona.getLongitud());
                if (persona.getVideo() != null && !persona.getVideo().isEmpty()) {
                    String videoUrl = "http://192.168.100.7/crud-php-person-examen/" + persona.getVideo();
                    videoView.setVideoPath(videoUrl);
                    videoView.seekTo(1);
                } else {
                    videoView.setVideoURI(null);
                }
            }
        }
    }

    private void setupListeners() {
        btnRecordVideo.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                PermisosCamara();
            }
        });
        btnUpdate.setOnClickListener(v -> updatePerson());
        btnDelete.setOnClickListener(v -> showDeleteConfirmation());
        btnBack.setOnClickListener(v -> finish());
        btnGoToLocation.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                goToLocation();
            }
        });
    }

    private void updatePerson() {
        if (persona == null) return;
        if (nombre.getText().toString().trim().isEmpty()) {
            Toast.makeText(this, "El nombre es obligatorio", Toast.LENGTH_SHORT).show();
            return;
        }
        if (telefono.getText().toString().trim().isEmpty()) {
            Toast.makeText(this, "El teléfono es obligatorio", Toast.LENGTH_SHORT).show();
            return;
        }
        if (latitud.getText().toString().trim().isEmpty()) {
            Toast.makeText(this, "La latitud es obligatoria", Toast.LENGTH_SHORT).show();
            return;
        }
        if (longitud.getText().toString().trim().isEmpty()) {
            Toast.makeText(this, "La longitud es obligatoria", Toast.LENGTH_SHORT).show();
            return;
        }
        if (currentVideoPath != null && !currentVideoPath.isEmpty()) {
            File videoFile = new File(currentVideoPath);
            uploadVideoForUpdate(videoFile, persona.getId(), nombre.getText().toString(), telefono.getText().toString(), latitud.getText().toString(), longitud.getText().toString());
        } else {
            sendUpdateWithVideoPath(persona.getId(), nombre.getText().toString(), telefono.getText().toString(), latitud.getText().toString(), longitud.getText().toString(), persona.getVideo());
        }
    }

    public void uploadVideoForUpdate(File videoFile, String id, String nombre, String telefono, String latitud, String longitud) {
        OkHttpClient client = new OkHttpClient();
        RequestBody fileBody = RequestBody.create(videoFile, MediaType.parse("video/mp4"));
        MultipartBody requestBody = new MultipartBody.Builder()
                .setType(MultipartBody.FORM)
                .addFormDataPart("video", videoFile.getName(), fileBody)
                .build();
        okhttp3.Request request = new okhttp3.Request.Builder()
                .url("http://192.168.100.7/crud-php-person-examen/UploadVideo.php")
                .post(requestBody)
                .build();
        client.newCall(request).enqueue(new Callback() {
            @Override
            public void onFailure(Call call, IOException e) {
                runOnUiThread(() -> Toast.makeText(DetailActivity.this, "Error al subir video", Toast.LENGTH_SHORT).show());
            }
            @Override
            public void onResponse(okhttp3.Call call, okhttp3.Response response) throws IOException {
                if (response.isSuccessful()) {
                    String responseBody = response.body().string();
                    try {
                        JSONObject json = new JSONObject(responseBody);
                        if (json.getBoolean("issuccess")) {
                            String videoPath = json.getString("video_path");
                            runOnUiThread(() -> sendUpdateWithVideoPath(id, nombre, telefono, latitud, longitud, videoPath));
                        } else {
                            runOnUiThread(() -> {
                                try {
                                    Toast.makeText(DetailActivity.this, "Error: " + json.getString("message"), Toast.LENGTH_SHORT).show();
                                } catch (org.json.JSONException e) {
                                    e.printStackTrace();
                                }
                            });
                        }
                    } catch (Exception e) {
                        e.printStackTrace();
                    }
                }
            }
        });
    }

    private void sendUpdateWithVideoPath(String id, String nombre, String telefono, String latitud, String longitud, String videoPath) {
        requestQueue = Volley.newRequestQueue(this);
        persona.setNombre(nombre);
        persona.setTelefono(telefono);
        persona.setLatitud(latitud);
        persona.setLongitud(longitud);
        persona.setVideo(videoPath);
        JSONObject jsonObject = new JSONObject();
        try {
            jsonObject.put("id", persona.getId());
            jsonObject.put("nombre", persona.getNombre());
            jsonObject.put("telefono", persona.getTelefono());
            jsonObject.put("latitud", persona.getLatitud());
            jsonObject.put("longitud", persona.getLongitud());
            jsonObject.put("video", persona.getVideo());
            JsonObjectRequest request = new JsonObjectRequest(Request.Method.PUT, RestApiMethods.EndpointUpdatePerson,
                    jsonObject, new Response.Listener<JSONObject>() {
                @Override
                public void onResponse(JSONObject response) {
                    try {
                        String mensaje = response.getString("message");
                        Toast.makeText(getApplicationContext(), mensaje, Toast.LENGTH_LONG).show();
                        setResult(RESULT_OK);
                        finish();
                    } catch (Exception ex) {
                        ex.printStackTrace();
                        Toast.makeText(getApplicationContext(), "Error al actualizar", Toast.LENGTH_LONG).show();
                    }
                }
            }, new Response.ErrorListener() {
                @Override
                public void onErrorResponse(VolleyError error) {
                    Toast.makeText(getApplicationContext(), "Error: " + error.getMessage(),
                            Toast.LENGTH_LONG).show();
                }
            });
            requestQueue.add(request);
        } catch (Exception ex) {
            ex.printStackTrace();
        }
    }

    private String ConvertVideoBase64(String path) {
        if (path == null || path.isEmpty()) {
            return "";
        }
        try {
            java.io.File file = new java.io.File(path);
            java.io.FileInputStream fis = new java.io.FileInputStream(file);
            byte[] bytes = new byte[(int) file.length()];
            fis.read(bytes);
            fis.close();
            return Base64.encodeToString(bytes, Base64.DEFAULT);
        } catch (Exception e) {
            e.printStackTrace();
            return "";
        }
    }

    private void PermisosCamara() {
        if (ContextCompat.checkSelfPermission(this, Manifest.permission.CAMERA) != PackageManager.PERMISSION_GRANTED) {
            ActivityCompat.requestPermissions(this, new String[]{Manifest.permission.CAMERA}, ACCESS_CAMERA);
        } else {
            dispatchTakeVideoIntent();
        }
    }

    @Override
    public void onRequestPermissionsResult(int requestCode, String[] permissions, int[] grantResults) {
        super.onRequestPermissionsResult(requestCode, permissions, grantResults);
        if (requestCode == ACCESS_CAMERA) {
            if (grantResults.length > 0 && grantResults[0] == PackageManager.PERMISSION_GRANTED) {
                dispatchTakeVideoIntent();
            } else {
                Toast.makeText(this, "Permiso de cámara denegado", Toast.LENGTH_SHORT).show();
            }
        }
    }

    private void dispatchTakeVideoIntent() {
        Intent takeVideoIntent = new Intent(MediaStore.ACTION_VIDEO_CAPTURE);
        if (takeVideoIntent.resolveActivity(getPackageManager()) != null) {
            startActivityForResult(takeVideoIntent, REQUEST_VIDEO);
        }
    }

    @Override
    protected void onActivityResult(int requestCode, int resultCode, Intent data) {
        super.onActivityResult(requestCode, resultCode, data);
        if (requestCode == REQUEST_VIDEO && resultCode == RESULT_OK && data != null) {
            Uri videoUri = data.getData();
            currentVideoPath = FileUtils.getPath(this, videoUri);
            videoView.setVideoURI(videoUri);
            videoView.start();
        }
    }

    private void showDeleteConfirmation() {
        new AlertDialog.Builder(this)
                .setTitle(R.string.eliminar)
                .setMessage(R.string.confirmar_eliminar)
                .setPositiveButton(R.string.si, (dialog, which) -> deletePerson())
                .setNegativeButton(R.string.no, null)
                .show();
    }

    private void deletePerson() {
        if (persona == null) return;

        JSONObject jsonObject = new JSONObject();
        try {
            jsonObject.put("id", persona.getId());

            JsonObjectRequest request = new JsonObjectRequest(Request.Method.POST, RestApiMethods.EndpointDeletePerson,
                    jsonObject, new Response.Listener<JSONObject>() {
                @Override
                public void onResponse(JSONObject response) {
                    try {
                        String mensaje = response.getString("message");
                        Toast.makeText(getApplicationContext(), mensaje, Toast.LENGTH_LONG).show();
                        setResult(RESULT_OK);
                        finish();
                    } catch (Exception ex) {
                        ex.printStackTrace();
                        Toast.makeText(getApplicationContext(), "Error al eliminar", Toast.LENGTH_LONG).show();
                    }
                }
            }, new Response.ErrorListener() {
                @Override
                public void onErrorResponse(VolleyError error) {
                    Toast.makeText(getApplicationContext(), "Error: " + error.getMessage(),
                            Toast.LENGTH_LONG).show();
                }
            });

            requestQueue.add(request);
        } catch (Exception ex) {
            ex.printStackTrace();
        }
    }

    private void goToLocation() {
        if (latitud.getText().toString().trim().isEmpty() || longitud.getText().toString().trim().isEmpty()) {
            Toast.makeText(this, "Latitud o longitud no disponible", Toast.LENGTH_SHORT).show();
            return;
        }
        String lat = latitud.getText().toString().trim();
        String lng = longitud.getText().toString().trim();
        String uri = "google.navigation:q=" + lat + "," + lng + "&mode=d";
        Intent intent = new Intent(Intent.ACTION_VIEW, android.net.Uri.parse(uri));
        // No usar setPackage para permitir cualquier app de mapas
        if (intent.resolveActivity(getPackageManager()) != null) {
            startActivity(intent);
        } else {
            Toast.makeText(this, "No hay aplicación de mapas instalada", Toast.LENGTH_SHORT).show();
        }
    }
} 