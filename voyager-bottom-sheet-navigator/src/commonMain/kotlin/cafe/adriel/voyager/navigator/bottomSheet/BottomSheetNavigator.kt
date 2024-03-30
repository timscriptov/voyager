package cafe.adriel.voyager.navigator.bottomSheet

import androidx.compose.animation.core.AnimationSpec
import androidx.compose.foundation.layout.Spacer
import androidx.compose.foundation.layout.height
import androidx.compose.material3.ExperimentalMaterial3Api
import androidx.compose.material3.MaterialTheme
import androidx.compose.material3.contentColorFor
import androidx.compose.runtime.*
import androidx.compose.ui.Modifier
import androidx.compose.ui.graphics.Color
import androidx.compose.ui.graphics.Shape
import androidx.compose.ui.unit.Dp
import androidx.compose.ui.unit.dp
import cafe.adriel.voyager.core.annotation.InternalVoyagerApi
import cafe.adriel.voyager.core.screen.Screen
import cafe.adriel.voyager.core.stack.Stack
import cafe.adriel.voyager.navigator.CurrentScreen
import cafe.adriel.voyager.navigator.Navigator
import cafe.adriel.voyager.navigator.bottomSheet.compositions.*
import cafe.adriel.voyager.navigator.bottomSheet.internal.BottomSheetNavigatorBackHandler
import cafe.adriel.voyager.navigator.compositionUniqueId
import kotlinx.coroutines.CoroutineScope
import kotlinx.coroutines.launch

public typealias BottomSheetNavigatorContent = @Composable (bottomSheetNavigator: BottomSheetNavigator) -> Unit

public val LocalBottomSheetNavigator: ProvidableCompositionLocal<BottomSheetNavigator> =
    staticCompositionLocalOf { error("BottomSheetNavigator not initialized") }

@OptIn(ExperimentalMaterial3Api::class)
@Composable
public fun BottomSheetNavigator(
    modifier: Modifier = Modifier,
    hideOnBackPress: Boolean = true,
    scrimColor: Color = ModalBottomSheetDefaults.scrimColor,
    sheetShape: Shape = MaterialTheme.shapes.large,
    sheetElevation: Dp = ModalBottomSheetDefaults.Elevation,
    sheetBackgroundColor: Color = MaterialTheme.colorScheme.surface,
    sheetContentColor: Color = contentColorFor(sheetBackgroundColor),
    skipHalfExpanded: Boolean = true,
    animationSpec: AnimationSpec<Float> = SwipeableDefaults.AnimationSpec,
    key: String = compositionUniqueId(),
    sheetContent: BottomSheetNavigatorContent = { CurrentScreen() },
    content: BottomSheetNavigatorContent
) {
    var hideBottomSheet: (() -> Unit)? = null
    val coroutineScope = rememberCoroutineScope()
    val sheetState = rememberModalBottomSheetState(
        initialValue = ModalBottomSheetValue.Hidden,
        skipHalfExpanded = skipHalfExpanded,
        animationSpec = animationSpec
    )

    LaunchedEffect(sheetState, sheetState.currentValue) {
        if (sheetState.currentValue == ModalBottomSheetValue.Hidden) {
            hideBottomSheet?.invoke()
        }
    }

    Navigator(HiddenBottomSheetScreen, onBackPressed = null, key = key) { navigator ->
        val bottomSheetNavigator = remember(navigator, sheetState, coroutineScope) {
            BottomSheetNavigator(navigator, sheetState, coroutineScope)
        }

        hideBottomSheet = bottomSheetNavigator::hide

        CompositionLocalProvider(LocalBottomSheetNavigator provides bottomSheetNavigator) {
            ModalBottomSheetLayout(
                modifier = modifier,
                scrimColor = scrimColor,
                sheetState = sheetState,
                sheetShape = sheetShape,
                sheetElevation = sheetElevation,
                sheetBackgroundColor = sheetBackgroundColor,
                sheetContentColor = sheetContentColor,
                sheetContent = {
                    BottomSheetNavigatorBackHandler(bottomSheetNavigator, sheetState, hideOnBackPress)
                    sheetContent(bottomSheetNavigator)
                },
                content = {
                    content(bottomSheetNavigator)
                }
            )
        }
    }
}

public class BottomSheetNavigator @OptIn(ExperimentalMaterial3Api::class)
@InternalVoyagerApi constructor(
    private val navigator: Navigator,
    private val sheetState: ModalBottomSheetState,
    private val coroutineScope: CoroutineScope
) : Stack<Screen> by navigator {
    private val results = mutableStateMapOf<Int, OnResult?>()

    public data class OnResult(
        val resultCode: Int,
        val data: Any?,
    )

    @OptIn(ExperimentalMaterial3Api::class)
    public val isVisible: Boolean
        get() = sheetState.isVisible

    @OptIn(ExperimentalMaterial3Api::class)
    public fun show(screen: Screen) {
        coroutineScope.launch {
            replaceAll(screen)
            sheetState.expand()
        }
    }

    @OptIn(ExperimentalMaterial3Api::class)
    public fun hide() {
        coroutineScope.launch {
            if (isVisible) {
                sheetState.hide()
                replaceAll(HiddenBottomSheetScreen)
            } else if (!sheetState.isVisible) {
                // Swipe down - sheetState is already hidden here so `isVisible` is false
                replaceAll(HiddenBottomSheetScreen)
            }
        }
    }

    @OptIn(ExperimentalMaterial3Api::class)
    public fun hideWithResult(requestCode: Int, resultCode: Int, data: Any?) {
        coroutineScope.launch {
            results[requestCode] = OnResult(resultCode, data)
            sheetState.hide()
            replaceAll(HiddenBottomSheetScreen)
        }
    }

    @Composable
    public fun getResult(requestCode: Int): State<OnResult?> {
        val result = results[requestCode]
        val resultState = remember(requestCode, result) {
            derivedStateOf {
                results -= requestCode
                result
            }
        }
        return resultState
    }

    public fun clearResults() {
        results.clear()
    }

    @Composable
    public fun saveableState(
        key: String,
        content: @Composable () -> Unit
    ) {
        navigator.saveableState(key, content = content)
    }
}

private object HiddenBottomSheetScreen : Screen {

    @Composable
    override fun Content() {
        Spacer(modifier = Modifier.height(1.dp))
    }
}
