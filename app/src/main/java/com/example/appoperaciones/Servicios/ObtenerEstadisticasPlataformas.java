package com.example.appoperaciones.Servicios;

import android.content.Context;
import android.os.AsyncTask;

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

public class ObtenerEstadisticasPlataformas extends AsyncTask<String, Void, String> {

    Context context;
    public ObtenerEstadisticasPlataformas(Context context)
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
    protected String doInBackground(String... strings) {
        String result = "";
        String fechainicial  =strings[0];
        String fechafinal  =strings[1];
        String connstr = "https://tiendapizzaamericana.co/ProyectoPizzaAmericana/ObtenerEstadisticasPlataformas";
        HttpURLConnection http = null;

        try {
            URL url = new URL(connstr);
            http = (HttpURLConnection) url.openConnection();
            http.setRequestMethod("POST");
            http.setDoInput(true);
            http.setDoOutput(true);

            OutputStream ops = http.getOutputStream();


            BufferedWriter writer = new BufferedWriter(new OutputStreamWriter(ops,"UTF-8"));
            String data = URLEncoder.encode("fechainicial","UTF-8")+"="+fechainicial+"&"+URLEncoder.encode("fechafinal","UTF-8")+"="+URLEncoder.encode(fechafinal,"UTF-8");
            writer.write(data);
            writer.flush();
            writer.close();
            ops.close();


            InputStream ips = http.getInputStream();
            BufferedReader reader = new BufferedReader(new InputStreamReader(ips,"ISO-8859-1"));
            String line ="";
            while ((line = reader.readLine()) != null)
            {
                result += line;
            }

            reader.close();
            ips.close();
            http.disconnect();

        } catch (MalformedURLException e) {
            result = "Error de URL Mal formada";
        } catch (IOException e) {
            result = "Error: "+e.toString();
        }catch (Exception e){
            result = "Error: "+e.toString();
        }finally {
            if (http != null) http.disconnect();


        }
        return result;
    }
}
