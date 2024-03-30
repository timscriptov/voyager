package cafe.adriel.voyager.core.screen

import androidx.compose.runtime.Composable

public actual interface Screen {
    public actual val key: ScreenKey
        get() = commonKeyGeneration()

    public actual val RESULT_OK: Int
        get() = -1
    public actual val RESULT_CANCEL: Int
        get() = 0

    @Composable
    public actual fun Content()

    public actual fun onResult(requestCode: Int, resultCode: Int, data: Any?) {

    }
}
