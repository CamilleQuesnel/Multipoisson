import { GameConfig, Question } from '../types';

function shuffleArray<T>(arr: T[]): T[] {
  const result = [...arr];
  for (let i = result.length - 1; i > 0; i--) {
    const j = Math.floor(Math.random() * (i + 1));
    [result[i], result[j]] = [result[j], result[i]];
  }
  return result;
}

export function generateQuestions(config: GameConfig): Question[] {
  const pool: Question[] = [];

  for (const table of config.selectedTables) {
    for (let m = 0; m <= 10; m++) {
      if (config.excludeZero && m === 0) continue;
      if (config.excludeOne && m === 1) continue;
      if (config.excludeTen && m === 10) continue;
      pool.push({ a: table, b: m, answer: table * m });
    }
  }

  if (pool.length === 0) return [];

  const result: Question[] = [];
  while (result.length < config.questionCount) {
    result.push(...shuffleArray(pool));
  }

  return result.slice(0, config.questionCount);
}

export function formatTime(totalSeconds: number): string {
  const mins = Math.floor(totalSeconds / 60);
  const secs = totalSeconds % 60;
  if (mins === 0) return `${secs}s`;
  return `${mins}min ${secs.toString().padStart(2, '0')}s`;
}

// Niveau de base selon le % de score (pour 0–99%)
function baseLevelFromPct(pct: number): number {
  if (pct <= 20) return 1;
  if (pct <= 40) return 2;
  if (pct <= 60) return 3;
  if (pct <= 80) return 4;
  return 5; // 81–99 %
}

// Niveau final (100%) selon le nombre de questions
function perfectLevelFromCount(questionCount: number): number {
  if (questionCount >= 500) return 19; // Poséidon
  if (questionCount >= 300) return 18; // Léviathan
  if (questionCount >= 200) return 17; // Capibara
  if (questionCount >= 150) return 16; // Mégalodon
  if (questionCount >= 100) return 15; // Grand Requin Blanc
  if (questionCount >= 90)  return 14; // Tortue Luth
  if (questionCount >= 80)  return 13; // Espadon
  if (questionCount >= 70)  return 12; // Pieuvre
  if (questionCount >= 60)  return 11; // Baleine à Bosse
  if (questionCount >= 50)  return 10; // Orque
  if (questionCount >= 40)  return 9;  // Requin Marteau
  if (questionCount >= 30)  return 8;  // Béluga
  if (questionCount >= 20)  return 7;  // Narval
  return 6;                            // Dragon Bleu (10 questions)
}

export function getMascotLevel(
  score: number,
  maxScore: number,
  questionCount: number = 10,
): number {
  if (maxScore === 0) return 1;
  const pct = (score / maxScore) * 100;
  if (pct >= 100) return perfectLevelFromCount(questionCount);
  return baseLevelFromPct(pct);
}

export function getMascotLabel(level: number): string {
  switch (level) {
    case 1:  return 'Petit poisson';
    case 2:  return 'Poisson coloré';
    case 3:  return 'Poisson brillant';
    case 4:  return 'Poisson tropical';
    case 5:  return 'Poisson légendaire';
    case 6:  return '🐉 DRAGON BLEU';
    case 7:  return '🦄 NARVAL';
    case 8:  return '🤍 BÉLUGA';
    case 9:  return '🔨 REQUIN MARTEAU';
    case 10: return '🖤 ORQUE';
    case 11: return '💙 BALEINE À BOSSE';
    case 12: return '🪸 PIEUVRE';
    case 13: return '⚔️ ESPADON';
    case 14: return '🐢 TORTUE LUTH';
    case 15: return '🦷 GRAND REQUIN BLANC';
    case 16: return '💀 MÉGALODON';
    case 17: return '🦫 CAPIBARA';
    case 18: return '👁️ LÉVIATHAN';
    case 19: return '🔱 POSÉIDON';
    default: return 'Poisson';
  }
}

// Bonus rapide selon temps par question
export function getBonusForTime(ms: number, timerEnabled: boolean): number {
  if (!timerEnabled) return 0;
  if (ms < 5_000)  return 5;
  if (ms < 10_000) return 3;
  if (ms < 20_000) return 1;
  return 0;
}

export function getBonusLabel(bonus: number): string {
  switch (bonus) {
    case 5: return '⚡⚡ ULTRA ! +5 bonus';
    case 3: return '⚡ RAPIDE ! +3 bonus';
    case 1: return '👍 BIEN ! +1 bonus';
    default: return '';
  }
}

// ── Bonus de difficulté ──────────────────────────────────
// ×6 ×7 ×8 ×9 = tables difficiles  → +2 pts
// ×3 ×4 ×5    = tables intermédiaires → +1 pt
// ×0 ×1 ×2 ×10 = tables faciles    → +0 pt

export function getDifficultyBonus(table: number): number {
  if ([6, 7, 8, 9].includes(table)) return 2;
  if ([3, 4, 5].includes(table)) return 1;
  return 0;
}

export function getDifficultyLabel(bonus: number): string {
  switch (bonus) {
    case 2: return '🔥 TABLE DIFFICILE ! +2 bonus';
    case 1: return '💪 TABLE INTERMÉDIAIRE ! +1 bonus';
    default: return '';
  }
}
