package com.example.nala.ui.study

import androidx.compose.runtime.MutableState
import androidx.compose.runtime.mutableStateOf
import androidx.lifecycle.ViewModel
import androidx.lifecycle.viewModelScope
import com.example.nala.domain.model.dictionary.DictionaryModel
import com.example.nala.repository.DictionaryRepository
import com.example.nala.repository.ReviewRepository
import dagger.hilt.android.lifecycle.HiltViewModel
import kotlinx.coroutines.delay
import kotlinx.coroutines.launch
import javax.inject.Inject

@HiltViewModel
class StudyViewModel @Inject constructor(
    private val dictionaryRepository: DictionaryRepository,
    private val reviewRepository: ReviewRepository,
) : ViewModel() {

    val currentStudyContext: MutableState<String> = mutableStateOf("")

    val currentStudyTargetWord: MutableState<DictionaryModel> = mutableStateOf(
        DictionaryModel.Empty()
    )
    //TODO(Substitute simple strings with appropriate model)
    val similarSentences: MutableState<List<String>> = mutableStateOf(listOf())

    val selectedWord: MutableState<String> = mutableStateOf("")

    val contextLoading: MutableState<Boolean> = mutableStateOf(true)

    val wordModelLoading: MutableState<Boolean> = mutableStateOf(true)

    val similarSentencesLoading: MutableState<Boolean> = mutableStateOf(false)

    //TODO
    fun loadSimilarSentences() {

        viewModelScope.launch {
            similarSentencesLoading.value = true
            delay(2000)
            similarSentences.value = listOf(
                "ボーカル・ギター担当。青森県上北郡横浜町出身[4]。デビュー当時はむつ市に在住[5]して活動していたが、2014年頃より青森市在住[6]。\n" +
                        "音楽を始めたきっかけは、小学校6年生の時に姉が聴いていたTM NETWORKに憧れて",
                "ギター・プログラミング・楽器演奏・サウンドプロデュース担当[17]。バンドマスターおよびギターサポートライブ演奏[18]。LIVE TOUR 2017「メッセージボトル」福岡市民会館公演を最後にライブ活動を休止。",
                "※以降、バンド名をあまざらしに変更。2008年、現在の所属事務所であるレインボーエンタテインメントのA&Rが、担当アーティストのキャンペーンのために青森県内のFMラジオ局を訪れた際、番組内の地元インディーズバンドの音源を流すコーナーで楽曲を聴いたことがきっかけとなり",
                "同日、中島美嘉のトリビュートアルバム『MIKA NAKASHIMA TRIBUTE』リリース、本作で秋田の弾き語りによる「僕が死のうと思ったのは」のセルフカバーが初のCD音源化。",
                "自身初のベストアルバム『メッセージボトル』リリース。同年1月7日より放送のテレビ東京系深夜ドラマ「銀と金」の主題歌に選ばれた新曲「ヒーロー」のほか、限定盤にはあまざらし名義のミニアルバム『光、再考』などを収録",
            )
            similarSentencesLoading.value = false
        }
    }

    fun setSelectedWord(text: String){
        selectedWord.value = text
    }

    fun setStudyContext(sentence: String?) {
        contextLoading.value = true
        currentStudyContext.value = sentence ?: ""
        similarSentences.value = listOf()
        contextLoading.value = false
    }

    fun setStudyTargetWord(word: String) {
        viewModelScope.launch {
            wordModelLoading.value = true
            var wordModel: DictionaryModel
            val cachedWord = reviewRepository.getWordReview(word)
            wordModel = if(cachedWord == null){
                dictionaryRepository.search(word)
            } else {
                reviewRepository.getWordData(cachedWord)
            }
            currentStudyTargetWord.value = wordModel
            wordModelLoading.value = false
        }
    }

}