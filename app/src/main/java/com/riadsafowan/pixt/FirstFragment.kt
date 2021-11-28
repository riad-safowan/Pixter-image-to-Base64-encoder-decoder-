package com.riadsafowan.pixt

import android.Manifest
import android.app.Activity
import android.content.Intent
import android.net.Uri
import android.os.Bundle
import android.provider.MediaStore
import androidx.fragment.app.Fragment
import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import androidx.navigation.fragment.findNavController
import com.bumptech.glide.Glide
import com.riadsafowan.pixt.databinding.FragmentFirstBinding
import android.graphics.Bitmap
import android.content.ClipData
import android.content.ClipboardManager
import android.content.Context
import android.content.pm.PackageManager
import android.os.Build
import org.apache.commons.io.FileUtils
import android.util.Base64
import android.widget.Toast
import androidx.annotation.RequiresApi
import androidx.appcompat.app.AppCompatActivity
import androidx.core.app.ActivityCompat
import androidx.core.content.ContextCompat
import com.riadsafowan.pixt.databinding.ActivityMainBinding
import kotlinx.coroutines.CoroutineScope
import kotlinx.coroutines.Dispatchers.Default
import kotlinx.coroutines.Dispatchers.Main
import kotlinx.coroutines.launch
import kotlinx.coroutines.withContext
import java.io.ByteArrayOutputStream
import java.io.IOException


class FirstFragment : Fragment() {
    val PICK_IMAGE = 100
    private var _binding: FragmentFirstBinding? = null
    private lateinit var imageString: String
    private lateinit var imageUri: Uri

    private val binding get() = _binding!!

    override fun onCreateView(
        inflater: LayoutInflater, container: ViewGroup?,
        savedInstanceState: Bundle?
    ): View {
        _binding = FragmentFirstBinding.inflate(inflater, container, false)

        (activity as AppCompatActivity).supportActionBar?.title = "Image To Text"

        binding.copyButton.visibility = View.INVISIBLE
        binding.encodeButton.visibility = View.INVISIBLE
        return binding.root
    }

    @RequiresApi(Build.VERSION_CODES.N)
    override fun onViewCreated(view: View, savedInstanceState: Bundle?) {
        super.onViewCreated(view, savedInstanceState)


        binding.addImage.setOnClickListener {
//            val galleryIntent = Intent(
//                Intent.ACTION_PICK,
//                MediaStore.Images.Media.EXTERNAL_CONTENT_URI
//            )
//            startActivityForResult(galleryIntent, PICK_IMAGE)


//            val intent = Intent(Intent.ACTION_GET_CONTENT)
//            intent.type = "image/*"
//            val chooser = Intent.createChooser(intent, "Choose a Picture")
//            startActivityForResult(chooser, PICK_IMAGE)

            if (ContextCompat.checkSelfPermission(
                    requireContext(),
                    Manifest.permission.READ_EXTERNAL_STORAGE
                ) != PackageManager.PERMISSION_GRANTED
            ) {
                Toast.makeText(requireContext(), "permission nai", Toast.LENGTH_SHORT).show()
                ActivityCompat.requestPermissions(
                    requireActivity(),
                    arrayOf(Manifest.permission.READ_EXTERNAL_STORAGE),
                    PICK_IMAGE
                )
            } else {
                Toast.makeText(requireContext(), "permission ase", Toast.LENGTH_SHORT).show()
                selectImage()
            }

        }
        binding.next.setOnClickListener {
            findNavController().navigate(R.id.action_FirstFragment_to_SecondFragment)
        }
        binding.copyButton.setOnClickListener {
            val clipBoard =
                activity?.getSystemService(Context.CLIPBOARD_SERVICE) as ClipboardManager
            val clipData = ClipData.newPlainText("image", imageString)
            clipBoard.setPrimaryClip(clipData)
            Toast.makeText(requireContext(), "copied", Toast.LENGTH_SHORT).show()
        }
        binding.encodeButton.setOnClickListener {
            binding.progressBar.visibility = View.VISIBLE
            CoroutineScope(Default).launch {

//                val byteArray = activity?.contentResolver?.openInputStream(imageUri)?.readBytes()
//                imageString = Base64.encodeToString(byteArray, Base64.DEFAULT)

//                imageString = File(imageUri.path).toBase64()!!

                try {
                    val bitmap =
                        MediaStore.Images.Media.getBitmap(activity?.getContentResolver(), imageUri)
                    val baos = ByteArrayOutputStream()
                    bitmap.compress(Bitmap.CompressFormat.JPEG, 30, baos)
                    val byteArray = baos.toByteArray()
                    imageString = Base64.encodeToString(byteArray, Base64.CRLF)
                } catch (e: IOException) {

                }


                withContext(Main) {
                    binding.progressBar.visibility = View.INVISIBLE
                    binding.text.text = imageString
                    binding.copyButton.visibility = View.VISIBLE
                    binding.encodeButton.visibility = View.INVISIBLE
                }

            }
        }

    }

    private fun selectImage() {
        Toast.makeText(requireContext(), "permission nai", Toast.LENGTH_SHORT).show()
        val intent = Intent(Intent.ACTION_GET_CONTENT)
        intent.type = "image/*"
        val chooser = Intent.createChooser(intent, "Choose a Picture")
        startActivityForResult(chooser, PICK_IMAGE)
    }

//    fun File.toBase64(): String? {
//        val result: String?
//        inputStream().use { inputStream ->
//            val sourceBytes = inputStream.readBytes()
//            result = Base64.encodeToString(sourceBytes, Base64.DEFAULT)
//        }
//
//        return result
//    }

    override fun onActivityResult(requestCode: Int, resultCode: Int, data: Intent?) {
        if (requestCode == PICK_IMAGE && resultCode == Activity.RESULT_OK && null != data) {
            imageUri = data.getData()!!

            Glide.with(requireContext())
                .load(imageUri)
                .into(binding.imageView)
            binding.encodeButton.visibility = View.VISIBLE
        }

    }

    override fun onRequestPermissionsResult(
        requestCode: Int,
        permissions: Array<out String>,
        grantResults: IntArray
    ) {
        super.onRequestPermissionsResult(requestCode, permissions, grantResults)

        if (requestCode == 100 && grantResults[0] == PackageManager.PERMISSION_GRANTED) {
            selectImage()
        } else {
            Toast.makeText(requireContext(), "Permission denied", Toast.LENGTH_SHORT).show()
        }
    }

}