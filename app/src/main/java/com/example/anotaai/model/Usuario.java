package com.example.anotaai.model;

import com.example.anotaai.ReceitaActivity;
import com.google.firebase.database.Exclude;

public class Usuario {
    private String id;
    private String nome;
    private String sobrenome;
    private String email;
    private String senha;


    public Usuario() {
    }

    public String getId() {
        return id;
    }

    public void setId(String id) {
        this.id = id;
    }

    public String getNome() {
        return nome;
    }

    public void setNome(String nome) {
        this.nome = nome;
    }

    public String getSobrenome() {
        return sobrenome;
    }

    public void setSobrenome(String sobrenome) {
        this.sobrenome = sobrenome;
    }

    public String getEmail() {
        return email;
    }

    public void setEmail(String email) {
        this.email = email;
    }

    @Exclude
    public String getSenha() {
        return senha;
    }

    public void setSenha(String senha) {
        this.senha = senha;
    }

    /*

    //dados cartao de credito
    public Double getCreditoTotal() {
        return creditoTotal;
    }

    public void setCreditoTotal(Double creditoTotal) {
        this.creditoTotal = creditoTotal;
    }

     */
}
