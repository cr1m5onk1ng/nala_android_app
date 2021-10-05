package com.example.knn.tokenization

import java.util.HashMap

data class VocabLoadingOutput (
    val tokenToIdMap: HashMap<String, Long>,
    val idToTokenMap: HashMap<Long, String>
)