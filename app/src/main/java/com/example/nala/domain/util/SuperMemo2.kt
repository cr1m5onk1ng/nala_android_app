package com.example.nala.domain.util

import android.os.Build
import androidx.annotation.RequiresApi
import java.time.LocalDate
import kotlin.math.roundToInt

class SuperMemo2 {
    companion object {
        fun updateParams(
            quality: Int,
            previousRepetitions: Int,
            previousEaseFactor: Double,
            previousInterval: Int,
        ) : SmResponse {
            var interval: Int;
            var easeFactor = 0.0;
            var repetitions = previousRepetitions;
            if (quality >= 3) {
                when (repetitions) {
                    0 -> interval = 1
                    1 -> interval = 6
                    else ->
                        interval = (previousInterval.toLong() * previousEaseFactor)
                            .roundToInt()
                }
                repetitions++;
                easeFactor = previousEaseFactor +
                        (0.1 - (5 - quality) * (0.08 + (5 - quality) * 0.02));
            } else {
                repetitions = 0;
                interval = 1;
                easeFactor = previousEaseFactor;
            }

            if (easeFactor < 1.3) {
                easeFactor = 1.3;
            }

            return SmResponse(
                interval= interval,
                repetitions= repetitions,
                easeFactor= easeFactor);
            }

        @RequiresApi(Build.VERSION_CODES.O)
        fun calculateNextDate(stringDate: String, interval: Int) : String {
            val date = LocalDate.parse(stringDate)
            val updatedDate = date.plusDays(interval.toLong())
            return updatedDate.toString()
        }
    }
}