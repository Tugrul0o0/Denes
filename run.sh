#!/bin/bash

# JavaFX ve JNativeHook kütüphanelerinin yolları
JAVAFX_PATH="/usr/share/openjfx/lib"
JNI_PATH="lib/jnativehook-2.1.0.jar"

# 1. Derleme
echo "Uygulama derleniyor..."
javac --module-path "$JAVAFX_PATH" \
      --add-modules javafx.controls,javafx.graphics \
      -cp "$JNI_PATH" \
      -d bin \
      src/com/desktoppet/*.java

# Derleme başarılı oldu mu kontrol et
if [ $? -ne 0 ]; then
    echo "Derleme başarısız oldu."
    exit 1
fi

# Kaynakları kopyala
cp res/style.css bin/style.css

# 2. Çalıştırma
echo "Uygulama başlatılıyor..."
java --module-path "$JAVAFX_PATH" \
     --add-modules javafx.controls,javafx.graphics \
     -cp "bin:$JNI_PATH" \
     com.desktoppet.Main
