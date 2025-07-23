import React from 'react';
import { StyleSheet, TouchableOpacity } from 'react-native';
import { useRouter } from 'expo-router';
import { MaterialCommunityIcons } from '@expo/vector-icons';

export default function CustomFAB() {
  const router = useRouter();
  const handlePress = () => {
    router.push('/camerascreen');
  };
  return (
    <TouchableOpacity style={styles.fab} onPress={handlePress}>
      <MaterialCommunityIcons name="plus" size={32} color="#fff" />
    </TouchableOpacity>
  );
}

const styles = StyleSheet.create({
  fab: {
    position: 'absolute',
    right: 28,
    bottom: 90,
    backgroundColor: '#6C63FF',
    width: 64,
    height: 64,
    borderRadius: 32,
    alignItems: 'center',
    justifyContent: 'center',
    shadowColor: '#000',
    shadowOffset: { width: 0, height: 4 },
    shadowOpacity: 0.18,
    shadowRadius: 8,
    elevation: 8,
    zIndex: 10,
  },
}); 