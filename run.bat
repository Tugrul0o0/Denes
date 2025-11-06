@echo off
echo Uygulama derleniyor...

:: JavaFX SDK'nızın yolunu gösteren JAVAFX_HOME ortam değişkenini ayarladığınızdan emin olun.
:: Örnek: set JAVAFX_HOME="C:\path\to\javafx-sdk-21\lib"

javac --module-path %JAVAFX_HOME% ^
      --add-modules javafx.controls,javafx.graphics ^
      -cp "lib\\jnativehook-2.1.0.jar" ^
      -d bin ^
      src\\com\\desktoppet\\*.java

if %errorlevel% neq 0 (
    echo Derleme basarisiz oldu.
    pause
    exit /b
)

echo Kaynaklar kopyalaniyor...
xcopy res\\style.css bin\\ /Y

echo Uygulama baslatiliyor...
java --module-path %JAVAFX_HOME% ^
     --add-modules javafx.controls,javafx.graphics ^
     -cp "bin;lib\\jnativehook-2.1.0.jar" ^
     com.desktoppet.Main
