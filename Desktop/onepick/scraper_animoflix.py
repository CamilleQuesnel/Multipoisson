"""
Scraper One Piece — animoflix.com
==================================
Utilise Selenium car les images sont chargées via JavaScript.

Installation :
    pip install selenium

Selenium télécharge Chrome automatiquement via Selenium Manager (inclus depuis Selenium 4.6).
Pas besoin d'installer ChromeDriver manuellement.

Usage :
    # Un chapitre précis
    python scraper_animoflix.py --chapitre 1

    # Une plage
    python scraper_animoflix.py --debut 1 --fin 10

    # Tous les chapitres
    python scraper_animoflix.py --tous

    # Dossier de sortie personnalisé
    python scraper_animoflix.py --chapitre 1 --sortie D:\\manga\\one_piece

Structure de sortie :
    one_piece/
        chapitre_0001/
            page_001.jpg
            ...
        chapitre_0002/
            ...
"""

import os
import re
import time
import argparse
import requests
from urllib.parse import urljoin

from selenium import webdriver
from selenium.webdriver.chrome.options import Options
from selenium.webdriver.common.by import By
from selenium.webdriver.support.ui import WebDriverWait
from selenium.webdriver.support import expected_conditions as EC

# ──────────────────────────────────────────────
# Configuration
# ──────────────────────────────────────────────

BASE_URL      = "https://animoflix.com"
MANGA_BASE    = "https://animoflix.com/anime/one-piece/scan/vf"
OUTPUT_DIR    = "one_piece"

ATTENTE_JS        = 8    # secondes max pour que les images se chargent
DELAY_PAGES       = 2.0  # secondes entre les pages d'un chapitre
DELAY_CHAPITRES   = 4.0  # secondes entre les chapitres


# ──────────────────────────────────────────────
# Initialisation Selenium
# ──────────────────────────────────────────────

def appliquer_options_furtives(options: Options):
    """Options communes pour masquer Selenium au maximum."""
    options.add_argument("--no-sandbox")
    options.add_argument("--disable-dev-shm-usage")
    options.add_argument("--disable-gpu")
    options.add_argument("--window-size=1280,900")
    options.add_argument("--disable-blink-features=AutomationControlled")
    options.add_argument("--disable-infobars")
    options.add_argument(
        "user-agent=Mozilla/5.0 (Windows NT 10.0; Win64; x64) "
        "AppleWebKit/537.36 (KHTML, like Gecko) "
        "Chrome/122.0.0.0 Safari/537.36"
    )
    options.add_experimental_option("excludeSwitches", ["enable-automation", "enable-logging"])
    options.add_experimental_option("useAutomationExtension", False)


def masquer_webdriver(driver: webdriver.Chrome):
    """Injecte du JS pour masquer navigator.webdriver."""
    driver.execute_cdp_cmd(
        "Page.addScriptToEvaluateOnNewDocument",
        {"source": """
            Object.defineProperty(navigator, 'webdriver', {get: () => undefined});
            window.chrome = { runtime: {} };
            Object.defineProperty(navigator, 'languages', {get: () => ['fr-FR', 'fr', 'en-US', 'en']});
            Object.defineProperty(navigator, 'plugins', {get: () => [1, 2, 3]});
        """}
    )


def creer_driver() -> webdriver.Chrome:
    """Crée un driver Chrome headless furtif."""
    options = Options()
    options.add_argument("--headless=new")
    appliquer_options_furtives(options)
    driver = webdriver.Chrome(options=options)
    masquer_webdriver(driver)
    return driver


# ──────────────────────────────────────────────
# Utilitaires
# ──────────────────────────────────────────────

def extension_depuis_url(url: str) -> str:
    clean = url.split("?")[0]
    ext = os.path.splitext(clean)[1].lower()
    return ext if ext in (".jpg", ".jpeg", ".png", ".webp", ".gif") else ".jpg"


def telecharger_image(url: str, chemin: str, referer: str) -> bool:
    """Télécharge une image avec le bon Referer."""
    headers = {
        "User-Agent": (
            "Mozilla/5.0 (Windows NT 10.0; Win64; x64) "
            "AppleWebKit/537.36 (KHTML, like Gecko) "
            "Chrome/122.0.0.0 Safari/537.36"
        ),
        "Referer": referer,
        "Accept": "image/webp,image/apng,image/*,*/*;q=0.8",
    }
    try:
        r = requests.get(url, headers=headers, timeout=30)
        r.raise_for_status()
        with open(chemin, "wb") as f:
            f.write(r.content)
        return True
    except Exception as e:
        print(f"      [✗] Erreur téléchargement {url} : {e}")
        return False


# ──────────────────────────────────────────────
# Récupération de la liste des chapitres
# ──────────────────────────────────────────────

def recuperer_liste_chapitres(driver: webdriver.Chrome) -> list[dict]:
    """Scrape la page principale pour récupérer tous les liens de chapitres."""
    print(f"[→] Récupération de la liste depuis {MANGA_BASE}/")
    driver.get(MANGA_BASE + "/")
    time.sleep(3)

    chapitres = []
    vus = set()

    pattern = re.compile(r"/chapitre-(\d+(?:\.\d+)?)/?\s*$", re.IGNORECASE)

    liens = driver.find_elements(By.TAG_NAME, "a")
    for a in liens:
        href = a.get_attribute("href") or ""
        m = pattern.search(href)
        if m and href not in vus:
            vus.add(href)
            num = float(m.group(1))
            chapitres.append({"num": num, "url": href})

    chapitres.sort(key=lambda x: x["num"])
    print(f"[✓] {len(chapitres)} chapitres trouvés.")
    return chapitres


def url_chapitre(num: int | float) -> str:
    n = str(int(num)) if num == int(num) else str(num)
    return f"{MANGA_BASE}/chapitre-{n}/"


# ──────────────────────────────────────────────
# Extraction des images via Selenium
# ──────────────────────────────────────────────

def fermer_popup_alert(driver: webdriver.Chrome):
    """Ferme une popup alert JavaScript si elle est présente."""
    try:
        WebDriverWait(driver, 2).until(EC.alert_is_present())
        alert = driver.switch_to.alert
        print(f"    [popup] Alert détectée : '{alert.text}' → fermeture.")
        alert.accept()
        time.sleep(1)
        return True
    except Exception:
        return False


def cliquer_lire_chapitre(driver: webdriver.Chrome):
    """Cherche et clique sur le bouton 'LIRE LE CHAPITRE' si présent."""
    # Fermer toute popup présente avant de chercher le bouton
    fermer_popup_alert(driver)

    mots_cles = ["lire le chapitre", "lire", "read", "📖", "start reading"]
    try:
        boutons = driver.find_elements(By.TAG_NAME, "button")
        liens = driver.find_elements(By.TAG_NAME, "a")
        for el in boutons + liens:
            texte = (el.text or "").strip().lower()
            if any(mot in texte for mot in mots_cles):
                print(f"    [clic] Bouton trouvé : '{el.text.strip()}'")
                driver.execute_script("arguments[0].click();", el)
                time.sleep(1)
                # Fermer la popup "Erreur de vérification" si elle apparaît
                fermer_popup_alert(driver)
                return True
    except Exception as e:
        print(f"    [!] Erreur clic bouton : {e}")
    return False


def fermer_alert_si_presente(driver: webdriver.Chrome):
    """Ferme immédiatement toute alert JS ouverte."""
    try:
        alert = driver.switch_to.alert
        print(f"    [alert] '{alert.text}' → fermée.")
        alert.accept()
        time.sleep(0.5)
        return True
    except Exception:
        return False


def exec_js_safe(driver: webdriver.Chrome, script: str):
    """Exécute du JS en fermant d'abord toute alert présente."""
    fermer_alert_si_presente(driver)
    return driver.execute_script(script)


def attendre_et_extraire_images(driver: webdriver.Chrome, url_page: str) -> list[str]:
    """
    Charge une page de chapitre, clique sur le bouton de lecture,
    attend que le JS charge les images, et retourne leurs URLs.
    """
    driver.get(url_page)
    time.sleep(3)

    # Fermer toute alert présente au chargement
    fermer_alert_si_presente(driver)

    # Cliquer sur "LIRE LE CHAPITRE" si présent
    clique = cliquer_lire_chapitre(driver)
    if clique:
        print("    [~] Vérification de sécurité en cours...")
        # Attendre 4s que la vérification auto se termine
        time.sleep(4)
        # Fermer l'alert d'erreur si elle a quand même surgi
        fermer_alert_si_presente(driver)
        # Retenter le clic si la vérification a échoué
        fermer_alert_si_presente(driver)
        print("    [✓] Vérification terminée.")

    # Scroll progressif — en fermant les alerts au fur et à mesure
    hauteur = exec_js_safe(driver, "return document.body.scrollHeight")
    position = 0
    pas = 500
    while position < hauteur:
        fermer_alert_si_presente(driver)
        exec_js_safe(driver, f"window.scrollTo(0, {position});")
        time.sleep(0.4)
        position += pas
        hauteur = exec_js_safe(driver, "return document.body.scrollHeight")

    time.sleep(1)
    fermer_alert_si_presente(driver)
    exec_js_safe(driver, "window.scrollTo(0, 0);")
    time.sleep(0.5)

    # Extraire toutes les URLs d'images candidates
    candidats = []
    attrs = ["src", "data-src", "data-lazy-src", "data-original", "data-url"]

    images = driver.find_elements(By.TAG_NAME, "img")
    for img in images:
        for attr in attrs:
            val = img.get_attribute(attr) or ""
            val = val.strip()
            if not val or val.startswith("data:"):
                continue
            # Filtrer les images de layout
            if any(kw in val.lower() for kw in ["logo", "icon", "avatar", "banner", "ad", "sponsor"]):
                continue
            # Garder seulement les images "grandes" (probables pages de manga)
            try:
                largeur = img.size.get("width", 0)
                hauteur_img = img.size.get("height", 0)
                if largeur < 100 and hauteur_img < 100:
                    continue
            except Exception:
                pass
            candidats.append(val)
            break

    # Dédoublonnage en conservant l'ordre
    return list(dict.fromkeys(candidats))


def trouver_pages_chapitre(driver: webdriver.Chrome, url_chapitre_base: str) -> list[str]:
    """
    Gère la pagination d'un chapitre (ex: Page 1/57, Page 2/57...).
    Retourne la liste complète des URLs d'images.
    """
    toutes_images = []
    url_courante = url_chapitre_base
    pages_visitees = set()

    while url_courante and url_courante not in pages_visitees:
        pages_visitees.add(url_courante)
        print(f"    Chargement : {url_courante}")

        images = attendre_et_extraire_images(driver, url_courante)
        print(f"    → {len(images)} image(s) trouvée(s)")
        toutes_images.extend(images)

        # Chercher le bouton "SUIVANT" ou lien page suivante
        url_suivante = None
        try:
            # Chercher un lien "Suivant" / "SUIVANT" / ">"
            for a in driver.find_elements(By.TAG_NAME, "a"):
                texte = (a.text or "").strip().upper()
                href = a.get_attribute("href") or ""
                if texte in ("SUIVANT", "NEXT", ">", "→") and href and href not in pages_visitees:
                    url_suivante = href
                    break

            # Certains sites paginent via ?page=N
            if not url_suivante:
                m = re.search(r"[?&]page=(\d+)", url_courante)
                if m:
                    page_num = int(m.group(1)) + 1
                    url_suivante = re.sub(r"([?&]page=)\d+", rf"\g<1>{page_num}", url_courante)
                    # Vérifier via l'URL courante du driver après navigation test
        except Exception:
            pass

        if url_suivante and url_suivante != url_courante:
            url_courante = url_suivante
            time.sleep(DELAY_PAGES)
        else:
            break

    return list(dict.fromkeys(toutes_images))  # dédoublonnage final


# ──────────────────────────────────────────────
# Scraping d'un chapitre complet
# ──────────────────────────────────────────────

def scraper_chapitre(driver: webdriver.Chrome, num: float, url: str):
    """Télécharge toutes les images d'un chapitre."""
    if num == int(num):
        dossier_nom = f"chapitre_{int(num):04d}"
    else:
        dossier_nom = f"chapitre_{str(num).replace('.', '_')}"

    dossier = os.path.join(OUTPUT_DIR, dossier_nom)
    os.makedirs(dossier, exist_ok=True)

    # Vider le dossier si des images existent déjà (remplacement)
    fichiers_existants = [f for f in os.listdir(dossier) if f.startswith("page_")]
    if fichiers_existants:
        print(f"  [~] {dossier_nom} — {len(fichiers_existants)} pages existantes supprimées, remplacement en cours.")
        for f in fichiers_existants:
            os.remove(os.path.join(dossier, f))

    print(f"\n[→] Chapitre {num} — {url}")

    images = trouver_pages_chapitre(driver, url)

    if not images:
        print(f"  [!] Aucune image trouvée pour le chapitre {num}.")
        # Sauvegarder le HTML pour diagnostic
        with open(os.path.join(dossier, "debug.html"), "w", encoding="utf-8") as f:
            f.write(driver.page_source)
        print(f"      HTML sauvegardé dans {dossier}/debug.html")
        return

    print(f"  [→] {len(images)} images à télécharger...")

    for i, img_url in enumerate(images, start=1):
        ext = extension_depuis_url(img_url)
        nom_fichier = f"page_{i:03d}{ext}"
        chemin = os.path.join(dossier, nom_fichier)

        if os.path.exists(chemin):
            print(f"    [{i}/{len(images)}] {nom_fichier} déjà présent.")
            continue

        ok = telecharger_image(img_url, chemin, referer=url)
        if ok:
            print(f"    [{i}/{len(images)}] {nom_fichier} ✓")
        time.sleep(0.4)

    print(f"  [✓] {dossier_nom} terminé.")


# ──────────────────────────────────────────────
# Point d'entrée
# ──────────────────────────────────────────────

def main():
    global OUTPUT_DIR

    parser = argparse.ArgumentParser(description="Scraper One Piece — animoflix.com")
    group = parser.add_mutually_exclusive_group(required=True)
    group.add_argument("--chapitre", type=float, help="Numéro d'un chapitre (ex: 1)")
    group.add_argument("--debut", type=float, help="Premier chapitre d'une plage")
    group.add_argument("--tous", action="store_true", help="Tous les chapitres")
    parser.add_argument("--fin", type=float, default=None, help="Dernier chapitre (avec --debut)")
    parser.add_argument("--sortie", type=str, default=OUTPUT_DIR, help=f"Dossier de sortie (défaut: {OUTPUT_DIR})")
    parser.add_argument("--visible", action="store_true", help="Afficher la fenêtre Chrome (mode debug)")
    parser.add_argument("--debug-port", action="store_true", help="Se connecter à un Chrome déjà ouvert sur le port 9222")

    args = parser.parse_args()

    OUTPUT_DIR = args.sortie
    os.makedirs(OUTPUT_DIR, exist_ok=True)

    # Mode debug : se connecter à un Chrome déjà ouvert avec --remote-debugging-port=9222
    if args.debug_port:
        options = Options()
        options.add_experimental_option("debuggerAddress", "127.0.0.1:9222")
        driver = webdriver.Chrome(options=options)
        print("[✓] Connecté au Chrome existant.")
    elif args.visible:
        options = Options()
        appliquer_options_furtives(options)
        driver = webdriver.Chrome(options=options)
        masquer_webdriver(driver)
    else:
        driver = creer_driver()

    try:
        if args.chapitre is not None:
            scraper_chapitre(driver, args.chapitre, url_chapitre(args.chapitre))

        elif args.debut is not None:
            fin = args.fin if args.fin else args.debut
            chapitres = recuperer_liste_chapitres(driver)
            selection = [c for c in chapitres if args.debut <= c["num"] <= fin]
            if not selection:
                print(f"[!] Aucun chapitre entre {args.debut} et {fin}.")
                return
            print(f"[→] {len(selection)} chapitres à scraper.")
            for c in selection:
                scraper_chapitre(driver, c["num"], c["url"])
                time.sleep(DELAY_CHAPITRES)

        elif args.tous:
            chapitres = recuperer_liste_chapitres(driver)
            print(f"[→] {len(chapitres)} chapitres à scraper.")
            for c in chapitres:
                scraper_chapitre(driver, c["num"], c["url"])
                time.sleep(DELAY_CHAPITRES)

    finally:
        driver.quit()

    print("\n[✓] Scraping terminé.")


if __name__ == "__main__":
    main()
