package com.core.gsbaseandroid.ui.view.progressview.utils

import android.text.TextUtils
import android.util.TypedValue
import android.widget.TextView
import com.core.gsbaseandroid.ui.view.progressview.view.TextForm

/** applies text form attributes to a TextView instance. */
@JvmSynthetic
internal fun TextView.applyTextForm(textForm: TextForm) {
    setTextSize(TypedValue.COMPLEX_UNIT_SP, textForm.textSize)
    setTextColor(textForm.textColor)
    textForm.textStyleObject?.let {
        typeface = textForm.textStyleObject
    } ?: setTypeface(typeface, textForm.textStyle)
    text = if (textForm.textAllCaps) {
        val upper = textForm.text.toString().uppercase()
        if (TextUtils.equals(upper, textForm.text.toString())) {
            textForm.text
        } else {
            upper
        }
    } else {
        textForm.text
    }
}
