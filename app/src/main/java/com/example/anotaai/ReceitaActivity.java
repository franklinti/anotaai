package com.example.anotaai;

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

public class ReceitaActivity extends AppCompatActivity
        implements View.OnClickListener {

    private TextView cValor,cData;
    private TextInputEditText  cCategoria, cDescricao;
    FloatingActionButton fab;
    private double receitaTotal = 0.00;
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
        setContentView(R.layout.activity_receita);
        iniciarComponentes();
    }

    public void iniciarComponentes() {

        cValor = findViewById(R.id.activityreceitavalor);
        cData = findViewById(R.id.activityreceitadata);
        cData.setText(DateCustom.getData());
        cCategoria = findViewById(R.id.activityreceitacategoria);
        cDescricao = findViewById(R.id.activityreceitadescricao);
        fab = findViewById(R.id.fabsalvarreceita);
        fab.setOnClickListener(this);


        firebaseAuth = ConfiguracaoFirebase.getFirebaseAutenticacao();
        idUsuarioAtual = firebaseAuth.getCurrentUser();
        id = idUsuarioAtual.getUid();
        gerarId();
    }

    @Override
    protected void onStart() {
        super.onStart();
        recuperarReceitaTotal();
    }

    public void gerarId() {
        DatabaseReference dataKey = ConfiguracaoFirebase.getDatabaseReference();
        novaConta.setId(dataKey.push().getKey());
        novaConta.setTipo("r");
        usuario.setId(id);
    }

    @Override
    public void onClick(View v) {
        switch (v.getId()) {
            case R.id.fabsalvarreceita:
                validarCampos();
                break;
        }
    }


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
        // Calendar calendar = null;
        String data = cData.getText().toString();
        String mesAno = DateCustom.dataEscolhida(data);
        novaConta.setData(mesAno);
        novaConta.setCategoria(cCategoria.getText().toString());
        novaConta.setDescricao(cDescricao.getText().toString());
        //soma campo receita e atualiza
        // Log.i("",""+receitaTotal);
        // Log.i("",""+novaConta.getValor());
        double receitaAtualizada = receitaTotal + novaConta.getValor();
        // Log.i("",""+receitaAtualizada);
        atualizaReceita(receitaAtualizada);

        ContaDAO contaDAO = new ContaDAO();
        contaDAO.salvar(usuario, novaConta);
        finish();
    }

    public void recuperarReceitaTotal() {
        DatabaseReference dbrrrt = ConfiguracaoFirebase.getDatabaseReference()
                .child("usuarios")
                .child(id);
        Log.i("onStop", "evento acionado");
        valueEventListenerUsuario = dbrrrt.addValueEventListener(new ValueEventListener() {
            @Override
            public void onDataChange(@NonNull DataSnapshot snapshot) {
                Usuario usuario = snapshot.getValue(Usuario.class);
                Log.i("Receita", "Dado :" + usuario.getReceitaTotal());
                receitaTotal += usuario.getReceitaTotal();
            }

            @Override
            public void onCancelled(@NonNull DatabaseError error) {
                error.getMessage();
            }
        });
    }

    private void atualizaReceita(double receitaAtualizada) {
        DatabaseReference dbrar = ConfiguracaoFirebase.getDatabaseReference()
                .child("usuarios").child(id);
        dbrar.child("receitaTotal").setValue(receitaAtualizada);
    }

    @Override
    protected void onStop() {
        super.onStop();
        Log.i("onStop", "evento removido");
        DatabaseReference databaseReference = ConfiguracaoFirebase.getDatabaseReference();
        databaseReference.removeEventListener(valueEventListenerUsuario);
    }

}
