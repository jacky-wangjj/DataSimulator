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
    private int tableIndex;
    private int BUFSIZE;
    private Map<String, String> fileNameMap;
    private String XLS = "xls";
    private String XLSX = "xlsx";
    private String SUFFIX;
    private String XLS_SUFFIX = ".xls";
    private String XLSX_SUFFIX = ".xlsx";
    private long SHEET_CAPACITY;
    private int TABLE_CAPACITY;

    public void setSheetIndex(int sheetIndex) {
        this.sheetIndex = sheetIndex;
    }

    public void setTableIndex(int tableIndex) {
        this.tableIndex = tableIndex;
    }

    public void setBUFSIZE(int BUFSIZE) {
        this.BUFSIZE = BUFSIZE;
    }

    public void setfileNameMap(Map<String, String> fileNameMap) {
        this.fileNameMap = fileNameMap;
    }

    public void setSHEET_CAPACITY(long SHEET_CAPACITY) {
        this.SHEET_CAPACITY = SHEET_CAPACITY;
    }

    public void setTABLE_CAPACITY(int TABLE_CAPACITY) {
        this.TABLE_CAPACITY = TABLE_CAPACITY;
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
     * @return
     */
    public HSSFSheet getSheet(HSSFWorkbook workbook) {
        HSSFSheet sheet = workbook.getSheet("Sheet"+String.valueOf(sheetIndex));
        if (sheet == null || sheet.getLastRowNum()+BUFSIZE > SHEET_CAPACITY) {//判断sheet的容量是否满足，不够则新建sheet
            sheet = (HSSFSheet) workbook.createSheet();//workbook中创建sheet对应excel中的sheet
            sheetIndex++;
            System.out.println("************ new sheet, sheetIndex:"+sheetIndex+" *************");
        }
        return sheet;
    }

    /**
     * 获取sheet
     * @param workbook
     * @return
     */
    public XSSFSheet getSheet(XSSFWorkbook workbook) {
        XSSFSheet sheet = workbook.getSheet("Sheet"+String.valueOf(sheetIndex));
        if (sheet == null || sheet.getLastRowNum()+BUFSIZE > SHEET_CAPACITY) {//判断sheet的容量是否满足，不够则新建sheet
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
    public int getLine(HSSFSheet sheet) {
        int line = sheet.getLastRowNum();
        if (line == 0) {
            return line;
        } else {
            return line + 1;
        }
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

    /**
     * 判断table是否存在，table能否容纳这次存储，以此判断是否需要新建表
     * @param filePath
     * @return
     */
    public boolean isTableFull(String filePath) {
        File file = new File(filePath);
        if (file.exists()) {
            try {
                HSSFWorkbook workbook = new HSSFWorkbook(new FileInputStream(filePath));
                HSSFSheet sheet = workbook.getSheet("Sheet"+String.valueOf(sheetIndex));
                if (sheet == null) {
                    return false;
                } else {
                    if (sheet.getLastRowNum()+BUFSIZE > SHEET_CAPACITY && sheetIndex == TABLE_CAPACITY-1) {
                        return true;
                    } else {
                        return false;
                    }
                }
            } catch (IOException e) {
                e.printStackTrace();
            }
        }
        return false;
    }

    /**
     * 保存xls文件
     * @param params
     * @param type
     * @param paramName
     */
    public void saveToXls(List<Param> params, int type, String paramName) {
        String fileName = fileNameMap.get(paramName);
        String filePath = fileName+String.valueOf(tableIndex)+SUFFIX;
        if (isTableFull(filePath)) {
            tableIndex++;
            sheetIndex = -1;
            filePath = fileName+String.valueOf(tableIndex)+SUFFIX;
            System.out.println("******** new table, tableIndex:"+tableIndex+" *********");
        }
        HSSFWorkbook workbook = openXlsWorkbook(filePath);
        int line;
        if (type == 0) {//常量表
            HSSFSheet sheet = getSheet(workbook);
            line = getLine(sheet);
            for (Param param : params) {
                HSSFRow row = sheet.createRow(line++);
                row.createCell(0).setCellValue(param.getName());
                fillOtherValues(row, param);
            }
        } else if (type == 1) {//时间序列表
            HSSFSheet sheet = getSheet(workbook);
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
     * 保存xlsx文件
     * @param params
     * @param type
     * @param paramName
     */
    public void saveToXlsx(List<Param> params, int type, String paramName) {
        String fileName = fileNameMap.get(paramName);
        String filePath = fileName+String.valueOf(tableIndex)+SUFFIX;
        if (isTableFull(filePath)) {
            tableIndex++;
            sheetIndex = -1;
            filePath = fileName+String.valueOf(tableIndex)+SUFFIX;
            System.out.println("******** new table, tableIndex:"+tableIndex+" *********");
        }
        XSSFWorkbook workbook = openXlsxWorkbook(filePath);
        int line;
        if (type == 0) {//常量表
            XSSFSheet sheet = getSheet(workbook);
            line = getLine(sheet);
            for (Param param : params) {
                XSSFRow row = sheet.createRow(line++);
                row.createCell(0).setCellValue(param.getName());
                fillOtherValues(row, param);
            }
        } else if (type == 1) {//时间序列表
            XSSFSheet sheet = getSheet(workbook);
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
            SUFFIX = XLS_SUFFIX;
            saveToXls(params, type, paramName);
        } else if (fileType.equals(XLSX)) {
            SUFFIX = XLSX_SUFFIX;
            saveToXlsx(params, type, paramName);
        }
    }
}
