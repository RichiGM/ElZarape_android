package com.baite.elzarape.view;

import android.content.Intent;
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
                        // Redirigir a otra actividad
                        Intent intent = new Intent(ActivityLogin.this, ActivityAlimentos.class);
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
}