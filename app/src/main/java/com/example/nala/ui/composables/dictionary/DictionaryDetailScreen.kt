package com.example.nala.ui.composables.dictionary

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
import androidx.compose.ui.res.stringResource
import androidx.compose.ui.text.TextStyle
import androidx.compose.ui.text.font.FontWeight
import androidx.compose.ui.text.style.TextDecoration
import androidx.compose.ui.unit.dp
import androidx.compose.ui.unit.sp
import androidx.constraintlayout.compose.ConstraintLayout
import androidx.navigation.NavController
import com.example.nala.R
import com.example.nala.domain.model.dictionary.DictionaryModel
import com.example.nala.domain.model.dictionary.Sense
import com.example.nala.domain.model.utils.DataState
import com.example.nala.domain.model.utils.ErrorType
import com.example.nala.ui.composables.BackButton
import com.example.nala.ui.composables.DefaultSnackbar
import com.example.nala.ui.composables.ErrorScreen
import com.example.nala.ui.composables.LoadingIndicator
import com.example.nala.ui.theme.*
import kotlin.random.Random

@Composable
fun DictionaryDetailScreen(
    searchState: DataState<DictionaryModel>,
    onRetry: () -> Unit,
    navController: NavController,
    wordKanjis: List<String>,
    setCurrentKanji: (String) -> Unit,
    setCurrentStory: (String) -> Unit,
    addToReview: (DictionaryModel) -> Unit,
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

        Column(modifier = Modifier.padding(paddingValue)) {
            Row(
                modifier = Modifier
                    .fillMaxWidth()
                    .padding(top = 22.dp, start = 16.dp)
            ) {
                BackButton(
                    navController = navController,
                )
            }
            when(searchState) {
                is DataState.Initial<*>, DataState.Loading -> {
                    LoadingIndicator()
                }
                is DataState.Error -> {
                    Column(
                        modifier = Modifier
                            .fillMaxSize()
                            .padding(16.dp),
                        horizontalAlignment = Alignment.CenterHorizontally,
                        verticalArrangement = Arrangement.Center,
                    ) {
                        when(searchState.type){
                            ErrorType.NETWORK_NOT_AVAILABLE -> {
                                ErrorScreen(
                                    text = stringResource(R.string.connection_not_available),
                                    subtitle = "¯\\_(ツ)_/¯",
                                    action = onRetry,
                                )
                            }
                            else -> {
                                ErrorScreen(
                                    text = stringResource(R.string.no_result_from_jisho),
                                    subtitle = "¯\\_(ツ)_/¯"
                                )
                            }
                        }
                    }
                }
                is DataState.Success<DictionaryModel> -> {
                    ConstraintLayout(
                        modifier = Modifier.padding(paddingValue)
                    ) {
                        Column(
                            modifier = Modifier
                                .fillMaxSize()
                                .padding(16.dp),
                        ) {
                            LazyColumn{
                                item{
                                    DataSection(
                                        searchState.data,
                                        navController,
                                        wordKanjis,
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
                        val snackbar = createRef()
                        DefaultSnackbar(
                            modifier = Modifier
                                .padding(16.dp)
                                .constrainAs(snackbar) {
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
    }
}

@Composable
fun DataSection(
    wordModel: DictionaryModel,
    navController: NavController,
    wordKanjis: List<String>,
    setCurrentKanji: (String) -> Unit,
    setCurrentStory: (String) -> Unit,
    addToReview: (DictionaryModel) -> Unit,
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
            wordKanjis = wordKanjis,
            navController,
            setCurrentKanji,
            setCurrentStory,
        )
        Spacer(
            modifier = Modifier.padding(vertical=5.dp)
        )
        ButtonSection(wordModel, addToReview, loadWordReviews, scaffoldState, onShowSnackbar)
        Spacer(modifier = Modifier.padding(vertical = 5.dp) )
        val isCommon: Boolean = wordModel.common ?: false
        val isCommonTag: String = if(isCommon) stringResource(R.string.common_tag) else ""
        val jlpt = wordModel.jlpt ?: ""
        val wordTags: MutableList<String> = mutableListOf()
        if (jlpt.isNotEmpty()) wordTags.add(jlpt)
        if(isCommonTag.isNotEmpty()) wordTags.add(isCommonTag)
        val tags = wordModel.dataTags
        val allTags = tags + wordTags
        TagRow(tags = allTags)
        SensesSection(wordModel.senses)
    }
}

@Composable
fun WordSection(
    reading: String,
    word: String,
    wordKanjis: List<String>,
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
            wordKanjis,
            navController,
            setCurrentKanji,
            setCurrentStory,
        )
    }
}

@Composable
fun KanjiRow(
    kanjis: String,
    wordKanjis: List<String>,
    navController: NavController,
    setCurrentKanji: (String) -> Unit,
    setCurrentStory: (String) -> Unit,
) {
    Row() {
        for (kanji in kanjis) {
            val kanjiString = kanji.toString()
            val isKanji: Boolean = wordKanjis.contains(kanjiString)
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
    wordModel: DictionaryModel,
    addToReview: (DictionaryModel) -> Unit,
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
                addToReview(wordModel)
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
                Text(stringResource(R.string.add_to_review))
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
                    Tag(tags[index], index)
                }
            }
        }
    }
}

@Composable
fun Tag(
    tag: String,
    index: Int,
) {
    Card(
        modifier = Modifier
            .height(60.dp)
            .padding(8.dp)
            .shadow(
                elevation = 5.dp,
                shape = RoundedCornerShape(12.dp)
            ),
        backgroundColor = TEXT_COLORS[index % TEXT_COLORS.size],
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



