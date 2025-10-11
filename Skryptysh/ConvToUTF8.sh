find src/ -name "*.java" | while read file; do
    iconv -f WINDOWS-1250 -t UTF-8 "$file" > "$file.utf8"
    mv "$file.utf8" "$file"
done
