package com.baite.elzarape.view;


import static com.baite.elzarape.commons.ZarapeCommons.SERVER_URL;

import android.content.SharedPreferences;
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
import com.google.android.material.floatingactionbutton.FloatingActionButton;
import com.google.android.material.snackbar.Snackbar;
import com.google.gson.Gson;
import com.google.gson.reflect.TypeToken;

import org.utl.dsm.zarape.model.Alimento;

import java.util.ArrayList;
import java.util.HashMap;
import java.util.List;
import java.util.Map;

public class ActivityAlimentos extends BaseActivity {
    RecyclerView rclvAlimentos;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        EdgeToEdge.enable(this);
        setupDrawer(R.layout.activity_alimentos);

        ViewCompat.setOnApplyWindowInsetsListener(findViewById(R.id.main), (v, insets) -> {
            Insets systemBars = insets.getInsets(WindowInsetsCompat.Type.systemBars());
            v.setPadding(systemBars.left, systemBars.top, systemBars.right, systemBars.bottom);
            return insets;
        });

        inicializarComponentes();

        FloatingActionButton fabAgregarAlimento = findViewById(R.id.fabAgregarAlimento);
        fabAgregarAlimento.setOnClickListener(v -> {
            AddAlimentoBottomSheet bottomSheet = new AddAlimentoBottomSheet();
            bottomSheet.show(getSupportFragmentManager(), bottomSheet.getTag());
        });
    }

    private void inicializarComponentes() {
        rclvAlimentos = findViewById(R.id.rclvAlimentos);
        rclvAlimentos.setLayoutManager(new LinearLayoutManager(getBaseContext()));
        cargarAlimentos();
    }


    private void cargarAlimentos() {
        String url_str = SERVER_URL + "api/alimento/getall";
        SharedPreferences sharedPreferences = getSharedPreferences("MyAppPrefs", MODE_PRIVATE);

        String token = sharedPreferences.getString("lastToken", null);

        Response.Listener<String> rl = response -> {
            List<Alimento> alimentos;
            TypeToken<List<Alimento>> tokenType = new TypeToken<List<Alimento>>() {};

            try {
                alimentos = new Gson().fromJson(response, tokenType.getType());

                AdapterAlimento adapter = new AdapterAlimento(alimentos);
                rclvAlimentos.setAdapter(adapter);

                Snackbar.make(findViewById(R.id.main).getRootView(), "Catálogo de alimentos recuperado.",
                        Snackbar.LENGTH_SHORT).show();

            } catch (Exception e) {
                e.printStackTrace();
                Snackbar.make(findViewById(R.id.main).getRootView(), "Error al consultar el catálogo de alimentos.",
                        Snackbar.LENGTH_SHORT).show();
            }
        };

        Response.ErrorListener re = error -> {
            Snackbar.make(findViewById(R.id.main), "Error al consultar los alimentos: " + error.getMessage(), Snackbar.LENGTH_SHORT).show();
        };

        StringRequest request = new StringRequest(Request.Method.GET, url_str, rl, re) {
            @Override
            public Map<String, String> getHeaders() {
                Map<String, String> headers = new HashMap<>();
                headers.put("Authorization", "Bearer " + token);
                return headers;
            }
        };

        Snackbar.make(findViewById(R.id.main), "Consultando alimentos", Snackbar.LENGTH_SHORT).show();

        Volley.newRequestQueue(getBaseContext()).add(request);
    }

}