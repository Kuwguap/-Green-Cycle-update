import React from 'react';
import { FlatList, StyleSheet, Text, View } from 'react-native';

const dummyDumps = [
  { id: '1', title: 'Old Garbage Site', location: 'Accra, Ghana' },
  { id: '2', title: 'Illegal Dump Area', location: 'Kumasi, Ghana' },
  { id: '3', title: 'Roadside Waste', location: 'Tamale, Ghana' },
  { id: '4', title: 'Blocked Drain', location: 'Takoradi, Ghana' },
  { id: '5', title: 'Plastic Heap', location: 'Cape Coast, Ghana' },
];

export default function ListViewComponent() {
  return (
    <View style={styles.container}>
      <FlatList
        data={dummyDumps}
        keyExtractor={(item) => item.id}
        renderItem={({ item }) => (
          <View style={styles.card}>
            <Text style={styles.title}>{item.title}</Text>
            <Text style={styles.subtitle}>{item.location}</Text>
          </View>
        )}
        contentContainerStyle={{ paddingBottom: 20 }}
      />
    </View>
  );
}

const styles = StyleSheet.create({
  container: {
    flex: 1,
    paddingHorizontal: 16,
    paddingTop: 10,
    backgroundColor: '#f2f2f2',
  },
  card: {
    backgroundColor: '#fff',
    padding: 16,
    borderRadius: 10,
    marginBottom: 12,
    shadowColor: '#000',
    shadowOpacity: 0.05,
    shadowOffset: { width: 0, height: 2 },
    shadowRadius: 4,
    elevation: 3,
  },
  title: {
    fontSize: 18,
    fontWeight: '600',
    marginBottom: 4,
  },
  subtitle: {
    fontSize: 14,
    color: '#555',
  },
});
