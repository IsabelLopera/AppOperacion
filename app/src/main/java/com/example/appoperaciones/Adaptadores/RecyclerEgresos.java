package com.example.appoperaciones.Adaptadores;

import android.app.Activity;
import android.content.Context;
import android.content.DialogInterface;
import android.text.Html;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.TextView;
import android.widget.Toast;
import androidx.appcompat.app.AlertDialog;
import androidx.cardview.widget.CardView;
import androidx.recyclerview.widget.RecyclerView;

import com.example.appoperaciones.Clases.Tienda;
import com.example.appoperaciones.R;
import com.example.appoperaciones.Servicios.AprobarEgresoServicio;
import com.example.appoperaciones.Servicios.ObtenerEgresosServicio;
import com.google.gson.JsonObject;

import org.jetbrains.annotations.NotNull;
import org.json.JSONArray;
import org.json.JSONException;
import org.json.JSONObject;

import java.text.NumberFormat;
import java.util.ArrayList;
import java.util.Collection;
import java.util.concurrent.ExecutionException;

public class RecyclerEgresos extends  RecyclerView.Adapter<RecyclerEgresos.ViewHolder>{
    ArrayList<JSONObject> lista;
    Context context;
    int idtienda;
    String fecha;


    public RecyclerEgresos(ArrayList<JSONObject> lista, Context context,int idtienda,String fecha) {

        this.lista = lista;
        this.context = context;
        this.idtienda =idtienda;
        this.fecha =fecha;
    }


    @NotNull
    @Override
    public ViewHolder onCreateViewHolder(@NotNull ViewGroup parent, int viewType) {
        View view = LayoutInflater.from(parent.getContext()).inflate(R.layout.item_egresos, null, false);
        return new ViewHolder(view);
    }

    @Override
    public void onBindViewHolder(@NotNull ViewHolder holder, int position) {
        try {
            String aprobado =lista.get(position).getString("aprobado");
            if(aprobado.equals("N")){
                holder.aprobado.setText("No aprobado");
                holder.aprobado.setBackgroundColor(context.getResources().getColor(R.color.rojo));
            }else{
                holder.aprobado.setText("Aprobado");
                holder.aprobado.setBackgroundColor(context.getResources().getColor(R.color.verde));
            }
            holder.descripcion.setText(lista.get(position).getString("descripcion"));
            holder.fecha.setText(Html.fromHtml("<b>Fecha: </b>"+lista.get(position).getString("fecha")));
            int valorEgreso = lista.get(position).getInt("valoregreso");
            String valorEgresoFormateado = NumberFormat.getInstance().format(valorEgreso);
            String textoFormateado = "<b>Valor: </b>" + valorEgresoFormateado;
            holder.valoregreso.setText(Html.fromHtml(textoFormateado));
            holder.usuario.setText(Html.fromHtml("<b>Usuario: </b>"+lista.get(position).getString("usuario")));
            holder.tipoegreso.setText(Html.fromHtml("<b>Tipo: </b>"+lista.get(position).getString("tipoegreso")));

            holder.card_view.setOnClickListener(new View.OnClickListener() {
                @Override
                public void onClick(View view) {
                    try {

                        if(aprobado.equals("N")){
                            int idegreso = lista.get(position).getInt("idegreso");
                            createSimpleDialog(idegreso);
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
        TextView descripcion,fecha,valoregreso,usuario,tipoegreso,aprobado;
        CardView card_view;
        public ViewHolder( @NotNull View itemView) {
            super(itemView);
            descripcion = itemView.findViewById(R.id.descripcion);
            fecha = itemView.findViewById(R.id.fecha);
            valoregreso = itemView.findViewById(R.id.valoregreso);
            usuario = itemView.findViewById(R.id.usuario);
            tipoegreso = itemView.findViewById(R.id.tipoegreso);
            aprobado = itemView.findViewById(R.id.aprobado);
            card_view = itemView.findViewById(R.id.card_view);


        }
    }

    public void createSimpleDialog(int idegreso) {
        AlertDialog.Builder builder = new AlertDialog.Builder(context);

        builder.setTitle("Aprobacion")
                .setMessage("¿Desea realizar la aprobacion del egreso #" + idegreso + "?")
                .setPositiveButton("Aceptar",
                        (dialog, which) -> {
                            try {
                                String resp = new AprobarEgresoServicio(context).execute(String.valueOf(idegreso), String.valueOf(idtienda)).get();
                                if (isJSONObjectValid(resp)) {
                                    JSONObject respuesta = new JSONObject(resp);
                                    String json = respuesta.getString("resultado");
                                    if(json.equals("exitoso")){
                                        Toast.makeText(context,"Aprobacion Exitosa",Toast.LENGTH_LONG).show();
                                        String resp2 = new ObtenerEgresosServicio(context).execute(fecha, String.valueOf(idtienda)).get();
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

                                } else {
                                    Toast.makeText(context,"Error en el servicio",Toast.LENGTH_LONG).show();
                                }
                            }catch (JSONException |ExecutionException|InterruptedException e ) {

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
