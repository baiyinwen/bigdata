import java.util.ResourceBundle;

public class Prop {

    ResourceBundle bundle = ResourceBundle.getBundle("flume_monitor.properties");
    public String getString(String key){
        return bundle.getString(key);
    }
}

