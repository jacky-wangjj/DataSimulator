import java.io.File;
import java.text.SimpleDateFormat;
import java.util.*;
import java.util.regex.Matcher;
import java.util.regex.Pattern;

/**
 * Created by wangjj17 on 2018/11/14.
 */
public class DataSimulator{
    private static Config config;
    private Thread thread;
    private boolean isStop = false;
    private static String fileDir;
    private Map<String, String> fileNameMap;
    private long duration;
    private long startTime;
    private List<ParamConfs> constParams = new ArrayList<ParamConfs>();
    private List<ParamConfs> timeSequenceParams = new ArrayList<ParamConfs>();
    private int sheetIndex;//sheet索引
    private int tableIndex;//table索引
    private String XLS_SUFFIX = ".xls";
    private String XLSX_SUFFIX = ".xlsx";
    private String XLS = "xls";
    private String XLSX = "xlsx";
    private String SUFFIX;//后缀
    private String CONST_NAME = "const";
    private int CONST_TYPE = 0;
    private int TIMESEQUENCE_TYPE = 1;
    private int BUFSIZE = 1024;
    private String FILE_FORMAT = "xls";
    private long SHEET_CAPACITY = 65536;
    private int TABLE_CAPACITY = 1;
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
        SHEET_CAPACITY = config.getSheetCapacity();//获取sheet表的最大容量
        TABLE_CAPACITY = config.getTableCapacity();//获取table表的最大sheet数
        BUFSIZE = config.getBufSize();//获取buf的大小
        CONST_NAME = config.getConstName();//获取常量表的表名
        List<ParamConfs> paramConfs = config.getParamConfs();
        checkParamConfs(paramConfs);
        checkFormat();
        checkFile();
        eu.setBUFSIZE(BUFSIZE);
        eu.setfileNameMap(fileNameMap);
        eu.setSHEET_CAPACITY(SHEET_CAPACITY);
        eu.setTABLE_CAPACITY(TABLE_CAPACITY);
    }

    /**
     * 依据ParamConfs.timeInterval分别填充constParams和timeSequenceParams
     * @param paramConfs
     */
    public void checkParamConfs(List<ParamConfs> paramConfs) {
        for (ParamConfs paramConf : paramConfs) {
            if (paramConf.getTimeInterval() == 0) {
                constParams.add(paramConf);
            } else if (paramConf.getTimeInterval() > 0) {
                timeSequenceParams.add(paramConf);
            }
        }
    }

    /**
     * 设置SUFFIX
     */
    public void checkFormat() {
        if (FILE_FORMAT.equals(XLS)) {
            SUFFIX = XLS_SUFFIX;
        } else if (FILE_FORMAT.equals(XLSX)) {
            SUFFIX = XLSX_SUFFIX;
        }
    }

    /**
     * 检查fileNameMap中的文件是否存在，存在则删除
     */
    public void checkFile() {
        //新建容量为timeSequenceParams.size()+1的Map，存储文件路径
        fileNameMap = new HashMap<String, String>(timeSequenceParams.size()+1);
        fileNameMap.put(CONST_NAME, fileDir+CONST_NAME+"-");
        for (ParamConfs paramConf : timeSequenceParams) {
            String paramName = paramConf.getName();
            fileNameMap.put(paramName, fileDir+paramName+"-");
        }
        File[] files = new File(fileDir).listFiles();
        Set<String> keys = fileNameMap.keySet();
        for (File file : files) {
            for (String key : keys) {
                //模式匹配param-0.xls，[0-9]+匹配一到多位数字
                Pattern pattern = Pattern.compile(key + "-[0-9]+" + SUFFIX);
                Matcher matcher = pattern.matcher(file.getName());
                if (matcher.matches()) {
                    System.out.println(file.getName());
                    //检查fileName的excel文件是否存在，存在则删除
                    if (file.exists()) {
                        file.delete();
                    }
                }
            }
        }
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

    /**
     * 打印参数信息
     * @param params
     */
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
     * 初始化sheet，table的索引
     */
    public void beforeSave() {
        getCurrentTime();
        sheetIndex = -1;
        eu.setSheetIndex(sheetIndex);
        tableIndex = 0;
        eu.setTableIndex(tableIndex);
    }

    /**
     * 保存常量
     */
    public void saveConst() {
        System.out.println("saveConst:");
        beforeSave();
        List<Param> params = new ArrayList(BUFSIZE);
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
            beforeSave();
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
