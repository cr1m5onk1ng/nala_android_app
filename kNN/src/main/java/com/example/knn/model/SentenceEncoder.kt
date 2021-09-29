package com.example.knn.model

import android.content.Context
import android.util.Log
import com.example.knn.config.MODEL_INPUT_LENGTH
import com.example.knn.config.PAD
import com.example.knn.tokenization.TokenizationException
import com.example.knn.tokenization.WordPieceTokenizer
import com.example.knn.utils.Utils
import org.pytorch.*

class SentenceEncoder(
    private val appContext: Context,
    private val modelPath: String,
    private val tokenizer: WordPieceTokenizer,
) : TextEncoderModel {

    var model: Module? = null

    init{
        if (model == null) {
            model = LiteModuleLoader.load(Utils.assetFilePath(appContext, modelPath))
        }
    }

    override fun predict(text: String): FloatArray {
        var embeddings: FloatArray = floatArrayOf()
        try {
            // TOKENIZATION //
            val tokenIds = tokenizer.tokenize(text)
            val inTensorBuffer = Tensor.allocateLongBuffer(MODEL_INPUT_LENGTH)
            for (n in tokenIds) inTensorBuffer.put(n)
            // padding
            for (i in 0 until MODEL_INPUT_LENGTH - tokenIds.size) {
                tokenizer.tokenToIdMap[PAD]?.let { inTensorBuffer.put(it) }
            }

            // loading tensor from long buffer
            val inTensor = Tensor.fromBlob(inTensorBuffer, longArrayOf(1, MODEL_INPUT_LENGTH.toLong()))
            println("Input tensor: $inTensor")
            val outTensorsMap = model!!.forward(IValue.from(inTensor)).toDictStringKey()
            println("Mapping: $outTensorsMap")
            val outTensors = outTensorsMap["pooler_output"]!!.toTensor()
            println("Out tensor: $outTensors")
            embeddings = outTensors.dataAsFloatArray
            println("Output: $embeddings")
        } catch (e: TokenizationException) {
            e.printStackTrace()
        }
        return embeddings
    }
}