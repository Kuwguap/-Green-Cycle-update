import React from 'react';
import { View, Text, StyleSheet, TouchableOpacity } from 'react-native';
import { Card, Avatar, IconButton } from 'react-native-paper';

const recent = [
  { id: '1', title: 'Bin Report', subtitle: '2 hours ago', icon: 'delete', color: '#6C63FF' },
  { id: '2', title: 'Marketplace', subtitle: '5 hours ago', icon: 'cart', color: '#00C853' },
  { id: '3', title: 'Adopted Spot', subtitle: '1 day ago', icon: 'map-marker', color: '#FFD600' },
];

export default function RecentlyVisitedCard() {
  return (
    <Card style={styles.card}>
      <View style={styles.headerRow}>
        <Text style={styles.header}>Recently visited</Text>
        <IconButton icon="arrow-right" size={24} style={styles.arrowBtn} />
      </View>
      <View style={styles.list}>
        {recent.map((item) => (
          <TouchableOpacity key={item.id} style={styles.itemRow}>
            <Avatar.Icon icon={item.icon} size={38} style={{ backgroundColor: item.color, marginRight: 12 }} />
            <View style={{ flex: 1 }}>
              <Text style={styles.title}>{item.title}</Text>
              <Text style={styles.subtitle}>{item.subtitle}</Text>
            </View>
            <View style={styles.dot} />
            <IconButton icon="star-outline" size={22} style={styles.starBtn} />
          </TouchableOpacity>
        ))}
      </View>
    </Card>
  );
}

const styles = StyleSheet.create({
  card: {
    marginHorizontal: 18,
    marginTop: 8,
    borderRadius: 24,
    backgroundColor: '#fff',
    paddingBottom: 8,
    elevation: 0,
    shadowOpacity: 0,
  },
  headerRow: {
    flexDirection: 'row',
    justifyContent: 'space-between',
    alignItems: 'center',
    marginTop: 18,
    marginHorizontal: 18,
  },
  header: {
    color: '#222',
    fontSize: 18,
    fontWeight: '700',
  },
  arrowBtn: {
    backgroundColor: '#F2F4F8',
    borderRadius: 12,
  },
  list: {
    marginTop: 8,
    marginBottom: 2,
  },
  itemRow: {
    flexDirection: 'row',
    alignItems: 'center',
    backgroundColor: '#F6F8FB',
    borderRadius: 16,
    marginHorizontal: 12,
    marginVertical: 6,
    padding: 8,
    shadowOpacity: 0,
  },
  title: {
    fontSize: 15,
    fontWeight: '600',
    color: '#222',
  },
  subtitle: {
    fontSize: 12,
    color: '#888',
    fontWeight: '400',
    marginTop: 1,
  },
  dot: {
    width: 8,
    height: 8,
    borderRadius: 4,
    backgroundColor: '#6C63FF',
    marginHorizontal: 8,
  },
  starBtn: {
    backgroundColor: 'rgba(255,255,255,0.15)',
    borderRadius: 12,
  },
}); 