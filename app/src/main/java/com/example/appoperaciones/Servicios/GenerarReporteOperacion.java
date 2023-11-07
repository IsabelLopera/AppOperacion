package com.example.appoperaciones.Servicios;
import android.app.AlertDialog;
import android.app.ProgressDialog;
import android.content.Context;
import android.os.AsyncTask;

import com.example.appoperaciones.Fragmentos.ReporteGeneralFragment;
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


public class GenerarReporteOperacion extends AsyncTask<String, Void, String> {

    Context context;
    public GenerarReporteOperacion(Context context)
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
    protected String doInBackground(String... parametros) {
        String result = "";
        String connstr = "https://www.tiendapizzaamericana.co/ProyectoPizzaAmericana/GenerarReporteOperacion";
        HttpURLConnection http = null;
        try {
            URL url = new URL(connstr);
            http = (HttpURLConnection) url.openConnection();
            http.setRequestMethod("POST");
            http.setConnectTimeout(40000);
            http.setReadTimeout(40000);
            http.setDoInput(true);
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
            return result;

        } catch (MalformedURLException e) {
            result = "Error en servicio: URL Mal formada";
        } catch (IOException e) {
            result = "Error en servicio: "+e.toString();
        }catch (Exception e){
            result = "Error en servicio: "+e.toString();
        }finally {
            if (http != null) http.disconnect();
        }


        return result;
    }


    @Override
    protected void onPostExecute(String s) {
        super.onPostExecute(s);
        if (asyncTaskListener != null) {
            asyncTaskListener.datos(s);
            asyncTaskListener.CloseProgressBar();

        }
    }
}
