package com.example.nala.ui.composables.dictionary

import androidx.compose.foundation.layout.*
import androidx.compose.material.*
import androidx.compose.material.icons.Icons
import androidx.compose.material.icons.rounded.Close
import androidx.compose.runtime.Composable
import androidx.compose.ui.Modifier
import androidx.compose.ui.res.stringResource
import androidx.compose.ui.unit.dp
import androidx.constraintlayout.compose.ConstraintLayout
import com.example.nala.R
import com.example.nala.domain.model.dictionary.DictionaryModel
import com.example.nala.domain.model.utils.DataState
import com.example.nala.ui.composables.ErrorScreen
import com.example.nala.ui.composables.LoadingIndicator

@Composable
fun DictionaryWindowScreen(
    searchState: DataState<DictionaryModel>,
    onClose: () -> Unit,
){
    Scaffold { paddingValues ->
        when(searchState) {
            is DataState.Initial<*>, DataState.Loading -> {
                LoadingIndicator()
            }
            is DataState.Error -> {
                Column(
                    modifier = Modifier
                        .fillMaxSize()
                        .padding(16.dp),
                ) {
                    ErrorScreen(
                        text = stringResource(R.string.no_result_from_jisho),
                        subtitle = "¯\\_(ツ)_/¯"
                    )
                }
            }
            is DataState.Success<DictionaryModel> -> {
                ConstraintLayout(
                    modifier = androidx.compose.ui.Modifier.padding(paddingValues)
                ) {
                    Column(
                        modifier = Modifier
                            .fillMaxSize()
                            .padding(16.dp),
                    ) {
                        Row(
                            modifier = Modifier
                                .fillMaxWidth()
                                .padding(top = 26.dp, end = 32.dp),
                            horizontalArrangement = Arrangement.End
                        ){
                            IconButton(
                                onClick = {
                                   onClose()
                                }
                            ) {
                                Icon(Icons.Rounded.Close, contentDescription = "close icon")
                            }
                        }
                        Text(searchState.data.word)
                    }
                }
            }
        }
    }
}