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
import { RootStackParamList } from '../types';
import { COLORS } from '../theme/colors';
import { ScrollIndicator } from '../components/ScrollIndicator';

type Props = {
  navigation: StackNavigationProp<RootStackParamList, 'TableSelection'>;
  route: RouteProp<RootStackParamList, 'TableSelection'>;
};

const TABLE_COLORS = [
  COLORS.blue,
  COLORS.green,
  COLORS.orange,
  COLORS.purple,
  '#FF4B4B',
  '#1CB0F6',
  '#58CC02',
  '#FF9600',
  '#CE82FF',
  '#FFD900',
];

export default function TableSelectionScreen({ navigation, route }: Props) {
  const { playerName } = route.params;
  const [selected, setSelected] = useState<number[]>([1, 2, 3, 4, 5, 6, 7, 8, 9, 10]);
  const [error, setError] = useState('');

  const [scrollY, setScrollY] = useState(0);
  const [contentHeight, setContentHeight] = useState(0);
  const [containerHeight, setContainerHeight] = useState(0);

  const toggle = (table: number) => {
    setError('');
    setSelected((prev) => {
      if (prev.includes(table)) {
        if (prev.length === 1) {
          setError('Garde au moins une table ! 🐟');
          return prev;
        }
        return prev.filter((t) => t !== table);
      }
      return [...prev, table].sort((a, b) => a - b);
    });
  };

  const handleNext = () => {
    if (selected.length === 0) {
      setError('Choisis au moins une table !');
      return;
    }
    navigation.navigate('AdvancedConfig', { playerName, selectedTables: selected });
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
          {/* Header */}
          <View style={styles.header}>
            <Text style={styles.playerGreeting}>Salut {playerName} ! 👋</Text>
            <Text style={styles.title}>Quelles tables tu veux travailler ?</Text>
          </View>

          {/* Grid */}
          <View style={styles.grid}>
            {[1, 2, 3, 4, 5, 6, 7, 8, 9, 10].map((table) => {
              const isSelected = selected.includes(table);
              const color = TABLE_COLORS[table - 1];
              return (
                <TouchableOpacity
                  key={table}
                  style={[
                    styles.tableBtn,
                    isSelected
                      ? { backgroundColor: color, borderColor: color }
                      : styles.tableBtnOff,
                  ]}
                  onPress={() => toggle(table)}
                  activeOpacity={0.7}
                >
                  <Text
                    style={[
                      styles.tableBtnNum,
                      isSelected ? styles.tableBtnNumOn : styles.tableBtnNumOff,
                    ]}
                  >
                    {table}
                  </Text>
                  <Text
                    style={[
                      styles.tableBtnLabel,
                      isSelected ? styles.tableBtnLabelOn : styles.tableBtnLabelOff,
                    ]}
                  >
                    × {table}
                  </Text>
                  {isSelected && <Text style={styles.check}>✓</Text>}
                </TouchableOpacity>
              );
            })}
          </View>

          {/* Select all / none */}
          <View style={styles.shortcuts}>
            <TouchableOpacity
              style={styles.shortcutBtn}
              onPress={() => {
                setSelected([1, 2, 3, 4, 5, 6, 7, 8, 9, 10]);
                setError('');
              }}
            >
              <Text style={styles.shortcutText}>Tout sélectionner</Text>
            </TouchableOpacity>
            <TouchableOpacity
              style={[styles.shortcutBtn, styles.shortcutBtnAlt]}
              onPress={() => {
                if (selected.length > 1) {
                  setSelected([selected[0]]);
                }
              }}
            >
              <Text style={[styles.shortcutText, styles.shortcutTextAlt]}>
                Tout désélectionner
              </Text>
            </TouchableOpacity>
          </View>

          {error ? <Text style={styles.errorText}>{error}</Text> : null}

          <TouchableOpacity
            style={styles.nextBtn}
            onPress={handleNext}
            activeOpacity={0.8}
          >
            <Text style={styles.nextBtnText}>Suivant →</Text>
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
    paddingHorizontal: 20,
    paddingTop: 40,
    paddingBottom: 40,
    paddingRight: 18,
    gap: 20,
  },
  header: {
    alignItems: 'center',
    gap: 6,
  },
  playerGreeting: {
    fontSize: 18,
    fontWeight: '700',
    color: COLORS.blue,
  },
  title: {
    fontSize: 24,
    fontWeight: '900',
    color: COLORS.textPrimary,
    textAlign: 'center',
  },
  grid: {
    flexDirection: 'row',
    flexWrap: 'wrap',
    gap: 10,
    justifyContent: 'center',
  },
  tableBtn: {
    width: 100,
    height: 90,
    borderRadius: 20,
    alignItems: 'center',
    justifyContent: 'center',
    borderWidth: 3,
    position: 'relative',
    shadowColor: '#000',
    shadowOffset: { width: 0, height: 3 },
    shadowOpacity: 0.12,
    shadowRadius: 5,
    elevation: 4,
  },
  tableBtnOff: {
    backgroundColor: COLORS.white,
    borderColor: COLORS.border,
  },
  tableBtnNum: {
    fontSize: 30,
    fontWeight: '900',
    lineHeight: 34,
  },
  tableBtnNumOn: {
    color: COLORS.white,
  },
  tableBtnNumOff: {
    color: COLORS.textSecondary,
  },
  tableBtnLabel: {
    fontSize: 14,
    fontWeight: '700',
  },
  tableBtnLabelOn: {
    color: 'rgba(255,255,255,0.85)',
  },
  tableBtnLabelOff: {
    color: COLORS.textSecondary,
  },
  check: {
    position: 'absolute',
    top: 6,
    right: 8,
    fontSize: 13,
    color: 'rgba(255,255,255,0.9)',
    fontWeight: '900',
  },
  shortcuts: {
    flexDirection: 'row',
    gap: 10,
    justifyContent: 'center',
  },
  shortcutBtn: {
    backgroundColor: COLORS.blueLight,
    borderRadius: 12,
    paddingHorizontal: 16,
    paddingVertical: 8,
  },
  shortcutBtnAlt: {
    backgroundColor: COLORS.orangeLight,
  },
  shortcutText: {
    color: COLORS.blue,
    fontWeight: '700',
    fontSize: 14,
  },
  shortcutTextAlt: {
    color: COLORS.orange,
  },
  errorText: {
    color: COLORS.red,
    fontSize: 16,
    fontWeight: '700',
    textAlign: 'center',
  },
  nextBtn: {
    backgroundColor: COLORS.green,
    borderRadius: 20,
    paddingVertical: 18,
    alignItems: 'center',
    shadowColor: COLORS.greenDark,
    shadowOffset: { width: 0, height: 4 },
    shadowOpacity: 0.35,
    shadowRadius: 6,
    elevation: 5,
  },
  nextBtnText: {
    color: COLORS.white,
    fontSize: 22,
    fontWeight: '900',
  },
});
