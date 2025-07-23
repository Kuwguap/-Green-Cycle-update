import React from 'react';
import { View, Text, StyleSheet, Image, ScrollView } from 'react-native';

const leaderboard = [
  { name: 'Jodey', score: 150, avatar: require('../assets/images/greencycle.png'), badges: ['🏆', '🌳'] },
  { name: 'Felix', score: 120, avatar: require('../assets/images/greencycle.png'), badges: ['🌳', '🛒'] },
  { name: 'Suhair', score: 100, avatar: require('../assets/images/greencycle.png'), badges: ['🛒'] },
  { name: 'Chessman', score: 90, avatar: require('../assets/images/greencycle.png'), badges: ['🌳'] },
  { name: 'Umar', score: 80, avatar: require('../assets/images/greencycle.png'), badges: ['🌳'] },
];

const currentUser = 'Felix';

const nfts = [
  { id: 1, name: 'Eco Hero', rarity: 'Legendary', image: require('../assets/images/star.png') },
  { id: 2, name: 'Trash Hunter', rarity: 'Epic', image: require('../assets/images/greencycle.png') },
  { id: 3, name: 'Green Guardian', rarity: 'Rare', image: require('../assets/images/cartoontree.png') },
];

export default function LeaderboardScreen() {
  return (
    <ScrollView style={{ flex: 1, backgroundColor: '#F6F8FB' }} contentContainerStyle={{ paddingBottom: 40 }}>
      <Text style={styles.header}>Leaderboard</Text>
      <View style={styles.card}>
        {leaderboard.map((entry, idx) => (
          <View
            key={entry.name}
            style={[styles.row, entry.name === currentUser && styles.currentUserRow]}
          >
            <Text style={[styles.rank, idx === 0 && styles.gold, idx === 1 && styles.silver, idx === 2 && styles.bronze]}>{idx + 1}</Text>
            <Image source={entry.avatar} style={styles.avatar} />
            <View style={{ flex: 1 }}>
              <Text style={[styles.name, entry.name === currentUser && styles.currentUserName]}>{entry.name}</Text>
              <View style={styles.badgeRow}>
                {entry.badges.map((badge, i) => (
                  <Text key={i} style={styles.badge}>{badge}</Text>
                ))}
              </View>
            </View>
            <Text style={styles.score}>{entry.score}</Text>
          </View>
        ))}
      </View>
      <View style={styles.achievementsCard}>
        <Text style={styles.sectionTitle}>Your Achievements</Text>
        <View style={styles.achievementsRow}>
          <View style={styles.achievementCard}><Text style={styles.achievementIcon}>🏆</Text><Text style={styles.achievementLabel}>Top Cleaner</Text></View>
          <View style={styles.achievementCard}><Text style={styles.achievementIcon}>🌳</Text><Text style={styles.achievementLabel}>Spot Adopter</Text></View>
          <View style={styles.achievementCard}><Text style={styles.achievementIcon}>🛒</Text><Text style={styles.achievementLabel}>Marketplace Seller</Text></View>
        </View>
      </View>
      {/* NFT Badges Section */}
      <View style={styles.nftCard}>
        <Text style={styles.sectionTitle}>Your NFT Badges</Text>
        <View style={styles.nftRow}>
          {nfts.map(nft => (
            <View key={nft.id} style={styles.nftBadge}>
              <Image source={nft.image} style={styles.nftImage} />
              <Text style={styles.nftName}>{nft.name}</Text>
              <Text style={styles.nftRarity}>{nft.rarity}</Text>
            </View>
          ))}
        </View>
      </View>
    </ScrollView>
  );
}

const styles = StyleSheet.create({
  header: {
    fontSize: 24,
    fontWeight: '700',
    color: '#222',
    marginTop: 60,
    marginBottom: 18,
    alignSelf: 'center',
  },
  card: {
    backgroundColor: '#fff',
    borderRadius: 24,
    marginHorizontal: 18,
    padding: 18,
    marginBottom: 24,
    shadowColor: '#000',
    shadowOpacity: 0.04,
    shadowRadius: 8,
    elevation: 2,
  },
  row: {
    flexDirection: 'row',
    alignItems: 'center',
    paddingVertical: 12,
    borderBottomWidth: 1,
    borderBottomColor: '#F6F8FB',
  },
  currentUserRow: {
    backgroundColor: '#F6F8FB',
    borderRadius: 12,
  },
  rank: {
    width: 28,
    height: 28,
    borderRadius: 14,
    backgroundColor: '#eee',
    textAlign: 'center',
    fontWeight: '700',
    color: '#888',
    marginRight: 10,
    lineHeight: 28,
    fontSize: 16,
  },
  gold: { backgroundColor: '#FFD700', color: '#fff' },
  silver: { backgroundColor: '#C0C0C0', color: '#fff' },
  bronze: { backgroundColor: '#CD7F32', color: '#fff' },
  avatar: {
    width: 38,
    height: 38,
    borderRadius: 19,
    marginRight: 10,
  },
  name: {
    fontSize: 16,
    color: '#222',
    fontWeight: '500',
  },
  currentUserName: {
    color: '#4CC075',
    fontWeight: '700',
  },
  badgeRow: {
    flexDirection: 'row',
    marginTop: 2,
    gap: 4,
  },
  badge: {
    fontSize: 18,
    marginRight: 4,
  },
  score: {
    fontSize: 18,
    color: '#4CC075',
    fontWeight: '700',
    marginLeft: 8,
  },
  achievementsCard: {
    backgroundColor: '#fff',
    borderRadius: 18,
    marginHorizontal: 18,
    marginBottom: 18,
    padding: 16,
    elevation: 0,
  },
  sectionTitle: {
    fontSize: 16,
    fontWeight: '700',
    marginBottom: 10,
    color: '#222',
  },
  achievementsRow: {
    flexDirection: 'row',
    justifyContent: 'space-around',
  },
  achievementCard: {
    alignItems: 'center',
    backgroundColor: '#F6F8FB',
    borderRadius: 12,
    padding: 10,
    minWidth: 80,
    marginHorizontal: 4,
  },
  achievementIcon: {
    fontSize: 28,
    marginBottom: 4,
  },
  achievementLabel: {
    fontSize: 12,
    color: '#4CC075',
    fontWeight: '600',
    textAlign: 'center',
  },
  nftCard: {
    backgroundColor: '#fff',
    borderRadius: 18,
    marginHorizontal: 18,
    marginBottom: 18,
    padding: 16,
    elevation: 0,
  },
  nftRow: {
    flexDirection: 'row',
    justifyContent: 'space-around',
    marginTop: 8,
  },
  nftBadge: {
    alignItems: 'center',
    backgroundColor: '#F6F8FB',
    borderRadius: 12,
    padding: 10,
    minWidth: 90,
    marginHorizontal: 4,
  },
  nftImage: {
    width: 48,
    height: 48,
    borderRadius: 12,
    marginBottom: 4,
  },
  nftName: {
    fontSize: 13,
    color: '#222',
    fontWeight: '600',
    textAlign: 'center',
    marginBottom: 2,
  },
  nftRarity: {
    fontSize: 12,
    color: '#4CC075',
    fontWeight: '500',
    textAlign: 'center',
  },
}); 