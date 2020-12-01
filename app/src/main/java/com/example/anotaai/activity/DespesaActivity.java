package com.example.anotaai.activity;

import androidx.annotation.NonNull;
import androidx.appcompat.app.AppCompatActivity;

import android.os.Bundle;
import android.util.Log;
import android.view.View;
import android.widget.TextView;

import com.example.anotaai.PrincipalActivity;
import com.example.anotaai.R;
import com.example.anotaai.ReceitaActivity;
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

import java.text.DecimalFormat;

public class DespesaActivity extends AppCompatActivity
        implements View.OnClickListener {

    private TextView cValor,cData;
    private TextInputEditText cCategoria, cDescricao;
    FloatingActionButton fab;
    private double despesaTotal = 0.0;
    private double receitaTotal = 0.0;
    private ValueEventListener valueEventListenerUsuario;
    Conta novaConta = new Conta();
    Usuario usuario = new Usuario();
    ContaDAO contaDAO = new ContaDAO();
    private FirebaseAuth firebaseAuth;
    private FirebaseUser idUsuarioAtual;
    private String idUsuario;
    String mesAno;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_despesa);
        iniciarComponentes();
    }

    private void iniciarComponentes() {
        cValor = findViewById(R.id.activitydespesavalor);
        cData = findViewById(R.id.activitydespesadata);
        cData.setText(DateCustom.getData());
        cCategoria = findViewById(R.id.activitydespesacategoria);
        cDescricao = findViewById(R.id.activitydespesadescricao);

        fab = findViewById(R.id.fabsalvardespesa);
        fab.setOnClickListener(this);

        firebaseAuth = ConfiguracaoFirebase.getFirebaseAutenticacao();
        idUsuarioAtual = firebaseAuth.getCurrentUser();
        idUsuario = idUsuarioAtual.getUid();
        geraId();
    }

    @Override
    protected void onStart() {
        super.onStart();
      //  recuperarDespesas();
    }

    public void geraId() {
        DatabaseReference dataKey = ConfiguracaoFirebase.getDatabaseReference();
        novaConta.setId(dataKey.push().getKey());
        novaConta.setTipo("d");
        usuario.setId(idUsuario);
    }

    @Override
    public void onClick(View v) {
        switch (v.getId()) {
            case R.id.fabsalvardespesa:
                validarCampos();
                break;
        }
    }

    public void validarCampos() {
        if (!cValor.getText().toString().isEmpty()) {
            if (!cData.getText().toString().isEmpty()) {
                if (!cCategoria.getText().toString().isEmpty()) {
                    if (!cDescricao.getText().toString().isEmpty()) {
                        salvarDespesa();
                    } else {
                        Mensagem.mensagem(this, "Insira descricao");
                    }
                } else {
                    Mensagem.mensagem(this, "Insira categoria");
                }
            } else {
                Mensagem.mensagem(this, "Insira data");
            }
        } else {
            Mensagem.mensagem(this, "Insira valor");
        }
    }

    public void salvarDespesa() {
        novaConta.setValor(Double.parseDouble(cValor.getText().toString()));
        String data = cData.getText().toString();
        mesAno = DateCustom.dataEscolhida(data);
        novaConta.setData(mesAno);
        novaConta.setCategoria(cCategoria.getText().toString());
        novaConta.setDescricao(cDescricao.getText().toString());
        contaDAO.salvarDespesas(usuario, novaConta);
        contaDAO.salvarResumoContas(usuario,novaConta);
      //  double valor = despesaTotal + novaConta.getValor();
      //  DecimalFormat decimalFormat = new DecimalFormat("#.##");
      //  String formatValor = decimalFormat.format(valor);
      //  atualizaDespesa(Double.parseDouble(formatValor));
        finish();
    }
/*
    public void recuperarDespesas() {
       // String d  = DateCustom.getData();
       // String mesAnoEscolhido = DateCustom.dataEscolhida(d);
        DatabaseReference dbReferenceDespesas = ConfiguracaoFirebase.getDatabaseReference()
                .child("saldo")
                .child(idUsuario);
        valueEventListenerUsuario = dbReferenceDespesas.addValueEventListener(new ValueEventListener() {
            @Override
            public void onDataChange(@NonNull DataSnapshot snapshot) {
                if(snapshot.exists()){
                        Conta conta = snapshot.getValue(Conta.class);
                        despesaTotal = conta.getDespesaTotal();
                }
            }
            @Override
            public void onCancelled(@NonNull DatabaseError error) {
                error.getMessage();
            }
        });
    }

    public void atualizaDespesa(double despesaAtualizada) {
        DatabaseReference dbrad = ConfiguracaoFirebase.getDatabaseReference()
                .child("saldo").child(idUsuario);
        dbrad.child("despesaTotal").setValue(despesaAtualizada);
    }
*/


}
