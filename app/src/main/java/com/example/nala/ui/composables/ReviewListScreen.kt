package com.example.nala.ui.composables

import androidx.compose.foundation.BorderStroke
import androidx.compose.foundation.clickable
import androidx.compose.foundation.layout.*
import androidx.compose.foundation.lazy.LazyColumn
import androidx.compose.foundation.shape.RoundedCornerShape
import androidx.compose.material.*
import androidx.compose.material.icons.Icons
import androidx.compose.material.icons.rounded.CheckCircle
import androidx.compose.runtime.Composable
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.graphics.Color
import androidx.compose.ui.text.TextStyle
import androidx.compose.ui.text.font.FontWeight
import androidx.compose.ui.unit.dp
import androidx.compose.ui.unit.sp
import androidx.navigation.NavController
import androidx.compose.runtime.MutableState
import androidx.compose.runtime.mutableStateOf
import androidx.compose.runtime.remember
import androidx.compose.ui.text.SpanStyle
import androidx.compose.ui.text.style.TextDecoration
import androidx.constraintlayout.compose.ConstraintLayout
import com.example.nala.db.models.review.KanjiReviewModel
import com.example.nala.db.models.review.WordReviewModel
import com.example.nala.domain.model.review.ReviewCategory
import com.example.nala.domain.model.review.SentenceReviewModel
import com.example.nala.ui.theme.*

val specialReviewStyle = SpanStyle(
    fontWeight = FontWeight.Bold,
    fontSize = 22.sp,
    fontFamily = Quicksand,
    color = Color.Black,
    textDecoration = TextDecoration.Underline,
)
val normalReviewStyle = SpanStyle(
    fontWeight = FontWeight.Light,
    fontSize = 20.sp,
    fontFamily = Quicksand,
    color = Color.Black,
)

@Composable
fun ReviewListScreen(
    isLoading: Boolean,
    selectedCategory: ReviewCategory,
    setCategory: (ReviewCategory) -> Unit,
    wordReviewItems: List<WordReviewModel>,
    sentenceReviewItems: List<SentenceReviewModel>,
    kanjiReviewItems: List<KanjiReviewModel>,
    loadWordReviews: () -> Unit,
    loadSentenceReviews: () -> Unit,
    loadKanjiReviews: () -> Unit,
    setWordItem: (WordReviewModel) -> Unit,
    setSentenceItem: (String) -> Unit,
    setTargetWordItem: (String) -> Unit,
    setKanjiItem: (String) -> Unit,
    removeWordReview: (WordReviewModel) -> Unit,
    removeSentenceReview: (SentenceReviewModel) -> Unit,
    removeKanjiReview: (KanjiReviewModel) -> Unit,
    dismissWordReview: (String) -> Unit,
    dismissSentenceReview: (String) -> Unit,
    dismissKanjiReview: (String) -> Unit,
    isHomeSelected: Boolean,
    isReviewsSelected: Boolean,
    toggleHome: (Boolean) -> Unit,
    toggleReviews: (Boolean) -> Unit,
    updateWordReviewItem: (quality: Int, reviewItem: WordReviewModel) -> Unit,
    updateSentenceReviewItem: (quality: Int, sentenceReview: SentenceReviewModel) -> Unit,
    updateKanjiReviewItem: (quality: Int, kanjiReview: KanjiReviewModel) -> Unit,
    navController: NavController,
    scaffoldState: ScaffoldState,
    showSnackbar: (ScaffoldState) -> Unit
) {
    // the default reviews displayed are word reviews


    Scaffold(
        bottomBar = {
            BottomBar(
                navController,
                isHomeSelected,
                isReviewsSelected,
                toggleHome,
                toggleReviews
            )
        }
    ) { innerPadding ->
        ConstraintLayout(
            modifier = Modifier.padding(innerPadding)
        ){
            Column(
                modifier = Modifier
                    .fillMaxSize()
                    .padding(16.dp)
            ) {
                Row(
                    modifier = Modifier
                        .fillMaxWidth()
                        .padding(top = 16.dp, start = 16.dp),
                    verticalAlignment = Alignment.CenterVertically,
                ) {
                    /*
                    BackButton(
                        navController,
                        cleanupFunction = { toggleHome(true) }
                    ) */
                    Text(
                        "Reviews",
                        style = TextStyle(
                            fontFamily = Quicksand,
                            fontSize = 24.sp,
                            fontWeight = FontWeight.W500,
                            color = Color.Black
                        )
                    )
                }
                FilterButtonsRow(
                    setCategory = setCategory,
                    selectedCategory,
                    wordReviewItems = wordReviewItems,
                    sentenceReviewItems = sentenceReviewItems,
                    kanjiReviewItems = kanjiReviewItems,
                    loadKanjiReviews = loadKanjiReviews,
                    loadSentenceReviews = loadSentenceReviews,
                    loadWordReviews = loadWordReviews
                )
                if (isLoading) {
                    LoadingIndicator()
                } else{
                    LazyColumn(
                        modifier = Modifier
                            .padding(8.dp)
                    ) {
                        when(selectedCategory) {
                            ReviewCategory.Word -> {
                                if(wordReviewItems.isEmpty()) {
                                    item {ErrorScreen(text = "No word added to review", subtitle = "")}
                                }
                                else {
                                    items(count = wordReviewItems.size) { index ->
                                        WordReviewCard(
                                            wordReviewItems[index],
                                            setWordItem,
                                            updateWordReviewItem,
                                            removeWordReview,
                                            dismissWordReview,
                                            navController
                                        )
                                    }
                                }
                            }
                            ReviewCategory.Sentence -> {
                                if(sentenceReviewItems.isEmpty()) {
                                    item { ErrorScreen(text = "No sentence added to review", subtitle = "")}
                                }
                                else {
                                    items(count = sentenceReviewItems.size) { index ->
                                        SentenceReviewCard(
                                            sentenceReviewItems[index],
                                            setSentenceItem,
                                            setTargetWordItem,
                                            updateSentenceReviewItem,
                                            removeSentenceReview,
                                            dismissSentenceReview,
                                            navController
                                        )
                                    }
                                }
                            }
                            ReviewCategory.Kanji -> {
                                items(count = kanjiReviewItems.size) { index ->
                                    KanjiReviewCard(
                                        kanjiReviewItems[index],
                                        setKanjiItem,
                                        updateKanjiReviewItem,
                                        removeKanjiReview,
                                        dismissKanjiReview,
                                        navController
                                    )
                                }
                            }
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

@Composable
fun KanjiReviewCard(
    item: KanjiReviewModel,
    setKanjiItem: (String) -> Unit,
    updateKanjiReviewItem: (quality: Int, kanjiReview: KanjiReviewModel) -> Unit,
    removeKanjiReview: (KanjiReviewModel) -> Unit,
    dismissKanjiReview: (String) -> Unit,
    navController: NavController,
){
    val isEasyChecked = remember { mutableStateOf(false) }
    val isOkChecked = remember { mutableStateOf(false) }
    val isKoChecked = remember { mutableStateOf(false) }

    Card(
        modifier = Modifier
            .fillMaxWidth()
            .padding(3.dp)
            .clickable {
                setKanjiItem(item.kanji)
                navController.navigate("kanji_detail_screen")
            },
        backgroundColor = Color.White,
        shape = RoundedCornerShape(18.dp),
        border = BorderStroke(
            width = 1.dp,
            color = Color.LightGray,
        ),
        elevation = 5.dp,
    ) {

        //Content Column
        Column(
            modifier = Modifier
                .fillMaxSize()
                .padding(8.dp),
            verticalArrangement = Arrangement.SpaceBetween,
            horizontalAlignment = Alignment.CenterHorizontally,
        ) {
            Text(
                item.kanji,
                modifier = Modifier.padding(top=32.dp),
                style = TextStyle(
                    fontFamily = Quicksand,
                    fontWeight = FontWeight.W500,
                    fontSize = 46.sp,
                ),
            )
            //CHECKBOX SECTION
            CheckBoxRow(
                isEasyChecked = isEasyChecked,
                isOkChecked = isOkChecked,
                isKoChecked = isKoChecked,
                onDismiss = {
                    val quality = checkBoxLogic(
                        isOkChecked,
                        isKoChecked,
                        isEasyChecked,
                    )
                    updateKanjiReviewItem(quality, item)
                    dismissKanjiReview(item.kanji)

                }
            )
            //BUTTONS SECTION
            ButtonsRow(
                removeAction = {
                    removeKanjiReview(item)
                    dismissKanjiReview(item.kanji)
                }
            )
        }
    }
}

@Composable
fun SentenceReviewCard(
    item: SentenceReviewModel,
    setSentenceItem: (String) -> Unit,
    setTargetWordItem: (String) -> Unit,
    updateSentenceReviewItem: (quality: Int, sentenceReview: SentenceReviewModel) -> Unit,
    removeSentenceReview: (SentenceReviewModel) -> Unit,
    dismissSentenceReview: (String) -> Unit,
    navController: NavController,
){
    val isEasyChecked = remember { mutableStateOf(false) }
    val isOkChecked = remember { mutableStateOf(false) }
    val isKoChecked = remember { mutableStateOf(false) }

    val parts = item.sentence.split(Regex(item.targetWord))
    Card(
        modifier = Modifier
            .fillMaxWidth()
            .padding(16.dp)
            .clickable {
                setSentenceItem(item.sentence)
                setTargetWordItem(item.targetWord)
                navController.navigate("study_screen")
            },
        backgroundColor = Color.White,
        shape = RoundedCornerShape(18.dp),
        border = BorderStroke(
            width = 1.dp,
            color = Color.LightGray,
        ),
        elevation = 5.dp,
    ) {

        //Content Column
        Column(
            modifier = Modifier
                .fillMaxSize()
                .padding(
                    top = 20.dp,
                    bottom = 8.dp,
                    start = 16.dp,
                    end = 16.dp,
                ),
            verticalArrangement = Arrangement.SpaceBetween,
            horizontalAlignment = Alignment.CenterHorizontally,
        ) {
            CustomAnnotatedString(
                parts = parts,
                word = item.targetWord,
                textStyle = normalReviewStyle,
                specialStyle = specialReviewStyle
            )
            //CHECKBOX SECTION
            CheckBoxRow(
                isEasyChecked = isEasyChecked,
                isOkChecked = isOkChecked,
                isKoChecked = isKoChecked,
                onDismiss = {
                    val quality = checkBoxLogic(
                        isOkChecked,
                        isKoChecked,
                        isEasyChecked,
                    )
                    updateSentenceReviewItem(quality, item)
                    dismissSentenceReview(item.sentence)
                }
            )
            //BUTTONS SECTION
            ButtonsRow(
                removeAction = {
                    removeSentenceReview(item)
                    dismissSentenceReview(item.sentence)
                }
            )
        }

    }
}

@Composable
fun WordReviewCard(
    item: WordReviewModel,
    setWordItem: (WordReviewModel) -> Unit,
    updateWordReviewItem: (quality: Int, reviewItem: WordReviewModel) -> Unit,
    removeWordReview: (WordReviewModel) -> Unit,
    dismissWordReview: (String) -> Unit,
    navController: NavController,
    ) {

    val isEasyChecked = remember { mutableStateOf(false) }
    val isOkChecked = remember { mutableStateOf(false) }
    val isKoChecked = remember { mutableStateOf(false) }

    Card(
        modifier = Modifier
            .fillMaxWidth()
            .padding(3.dp)
            .clickable {
                setWordItem(item)
                navController.navigate("detail_screen")
            },
        backgroundColor = Color.White,
        shape = RoundedCornerShape(18.dp),
        border = BorderStroke(
            width = 1.dp,
            color = Color.LightGray,
        ),
        elevation = 5.dp,
    ) {

        //Content Column
        Column(
            modifier = Modifier
                .padding(8.dp),
            verticalArrangement = Arrangement.SpaceBetween,
            horizontalAlignment = Alignment.CenterHorizontally,
        ) {
            Text(
                item.word,
                modifier = Modifier.padding(top=32.dp),
                style = TextStyle(
                    fontFamily = Quicksand,
                    fontWeight = FontWeight.W500,
                    fontSize = 28.sp,
                ),
            )
            //CHECKBOX SECTION
            CheckBoxRow(
                isEasyChecked = isEasyChecked,
                isOkChecked = isOkChecked,
                isKoChecked = isKoChecked,
                onDismiss = {
                    val quality = checkBoxLogic(
                        isOkChecked,
                        isKoChecked,
                        isEasyChecked,
                    )
                    updateWordReviewItem(quality, item)
                    dismissWordReview(item.word)
                }
            )
            //BUTTONS SECTION
            ButtonsRow(
                removeAction = {
                    removeWordReview(item)
                    dismissWordReview(item.word)
                }
            )
        }
    }
}

fun checkBoxLogic(
    isOkChecked: MutableState<Boolean>,
    isKoChecked: MutableState<Boolean>,
    isEasyChecked: MutableState<Boolean>,
) : Int {
    var quality: Int = 0
    if(isOkChecked.value || isKoChecked.value || isEasyChecked.value) {
        quality = if(isOkChecked.value){
            4
        }else if(isEasyChecked.value){
            5
        } else {
            2
        }
        isOkChecked.value = false
        isKoChecked.value = false
        isEasyChecked.value = false
    }
    return quality
}

@Composable
fun CheckBoxRow(
    isEasyChecked: MutableState<Boolean>,
    isOkChecked: MutableState<Boolean>,
    isKoChecked: MutableState<Boolean>,
    onDismiss: () -> Unit,
) {
    Row(
        modifier = Modifier
            .fillMaxWidth()
            .padding(top = 8.dp),
        horizontalArrangement = Arrangement.Center
    ) {
        // CHECKBOX
        Row(
            horizontalArrangement = Arrangement.Center,
            verticalAlignment = Alignment.CenterVertically
        ){
            CustomCheckBox(
                label = "Easy",
                isChecked = isEasyChecked,
                onCheckedChange = { checked ->
                    if(checked) {
                        isEasyChecked.value = true
                        isOkChecked.value = false
                        isKoChecked.value = false
                    }
                    else{
                        isEasyChecked.value = false
                    }
                },
                colors = CheckboxDefaults.colors(Color.Blue)
            )
            CustomCheckBox(
                label = "OK",
                isChecked = isOkChecked,
                onCheckedChange = {
                    if(it) {
                        isOkChecked.value = true
                        isKoChecked.value = false
                        isEasyChecked.value = false
                    }
                    else{
                        isOkChecked.value = false
                    }
                },
                colors = CheckboxDefaults.colors(Color.Green)
            )
            CustomCheckBox(
                label = "KO",
                isChecked = isKoChecked,
                onCheckedChange = {
                    if(it) {
                        isKoChecked.value = true
                        isOkChecked.value = false
                        isEasyChecked.value = false
                    }
                    else{
                        isKoChecked.value = false
                    }
                },
                colors = CheckboxDefaults.colors(Color.Red)
            )
            IconButton(
                onClick = onDismiss
            ) {
                Icon(
                    Icons.Rounded.CheckCircle,
                    modifier = Modifier
                        .size(22.dp),
                    contentDescription = "Ok",
                    tint = LightBlue
                )
            }
        }
    }
}

@Composable
fun CustomCheckBox(
    label: String,
    isChecked: MutableState<Boolean>,
    onCheckedChange: (Boolean) -> Unit,
    colors: CheckboxColors
) {
    Row(modifier = Modifier.padding(horizontal = 8.dp)) {
        Checkbox(
            checked = isChecked.value,
            onCheckedChange = onCheckedChange,
            enabled = true,
            colors = colors
        )
        Text(
            text = label,
            style = TextStyle(
                fontFamily = Quicksand,
                fontSize = 14.sp,
                fontWeight = FontWeight.W500
            )
        )
    }
}

@Composable
fun ButtonsRow(
    removeAction: () -> Unit,
){
    Row(
        modifier = Modifier
            .fillMaxWidth()
            .padding(2.dp),
        horizontalArrangement = Arrangement.End,
        verticalAlignment = Alignment.CenterVertically
    ) {
        CustomTextButton(
            text = "Remove",
            onClick = removeAction,
        )
    }
}

@Composable
fun FilterButtonsRow(
    setCategory: (ReviewCategory) -> Unit,
    selectedCategory: ReviewCategory,
    wordReviewItems: List<WordReviewModel>,
    sentenceReviewItems: List<SentenceReviewModel>,
    kanjiReviewItems: List<KanjiReviewModel>,
    loadWordReviews: () -> Unit,
    loadSentenceReviews: () -> Unit,
    loadKanjiReviews: () -> Unit
) {
    Row(
        modifier = Modifier
            .fillMaxWidth()
            .padding(8.dp),
        horizontalArrangement = Arrangement.End,
        verticalAlignment = Alignment.CenterVertically
    ) {
        TagButton(
            text = "Word",
            textWeight = if (selectedCategory == ReviewCategory.Word) FontWeight.W500
                            else FontWeight.Light,
            height = 40.dp,
            width = 90.dp,
            backgroundColor = if (selectedCategory == ReviewCategory.Word) LightGreen
                                else Color.White,
            onClick = {
                setCategory(ReviewCategory.Word)
                if(wordReviewItems.isEmpty()) {
                    loadWordReviews()
                }
            }
        )
        TagButton(
            text = "Sentence",
            textWeight = if (selectedCategory == ReviewCategory.Sentence) FontWeight.W500
                            else FontWeight.Light,
            height = 40.dp,
            width = 90.dp,
            backgroundColor = if (selectedCategory == ReviewCategory.Sentence) LightBlue
                                else Color.White,
            onClick = {
                setCategory(ReviewCategory.Sentence)
                if(sentenceReviewItems.isEmpty()) {
                    loadSentenceReviews()
                }
            }
        )
        TagButton(
            text = "Kanji",
            textWeight = if (selectedCategory == ReviewCategory.Kanji) FontWeight.W500
                            else FontWeight.Light,
            height = 40.dp,
            width = 90.dp,
            backgroundColor = if (selectedCategory == ReviewCategory.Kanji) LightYellow
                                else Color.White,
            onClick = {
                setCategory(ReviewCategory.Kanji)
                if(kanjiReviewItems.isEmpty()) {
                    loadKanjiReviews()
                }
            }
        )
    }
}

