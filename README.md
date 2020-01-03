# Manglish

A malayalam to manglish converter Android app.

[More about this app](https://subinsb.com/manglish)

[Donate](https://subinsb.com/malayalam-to-manglish-converter/#donate)

[<img src="https://fdroid.gitlab.io/artwork/badge/get-it-on.png"
     alt="Get it on F-Droid"
     height="80">](https://f-droid.org/packages/subins2000.manglish/)
[<img src="https://play.google.com/intl/en_us/badges/images/generic/en-play-badge.png"
     alt="Get it on Google Play"
     height="80">](https://play.google.com/store/apps/details?id=subins2000.manglish)

## LICENSE

```
Manglish, malayalam to manglish converter
Copyright (C) 2019 Subin Siby

This program is free software: you can redistribute it and/or modify
it under the terms of the GNU General Public License as published by
the Free Software Foundation, version 3 of the License

This program is distributed in the hope that it will be useful,
but WITHOUT ANY WARRANTY; without even the implied warranty of
MERCHANTABILITY or FITNESS FOR A PARTICULAR PURPOSE.  See the
GNU General Public License for more details.

You should have received a copy of the GNU General Public License
along with this program. If not, see <https://www.gnu.org/licenses/>.
```

[GNU GPL-3.0-only](https://spdx.org/licenses/GPL-3.0-only)

## Publishing

[fastlane supply](https://docs.fastlane.tools/actions/supply/) is used to publish to Google Play. This metadata is also [used by F-Droid](https://f-droid.org/en/docs/All_About_Descriptions_Graphics_and_Screenshots/).

See `fastlane/` folder.

* Update version in `app/build.gradle` file
* Add changelog
* Update screenshots
* Make release APK

```
fastlane supply --apk app/release/app-release.apk --json-key api-file.json --track production --rollout 0.5 --package_name subins2000.manglish
```
