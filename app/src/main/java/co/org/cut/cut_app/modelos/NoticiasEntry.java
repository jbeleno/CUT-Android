package co.org.cut.cut_app.modelos;

/**
 * Created by Developer_1DOC3 on 24/10/15.
 */
public class NoticiasEntry {
        int idNoticia;
        String titulo, tiempo, imagen;

        public NoticiasEntry(int idNoticia, String titulo, String tiempo, String imagen){
            this.idNoticia = idNoticia;
            this.titulo = titulo;
            this.tiempo = tiempo;
            this.imagen = imagen;
        }

    public int getIdNoticia() {
        return idNoticia;
    }

    public String getImagen() {
        return imagen;
    }

    public String getTiempo() {
        return tiempo;
    }

    public String getTitulo() {
        return titulo;
    }

    public void setIdNoticia(int idNoticia) {
        this.idNoticia = idNoticia;
    }

    public void setImagen(String imagen) {
        this.imagen = imagen;
    }

    public void setTiempo(String tiempo) {
        this.tiempo = tiempo;
    }

    public void setTitulo(String titulo) {
        this.titulo = titulo;
    }
}
