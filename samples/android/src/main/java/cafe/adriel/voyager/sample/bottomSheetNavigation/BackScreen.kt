package cafe.adriel.voyager.sample.bottomSheetNavigation

import android.util.Log
import android.widget.Toast
import androidx.compose.foundation.layout.Box
import androidx.compose.foundation.layout.fillMaxSize
import androidx.compose.material.Button
import androidx.compose.material.Text
import androidx.compose.runtime.Composable
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.platform.LocalContext
import cafe.adriel.voyager.core.screen.Screen
import cafe.adriel.voyager.navigator.bottomSheet.LocalBottomSheetNavigator
import cafe.adriel.voyager.sample.basicNavigation.BasicNavigationScreen

class BackScreen : Screen {
    companion object {
        const val HELLO_REQUEST_CODE = 1
    }

    @Composable
    override fun Content() {
        val bottomSheetNavigator = LocalBottomSheetNavigator.current
        val context = LocalContext.current
        bottomSheetNavigator.getResult(HELLO_REQUEST_CODE).value?.let { result ->
            if (result.resultCode == RESULT_OK) {
                val data = result.data
                if (data != null && data is String) {
                    Log.e("getResult", "BackScreen $data")
                    Toast.makeText(context, "BackScreen $data", Toast.LENGTH_SHORT).show()
                    bottomSheetNavigator.clearResults()
                }
            }
        }
        Box(
            contentAlignment = Alignment.Center,
            modifier = Modifier.fillMaxSize()
        ) {
            Button(
                onClick = { bottomSheetNavigator.show(BasicNavigationScreen(index = 0, wrapContent = true)) }
            ) {
                Text(text = "Show BottomSheet")
            }
        }
    }
}
