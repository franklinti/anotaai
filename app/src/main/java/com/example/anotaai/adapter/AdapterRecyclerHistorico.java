package com.example.anotaai.adapter;

import android.content.Context;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.TextView;

import androidx.annotation.NonNull;
import androidx.recyclerview.widget.RecyclerView;

import com.example.anotaai.R;
import com.example.anotaai.model.Conta;

import java.util.List;

public class AdapterRecyclerHistorico extends RecyclerView.Adapter<AdapterRecyclerHistorico.MyViewHolder> {

    List<Conta> contas;
    Context context;

    public AdapterRecyclerHistorico(List<Conta> contas1,Context context1){
        this.contas = contas1;
        this.context = context1;
    }
    @NonNull
    @Override
    public MyViewHolder onCreateViewHolder(@NonNull ViewGroup parent, int viewType) {
       View itemlista = LayoutInflater.from(parent.getContext()).inflate(R.layout.activity_adapter_historico ,parent,false);
        return new MyViewHolder(itemlista);
    }

    @Override
    public void onBindViewHolder(@NonNull MyViewHolder holder, int position) {
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


    public class MyViewHolder extends RecyclerView.ViewHolder {
       TextView titulo,valor,categoria;
        public MyViewHolder(@NonNull View itemView) {
            super(itemView);
            titulo = itemView.findViewById(R.id.textadaptertitulo);
            valor = itemView.findViewById(R.id.textadaptervalor);
            categoria = itemView.findViewById(R.id.textadaptercategoria);
        }

    }
}
