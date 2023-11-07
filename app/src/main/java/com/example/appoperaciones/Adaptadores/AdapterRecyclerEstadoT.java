package com.example.appoperaciones.Adaptadores;

import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.TextView;

import androidx.recyclerview.widget.RecyclerView;

import com.example.appoperaciones.R;
import com.google.gson.JsonObject;

import org.checkerframework.checker.nullness.qual.NonNull;
import org.jetbrains.annotations.NotNull;
import org.json.JSONException;
import org.json.JSONObject;

import java.util.ArrayList;

public class AdapterRecyclerEstadoT extends RecyclerView.Adapter<AdapterRecyclerEstadoT.ViewHolder> {

    ArrayList<JsonObject> lista;
    private TextView emptyTextView;

    @NotNull
    @Override
    public AdapterRecyclerEstadoT.ViewHolder onCreateViewHolder(@NotNull ViewGroup parent, int viewType) {
        View view = LayoutInflater.from(parent.getContext()).inflate(R.layout.item_estadotiendas, null, false);
        return new ViewHolder(view);
    }


    public AdapterRecyclerEstadoT(ArrayList<JsonObject> listaContactos,TextView emptyTextView) {
        this.lista = listaContactos;
        this.emptyTextView = emptyTextView;
    }

    @Override
    public void onBindViewHolder(@NotNull AdapterRecyclerEstadoT.ViewHolder holder, int position) {
        try {
            JSONObject jsonObject = new JSONObject(lista.get(position).toString());
            System.out.print(lista.get(position));
            holder.tienda.setText(jsonObject.getString("tienda"));
            holder.estado.setText(jsonObject.getString("estado"));
            holder.tiempopedido.setText(String.valueOf(jsonObject.getInt("tiempopedido")+" min."));
        } catch (JSONException e) {
            e.printStackTrace();
        }


    }


    public void updateEmptyView() {
        if (getItemCount() == 0) {
            emptyTextView.setVisibility(View.VISIBLE);
        } else {
            emptyTextView.setVisibility(View.GONE);
        }
    }

    @Override
    public int getItemCount() {
        return lista.size();
    }

    public class ViewHolder extends RecyclerView.ViewHolder {

        TextView tienda, estado, tiempopedido;

        public ViewHolder(@NonNull View itemView) {
            super(itemView);

            tienda = itemView.findViewById(R.id.tienda);
            estado = itemView.findViewById(R.id.estado);
            tiempopedido = itemView.findViewById(R.id.tiempopedido);
        }
    }
}