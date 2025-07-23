import React, { useState } from 'react';
import { View, StyleSheet, TouchableOpacity, Text } from 'react-native';
import { useRouter } from 'expo-router';
import { MaterialCommunityIcons, Ionicons, FontAwesome5 } from '@expo/vector-icons';
import { BlurView } from 'expo-blur';

const tabs = [
  { key: 'home', label: 'Home', icon: (color: string) => <Ionicons name="home" size={24} color={color} /> },
  { key: 'adopt', label: 'Adopt Spot', icon: (color: string) => <FontAwesome5 name="tree" size={22} color={color} /> },
  { key: 'marketplace', label: 'Market', icon: (color: string) => <MaterialCommunityIcons name="shopping" size={24} color={color} /> },
  { key: 'leaderboard', label: 'Leadboard', icon: (color: string) => <MaterialCommunityIcons name="trophy-award" size={24} color={color} /> },
  { key: 'profile', label: 'Profile', icon: (color: string) => <MaterialCommunityIcons name="account-circle" size={24} color={color} /> },
];

export default function BottomNavBar() {
  const [active, setActive] = useState('home');
  const router = useRouter();

  const handleTab = (key: string) => {
    setActive(key);
    router.replace(`/${key}` as any);
  };

  return (
    <BlurView intensity={40} tint="light" style={styles.blurContainer}>
      <View style={styles.container}>
        {tabs.map((tab) => (
          <TouchableOpacity
            key={tab.key}
            style={styles.tab}
            onPress={() => handleTab(tab.key)}
          >
            {tab.icon(active === tab.key ? '#6C63FF' : '#B0B3C7')}
            <Text style={[styles.label, active === tab.key && styles.activeLabel]}>{tab.label}</Text>
          </TouchableOpacity>
        ))}
      </View>
    </BlurView>
  );
}

const styles = StyleSheet.create({
  blurContainer: {
    position: 'absolute',
    left: 16,
    right: 16,
    bottom: 24,
    borderRadius: 24,
    overflow: 'hidden',
    zIndex: 100,
    borderWidth: 2,
    borderColor: 'rgba(255, 255, 255, 0.99)', // Thin white outline for visibility
  },
  container: {
    flexDirection: 'row',
    justifyContent: 'space-around',
    alignItems: 'center',
    backgroundColor: 'rgba(255,255,255,0.35)',
    borderRadius: 24,
    paddingVertical: 8,
    shadowColor: '#000',
    shadowOffset: { width: 0, height: 2 },
    shadowOpacity: 0.10,
    shadowRadius: 12,
    elevation: 8,
  },
  tab: {
    alignItems: 'center',
    flex: 1,
  },
  label: {
    fontSize: 12,
    color: '#B0B3C7',
    marginTop: 2,
    fontWeight: '500',
    textAlign: 'center',
  },
  activeLabel: {
    color: '#6C63FF',
    fontWeight: '700',
  },
}); 