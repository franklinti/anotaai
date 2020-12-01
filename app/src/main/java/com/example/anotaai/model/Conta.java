package com.example.anotaai.model;

import com.example.anotaai.config.ConfiguracaoFirebase;
import com.google.firebase.database.DatabaseReference;

import java.io.Serializable;

public class Conta extends Saldo implements Serializable {

    private String keyMes;
    private String id;
    private Double valor = 0.00;
    private String data;
    private String categoria;
    private String descricao;
    private String tipo;
    //   private Double creditoTotal = 0.00;
    //dados cartao credito
  //  private String dataFechaDia;
  //  private String dataMelhorDia;
  //  private String tipoCartaoCredito;

    public Conta() {
    }

    public String getKeyMes() {
        return keyMes;
    }

    public void setKeyMes(String keyMes) {
        this.keyMes = keyMes;
    }

    public String getId() {
        return id;
    }

    public void setId(String id) {
        this.id = id;
    }

    public Double getValor() {
        return valor;
    }

    public void setValor(Double valor) {
        this.valor = valor;
    }

    public String getData() {
        return data;
    }

    public void setData(String data) {
        this.data = data;
    }

    public String getCategoria() {
        return categoria;
    }

    public void setCategoria(String categoria) {
        this.categoria = categoria;
    }

    public String getDescricao() {
        return descricao;
    }

    public void setDescricao(String descricao) {
        this.descricao = descricao;
    }

    public String getTipo() {
        return tipo;
    }

    public void setTipo(String tipo) {
        this.tipo = tipo;
    }


    //dados cartao de credito
    /*
    public String getDataFechaDia() {
        return dataFechaDia;
    }

    public void setDataFechaDia(String dataFechaDia) {
        this.dataFechaDia = dataFechaDia;
    }

    public String getDataMelhorDia() {
        return dataMelhorDia;
    }

    public void setDataMelhorDia(String dataMelhorDia) {
        this.dataMelhorDia = dataMelhorDia;
    }

     */
}
