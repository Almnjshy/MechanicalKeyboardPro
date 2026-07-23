# Mechanical Keyboard Pro

## تصحيح مهم: التطبيق أصبح لوحة مفاتيح نظام (IME) أولاً

المفهوم الأساسي تصحّح: هذا ليس تطبيق تحكم عن بعد بالحاسوب فقط - إنه
**لوحة مفاتيح أندرويد حقيقية (Input Method Editor)** تحل محل لوحة المفاتيح
الافتراضية وتعمل في أي تطبيق (متصفح، دردشة، Termux، ألعاب...) بدون أي اتصال
بحاسوب. الاتصال بالحاسوب أصبح **ميزة إضافية اختيارية**.

## كيف تفتحه وتجربه
1. افتح المجلد في Android Studio ودع Gradle يزامن.
2. شغّل على جهاز/محاكي Android 8+ (المفضّل API 28+ لدعم Bluetooth HID الكامل).
3. أول تشغيل: Splash → شاشة رئيسية (Home) فيها زرّان:
   - **"تفعيل لوحة المفاتيح من إعدادات أندرويد"** → يفتح
     `Settings.ACTION_INPUT_METHOD_SETTINGS` لتفعيل "Mechanical Keyboard Pro".
   - **"اختيارها كلوحة المفاتيح الحالية"** → يفتح منتقي لوحات المفاتيح.
4. بعدها افتح أي تطبيق فيه حقل كتابة - تظهر اللوحة تلقائيًا وتكتب مباشرة.
5. زر إضافي اختياري في الشاشة الرئيسية يفتح تدفق ربط الحاسوب (Bluetooth HID)
   القديم كـ preview/محاكاة للوضع الثانوي.

## بنية المجلدات المحدّثة

```
app/src/main/java/com/mkpro/keyboard/
├── ime/
│   ├── KeyboardService.kt      # InputMethodService - المنتج الأساسي الآن
│   └── ImeLifecycleOwner.kt    # يمنح ComposeView دورة حياة داخل IME
├── core/
│   ├── keyboard/    # KeyModel + StandardLayout (EN) + ArabicLayout (AR) + PcKeysLayout
│   ├── layers/       # LayerManager: English / العربية / PC Keys (+ Programming/Gaming/Macros لاحقًا)
│   ├── connection/   # كما هو - Bluetooth HID الحقيقي، الآن "ميزة اختيارية" فقط
│   ├── macro/ profiles/ rgb/ settings/   # كما هي
├── ui/
│   ├── screens/home/       # الشاشة الرئيسية الجديدة (تفعيل اللوحة)
│   ├── screens/keyboard/   # KeyboardIme.kt (اللوحة الفعلية داخل IME) + KeyboardScreen.kt (معاينة وضع الحاسوب)
│   ├── screens/connection/ splash/   # كما هي - جزء من الوضع الاختياري
│   └── components/ theme/ navigation/   # كما هي
```

## كيف تعمل لوحة المفاتيح فعليًا (IME)
- `KeyboardService` يسجَّل في `AndroidManifest.xml` كـ `<service>` بصلاحية
  `BIND_INPUT_METHOD` + `res/xml/method.xml` (subtypes: English, العربية).
- يستضيف `ComposeView` عبر `ImeLifecycleOwner` (IME ليس LifecycleOwner افتراضيًا).
- `onKeyPressed(key)` يوزّع الضغطة حسب نوعها:
  - حروف/أرقام إنجليزية → `InputConnection.commitText` مع مراعاة Shift/Caps Lock.
  - حروف عربية (`ArabicLayout`, action = TEXT_INSERT) → `commitText` مباشرة.
  - مفاتيح PC (`PcKeysLayout`, action = PC_KEY_EVENT) → `InputConnection.sendKeyEvent`
    بأكواد `KeyEvent.KEYCODE_*` حقيقية (ESC, Insert, Home, Page Up...).
  - Ctrl/Alt/Win + مفتاح → يُبنى `metaState` ويُرسل كاختصار حقيقي (مثل Ctrl+C
    داخل Termux أو أي تطبيق يدعمها).
  - مفتاح اللغة 🌐 → `LayerManager.cycleLanguage()` للتبديل الفوري إنجليزي/عربي.

## الحالة الحالية مقابل ترتيب الأولويات المطلوب
1. ✅ IME أندرويد يعمل فعليًا (كتابة حقيقية في أي تطبيق).
2. ✅ واجهة احترافية (نفس Theme/KeyCap/CommandBar من قبل، بارتفاع ثابت مناسب لـ IME).
3. ✅ دعم مفاتيح PC الأساسية (ESC/TAB/CTRL/ALT/WIN/F1-F12/الأسهم/Home/End/Insert/Delete/PageUp/PageDown).
4. 🔶 نظام الطبقات: 3 طبقات جاهزة (English/العربية/PC Keys)؛ Programming/Gaming/Macros
   لم تُبنَ بعد - تُضاف بنفس الطريقة (ملف Layout جديد + تسجيله في LayerManager).
5. ⬜ التخصيص (تصميم اللوحة بالسحب والإفلات) - لم يبدأ.
6. 🔶 محرك الماكرو (`MacroEngine`) موجود من قبل لكنه غير مربوط بعد بمفاتيح IME.
7. ⬜ تأثيرات RGB الفعلية على المفاتيح (البنية `RgbController` جاهزة، الربط بصريًا لم يتم).
8. ✅ وضع الاتصال بالحاسوب (Bluetooth HID حقيقي) موجود من قبل، أصبح الآن مسارًا
   اختياريًا واضحًا بدل أن يكون الأساس.
