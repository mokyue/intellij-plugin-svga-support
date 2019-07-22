package cc.moky.intellij.plugin.svga;

/*******************************************************************************
 * Created on: 2019/7/16 9:52
 * Author: Moky
 * Mail: mokyue@163.com
 *******************************************************************************/

import com.intellij.openapi.components.ServiceManager;
import com.intellij.openapi.fileTypes.FileTypeFactory;
import com.intellij.openapi.vfs.VirtualFile;

public abstract class SvgaFileTypeManager extends FileTypeFactory {

    static SvgaFileTypeManager getInstance() {
        return ServiceManager.getService(SvgaFileTypeManager.class);
    }

    public abstract boolean isSvga(VirtualFile file);
}
