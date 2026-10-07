# Investigator preview

Render the four new investigator portraits with the production lock badges and labels, plus the expanded full-game offer. This does not open a saved game or initiate a purchase.

From the repository root with the project's JDK configured:

```powershell
.\gradlew.bat -I tools/investigator-preview/preview.gradle :desktop:previewInvestigators --offline --console=plain
```

Screenshots are written to `build/investigator-preview/`.
