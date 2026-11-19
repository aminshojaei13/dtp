# پیاده‌سازی Two-Pane Navigation برای Android TV

## ۱. برداشت من از مسئله

هدف، پیاده‌سازی یک پروژه شبیه به تنظیمات برای Android TV با استفاده از Kotlin، Jetpack Compose و Navigation 3 است.

صفحه از دو بخش تشکیل می‌شود:

- **Main Pane (سمت راست):** نمایش لیست فعلی و مدیریت Focus.
- **Preview Pane (سمت چپ):** نمایش اطلاعات آیتمی که در Main Pane فوکوس شده و نباید Focus بگیرد.

شبیه‌سازی تمام تعاملات باید از طریق ریموت تلویزیون انجام می‌شود.

### فرضیات
- حرکت به جلو با Left یا Select انجام می‌شود.
- بازگشت به سطح قبل با Right یا Back انجام می‌شود.
- حرکت Up و Down بدون چرخش بین ابتدا و انتهای لیست است.
- هنگام Back، فوکوس باید به آیتم قبلی برگردد.
- داده‌ها از یک Fake In-Memory Repository دریافت می‌شوند.

## ۲. مراحل پیاده‌سازی و تخمین زمان
*زمان ها تقریبی و با کمک هوش مصنوعی تخمین زده شده‌اند.

| زمان   | مرحله |
|--------|--:|
| ۲ ساعت | ساخت پروژه و طراحی ساختار اولیه |
| ۲ ساعت | طراحی Data Model و Repository |
| ۳ ساعت | پیاده‌سازی Navigation State و Back Stack |
| ۵ ساعت | پیاده‌سازی Navigation 3 و Custom SceneStrategy |
| ۵ ساعت | پیاده‌سازی UI و مدیریت Focus و D-Pad |
| ۳ ساعت | نوشتن Unit Test و بررسی حالت‌های خاص |
| ۲ ساعت | تکمیل README و مستندات |
| ۲ ساعت | تست نهایی و رفع باگ‌ها |

**زمان تحویل پیشنهادی: ۲۴ ساعت**

## ۳. طراحی Navigation و Focus

در Navigation State اطلاعات زیر نگهداری می‌شود:
- مسیر فعلی Navigation
- شناسه و موقعیت آیتم فوکوس‌شده
- لیست سطح فعلی
- اطلاعات موردنیاز Preview

### نمونه Back Stack

```text
1. Initial State
   Back Stack: [Settings]
   Current Level: Settings
   Focus: Network

2. Forward
   Back Stack: [Settings, Network]
   Current Level: Network
   Focus: Wi-Fi

3. Forward
   Back Stack: [Settings, Network, Wi-Fi]
   Current Level: Wi-Fi
   Focus: Home Network

4. Forward
   Back Stack: [Settings, Network, Wi-Fi, Home Network]
   Current Level: Home Network
   Focus: Connected

5. Press Back
   Back Stack: [Settings, Network, Wi-Fi]
   Current Level: Wi-Fi
   Focus: Home Network
```

با حرکت به جلو، آیتم انتخاب‌شده وارد مسیر Navigation می‌شود و با Back به سطح قبلی برمی‌گردیم و Focus روی آیتم قبلی بازیابی می‌شود.

## ۴. ریسک‌های اصلی

- **مدیریت Focus:** احتمال از دست رفتن Focus هنگام تغییر سطح وجود دارد. باید برای حل آن راهی پیدا کنم مثلا از شناسه‌های ثابت استفاده می‌کنم.
- **Navigation 3:** پیاده‌سازی Custom SceneStrategy بخش چالش‌برانگیز پروژه است؛ بنابراین از ابتدا آن را بررسی و اعتبارسنجی می‌کنم.
- **Back Stack:** برای اطمینان از صحت بازگشت بین سطوح، انتقال‌های Navigation را Test می‌کنم.
- **رفتار ریموت:** باید راهی برای تست تعاملات D-Pad روی Android TV Emulator پیدا کنم.
