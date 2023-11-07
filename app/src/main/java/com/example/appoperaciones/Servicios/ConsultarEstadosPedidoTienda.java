package com.example.appoperaciones.Servicios;

import android.content.Context;
import android.os.AsyncTask;
import android.widget.Toast;

import com.example.appoperaciones.ReportesOperacionActivity;

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
import java.nio.charset.StandardCharsets;

public class ConsultarEstadosPedidoTienda  extends AsyncTask<String, Void, String> {



    Context context;
    public ConsultarEstadosPedidoTienda(Context context)
    {
        this.context = context;
    }

    private ReportesOperacionActivity.AsyncTaskListener asyncTaskListener;
    public void setAsyncTaskListener(ReportesOperacionActivity.AsyncTaskListener listener) {
        asyncTaskListener = listener;
    }

    @Override
    protected void onPreExecute() {
        if (asyncTaskListener != null) {
            asyncTaskListener.showProgressBar();
        }
    }
    @Override
    protected void onPostExecute(String s) {
        super.onPostExecute(s);
        if (asyncTaskListener != null) {
            asyncTaskListener.CloseProgressBar();
            asyncTaskListener.datos(s);


        }
    }

    @Override
    protected String doInBackground(String... parametros){
        String result = "";
        String idtienda = parametros[0];
        String connstr = "https://www.tiendapizzaamericana.co/ProyectoPizzaAmericana/ConsultarEstadosPedidoTienda";

        try {
            URL url = new URL(connstr);
            HttpURLConnection http = (HttpURLConnection) url.openConnection();
            http.setRequestMethod("POST");
            http.setDoOutput(true);
            http.setDoInput(true);

            // Configurar la conexión HttpURLConnection
            // ...
            OutputStream ops = http.getOutputStream();
            BufferedWriter writer = new BufferedWriter(new OutputStreamWriter(ops, "UTF-8"));
            String data = URLEncoder.encode("idtienda", "UTF-8") + "=" + URLEncoder.encode(idtienda, "UTF-8");
            writer.write(data);
            writer.flush();
            writer.close();
            ops.close();

            // Realizar la solicitud y obtener la respuesta
            int responseCode = http.getResponseCode();
            if (responseCode == HttpURLConnection.HTTP_OK) {
                InputStream ips = http.getInputStream();
                BufferedReader reader = new BufferedReader(new InputStreamReader(ips, StandardCharsets.ISO_8859_1));
                String line ="";
                while ((line = reader.readLine()) != null)
                {
                    result += line;
                }

                reader.close();
                ips.close();
            } else {
                result= "Error: No es posible la conexion con el servicio";
                // ...
            }

            http.disconnect();
        }catch (MalformedURLException e) {
            result = "Error de URL Mal formada";
        } catch (Exception e) {
            result = "Error: "+e.toString();
        }
        return result;

    }
}
