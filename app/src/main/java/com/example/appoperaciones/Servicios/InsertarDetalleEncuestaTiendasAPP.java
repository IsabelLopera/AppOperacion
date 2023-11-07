package com.example.appoperaciones.Servicios;

import android.app.Dialog;
import android.content.Context;
import android.os.AsyncTask;

import com.example.appoperaciones.EncuestaTiendaActivity;
import com.example.appoperaciones.R;
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

public class InsertarDetalleEncuestaTiendasAPP extends AsyncTask<String, Void, String> {

    Context context;
    String count="0";

    public InsertarDetalleEncuestaTiendasAPP(Context context)
    {
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
            if(!s.contains("Error en servicio")){
                s ="La encuesta se registro con éxito.";
            }
            asyncTaskListener.datos(count,s);
        }
    }

    @Override
    protected String doInBackground(String... strings) {
        String result = "";
        String idempleadoencuesta   =strings[0];
        String Idencuestadetalle  =strings[1];
        String valor  =strings[2];
        String observacionadi= strings[3];
        count  = strings[4];

        String connstr = "https://tiendapizzaamericana.co/ProyectoPizzaAmericana/InsertarDetalleEncuestaTiendasAPP";
        HttpURLConnection http = null;

        try {
            URL url = new URL(connstr);
            http = (HttpURLConnection) url.openConnection();
            http.setRequestMethod("POST");
            http.setDoInput(true);
            http.setDoOutput(true);

            OutputStream ops = http.getOutputStream();


            BufferedWriter writer = new BufferedWriter(new OutputStreamWriter(ops,"UTF-8"));
            String data = URLEncoder.encode("idempleadoencuesta","UTF-8")+"="+idempleadoencuesta+"&"+URLEncoder.encode("idencuestadetalle","UTF-8")+"="+URLEncoder.encode(Idencuestadetalle,"UTF-8")+"&"+URLEncoder.encode("valor","UTF-8")+"="+URLEncoder.encode(valor,"UTF-8")+"&"+URLEncoder.encode("observacionadi","UTF-8")+"="+URLEncoder.encode(observacionadi,"UTF-8");
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
