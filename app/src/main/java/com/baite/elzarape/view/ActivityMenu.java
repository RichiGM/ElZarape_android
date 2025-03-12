package com.baite.elzarape.view;

import android.content.Intent;
import android.os.Bundle;
import android.widget.LinearLayout;

import androidx.appcompat.app.AppCompatActivity;

import com.baite.elzarape.R;

public class ActivityMenu extends BaseActivity {
    LinearLayout llAlimento;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setupDrawer(R.layout.activity_menu);

        inicializarComponentes();
    }

    private void inicializarComponentes() {
        llAlimento = findViewById(R.id.llAlimento);

        llAlimento.setOnClickListener(v -> {
            Intent intent = new Intent(ActivityMenu.this, ActivityAlimentos.class);
            startActivity(intent);
        });
    }
}
