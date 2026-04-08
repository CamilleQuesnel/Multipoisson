"""
Vérification des chapitres One Piece
======================================
Vérifie que chaque dossier chapitre contient :
  - Au moins 15 images
  - Des pages qui se suivent sans trou (page_001, page_002, page_003...)

Usage :
    python verifier_chapitres.py
    python verifier_chapitres.py --dossier D:\\manga\\one_piece
    python verifier_chapitres.py --min-pages 10
"""

import os
import re
import argparse


def renommer_si_necessaire(chemin: str, nom_dossier: str) -> int:
    """
    Détecte les fichiers XXX.jpg (sans préfixe page_) et les renomme en page_XXX.jpg.
    Retourne le nombre de fichiers renommés.
    """
    fichiers = os.listdir(chemin)
    a_renommer = sorted([
        f for f in fichiers
        if re.match(r"^\d+\.(jpg|jpeg|png|webp|gif)$", f, re.IGNORECASE)
    ])

    if not a_renommer:
        return 0

    print(f"  [renommage] {nom_dossier} : {len(a_renommer)} fichier(s) à renommer...")
    for f in a_renommer:
        m = re.match(r"^(\d+)\.(.+)$", f, re.IGNORECASE)
        if m:
            num = int(m.group(1))
            ext = m.group(2).lower()
            nouveau = f"page_{num:03d}.{ext}"
            src = os.path.join(chemin, f)
            dst = os.path.join(chemin, nouveau)
            if not os.path.exists(dst):
                os.rename(src, dst)
    print(f"  [✓] Renommage terminé.")
    return len(a_renommer)


def verifier_chapitres(dossier_base: str, min_pages: int = 15):
    if not os.path.isdir(dossier_base):
        print(f"[✗] Dossier introuvable : {dossier_base}")
        return

    sous_dossiers = sorted([
        d for d in os.listdir(dossier_base)
        if os.path.isdir(os.path.join(dossier_base, d))
        and d.startswith("chapitre_")
    ])

    if not sous_dossiers:
        print(f"[!] Aucun dossier chapitre_ trouvé dans {dossier_base}")
        return

    print(f"[→] Vérification de {len(sous_dossiers)} chapitres dans {dossier_base}\n")

    total_renommes = 0
    incomplets = []
    ok = 0

    for nom in sous_dossiers:
        chemin = os.path.join(dossier_base, nom)

        # Renommer les fichiers XXX.jpg → page_XXX.jpg si nécessaire
        total_renommes += renommer_si_necessaire(chemin, nom)

        # Récupérer tous les fichiers page_XXX
        pages = sorted([
            f for f in os.listdir(chemin)
            if re.match(r"page_\d+\.(jpg|jpeg|png|webp|gif)$", f, re.IGNORECASE)
        ])

        numeros = []
        for p in pages:
            m = re.match(r"page_(\d+)\.", p, re.IGNORECASE)
            if m:
                numeros.append(int(m.group(1)))

        nb = len(numeros)
        problemes = []

        # Vérification 1 : nombre minimum
        if nb < min_pages:
            problemes.append(f"seulement {nb} image(s) (min {min_pages})")

        # Vérification 2 : pages qui se suivent sans trou
        if numeros:
            numeros_tries = sorted(numeros)
            trous = []
            for i in range(len(numeros_tries) - 1):
                attendu = numeros_tries[i] + 1
                suivant = numeros_tries[i + 1]
                if suivant != attendu:
                    trous.append(f"page_{attendu:03d} manquante")
            if trous:
                problemes.append("trous : " + ", ".join(trous[:5]) + (" ..." if len(trous) > 5 else ""))

        if problemes:
            incomplets.append((nom, nb, problemes))
        else:
            ok += 1

    # Résumé
    if total_renommes > 0:
        print(f"\n[✓] {total_renommes} fichier(s) renommé(s) au format page_XXX au total.")
    print(f"{'─'*55}")
    if not incomplets:
        print(f"[✓] Tous les {ok} chapitres sont complets ({min_pages}+ pages, sans trou).")
    else:
        print(f"[✓] {ok} chapitres OK")
        print(f"[!] {len(incomplets)} chapitre(s) incomplet(s) :\n")
        for nom, nb, problemes in incomplets:
            print(f"  {nom} ({nb} pages)")
            for p in problemes:
                print(f"    → {p}")
        print(f"\n{'─'*55}")
        print(f"Pour re-scraper les chapitres incomplets, lance :")
        nums = []
        for nom, _, _ in incomplets:
            m = re.search(r"chapitre_(\d+)", nom)
            if m:
                nums.append(int(m.group(1)))
        if nums:
            for n in nums:
                print(f"  python scraper_animoflix.py --chapitre {n} --debug-port")


if __name__ == "__main__":
    parser = argparse.ArgumentParser(description="Vérifie l'intégrité des chapitres One Piece")
    parser.add_argument("--dossier", type=str, default="one_piece", help="Dossier racine (défaut: one_piece)")
    parser.add_argument("--min-pages", type=int, default=15, help="Nombre minimum de pages par chapitre (défaut: 15)")
    args = parser.parse_args()

    verifier_chapitres(args.dossier, args.min_pages)
