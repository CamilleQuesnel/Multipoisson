import React, { useEffect, useState } from 'react';
import { View, Text, StyleSheet, Dimensions } from 'react-native';
import Animated, {
  useSharedValue,
  useAnimatedStyle,
  withSpring,
  withTiming,
  withSequence,
  runOnJS,
} from 'react-native-reanimated';
import { StackNavigationProp } from '@react-navigation/stack';
import { RouteProp } from '@react-navigation/native';
import { RootStackParamList } from '../types';
import { COLORS } from '../theme/colors';

type Props = {
  navigation: StackNavigationProp<RootStackParamList, 'Countdown'>;
  route: RouteProp<RootStackParamList, 'Countdown'>;
};

const STEPS: { label: string; color: string; bg: string }[] = [
  { label: '3', color: COLORS.white, bg: COLORS.orange },
  { label: '2', color: COLORS.white, bg: COLORS.purple },
  { label: '1', color: COLORS.white, bg: COLORS.blue },
  { label: 'GO !', color: COLORS.white, bg: COLORS.green },
];

const { width, height } = Dimensions.get('window');

export default function CountdownScreen({ navigation, route }: Props) {
  const { playerName, config } = route.params;
  const [stepIndex, setStepIndex] = useState(0);

  const scale = useSharedValue(0.2);
  const opacity = useSharedValue(0);
  const bgOpacity = useSharedValue(1);

  const animatedStyle = useAnimatedStyle(() => ({
    transform: [{ scale: scale.value }],
    opacity: opacity.value,
  }));

  const bgAnimStyle = useAnimatedStyle(() => ({
    opacity: bgOpacity.value,
  }));

  const navigateToGame = () => {
    navigation.replace('Game', { playerName, config });
  };

  const animateStep = (index: number) => {
    // Reset
    scale.value = 0.2;
    opacity.value = 0;

    // Animate in
    scale.value = withSpring(1, { damping: 7, stiffness: 200 });
    opacity.value = withTiming(1, { duration: 180 });

    const isGo = index === STEPS.length - 1;
    const holdDuration = isGo ? 600 : 450;

    setTimeout(() => {
      // Animate out
      scale.value = withTiming(1.6, { duration: 200 });
      opacity.value = withTiming(0, { duration: 200 });

      setTimeout(() => {
        if (index < STEPS.length - 1) {
          setStepIndex(index + 1);
        } else {
          navigateToGame();
        }
      }, 220);
    }, holdDuration);
  };

  useEffect(() => {
    const timer = setTimeout(() => animateStep(stepIndex), 80);
    return () => clearTimeout(timer);
  }, [stepIndex]);

  const step = STEPS[stepIndex];

  return (
    <Animated.View style={[styles.root, { backgroundColor: step.bg }, bgAnimStyle]}>
      <Animated.Text style={[styles.countText, { color: step.color }, animatedStyle]}>
        {step.label}
      </Animated.Text>
      {stepIndex === STEPS.length - 1 && (
        <Animated.Text style={[styles.subText, animatedStyle]}>
          C'est parti ! 🐟
        </Animated.Text>
      )}
    </Animated.View>
  );
}

const styles = StyleSheet.create({
  root: {
    flex: 1,
    alignItems: 'center',
    justifyContent: 'center',
  },
  countText: {
    fontSize: 130,
    fontWeight: '900',
    textShadowColor: 'rgba(0,0,0,0.2)',
    textShadowOffset: { width: 3, height: 3 },
    textShadowRadius: 8,
    letterSpacing: -2,
  },
  subText: {
    fontSize: 28,
    fontWeight: '800',
    color: 'rgba(255,255,255,0.9)',
    marginTop: -10,
  },
});
