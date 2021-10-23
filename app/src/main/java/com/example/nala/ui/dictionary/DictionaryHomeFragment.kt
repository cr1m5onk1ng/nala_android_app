package com.example.nala.ui.dictionary

import android.os.Bundle
import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import androidx.compose.material.rememberScaffoldState
import androidx.compose.ui.ExperimentalComposeUiApi
import androidx.compose.ui.platform.ComposeView
import androidx.fragment.app.Fragment
import androidx.fragment.app.activityViewModels
import androidx.navigation.NavController
import androidx.navigation.findNavController
import com.example.nala.domain.model.utils.AuthState
import com.example.nala.domain.model.utils.ErrorType
import com.example.nala.ui.BaseApplication
import com.example.nala.ui.composables.dictionary.HomeScreen
import com.example.nala.ui.theme.AppTheme
import com.example.nala.utils.types.InputStringType
import com.example.nala.utils.Utils
import dagger.hilt.android.AndroidEntryPoint
import javax.inject.Inject


@AndroidEntryPoint
class DictionaryHomeFragment : Fragment() {

    @Inject
    lateinit var application: BaseApplication

    private val viewModel: DictionaryViewModel by activityViewModels()

    @ExperimentalComposeUiApi
    override fun onCreateView(
        inflater: LayoutInflater,
        container: ViewGroup?,
        savedInstanceState: Bundle?
    ): View {
        //viewModel.onTriggerEvent(DictionaryEvent.LoadReviewsEvent)
        return ComposeView(requireContext()).apply {
            setContent {
                val scaffoldState = rememberScaffoldState()
                AppTheme(darkTheme = false) {
                    HomeScreen(
                        query = viewModel.query.value,
                        mightForgetItemsState = viewModel.mightForgetItemsState.value,
                        authState = AuthState.AuthError(ErrorType.OAUTH_ERROR),
                        authPending = false,
                        onQueryChange = viewModel::onQueryChanged,
                        onClick = {viewModel.onTriggerEvent(DictionaryEvent.SearchWordEvent)},
                        onSignIn = {},
                        onSignOut = {},
                        onSetMFI = {},
                        textReceived = viewModel.textReceived.value,
                        sentenceReceived = viewModel.sentenceReceived.value,
                        isHomeSelected = viewModel.isHomeSelected.value,
                        isReviewsSelected = viewModel.isReviewSelected.value,
                        toggleHome = viewModel::toggleHome,
                        toggleReviews = viewModel::toggleReviews,
                        onHandleQueryTextType = @DictionaryHomeFragment::onHandleQueryTextTypes,
                        scaffoldState = scaffoldState,
                        navController = findNavController(),
                    )
                }
            }
        }
    }

        private fun onHandleQueryTextTypes(inputString: String, navController: NavController) {
            when(Utils.parseInputString(inputString)) {
                InputStringType.Sentence -> {
                    viewModel.setSharedSentence(inputString)
                    navController.navigate("sentence_form_screen")
                }
                InputStringType.ArticleUrl -> {
                    //reviewViewModel.setArticle(inputString)
                    navController.navigate("article_screen")
                }
                InputStringType.YoutubeUrl -> {
                    //ytViewModel.setVideoModel(inputString)
                    navController.navigate("video_screen")
                }
            }
        }
}