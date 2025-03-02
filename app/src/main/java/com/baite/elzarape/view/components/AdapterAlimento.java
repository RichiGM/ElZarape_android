package com.baite.elzarape.view.components;

import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;

import androidx.annotation.NonNull;
import androidx.recyclerview.widget.RecyclerView;

import com.baite.elzarape.R;

import org.utl.dsm.zarape.model.Alimento;

import java.util.List;

public class AdapterAlimento extends RecyclerView.Adapter<ViewHolderAlimento>{
    List<Alimento> alimentos;

    public AdapterAlimento(List<Alimento> a) {
        alimentos = a;
    }

    @NonNull
    @Override
    public ViewHolderAlimento onCreateViewHolder(@NonNull ViewGroup parent, int viewType) {
        View view = LayoutInflater.from(parent.getContext()).inflate(R.layout.row_alimentos, parent, false);
        ViewHolderAlimento vha = new ViewHolderAlimento(view);
        return vha;
    }

    @Override
    public void onBindViewHolder(@NonNull ViewHolderAlimento holder, int position) {
        Alimento a = alimentos.get(position);
        holder.tvNombreAlimento.setText(a.getProducto().getNombre());
        holder.tvCategoria.setText(a.getProducto().getCategoria().getDescripcion());
        holder.tvPrecio.setText(String.valueOf(a.getProducto().getPrecio()));
    }

    @Override
    public int getItemCount() {
        return alimentos == null ? 0 : alimentos.size();
    }
}
