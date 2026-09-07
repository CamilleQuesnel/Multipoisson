import AsyncStorage from '@react-native-async-storage/async-storage';
import { GameResult } from '../types';

const HISTORY_KEY = 'multipoisson_history';
const PLAYER_NAME_KEY = 'multipoisson_last_player';
const MAX_HISTORY = 50;

export async function saveGameResult(result: GameResult): Promise<void> {
  try {
    const history = await getHistory();
    const updated = [result, ...history].slice(0, MAX_HISTORY);
    await AsyncStorage.setItem(HISTORY_KEY, JSON.stringify(updated));
  } catch (e) {
    console.warn('Failed to save game result:', e);
  }
}

export async function getHistory(): Promise<GameResult[]> {
  try {
    const raw = await AsyncStorage.getItem(HISTORY_KEY);
    if (!raw) return [];
    return JSON.parse(raw) as GameResult[];
  } catch (e) {
    console.warn('Failed to load history:', e);
    return [];
  }
}

export async function savePlayerName(name: string): Promise<void> {
  try {
    await AsyncStorage.setItem(PLAYER_NAME_KEY, name);
  } catch (e) {
    console.warn('Failed to save player name:', e);
  }
}

export async function getLastPlayerName(): Promise<string> {
  try {
    const name = await AsyncStorage.getItem(PLAYER_NAME_KEY);
    return name ?? '';
  } catch (e) {
    return '';
  }
}
