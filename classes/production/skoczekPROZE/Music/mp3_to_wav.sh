#!/bin/bash
# Skrypt konwertuje wszystkie pliki MP3 w katalogu na WAV

# Sprawdzenie, czy ffmpeg jest dostępny
if ! command -v ffmpeg &> /dev/null
then
    echo "ffmpeg nie znaleziony! Dodaj go do PATH."
    exit 1
fi

# Pętla po wszystkich plikach .mp3 w katalogu
for file in *.mp3; do
    # Sprawdzenie, czy plik istnieje
    if [ -f "$file" ]; then
        # Tworzenie nazwy pliku wyjściowego
        out="${file%.mp3}.wav"
        echo "Konwertuję $file → $out"
        ffmpeg -y -i "$file" "$out"
    fi
done

echo "Gotowe! Wszystkie pliki MP3 zostały przekonwertowane na WAV."
