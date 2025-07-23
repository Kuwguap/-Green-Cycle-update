import React from 'react';
import { View, Text, Image, StyleSheet } from 'react-native';
import { IconButton } from 'react-native-paper';
import { SafeAreaView } from 'react-native-safe-area-context';

export default function TopNavBar({ showSearch = true }: { showSearch?: boolean }) {
  return (
    <SafeAreaView edges={['top']} style={{ backgroundColor: '#fff' }}>
      <View style={styles.container}>
        <View style={styles.logoRow}>
          <Image source={require('../assets/images/greencycle.png')} style={styles.logo} />
          <View>
            <Text style={styles.title}>Green Cycle</Text>
            <Text style={styles.subtitle}>environmental app</Text>
          </View>
        </View>
        {showSearch && <IconButton icon="magnify" size={28} style={styles.searchIcon} />}
      </View>
    </SafeAreaView>
  );
}

const styles = StyleSheet.create({
  container: {
    flexDirection: 'row',
    alignItems: 'center',
    justifyContent: 'space-between',
    backgroundColor: '#fff',
    paddingHorizontal: 18,
    paddingTop: 18,
    paddingBottom: 10,
    shadowColor: '#000',
    shadowOffset: { width: 0, height: 2 },
    shadowOpacity: 0.07,
    shadowRadius: 8,
    elevation: 2,
    borderBottomLeftRadius: 24,
    borderBottomRightRadius: 24,
  },
  logoRow: {
    flexDirection: 'row',
    alignItems: 'center',
  },
  logo: {
    width: 38,
    height: 38,
    marginRight: 10,
    borderRadius: 10,
  },
  title: {
    fontSize: 20,
    fontWeight: '700',
    color: '#222',
    letterSpacing: 0.5,
  },
  subtitle: {
    fontSize: 13,
    color: '#888',
    fontWeight: '400',
    marginTop: -2,
    letterSpacing: 0.2,
  },
  searchIcon: {
    backgroundColor: '#F2F4F8',
    borderRadius: 12,
  },
}); 