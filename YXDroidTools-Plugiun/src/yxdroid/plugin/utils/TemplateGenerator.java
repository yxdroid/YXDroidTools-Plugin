package yxdroid.plugin.utils;

import freemarker.template.Configuration;
import freemarker.template.Template;
import freemarker.template.TemplateExceptionHandler;

import java.io.*;
import java.net.URISyntaxException;
import java.net.URL;
import java.util.Map;

/**
 * User: yxfang
 * Date: 2017-06-30
 * Time: 11:08
 * ------------- Description -------------
 * 模板生成器
 * ---------------------------------------
 */
public class TemplateGenerator {

    private static TemplateGenerator instance;

    private Configuration cfg;

    public static synchronized TemplateGenerator getInstance() {
        if (instance == null) {
            synchronized (TemplateGenerator.class) {
                instance = new TemplateGenerator();
            }
        }
        return instance;
    }

    public TemplateGenerator() {
        cfg = new Configuration(Configuration.VERSION_2_3_22);
        try {
            System.out.println("setDirectoryForTemplateLoading = " + getTemplateDir());
            cfg.setDirectoryForTemplateLoading(getTemplateDir());
            cfg.setDefaultEncoding("UTF-8");
            cfg.setTemplateExceptionHandler(TemplateExceptionHandler.RETHROW_HANDLER);
        } catch (IOException e) {
            e.printStackTrace();
        }
    }

    /**
     * 根据freemaker模板生成文件
     *
     * @param root         占位值
     * @param templateFile ftl文件
     * @param outFile      输出文件
     */
    public void gen(Map<String, Object> root, String templateFile, File outFile) {

        OutputStream fos = null;
        try {
            Template temp = cfg.getTemplate(templateFile);
            if (!outFile.exists()) {
                outFile.createNewFile();
            }
            fos = new FileOutputStream(outFile);
            Writer out = new OutputStreamWriter(fos);
            temp.process(root, out);
        } catch (Exception e) {
            e.printStackTrace();
        } finally {
            if (fos != null) {
                try {
                    fos.flush();
                    fos.close();
                } catch (Exception e) {
                    e.printStackTrace();
                }
            }
        }

    }

    /**
     * 获取template 路径
     *
     * @return
     */
    private File getTemplateDir() {
        String templateDir = "template/";
        URL templateUri = TemplateGenerator.class.getClassLoader().getResource(templateDir);
        if (templateUri == null) {
            System.out.println(templateDir + " not exists");
            return null;
        }
        File fileTemplateDir = null;
        try {
            fileTemplateDir = new File(templateUri.toURI());
        } catch (URISyntaxException e) {
            e.printStackTrace();
        }
        return fileTemplateDir;
    }
}
