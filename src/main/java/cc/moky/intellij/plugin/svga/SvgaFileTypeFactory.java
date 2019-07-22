package cc.moky.intellij.plugin.svga;

/*******************************************************************************
 * Created on: 2019/7/16 9:51
 * Author: Moky
 * Mail: mokyue@163.com
 *******************************************************************************/

import com.intellij.openapi.fileTypes.FileTypeConsumer;
import com.intellij.openapi.fileTypes.FileTypeFactory;
import org.jetbrains.annotations.NotNull;

public class SvgaFileTypeFactory extends FileTypeFactory {

    @Override
    public void createFileTypes(@NotNull FileTypeConsumer fileTypeConsumer) {
        fileTypeConsumer.consume(SvgaFileType.INSTANCE);
    }
}
