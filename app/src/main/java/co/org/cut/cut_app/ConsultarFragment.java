package co.org.cut.cut_app;


import android.app.ProgressDialog;
import android.content.Context;
import android.os.Bundle;
import android.support.v4.app.Fragment;
import android.util.Log;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.view.inputmethod.InputMethodManager;
import android.widget.Button;
import android.widget.EditText;

import com.android.volley.Request;
import com.android.volley.RequestQueue;
import com.android.volley.Response;
import com.android.volley.VolleyError;
import com.android.volley.toolbox.StringRequest;
import org.json.JSONException;
import org.json.JSONObject;
import java.util.HashMap;
import java.util.Map;
import co.org.cut.cut_app.herramientas.App_cut;
import co.org.cut.cut_app.herramientas.Mensaje;
import co.org.cut.cut_app.herramientas.MyVolley;


public class ConsultarFragment extends Fragment {
    public String URL_CONSULTAR = "http://52.27.16.14/cut/consultas/nueva";
    private String ARG_CORREO = "correo";
    private String ARG_CONSULTA = "mensaje";
    String STR_ERROR = "Ocurrió un error cargando enviando la consulta a la CUT.";
    String STR_SUCCESS = "Tu consulta fue enviada correctamente, dentro de poco una persona de la CUT te contactará al correo que ingresaste.";

    String STR_ESTADO = "status";
    String STR_ESTADO_OK = "OK";

    private ProgressDialog progress;

    private EditText ET_correo, ET_consulta;

    public ConsultarFragment() {
        // Required empty public constructor
    }

    @Override
    public void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
    }

    @Override
    public View onCreateView(LayoutInflater inflater, ViewGroup container,
                             Bundle savedInstanceState) {
        // Inflate the layout for this fragment
        View rootView =  inflater.inflate(R.layout.fragment_consultar, container, false);
        ET_correo = (EditText) rootView.findViewById(R.id.correo);
        ET_consulta = (EditText) rootView.findViewById(R.id.consulta);

        Button btnEnviar = (Button) rootView.findViewById(R.id.btn_enviar);

        btnEnviar.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                InputMethodManager imm =  (InputMethodManager) getActivity().getSystemService(Context.INPUT_METHOD_SERVICE);
                imm.hideSoftInputFromWindow(v.getWindowToken(), 0);

                enviarConsulta();
            }
        });


        return rootView;
    }

    public void enviarConsulta(){
        progress = ProgressDialog.show(getActivity(), getString(R.string.app_name), getString(R.string.app_name), true);
        RequestQueue queue = MyVolley.getRequestQueue();

        StringRequest myReq = new StringRequest(Request.Method.POST,
                URL_CONSULTAR,
                successConsultar(),
                errorConexion()) {
            protected Map<String, String> getParams() throws com.android.volley.AuthFailureError {
                Map<String, String> params = new HashMap<>();
                params.put(ARG_CORREO, ET_correo.getText().toString());
                params.put(ARG_CONSULTA, ET_consulta.getText().toString());
                return params;
            }
        };
        queue.add(myReq);
    }

    private Response.Listener<String> successConsultar() {
        return new Response.Listener<String>() {
            @Override
            public void onResponse(String response) {
                progress.dismiss();
                try{
                    JSONObject jsonObj = new JSONObject(response);
                    Log.d(App_cut.getTag(), jsonObj.toString());
                    if(jsonObj.get(STR_ESTADO).equals(STR_ESTADO_OK)){
                        ET_correo.setText("");
                        ET_consulta.setText("");

                        Mensaje.mostrar(STR_SUCCESS, getActivity());
                    }else{
                        Mensaje.mostrar(STR_ERROR, getActivity());
                    }
                }catch (JSONException e){
                        //En caso que se estallé la app por un servicio con mal formato
                        Log.e(App_cut.getTag(), response);
                        Mensaje.mostrar(STR_ERROR, getActivity());
                }
            }
        };
    }

    //Se reciben los errores que puedan ocurrir por parte del servidor
    private Response.ErrorListener errorConexion() {
        return new Response.ErrorListener() {
            @Override
            public void onErrorResponse(VolleyError error) {
                //Se estalla por un error 404 o 500 en el servicio
                progress.dismiss();
                Log.e(App_cut.getTag(), error.toString());
                if(getActivity() != null)Mensaje.mostrar(STR_ERROR, getActivity());
            }
        };
    }
}
