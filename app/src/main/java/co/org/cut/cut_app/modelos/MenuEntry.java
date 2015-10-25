package co.org.cut.cut_app.modelos;

public class MenuEntry {
    Integer img;
    String titulo;

    public MenuEntry(Integer img, String titulo){
        this.img = img;
        this.titulo = titulo;
    }

    public int getImg() {
        return img;
    }

    public void setImg(int img) {
        this.img = img;
    }

    public String getTitulo() {
        return titulo;
    }

    public void setTitulo(String titulo) {
        this.titulo = titulo;
    }
}
