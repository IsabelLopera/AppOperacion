 package com.example.appoperaciones.Fragmentos;

import android.app.DatePickerDialog;
import android.app.Dialog;
import android.content.Intent;
import android.os.Bundle;

import androidx.fragment.app.Fragment;

import android.text.InputType;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.ArrayAdapter;
import android.widget.Button;
import android.widget.CheckBox;
import android.widget.DatePicker;
import android.widget.EditText;
import android.widget.Spinner;
import android.widget.TextView;
import android.widget.Toast;

import java.util.Calendar;

import com.example.appoperaciones.Clases.Tienda;
import com.example.appoperaciones.EstadisticaPromActivity;
import com.example.appoperaciones.GraficaPromActivity;
import com.example.appoperaciones.R;
import com.example.appoperaciones.ReportesOperacionActivity;
import com.example.appoperaciones.Servicios.CRUDTiempoPedido;
import com.example.appoperaciones.Servicios.ObtenerEstadisticasPromocionAPP;

import org.json.JSONArray;
import org.json.JSONException;
import org.json.JSONObject;

import java.util.ArrayList;
import java.util.Locale;
import java.util.concurrent.ExecutionException;

 /**
 * A simple {@link Fragment} subclass.
 * Use the {@link EstadisticaPromFragment#newInstance} factory method to
 * create an instance of this fragment.
 */
public class EstadisticaPromFragment extends Fragment {

    // TODO: Rename parameter arguments, choose names that match
    // the fragment initialization parameters, e.g. ARG_ITEM_NUMBER
    private static final String ARG_PARAM1 = "param1";
    private static final String ARG_PARAM2 = "param2";

    // TODO: Rename and change types of parameters
    private String mParam1;
    private String mParam2;
    Spinner spinner_tiendas;
    EditText fecha1,fecha2;
    Button consultar,ButtonGrafica;
    CheckBox checkBoxTienda,checkBoxFecha;
    TextView titleTienda;
    int selectedCheckboxCount = 0;

    public EstadisticaPromFragment() {
        // Required empty public constructor
    }

    /**
     * Use this factory method to create a new instance of
     * this fragment using the provided parameters.
     *
     * @param param1 Parameter 1.
     * @param param2 Parameter 2.
     * @return A new instance of fragment EstadisticaPromocionFragment.
     */
    // TODO: Rename and change types and number of parameters
    public static EstadisticaPromFragment newInstance(String param1, String param2) {
        EstadisticaPromFragment fragment = new EstadisticaPromFragment();
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
        View view = inflater.inflate(R.layout.fragment_estadistica_promocion, container, false);
        ButtonGrafica = view.findViewById(R.id.graficar);
        ButtonGrafica.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {
                    Intent intent = new Intent(getContext(), GraficaPromActivity.class);
                    String rango_fecha = fecha1.getText().toString() +" / " +fecha2.getText();
                    intent.putExtra("fechas", rango_fecha);
                    ObtenerDatos(intent);
            }
        });

        final Calendar calendario = Calendar.getInstance();
        int año = calendario.get(Calendar.YEAR);
        int mes = calendario.get(Calendar.MONTH);
        int dia = calendario.get(Calendar.DAY_OF_MONTH);
        checkBoxTienda = view.findViewById(R.id.checkbox_tienda);
        checkBoxFecha = view.findViewById(R.id.checkbox_fecha);

        checkBoxTienda.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {
                boolean checked = ((CheckBox) view).isChecked();
                if(checked){
                    selectedCheckboxCount++;
                    spinner_tiendas.setVisibility(View.GONE);
                    titleTienda.setVisibility(View.GONE);
                    spinner_tiendas.setSelection(0);
                }else{
                    selectedCheckboxCount--;
                    spinner_tiendas.setVisibility(View.VISIBLE);
                    titleTienda.setVisibility(View.VISIBLE);
                }
                updateButtonVisibility();

            }
        });

        checkBoxFecha.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {
                boolean checked = ((CheckBox) view).isChecked();
                if(checked){
                    selectedCheckboxCount++;
                }else{
                    selectedCheckboxCount--;
                }
                updateButtonVisibility();
            }
        });
        titleTienda = view.findViewById(R.id.titleTienda);
        fecha1 = view.findViewById(R.id.fecha1);
        fecha2 = view.findViewById(R.id.fecha2);
        spinner_tiendas = view.findViewById(R.id.tiendas);
        consultar = view.findViewById(R.id.consultar);
        consultar.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {
                Intent intent = new Intent(getContext(), EstadisticaPromActivity.class);
                ObtenerDatos(intent);

            }
        });
        MostrarTiendas();
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

        return view;
    }


        private  void updateButtonVisibility(){
            if (selectedCheckboxCount == 2) {
                ButtonGrafica.setVisibility(View.VISIBLE);
            } else {
                ButtonGrafica.setVisibility(View.GONE);
            }
        }


     public ArrayList<Tienda> MostrarTiendas(){
         ArrayList<Tienda> lista = new ArrayList<>();

         try {
             lista.add(new Tienda(0,"Todas"));
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

     private  void ObtenerDatos(Intent intent){
         String fecha_incial = fecha1.getText().toString();
         String fecha_final = fecha2.getText().toString();
         Tienda item = (Tienda )spinner_tiendas.getSelectedItem();
         int idtienda = item.getId();

         ObtenerEstadisticasPromocionAPP service = new ObtenerEstadisticasPromocionAPP(getContext());
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
                 try {
                 if(isJSONArrayValid(resp)){

                     JSONArray jsonArray =  new JSONArray(resp);
                     ArrayList<JSONObject> nueva_lista= new ArrayList<>();
                     ArrayList<Tienda> tiendas=  MostrarTiendas();

                     for(int i=0;jsonArray.length()> i;i++){

                         for(Tienda t : tiendas){

                             if(jsonArray.getJSONObject(i).getInt("idtienda") == t.getId()){
                                 jsonArray.getJSONObject(i).put("nombre_tienda",t.getNombre());

                             }

                         }

                         nueva_lista.add(jsonArray.getJSONObject(i));
                     }
                     if(nueva_lista.size() > 0){
                         intent.putExtra("datos", nueva_lista.toString());
                         startActivity(intent);
                     }else{
                         Toast.makeText(getContext(),"No se encontraron datos.",Toast.LENGTH_LONG).show();

                     }


                 } else {
                     Toast.makeText(getContext(),"Error en el servicio",Toast.LENGTH_LONG).show();
                 }
                 } catch (JSONException e) {
                     e.printStackTrace();
                 }
             }
         });

         service.execute(fecha_incial,fecha_final,String.valueOf(idtienda),String.valueOf(checkBoxTienda.isChecked()),String.valueOf(checkBoxFecha.isChecked()));


     }

 }