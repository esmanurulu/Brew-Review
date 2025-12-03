<div align="center">

  <img src="src/main/resources/static/images/logo.png" alt="Brew&Review Logo" width="230" height="230" />
  
  <h1>☕️ Brew&Review</h1>
  
  <p><strong>Kahve Tutkunları İçin Sosyal Değerlendirme ve Keşif Platformu</strong></p>

  <p>
    <a href="#-proje-hakkında">Proje Hakkında</a> •
    <a href="#-özellikler">Özellikler</a> •
    <a href="#-teknolojiler">Teknolojiler</a> •
    <a href="#-kurulum-ve-çalıştırma">Kurulum</a>
  </p>

  ![Java](https://img.shields.io/badge/Java-21-ED8B00?style=for-the-badge&logo=openjdk&logoColor=white)
  ![Spring Boot](https://img.shields.io/badge/Spring_Boot-3.x-6DB33F?style=for-the-badge&logo=spring&logoColor=white)
  ![MySQL](https://img.shields.io/badge/MySQL-Aiven_Cloud-4479A1?style=for-the-badge&logo=mysql&logoColor=white)
  ![Thymeleaf](https://img.shields.io/badge/Thymeleaf-Frontend-005F0F?style=for-the-badge&logo=thymeleaf&logoColor=white)

</div>

---

## 📖 Proje Hakkında

**Brew&Review**, standart kafe rehberlerinden farklı olarak kullanıcıların yalnızca işletmeleri değil, **ürünleri (örn. Latte)** ve **baristaları** da ayrı ayrı değerlendirebildiği sosyal bir platformdur.  
Kullanıcılar **check-in sistemi** ile ziyaret geçmişi oluşturabilir, yorum yapabilir ve diğer kullanıcılarla sosyal etkileşime girebilir.

Proje, **veri bütünlüğü**, **performans optimizasyonu** ve **gerçek zamanlı Bulut MySQL veritabanı yönetimi** üzerine inşa edilmiştir.

---

## ✨ Özellikler

### 👤 Kullanıcı Modülü
- Kafe, ürün ve barista değerlendirmesi
- Check-in sistemi (ziyaret etmeden yorum yapılamaz)
- Kullanıcı takip etme, favorilere ekleme
- Profil sayfası, ziyaret geçmişi ve yorumlar

### 👔 Yönetici (Manager) Paneli
- İşyeri & ruhsat doğrulamalı kayıt
- Menü ve barista yönetimi
- 🔥 Trend ürün analizi  
- ⏱ Yoğun saat/gün istatistikleri

### 🗺 Keşfet & Harita
- Akıllı arama (şehir, kafe adı, filtreleme)
- A–Z veya puana göre sıralama
- Leaflet.js + OpenStreetMap harita görünümü

---

## 🛠 Kullanılan Teknolojiler

| Alan | Teknoloji |
| :--- | :--- |
| **Backend** | Java 21, Spring Boot 3.2 (Web, JPA) |
| **Frontend** | Thymeleaf, HTML, CSS, JS |
| **Database** | MySQL 8.0 (Aiven Cloud) |
| **Map API** | Leaflet.js & OpenStreetMap |
| **Tools** | Maven, Git, DBeaver |

---

🚀 Kurulum ve Çalıştırma
1. Projeyi Klonlayın
```bash
git clone https://github.com/esmanurulu/BrewAndReview.git
cd BrewAndReview
```

3. Veritabanı Ayarları

src/main/resources/application.properties dosyasını açın ve şifreyi ekleyin:
```bash
spring.datasource.url=jdbc:mysql://brewandreview-uluesma8-d25c.b.aivencloud.com:22065/defaultdb?ssl-mode=REQUIRED
spring.datasource.username=avnadmin
spring.datasource.password=BURAYA_AIVEN_SIFRESINI_YAZIN
```

3. Uygulamayı Başlatma
```bash
./mvnw spring-boot:run
```

Tarayıcıdan açabilirsiniz:
```bash
👉 http://localhost:8080
```

<div align="center">

Developed with ❤️ by
Esmanur Ulu & Zeynep Yetkin

</div>
