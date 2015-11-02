package co.org.cut.cut_app;


import android.app.ProgressDialog;
import android.os.Bundle;
import android.os.Handler;
import android.support.v4.app.Fragment;
import android.support.v4.app.FragmentManager;
import android.support.v4.widget.SwipeRefreshLayout;
import android.util.Log;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.AbsListView;
import android.widget.AdapterView;
import android.widget.ListView;

import com.android.volley.Request;
import com.android.volley.RequestQueue;
import com.android.volley.Response;
import com.android.volley.VolleyError;
import com.android.volley.toolbox.StringRequest;

import org.json.JSONArray;
import org.json.JSONException;
import org.json.JSONObject;

import java.util.ArrayList;
import java.util.HashMap;
import java.util.Map;

import co.org.cut.cut_app.herramientas.App_cut;
import co.org.cut.cut_app.herramientas.Mensaje;
import co.org.cut.cut_app.herramientas.MyVolley;
import co.org.cut.cut_app.modelos.EventosAdapter;
import co.org.cut.cut_app.modelos.EventosEntry;

public class EventosFragment extends Fragment {
    public String URL_EVENTOS = "http://52.27.16.14/cut/eventos/ver";
    public String ARG_OFFSET = "offset";

    String STR_ESTADO = "status";
    String STR_ESTADO_OK = "OK";
    String STR_EVENTOS = "eventos";
    String STR_ERROR = "Ocurrió un error cargando los eventos de la CUT.";
    String STR_ID_EVENTO = "id";
    String STR_NOMBRE = "titulo";
    String STR_DESCRIPCION = "descripcion";
    String STR_TIEMPO = "tiempo";
    String STR_IMAGEN = "imagen";

    private ProgressDialog progress;
    private SwipeRefreshLayout swipeLayout;
    private int offset = 0;
    boolean final_scroll = false;

    private EventosAdapter adaptador;
    private ArrayList<EventosEntry> entradas = new ArrayList<>();
    private ListView listaEventos;

    public EventosFragment() {
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
        View view = inflater.inflate(R.layout.fragment_eventos, container, false);

        listaEventos =  (ListView) view.findViewById(R.id.eventos_data);

        swipeLayout = (SwipeRefreshLayout) view.findViewById(R.id.swipe_eventos);
        swipeLayout.setOnRefreshListener(new RefreshListener());
        swipeLayout.setColorSchemeResources(R.color.naranja);

        cargarEventos();

        return view;
    }

    public void cargarEventos(){
        progress = ProgressDialog.show(getActivity(), getString(R.string.app_name), getString(R.string.app_name), true);
        RequestQueue queue = MyVolley.getRequestQueue();

        StringRequest myReq = new StringRequest(Request.Method.POST,
                URL_EVENTOS,
                successEventos(),
                errorConexion()) {
            protected Map<String, String> getParams() throws com.android.volley.AuthFailureError {
                Map<String, String> params = new HashMap<>();
                params.put(ARG_OFFSET, String.valueOf(offset));
                return params;
            }
        };
        queue.add(myReq);
    }


    private Response.Listener<String> successEventos() {
        return new Response.Listener<String>() {
            @Override
            public void onResponse(String response) {
                progress.dismiss();
                try{
                    JSONObject jsonObj = new JSONObject(response);
                    Log.d(App_cut.getTag(), jsonObj.toString());
                    if(jsonObj.get(STR_ESTADO).equals(STR_ESTADO_OK)){
                        JSONArray publicaciones = jsonObj.getJSONArray(STR_EVENTOS);
                        if(publicaciones.length() != 0) {
                            for (int i = 0; i < publicaciones.length(); i++) {

                                JSONObject publicacion = publicaciones.getJSONObject(i);

                                int IdEvento = publicacion.getInt(STR_ID_EVENTO);
                                String nombre = publicacion.getString(STR_NOMBRE);
                                String tiempo = publicacion.getString(STR_TIEMPO);
                                String imagen = publicacion.getString(STR_IMAGEN);
                                String descripcion = publicacion.getString(STR_DESCRIPCION);

                                entradas.add(new EventosEntry(IdEvento, nombre, imagen, descripcion, tiempo));
                            }
                            if (offset == 0) {
                                adaptador = new EventosAdapter(getActivity(), 0, entradas, MyVolley.getImageLoader());
                                listaEventos.setAdapter(adaptador);
                                listaEventos.setOnItemClickListener(new EventosItemListener());
                                listaEventos.setOnScrollListener(new EventosScrollListener());
                            } else {
                                adaptador.notifyDataSetChanged();
                            }
                            final_scroll = false;
                        }
                    }else{
                        if(offset == 0) {
                            // Se muestra un mensaje en caso de error
                            Log.d(App_cut.getTag(), response);
                            Mensaje.mostrar(STR_ERROR, getActivity());
                        }
                    }
                }catch (JSONException e){
                    if(offset == 0) {
                        //En caso que se estallé la app por un servicio con mal formato
                        Log.e(App_cut.getTag(), response);
                        Mensaje.mostrar(STR_ERROR, getActivity());
                    }
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

    private class RefreshListener implements SwipeRefreshLayout.OnRefreshListener{
        @Override public void onRefresh() {
            new Handler().postDelayed(new Runnable() {
                @Override public void run() {

                    swipeLayout.setRefreshing(false);
                    entradas = new ArrayList<>();
                    offset = 0;

                    cargarEventos();
                }
            }, 1000);
        }
    }


    private class EventosItemListener implements ListView.OnItemClickListener {
        @Override
        public void onItemClick(AdapterView parent, View view, int position, long id) {
            android.support.v4.app.Fragment fragment = new EventoFragment();
            Bundle args = new Bundle();
            args.putString(EventoFragment.ARG_ID_EVENTO, String.valueOf(view.getTag(R.id.id_servidor)));
            fragment.setArguments(args);

            FragmentManager fragmentManager = getActivity().getSupportFragmentManager();
            fragmentManager.beginTransaction()
                    .replace(R.id.content_frame, fragment, null)
                    .addToBackStack(null)
                    .commit();
        }
    }

    /**
     * Para escuchar cuando se llega al final del ListView
     */
    private class EventosScrollListener implements ListView.OnScrollListener {
        @Override
        public void onScrollStateChanged(AbsListView view, int scrollState) {

        }

        @Override
        public void onScroll(AbsListView lw, final int firstVisibleItem,
                             final int visibleItemCount, final int totalItemCount) {
            switch(lw.getId()) {
                case R.id.noticias_data:
                    // Sample calculation to determine if the last
                    // item is fully visible.
                    final int lastItem = firstVisibleItem + visibleItemCount;
                    if(lastItem == totalItemCount) {
                        if(!final_scroll){
                            offset++;
                            cargarEventos();
                            final_scroll = true;
                        }
                    }
            }
        }
    }
}
