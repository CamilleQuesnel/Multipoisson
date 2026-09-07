import React, { useState, useEffect } from 'react';
import {
  View,
  Text,
  TextInput,
  TouchableOpacity,
  StyleSheet,
  KeyboardAvoidingView,
  Platform,
  ScrollView,
  Dimensions,
} from 'react-native';
import { StackNavigationProp } from '@react-navigation/stack';
import { RootStackParamList } from '../types';
import { COLORS } from '../theme/colors';
import FishMascot from '../components/FishMascot';
import { savePlayerName, getLastPlayerName } from '../utils/storage';

type Props = {
  navigation: StackNavigationProp<RootStackParamList, 'Home'>;
};

export default function HomeScreen({ navigation }: Props) {
  const [name, setName] = useState('');
  const [error, setError] = useState('');

  useEffect(() => {
    getLastPlayerName().then((n) => {
      if (n) setName(n);
    });
  }, []);

  const handleStart = async () => {
    const trimmed = name.trim();
    if (!trimmed) {
      setError('Oups ! Entre ton prénom pour commencer 😊');
      return;
    }
    setError('');
    await savePlayerName(trimmed);
    navigation.navigate('TableSelection', { playerName: trimmed });
  };

  return (
    <KeyboardAvoidingView
      style={styles.root}
      behavior={Platform.OS === 'ios' ? 'padding' : 'height'}
    >
      <ScrollView
        contentContainerStyle={styles.scroll}
        keyboardShouldPersistTaps="handled"
      >
        {/* Header */}
        <View style={styles.header}>
          <Text style={styles.title}>🐟 MultiPoisson</Text>
          <Text style={styles.subtitle}>Apprends les tables de multiplication !</Text>
        </View>

        {/* Mascot */}
        <View style={styles.mascotArea}>
          <FishMascot level={1} size={160} animate />
        </View>

        {/* Name card */}
        <View style={styles.card}>
          <Text style={styles.label}>C'est qui ce champion ?</Text>
          <TextInput
            style={[styles.input, error ? styles.inputError : null]}
            placeholder="Ton prénom..."
            placeholderTextColor={COLORS.textSecondary}
            value={name}
            onChangeText={(t) => {
              setName(t);
              if (t.trim()) setError('');
            }}
            autoCapitalize="words"
            maxLength={20}
            returnKeyType="done"
            onSubmitEditing={handleStart}
          />
          {error ? <Text style={styles.errorText}>{error}</Text> : null}
          <TouchableOpacity
            style={styles.button}
            onPress={handleStart}
            activeOpacity={0.8}
          >
            <Text style={styles.buttonText}>C'est parti ! 🚀</Text>
          </TouchableOpacity>
        </View>
      </ScrollView>
    </KeyboardAvoidingView>
  );
}

const styles = StyleSheet.create({
  root: {
    flex: 1,
    backgroundColor: COLORS.blue,
  },
  scroll: {
    flexGrow: 1,
    alignItems: 'center',
    paddingTop: 60,
    paddingBottom: 40,
    paddingHorizontal: 20,
  },
  header: {
    alignItems: 'center',
    marginBottom: 8,
  },
  title: {
    fontSize: 42,
    fontWeight: '900',
    color: COLORS.white,
    textShadowColor: 'rgba(0,0,0,0.2)',
    textShadowOffset: { width: 2, height: 2 },
    textShadowRadius: 4,
  },
  subtitle: {
    fontSize: 17,
    color: COLORS.white,
    opacity: 0.9,
    marginTop: 4,
    fontWeight: '600',
  },
  mascotArea: {
    marginVertical: 20,
    height: 130,
    justifyContent: 'center',
    alignItems: 'center',
  },
  card: {
    width: '100%',
    backgroundColor: COLORS.white,
    borderRadius: 28,
    padding: 24,
    shadowColor: '#000',
    shadowOffset: { width: 0, height: 6 },
    shadowOpacity: 0.15,
    shadowRadius: 12,
    elevation: 8,
    gap: 14,
  },
  label: {
    fontSize: 22,
    fontWeight: '800',
    color: COLORS.textPrimary,
    textAlign: 'center',
  },
  input: {
    backgroundColor: COLORS.background,
    borderRadius: 16,
    paddingHorizontal: 20,
    paddingVertical: 14,
    fontSize: 22,
    fontWeight: '700',
    color: COLORS.textPrimary,
    borderWidth: 2,
    borderColor: COLORS.border,
    textAlign: 'center',
  },
  inputError: {
    borderColor: COLORS.red,
    backgroundColor: COLORS.redLight,
  },
  errorText: {
    color: COLORS.red,
    fontSize: 15,
    fontWeight: '600',
    textAlign: 'center',
  },
  button: {
    backgroundColor: COLORS.green,
    borderRadius: 20,
    paddingVertical: 18,
    alignItems: 'center',
    shadowColor: COLORS.greenDark,
    shadowOffset: { width: 0, height: 4 },
    shadowOpacity: 0.4,
    shadowRadius: 6,
    elevation: 5,
  },
  buttonText: {
    color: COLORS.white,
    fontSize: 22,
    fontWeight: '900',
    letterSpacing: 0.5,
  },
});
