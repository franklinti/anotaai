package com.example.anotaai.helper;

import android.app.Activity;
import android.widget.Toast;

public class Mensagem {

    public static String mensagem(Activity activity, String text){
       Toast.makeText(activity,text,Toast.LENGTH_SHORT).show();
       return text;
    }

}
