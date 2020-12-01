package com.example.anotaai.view;

import android.view.View;
import android.widget.TextView;
import androidx.recyclerview.widget.RecyclerView;
import com.example.anotaai.R;

public class DespesasViewHolder extends RecyclerView.ViewHolder {

    public TextView gastos, data;
    public DespesasViewHolder(View itemView) {
        super(itemView);
        gastos = itemView.findViewById(R.id.textAdapterGastos);
        data = itemView.findViewById(R.id.textAdapterMes);
    }
}
