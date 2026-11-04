# November 3 evening bug reports

The November 3 review found 13 open reports among 47 bug and crash reports. All
13 came from Android and recorded the shared `1.7.17` asset version. That value
cannot establish their source revision. Each request was checked against `ad24ca5d`.

| Report ID prefix | Change |
| --- | --- |
| `a56a9c81` | Pool and map clues finish their flight and update the HUD before the game advances. |
| `c1a618c7` | Rumor storms form in place with smoke and thunder. Each map copy opens the matching active rumor. Loading a save is silent. |
| `7104a96a`, `e26fdd2f` | Defeated monsters grow to 90% of the previous target size and scatter blood droplets with their fragments. |
| `735c9356` | Discarded monsters spiral into smoke and play a separate disappearance sound. |
| `57ccb9fd` | The current mystery opens from below and still clears the active question. |
| `8f49cc9b` | Clue and gate map impacts are louder. Orbiting omen tokens use the investigator action pip artwork and gold tint. |
| `001e7ffe`, `5c82eea0`, `0924d409` | Focus plays a fading om chant. Health and sanity have distinct pickup cues and a shared HUD landing. Omen changes have a new sound. |
| `c0ecabcc`, `188a722e` | Opening the reserve plays a cash register. A collected card quietly swishes toward its investigator. |
| `ab0e9238` | New reports carry the packaged revision and Android build identity. The report list displays the version and revision, including for CI builds. |

Audio sources and rebuild instructions are in [NOV3_AUDIO.md](../assets/sounds/NOV3_AUDIO.md).
