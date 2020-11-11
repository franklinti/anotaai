package com.example.anotaai;

import androidx.annotation.NonNull;
import androidx.appcompat.app.AlertDialog;
import androidx.appcompat.app.AppCompatActivity;
import androidx.appcompat.widget.Toolbar;
import androidx.recyclerview.widget.ItemTouchHelper;
import androidx.recyclerview.widget.LinearLayoutManager;
import androidx.recyclerview.widget.RecyclerView;

import android.content.DialogInterface;
import android.content.Intent;
import android.content.res.Configuration;
import android.os.Bundle;
import android.util.Log;
import android.view.Menu;
import android.view.MenuItem;
import android.view.View;
import android.widget.ProgressBar;
import android.widget.TextView;

import com.example.anotaai.activity.DespesaActivity;
import com.example.anotaai.adapter.AdapterRecyclerHistorico;
import com.example.anotaai.config.ConfiguracaoFirebase;
import com.example.anotaai.dao.ContaDAO;
import com.example.anotaai.helper.Mensagem;
import com.example.anotaai.model.Conta;
import com.example.anotaai.model.Usuario;
import com.github.clans.fab.FloatingActionButton;
import com.google.firebase.auth.FirebaseAuth;
import com.google.firebase.auth.FirebaseUser;
import com.google.firebase.database.DataSnapshot;
import com.google.firebase.database.DatabaseError;
import com.google.firebase.database.DatabaseReference;
import com.google.firebase.database.ValueEventListener;
import com.prolificinteractive.materialcalendarview.CalendarDay;
import com.prolificinteractive.materialcalendarview.MaterialCalendarView;
import com.prolificinteractive.materialcalendarview.OnMonthChangedListener;

import java.text.DecimalFormat;
import java.text.MessageFormat;
import java.util.ArrayList;
import java.util.List;

public class PrincipalActivity extends AppCompatActivity
        implements View.OnClickListener {

    FloatingActionButton fabDespesa, fabReceita;
    private MaterialCalendarView calendarView;
    private TextView textSaldacao, textSaldo, textGastos;
    private double despesaTotal = 0.0;
    private double receitaTotal = 0.0;
    private double saldoConta = 0.0;
    private ValueEventListener valueEventListenerUsuario;
    DatabaseReference dbReferenceValores;
    FirebaseAuth firebaseAuth;
    FirebaseUser idUserAtual;
    String id;
    private RecyclerView recyclerView;
    private AdapterRecyclerHistorico adapterRecyclerHistorico;
    private List<Conta> contas = new ArrayList<>();
    private String mesano;
    Conta conta;
    Usuario usuario;
    ContaDAO contaDAO;

    ProgressBar progressBarSaldo, progressBarGastos;
    double progressSaldo = 0.00;
    double progressGastos = 0.00;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_principal);

        Toolbar toolbar = findViewById(R.id.toolbar);
        toolbar.setTitle("Anota ai");
        setSupportActionBar(toolbar);
        iniciarComponentes();
    }

    public void iniciarComponentes() {

        fabDespesa = findViewById(R.id.menu_despesa);
        fabDespesa.setOnClickListener(this);
        fabReceita = findViewById(R.id.menu_receita);
        fabReceita.setOnClickListener(this);

        calendarView = findViewById(R.id.calendarview);
        configuraCalendarView();

        textSaldacao = findViewById(R.id.activityprincipalsaldacao);
        textSaldo = findViewById(R.id.activityprincipaltextsaldo);
        progressBarSaldo = findViewById(R.id.progressBarSaldo);

        textGastos = findViewById(R.id.activityprincipaltextgastos);
        progressBarGastos = findViewById(R.id.progressBarGastos);


        firebaseAuth = ConfiguracaoFirebase.getFirebaseAutenticacao();
        idUserAtual = firebaseAuth.getCurrentUser();
        id = idUserAtual.getUid();

        adapterRecyclerHistorico = new AdapterRecyclerHistorico(contas, this);

        recyclerView = findViewById(R.id.activityprincipalrecyclerhistorico);
        RecyclerView.LayoutManager layoutManager = new LinearLayoutManager(this);
        recyclerView.setLayoutManager(layoutManager);
        recyclerView.setHasFixedSize(true);
        recyclerView.setAdapter(adapterRecyclerHistorico);

        swipe();
    }

    @Override
    public void onConfigurationChanged(@NonNull Configuration newConfig) {
        super.onConfigurationChanged(newConfig);
        if(newConfig.orientation == Configuration.ORIENTATION_LANDSCAPE){
            Mensagem.mensagem(this,"land");
        }else if(newConfig.orientation == Configuration.ORIENTATION_PORTRAIT){
            Mensagem.mensagem(this,"port");
        }
    }

    @Override
    protected void onStart() {
        super.onStart();
        saldo();
        recuperarValores();
    }

    @Override
    public void onClick(View v) {
        switch (v.getId()) {
            case R.id.menu_despesa:
                addDespesa();
                break;
            case R.id.menu_receita:
                addReceita();
                break;
        }
    }

    public void swipe() {
        ItemTouchHelper.Callback itemTouch = new ItemTouchHelper.Callback() {
            @Override
            public int getMovementFlags(@NonNull RecyclerView recyclerView, @NonNull RecyclerView.ViewHolder viewHolder) {
                int dragFlags = ItemTouchHelper.ACTION_STATE_IDLE;
                int swipeFlags = ItemTouchHelper.START | ItemTouchHelper.END;
                return makeMovementFlags(dragFlags, swipeFlags);
            }

            @Override
            public boolean onMove(@NonNull RecyclerView recyclerView, @NonNull RecyclerView.ViewHolder viewHolder, @NonNull RecyclerView.ViewHolder target) {
                return false;
            }

            @Override
            public void onSwiped(@NonNull RecyclerView.ViewHolder viewHolder, int direction) {
                excluirConta(viewHolder);
            }
        };
        new ItemTouchHelper(itemTouch).attachToRecyclerView(recyclerView);
    }

    private void excluirConta(final RecyclerView.ViewHolder viewHolder) {
        AlertDialog.Builder alertDialog = new AlertDialog.Builder(this);
        alertDialog.setTitle("Exluir valor");
        alertDialog.setMessage("Deseja realmente exluir");
        alertDialog.setCancelable(false);
        alertDialog.setPositiveButton("Confirmar", new DialogInterface.OnClickListener() {
            @Override
            public void onClick(DialogInterface dialog, int which) {
                //pega position do item removido
                int position = viewHolder.getAdapterPosition();
                conta = contas.get(position);
                usuario = new Usuario();
                usuario.setId(id);
                contaDAO = new ContaDAO();
                contaDAO.remover(usuario, conta);
                adapterRecyclerHistorico.notifyItemRemoved(position);
                atualizarSaldo();
            }
        });
        alertDialog.setNegativeButton("Cancelar", new DialogInterface.OnClickListener() {
            @Override
            public void onClick(DialogInterface dialog, int which) {
                Mensagem.mensagem(PrincipalActivity.this, "cancelado");
                adapterRecyclerHistorico.notifyDataSetChanged();
            }
        });
        AlertDialog alert = alertDialog.create();
        alert.show();
    }

    @Override
    public boolean onCreateOptionsMenu(Menu menu) {
        //inflater converte xml em uma view
        getMenuInflater().inflate(R.menu.menuprincipal, menu);
        return super.onCreateOptionsMenu(menu);
    }

    @Override
    public boolean onOptionsItemSelected(@NonNull MenuItem item) {
        switch (item.getItemId()) {
            case R.id.menusair:
                sair();
                break;
        }
        return super.onOptionsItemSelected(item);
    }

    private void sair() {
        FirebaseAuth firebaseAuth = ConfiguracaoFirebase.getFirebaseAutenticacao();
        firebaseAuth.signOut();
        startActivity(new Intent(this, MainActivity.class));
        finishAffinity();
    }

    public void addDespesa() {
        if (receitaTotal == 0) {
            AlertDialog.Builder alertDialog = new AlertDialog.Builder(this);
            alertDialog.setTitle("Saldo negativo");
            alertDialog.setMessage("Vou lhe direcionar para " +
                    "inserir saldo primeiro, só confirmar.");
            alertDialog.setCancelable(false);
            alertDialog.setPositiveButton("Confirmar", new DialogInterface.OnClickListener() {
                @Override
                public void onClick(DialogInterface dialog, int which) {
                    addReceita();
                }
            });
            AlertDialog alert = alertDialog.create();
            alert.show();

        } else {
            startActivity(new Intent(this, DespesaActivity.class));
        }

    }

    public void addReceita() {
        startActivity(new Intent(this, ReceitaActivity.class));
    }

    public void configuraCalendarView() {
        CharSequence meses[] = {"Janeiro ", "Fevereiro", "Março", "Abril", "Maio", "Junho", "Julho", "Agosto", "Setembro", "Outubro", "Novembro", "Dezembro"};
        calendarView.setTitleMonths(meses);
        CalendarDay dataAtual = calendarView.getCurrentDate();
        mesano = (dataAtual.getMonth() + 1) + "" + dataAtual.getYear();
        calendarView.setOnMonthChangedListener(new OnMonthChangedListener() {
            @Override
            public void onMonthChanged(MaterialCalendarView widget, CalendarDay date) {
                mesano = (date.getMonth() + 1) + "" + date.getYear();
                //Log.i("Data","Periodo"+ mesano);
                dbReferenceValores.removeEventListener(valueEventListenerUsuario);
                recuperarValores();
            }
        });
    }

    //recupera receitas - despesas e exibe saldo da tela
    public void saldo() {
        DatabaseReference databaseReference = ConfiguracaoFirebase.getDatabaseReference().child("usuarios").child(id);
        Log.i("onStop", "evento acionado");
        valueEventListenerUsuario = databaseReference.addValueEventListener(new ValueEventListener() {
            @Override
            public void onDataChange(@NonNull DataSnapshot snapshot) {
                Usuario usuario = snapshot.getValue(Usuario.class);
                despesaTotal = usuario.getDespesaTotal();
                DecimalFormat decimalFormat1 = new DecimalFormat("0.##");
                String formatDespesasTotal = decimalFormat1.format(despesaTotal);

                receitaTotal = usuario.getReceitaTotal();
                DecimalFormat decimalFormat2 = new DecimalFormat("0.##");
                String formatReceitaTotal = decimalFormat2.format(receitaTotal);

                saldoConta = receitaTotal - despesaTotal;
                DecimalFormat decimalFormat = new DecimalFormat("0.##");
                String formatResultadoGastos = decimalFormat.format(saldoConta);

                textSaldacao.setText("Olá " + usuario.getNome());
                //textSaldo.setText("R$ " + formatDespesasTotal);
                //carrega dados do saldo
                progressBarSaldo(formatResultadoGastos);
                //carrega dados gastos
                progressBarGastos(formatDespesasTotal);

             //   Log.i("","despesa"+formatDespesasTotal);
             //   Log.i("","receita"+formatReceitaTotal);
             //   Log.i("","saldo"+formatResultadoGastos);
            }
            @Override
            public void onCancelled(@NonNull DatabaseError error) {

            }
        });
    }

    public void verificaSaldo(){
        //calcula 50% do salario
        double metadeSaldo = (receitaTotal* 50) / 100;
        if(saldoConta < metadeSaldo){
            AlertDialog.Builder alertDialog = new AlertDialog.Builder(this);
            alertDialog.setTitle("Opa!!!");
            alertDialog.setMessage("Cuidado, voce ja gastou muito!");
            alertDialog.setCancelable(false);
            alertDialog.setPositiveButton("Confirmar", new DialogInterface.OnClickListener() {
                @Override
                public void onClick(DialogInterface dialog, int which) {
                return;
                }
            });
            AlertDialog alert = alertDialog.create();
            alert.show();
        }
    }
    public void progressBarSaldo(String saldo) {
        progressBarSaldo.setMax((int) receitaTotal);
        progressBarSaldo.setProgress((int) saldoConta);
        textSaldo.setText("R$" + saldo);
    }
    public void progressBarGastos(String gastos){
        progressBarGastos.setMax((int) receitaTotal);
        progressBarGastos.setProgress((int) despesaTotal);
        textGastos.setText("R$" + gastos);
    }
    public void atualizarSaldo() {
        if (conta.getTipo().equals("d")) {
            despesaTotal -= conta.getValor();
            atualizaDespesaTotal(despesaTotal);
        }
        if (conta.getTipo().equals("r")) {
            receitaTotal -= conta.getValor();
            atualizaReceitaTotal(receitaTotal);
        }
    }

    public void atualizaDespesaTotal(double despesaAtualizada) {
        DatabaseReference dbrad = ConfiguracaoFirebase.getDatabaseReference()
                .child("usuarios").child(id);
        dbrad.child("despesaTotal").setValue(despesaAtualizada);

    }

    private void atualizaReceitaTotal(double receitaAtualizada) {
        DatabaseReference dbrar = ConfiguracaoFirebase.getDatabaseReference()
                .child("usuarios").child(id);
        dbrar.child("receitaTotal").setValue(receitaAtualizada);
    }

    public void recuperarValores() {
        dbReferenceValores = ConfiguracaoFirebase.getDatabaseReference()
                .child("contas")
                .child(id)
                .child(mesano);
        dbReferenceValores.addValueEventListener(new ValueEventListener() {
            @Override
            public void onDataChange(@NonNull DataSnapshot snapshot) {
                contas.clear();
                for (DataSnapshot dados : snapshot.getChildren()) {
                    Conta conta = dados.getValue(Conta.class);
                    contas.add(conta);
                }
                adapterRecyclerHistorico.notifyDataSetChanged();
            }

            @Override
            public void onCancelled(@NonNull DatabaseError error) {
                error.getMessage();
            }
        });
    }

    @Override
    protected void onStop() {
        super.onStop();
        Log.i("onStop", "evento removido");
        DatabaseReference databaseReference = ConfiguracaoFirebase.getDatabaseReference();
        databaseReference.removeEventListener(valueEventListenerUsuario);

    }
}