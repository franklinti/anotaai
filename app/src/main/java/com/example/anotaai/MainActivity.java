package com.example.anotaai;

import androidx.appcompat.app.AppCompatActivity;

import android.content.Intent;
import android.os.Bundle;
import android.view.View;

import com.example.anotaai.activity.CadastroActivity;
import com.example.anotaai.activity.LoginActivity;
import com.example.anotaai.config.ConfiguracaoFirebase;
import com.google.firebase.auth.FirebaseAuth;
import com.heinrichreimersoftware.materialintro.app.IntroActivity;
import com.heinrichreimersoftware.materialintro.slide.FragmentSlide;

public class MainActivity extends IntroActivity {

    FirebaseAuth firebaseAuth;
    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
       // setContentView(R.layout.activity_main);
        setButtonBackVisible(false);
        setButtonNextVisible(false);
        //add slide
        addSlide(new FragmentSlide.Builder()
                .backgroundDark(android.R.color.white)
                .background(android.R.color.white)
                .fragment(R.layout.intro_1)
                .build()
        );

        addSlide(new FragmentSlide.Builder()
                .backgroundDark(android.R.color.white)
                .background(android.R.color.white)
                .fragment(R.layout.intro_2)
                .build()
        );
        addSlide(new FragmentSlide.Builder()
                .backgroundDark(android.R.color.white)
                .background(android.R.color.white)
                .fragment(R.layout.intro_cadastro)
                .canGoForward(false)
                .build()
        );
    }
    @Override
    protected void onStart() {
        super.onStart();
        verificaUsuarioLogado();
    }
    public void verificaUsuarioLogado(){
        firebaseAuth = ConfiguracaoFirebase.getFirebaseAutenticacao();
        // firebaseAuth.signOut();
        if(firebaseAuth.getCurrentUser() != null){
            abrirTelaPrincipal();
        }
    }
    public void btnEntrar(View view ){startActivity(new Intent(this, LoginActivity.class));}
    public void btnCadastro(View view){startActivity(new Intent(this, CadastroActivity.class));}
    public void abrirTelaPrincipal(){ startActivity(new Intent(this, PrincipalActivity.class));
        finishAffinity();
    }
}