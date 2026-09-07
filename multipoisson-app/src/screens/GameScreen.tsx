import React, { useState, useEffect, useRef, useCallback } from 'react';
import {
  View,
  Text,
  StyleSheet,
  Dimensions,
  SafeAreaView,
  TouchableOpacity,
} from 'react-native';
import Animated, {
  useSharedValue,
  useAnimatedStyle,
  withSpring,
  withTiming,
} from 'react-native-reanimated';
import { StackNavigationProp } from '@react-navigation/stack';
import { RouteProp } from '@react-navigation/native';
import { RootStackParamList, Question } from '../types';
import { COLORS } from '../theme/colors';
import NumericKeypad from '../components/NumericKeypad';
import { generateQuestions, getBonusForTime, getBonusLabel, getDifficultyBonus, getDifficultyLabel } from '../utils/gameLogic';

type Props = {
  navigation: StackNavigationProp<RootStackParamList, 'Game'>;
  route: RouteProp<RootStackParamList, 'Game'>;
};

type GamePhase = 'playing' | 'feedback-correct' | 'feedback-wrong';

const FEEDBACK_DURATION_CORRECT = 2000;
const FEEDBACK_DURATION_WRONG   = 4500; // +1.5s, tap pour passer

export default function GameScreen({ navigation, route }: Props) {
  const { playerName, config } = route.params;

  const [questions] = useState<Question[]>(() => generateQuestions(config));
  const [currentIndex, setCurrentIndex] = useState(0);
  const [inputValue, setInputValue] = useState('');
  const [correctCount, setCorrectCount] = useState(0);
  const [bonusPoints, setBonusPoints] = useState(0);
  const [lastBonus, setLastBonus] = useState(0);           // bonus vitesse (0/1/3/5)
  const [lastDiffBonus, setLastDiffBonus] = useState(0);   // bonus difficulté (0/1/2)
  const [phase, setPhase] = useState<GamePhase>('playing');
  const [elapsedSeconds, setElapsedSeconds] = useState(0);

  const timerPausedRef = useRef(false);
  const timerRef = useRef<ReturnType<typeof setInterval> | null>(null);
  const feedbackTimeoutRef = useRef<ReturnType<typeof setTimeout> | null>(null);
  const questionStartTimeRef = useRef<number>(Date.now());
  // Fonction de passage à la question suivante, appelable depuis tap ou timeout
  const doAdvanceRef = useRef<(() => void) | null>(null);

  // Animations
  const feedbackScale = useSharedValue(0);
  const feedbackOpacity = useSharedValue(0);
  const questionScale = useSharedValue(1);

  // ── Chronomètre global ────────────────────────────────────
  useEffect(() => {
    if (!config.timerEnabled) return;
    timerRef.current = setInterval(() => {
      if (!timerPausedRef.current) {
        setElapsedSeconds((s) => s + 1);
      }
    }, 1000);
    return () => {
      if (timerRef.current) clearInterval(timerRef.current);
    };
  }, []);

  // ── Démarrage du chrono par question ──────────────────────
  useEffect(() => {
    if (phase === 'playing') {
      questionStartTimeRef.current = Date.now();
    }
  }, [currentIndex, phase]);

  // ── Phase feedback ────────────────────────────────────────
  useEffect(() => {
    if (phase === 'playing') return;

    timerPausedRef.current = true;

    feedbackScale.value = withSpring(1, { damping: 8, stiffness: 180 });
    feedbackOpacity.value = withTiming(1, { duration: 200 });

    // Logique de passage : partagée entre le timeout et le tap
    let alreadyDismissed = false;
    const runDismiss = () => {
      if (alreadyDismissed) return;
      alreadyDismissed = true;
      if (feedbackTimeoutRef.current) {
        clearTimeout(feedbackTimeoutRef.current);
        feedbackTimeoutRef.current = null;
      }

      feedbackOpacity.value = withTiming(0, { duration: 200 });
      feedbackScale.value = withTiming(0.8, { duration: 200 });

      setTimeout(() => {
        timerPausedRef.current = false;
        const nextIndex = currentIndex + 1;

        if (nextIndex >= questions.length) {
          if (timerRef.current) clearInterval(timerRef.current);
          navigation.replace('Result', {
            playerName,
            score: correctCount * 5,
            maxScore: questions.length * 5,
            bonusPoints,
            questionCount: questions.length,
            timeSeconds: config.timerEnabled ? elapsedSeconds : null,
            timerEnabled: config.timerEnabled,
          });
        } else {
          setCurrentIndex(nextIndex);
          setInputValue('');
          setLastBonus(0);
          setLastDiffBonus(0);
          setPhase('playing');
          feedbackScale.value = 0;
          feedbackOpacity.value = 0;
        }
      }, 220);
    };

    doAdvanceRef.current = runDismiss;

    feedbackTimeoutRef.current = setTimeout(
      runDismiss,
      phase === 'feedback-correct' ? FEEDBACK_DURATION_CORRECT : FEEDBACK_DURATION_WRONG,
    );

    return () => {
      if (feedbackTimeoutRef.current) clearTimeout(feedbackTimeoutRef.current);
      doAdvanceRef.current = null;
    };
  }, [phase]);

  // Question — animation d'entrée
  useEffect(() => {
    if (phase !== 'playing') return;
    questionScale.value = 0.85;
    questionScale.value = withSpring(1, { damping: 10, stiffness: 200 });
  }, [currentIndex, phase]);

  // Nettoyage
  useEffect(() => {
    return () => {
      if (timerRef.current) clearInterval(timerRef.current);
      if (feedbackTimeoutRef.current) clearTimeout(feedbackTimeoutRef.current);
    };
  }, []);

  const handleWrongTap = useCallback(() => {
    if (phase !== 'feedback-wrong') return;
    doAdvanceRef.current?.();
  }, [phase]);

  const handleValidate = useCallback(() => {
    if (phase !== 'playing' || !inputValue) return;

    const question = questions[currentIndex];
    const userAnswer = parseInt(inputValue, 10);
    const isCorrect = userAnswer === question.answer;

    if (isCorrect) {
      const timeTaken = Date.now() - questionStartTimeRef.current;
      const speedBonus = getBonusForTime(timeTaken, config.timerEnabled);
      const diffBonus  = getDifficultyBonus(question.a);
      const total = speedBonus + diffBonus;
      if (total > 0) setBonusPoints((b) => b + total);
      setLastBonus(speedBonus);
      setLastDiffBonus(diffBonus);
      setCorrectCount((c) => c + 1);
      setPhase('feedback-correct');
    } else {
      setLastBonus(0);
      setLastDiffBonus(0);
      setPhase('feedback-wrong');
    }
  }, [phase, inputValue, currentIndex, questions, config.timerEnabled]);

  const feedbackAnimStyle = useAnimatedStyle(() => ({
    transform: [{ scale: feedbackScale.value }],
    opacity: feedbackOpacity.value,
  }));

  const questionAnimStyle = useAnimatedStyle(() => ({
    transform: [{ scale: questionScale.value }],
  }));

  if (questions.length === 0) {
    return (
      <SafeAreaView style={styles.root}>
        <Text style={styles.emptyText}>
          Aucune question disponible avec ces réglages 😕{'\n'}
          Retourne modifier les paramètres !
        </Text>
      </SafeAreaView>
    );
  }

  const question = questions[currentIndex];
  const progress = currentIndex / questions.length;
  const displayScore = correctCount * 5 + bonusPoints;

  return (
    <SafeAreaView style={styles.root}>
      {/* ── TOP BAR ───────────────── */}
      <View style={styles.topBar}>
        <View style={styles.scoreChip}>
          <Text style={styles.scoreText}>⭐ {displayScore}</Text>
        </View>
        <View style={styles.questionCounter}>
          <Text style={styles.questionCounterText}>
            {currentIndex + 1} / {questions.length}
          </Text>
        </View>
      </View>

      {/* ── PROGRESS BAR ──────────── */}
      <View style={styles.progressBg}>
        <View style={[styles.progressFill, { width: `${progress * 100}%` }]} />
      </View>

      {/* ── QUESTION ──────────────── */}
      <Animated.View style={[styles.questionArea, questionAnimStyle]}>
        <Text style={styles.questionText}>
          {question.a} × {question.b} = ?
        </Text>
        {getDifficultyBonus(question.a) > 0 && (
          <View style={styles.difficultyBadge}>
            <Text style={styles.difficultyBadgeText}>
              {getDifficultyBonus(question.a) === 2 ? '🔥 +2 bonus difficulté' : '💪 +1 bonus difficulté'}
            </Text>
          </View>
        )}
      </Animated.View>

      {/* ── KEYPAD ────────────────── */}
      <View style={styles.keypadArea}>
        <NumericKeypad
          value={inputValue}
          onChange={setInputValue}
          onValidate={handleValidate}
        />
      </View>

      {/* ── FEEDBACK OVERLAY ──────── */}
      {phase !== 'playing' && (
        <Animated.View
          style={[
            styles.feedbackOverlay,
            phase === 'feedback-correct'
              ? styles.feedbackOverlayCorrect
              : styles.feedbackOverlayWrong,
            feedbackAnimStyle,
          ]}
        >
          {phase === 'feedback-correct' ? (
            <>
              <Text style={styles.feedbackIcon}>✓</Text>
              <Text style={styles.feedbackTextCorrect}>Bravo !</Text>
              <Text style={styles.feedbackSubText}>+5 points ⭐</Text>
              {lastBonus > 0 && (
                <Text style={styles.feedbackBonusText}>{getBonusLabel(lastBonus)}</Text>
              )}
              {lastDiffBonus > 0 && (
                <Text style={styles.feedbackDiffText}>{getDifficultyLabel(lastDiffBonus)}</Text>
              )}
            </>
          ) : (
            <TouchableOpacity
              style={styles.feedbackWrongTouchable}
              onPress={handleWrongTap}
              activeOpacity={0.95}
            >
              <Text style={styles.feedbackIcon}>✗</Text>
              <Text style={styles.feedbackTextWrong}>Pas tout à fait...</Text>
              <Text style={styles.feedbackAnswerText}>
                {question.a} × {question.b} = {question.answer}
              </Text>
              <Text style={styles.feedbackSubText}>Retiens bien ! 🧠</Text>
              <Text style={styles.tapToContinue}>Appuie pour continuer →</Text>
            </TouchableOpacity>
          )}
        </Animated.View>
      )}
    </SafeAreaView>
  );
}

const styles = StyleSheet.create({
  root: {
    flex: 1,
    backgroundColor: COLORS.background,
  },
  topBar: {
    flexDirection: 'row',
    justifyContent: 'space-between',
    alignItems: 'center',
    paddingHorizontal: 16,
    paddingTop: 12,
    paddingBottom: 8,
  },
  scoreChip: {
    backgroundColor: COLORS.yellowLight,
    borderRadius: 12,
    paddingHorizontal: 14,
    paddingVertical: 6,
    borderWidth: 2,
    borderColor: COLORS.yellow,
  },
  scoreText: {
    fontSize: 18,
    fontWeight: '800',
    color: '#8B6800',
  },
  questionCounter: {
    backgroundColor: COLORS.blueLight,
    borderRadius: 12,
    paddingHorizontal: 14,
    paddingVertical: 6,
    borderWidth: 2,
    borderColor: COLORS.blue,
  },
  questionCounterText: {
    fontSize: 16,
    fontWeight: '800',
    color: COLORS.blueDark,
  },
  progressBg: {
    height: 12,
    backgroundColor: COLORS.border,
    marginHorizontal: 16,
    borderRadius: 6,
    overflow: 'hidden',
  },
  progressFill: {
    height: '100%',
    backgroundColor: COLORS.green,
    borderRadius: 6,
  },
  questionArea: {
    flex: 1,
    justifyContent: 'center',
    alignItems: 'center',
    paddingHorizontal: 20,
  },
  questionText: {
    fontSize: 58,
    fontWeight: '900',
    color: COLORS.blue,
    textAlign: 'center',
    textShadowColor: 'rgba(28,176,246,0.2)',
    textShadowOffset: { width: 2, height: 2 },
    textShadowRadius: 6,
  },
  keypadArea: {
    paddingHorizontal: 16,
    paddingBottom: 20,
    paddingTop: 8,
  },
  emptyText: {
    flex: 1,
    textAlign: 'center',
    fontSize: 18,
    color: COLORS.textSecondary,
    padding: 40,
    lineHeight: 28,
  },
  feedbackOverlay: {
    ...StyleSheet.absoluteFillObject,
    alignItems: 'center',
    justifyContent: 'center',
    gap: 12,
  },
  feedbackOverlayCorrect: {
    backgroundColor: COLORS.green,
  },
  feedbackOverlayWrong: {
    backgroundColor: '#FF6B35',
  },
  feedbackIcon: {
    fontSize: 90,
    color: COLORS.white,
    fontWeight: '900',
    textShadowColor: 'rgba(0,0,0,0.2)',
    textShadowOffset: { width: 3, height: 3 },
    textShadowRadius: 6,
  },
  feedbackTextCorrect: {
    fontSize: 42,
    fontWeight: '900',
    color: COLORS.white,
  },
  feedbackTextWrong: {
    fontSize: 32,
    fontWeight: '900',
    color: COLORS.white,
  },
  feedbackAnswerText: {
    fontSize: 50,
    fontWeight: '900',
    color: COLORS.white,
    textAlign: 'center',
    textShadowColor: 'rgba(0,0,0,0.2)',
    textShadowOffset: { width: 2, height: 2 },
    textShadowRadius: 4,
  },
  feedbackSubText: {
    fontSize: 20,
    fontWeight: '700',
    color: 'rgba(255,255,255,0.9)',
  },
  feedbackBonusText: {
    fontSize: 18,
    fontWeight: '800',
    color: COLORS.yellow,
    backgroundColor: 'rgba(0,0,0,0.15)',
    paddingHorizontal: 16,
    paddingVertical: 6,
    borderRadius: 12,
  },
  feedbackDiffText: {
    fontSize: 17,
    fontWeight: '800',
    color: '#FFE066',
    backgroundColor: 'rgba(0,0,0,0.15)',
    paddingHorizontal: 16,
    paddingVertical: 6,
    borderRadius: 12,
  },
  difficultyBadge: {
    marginTop: 10,
    backgroundColor: 'rgba(255,255,255,0.18)',
    borderRadius: 12,
    paddingHorizontal: 14,
    paddingVertical: 5,
  },
  difficultyBadgeText: {
    fontSize: 14,
    fontWeight: '700',
    color: COLORS.textSecondary,
    textAlign: 'center',
  },
  feedbackWrongTouchable: {
    flex: 1,
    width: '100%',
    alignItems: 'center',
    justifyContent: 'center',
    gap: 12,
  },
  tapToContinue: {
    marginTop: 12,
    fontSize: 15,
    fontWeight: '700',
    color: 'rgba(255,255,255,0.65)',
    letterSpacing: 0.5,
  },
});
