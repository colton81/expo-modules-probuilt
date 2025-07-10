// android/src/main/java/expo/modules/probuiltui/AndroidBottomSheet.kt
package expo.modules.probuiltui

import android.content.Context
import androidx.compose.foundation.layout.*
import androidx.compose.material3.*
import androidx.compose.runtime.*
import androidx.compose.ui.Modifier
import androidx.compose.ui.layout.onSizeChanged
import androidx.compose.ui.platform.LocalDensity
import androidx.compose.ui.unit.Dp
import androidx.compose.ui.unit.dp
import kotlinx.coroutines.launch
import expo.modules.kotlin.AppContext
import expo.modules.kotlin.viewevent.EventDispatcher
import expo.modules.kotlin.views.ExpoComposeView

@OptIn(ExperimentalMaterial3Api::class)
class AndroidBottomSheet(context: Context, appContext: AppContext) : ExpoComposeView(context, appContext) {
  
  private val onIsOpenedChange by EventDispatcher()
  
  var isOpened: Boolean = false
    set(value) {
      field = value
      // Trigger recomposition when isOpened changes
      invalidate()
    }
  
  var detents: Array<String> = arrayOf("medium")
    set(value) {
      field = value
      // Trigger recomposition when detents change
      invalidate()
    }
  
  @Composable
  override fun Content() {
    if (isOpened) {
      BottomSheetModal()
    }
  }
  
  @Composable
  private fun BottomSheetModal() {
    val density = LocalDensity.current
    val scope = rememberCoroutineScope()
    
    var showModal by remember { mutableStateOf(isOpened) }
    
    val sheetState = rememberModalBottomSheetState(
      skipPartiallyExpanded = detents.size == 1
    )
    
    var contentHeight by remember { mutableStateOf(0.dp) }
    
    LaunchedEffect(isOpened) {
      showModal = isOpened
    }
    
    LaunchedEffect(showModal) {
      if (showModal && !sheetState.isVisible) {
        sheetState.show()
      } else if (!showModal && sheetState.isVisible) {
        scope.launch {
          sheetState.hide()
        }.invokeOnCompletion {
          if (!sheetState.isVisible) {
            onIsOpenedChange(mapOf("isOpened" to false))
          }
        }
      }
    }
    
    fun convertDetents(detentStrings: Array<String>): Dp {
      val screenHeight = with(density) {
        context.resources.displayMetrics.heightPixels.toDp()
      }
      
      var maxHeight = 0.dp
      
      for (detentString in detentStrings) {
        val height = when (detentString.lowercase()) {
          "medium" -> screenHeight * 0.5f
          "large" -> screenHeight * 0.9f
          "auto" -> if (contentHeight > 0.dp) contentHeight else 400.dp
          else -> {
            when {
              detentString.startsWith("height:") -> {
                val heightString = detentString.substringAfter("height:")
                heightString.toFloatOrNull()?.dp ?: 0.dp
              }
              detentString.startsWith("fraction:") -> {
                val fractionString = detentString.substringAfter("fraction:")
                fractionString.toFloatOrNull()?.let { fraction ->
                  screenHeight * fraction
                } ?: 0.dp
              }
              else -> 0.dp
            }
          }
        }
        
        if (height > maxHeight) {
          maxHeight = height
        }
      }
      
      return if (maxHeight > 0.dp) maxHeight else screenHeight * 0.5f
    }
    
    val sheetHeight = remember(detents, contentHeight) {
      convertDetents(detents)
    }
    
    if (showModal) {
      ModalBottomSheet(
        onDismissRequest = {
          showModal = false
          onIsOpenedChange(mapOf("isOpened" to false))
        },
        sheetState = sheetState,
        modifier = Modifier.heightIn(max = sheetHeight)
      ) {
        Column(
          modifier = Modifier
            .fillMaxWidth()
            .onSizeChanged { size ->
              with(density) {
                val newHeight = size.height.toDp()
                if (newHeight != contentHeight && newHeight > 0.dp) {
                  contentHeight = newHeight
                }
              }
            }
        ) {
          // Render React Native children
          Children()
        }
      }
    }
  }
}