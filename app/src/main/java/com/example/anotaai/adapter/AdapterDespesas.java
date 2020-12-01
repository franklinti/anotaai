package com.example.anotaai.adapter;

import android.content.Context;
import android.util.Log;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;

import androidx.annotation.NonNull;
import androidx.recyclerview.widget.RecyclerView;

import com.example.anotaai.PrincipalActivity;
import com.example.anotaai.R;
import com.example.anotaai.helper.DateCustom;
import com.example.anotaai.model.Conta;
import com.example.anotaai.view.DespesasViewHolder;
import com.google.firebase.database.ChildEventListener;
import com.google.firebase.database.DatabaseReference;

import java.text.DecimalFormat;
import java.util.ArrayList;
import java.util.Collections;
import java.util.List;
import java.util.Map;

public class AdapterDespesas extends RecyclerView.Adapter<DespesasViewHolder> {

    private Context context;
    private List<Conta> despesas;

    public AdapterDespesas(List<Conta> despesasMes, Context context1) {
        this.despesas = despesasMes;
        this.context = context1;
    }

    @Override
    public DespesasViewHolder onCreateViewHolder(ViewGroup parent, int viewType) {
        LayoutInflater inflater = LayoutInflater.from(context);
        View view = inflater.inflate(R.layout.activity_adapter_despesas_por_mes, parent, false);
        return new DespesasViewHolder(view);
    }

    @Override
    public void onBindViewHolder(@NonNull DespesasViewHolder holder, int position) {
        Conta contas = despesas.get(position);
        DecimalFormat decimalFormat1 = new DecimalFormat("#.##");
        String format = decimalFormat1.format(contas.getValor());
        Log.w("","ADC"+format);
        holder.gastos.setText(format);
        String idMesAno = contas.getKeyMes();
       // Log.w("",""+idMesAno);

        //converte data numerica do banco em texto
        String dataEmTexto = DateCustom.convetDataTexto(idMesAno);

        holder.data.setText(dataEmTexto);
    }

    @Override
    public int getItemCount() {
        return despesas.size();
    }

}
