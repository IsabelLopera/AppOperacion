package com.example.appoperaciones.Servicios;

import android.app.Dialog;
import android.content.Context;
import android.os.AsyncTask;

import com.example.appoperaciones.EncuestaTiendaActivity;
import com.example.appoperaciones.R;
import com.example.appoperaciones.ReportesOperacionActivity;

import org.json.JSONException;
import org.json.JSONObject;

import java.io.BufferedReader;
import java.io.BufferedWriter;
import java.io.IOException;
import java.io.InputStream;
import java.io.InputStreamReader;
import java.io.OutputStream;
import java.io.OutputStreamWriter;
import java.net.HttpURLConnection;
import java.net.MalformedURLException;
import java.net.URL;
import java.net.URLEncoder;

public class InsertarDetalleEncuestaTiendasAPP extends AsyncTask<String, Void, String> {

    Context context;


    public InsertarDetalleEncuestaTiendasAPP(Context context) {
        this.context = context;
    }

    private EncuestaTiendaActivity.AsyncTaskListener asyncTaskListener;

    public void setAsyncTaskListener(EncuestaTiendaActivity.AsyncTaskListener listener) {
        asyncTaskListener = listener;
    }

    @Override
    protected void onPostExecute(String s) {
        super.onPostExecute(s);

        if (asyncTaskListener != null) {
            if (!s.contains("Error en servicio")) {

                try {
                    JSONObject ob = new JSONObject(s);
                    if (ob.getBoolean("success")) {
                        s = "La encuesta se registró con éxito.";
                    } else if (ob.has("failed_count")) {
                        s = "Registro incompleto. Se presentó un problema al intentar insertar " +
                                ob.getInt("failed_count") + " de las respuestas.";
                    } else {
                        s = "Algo falló mientras se intentaba registrar las respuestas.";
                    }

                } catch (JSONException e) {
                    s = "Ocurrio un problema con la respuesta del servicio, verifique si la encuesta fue registrada correctamente.";
                }

            }
            asyncTaskListener.datos(s);
        }
    }

    @Override
    protected String doInBackground(String... strings) {
        String result = "";
        String lista = strings[0];


        String connstr = "https://www.tiendapizzaamericana.co/ProyectoPizzaAmericana/InsertarDetalleEncuestaTiendasAPP";
        HttpURLConnection http = null;

        try {
            URL url = new URL(connstr);
            http = (HttpURLConnection) url.openConnection();
            http.setRequestMethod("POST");
            http.setRequestProperty("Content-Type", "application/json; charset=UTF-8");
            http.setDoInput(true);
            http.setDoOutput(true);

            // Enviar el JSON
            OutputStream os = http.getOutputStream();
            BufferedWriter writer = new BufferedWriter(new OutputStreamWriter(os, "UTF-8"));
            writer.write(lista);
            writer.flush();
            writer.close();
            os.close();

            // Leer la respuesta
            InputStream is = http.getInputStream();
            BufferedReader reader = new BufferedReader(new InputStreamReader(is, "UTF-8"));
            String line;
            while ((line = reader.readLine()) != null) {
                result += line;
            }
            reader.close();
            is.close();

        } catch (Exception e) {
            result = "Error en servicio: " + e.toString();
        } finally {
            if (http != null) http.disconnect();
        }
        return result;
    }
}