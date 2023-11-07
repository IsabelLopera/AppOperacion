package com.example.appoperaciones.Adaptadores;

import android.app.Dialog;
import android.content.Context;
import android.content.Intent;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.TextView;
import android.widget.Toast;

import androidx.cardview.widget.CardView;
import androidx.recyclerview.widget.RecyclerView;

import com.example.appoperaciones.EstadisticaPromActivity;
import com.example.appoperaciones.EstadoPedidoActivity;
import com.example.appoperaciones.Fragmentos.OperacionVentasFragment;
import com.example.appoperaciones.R;
import com.example.appoperaciones.ReportesOperacionActivity;
import com.example.appoperaciones.Servicios.ConsultarEstadosPedidoTienda;
import com.example.appoperaciones.Servicios.GenerarReporteOperacionVenta;
import com.example.appoperaciones.Servicios.GetTiendas;
import com.google.gson.Gson;
import com.google.gson.JsonObject;
import com.google.gson.reflect.TypeToken;

import org.jetbrains.annotations.NotNull;
import org.json.JSONArray;
import org.json.JSONException;
import org.json.JSONObject;

import java.lang.reflect.Type;
import java.util.ArrayList;
import java.util.concurrent.ExecutionException;

public class RecyclerTiendasAdapter extends RecyclerView.Adapter<RecyclerTiendasAdapter.ViewHolder> {
    ArrayList<JsonObject> lista;
    Context context;
    private Type type = new TypeToken<ArrayList<JsonObject>>(){}.getType();

    public RecyclerTiendasAdapter(ArrayList<JsonObject> lista,Context context) {
        this.lista =  lista;
        this.context =context;

    }


    @NotNull
    @Override
    public ViewHolder onCreateViewHolder(@NotNull ViewGroup parent, int viewType) {
        View view = LayoutInflater.from(parent.getContext()).inflate(R.layout.item_tiendas, null, false);
        return new ViewHolder(view);
    }

    @Override
    public void onBindViewHolder(@NotNull ViewHolder holder, int position) {
        try {
            JSONObject jsonObject = new JSONObject(lista.get(position).toString());
            holder.nombre_tienda.setText(jsonObject.getString("tienda"));
            holder.card_view.setOnClickListener(view -> {
                try {
                    String idtienda = jsonObject.getString("idtienda");
                    ConsultarEstadospedido(idtienda);
                } catch (JSONException e) {
                    e.printStackTrace();
                }


            });
        } catch (JSONException e) {
            e.printStackTrace();
        }

    }

    @Override
    public int getItemCount() {
        return lista.size();
    }

    public class ViewHolder extends RecyclerView.ViewHolder {

        TextView nombre_tienda;
        CardView  card_view;

        public ViewHolder(@NotNull View itemView) {
            super(itemView);

            nombre_tienda = itemView.findViewById(R.id.nombre_tienda);
            card_view =itemView.findViewById(R.id.card_view);


        }
    }


    private void ConsultarEstadospedido(String idtienda){
        try {
            ConsultarEstadosPedidoTienda service = new ConsultarEstadosPedidoTienda(context);
            Dialog dialog = new Dialog(context, R.style.CustomAlertDialog);
            dialog.setContentView(R.layout.custom_dialog);
            service.setAsyncTaskListener(new ReportesOperacionActivity.AsyncTaskListener() {
                @Override
                public void showProgressBar() {
                    if(!dialog.isShowing()){
                        dialog.show();
                    }

                }

                @Override
                public void CloseProgressBar() {
                    if(dialog.isShowing()){
                        dialog.dismiss();
                    }
                }

                @Override
                public void datos(String resp) {
                    ArrayList<JsonObject>  respuesta = new ArrayList<>();
                    if(isJSONArrayValid(resp)){
                        respuesta = new Gson().fromJson(resp,type );
                        if(respuesta != null){
                            if(respuesta.size() > 0){
                                Intent i = new Intent(context, EstadoPedidoActivity.class);
                                i.putExtra("datos", respuesta.toString());
                                i.putExtra("idtienda", idtienda);
                                context.startActivity(i);
                            }else{
                                Toast.makeText(context,"No se encontraron datos.",Toast.LENGTH_LONG).show();
                            }
                        }else{
                            Toast.makeText(context,"No se encontraron datos.",Toast.LENGTH_LONG).show();
                        }

                    } else {
                        Toast.makeText(context,"Error en el servicio",Toast.LENGTH_LONG).show();
                    }
                }
            });
            service.execute(idtienda);

        } catch (Exception e) {
            Toast.makeText(context,"Error: "+e.getMessage(),Toast.LENGTH_LONG).show();
        }

    }
    private boolean isJSONArrayValid(String jsonArrayString) {
        try {
            new JSONArray(jsonArrayString);

        } catch (JSONException e) {
            return false;
        }
        return true;
    }
}

