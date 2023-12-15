package com.jeahwan.origin.utils.expandfun

import java.math.BigDecimal
import java.text.DecimalFormat

object StringKt {

    fun Int.circleNumberFormat(): String =
        if (this >= 10000) {
            DecimalFormat("0.0w").format(
                BigDecimal(this).divide(BigDecimal(10000))
                    .setScale(1, BigDecimal.ROUND_UP)
            )
        } else this.toString()
}