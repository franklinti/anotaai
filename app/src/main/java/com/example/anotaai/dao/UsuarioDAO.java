package com.example.anotaai.dao;

import android.util.Log;

import com.example.anotaai.config.ConfiguracaoFirebase;
import com.example.anotaai.model.Usuario;
import com.google.firebase.database.DatabaseReference;

public class UsuarioDAO {
    DatabaseReference databaseReference;

    public boolean salvar(Usuario usuario){
        databaseReference = ConfiguracaoFirebase.getDatabaseReference();
        databaseReference.child("usuarios").child(usuario.getId())
                .setValue(usuario);
       // Log.i("","KEY"+databaseReference.getKey());
        return true;
    }
}
