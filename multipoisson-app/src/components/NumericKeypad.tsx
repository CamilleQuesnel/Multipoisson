import React from 'react';
import {
  View,
  Text,
  TouchableOpacity,
  StyleSheet,
  Dimensions,
} from 'react-native';
import { COLORS } from '../theme/colors';

interface NumericKeypadProps {
  value: string;
  onChange: (value: string) => void;
  onValidate: () => void;
  maxLength?: number;
}

const BUTTON_ROWS = [
  ['1', '2', '3'],
  ['4', '5', '6'],
  ['7', '8', '9'],
  ['←', '0', '✓'],
];

const { width } = Dimensions.get('window');
const KEYPAD_WIDTH = Math.min(width - 32, 360);
const BTN_SIZE = Math.floor((KEYPAD_WIDTH - 24) / 3);

export default function NumericKeypad({
  value,
  onChange,
  onValidate,
  maxLength = 4,
}: NumericKeypadProps) {
  const handlePress = (key: string) => {
    if (key === '←') {
      onChange(value.slice(0, -1));
    } else if (key === '✓') {
      onValidate();
    } else {
      if (value.length < maxLength) {
        onChange(value + key);
      }
    }
  };

  return (
    <View style={styles.wrapper}>
      {/* Answer display */}
      <View style={styles.display}>
        <Text style={styles.displayText}>{value || '?'}</Text>
      </View>

      {/* Keys */}
      <View style={styles.keypad}>
        {BUTTON_ROWS.map((row, rowIdx) => (
          <View key={rowIdx} style={styles.row}>
            {row.map((key) => {
              const isValidate = key === '✓';
              const isDelete = key === '←';
              return (
                <TouchableOpacity
                  key={key}
                  style={[
                    styles.btn,
                    isValidate && styles.btnValidate,
                    isDelete && styles.btnDelete,
                  ]}
                  onPress={() => handlePress(key)}
                  activeOpacity={0.7}
                >
                  <Text
                    style={[
                      styles.btnText,
                      isValidate && styles.btnTextValidate,
                      isDelete && styles.btnTextDelete,
                    ]}
                  >
                    {key}
                  </Text>
                </TouchableOpacity>
              );
            })}
          </View>
        ))}
      </View>
    </View>
  );
}

const styles = StyleSheet.create({
  wrapper: {
    alignItems: 'center',
    gap: 12,
  },
  display: {
    width: KEYPAD_WIDTH,
    height: 70,
    backgroundColor: COLORS.white,
    borderRadius: 20,
    borderWidth: 3,
    borderColor: COLORS.blue,
    justifyContent: 'center',
    alignItems: 'center',
    shadowColor: COLORS.blue,
    shadowOffset: { width: 0, height: 3 },
    shadowOpacity: 0.15,
    shadowRadius: 6,
    elevation: 4,
  },
  displayText: {
    fontSize: 40,
    fontWeight: '800',
    color: COLORS.textPrimary,
    letterSpacing: 4,
  },
  keypad: {
    width: KEYPAD_WIDTH,
    gap: 8,
  },
  row: {
    flexDirection: 'row',
    gap: 8,
    justifyContent: 'center',
  },
  btn: {
    width: BTN_SIZE,
    height: BTN_SIZE * 0.72,
    backgroundColor: COLORS.white,
    borderRadius: 16,
    justifyContent: 'center',
    alignItems: 'center',
    borderWidth: 2,
    borderColor: COLORS.border,
    shadowColor: '#000',
    shadowOffset: { width: 0, height: 3 },
    shadowOpacity: 0.08,
    shadowRadius: 4,
    elevation: 3,
  },
  btnValidate: {
    backgroundColor: COLORS.green,
    borderColor: COLORS.greenDark,
  },
  btnDelete: {
    backgroundColor: COLORS.orangeLight,
    borderColor: COLORS.orange,
  },
  btnText: {
    fontSize: 28,
    fontWeight: '700',
    color: COLORS.textPrimary,
  },
  btnTextValidate: {
    color: COLORS.white,
    fontSize: 30,
  },
  btnTextDelete: {
    color: COLORS.orange,
    fontSize: 26,
  },
});
