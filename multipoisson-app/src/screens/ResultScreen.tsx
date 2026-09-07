import React, { useEffect, useRef, useState } from 'react';
import {
  View,
  Text,
  StyleSheet,
  ScrollView,
  TouchableOpacity,
  SafeAreaView,
  Dimensions,
} from 'react-native';
import Animated, {
  useSharedValue,
  useAnimatedStyle,
  withSpring,
  withTiming,
  withSequence,
  withRepeat,
  Easing,
} from 'react-native-reanimated';
import { StackNavigationProp } from '@react-navigation/stack';
import { RouteProp } from '@react-navigation/native';
import { RootStackParamList, GameResult } from '../types';
import { COLORS } from '../theme/colors';
import FishMascot from '../components/FishMascot';
import { getMascotLevel, getMascotLabel, formatTime } from '../utils/gameLogic';
import { saveGameResult, getHistory } from '../utils/storage';

type Props = {
  navigation: StackNavigationProp<RootStackParamList, 'Result'>;
  route: RouteProp<RootStackParamList, 'Result'>;
};

const { width } = Dimensions.get('window');

function formatDate(dateStr: string) {
  const d = new Date(dateStr);
  return d.toLocaleDateString('fr-FR', {
    day: '2-digit',
    month: '2-digit',
    year: '2-digit',
    hour: '2-digit',
    minute: '2-digit',
  });
}

export default function ResultScreen({ navigation, route }: Props) {
  const { playerName, score, maxScore, bonusPoints, questionCount, timeSeconds, timerEnabled } =
    route.params;

  const [history, setHistory] = useState<GameResult[]>([]);
  const [dragonFlash, setDragonFlash] = useState(false);

  const level = getMascotLevel(score, maxScore, questionCount);
  const percentage = maxScore > 0 ? Math.round((score / maxScore) * 100) : 0;

  // Animations
  const scoreScale = useSharedValue(0);
  const scoreOpacity = useSharedValue(0);
  const flashOpacity = useSharedValue(0);
  const flashScale = useSharedValue(1);

  useEffect(() => {
    // Save result
    const result: GameResult = {
      id: Date.now().toString(),
      date: new Date().toISOString(),
      playerName,
      questionCount,
      score,
      maxScore,
      timeSeconds,
    };
    saveGameResult(result);

    // Load history
    getHistory().then(setHistory);

    // Entrance animations
    scoreScale.value = withSequence(
      withSpring(1.3, { damping: 6, stiffness: 200 }),
      withSpring(1, { damping: 10 }),
    );
    scoreOpacity.value = withTiming(1, { duration: 400 });

    // Dragon flash effect
    if (level === 6) {
      setDragonFlash(true);
      flashOpacity.value = withSequence(
        withTiming(0.9, { duration: 150 }),
        withTiming(0, { duration: 150 }),
        withTiming(0.7, { duration: 120 }),
        withTiming(0, { duration: 200 }),
      );
      flashScale.value = withSequence(
        withTiming(1.05, { duration: 300 }),
        withTiming(1, { duration: 200 }),
      );
      setTimeout(() => setDragonFlash(false), 800);
    }
  }, []);

  const scoreAnimStyle = useAnimatedStyle(() => ({
    transform: [{ scale: scoreScale.value }],
    opacity: scoreOpacity.value,
  }));

  const flashStyle = useAnimatedStyle(() => ({
    opacity: flashOpacity.value,
    transform: [{ scale: flashScale.value }],
  }));

  const getScoreColor = () => {
    if (percentage >= 80) return COLORS.green;
    if (percentage >= 50) return COLORS.orange;
    return COLORS.red;
  };

  return (
    <SafeAreaView style={styles.root}>
      {/* Dragon flash overlay */}
      {dragonFlash && (
        <Animated.View
          style={[StyleSheet.absoluteFill, styles.flashOverlay, flashStyle]}
          pointerEvents="none"
        />
      )}

      <ScrollView contentContainerStyle={styles.scroll} bounces={false}>
        {/* Title */}
        <Text style={styles.title}>Bravo {playerName} ! 🎉</Text>

        {/* Mascot */}
        <View style={styles.mascotArea}>
          {level === 6 && (
            <Text style={styles.dragonLabel}>✨ TRANSFORMATION FINALE ✨</Text>
          )}
          <FishMascot level={level} size={level === 6 ? 160 : 140} animate />
          <Text style={styles.mascotLabel}>{getMascotLabel(level)}</Text>
        </View>

        {/* Score card */}
        <Animated.View style={[styles.scoreCard, scoreAnimStyle]}>
          <Text style={styles.scoreLabel}>Ton score</Text>
          <Text style={[styles.scoreValue, { color: getScoreColor() }]}>
            {score + bonusPoints} / {maxScore}
          </Text>
          {timerEnabled && bonusPoints > 0 && (
            <Text style={styles.bonusBreakdown}>
              {score} pts + {bonusPoints} bonus ⚡
            </Text>
          )}
          <Text style={styles.percentText}>{percentage}%</Text>

          {/* Stars row */}
          <View style={styles.starsRow}>
            {[1, 2, 3, 4, 5].map((i) => (
              <Text
                key={i}
                style={[
                  styles.star,
                  { opacity: percentage >= i * 20 ? 1 : 0.2 },
                ]}
              >
                ⭐
              </Text>
            ))}
          </View>

          {/* Timer */}
          {timerEnabled && timeSeconds !== null && (
            <View style={styles.timerRow}>
              <Text style={styles.timerText}>⏱ Temps : {formatTime(timeSeconds)}</Text>
            </View>
          )}
        </Animated.View>

        {/* Buttons */}
        <View style={styles.btnRow}>
          <TouchableOpacity
            style={styles.replayBtn}
            onPress={() =>
              navigation.replace('Home')
            }
            activeOpacity={0.8}
          >
            <Text style={styles.replayBtnText}>🔄 Rejouer</Text>
          </TouchableOpacity>
          <TouchableOpacity
            style={styles.changeBtn}
            onPress={() => navigation.replace('Home')}
            activeOpacity={0.8}
          >
            <Text style={styles.changeBtnText}>👤 Changer</Text>
          </TouchableOpacity>
        </View>

        {/* History */}
        {history.length > 0 && (
          <View style={styles.historyCard}>
            <Text style={styles.historyTitle}>📜 Historique des parties</Text>
            {history.map((item, idx) => (
              <View
                key={item.id}
                style={[
                  styles.historyItem,
                  idx % 2 === 0 ? styles.historyItemEven : null,
                ]}
              >
                <View style={styles.historyLeft}>
                  <Text style={styles.historyName}>{item.playerName}</Text>
                  <Text style={styles.historyDate}>{formatDate(item.date)}</Text>
                </View>
                <View style={styles.historyRight}>
                  <Text style={styles.historyScore}>
                    {item.score}/{item.maxScore}
                  </Text>
                  <Text style={styles.historyQ}>{item.questionCount} questions</Text>
                  {item.timeSeconds !== null && (
                    <Text style={styles.historyTime}>
                      ⏱ {formatTime(item.timeSeconds)}
                    </Text>
                  )}
                </View>
              </View>
            ))}
          </View>
        )}
      </ScrollView>
    </SafeAreaView>
  );
}

const styles = StyleSheet.create({
  root: {
    flex: 1,
    backgroundColor: COLORS.background,
  },
  scroll: {
    flexGrow: 1,
    paddingHorizontal: 16,
    paddingTop: 32,
    paddingBottom: 40,
    gap: 20,
    alignItems: 'center',
  },
  flashOverlay: {
    backgroundColor: COLORS.yellow,
    zIndex: 999,
  },
  title: {
    fontSize: 32,
    fontWeight: '900',
    color: COLORS.textPrimary,
    textAlign: 'center',
  },
  mascotArea: {
    alignItems: 'center',
    gap: 8,
  },
  dragonLabel: {
    fontSize: 16,
    fontWeight: '800',
    color: COLORS.blue,
    letterSpacing: 1,
  },
  mascotLabel: {
    fontSize: 18,
    fontWeight: '800',
    color: COLORS.textSecondary,
    marginTop: 4,
  },
  scoreCard: {
    width: '100%',
    backgroundColor: COLORS.white,
    borderRadius: 28,
    padding: 24,
    alignItems: 'center',
    gap: 8,
    shadowColor: '#000',
    shadowOffset: { width: 0, height: 5 },
    shadowOpacity: 0.1,
    shadowRadius: 12,
    elevation: 6,
  },
  scoreLabel: {
    fontSize: 18,
    fontWeight: '700',
    color: COLORS.textSecondary,
  },
  scoreValue: {
    fontSize: 56,
    fontWeight: '900',
    lineHeight: 62,
  },
  percentText: {
    fontSize: 24,
    fontWeight: '800',
    color: COLORS.textSecondary,
  },
  starsRow: {
    flexDirection: 'row',
    gap: 6,
    marginTop: 4,
  },
  star: {
    fontSize: 28,
  },
  timerRow: {
    backgroundColor: COLORS.blueLight,
    borderRadius: 12,
    paddingHorizontal: 16,
    paddingVertical: 8,
    marginTop: 4,
  },
  bonusBreakdown: {
    fontSize: 15,
    fontWeight: '700',
    color: COLORS.orange,
  },
  timerText: {
    fontSize: 17,
    fontWeight: '700',
    color: COLORS.blueDark,
  },
  btnRow: {
    flexDirection: 'row',
    gap: 12,
    width: '100%',
  },
  replayBtn: {
    flex: 1,
    backgroundColor: COLORS.green,
    borderRadius: 18,
    paddingVertical: 16,
    alignItems: 'center',
    shadowColor: COLORS.greenDark,
    shadowOffset: { width: 0, height: 4 },
    shadowOpacity: 0.35,
    shadowRadius: 6,
    elevation: 5,
  },
  replayBtnText: {
    fontSize: 18,
    fontWeight: '900',
    color: COLORS.white,
  },
  changeBtn: {
    flex: 1,
    backgroundColor: COLORS.blue,
    borderRadius: 18,
    paddingVertical: 16,
    alignItems: 'center',
    shadowColor: COLORS.blueDark,
    shadowOffset: { width: 0, height: 4 },
    shadowOpacity: 0.3,
    shadowRadius: 6,
    elevation: 5,
  },
  changeBtnText: {
    fontSize: 18,
    fontWeight: '900',
    color: COLORS.white,
  },
  historyCard: {
    width: '100%',
    backgroundColor: COLORS.white,
    borderRadius: 24,
    padding: 16,
    gap: 2,
    shadowColor: '#000',
    shadowOffset: { width: 0, height: 3 },
    shadowOpacity: 0.07,
    shadowRadius: 8,
    elevation: 3,
  },
  historyTitle: {
    fontSize: 18,
    fontWeight: '800',
    color: COLORS.textPrimary,
    marginBottom: 8,
  },
  historyItem: {
    flexDirection: 'row',
    justifyContent: 'space-between',
    alignItems: 'center',
    paddingVertical: 10,
    paddingHorizontal: 8,
    borderRadius: 12,
  },
  historyItemEven: {
    backgroundColor: COLORS.background,
  },
  historyLeft: {
    gap: 2,
  },
  historyRight: {
    alignItems: 'flex-end',
    gap: 2,
  },
  historyName: {
    fontSize: 15,
    fontWeight: '800',
    color: COLORS.textPrimary,
  },
  historyDate: {
    fontSize: 12,
    color: COLORS.textSecondary,
  },
  historyScore: {
    fontSize: 16,
    fontWeight: '900',
    color: COLORS.green,
  },
  historyQ: {
    fontSize: 12,
    color: COLORS.textSecondary,
  },
  historyTime: {
    fontSize: 12,
    color: COLORS.blue,
    fontWeight: '700',
  },
});
