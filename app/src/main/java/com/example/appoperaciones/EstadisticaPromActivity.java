package com.example.appoperaciones;

import androidx.appcompat.app.AppCompatActivity;
import androidx.recyclerview.widget.LinearLayoutManager;
import androidx.recyclerview.widget.RecyclerView;

import android.content.Intent;
import android.os.Bundle;
import android.view.MenuItem;
import android.widget.TextView;

import com.example.appoperaciones.Adaptadores.RecyclerEstadisticaProm;
import com.example.appoperaciones.Adaptadores.RecyclerEstadopAdapter;
import com.google.gson.Gson;
import com.google.gson.JsonObject;
import com.google.gson.reflect.TypeToken;

import org.jetbrains.annotations.NotNull;
import org.json.JSONArray;
import org.json.JSONException;
import org.json.JSONObject;

import java.lang.reflect.Type;
import java.util.ArrayList;

public class EstadisticaPromActivity extends AppCompatActivity {
    RecyclerView recyclerView;
    ArrayList<JsonObject> lista_datos = new ArrayList<>();
    private Type type = new TypeToken<ArrayList<JsonObject>>(){}.getType();

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_estadistica_prom);
        getSupportActionBar().setDisplayHomeAsUpEnabled(true);
        String datos =  getIntent().getStringExtra("datos");
        lista_datos = new Gson().fromJson(datos,type );
        recyclerView = findViewById(R.id.recyclerview);
        recyclerView.setLayoutManager(new LinearLayoutManager(this, LinearLayoutManager.VERTICAL,false));
        RecyclerEstadisticaProm recyclerEstadisticaProm= new RecyclerEstadisticaProm(lista_datos);
        recyclerView.setAdapter(recyclerEstadisticaProm);




    }


    @Override
    public boolean onOptionsItemSelected(@NotNull MenuItem item) {
        //definimos la accion al pulsar la fecla para ir hacia la actividad anterior.
        switch (item.getItemId()) {
            case android.R.id.home:
                finish();
                return true;

        }
        return super.onOptionsItemSelected(item);


    }
}