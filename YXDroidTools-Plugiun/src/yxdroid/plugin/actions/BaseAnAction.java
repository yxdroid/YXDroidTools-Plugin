package yxdroid.plugin.actions;

import com.intellij.openapi.actionSystem.AnAction;
import com.intellij.openapi.actionSystem.AnActionEvent;
import com.intellij.openapi.actionSystem.DataContext;
import com.intellij.openapi.actionSystem.DataKeys;
import com.intellij.openapi.command.WriteCommandAction;
import com.intellij.openapi.project.Project;
import com.intellij.openapi.ui.InputValidator;
import com.intellij.openapi.ui.Messages;
import com.intellij.openapi.vfs.VirtualFile;
import org.apache.http.util.TextUtils;

import java.util.regex.Pattern;

/**
 * User: yxfang
 * Date: 2017-06-30
 * Time: 15:51
 * ------------- Description -------------
 * <p>
 * ---------------------------------------
 */
public abstract class BaseAnAction extends AnAction {

    protected Project mProject;

    @Override
    public void actionPerformed(AnActionEvent anActionEvent) {

    }

    /**
     * 获取当前选中的文件
     *
     * @param dataContext
     * @return
     */
    protected VirtualFile getSelectedFile(DataContext dataContext) {
        VirtualFile file = DataKeys.VIRTUAL_FILE.getData(dataContext);
        log("SelectedFile", file.getPath());
        return file;
    }

    /**
     * 获取当前选中的文件扩展名
     *
     * @param dataContext
     * @return
     */
    protected String getFileExtension(DataContext dataContext) {
        VirtualFile file = getSelectedFile(dataContext);
        return file == null ? null : file.getExtension();
    }

    /**
     * 正则匹配
     *
     * @param regex
     * @param inputText
     * @return
     */
    protected boolean isMatch(String regex, String inputText) {
        Pattern pattern = Pattern.compile(regex);
        return pattern.matcher(inputText).find();
    }

    /**
     * 设置action是否可用
     *
     * @param event
     * @param enable
     */
    protected void setEnableAction(AnActionEvent event, boolean enable) {
        event.getPresentation().setEnabled(enable);
    }

    protected void showTip(String smg) {
        Messages.showMessageDialog(mProject, smg,
                "YXDroid Tools", Messages.getInformationIcon());
    }

    protected String showInputDialog(String msg) {
        return Messages.showInputDialog(mProject, msg,
                "YXDroid Tools", Messages.getInformationIcon(), "", new InputValidator() {
                    @Override
                    public boolean checkInput(String s) {
                        if (TextUtils.isEmpty(s)) {
                            return false;
                        } else {
                            return true;
                        }
                    }

                    @Override
                    public boolean canClose(String s) {
                        return true;
                    }
                });
    }

    protected void doWrite(Runnable runnable) {
        WriteCommandAction.runWriteCommandAction(mProject, runnable);
    }

    protected void log(String msg) {
        log(getClass().getSimpleName(), msg);
    }

    protected void log(String tag, String msg) {
        System.out.println(String.format("%s == %s", tag, msg));
    }
}
