import { AntDesign, Feather } from '@expo/vector-icons';
import FontAwesome6 from '@expo/vector-icons/FontAwesome6';
import { CameraMode, CameraType, CameraView, useCameraPermissions } from 'expo-camera';
import * as ImagePicker from 'expo-image-picker';
import { useRouter } from 'expo-router';
import { useRef, useState } from 'react';
import { Image, Pressable, StyleSheet, Text, TouchableOpacity, View } from 'react-native';
import { BlurView } from 'expo-blur';

export default function CameraScreen() {
  const [facing, setFacing] = useState<CameraType>('back');
  const [permission, requestPermission] = useCameraPermissions();
  const ref = useRef<CameraView>(null);
  const [uri, setUri] = useState<string | null>(null);
  const [mode, setMode] = useState<CameraMode>('picture');
  const [takingPhoto, setTakingPhoto] = useState(false);
  const router = useRouter();

  const toggleCameraFacing = () => setFacing(facing === 'back' ? 'front' : 'back');
  const toggleMode = () => setMode(mode === 'picture' ? 'video' : 'picture');

  const handleCancelPress = () => router.back();

  const takePicture = async () => {
    if (ref.current && !takingPhoto) {
      setTakingPhoto(true);
      const photo = await ref.current.takePictureAsync();
      setUri(photo.uri);
      setTakingPhoto(false);
    }
  };

  const pickImageFromGallery = async () => {
    const { status } = await ImagePicker.requestMediaLibraryPermissionsAsync();
    if (status !== 'granted') {
      alert('Sorry, we need gallery permissions to make this work!');
      return;
    }
    const result = await ImagePicker.launchImageLibraryAsync({
      mediaTypes: 'images',
      allowsEditing: true,
      allowsMultipleSelection: false,
      quality: 1,
    });
    if (!result.canceled && result.assets.length > 0) {
      setUri(result.assets[0].uri);
    }
  };

  if (!permission) return <View />;
  if (!permission.granted) {
    return (
      <View style={styles.container}>
        <Text style={styles.message}>We need your permission to show the camera</Text>
        <TouchableOpacity style={styles.permissionBtn} onPress={requestPermission}>
          <Text style={styles.permissionText}>Grant permission</Text>
        </TouchableOpacity>
      </View>
    );
  }

  // Photo preview/confirmation
  if (uri) {
    return (
      <View style={styles.container}>
        <Image source={{ uri }} style={styles.previewImg} />
        <BlurView intensity={40} tint="dark" style={styles.previewOverlay}>
          <TouchableOpacity style={styles.previewBtn} onPress={() => setUri(null)}>
            <Feather name="rotate-ccw" size={28} color="#fff" />
            <Text style={styles.previewBtnText}>Retake</Text>
          </TouchableOpacity>
          <TouchableOpacity style={styles.previewBtn} onPress={() => router.replace({ pathname: '/reportdump', params: { photoUri: uri } })}>
            <AntDesign name="checkcircle" size={28} color="#4CC075" />
            <Text style={[styles.previewBtnText, { color: '#4CC075' }]}>Use Photo</Text>
          </TouchableOpacity>
        </BlurView>
      </View>
    );
  }

  return (
    <View style={styles.container}>
      <CameraView
        style={styles.camera}
        facing={facing}
        mode={mode}
        mute={false}
        ref={ref}
        enableTorch={false}
        responsiveOrientationWhenOrientationLocked
      />
      {/* Top controls */}
      <View style={styles.topBar}>
        <TouchableOpacity style={styles.topBtn} onPress={handleCancelPress}>
          <Feather name="x" size={28} color="#fff" />
        </TouchableOpacity>
        <TouchableOpacity style={styles.topBtn} onPress={pickImageFromGallery}>
          <Feather name="image" size={28} color="#fff" />
        </TouchableOpacity>
      </View>
      {/* Bottom controls */}
      <BlurView intensity={40} tint="dark" style={styles.bottomBar}>
        <TouchableOpacity style={styles.flipBtn} onPress={toggleCameraFacing}>
          <FontAwesome6 name="camera-rotate" size={28} color="#fff" />
        </TouchableOpacity>
        <Pressable onPress={takePicture} disabled={takingPhoto} style={styles.shutterBtn}>
          <View style={styles.shutterBtnInner} />
        </Pressable>
        <TouchableOpacity style={styles.modeBtn} onPress={toggleMode}>
          {mode === 'picture' ? (
            <AntDesign name="picture" size={28} color="#fff" />
          ) : (
            <Feather name="video" size={28} color="#fff" />
          )}
        </TouchableOpacity>
      </BlurView>
    </View>
  );
}

const styles = StyleSheet.create({
  container: {
    flex: 1,
    backgroundColor: '#000',
    justifyContent: 'center',
    alignItems: 'center',
  },
  camera: {
    flex: 1,
    width: '100%',
  },
  topBar: {
    position: 'absolute',
    top: 40,
    left: 0,
    right: 0,
    flexDirection: 'row',
    justifyContent: 'space-between',
    paddingHorizontal: 24,
    zIndex: 10,
  },
  topBtn: {
    backgroundColor: 'rgba(0,0,0,0.5)',
    borderRadius: 18,
    padding: 8,
  },
  bottomBar: {
    position: 'absolute',
    left: 0,
    right: 0,
    bottom: 0,
    flexDirection: 'row',
    justifyContent: 'space-around',
    alignItems: 'center',
    paddingVertical: 18,
    paddingHorizontal: 32,
    borderTopLeftRadius: 24,
    borderTopRightRadius: 24,
    zIndex: 20,
  },
  flipBtn: {
    backgroundColor: 'rgba(0,0,0,0.5)',
    borderRadius: 18,
    padding: 10,
  },
  modeBtn: {
    backgroundColor: 'rgba(0,0,0,0.5)',
    borderRadius: 18,
    padding: 10,
  },
  shutterBtn: {
    backgroundColor: '#fff',
    borderWidth: 4,
    borderColor: '#4CC075',
    width: 74,
    height: 74,
    borderRadius: 37,
    alignItems: 'center',
    justifyContent: 'center',
    marginHorizontal: 18,
    shadowColor: '#000',
    shadowOpacity: 0.18,
    shadowRadius: 8,
    elevation: 8,
  },
  shutterBtnInner: {
    width: 54,
    height: 54,
    borderRadius: 27,
    backgroundColor: '#4CC075',
  },
  previewImg: {
    flex: 1,
    width: '100%',
    resizeMode: 'cover',
    borderRadius: 18,
  },
  previewOverlay: {
    position: 'absolute',
    left: 0,
    right: 0,
    bottom: 0,
    flexDirection: 'row',
    justifyContent: 'space-around',
    alignItems: 'center',
    paddingVertical: 24,
    borderTopLeftRadius: 24,
    borderTopRightRadius: 24,
  },
  previewBtn: {
    alignItems: 'center',
    marginHorizontal: 24,
  },
  previewBtnText: {
    color: '#fff',
    fontWeight: '700',
    fontSize: 15,
    marginTop: 4,
  },
  message: {
    textAlign: 'center',
    color: '#fff',
    fontSize: 18,
    marginTop: 40,
    marginBottom: 20,
  },
  permissionBtn: {
    backgroundColor: '#4CC075',
    borderRadius: 18,
    paddingHorizontal: 22,
    paddingVertical: 10,
    alignSelf: 'center',
    marginTop: 10,
  },
  permissionText: {
    color: '#fff',
    fontWeight: '700',
    fontSize: 16,
  },
});
