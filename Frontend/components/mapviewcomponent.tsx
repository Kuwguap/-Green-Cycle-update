import * as Location from 'expo-location';
import React, { useEffect, useState } from 'react';
import { ActivityIndicator, StyleSheet, Text, View } from 'react-native';
import MapView, { Marker, PROVIDER_GOOGLE } from 'react-native-maps';

export default function MapViewComponent({ dumps = [] }) {
    const [location, setLocation] = useState<Location.LocationObject | null>(null);
      const [loading, setLoading] = useState(true);
    
      useEffect(() => {
        (async () => {
          const { status } = await Location.requestForegroundPermissionsAsync();
          if (status !== 'granted') {
            alert('Permission to access location was denied');
            setLoading(false);
            return;
          }
    
          const loc = await Location.getCurrentPositionAsync({});
          setLocation(loc);
          setLoading(false);
        })();
      }, []);
    
      if (loading) {
        return (
          <View style={styles.center}>
            <ActivityIndicator size="large" />
            <Text>Loading map...</Text>
          </View>
        );
      }

  return (
    <View style={styles.container}>
      <MapView
      provider={PROVIDER_GOOGLE} 
      style={styles.map}
      initialRegion={{
        latitude: location?.coords.latitude || 37.78825,
        longitude: location?.coords.longitude || -122.4324,
        latitudeDelta: 0.05,
        longitudeDelta: 0.05,
      }}
    >
      {dumps.map(dump => (
        <Marker
          key={dump.id}
          coordinate={{ latitude: dump.latitude, longitude: dump.longitude }}
          title={dump.description}
          description={dump.reportType}
          // Optionally add image or custom marker
        />
      ))}
      {location && (
        <Marker
          coordinate={{
            latitude: location.coords.latitude,
            longitude: location.coords.longitude,
          }}
          title="You are here"
          pinColor="#4CC075"
        />
      )}
    </MapView>
    </View>
  );
}

const styles = StyleSheet.create({
  container: { 
    flex: 1
  },
  map: {
    flex: 1,
  },
  center: {
    flex: 1,
    justifyContent: 'center',
    alignItems: 'center',
  },
});
