package yxdroid.plugin.actions;

import com.intellij.openapi.actionSystem.AnActionEvent;
import com.intellij.openapi.actionSystem.DataKeys;
import com.intellij.openapi.actionSystem.PlatformDataKeys;
import com.intellij.openapi.command.WriteCommandAction;
import com.intellij.openapi.fileTypes.StdFileTypes;
import com.intellij.openapi.vfs.VirtualFile;
import com.intellij.psi.*;
import com.intellij.psi.codeStyle.CodeStyleManager;
import com.intellij.psi.xml.XmlDocument;
import com.intellij.psi.xml.XmlFile;
import com.intellij.psi.xml.XmlTag;
import org.apache.http.util.TextUtils;

import java.io.IOException;

/**
 * User: yxfang
 * Date: 2017-06-29
 * Time: 20:30
 * ------------- Description -------------
 * <p>
 * ---------------------------------------
 */
public class GenerateSelector extends BaseAnAction {

    @Override
    public void actionPerformed(AnActionEvent anActionEvent) {

        mProject = anActionEvent.getData(PlatformDataKeys.PROJECT);

        //获取选中的文件
        VirtualFile[] files = DataKeys.VIRTUAL_FILE_ARRAY.getData(anActionEvent.getDataContext());
        if (files != null) {
            StringBuffer sb = new StringBuffer();

            // res
            VirtualFile resVFile = files[0].getParent().getParent();
            // 查找res/drawable
            final VirtualFile[] drawableVFile = {resVFile.findChild("drawable")};

            // 如果drawable 不存在 则创建
            if (drawableVFile[0] == null || !drawableVFile[0].exists()) {
                doWrite(new Runnable() {
                    @Override
                    public void run() {
                        try {
                            drawableVFile[0] = resVFile.createChildDirectory(this, "drawable");
                        } catch (IOException e) {
                            e.printStackTrace();
                            showTip(e.getMessage());
                        }
                    }
                });

            }

            // 获取drawable 目录
            PsiDirectory drawableDir = PsiManager.getInstance(mProject)
                    .findDirectory(drawableVFile[0]);

            for (PsiFile f : drawableDir.getFiles()) {
                sb.append(f.getName() + "\n");
            }
            log(sb.toString());

            final String selectorName = showInputDialog("请定义selector名称") + ".xml";


            XmlFile xmlFile = (XmlFile) PsiFileFactory.getInstance(mProject)
                    .createFileFromText(selectorName, StdFileTypes.XML, "<?xml version=\"1.0\" encoding=\"utf-8\" ?>" +
                            "\n<selector xmlns:android=\"http://schemas.android.com/apk/res/android\">\n</selector>");

            WriteCommandAction.runWriteCommandAction(mProject, () -> {

                XmlDocument document = xmlFile.getDocument();
                XmlTag rootTag;
                if (document != null && (rootTag = document.getRootTag()) != null) {

                    log(rootTag.getName() + " " + rootTag.getNamespace());

                    /**
                     * <?xml version="1.0" encoding="utf-8" ?>
                     * <selector xmlns:android="http://schemas.android.com/apk/res/android">
                     * <item android:state_selected="false" android:drawable="${item1}"/>
                     * <item android:state_selected="true" android:drawable="${item2}"/>
                     * <item android:state_focused="false" android:drawable="${item2}"/>
                     * <item android:state_focused="true" android:drawable="${item2}"/>
                     * </selector>
                     */
                    for (int i = 0; i < files.length; i++) {

                        String textTag = null;
                        XmlTag itemTag = null;
                        switch (i + 1) {
                            case 1:
                                textTag = "<item android:state_selected=\"false\"  android:drawable=\"%s\"/>";
                                break;
                            case 2:
                                textTag = "<item android:state_selected=\"true\" android:drawable=\"%s\"/>";
                                break;
                            case 3:
                                textTag = "<item android:state_focused=\"false\" android:drawable=\"%s\"/>";
                                break;
                            case 4:
                                textTag = "<item android:state_focused=\"true\" android:drawable=\"%s\"/>";
                                break;
                        }
                        if (!TextUtils.isEmpty(textTag)) {
                            itemTag = createTag(textTag, files[i]);
                            rootTag.addSubTag(itemTag, false);
                        }
                    }
                }
                // 格式化代码
                CodeStyleManager.getInstance(mProject).reformat(xmlFile);
                drawableDir.add(xmlFile);
            });
        }
    }

    /**
     * 通过text tag 创建xml tag
     *
     * @param textTag
     * @param drawableFile
     * @return
     */
    private XmlTag createTag(String textTag, VirtualFile drawableFile) {

        String fileParentName = drawableFile.getParent().getName();
        String value = null;

        // 判断选中的文件是在 drawable 还是在mipmap 下面
        if (fileParentName.contains("drawable")) {
            value = "@drawable/" + drawableFile.getNameWithoutExtension();
        } else {
            value = "@mipmap/" + drawableFile.getNameWithoutExtension();
        }

        return XmlElementFactory.getInstance(mProject).createTagFromText(String.format(textTag + "\n", value));
    }

    /**
     * 判断选中的文件是在 drawable 还是在mipmap 下面
     *
     * @param drawableFile
     * @return
     */
    private String getDrawableFrom(VirtualFile drawableFile) {
        String fileParentName = drawableFile.getParent().getName();
        String value = null;

        // 判断选中的文件是在 drawable 还是在mipmap 下面
        if (fileParentName.contains("drawable")) {
            value = "@drawable/" + drawableFile.getNameWithoutExtension();
        } else {
            value = "@mipmap/" + drawableFile.getNameWithoutExtension();
        }
        return value;
    }

    @Override
    public void update(AnActionEvent e) {
        VirtualFile selectedFile = getSelectedFile(e.getDataContext());
        if (selectedFile != null) {
            // 如果是res/layout 目录下的文件则显示action
            if (isMatch("[a-z]+-[a-z]+dpi$", selectedFile.getParent().getName())
                    && "res".equals(selectedFile.getParent().getParent().getName())) {
                setEnableAction(e, true);
            } else {
                setEnableAction(e, false);
            }
        } else {
            setEnableAction(e, false);
        }
    }
}
