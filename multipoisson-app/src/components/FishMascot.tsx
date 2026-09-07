import React, { useEffect } from 'react';
import { View, StyleSheet } from 'react-native';
import Animated, {
  useSharedValue, useAnimatedStyle,
  withRepeat, withSequence, withTiming, withSpring, Easing,
} from 'react-native-reanimated';
import Svg, {
  Ellipse, Polygon, Circle, Path, G, Defs,
  LinearGradient, Stop, Rect, Line,
} from 'react-native-svg';

interface FishMascotProps {
  level?: number;
  size?: number;
  animate?: boolean;
  onDragonAppear?: () => void;
}

/* ══════════════════════════════════════════════════════
   NIVEAUX 1–5  —  Poissons de base
═══════════════════════════════════════════════════════ */

const Fish1 = ({ size }: { size: number }) => (
  <Svg width={size} height={size * 0.67} viewBox="0 0 150 100">
    <Polygon points="28,50 6,22 6,78" fill="#9E9E9E" />
    <Ellipse cx={80} cy={50} rx={52} ry={33} fill="#BDBDBD" />
    <Polygon points="65,17 85,17 72,30" fill="#9E9E9E" />
    <Circle cx={112} cy={42} r={9} fill="white" />
    <Circle cx={114} cy={42} r={5} fill="#333" />
    <Circle cx={116} cy={40} r={2} fill="white" />
  </Svg>
);

const Fish2 = ({ size }: { size: number }) => (
  <Svg width={size} height={size * 0.67} viewBox="0 0 150 100">
    <Defs>
      <LinearGradient id="g2" x1="0" y1="0" x2="1" y2="0">
        <Stop offset="0" stopColor="#FF7043" /><Stop offset="1" stopColor="#FF9800" />
      </LinearGradient>
    </Defs>
    <Polygon points="26,50 4,18 4,82" fill="#FF7043" />
    <Ellipse cx={80} cy={50} rx={54} ry={35} fill="url(#g2)" />
    <Ellipse cx={72} cy={50} rx={6} ry={33} fill="white" fillOpacity={0.6} />
    <Ellipse cx={90} cy={50} rx={5} ry={28} fill="white" fillOpacity={0.5} />
    <Polygon points="60,15 88,15 74,26" fill="#FF5722" />
    <Polygon points="68,50 55,38 55,62" fill="#FFB74D" />
    <Circle cx={113} cy={41} r={10} fill="white" />
    <Circle cx={115} cy={41} r={6} fill="#1A237E" />
    <Circle cx={117} cy={39} r={2} fill="white" />
  </Svg>
);

const Fish3 = ({ size }: { size: number }) => (
  <Svg width={size} height={size * 0.67} viewBox="0 0 150 100">
    <Defs>
      <LinearGradient id="g3" x1="0" y1="0" x2="1" y2="1">
        <Stop offset="0" stopColor="#CDDC39" /><Stop offset="1" stopColor="#4CAF50" />
      </LinearGradient>
    </Defs>
    <Polygon points="24,50 2,14 2,86" fill="#8BC34A" />
    <Ellipse cx={80} cy={50} rx={56} ry={37} fill="url(#g3)" />
    <Circle cx={70} cy={38} r={7} fill="#FFEB3B" fillOpacity={0.8} />
    <Circle cx={88} cy={60} r={5} fill="#FFEB3B" fillOpacity={0.8} />
    <Circle cx={100} cy={40} r={4} fill="#FFEB3B" fillOpacity={0.8} />
    <Polygon points="58,12 92,12 75,24" fill="#388E3C" />
    <Polygon points="66,52 50,36 50,68" fill="#AED581" />
    <Circle cx={114} cy={40} r={11} fill="white" />
    <Circle cx={116} cy={40} r={7} fill="#2E7D32" />
    <Circle cx={118} cy={38} r={2.5} fill="white" />
  </Svg>
);

const Fish4 = ({ size }: { size: number }) => (
  <Svg width={size} height={size * 0.67} viewBox="0 0 150 100">
    <Defs>
      <LinearGradient id="g4" x1="0" y1="0" x2="1" y2="1">
        <Stop offset="0" stopColor="#7C4DFF" />
        <Stop offset="0.5" stopColor="#1CB0F6" />
        <Stop offset="1" stopColor="#CE82FF" />
      </LinearGradient>
    </Defs>
    <Polygon points="22,50 0,12 0,88" fill="#5E35B1" />
    <Ellipse cx={80} cy={50} rx={58} ry={39} fill="url(#g4)" />
    <Ellipse cx={74} cy={50} rx={8} ry={37} fill="white" fillOpacity={0.2} />
    <Ellipse cx={92} cy={50} rx={5} ry={35} fill="white" fillOpacity={0.2} />
    <Polygon points="55,14 68,14 61,24" fill="#9C27B0" />
    <Polygon points="68,10 80,10 73,22" fill="#7B1FA2" />
    <Polygon points="80,12 92,12 86,23" fill="#9C27B0" />
    <Polygon points="65,50 45,32 45,68" fill="#9575CD" />
    <Circle cx={115} cy={39} r={12} fill="white" />
    <Circle cx={117} cy={39} r={8} fill="#4A148C" />
    <Circle cx={120} cy={37} r={3} fill="white" />
  </Svg>
);

const Fish5 = ({ size }: { size: number }) => (
  <Svg width={size} height={size * 0.67} viewBox="0 0 150 100">
    <Defs>
      <LinearGradient id="g5" x1="0" y1="0" x2="1" y2="1">
        <Stop offset="0" stopColor="#FFD700" />
        <Stop offset="0.5" stopColor="#FFA500" />
        <Stop offset="1" stopColor="#DA70D6" />
      </LinearGradient>
    </Defs>
    <Polygon points="20,50 0,10 0,90" fill="#FFA500" />
    <Ellipse cx={80} cy={50} rx={60} ry={41} fill="url(#g5)" />
    <Ellipse cx={75} cy={42} rx={12} ry={8} fill="white" fillOpacity={0.35} />
    <Polygon points="68,18 72,8 77,18" fill="#FFD700" />
    <Polygon points="76,15 81,5 86,15" fill="#FFD700" />
    <Polygon points="84,18 89,8 93,18" fill="#FFD700" />
    <Rect x={66} y={18} width={30} height={7} rx={2} fill="#FFA500" />
    <Path d="M 15 30 L 18 25 L 21 30 L 18 35 Z" fill="#FFD700" />
    <Path d="M 140 30 L 143 25 L 146 30 L 143 35 Z" fill="#FFD700" />
    <Polygon points="64,50 40,28 40,72" fill="#FFB347" />
    <Circle cx={115} cy={38} r={13} fill="white" />
    <Circle cx={117} cy={38} r={9} fill="#8B4513" />
    <Circle cx={120} cy={36} r={3.5} fill="white" />
  </Svg>
);

/* ══════════════════════════════════════════════════════
   NIVEAU 6  —  Dragon Bleu (100 % / 10 questions)
═══════════════════════════════════════════════════════ */

const Dragon6 = ({ size }: { size: number }) => (
  <Svg width={size} height={size} viewBox="0 0 180 180">
    <Defs>
      <LinearGradient id="gd" x1="0" y1="0" x2="1" y2="1">
        <Stop offset="0" stopColor="#0D47A1" />
        <Stop offset="0.5" stopColor="#1565C0" />
        <Stop offset="1" stopColor="#42A5F5" />
      </LinearGradient>
      <LinearGradient id="gfire" x1="0" y1="1" x2="0" y2="0">
        <Stop offset="0" stopColor="#FF6D00" />
        <Stop offset="0.5" stopColor="#FFD600" />
        <Stop offset="1" stopColor="#FF1744" />
      </LinearGradient>
    </Defs>
    <Polygon points="50,90 5,30 55,70" fill="#0D47A1" fillOpacity={0.8} />
    <Polygon points="130,90 175,30 125,70" fill="#0D47A1" fillOpacity={0.8} />
    <Path d="M 55 100 Q 20 120 10 150 Q 30 130 55 115 Z" fill="#0D47A1" />
    <Ellipse cx={90} cy={95} rx={50} ry={62} fill="url(#gd)" />
    <Ellipse cx={90} cy={105} rx={30} ry={40} fill="#42A5F5" fillOpacity={0.5} />
    <Ellipse cx={90} cy={45} rx={22} ry={25} fill="#1565C0" />
    <Ellipse cx={90} cy={28} rx={28} ry={24} fill="url(#gd)" />
    <Polygon points="74,10 68,0 78,12" fill="#42A5F5" />
    <Polygon points="106,10 112,0 102,12" fill="#42A5F5" />
    <Circle cx={78} cy={24} r={8} fill="#FFD600" />
    <Circle cx={78} cy={24} r={5} fill="#0D47A1" />
    <Circle cx={80} cy={22} r={2} fill="white" />
    <Circle cx={102} cy={24} r={8} fill="#FFD600" />
    <Circle cx={102} cy={24} r={5} fill="#0D47A1" />
    <Circle cx={104} cy={22} r={2} fill="white" />
    <Path d="M 76 46 Q 50 65 30 80 Q 45 62 55 50 Z" fill="url(#gfire)" />
    <Path d="M 145 40 L 149 32 L 153 40 L 149 48 Z" fill="#FFD600" />
    <Path d="M 30 40 L 34 32 L 38 40 L 34 48 Z" fill="#FFD600" />
  </Svg>
);

/* ══════════════════════════════════════════════════════
   NIVEAU 7  —  Narval (100 % / 20 questions)
═══════════════════════════════════════════════════════ */

const Narval7 = ({ size }: { size: number }) => (
  <Svg width={size} height={size * 0.65} viewBox="0 0 200 130">
    <Defs>
      <LinearGradient id="gnarv" x1="0" y1="0" x2="1" y2="1">
        <Stop offset="0" stopColor="#E3F2FD" /><Stop offset="1" stopColor="#90CAF9" />
      </LinearGradient>
    </Defs>
    {/* Queue en V horizontal */}
    <Path d="M 22 65 L 4 42 L 18 65 L 4 88 Z" fill="#64B5F6" />
    {/* Corps */}
    <Ellipse cx={100} cy={65} rx={80} ry={36} fill="url(#gnarv)" />
    {/* Taches */}
    <Circle cx={70} cy={55} r={5} fill="#90A4AE" fillOpacity={0.5} />
    <Circle cx={90} cy={72} r={4} fill="#90A4AE" fillOpacity={0.4} />
    <Circle cx={110} cy={55} r={3} fill="#90A4AE" fillOpacity={0.4} />
    <Circle cx={55} cy={68} r={3} fill="#90A4AE" fillOpacity={0.35} />
    {/* Corne/tusk avec spirale */}
    <Path d="M 178 65 L 205 58 L 208 65 L 205 72 Z" fill="#B0BEC5" />
    <Line x1={185} y1={62} x2={187} y2={68} stroke="#78909C" strokeWidth={1.5} />
    <Line x1={192} y1={60} x2={194} y2={66} stroke="#78909C" strokeWidth={1.5} />
    <Line x1={199} y1={59} x2={201} y2={65} stroke="#78909C" strokeWidth={1.5} />
    {/* Nageoire dorsale */}
    <Path d="M 95 29 Q 102 12 115 29" fill="#90CAF9" />
    {/* Nageoire pectorale */}
    <Polygon points="85,72 68,90 88,80" fill="#90CAF9" />
    {/* Œil */}
    <Circle cx={158} cy={55} r={9} fill="white" />
    <Circle cx={160} cy={55} r={5} fill="#1565C0" />
    <Circle cx={162} cy={53} r={2} fill="white" />
    {/* Sourire */}
    <Path d="M 167 67 Q 172 73 178 67" stroke="#90CAF9" strokeWidth={2.5} fill="none" />
    {/* Étoile magique */}
    <Path d="M 148 22 L 151 16 L 154 22 L 151 28 Z" fill="#FFD700" />
  </Svg>
);

/* ══════════════════════════════════════════════════════
   NIVEAU 8  —  Béluga (100 % / 30 questions)
═══════════════════════════════════════════════════════ */

const Beluga8 = ({ size }: { size: number }) => (
  <Svg width={size} height={size * 0.75} viewBox="0 0 180 135">
    <Defs>
      <LinearGradient id="gbel" x1="0" y1="0" x2="1" y2="1">
        <Stop offset="0" stopColor="#FFFFFF" /><Stop offset="1" stopColor="#E3F2FD" />
      </LinearGradient>
    </Defs>
    {/* Queue */}
    <Path d="M 20 68 L 4 48 L 18 68 L 4 88 Z" fill="#E0E0E0" />
    {/* Corps très rond */}
    <Ellipse cx={98} cy={68} rx={76} ry={50} fill="url(#gbel)" />
    {/* Melon (bosse de la tête, caractéristique du béluga) */}
    <Ellipse cx={155} cy={52} rx={28} ry={24} fill="white" />
    {/* Ombre douce */}
    <Ellipse cx={90} cy={100} rx={55} ry={12} fill="#BBDEFB" fillOpacity={0.4} />
    {/* Nageoires pectorales petites */}
    <Polygon points="80,82 60,100 85,90" fill="#E0E0E0" />
    {/* Œil */}
    <Circle cx={150} cy={54} r={10} fill="#E3F2FD" />
    <Circle cx={152} cy={54} r={6} fill="#1565C0" />
    <Circle cx={155} cy={51} r={2.5} fill="white" />
    {/* Grand sourire */}
    <Path d="M 158 68 Q 165 80 175 70" stroke="#90CAF9" strokeWidth={3} fill="none" strokeLinecap="round" />
    {/* Petits cœurs autour */}
    <Path d="M 30 35 Q 33 30 36 35 Q 39 30 42 35 L 36 42 Z" fill="#F48FB1" fillOpacity={0.8} />
    <Path d="M 50 22 Q 52 18 55 22 Q 58 18 60 22 L 55 28 Z" fill="#F48FB1" fillOpacity={0.7} />
  </Svg>
);

/* ══════════════════════════════════════════════════════
   NIVEAU 9  —  Requin Marteau (100 % / 40 questions)
═══════════════════════════════════════════════════════ */

const RequinMarteau9 = ({ size }: { size: number }) => (
  <Svg width={size} height={size * 0.72} viewBox="0 0 190 137">
    <Defs>
      <LinearGradient id="gham" x1="0" y1="0" x2="0" y2="1">
        <Stop offset="0" stopColor="#546E7A" /><Stop offset="1" stopColor="#ECEFF1" />
      </LinearGradient>
    </Defs>
    {/* Queue bifurquée */}
    <Path d="M 25 68 L 4 44 L 22 68 L 4 94 Z" fill="#455A64" />
    {/* Corps */}
    <Ellipse cx={95} cy={68} rx={75} ry={34} fill="url(#gham)" />
    {/* Ventre blanc */}
    <Ellipse cx={95} cy={82} rx={60} ry={18} fill="#ECEFF1" />
    {/* TÊTE EN MARTEAU (extension latérale) */}
    <Rect x={148} y={44} width={40} height={22} rx={11} fill="#546E7A" />
    {/* Nageoire dorsale haute */}
    <Path d="M 80 34 L 90 4 L 108 34" fill="#455A64" />
    {/* Nageoire pectorale */}
    <Polygon points="78,80 52,98 76,88" fill="#546E7A" />
    {/* Œils sur les extrémités du marteau */}
    <Circle cx={153} cy={55} r={8} fill="white" />
    <Circle cx={155} cy={55} r={5} fill="#1A1A1A" />
    <Circle cx={157} cy={53} r={1.5} fill="white" />
    <Circle cx={183} cy={55} r={8} fill="white" />
    <Circle cx={185} cy={55} r={5} fill="#1A1A1A" />
    <Circle cx={187} cy={53} r={1.5} fill="white" />
    {/* Narines */}
    <Ellipse cx={162} cy={62} rx={3} ry={2} fill="#37474F" />
    <Ellipse cx={174} cy={62} rx={3} ry={2} fill="#37474F" />
    {/* Lignes de branchies */}
    <Path d="M 128 56 Q 130 68 128 80" stroke="#455A64" strokeWidth={1.5} fill="none" />
    <Path d="M 136 54 Q 138 68 136 82" stroke="#455A64" strokeWidth={1.5} fill="none" />
  </Svg>
);

/* ══════════════════════════════════════════════════════
   NIVEAU 10  —  Orque (100 % / 50 questions)
═══════════════════════════════════════════════════════ */

const Orque10 = ({ size }: { size: number }) => (
  <Svg width={size} height={size * 0.85} viewBox="0 0 190 162">
    {/* Corps noir */}
    <Path d="M 20 81 L 4 56 L 18 81 L 4 108 Z" fill="#1A1A1A" />
    <Ellipse cx={95} cy={82} rx={80} ry={50} fill="#1A1A1A" />
    {/* Ventre blanc caractéristique */}
    <Ellipse cx={95} cy={100} rx={55} ry={28} fill="white" />
    {/* Patch blanc derrière l'œil (marquage distinctif orque) */}
    <Ellipse cx={148} cy={60} rx={20} ry={14} fill="white" />
    {/* Nageoire dorsale TRÈS HAUTE */}
    <Path d="M 75 32 L 88 2 L 100 32" fill="#1A1A1A" />
    {/* Nageoires pectorales larges */}
    <Polygon points="70,95 40,118 68,105" fill="#1A1A1A" />
    <Polygon points="120,95 148,118 122,105" fill="#1A1A1A" />
    {/* Œil */}
    <Circle cx={152} cy={65} r={10} fill="white" />
    <Circle cx={154} cy={65} r={6} fill="#1A1A1A" />
    <Circle cx={156} cy={63} r={2} fill="white" />
    {/* Sourire */}
    <Path d="M 160 78 Q 167 86 175 78" stroke="#1A1A1A" strokeWidth={3} fill="none" strokeLinecap="round" />
    {/* Étoiles */}
    <Path d="M 35 40 L 38 34 L 41 40 L 38 46 Z" fill="#42A5F5" />
    <Path d="M 155 25 L 158 19 L 161 25 L 158 31 Z" fill="#42A5F5" />
  </Svg>
);

/* ══════════════════════════════════════════════════════
   NIVEAU 11  —  Baleine à Bosse (100 % / 60 questions)
═══════════════════════════════════════════════════════ */

const BaleineBosse11 = ({ size }: { size: number }) => (
  <Svg width={size} height={size * 0.75} viewBox="0 0 220 165">
    <Defs>
      <LinearGradient id="gbal" x1="0" y1="0" x2="0" y2="1">
        <Stop offset="0" stopColor="#1565C0" /><Stop offset="1" stopColor="#42A5F5" />
      </LinearGradient>
    </Defs>
    {/* Queue avec bosse caractéristique */}
    <Path d="M 22 82 L 4 55 L 20 82 L 4 110 Z" fill="#0D47A1" />
    {/* Corps très grand */}
    <Ellipse cx={110} cy={85} rx={95} ry={55} fill="url(#gbal)" />
    {/* Ventre clair */}
    <Ellipse cx={110} cy={108} rx={70} ry={28} fill="#90CAF9" fillOpacity={0.7} />
    {/* Tubercules sur le museau */}
    <Circle cx={195} cy={78} r={4} fill="#0D47A1" />
    <Circle cx={200} cy={84} r={3} fill="#0D47A1" />
    <Circle cx={195} cy={90} r={4} fill="#0D47A1" />
    {/* Nageoires PECTORALES TRÈS LONGUES (caractéristique !) */}
    <Path d="M 90 110 Q 60 148 28 140 Q 55 128 75 118 Z" fill="#1565C0" />
    <Path d="M 130 110 Q 158 145 185 135 Q 162 124 145 115 Z" fill="#1565C0" />
    {/* Petite nageoire dorsale bosse */}
    <Path d="M 75 30 Q 82 18 92 30" fill="#0D47A1" />
    {/* Œil */}
    <Circle cx={185} cy={74} r={11} fill="#E3F2FD" />
    <Circle cx={187} cy={74} r={7} fill="#0D47A1" />
    <Circle cx={190} cy={71} r={2.5} fill="white" />
    {/* Bulles */}
    <Circle cx={50} cy={38} r={6} fill="none" stroke="#90CAF9" strokeWidth={2} />
    <Circle cx={62} cy={25} r={4} fill="none" stroke="#90CAF9" strokeWidth={2} />
    <Circle cx={40} cy={22} r={3} fill="none" stroke="#90CAF9" strokeWidth={1.5} />
  </Svg>
);

/* ══════════════════════════════════════════════════════
   NIVEAU 12  —  Pieuvre (100 % / 70 questions)
═══════════════════════════════════════════════════════ */

const Pieuvre12 = ({ size }: { size: number }) => (
  <Svg width={size} height={size} viewBox="0 0 170 170">
    <Defs>
      <LinearGradient id="gpieuvre" x1="0" y1="0" x2="1" y2="1">
        <Stop offset="0" stopColor="#7B1FA2" /><Stop offset="1" stopColor="#CE82FF" />
      </LinearGradient>
    </Defs>
    {/* 8 tentacules qui se courbent */}
    <Path d="M 60 110 Q 30 140 20 160" stroke="#7B1FA2" strokeWidth={10} fill="none" strokeLinecap="round" />
    <Path d="M 72 118 Q 48 152 42 170" stroke="#8E24AA" strokeWidth={9} fill="none" strokeLinecap="round" />
    <Path d="M 85 122 Q 80 158 78 170" stroke="#7B1FA2" strokeWidth={9} fill="none" strokeLinecap="round" />
    <Path d="M 98 122 Q 100 158 102 170" stroke="#8E24AA" strokeWidth={9} fill="none" strokeLinecap="round" />
    <Path d="M 110 118 Q 125 152 130 170" stroke="#7B1FA2" strokeWidth={9} fill="none" strokeLinecap="round" />
    <Path d="M 118 110 Q 142 140 152 160" stroke="#8E24AA" strokeWidth={9} fill="none" strokeLinecap="round" />
    <Path d="M 52 105 Q 22 128 10 148" stroke="#9C27B0" strokeWidth={8} fill="none" strokeLinecap="round" />
    <Path d="M 126 105 Q 155 128 165 148" stroke="#9C27B0" strokeWidth={8} fill="none" strokeLinecap="round" />
    {/* Manteau/corps */}
    <Ellipse cx={85} cy={72} rx={55} ry={65} fill="url(#gpieuvre)" />
    {/* Tête arrondie */}
    <Ellipse cx={85} cy={52} rx={42} ry={35} fill="#9C27B0" />
    {/* Reflet */}
    <Ellipse cx={74} cy={40} rx={16} ry={10} fill="#CE82FF" fillOpacity={0.4} />
    {/* Grands yeux expressifs */}
    <Circle cx={68} cy={72} r={16} fill="white" />
    <Circle cx={71} cy={72} r={10} fill="#FFD700" />
    <Circle cx={71} cy={72} r={6} fill="#1A1A1A" />
    <Circle cx={74} cy={69} r={2.5} fill="white" />
    <Circle cx={103} cy={72} r={16} fill="white" />
    <Circle cx={106} cy={72} r={10} fill="#FFD700" />
    <Circle cx={106} cy={72} r={6} fill="#1A1A1A" />
    <Circle cx={109} cy={69} r={2.5} fill="white" />
    {/* Ventouses sur les tentacules */}
    <Circle cx={32} cy={140} r={4} fill="#CE82FF" fillOpacity={0.7} />
    <Circle cx={148} cy={140} r={4} fill="#CE82FF" fillOpacity={0.7} />
    <Circle cx={80} cy={162} r={3} fill="#CE82FF" fillOpacity={0.7} />
    {/* Étoiles */}
    <Path d="M 30 30 L 33 24 L 36 30 L 33 36 Z" fill="#FFD700" />
    <Path d="M 140 25 L 143 19 L 146 25 L 143 31 Z" fill="#FFD700" />
  </Svg>
);

/* ══════════════════════════════════════════════════════
   NIVEAU 13  —  Espadon (100 % / 80 questions)
═══════════════════════════════════════════════════════ */

const Espadon13 = ({ size }: { size: number }) => (
  <Svg width={size} height={size * 0.65} viewBox="0 0 220 143">
    <Defs>
      <LinearGradient id="gesp" x1="0" y1="0" x2="0" y2="1">
        <Stop offset="0" stopColor="#1565C0" /><Stop offset="1" stopColor="#E8EAF6" />
      </LinearGradient>
    </Defs>
    {/* Queue bifurquée */}
    <Path d="M 22 72 L 4 50 L 20 72 L 4 94 Z" fill="#1565C0" />
    {/* Corps élancé */}
    <Ellipse cx={100} cy={72} rx={82} ry={30} fill="url(#gesp)" />
    {/* Ventre clair */}
    <Ellipse cx={105} cy={85} rx={65} ry={16} fill="#E8EAF6" />
    {/* Épée/Bill TRÈS LONG */}
    <Path d="M 180 72 L 220 66 L 222 72 L 220 78 Z" fill="#37474F" />
    {/* Ligne latérale caractéristique */}
    <Path d="M 30 68 Q 100 58 182 70" stroke="#90A4AE" strokeWidth={1.5} fill="none" />
    {/* Nageoire dorsale haute et longue */}
    <Path d="M 60 42 L 75 6 L 145 42" fill="#0D47A1" />
    {/* Nageoire anale */}
    <Polygon points="65,82 50,105 75,88" fill="#1565C0" />
    {/* Nageoire pectorale */}
    <Polygon points="90,78 65,100 88,86" fill="#1A237E" />
    {/* Œil */}
    <Circle cx={168} cy={65} r={10} fill="white" />
    <Circle cx={170} cy={65} r={6} fill="#1A237E" />
    <Circle cx={173} cy={62} r={2} fill="white" />
    {/* Reflets métalliques */}
    <Path d="M 40 52 L 48 48 L 50 55" stroke="#90CAF9" strokeWidth={2} fill="none" />
    <Path d="M 70 46 L 78 42 L 80 49" stroke="#90CAF9" strokeWidth={2} fill="none" />
  </Svg>
);

/* ══════════════════════════════════════════════════════
   NIVEAU 14  —  Tortue Luth (100 % / 90 questions)
═══════════════════════════════════════════════════════ */

const TortueL14 = ({ size }: { size: number }) => (
  <Svg width={size} height={size * 0.9} viewBox="0 0 170 153">
    <Defs>
      <LinearGradient id="gtort" x1="0" y1="0" x2="1" y2="1">
        <Stop offset="0" stopColor="#1B5E20" /><Stop offset="1" stopColor="#4CAF50" />
      </LinearGradient>
    </Defs>
    {/* Carapace (ovale avec crêtes caractéristiques luth) */}
    <Ellipse cx={85} cy={85} rx={65} ry={60} fill="url(#gtort)" />
    {/* 5 crêtes longitudinales (caractéristique tortue luth) */}
    <Path d="M 85 25 Q 85 85 85 145" stroke="#2E7D32" strokeWidth={4} fill="none" />
    <Path d="M 66 30 Q 62 85 66 140" stroke="#2E7D32" strokeWidth={3} fill="none" />
    <Path d="M 104 30 Q 108 85 104 140" stroke="#2E7D32" strokeWidth={3} fill="none" />
    <Path d="M 48 40 Q 42 85 48 130" stroke="#2E7D32" strokeWidth={2.5} fill="none" />
    <Path d="M 122 40 Q 128 85 122 130" stroke="#2E7D32" strokeWidth={2.5} fill="none" />
    {/* Taches blanches (motif luth) */}
    <Circle cx={70} cy={65} r={6} fill="#A5D6A7" fillOpacity={0.6} />
    <Circle cx={100} cy={65} r={5} fill="#A5D6A7" fillOpacity={0.6} />
    <Circle cx={85} cy={82} r={7} fill="#A5D6A7" fillOpacity={0.5} />
    <Circle cx={68} cy={95} r={4} fill="#A5D6A7" fillOpacity={0.5} />
    <Circle cx={102} cy={95} r={4} fill="#A5D6A7" fillOpacity={0.5} />
    {/* Grandes nageoires avant */}
    <Path d="M 28 75 Q 5 58 8 38 Q 20 55 35 68 Z" fill="#2E7D32" />
    <Path d="M 142 75 Q 165 58 162 38 Q 150 55 135 68 Z" fill="#2E7D32" />
    {/* Nageoires arrière */}
    <Polygon points="45,120 25,145 48,132" fill="#388E3C" />
    <Polygon points="125,120 145,145 122,132" fill="#388E3C" />
    {/* Tête */}
    <Ellipse cx={85} cy={28} rx={22} ry={20} fill="#388E3C" />
    {/* Bec légèrement crochu */}
    <Path d="M 75 34 Q 85 40 96 34" stroke="#1B5E20" strokeWidth={2.5} fill="none" />
    {/* Œils */}
    <Circle cx={74} cy={23} r={8} fill="#E8F5E9" />
    <Circle cx={76} cy={23} r={5} fill="#1B5E20" />
    <Circle cx={78} cy={21} r={2} fill="white" />
    <Circle cx={96} cy={23} r={8} fill="#E8F5E9" />
    <Circle cx={98} cy={23} r={5} fill="#1B5E20" />
    <Circle cx={100} cy={21} r={2} fill="white" />
  </Svg>
);

/* ══════════════════════════════════════════════════════
   NIVEAU 15  —  Grand Requin Blanc (100 % / 100 questions)
═══════════════════════════════════════════════════════ */

const GRB15 = ({ size }: { size: number }) => (
  <Svg width={size} height={size * 0.7} viewBox="0 0 210 147">
    <Defs>
      <LinearGradient id="ggrb" x1="0" y1="0" x2="0" y2="1">
        <Stop offset="0" stopColor="#546E7A" /><Stop offset="0.6" stopColor="#78909C" />
        <Stop offset="1" stopColor="#F5F5F5" />
      </LinearGradient>
    </Defs>
    {/* Queue en croissant */}
    <Path d="M 25 73 L 4 45 L 22 73 L 4 102 Z" fill="#455A64" />
    {/* Corps en torpille */}
    <Ellipse cx={108} cy={73} rx={88} ry={42} fill="url(#ggrb)" />
    {/* Ventre blanc net */}
    <Path d="M 30 90 Q 108 112 190 90 Q 108 105 30 90 Z" fill="#F5F5F5" />
    {/* Nageoire dorsale imposante */}
    <Path d="M 85 31 L 100 4 L 120 31" fill="#455A64" />
    {/* Nageoires pectorales grandes */}
    <Polygon points="85,88 52,118 82,98" fill="#546E7A" />
    <Polygon points="132,88 165,115 136,98" fill="#546E7A" />
    {/* Branchies */}
    <Path d="M 150 58 Q 152 73 150 88" stroke="#37474F" strokeWidth={2} fill="none" />
    <Path d="M 158 56 Q 160 73 158 90" stroke="#37474F" strokeWidth={2} fill="none" />
    <Path d="M 166 58 Q 168 73 166 88" stroke="#37474F" strokeWidth={2} fill="none" />
    {/* Gueule avec dents */}
    <Path d="M 182 82 Q 200 73 205 73 Q 200 73 182 64" fill="#546E7A" />
    <Path d="M 185 70 L 188 78 L 192 70 L 196 78 L 200 70" stroke="white" strokeWidth={2} fill="none" />
    {/* Œil froid */}
    <Circle cx={178} cy={62} r={12} fill="#1A1A1A" />
    <Circle cx={181} cy={59} r={3} fill="#37474F" />
    {/* Ligne latérale */}
    <Path d="M 35 70 Q 108 62 178 68" stroke="#607D8B" strokeWidth={1} fill="none" />
  </Svg>
);

/* ══════════════════════════════════════════════════════
   NIVEAU 16  —  Mégalodon (100 % / 150 questions)
═══════════════════════════════════════════════════════ */

const Megalodont16 = ({ size }: { size: number }) => (
  <Svg width={size} height={size * 0.8} viewBox="0 0 220 176">
    <Defs>
      <LinearGradient id="gmeg" x1="0" y1="0" x2="0" y2="1">
        <Stop offset="0" stopColor="#212121" /><Stop offset="1" stopColor="#37474F" />
      </LinearGradient>
    </Defs>
    {/* Queue massive */}
    <Path d="M 28 88 L 4 52 L 24 88 L 4 124 Z" fill="#1A1A1A" />
    {/* Corps MASSIF */}
    <Ellipse cx={112} cy={88} rx={95} ry={58} fill="url(#gmeg)" />
    {/* Ventre gris clair */}
    <Path d="M 30 108 Q 112 138 200 108 Q 112 128 30 108 Z" fill="#455A64" />
    {/* Nageoire dorsale massive */}
    <Path d="M 78 30 L 96 2 L 128 30" fill="#1A1A1A" />
    {/* Nageoires pectorales larges */}
    <Polygon points="88,105 48,142 86,115" fill="#212121" />
    <Polygon points="138,105 178,140 140,115" fill="#212121" />
    {/* ÉNORME GUEULE OUVERTE */}
    <Path d="M 188 105 Q 210 88 212 88 Q 210 88 188 72 Q 205 88 188 105 Z" fill="#1A1A1A" />
    {/* Mâchoire inférieure */}
    <Path d="M 188 88 Q 210 100 215 88" fill="#212121" />
    {/* Dents GÉANTES */}
    <Polygon points="192,88 196,98 200,88" fill="white" />
    <Polygon points="200,88 204,100 208,88" fill="white" />
    <Polygon points="192,88 196,78 200,88" fill="white" />
    <Polygon points="200,88 204,76 208,88" fill="white" />
    {/* Yeux avec lueur */}
    <Circle cx={175} cy={72} r={14} fill="#E65100" />
    <Circle cx={175} cy={72} r={8} fill="#1A1A1A" />
    <Circle cx={178} cy={69} r={3} fill="#FF6D00" />
    {/* Cicatrices */}
    <Path d="M 90 55 L 95 68" stroke="#455A64" strokeWidth={3} />
    <Path d="M 108 50 L 112 65" stroke="#455A64" strokeWidth={2.5} />
    {/* Branchies */}
    <Path d="M 148 62 Q 150 88 148 114" stroke="#37474F" strokeWidth={3} fill="none" />
    <Path d="M 158 60 Q 160 88 158 116" stroke="#37474F" strokeWidth={3} fill="none" />
    {/* Particules */}
    <Path d="M 40 35 L 43 29 L 46 35 L 43 41 Z" fill="#FF6D00" fillOpacity={0.6} />
    <Path d="M 155 20 L 158 14 L 161 20 L 158 26 Z" fill="#FF6D00" fillOpacity={0.5} />
  </Svg>
);

/* ══════════════════════════════════════════════════════
   NIVEAU 17  —  Capibara (100 % / 200 questions)
═══════════════════════════════════════════════════════ */

const Capibara17 = ({ size }: { size: number }) => (
  <Svg width={size} height={size * 0.8} viewBox="0 0 190 152">
    <Defs>
      <LinearGradient id="gcap" x1="0" y1="0" x2="1" y2="1">
        <Stop offset="0" stopColor="#8D6E63" /><Stop offset="1" stopColor="#A1887F" />
      </LinearGradient>
    </Defs>
    {/* Corps barrel-shaped (rectangulaire arrondi) */}
    <Rect x={35} y={55} width={130} height={78} rx={38} fill="url(#gcap)" />
    {/* Ventre plus clair */}
    <Ellipse cx={100} cy={115} rx={52} ry={20} fill="#BCAAA4" />
    {/* Pattes courtes */}
    <Rect x={52} y={120} width={20} height={28} rx={8} fill="#795548" />
    <Rect x={80} y={122} width={20} height={26} rx={8} fill="#795548" />
    <Rect x={108} y={122} width={20} height={26} rx={8} fill="#795548" />
    <Rect x={136} y={120} width={20} height={28} rx={8} fill="#795548" />
    {/* Tête grande et carrée */}
    <Rect x={118} y={28} width={62} height={56} rx={22} fill="#8D6E63" />
    {/* Grandes narines caractéristiques */}
    <Ellipse cx={161} cy={68} rx={7} ry={5} fill="#5D4037" />
    <Ellipse cx={148} cy={68} rx={6} ry={5} fill="#5D4037" />
    {/* Yeux petits */}
    <Circle cx={145} cy={44} r={8} fill="#4E342E" />
    <Circle cx={147} cy={42} r={3} fill="#A5D6A7" />
    <Circle cx={165} cy={44} r={8} fill="#4E342E" />
    <Circle cx={167} cy={42} r={3} fill="#A5D6A7" />
    {/* Oreilles rondes */}
    <Circle cx={142} cy={30} r={10} fill="#795548" />
    <Circle cx={170} cy={30} r={10} fill="#795548" />
    {/* Sourire de capibara :) */}
    <Path d="M 148 77 Q 156 84 164 77" stroke="#5D4037" strokeWidth={2.5} fill="none" strokeLinecap="round" />
    {/* Gouttes d'eau autour (il nage !) */}
    <Path d="M 22 72 Q 24 66 28 72 Q 30 78 26 80 Q 22 78 22 72 Z" fill="#42A5F5" fillOpacity={0.7} />
    <Path d="M 18 50 Q 20 44 24 50 Q 26 56 22 58 Q 18 56 18 50 Z" fill="#42A5F5" fillOpacity={0.6} />
    <Path d="M 170 28 Q 172 22 176 28 Q 178 34 174 36 Q 170 34 170 28 Z" fill="#42A5F5" fillOpacity={0.6} />
    {/* Couronne de fleurs (icon capibara famous) */}
    <Circle cx={132} cy={20} r={6} fill="#FF80AB" />
    <Circle cx={148} cy={14} r={6} fill="#FFEB3B" />
    <Circle cx={164} cy={16} r={6} fill="#80CBC4" />
    <Circle cx={178} cy={22} r={5} fill="#FF80AB" />
  </Svg>
);

/* ══════════════════════════════════════════════════════
   NIVEAU 18  —  Léviathan (100 % / 300 questions)
═══════════════════════════════════════════════════════ */

const Leviathan18 = ({ size }: { size: number }) => (
  <Svg width={size} height={size * 1.1} viewBox="0 0 180 198">
    <Defs>
      <LinearGradient id="glev" x1="0" y1="0" x2="1" y2="1">
        <Stop offset="0" stopColor="#004D40" /><Stop offset="1" stopColor="#00695C" />
      </LinearGradient>
    </Defs>
    {/* Corps serpentin sinueux */}
    <Path d="M 90 10 Q 145 30 150 70 Q 155 110 110 120 Q 65 130 70 165 Q 75 185 100 195"
      stroke="url(#glev)" strokeWidth={42} fill="none" strokeLinecap="round" />
    {/* Écailles (lignes) */}
    <Path d="M 110 20 Q 140 32 145 58" stroke="#00897B" strokeWidth={2} fill="none" />
    <Path d="M 122 35 Q 148 50 148 75" stroke="#00897B" strokeWidth={2} fill="none" />
    <Path d="M 120 80 Q 100 105 95 118" stroke="#00897B" strokeWidth={2} fill="none" />
    <Path d="M 80 135 Q 75 158 80 178" stroke="#00897B" strokeWidth={2} fill="none" />
    {/* Points bioluminescents */}
    <Circle cx={130} cy={48} r={4} fill="#64FFDA" fillOpacity={0.9} />
    <Circle cx={148} cy={75} r={3} fill="#64FFDA" fillOpacity={0.8} />
    <Circle cx={112} cy={110} r={4} fill="#64FFDA" fillOpacity={0.9} />
    <Circle cx={78} cy={142} r={3} fill="#64FFDA" fillOpacity={0.8} />
    <Circle cx={85} cy={170} r={4} fill="#64FFDA" fillOpacity={0.9} />
    {/* Nageoires latérales */}
    <Path d="M 148 55 Q 168 42 172 55 Q 165 60 148 65 Z" fill="#004D40" />
    <Path d="M 140 100 Q 162 92 165 105 Q 158 108 140 112 Z" fill="#004D40" />
    {/* Tête */}
    <Circle cx={90} cy={22} r={32} fill="#00695C" />
    {/* Cornes */}
    <Path d="M 72 6 Q 66 -4 72 2" stroke="#00897B" strokeWidth={6} fill="none" strokeLinecap="round" />
    <Path d="M 108 6 Q 114 -4 108 2" stroke="#00897B" strokeWidth={6} fill="none" strokeLinecap="round" />
    {/* Yeux ROUGEOYANTS */}
    <Circle cx={74} cy={20} r={11} fill="#FF3D00" />
    <Circle cx={74} cy={20} r={6} fill="#1A1A1A" />
    <Circle cx={77} cy={17} r={2} fill="#FF6E40" />
    <Circle cx={106} cy={20} r={11} fill="#FF3D00" />
    <Circle cx={106} cy={20} r={6} fill="#1A1A1A" />
    <Circle cx={109} cy={17} r={2} fill="#FF6E40" />
    {/* Gueule */}
    <Path d="M 76 34 Q 90 42 104 34" stroke="#1A1A1A" strokeWidth={3} fill="none" />
    <Polygon points="82,36 86,43 90,36" fill="white" />
    <Polygon points="90,36 94,43 98,36" fill="white" />
    {/* Halo de lumière */}
    <Circle cx={90} cy={22} r={38} fill="none" stroke="#64FFDA" strokeWidth={2} strokeOpacity={0.4} />
  </Svg>
);

/* ══════════════════════════════════════════════════════
   NIVEAU 19  —  Poséidon (100 % / 500 questions)
═══════════════════════════════════════════════════════ */

const Poseidon19 = ({ size }: { size: number }) => (
  <Svg width={size} height={size * 1.3} viewBox="0 0 180 234">
    <Defs>
      <LinearGradient id="gpos" x1="0" y1="0" x2="0" y2="1">
        <Stop offset="0" stopColor="#1565C0" /><Stop offset="0.5" stopColor="#0288D1" />
        <Stop offset="1" stopColor="#006064" />
      </LinearGradient>
      <LinearGradient id="gpeau" x1="0" y1="0" x2="1" y2="0">
        <Stop offset="0" stopColor="#1A6FA0" /><Stop offset="1" stopColor="#0288D1" />
      </LinearGradient>
      <LinearGradient id="gor" x1="0" y1="0" x2="1" y2="1">
        <Stop offset="0" stopColor="#FFD700" /><Stop offset="1" stopColor="#FFA000" />
      </LinearGradient>
    </Defs>
    {/* Queue de sirène / serpent marin */}
    <Path d="M 65 140 Q 40 170 35 210 Q 55 195 70 175 Q 80 195 90 220 Q 100 195 110 175 Q 125 195 145 210 Q 140 170 115 140 Z"
      fill="url(#gpos)" />
    {/* Écailles queue */}
    <Path d="M 70 148 Q 90 145 110 148" stroke="#006064" strokeWidth={2} fill="none" />
    <Path d="M 62 162 Q 90 158 118 162" stroke="#006064" strokeWidth={2} fill="none" />
    <Path d="M 58 176 Q 90 172 122 176" stroke="#006064" strokeWidth={2} fill="none" />
    {/* Torse */}
    <Ellipse cx={90} cy={118} rx={38} ry={35} fill="url(#gpeau)" />
    {/* Bras gauche (tient le trident) */}
    <Path d="M 54 100 Q 35 115 30 135 Q 40 130 52 120 Z" fill="url(#gpeau)" />
    {/* Bras droit */}
    <Path d="M 126 100 Q 145 115 148 130 Q 138 125 128 118 Z" fill="url(#gpeau)" />
    {/* TRIDENT */}
    <Rect x={25} y={80} width={6} height={70} rx={3} fill="url(#gor)" />
    <Polygon points="28,80 22,62 28,68 34,62 28,80" fill="url(#gor)" />
    <Polygon points="20,72 14,55 20,62" fill="url(#gor)" />
    <Polygon points="36,72 42,55 36,62" fill="url(#gor)" />
    {/* Tête */}
    <Circle cx={90} cy={68} r={35} fill="url(#gpeau)" />
    {/* Barbe et cheveux (Poséidon) */}
    <Path d="M 58 78 Q 55 90 56 104 Q 62 98 66 88" fill="#0D47A1" />
    <Path d="M 122 78 Q 125 90 124 104 Q 118 98 114 88" fill="#0D47A1" />
    {/* Cheveux */}
    <Path d="M 58 48 Q 62 30 70 28" stroke="#0D47A1" strokeWidth={6} fill="none" strokeLinecap="round" />
    <Path d="M 90 35 Q 90 20 88 18" stroke="#0D47A1" strokeWidth={6} fill="none" strokeLinecap="round" />
    <Path d="M 122 48 Q 118 30 110 28" stroke="#0D47A1" strokeWidth={6} fill="none" strokeLinecap="round" />
    {/* COURONNE */}
    <Polygon points="62,44 66,34 72,44" fill="url(#gor)" />
    <Polygon points="72,42 76,30 82,42" fill="url(#gor)" />
    <Polygon points="82,40 88,26 94,40" fill="url(#gor)" />
    <Polygon points="94,40 100,28 106,40" fill="url(#gor)" />
    <Polygon points="106,42 112,32 118,42" fill="url(#gor)" />
    <Rect x={62} y={44} width={56} height={10} rx={3} fill="url(#gor)" />
    {/* Gems sur la couronne */}
    <Circle cx={74} cy={47} r={3} fill="#00BCD4" />
    <Circle cx={88} cy={46} r={4} fill="#FF1744" />
    <Circle cx={104} cy={46} r={4} fill="#00E5FF" />
    <Circle cx={116} cy={47} r={3} fill="#00BCD4" />
    {/* Yeux */}
    <Circle cx={78} cy={66} r={10} fill="#E3F2FD" />
    <Circle cx={80} cy={66} r={6} fill="#FFD700" />
    <Circle cx={80} cy={66} r={3} fill="#1A237E" />
    <Circle cx={82} cy={64} r={1.5} fill="white" />
    <Circle cx={102} cy={66} r={10} fill="#E3F2FD" />
    <Circle cx={104} cy={66} r={6} fill="#FFD700" />
    <Circle cx={104} cy={66} r={3} fill="#1A237E" />
    <Circle cx={106} cy={64} r={1.5} fill="white" />
    {/* Barbe */}
    <Path d="M 68 86 Q 78 95 90 96 Q 102 95 112 86" stroke="#0D47A1" strokeWidth={5} fill="none" strokeLinecap="round" />
    {/* Éclair/pouvoir autour */}
    <Path d="M 150 55 L 158 45 L 154 60 L 163 52 L 158 68" stroke="#FFD700" strokeWidth={3} fill="none" strokeLinejoin="round" />
    <Path d="M 12 90 L 8 80 L 12 95 L 5 88 L 10 103" stroke="#00E5FF" strokeWidth={3} fill="none" strokeLinejoin="round" />
    {/* Bulles */}
    <Circle cx={155} cy={130} r={5} fill="none" stroke="#90CAF9" strokeWidth={2} />
    <Circle cx={162} cy={118} r={3} fill="none" stroke="#90CAF9" strokeWidth={1.5} />
    <Circle cx={20} cy={140} r={4} fill="none" stroke="#90CAF9" strokeWidth={1.5} />
  </Svg>
);

/* ══════════════════════════════════════════════════════
   Composant principal
═══════════════════════════════════════════════════════ */

export default function FishMascot({
  level = 1, size = 150, animate = true, onDragonAppear,
}: FishMascotProps) {
  const translateY = useSharedValue(0);
  const rotate    = useSharedValue(0);
  const scale     = useSharedValue(1);
  const opacity   = useSharedValue(0);

  useEffect(() => {
    if (!animate) return;

    translateY.value = withRepeat(
      withSequence(
        withTiming(-8, { duration: 900, easing: Easing.inOut(Easing.sin) }),
        withTiming(8,  { duration: 900, easing: Easing.inOut(Easing.sin) }),
      ), -1, true,
    );
    rotate.value = withRepeat(
      withSequence(
        withTiming(-4, { duration: 1100, easing: Easing.inOut(Easing.sin) }),
        withTiming(4,  { duration: 1100, easing: Easing.inOut(Easing.sin) }),
      ), -1, true,
    );

    // Entrée spectaculaire pour niveaux 6+ (créatures légendaires)
    if (level >= 6) {
      scale.value = 0;
      opacity.value = 0;
      scale.value = withSequence(
        withSpring(1.4, { damping: 6, stiffness: 120 }),
        withSpring(1,   { damping: 8 }),
      );
      opacity.value = withTiming(1, { duration: 400 });
      if (onDragonAppear) setTimeout(onDragonAppear, 600);
    } else {
      scale.value = withSpring(1);
      opacity.value = withTiming(1, { duration: 300 });
    }
  }, [level, animate]);

  const animatedStyle = useAnimatedStyle(() => ({
    transform: [
      { translateY: translateY.value },
      { rotate: `${rotate.value}deg` },
      { scale: scale.value },
    ],
    opacity: opacity.value,
  }));

  const renderCreature = () => {
    switch (level) {
      case 1:  return <Fish1 size={size} />;
      case 2:  return <Fish2 size={size} />;
      case 3:  return <Fish3 size={size} />;
      case 4:  return <Fish4 size={size} />;
      case 5:  return <Fish5 size={size} />;
      case 6:  return <Dragon6 size={size * 1.2} />;
      case 7:  return <Narval7 size={size * 1.1} />;
      case 8:  return <Beluga8 size={size * 1.1} />;
      case 9:  return <RequinMarteau9 size={size * 1.15} />;
      case 10: return <Orque10 size={size * 1.15} />;
      case 11: return <BaleineBosse11 size={size * 1.2} />;
      case 12: return <Pieuvre12 size={size * 1.1} />;
      case 13: return <Espadon13 size={size * 1.2} />;
      case 14: return <TortueL14 size={size * 1.1} />;
      case 15: return <GRB15 size={size * 1.2} />;
      case 16: return <Megalodont16 size={size * 1.3} />;
      case 17: return <Capibara17 size={size * 1.15} />;
      case 18: return <Leviathan18 size={size * 1.1} />;
      case 19: return <Poseidon19 size={size * 1.2} />;
      default: return <Fish1 size={size} />;
    }
  };

  return (
    <Animated.View style={[styles.container, animatedStyle]}>
      {renderCreature()}
    </Animated.View>
  );
}

const styles = StyleSheet.create({
  container: { alignItems: 'center', justifyContent: 'center' },
});
