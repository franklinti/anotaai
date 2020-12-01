package com.example.anotaai.view;

import android.view.View;
import android.widget.TextView;

import androidx.annotation.NonNull;
import androidx.recyclerview.widget.RecyclerView;

import com.example.anotaai.R;

public class HistoricoViewHolder extends RecyclerView.ViewHolder {
    public TextView titulo,valor,categoria;

    public HistoricoViewHolder(@NonNull View itemView) {
        super(itemView);
        titulo = itemView.findViewById(R.id.textadaptertitulo);
        valor = itemView.findViewById(R.id.textadaptervalor);
        categoria = itemView.findViewById(R.id.textadaptercategoria);
    }
}
