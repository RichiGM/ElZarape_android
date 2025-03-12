package com.baite.elzarape.view.components;

import android.content.Context;
import android.content.SharedPreferences;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.util.Log;
import android.widget.Toast;

import androidx.annotation.NonNull;
import androidx.appcompat.app.AppCompatActivity;
import androidx.core.content.ContextCompat;
import androidx.recyclerview.widget.RecyclerView;

import com.android.volley.Request;
import com.android.volley.VolleyError;
import com.android.volley.toolbox.JsonObjectRequest;
import com.android.volley.toolbox.Volley;
import com.baite.elzarape.R;
import com.baite.elzarape.commons.ZarapeCommons;
import com.baite.elzarape.view.EditAlimentoBottomSheet;

import org.utl.dsm.zarape.model.Alimento;

import java.util.HashMap;
import java.util.List;
import java.util.Map;

public class AdapterAlimento extends RecyclerView.Adapter<ViewHolderAlimento> {
    private List<Alimento> alimentos;

    public AdapterAlimento(List<Alimento> a) {
        alimentos = a;
    }

    @NonNull
    @Override
    public ViewHolderAlimento onCreateViewHolder(@NonNull ViewGroup parent, int viewType) {
        View view = LayoutInflater.from(parent.getContext()).inflate(R.layout.row_alimentos, parent, false);
        return new ViewHolderAlimento(view);
    }

    @Override
    public void onBindViewHolder(@NonNull ViewHolderAlimento holder, int position) {
        String b64 = null;
        Alimento a = alimentos.get(position);
        holder.tvNombreAlimento.setText(a.getProducto().getNombre());
        holder.tvCategoria.setText(a.getProducto().getCategoria().getDescripcion());
        holder.tvPrecio.setText(String.valueOf(a.getProducto().getPrecio()));

        if (a.getProducto().getFoto() != null && a.getProducto().getFoto().trim().length() > 64) {
            b64 = a.getProducto().getFoto();
            b64 = b64.substring(b64.indexOf(",") + 1);
            holder.imvFotoAlimento.setImageBitmap(ZarapeCommons.procesarImagen(b64));
        } else {
            holder.imvFotoAlimento.setImageResource(R.drawable.ic_launcher_background);
        }

        if (a.getProducto().getActivo() == 0) {
            holder.rowAlimento.setBackgroundColor(ContextCompat.getColor(holder.itemView.getContext(), R.color.inactivo));
            holder.btnCambiarEstatus.setImageResource(R.drawable.ic_activar);
        }

        // Configurar el botón de cambiar estatus
        holder.btnCambiarEstatus.setOnClickListener(v -> {
            cambiarEstatusAlimento(a.getProducto().getIdProducto(), holder.itemView.getContext(), position);
        });

        // Configurar el botón de editar
        holder.btnEditar.setOnClickListener(v -> {
            EditAlimentoBottomSheet editBottomSheet = new EditAlimentoBottomSheet(a);
            editBottomSheet.show(((AppCompatActivity) holder.itemView.getContext()).getSupportFragmentManager(), "EditAlimentoBottomSheet");
        });
    }

    @Override
    public int getItemCount() {
        return alimentos == null ? 0 : alimentos.size();
    }

    private void cambiarEstatusAlimento(int idProducto, Context context, int position) {
        String url = ZarapeCommons.SERVER_URL + "api/alimento/cambiarEstatus/" + idProducto;
        SharedPreferences sharedPreferences = context.getSharedPreferences("MyAppPrefs", Context.MODE_PRIVATE);
        String token = sharedPreferences.getString("lastToken", null);

        JsonObjectRequest jsonObjectRequest = new JsonObjectRequest(Request.Method.POST, url, null,
                response -> {
                    Log.d("API Response", response.toString());
                    Toast.makeText(context, "Estatus cambiado correctamente", Toast.LENGTH_SHORT).show();
                    notifyItemChanged(position);
                },
                error -> {
                    Log.e("API Error", error.toString());
                    Toast.makeText(context, "Error al cambiar el estatus: " + error.getMessage(), Toast.LENGTH_SHORT).show();
                }) {
            @Override
            public Map<String, String> getHeaders() {
                Map<String, String> headers = new HashMap<>();
                headers.put("Content-Type", "application/json");
                headers.put("Authorization", "Bearer " + token);
                return headers;
            }
        };

        Volley.newRequestQueue(context).add(jsonObjectRequest);
    }
}