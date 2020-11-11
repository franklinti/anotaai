package com.example.anotaai.helper;

import java.text.SimpleDateFormat;

public class DateCustom {

    public static String getData(){
        long data = System.currentTimeMillis();
        SimpleDateFormat simpleDateFormat = new SimpleDateFormat("dd/M/yyyy");
        String dataString = simpleDateFormat.format(data);
        return  dataString;
    }
    public static String dataEscolhida(String data){
        String retornoData[] = data.split("/");
        String dia = retornoData[0];
        String mes = retornoData[1];
        String ano = retornoData[2];

        String mesano = mes + ano;
        return mesano;
    }
}
