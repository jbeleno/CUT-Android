package co.org.cut.cut_app;

import android.content.Context;
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
import android.widget.TextView;

import com.android.volley.Request;
import com.android.volley.RequestQueue;
import com.android.volley.Response;
import com.android.volley.VolleyError;
import com.android.volley.toolbox.StringRequest;
import com.google.android.gms.analytics.HitBuilders;
import com.google.android.gms.analytics.Tracker;

import org.json.JSONArray;
import org.json.JSONException;
import org.json.JSONObject;
import java.util.ArrayList;
import java.util.HashMap;
import java.util.Map;
import co.org.cut.cut_app.herramientas.App_cut;
import co.org.cut.cut_app.herramientas.Mensaje;
import co.org.cut.cut_app.herramientas.MyVolley;
import co.org.cut.cut_app.modelos.NoticiasAdapter;
import co.org.cut.cut_app.modelos.NoticiasEntry;


/**
 *
 */
public class NoticiasFragment extends Fragment {
    public String URL_NOTICIAS = "http://52.27.16.14/cut/feed/ver";
    public String ARG_OFFSET = "offset";

    public static Tracker mTracker;

    String STR_ESTADO = "status";
    String STR_ESTADO_OK = "OK";
    String STR_PUBLICACIONES = "publicaciones";
    String STR_ERROR = "Ocurrió un error cargando las publicaciones de la CUT.";
    String STR_ID_NOTICIA = "id";
    String STR_TITULO = "titulo";
    String STR_TIEMPO = "tiempo";
    String STR_IMAGEN = "imagen";
    String STR_URL = "url";

    private SwipeRefreshLayout swipeLayout;
    private int offset = 0;
    boolean final_scroll = false;

    private NoticiasAdapter adaptador;
    private ArrayList<NoticiasEntry> entradas = new ArrayList<>();
    private ListView listaNoticias;
    private TextView loadingText;

    public NoticiasFragment() {
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
        View view = inflater.inflate(R.layout.fragment_noticias, container, false);
        listaNoticias = (ListView) view.findViewById(R.id.noticias_data);
        loadingText = (TextView) view.findViewById(R.id.loading_text);

        swipeLayout = (SwipeRefreshLayout) view.findViewById(R.id.swipe_container);
        swipeLayout.setOnRefreshListener(new RefreshListener());
        swipeLayout.setColorSchemeResources(R.color.naranja);

        cargarNoticias();

        return view;
    }

    public void cargarNoticias(){
        RequestQueue queue = MyVolley.getRequestQueue();

        StringRequest myReq = new StringRequest(Request.Method.POST,
                URL_NOTICIAS,
                successCargarNoticias(),
                errorConexion()) {
            protected Map<String, String> getParams() throws com.android.volley.AuthFailureError {
                Map<String, String> params = new HashMap<>();
                params.put(ARG_OFFSET, String.valueOf(offset));
                return params;
            }
        };
        queue.add(myReq);
    }


    private Response.Listener<String> successCargarNoticias() {
        return new Response.Listener<String>() {
            @Override
            public void onResponse(String response) {
                loadingText.setVisibility(View.GONE);
                try{
                    JSONObject jsonObj = new JSONObject(response);
                    Log.d(App_cut.getTag(), jsonObj.toString());
                    if(jsonObj.get(STR_ESTADO).equals(STR_ESTADO_OK)){
                        JSONArray publicaciones = jsonObj.getJSONArray(STR_PUBLICACIONES);
                        if(publicaciones.length() != 0) {
                            for (int i = 0; i < publicaciones.length(); i++) {

                                JSONObject publicacion = publicaciones.getJSONObject(i);

                                int IdNoticia = publicacion.getInt(STR_ID_NOTICIA);
                                String titulo = publicacion.getString(STR_TITULO);
                                String tiempo = publicacion.getString(STR_TIEMPO);
                                String imagen = publicacion.getString(STR_IMAGEN);
                                String url = publicacion.getString(STR_URL);

                                entradas.add(new NoticiasEntry(IdNoticia, titulo, tiempo, imagen, url));
                            }
                            if (offset == 0) {
                                adaptador = new NoticiasAdapter(getActivity(), 0, entradas, MyVolley.getImageLoader());
                                listaNoticias.setAdapter(adaptador);
                                listaNoticias.setOnItemClickListener(new NoticiasItemListener());
                                listaNoticias.setOnScrollListener(new NoticiasScrollListener());
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
                loadingText.setVisibility(View.GONE);
                Log.e(App_cut.getTag(), error.toString());
                if(getActivity() != null)Mensaje.mostrar(STR_ERROR, getActivity());
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

                    cargarNoticias();
                }
            }, 1000);
        }
    }

    private class NoticiasItemListener implements ListView.OnItemClickListener {
        @Override
        public void onItemClick(AdapterView parent, View view, int position, long id) {
            Fragment fragment = new WebFragment();
            Bundle args = new Bundle();
            args.putString(WebFragment.ARG_URL, (String) view.getTag(R.id.url));
            fragment.setArguments(args);

            FragmentManager fragmentManager = getActivity().getSupportFragmentManager();
            fragmentManager.beginTransaction()
                    .add(R.id.content_frame, fragment, null)
                    .addToBackStack(null)
                    .commit();
        }
    }

    /**
     * Para escuchar cuando se llega al final del ListView
     */
    private class NoticiasScrollListener implements ListView.OnScrollListener {
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
                            cargarNoticias();
                            final_scroll = true;
                        }
                    }
            }
        }
    }

    @Override
    public void onAttach(Context context) {
        super.onAttach(context);

        String STR_VISTA = "Noticias";

        App_cut application = (App_cut) context.getApplicationContext();
        mTracker = application.getDefaultTracker();
        mTracker.setScreenName(STR_VISTA);
        mTracker.send(new HitBuilders.ScreenViewBuilder().build());
    }
}
