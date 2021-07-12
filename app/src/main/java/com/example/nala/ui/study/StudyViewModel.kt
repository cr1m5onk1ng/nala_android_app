package com.example.nala.ui.study

import android.util.Log
import androidx.compose.runtime.MutableState
import androidx.compose.runtime.mutableStateOf
import androidx.lifecycle.ViewModel
import androidx.lifecycle.viewModelScope
import com.example.nala.domain.model.dictionary.DictionaryModel
import com.example.nala.domain.model.kanji.KanjiModel
import com.example.nala.repository.DictionaryRepository
import com.example.nala.repository.KanjiRepository
import com.example.nala.repository.ReviewRepository
import com.example.nala.ui.DataState
import dagger.hilt.android.lifecycle.HiltViewModel
import kotlinx.coroutines.delay
import kotlinx.coroutines.launch
import javax.inject.Inject

@HiltViewModel
class StudyViewModel @Inject constructor(
    private val dictionaryRepository: DictionaryRepository,
    private val reviewRepository: ReviewRepository,
    private val kanjiRepository: KanjiRepository,
) : ViewModel() {

    val studyContextState: MutableState<DataState<String>> = mutableStateOf(DataState.Initial(""))

    val similarSentencesState: MutableState<DataState<List<String>>> =
        mutableStateOf(DataState.Initial(listOf()))

    val targetWordState: MutableState<DataState< DictionaryModel>> =
        mutableStateOf(DataState.Initial( DictionaryModel.Empty()))

    val targetWordKanjisState: MutableState<DataState<List<String>>> =
        mutableStateOf(DataState.Initial( listOf()))

    val selectedWord: MutableState<String> = mutableStateOf("")


    //TODO
    fun loadSimilarSentences() {

        viewModelScope.launch {
            similarSentencesState.value = DataState.Loading
            delay(2000)
            val similarSentences = listOf(
                "ボーカル・ギター担当。青森県上北郡横浜町出身[4]。デビュー当時はむつ市に在住[5]して活動していたが、2014年頃より青森市在住[6]。\n" +
                        "音楽を始めたきっかけは、小学校6年生の時に姉が聴いていたTM NETWORKに憧れて",
                "ギター・プログラミング・楽器演奏・サウンドプロデュース担当[17]。バンドマスターおよびギターサポートライブ演奏[18]。LIVE TOUR 2017「メッセージボトル」福岡市民会館公演を最後にライブ活動を休止。",
                "※以降、バンド名をあまざらしに変更。2008年、現在の所属事務所であるレインボーエンタテインメントのA&Rが、担当アーティストのキャンペーンのために青森県内のFMラジオ局を訪れた際、番組内の地元インディーズバンドの音源を流すコーナーで楽曲を聴いたことがきっかけとなり",
                "同日、中島美嘉のトリビュートアルバム『MIKA NAKASHIMA TRIBUTE』リリース、本作で秋田の弾き語りによる「僕が死のうと思ったのは」のセルフカバーが初のCD音源化。",
                "自身初のベストアルバム『メッセージボトル』リリース。同年1月7日より放送のテレビ東京系深夜ドラマ「銀と金」の主題歌に選ばれた新曲「ヒーロー」のほか、限定盤にはあまざらし名義のミニアルバム『光、再考』などを収録",
            )
            if(similarSentences.isEmpty()) {
                similarSentencesState.value = DataState.Error("No sentences found")
            } else {
               similarSentencesState.value = DataState.Success(similarSentences)
            }
        }
    }

    fun setSelectedWord(text: String){
        selectedWord.value = text
    }

    fun unsetSelectedWord() {
        selectedWord.value = ""
    }

    fun setStudyContext(sentence: String?) {
        studyContextState.value = DataState.Loading
        val currentStudyContext = sentence ?: ""
        if(currentStudyContext.isEmpty()) {
            studyContextState.value = DataState.Error("Couldn't fetch target sentence")
        } else {
            studyContextState.value = DataState.Success(currentStudyContext)
        }
    }

    fun setStudyTargetWord(word: String) {
        viewModelScope.launch {
            targetWordState.value = DataState.Loading
            val cachedWord = reviewRepository.getWordReview(word)
            val wordModel = if(cachedWord == null){
                dictionaryRepository.search(word)
            } else {
                reviewRepository.getWordData(cachedWord)
            }
            if(wordModel.isEmpty()) {
                targetWordState.value = DataState.Error("Couldn't fetch target word")
            } else {
                targetWordState.value = DataState.Success(wordModel)
            }
        }
    }

    fun setCurrentWordKanjis(word: String) {
        viewModelScope.launch {
            targetWordKanjisState.value = DataState.Loading
            val kanjiList = mutableListOf<String>()
            for(k in word) {
                val kanji = kanjiRepository.getKanjiModel(k.toString())
                if (!kanji.isEmpty()){
                    kanjiList.add(kanji.kanji)
                }
            }
            targetWordKanjisState.value = DataState.Success(kanjiList)
        }
    }

}