package cafe.adriel.voyager.core.screen

import androidx.compose.runtime.Composable
import java.io.Serializable

public actual interface Screen : Serializable {
    public actual val RESULT_OK: Int
        get() = -1

    public actual val RESULT_CANCEL: Int
        get() = 0

    public actual val key: ScreenKey
        get() = commonKeyGeneration()

    @Composable
    public actual fun Content()

    public actual fun onResult(requestCode: Int, resultCode: Int, data: Any?) {

    }
}
