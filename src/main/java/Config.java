import java.util.List;

/**
 * 解析config.json到Config对象
 * Created by wangjj17 on 2018/11/14.
 */
public class Config {
    private int numOfParams;
    private String fileDir;
    private String fileFormat;
    private int bufSize;
    private String constName;
    private long duration;
    private List<ParamConfs> paramConfs;

    public int getNumOfParams() {
        return numOfParams;
    }

    public void setNumOfParams(int numOfParams) {
        this.numOfParams = numOfParams;
    }

    public String getFileDir() {
        return fileDir;
    }

    public void setFileDir(String fileDir) {
        this.fileDir = fileDir;
    }

    public String getFileFormat() {
        return fileFormat;
    }

    public void setFileFormat(String fileFormat) {
        this.fileFormat = fileFormat;
    }

    public int getBufSize() {
        return bufSize;
    }

    public void setBufSize(int bufSize) {
        this.bufSize = bufSize;
    }

    public String getConstName() {
        return constName;
    }

    public void setConstName(String constName) {
        this.constName = constName;
    }

    public long getDuration() {
        return duration;
    }

    public void setDuration(long duration) {
        this.duration = duration;
    }

    public List<ParamConfs> getParamConfs() {
        return paramConfs;
    }

    public void setParamConfs(List<ParamConfs> paramConfs) {
        this.paramConfs = paramConfs;
    }
}
