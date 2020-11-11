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
import com.example.anotaai.dao.UsuarioDAO;
import com.example.anotaai.helper.Base64Custom;
import com.example.anotaai.helper.Mensagem;
import com.example.anotaai.model.Usuario;
import com.google.android.gms.tasks.OnCompleteListener;
import com.google.android.gms.tasks.Task;
import com.google.firebase.auth.AuthResult;
import com.google.firebase.auth.FirebaseAuth;
import com.google.firebase.auth.FirebaseAuthInvalidCredentialsException;
import com.google.firebase.auth.FirebaseAuthInvalidUserException;
import com.google.firebase.auth.FirebaseAuthUserCollisionException;
import com.google.firebase.auth.FirebaseAuthWeakPasswordException;

public class CadastroActivity extends AppCompatActivity {

    private EditText campoNome, campoEmail,campoSenha;
    private Button buttonCadastrar;
    private Usuario user = new Usuario();
    private FirebaseAuth firebaseAuth;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_cadastro);

        Toolbar toolbar = findViewById(R.id.toolbar);
        toolbar.setTitle("Cadastro");
        setSupportActionBar(toolbar);
       // getSupportActionBar().setDisplayHomeAsUpEnabled(true);
        iniciarComponentes();
    }
    public void iniciarComponentes(){
        campoNome = findViewById(R.id.editNome);
        campoEmail = findViewById(R.id.editEmailLogin);
        campoSenha = findViewById(R.id.editSenhaLogin);
        buttonCadastrar = findViewById(R.id.btnCadastro);
        buttonCadastrar .setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                String textNome = campoNome.getText().toString();
                String textEmail = campoEmail.getText().toString();
                String textSenha = campoSenha.getText().toString();

                if(!textNome.isEmpty() && !textEmail.isEmpty() && !textSenha.isEmpty()){
                    user.setNome(textNome);
                    String email = Base64Custom.codificarBase64(textEmail);
                    user.setEmail(email);
                    String senha = Base64Custom.codificarBase64(textSenha);
                    user.setSenha(senha);
                    cadastrarUsuario();
                }else{
                    Mensagem.mensagem(CadastroActivity.this,"Preencha todos os campos do Formulario!");
                }
            }
        });
        firebaseAuth = ConfiguracaoFirebase.getFirebaseAutenticacao();
    }
    public void cadastrarUsuario(){
        String decodificaEmail = Base64Custom.decodificarBase64(user.getEmail());
        String decodificaSenha = Base64Custom.decodificarBase64(user.getSenha());
       firebaseAuth.createUserWithEmailAndPassword(
                decodificaEmail, decodificaSenha
        ).addOnCompleteListener(this, new OnCompleteListener<AuthResult>() {
            @Override
            public void onComplete(@NonNull Task<AuthResult> task) {
                if(task.isSuccessful()){
                   String idUsuario = firebaseAuth.getCurrentUser().getUid();
                   user.setId(idUsuario);
                   UsuarioDAO usuarioDAO = new UsuarioDAO();
                   if(usuarioDAO.salvar(user)){
                        Mensagem.mensagem(CadastroActivity.this,"Usuario cadastrado!");
                        abrirTelaPrincipal();
                   }else{
                       Mensagem.mensagem(CadastroActivity.this,"Usuario não cadastrado!");
                       firebaseAuth.getCurrentUser().delete();
                       return;
                   }

                  //  finish();
                }else{
                    String excecao="";
                    try{
                        throw task.getException();
                    }catch(FirebaseAuthWeakPasswordException e){
                        excecao = "Digite uma senha mais forte";
                    }catch(FirebaseAuthInvalidCredentialsException e){
                        excecao ="Por favor, digite um e-mail valido";
                    }catch(FirebaseAuthUserCollisionException e){
                        excecao = "Conta ja cadastrada.";
                    }catch(FirebaseAuthInvalidUserException e){
                        excecao = "E-mail not exist";
                    }
                    catch(Exception e){
                        excecao = "Erro ao cadastrar usuario" + e.getMessage();
                        e.printStackTrace();
                    }
                    Mensagem.mensagem(CadastroActivity.this,excecao);
                }
            }
        });
    }
    public void abrirTelaPrincipal(){
        startActivity(new Intent(this, PrincipalActivity.class));
        finishAffinity();
    }
}
