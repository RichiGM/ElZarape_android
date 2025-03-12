package com.baite.elzarape.view.components;

import android.view.View;
import android.widget.ImageButton;
import android.widget.ImageView;
import android.widget.LinearLayout;
import android.widget.TextView;

import androidx.annotation.NonNull;
import androidx.recyclerview.widget.RecyclerView;

import com.baite.elzarape.R;

public class ViewHolderAlimento extends RecyclerView.ViewHolder {
    ImageView imvFotoAlimento;
    TextView tvNombreAlimento;
    TextView tvCategoria;
    TextView tvPrecio;
    LinearLayout rowAlimento;
    ImageButton btnCambiarEstatus, btnEditar;
    public ViewHolderAlimento(@NonNull View itemView) {
        super(itemView);
        imvFotoAlimento = itemView.findViewById(R.id.imvFotoAlimento);
        tvNombreAlimento = itemView.findViewById(R.id.tvNombreAlimento);
        tvCategoria = itemView.findViewById(R.id.tvCategoria);
        tvPrecio = itemView.findViewById(R.id.tvPrecio);
        rowAlimento = itemView.findViewById(R.id.rowAlimento);
        btnCambiarEstatus = itemView.findViewById(R.id.btnCambiarEstatus);
        btnEditar = itemView.findViewById(R.id.btnEditar);
    }
}
