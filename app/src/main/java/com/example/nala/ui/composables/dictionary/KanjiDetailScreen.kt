package com.example.nala.ui.composables.dictionary

import androidx.compose.foundation.background
import androidx.compose.foundation.layout.*
import androidx.compose.foundation.lazy.LazyColumn
import androidx.compose.foundation.text.KeyboardActions
import androidx.compose.foundation.text.KeyboardOptions
import androidx.compose.material.*
import androidx.compose.material.Icon
import androidx.compose.material.icons.Icons
import androidx.compose.material.icons.rounded.Close
import androidx.compose.runtime.Composable
import androidx.compose.runtime.mutableStateOf
import androidx.compose.runtime.remember
import androidx.compose.ui.Alignment
import androidx.compose.ui.ExperimentalComposeUiApi
import androidx.compose.ui.Modifier
import androidx.compose.ui.graphics.Color
import androidx.compose.ui.platform.LocalSoftwareKeyboardController
import androidx.compose.ui.text.TextStyle
import androidx.compose.ui.text.font.FontWeight
import androidx.compose.ui.text.input.ImeAction
import androidx.compose.ui.text.input.KeyboardType
import androidx.compose.ui.text.input.TextFieldValue
import androidx.compose.ui.text.style.TextOverflow
import androidx.compose.ui.unit.TextUnit
import androidx.compose.ui.unit.dp
import androidx.compose.ui.unit.sp
import androidx.constraintlayout.compose.ConstraintLayout
import androidx.navigation.NavController
import com.example.nala.domain.model.kanji.KanjiModel
import com.example.nala.domain.model.utils.DataState
import com.example.nala.ui.composables.*
import com.example.nala.ui.theme.Quicksand

@ExperimentalComposeUiApi
@Composable
fun KanjiDetailScreen(
    kanjiSearchState: DataState<KanjiModel>,
    kanjiStoryState: DataState<String>,
    storyFormActive: Boolean,
    addKanjiToReview: (KanjiModel) -> Unit,
    updateKanjiStory: (String, String) -> Unit,
    setCurrentStory: (String) -> Unit,
    toggleStoryEditForm: (Boolean) -> Unit,
    navController: NavController,
    scaffoldState: ScaffoldState,
    onShare: (String?) -> Unit,
    showSnackbar: () -> Unit,
) {

    Scaffold { paddingValue ->
            Column(modifier = Modifier.padding(paddingValue)) {
                BackButton(
                    modifier = Modifier.padding(top = 22.dp, start = 16.dp),
                    iconColor = Color.Black,
                    navController = navController,
                )
                when(kanjiSearchState){
                    is DataState.Initial<*>, DataState.Loading -> {
                        LoadingIndicator()
                    }
                    is DataState.Error -> {
                        ErrorScreen(text = "Couldn't fetch kanji from dictionary", subtitle = "sorry dude")
                    }
                    is DataState.Success<KanjiModel> -> {
                        ConstraintLayout {
                            val kanji = kanjiSearchState.data
                            LazyColumn (
                                modifier = Modifier
                                    .fillMaxWidth()
                                    .padding(16.dp),
                            ){
                                item {
                                    Column {
                                        val tags: MutableList<String> = mutableListOf()
                                        var freq = kanji.freq ?: ""
                                        if(freq.isNotEmpty()) {
                                            freq = "frequency: $freq"
                                            tags.add(freq)
                                        }

                                        var jlpt = kanji.jlpt ?: ""
                                        if(jlpt.isNotEmpty()) {
                                            jlpt = "jlptn-$jlpt"
                                            tags.add(jlpt)
                                        }

                                        var grade = kanji.grade ?: ""
                                        if(grade.isNotEmpty()) {
                                            grade = "grade: $grade"
                                            tags.add(grade)
                                        }

                                        // SECTIONS
                                        Spacer(modifier = Modifier.padding(vertical = 16.dp))
                                        KanjiSection(kanji = kanji.kanji, onShare = onShare)
                                        Row(
                                            modifier = Modifier
                                                .fillMaxWidth()
                                                .padding(8.dp),
                                            horizontalArrangement = Arrangement.Center,
                                        ){
                                            AddToReviewButton(
                                                scaffoldState = scaffoldState,
                                                addToReview = {
                                                    addKanjiToReview(kanji)
                                                    showSnackbar()
                                                },
                                                onShowSnackbar = {showSnackbar()}
                                            )
                                        }
                                        TagRow(tags = tags)
                                        DetailsSection(kanji)
                                        StorySection(storyState = kanjiStoryState)
                                        if(storyFormActive){
                                            StoryEditForm(
                                                kanji = kanji.kanji,
                                                onEditStory = updateKanjiStory,
                                                setCurrentStory = setCurrentStory,
                                                toggleStoryEditForm = toggleStoryEditForm,
                                            )
                                        } else{
                                            Row(
                                                modifier = Modifier
                                                    .fillMaxWidth()
                                                    .padding(end = 16.dp),
                                                horizontalArrangement = Arrangement.End,
                                            ) {
                                                CustomTextButton(
                                                    text = "Edit story",
                                                    textSize = 16.sp,
                                                    onClick = {
                                                        toggleStoryEditForm(true)
                                                    }
                                                )
                                            }
                                        }

                                    }
                                }
                            }
                            val snackbar = createRef()
                            DefaultSnackbar(
                                modifier = Modifier
                                    .constrainAs(snackbar) {
                                        bottom.linkTo(parent.bottom)
                                        start.linkTo(parent.start)
                                        end.linkTo(parent.end)
                                    }
                                    .padding(8.dp),
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
fun KanjiSection(
    kanji: String,
    onShare: (String?) -> Unit,
) {
    Row(
        modifier = Modifier
            .padding(16.dp)
            .fillMaxWidth(),
        horizontalArrangement = Arrangement.Start,
        verticalAlignment = Alignment.CenterVertically,
    ) {
        Text(
            kanji,
            style = TextStyle(
                fontFamily = Quicksand,
                fontSize = 64.sp,
                fontWeight = FontWeight.Bold
            )
        )
        ShareButton(onShare = onShare, text = kanji, buttonSize = 28.dp)
    }
}

@Composable
fun StorySection(
    storyState: DataState<String>,
){
    when(storyState){
        is DataState.Initial, DataState.Loading -> {
            LoadingIndicator()
        }
        is DataState.Error -> {
            Text("No story found for this kanji. Add one!")
        }
        is DataState.Success<String> -> {
            Column (
                modifier = Modifier
                    .padding(horizontal = 16.dp)
                    .fillMaxWidth(),
            ){
                Spacer(modifier = Modifier
                    .height(1.dp)
                    .background(Color.LightGray)
                    .fillMaxWidth())
                Column(
                    modifier = Modifier.padding(8.dp)
                ) {
                    Column(
                        horizontalAlignment = Alignment.Start
                    ) {
                        Text(
                            modifier = Modifier.padding(bottom = 3.dp),
                            text = "Story",
                            style = MaterialTheme.typography.h6,
                        )
                    }
                    Column(
                        horizontalAlignment = Alignment.CenterHorizontally
                    ) {
                        Text(
                            storyState.data,
                            style = TextStyle(
                                fontFamily = Quicksand,
                                fontWeight = FontWeight.W500,
                                fontSize = 16.sp,
                                color = Color.Black
                            )
                        )
                    }
                }
                Spacer(modifier = Modifier
                    .height(1.dp)
                    .background(Color.LightGray)
                    .fillMaxWidth())
            }
        }
    }

}

@Composable
fun DetailsColumn(
    details: List<String>,
    title: String,
    fontSize: TextUnit
) {
    Column(
        modifier = Modifier
            .padding(3.dp)
    ){
        Text(
            title,
            style = TextStyle(
                fontFamily = Quicksand,
                fontSize = 20.sp,
                fontWeight = FontWeight.SemiBold,
                color = Color.Black
            )
        )
        for (detail in details) {
            Text(
                detail,
                modifier = Modifier
                    .padding(vertical = 8.dp),
                style = TextStyle(
                    fontFamily = Quicksand,
                    fontSize = fontSize,
                    fontWeight = FontWeight.W500,
                    color = Color.Black,
                ),
                overflow = TextOverflow.Ellipsis
            )
        }
    }
}

@Composable
fun DetailsSection(
    kanji: KanjiModel
){
    Row(
        modifier = Modifier
            .fillMaxWidth()
            .padding(16.dp),
        horizontalArrangement = Arrangement.SpaceBetween
    ) {
        DetailsColumn(details = kanji.meaning ?: listOf(), title = "Meanings", 20.sp)
        DetailsColumn(details = kanji.kunReadings ?: listOf(), title = "Kun", 16.sp)
        DetailsColumn(details = kanji.onReadings ?: listOf(), title = "On", 16.sp)
    }
}


@ExperimentalComposeUiApi
@Composable
fun StoryEditForm(
    kanji: String,
    onEditStory: (String, String) -> Unit,
    setCurrentStory: (String) -> Unit,
    toggleStoryEditForm: (Boolean) -> Unit,
) {
    val keyboardController = LocalSoftwareKeyboardController.current
    Row(
        Modifier
            .padding(8.dp)
            .fillMaxWidth(),
        horizontalArrangement = Arrangement.Center,
    ) {
        val textState = remember { mutableStateOf(TextFieldValue()) }
        TextField(
            modifier = Modifier.fillMaxWidth(),
            value = textState.value,
            onValueChange = { textState.value = it },
            trailingIcon = {
                IconButton(
                    onClick = {
                        toggleStoryEditForm(false)
                    }
                   ) {
                    Icon(
                        Icons.Rounded.Close,
                        contentDescription = "close",
                        tint = Color.Black
                    )
                }
            },
            keyboardOptions = KeyboardOptions(
                keyboardType = KeyboardType.Text,
                imeAction = ImeAction.Done,
            ),
            keyboardActions = KeyboardActions (
                onDone = {
                    onEditStory(kanji, textState.value.text)
                    setCurrentStory(kanji)
                    keyboardController?.hide()
                    toggleStoryEditForm(false)
                }
            )
        )
    }
}
