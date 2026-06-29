package utilitaire;

import java.util.*;

public class UrlMethode {
    private String path;
    private String methode;
    
    public UrlMethode(String path , String methode){
        this.path= path;
        this.methode= methode;
    }

    @Override
    public boolean equals(Object obj){
        if(obj instanceof UrlMethode){
            UrlMethode urlMethode = (UrlMethode) obj;
            return this.path.equals(urlMethode.getPath()) && this.methode.equals(urlMethode.getMethode());
        }
        return false;
    }

    public String getPath(){
        return this.path;
    }
    public String getMethode(){
        return this.methode;
    }

    @Override
    public int hashCode() {
        return Objects.hash(path, methode);
    }

    // public boolean existKey(Map<UrlMethode,Object> map){
    //     for(Map.Entry<UrlMethode,Object> entity = map.entrySet()){
    //         if(equals(entity.getKey())){
    //             return true;
    //         }
    //     }
    //     return false;
    // }
}     
