package config;

import java.util.List;

/**
 * Created by wangjj17 on 2018/11/15.
 */
public class ParamRanges<T> {
    private List<T> paramRange;

    public List<T> getParamRange() {
        return paramRange;
    }

    public void setParamRange(List<T> paramRange) {
        this.paramRange = paramRange;
    }
}
