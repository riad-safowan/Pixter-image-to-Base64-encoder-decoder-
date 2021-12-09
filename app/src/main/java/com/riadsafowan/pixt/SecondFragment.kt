package com.riadsafowan.pixt

import android.content.ClipData
import android.content.ClipboardManager
import android.content.Context
import android.os.Bundle
import androidx.fragment.app.Fragment
import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import com.riadsafowan.pixt.databinding.FragmentSecondBinding
import android.graphics.BitmapFactory
import android.util.Base64
import android.widget.Toast
import androidx.fragment.app.activityViewModels
import android.graphics.Bitmap
import android.os.Environment
import java.io.File
import java.io.FileOutputStream
import java.io.OutputStream
import java.text.SimpleDateFormat


class SecondFragment : Fragment() {
    private var _binding: FragmentSecondBinding? = null
    private val binding get() = _binding!!
    private val viewModel: MainViewModel by activityViewModels()

    override fun onCreateView(
        inflater: LayoutInflater, container: ViewGroup?,
        savedInstanceState: Bundle?
    ): View {
        _binding = FragmentSecondBinding.inflate(inflater, container, false)
        return binding.root
    }

    override fun onViewCreated(view: View, savedInstanceState: Bundle?) {
        super.onViewCreated(view, savedInstanceState)
        viewModel.outputString.observe(viewLifecycleOwner) {
            val imageBytes = Base64.decode(it, Base64.DEFAULT)
            val decodedImage = BitmapFactory.decodeByteArray(imageBytes, 0, imageBytes.size)
            binding.imageView.setImageBitmap(decodedImage)
            binding.btnDownload.visibility = View.VISIBLE
            viewModel.outputBitmap.value = decodedImage
        }
        binding.btnPaste.setOnClickListener {
            val clipboard =
                activity?.getSystemService(Context.CLIPBOARD_SERVICE) as ClipboardManager
            viewModel.outputString.value = clipboard.primaryClip?.getItemAt(0)?.text.toString()
//            binding.editText.setText(item)
            val clipData = ClipData.newPlainText("image", null)
            clipboard.setPrimaryClip(clipData)
        }
        binding.btnDownload.setOnClickListener {
            saveToLocal()
        }
    }

    private fun saveToLocal() {
        val folderPath = Environment.getExternalStorageDirectory().toString() + "/DCIM/Pixt/"
        val folder = File(folderPath)
        if (!folder.exists()) {
            File(folderPath).mkdirs()
        }
        val name = SimpleDateFormat("yyyyMMdd_HHmmss").format(System.currentTimeMillis())
        val fOut: OutputStream?
        val file = File(folder, "Pixt_$name.jpg")
        try {
            fOut = FileOutputStream(file)
            val pictureBitmap: Bitmap = viewModel.outputBitmap.value!!
            pictureBitmap.compress(Bitmap.CompressFormat.JPEG, 100, fOut)
            fOut.flush()
            fOut.close()
            Toast.makeText(requireContext(), "Saved to local", Toast.LENGTH_SHORT).show()
        } catch (e: Exception) {
            e.printStackTrace();
        }
//        MediaStore.Images.Media.insertImage(
//            activity?.contentResolver,
//            file.absolutePath,
//            file.name,
//            file.name
//        ) //directly save to Pictures folder
    }

    override fun onDestroyView() {
        super.onDestroyView()
        _binding = null
    }
}