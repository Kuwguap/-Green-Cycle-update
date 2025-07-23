import AntDesign from '@expo/vector-icons/AntDesign';
import { useRouter } from 'expo-router';
import React, { useEffect } from 'react';
import { StyleSheet, TouchableOpacity } from 'react-native';
import Animated, { runOnJS, useAnimatedStyle, useSharedValue, withTiming } from 'react-native-reanimated';

export default function StaticFloatingButton() {
  const opacity = useSharedValue(1);
  const router = useRouter();
  let idleTimer: ReturnType<typeof setTimeout>;


  
  const resetIdleTimer = () => {
    clearTimeout(idleTimer);
    opacity.value = withTiming(1); // make visible
    idleTimer = setTimeout(() => {
      opacity.value = withTiming(0.3, { duration: 500 }); // fade when idle
    }, 3000);
  };

  useEffect(() => {
    resetIdleTimer();
    return () => clearTimeout(idleTimer);
  }, []);

  const animatedStyle = useAnimatedStyle(() => ({
    opacity: opacity.value,
  }));

   const HandlePlusPress = () => {
    runOnJS(resetIdleTimer)();
    router.push('/camerascreen')
  }

  return (
    <Animated.View style={[styles.floatingButton, animatedStyle]}>
      <TouchableOpacity
        onPress={HandlePlusPress}
        onPressIn={() => (opacity.value = withTiming(1))}
      >
       <AntDesign name="plus" size={30} color="white" />
      </TouchableOpacity>
    </Animated.View>
  );
}

const styles = StyleSheet.create({
  floatingButton: {
    position: 'absolute',
    right: 20,
    bottom: 40, // Adjust based on tab bar height
    width: 60,
    height: 60,
    backgroundColor: '#000',
    borderRadius: 30,
    justifyContent: 'center',
    alignItems: 'center',
    elevation: 5,
    zIndex: 999,
  },
});
