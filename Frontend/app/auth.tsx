import Entypo from '@expo/vector-icons/Entypo';
import Ionicons from '@expo/vector-icons/Ionicons';
import { useRouter } from 'expo-router';
import React, { useState } from 'react';
import { Linking, ScrollView, StyleSheet, TouchableOpacity, View, Image } from 'react-native';
import { Button, Text, TextInput } from 'react-native-paper';
import { SafeAreaView } from 'react-native-safe-area-context';
import AsyncStorage from '@react-native-async-storage/async-storage';

const API_URL = 'http://192.168.0.186:8080/api/auth'; // Use your computer's IP for Expo Go

export default function AuthScreen() {
  const router = useRouter()
  const [selectedTab, setSelectedTab] = useState<'login' | 'signup'>("login")
  const [email, setEmail] = useState<string>("")
  const [password, setPassword] = useState('')
  const [name, setName] = useState('')
  const [showPassword, setShowPassword] = useState(false)
  const [loading, setLoading] = useState(false);
  const [error, setError] = useState<string | null>(null);

  const handleLogin = async () => {
    setLoading(true);
    setError(null);
    try {
      const response = await fetch(`${API_URL}/login`, {
        method: 'POST',
        headers: { 'Content-Type': 'application/json' },
        body: JSON.stringify({ email, password })
      });
      if (response.ok) {
        const data = await response.json();
        await AsyncStorage.setItem('token', data.token);
        router.replace('/home');
      } else {
        setError('Invalid credentials');
      }
    } catch (e) {
      setError('Login failed');
    } finally {
      setLoading(false);
    }
  };

  const handleSignup = async () => {
    setLoading(true);
    setError(null);
    try {
      const response = await fetch(`${API_URL}/register`, {
        method: 'POST',
        headers: { 'Content-Type': 'application/json' },
        body: JSON.stringify({ email, password, name: name || email.split('@')[0] })
      });
      if (response.ok) {
        setError(null);
        alert('Registration successful! Please log in.');
        setSelectedTab('login');
      } else {
        const msg = await response.text();
        setError(msg || 'Registration failed');
      }
    } catch (e) {
      setError('Signup failed');
    } finally {
      setLoading(false);
    }
  };

  const HandleSwitchMode = () => {
    setSelectedTab('signup');
    setError(null);
  }

  const HandleOtherMode = () => {
    setSelectedTab('login');
    setError(null);
  }

  return (
    <SafeAreaView style={{ flex: 1, backgroundColor: '#F6F8FB' }}>
      <ScrollView contentContainerStyle={{ flexGrow: 1, justifyContent: 'center' }}>
        <View style={styles.logoContainer}>
          <Image source={require('../assets/images/greencycle.png')} style={styles.logo} />
          <Text style={styles.logoTitle}>Green Cycle</Text>
        </View>
        <View style={styles.card}>
          <View style={styles.tabContainer}>
            <TouchableOpacity
              style={[styles.tab, selectedTab === 'login' && styles.activeTab]}
              onPress={() => setSelectedTab('login')}
            >
              <Text style={[styles.tabText, selectedTab === 'login' && styles.activeTabText]}>Login</Text>
            </TouchableOpacity>
            <TouchableOpacity
              style={[styles.tab, selectedTab === 'signup' && styles.activeTab]}
              onPress={() => setSelectedTab('signup')}
            >
              <Text style={[styles.tabText, selectedTab === 'signup' && styles.activeTabText]}>Signup</Text>
            </TouchableOpacity>
          </View>
          {error && <Text style={styles.errorText}>{error}</Text>}
          {selectedTab === 'signup' && (
            <View style={styles.inputGroup}>
              <Text style={styles.label}>Name</Text>
              <TextInput
                style={styles.input}
                placeholder="Enter your name"
                autoCapitalize='words'
                mode='outlined'
                value={name}
                onChangeText={setName}
                theme={{ colors: { primary: '#4CC075' } }}
              />
            </View>
          )}
          <View style={styles.inputGroup}>
            <Text style={styles.label}>Email</Text>
            <TextInput
              style={styles.input}
              placeholder="example@gmail.com"
              autoCapitalize='none'
              mode='outlined'
              value={email}
              onChangeText={setEmail}
              theme={{ colors: { primary: '#4CC075' } }}
            />
          </View>
          <View style={styles.inputGroup}>
            <Text style={styles.label}>Password</Text>
            <View style={styles.eyeRow}>
              <TextInput
                style={[styles.input, { flex: 1 }]}
                placeholder="Enter Password"
                autoCapitalize='none'
                autoComplete="current-password"
                secureTextEntry={!showPassword}
                mode='outlined'
                value={password}
                onChangeText={setPassword}
                theme={{ colors: { primary: '#4CC075' } }}
              />
              <TouchableOpacity onPress={() => setShowPassword(!showPassword)} style={styles.eyeBtn}>
                <Ionicons name={showPassword ? 'eye-sharp' : 'eye-off'} size={22} color="#4CC075" />
              </TouchableOpacity>
            </View>
          </View>
          {selectedTab === 'login' ? (
            <Button onPress={handleLogin} style={styles.button} mode='contained' loading={loading} disabled={loading}>Log In</Button>
          ) : (
            <Button onPress={handleSignup} style={styles.button} mode='contained' loading={loading} disabled={loading}>Create Account</Button>
          )}
          <TouchableOpacity style={styles.switchBtn} onPress={selectedTab === 'login' ? HandleSwitchMode : HandleOtherMode}>
            <Text style={styles.switchText}>
              {selectedTab === 'login' ? "Don't have an account? Create Account" : "Already have an account? Log In"}
            </Text>
          </TouchableOpacity>
        </View>
        <View style={styles.termsContainer}>
          <Text style={styles.termsText}>By signing up you agree to our Terms and Conditions & Privacy Policy.</Text>
          <TouchableOpacity style={styles.linkContainer} onPress={() => Linking.openURL('https://www.trashout.ngo/policy')}>
            <Entypo name="link" size={20} color="#4CC075" />
            <Text style={styles.linkText}>Privacy Policy</Text>
          </TouchableOpacity>
          <TouchableOpacity style={styles.linkContainer} onPress={() => Linking.openURL('https://www.trashout.ngo/policy')}>
            <Entypo name="link" size={20} color="#4CC075" />
            <Text style={styles.linkText}>Terms and Conditions</Text>
          </TouchableOpacity>
        </View>
      </ScrollView>
    </SafeAreaView>
  );
}

const styles = StyleSheet.create({
  logoContainer: {
    alignItems: 'center',
    marginTop: 40,
    marginBottom: 10,
  },
  logo: {
    width: 80,
    height: 80,
    marginBottom: 8,
  },
  logoTitle: {
    fontSize: 28,
    fontWeight: 'bold',
    color: '#4CC075',
    letterSpacing: 1.2,
  },
  card: {
    backgroundColor: '#fff',
    borderRadius: 24,
    marginHorizontal: 18,
    padding: 24,
    shadowColor: '#000',
    shadowOpacity: 0.08,
    shadowRadius: 12,
    elevation: 4,
    marginBottom: 18,
  },
  tabContainer: {
    flexDirection: 'row',
    backgroundColor: '#E6F7EF',
    borderRadius: 12,
    marginBottom: 18,
    overflow: 'hidden',
  },
  tab: {
    flex: 1,
    paddingVertical: 12,
    alignItems: 'center',
    borderRadius: 12,
  },
  activeTab: {
    backgroundColor: '#4CC075',
  },
  tabText: {
    fontSize: 16,
    color: '#4CC075',
    fontWeight: '600',
  },
  activeTabText: {
    color: '#fff',
    fontWeight: 'bold',
  },
  inputGroup: {
    marginBottom: 16,
  },
  label: {
    fontSize: 15,
    color: '#222',
    fontWeight: '600',
    marginBottom: 4,
  },
  input: {
    backgroundColor: '#F6F8FB',
    borderRadius: 10,
    fontSize: 15,
    paddingHorizontal: 10,
  },
  eyeRow: {
    flexDirection: 'row',
    alignItems: 'center',
  },
  eyeBtn: {
    marginLeft: 8,
    padding: 4,
  },
  button: {
    backgroundColor: '#4CC075',
    borderRadius: 16,
    marginTop: 8,
    marginBottom: 8,
    paddingVertical: 6,
  },
  switchBtn: {
    marginTop: 8,
    alignItems: 'center',
  },
  switchText: {
    color: '#4CC075',
    fontWeight: 'bold',
    fontSize: 15,
  },
  errorText: {
    color: '#e74c3c',
    fontWeight: 'bold',
    marginBottom: 10,
    textAlign: 'center',
  },
  termsContainer: {
    alignItems: 'center',
    marginTop: 10,
    marginBottom: 30,
  },
  termsText: {
    color: '#888',
    fontSize: 13,
    textAlign: 'center',
    marginBottom: 8,
  },
  linkContainer: {
    flexDirection: 'row',
    alignItems: 'center',
    marginBottom: 4,
  },
  linkText: {
    color: '#4CC075',
    marginLeft: 6,
    fontWeight: '600',
    fontSize: 15,
  },
});
