package com.example.appoperaciones.Fragmentos;

import android.os.Bundle;

import androidx.fragment.app.Fragment;

import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.webkit.WebView;
import android.widget.Toast;

import com.example.appoperaciones.R;
import com.example.appoperaciones.Servicios.GenerarReporteOperacionVenta;

import java.util.concurrent.ExecutionException;

/**
 * A simple {@link Fragment} subclass.
 * Use the {@link OperacionVentasFragment#newInstance} factory method to
 * create an instance of this fragment.
 */
public class OperacionVentasFragment extends Fragment {

    // TODO: Rename parameter arguments, choose names that match
    // the fragment initialization parameters, e.g. ARG_ITEM_NUMBER
    private static final String ARG_PARAM1 = "param1";
    private static final String ARG_PARAM2 = "param2";

    // TODO: Rename and change types of parameters
    private String mParam1;
    private String mParam2;

    public OperacionVentasFragment() {
        // Required empty public constructor
    }

    /**
     * Use this factory method to create a new instance of
     * this fragment using the provided parameters.
     *
     * @param param1 Parameter 1.
     * @param param2 Parameter 2.
     * @return A new instance of fragment OperacionVentasFragment.
     */
    // TODO: Rename and change types and number of parameters
    public static OperacionVentasFragment newInstance(String param1, String param2) {
        OperacionVentasFragment fragment = new OperacionVentasFragment();
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
        View view = inflater.inflate(R.layout.fragment_operacion_ventas, container, false);
        WebView webView = view.findViewById(R.id.resp);
        Bundle args = getArguments();
        if (args != null) {
            String data = args.getString("param1");
            webView.loadDataWithBaseURL(null,data,"text/html", "utf-8", null);
        }

        return view;
    }
}