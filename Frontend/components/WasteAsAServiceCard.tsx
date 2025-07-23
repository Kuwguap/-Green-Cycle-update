import React from 'react';
import { View, Text, StyleSheet, TouchableOpacity, FlatList } from 'react-native';

const services = [
  { id: 'w1', name: 'Waste Pickup', description: 'Schedule a professional waste pickup at your location.' },
  { id: 'w2', name: 'Recycling Service', description: 'Request recycling bins and regular collection.' },
  { id: 'w3', name: 'Professional Cleaning', description: 'Book a team for large-scale or hazardous waste cleanup.' },
];

export default function WasteAsAServiceCard() {
  return (
    <View style={styles.container}>
      <Text style={styles.header}>Waste-as-a-Service</Text>
      <FlatList
        data={services}
        keyExtractor={item => item.id}
        renderItem={({ item }) => (
          <View style={styles.card}>
            <View style={{ flex: 1 }}>
              <Text style={styles.title}>{item.name}</Text>
              <Text style={styles.desc}>{item.description}</Text>
            </View>
            <TouchableOpacity style={styles.button}>
              <Text style={styles.buttonText}>Request Service</Text>
            </TouchableOpacity>
          </View>
        )}
        contentContainerStyle={{ paddingBottom: 8 }}
      />
    </View>
  );
}

const styles = StyleSheet.create({
  container: {
    backgroundColor: '#fff',
    borderRadius: 18,
    marginHorizontal: 16,
    marginTop: 18,
    padding: 16,
    shadowColor: '#000',
    shadowOpacity: 0.04,
    shadowRadius: 8,
    elevation: 2,
  },
  header: {
    fontSize: 18,
    fontWeight: '700',
    color: '#222',
    marginBottom: 12,
  },
  card: {
    flexDirection: 'row',
    alignItems: 'center',
    backgroundColor: '#F6F8FB',
    borderRadius: 14,
    padding: 14,
    marginBottom: 12,
    shadowColor: '#000',
    shadowOpacity: 0.02,
    shadowRadius: 4,
    elevation: 1,
  },
  title: {
    fontSize: 15,
    fontWeight: '700',
    color: '#222',
    marginBottom: 2,
  },
  desc: {
    fontSize: 13,
    color: '#888',
    marginBottom: 2,
  },
  button: {
    backgroundColor: '#4CC075',
    borderRadius: 16,
    paddingHorizontal: 16,
    paddingVertical: 8,
    marginLeft: 10,
  },
  buttonText: {
    color: '#fff',
    fontWeight: '700',
    fontSize: 14,
  },
}); 