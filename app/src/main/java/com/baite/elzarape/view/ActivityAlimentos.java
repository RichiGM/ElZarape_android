package com.baite.elzarape.view;


import static com.baite.elzarape.commons.ZarapeCommons.SERVER_URL;

import android.os.Bundle;

import androidx.activity.EdgeToEdge;
import androidx.appcompat.app.AppCompatActivity;
import androidx.core.graphics.Insets;
import androidx.core.view.ViewCompat;
import androidx.core.view.WindowInsetsCompat;
import androidx.recyclerview.widget.LinearLayoutManager;
import androidx.recyclerview.widget.RecyclerView;

import com.android.volley.Request;
import com.android.volley.Response;
import com.android.volley.VolleyError;
import com.android.volley.toolbox.JsonObjectRequest;
import com.android.volley.toolbox.StringRequest;
import com.android.volley.toolbox.Volley;
import com.baite.elzarape.R;
import com.baite.elzarape.view.components.AdapterAlimento;
import com.google.android.material.snackbar.Snackbar;
import com.google.gson.Gson;
import com.google.gson.reflect.TypeToken;

import org.utl.dsm.zarape.model.Alimento;

import java.util.ArrayList;
import java.util.HashMap;
import java.util.List;
import java.util.Map;

public class ActivityAlimentos extends AppCompatActivity {
    RecyclerView rclvAlimentos;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        EdgeToEdge.enable(this);
        setContentView(R.layout.activity_alimentos);
        ViewCompat.setOnApplyWindowInsetsListener(findViewById(R.id.main), (v, insets) -> {
            Insets systemBars = insets.getInsets(WindowInsetsCompat.Type.systemBars());
            v.setPadding(systemBars.left, systemBars.top, systemBars.right, systemBars.bottom);
            return insets;

        });
        inicializarComponentes();
    }

    private void inicializarComponentes() {
        rclvAlimentos = findViewById(R.id.rclvAlimentos);
        rclvAlimentos.setLayoutManager(new LinearLayoutManager(getBaseContext()));
        cargarAlimentos();
    }


    private void cargarAlimentos() {
        // Definimos la URL de la API a consultar
        String url_str = SERVER_URL + "api/alimento/getall";

        // Token de autorización (sin caracteres extraños y con prefijo "Bearer")
        String token = "40b51d2b1402e9c465dbe00b8a96f8ab";

        // Definimos un Response.Listener donde indicamos qué hacer cuando se haya obtenido la respuesta
        Response.Listener<String> rl = response -> {
            List<Alimento> alimentos;
            TypeToken<List<Alimento>> tokenType = new TypeToken<List<Alimento>>() {};

            try {
                // Obtenemos el objeto de la respuesta
                alimentos = new Gson().fromJson(response, tokenType.getType());

                // Creamos el adaptador solo después de obtener los datos
                AdapterAlimento adapter = new AdapterAlimento(alimentos);
                rclvAlimentos.setAdapter(adapter);

                // Mostramos un mensaje de éxito
                Snackbar.make(findViewById(R.id.main).getRootView(), "Catálogo de alimentos recuperado.",
                        Snackbar.LENGTH_SHORT).show();

            } catch (Exception e) {
                // Mostramos un mensaje de error
                e.printStackTrace();
                Snackbar.make(findViewById(R.id.main).getRootView(), "Error al consultar el catálogo de alimentos.",
                        Snackbar.LENGTH_SHORT).show();
            }
        };

        // Definimos un Response.ErrorListener donde indicamos qué hacer cuando se haya producido un error con la API
        Response.ErrorListener re = error -> {
            // Mostramos un mensaje de error
            Snackbar.make(findViewById(R.id.main), "Error al consultar los alimentos: " + error.getMessage(), Snackbar.LENGTH_SHORT).show();
        };

        // Creamos el objeto de petición del servicio con el token en los encabezados
        StringRequest request = new StringRequest(Request.Method.GET, url_str, rl, re) {
            @Override
            public Map<String, String> getHeaders() {
                Map<String, String> headers = new HashMap<>();
                headers.put("Authorization", "Bearer " + token); // 🔥 Agregamos el prefijo "Bearer"
                return headers;
            }
        };

        // Mostrar una alerta al usuario
        Snackbar.make(findViewById(R.id.main), "Consultando alimentos", Snackbar.LENGTH_SHORT).show();

        // Ejecutamos la petición
        Volley.newRequestQueue(getBaseContext()).add(request);
    }

}