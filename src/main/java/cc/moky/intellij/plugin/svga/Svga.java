package cc.moky.intellij.plugin.svga;

/*******************************************************************************
 * Created on: 2019/7/16 9:47
 * Author: Moky
 * Mail: mokyue@163.com
 *******************************************************************************/

import com.intellij.lang.Language;

class Svga extends Language {

    private static final String ID = "SVGA";
    static final Svga INSTANCE = new Svga();

    private Svga() {
        super(ID);
    }
}
