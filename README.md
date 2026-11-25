# DTP - Android TV Two-Pane Navigation

پروژه پیاده‌سازی صفحه تنظیمات دوپنلی (Two-Pane) برای Android TV با **Kotlin**، **Jetpack Compose for TV**، **Navigation 3**، **Coil** و **Koin**.

---

## 🏗️ برنچ اصلی و ساختار پروژه

- **برنچ اصلی (`new-develop`):** شامل کدبیس اصلی پروژه، پیاده‌سازی کامل UI تنظیمات Two-Pane، ناوبری بر پایه Navigation 3 و آخرین تغییرات برنامه.
- **نمایش تصویر در About:** قابلیت نمایش تصویر اختصاصی در بخش **About** (`device -> about`) با استفاده از `imageUrl` و کتابخانه Coil.

---

## ✨ ویژگی‌های جدید و برجسته

- **طراحی دو پنلی (Two-Pane) پیشرفته:** نمایش همزمان لیست اصلی تنظیمات و پنل پیش‌نمایش (Preview) با انیمیشن‌های transition روان.
- **نمایش تصویر در بخش About:** امکان بارگذاری و نمایش تصویر در صفحات تنظیمات (مانند بخش About) بر اساس فیلد `imageUrl` داده‌ها.
- **ناوبری چند سطحی (Multi-Level Hierarchy):** پشتیبانی از ساختار درختی تنظیمات به همراه حفظ و بازیابی دقیق فوکوس D-Pad هنگام برگشت به سطح قبل (Back).
- **پشتیبانی کامل از D-Pad ریموت TV:** پیمایش با کلیدهای Up/Down/Left/Right/Enter و دکمه Back.
- **پیاده‌سازی با Navigation 3:** مدیریت Backstack و `SceneStrategy` اختصاصی (`rememberTwoPaneSceneStrategy`).

---

## 🚀 مراحل Build و Run

1. پروژه را در **Android Studio** باز کنید.
2. روی یک **Android TV Emulator** یا دستگاه واقعی اجرا کنید:
   ```bash
   ./gradlew :app:assembleDebug
   ```

---

## 🧪 تست‌های خودکار

اجرای تمامی یونیت‌تست‌ها با دستور:
```bash
./gradlew test
```

---

## 🕹️ راهنمای تست دستی (Manual Testing)

برنامه را اجرا کرده و با ریموت یا D-Pad کیبورد موارد زیر را تست کنید:
- **Up / Down:** جابه‌جایی فوکوس روی لیست اصلی (بدون چرخش Wrap-around در ابتدا و انتها).
- **Select / Enter / Left:** ورود به زیرسطح (Forward) روی آیتم‌های دارای فرزند.
- **Right / Back:** بازگشت به سطح قبلی و **بازیابی دقیق فوکوس روی همان آیتم قبلی**.
- **بخش Device Preferences > About:** مشاهده تصویر اختصاصی در بخش **About**.
- **Back در سطح ۱:** خروج از صفحه تنظیمات.

---

## 💡 تصمیمات فنی و Trade-offها

- **Navigation 3 به جای روش‌های قدیمی:** پیاده‌سازی `SceneStrategy` اختصاصی برای رندر همزمان دو پنل کنار هم طبق مدرن‌ترین الگوی گوگل.
- **Coil:** بارگذاری تصاویری که در `settings.json` تعریف شده‌اند (`imageUrl`).
- **Koin به جای Hilt:** استفاده از Koin به دلیل سرعت بالا در Compile، عدم نیاز به Code Generation سنگین (KSP) و تعریف کدهای ساده‌تر.
- **Navigator مستقل از Compose:** کدهای ناوبری در ViewModel / Navigation کاملاً مستقل از UI پیاده‌سازی شده‌اند.

---
