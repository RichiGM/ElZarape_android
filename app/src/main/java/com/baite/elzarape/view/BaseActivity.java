package com.baite.elzarape.view;

import android.content.Intent;
import android.os.Bundle;
import android.view.View;
import android.widget.Button;
import android.widget.ImageButton;
import androidx.appcompat.app.AppCompatActivity;
import androidx.core.view.GravityCompat;
import androidx.drawerlayout.widget.DrawerLayout;
import com.baite.elzarape.R;

public class BaseActivity extends AppCompatActivity {

    protected DrawerLayout drawerLayout;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
    }

    protected void setupDrawer(int layoutId) {
        setContentView(layoutId); // Establece el layout específico de la actividad

        // Inicializa el DrawerLayout
        drawerLayout = findViewById(R.id.drawer_layout);
        ImageButton logoButton = findViewById(R.id.logo); // Asegúrate de que este ID exista en tu layout
        ImageButton menuButton = findViewById(R.id.menu_button); // Asegúrate de que este ID exista en tu layout
        ImageButton logoDrawerButton = findViewById(R.id.logo_drawer);
        Button logoutButton = findViewById(R.id.nav_logout);

        // Configura el clic en el logo
        logoButton.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                Intent intent = new Intent(BaseActivity.this, ActivityMenu.class); // Cambia "ActivityMenu" por la actividad que desees
                startActivity(intent);
            }
        });
        logoDrawerButton.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                Intent intent = new Intent(BaseActivity.this, ActivityMenu.class); // Cambia "ActivityMenu" por la actividad que desees
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
                // Lógica para cerrar sesión
            }
        });
    }
}