package co.org.cut.cut_app.herramientas;

import android.content.Context;
import android.widget.Toast;

public class Mensaje {
    public static void mostrar(String msj, Context contexto){
        Toast toast = Toast.makeText(contexto, msj, Toast.LENGTH_LONG);
        toast.show();
    }
}
