package utilitaire;

import java.util.Objects;

public class UrlMethode {
    private String path;
    private String methode;
    
    public UrlMethode(String path, String methode) {
        this.path = path;
        this.methode = methode;
    }

    @Override
    public boolean equals(Object obj) {
        if (this == obj) return true;
        if (obj == null || getClass() != obj.getClass()) return false;
        UrlMethode that = (UrlMethode) obj;
        
        return Objects.equals(path, that.path) && 
               Objects.equals(methode, that.methode);
    }

    @Override
    public int hashCode() {
        return Objects.hash(path, methode);
    }

    public String getPath() {
        return this.path;
    }

    public String getMethode() {
        return this.methode;
    }
}