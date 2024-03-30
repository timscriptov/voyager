package cafe.adriel.voyager.navigator.bottomSheet.internal

import androidx.compose.material3.ExperimentalMaterial3Api
import androidx.compose.runtime.Composable
import cafe.adriel.voyager.navigator.bottomSheet.BottomSheetNavigator
import cafe.adriel.voyager.navigator.bottomSheet.compositions.ModalBottomSheetState

@Composable
internal expect fun BackHandler(enabled: Boolean, onBack: () -> Unit)

@OptIn(ExperimentalMaterial3Api::class)
@Composable
internal fun BottomSheetNavigatorBackHandler(
    navigator: BottomSheetNavigator,
    sheetState: ModalBottomSheetState,
    hideOnBackPress: Boolean
) {
    BackHandler(enabled = sheetState.isVisible) {
        if (navigator.pop().not() && hideOnBackPress) {
            navigator.hide()
        }
    }
}
