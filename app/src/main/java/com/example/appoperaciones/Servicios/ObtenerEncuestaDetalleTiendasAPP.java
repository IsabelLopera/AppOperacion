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

public class ObtenerEncuestaDetalleTiendasAPP extends AsyncTask<String, Void, String> {

    Context context;
    public ObtenerEncuestaDetalleTiendasAPP(Context context)
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
    protected String doInBackground(String... parametros) {
        String result = "";
        String idencuesta  =parametros[0];
        String connstr = "https://www.tiendapizzaamericana.co/ProyectoPizzaAmericana/ObtenerEncuestaDetalleTiendasAPP";
        HttpURLConnection http = null;
        try {
            URL url = new URL(connstr);
            http = (HttpURLConnection) url.openConnection();
            http.setRequestMethod("POST");
            http.setDoInput(true);
            http.setDoOutput(true);
            OutputStream ops = http.getOutputStream();


            BufferedWriter writer = new BufferedWriter(new OutputStreamWriter(ops,"UTF-8"));
            String data = URLEncoder.encode("idencuesta","UTF-8")+"="+URLEncoder.encode(idencuesta,"UTF-8");
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


}
