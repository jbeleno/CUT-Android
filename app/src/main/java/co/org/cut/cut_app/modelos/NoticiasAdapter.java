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

public class NoticiasAdapter extends ArrayAdapter<NoticiasEntry>{

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
    public NoticiasAdapter(Activity context,
                            int IdRecurso,
                            List<NoticiasEntry> objects,
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
     * @return
     */
    public View getView(int position,View view,ViewGroup parent) {
        View rowView = view;
        if (rowView == null) {
            LayoutInflater vi = (LayoutInflater) context.getSystemService(Context.LAYOUT_INFLATER_SERVICE);
            rowView = vi.inflate(R.layout.noticia_item, null);
        }

        ViewHolder holder = (ViewHolder) rowView.getTag(R.id.id_holder);

        if (holder == null) {
            holder = new ViewHolder(rowView);
            rowView.setTag(R.id.id_holder, holder);
        }

        NoticiasEntry entry = getItem(position);

        holder.titulo.setText(entry.getTitulo());
        holder.tiempo.setText(entry.getTiempo());
        if(entry.getImagen() != null && !entry.getImagen().equals("")){
            holder.imagen.setImageUrl(entry.getImagen(), mImageLoader);
        }else{
            holder.imagen.setImageResource(android.R.color.transparent);
        }

        rowView.setTag(R.id.id_servidor, entry.getIdNoticia());

        return rowView;
    }

    /**
     * Esta clase debe se crear para administrar lo que lleva
     * la interfaz gráfica de una noticia
     */
    private class ViewHolder{
        TextView titulo, tiempo;
        NetworkImageView imagen;
        public ViewHolder(View v){
            titulo = (TextView) v.findViewById(R.id.noticia_titulo);
            tiempo = (TextView) v.findViewById(R.id.noticia_tiempo);
            imagen = (NetworkImageView) v.findViewById(R.id.noticia_imagen);

            v.setTag(this);
        }
    }
}
