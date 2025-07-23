import React from 'react';
import { View, Text, StyleSheet, ScrollView, TouchableOpacity, FlatList } from 'react-native';
import { Card, IconButton } from 'react-native-paper';

const favorites = [
  { id: '1', title: 'Adopted Spot', subtitle: 'Central Park', color: '#6C63FF' },
  { id: '2', title: 'Marketplace', subtitle: 'Eco Bottle', color: '#00C853' },
  { id: '3', title: 'Waste-as-a-Service', subtitle: 'Professional Pickup', color: '#FF9800' },
];

export default function FavoritesCard() {
  return (
    <Card style={styles.card}>
      <View style={styles.headerRow}>
        <Text style={styles.header}>My favorites</Text>
        <IconButton icon="arrow-right" size={24} style={styles.arrowBtn} />
      </View>
      <FlatList
        horizontal
        showsHorizontalScrollIndicator={false}
        data={[{ id: 'add', isAdd: true }, ...favorites]}
        keyExtractor={item => item.id}
        renderItem={({ item }) =>
          item.isAdd ? (
            <TouchableOpacity style={styles.addCard}>
              <Text style={styles.plus}>+</Text>
            </TouchableOpacity>
          ) : (
            <View key={item.id} style={[styles.favCard, { backgroundColor: item.color }] }>
              <Text style={styles.favTitle}>{item.title}</Text>
              <Text style={styles.favSubtitle}>{item.subtitle}</Text>
            </View>
          )
        }
        contentContainerStyle={{ paddingLeft: 18, marginTop: 12, marginBottom: 8 }}
      />
      <View style={styles.dotsRow}>
        <View style={[styles.dot, { opacity: 1 }]} />
        <View style={[styles.dot, { opacity: 0.3 }]} />
      </View>
    </Card>
  );
}

const styles = StyleSheet.create({
  card: {
    margin: 18,
    marginTop: 24,
    borderRadius: 24,
    backgroundColor: '#6C63FF', // Use a solid color instead of CSS gradient
    padding: 0,
    elevation: 0,
    shadowOpacity: 0,
    // overflow: 'hidden', // Removed to fix VirtualizedLists warning
  },
  headerRow: {
    flexDirection: 'row',
    justifyContent: 'space-between',
    alignItems: 'center',
    marginTop: 18,
    marginHorizontal: 18,
  },
  header: {
    color: '#fff',
    fontSize: 20,
    fontWeight: '700',
  },
  arrowBtn: {
    backgroundColor: 'rgba(255,255,255,0.15)',
    borderRadius: 12,
  },
  scroll: {
    marginTop: 12,
    marginBottom: 8,
    paddingLeft: 18,
  },
  addCard: {
    width: 80,
    height: 90,
    borderRadius: 18,
    backgroundColor: 'rgba(255,255,255,0.18)',
    alignItems: 'center',
    justifyContent: 'center',
    marginRight: 12,
  },
  plus: {
    fontSize: 36,
    color: '#fff',
    fontWeight: '700',
  },
  favCard: {
    width: 120,
    height: 90,
    borderRadius: 18,
    marginRight: 12,
    padding: 12,
    justifyContent: 'center',
  },
  favTitle: {
    color: '#fff',
    fontSize: 16,
    fontWeight: '700',
  },
  favSubtitle: {
    color: '#fff',
    fontSize: 13,
    fontWeight: '400',
    marginTop: 2,
  },
  dotsRow: {
    flexDirection: 'row',
    justifyContent: 'center',
    marginBottom: 12,
    marginTop: 2,
  },
  dot: {
    width: 8,
    height: 8,
    borderRadius: 4,
    backgroundColor: '#fff',
    marginHorizontal: 3,
  },
}); 