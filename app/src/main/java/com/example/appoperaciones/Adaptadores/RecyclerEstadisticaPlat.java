package com.example.appoperaciones.Adaptadores;

import android.text.Html;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.TextView;

import androidx.recyclerview.widget.RecyclerView;

import com.example.appoperaciones.R;
import com.google.gson.JsonObject;

import org.json.JSONException;
import org.json.JSONObject;

import java.text.DecimalFormat;
import java.text.NumberFormat;
import java.util.ArrayList;

public class RecyclerEstadisticaPlat extends  RecyclerView.Adapter<RecyclerEstadisticaPlat.ViewHolder>{

    ArrayList<JSONObject> lista;
    public RecyclerEstadisticaPlat(ArrayList<JSONObject> lista) {

        this.lista = lista;
    }


    @Override
    public ViewHolder onCreateViewHolder(ViewGroup parent, int viewType) {
        View view = LayoutInflater.from(parent.getContext()).inflate(R.layout.item_estadisticaplat, null, false);
        return new ViewHolder(view);
    }

    @Override
    public void onBindViewHolder(ViewHolder holder, int position) {

        try {
            holder.fuente.setText(lista.get(position).getString("fuente"));
            NumberFormat numberFormat = NumberFormat.getNumberInstance();
            String TotalVenta = numberFormat.format(lista.get(position).getDouble("totalventa"));
            String CantidadPedidos = numberFormat.format(lista.get(position).getDouble("cantidadpedidos"));
            holder.totalventa.setText(Html.fromHtml("<b>Total de venta: </b>"+TotalVenta));
            holder.cantidadpedidos.setText(Html.fromHtml("<b>Cantidad de pedidos: </b>"+CantidadPedidos));

        } catch (JSONException e) {
            e.printStackTrace();
        }

    }

    @Override
    public int getItemCount() {
        return lista.size();
    }

    public class ViewHolder extends RecyclerView.ViewHolder {
        TextView fuente,totalventa,cantidadpedidos;
        public ViewHolder(View itemView) {
            super(itemView);
            fuente = itemView.findViewById(R.id.fuente);
            totalventa = itemView.findViewById(R.id.totalventa);
            cantidadpedidos = itemView.findViewById(R.id.cantidadpedidos);
        }
    }
}
