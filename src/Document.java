import java.util.Map;

public class Document {
    private String fileName;
    private Map<String, Integer> voc;

    public Document(){
    }

    public String getFileName(){
        return this.fileName;
    }

    public void setFileName(String fileName){
        this.fileName = fileName;
    }

    public Map<String, Integer> getVoc(){
        return this.voc;
    }

    public void setVoc(Map<String, Integer> voc){
        this.voc = voc;
    }
}
