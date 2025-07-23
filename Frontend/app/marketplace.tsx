import React, { useState } from 'react';
import { View, Text, StyleSheet, FlatList, Image, TouchableOpacity } from 'react-native';

// Mock data for marketplace items
const items = [
  {
    id: '1',
    title: 'Plastic Voltic Bottles',
    description: 'Reusable upcycled bottle',
    category: 'Upcycling',
    price: 80,
    photo: require('../assets/images/greencycle.png'),
    artisan: { name: 'Felix', verified: true },
  },
  {
    id: '2',
    title: 'Broken Chairs',
    description: 'Wooden chair stands',
    category: 'Recycling',
    price: 100,
    photo: require('../assets/images/greencycle.png'),
    artisan: { name: 'Suhair', verified: false },
  },
  {
    id: '3',
    title: 'Old Clothes',
    description: 'Upcycled glass jar for plants',
    category: 'Upcycling',
    price: 50,
    photo: require('../assets/images/greencycle.png'),
    artisan: { name: 'Jodey', verified: true },
  },
];

const categories = ['All', 'Upcycling', 'Recycling'];

export default function MarketplaceScreen() {
  const [selectedCategory, setSelectedCategory] = useState('All');

  const filteredItems = selectedCategory === 'All'
    ? items
    : items.filter(item => item.category === selectedCategory);

  return (
    <View style={{ flex: 1, backgroundColor: '#F6F8FB' }}>
      <Text style={styles.header}>Marketplace</Text>
      <View style={styles.rowLayout}>
        {/* Category Column */}
        <View style={styles.categoryColumn}>
          {categories.map(cat => (
            <TouchableOpacity
              key={cat}
              style={[styles.categoryBtn, selectedCategory === cat && styles.categoryBtnActive]}
              onPress={() => setSelectedCategory(cat)}
            >
              <Text style={[styles.categoryText, selectedCategory === cat && styles.categoryTextActive]} numberOfLines={1} ellipsizeMode="tail">{cat}</Text>
            </TouchableOpacity>
          ))}
        </View>
        {/* Item List */}
        <FlatList
          data={filteredItems}
          keyExtractor={item => item.id}
          contentContainerStyle={{ padding: 0, paddingBottom: 90 }}
          style={styles.itemList}
          renderItem={({ item }) => (
            <View style={styles.card}>
              <Image source={item.photo} style={styles.photo} />
              <View style={{ flex: 1, marginLeft: 12 }}>
                <Text style={styles.title}>{item.title}</Text>
                <Text style={styles.desc}>{item.description}</Text>
                <View style={styles.row}>
                  <Text style={styles.price}>GH₵{item.price.toFixed(2)}</Text>
                  <View style={styles.artisanRow}>
                    <Text style={styles.artisanName}>{item.artisan.name}</Text>
                    {item.artisan.verified && <Text style={styles.verified}>✔️</Text>}
                  </View>
                </View>
              </View>
              <TouchableOpacity style={styles.msgBtn}>
                <Text style={styles.msgText}>💬</Text>
              </TouchableOpacity>
            </View>
          )}
        />
      </View>
    </View>
  );
}

const styles = StyleSheet.create({
  header: {
    fontSize: 24,
    fontWeight: '700',
    color: '#222',
    marginTop: 60,
    marginBottom: 8,
    alignSelf: 'center',
  },
  rowLayout: {
    flex: 1,
    flexDirection: 'row',
    alignItems: 'flex-start',
    paddingHorizontal: 8,
  },
  categoryColumn: {
    width: 90,
    marginTop: 8,
    marginRight: 8,
  },
  categoryBtn: {
    backgroundColor: '#fff',
    borderRadius: 16,
    width: 90,
    paddingVertical: 10,
    marginBottom: 10,
    borderWidth: 1,
    borderColor: '#E0E0E0',
    alignItems: 'center',
    justifyContent: 'center',
  },
  categoryBtnActive: {
    backgroundColor: '#4CC075',
    borderColor: '#4CC075',
  },
  categoryText: {
    color: '#888',
    fontWeight: '500',
    fontSize: 14,
    textAlign: 'center',
    flexShrink: 1,
  },
  categoryTextActive: {
    color: '#fff',
  },
  itemList: {
    flex: 1,
    marginLeft: 0,
  },
  card: {
    flexDirection: 'row',
    backgroundColor: '#fff',
    borderRadius: 18,
    padding: 14,
    marginBottom: 16,
    alignItems: 'center',
    shadowColor: '#000',
    shadowOpacity: 0.04,
    shadowRadius: 8,
    elevation: 2,
  },
  photo: {
    width: 64,
    height: 64,
    borderRadius: 12,
    backgroundColor: '#E0E0E0',
  },
  title: {
    fontSize: 16,
    fontWeight: '700',
    color: '#222',
  },
  desc: {
    fontSize: 13,
    color: '#888',
    marginTop: 2,
    marginBottom: 4,
  },
  price: {
    fontSize: 15,
    fontWeight: '700',
    color: '#4CC075',
    marginRight: 10,
  },
  row: {
    flexDirection: 'row',
    alignItems: 'center',
    marginTop: 4,
  },
  artisanRow: {
    flexDirection: 'row',
    alignItems: 'center',
    marginLeft: 8,
  },
  artisanName: {
    fontSize: 13,
    color: '#222',
    fontWeight: '500',
    marginRight: 2,
  },
  verified: {
    fontSize: 13,
    color: '#4CC075',
    fontWeight: '700',
  },
  msgBtn: {
    marginLeft: 10,
    backgroundColor: '#F6F8FB',
    borderRadius: 16,
    padding: 8,
  },
  msgText: {
    fontSize: 18,
  },
}); 