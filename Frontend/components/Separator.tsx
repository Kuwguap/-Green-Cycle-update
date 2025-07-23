import React from 'react';
import { StyleSheet, View } from 'react-native';

export default function Separator() {
  return <View style={styles.separator} />;
}

const styles = StyleSheet.create({
  separator: {
    borderBottomColor: 'white',
    borderBottomWidth: StyleSheet.hairlineWidth,
    marginVertical: 10,
    marginHorizontal: 15,
  },
});
