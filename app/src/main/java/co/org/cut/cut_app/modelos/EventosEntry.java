package co.org.cut.cut_app.modelos;

public class EventosEntry {
    int idEvento;
    String nombre,imagen, descripcion;

    public EventosEntry(int idEvento, String nombre, String imagen, String descripcion){
        this.idEvento = idEvento;
        this.nombre = nombre;
        this.imagen = imagen;
        this.descripcion = descripcion;
    }

    public int getIdEvento() {
        return idEvento;
    }

    public String getDescripcion() {
        return descripcion;
    }

    public String getImagen() {
        return imagen;
    }

    public String getNombre() {
        return nombre;
    }

    public void setDescripcion(String descripcion) {
        this.descripcion = descripcion;
    }

    public void setIdEvento(int idEvento) {
        this.idEvento = idEvento;
    }

    public void setImagen(String imagen) {
        this.imagen = imagen;
    }

    public void setNombre(String nombre) {
        this.nombre = nombre;
    }
}
