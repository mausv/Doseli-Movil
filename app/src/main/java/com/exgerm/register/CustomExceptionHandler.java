package com.exgerm.register;

import android.content.Context;
import android.content.SharedPreferences;
import android.os.AsyncTask;
import android.text.format.Time;
import android.util.Log;

import org.apache.http.NameValuePair;
import org.apache.http.client.entity.UrlEncodedFormEntity;
import org.apache.http.client.methods.HttpPost;
import org.apache.http.impl.client.DefaultHttpClient;
import org.apache.http.message.BasicNameValuePair;
import org.apache.http.protocol.HTTP;
import org.joda.time.LocalDate;
import org.joda.time.LocalDateTime;
import org.joda.time.format.DateTimeFormat;
import org.joda.time.format.DateTimeFormatter;

import java.io.BufferedWriter;
import java.io.FileWriter;
import java.io.IOException;
import java.io.PrintWriter;
import java.io.StringWriter;
import java.io.Writer;
import java.sql.Timestamp;
import java.util.ArrayList;
import java.util.Calendar;
import java.util.List;

/**
 * Created by mausv on 2/23/2016.
 */
public class CustomExceptionHandler implements Thread.UncaughtExceptionHandler {

    private Thread.UncaughtExceptionHandler defaultUEH;

    private String localPath;

    private String url;

    private String filename;
    private String stacktrace;

    private Context contextClass;

    /*
     * if any of the parameters is null, the respective functionality
     * will not be used
     */
    public CustomExceptionHandler(String localPath, String url, Context context) {
        this.localPath = localPath;
        this.url = url;
        this.defaultUEH = Thread.getDefaultUncaughtExceptionHandler();
        this.contextClass = context;
    }

    public void uncaughtException(Thread t, Throwable e) {
        LocalDateTime date = new LocalDateTime();
        DateTimeFormatter dateFormat = DateTimeFormat
                .forPattern("dd-MM-yyyy HH-mm-ss");
        String timestamp = String.valueOf(dateFormat.print(date));
        final Writer result = new StringWriter();
        final PrintWriter printWriter = new PrintWriter(result);
        e.printStackTrace(printWriter);
        stacktrace = result.toString();
        printWriter.close();
        filename = "report"+ timestamp + ".stacktrace";


        if (localPath != null) {
            writeToFile(stacktrace, filename);
            writeToSharedPrefs(filename);
            System.out.println("Name: " + filename);
        }
        /*if (url != null) {
            Log.d("SendToServer", url);
            Log.d("SendToServer", stacktrace);
            new SendToServer().execute();
        }*/

        defaultUEH.uncaughtException(t, e);
    }

    private void writeToSharedPrefs(String filename) {

        SharedPreferences pref = contextClass.getSharedPreferences("DoseliCrash", Context.MODE_PRIVATE);
        SharedPreferences.Editor editor = pref.edit();
        editor.putBoolean("crash", true);
        editor.putString("filename", filename);
        editor.apply();
    }

    private void writeToFile(String stacktrace, String filename) {
        try {
            BufferedWriter bos = new BufferedWriter(new FileWriter(
                    localPath + "/" + filename));
            bos.write(stacktrace);
            bos.flush();
            bos.close();
        } catch (Exception e) {
            e.printStackTrace();
        }
    }

    public void sendReport(String filename, String stacktrace) {
        this.filename = filename;
        this.stacktrace = stacktrace;
        new SendToServer().execute();
    }

    class SendToServer extends AsyncTask<Void, Void, Void> {
        @Override
        protected Void doInBackground(Void... params) {
            DefaultHttpClient httpClient = new DefaultHttpClient();
            HttpPost httpPost = new HttpPost(url);
            List<NameValuePair> nvps = new ArrayList<NameValuePair>();
            nvps.add(new BasicNameValuePair("filename", filename));
            nvps.add(new BasicNameValuePair("stacktrace", stacktrace));
            try {
                httpPost.setEntity(
                        new UrlEncodedFormEntity(nvps, HTTP.UTF_8));
                httpClient.execute(httpPost);
            } catch (IOException e) {
                e.printStackTrace();
            }
            SharedPreferences pref = contextClass.getSharedPreferences("DoseliCrash", Context.MODE_PRIVATE);
            SharedPreferences.Editor editor = pref.edit();
            editor.putBoolean("crash", false);
            editor.apply();
            return null;
        }
    }
}