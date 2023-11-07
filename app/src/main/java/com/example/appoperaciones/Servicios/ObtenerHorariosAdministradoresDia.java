package com.example.appoperaciones.Servicios;

import android.content.Context;
import android.os.AsyncTask;

import java.io.BufferedReader;
import java.io.IOException;
import java.io.InputStream;
import java.io.InputStreamReader;
import java.net.HttpURLConnection;
import java.net.MalformedURLException;
import java.net.URL;

public class ObtenerHorariosAdministradoresDia  extends AsyncTask<String, Void, String> {

    Context context;
    public ObtenerHorariosAdministradoresDia(Context context)
    {
        this.context = context;
    }


    @Override
    protected String doInBackground(String... parametros) {
        String result = "";
        String connstr = "https://www.tiendapizzaamericana.co/ProyectoPizzaAmericana/ObtenerHorariosAdministradoresDia";
        HttpURLConnection http = null;
        try {
            URL url = new URL(connstr);
            http = (HttpURLConnection) url.openConnection();
            http.setRequestMethod("POST");
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
