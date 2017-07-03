package yxdroid.plugin.actions;

import com.intellij.openapi.actionSystem.*;
import com.intellij.openapi.project.Project;
import com.intellij.openapi.vfs.VirtualFile;

/**
 * User: yxfang
 * Date: 2017-06-29
 * Time: 20:06
 * ------------- Description -------------
 * <p>
 * ---------------------------------------
 */
public class GenerateMVPAction extends AnAction {

    @Override
    public void actionPerformed(AnActionEvent anActionEvent) {
        Project mProject = anActionEvent.getData(PlatformDataKeys.PROJECT);

        DataContext dataContext = anActionEvent.getDataContext();

        //获取选中的文件
        VirtualFile[] files = DataKeys.VIRTUAL_FILE_ARRAY.getData(anActionEvent.getDataContext());
    }

    @Override
    public void update(AnActionEvent e) {
        super.update(e);
    }
}
