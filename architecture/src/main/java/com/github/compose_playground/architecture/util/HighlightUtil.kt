package com.github.compose_playground.architecture.util

import android.graphics.Color
import android.text.Spannable
import android.text.SpannableStringBuilder
import android.text.style.ForegroundColorSpan
import android.text.style.UnderlineSpan
import java.util.regex.Pattern


fun highlight(text: String, _target: String): Spannable {
    val prefix = "<em class='highlight'>"
    val postfix = "</em>"
    val hasEmTag = text.indexOf(prefix) >= 0

    val toBeMatched =
        text.replace(if (hasEmTag) "$prefix$_target$postfix" else _target, _target, true)

    val spannable =
        SpannableStringBuilder(
            if (hasEmTag) text.replace(
                "$prefix$_target$postfix",
                text.substring(text.indexOf(prefix) + prefix.length until text.indexOf(postfix)),
                true
            ) else text
        )

    val m = Pattern.compile(_target).matcher(toBeMatched)
    while (m.find()) {
        val span = ForegroundColorSpan(Color.RED)
        spannable.setSpan(span, m.start(), m.end(), Spannable.SPAN_EXCLUSIVE_EXCLUSIVE)
    }
    spannable.setSpan(UnderlineSpan(),0, toBeMatched.length, 0)
    return spannable
}
