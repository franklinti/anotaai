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
import com.prolificinteractive.materialcalendarview.CalendarDay;
import com.prolificinteractive.materialcalendarview.MaterialCalendarView;
import com.prolificinteractive.materialcalendarview.OnMonthChangedListener;

import java.text.DecimalFormat;

public class ReceitaActivity extends AppCompatActivity
        implements View.OnClickListener {

    private TextView cValor, cData;
    private TextInputEditText cCategoria, cDescricao;
    FloatingActionButton fab;
    private double receitaTotal = 0.0;
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
        setContentView(R.layout.activity_receita);
        iniciarComponentes();
    }

    @Override
    protected void onStart() {
        super.onStart();
       // recuperarReceitas();
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
        idUsuario = idUsuarioAtual.getUid();
        gerarId();
    }
    public void gerarId() {
        DatabaseReference dataKey = ConfiguracaoFirebase.getDatabaseReference();
        novaConta.setId(dataKey.push().getKey());
        novaConta.setTipo("r");
        usuario.setId(idUsuario);
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
                        salvarReceita();
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

    public void salvarReceita() {
        novaConta.setValor(Double.parseDouble(cValor.getText().toString()));
        String data = cData.getText().toString();
        mesAno = DateCustom.dataEscolhida(data);
        novaConta.setData(mesAno);
        novaConta.setCategoria(cCategoria.getText().toString());
        novaConta.setDescricao(cDescricao.getText().toString());
        contaDAO.salvarReceitas(usuario, novaConta);
        contaDAO.salvarResumoContas(usuario,novaConta);
      //  double valor = receitaTotal + novaConta.getValor();
      //  DecimalFormat decimalFormat = new DecimalFormat("#.##");
      //  String formatValor = decimalFormat.format(valor);
      //  atualizaReceita(Double.parseDouble(formatValor));
        finish();
    }
    /*
    public double recuperarReceitas() {
      //  String d  = DateCustom.getData();
      //  String mesAnoEscolhido = DateCustom.dataEscolhida(d);
       // Log.i("","DATA"+mesAnoEscolhido);
        DatabaseReference dbReferenceReceitas = ConfiguracaoFirebase.getDatabaseReference()
                .child("saldo")
                .child(idUsuario);
      dbReferenceReceitas.addValueEventListener(new ValueEventListener() {
            @Override
            public void onDataChange(@NonNull DataSnapshot snapshot) {
                if(snapshot.exists()) {
                        Conta conta = snapshot.getValue(Conta.class);
                        receitaTotal = conta.getReceitaTotal();
                }
            }
            @Override
            public void onCancelled(@NonNull DatabaseError error) {
                error.getMessage();
            }
        });
      return receitaTotal;
    }

    private void atualizaReceita(double receitaAtualizada) {
        DatabaseReference dbrar = ConfiguracaoFirebase.getDatabaseReference()
                .child("saldo").child(idUsuario);
        dbrar.child("receitaTotal").setValue(receitaAtualizada);
    }
*/
}
