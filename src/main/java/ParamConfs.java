import java.util.List;

/**
 * 解析config.json中params数组到Param对象List
 * Created by wangjj17 on 2018/11/14.
 */
public class ParamConfs {
    private int id;
    private String name;
    private long timeInterval;
    private List<ParamRanges> paramRanges;

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

    public long getTimeInterval() {
        return timeInterval;
    }

    public void setTimeInterval(long timeInterval) {
        this.timeInterval = timeInterval;
    }

    public List<ParamRanges> getParamRanges() {
        return paramRanges;
    }

    public void setParamRanges(List<ParamRanges> paramRanges) {
        this.paramRanges = paramRanges;
    }
}
