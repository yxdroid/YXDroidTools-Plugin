package yxdroid.plugin.actions;

import com.intellij.openapi.actionSystem.AnActionEvent;
import com.intellij.openapi.actionSystem.DataContext;
import com.intellij.openapi.actionSystem.DataKeys;
import com.intellij.openapi.actionSystem.PlatformDataKeys;
import com.intellij.openapi.vfs.VirtualFile;
import com.intellij.psi.PsiClass;
import com.intellij.psi.search.GlobalSearchScope;
import com.intellij.psi.search.PsiShortNamesCache;
import yxdroid.plugin.utils.ShellUtil;

import java.io.File;

/**
 * User: yxfang
 * Date: 2017-06-29
 * Time: 19:35
 * ------------- Description -------------
 * 通过选中的java文件生成native jni .h /cpp 文件
 * ---------------------------------------
 */
public class GenerateJNIAction extends BaseAnAction {

    @Override
    public void actionPerformed(AnActionEvent event) {
        mProject = event.getData(PlatformDataKeys.PROJECT);
        DataContext dataContext = event.getDataContext();

        if ("java".equals(getFileExtension(dataContext))) {
            //获取选中的文件
            VirtualFile file = DataKeys.VIRTUAL_FILE.getData(event.getDataContext());
            if (file != null) {

                String filePath = file.getPath();

                System.out.println("select file = " + filePath);

                // 根据选择的java文件名 获取所有PsiClass
                PsiClass[] psiClasses = PsiShortNamesCache.getInstance(mProject)
                        .getClassesByName(file.getNameWithoutExtension(), GlobalSearchScope.allScope(mProject));

                PsiClass selectedPsiClass = null;
                File srcDir = null;
                // 根据选择的java路径 过滤 真正选择的 PsiClass
                for (PsiClass p : psiClasses) {
                    // 类引用，全路径
                    String qualifiedName = p.getQualifiedName();
                    // 将.替换成/
                    String qualifiedPath = qualifiedName.replaceAll("\\.", "/");
                    if (filePath.contains(qualifiedPath)) {
                        selectedPsiClass = p;
                        srcDir = new File(filePath.replace(qualifiedPath + ".java", ""));
                    }
                }
                //System.out.println("srcPath = " + srcPath);
                if (selectedPsiClass != null) {
                    try {
                        File jniOutDir = new File(srcDir.getParent(), "jni");
                        if (!jniOutDir.exists()) {
                            jniOutDir.mkdirs();
                        }
                        ShellUtil.runCmd(String.format("javah -classpath %s -d %s %s",
                                srcDir.getAbsolutePath(), jniOutDir.getAbsolutePath(), selectedPsiClass.getQualifiedName()));

                        // 根据jni输出目录和java类名 创建 cpp文件
                        File cppFile = new File(jniOutDir, selectedPsiClass.getName() + ".cpp");
                        if (!cppFile.exists()) {
                            cppFile.createNewFile();
                        }
                        // 刷新工作空间
                        mProject.getBaseDir().refresh(true, true);
                    } catch (Exception e) {
                        showTip(e.getMessage());
                    }

                    log(selectedPsiClass.getQualifiedName());
                }
                /*JavaPsiFacade.getInstance(mProject).findClass();
                JavaPsiFacade.getInstance(mProject).findPackage("");*/
                //FilenameIndex.getFilesByName();
            }
        }

    }

    @Override
    public void update(AnActionEvent event) {
        //在Action显示之前,根据选中文件扩展名判定是否显示此Action
        String extension = getFileExtension(event.getDataContext());
        setEnableAction(event, extension != null && "java".equals(extension));
    }

}
