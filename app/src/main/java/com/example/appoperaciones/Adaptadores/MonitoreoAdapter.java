package com.example.appoperaciones.Adaptadores;


import android.graphics.Color;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.TextView;

import androidx.annotation.NonNull;
import androidx.recyclerview.widget.RecyclerView;

import com.example.appoperaciones.Clases.MonitoreoTienda;
import com.example.appoperaciones.R;

import java.util.List;

public class MonitoreoAdapter extends RecyclerView.Adapter<MonitoreoAdapter.ViewHolder> {

    private final List<MonitoreoTienda> listaTiendas;

    public MonitoreoAdapter(List<MonitoreoTienda> listaTiendas) {
        this.listaTiendas = listaTiendas;
    }

    @NonNull
    @Override
    public MonitoreoAdapter.ViewHolder onCreateViewHolder(@NonNull ViewGroup parent, int viewType) {
        View view = LayoutInflater.from(parent.getContext())
                .inflate(R.layout.item_monitoreo, parent, false);
        return new ViewHolder(view);
    }

    @Override
    public void onBindViewHolder(@NonNull MonitoreoAdapter.ViewHolder holder, int position) {
        MonitoreoTienda item = listaTiendas.get(position);

        holder.textTienda.setText(item.tienda);
        holder.textPedidosCocina.setText("En cocina: " + item.pedidoscocina);
        holder.textPedidosPV.setText("Pedidos PV: " + item.pedidoscocinapv);
        holder.textPedidosDidi.setText("Pedidos Didi: " + item.pedidoscocinadidi);
        holder.textPedidosRappi.setText("Pedidos Rappi: " + item.pedidoscocinarappi);
        holder.textPedidosDomicilio.setText("Pedidos Domicilio: " + item.pedidoscocinadomicilio);
        holder.textPedidosRuta.setText("Pedidos en ruta: " + item.pedidosenruta);
        holder.textDomiciliarios.setText("Domiciliarios: " + item.domiciliarios);
        holder.textArtesanos.setText("\uD83E\uDDD1\u200D\uD83C\uDF73 Artesanos: " + item.artesanos);
        holder.textArtesanos.setShadowLayer(1.5f, 1, 1, Color.GRAY); // radio, dx, dy, color

        holder.textPendientes.setText("Pendientes a salir: " + item.pedidospendientesalir);

        if (item.pedidospendientesalir > item.domiciliarios * 3) {
            holder.textPendientes.setTextColor(Color.parseColor("#D32F2F"));
        } else {
            holder.textPendientes.setTextColor(Color.parseColor("#6F7272")); // más claro que el actual
        }

        // Mostrar mensaje de error si hay
        if (item.error) {
            holder.textError.setVisibility(View.VISIBLE);
        } else {
            holder.textError.setVisibility(View.GONE);
        }
    }

    @Override
    public int getItemCount() {
        return listaTiendas.size();
    }

    public static class ViewHolder extends RecyclerView.ViewHolder {
        TextView textTienda, textPedidosCocina, textPedidosPV, textPedidosDidi,
                textPedidosRappi, textPedidosDomicilio, textPedidosRuta,
                textPendientes, textDomiciliarios, textArtesanos, textError;

        public ViewHolder(@NonNull View itemView) {
            super(itemView);
            textTienda = itemView.findViewById(R.id.textTienda);
            textPedidosCocina = itemView.findViewById(R.id.textPedidosCocina);
            textPedidosPV = itemView.findViewById(R.id.textPedidosPV);
            textPedidosDidi = itemView.findViewById(R.id.textPedidosDidi);
            textPedidosRappi = itemView.findViewById(R.id.textPedidosRappi);
            textPedidosDomicilio = itemView.findViewById(R.id.textPedidosDomicilio);
            textPedidosRuta = itemView.findViewById(R.id.textPedidosRuta);
            textPendientes = itemView.findViewById(R.id.textPendientes);
            textDomiciliarios = itemView.findViewById(R.id.textDomiciliarios);
            textArtesanos = itemView.findViewById(R.id.textArtesanos);
            textError = itemView.findViewById(R.id.textError);
        }
    }
}
