# NaLa - Japanese learning toolbox 

A simple Android app that offers a set of tools to make the life o japanese learners easier, from simple dictionary features to specific words, sentences and kanji learning and review features, with a little help from **Natural language processing** models.

<a href="https://developer.android.com/jetpack"><img src="https://2.bp.blogspot.com/--asT-h3qn_s/X0aLtRWOesI/AAAAAAAAPkY/iOkd702WAts7_4dIXlzQhyiJWGaL5f9CgCLcBGAsYHQ/s1600/JetpackCompose_logo.png" width="200"/></a>
<br>

The [Jetpack Toolkit](https://developer.android.com/jetpack) was used to build the app, including the new Jetpack Compose for the UI.

<a href="https://pytorch.org/"><img src="https://miro.medium.com/max/691/0*xXUYOs5MWWenxoNz" width="200"/></a>
<br>

For the on-device neural language models, I used [pytorch mobile](https://pytorch.org/mobile/home/)

A [personal Web API](https://github.com/cr1m5onk1ng/semantic-search-api/tree/master) built with FastApi was used as a base for the semantic search service used in the app.
Working on an improved and more feature-rich version using AWS Lambda

The app makes also use of the incredibly useful [Jisho](https://jisho.org/) API for words lookup

<a href="https://jisho.org/"><img src="https://camo.githubusercontent.com/ecf2e1f38cf2d00aa5e9b7ae2cba32ab9865101567767328e04de495038672d4/687474703a2f2f6173736574732e6a6973686f2e6f72672f6173736574732f6a6973686f2d6c6f676f2d76344032782d373333303039316330373962396464353936303134303162303532623532653130333937383232316338666236663565323234303664383731666363373436612e706e67" width="200"/></a>
<br>


## ✨ Features

- [x] Look up words in the dictionary
- [x] Search for a Japanese word everywhere on your phone
- [x] Add sentences with a target word to study/review them
- [x] Starting from a sentence, search for the most similar/dissimilar sentences
- [x] Automatically organize your sentences/documents by category/topic
- [ ] Find the most frequent words that use a certain kanji
- [ ] Search for semantically related words saved locally
- [ ] Share words/sentences with AnkiDroid
- [ ] Import articles and other media from the web
- [ ] OCR functionality for non-textual content

## 📸 Screenshots

Home
 ---------------------------------
<img src="screens/app-screenshot-home.png" width="400">

| Word                             | Sentence                              |
| --------------------------------- | --------------------------------- |
| <img src="screens/app-screeenshot-word-search.png" width="400">  | <img src="screens/app-screenshot-share.png" width="400">  |
| <img src="screens/app-screenshot-word-detail-2.png" width="400">  | <img src="screens/app-screenshot-one-target-form.png" width="400">  |
| <img src="screens/app-screenshot-added-to-review.png" width="400">  | <img src="screens/app-screenshot-study-screen-music.png" width="400">  |
| <img src="screens/app-screenshot-kanji-detail.png" width="400">  | <img src="screens/app-screenshot-similar-sentences.png" width="400">  |
| <img src="screens/app-screenshot-word-reviews.png" width="400">  | <img src="screens/app-screenshot-sentence-reviews.png" width="400"> |
| <img src="screens/app-screenshot-article-lookup.png" width="400"> | <img src="screens/app-screenshot-settings.png" width="400"> | 

| Videos                             |
| <img src="screens/app-screenshot-video-first.png" width="400">  | <img src="screens/app-screenshot-video-second.png" width="400">  |
| <img src="screens/app-screenshot-video-third.png" width="400">  | <img src="screens/app-screenshot-video-second.png" width="400">  |
| <img src="screens/app-screenshot-drawer.png" width="400">  | <img src="screens/app-screenshot-video-second.png" width="400">  |

| Saved items                             |
| <img src="screens/app-screenshot-saved-videos.png" width="400">  | <img src="screens/app-screenshot-saved-articles.png" width="400">  |
## Author

**Mirco Cardinale**
[Personal website](https://mirco-cardinale-portfolio.herokuapp.com/)

## 🔖 LICENCE

[GPL](https://github.com/cr1m5onk1ng/nala_android_app/blob/dev/LICENSE)