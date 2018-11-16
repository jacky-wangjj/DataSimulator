import org.apache.poi.hssf.usermodel.HSSFRow;
import org.apache.poi.hssf.usermodel.HSSFSheet;
import org.apache.poi.hssf.usermodel.HSSFWorkbook;

import java.io.File;
import java.io.FileInputStream;
import java.io.FileOutputStream;
import java.io.IOException;
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
    private String SUFFIX = ".xls";
    private String CONST = "const";
    private int CONST_TYPE = 0;
    private int TIMESEQUENCE_TYPE = 1;
    private int BUFSIZE = 1024;

    /**
     * 初始化数据模拟器，读取配置文件并解析到Config对象
     */
    public void init() {
        config = ParseConfig.parseConfig();//解析config.json中的配置到Config对象
        startTime = System.currentTimeMillis();//获取系统时间，作为开始时间
        duration = config.getDuration();//获取模拟总时间，以ms为单位
        fileDir = config.getFileDir();//获取excel文件的存储路径
        //依据ParamConfs.timeInterval分别填充constParams和timeSequenceParams
        List<ParamConfs> paramConfs = config.getParamConfs();
        for (ParamConfs paramConf : paramConfs) {
            if (paramConf.getTimeInterval() == 0) {
                constParams.add(paramConf);
            } else if (paramConf.getTimeInterval() > 0) {
                timeSequenceParams.add(paramConf);
            }
        }
        //新建容量为timeSequenceParams.size()+1的Map，存储文件路径
        filePathMap = new HashMap<String, String>(timeSequenceParams.size()+1);
        filePathMap.put(CONST, fileDir+CONST+SUFFIX);
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
     * 获取worbook，文件存在则读取，否则新建
     * @param filePath
     * @return
     */
    public HSSFWorkbook openWorkbook(String filePath) {
        HSSFWorkbook workbook = null;
        File file = new File(filePath);
        if (file.exists()) {
            try {
                workbook = new HSSFWorkbook(new FileInputStream(filePath));//若数据文件存在，则在此基础上添加
            } catch (IOException e) {
                e.printStackTrace();
            }
        } else {
            workbook = new HSSFWorkbook();//若数据文件不存在在新建
        }
        return workbook;
    }

    /**
     * 获取sheet
     * @param workbook
     * @param filePath
     * @return
     */
    public HSSFSheet getSheet(HSSFWorkbook workbook, String filePath) {
        HSSFSheet sheet = workbook.getSheet("Sheet"+String.valueOf(sheetIndex));
        if (sheet == null || sheet.getLastRowNum()+BUFSIZE > 66535) {//判断sheet的容量是否满足，不够则新建sheet
            sheet = (HSSFSheet) workbook.createSheet();//workbook中创建sheet对应excel中的sheet
            sheetIndex++;
            System.out.println("************ new sheet, sheetIndex:"+sheetIndex+" *************");
        }
        return sheet;
    }

    /**
     * 获取新建行的行号
     * @param sheet
     * @return
     */
    public int getLine(HSSFSheet sheet) {
        int line = sheet.getLastRowNum();
        if (line == 0) {
            return line;
        } else {
            return line + 1;
        }
    }

    /**
     * 依次填充param中的公共数据到excel中一行的cell
     * @param row
     * @param param
     */
    public void fillOtherValues(HSSFRow row, Param param) {
        row.createCell(1).setCellValue(param.getId());
        Map<String, Number> values = param.getValues();
        Set<String> keys = values.keySet();
        int i = 1;//从i开始往后遍历values的值填充cell
        for (String key : keys) {
            row.createCell(++i).setCellValue(String.valueOf(values.get(key)));
        }
    }

    /**
     * 写workbook到excel文件
     * @param workbook
     * @param filePath
     */
    public void closeWorkbook(HSSFWorkbook workbook, String filePath) {
        try {
            FileOutputStream fos = new FileOutputStream(filePath);
            workbook.write(fos);
            fos.flush();
            fos.close();
        } catch (Exception e) {
            e.printStackTrace();
        }
    }

    /**
     * 保存数据到excel，每BUFSIZE行调用一次
     * @param params
     * @param type
     * @param paramName
     */
    public void saveToExcel(List<Param> params, int type, String paramName) {
        String filePath = filePathMap.get(paramName);
        HSSFWorkbook workbook = openWorkbook(filePath);
        int line;
        if (type == 0) {//常量表
            HSSFSheet sheet = getSheet(workbook, filePath);
            line = getLine(sheet);
            for (Param param : params) {
                HSSFRow row = sheet.createRow(line++);
                row.createCell(0).setCellValue(param.getName());
                fillOtherValues(row, param);
            }
        } else if (type == 1) {//时间序列表
            HSSFSheet sheet = getSheet(workbook, filePath);
            line = getLine(sheet);
            for (Param param : params) {
                HSSFRow row = sheet.createRow(line++);//创建一行
                row.createCell(0).setCellValue(param.getTimestamp());
                fillOtherValues(row, param);
            }
            //System.out.println("line:"+line+" LastRow:"+sheet.getLastRowNum());
        }
        closeWorkbook(workbook, filePath);
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
        for (ParamConfs constParam : constParams) {
            //将参数信息填充到Param对象
            Param<String, Number> param = new Param<String, Number>();
            param.setId(constParam.getId());
            param.setName(constParam.getName());
            param.setValues(getRangesValues(constParam));
            params.add(param);
        }
        //printParams(params);
        saveToExcel(params, CONST_TYPE, CONST);
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
                    saveToExcel(params, TIMESEQUENCE_TYPE, timeSequenceParam.getName());
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
        saveToExcel(params, TIMESEQUENCE_TYPE, timeSequenceParam.getName());
    }

    public static void main(String[] args) {
        DataSimulator ds = new DataSimulator();
        ds.init();
        ds.saveConst();
        ds.saveTimeSequence();
    }
}
