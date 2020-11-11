package com.example.anotaai.config;

import com.google.firebase.auth.FirebaseAuth;
import com.google.firebase.database.DatabaseReference;
import com.google.firebase.database.FirebaseDatabase;

public class ConfiguracaoFirebase {

    public static FirebaseAuth autenticacao;
    public static DatabaseReference databaseReference;
    //retorna instancia FirebaseAuth
    public static FirebaseAuth getFirebaseAutenticacao(){
        if(autenticacao == null) {
            autenticacao = FirebaseAuth.getInstance();
        }
        return autenticacao;
    }
    public static DatabaseReference getDatabaseReference(){
        if(databaseReference == null){
            databaseReference = FirebaseDatabase.getInstance().getReference();
        }
        return databaseReference;
    }

}
