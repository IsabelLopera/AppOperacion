package com.example.appoperaciones;


import androidx.appcompat.app.AppCompatActivity;
import androidx.recyclerview.widget.GridLayoutManager;
import androidx.recyclerview.widget.LinearLayoutManager;
import androidx.recyclerview.widget.RecyclerView;
import androidx.swiperefreshlayout.widget.SwipeRefreshLayout;

import android.app.Dialog;
import android.content.Intent;
import android.os.Bundle;
import android.view.Menu;
import android.view.MenuItem;
import android.view.View;
import android.widget.LinearLayout;
import android.widget.TextView;
import android.widget.Toast;

import com.example.appoperaciones.Adaptadores.RecyclerEstadopAdapter;
import com.example.appoperaciones.Servicios.CRUDTiempoPedido;
import com.example.appoperaciones.Servicios.ConsultarEstadosPedidoTienda;
import com.google.gson.Gson;
import com.google.gson.JsonObject;
import com.google.gson.reflect.TypeToken;

import org.jetbrains.annotations.NotNull;
import org.json.JSONArray;
import org.json.JSONException;

import java.lang.reflect.Type;
import java.util.ArrayList;
import java.util.concurrent.ExecutionException;

public class EstadoPedidoActivity extends AppCompatActivity {
    RecyclerView recyclerView;
    ArrayList<JsonObject> lista_datos = new ArrayList<>();
    private Type type = new TypeToken<ArrayList<JsonObject>>(){}.getType();
    private SwipeRefreshLayout swipeRefreshLayout;
    String idtienda;
    RecyclerEstadopAdapter recyclerEstadopAdapter;
    private int estadoActual = Estado.VISTA_UNO;
    private LinearLayout columns ;


    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_estado_pedido);
        getSupportActionBar().setDisplayHomeAsUpEnabled(true);
        String datos =  getIntent().getStringExtra("datos");
        idtienda =  getIntent().getStringExtra("idtienda");
        lista_datos = new Gson().fromJson(datos,type );
        swipeRefreshLayout = findViewById(R.id.swipeRefreshLayout);
        columns =  findViewById(R.id.columns);
        recyclerView = findViewById(R.id.recyclerview);
        recyclerView.setLayoutManager(new LinearLayoutManager(this, LinearLayoutManager.VERTICAL,false));
        recyclerEstadopAdapter= new RecyclerEstadopAdapter(lista_datos);
        recyclerView.setAdapter(recyclerEstadopAdapter);

        swipeRefreshLayout.setOnRefreshListener(new SwipeRefreshLayout.OnRefreshListener() {
            @Override
            public void onRefresh() {
                // Aquí puedes realizar la lógica de actualización de datos, como cargar nuevos datos desde una fuente externa
                // Luego, una vez que los nuevos datos estén disponibles, llama al método actualizarDatos() en tu adaptador y detén la animación de actualización
                actualizarDatos();
            }
        });


    }


    private void actualizarDatos() {
        try {
            ConsultarEstadosPedidoTienda service = new ConsultarEstadosPedidoTienda(this);
            service.setAsyncTaskListener(new ReportesOperacionActivity.AsyncTaskListener() {
                @Override
                public void showProgressBar() {

                }

                @Override
                public void CloseProgressBar() {
                }

                @Override
                public void datos(String resp) {
                    ArrayList<JsonObject>  respuesta = new ArrayList<>();
                    if(isJSONArrayValid(resp)){
                        respuesta = new Gson().fromJson(resp,type );
                        if(respuesta != null){
                                recyclerEstadopAdapter.actualizarDatos(respuesta);
                                // Finalmente, detienes la animación de actualización
                                swipeRefreshLayout.setRefreshing(false);
                        }else{
                            Toast.makeText(getApplicationContext(),"No se encontraron datos.",Toast.LENGTH_LONG).show();
                        }

                    } else {
                        Toast.makeText(getApplicationContext(),"Error en el servicio",Toast.LENGTH_LONG).show();
                    }
                }
            });
            service.execute(idtienda);

        } catch (Exception e) {
            Toast.makeText(getApplicationContext(),"Error: "+e.getMessage(),Toast.LENGTH_LONG).show();
        }
        // Aquí obtienes los nuevos datos y actualizas tu RecyclerView

        // Luego, una vez que los nuevos datos estén disponibles, llamas al método actualizarDatos() en tu adaptador

    }

    @Override
    public boolean onCreateOptionsMenu(Menu menu) {
        getMenuInflater().inflate(R.menu.menu_estpedido, menu);
        return true;
    }

    @Override
    public boolean onOptionsItemSelected(@NotNull MenuItem item) {

        switch (item.getItemId()) {
            case R.id.detalle_pedido:
                cambiarPresentacion(item);
                return true;
            case android.R.id.home:
                finish(); // Acción al pulsar la flecha para ir hacia la actividad anterior
                return true;
            default:
                return super.onOptionsItemSelected(item);
        }

    }

    private void cambiarPresentacion(MenuItem item) {
        if (estadoActual == Estado.VISTA_UNO) {
            estadoActual = Estado.VISTA_DOS;
            item.setTitle("Ocultar detalles.");
            columns.setVisibility(View.GONE);
        } else {
            estadoActual = Estado.VISTA_UNO;
            item.setTitle("Mas detalles.");
            columns.setVisibility(View.VISIBLE);
        }

        recyclerEstadopAdapter.setEstadoActual(estadoActual);
    }

    private boolean isJSONArrayValid(String jsonArrayString) {
        try {
            new JSONArray(jsonArrayString);

        } catch (JSONException e) {
            return false;
        }
        return true;
    }

    public class Estado {
        public static final int VISTA_UNO = 1;
        public static final int VISTA_DOS = 2;
    }

}

