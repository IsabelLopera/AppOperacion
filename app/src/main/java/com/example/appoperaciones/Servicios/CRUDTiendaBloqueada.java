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

public class CRUDTiendaBloqueada extends AsyncTask<String, Void, String> {

    Context context;
    public CRUDTiendaBloqueada(Context context)
    {
        this.context = context;
    }


    @Override
    protected String doInBackground(String... parametros) {
        String result = "";
        String idoperacion  =parametros[0];
        String idtienda  =parametros[1];
        String observacion  =parametros[2];
        String motivo  =parametros[3];
        String desbloqueoen  =parametros[4];

        String connstr = "https://tiendapizzaamericana.co/ProyectoPizzaAmericana/CRUDTiendaBloqueada";
        HttpURLConnection http = null;

        try {
            URL url = new URL(connstr);
            http = (HttpURLConnection) url.openConnection();
            http.setRequestMethod("POST");
            http.setDoInput(true);
            http.setDoOutput(true);

            OutputStream ops = http.getOutputStream();


            BufferedWriter writer = new BufferedWriter(new OutputStreamWriter(ops,"UTF-8"));
            String data ="";
       /*     if(idoperacion.equals("1")){
                data = "idoperacion="+idoperacion+"&"+"idtienda="+idtienda+"&observacion="+observacion+"&motivo="+motivo+"&desbloqueoen="+desbloqueoen;
            }else{
                data = "idoperacion="+idoperacion+"&"+"idtienda="+idtienda;
            }
*/
           if(idoperacion.equals("1")){
                data = URLEncoder.encode("idoperacion","UTF-8")+"="+idoperacion+"&"+URLEncoder.encode("idtienda","UTF-8")+"="+URLEncoder.encode(idtienda,"UTF-8")+"&"+URLEncoder.encode("observacion","UTF-8")+"="+URLEncoder.encode(observacion,"UTF-8")+"&"+URLEncoder.encode("motivo","UTF-8")+"="+motivo+"&"+URLEncoder.encode("desbloqueoen","UTF-8")+"="+URLEncoder.encode(desbloqueoen,"UTF-8");
            }else{
                data = URLEncoder.encode("idoperacion","UTF-8")+"="+idoperacion+"&"+URLEncoder.encode("idtienda","UTF-8")+"="+URLEncoder.encode(idtienda,"UTF-8");
            }

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
