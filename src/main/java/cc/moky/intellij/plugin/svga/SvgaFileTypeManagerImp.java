package cc.moky.intellij.plugin.svga;

/*******************************************************************************
 * Created on: 2019/7/16 9:53
 * Author: Moky
 * Mail: mokyue@163.com
 *******************************************************************************/

import com.intellij.openapi.fileTypes.FileTypeConsumer;
import com.intellij.openapi.vfs.VirtualFile;
import org.jetbrains.annotations.NotNull;

public class SvgaFileTypeManagerImp extends SvgaFileTypeManager {

    @Override
    public void createFileTypes(@NotNull FileTypeConsumer consumer) {
        consumer.consume(SvgaFileType.INSTANCE, SvgaFileType.INSTANCE.getDefaultExtension());
    }

    @Override
    public boolean isSvga(VirtualFile file) {
        return file.getFileType() instanceof SvgaFileType;
    }
}
