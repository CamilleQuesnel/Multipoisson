export interface GameConfig {
  selectedTables: number[];
  excludeZero: boolean;
  excludeOne: boolean;
  excludeTen: boolean;
  timerEnabled: boolean;
  questionCount: number;
}

export interface Question {
  a: number;
  b: number;
  answer: number;
}

export interface GameResult {
  id: string;
  date: string;
  playerName: string;
  questionCount: number;
  score: number;
  maxScore: number;
  timeSeconds: number | null;
}

export type RootStackParamList = {
  Home: undefined;
  TableSelection: { playerName: string };
  AdvancedConfig: { playerName: string; selectedTables: number[] };
  Countdown: { playerName: string; config: GameConfig };
  Game: { playerName: string; config: GameConfig };
  Result: {
    playerName: string;
    score: number;
    maxScore: number;
    bonusPoints: number;
    questionCount: number;
    timeSeconds: number | null;
    timerEnabled: boolean;
  };
};
