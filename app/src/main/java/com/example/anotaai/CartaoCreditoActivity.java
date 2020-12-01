package com.example.anotaai;

import androidx.annotation.NonNull;
import androidx.appcompat.app.AlertDialog;
import androidx.appcompat.app.AppCompatActivity;

import android.app.DatePickerDialog;
import android.content.DialogInterface;
import android.content.Intent;
import android.os.Bundle;
import android.util.Log;
import android.view.LayoutInflater;
import android.view.View;
import android.widget.Button;
import android.widget.DatePicker;
import android.widget.TextView;

import com.example.anotaai.activity.DespesaActivity;
import com.example.anotaai.config.ConfiguracaoFirebase;
import com.example.anotaai.dao.ContaDAO;
import com.example.anotaai.helper.DateCustom;
import com.example.anotaai.helper.Mensagem;
import com.example.anotaai.model.Conta;
import com.example.anotaai.model.Usuario;
import com.google.android.material.floatingactionbutton.FloatingActionButton;
import com.google.android.material.textfield.TextInputEditText;
import com.google.firebase.auth.FirebaseAuth;
import com.google.firebase.auth.FirebaseUser;
import com.google.firebase.database.DataSnapshot;
import com.google.firebase.database.DatabaseError;
import com.google.firebase.database.DatabaseReference;
import com.google.firebase.database.ValueEventListener;
import com.prolificinteractive.materialcalendarview.CalendarDay;
import com.prolificinteractive.materialcalendarview.MaterialCalendarView;
import com.prolificinteractive.materialcalendarview.OnMonthChangedListener;

import java.util.Calendar;

public class CartaoCreditoActivity extends AppCompatActivity
        implements View.OnClickListener {

    private TextView cValor,cData;
    private Button cDataFechaDia,cDataMelhorDia;
    FloatingActionButton fab;
    private double creditoTotal = 0.00;
    private ValueEventListener valueEventListenerUsuario;
    Conta novaConta = new Conta();
    Usuario usuario = new Usuario();
    ContaDAO contaDAO = new ContaDAO();
    private FirebaseAuth firebaseAuth;
    private FirebaseUser idUsuarioAtual;
    private String id;
    private int mYear, mMonth, mDay, mHour, mMinute;



    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_cartao_credito);
        iniciarComponentes();
    }

    public void iniciarComponentes() {

        cValor = findViewById(R.id.activitycartaocreditovalor);
        cData = findViewById(R.id.activitycartaocreditodata);
        cData.setText(DateCustom.getData());

        cDataFechaDia = findViewById(R.id.activitycartaocreditodatafechadia);
        cDataFechaDia.setOnClickListener(this);
        cDataMelhorDia = findViewById(R.id.activitycartaocreditodatamelhordia);
        cDataMelhorDia.setOnClickListener(this);

        fab = findViewById(R.id.fabsalvarcartaocredito);
        fab.setOnClickListener(this);

        firebaseAuth = ConfiguracaoFirebase.getFirebaseAutenticacao();
        idUsuarioAtual = firebaseAuth.getCurrentUser();
        id = idUsuarioAtual.getUid();
        gerarId();

    }

    @Override
    protected void onStart() {
        super.onStart();
        recuperarCreditoTotal();
    }

    public void gerarId() {
        DatabaseReference dataKey = ConfiguracaoFirebase.getDatabaseReference();
        novaConta.setId(dataKey.push().getKey());
        novaConta.setTipo("d");
        usuario.setId(id);
    }

    @Override
    public void onClick(View v) {
        switch (v.getId()) {
            case R.id.activitycartaocreditodatafechadia:
                abrirDataFechaDia();
                break;
            case R.id.activitycartaocreditodatamelhordia:
                abrirDataMehorDia();
                break;
            case R.id.fabsalvarcartaocredito:
                validarCampos();
                break;
        }
    }
    private void abrirDataFechaDia(){
        final Calendar c = Calendar.getInstance();
        mYear = c.get(Calendar.YEAR);
        mMonth = c.get(Calendar.MONTH);
        mDay = c.get(Calendar.DAY_OF_MONTH);
        DatePickerDialog datePickerDialog = new DatePickerDialog(this,
                new DatePickerDialog.OnDateSetListener() {
                    @Override
                    public void onDateSet(DatePicker view, int year,
                                          int monthOfYear, int dayOfMonth) {
                        cDataFechaDia.setText(dayOfMonth + "-" + (monthOfYear + 1) + "-" + year);

                    }
                }, mYear, mMonth, mDay);
        datePickerDialog.show();
    }
    private void abrirDataMehorDia(){
        final Calendar c = Calendar.getInstance();
        mYear = c.get(Calendar.YEAR);
        mMonth = c.get(Calendar.MONTH);
        mDay = c.get(Calendar.DAY_OF_MONTH);
        DatePickerDialog datePickerDialog = new DatePickerDialog(this,
                new DatePickerDialog.OnDateSetListener() {
                    @Override
                    public void onDateSet(DatePicker view, int year,
                                          int monthOfYear, int dayOfMonth) {
                        cDataMelhorDia.setText(dayOfMonth + "-" + (monthOfYear + 1) + "-" + year);

                    }
                }, mYear, mMonth, mDay);
        datePickerDialog.show();
    }
    public void validarCampos() {
        if (!cValor.getText().toString().isEmpty()) {
            if (!cData.getText().toString().isEmpty()) {
                if (!cDataFechaDia.getText().toString().isEmpty()) {
                    if (!cDataMelhorDia.getText().toString().isEmpty()) {
                        salvar();
                    } else {
                        Mensagem.mensagem(this, "Data Melhor dia?");
                    }
                } else {
                    Mensagem.mensagem(this, "Data fecha dia?");
                }
            } else {
                Mensagem.mensagem(this, "Insira data");
            }
        } else {
            Mensagem.mensagem(this, "Insira valor");
        }
    }

    public void salvar() {
        novaConta.setValor(Double.parseDouble(cValor.getText().toString()));
        // Calendar calendar = null;
        String data = cData.getText().toString();
        String mesAno = DateCustom.dataEscolhida(data);
        novaConta.setData(mesAno);
      //  novaConta.setDataFechaDia(cDataFechaDia.getText().toString());
       // novaConta.setDataMelhorDia(cDataMelhorDia.getText().toString());
        //soma campo receita e atualiza
        // Log.i("",""+receitaTotal);
        // Log.i("",""+novaConta.getValor());
        double creditoAtualizado = creditoTotal + novaConta.getValor();
        // Log.i("",""+receitaAtualizada);
        atualizaCredito(creditoAtualizado);

        contaDAO.salvarDespesas(usuario, novaConta);
        finish();
    }

    public void recuperarCreditoTotal() {
        DatabaseReference dbrrrt = ConfiguracaoFirebase.getDatabaseReference()
                .child("usuarios")
                .child(id);
        Log.i("onStop", "evento acionado");
        valueEventListenerUsuario = dbrrrt.addValueEventListener(new ValueEventListener() {
            @Override
            public void onDataChange(@NonNull DataSnapshot snapshot) {
                Usuario usuario = snapshot.getValue(Usuario.class);
              //  Log.i("Despesa", "Credito :" + usuario.getCreditoTotal());
             //   creditoTotal += usuario.getCreditoTotal();
            }

            @Override
            public void onCancelled(@NonNull DatabaseError error) {
                error.getMessage();
            }
        });
    }

    private void atualizaCredito(double creditoAtualizado) {
        DatabaseReference dbrar = ConfiguracaoFirebase.getDatabaseReference()
                .child("usuarios").child(id);
        dbrar.child("creditoTotal").setValue(creditoAtualizado);
    }

    @Override
    protected void onStop() {
        super.onStop();
        Log.i("onStop", "evento removido");
        DatabaseReference databaseReference = ConfiguracaoFirebase.getDatabaseReference();
        databaseReference.removeEventListener(valueEventListenerUsuario);
    }
}