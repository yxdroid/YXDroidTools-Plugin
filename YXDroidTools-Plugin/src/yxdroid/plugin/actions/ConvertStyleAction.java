package yxdroid.plugin.actions;

import com.intellij.openapi.actionSystem.AnActionEvent;
import com.intellij.openapi.actionSystem.DataKeys;
import com.intellij.openapi.actionSystem.PlatformDataKeys;
import com.intellij.openapi.editor.Editor;
import com.intellij.openapi.editor.SelectionModel;
import com.intellij.openapi.fileTypes.StdFileTypes;
import com.intellij.openapi.vfs.VirtualFile;
import com.intellij.psi.PsiFile;
import com.intellij.psi.PsiFileFactory;
import com.intellij.psi.PsiManager;
import com.intellij.psi.codeStyle.CodeStyleManager;
import com.intellij.psi.xml.XmlFile;
import com.intellij.psi.xml.XmlTag;
import org.apache.http.util.TextUtils;

import java.util.ArrayList;
import java.util.List;
import java.util.regex.Matcher;
import java.util.regex.Pattern;

/**
 * User: yxfang
 * Date: 2017-06-29
 * Time: 20:49
 * ------------- Description -------------
 * 将layout.xml 中选中的属性转换成style item，同时用style 替换原选中属性
 * ---------------------------------------
 */
public class ConvertStyleAction extends BaseAnAction {

    private Editor editor;
    private SelectionModel selectionModel;

    @Override
    public void actionPerformed(AnActionEvent anActionEvent) {
        mProject = anActionEvent.getData(PlatformDataKeys.PROJECT);

        editor = DataKeys.EDITOR.getData(anActionEvent.getDataContext());

        selectionModel = editor.getSelectionModel();

        String selectedText = selectionModel.getSelectedText();

        if (!TextUtils.isEmpty(selectedText)) {
            log("selectedText", selectedText);

            // 匹配选中内容中合法的属性
            List<String> attributeList = match("android:\\w+=\".+\"", selectedText);
            if (attributeList.isEmpty()) {
                showTip("请选择完整属性");
                return;
            }

            String styleName = showInputDialog("请输入style名称");

            if (TextUtils.isEmpty(styleName)) {
                return;
            }

            StringBuffer sb = new StringBuffer();

            sb.append(String.format("<style name=\"%s\">\n", styleName));

            // 遍历属性集合 转成style item
            for (String a : attributeList) {
                List<String> itemList = match("[^=\"]+", a);
                String name = itemList.get(0);
                String value = itemList.get(1);
                sb.append(String.format("    <item name=\"%s\">%s</item>\n", name, value));
            }
            sb.append("</style>");

            log(sb.toString());

            VirtualFile currFile = DataKeys.VIRTUAL_FILE.getData(anActionEvent.getDataContext());

            if (currFile == null) {
                return;
            }

            // 通过当前layout文件获取res 目录
            VirtualFile resDir = currFile.getParent().getParent();
            // 获取values 下的styles.xml 通过这种相对路径的方式查找 避免项目中有多个模块的时候找到另外一个模块中
            VirtualFile valuesDir = resDir.findChild("values");
            VirtualFile styleVFile = valuesDir.findChild("styles.xml");
            if (styleVFile == null) {

                XmlFile styleFile = (XmlFile) PsiFileFactory.getInstance(mProject)
                        .createFileFromText("styles.xml", StdFileTypes.XML, "<resources></resources>");

                // 新建styles.xml
                doWrite(() -> {
                    addStyle(styleFile, styleName, attributeList);
                    PsiManager.getInstance(mProject).findDirectory(valuesDir).add(styleFile);
                });
            } else {
                PsiFile psiFile = PsiManager.getInstance(mProject).findFile(styleVFile);
                XmlFile styleFile = (XmlFile) PsiManager.getInstance(mProject).findFile(psiFile.getVirtualFile());
                addStyle(styleFile, styleName, attributeList);
            }


            /*PsiFile[] psiFiles = FilenameIndex.getFilesByName(mProject, "styles.xml", GlobalSearchScope.allScope(mProject));

            PsiFile psiFile = null;
            for (PsiFile file : psiFiles) {
                log(file.getName());
                // 过滤style.xml 是在res/values 目录下
                if ("values".equals(file.getParent().getName()) && "res".equals(file.getParent().getParent().getName())) {
                    psiFile = file;
                    break;
                }
            }*/
        }
    }

    /**
     * 往styles.xml 中添加style
     *
     * @param styleFile
     * @param styleName
     * @param attributeList
     */
    private void addStyle(XmlFile styleFile, String styleName, List<String> attributeList) {
        XmlTag rootTag = styleFile.getDocument().getRootTag();

        XmlTag[] subTags = rootTag.getSubTags();
        for (XmlTag tag : subTags) {
            if (styleName.equals(tag.getName())) {
                showTip("\"" + styleName + "\"style 已经存在");
                return;
            }
            log(tag.getName() + " " + tag.getNamespace() + " " + tag.getText());
        }

        doWrite(() -> {
            // name namespace value
            XmlTag styleTag = rootTag.createChildTag("style", null, null, false);
            styleTag.setAttribute("name", styleName);

            // 遍历属性集合 转成style item
            for (String a : attributeList) {
                List<String> itemList = match("[^=\"]+", a);
                String name = itemList.get(0);
                String value = itemList.get(1);

                XmlTag tag = styleTag.createChildTag("item", null, value, false);
                tag.setAttribute("name", name);
                styleTag.addSubTag(tag, false);
            }
            rootTag.addSubTag(styleTag, false);
            // 格式化代码
            CodeStyleManager.getInstance(mProject).reformat(styleFile);

            // 替换选中的内容
            editor.getDocument().replaceString(selectionModel.getSelectionStart(), selectionModel.getSelectionEnd(), "style=\"@style/" + styleName + "\"");
        });
    }

    private List<String> match(String regex, String inputText) {
        Pattern pattern = Pattern.compile(regex);
        Matcher matcher = pattern.matcher(inputText);
        List<String> resultList = new ArrayList<>();
        while (matcher.find()) {
            resultList.add(matcher.group());
        }
        return resultList;
    }

    @Override
    public void update(AnActionEvent e) {
        VirtualFile selectedFile = getSelectedFile(e.getDataContext());
        if (selectedFile != null) {
            // 如果是res/layout 目录下的文件则显示action
            if ("layout".equals(selectedFile.getParent().getName())
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
