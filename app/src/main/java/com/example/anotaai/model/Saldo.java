package com.example.anotaai.model;

class Saldo {

    private Double receitaTotal = 0.00;
    private Double despesaTotal = 0.00;
    private Double creditoTotal = 0.00;

    public Saldo() {
    }

    public Double getReceitaTotal() {
        return receitaTotal;
    }

    public void setReceitaTotal(Double receitaTotal) {
        this.receitaTotal = receitaTotal;
    }

    public Double getDespesaTotal() {
        return despesaTotal;
    }

    public void setDespesaTotal(Double despesaTotal) {
        this.despesaTotal = despesaTotal;
    }

    public Double getCreditoTotal() {
        return creditoTotal;
    }

    public void setCreditoTotal(Double creditoTotal) {
        this.creditoTotal = creditoTotal;
    }
}
