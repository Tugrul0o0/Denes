# JavaFX Desktop Pet Uygulaması

Bu proje, masaüstünüzde her zaman üstte kalan, etkileşimli bir 3D karakter görüntüleyen bir JavaFX uygulamasıdır.

## Özellikler

- **Her Zaman Üstte:** 3D karakter her zaman diğer pencerelerin üzerinde görünür.
- **Şeffaf Arka Plan:** Sadece 3D karakter görünür, arkasındaki masaüstünüzü görebilirsiniz.
- **Sağ Tıklama Menüsü:**
  - **Animasyon Oynat:** Karakterin zıplamasını sağlar.
  - **Tıklamayı Kapat:** Pencereyi tıklanamaz hale getirir, böylece arkasındaki uygulamalarla etkileşime girebilirsiniz.
  - **Kontrol Paneli:** Uygulama ayarlarını ve not defterini açar.
- **Global Kısayol:** `Ctrl+R+T` tuş kombinasyonu, tıklanamaz hale getirilmiş pencereyi tekrar tıklanabilir yapar.
- **Kontrol Paneli:**
  - Farklı 3D karakterler (yer tutucular) arasında geçiş yapma.
  - Not alma, kaydetme ve silme işlevselliği.
- **Tema:** Yeşil-siyah renk paletine sahip "hacker" teması.

## Kurulum ve Çalıştırma

Bu uygulama Java 21 ve JavaFX 11+ gerektirir.

### Linux / macOS

1.  **JavaFX'i Kurun:**
    - **Ubuntu/Debian:** `sudo apt-get install openjfx`
    - Diğer sistemler için JavaFX SDK'yı manuel olarak indirmeniz gerekebilir. `run.sh` içindeki `JAVAFX_PATH` değişkenini SDK'nızın `lib` klasörüne göre güncelleyin.

2.  **Uygulamayı Çalıştırın:**
    Terminalde aşağıdaki komutu çalıştırın:
    ```bash
    ./run.sh
    ```

### Windows

1.  **JavaFX SDK'yı İndirin:**
    - JavaFX SDK'sını [GluonHQ web sitesinden](https://gluonhq.com/products/javafx/) indirin ve bir yere çıkarın.
    - `JAVAFX_HOME` adında bir ortam değişkeni oluşturun ve bu değişkenin değerini SDK'nın içindeki `lib` klasörünün yolu olarak ayarlayın (örn: `C:\path\to\javafx-sdk-21\lib`).

2.  **Uygulamayı Çalıştırın:**
    `run.bat` dosyasına çift tıklayarak uygulamayı derleyin ve çalıştırın.

## Paketleme Notu

Proje, `jpackage` aracıyla platforma özgü bağımsız bir çalıştırılabilir (`.exe` vb.) olarak paketlenmek üzere tasarlanmıştır. Ancak, geliştirme ortamının dosya sayısı kısıtlamaları nedeniyle bu adım tamamlanamamıştır. Gerekli komutlar ve yapılandırma hazırdır ve kısıtlamaların olmadığı bir makinede çalıştırılabilir.
