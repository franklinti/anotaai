package com.example.anotaai.dao;

import com.example.anotaai.config.ConfiguracaoFirebase;
import com.example.anotaai.model.Conta;
import com.example.anotaai.model.Usuario;
import com.google.firebase.database.DatabaseReference;

public class ContaDAO {

    DatabaseReference databaseReference;
    public boolean salvar(Usuario usuario, Conta conta){
        databaseReference = ConfiguracaoFirebase.getDatabaseReference();
        databaseReference.child("contas")
                .child(usuario.getId())
                .child(conta.getData())
                .child(conta.getId())
                .setValue(conta);
        return true;
    }
    public void remover(Usuario usuario,Conta conta){
        databaseReference = ConfiguracaoFirebase.getDatabaseReference();
        databaseReference.child("contas")
                .child(usuario.getId())
                .child(conta.getData())
                .child(conta.getId())
                .removeValue();


    }
}
