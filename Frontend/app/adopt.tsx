import React, { useState, useRef, useEffect } from 'react';
import { View, Text, StyleSheet, FlatList, TouchableOpacity, Image, Modal, Linking, Animated } from 'react-native';
import MapViewComponent from '../components/mapviewcomponent';
import ListViewComponent from '../components/listviewcomponent';
import CustomFAB from '../components/CustomFAB';
import { SafeAreaView } from 'react-native-safe-area-context';
import { MaterialIcons } from '@expo/vector-icons';
import AsyncStorage from '@react-native-async-storage/async-storage';

type DumpReport = {
  id: number;
  userId: number;
  photoUrl: string;
  reportType: string;
  description: string;
  location: string;
  latitude: number;
  longitude: number;
  createdAt: string;
};

// Remove initialDumps and all mock dump logic
const [dumps, setDumps] = useState<DumpReport[]>([]);
const [userId, setUserId] = useState<number | null>(null);

useEffect(() => {
  // Fetch dumps
  fetch('http://192.168.0.186:8080/api/dumps/reports')
    .then(res => res.json())
    .then(data => setDumps(data))
    .catch(err => setDumps([]));
  // Fetch user profile
  (async () => {
    const token = await AsyncStorage.getItem('token');
    if (token) {
      const res = await fetch('http://192.168.0.186:8080/api/user/me', {
        headers: { 'Authorization': `Bearer ${token}` }
      });
      if (res.ok) {
        const data = await res.json();
        setUserId(data.id);
      }
    }
  })();
}, []);

export default function AdoptScreen() {
  const [modalDump, setModalDump] = useState<DumpReport | null>(null);
  const [showModal, setShowModal] = useState(false);
  const [closeAnim] = useState(new Animated.Value(1));
  // Replace showDumps and showAnim with dumpsExpanded and animValue
  const [dumpsExpanded, setDumpsExpanded] = useState(true);
  const animValue = useRef(new Animated.Value(1)).current; // 1 = expanded, 0 = collapsed

  // Handler for confirming a dump (before/after flow)
  const handleConfirm = (dumpId: string) => {
    setDumps(dumps => dumps.map(d => d.id === dumpId ? { ...d, status: 'completed', after: require('../assets/images/cartoontree.png') } : d));
  };

  // Handler for getting directions
  const handleDirections = (dump: DumpReport) => {
    const url = `https://www.google.com/maps/dir/?api=1&destination=${dump.latitude},${dump.longitude}`;
    Linking.openURL(url);
  };

  // Animated close button logic
  const handleCloseModal = () => {
    Animated.timing(closeAnim, {
      toValue: 0,
      duration: 300,
      useNativeDriver: true,
    }).start(() => {
      setModalDump(null);
      closeAnim.setValue(1);
    });
  };

  const toggleDumpsSection = () => {
    Animated.timing(animValue, {
      toValue: dumpsExpanded ? 0 : 1,
      duration: 300,
      useNativeDriver: true,
    }).start(() => {
      setDumpsExpanded(!dumpsExpanded);
    });
  };

  return (
    <View style={{ flex: 1, backgroundColor: '#F6F8FB' }}>
      <Text style={styles.header}>Adopt a Spot</Text>
      <View style={styles.mapContainer}>
        <MapViewComponent dumps={dumps} />
      </View>
      {/* Reported Dumps Section with retractable animation */}
      <Animated.View
        style={[
          styles.section,
          {
            opacity: animValue,
            transform: [{ scale: animValue }],
            position: 'absolute',
            left: 16,
            right: 16,
            bottom: 90 + 64 + 16, // 90 (FAB bottom) + 64 (FAB height) + 16 (gap)
            zIndex: 20,
            height: 220, // Fixed height for the section
          },
        ]}
        pointerEvents={dumpsExpanded ? 'auto' : 'none'}
      >
        <TouchableOpacity style={styles.sectionCloseBtn} onPress={toggleDumpsSection}>
          <MaterialIcons name="close" size={28} color="#fff" />
        </TouchableOpacity>
        <Text style={styles.sectionTitle}>Reported Dumps</Text>
        <View style={{ paddingBottom: 120 }}>
          {userId !== null && dumps.filter(dump => dump.userId === userId).length === 0 && <Text style={{ color: '#888', textAlign: 'center', marginTop: 10 }}>No dumps reported yet.</Text>}
          {userId !== null && dumps.filter(dump => dump.userId === userId).map(dump => (
            <View key={dump.id} style={styles.dumpCard}>
              <TouchableOpacity onPress={() => { setModalDump(dump); setShowModal(true); }}>
                <Image source={{ uri: dump.photoUrl }} style={styles.dumpImage} />
              </TouchableOpacity>
              <View style={{ flex: 1, marginLeft: 12 }}>
                <Text style={styles.dumpDesc}>{dump.description}</Text>
                <Text style={styles.dumpStatus}>{dump.reportType}</Text>
              </View>
            </View>
          ))}
        </View>
      </Animated.View>
      {/* Show/Expand Button - always visible, animates in/out */}
      <Animated.View
        style={[
          styles.fabShowDumps,
          {
            left: 28,
            bottom: 90,
            width: 64,
            height: 64,
            borderRadius: 32,
            opacity: animValue.interpolate({ inputRange: [0, 0.01, 1], outputRange: [1, 0, 0] }),
            transform: [{ scale: animValue.interpolate({ inputRange: [0, 1], outputRange: [1, 0.7] }) }],
            zIndex: 30,
            position: 'absolute',
            // display: dumpsExpanded ? 'none' : 'flex',
          },
        ]}
        pointerEvents={dumpsExpanded ? 'none' : 'auto'}
      >
        <TouchableOpacity style={{ flex: 1, alignItems: 'center', justifyContent: 'center' }} onPress={toggleDumpsSection}>
          <MaterialIcons name="list" size={32} color="#fff" />
        </TouchableOpacity>
      </Animated.View>
      {/* Dump Detail Modal */}
      <Modal visible={!!modalDump} transparent animationType="slide" onRequestClose={handleCloseModal}>
        {modalDump && (
          <View style={styles.modalOverlay}>
            <View style={styles.modalCard}>
              <Image source={{ uri: modalDump.photoUrl }} style={styles.modalImage} />
              <Text style={styles.modalDesc}>{modalDump.description}</Text>
              <Text style={styles.modalStatus}>{modalDump.reportType}</Text>
              <TouchableOpacity style={styles.directionsBtn} onPress={() => modalDump && handleDirections(modalDump)}>
                <Text style={styles.directionsText}>Get Directions</Text>
              </TouchableOpacity>
              <Animated.View style={{
                position: 'absolute',
                top: 10,
                right: 10,
                transform: [{ scale: closeAnim }],
                opacity: closeAnim,
              }}>
                <TouchableOpacity style={styles.fabClose} onPress={handleCloseModal}>
                  <Text style={styles.fabCloseText}>×</Text>
                </TouchableOpacity>
              </Animated.View>
            </View>
          </View>
        )}
      </Modal>
      <SafeAreaView edges={['bottom']} style={{ position: 'absolute', left: 0, right: 0, bottom: 0 }}>
        <CustomFAB />
      </SafeAreaView>
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
  mapContainer: {
    height: 220,
    borderRadius: 18,
    marginHorizontal: 16,
    marginBottom: 12,
    overflow: 'hidden',
    backgroundColor: '#fff',
    elevation: 2,
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
  cardAdopted: {
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
  meta: {
    fontSize: 12,
    color: '#4CC075',
    marginBottom: 2,
  },
  adoptBtn: {
    backgroundColor: '#4CC075',
    borderRadius: 16,
    paddingHorizontal: 18,
    paddingVertical: 8,
    marginLeft: 10,
  },
  adoptedBtn: {
    backgroundColor: '#aaa',
  },
  adoptText: {
    color: '#fff',
    fontWeight: '700',
    fontSize: 15,
  },
  adoptedText: {
    color: '#fff',
    fontWeight: '700',
    fontSize: 15,
    opacity: 0.7,
  },
  section: {
    marginTop: 20,
    marginHorizontal: 16,
    padding: 16,
    backgroundColor: '#fff',
    borderRadius: 18,
    shadowColor: '#000',
    shadowOpacity: 0.04,
    shadowRadius: 8,
    elevation: 2,
  },
  sectionTitle: {
    fontSize: 18,
    fontWeight: '700',
    color: '#222',
    marginBottom: 12,
  },
  dumpCard: {
    flexDirection: 'row',
    alignItems: 'center',
    backgroundColor: '#fff',
    borderRadius: 18,
    padding: 14,
    marginBottom: 16,
    shadowColor: '#000',
    shadowOpacity: 0.04,
    shadowRadius: 8,
    elevation: 2,
  },
  dumpCardCompleted: {
    backgroundColor: '#E0E0E0',
  },
  dumpImage: {
    width: 50,
    height: 50,
    borderRadius: 25,
  },
  dumpDesc: {
    fontSize: 14,
    fontWeight: '600',
    color: '#222',
    marginBottom: 4,
  },
  dumpStatus: {
    fontSize: 12,
    color: '#4CC075',
  },
  confirmBtn: {
    backgroundColor: '#4CC075',
    borderRadius: 16,
    paddingHorizontal: 18,
    paddingVertical: 8,
    marginLeft: 10,
  },
  confirmText: {
    color: '#fff',
    fontWeight: '700',
    fontSize: 15,
  },
  modalOverlay: {
    flex: 1,
    justifyContent: 'center',
    alignItems: 'center',
    backgroundColor: 'rgba(0,0,0,0.7)',
  },
  modalCard: {
    backgroundColor: '#fff',
    borderRadius: 20,
    padding: 20,
    alignItems: 'center',
    width: '90%',
    maxWidth: 350,
  },
  modalImage: {
    width: 100,
    height: 100,
    borderRadius: 50,
    marginBottom: 15,
  },
  modalDesc: {
    fontSize: 16,
    fontWeight: '600',
    color: '#222',
    marginBottom: 10,
    textAlign: 'center',
  },
  modalStatus: {
    fontSize: 14,
    color: '#4CC075',
    marginBottom: 20,
  },
  directionsBtn: {
    backgroundColor: '#4CC075',
    borderRadius: 16,
    paddingHorizontal: 25,
    paddingVertical: 10,
  },
  directionsText: {
    color: '#fff',
    fontWeight: '700',
    fontSize: 16,
  },
  closeBtn: {
    backgroundColor: '#E0E0E0',
    borderRadius: 16,
    paddingHorizontal: 25,
    paddingVertical: 10,
    marginTop: 10,
  },
  closeText: {
    color: '#222',
    fontWeight: '700',
    fontSize: 16,
  },
  fabClose: {
    backgroundColor: '#4CC075',
    width: 44,
    height: 44,
    borderRadius: 22,
    alignItems: 'center',
    justifyContent: 'center',
    elevation: 6,
    shadowColor: '#000',
    shadowOpacity: 0.12,
    shadowRadius: 8,
  },
  fabCloseText: {
    color: '#fff',
    fontSize: 28,
    fontWeight: '700',
    marginTop: -2,
  },
  sectionCloseBtn: {
    position: 'absolute',
    top: 10,
    right: 10,
    backgroundColor: '#4CC075',
    borderRadius: 18,
    width: 36,
    height: 36,
    alignItems: 'center',
    justifyContent: 'center',
    zIndex: 10,
  },
  fabShowDumps: {
    position: 'absolute',
    bottom: 100,
    left: 24,
    backgroundColor: '#4CC075',
    borderRadius: 32,
    width: 64,
    height: 64,
    alignItems: 'center',
    justifyContent: 'center',
    elevation: 8,
    shadowColor: '#000',
    shadowOpacity: 0.12,
    shadowRadius: 8,
  },
}); 