package com.riadsafowan.pixt

import android.graphics.Bitmap
import android.net.Uri
import android.util.Base64
import androidx.lifecycle.MutableLiveData
import androidx.lifecycle.ViewModel
import androidx.lifecycle.viewModelScope
import kotlinx.coroutines.Dispatchers
import kotlinx.coroutines.launch
import org.apache.commons.io.output.ByteArrayOutputStream

class MainViewModel : ViewModel() {

    val inputURI: MutableLiveData<Uri> = MutableLiveData()
    val inputBitmap: MutableLiveData<Bitmap> = MutableLiveData()
    val inputString: MutableLiveData<String> = MutableLiveData()
    val outputString: MutableLiveData<String> = MutableLiveData()

    fun generateString() {
        viewModelScope.launch(Dispatchers.IO) {
            val bitmap = inputBitmap.value!!
            val size = bitmap.height * bitmap.width
            val quality = ((1500 * 1500 / size.toDouble()) * 100).toInt()
            val baos = ByteArrayOutputStream()
            bitmap.compress(
                Bitmap.CompressFormat.JPEG,
                if (quality <= 100) quality else 100,
                baos
            )
            val byteArray = baos.toByteArray()
            inputString.postValue(Base64.encodeToString(byteArray, Base64.DEFAULT))
        }
    }

}