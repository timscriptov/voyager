package cafe.adriel.voyager.core.screen

import androidx.compose.runtime.Composable
import cafe.adriel.voyager.core.platform.multiplatformName

public expect interface Screen {
    public open val key: ScreenKey

    public open val RESULT_OK: Int
    public open val RESULT_CANCEL: Int

    @Composable
    public fun Content()
    public open fun onResult(requestCode: Int, resultCode: Int, data: Any?)
}

internal fun Screen.commonKeyGeneration() =
    this::class.multiplatformName ?: error("Default ScreenKey not found, please provide your own key")
