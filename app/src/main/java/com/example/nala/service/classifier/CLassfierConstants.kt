package com.example.nala.service.classifier

import java.util.*

val MODEL_INPUT_LENGTH = 360
val EXTRA_ID_NUM = 3
val CLS = "[CLS]"
val SEP = "[SEP]"
val PAD = "[PAD]"

val IDS_TO_LABELS: Map<Int, String> = mapOf<Int, String>()