package com.example.nala.service.classifier

 import android.content.Context
 import dagger.hilt.android.qualifiers.ApplicationContext
 import org.pytorch.IValue
 import java.io.IOException
 import java.util.*
 import java.util.regex.Pattern
 import org.pytorch.Module
 import org.pytorch.PyTorchAndroid
 import org.pytorch.Tensor
 import java.lang.Double.MAX_VALUE

 internal class QAException(override var message: String) : Exception()

class BertClassifierVocabulary (
    @ApplicationContext val appContext: Context,
) {

    var tokenToIdMap: HashMap<String, Long>? = HashMap()
    var idToTokenMap: HashMap<Long, String>? = HashMap()
    var model: Module? = null

    init {
        val vocabs = readVocab(appContext, "vocab.txt")
        tokenToIdMap = vocabs.tokenToIdMap
        idToTokenMap = vocabs.idToTokenMap
    }

    fun predict(sentence: String) : String?{
        loadModel()
        val category: String? = null
        try {
            val tokenIds = tokenize(sentence)
            val inTensorBuffer = Tensor.allocateLongBuffer(MODEL_INPUT_LENGTH)
            for (n in tokenIds) inTensorBuffer.put(n.toLong())
            for (i in 0 until MODEL_INPUT_LENGTH - tokenIds.size) tokenToIdMap!![PAD]?.let { inTensorBuffer.put(it) }
            val inTensor = Tensor.fromBlob(inTensorBuffer, longArrayOf(1, MODEL_INPUT_LENGTH.toLong()))
            val outTensors: Map<String, IValue> = model!!.forward(IValue.from(inTensor)).toDictStringKey()
            val _logits = outTensors!!["0"]!!
            val logits = _logits.toTensor().dataAsIntArray
            val predictedClass = argmax(logits)
            val category = IDS_TO_LABELS[predictedClass]
        } catch (e: QAException) {
            e.printStackTrace()
        }
        return category
    }

    fun tokenize(text: String): LongArray {
        val tokenIds = wordPieceTokenizer(text)
        if (tokenIds.size >= MODEL_INPUT_LENGTH) throw QAException("Sentence too long")
        val inputLength = tokenIds.size + EXTRA_ID_NUM
        val ids = LongArray(Math.min(MODEL_INPUT_LENGTH, inputLength))
        ids[0] = tokenToIdMap!![CLS]!!

        for (i in tokenIds.indices) ids[i + 1] = tokenIds[i]!!.toLong()
        ids[tokenIds.size + 1] = tokenToIdMap!![SEP]!!
        return ids
    }

    private fun loadModel() {
        if (model == null) {
            model = PyTorchAndroid.loadModuleFromAsset(appContext.assets, "class_quantized.pt")
        }
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
                    tokenToIdMap!![line] = count
                    idToTokenMap!![count] = line
                    count++
                }
                else break
            }
        } catch(e: IOException) {
            e.printStackTrace()
        }
        return VocabLoadingOutput(
            tokenToIdMap = tokenToIdMap,
            idToTokenMap = idToTokenMap
        )
    }

    private fun wordPieceTokenizer(text: String): List<Long?> {
        val tokenIds: MutableList<Long?> = ArrayList()
        val p = Pattern.compile("\\w+|\\S")
        val m = p.matcher(text)
        while (m.find()) {
            val token = m.group().toLowerCase()
            if (tokenToIdMap!!.containsKey(token)) tokenIds.add(tokenToIdMap!![token]) else {
                for (i in 0 until token.length) {
                    if (tokenToIdMap!!.containsKey(token.substring(0, token.length - i - 1))) {
                        tokenIds.add(tokenToIdMap!![token.substring(0, token.length - i - 1)])
                        var subToken = token.substring(token.length - i - 1)
                        var j = 0

                        while (j < subToken.length) {
                            if (tokenToIdMap!!.containsKey("##" + subToken.substring(0, subToken.length - j))) {
                                tokenIds.add(tokenToIdMap!!["##" + subToken.substring(0, subToken.length - j)])
                                subToken = subToken.substring(subToken.length - j)
                                j = subToken.length - j
                            } else if (j == subToken.length - 1) {
                                tokenIds.add(tokenToIdMap!!["##$subToken"])
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

    private fun argmax(array: IntArray): Int {
        var maxIdx = 0
        var maxVal: Double = -MAX_VALUE
        for (j in array.indices) {
            if (array[j] > maxVal) {
                maxVal = array[j].toDouble()
                maxIdx = j
            }
        }
        return maxIdx
    }
}