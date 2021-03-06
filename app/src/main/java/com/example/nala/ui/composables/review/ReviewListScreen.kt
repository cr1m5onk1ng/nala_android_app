package com.example.nala.ui.composables.review

import androidx.compose.foundation.BorderStroke
import androidx.compose.foundation.clickable
import androidx.compose.foundation.layout.*
import androidx.compose.foundation.lazy.LazyColumn
import androidx.compose.foundation.lazy.LazyListState
import androidx.compose.foundation.shape.RoundedCornerShape
import androidx.compose.foundation.text.KeyboardActions
import androidx.compose.foundation.text.KeyboardOptions
import androidx.compose.material.*
import androidx.compose.material.icons.Icons
import androidx.compose.material.icons.outlined.DeleteOutline
import androidx.compose.material.icons.rounded.*
import androidx.compose.runtime.*
import androidx.compose.ui.Alignment
import androidx.compose.ui.ExperimentalComposeUiApi
import androidx.compose.ui.Modifier
import androidx.compose.ui.draw.alpha
import androidx.compose.ui.graphics.Color
import androidx.compose.ui.text.TextStyle
import androidx.compose.ui.text.font.FontWeight
import androidx.compose.ui.unit.dp
import androidx.compose.ui.unit.sp
import androidx.navigation.NavController
import androidx.compose.ui.platform.LocalSoftwareKeyboardController
import androidx.compose.ui.res.stringResource
import androidx.compose.ui.text.SpanStyle
import androidx.compose.ui.text.input.ImeAction
import androidx.compose.ui.text.input.KeyboardType
import androidx.compose.ui.text.style.TextDecoration
import androidx.constraintlayout.compose.ConstraintLayout
import com.example.nala.R
import com.example.nala.db.models.review.KanjiReviewCache
import com.example.nala.db.models.review.WordReviewModel
import com.example.nala.domain.model.review.ReviewCategory
import com.example.nala.domain.model.review.SentenceReviewModel
import com.example.nala.domain.model.utils.DataState
import com.example.nala.ui.composables.*
import com.example.nala.ui.theme.*
import com.example.nala.utils.extensions.isLastVisibleItem

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

@ExperimentalComposeUiApi
@Composable
fun ReviewListScreen(
    selectedCategory: ReviewCategory,
    setCategory: (ReviewCategory) -> Unit,
    wordReviewItems: DataState<List<WordReviewModel>>,
    sentenceReviewItems: DataState<List<SentenceReviewModel>>,
    kanjiReviewItems: DataState<List<KanjiReviewCache>>,
    wordsEndReached: Boolean,
    sentencesEndReached: Boolean,
    kanjisEndReached: Boolean,
    wordsListState: LazyListState,
    sentencesListState: LazyListState,
    kanjisListState: LazyListState,
    setWordItem: (WordReviewModel) -> Unit,
    setSentenceItem: (String) -> Unit,
    setTargetWordItem: (String) -> Unit,
    setKanjiItem: (String) -> Unit,
    removeWordReview: (WordReviewModel) -> Unit,
    removeSentenceReview: (SentenceReviewModel) -> Unit,
    removeKanjiReview: (KanjiReviewCache) -> Unit,
    addWordToReview: (WordReviewModel) -> Unit,
    addSentenceToReview: (SentenceReviewModel) -> Unit,
    addKanjiToReview: (KanjiReviewCache) -> Unit,
    isHomeSelected: Boolean,
    isReviewsSelected: Boolean,
    toggleHome: (Boolean) -> Unit,
    toggleReviews: (Boolean) -> Unit,
    updateWordReviewItem: (quality: Int, reviewItem: WordReviewModel) -> Unit,
    updateSentenceReviewItem: (quality: Int, sentenceReview: SentenceReviewModel) -> Unit,
    updateKanjiReviewItem: (quality: Int, kanjiReview: KanjiReviewCache) -> Unit,
    onSearch: (String) -> Unit,
    onRestore: () -> Unit,
    onShare: (String?) -> Unit,
    onUpdateWordReviews: () -> Unit,
    onUpdateSentenceReviews: () -> Unit,
    onUpdateKanjiReviews: () -> Unit,
    navController: NavController,
    scaffoldState: ScaffoldState,
    showSnackbar: (ScaffoldState) -> Unit
) {

    val searchOpen = remember { mutableStateOf(false) }
    val searchQuery = remember { mutableStateOf("") }

    val removedWordReview = remember { mutableStateOf<WordReviewModel?>(null) }
    val removedSentenceReview = remember { mutableStateOf<SentenceReviewModel?>(null) }
    val removedKanjiReview = remember { mutableStateOf<KanjiReviewCache?>(null) }

    Scaffold(
        topBar = {
             TopAppBar(
                 elevation = 0.dp,
                 title = {
                     Text(
                         text = stringResource(R.string.reviews_screen_title),
                         style = MaterialTheme.typography.h6
                     )
                 },
                 backgroundColor = MaterialTheme.colors.primary,
                 navigationIcon = {
                     BackButton(iconColor = Color.White, navController = navController)
                 },
                 actions = {
                     if(!searchOpen.value)
                         IconButton(onClick = { searchOpen.value = true }) {
                             Icon(
                                 imageVector = Icons.Rounded.Search,
                                 contentDescription = null,
                             )
                        } else
                         SearchField(
                             searchQuery = searchQuery,
                             searchOpen = searchOpen,
                             onSearch = onSearch,
                             onRestore = onRestore,
                         )
                 }
             )
        },
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
                FilterButtonsRow(
                    setCategory = setCategory,
                    selectedCategory,
                )
                Column(
                    modifier = Modifier
                        .padding(8.dp)
                        .fillMaxHeight()
                ) {
                    when(selectedCategory) {
                        ReviewCategory.Word -> {
                            when(wordReviewItems){
                                is DataState.Initial<*> -> {
                                    LoadingIndicator()
                                }
                                is DataState.Error -> {
                                    ErrorScreen(
                                        text = stringResource(R.string.no_words_in_review),
                                        subtitle = ""
                                    )
                                }
                                is DataState.Success<List<WordReviewModel>>  -> {
                                    val items = wordReviewItems.data
                                    LazyColumn(state = wordsListState) {
                                        items(count = items.size) { index ->
                                            WordReviewCard(
                                                items[index],
                                                removedWordReview,
                                                setWordItem,
                                                updateWordReviewItem,
                                                removeWordReview,
                                                onShare,
                                                scaffoldState,
                                                showSnackbar,
                                                navController,
                                            )
                                        }
                                        if(wordsListState.isLastVisibleItem(items.size - 1) &&
                                            wordsListState.isScrollInProgress && (!wordsEndReached)
                                        ) {
                                            onUpdateWordReviews()
                                        }
                                    }
                                }
                                is DataState.Loading -> {
                                    LoadingIndicator()
                                }
                            }
                        }
                        ReviewCategory.Sentence -> {
                            when(sentenceReviewItems){
                                is DataState.Initial<*>, DataState.Loading -> {
                                    LoadingIndicator()
                                }
                                is DataState.Error -> {
                                    ErrorScreen(
                                        text = stringResource(R.string.no_sentence_in_review),
                                        subtitle = ""
                                    )
                                }
                                is DataState.Success<List<SentenceReviewModel>>  -> {
                                    val items = sentenceReviewItems.data
                                    LazyColumn(state = sentencesListState) {
                                        items(count = items.size) { index ->
                                            SentenceReviewCard(
                                                items[index],
                                                removedSentenceReview,
                                                setSentenceItem,
                                                setTargetWordItem,
                                                updateSentenceReviewItem,
                                                removeSentenceReview,
                                                onShare,
                                                scaffoldState,
                                                showSnackbar,
                                                navController
                                            )
                                        }
                                        if(sentencesListState.isLastVisibleItem(items.size - 1) &&
                                            sentencesListState.isScrollInProgress && (!sentencesEndReached)
                                        ) {
                                            onUpdateSentenceReviews()
                                        }
                                    }
                                }
                            }
                        }
                        ReviewCategory.Kanji -> {
                            when(kanjiReviewItems){
                                is DataState.Initial<*>, DataState.Loading -> {
                                    LoadingIndicator()
                                }
                                is DataState.Error -> {
                                    ErrorScreen(
                                        text = stringResource(R.string.no_kanjis_in_review),
                                        subtitle = ""
                                    )
                                }
                                is DataState.Success<List<KanjiReviewCache>>  -> {
                                    val items = kanjiReviewItems.data
                                    LazyColumn(state = kanjisListState) {
                                        items(count = items.size) { index ->
                                            KanjiReviewCard(
                                                items[index],
                                                removedKanjiReview,
                                                setKanjiItem,
                                                updateKanjiReviewItem,
                                                removeKanjiReview,
                                                onShare,
                                                scaffoldState,
                                                showSnackbar,
                                                navController
                                            )
                                        }
                                        if(kanjisListState.isLastVisibleItem(items.size - 1) &&
                                            kanjisListState.isScrollInProgress && (!kanjisEndReached)
                                        ) {
                                            onUpdateKanjiReviews()
                                        }
                                    }
                                }
                            }
                        }
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
                },
                onAction = {
                    when(selectedCategory){
                        ReviewCategory.Word -> {
                            removedWordReview.value?.let{
                                addWordToReview(it)
                            }
                        }
                        ReviewCategory.Sentence -> {
                            removedSentenceReview.value?.let{
                                addSentenceToReview(it)
                            }
                        }
                        ReviewCategory.Kanji -> {
                            removedKanjiReview.value?.let{
                                addKanjiToReview(it)
                            }
                        }
                    }
                }
            )
        }
    }
}

@Composable
fun KanjiReviewCard(
    item: KanjiReviewCache,
    removedItem: MutableState<KanjiReviewCache?>,
    setKanjiItem: (String) -> Unit,
    updateKanjiReviewItem: (quality: Int, kanjiReview: KanjiReviewCache) -> Unit,
    removeKanjiReview: (KanjiReviewCache) -> Unit,
    onShare: (String?) -> Unit,
    scaffoldState: ScaffoldState,
    onShowSnackBar: (ScaffoldState) -> Unit,
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
                }
            )
            //BUTTONS SECTION
            ButtonsRow(
                text = item.kanji,
                removeAction = {
                    removedItem.value = item
                    removeKanjiReview(item)
                    onShowSnackBar(scaffoldState)
                },
                onShare = onShare,
            )
        }
    }
}

@Composable
fun SentenceReviewCard(
    item: SentenceReviewModel,
    removedItem: MutableState<SentenceReviewModel?>,
    setSentenceItem: (String) -> Unit,
    setTargetWordItem: (String) -> Unit,
    updateSentenceReviewItem: (quality: Int, sentenceReview: SentenceReviewModel) -> Unit,
    removeSentenceReview: (SentenceReviewModel) -> Unit,
    onShare: (String?) -> Unit,
    scaffoldState: ScaffoldState,
    onShowSnackBar: (ScaffoldState) -> Unit,
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
                }
            )
            //BUTTONS SECTION
            ButtonsRow(
                text = item.sentence,
                removeAction = {
                    removedItem.value = item
                    removeSentenceReview(item)
                    onShowSnackBar(scaffoldState)
                },
                onShare = onShare,
            )
        }
    }
}

@Composable
fun WordReviewCard(
    item: WordReviewModel,
    removedItem: MutableState<WordReviewModel?>,
    setWordItem: (WordReviewModel) -> Unit,
    updateWordReviewItem: (quality: Int, reviewItem: WordReviewModel) -> Unit,
    removeWordReview: (WordReviewModel) -> Unit,
    onShare: (String?) -> Unit,
    scaffoldState: ScaffoldState,
    onShowSnackBar: (ScaffoldState) -> Unit,
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
                }
            )
            //BUTTONS SECTION
            ButtonsRow(
                text = item.word,
                removeAction = {
                    removedItem.value = item
                    removeWordReview(item)
                    onShowSnackBar(scaffoldState)
                },
                onShare = onShare,
            )
        }
    }
}

fun checkBoxLogic(
    isOkChecked: MutableState<Boolean>,
    isKoChecked: MutableState<Boolean>,
    isEasyChecked: MutableState<Boolean>,
) : Int {
    var quality = 0
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
                label = stringResource(R.string.review_feedback_easy),
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
                label = stringResource(R.string.review_feedback_ok),
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
                label = stringResource(R.string.review_feedback_ko),
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
                    contentDescription = "Ko",
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
    text: String,
    removeAction: () -> Unit,
    onShare: (String?) -> Unit,
){
    Row(
        modifier = Modifier
            .fillMaxWidth()
            .padding(2.dp),
        horizontalArrangement = Arrangement.End,
        verticalAlignment = Alignment.CenterVertically
    ) {
        IconButton(onClick = removeAction) {
            Icon(
                modifier = Modifier.size(20.dp),
                imageVector = Icons.Outlined.DeleteOutline,
                contentDescription = "remove",
                tint = Color.DarkGray,
            )
        }
        ShareButton(
            onShare = onShare,
            text = text,
            buttonSize = 20.dp,
            buttonColor = Color.DarkGray,
        )
        /*
        CustomTextButton(
            text = stringResource(R.string.remove_review_button_text),
            onClick = removeAction,
        )*/
    }
}

@Composable
fun FilterButtonsRow(
    setCategory: (ReviewCategory) -> Unit,
    selectedCategory: ReviewCategory,
) {
    Row(
        modifier = Modifier
            .fillMaxWidth()
            .padding(8.dp),
        horizontalArrangement = Arrangement.End,
        verticalAlignment = Alignment.CenterVertically
    ) {
        TagButton(
            text = stringResource(R.string.reviews_filter_word),
            textWeight = if (selectedCategory == ReviewCategory.Word) FontWeight.W500
                            else FontWeight.Light,
            height = 40.dp,
            width = 90.dp,
            backgroundColor = if (selectedCategory == ReviewCategory.Word) LightGreen
                                else Color.White,
            onClick = {
                setCategory(ReviewCategory.Word)
            }
        )
        TagButton(
            text = stringResource(R.string.reviews_filter_sentence),
            textWeight = if (selectedCategory == ReviewCategory.Sentence) FontWeight.W500
                            else FontWeight.Light,
            height = 40.dp,
            width = 90.dp,
            backgroundColor = if (selectedCategory == ReviewCategory.Sentence) LightBlue
                                else Color.White,
            onClick = {
                setCategory(ReviewCategory.Sentence)
            }
        )
        TagButton(
            text = stringResource(R.string.reviews_filter_kanjis),
            textWeight = if (selectedCategory == ReviewCategory.Kanji) FontWeight.W500
                            else FontWeight.Light,
            height = 40.dp,
            width = 90.dp,
            backgroundColor = if (selectedCategory == ReviewCategory.Kanji) LightYellow
                                else Color.White,
            onClick = {
                setCategory(ReviewCategory.Kanji)
            }
        )
    }
}

@ExperimentalComposeUiApi
@Composable
fun SearchField(
    searchQuery: MutableState<String>,
    searchOpen: MutableState<Boolean>,
    onSearch: (String) -> Unit,
    onRestore: () -> Unit,
) {
    val keyboardController = LocalSoftwareKeyboardController.current
    TextField(
        modifier = Modifier.fillMaxWidth().padding(horizontal = 5.dp),
        value = searchQuery.value,
        onValueChange =  {
            searchQuery.value = it
            onSearch(searchQuery.value)
                         },
        textStyle = TextStyle(color = Color.White, fontSize = 16.sp),
        colors = TextFieldDefaults.textFieldColors(
            backgroundColor = MaterialTheme.colors.primary,
            focusedIndicatorColor =  Color.White,
            unfocusedIndicatorColor = Color.White),
        placeholder = {
            Text(
                text = stringResource(R.string.search_in_review),
                style = TextStyle(color = Color.White, fontSize = 16.sp),
            )
        },
        leadingIcon = {
            Icon(
                modifier = Modifier.padding(start = 5.dp),
                imageVector = Icons.Rounded.Search,
                contentDescription = "search",
                tint = Color.White
            )
        },
        trailingIcon = {
            IconButton(
                modifier = Modifier.padding(end = 5.dp),
                onClick = {
                    onRestore()
                    searchQuery.value = ""
                    searchOpen.value = false
                }
            ) {
                Icon(
                    modifier = Modifier.size(20.dp),
                    imageVector = Icons.Rounded.Close,
                    contentDescription = "reset",
                    tint = Color.White
                )
            }
        },
        keyboardOptions = KeyboardOptions(
            keyboardType = KeyboardType.Text,
            imeAction = ImeAction.Search,
        ),
        keyboardActions = KeyboardActions (
            onSearch = {
                onSearch(searchQuery.value)
                keyboardController?.hide()
            }
        )
    )


}

