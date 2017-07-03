package yxdroid.plugin.utils;

import java.io.BufferedReader;
import java.io.InputStreamReader;

/**
 * User: yxfang
 * Date: 2017-06-30
 * Time: 10:24
 * ------------- Description -------------
 * <p>
 * ---------------------------------------
 */
public class ShellUtil {

    public native void getString();
    public native void setValue(byte[] value);

    public static String runCmd(String cmd) throws Exception {
        BufferedReader br = null;
        try {

            System.out.println(cmd);
            Process p = Runtime.getRuntime().exec(cmd);
            br = new BufferedReader(new InputStreamReader(p.getInputStream()));
            String line = null;
            StringBuilder sb = new StringBuilder();
            while ((line = br.readLine()) != null) {
                sb.append(line + "\n");
            }
            System.out.println(sb.toString());
            return sb.toString();
        } catch (Exception e) {
            throw e;
        } finally {
            if (br != null) {
                try {
                    br.close();
                } catch (Exception e) {
                    e.printStackTrace();
                }
            }
        }
    }
}
