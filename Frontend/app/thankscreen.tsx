import { useRouter } from 'expo-router';
import React from 'react';
import { Image, StyleSheet, Text, View, TouchableOpacity } from 'react-native';
import { SafeAreaView } from 'react-native-safe-area-context';

export default function ThankScreen() {
  const router = useRouter();

  const handleHome = () => router.replace('/home');

  return (
    <SafeAreaView style={{ flex: 1, backgroundColor: '#F6F8FB' }}>
      <View style={styles.container}>
        <View style={styles.card}>
          <Image style={styles.star} source={require('../assets/images/star.png')} />
          <Text style={styles.header}>Thank You!</Text>
          <Text style={styles.reward}>You just gained <Text style={{ color: '#4CC075', fontWeight: '700' }}>10 cycloids</Text></Text>
          <Text style={styles.subtext}>Your report helps make the world cleaner 🌍</Text>
          <View style={styles.buttonRow}>
            <TouchableOpacity style={styles.actionBtn}>
              <Text style={styles.actionText}>Share</Text>
            </TouchableOpacity>
            <TouchableOpacity style={styles.actionBtn}>
              <Text style={styles.actionText}>View Report</Text>
            </TouchableOpacity>
          </View>
        </View>
        <View style={styles.bottomCard}>
          <Text style={styles.bottomText}>Do not forget to Trash Hunt and help clear more dumps! 😉</Text>
          <Image source={require('../assets/images/cartoontree.png')} style={styles.tree} />
          <TouchableOpacity style={styles.homeBtn} onPress={handleHome}>
            <Text style={styles.homeBtnText}>Go Home</Text>
          </TouchableOpacity>
        </View>
      </View>
    </SafeAreaView>
  );
}

const styles = StyleSheet.create({
  container: {
    flex: 1,
    justifyContent: 'flex-start',
    alignItems: 'stretch',
    padding: 0,
  },
  card: {
    backgroundColor: '#fff',
    borderRadius: 24,
    padding: 32,
    alignItems: 'center',
    marginTop: 0,
    marginHorizontal: 18,
    shadowColor: '#000',
    shadowOpacity: 0.04,
    shadowRadius: 8,
    elevation: 2,
  },
  star: {
    width: 80,
    height: 80,
    marginBottom: 12,
    opacity: 0.9,
  },
  header: {
    fontSize: 26,
    fontWeight: '700',
    color: '#222',
    marginBottom: 8,
    textAlign: 'center',
  },
  reward: {
    fontSize: 18,
    fontWeight: '600',
    color: '#222',
    marginBottom: 6,
    textAlign: 'center',
  },
  subtext: {
    fontSize: 15,
    color: '#4CC075',
    marginBottom: 18,
    textAlign: 'center',
  },
  buttonRow: {
    flexDirection: 'row',
    justifyContent: 'center',
    gap: 16,
    marginTop: 10,
  },
  actionBtn: {
    backgroundColor: '#4CC075',
    borderRadius: 18,
    paddingHorizontal: 22,
    paddingVertical: 10,
    marginHorizontal: 6,
  },
  actionText: {
    color: '#fff',
    fontWeight: '700',
    fontSize: 15,
  },
  bottomCard: {
    backgroundColor: '#fff',
    borderRadius: 18,
    marginTop: 32,
    alignItems: 'center',
    padding: 18,
    marginHorizontal: 18,
    elevation: 0,
  },
  bottomText: {
    fontSize: 16,
    color: '#222',
    fontWeight: '600',
    marginBottom: 10,
    textAlign: 'center',
  },
  tree: {
    width: 120,
    height: 90,
    marginBottom: 10,
    alignSelf: 'center',
    opacity: 0.9,
  },
  homeBtn: {
    backgroundColor: '#4CC075',
    borderRadius: 18,
    paddingHorizontal: 32,
    paddingVertical: 12,
    marginTop: 10,
  },
  homeBtnText: {
    color: '#fff',
    fontWeight: '700',
    fontSize: 17,
    textAlign: 'center',
  },
});