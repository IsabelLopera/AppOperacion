package com.example.appoperaciones.Fragmentos;

import android.app.DatePickerDialog;
import android.os.Bundle;

import androidx.fragment.app.Fragment;
import androidx.recyclerview.widget.LinearLayoutManager;
import androidx.recyclerview.widget.RecyclerView;

import android.text.InputType;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.ArrayAdapter;
import android.widget.Button;
import android.widget.DatePicker;
import android.widget.EditText;
import android.widget.Spinner;
import android.widget.TextView;
import android.widget.Toast;

import com.example.appoperaciones.Adaptadores.RecyclerEgresos;
import com.example.appoperaciones.Adaptadores.RecyclerHorarioAdmin;
import com.example.appoperaciones.Clases.Tienda;
import com.example.appoperaciones.R;
import com.example.appoperaciones.Servicios.CRUDTiempoPedido;
import com.example.appoperaciones.Servicios.ObtenerEgresosServicio;

import org.json.JSONArray;
import org.json.JSONException;
import org.json.JSONObject;

import java.util.ArrayList;
import java.util.Calendar;
import java.util.Locale;
import java.util.concurrent.ExecutionException;

/**
 * A simple {@link Fragment} subclass.
 * Use the {@link EgresosFragment#newInstance} factory method to
 * create an instance of this fragment.
 */
public class EgresosFragment extends Fragment {

    // TODO: Rename parameter arguments, choose names that match
    // the fragment initialization parameters, e.g. ARG_ITEM_NUMBER
    private static final String ARG_PARAM1 = "param1";
    private static final String ARG_PARAM2 = "param2";

    // TODO: Rename and change types of parameters
    private String mParam1;
    private String mParam2;
    Spinner spinner_tiendas;
    EditText fecha;
    RecyclerView recyclerView;

    public EgresosFragment() {
        // Required empty public constructor
    }

    /**
     * Use this factory method to create a new instance of
     * this fragment using the provided parameters.
     *
     * @param param1 Parameter 1.
     * @param param2 Parameter 2.
     * @return A new instance of fragment ConsultarEgresosFragment.
     */
    // TODO: Rename and change types and number of parameters
    public static EgresosFragment newInstance(String param1, String param2) {
        EgresosFragment fragment = new EgresosFragment();
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
        View view =  inflater.inflate(R.layout.fragment_egresos, container, false);
        final Calendar calendario = Calendar.getInstance();
        int año = calendario.get(Calendar.YEAR);
        int mes = calendario.get(Calendar.MONTH);
        int dia = calendario.get(Calendar.DAY_OF_MONTH);
        recyclerView = view.findViewById(R.id.recyclerview);
        recyclerView.setLayoutManager(new LinearLayoutManager(getContext(),LinearLayoutManager.VERTICAL,false));
        spinner_tiendas = view.findViewById(R.id.tienda);
        MostrarDatos();
        fecha = view.findViewById(R.id.fecha);
        Button consultar = view.findViewById(R.id.consultar);
        fecha.setInputType(InputType.TYPE_NULL);
        String format_fecha = String.format(Locale.getDefault(), "%02d-%02d-%02d", año,(mes+1),dia);
        fecha.setText(format_fecha);
        fecha.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {
                //String[] get_fecha = fecha.getText().toString().split("/");
;
                DatePickerDialog.OnDateSetListener listenerDeDatePicker = new DatePickerDialog.OnDateSetListener() {
                    @Override
                    public void onDateSet(DatePicker view, int año, int mes, int diaDelMes) {
                        String format_fecha = String.format(Locale.getDefault(), "%02d-%02d-%02d", año,(mes+1),diaDelMes);
                        fecha.setText(format_fecha);
                    }
                };

                DatePickerDialog dialogoFecha = new DatePickerDialog(getContext(), listenerDeDatePicker, año,mes, dia);
                dialogoFecha.show();


            }
        });

        consultar.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {

                try {
                    String value_fecha= fecha.getText().toString();
                    Tienda item = (Tienda )spinner_tiendas.getSelectedItem();
                    int idtienda = item.getId();
                   if(!spinner_tiendas.getSelectedItem().toString().equals("Seleccionar")){
                       String resp = new ObtenerEgresosServicio(getContext()).execute(value_fecha, String.valueOf(idtienda)).get();
                       JSONArray jsonArray =  new JSONArray(resp);
                       ArrayList<JSONObject> nueva_lista= new ArrayList<>();
                       for(int i=0;jsonArray.length()> i;i++){
                               nueva_lista.add(jsonArray.getJSONObject(i));
                       }
                       if(nueva_lista.size() > 0){
                               RecyclerEgresos recyclerEgresos= new RecyclerEgresos(nueva_lista,getContext(),idtienda,value_fecha);
                               recyclerView.setAdapter(recyclerEgresos);
                       }else{
                               Toast.makeText(getContext(),"No se encontraron datos",Toast.LENGTH_LONG).show();
                           }
                   }else{
                       Toast.makeText(getContext(),"Debes seleccionar alguna de las opciones",Toast.LENGTH_LONG).show();
                   }

                } catch (ExecutionException e) {
                    e.printStackTrace();
                } catch (InterruptedException e) {
                    e.printStackTrace();
                } catch (JSONException e) {
                    Toast.makeText(getContext(),"Error en el servicio",Toast.LENGTH_LONG).show();
                }


            }
        });
        // Inflate the layout for this fragment
        return view;
    }



    public ArrayList<Tienda> MostrarDatos(){
        ArrayList<Tienda> lista = new ArrayList<>();

        try {
            lista.add(new Tienda(0,"Seleccionar"));
            String resp =new CRUDTiempoPedido(getContext()).execute().get();
            if(isJSONArrayValid(resp)){
                JSONArray lista_obtenida = new JSONArray(resp);
                for(int i = 0; lista_obtenida.length()> i; i++){
                    JSONObject jsonObject = lista_obtenida.getJSONObject(i);
                    lista.add(new Tienda(jsonObject.getInt("idtienda"),jsonObject.getString("tienda")));
                }

            } else {
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