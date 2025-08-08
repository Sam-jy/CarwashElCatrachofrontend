package com.example.carwashelcatracho;

import android.content.Intent;
import android.content.pm.PackageManager;
import android.net.Uri;
import android.os.Bundle;
import android.provider.MediaStore;
import android.util.Base64;
import android.view.View;
import android.widget.Button;
import android.widget.EditText;
import android.widget.Toast;
import android.widget.VideoView;

import androidx.activity.EdgeToEdge;
import androidx.annotation.NonNull;
import androidx.annotation.Nullable;
import androidx.appcompat.app.AppCompatActivity;
import androidx.core.app.ActivityCompat;
import androidx.core.content.ContextCompat;

import com.android.volley.RequestQueue;
import com.android.volley.VolleyError;
import com.android.volley.toolbox.JsonObjectRequest;
import com.android.volley.toolbox.Volley;
import com.example.carwashelcatracho.Config.Personas;
import com.example.carwashelcatracho.Config.RestApiMethods;

import org.json.JSONObject;

import java.io.File;
import java.io.IOException;
import java.util.Calendar;
import java.io.FileInputStream;

import okhttp3.Call;
import okhttp3.Callback;
import okhttp3.MediaType;
import okhttp3.MultipartBody;
import okhttp3.OkHttpClient;
import okhttp3.RequestBody;
import okhttp3.Response;

public class CreateActivity extends AppCompatActivity {

    static final int REQUEST_VIDEO = 102;
    static final int ACCESS_CAMERA =  201;
    VideoView videoView;
    Button btnRecordVideo, btncreate;
    String currentVideoPath;
    EditText nombre, telefono, latitud, longitud;
    private RequestQueue requestQueue;
    Calendar calendario = Calendar.getInstance();

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        EdgeToEdge.enable(this);
        setContentView(R.layout.activity_create);

        videoView = (VideoView) findViewById(R.id.videoView);
        btnRecordVideo = (Button) findViewById(R.id.btnRecordVideo);
        btncreate = (Button) findViewById(R.id.btncreate);

        nombre = (EditText) findViewById(R.id.nombre);
        telefono = (EditText) findViewById(R.id.telefono);
        latitud = (EditText) findViewById(R.id.latitud);
        longitud = (EditText) findViewById(R.id.longitud);

        btnRecordVideo.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                PermisosCamara();
            }
        });

        btncreate.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                SendData();
            }
        });
    }

    private void SendData() {
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
        if (currentVideoPath == null || currentVideoPath.isEmpty()) {
            Toast.makeText(this, "Debe grabar un video", Toast.LENGTH_SHORT).show();
            return;
        }
        File videoFile = new File(currentVideoPath);
        uploadVideo(videoFile, nombre.getText().toString(), telefono.getText().toString(), latitud.getText().toString(), longitud.getText().toString());
    }

    public void uploadVideo(File videoFile, String nombre, String telefono, String latitud, String longitud) {
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
                runOnUiThread(() -> Toast.makeText(CreateActivity.this, "Error al subir video", Toast.LENGTH_SHORT).show());
            }
            @Override
            public void onResponse(Call call, Response response) throws IOException {
                if (response.isSuccessful()) {
                    String responseBody = response.body().string();
                    try {
                        JSONObject json = new JSONObject(responseBody);
                        if (json.getBoolean("issuccess")) {
                            String videoPath = json.getString("video_path");
                            runOnUiThread(() -> sendDataWithVideoPath(nombre, telefono, latitud, longitud, videoPath));
                        } else {
                            runOnUiThread(() -> {
                                try {
                                    Toast.makeText(CreateActivity.this, "Error: " + json.getString("message"), Toast.LENGTH_SHORT).show();
                                } catch (org.json.JSONException e) {
                                    e.printStackTrace();
                                }
                            });
                        }
                    } catch (org.json.JSONException e) {
                        e.printStackTrace();
                    }
                }
            }
        });
    }

    private void sendDataWithVideoPath(String nombre, String telefono, String latitud, String longitud, String videoPath) {
        requestQueue = Volley.newRequestQueue(this);
        Personas personas = new Personas();
        personas.setNombre(nombre);
        personas.setTelefono(telefono);
        personas.setLatitud(latitud);
        personas.setLongitud(longitud);
        personas.setVideo(videoPath);
        JSONObject jsonObject = new JSONObject();
        try {
            jsonObject.put("nombre", personas.getNombre());
            jsonObject.put("telefono", personas.getTelefono());
            jsonObject.put("latitud", personas.getLatitud());
            jsonObject.put("longitud", personas.getLongitud());
            jsonObject.put("video", personas.getVideo());
            JsonObjectRequest request = new JsonObjectRequest(
                    com.android.volley.Request.Method.POST,
                    RestApiMethods.EndpointCreatePerson,
                    jsonObject,
                    new com.android.volley.Response.Listener<JSONObject>() {
                        @Override
                        public void onResponse(JSONObject response) {
                            try {
                                String mensaje = response.getString("message");
                                Toast.makeText(getApplicationContext(), mensaje, Toast.LENGTH_LONG).show();
                                setResult(RESULT_OK);
                                finish();
                            } catch (Exception ex) {
                                ex.printStackTrace();
                                Toast.makeText(getApplicationContext(), "Error al crear contacto", Toast.LENGTH_LONG).show();
                            }
                        }
                    },
                    new com.android.volley.Response.ErrorListener() {
                        @Override
                        public void onErrorResponse(VolleyError error) {
                            Toast.makeText(getApplicationContext(), "Error: " + error.getMessage(),
                                    Toast.LENGTH_LONG).show();
                        }
                    }
            );
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
            File file = new File(path);
            FileInputStream fis = new FileInputStream(file);
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
        if (ContextCompat.checkSelfPermission(this, android.Manifest.permission.CAMERA) != PackageManager.PERMISSION_GRANTED) {
            ActivityCompat.requestPermissions(this, new String[]{android.Manifest.permission.CAMERA}, ACCESS_CAMERA);
        } else {
            dispatchTakeVideoIntent();
        }
    }

    @Override
    public void onRequestPermissionsResult(int requestCode, @NonNull String[] permissions, @NonNull int[] grantResults) {
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
    protected void onActivityResult(int requestCode, int resultCode, @Nullable Intent data) {
        super.onActivityResult(requestCode, resultCode, data);
        if (requestCode == REQUEST_VIDEO && resultCode == RESULT_OK && data != null) {
            Uri videoUri = data.getData();
            currentVideoPath = FileUtils.getPath(this, videoUri);
            videoView.setVideoURI(videoUri);
            videoView.start();
        }
    }
}