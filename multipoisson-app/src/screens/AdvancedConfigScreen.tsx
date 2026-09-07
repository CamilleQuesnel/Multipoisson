import React, { useState } from 'react';
import {
  View,
  Text,
  TouchableOpacity,
  StyleSheet,
  ScrollView,
  SafeAreaView,
} from 'react-native';
import { StackNavigationProp } from '@react-navigation/stack';
import { RouteProp } from '@react-navigation/native';
import { RootStackParamList, GameConfig } from '../types';
import { COLORS } from '../theme/colors';
import { ScrollIndicator } from '../components/ScrollIndicator';

type Props = {
  navigation: StackNavigationProp<RootStackParamList, 'AdvancedConfig'>;
  route: RouteProp<RootStackParamList, 'AdvancedConfig'>;
};

interface CheckboxProps {
  label: string;
  example: string;
  checked: boolean;
  onToggle: () => void;
  color?: string;
}

function Checkbox({ label, example, checked, onToggle, color = COLORS.blue }: CheckboxProps) {
  return (
    <TouchableOpacity style={styles.checkboxRow} onPress={onToggle} activeOpacity={0.7}>
      <View style={[styles.checkbox, checked && { backgroundColor: color, borderColor: color }]}>
        {checked && <Text style={styles.checkMark}>✓</Text>}
      </View>
      <View style={styles.checkboxLabel}>
        <Text style={styles.checkboxText}>{label}</Text>
        <Text style={styles.checkboxExample}>{example}</Text>
      </View>
    </TouchableOpacity>
  );
}

export default function AdvancedConfigScreen({ navigation, route }: Props) {
  const { playerName, selectedTables } = route.params;

  // Bloc A
  const [excludeZero, setExcludeZero] = useState(false);
  const [excludeOne, setExcludeOne] = useState(false);
  const [excludeTen, setExcludeTen] = useState(false);

  // Bloc B
  const [timerEnabled, setTimerEnabled] = useState(false);

  // Bloc C
  const [questionCount, setQuestionCount] = useState(0);
  const [countError, setCountError] = useState('');

  // Scroll indicator
  const [scrollY, setScrollY] = useState(0);
  const [contentHeight, setContentHeight] = useState(0);
  const [containerHeight, setContainerHeight] = useState(0);

  const addQuestions = (amount: number) => {
    setQuestionCount((prev) => Math.min(prev + amount, 500));
    setCountError('');
  };

  const handlePlay = () => {
    if (questionCount === 0 || questionCount < 10) {
      setCountError('Choisis entre 10 et 500 ! 🐠');
      return;
    }
    const config: GameConfig = {
      selectedTables,
      excludeZero,
      excludeOne,
      excludeTen,
      timerEnabled,
      questionCount,
    };
    navigation.navigate('Countdown', { playerName, config });
  };

  return (
    <SafeAreaView style={styles.root}>
      <View
        style={styles.container}
        onLayout={(e) => setContainerHeight(e.nativeEvent.layout.height)}
      >
        <ScrollView
          contentContainerStyle={styles.scroll}
          bounces={false}
          showsVerticalScrollIndicator={false}
          onScroll={(e) => setScrollY(e.nativeEvent.contentOffset.y)}
          onContentSizeChange={(_, h) => setContentHeight(h)}
          scrollEventThrottle={16}
        >
          <Text style={styles.screenTitle}>Paramètres du jeu ⚙️</Text>

          {/* ── Bloc A ─── */}
          <View style={styles.card}>
            <View style={styles.cardHeader}>
              <Text style={styles.cardIcon}>💤</Text>
              <Text style={styles.cardTitle}>Supprimer les opérations faciles ?</Text>
            </View>
            <Text style={styles.cardHint}>Coche ce que tu veux enlever</Text>
            <Checkbox
              label="× 0"
              example="ex : 3 × 0 = 0"
              checked={excludeZero}
              onToggle={() => setExcludeZero(!excludeZero)}
              color={COLORS.orange}
            />
            <Checkbox
              label="× 1"
              example="ex : 3 × 1 = 3"
              checked={excludeOne}
              onToggle={() => setExcludeOne(!excludeOne)}
              color={COLORS.orange}
            />
            <Checkbox
              label="× 10"
              example="ex : 3 × 10 = 30"
              checked={excludeTen}
              onToggle={() => setExcludeTen(!excludeTen)}
              color={COLORS.orange}
            />
          </View>

          {/* ── Bloc B ─── */}
          <View style={styles.card}>
            <View style={styles.cardHeader}>
              <Text style={styles.cardIcon}>⏱️</Text>
              <Text style={styles.cardTitle}>Veux-tu être chronométré ?</Text>
            </View>
            <View style={styles.toggleRow}>
              <TouchableOpacity
                style={[styles.toggleBtn, timerEnabled && styles.toggleBtnActive]}
                onPress={() => setTimerEnabled(true)}
                activeOpacity={0.7}
              >
                <Text style={[styles.toggleBtnText, timerEnabled && styles.toggleBtnTextActive]}>
                  OUI ⏱
                </Text>
              </TouchableOpacity>
              <TouchableOpacity
                style={[styles.toggleBtn, !timerEnabled && styles.toggleBtnActiveNo]}
                onPress={() => setTimerEnabled(false)}
                activeOpacity={0.7}
              >
                <Text style={[styles.toggleBtnText, !timerEnabled && styles.toggleBtnTextActive]}>
                  NON 😌
                </Text>
              </TouchableOpacity>
            </View>
          </View>

          {/* ── Bloc C ─── */}
          <View style={styles.card}>
            <View style={styles.cardHeader}>
              <Text style={styles.cardIcon}>🔢</Text>
              <Text style={styles.cardTitle}>Combien de multiplications ?</Text>
            </View>
            <View style={styles.counterArea}>
              <Text style={styles.counterValue}>{questionCount}</Text>
              <View style={styles.counterBtns}>
                <TouchableOpacity
                  style={styles.counterBtn}
                  onPress={() => addQuestions(10)}
                  activeOpacity={0.7}
                >
                  <Text style={styles.counterBtnText}>+10</Text>
                </TouchableOpacity>
                <TouchableOpacity
                  style={[styles.counterBtn, styles.counterBtn100]}
                  onPress={() => addQuestions(100)}
                  activeOpacity={0.7}
                >
                  <Text style={[styles.counterBtnText, styles.counterBtnText100]}>+100</Text>
                </TouchableOpacity>
                <TouchableOpacity
                  style={[styles.counterBtn, styles.counterBtnReset]}
                  onPress={() => { setQuestionCount(0); setCountError(''); }}
                  activeOpacity={0.7}
                >
                  <Text style={[styles.counterBtnText, styles.counterBtnTextReset]}>↺</Text>
                </TouchableOpacity>
              </View>
            </View>
            <Text style={styles.counterHint}>Max 500 · Min 10</Text>
            {countError ? <Text style={styles.errorText}>{countError}</Text> : null}
          </View>

          {/* Play button */}
          <TouchableOpacity
            style={styles.playBtn}
            onPress={handlePlay}
            activeOpacity={0.8}
          >
            <Text style={styles.playBtnText}>Jouer ! 🎮</Text>
          </TouchableOpacity>
        </ScrollView>

        <ScrollIndicator
          scrollY={scrollY}
          contentHeight={contentHeight}
          containerHeight={containerHeight}
        />
      </View>
    </SafeAreaView>
  );
}

const styles = StyleSheet.create({
  root: {
    flex: 1,
    backgroundColor: COLORS.background,
  },
  container: {
    flex: 1,
  },
  scroll: {
    flexGrow: 1,
    paddingHorizontal: 16,
    paddingTop: 40,
    paddingBottom: 40,
    paddingRight: 18,
    gap: 16,
  },
  screenTitle: {
    fontSize: 28,
    fontWeight: '900',
    color: COLORS.textPrimary,
    textAlign: 'center',
    marginBottom: 4,
  },
  card: {
    backgroundColor: COLORS.white,
    borderRadius: 24,
    padding: 20,
    gap: 12,
    shadowColor: '#000',
    shadowOffset: { width: 0, height: 3 },
    shadowOpacity: 0.08,
    shadowRadius: 8,
    elevation: 4,
  },
  cardHeader: {
    flexDirection: 'row',
    alignItems: 'center',
    gap: 8,
  },
  cardIcon: {
    fontSize: 24,
  },
  cardTitle: {
    flex: 1,
    fontSize: 18,
    fontWeight: '800',
    color: COLORS.textPrimary,
  },
  cardHint: {
    fontSize: 14,
    color: COLORS.textSecondary,
    fontStyle: 'italic',
    marginTop: -4,
  },
  checkboxRow: {
    flexDirection: 'row',
    alignItems: 'center',
    gap: 14,
    paddingVertical: 4,
  },
  checkbox: {
    width: 32,
    height: 32,
    borderRadius: 10,
    borderWidth: 2.5,
    borderColor: COLORS.border,
    backgroundColor: COLORS.background,
    alignItems: 'center',
    justifyContent: 'center',
  },
  checkMark: {
    color: COLORS.white,
    fontSize: 18,
    fontWeight: '900',
  },
  checkboxLabel: {
    gap: 2,
  },
  checkboxText: {
    fontSize: 18,
    fontWeight: '800',
    color: COLORS.textPrimary,
  },
  checkboxExample: {
    fontSize: 13,
    color: COLORS.textSecondary,
  },
  toggleRow: {
    flexDirection: 'row',
    gap: 12,
    justifyContent: 'center',
  },
  toggleBtn: {
    flex: 1,
    paddingVertical: 14,
    borderRadius: 16,
    alignItems: 'center',
    backgroundColor: COLORS.background,
    borderWidth: 2.5,
    borderColor: COLORS.border,
  },
  toggleBtnActive: {
    backgroundColor: COLORS.blue,
    borderColor: COLORS.blueDark,
  },
  toggleBtnActiveNo: {
    backgroundColor: COLORS.purpleLight,
    borderColor: COLORS.purple,
  },
  toggleBtnText: {
    fontSize: 17,
    fontWeight: '800',
    color: COLORS.textSecondary,
  },
  toggleBtnTextActive: {
    color: COLORS.textPrimary,
  },
  counterArea: {
    alignItems: 'center',
    gap: 16,
  },
  counterValue: {
    fontSize: 72,
    fontWeight: '900',
    color: COLORS.purple,
    lineHeight: 80,
  },
  counterBtns: {
    flexDirection: 'row',
    gap: 12,
  },
  counterBtn: {
    backgroundColor: COLORS.blue,
    borderRadius: 16,
    paddingHorizontal: 22,
    paddingVertical: 12,
    shadowColor: COLORS.blueDark,
    shadowOffset: { width: 0, height: 3 },
    shadowOpacity: 0.3,
    shadowRadius: 5,
    elevation: 4,
  },
  counterBtn100: {
    backgroundColor: COLORS.purple,
  },
  counterBtnReset: {
    backgroundColor: COLORS.orangeLight,
    shadowColor: COLORS.orange,
  },
  counterBtnText: {
    fontSize: 20,
    fontWeight: '900',
    color: COLORS.white,
  },
  counterBtnText100: {
    color: COLORS.white,
  },
  counterBtnTextReset: {
    color: COLORS.orange,
    fontSize: 22,
  },
  counterHint: {
    fontSize: 13,
    color: COLORS.textSecondary,
    textAlign: 'center',
    fontStyle: 'italic',
  },
  errorText: {
    color: COLORS.red,
    fontSize: 16,
    fontWeight: '700',
    textAlign: 'center',
  },
  playBtn: {
    backgroundColor: COLORS.green,
    borderRadius: 20,
    paddingVertical: 20,
    alignItems: 'center',
    shadowColor: COLORS.greenDark,
    shadowOffset: { width: 0, height: 5 },
    shadowOpacity: 0.4,
    shadowRadius: 8,
    elevation: 6,
    marginTop: 4,
  },
  playBtnText: {
    color: COLORS.white,
    fontSize: 24,
    fontWeight: '900',
    letterSpacing: 0.5,
  },
});
