import { useRouter } from 'expo-router';
import { useEffect } from 'react';
import { Image, StyleSheet, Text, View } from 'react-native';

export default function Splashscreen() {
  const router = useRouter();

  useEffect(() => {
    const timeout = setTimeout(() => {
      router.replace('/auth');
    }, 1200);

    return () => clearTimeout(timeout);
  }, []);

  return (
    <View style={styles.container}>
      <Image
        source={require('../assets/images/greenpng.png')}
        resizeMode='contain'
        style={styles.logo}
      />
      <Text style={styles.text}>GREEN CYCLE</Text>
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