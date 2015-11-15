package co.org.cut.cut_app;


import android.app.ProgressDialog;
import android.os.Bundle;
import android.support.v4.app.Fragment;
import android.text.Html;
import android.util.Log;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.TextView;

import com.android.volley.Request;
import com.android.volley.RequestQueue;
import com.android.volley.Response;
import com.android.volley.VolleyError;
import com.android.volley.toolbox.NetworkImageView;
import com.android.volley.toolbox.StringRequest;
import com.google.android.gms.analytics.HitBuilders;
import com.google.android.gms.analytics.Tracker;

import org.json.JSONException;
import org.json.JSONObject;

import java.util.HashMap;
import java.util.Map;

import co.org.cut.cut_app.herramientas.App_cut;
import co.org.cut.cut_app.herramientas.Mensaje;
import co.org.cut.cut_app.herramientas.MyVolley;

public class EventoFragment extends Fragment {
    public String URL_EVENTO = "http://52.27.16.14/cut/eventos/detalle";
    public static final String ARG_ID_EVENTO = "idEvento";
    private String idEvento;

    public static Tracker mTracker;

    String STR_ESTADO = "status";
    String STR_ESTADO_OK = "OK";
    String STR_EVENTO = "evento";
    String STR_ERROR = "Ocurrió un error cargando este evento.";
    String STR_NOMBRE = "titulo";
    String STR_DESCRIPCION = "descripcion";
    String STR_AGENDA = "agenda";
    String STR_IMAGEN = "imagen";

    private ProgressDialog progress;

    public TextView nombre, descripcion, agenda;
    public NetworkImageView imagen;


    public EventoFragment() {
        // Required empty public constructor
    }

    @Override
    public void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        if (getArguments() != null) {
            idEvento = getArguments().getString(ARG_ID_EVENTO);
        }
    }

    @Override
    public View onCreateView(LayoutInflater inflater, ViewGroup container,
                             Bundle savedInstanceState) {
        // Inflate the layout for this fragment
        View rootView = inflater.inflate(R.layout.fragment_evento, container, false);

        nombre = (TextView) rootView.findViewById(R.id.evento_nombre);
        descripcion = (TextView) rootView.findViewById(R.id.evento_descripcion);
        agenda = (TextView) rootView.findViewById(R.id.evento_agenda);
        imagen = (NetworkImageView) rootView.findViewById(R.id.evento_imagen);

        cargarEvento();

        return rootView;
    }

    public void cargarEvento(){
        progress = ProgressDialog.show(getActivity(), getString(R.string.app_name), getString(R.string.app_name), true);
        RequestQueue queue = MyVolley.getRequestQueue();

        StringRequest myReq = new StringRequest(Request.Method.POST,
                URL_EVENTO,
                successEvento(),
                errorConexion()) {
            protected Map<String, String> getParams() throws com.android.volley.AuthFailureError {
                Map<String, String> params = new HashMap<>();
                params.put(ARG_ID_EVENTO, String.valueOf(idEvento));
                return params;
            }
        };
        queue.add(myReq);
    }

    private Response.Listener<String> successEvento() {
        return new Response.Listener<String>() {
            @Override
            public void onResponse(String response) {
                progress.dismiss();
                try{
                    JSONObject jsonObj = new JSONObject(response);
                    Log.d(App_cut.getTag(), jsonObj.toString());
                    if(jsonObj.get(STR_ESTADO).equals(STR_ESTADO_OK)){

                        JSONObject evento = jsonObj.getJSONObject(STR_EVENTO);

                        nombre.setText(evento.getString(STR_NOMBRE));
                        descripcion.setText(evento.getString(STR_DESCRIPCION));
                        agenda.setText(Html.fromHtml(evento.getString(STR_AGENDA)));

                        String img = evento.getString(STR_IMAGEN);

                        imagen.setDefaultImageResId(R.drawable.img_placeholder);
                        if(img != null && !img.equals("")){
                            imagen.setImageUrl(img, MyVolley.getImageLoader());
                        }else{
                            imagen.setImageResource(android.R.color.transparent);
                        }

                        //Analytics
                        String STR_VISTA = evento.getString(STR_NOMBRE);

                        App_cut application = (App_cut) getActivity().getApplicationContext();
                        mTracker = application.getDefaultTracker();
                        mTracker.setScreenName(STR_VISTA);
                        mTracker.send(new HitBuilders.ScreenViewBuilder().build());

                    }else{
                        //En caso que se estallé la app por un servicio con mal formato
                        Log.e(App_cut.getTag(), response);
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
                if(getActivity() != null) Mensaje.mostrar(STR_ERROR, getActivity());
            }
        };
    }
}
