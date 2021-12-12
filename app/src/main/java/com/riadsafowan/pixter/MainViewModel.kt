package com.riadsafowan.pixter

import android.graphics.Bitmap
import android.net.Uri
import android.util.Base64
import android.util.Log
import androidx.lifecycle.MutableLiveData
import androidx.lifecycle.ViewModel
import androidx.lifecycle.viewModelScope
import com.riadsafowan.pixter.FirstFragment.Companion.TAG
import kotlinx.coroutines.Dispatchers
import kotlinx.coroutines.launch
import org.apache.commons.io.output.ByteArrayOutputStream
import kotlin.math.ceil
import kotlin.math.log

class MainViewModel : ViewModel() {

    val inputURI: MutableLiveData<Uri> = MutableLiveData()
    val inputBitmap: MutableLiveData<Bitmap> = MutableLiveData()
    val inputString: MutableLiveData<String> = MutableLiveData()
    val outputString: MutableLiveData<String> = MutableLiveData()
    val outputBitmap: MutableLiveData<Bitmap> = MutableLiveData()
    var isDownloaded: Boolean? = null

    fun generateString() {
        viewModelScope.launch(Dispatchers.IO) {
        }
        var string = ""
        var x = 0
        do {
            val bitmap = inputBitmap.value!!
            val scaled =
                Bitmap.createScaledBitmap(bitmap,
                    if (bitmap.width > 512) 512 else bitmap.width,
                    if (bitmap.width > 512) ((bitmap.height * (512.0 / bitmap.width)).toInt()) else bitmap.height,
                    true)
            val quality = getQuality(scaled, 35000) - x++
            val baos = ByteArrayOutputStream()
            scaled.compress(
                Bitmap.CompressFormat.JPEG,
                if (quality <= 100) quality else 100,
                baos
            )
            val byteArray = baos.toByteArray()
            string = Base64.encodeToString(byteArray, Base64.DEFAULT)
        } while (string.length > 20000)
        inputString.postValue(string)

    }

    private fun getQuality(bitmap: Bitmap, size: Int): Int {
        val baos = ByteArrayOutputStream()
        bitmap.compress(
            Bitmap.CompressFormat.JPEG,
            100,
            baos
        )
        val byteArray = baos.toByteArray()
        val quality = (size.toDouble() / (4 * (ceil(byteArray.size / 3.0)))) * 100
        return quality.toInt()
    }
}