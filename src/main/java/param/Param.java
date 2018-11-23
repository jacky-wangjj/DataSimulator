package param;

import java.io.Serializable;
import java.util.Map;
import java.util.Set;

/**
 * 保存参数变量
 * Created by wangjj17 on 2018/11/14.
 */
public class Param<K, V> implements Serializable {
    private static final long serialVersionUID = 6439856118442143605L;
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
    @Override
    public String toString() {
        StringBuilder sb = new StringBuilder();
        sb.append("timestamp:"+timestamp+" id:"+id+" name:"+name+" values[");
        Set<String> keys = (Set<String>) values.keySet();
        for (String key : keys) {
            sb.append(" "+key+":"+values.get(key)+" ");
        }
        sb.append("]");
        return sb.toString();
    }
}
