package com.riadsafowan.pixt

import android.app.Activity
import android.content.Intent
import android.net.Uri
import android.os.Bundle
import androidx.fragment.app.Fragment
import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import androidx.navigation.fragment.findNavController
import com.bumptech.glide.Glide
import com.riadsafowan.pixt.databinding.FragmentFirstBinding
import android.content.ClipData
import android.content.ClipboardManager
import android.content.Context
import android.graphics.Bitmap
import android.os.Build
import android.provider.MediaStore
import android.util.Base64
import android.widget.Toast
import androidx.activity.result.contract.ActivityResultContracts
import androidx.annotation.RequiresApi
import androidx.appcompat.app.AppCompatActivity
import kotlinx.coroutines.CoroutineScope
import kotlinx.coroutines.Dispatchers.Default
import kotlinx.coroutines.Dispatchers.Main
import kotlinx.coroutines.launch
import kotlinx.coroutines.withContext
import org.apache.commons.io.output.ByteArrayOutputStream
import java.io.IOException


class FirstFragment : Fragment() {
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

        var resultLauncher =
            registerForActivityResult(ActivityResultContracts.StartActivityForResult()) { result ->
                if (result.resultCode == Activity.RESULT_OK) {

                    val data: Intent? = result.data
                    imageUri = data?.data!!

                    Glide.with(requireContext())
                        .load(imageUri)
                        .into(binding.imageView)
                    binding.encodeButton.visibility = View.VISIBLE
                }
            }

        binding.addImage.setOnClickListener {

//            val galleryIntent = Intent(Intent.ACTION_PICK,  MediaStore.Images.Media.EXTERNAL_CONTENT_URI) //direct native gallery

//            val galleryIntent = Intent(Intent.ACTION_PICK)
//            galleryIntent.type = "image/*" // native photo , google photo

            // recent file with all folder access
            val galleryIntent = Intent(Intent.ACTION_GET_CONTENT)
            galleryIntent.type = "image/*"
            resultLauncher.launch(galleryIntent)
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

//                takes a lot of time and doesn't compress
//                val byteArray = activity?.contentResolver?.openInputStream(imageUri)?.readBytes()
//                imageString = Base64.encodeToString(byteArray, Base64.DEFAULT)

                try {
                    val bitmap =
                        MediaStore.Images.Media.getBitmap(activity?.contentResolver, imageUri)
                    val baos = ByteArrayOutputStream()
                    bitmap.compress(Bitmap.CompressFormat.JPEG, 10, baos)
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

}