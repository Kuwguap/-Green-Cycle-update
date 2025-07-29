import { useRouter } from 'expo-router';
import { useEffect, useState, useRef } from 'react';
import { Image, StyleSheet, Text, View, Animated, Easing } from 'react-native';

export default function Splashscreen() {
  const router = useRouter();
  const [text, setText] = useState('');
  const fullText = 'GREEN CYCLE';
  const spinValue = useRef(new Animated.Value(0)).current;
  const scaleValue = useRef(new Animated.Value(0.5)).current;

  // Typewriter effect
  useEffect(() => {
    let currentIndex = 0;
    const typingInterval = setInterval(() => {
      if (currentIndex < fullText.length) {
        setText(fullText.substring(0, currentIndex + 1));
        currentIndex++;
      } else {
        clearInterval(typingInterval);
      }
    }, 150); // Adjust typing speed here

    return () => clearInterval(typingInterval);
  }, []);

  // Rotation and zoom animation
  useEffect(() => {
    // Spin animation
    Animated.timing(spinValue, {
      toValue: 1,
      duration: 2000,
      easing: Easing.linear,
      useNativeDriver: true,
    }).start();

    // Scale animation
    Animated.timing(scaleValue, {
      toValue: 1,
      duration: 1500,
      easing: Easing.out(Easing.cubic),
      useNativeDriver: true,
    }).start();

    // Navigate to auth screen after animations
    const timeout = setTimeout(() => {
      router.replace('/auth');
    }, 3000); // Increased timeout to allow animations to complete

    return () => clearTimeout(timeout);
  }, []);

  // Interpolate rotation
  const spin = spinValue.interpolate({
    inputRange: [0, 1],
    outputRange: ['0deg', '360deg'],
  });

  return (
    <View style={styles.container}>
      <Animated.View style={{
        transform: [
          { rotate: spin },
          { scale: scaleValue }
        ]
      }}>
        <Image
          source={require('../assets/images/greenpng.png')}
          resizeMode='contain'
          style={styles.logo}
        />
      </Animated.View>
      <Text style={styles.text}>{text}</Text>
    </View>
  );
}

const styles = StyleSheet.create({
  container: {
    backgroundColor: '#1ED760',
    flex: 1,
    alignItems: 'center',
    justifyContent: 'center',
  },
  text: {
    fontWeight: '700',
    fontSize: 28,
    color: 'white',
    marginTop: 18,
    letterSpacing: 2,
  },
  logo: {
    width: 120,
    height: 120,
  },
});