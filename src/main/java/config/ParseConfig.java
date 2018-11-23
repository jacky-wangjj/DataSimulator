package config;

import com.alibaba.fastjson.JSON;
import com.alibaba.fastjson.JSONReader;

import java.io.*;
import java.util.List;

/**
 * 读取配置文件config.json，并解析json文件到Config对象
 * Created by wangjj17 on 2018/11/14.
 */
public class ParseConfig {
    /**
     * 解析json文件到Config对象
     * @return
     */
    public static Config parseConfig() {
        JSONReader jsonReader = null;
        try {
            jsonReader = new JSONReader(new FileReader(new File("src/main/resources/config.json")));
        } catch (FileNotFoundException e) {
            e.printStackTrace();
        }
        String text = jsonReader.readString();
//        System.out.println(text);
        Config config = JSON.parseObject(text, Config.class);
        return config;
    }
    public static void main(String[] args) {
        Config config = ParseConfig.parseConfig();
        System.out.println(config.getNumOfParams());
        System.out.println(config.getFileDir());
        System.out.println(config.getFileFormat());
        System.out.println(config.getSheetCapacity());
        System.out.println(config.getTableCapacity());
        System.out.println(config.getBufSize());
        System.out.println(config.getConstName());
        System.out.println(config.getDuration());
        List<ParamConfs> list = config.getParamConfs();
        for (ParamConfs paramConfs : list) {
            System.out.print("id:"+paramConfs.getId()+" name:"+paramConfs.getName()+" timeInterval:"+paramConfs.getTimeInterval()+" others[");
            List<ParamRanges> paramRanges = paramConfs.getParamRanges();
            for (ParamRanges param : paramRanges) {
                List<?> range = param.getParamRange();
                System.out.print(" "+range.get(0).getClass()+" "+range.get(1).getClass()+" ");
            }
            System.out.println("]");
        }
    }
}
