import { FontAwesome5, MaterialCommunityIcons, MaterialIcons } from '@expo/vector-icons';
import Ionicons from '@expo/vector-icons/Ionicons';
import { useLocalSearchParams, useRouter } from 'expo-router';
import React, { useState, useEffect } from 'react';
import { Alert, Image, KeyboardAvoidingView, Platform, Pressable, ScrollView, StyleSheet, Switch, Text, TouchableOpacity, View, ActivityIndicator } from 'react-native';
import { TextInput } from 'react-native-paper';
import * as FileSystem from 'expo-file-system';
import * as Location from 'expo-location';
import MapView, { Marker } from 'react-native-maps';
/*import camera from './camera.svg';// Import your camera SVG icon here
import { SvgUri } from 'react-native-svg'; // If you want to use SVG icons directly*/
// ----- Option Arrays -----



const accessibilityOptions = [
  { id: 'requires tools', label: 'Requires tools', icon: 'tools', },
  { id: 'dangerous area', label: 'Dangerous area', icon: 'exclamation-circle' },
  { id: 'accessible by car', label: 'Accessible by car', icon: 'car-alt' },
  { id: 'accessible by bike', label: 'Accessible by bike', icon: 'bicycle' },
  { id: 'accessible by foot', label: 'Accessible by foot', icon: 'walking' },
  { id: 'under water/on the waterside', label: 'Under water/On water', icon: 'water' },
  { id: 'not for general cleanup', label: 'Not for general cleanup', icon: 'user-alt' }
];


const trashTypeOptions = [
  { id: 'plastic', label: 'Plastic', icon: 'prescription-bottle-alt' },
  { id: 'organic', label: 'Organic', icon: 'leaf' },
  { id: 'paper', label: 'Paper', icon: 'newspaper' },
  { id: 'metal', label: 'Metal', icon: 'cogs' },
  { id: 'electronic', label: 'Electronic', icon: 'robot' },
  { id: 'liquid', label: 'Liquid', icon: 'hand-holding-water' },
  { id: 'dangerous', label: 'Dangerous', icon: 'exclamation-triangle' },
  { id: 'animal carcass', label: 'Animal Carcass', icon: 'paw' },
  { id: 'glass', label: 'Glass', icon: 'glass-martini-alt' },
  { id: 'recyclable', label: 'Recyclable', icon: 'recycle' },
  { id: 'construction', label: 'Construction Waste', icon: 'hard-hat' },
];


const trashSizeOptions = [
  { id: 'Small', label: 'Fits in a bag', icon: 'shopping-bag' },
  { id: 'medium', label: 'Fits in a wheelbarrow', icon: 'baby-carriage' },
  { id: 'large', label: 'Car needed', icon: 'car' },
];


export default function ReportForm() {
  const [name, setName] = useState('');
  const [location, setLocation] = useState('');
  const [notes, setNotes] = useState('');

  const [selectedTrashType, setSelectedTrashType] = useState<string | null>(null);
  const [selectedTrashSize, setSelectedTrashSize] = useState<string | null>(null);

  const [selectedAccessibility, setSelectedAccessibility] = useState<Record<string, boolean>>({});

  const [latitude, setLatitude] = useState<number | null>(null);
  const [longitude, setLongitude] = useState<number | null>(null);
  const [locationLoading, setLocationLoading] = useState(true);

  const { photoUri } = useLocalSearchParams<{ photoUri: string }>();

  const router = useRouter();

  useEffect(() => {
    (async () => {
      let { status } = await Location.requestForegroundPermissionsAsync();
      if (status !== 'granted') {
        setLocationLoading(false);
        return;
      }
      let loc = await Location.getCurrentPositionAsync({});
      setLatitude(loc.coords.latitude);
      setLongitude(loc.coords.longitude);
      setLocationLoading(false);
    })();
  }, []);

  interface AccessibilityOption {
    id: string;
    label: string;
    icon: string;
  }

  interface TrashTypeOption {
    id: string;
    label: string;
    icon: string;
  }

  interface TrashSizeOption {
    id: string;
    label: string;
    icon: string;
  }

  const toggleAccessibility = (id: string) => {
    setSelectedAccessibility((prevState: Record<string, boolean>) => ({
      ...prevState,
      [id]: !prevState[id],
    }));
  };

  const toggleTrashSize = (id: string) => {
    setSelectedTrashSize((prevState: string | null) => (prevState === id ? null : id));
  };

   const HandleCameraPress = () => {
    router.replace('/camerascreen')
  }



  const handleSubmit = async () => {
    if (!selectedAccessibility || !selectedTrashType || !selectedTrashSize) {
      Alert.alert('Error', 'Please complete all required fields.');
      return;
    }
    if (!photoUri) {
      Alert.alert('Error', 'Please add a photo.');
      return;
    }
    if (latitude == null || longitude == null) {
      Alert.alert('Error', 'Location not available.');
      return;
    }
    try {
      const apiUrl = 'http://192.168.0.186:8080/api/dumps/report/image'; // Update to your backend IP if needed
      const formData = new FormData();
      formData.append('userId', '1'); // TODO: Replace with actual user ID from auth/profile
      formData.append('reportType', selectedTrashType || 'general');
      formData.append('description', `${notes}\nAccessibility: ${Object.keys(selectedAccessibility).filter(k => selectedAccessibility[k]).join(', ')}`);
      formData.append('location', location);
      formData.append('latitude', String(latitude));
      formData.append('longitude', String(longitude));
      // Convert image URI to file for upload
      const fileName = photoUri.split('/').pop() || `photo.jpg`;
      const fileType = fileName.endsWith('.png') ? 'image/png' : 'image/jpeg';
      formData.append('image', {
        uri: photoUri,
        name: fileName,
        type: fileType,
      } as any); // Cast as any for React Native FormData compatibility
      const response = await fetch(apiUrl, {
        method: 'POST',
        headers: {
          'Content-Type': 'multipart/form-data',
        },
        body: formData,
      });
      if (response.ok) {
        Alert.alert('Thank You!', 'Your trash report has been submitted.', [{
          text: 'Ok',
          onPress: () => router.replace('/thankscreen'),
          style: 'default',
        }], { cancelable: false });
        setName('');
        setLocation('');
        setSelectedTrashType(null);
        setSelectedTrashSize(null);
        setNotes('');
        setSelectedAccessibility({});
      } else {
        const msg = await response.text();
        Alert.alert('Error', msg || 'Failed to submit report.');
      }
    } catch (err) {
      Alert.alert('Error', 'Failed to submit report.');
    }
  };


  function changecolor(color: string): void {
    console.log(`Anonymous report indicator color changed to: ${color}`);
  }

  return (
    <KeyboardAvoidingView
      behavior={Platform.OS === 'ios' ? 'padding' : 'height'}
      style={{ flex: 1, backgroundColor: '#F6F8FB' }}
    >
      <ScrollView contentContainerStyle={{ padding: 0, paddingBottom: 40 }} showsVerticalScrollIndicator={false} keyboardShouldPersistTaps="handled">
        {/* Photo Card */}
        <View style={styles.card}>
          {photoUri ? (
            <View style={styles.photoWrapper}>
              <Image source={{ uri: photoUri }} style={styles.photo} />
              <TouchableOpacity style={styles.fab} onPress={HandleCameraPress}>
                <MaterialIcons name="camera-alt" size={28} color="#fff" />
              </TouchableOpacity>
            </View>
          ) : (
            <TouchableOpacity style={styles.photoPlaceholder} onPress={HandleCameraPress}>
              <MaterialIcons name="camera-alt" size={40} color="#4CC075" />
              <Text style={styles.photoText}>Add a photo</Text>
            </TouchableOpacity>
          )}
        </View>
        {/* Trash Type */}
        <View style={styles.card}>
          <Text style={styles.sectionTitle}>Trash Type</Text>
          <View style={styles.pillRow}>
            {trashTypeOptions.map((option) => (
              <TouchableOpacity
                key={option.id}
                style={[styles.pill, selectedTrashType === option.id && styles.pillActive]}
                onPress={() => setSelectedTrashType(option.id)}
              >
                <FontAwesome5 name={option.icon} size={16} color={selectedTrashType === option.id ? '#fff' : '#4CC075'} style={styles.pillIcon} />
                <Text style={[styles.pillText, selectedTrashType === option.id && styles.pillTextActive]}>{option.label}</Text>
              </TouchableOpacity>
            ))}
          </View>
        </View>
        {/* Trash Size */}
        <View style={styles.card}>
          <Text style={styles.sectionTitle}>Trash Size</Text>
          <View style={styles.pillRow}>
            {trashSizeOptions.map((option) => (
              <TouchableOpacity
                key={option.id}
                style={[styles.pill, selectedTrashSize === option.id && styles.pillActive]}
                onPress={() => setSelectedTrashSize(option.id)}
              >
                <FontAwesome5 name={option.icon} size={16} color={selectedTrashSize === option.id ? '#fff' : '#4CC075'} style={styles.pillIcon} />
                <Text style={[styles.pillText, selectedTrashSize === option.id && styles.pillTextActive]}>{option.label}</Text>
              </TouchableOpacity>
            ))}
          </View>
        </View>
        {/* Accessibility */}
        <View style={styles.card}>
          <Text style={styles.sectionTitle}>Accessibility</Text>
          <View style={styles.pillRow}>
            {accessibilityOptions.map((option) => (
              <TouchableOpacity
                key={option.id}
                style={[styles.pill, selectedAccessibility[option.id] && styles.pillActive]}
                onPress={() => toggleAccessibility(option.id)}
              >
                <FontAwesome5 name={option.icon} size={16} color={selectedAccessibility[option.id] ? '#fff' : '#4CC075'} style={styles.pillIcon} />
                <Text style={[styles.pillText, selectedAccessibility[option.id] && styles.pillTextActive]}>{option.label}</Text>
              </TouchableOpacity>
            ))}
          </View>
        </View>
        {/* Map Picker */}
        <View style={styles.card}>
          <Text style={styles.sectionTitle}>Select Dump Location</Text>
          {locationLoading ? (
            <ActivityIndicator size="large" color="#4CC075" />
          ) : latitude && longitude ? (
            <MapView
              style={{ height: 180, borderRadius: 14 }}
              initialRegion={{
                latitude,
                longitude,
                latitudeDelta: 0.005,
                longitudeDelta: 0.005,
              }}
              region={{
                latitude,
                longitude,
                latitudeDelta: 0.005,
                longitudeDelta: 0.005,
              }}
              onPress={e => {
                setLatitude(e.nativeEvent.coordinate.latitude);
                setLongitude(e.nativeEvent.coordinate.longitude);
              }}
            >
              <Marker
                coordinate={{ latitude, longitude }}
                draggable
                onDragEnd={e => {
                  setLatitude(e.nativeEvent.coordinate.latitude);
                  setLongitude(e.nativeEvent.coordinate.longitude);
                }}
                title="Dump Location"
                pinColor="#4CC075"
              />
            </MapView>
          ) : (
            <Text style={{ color: '#888', textAlign: 'center' }}>Location not available</Text>
          )}
        </View>
        {/* Location */}
        <View style={styles.card}>
          <Text style={styles.sectionTitle}>Location</Text>
          <View style={styles.inputRow}>
            <FontAwesome5 name="map-marker" size={18} color="#4CC075" style={styles.inputIcon} />
            <TextInput
              style={styles.input}
              placeholder="Enter location (optional)"
              value={location}
              onChangeText={setLocation}
              placeholderTextColor="#aaa"
            />
          </View>
        </View>
        {/* Notes */}
        <View style={styles.card}>
          <Text style={styles.sectionTitle}>Additional Notes</Text>
          <TextInput
            style={[styles.input, styles.notesInput]}
            placeholder="Additional Notes (optional)"
            value={notes}
            onChangeText={setNotes}
            multiline
            numberOfLines={3}
            placeholderTextColor="#aaa"
          />
        </View>
        {/* Name & Anonymous */}
        <View style={styles.card}>
          <Text style={styles.sectionTitle}>Your Name</Text>
          <View style={styles.inputRow}>
            <MaterialCommunityIcons name="rename-box" size={20} color="#4CC075" style={styles.inputIcon} />
            <TextInput
              style={styles.input}
              placeholder="Enter Name"
              value={name}
              onChangeText={setName}
              placeholderTextColor="#aaa"
            />
            <View style={styles.anonRow}>
              <MaterialCommunityIcons name="incognito" size={20} color="#e74c3c" />
              <Text style={styles.anonText}>Anonymous</Text>
              <Switch
                value={name === ''}
                onValueChange={(value) => {
                  if (value) setName('');
                }}
                style={{ marginLeft: 6 }}
              />
            </View>
          </View>
        </View>
        {/* Info & Submit */}
        <View style={styles.card}>
          <View style={styles.infoRow}>
            <MaterialCommunityIcons name="information" size={22} color="#e74c3c" style={{ marginRight: 6 }} />
            <Text style={styles.infoText}>Please ensure all information is accurate before submitting your report.</Text>
          </View>
          <TouchableOpacity style={styles.submitBtn} onPress={handleSubmit}>
            <Text style={styles.submitText}>Submit Report</Text>
            <Ionicons name="send-sharp" size={18} color="#fff" style={styles.submitIcon} />
          </TouchableOpacity>
        </View>
      </ScrollView>
    </KeyboardAvoidingView>
  );
}

const styles = StyleSheet.create({
  card: {
    backgroundColor: '#fff',
    borderRadius: 18,
    marginHorizontal: 16,
    marginTop: 18,
    marginBottom: 0,
    padding: 18,
    shadowColor: '#000',
    shadowOpacity: 0.04,
    shadowRadius: 8,
    elevation: 2,
  },
  sectionTitle: {
    fontSize: 16,
    fontWeight: '700',
    color: '#222',
    marginBottom: 10,
  },
  photoWrapper: {
    position: 'relative',
    alignItems: 'center',
    justifyContent: 'center',
  },
  photo: {
    width: '100%',
    height: 180,
    borderRadius: 14,
    marginBottom: 8,
  },
  fab: {
    position: 'absolute',
    bottom: 16,
    right: 16,
    backgroundColor: '#4CC075',
    borderRadius: 24,
    padding: 12,
    shadowColor: '#000',
    shadowOpacity: 0.12,
    shadowRadius: 8,
    elevation: 6,
  },
  photoPlaceholder: {
    alignItems: 'center',
    justifyContent: 'center',
    paddingVertical: 32,
    borderRadius: 14,
    borderWidth: 2,
    borderColor: '#4CC075',
    backgroundColor: '#F6F8FB',
  },
  photoText: {
    color: '#4CC075',
    fontWeight: '600',
    fontSize: 16,
    marginTop: 8,
  },
  pillRow: {
    flexDirection: 'row',
    flexWrap: 'wrap',
    gap: 8,
    marginBottom: 4,
  },
  pill: {
    flexDirection: 'row',
    alignItems: 'center',
    backgroundColor: '#F6F8FB',
    borderRadius: 18,
    paddingHorizontal: 16,
    paddingVertical: 8,
    marginBottom: 8,
    marginRight: 8,
  },
  pillActive: {
    backgroundColor: '#4CC075',
  },
  pillIcon: {
    marginRight: 6,
  },
  pillText: {
    color: '#4CC075',
    fontWeight: '600',
    fontSize: 14,
  },
  pillTextActive: {
    color: '#fff',
  },
  inputRow: {
    flexDirection: 'row',
    alignItems: 'center',
    backgroundColor: '#F6F8FB',
    borderRadius: 12,
    paddingHorizontal: 12,
    marginBottom: 8,
  },
  inputIcon: {
    marginRight: 8,
  },
  input: {
    flex: 1,
    fontSize: 15,
    color: '#222',
    paddingVertical: 10,
    backgroundColor: 'transparent',
  },
  notesInput: {
    minHeight: 60,
    backgroundColor: '#F6F8FB',
    borderRadius: 12,
    paddingHorizontal: 12,
    marginTop: 8,
  },
  anonRow: {
    flexDirection: 'row',
    alignItems: 'center',
    marginLeft: 10,
  },
  anonText: {
    fontSize: 13,
    color: '#e74c3c',
    marginLeft: 4,
    marginRight: 2,
  },
  infoRow: {
    flexDirection: 'row',
    alignItems: 'center',
    marginBottom: 12,
  },
  infoText: {
    color: '#e74c3c',
    fontWeight: '600',
    fontSize: 13,
    flex: 1,
  },
  submitBtn: {
    flexDirection: 'row',
    backgroundColor: '#4CC075',
    borderRadius: 24,
    alignItems: 'center',
    justifyContent: 'center',
    paddingVertical: 14,
    marginTop: 8,
  },
  submitText: {
    color: '#fff',
    fontWeight: '700',
    fontSize: 18,
    marginRight: 8,
  },
  submitIcon: {
    marginLeft: 2,
  },
});

