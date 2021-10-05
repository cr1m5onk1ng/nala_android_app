package com.example.knn.tokenization

import android.content.Context
import com.example.knn.config.CLS
import com.example.knn.config.EXTRA_ID_NUM
import com.example.knn.config.MODEL_INPUT_LENGTH
import com.example.knn.config.SEP
import java.io.IOException
import java.util.ArrayList
import java.util.HashMap
import java.util.regex.Pattern

class WordPieceTokenizer(
    appContext: Context,
) : Tokenizer {

    var tokenToIdMap: HashMap<String, Long>
    var idToTokenMap: HashMap<Long, String>

    init {
        val vocabs = readVocab(appContext, "vocab.txt")
        tokenToIdMap = vocabs.tokenToIdMap
        idToTokenMap = vocabs.idToTokenMap
    }

    override fun tokenize(text: String): LongArray {
        val tokenIds = wordPieceTokenizer(text)
        if (tokenIds.size >= MODEL_INPUT_LENGTH) throw TokenizationException("Sentence too long")
        val inputLength = tokenIds.size + EXTRA_ID_NUM
        val ids = LongArray(MODEL_INPUT_LENGTH.coerceAtMost(inputLength))
        ids[0] = tokenToIdMap[CLS]!!

        for (i in tokenIds.indices) ids[i + 1] = tokenIds[i]!!.toLong()
        ids[tokenIds.size + 1] = tokenToIdMap[SEP]!!
        return ids
    }

    private fun wordPieceTokenizer(text: String): List<Long?> {
        val tokenIds: MutableList<Long?> = ArrayList()
        val p = Pattern.compile("\\w+|\\S")
        val m = p.matcher(text)
        while (m.find()) {
            val token = m.group().lowercase()
            if (tokenToIdMap.containsKey(token)) tokenIds.add(tokenToIdMap[token]) else {
                for (i in token.indices) {
                    if (tokenToIdMap.containsKey(token.substring(0, token.length - i - 1))) {
                        tokenIds.add(tokenToIdMap[token.substring(0, token.length - i - 1)])
                        var subToken = token.substring(token.length - i - 1)
                        var j = 0

                        while (j < subToken.length) {
                            if (tokenToIdMap.containsKey("##" + subToken.substring(0, subToken.length - j))) {
                                tokenIds.add(tokenToIdMap["##" + subToken.substring(0, subToken.length - j)])
                                subToken = subToken.substring(subToken.length - j)
                                j = subToken.length - j
                            } else if (j == subToken.length - 1) {
                                tokenIds.add(tokenToIdMap["##$subToken"])
                                break
                            } else j++
                        }
                        break
                    }
                }
            }
        }
        return tokenIds
    }

    private fun readVocab(context: Context, fileName: String): VocabLoadingOutput {
        val tokenToIdMap: HashMap<String, Long> = HashMap()
        val idToTokenMap: HashMap<Long, String> = HashMap()
        try{
            val vocabLines = context
                .assets
                .open(fileName)
                .bufferedReader()
            var count = 0L
            while (true) {
                val line = vocabLines.readLine()
                if (line != null) {
                    tokenToIdMap[line] = count
                    idToTokenMap[count] = line
                    count++
                }
                else{
                    break
                }
            }
        } catch(e: IOException) {
            e.printStackTrace()
        }
        return VocabLoadingOutput(
            tokenToIdMap = tokenToIdMap,
            idToTokenMap = idToTokenMap
        )
    }
}