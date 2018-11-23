package random;

import java.text.DecimalFormat;

/**
 * 生成随机数的工具类
 * Created by wangjj17 on 2018/11/14.
 */
public class RandomUtils {
    /**
     * 生成low - high之间的一个double类型的随机数，保留n位小数。
     * @param min
     * @param max
     * @param n
     * @return
     */
    public static double getDoubleEvenNum(double min, double max, int n) {
        StringBuilder sb = new StringBuilder(".");
        if (n > 0) {
            for (int i=0; i<n; i++) {
                sb.append("#");
            }
        }
        String pattern = sb.toString();
//        System.out.println("pattern:"+pattern);
        DecimalFormat df = new DecimalFormat();
        df.applyPattern(pattern);
        String res = df.format((min + Math.random()*(max - min)));
        return Double.parseDouble(res);
    }

    /**
     * 生成low - high之间的int类型的随机数
     * @param min
     * @param max
     * @return
     */
    public static int getIntEvenNum(int min, int max) {
        return min + (int)(Math.random()*(max - min));
    }
    /**
     * 生成c1-c2之间的随机字符
     * @param c1
     * @param c2
     * @return
     */
    public static char getChar(char c1, char c2) {
        return (char)(c1 + Math.random() * (c2 - c1 + 1));
    }

    public static void main(String[] args) {
        RandomUtils ru = new RandomUtils();
        System.out.println(ru.getIntEvenNum(2, 32));
        System.out.println(ru.getDoubleEvenNum(0, 100, 2));
        System.out.println(ru.getChar('a', 'z'));
    }
}
