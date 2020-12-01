package com.example.anotaai.dao;

import com.example.anotaai.config.ConfiguracaoFirebase;
import com.example.anotaai.model.Conta;
import com.example.anotaai.model.Usuario;
import com.google.firebase.database.DatabaseReference;

public class ContaDAO {

    DatabaseReference databaseReference,dbRemoverResumo,dbRemoverDespesas,
    dbRemoverReceitas;
    public boolean salvarReceitas(Usuario usuario, Conta conta){
        databaseReference = ConfiguracaoFirebase.getDatabaseReference();
        databaseReference.child("receitas")
                .child(usuario.getId())
                .child(conta.getData())
                .child(conta.getId())
                .setValue(conta);
        return true;
    }
    public boolean salvarDespesas(Usuario usuario, Conta conta){
        databaseReference = ConfiguracaoFirebase.getDatabaseReference();
        databaseReference.child("despesas")
                .child(usuario.getId())
                .child(conta.getData())
                .child(conta.getId())
                .setValue(conta);
        return true;
    }
    public boolean salvarResumoContas(Usuario usuario, Conta conta){
        databaseReference = ConfiguracaoFirebase.getDatabaseReference();
        databaseReference.child("receitaedespesaunificadas")
                .child(usuario.getId())
                .child(conta.getData())
                .child(conta.getId())
                .setValue(conta);
        return true;
    }
    public void remover(Usuario usuario,Conta conta){
        dbRemoverResumo= ConfiguracaoFirebase.getDatabaseReference();
        dbRemoverResumo.child("receitaedespesaunificadas")
                .child(usuario.getId())
                .child(conta.getData())
                .child(conta.getId())
                .removeValue();
        dbRemoverReceitas = ConfiguracaoFirebase.getDatabaseReference();
        dbRemoverReceitas.child("receitas")
                .child(usuario.getId())
                .child(conta.getData())
                .child(conta.getId())
                .removeValue();
        dbRemoverDespesas = ConfiguracaoFirebase.getDatabaseReference();
        dbRemoverDespesas.child("despesas")
                .child(usuario.getId())
                .child(conta.getData())
                .child(conta.getId())
                .removeValue();

        //remover no child despesas, receitas e receitaedespesaunifadas

    }
}
