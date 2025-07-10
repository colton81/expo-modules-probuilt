import {BottomSheet } from 'expo-probuilt-ui';
import { useState } from 'react'
import { Button, ScrollView, Text, View } from 'react-native'

export default function App() {
  var [openSheet, setOpenSheet] = useState(false);
  return <View style={{ flex:1,alignItems: 'center', justifyContent: 'center',padding:50 }}> 

    <BottomSheet detents={['height:100','medium','large']} isOpened={openSheet} onIsOpenedChange={(isOpened) => {
      console.log('isOpened', isOpened);
      setOpenSheet(isOpened);
    }}>
      <View style={{ flex: 1, alignItems: 'center', justifyContent: 'center', padding: 20 }}>
        {/* Navigation Header */}
        <View style={{ height: 100, alignItems: 'center', justifyContent: 'center' }}>
          <Text style={{ fontSize: 20, color: '#0000ff' }}>Hello World!</Text>
        </View>
        <Text style={{fontSize: 20, color: '#0000ff'}}>Hello World!</Text>
        <Text style={{fontSize: 20, color: '#0000ff'}}>Hello World!</Text>
        <Text style={{fontSize: 20, color: '#0000ff'}}>Hello World!</Text>

      </View>
    </BottomSheet>
     <Button title="Open Bottom Sheet" onPress={() => { setOpenSheet(true); }} />
  </View>
}
