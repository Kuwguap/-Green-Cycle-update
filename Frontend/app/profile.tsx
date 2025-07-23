import React, { useEffect, useState } from 'react';
import { View, Text, StyleSheet, Image, TouchableOpacity, ScrollView, ActivityIndicator, Alert } from 'react-native';
import { SafeAreaView } from 'react-native-safe-area-context';
import AsyncStorage from '@react-native-async-storage/async-storage';
import { TextInput, Button } from 'react-native-paper';

const API_URL = 'http://192.168.0.186:8080/api/user/me'; // Use your backend IP

export default function ProfileScreen() {
  const [profile, setProfile] = useState<any>(null);
  const [loading, setLoading] = useState(true);
  const [editMode, setEditMode] = useState(false);
  const [name, setName] = useState('');
  const [saving, setSaving] = useState(false);
  const [error, setError] = useState<string | null>(null);

  const fetchProfile = async () => {
    setLoading(true);
    setError(null);
    try {
      const token = await AsyncStorage.getItem('token');
      const response = await fetch(API_URL, {
        headers: { 'Authorization': `Bearer ${token}` }
      });
      if (response.ok) {
        const data = await response.json();
        setProfile(data);
        setName(data.name);
      } else {
        setError('Failed to load profile');
      }
    } catch (e) {
      setError('Failed to load profile');
    } finally {
      setLoading(false);
    }
  };

  useEffect(() => {
    fetchProfile();
  }, []);

  const handleSave = async () => {
    setSaving(true);
    setError(null);
    try {
      const token = await AsyncStorage.getItem('token');
      const response = await fetch(API_URL, {
        method: 'PUT',
        headers: {
          'Authorization': `Bearer ${token}`,
          'Content-Type': 'application/json',
        },
        body: JSON.stringify({ name })
      });
      if (response.ok) {
        const data = await response.json();
        setProfile(data);
        setEditMode(false);
        Alert.alert('Profile updated!');
      } else {
        setError('Failed to update profile');
      }
    } catch (e) {
      setError('Failed to update profile');
    } finally {
      setSaving(false);
    }
  };

  if (loading) {
    return (
      <SafeAreaView style={{ flex: 1, backgroundColor: '#F6F8FB', justifyContent: 'center', alignItems: 'center' }}>
        <ActivityIndicator size="large" color="#4CC075" />
      </SafeAreaView>
    );
  }

  if (error) {
    return (
      <SafeAreaView style={{ flex: 1, backgroundColor: '#F6F8FB', justifyContent: 'center', alignItems: 'center' }}>
        <Text style={{ color: '#e74c3c', fontWeight: 'bold' }}>{error}</Text>
        <Button onPress={fetchProfile} mode="contained" style={{ marginTop: 16 }}>Retry</Button>
      </SafeAreaView>
    );
  }

  return (
    <SafeAreaView edges={['top']} style={{ flex: 1, backgroundColor: '#F6F8FB' }}>
      <ScrollView contentContainerStyle={{ paddingBottom: 40 }}>
        <View style={styles.header}>
          <Image source={require('../assets/images/greencycle.png')} style={styles.avatar} />
          {editMode ? (
            <TextInput
              style={styles.nameInput}
              value={name}
              onChangeText={setName}
              mode="outlined"
              theme={{ colors: { primary: '#4CC075' } }}
              disabled={saving}
            />
          ) : (
            <Text style={styles.name}>{profile.name}</Text>
          )}
          <Text style={styles.email}>{profile.email}</Text>
          <Text style={styles.wallet}>Wallet: <Text style={{ color: '#4CC075', fontWeight: '700' }}>{profile.walletAddress}</Text></Text>
          <Text style={styles.roles}>Roles: {Array.isArray(profile.roles) ? profile.roles.join(', ') : profile.roles}</Text>
          {editMode ? (
            <View style={styles.editRow}>
              <Button mode="contained" onPress={handleSave} loading={saving} disabled={saving} style={styles.saveBtn}>Save</Button>
              <Button mode="outlined" onPress={() => setEditMode(false)} disabled={saving} style={styles.cancelBtn}>Cancel</Button>
            </View>
          ) : (
            <TouchableOpacity style={styles.actionBtn} onPress={() => setEditMode(true)}>
              <Text style={styles.actionText}>Edit Profile</Text>
            </TouchableOpacity>
          )}
        </View>
      </ScrollView>
    </SafeAreaView>
  );
}

const styles = StyleSheet.create({
  header: {
    alignItems: 'center',
    backgroundColor: '#fff',
    borderRadius: 24,
    margin: 18,
    padding: 24,
    shadowColor: '#000',
    shadowOpacity: 0.08,
    shadowRadius: 12,
    elevation: 4,
  },
  avatar: {
    width: 80,
    height: 80,
    borderRadius: 40,
    marginBottom: 10,
  },
  name: {
    fontSize: 24,
    fontWeight: 'bold',
    color: '#222',
    marginBottom: 4,
  },
  nameInput: {
    fontSize: 22,
    fontWeight: 'bold',
    color: '#222',
    marginBottom: 4,
    width: 200,
    alignSelf: 'center',
    backgroundColor: '#F6F8FB',
  },
  email: {
    fontSize: 16,
    color: '#888',
    marginBottom: 4,
  },
  wallet: {
    fontSize: 15,
    color: '#222',
    marginBottom: 4,
  },
  roles: {
    fontSize: 14,
    color: '#4CC075',
    marginBottom: 10,
  },
  actionBtn: {
    backgroundColor: '#4CC075',
    borderRadius: 16,
    paddingHorizontal: 24,
    paddingVertical: 10,
    marginTop: 12,
  },
  actionText: {
    color: '#fff',
    fontWeight: '700',
    fontSize: 16,
  },
  editRow: {
    flexDirection: 'row',
    justifyContent: 'center',
    marginTop: 10,
    gap: 10,
  },
  saveBtn: {
    marginRight: 8,
    backgroundColor: '#4CC075',
  },
  cancelBtn: {
    borderColor: '#4CC075',
  },
}); 