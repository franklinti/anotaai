package com.example.anotaai;

import androidx.annotation.NonNull;
import androidx.annotation.Nullable;
import androidx.appcompat.app.AlertDialog;
import androidx.appcompat.app.AppCompatActivity;
import androidx.appcompat.widget.Toolbar;
import androidx.recyclerview.widget.ItemTouchHelper;
import androidx.recyclerview.widget.LinearLayoutManager;
import androidx.recyclerview.widget.RecyclerView;

import android.content.DialogInterface;
import android.content.Intent;
import android.os.Bundle;
import android.util.Log;
import android.view.Menu;
import android.view.MenuItem;
import android.view.View;
import android.widget.ImageButton;
import android.widget.ProgressBar;
import android.widget.TextView;

import com.example.anotaai.activity.DespesaActivity;
import com.example.anotaai.adapter.AdapterDespesas;
import com.example.anotaai.adapter.AdapterHistoricoActivity;
import com.example.anotaai.config.ConfiguracaoFirebase;
import com.example.anotaai.dao.ContaDAO;
import com.example.anotaai.helper.Mensagem;
import com.example.anotaai.model.Conta;
import com.example.anotaai.model.Usuario;
import com.google.firebase.auth.FirebaseAuth;
import com.google.firebase.auth.FirebaseUser;
import com.google.firebase.database.ChildEventListener;
import com.google.firebase.database.DataSnapshot;
import com.google.firebase.database.DatabaseError;
import com.google.firebase.database.DatabaseReference;
import com.google.firebase.database.ValueEventListener;
import com.prolificinteractive.materialcalendarview.CalendarDay;
import com.prolificinteractive.materialcalendarview.MaterialCalendarView;
import com.prolificinteractive.materialcalendarview.OnMonthChangedListener;

import java.text.DecimalFormat;
import java.util.ArrayList;
import java.util.HashMap;
import java.util.List;
import java.util.Map;

public class PrincipalActivity extends AppCompatActivity
        implements View.OnClickListener {
    private static final String TAG = "DetailActivity";
    //imagem button
    ImageButton imgBtnAddSaldo, imgBtnAddGastosSuperfluos, imgBtnAddGastosCartaoCredito;
    //texto
    private TextView textSaldacao, textSaldo, textGastosSuperfluo, textCartaoCredito;
    //progressBar
    ProgressBar progressBarReceita, progressBarGastosSuperfluo, progressBarGastosCartaoCredito;
    //conexao banco
    FirebaseAuth firebaseAuth;
    FirebaseUser idUsuarioAtual;
    DatabaseReference dbReferenceUsuario;
    DatabaseReference dbReferenceReceitas;
    DatabaseReference dbReferenceDespesas, dbReferenceValores;
    String idUsuario;
    private double receitaTotal = 0.0;
    private ChildEventListener childEventListenerUsuario;
    private ValueEventListener valueEventListenerUsuario;

    //despesas
    private double despesaTotal = 0.0;

    //configurando calendar
    private MaterialCalendarView calendarView;
    private String mesano;
    private RecyclerView recyclerView;
    private AdapterHistoricoActivity adapterHistoricoActivity;
    private List<Conta> contas = new ArrayList<>();
    Conta conta;
    Usuario usuario;
    ContaDAO contaDAO;
    private RecyclerView recyclerViewDespesaMes;
    private AdapterDespesas adapterDespesas;
    private List<Conta> despesasMes = new ArrayList<>();


    /*
    FloatingActionButton fabDespesa, fabReceita,fabCartaoCredito;
    FloatingActionMenu fabMenu;
     */

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
        /* Inicio Dados Receita */
        textSaldacao = findViewById(R.id.activityprincipalsaldacao);
        textSaldo = findViewById(R.id.activityprincipaltextsaldo);
        progressBarReceita = findViewById(R.id.progressBarSaldo);

        imgBtnAddSaldo = findViewById(R.id.imgBtnAddSaldo);
        imgBtnAddSaldo.setOnClickListener(this);

        //autenticacao
        firebaseAuth = ConfiguracaoFirebase.getFirebaseAutenticacao();
        idUsuarioAtual = firebaseAuth.getCurrentUser();
        idUsuario = idUsuarioAtual.getUid();
        /* Fim Dados Receita */
        /* Inicio Dados Despesa */
        textGastosSuperfluo = findViewById(R.id.activityprincipaltextgastosurperfluo);
        imgBtnAddGastosSuperfluos = findViewById(R.id.imgBtnAddGastosSuperfluo);
        imgBtnAddGastosSuperfluos.setOnClickListener(this);

        progressBarGastosSuperfluo = findViewById(R.id.progressBarGastosSuperfluo);

        //configurando caledar view
        calendarView = findViewById(R.id.calendario);
        configuraCalendarView();

        //configurando adapter
        adapterHistoricoActivity = new AdapterHistoricoActivity(contas, this);
        //configurando recyclerview gastos
        recyclerView = findViewById(R.id.activityprincipalrecyclerhistorico);
        RecyclerView.LayoutManager layoutManager = new LinearLayoutManager(this,RecyclerView.VERTICAL,false);
        recyclerView.setLayoutManager(layoutManager);
        recyclerView.setHasFixedSize(true);
        recyclerView.setAdapter(adapterHistoricoActivity);
      //  recuperarValoresRecyclerView();

        //cria efeito puxa e arrasta nos itens dentro recyclerview
        swipe();

        //imgBtnAddGastosCartaoCredito = findViewById(R.id.imgBtnAddGastosCartaoCredito);
        // imgBtnAddGastosCartaoCredito.setOnClickListener(this);

        adapterDespesas = new AdapterDespesas(despesasMes, this);
        recyclerViewDespesaMes = findViewById(R.id.activityprincipalrecyclerdespesaspormes);
        RecyclerView.LayoutManager layoutManager1 = new LinearLayoutManager(this, RecyclerView.HORIZONTAL, false);
        recyclerViewDespesaMes.setLayoutManager(layoutManager1);
        recyclerViewDespesaMes.setHasFixedSize(true);
        recyclerViewDespesaMes.setAdapter(adapterDespesas);


        /*
        fabReceita = findViewById(R.id.menu_receita);
        fabReceita.setOnClickListener(this);
        fabMenu = findViewById(R.id.fabmenuprincipal);

        fabDespesa = findViewById(R.id.menu_despesa);
        fabDespesa.setOnClickListener(this);

        fabCartaoCredito = findViewById(R.id.menu_cartaocredito);
        fabCartaoCredito.setOnClickListener(this);

        textGastos = findViewById(R.id.activityprincipaltextgastos);
        progressBarGastos = findViewById(R.id.progressBarGastos);

        textCartaoCredito = findViewById(R.id.activityprincipaltextcartaocredito);
        progressBarCartaoCredito = findViewById(R.id.progressBarCartaoCredito);
        progressBarCartaoCredito.setOnClickListener(this);

         */
    }

    @Override
    protected void onStart() {
        super.onStart();
        nomeUsuario();
        recuperarDespesaTotal();
        recuperarReceitaTotal();
        recuperarDespesaTotalMes();
    }

    @Override
    public void onClick(View v) {
        switch (v.getId()) {
            case R.id.imgBtnAddSaldo:
                addReceita();
                break;
            case R.id.imgBtnAddGastosSuperfluo:
                addDespesa();
                break;
        }
        /*
        switch (v.getId()) {
            case R.id.menu_despesa:
                fabMenu.close(true);
                addDespesa();
                break;
            case R.id.menu_receita:
                fabMenu.close(true);
                addReceita();
                break;
            case R.id.menu_cartaocredito:
                fabMenu.close(true);
                addCartaoCredito();
                break;
            case R.id.progressBarCartaoCredito:
              //  fabMenu.close(true);
                addCartaoCredito();
                break;
        }

         */
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
        firebaseAuth.signOut();
        startActivity(new Intent(this, MainActivity.class));
        finishAffinity();
    }

    public void nomeUsuario() {
        dbReferenceUsuario = ConfiguracaoFirebase.getDatabaseReference()
                .child("usuarios")
                .child(idUsuario);
        valueEventListenerUsuario = dbReferenceUsuario.addValueEventListener(new ValueEventListener() {
            @Override
            public void onDataChange(@NonNull DataSnapshot snapshot) {
                if (snapshot.exists()) {
                    Usuario usuario = snapshot.getValue(Usuario.class);
                    textSaldacao.setText("Olá " + usuario.getNome());
                } else {
                    textSaldacao.setText("Carregando...");
                }
            }

            @Override
            public void onCancelled(@NonNull DatabaseError error) {
                Log.w(TAG, "loadNome:onCancelled", error.toException());
                Mensagem.mensagem(PrincipalActivity.this, "Falha ao carregar nome");
            }
        });
        // dbReferenceUsuario.removeEventListener(valueEventListenerUsuario);
    }

    @Override
    protected void onStop() {
        super.onStop();
        if (valueEventListenerUsuario != null) {
            dbReferenceUsuario.removeEventListener(valueEventListenerUsuario);
        }
        if (childEventListenerUsuario != null) {
            dbReferenceReceitas.removeEventListener(childEventListenerUsuario);
        }
    }

    public void addReceita() {
        startActivity(new Intent(this, ReceitaActivity.class));
    }

    public void addDespesa() {
        if (receitaTotal == 0) {
            AlertDialog.Builder alertDialog = new AlertDialog.Builder(this);
            alertDialog.setTitle("Sem Saldo");
            alertDialog.setMessage(": ) Para iniciar, vamos adicionar o saldo! Clique no confirmar.");
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

    //inicio receita
    public void recuperarReceitaTotal() {
        dbReferenceReceitas = ConfiguracaoFirebase.getDatabaseReference()
                .child("receitas")
                .child(idUsuario);
        ArrayList<Double> receitas = new ArrayList<>();
        dbReferenceReceitas.addChildEventListener(new ChildEventListener() {
            @Override
            public void onChildAdded(@NonNull DataSnapshot snapshot, @Nullable String previousChildName) {
                double valorRecuperado = 0;
                for (DataSnapshot ds : snapshot.getChildren()) {
                    Conta c = ds.getValue(Conta.class);
                    receitas.add(c.getValor());
                }
                for (int i = 0; i < receitas.size(); i++) {
                    valorRecuperado += receitas.get(i);
                }

                receitaTotal = valorRecuperado;
                atualizaReceitaTotal(receitaTotal);
                //  Log.w("", "" + saldo);
            }

            @Override
            public void onChildChanged(@NonNull DataSnapshot snapshot, @Nullable String previousChildName) {
                double valorAdicionado = 0;
                for (DataSnapshot dados : snapshot.getChildren()) {
                    Conta nc = dados.getValue(Conta.class);
                    valorAdicionado = receitaTotal + nc.getValor();
                    //  Log.w("","RTADD: "+valorAdicionado);
                }
                atualizaReceitaTotal(valorAdicionado);
            }

            @Override
            public void onChildRemoved(@NonNull DataSnapshot snapshot) {

            }

            @Override
            public void onChildMoved(@NonNull DataSnapshot snapshot, @Nullable String previousChildName) {

            }

            @Override
            public void onCancelled(@NonNull DatabaseError error) {

            }
        });

    }

    public void recuperarDespesaTotal() {
        dbReferenceDespesas = ConfiguracaoFirebase.getDatabaseReference()
                .child("despesas")
                .child(idUsuario);
        ArrayList<Double> despesas = new ArrayList<>();
        dbReferenceDespesas.addChildEventListener(new ChildEventListener() {
            @Override
            public void onChildAdded(@NonNull DataSnapshot snapshot, @Nullable String previousChildName) {
                double valorRecuperado = 0;

                for (DataSnapshot ds : snapshot.getChildren()) {
                    Conta conta1 = ds.getValue(Conta.class);
                    despesas.add(conta1.getValor());
                }
                for (int i = 0; i < despesas.size(); i++) {
                    valorRecuperado += despesas.get(i);
                }
                despesaTotal = valorRecuperado;
                atualizaDespesaTotal(despesaTotal);
            }

            @Override
            public void onChildChanged(@NonNull DataSnapshot snapshot, @Nullable String previousChildName) {
                double valorAdicionado = 0;
                for (DataSnapshot dados : snapshot.getChildren()) {
                    Conta nc = dados.getValue(Conta.class);
                    valorAdicionado = despesaTotal + nc.getValor();
                    //  Log.w("","RTADD: "+valorAdicionado);
                }
              //  despesaTotal = valorAdicionado;
                atualizaDespesaTotal(valorAdicionado);
            }

            @Override
            public void onChildRemoved(@NonNull DataSnapshot snapshot) {

            }

            @Override
            public void onChildMoved(@NonNull DataSnapshot snapshot, @Nullable String previousChildName) {

            }

            @Override
            public void onCancelled(@NonNull DatabaseError error) {

            }
        });
        // dbReferenceDespesas.removeEventListener(childEventListenerUsuario);
        // DecimalFormat decimalFormat1 = new DecimalFormat("#.##");
        // String formatReceitaTotal1 = decimalFormat1.format(progressReceita);


    }

    /*
    public void recuperarReceitaTotal() {

        dbReferenceReceitas = ConfiguracaoFirebase.getDatabaseReference()
                .child("receitas")
                .child(idUsuario).child(mesano);
        dbReferenceReceitas.addValueEventListener(new ValueEventListener() {
            @Override
            public void onDataChange(@NonNull DataSnapshot snapshot) {
                if (snapshot.exists()) {
                    double valor = 0.0;
                    for (DataSnapshot dado : snapshot.getChildren()) {
                        Conta conta = dado.getValue(Conta.class);
                        valor += conta.getValor();
                       // Log.i("",""+ valor);
                    }
                    receitaTotal = valor;
                    saldoFinal = receitaTotal - despesaTotal;
                   // Log.i("", "" + saldoFinal);
                    atualizaReceitaTotal(saldoFinal);

                    // Log.i("",""+receitaTotal);
                    // despesaTotal = conta.getDespesaTotal();
                    // textGastos(despesaTotal);
                    // textValorSaldo(receitaTotal);
                    // textGastos(despesaTotal);
                    //  progressBarReceita(receitaTotal);
                    // progressBarGastosSuperfluo(despesaTotal);
                    // DecimalFormat decimalFormat1 = new DecimalFormat("#.##");
                    // String formatReceitaTotal1 = decimalFormat1.format(progressReceita);
                } else {
                    atualizaReceitaTotal(0);
                }
            }

            @Override
            public void onCancelled(@NonNull DatabaseError error) {
                error.getMessage();
            }
        });

    }
    */
    public void atualizaReceitaTotal(double receitaAtualizada) {
        DatabaseReference dbrad = ConfiguracaoFirebase.getDatabaseReference()
                .child("saldo").child(idUsuario);
        dbrad.child("receitaTotal").setValue(receitaAtualizada);
        textSaldo(receitaAtualizada);
        progressBarReceita(receitaAtualizada);

    }

    public void atualizaDespesaTotal(double despesaAtualizada) {
        DatabaseReference dbrad = ConfiguracaoFirebase.getDatabaseReference()
                .child("saldo").child(idUsuario);
        dbrad.child("despesaTotal").setValue(despesaAtualizada);
        textGastos(despesaAtualizada);
       // progressBarGastosSuperfluo(despesaAtualizada);
    }

    public void recuperarDespesaTotalMes() {
        dbReferenceDespesas = ConfiguracaoFirebase.getDatabaseReference()
                .child("despesas")
                .child(idUsuario);
        despesasMes.clear();
        dbReferenceDespesas.addChildEventListener(new ChildEventListener() {
            @Override
            public void onChildAdded(@NonNull DataSnapshot snapshot, @Nullable String previousChildName) {
                //key do  recuperado pelo snapshot
                String keyMes = snapshot.getKey();
                //recuperando valores do mes
                dbReferenceDespesas.addValueEventListener(new ValueEventListener() {
                    @Override
                    public void onDataChange(@NonNull DataSnapshot snapshot) {
                        Conta newConta = new Conta();
                        double valorRecuperado = 0;
                        List<Double>despesasPorMes = new ArrayList<>();
                        for(DataSnapshot dado : snapshot.child(keyMes).getChildren()){
                            newConta = dado.getValue(Conta.class);
                            double despesaPorMes = newConta.getValor();
                            despesasPorMes.add(despesaPorMes);
                        }
                        //adicionahdo a key recuperado do snapshot
                        newConta.setKeyMes(keyMes);

                        for (int i = 0; i < despesasPorMes.size(); i++) {
                            valorRecuperado += despesasPorMes.get(i);
                        }
                        despesaTotal = valorRecuperado;
                        newConta.setValor(despesaTotal);

                        despesasMes.add(newConta);
                        adapterDespesas.notifyDataSetChanged();
                    }
                    @Override
                    public void onCancelled(@NonNull DatabaseError error) {
                    }
                });

            }

            @Override
            public void onChildChanged(@NonNull DataSnapshot snapshot, @Nullable String previousChildName) {
              /*
                String keyMes = snapshot.getKey();
                dbReferenceDespesas.addValueEventListener(new ValueEventListener() {
                    @Override
                    public void onDataChange(@NonNull DataSnapshot snapshot) {
                        Conta c2 = new Conta();
                        double valorRecuperado = 0;
                        List<Double>despesasPorMes = new ArrayList<>();
                        for(DataSnapshot dado : snapshot.child(keyMes).getChildren()){
                            c2 = dado.getValue(Conta.class);
                            despesasPorMes.add(c2.getValor());
                        }
                        c2.setKeyMes(keyMes);
                        for (int i = 0; i < despesasPorMes.size(); i++) {
                            valorRecuperado += despesasPorMes.get(i);
                        }
                        despesaTotal = valorRecuperado;
                        c2.setValor(despesaTotal);
                        despesasMes.add(c2);
                        adapterDespesas.notifyDataSetChanged();
                    }
                    @Override
                    public void onCancelled(@NonNull DatabaseError error) {
                    }
                });

               */
            }

            @Override
            public void onChildRemoved(@NonNull DataSnapshot snapshot) {

            }

            @Override
            public void onChildMoved(@NonNull DataSnapshot snapshot, @Nullable String previousChildName) {

            }

            @Override
            public void onCancelled(@NonNull DatabaseError error) {

            }
        });


        // dbReferenceDespesas.removeEventListener(childEventListenerUsuario);
        // DecimalFormat decimalFormat1 = new DecimalFormat("#.##");
        // String formatReceitaTotal1 = decimalFormat1.format(progressReceita);


    }

    public void configuraCalendarView() {
        CharSequence meses[] = {"Janeiro ", "Fevereiro", "Março", "Abril", "Maio", "Junho", "Julho", "Agosto", "Setembro", "Outubro", "Novembro", "Dezembro"};
        calendarView.setTitleMonths(meses);
        CalendarDay dataAtual = calendarView.getCurrentDate();

        mesano = (dataAtual.getMonth() + 1) + "" + dataAtual.getYear();
        calendarView.setOnMonthChangedListener(new OnMonthChangedListener() {
            @Override
            public void onMonthChanged(MaterialCalendarView widget, CalendarDay date) {
                mesano = String.format("%02d", date.getMonth() + 1) + "" + date.getYear();
             //   Log.i("Data", "Periodo" + mesano);
                //dbReferenceValores.removeEventListener(valueEventListenerUsuario);
                //  recuperarReceitaTotal();
                //  recuperarDespesas();
              //  recuperarValoresRecyclerView();
            }
        });
    }

    public void recuperarValoresRecyclerView() {
        dbReferenceValores = ConfiguracaoFirebase.getDatabaseReference()
                .child("receitaedespesaunificadas")
                .child(idUsuario)
                .child(mesano);
        dbReferenceValores.addValueEventListener(new ValueEventListener() {
            @Override
            public void onDataChange(@NonNull DataSnapshot snapshot) {
                contas.clear();
                for (DataSnapshot dados : snapshot.getChildren()) {
                    Conta conta = dados.getValue(Conta.class);
                    contas.add(conta);
                }
                adapterHistoricoActivity.notifyDataSetChanged();
            }

            @Override
            public void onCancelled(@NonNull DatabaseError error) {
                error.getMessage();
            }
        });
    }

    //swipe no recyclerview
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

    //Metodo para excluir receita ou despesa inserida
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
                usuario.setId(idUsuario);
                contaDAO = new ContaDAO();
                contaDAO.remover(usuario, conta);
                removerDespesaseReceitas();
                adapterHistoricoActivity.notifyItemRemoved(position);
                despesasMes.clear();
            }
        });
        alertDialog.setNegativeButton("Cancelar", new DialogInterface.OnClickListener() {
            @Override
            public void onClick(DialogInterface dialog, int which) {
                Mensagem.mensagem(PrincipalActivity.this, "cancelado");
                adapterHistoricoActivity.notifyDataSetChanged();
            }
        });
        AlertDialog alert = alertDialog.create();
        alert.show();
    }

    public void removerDespesaseReceitas() {
        if (conta.getTipo().equals("d")) {
            despesaTotal -= conta.getValor();
            atualizaDespesaTotal(despesaTotal);
        }
        if (conta.getTipo().equals("r")) {
            receitaTotal -= conta.getValor();
            atualizaReceitaTotal(receitaTotal);
        }
    }

    public void textSaldo(double valor) {
        DecimalFormat decimalFormat = new DecimalFormat("#,##0.00");
        String formatReceitaTotal = decimalFormat.format(valor);
        textSaldo.setText("R$ " + formatReceitaTotal);
    }

    public void progressBarReceita(double progress) {

        //  progressBarReceita.setMax((int) progressReceita);
        progressBarReceita.setProgress((int) progress);
    }

    public void textGastos(double valor) {
        DecimalFormat decimalFormat = new DecimalFormat("#,##0.00");
        String formatDespesaTotal = decimalFormat.format(valor);
        textGastosSuperfluo.setText("R$ " + formatDespesaTotal);
    }
/*
    @Override
    protected void onStop() {
        super.onStop();
        Log.i("onStop", "evento removido");
        // DatabaseReference databaseReference = ConfiguracaoFirebase.getDatabaseReference();
        //  dbReferenceUsuario.removeEventListener(valueEventListenerUsuario);

    }





    public void verificaSaldo() {
        //calcula 50% do salario
        double metadeSaldo = (receitaTotal * 50) / 100;
        if (saldoConta < metadeSaldo) {
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


    public void progressBarCartaoCredito(String cartaoCredito) {
        progressBarGastos.setMax((int) receitaTotal);
        progressBarGastos.setProgress((int) creditoTotal);
        textGastos.setText("R$" + cartaoCredito);
    }





     */
}