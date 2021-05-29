package com.example.nala.ui.composables

import androidx.compose.foundation.clickable
import androidx.compose.foundation.layout.*
import androidx.compose.foundation.lazy.LazyColumn
import androidx.compose.foundation.lazy.LazyRow
import androidx.compose.foundation.shape.RoundedCornerShape
import androidx.compose.material.*
import androidx.compose.material.icons.Icons
import androidx.compose.material.icons.rounded.Add
import androidx.compose.runtime.Composable
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.draw.shadow
import androidx.compose.ui.graphics.Color
import androidx.compose.ui.text.TextStyle
import androidx.compose.ui.text.font.FontWeight
import androidx.compose.ui.text.style.TextDecoration
import androidx.compose.ui.unit.dp
import androidx.compose.ui.unit.sp
import androidx.constraintlayout.compose.ConstraintLayout
import androidx.navigation.NavController
import com.example.nala.domain.model.dictionary.DictionaryModel
import com.example.nala.domain.model.dictionary.Sense
import com.example.nala.domain.model.kanji.KanjiCollection
import com.example.nala.ui.theme.*
import kotlin.random.Random


@Composable
fun DictionaryDetailScreen(
    wordModel: DictionaryModel,
    sentence: String? = null,
    isLoading: Boolean,
    navController: NavController,
    kanjiDict: KanjiCollection,
    setCurrentKanji: (String) -> Unit,
    setCurrentStory: (String) -> Unit,
    unsetSharedWord: () -> Unit,
    addToReview: () -> Unit,
    loadWordReviews: () -> Unit,
    scaffoldState: ScaffoldState,
    showSnackbar: (ScaffoldState) -> Unit
) {

    Scaffold(
        scaffoldState = scaffoldState,
        snackbarHost =  {
            scaffoldState.snackbarHostState
        }
    ) { paddingValue ->
        if(isLoading){
            LoadingIndicator()
        }  else {
            ConstraintLayout(
                modifier = Modifier.padding(paddingValue)
            ) {
                Column(
                    modifier = Modifier
                        .fillMaxSize()
                        .padding(16.dp),
                ) {
                    Row(
                        modifier = Modifier
                            .fillMaxWidth()
                            .padding(top = 22.dp, start = 16.dp)
                    ){
                        BackButton(
                            navController = navController,
                            cleanupFunction = { unsetSharedWord() }
                        )
                    }
                    if(wordModel.word.isEmpty()) {
                        ErrorScreen(
                            text = "No word found in Jisho dictionary",
                            subtitle = "¯\\_(ツ)_/¯")
                    }
                    else{
                        LazyColumn{
                            item() {
                                DataSection(
                                    wordModel,
                                    sentence = sentence,
                                    navController,
                                    kanjiDict,
                                    setCurrentKanji,
                                    setCurrentStory,
                                    addToReview,
                                    loadWordReviews,
                                    scaffoldState,
                                    showSnackbar
                                )
                            }
                        }
                    }
                }
                val snackbar = createRef()
                DefaultSnackbar(
                    modifier = Modifier
                        .constrainAs(snackbar){
                            bottom.linkTo(parent.bottom)
                            start.linkTo(parent.start)
                            end.linkTo(parent.end)
                        },
                    snackbarHostState = scaffoldState.snackbarHostState,
                    onDismiss = {
                        scaffoldState.snackbarHostState.currentSnackbarData?.dismiss()
                    }
                )
            }
        }
    }
}

@Composable
fun DataSection(
    wordModel: DictionaryModel,
    sentence: String? = null,
    navController: NavController,
    kanjiDict: KanjiCollection,
    setCurrentKanji: (String) -> Unit,
    setCurrentStory: (String) -> Unit,
    addToReview: () -> Unit,
    loadWordReviews: () -> Unit,
    scaffoldState: ScaffoldState,
    onShowSnackbar: (ScaffoldState) -> Unit
) {
    Column(
        modifier = Modifier
            .fillMaxWidth(),
        horizontalAlignment = Alignment.CenterHorizontally
    ) {
        Spacer(modifier = Modifier.padding(vertical = 16.dp))
        WordSection(
            reading = wordModel.reading,
            word = wordModel.word,
            kanjiDict = kanjiDict,
            navController,
            setCurrentKanji,
            setCurrentStory,
        )
        Spacer(
            modifier = Modifier.padding(vertical=5.dp)
        )
        ButtonSection(addToReview, loadWordReviews, scaffoldState, onShowSnackbar)
        Spacer(modifier = Modifier.padding(vertical = 5.dp) )
        val isCommon: Boolean = wordModel?.common ?: false
        val isCommonTag: String = if(isCommon) "Common" else ""
        val jlpt = wordModel?.jlpt ?: ""
        val wordTags: MutableList<String> = mutableListOf()
        if (jlpt.isNotEmpty()) wordTags.add(jlpt)
        if(isCommonTag.isNotEmpty()) wordTags.add(isCommonTag)
        val tags = wordModel?.dataTags ?: listOf()
        val allTags = tags + wordTags
        TagRow(tags = allTags)
        SensesSection(wordModel?.senses ?: listOf())
    }
}

@Composable
fun WordSection(
    reading: String,
    word: String,
    kanjiDict: KanjiCollection,
    navController: NavController,
    setCurrentKanji: (String) -> Unit,
    setCurrentStory: (String) -> Unit,
    fromStudy: Boolean = false,
) {
    Column(
        modifier = Modifier
            .fillMaxWidth()
            .padding(start = 32.dp, top = 32.dp)
    ){
        Text(
            reading,
            style = TextStyle(
                fontFamily = Quicksand,
                fontSize = 16.sp,
                fontWeight = FontWeight.W500
            )
        )
        Spacer(
            modifier = Modifier.padding(vertical=2.dp)
        )
        KanjiRow(
            word,
            kanjiDict,
            navController,
            setCurrentKanji,
            setCurrentStory,
            fromStudy=fromStudy)
    }
}

@Composable
fun KanjiRow(
    kanjis: String,
    kanjiDict: KanjiCollection,
    navController: NavController,
    setCurrentKanji: (String) -> Unit,
    setCurrentStory: (String) -> Unit,
    fromStudy: Boolean = false,
) {
    Row() {
        for (kanji in kanjis) {
            val kanjiString = kanji.toString()
            val isKanji: Boolean = kanjiDict.kanjis.contains(kanjiString)
            Text(
                kanjiString,
                modifier = Modifier
                    .clickable(enabled = isKanji, onClick = {
                        setCurrentStory(kanjiString)
                        setCurrentKanji(kanjiString)
                        navController.navigate("kanji_detail_screen")
                    }),
                style = TextStyle(
                    fontSize = 46.sp,
                    fontWeight = FontWeight.Bold,
                    textDecoration = if (isKanji)
                        TextDecoration.Underline else
                            TextDecoration.None
                )
            )
        }
    }
}

@Composable
fun ButtonSection(
    addToReview: () -> Unit,
    loadWordReviews: () -> Unit,
    scaffoldState: ScaffoldState,
    onShowSnackbar: (ScaffoldState) -> Unit
) {
    Row(
        modifier = Modifier
            .padding(horizontal = 16.dp)
            .fillMaxWidth(),
       horizontalArrangement = Arrangement.Center
    ) {
        Button(
            onClick = {
                addToReview()
                onShowSnackbar(scaffoldState)
                loadWordReviews()
                      },
            modifier = Modifier
                .fillMaxWidth()
                .shadow(
                    elevation = 5.dp,
                    shape = RoundedCornerShape(18.dp)
                ),
            colors = ButtonDefaults.buttonColors(
                backgroundColor = Blue700
            ),
            shape = RoundedCornerShape(18.dp)
        ) {
            Row(
                modifier = Modifier.padding(8.dp),
                horizontalArrangement = Arrangement.Center,
                verticalAlignment = Alignment.CenterVertically
            ) {
                Icon(Icons.Rounded.Add, contentDescription = "add to review")
                Spacer(modifier=Modifier.padding(horizontal = 5.dp))
                Text("Add to review")
            }
        }
    }
}

@Composable
fun SensesSection(
    senses: List<Sense>
) {
    if (senses.isEmpty()) {
        Spacer(modifier = Modifier.padding(0.dp))
    }
    else {
        Column(
            modifier = Modifier.fillMaxWidth()
        ) {
            for(sense in senses) {
                val pos = sense.partsOfSpeech ?: listOf()
                val tags = sense.tags ?: listOf()
                val allTags = pos + tags
                TagRow(tags = allTags)
                DefinitionsRow(definitions = sense.englishDefinitions ?: listOf())
            }
        }
    }
}

@Composable
fun DefinitionsRow(definitions: List<String>) {
    Column(
        modifier = Modifier
            .fillMaxWidth()
            .padding(start = 32.dp)
    ){
        for (def in definitions) {
            Text(
                "${definitions.indexOf(def)+1}. $def",
                modifier = Modifier.padding(8.dp),
                style = TextStyle(
                    fontFamily = Quicksand,
                    fontSize = 26.sp,
                    fontWeight = FontWeight.W500
                )
            )
        }
    }
}

@Composable
fun TagRow(tags: List<String>) {
    if(tags.isEmpty()) {
        Spacer(modifier = Modifier.padding(0.dp))
    }
    else {
        Column(
            modifier = Modifier.fillMaxWidth()
        ) {
            LazyRow (
                modifier = Modifier
                    .height(60.dp)
                    .padding(start = 8.dp, end = 8.dp),
                verticalAlignment = Alignment.CenterVertically
            ){
                items(count = tags.size){ index ->
                    Tag(tags[index])
                }
            }
        }
    }
}

@Composable
fun Tag(tag: String) {
    Card(
        modifier = Modifier
            .height(60.dp)
            .padding(8.dp)
            .shadow(
                elevation = 5.dp,
                shape = RoundedCornerShape(12.dp)
            ),
        backgroundColor = TEXT_COLORS[Random.nextInt(0, TEXT_COLORS.size)],
        elevation = 5.dp,
        shape = RoundedCornerShape(12.dp),

    ) {
        Text(
            tag,
            modifier = Modifier.padding(8.dp),
            style = TextStyle(
                fontSize = 16.sp,
                fontFamily = Quicksand,
                fontWeight = FontWeight.W500,
                color = Color.Black
            )
        )
    }
}



