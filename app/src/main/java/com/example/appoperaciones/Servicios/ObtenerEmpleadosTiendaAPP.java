package com.example.appoperaciones.Servicios;

import android.content.Context;
import android.os.AsyncTask;

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

public class ObtenerEmpleadosTiendaAPP extends AsyncTask<String, Void, String> {

    private Context context;
    private OnEmpleadosObtenidosListener listener;

    public interface OnEmpleadosObtenidosListener {
        void onResultado(String result);
    }

    public ObtenerEmpleadosTiendaAPP(Context context, OnEmpleadosObtenidosListener listener) {
        this.context = context;
        this.listener = listener;
    }

    @Override
    protected void onPostExecute(String result) {
        super.onPostExecute(result);
        if (listener != null) {
            listener.onResultado(result);
        }
    }

    @Override
    protected String doInBackground(String... params) {
        String result = "";
        String idtienda = params.length > 0 ? params[0] : "";
        String connstr = "https://tiendapizzaamericana.co/ProyectoPizzaAmericana/ObtenerEmpleadosTiendaAPP";
        HttpURLConnection http = null;
        try {
            URL url = new URL(connstr);
            http = (HttpURLConnection) url.openConnection();
            http.setRequestMethod("POST");
            http.setDoInput(true);
            http.setDoOutput(true);
            http.setConnectTimeout(8000);
            http.setReadTimeout(8000);

            OutputStream ops = http.getOutputStream();
            BufferedWriter writer = new BufferedWriter(new OutputStreamWriter(ops, "UTF-8"));
            String data = URLEncoder.encode("idtienda", "UTF-8") + "=" + URLEncoder.encode(idtienda, "UTF-8");
            writer.write(data);
            writer.flush();
            writer.close();
            ops.close();

            InputStream ips = http.getInputStream();
            BufferedReader reader = new BufferedReader(new InputStreamReader(ips, "ISO-8859-1"));
            StringBuilder sb = new StringBuilder();
            String line;
            while ((line = reader.readLine()) != null) {
                sb.append(line);
            }
            result = sb.toString();
            reader.close();
            ips.close();
        } catch (MalformedURLException e) {
            result = "Error: URL mal formada";
        } catch (IOException e) {
            result = "Error: " + e.toString();
        } catch (Exception e) {
            result = "Error: " + e.toString();
        } finally {
            if (http != null) {
                http.disconnect();
            }
        }
        return result;
    }
}

