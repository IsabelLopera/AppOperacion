package com.example.appoperaciones.Fragmentos;

import android.app.DatePickerDialog;
import android.app.Dialog;
import android.content.DialogInterface;
import android.content.Intent;
import android.os.Bundle;

import androidx.appcompat.app.AlertDialog;
import androidx.fragment.app.Fragment;
import androidx.recyclerview.widget.LinearLayoutManager;
import androidx.recyclerview.widget.RecyclerView;

import android.text.InputType;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.Button;
import android.widget.DatePicker;
import android.widget.EditText;
import android.widget.TextView;
import android.widget.Toast;

import com.example.appoperaciones.Adaptadores.RecyclerEstadisticaPlat;
import com.example.appoperaciones.GraficaPlatActivity;
import com.example.appoperaciones.GraficaPromActivity;
import com.example.appoperaciones.R;
import com.example.appoperaciones.ReportesOperacionActivity;
import com.example.appoperaciones.Servicios.ObtenerEstadisticasPlataformas;

import org.json.JSONArray;
import org.json.JSONException;
import org.json.JSONObject;

import java.util.ArrayList;
import java.util.Calendar;
import java.util.Locale;

/**
 * A simple {@link Fragment} subclass.
 * Use the {@link EstadisticaPlatFragment#newInstance} factory method to
 * create an instance of this fragment.
 */
public class EstadisticaPlatFragment extends Fragment {

    // TODO: Rename parameter arguments, choose names that match
    // the fragment initialization parameters, e.g. ARG_ITEM_NUMBER
    private static final String ARG_PARAM1 = "param1";
    private static final String ARG_PARAM2 = "param2";

    // TODO: Rename and change types of parameters
    private String mParam1;
    private String mParam2;
    EditText fecha1,fecha2;
    TextView rango_fechas;
    Button consultar,ButtonGrafica;
    RecyclerView recyclerView;
    final private CharSequence[] OPCIONES_ALERTA = {"Total de venta", "Cantidad de pedidos","Porcentaje / Total de venta","Porcentaje / Cantidad de pedidos" };

    public EstadisticaPlatFragment() {
        // Required empty public constructor
    }

    /**
     * Use this factory method to create a new instance of
     * this fragment using the provided parameters.
     *
     * @param param1 Parameter 1.
     * @param param2 Parameter 2.
     * @return A new instance of fragment EstadisticaPlatFragment.
     */
    // TODO: Rename and change types and number of parameters
    public static EstadisticaPlatFragment newInstance(String param1, String param2) {
        EstadisticaPlatFragment fragment = new EstadisticaPlatFragment();
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
        View view=  inflater.inflate(R.layout.fragment_estadistica_plat, container, false);
        final Calendar calendario = Calendar.getInstance();
        int año = calendario.get(Calendar.YEAR);
        int mes = calendario.get(Calendar.MONTH);
        int dia = calendario.get(Calendar.DAY_OF_MONTH);
        recyclerView = view.findViewById(R.id.recyclerview);
        recyclerView.setLayoutManager(new LinearLayoutManager(getContext(),LinearLayoutManager.VERTICAL,false));
        consultar = view.findViewById(R.id.consultar);
        fecha1 =  view.findViewById(R.id.fecha1);
        fecha2 =  view.findViewById(R.id.fecha2);
        rango_fechas =  view.findViewById(R.id.rango_fechas);
        ButtonGrafica = view.findViewById(R.id.graficar);
        ButtonGrafica.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {
                Intent intent= new Intent(getContext(), GraficaPlatActivity.class);
                AlertDialog.Builder builder = new AlertDialog.Builder(getActivity());
                builder.setTitle("Opciones");
                builder.setItems(OPCIONES_ALERTA, new DialogInterface.OnClickListener() {
                    @Override
                    public void onClick(DialogInterface dialog, int which) {
                        // which representa el índice del arreglo de opciones
                        String eleccion = OPCIONES_ALERTA[which].toString();
                        intent.putExtra("tipo",eleccion);
                        ObtenerDatos(intent);
                        dialog.dismiss();
                    }
                });
                builder.show();

            }
        });

        fecha1.setInputType(InputType.TYPE_NULL);
        String format_fecha = String.format(Locale.getDefault(), "%02d-%02d-%02d", año,(mes+1),dia);
        fecha1.setText(format_fecha);
        fecha1.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {
                //String[] get_fecha = fecha.getText().toString().split("/");
                DatePickerDialog.OnDateSetListener listenerDeDatePicker = new DatePickerDialog.OnDateSetListener() {
                    @Override
                    public void onDateSet(DatePicker view, int año, int mes, int diaDelMes) {
                        String format_fecha = String.format(Locale.getDefault(), "%02d-%02d-%02d", año,(mes+1),diaDelMes);
                        fecha1.setText(format_fecha);
                    }
                };

                DatePickerDialog dialogoFecha = new DatePickerDialog(getContext(), listenerDeDatePicker, año,mes, dia);
                dialogoFecha.show();


            }
        });

        fecha2.setInputType(InputType.TYPE_NULL);
        fecha2.setText(format_fecha);
        fecha2.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {
                //String[] get_fecha = fecha.getText().toString().split("/");
                ;
                DatePickerDialog.OnDateSetListener listenerDeDatePicker = new DatePickerDialog.OnDateSetListener() {
                    @Override
                    public void onDateSet(DatePicker view, int año, int mes, int diaDelMes) {
                        String format_fecha = String.format(Locale.getDefault(), "%02d-%02d-%02d", año,(mes+1),diaDelMes);
                        fecha2.setText(format_fecha);
                    }
                };

                DatePickerDialog dialogoFecha = new DatePickerDialog(getContext(), listenerDeDatePicker, año,mes, dia);
                dialogoFecha.show();


            }
        });

        consultar.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {
                ObtenerDatos(null);
            }
        });

        return  view;
    }
    private  void ObtenerDatos(Intent intent){

        String fecha_incial = fecha1.getText().toString();
        String fecha_final = fecha2.getText().toString();

        String rango_fecha= fecha1.getText().toString()+" / "+fecha2.getText().toString();

        ObtenerEstadisticasPlataformas service = new ObtenerEstadisticasPlataformas(getContext());
        Dialog dialog = new Dialog(getContext(), R.style.CustomAlertDialog);
        dialog.setContentView(R.layout.custom_dialog);
        service.setAsyncTaskListener(new ReportesOperacionActivity.AsyncTaskListener() {
            @Override
            public void showProgressBar() {
                if(!dialog.isShowing()){
                    dialog.show();
                }
            }

            @Override
            public void CloseProgressBar() {
                if(dialog.isShowing()){
                    dialog.dismiss();
                }
            }

            @Override
            public void datos(String resp) {
                System.out.println(resp);
                try {
                        JSONArray jsonArray =  new JSONArray(resp);
                        ArrayList<JSONObject> nueva_lista= new ArrayList<>();

                        for(int i=0;jsonArray.length()> i;i++){

                            if(jsonArray.getJSONObject(i).getString("fuente").equals("TK")){
                                jsonArray.getJSONObject(i).put("fuente", "TIENDA VIRTUAL");
                            }

                            else if (jsonArray.getJSONObject(i).getString("fuente").equals("C")){
                                jsonArray.getJSONObject(i).put("fuente", "CONTACT CENTER");
                            }
                            nueva_lista.add(jsonArray.getJSONObject(i));
                        }
                        if(nueva_lista.size() > 0){
                            if(intent == null){
                                rango_fechas.setText(rango_fecha);
                                RecyclerEstadisticaPlat recyclerEstadisticaPlat= new RecyclerEstadisticaPlat(nueva_lista);
                                recyclerView.setAdapter(recyclerEstadisticaPlat);
                            }else{
                                intent.putExtra("datos", nueva_lista.toString());
                                intent.putExtra("fechas", rango_fecha);
                                startActivity(intent);
                            }


                        }else{
                            Toast.makeText(getContext(),"No se encontraron datos.",Toast.LENGTH_LONG).show();

                        }
                } catch (JSONException e) {
                Toast.makeText(getContext(),"Error en el servicio",Toast.LENGTH_LONG).show();
                }
            }
        });

        service.execute(fecha_incial,fecha_final);
    }

}