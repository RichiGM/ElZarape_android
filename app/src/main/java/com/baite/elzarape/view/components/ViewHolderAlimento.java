package com.baite.elzarape.view.components;

import android.view.View;
import android.widget.ImageView;
import android.widget.TextView;

import androidx.annotation.NonNull;
import androidx.recyclerview.widget.RecyclerView;

import com.baite.elzarape.R;

public class ViewHolderAlimento extends RecyclerView.ViewHolder {
    ImageView imvFotoAlimento;
    TextView tvNombreAlimento;
    TextView tvCategoria;
    TextView tvPrecio;
    public ViewHolderAlimento(@NonNull View itemView) {
        super(itemView);
        imvFotoAlimento = itemView.findViewById(R.id.imvFotoAlimento);
        tvNombreAlimento = itemView.findViewById(R.id.tvNombreAlimento);
        tvCategoria = itemView.findViewById(R.id.tvCategoria);
        tvPrecio = itemView.findViewById(R.id.tvPrecio);

    }

}
