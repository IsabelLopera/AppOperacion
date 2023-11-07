package com.example.appoperaciones.Fragmentos;

import android.content.DialogInterface;
import android.os.Bundle;

import androidx.appcompat.app.AlertDialog;
import androidx.fragment.app.Fragment;

import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.AdapterView;
import android.widget.ArrayAdapter;
import android.widget.Button;
import android.widget.EditText;
import android.widget.LinearLayout;
import android.widget.Spinner;
import android.widget.Toast;

import com.example.appoperaciones.Clases.Tienda;
import com.example.appoperaciones.R;
import com.example.appoperaciones.Servicios.CRUDTiempoPedido;
import com.example.appoperaciones.Servicios.CRUDTiendaBloqueada;
import com.example.appoperaciones.Servicios.GetTiendas;
import com.google.gson.Gson;
import com.google.gson.JsonObject;
import com.google.gson.reflect.TypeToken;

import org.json.JSONArray;
import org.json.JSONException;
import org.json.JSONObject;

import java.lang.reflect.Type;
import java.util.ArrayList;
import java.util.Collections;
import java.util.concurrent.ExecutionException;

/**
 * A simple {@link Fragment} subclass.
 * Use the {@link BloqueoTiendaFragment#newInstance} factory method to
 * create an instance of this fragment.
 */
public class BloqueoTiendaFragment extends Fragment {

    // TODO: Rename parameter arguments, choose names that match
    // the fragment initialization parameters, e.g. ARG_ITEM_NUMBER
    private static final String ARG_PARAM1 = "param1";
    private static final String ARG_PARAM2 = "param2";

    // TODO: Rename and change types of parameters
    private String mParam1;
    private String mParam2;

    int value_idtienda = 0;
    String value_evento;
    public EditText observacion;
    LinearLayout opciones_bloq;
    String value_motivo = "";
    String value_Desbloqueo = "";
    String value_Observacion = "";
    Spinner spinner_tiendas;
    Spinner spinner_motivo;
    Spinner spinner_Desbloqueo;
    Spinner spinner_evento;
    public BloqueoTiendaFragment() {
        // Required empty public constructor
    }

    /**
     * Use this factory method to create a new instance of
     * this fragment using the provided parameters.
     *
     * @param param1 Parameter 1.
     * @param param2 Parameter 2.
     * @return A new instance of fragment BloqueoTiendaFragment.
     */
    // TODO: Rename and change types and number of parameters
    public static BloqueoTiendaFragment newInstance(String param1, String param2) {
        BloqueoTiendaFragment fragment = new BloqueoTiendaFragment();
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
        View view = inflater.inflate(R.layout.fragment_bloqueo_tienda, container, false);
        observacion=  view.findViewById(R.id.observacion);
        Button guardar_cambios=  view.findViewById(R.id.guardar);
        opciones_bloq = view.findViewById(R.id.opciones_bloq);
        spinner_evento = view.findViewById(R.id.tipoevento);
        spinner_tiendas = view.findViewById(R.id.tiendas);
        MostrarDatos(spinner_evento.getSelectedItem().toString());
        spinner_evento.setOnItemSelectedListener(new AdapterView.OnItemSelectedListener() {
            @Override
            public void onItemSelected(AdapterView<?> adapterView, View view, int i, long l) {
                String evento =adapterView.getSelectedItem().toString();
                if(evento.equals("Desbloquear")){
                    opciones_bloq.setVisibility(View.GONE);
                }else{
                    opciones_bloq.setVisibility(View.VISIBLE);
                }
                MostrarDatos(evento);
            }

            @Override
            public void onNothingSelected(AdapterView<?> adapterView) {

            }
        });


        spinner_motivo = view.findViewById(R.id.motivo);
        spinner_Desbloqueo = view.findViewById(R.id.Desbloqueo);
        spinner_Desbloqueo.setOnItemSelectedListener(new AdapterView.OnItemSelectedListener() {
            @Override
            public void onItemSelected(AdapterView<?> adapterView, View view, int i, long l) {
                value_Desbloqueo = adapterView.getSelectedItem().toString();
                switch (value_Desbloqueo){

                    case "15 minutos" :
                        value_Desbloqueo = "15";
                        break;
                    case "20 minutos" :
                        value_Desbloqueo = "20";
                        break;
                    case "30 minutos" :
                        value_Desbloqueo = "30";
                        break;
                }

            }

            @Override
            public void onNothingSelected(AdapterView<?> adapterView) {

            }
        });

        guardar_cambios.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {
                    value_evento = spinner_evento.getSelectedItem().toString();
                    AlertDialog.Builder builder = new AlertDialog.Builder(getContext());
                    builder.setTitle("Confirmar");
                    builder.setMessage("¿Esta seguro de realizar esta acción?");
                    builder.setPositiveButton("Aceptar", new DialogInterface.OnClickListener() {
                        @Override
                        public void onClick(DialogInterface dialogInterface, int i) {

                            try {
                                Tienda item = (Tienda )spinner_tiendas.getSelectedItem();
                                value_idtienda = item.getId();
                                String nombre_tienda = item.getNombre();
                                value_motivo = spinner_motivo.getSelectedItem().toString();
                                value_Observacion = observacion.getText().toString();
                                String idoperacion = "";
                                if (value_evento.equals("Bloquear")) {

                                    idoperacion = "1";
                                }else{
                                    idoperacion = "3";
                                }

                                String  resp = new CRUDTiendaBloqueada(getContext()).execute(idoperacion,String.valueOf(value_idtienda),value_Observacion,value_motivo,value_Desbloqueo).get();
                                if(isJSONArrayValid(resp)){
                                    String json = new JSONArray(resp).getJSONObject(0).getString("resultado");

                                    if(json.equals("exitoso")){
                                        Toast.makeText(getContext(),"Cambio exitoso en tienda "+nombre_tienda,Toast.LENGTH_LONG).show();
                                    }else{
                                        Toast.makeText(getContext(),"Problemas al realizar el cambio."+resp,Toast.LENGTH_LONG).show();
                                    }
                                } else {
                                    Toast.makeText(getContext(),"Error en el servicio",Toast.LENGTH_LONG).show();
                                }

                                observacion.setText("");
                                spinner_motivo.setSelection(0);
                                MostrarDatos(value_evento);
                            } catch (ExecutionException e) {
                                e.printStackTrace();
                            } catch (InterruptedException e) {
                                e.printStackTrace();
                            } catch (JSONException e) {
                                Toast.makeText(getContext(),"Error en el servicio",Toast.LENGTH_LONG).show();
                            }

                        }
                    });
                    builder.setNegativeButton("Cancelar",null);
                if (value_evento.equals("Bloquear")) {
                    if(!spinner_tiendas.getSelectedItem().toString().equals("Seleccionar") && !spinner_motivo.getSelectedItem().equals("Seleccionar")){
                        builder.show();

                    }else{
                        Toast.makeText(getContext(),"Debes seleccionar alguna de las opciones",Toast.LENGTH_LONG).show();
                    }

                }else{

                    if(!spinner_tiendas.getSelectedItem().toString().equals("Seleccionar")){
                        builder.show();
                    }else{
                            Toast.makeText(getContext(),"Debes seleccionar alguna de las opciones",Toast.LENGTH_LONG).show();
                    }
                }


            }
        });
        return view;
    }

    public ArrayList<Tienda> MostrarDatos(String evento){
        ArrayList<Tienda> lista = new ArrayList<>();

        try {
            lista.add(new Tienda(0,"Seleccionar"));
            String resp =new CRUDTiempoPedido(getContext()).execute().get();
            if(isJSONArrayValid(resp)){
                JSONArray lista_obtenida = new JSONArray(resp);
                for(int i = 0; lista_obtenida.length()> i; i++){
                    JSONObject jsonObject = lista_obtenida.getJSONObject(i);

                    if(evento.equals("Bloquear")){
                        if(jsonObject.getString("estado").equals("DISPONIBLE")){
                            lista.add(new Tienda(jsonObject.getInt("idtienda"),jsonObject.getString("tienda")));

                        }
                    }else{
                        if(jsonObject.getString("estado").equals("BLOQUEADA")){
                            lista.add(new Tienda(jsonObject.getInt("idtienda"),jsonObject.getString("tienda")));

                        }
                    }


                }

            }
            else {
                Toast.makeText(getContext(),"Error en el servicio",Toast.LENGTH_LONG).show();
            }

            ArrayAdapter<Tienda> dataAdapter = new ArrayAdapter<Tienda>(getContext(),android.R.layout.simple_spinner_item,lista);
            dataAdapter.setDropDownViewResource(android.R.layout.simple_spinner_dropdown_item);
            spinner_tiendas.setAdapter(dataAdapter);

        } catch (ExecutionException e) {
            e.printStackTrace();
        } catch (InterruptedException e) {
            e.printStackTrace();
        } catch (JSONException e) {
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


