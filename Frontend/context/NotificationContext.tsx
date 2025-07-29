import React, { createContext, useContext, useState, ReactNode } from 'react';
import { Animated, StyleSheet, View, Text } from 'react-native';
import { Snackbar, Portal, MD3Colors } from 'react-native-paper';
import { MaterialIcons } from '@expo/vector-icons';

// Define notification types
export type NotificationType = 'info' | 'success' | 'warning' | 'danger';

// Define notification data structure
interface NotificationData {
  message: string;
  type: NotificationType;
  duration?: number;
}

// Define context type
interface NotificationContextType {
  showNotification: (data: NotificationData) => void;
  hideNotification: () => void;
}

// Create context with default values
const NotificationContext = createContext<NotificationContextType>({
  showNotification: () => {},
  hideNotification: () => {},
});

// Custom hook to use the notification context
export const useNotification = () => useContext(NotificationContext);

// Type colors mapping
const typeColors = {
  info: MD3Colors.primary60,
  success: MD3Colors.tertiary60,
  warning: MD3Colors.error60,
  danger: MD3Colors.error80,
};

// Type icons mapping
const typeIcons = {
  info: 'info',
  success: 'check-circle',
  warning: 'warning',
  danger: 'error',
};

// Provider component
export const NotificationProvider: React.FC<{ children: ReactNode }> = ({ children }) => {
  const [visible, setVisible] = useState(false);
  const [notification, setNotification] = useState<NotificationData>({
    message: '',
    type: 'info',
    duration: 3000,
  });
  
  // Animation value for slide-in effect
  const [slideAnim] = useState(new Animated.Value(-100));

  const showNotification = (data: NotificationData) => {
    setNotification({
      ...data,
      duration: data.duration || 3000,
    });
    setVisible(true);
    
    // Reset animation and start slide-in
    slideAnim.setValue(-100);
    Animated.spring(slideAnim, {
      toValue: 0,
      useNativeDriver: true,
      friction: 8,
    }).start();
  };

  const hideNotification = () => {
    // Slide-out animation
    Animated.timing(slideAnim, {
      toValue: -100,
      duration: 300,
      useNativeDriver: true,
    }).start(() => {
      setVisible(false);
    });
  };

  // Custom notification component
  const NotificationComponent = () => (
    <Animated.View
      style={[
        styles.notificationContainer,
        { 
          backgroundColor: typeColors[notification.type],
          transform: [{ translateY: slideAnim }]
        }
      ]}
    >
      <View style={styles.iconContainer}>
        <MaterialIcons 
          name={typeIcons[notification.type] as any} 
          size={24} 
          color="white" 
        />
      </View>
      <Text style={styles.notificationText}>{notification.message}</Text>
    </Animated.View>
  );

  return (
    <NotificationContext.Provider value={{ showNotification, hideNotification }}>
      {children}
      <Portal>
        <Snackbar
          visible={visible}
          onDismiss={hideNotification}
          duration={notification.duration}
          style={{ backgroundColor: 'transparent', elevation: 0 }}
          wrapperStyle={styles.snackbarWrapper}
        >
          <NotificationComponent />
        </Snackbar>
      </Portal>
    </NotificationContext.Provider>
  );
};

const styles = StyleSheet.create({
  snackbarWrapper: {
    position: 'absolute',
    top: 0,
    left: 0,
    right: 0,
    bottom: 'auto',
  },
  notificationContainer: {
    flexDirection: 'row',
    alignItems: 'center',
    padding: 16,
    borderRadius: 8,
    marginHorizontal: 16,
    marginTop: 50,
    elevation: 4,
    shadowColor: '#000',
    shadowOffset: { width: 0, height: 2 },
    shadowOpacity: 0.25,
    shadowRadius: 3.84,
  },
  iconContainer: {
    marginRight: 12,
  },
  notificationText: {
    color: 'white',
    flex: 1,
    fontSize: 16,
  },
});