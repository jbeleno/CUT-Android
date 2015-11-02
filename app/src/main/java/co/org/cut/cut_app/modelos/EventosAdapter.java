package co.org.cut.cut_app.modelos;

import android.app.Activity;
import android.content.Context;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.ArrayAdapter;
import android.widget.TextView;

import com.android.volley.toolbox.ImageLoader;
import com.android.volley.toolbox.NetworkImageView;

import java.util.List;

import co.org.cut.cut_app.R;

public class EventosAdapter extends ArrayAdapter<EventosEntry> {
    private final Activity context;
    private ImageLoader mImageLoader;

    /**
     * Este método es el contructor del adaptador que incializa todas
     * las variables que serán usadas por los demás métodos
     * @param context: La actividad donde se llama el dapatador
     * @param IdRecurso: No sé para que sirve
     * @param objects: Un array de noticias
     * @param mImageLoader: Una instancia de un cargador de imágenes asincronico
     */
    public EventosAdapter(Activity context,
                           int IdRecurso,
                           List<EventosEntry> objects,
                           ImageLoader mImageLoader) {
        super(context, IdRecurso, objects);

        this.context=context;
        this.mImageLoader = mImageLoader;
    }

    /**
     * Este método infla cada una de las noticias que se incluyen
     * @param position: La posición de la noticia según el array
     * @param view: La vista a la que se debe adicionar la noticia
     * @param parent: NPI
     * @return devuelve la vista con datos llenos según la posición
     */
    public View getView(int position,View view,ViewGroup parent) {
        View rowView = view;
        if (rowView == null) {
            LayoutInflater vi = (LayoutInflater) context.getSystemService(Context.LAYOUT_INFLATER_SERVICE);
            rowView = vi.inflate(R.layout.evento_item, null);
        }

        ViewHolder holder = (ViewHolder) rowView.getTag(R.id.id_holder);

        if (holder == null) {
            holder = new ViewHolder(rowView);
            rowView.setTag(R.id.id_holder, holder);
        }

        EventosEntry entry = getItem(position);

        holder.nombre.setText(entry.getNombre());
        holder.descripcion.setText(entry.getDescripcion());
        holder.tiempo.setText(entry.getTiempo());
        if(entry.getImagen() != null && !entry.getImagen().equals("")){
            holder.imagen.setImageUrl(entry.getImagen(), mImageLoader);
        }else{
            holder.imagen.setImageResource(android.R.color.transparent);
        }

        rowView.setTag(R.id.id_servidor, entry.getIdEvento());

        return rowView;
    }


    /**
     * Esta clase debe se crear para administrar lo que lleva
     * la interfaz gráfica de un evento
     */
    private class ViewHolder{
        TextView nombre, descripcion, tiempo;
        NetworkImageView imagen;
        public ViewHolder(View v){
            nombre = (TextView) v.findViewById(R.id.evento_nombre);
            descripcion = (TextView) v.findViewById(R.id.evento_descripcion);
            tiempo = (TextView) v.findViewById(R.id.evento_tiempo);
            imagen = (NetworkImageView) v.findViewById(R.id.evento_imagen);

            v.setTag(this);
        }
    }
}
