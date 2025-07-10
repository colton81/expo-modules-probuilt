// android/src/main/java/expo/modules/probuiltui/ExpoProbuiltUiModule.kt
package expo.modules.probuiltui

import expo.modules.kotlin.modules.Module
import expo.modules.kotlin.modules.ModuleDefinition

class ExpoProbuiltUiModule : Module() {

  override fun definition() = ModuleDefinition {

    Name("ExpoProbuiltUi")

    View(AndroidBottomSheet::class) {
      Prop("isOpened") { view: AndroidBottomSheet, isOpened: Boolean ->
        view.isOpened = isOpened
      }
      
      Prop("detents") { view: AndroidBottomSheet, detents: Array<String> ->
        view.detents = detents
      }
      
      Events("onIsOpenedChange")
    }
  }
}