import Separator from '@/components/Separator'
import React from 'react'
import { Linking, StyleSheet, Text, View } from 'react-native'
import { Button } from 'react-native-paper'

export default function AboutUs() {
    return (
        <View>
            <Text style={{color: '#4CC075', fontWeight: '500', textAlign: 'center', fontSize: 40, marginTop: 100}}>Green Cycle</Text>
            <Separator />
            <Text style={styles.text}>GreenCycle is an environment project which aims to map and monitor all illegal dumps in Ghana</Text>
            <Text style={styles.text}>Our goal is to involve citizens, governments, municipalities and organizations so we can together contribute to cleaner world for everyone</Text>
            <View style={styles.button}>
            <Button mode='contained' onPress={() => Linking.openURL('https://www.trashout.ngo/policy')}>SUPPORT US</Button>
            <Button mode='contained' onPress={() => Linking.openURL('https://www.trashout.ngo/policy')}>PRIVACY POLICY</Button>
            <Button mode='contained' onPress={() => Linking.openURL('https://www.trashout.ngo/policy')}>TERMS AND CONDITIONS</Button>
            </View>
        </View>
    )
}

const styles = StyleSheet.create({
 text: {
   textAlign: 'center',  
   marginHorizontal: '5%',
   fontSize: 20,
   marginTop: 20
 },
 button: {
    marginTop: 30,
    flexDirection: 'column',
    gap: 10,
    marginHorizontal: '15%'
 }
})