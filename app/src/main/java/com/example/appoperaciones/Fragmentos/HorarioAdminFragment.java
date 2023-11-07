package com.example.appoperaciones.Fragmentos;

import android.os.Bundle;

import androidx.fragment.app.Fragment;
import androidx.recyclerview.widget.LinearLayoutManager;
import androidx.recyclerview.widget.RecyclerView;

import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.TextView;
import android.widget.Toast;

import com.example.appoperaciones.Adaptadores.RecyclerHorarioAdmin;
import com.example.appoperaciones.R;
import com.example.appoperaciones.Servicios.ObtenerHorariosAdministradoresDia;
import com.google.gson.Gson;
import com.google.gson.JsonObject;
import com.google.gson.reflect.TypeToken;

import org.json.JSONArray;
import org.json.JSONException;

import java.lang.reflect.Type;
import java.util.ArrayList;
import java.util.concurrent.ExecutionException;

/**
 * A simple {@link Fragment} subclass.
 * Use the {@link HorarioAdminFragment#newInstance} factory method to
 * create an instance of this fragment.
 */
public class HorarioAdminFragment extends Fragment {

    // TODO: Rename parameter arguments, choose names that match
    // the fragment initialization parameters, e.g. ARG_ITEM_NUMBER
    private static final String ARG_PARAM1 = "param1";
    private static final String ARG_PARAM2 = "param2";
    private Type type = new TypeToken<ArrayList<JsonObject>>(){}.getType();

    // TODO: Rename and change types of parameters
    private String mParam1;
    private String mParam2;
    RecyclerView recyclerView;

    public HorarioAdminFragment() {
        // Required empty public constructor
    }

    /**
     * Use this factory method to create a new instance of
     * this fragment using the provided parameters.
     *
     * @param param1 Parameter 1.
     * @param param2 Parameter 2.
     * @return A new instance of fragment HorarioAdminFragment.
     */
    // TODO: Rename and change types and number of parameters
    public static HorarioAdminFragment newInstance(String param1, String param2) {
        HorarioAdminFragment fragment = new HorarioAdminFragment();
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
        // Inflate the layout for this fragment

        // Inflate the layout for this fragment
        View  view =inflater.inflate(R.layout.fragment_horario_admin, container, false);
        recyclerView = view.findViewById(R.id.recyclerview);
        TextView textInfo =  view.findViewById(R.id.textInfo);
        recyclerView.setLayoutManager(new LinearLayoutManager(getContext(),LinearLayoutManager.VERTICAL,false));
        RecyclerHorarioAdmin recyclerHorarioAdmin= new RecyclerHorarioAdmin(MostrarDatos(),getContext(),textInfo);
        recyclerView.setAdapter(recyclerHorarioAdmin);
        recyclerHorarioAdmin.updateEmptyView();


        return  view;
    }


    public ArrayList<JsonObject> MostrarDatos(){
        ArrayList<JsonObject> lista = new ArrayList<>();
        try {
            String resp =new ObtenerHorariosAdministradoresDia(getContext()).execute().get();
            if(isJSONArrayValid(resp)){
                lista = new Gson().fromJson(resp,type );
            } else {
                Toast.makeText(getContext(),"Error en el servicio",Toast.LENGTH_LONG).show();
            }

        } catch (ExecutionException e) {
            e.printStackTrace();
        } catch (InterruptedException e) {
            e.printStackTrace();
        }
        return  lista;
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