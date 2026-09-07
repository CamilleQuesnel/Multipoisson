/**
 * Génère icon.png et adaptive-icon.png depuis icon.svg
 * Exécuter : node scripts/generate-icon.js
 * Prérequis : npm install sharp (une seule fois)
 */

const sharp = require('sharp');
const path  = require('path');
const fs    = require('fs');

const SRC  = path.join(__dirname, '../assets/icon.svg');
const DEST = path.join(__dirname, '../assets');

async function generate() {
  const svgBuffer = fs.readFileSync(SRC);

  // icon.png – 1024×1024
  await sharp(svgBuffer)
    .resize(1024, 1024)
    .png()
    .toFile(path.join(DEST, 'icon.png'));
  console.log('✅  icon.png généré (1024×1024)');

  // adaptive-icon.png – 1024×1024
  await sharp(svgBuffer)
    .resize(1024, 1024)
    .png()
    .toFile(path.join(DEST, 'adaptive-icon.png'));
  console.log('✅  adaptive-icon.png généré (1024×1024)');

  // splash.png – 1284×2778 (fond bleu + logo centré)
  await sharp(svgBuffer)
    .resize(512, 512)
    .toBuffer()
    .then((logoBuffer) =>
      sharp({
        create: {
          width: 1284,
          height: 2778,
          channels: 4,
          background: { r: 28, g: 176, b: 246, alpha: 1 },
        },
      })
        .composite([{ input: logoBuffer, gravity: 'center' }])
        .png()
        .toFile(path.join(DEST, 'splash.png'))
    );
  console.log('✅  splash.png généré (1284×2778)');
}

generate().catch((err) => {
  console.error('❌ Erreur :', err.message);
  console.error('   → Lance d\'abord : npm install sharp');
});
