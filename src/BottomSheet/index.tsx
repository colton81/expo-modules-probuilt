import { requireNativeView } from 'expo';
import { Dimensions, NativeSyntheticEvent, Platform } from 'react-native';

import { Host } from '../Host';
export type PresentationDetent = 
  | 'medium' 
  | 'large' 
  | 'auto'
  | `height:${number}`
  | `fraction:${number}`;
export type BottomSheetProps = {
  /**
   * The children of the `BottomSheet` component.
   */
  children: any;
  /**
   * Whether the `BottomSheet` is opened.
   */
  isOpened: boolean;
  /**
   * Callback function that is called when the `BottomSheet` is opened.
   */
  onIsOpenedChange: (isOpened: boolean) => void;
  detents: Array<PresentationDetent>;
};

type NativeBottomSheetProps = Omit<BottomSheetProps, 'onIsOpenedChange'> & {
  onIsOpenedChange: (event: NativeSyntheticEvent<{ isOpened: boolean }>) => void;
};

const BottomSheetNativeView: React.ComponentType<NativeBottomSheetProps> = Platform.OS === "ios" ? requireNativeView(
  'ExpoProbuiltUi',
  'BottomSheetView'
) : requireNativeView<NativeBottomSheetProps>('ExpoProbuiltUi','AndroidBottomSheet');

export function transformBottomSheetProps(props: BottomSheetProps): NativeBottomSheetProps {
  return {
    ...props,
    onIsOpenedChange: ({ nativeEvent: { isOpened } }) => {
      props?.onIsOpenedChange?.(isOpened);
    },
  
  };
}

/**
 * `<BottomSheet>` component without a host view.
 * You should use this with a `Host` component in ancestor.
 */
export function BottomSheetPrimitive(props: BottomSheetProps) {
  return <BottomSheetNativeView {...transformBottomSheetProps(props)} />;
}

export function BottomSheet(props: BottomSheetProps) {
  const { width } = Dimensions.get('window');
  if(Platform.OS === "android"){
    return(
      <BottomSheetPrimitive {...props} />
    )
  }else{
    return (
    <Host style={{ position: 'absolute', width }}>
      <BottomSheetPrimitive {...props} />
    </Host>
  );
  }
  
}
