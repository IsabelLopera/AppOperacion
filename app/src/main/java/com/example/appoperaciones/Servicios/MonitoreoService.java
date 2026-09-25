package com.example.appoperaciones.Servicios;

import com.example.appoperaciones.Clases.MonitoreoTienda;
import com.google.gson.Gson;
import com.google.gson.reflect.TypeToken;

import java.io.IOException;
import java.lang.reflect.Type;
import java.util.List;

import okhttp3.Call;
import okhttp3.Callback;
import okhttp3.OkHttpClient;
import okhttp3.Request;
import okhttp3.Response;

public class MonitoreoService {

    // Esta URL apunta al servlet en tu servidor que actúa como proxy
    private static final String MONITOREO_URL = "https://tiendapizzaamericana.co/ProyectoPizzaAmericana/ObtenerMonitoreoEstadoPedido";

    private final OkHttpClient client = new OkHttpClient();
    private final Gson gson = new Gson();

    public interface CallbackMonitoreo {
        void onResult(List<MonitoreoTienda> monitoreos);
        void onError(Exception e);
    }

    public void obtenerMonitoreo(CallbackMonitoreo callback) {
        Request request = new Request.Builder().url(MONITOREO_URL).build();

        client.newCall(request).enqueue(new Callback() {
            @Override
            public void onResponse(Call call, Response response) throws IOException {
                if (!response.isSuccessful()) {
                    callback.onError(new IOException("Código no exitoso: " + response));
                    return;
                }

                String json = response.body().string();
                Type listType = new TypeToken<List<MonitoreoTienda>>() {}.getType();
                List<MonitoreoTienda> monitoreos = gson.fromJson(json, listType);
                callback.onResult(monitoreos);
            }

            @Override
            public void onFailure(Call call, IOException e) {
                callback.onError(e);
            }
        });
    }
}
