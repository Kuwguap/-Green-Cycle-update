import React from 'react';
import { ScrollView, View, Text, StyleSheet } from 'react-native';
import TopNavBar from '../components/TopNavBar';
import FavoritesCard from '../components/FavoritesCard';
import RecentlyVisitedCard from '../components/RecentlyVisitedCard';
import CustomFAB from '../components/CustomFAB';
import WasteAsAServiceCard from '../components/WasteAsAServiceCard';
import { SafeAreaView } from 'react-native-safe-area-context';

export default function HomeScreen() {
  return (
    <View style={{ flex: 1, backgroundColor: '#F6F8FB' }}>
      <TopNavBar />
      <ScrollView showsVerticalScrollIndicator={false}>
        {/* Quick Stats */}
        <View style={styles.statsRow}>
          <View style={styles.statCard}>
            <Text style={styles.statValue}>12</Text>
            <Text style={styles.statLabel}>Dumps Reported</Text>
          </View>
          <View style={styles.statCard}>
            <Text style={styles.statValue}>5</Text>
            <Text style={styles.statLabel}>Spots Adopted</Text>
          </View>
          <View style={styles.statCard}>
            <Text style={styles.statValue}>30</Text>
            <Text style={styles.statLabel}>$CYCLE Earned</Text>
          </View>
        </View>
        {/* Favorites */}
        <FavoritesCard />
        {/* Recently Visited */}
        <RecentlyVisitedCard />
        {/* News Feed */}
        <View style={styles.newsCard}>
          <Text style={styles.newsHeader}>Green Cycle News</Text>
          <Text style={styles.newsItem}>🌱 Community cleanup this Saturday at Central Park!</Text>
          <Text style={styles.newsItem}>♻️ New marketplace items available for upcycling.</Text>
          <Text style={styles.newsItem}>🏆 Leaderboard updated: You are #3 this week!</Text>
        </View>
        {/* Waste-as-a-Service Section */}
        <WasteAsAServiceCard />
      </ScrollView>
      <SafeAreaView edges={['bottom']} style={{ position: 'absolute', left: 0, right: 0, bottom: 0 }}>
        <CustomFAB />
      </SafeAreaView>
    </View>
  );
}

const styles = StyleSheet.create({
  statsRow: {
    flexDirection: 'row',
    justifyContent: 'space-around',
    marginTop: 18,
    marginBottom: 8,
  },
  statCard: {
    backgroundColor: '#fff',
    borderRadius: 16,
    padding: 16,
    alignItems: 'center',
    minWidth: 90,
    elevation: 1,
    shadowOpacity: 0.04,
  },
  statValue: {
    fontSize: 22,
    fontWeight: '700',
    color: '#4CC075',
  },
  statLabel: {
    fontSize: 12,
    color: '#888',
    marginTop: 2,
    textAlign: 'center',
  },
  newsCard: {
    backgroundColor: '#fff',
    borderRadius: 18,
    margin: 18,
    marginTop: 10,
    padding: 16,
    elevation: 0,
  },
  newsHeader: {
    fontSize: 16,
    fontWeight: '700',
    marginBottom: 8,
    color: '#222',
  },
  newsItem: {
    fontSize: 13,
    color: '#4CC075',
    marginBottom: 4,
  },
}); 