package com.mediprice.hospitals.smsIntegration;

import android.os.AsyncTask;
import android.util.Log;

import java.io.BufferedReader;
import java.io.IOException;
import java.io.InputStreamReader;
import java.net.HttpURLConnection;
import java.net.MalformedURLException;
import java.net.URL;

public class SmsHelper extends AsyncTask<String, String, String> {

    @Override
    protected String doInBackground(String... params) {
        String contact = "+91" + params[0];
        String msg = params[1];
        Log.e("SMS","contact number "+contact);
        Log.e("SMS",msg);
//        if(true)
//            return "";
        try {
            String urlRawString= String.format("https://stark-earth-78790.herokuapp.com/index.php?message_body=%s&receiver_number=%s",msg,contact );
            URL urlDEBUG =new URL(urlRawString);
            //don't use this below line
            URL urlPRODUCTION =new URL(urlRawString+"&production_enable=true");
            HttpURLConnection conn = (HttpURLConnection) urlPRODUCTION.openConnection();
            conn.setDoOutput(true);
            conn.setRequestMethod("GET");
            conn.setRequestProperty("Content-Type", "application/x-www-form-urlencoded");
            conn.setRequestProperty("charset", "utf-8");
            conn.setUseCaches(false);
            BufferedReader br = null;
            Log.e("SMSERR","SMS ALL DONE");
            if (100 <= conn.getResponseCode() && conn.getResponseCode() <= 399) {
                br = new BufferedReader(new InputStreamReader(conn.getInputStream()));
                String strCurrentLine;
                while ((strCurrentLine = br.readLine()) != null) {
                    System.out.println(strCurrentLine);
                }
            } else {
//                br = new BufferedReader(new InputStreamReader(conn.getErrorStream()));
            }
        } catch (MalformedURLException e) {
            Log.e("SMSERR","SMS ERROR"+e.getMessage());
            e.printStackTrace();
        } catch (IOException e) {
            Log.e("SMSERR","SMS ERROR IO "+e.getMessage());
            e.printStackTrace();
        }

        return "";
    }


}