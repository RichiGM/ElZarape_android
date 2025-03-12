package com.baite.elzarape.view;

import android.content.Intent;
import android.content.SharedPreferences;
import android.os.Bundle;
import android.widget.Button;
import android.widget.EditText;
import android.widget.Toast;

import androidx.activity.EdgeToEdge;
import androidx.appcompat.app.AppCompatActivity;
import androidx.core.graphics.Insets;
import androidx.core.view.ViewCompat;
import androidx.core.view.WindowInsetsCompat;

import com.baite.elzarape.R;
import com.baite.elzarape.controller.api.ApiClient;
import com.baite.elzarape.controller.api.ApiServiceLogin;
import com.baite.elzarape.controller.api.ApiServiceUsuario;
import com.google.gson.JsonObject;

import retrofit2.Call;
import retrofit2.Callback;
import retrofit2.Response;

public class ActivityLogin extends AppCompatActivity {

    EditText txtNombreUsuario;
    EditText txtContraseña;
    Button btnLogin;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        EdgeToEdge.enable(this);
        setContentView(R.layout.activity_login);
        ViewCompat.setOnApplyWindowInsetsListener(findViewById(R.id.main), (v, insets) -> {
            Insets systemBars = insets.getInsets(WindowInsetsCompat.Type.systemBars());
            v.setPadding(systemBars.left, systemBars.top, systemBars.right, systemBars.bottom);
            return insets;
        });

        InicializarComponentes();
    }

    private void InicializarComponentes() {
        txtNombreUsuario = findViewById(R.id.txtNombreUsuario);
        txtContraseña = findViewById(R.id.txtContraseña);
        btnLogin = findViewById(R.id.btnLogin);

        btnLogin.setOnClickListener(v -> {
            login();
        });
    }
    private void login() {
        String username = txtNombreUsuario.getText().toString();
        String password = txtContraseña.getText().toString();

        if (username.isEmpty() || password.isEmpty()) {
            showToast("Por favor, completa todos los campos.");
            return;
        }
        // Crear el JSON manualmente
        JsonObject jsonObject = new JsonObject();
        jsonObject.addProperty("username", username);
        jsonObject.addProperty("password", password);

        // Crear el servicio de API
        ApiServiceLogin apiService = ApiClient.getClient().create(ApiServiceLogin.class);
        Call<JsonObject> call = apiService.validateLogin(jsonObject);

        call.enqueue(new Callback<JsonObject>() {
            @Override
            public void onResponse(Call<JsonObject> call, Response<JsonObject> response) {
                if (response.isSuccessful() && response.body() != null) {
                    JsonObject loginResponse = response.body();
                    boolean success = loginResponse.get("success").getAsBoolean();
                    if (success) {
                        showToast("¡Inicio de sesión exitoso!");
                        checkUser(username);
                        Intent intent = new Intent(ActivityLogin.this, ActivityMenu.class);
                        startActivity(intent);
                    } else {
                        showToast("Credenciales incorrectas.");
                    }
                } else {
                    showToast("Error al validar las credenciales.");
                }
            }

            @Override
            public void onFailure(Call<JsonObject> call, Throwable t) {
                showToast("Error de conexión: " + t.getMessage());
            }
        });
    }
    private void showToast(String message) {
        Toast.makeText(this, message, Toast.LENGTH_SHORT).show();
    }

    private void checkUser (String username) {
        ApiServiceUsuario apiService = ApiClient.getClient().create(ApiServiceUsuario.class);
        Call<JsonObject> call = apiService.checkingUser(username);

        call.enqueue(new Callback<JsonObject>() {
            @Override
            public void onResponse(Call<JsonObject> call, Response<JsonObject> response) {
                if (response.isSuccessful() && response.body() != null) {
                    JsonObject jsonResponse = response.body();
                    if (jsonResponse.has("token")) {
                        String token = jsonResponse.get("token").getAsString();

                        saveUser(username, token);

                        showToast("Token recibido: " + token);
                    } else if (jsonResponse.has("error")) {
                        showToast("Error: " + jsonResponse.get("error").getAsString());
                    }
                } else {
                    showToast("Error al obtener el token.");
                }
            }

            @Override
            public void onFailure(Call<JsonObject> call, Throwable t) {
                showToast("Error de conexión: " + t.getMessage());
            }
        });
    }

    private void saveUser (String username, String token) {
        SharedPreferences sharedPreferences = getSharedPreferences("MyAppPrefs", MODE_PRIVATE);
        SharedPreferences.Editor editor = sharedPreferences.edit();

        editor.putString("username", username);
        editor.putString("lastToken", token);
        editor.apply(); // Aplicar los cambios
    }

}