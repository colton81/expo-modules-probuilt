import ExpoModulesCore

public class ExpoProbuiltUiModule: Module {
 
  public func definition() -> ModuleDefinition {
  
    Name("ExpoProbuiltUi")

    View(BottomSheetView.self)
    View(HostView.self)
   
  }
}
