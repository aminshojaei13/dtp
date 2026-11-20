# DTP - Android TV Two-Pane Navigation

پروژه پیاده‌سازی صفحه تنظیمات دوپنلی (Two-Pane) برای Android TV با **Kotlin**، **Jetpack Compose for TV**، **Navigation 3** و **Koin**.

---

## 🏗️ معماری و ماژولار ساختن (Modularization Branch)

- **برنچ اصلی (`main`):** شامل کدبیس اصلی پروژه، پیاده‌سازی کامل UI، ناوبری Two-Pane و ۲۰ یونیت‌تست با موفقیت پاس‌شده.
- **برنچ پیاده‌سازی (`develop`):** جهت مرج تمامی کد ها و بررسی و تست بیشتر (Review & Testing)، قابلیت Two-Pane به یک ماژول مستقل به نام `:twopane` تفکیک شده است تا فیچرهای دیگر برنامه هم بدون تغییر کد بتوانند از آن استفاده کنند.

---

## 🚀 مراحل Build و Run

1. پروژه را در **Android Studio** باز کنید.
2. روی یک **Android TV Emulator** یا دستگاه واقعی اجرا کنید:
   ```bash
   ./gradlew :app:assembleDebug
   ```

---

## 🧪 تست‌های خودکار

اجرای تمامی ۲۰ یونیت‌تست با یک دستور ساده:
```bash
./gradlew test
```

---

## 🕹️ راهنمای تست دستی (Manual Testing)

برنامه را اجرا کرده و با ریموت یا D-Pad کیبورد موارد زیر را تست کنید:
- **Up / Down:** جابه‌جایی فوکوس روی لیست اصلی (بدون چرخش Wrap-around در ابتدا و انتها).
- **Select / Enter / Left:** ورود به زیرسطح (Forward) روی آیتم‌های دارای فرزند.
- **Right / Back:** بازگشت به سطح قبلی و **بازیابی دقیق فوکوس روی همان آیتم قبلی**.
- **Back در سطح ۱:** خروج از صفحه تنظیمات.

---

## 💡 تصمیمات فنی و Trade-offها

- **Navigation 3 به جای روش‌های قدیمی:** پیاده‌سازی `SceneStrategy` اختصاصی برای رندر همزمان دو پنل کنار هم طبق مدرن‌ترین الگوی گوگل.
- **Koin به جای Hilt:** استفاده از Koin به دلیل سرعت بالا در Compile، عدم نیاز به Code Generation سنگین (KSP) و تعریف کدهای ساده‌تر.
- **Navigator مستقل از Compose:** کدهای ناوبری در `SettingsNavigator` کاملاً مستقل از UI پیاده‌سازی شده‌اند تا ۱۰۰٪ قابل یونیت‌تست باشند.

---

## 📖 راهنمای افزودن یک Flow جدید در ۵ گام (ماژول `:twopane`)

1. **افزودن وابستگی:** `implementation(project(":twopane"))` در `build.gradle.kts`.
2. **تعریف Data Model:** پیاده‌سازی اینترفیس `TwoPaneNavNode`.
3. **تعریف داده‌های درختی:** ایجاد درخت داده تا هر چند سطح دلخواه.
4. **مدیریت State ناوبری:** نگهداری `path` و `focusedIndex`.
5. **استفاده از UI عمومی:** فراخوانی `TwoPaneContainer` در کامپوزبل.

---
## 🤖 ابزار هوش مصنوعی مورد استفاده

- **Gemini in Android Studio:** کمک در تحلیل نیازمندی‌ها، ساخت SceneStrategy سفارشی در Navigation 3 و تنظیم دقیق Key Handling برای D-Pad.
