import React, { useRef } from 'react';
import { View, StyleSheet, ScrollView, NativeSyntheticEvent, NativeScrollEvent } from 'react-native';
import Animated, {
  useSharedValue,
  useAnimatedStyle,
  withTiming,
} from 'react-native-reanimated';
import { COLORS } from '../theme/colors';

interface ScrollIndicatorProps {
  scrollY: number;
  contentHeight: number;
  containerHeight: number;
}

const TRACK_PADDING = 24;
const MIN_THUMB = 36;

export function ScrollIndicator({ scrollY, contentHeight, containerHeight }: ScrollIndicatorProps) {
  if (contentHeight <= containerHeight + 10) return null;

  const trackHeight = containerHeight - TRACK_PADDING * 2;
  const thumbHeight = Math.max(MIN_THUMB, (containerHeight / contentHeight) * trackHeight);
  const maxScroll = contentHeight - containerHeight;
  const maxThumbY = trackHeight - thumbHeight;
  const thumbY = maxScroll > 0 ? (scrollY / maxScroll) * maxThumbY : 0;

  return (
    <View style={[styles.track, { height: trackHeight, top: TRACK_PADDING }]}>
      <View
        style={[
          styles.thumb,
          { height: thumbHeight, transform: [{ translateY: thumbY }] },
        ]}
      />
    </View>
  );
}

const styles = StyleSheet.create({
  track: {
    position: 'absolute',
    right: 4,
    width: 5,
    backgroundColor: COLORS.border,
    borderRadius: 3,
  },
  thumb: {
    width: 5,
    backgroundColor: COLORS.blue,
    borderRadius: 3,
    opacity: 0.7,
  },
});
