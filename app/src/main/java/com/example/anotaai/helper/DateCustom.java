package com.example.anotaai.helper;

import android.util.Log;

import java.text.DateFormat;
import java.text.ParseException;
import java.text.SimpleDateFormat;
import java.util.Date;
import java.util.Locale;

public class DateCustom {

    public static String getData(){
        long data = System.currentTimeMillis();
        SimpleDateFormat simpleDateFormat = new SimpleDateFormat("dd/M/yyyy");
        String dataString = simpleDateFormat.format(data);

        String retornoData[] = dataString.split("/");
        String dia = retornoData[0];
        String mes = retornoData[1];
        String ano = retornoData[2];
       // String a = String.format("%02d",mes);
       // String d = dia+a+ano;
        Log.i("",""+dia+mes+ano);
        return  dataString;
    }
    public static String dataEscolhida(String data){
        String retornoData[] = data.split("/");
        String dia = retornoData[0];
        int mes = Integer.parseInt(retornoData[1]);
        String ano = retornoData[2];
        String mesFormat =  String.format("%02d",mes);
        String mesano = mesFormat + ano;
        return mesano;
    }
    public static String convetDataTexto(String dataBanco){
        SimpleDateFormat dateFormat = new SimpleDateFormat("MMyyyy");
        String dataEmTextoResumido = null;
        try {
            Date date = dateFormat.parse(dataBanco);
            DateFormat df = new SimpleDateFormat("MMMM",new Locale("pt","BR"));
            String dataEmTextoFormata =  df.format(date);
            //pega as tres primeiras letras do texto.
            dataEmTextoResumido = dataEmTextoFormata.substring(0,3);
        } catch (ParseException e) {
            e.printStackTrace();
        }

        return dataEmTextoResumido;
    }
}
