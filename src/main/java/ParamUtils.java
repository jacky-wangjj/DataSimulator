import java.util.LinkedHashMap;
import java.util.List;
import java.util.Map;

/**
 * Created by wangjj17 on 2018/11/20.
 */
public class ParamUtils {

    /**
     * 生成min和max范围内保留n位小数的double随机数
     * @param min
     * @param max
     * @param n
     * @return
     */
    public double getValue(double min, double max, double n) {
        return RandomUtils.getDoubleEvenNum(min, max, (int)n);
    }

    /**
     * 生成大于min且小于max的随机整数
     * @param min
     * @param max
     * @return
     */
    public int getValue(int min, int max) {
        return RandomUtils.getIntEvenNum(min, max);
    }

    /**
     * 依据ParamConfs中的范围信息，生成对应的变量
     * @param param
     * @return
     */
    public Map<String, Number> getRangesValues(ParamConfs param) {
        //其他参数信息放入values的Map中，可增减
        Map<String, Number> values = new LinkedHashMap<String, Number>();
        int i = 0;
        //依据config.json中的paramRanges生成特定范围内的随机变量
        List<ParamRanges> paramRanges = param.getParamRanges();
        for (ParamRanges paramRange : paramRanges) {
            List<Number> range = paramRange.getParamRange();
            Number value = null;
            if (range.size() == 3) {
                value = getValue(range.get(0).doubleValue(), range.get(1).doubleValue(), range.get(2).intValue());
            } else if (range.size() == 2) {
                value = getValue(range.get(0).intValue(), range.get(1).intValue());
            } else {
                System.out.println("参数个数错误");
            }
            values.put("par"+String.valueOf(++i), value);//变量从par1依次增长
        }
        return values;
    }

    public Param getParam(ParamConfs paramConf, long timestamp) {
        //将参数信息填充到Param对象
        Param<String, Number> param = new Param<String, Number>();
        param.setId(paramConf.getId());
        param.setName(paramConf.getName());
        param.setValues(getRangesValues(paramConf));
        param.setTimestamp(timestamp);
        return param;
    }
}
