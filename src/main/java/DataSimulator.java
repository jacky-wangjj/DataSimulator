import java.io.File;
import java.text.SimpleDateFormat;
import java.util.*;

/**
 * Created by wangjj17 on 2018/11/14.
 */
public class DataSimulator{
    private static Config config;
    private Thread thread;
    private boolean isStop = false;
    private static String fileDir;
    private Map<String, String> filePathMap;
    private long duration;
    private long startTime;
    private List<ParamConfs> constParams = new ArrayList<ParamConfs>();
    private List<ParamConfs> timeSequenceParams = new ArrayList<ParamConfs>();
    private int sheetIndex;
    private String XLS_SUFFIX = ".xls";
    private String XLSX_SUFFIX = ".xlsx";
    private String XLS = "xls";
    private String XLSX = "xlsx";
    private String SUFFIX;
    private String CONST_NAME = "const";
    private int CONST_TYPE = 0;
    private int TIMESEQUENCE_TYPE = 1;
    private int BUFSIZE = 1024;
    private String FILE_FORMAT = "xls";
    private ExcelUtils eu = new ExcelUtils();

    /**
     * 初始化数据模拟器，读取配置文件并解析到Config对象
     */
    public void init() {
        config = ParseConfig.parseConfig();//解析config.json中的配置到Config对象
        startTime = System.currentTimeMillis();//获取系统时间，作为开始时间
        duration = config.getDuration();//获取模拟总时间，以ms为单位
        fileDir = config.getFileDir();//获取excel文件的存储路径
        FILE_FORMAT = config.getFileFormat();//获取excel文件的格式
        BUFSIZE = config.getBufSize();//获取buf的大小
        CONST_NAME = config.getConstName();//获取常量表的表名
        //依据ParamConfs.timeInterval分别填充constParams和timeSequenceParams
        List<ParamConfs> paramConfs = config.getParamConfs();
        for (ParamConfs paramConf : paramConfs) {
            if (paramConf.getTimeInterval() == 0) {
                constParams.add(paramConf);
            } else if (paramConf.getTimeInterval() > 0) {
                timeSequenceParams.add(paramConf);
            }
        }
        if (FILE_FORMAT.equals(XLS)) {
            SUFFIX = XLS_SUFFIX;
        } else if (FILE_FORMAT.equals(XLSX)) {
            SUFFIX = XLSX_SUFFIX;
        }
        //新建容量为timeSequenceParams.size()+1的Map，存储文件路径
        filePathMap = new HashMap<String, String>(timeSequenceParams.size()+1);
        filePathMap.put(CONST_NAME, fileDir+CONST_NAME+SUFFIX);
        for (ParamConfs paramConf : timeSequenceParams) {
            String paramName = paramConf.getName();
            filePathMap.put(paramName, fileDir+paramName+SUFFIX);
        }
        Set<String> keys = filePathMap.keySet();
        for (String key : keys) {
            String filePath = filePathMap.get(key);
            //检查filePath的excel文件是否存在，若存在，则删除
            File file = new File(filePath);
            if (file.exists()) {
                file.delete();
            }
        }
        eu.setBUFSIZE(BUFSIZE);
        eu.setFilePathMap(filePathMap);
    }

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

    public void printParams(List<Param> params) {
        for (Param param : params) {
            System.out.print("timestamp:"+param.getTimestamp()+" id:"+param.getId()+" name:"+param.getName()+" values[");
            Map<String, Number> values = param.getValues();
            Set<String> keys = values.keySet();
            for (String key : keys) {
                System.out.print(" "+key+":"+values.get(key)+" ");
            }
            System.out.println("]");
        }
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

    /**
     * 获取当前系统时间，并格式化输出
     */
    public void getCurrentTime() {
        SimpleDateFormat sdf = new SimpleDateFormat("yyyy-MM-dd HH:mm:ss");
        System.out.println("Time:"+sdf.format(new Date()));
    }

    /**
     * 保存常量
     */
    public void saveConst() {
        System.out.println("saveConst:");
        getCurrentTime();
        List<Param> params = new ArrayList(BUFSIZE);
        sheetIndex = -1;
        eu.setSheetIndex(sheetIndex);
        for (ParamConfs constParam : constParams) {
            //将参数信息填充到Param对象
            Param<String, Number> param = new Param<String, Number>();
            param.setId(constParam.getId());
            param.setName(constParam.getName());
            param.setValues(getRangesValues(constParam));
            params.add(param);
        }
        //printParams(params);
        eu.saveToExcel(params, CONST_TYPE, CONST_NAME, FILE_FORMAT);
        getCurrentTime();
    }

    /**
     * 保存时序变量
     */
    public void saveTimeSequence() {
        System.out.println("saveTimeSequence:");
        getCurrentTime();
        for (ParamConfs timeSequenceParam : timeSequenceParams) {
            sheetIndex = -1;
            eu.setSheetIndex(sheetIndex);
            startWork(timeSequenceParam);
        }
        getCurrentTime();
    }

    /**
     * 保存时序变量功能函数
     * @param timeSequenceParam
     */
    public void startWork(final ParamConfs timeSequenceParam) {
        final long timeInterval = timeSequenceParam.getTimeInterval();
        final List<Param> params = new ArrayList(BUFSIZE);
        while (true) {
            long endTime = startTime + duration;
            long timestamp = startTime;
            while (timestamp < endTime) {
                if (params.size() == BUFSIZE) {
                    //printParams(params);
                    eu.saveToExcel(params, TIMESEQUENCE_TYPE, timeSequenceParam.getName(), FILE_FORMAT);
                    params.clear();
                }
                //将变量参数信息填充到Param对象
                Param<String, Number> param = new Param<String, Number>();
                param.setId(timeSequenceParam.getId());
                param.setName(timeSequenceParam.getName());
                param.setValues(getRangesValues(timeSequenceParam));
                param.setTimestamp(timestamp);
                params.add(param);
                timestamp = timestamp + timeInterval;
            }
            isStop = true;
            if (isStop)
                break;
        }
        System.out.println("params.size:"+params.size());
        eu.saveToExcel(params, TIMESEQUENCE_TYPE, timeSequenceParam.getName(), FILE_FORMAT);
    }

    public static void main(String[] args) {
        DataSimulator ds = new DataSimulator();
        ds.init();
        ds.saveConst();
        ds.saveTimeSequence();
    }
}
