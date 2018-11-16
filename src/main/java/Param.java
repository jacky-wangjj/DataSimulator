import java.util.Map;

/**
 * 保存参数变量
 * Created by wangjj17 on 2018/11/14.
 */
public class Param<K, V> {
    private long timestamp;
    private int id;
    private String name;
    private Map<K, V> values;

    public long getTimestamp() {
        return timestamp;
    }

    public void setTimestamp(long timestamp) {
        this.timestamp = timestamp;
    }

    public int getId() {
        return id;
    }

    public void setId(int id) {
        this.id = id;
    }

    public String getName() {
        return name;
    }

    public void setName(String name) {
        this.name = name;
    }

    public Map<K, V> getValues() {
        return values;
    }

    public void setValues(Map<K, V> values) {
        this.values = values;
    }
}
