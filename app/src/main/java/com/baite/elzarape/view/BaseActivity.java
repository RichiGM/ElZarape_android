package com.baite.elzarape.view;

import android.content.Intent;
import android.content.SharedPreferences;
import android.os.Bundle;
import android.view.View;
import android.widget.Button;
import android.widget.ImageButton;
import android.widget.TextView;
import android.widget.Toast;

import androidx.appcompat.app.AppCompatActivity;
import androidx.core.view.GravityCompat;
import androidx.drawerlayout.widget.DrawerLayout;
import com.baite.elzarape.R;
import com.baite.elzarape.controller.api.ApiClient;
import com.baite.elzarape.controller.api.ApiServiceUsuario;
import com.google.gson.JsonObject;

import retrofit2.Call;
import retrofit2.Callback;
import retrofit2.Response;

public class BaseActivity extends AppCompatActivity {

    protected DrawerLayout drawerLayout;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
    }

    protected void setupDrawer(int layoutId) {
        setContentView(layoutId); // Establece el layout específico de la actividad
        SharedPreferences sharedPreferences = getSharedPreferences("MyAppPrefs", MODE_PRIVATE);

        // Inicializa el DrawerLayout
        drawerLayout = findViewById(R.id.drawer_layout);
        ImageButton logoButton = findViewById(R.id.logo); // Asegúrate de que este ID exista en tu layout
        ImageButton menuButton = findViewById(R.id.menu_button); // Asegúrate de que este ID exista en tu layout
        ImageButton logoDrawerButton = findViewById(R.id.logo_drawer);
        TextView user_name = findViewById(R.id.user_name);
        Button logoutButton = findViewById(R.id.nav_logout);

        user_name.setText(sharedPreferences.getString("username", null));
        // Configura el clic en el logo
        logoButton.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                Intent intent = new Intent(BaseActivity.this, ActivityMenu.class);
                startActivity(intent);
            }
        });

        logoDrawerButton.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                Intent intent = new Intent(BaseActivity.this, ActivityMenu.class);
                startActivity(intent);
            }
        });

        // Configura el clic en el botón de menú
        menuButton.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                if (drawerLayout.isDrawerOpen(GravityCompat.START)) {
                    drawerLayout.closeDrawer(GravityCompat.START);
                } else {
                    drawerLayout.openDrawer(GravityCompat.START);
                }
            }
        });

        logoutButton.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                logout();
            }
        });
    }

    private void logout() {
        // Obtener el nombre de usuario de SharedPreferences
        SharedPreferences sharedPreferences = getSharedPreferences("MyAppPrefs", MODE_PRIVATE);
        String username = sharedPreferences.getString("username", null);

        if (username != null) {
            // Crear el servicio de API
            ApiServiceUsuario apiService = ApiClient.getClient().create(ApiServiceUsuario.class);
            Call<JsonObject> call = apiService.logout(username);

            call.enqueue(new Callback<JsonObject>() {
                @Override
                public void onResponse(Call<JsonObject> call, Response<JsonObject> response) {
                    if (response.isSuccessful() && response.body() != null) {
                        JsonObject logoutResponse = response.body();
                        // Aquí puedes manejar la respuesta del servidor
                        if (logoutResponse.has("result") && logoutResponse.get("result").getAsString().contains("Logout exitoso")) {
                            showToast("Cierre de sesión exitoso.");
                        } else {
                            showToast("Error al cerrar sesión.");
                        }
                    } else {
                        showToast("Error en la respuesta del servidor.");
                    }
                }

                @Override
                public void onFailure(Call<JsonObject> call, Throwable t) {
                    showToast("Error de conexión: " + t.getMessage());
                }
            });

            // Borrar los datos de SharedPreferences
            SharedPreferences.Editor editor = sharedPreferences.edit();
            editor.remove("username");
            editor.remove("lastToken");
            editor.apply(); // Aplicar los cambios

            // Redirigir al login
            Intent intent = new Intent(this, ActivityLogin.class); // Cambia a tu actividad de login
            startActivity(intent);
            finish();
        } else {
            showToast("No hay usuario conectado.");
        }
    }

    private void showToast(String message) {
        Toast.makeText(this, message, Toast.LENGTH_SHORT).show();
    }
}