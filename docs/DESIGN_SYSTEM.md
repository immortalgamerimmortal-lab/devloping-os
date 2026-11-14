# Serein design system

## Visual language

Serein uses an original, quiet visual identity: mineral green, warm paper, and dusk-blue surroundings, with a choice of restrained teal, blue, plum, or ochre accents. The atmosphere comes from overlapping color fields, soft shadows, rounded translucent cards, and restrained highlights—not copied device branding or system screenshots.

## Color and contrast

- Light surfaces use soft paper (`#F4F6F5`), dark ink (`#18252D`), and muted mist (`#E1E9E7`).
- Dark surfaces use deep charcoal-blue (`#11191F`) and layered slate.
- Accent selection is user-controlled in Settings for the current preview session.
- High contrast raises text and outline contrast. Reduce transparency makes surfaces nearly opaque to preserve legibility.
- No photographic wallpaper or third-party visual assets are bundled.

## Typography and spacing

- Compose Material typography provides familiar Android scale and platform font fallback.
- Screen titles use a clear display-to-title hierarchy; supporting and metadata text use smaller, secondary styles.
- Common spacing steps are 4, 8, 12, 16, and 20 dp. Cards use 16 dp interior padding and approximately 24 dp corners.
- Larger text increases home-app labels and app-drawer names; layout uses scrollable content where lists may exceed the viewport.

## Surfaces and icons

- Surfaces use a translucent Material color over an ambient gradient with a soft elevation shadow.
- Reduce transparency switches to opaque-like cards; the prototype intentionally does not stack live blur effects.
- Material rounded vector icons share consistent size and tint. App glyphs use a common rounded tile and individual color.
- Status indicators and preview labels identify sample content; no OEM or third-party logo assets are used.

## Motion and interaction

- Screen changes use short, spring-tuned crossfades.
- Home pages respond to horizontal swipes; long-press and drag reorder the visible grid; tap launches a preview app.
- Quick settings expose editable tile order, local toggle state, and local slider state.
- Reduce motion chooses a short fade instead of spring-driven transition and removes icon-lift emphasis.
- Screen content follows safe-area and navigation-bar insets and uses scrollable lists for compact screens.
- This prototype does not implement wallpaper parallax or continuous blur; avoiding expensive or distracting effects favors battery and readability.

## Accessibility checks still required

Run TalkBack and Android Accessibility Scanner on small and large emulator profiles. Verify focus traversal, touch targets, system font scaling, contrast, gesture alternatives, landscape layouts, cutouts, and switch semantics. The app's accessibility toggles are local presentation preferences, not Android global accessibility settings.
