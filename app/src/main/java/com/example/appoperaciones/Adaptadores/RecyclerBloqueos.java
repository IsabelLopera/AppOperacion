package com.example.appoperaciones.Adaptadores;

import android.content.Context;
import android.text.Html;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.TextView;
import android.widget.Toast;


import androidx.appcompat.app.AlertDialog;
import androidx.cardview.widget.CardView;
import androidx.recyclerview.widget.RecyclerView;

import com.example.appoperaciones.R;
import com.example.appoperaciones.Servicios.AprobarBloqueoTienda;
import com.example.appoperaciones.Servicios.AprobarEgresoServicio;
import com.example.appoperaciones.Servicios.ConsultarBloqueosTienda;
import com.example.appoperaciones.Servicios.ObtenerEgresosServicio;

import org.jetbrains.annotations.NotNull;
import org.json.JSONArray;
import org.json.JSONException;
import org.json.JSONObject;

import java.util.ArrayList;
import java.util.concurrent.ExecutionException;

public class RecyclerBloqueos extends  RecyclerView.Adapter<RecyclerBloqueos.ViewHolder>{
    ArrayList<JSONObject> lista;
    Context context;
    int idtienda;
    String fecha;

    public RecyclerBloqueos(ArrayList<JSONObject> lista, Context context,int idtienda,String fecha) {

        this.lista = lista;
        this.context = context;
        this.idtienda =idtienda;
        this.fecha =fecha;
    }


    @NotNull
    @Override
    public RecyclerBloqueos.ViewHolder onCreateViewHolder(@NotNull ViewGroup parent, int viewType) {
        View view = LayoutInflater.from(parent.getContext()).inflate(R.layout.item_bloqueos, null, false);
        return new ViewHolder(view);
    }

    @Override
    public void onBindViewHolder(@NotNull RecyclerBloqueos.ViewHolder holder, int position) {
        try {
            String aprobado =lista.get(position).getString("aprobado");
            if(aprobado.equals("N")){
                holder.aprobado.setText("No aprobado");
                holder.aprobado.setBackgroundColor(context.getResources().getColor(R.color.rojo));
            }else{
                holder.aprobado.setText("Aprobado");
                holder.aprobado.setBackgroundColor(context.getResources().getColor(R.color.verde));
            }
            holder.motivo.setText(lista.get(position).getString("motivo"));
            holder.accion.setText(Html.fromHtml("<b>Accion: </b>"+lista.get(position).getString("accion")));
            holder.tienda.setText(Html.fromHtml("<b>Tienda: </b>"+lista.get(position).getString("tienda")));
            holder.fecha_accion.setText(Html.fromHtml("<b>Fecha y hora: </b>"+lista.get(position).getString("fecha_accion")));
            holder.observacion.setText(Html.fromHtml("<b>Observacion: </b>"+lista.get(position).getString("observacion")));

            holder.card_view.setOnClickListener(new View.OnClickListener() {
                @Override
                public void onClick(View view) {
                    try {

                        if(aprobado.equals("N")){
                            int idlogbloqueo = lista.get(position).getInt("idlogbloqueo");

                            createSimpleDialog(idlogbloqueo);
                        }
                    } catch (JSONException e) {
                        e.printStackTrace();
                    }

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
        TextView accion,tienda,motivo,fecha_accion,observacion,aprobado;
        CardView card_view;
        public ViewHolder(@NotNull View itemView) {
            super(itemView);
            accion = itemView.findViewById(R.id.accion);
            tienda = itemView.findViewById(R.id.tienda);
            motivo = itemView.findViewById(R.id.motivo);
            fecha_accion = itemView.findViewById(R.id.fecha_accion);
            observacion = itemView.findViewById(R.id.observacion);
            aprobado = itemView.findViewById(R.id.aprobado);
            card_view = itemView.findViewById(R.id.card_view);

        }
    }

    public void createSimpleDialog(int idlogbloqueo) {
        AlertDialog.Builder builder = new AlertDialog.Builder(context);

        builder.setTitle("Aprobacion")
                .setMessage("¿Desea realizar la aprobacion del bloqueo #" + idlogbloqueo + "?")
                .setPositiveButton("Aceptar",
                        (dialog, which) -> {
                            try {
                                String resp = new AprobarBloqueoTienda(context).execute(String.valueOf(idlogbloqueo)).get();
                                if(isJSONObjectValid(resp)){
                                    JSONObject respuesta = new JSONObject(resp);
                                    String json = respuesta.getString("resultado");
                                    if(json.equals("true")){
                                        Toast.makeText(context,"Aprobacion Exitosa",Toast.LENGTH_LONG).show();
                                        String resp2 = new ConsultarBloqueosTienda(context).execute(fecha, String.valueOf(idtienda)).get();
                                        if(isJSONArrayValid(resp2)){
                                            JSONArray jsonArray =  new JSONArray(resp2);
                                            ArrayList<JSONObject> nueva_lista= new ArrayList<>();
                                            for(int i=0;jsonArray.length()> i;i++){
                                                nueva_lista.add(jsonArray.getJSONObject(i));
                                            }
                                            lista = nueva_lista;
                                            notifyDataSetChanged();

                                        } else {
                                            Toast.makeText(context,"Error en el servicio al recargar la lista",Toast.LENGTH_LONG).show();
                                        }
                                    }else{

                                        Toast.makeText(context,"No se puedo realizar la accion.",Toast.LENGTH_LONG).show();
                                    }
                                }else{

                                    Toast.makeText(context,"Error en el servicio",Toast.LENGTH_LONG).show();
                                }

                            } catch (JSONException |ExecutionException|InterruptedException e ) {

                                Toast.makeText(context,"Error con el servicio",Toast.LENGTH_LONG).show();
                            }

                        })
                .setNegativeButton("Cancelar", null);

        builder.create();
        builder.show();
    }


    private boolean isJSONObjectValid(String jsonObjectString) {
        try {
             new JSONObject(jsonObjectString);

        } catch (JSONException e) {
            return false;
        }
        return true;
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
