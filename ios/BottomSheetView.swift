// Copyright 2025-present 650 Industries. All rights reserved.

import SwiftUI
import ExpoModulesCore

final class BottomSheetProps: ExpoSwiftUI.ViewProps {
  @Field var isOpened: Bool = false
    @Field var detents: [String] = ["medium"]
  var onIsOpenedChange = EventDispatcher()
}

struct HeightPreferenceKey: PreferenceKey {
  static var defaultValue: CGFloat?

  static func reduce(value: inout CGFloat?, nextValue: () -> CGFloat?) {
    guard let nextValue = nextValue() else {
      return
    }
    value = nextValue
  }
}

private struct ReadHeightModifier: ViewModifier {
  private var sizeView: some View {
    GeometryReader { geometry in
      Color.clear.preference(key: HeightPreferenceKey.self, value: geometry.size.height)
    }
  }

  func body(content: Content) -> some View {
    content.background(sizeView)
  }
}

struct BottomSheetView: ExpoSwiftUI.View {
  @ObservedObject var props: BottomSheetProps

  @State private var isOpened: Bool
   
  @State var height: CGFloat = 0

  init(props: BottomSheetProps) {
    self.props = props
    self._isOpened = State(initialValue: props.isOpened)
     
  }
    @available(iOS 16.0, tvOS 16.0, *)
      private func convertDetents() -> Set<PresentationDetent> {
        var presentationDetents: Set<PresentationDetent> = []
        for detentString in props.detents {
          switch detentString.lowercased() {
          case "medium":
            presentationDetents.insert(.medium)
          case "large":
            presentationDetents.insert(.large)
          case "auto":
            presentationDetents.insert(.height(self.height))
          default:
            // If height value is passed as "height:300", parse it
            if detentString.lowercased().hasPrefix("height:") {
              let heightString = String(detentString.dropFirst(7))
              if let customHeight = Double(heightString) {
                presentationDetents.insert(.height(CGFloat(customHeight)))
              }
            }
            // If fraction value is passed as "fraction:0.3", parse it
            else if detentString.lowercased().hasPrefix("fraction:") {
              let fractionString = String(detentString.dropFirst(9))
              if let fractionValue = Double(fractionString) {
              
                  presentationDetents.insert(.fraction(fractionValue))
                
              }
            }
          }
        }
        
        // Default to medium if no valid detents found
        if presentationDetents.isEmpty {
          presentationDetents.insert(.large)
        }
        
        return presentationDetents
      }
  var body: some View {
    if #available(iOS 16.0, tvOS 16.0, *) {
      // When children contain a UIView (UIViewRepresentable),
      // SwiftUI will try to expand the UIView size to match the SwiftUI layout.
      // This breaks the `ReadHeightModifier()` size measurement.
      // In this case, we must measure the current view directly.
      let hasHostingChildren = (props.children ?? []).first { ExpoSwiftUI.isHostingView($0) } != nil

      Rectangle().hidden()
        .if(hasHostingChildren) {
          $0
            .modifier(ReadHeightModifier())
            .onPreferenceChange(HeightPreferenceKey.self) { height in
              if let height {
                self.height = height
              }
            }
        }
        .sheet(isPresented: $isOpened) {
          Children()
            .if(!hasHostingChildren) {
              $0
                .modifier(ReadHeightModifier())
                .onPreferenceChange(HeightPreferenceKey.self) { height in
                  if let height {
                    self.height = height
                  }
                }
            }
            .presentationDetents(convertDetents())
        }
        .onChange(of: isOpened, perform: { newIsOpened in
          if props.isOpened == newIsOpened {
            return
          }
          props.onIsOpenedChange([
            "isOpened": newIsOpened
          ])
        })
        .onChange(of: props.isOpened) { newValue in
          isOpened = newValue
        }
        .onAppear {
          isOpened = props.isOpened
        }
    } else {
      Rectangle().hidden()
        .sheet(isPresented: $isOpened) {
          Children()
          
        }
        .onChange(of: isOpened, perform: { newIsOpened in
          if props.isOpened == newIsOpened {
            return
          }
          props.onIsOpenedChange([
            "isOpened": newIsOpened
          ])
        })
        .onChange(of: props.isOpened) { newValue in
          isOpened = newValue
        }
        .onAppear {
          isOpened = props.isOpened
        }
    }
  }
}

#Preview{
    BottomSheetView(props: .init())
}
