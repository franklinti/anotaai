package com.example.anotaai.activity;

import androidx.annotation.NonNull;
import androidx.appcompat.app.AppCompatActivity;

import android.os.Bundle;
import android.util.Log;
import android.view.View;
import android.widget.TextView;

import com.example.anotaai.R;
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

public class DespesaActivity extends AppCompatActivity
        implements View.OnClickListener {

    private TextView cValor,cData;
    private TextInputEditText cCategoria, cDescricao;
    FloatingActionButton fab;
    private double despesaTotal = 0.0;
    private ValueEventListener valueEventListenerUsuario;
    Conta novaConta = new Conta();
    Usuario usuario = new Usuario();
    ContaDAO contaDAO = new ContaDAO();
    private FirebaseAuth firebaseAuth;
    private FirebaseUser idUsuarioAtual;
    private String id;


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
        id = idUsuarioAtual.getUid();
        geraId();
    }

    @Override
    protected void onStart() {
        super.onStart();
        recuperarDespesaTotal();
    }

    public void geraId() {
        DatabaseReference dataKey = ConfiguracaoFirebase.getDatabaseReference();
        novaConta.setId(dataKey.push().getKey());
        novaConta.setTipo("d");
        usuario.setId(id);
    }

    @Override
    public void onClick(View v) {
        switch (v.getId()) {
            case R.id.fabsalvardespesa:
                validarCampos();
                break;
        }
    }
//inserir regex no campo data
    public void validarCampos() {
        if (!cValor.getText().toString().isEmpty()) {
            if (!cData.getText().toString().isEmpty()) {
                if (!cCategoria.getText().toString().isEmpty()) {
                    if (!cDescricao.getText().toString().isEmpty()) {
                        salvar();
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

    public void salvar() {
        novaConta.setValor(Double.parseDouble(cValor.getText().toString()));
        String data = cData.getText().toString();
        String mesAno = DateCustom.dataEscolhida(data);
        novaConta.setData(mesAno);
        novaConta.setCategoria(cCategoria.getText().toString());
        novaConta.setDescricao(cDescricao.getText().toString());
        // Log.i("",""+despesaTotal);
        // Log.i("",""+novaConta.getValor());
        double despesaAtualizada = despesaTotal + novaConta.getValor();
        // Log.i("",""+despesaAtualizada);
        atualizaDespesa(despesaAtualizada);
        contaDAO.salvar(usuario, novaConta);
        finish();
    }

    public void recuperarDespesaTotal() {
        DatabaseReference dbrrdt = ConfiguracaoFirebase.getDatabaseReference()
                .child("usuarios")
                .child(id);
        Log.i("onStop", "evento acionado");
        valueEventListenerUsuario = dbrrdt.addValueEventListener(new ValueEventListener() {
            @Override
            public void onDataChange(@NonNull DataSnapshot snapshot) {
                Usuario usuario = snapshot.getValue(Usuario.class);
                Log.i("Despesa", "Dado :" + usuario.getDespesaTotal());
                despesaTotal += usuario.getDespesaTotal();
            }

            @Override
            public void onCancelled(@NonNull DatabaseError error) {
                error.getMessage();
            }
        });
    }

    public void atualizaDespesa(double despesaAtualizada) {
        DatabaseReference dbrad = ConfiguracaoFirebase.getDatabaseReference()
                .child("usuarios").child(id);
        dbrad.child("despesaTotal").setValue(despesaAtualizada);

    }

    @Override
    protected void onStop() {
        super.onStop();
        Log.i("onStop", "evento removido");
        DatabaseReference databaseReference = ConfiguracaoFirebase.getDatabaseReference();
        databaseReference.removeEventListener(valueEventListenerUsuario);
    }
}
