package com.example.anotaai.activity;

import androidx.annotation.NonNull;
import androidx.appcompat.app.AppCompatActivity;
import androidx.appcompat.widget.Toolbar;

import android.content.Intent;
import android.os.Bundle;
import android.view.View;
import android.widget.Button;
import android.widget.EditText;
import android.widget.Toast;

import com.example.anotaai.PrincipalActivity;
import com.example.anotaai.R;
import com.example.anotaai.config.ConfiguracaoFirebase;
import com.example.anotaai.model.Usuario;
import com.google.android.gms.tasks.OnCompleteListener;
import com.google.android.gms.tasks.Task;
import com.google.firebase.auth.AuthResult;
import com.google.firebase.auth.FirebaseAuth;
import com.google.firebase.auth.FirebaseAuthInvalidCredentialsException;
import com.google.firebase.auth.FirebaseAuthInvalidUserException;

public class LoginActivity extends AppCompatActivity {

    private EditText campoEmail,campoSenha;
    private Button butttonEntrar;
    private Usuario user = new Usuario();
    private FirebaseAuth firebaseAuth;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_login);

        Toolbar toolbar = findViewById(R.id.toolbar);
        toolbar.setTitle("Login");
        setSupportActionBar(toolbar);
      //  getSupportActionBar().setDisplayHomeAsUpEnabled(true);
        iniciarComponentes();

    }
    public void iniciarComponentes(){
        campoEmail = findViewById(R.id.editEmailLogin);
        campoSenha = findViewById(R.id.editSenhaLogin);
        butttonEntrar = findViewById(R.id.btnCadastro);
       //erro button, user abre varias telas da aplicacao
        butttonEntrar.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {

                String textEmail = campoEmail.getText().toString();
                String textSenha = campoSenha.getText().toString();

                if(!textEmail.isEmpty() && !textSenha.isEmpty()){
                    user.setEmail(textEmail);
                    user.setSenha(textSenha);
                    butttonEntrar.setEnabled(false);
                    validarLogin();

                }else{
                    exibirMensagem("Preencha o Formulario!");
                }
            }
        });

        //recupera instancia do Firebase
        firebaseAuth = ConfiguracaoFirebase.getFirebaseAutenticacao();
    }
    public void exibirMensagem(String text){
        Toast.makeText(LoginActivity.this,text,
                Toast.LENGTH_SHORT).show();
    }
    public void validarLogin(){
        firebaseAuth.signInWithEmailAndPassword(
                user.getEmail(), user.getSenha()
        ).addOnCompleteListener(new OnCompleteListener<AuthResult>() {
            @Override
            public void onComplete(@NonNull Task<AuthResult> task) {
                if(task.isSuccessful()){
                    abrirTelaPrincipal();
                }else{
                    String excecao="";
                    try{
                        throw task.getException();
                    }catch(FirebaseAuthInvalidCredentialsException e){
                        excecao ="E-mail e Senha nao correspodem ao usuario cadastrado!";
                    }catch(FirebaseAuthInvalidUserException e ){
                        excecao = "Usuario nao cadastrado";
                    }
                    catch(Exception e){
                        excecao = "Erro ao realizar login" + e.getMessage();
                        e.printStackTrace();
                    }
                    exibirMensagem(excecao);
                }
            }
        });
    }
    public void abrirTelaPrincipal(){
        startActivity(new Intent(this, PrincipalActivity.class));
        finish();
    }
}
