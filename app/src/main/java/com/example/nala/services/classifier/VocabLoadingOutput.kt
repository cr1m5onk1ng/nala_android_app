package com.example.nala.services.classifier

import java.util.*

data class VocabLoadingOutput (
    val tokenToIdMap: HashMap<String, Long>,
    val idToTokenMap: HashMap<Long, String>
        )