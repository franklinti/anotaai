package com.example.anotaai.adapter;

import androidx.annotation.NonNull;
import androidx.appcompat.app.AppCompatActivity;
import androidx.recyclerview.widget.RecyclerView;

import android.content.Context;
import android.os.Bundle;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.TextView;

import com.example.anotaai.R;
import com.example.anotaai.model.Conta;
import com.example.anotaai.view.HistoricoViewHolder;

import java.util.List;

public class AdapterHistoricoActivity extends RecyclerView.Adapter<HistoricoViewHolder> {
    List<Conta> contas;
    Context context;

    public AdapterHistoricoActivity(List<Conta> contas1,Context context1){
        this.contas = contas1;
        this.context = context1;
    }
    @NonNull
    @Override
    public HistoricoViewHolder onCreateViewHolder(@NonNull ViewGroup parent, int viewType) {
        View itemlista = LayoutInflater.from(parent.getContext()).inflate(R.layout.activity_adapter_historico ,parent,false);
        return new HistoricoViewHolder(itemlista);
    }

    @Override
    public void onBindViewHolder(@NonNull HistoricoViewHolder holder, int position) {
        Conta conta = contas.get(position);
        holder.titulo.setText(conta.getDescricao());
        holder.valor.setText(String.valueOf(conta.getValor()));
        holder.categoria.setText(conta.getCategoria());
        holder.valor.setTextColor(context.getResources().getColor(R.color.ReceitaPrimary));

        if(conta.getTipo().equals("d")){
            holder.valor.setTextColor(context.getResources().getColor(R.color.DespesaPrimary));
            holder.valor.setText("-" +conta.getValor());
        }

    }

    @Override
    public int getItemCount() {
        return contas.size();
    }


}