import React from 'react';
import { StyleSheet, Text, TouchableOpacity, View } from 'react-native';

type Props = {
  selectedTab: 'map' | 'list';
  onSelectTab: (tab: 'map' | 'list') => void;
};

const SwitchHeader = ({ selectedTab, onSelectTab }: Props) => {
  return (
    <View style={styles.container}>
    <View style={styles.tabContainer}>
      <TouchableOpacity
        style={[styles.tab, selectedTab === 'map' && styles.activeTab]}
        onPress={() => onSelectTab('map')}
      >
        <Text style={[styles.tabText, selectedTab === 'map' && styles.activeTabText]}>
          Map
        </Text>
      </TouchableOpacity>
      <TouchableOpacity
        style={[styles.tab, selectedTab === 'list' && styles.activeTab]}
        onPress={() => onSelectTab('list')}
      >
        <Text style={[styles.tabText, selectedTab === 'list' && styles.activeTabText]}>
          List
        </Text>
      </TouchableOpacity>
    </View>
    </View>
  );
};

export default SwitchHeader; 

const styles = StyleSheet.create({
 container: {
        backgroundColor: '#4CC075'
    },
    tabContainer: {
        alignItems: 'center',
        flexDirection: 'row',
        backgroundColor: '#5FD288',
        borderRadius: 8,
        padding: 3,
        marginTop: '10%',
        marginBottom: '2%',
        marginHorizontal: '28%'
    },
    tab: {
        paddingVertical: 10,
        paddingHorizontal: 30,
        borderRadius: 6,
    },
    activeTab: {
        backgroundColor: '#fff',
        elevation: 2,
    },
    tabText: {
        fontSize: 16,
        color: '#000',
    },
    activeTabText: {
        fontWeight: 'bold',
        color: '#000',
    },

})