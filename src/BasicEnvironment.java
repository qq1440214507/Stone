import java.util.HashMap;

public class BasicEnvironment implements Environment {
    protected HashMap<String,Object> values;

    public BasicEnvironment() {
        this.values = new HashMap<>();
    }

    @Override
    public void put(String name, Object value) {
        values.put(name,value);
    }

    @Override
    public Object get(String name) {
        return values.get(name);
    }
}
