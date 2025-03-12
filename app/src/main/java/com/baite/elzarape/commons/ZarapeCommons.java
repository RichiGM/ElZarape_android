package com.baite.elzarape.commons;

import android.graphics.Bitmap;
import android.graphics.BitmapFactory;
import android.util.Base64;

public class ZarapeCommons {

    public static final String SERVER_URL = "http://10.16.7.112:8080/ElZarape2/";
    public static final String API_URL = SERVER_URL+"api/";
    public static Bitmap procesarImagen(String b64){
        byte[] decodedBytes = Base64.decode(b64, Base64.DEFAULT);
        return BitmapFactory.decodeByteArray(decodedBytes, 0, decodedBytes.length);
    }
}
