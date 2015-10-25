package co.org.cut.cut_app.modelos;

import android.app.Activity;
import android.content.Context;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.ArrayAdapter;
import android.widget.ImageView;
import android.widget.TextView;

import java.util.List;

import co.org.cut.cut_app.R;

/**
 * Esta clase es para llenar los datos que hay en el menú
 * usando lo que hay en un array de strings y unas imágenes
 */
public class MenuAdapter extends ArrayAdapter<MenuEntry> {

    private final Activity context;

    /**
     * Este es el constructor del adaptador del menú y es el que
     * inicializa los valores que se deben usar en los demás métodos
     * de la clase
     * @param context: Es el activity desde donde se llama el adaptador
     * @param IdRecurso: Desconozco para que sirve esto
     * @param objects: Son los items que van dentro del menú
     */
    public MenuAdapter(Activity context,
                       int IdRecurso,
                       List<MenuEntry> objects) {

        super(context, IdRecurso, objects);

        this.context=context;
    }

    /**
     * Se infla la vista del menú y se llenan los valores uno por uno con
     * los valores proporcionados anteriormente
     * @param position: Es la posición del item que se va a insertar en el menú
     * @param view: Es la vista de un item
     * @param parent: Es la vista a la que se debe agregar el item
     * @return
     */
    public View getView(int position,View view,ViewGroup parent) {
        View rowView = view;
        if (rowView == null) {
            LayoutInflater vi = (LayoutInflater) this.getContext().getSystemService(Context.LAYOUT_INFLATER_SERVICE);
            rowView = vi.inflate(R.layout.drawer_list_item, null);
        }

        ViewHolder holder = (ViewHolder) rowView.getTag(R.id.id_holder);

        if (holder == null) {
            holder = new ViewHolder(rowView);
            rowView.setTag(R.id.id_holder, holder);
        }

        MenuEntry entry = getItem(position);

        holder.titulo.setText(entry.getTitulo());
        holder.imagen.setImageResource(entry.getImg());

        rowView.setTag(R.id.id_servidor, entry.getTitulo());

        return rowView;
    }

    /**
     * Esta es una clase privada que contiene los elementos de cada
     * item
     */
    private class ViewHolder{
        TextView titulo;
        ImageView imagen;
        public ViewHolder(View v){
            titulo = (TextView) v.findViewById(R.id.Itemname);
            imagen = (ImageView) v.findViewById(R.id.icon);

            v.setTag(this);
        }
    }
}

