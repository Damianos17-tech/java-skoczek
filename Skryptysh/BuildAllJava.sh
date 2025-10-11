find src/ -name "*.java" | while read file; do
    javac --module-path "../javafx-sdk-25/lib" --add-modules javafx.controls,javafx.fxml -encoding UTF-8 -d bin "$file"
done
