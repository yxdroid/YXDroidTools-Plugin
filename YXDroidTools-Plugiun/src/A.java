import java.util.regex.Matcher;
import java.util.regex.Pattern;

/**
 * User: yxfang
 * Date: 2017-06-30
 * Time: 11:14
 * ------------- Description -------------
 * <p>
 * ---------------------------------------
 */
public class A {
    public static void main(String args[]) throws Exception {
        Pattern pattern = Pattern.compile("android:\\w+=\".+\"");
        Matcher matcher = pattern.matcher("width=\"wrap_content\"\n" +
                "        android:layout_height=\"wrap_content\"\n" +
                "        android:text=\"Hello World!\"");
        StringBuffer sb = new StringBuffer();
        while (matcher.find()) {
            sb.append(matcher.group()).append("\n");
        }
        System.out.println(sb.toString());


      /*  try {
            ShellUtil.runCmd(String.format("javah -classpath %s -d %s yxdroid.plugin.utils.ShellUtil", "F:\\workspace\\git\\YXDroidTools-Plugiun\\src", "src"));
        } catch (Exception e) {
            e.printStackTrace();
        }

        Map<String, Object> root = new HashMap<String, Object>();
        root.put("name", "yxdroid");
        TemplateGenerator.getInstance().gen(root, "jni.ftl", new File("E://jni.h"));*/
    }
}
