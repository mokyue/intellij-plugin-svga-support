package cc.moky.intellij.plugin.svga

/*******************************************************************************
 * Created on: 2023/2/7 13:49
 * Author: Moky
 * Mail: mokyue@163.com
 *******************************************************************************/

import com.intellij.lang.Language

internal class Svga private constructor() : Language("SVGA") {

    companion object {

        @JvmField
        val INSTANCE: Svga = Svga()
    }
}
