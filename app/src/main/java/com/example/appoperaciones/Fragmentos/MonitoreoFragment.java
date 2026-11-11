package com.example.appoperaciones.Fragmentos;

import android.os.Bundle;

import androidx.fragment.app.Fragment;
import androidx.recyclerview.widget.GridLayoutManager;
import androidx.recyclerview.widget.RecyclerView;

import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.ProgressBar;
import android.widget.TextView;
import android.widget.Toast;
import androidx.swiperefreshlayout.widget.SwipeRefreshLayout;
import com.example.appoperaciones.Adaptadores.MonitoreoAdapter;
import com.example.appoperaciones.Clases.MonitoreoTienda;
import com.example.appoperaciones.R;
import com.example.appoperaciones.Servicios.MonitoreoService;

import java.util.ArrayList;
import java.util.List;

/**
 * A simple {@link Fragment} subclass.
 * Use the {@link MonitoreoFragment#newInstance} factory method to
 * create an instance of this fragment.
 */
public class MonitoreoFragment extends Fragment {

    // TODO: Rename parameter arguments, choose names that match
    // the fragment initialization parameters, e.g. ARG_ITEM_NUMBER
    private static final String ARG_PARAM1 = "param1";
    private static final String ARG_PARAM2 = "param2";
    private RecyclerView recyclerView;
    private MonitoreoAdapter adapter; // Tu adapter para mostrar los datos
    private List<MonitoreoTienda> listaMonitoreo = new ArrayList<>();
    // TODO: Rename and change types of parameters
    private String mParam1;
    private String mParam2;

    private TextView alertaCritica;

    public MonitoreoFragment() {
        // Required empty public constructor
    }

    /**
     * Use this factory method to create a new instance of
     * this fragment using the provided parameters.
     *
     * @param param1 Parameter 1.
     * @param param2 Parameter 2.
     * @return A new instance of fragment MonitoreoFragment.
     */
    // TODO: Rename and change types and number of parameters
    public static MonitoreoFragment newInstance(String param1, String param2) {
        MonitoreoFragment fragment = new MonitoreoFragment();
        Bundle args = new Bundle();
        args.putString(ARG_PARAM1, param1);
        args.putString(ARG_PARAM2, param2);
        fragment.setArguments(args);
        return fragment;
    }

    @Override
    public void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        if (getArguments() != null) {
            mParam1 = getArguments().getString(ARG_PARAM1);
            mParam2 = getArguments().getString(ARG_PARAM2);
        }
    }


    @Override
    public View onCreateView(LayoutInflater inflater, ViewGroup container,
                             Bundle savedInstanceState) {
        View view = inflater.inflate(R.layout.fragment_monitoreo, container, false);

        ProgressBar progressBar = view.findViewById(R.id.progressBar);
        progressBar.setVisibility(View.VISIBLE); // Mostrar al inicio
        alertaCritica = view.findViewById(R.id.textAlertaCritica);

        SwipeRefreshLayout swipeRefreshLayout = view.findViewById(R.id.swipeRefreshLayout);
        swipeRefreshLayout.setOnRefreshListener(() -> cargarDatos(swipeRefreshLayout, null));

        recyclerView = view.findViewById(R.id.recyclerMonitoreo);
        recyclerView.setLayoutManager(new GridLayoutManager(getContext(), 2));
        adapter = new MonitoreoAdapter(listaMonitoreo);
        recyclerView.setAdapter(adapter);

        // ✅ Cargar los datos por primera vez
        cargarDatos(null, progressBar);

        return view;
    }


    private void cargarDatos(SwipeRefreshLayout swipeRefreshLayout, ProgressBar progressBar) {
        MonitoreoService service = new MonitoreoService();
        service.obtenerMonitoreo(new MonitoreoService.CallbackMonitoreo() {
            @Override
            public void onResult(List<MonitoreoTienda> monitoreos) {
                requireActivity().runOnUiThread(() -> {
                    listaMonitoreo.clear();
                    listaMonitoreo.addAll(monitoreos);
                    adapter.notifyDataSetChanged();

                    if (hayAlertaCritica(monitoreos)) {
                        alertaCritica.setVisibility(View.VISIBLE);
                    } else {
                        alertaCritica.setVisibility(View.GONE);
                    }

                    if (swipeRefreshLayout != null) swipeRefreshLayout.setRefreshing(false);
                    if (progressBar != null) progressBar.setVisibility(View.GONE);
                });
            }

            @Override
            public void onError(Exception e) {
                requireActivity().runOnUiThread(() -> {
                    Toast.makeText(getContext(), "Error al obtener datos", Toast.LENGTH_SHORT).show();
                    if (swipeRefreshLayout != null) swipeRefreshLayout.setRefreshing(false);
                    if (progressBar != null) progressBar.setVisibility(View.GONE);
                });
            }
        });
    }

    private boolean hayAlertaCritica(List<MonitoreoTienda> tiendas) {
        for (MonitoreoTienda tienda : tiendas) {
            if (tienda.pedidospendientesalir > tienda.domiciliarios * 3) {
                return true;
            }
        }
        return false;
    }


}