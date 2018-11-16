import org.apache.poi.hssf.usermodel.HSSFRow;
import org.apache.poi.hssf.usermodel.HSSFSheet;
import org.apache.poi.hssf.usermodel.HSSFWorkbook;
import org.apache.poi.xssf.usermodel.XSSFRow;
import org.apache.poi.xssf.usermodel.XSSFSheet;
import org.apache.poi.xssf.usermodel.XSSFWorkbook;

import java.io.File;
import java.io.FileInputStream;
import java.io.FileOutputStream;
import java.io.IOException;
import java.util.List;
import java.util.Map;
import java.util.Set;

/**
 * Created by wangjj17 on 2018/11/16.
 */
public class ExcelUtils {
    private int sheetIndex;
    private int BUFSIZE;
    private Map<String, String> filePathMap;
    private String XLS = "xls";
    private String XLSX = "xlsx";

    public void setSheetIndex(int sheetIndex) {
        this.sheetIndex = sheetIndex;
    }

    public void setBUFSIZE(int BUFSIZE) {
        this.BUFSIZE = BUFSIZE;
    }

    public void setFilePathMap(Map<String, String> filePathMap) {
        this.filePathMap = filePathMap;
    }
    /**
     * 获取worbook，文件存在则读取，否则新建
     * @param filePath
     * @return
     */
    public HSSFWorkbook openXlsWorkbook(String filePath) {
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

    public void saveToXls(List<Param> params, int type, String paramName) {
        String filePath = filePathMap.get(paramName);
        HSSFWorkbook workbook = openXlsWorkbook(filePath);
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

    /**
     * 获取worbook，文件存在则读取，否则新建
     * @param filePath
     * @return
     */
    public XSSFWorkbook openXlsxWorkbook(String filePath) {
        XSSFWorkbook workbook = null;
        File file = new File(filePath);
        if (file.exists()) {
            try {
                workbook = new XSSFWorkbook(new FileInputStream(filePath));//若数据文件存在，则在此基础上添加
            } catch (IOException e) {
                e.printStackTrace();
            }
        } else {
            workbook = new XSSFWorkbook();//若数据文件不存在在新建
        }
        return workbook;
    }

    /**
     * 获取sheet
     * @param workbook
     * @param filePath
     * @return
     */
    public XSSFSheet getSheet(XSSFWorkbook workbook, String filePath) {
        XSSFSheet sheet = workbook.getSheet("Sheet"+String.valueOf(sheetIndex));
        if (sheet == null || sheet.getLastRowNum()+BUFSIZE > 66535) {//判断sheet的容量是否满足，不够则新建sheet
            sheet = (XSSFSheet) workbook.createSheet();//workbook中创建sheet对应excel中的sheet
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
    public int getLine(XSSFSheet sheet) {
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
    public void fillOtherValues(XSSFRow row, Param param) {
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
    public void closeWorkbook(XSSFWorkbook workbook, String filePath) {
        try {
            FileOutputStream fos = new FileOutputStream(filePath);
            workbook.write(fos);
            fos.flush();
            fos.close();
        } catch (Exception e) {
            e.printStackTrace();
        }
    }
    public void saveToXlsx(List<Param> params, int type, String paramName) {
        String filePath = filePathMap.get(paramName);
        XSSFWorkbook workbook = openXlsxWorkbook(filePath);
        int line;
        if (type == 0) {//常量表
            XSSFSheet sheet = getSheet(workbook, filePath);
            line = getLine(sheet);
            for (Param param : params) {
                XSSFRow row = sheet.createRow(line++);
                row.createCell(0).setCellValue(param.getName());
                fillOtherValues(row, param);
            }
        } else if (type == 1) {//时间序列表
            XSSFSheet sheet = getSheet(workbook, filePath);
            line = getLine(sheet);
            for (Param param : params) {
                XSSFRow row = sheet.createRow(line++);//创建一行
                row.createCell(0).setCellValue(param.getTimestamp());
                fillOtherValues(row, param);
            }
            //System.out.println("line:"+line+" LastRow:"+sheet.getLastRowNum());
        }
        closeWorkbook(workbook, filePath);
    }
    /**
     * 保存数据到excel，每BUFSIZE行调用一次
     * @param params
     * @param type
     * @param paramName
     */
    public void saveToExcel(List<Param> params, int type, String paramName, String fileType) {
        if (fileType.equals(XLS)) {
            saveToXls(params, type, paramName);
        } else if (fileType.equals(XLSX)) {
            saveToXlsx(params, type, paramName);
        }
    }
}
