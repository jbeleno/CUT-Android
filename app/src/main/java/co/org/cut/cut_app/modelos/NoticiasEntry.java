package co.org.cut.cut_app.modelos;


public class NoticiasEntry {
        int idNoticia;
        String titulo, tiempo, imagen, url;

        public NoticiasEntry(int idNoticia, String titulo, String tiempo, String imagen, String url){
            this.idNoticia = idNoticia;
            this.titulo = titulo;
            this.tiempo = tiempo;
            this.imagen = imagen;
            this.url = url;
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

    public String getUrl() {
        return url;
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

    public void setUrl(String url) {
        this.url = url;
    }
}
