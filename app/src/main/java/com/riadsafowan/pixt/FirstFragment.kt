package com.riadsafowan.pixt

import android.app.Activity
import android.content.Intent
import android.os.Bundle
import androidx.fragment.app.Fragment
import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import com.bumptech.glide.Glide
import com.riadsafowan.pixt.databinding.FragmentFirstBinding
import android.content.ClipData
import android.content.ClipboardManager
import android.content.Context
import android.provider.MediaStore
import android.widget.Toast
import androidx.activity.result.contract.ActivityResultContracts
import androidx.fragment.app.activityViewModels


class FirstFragment : Fragment() {
    private var _binding: FragmentFirstBinding? = null
    private val binding get() = _binding!!
    private val viewModel: MainViewModel by activityViewModels()
    private val resultLauncher =
        registerForActivityResult(ActivityResultContracts.StartActivityForResult()) { result ->
            if (result.resultCode == Activity.RESULT_OK) {
                val data: Intent? = result.data
                viewModel.inputURI.value = data?.data!!
            }
        }
    private val copyClickListener = View.OnClickListener {

//            val galleryIntent = Intent(Intent.ACTION_PICK,  MediaStore.Images.Media.EXTERNAL_CONTENT_URI) //direct native gallery

//            val galleryIntent = Intent(Intent.ACTION_PICK)
//            galleryIntent.type = "image/*" // native photo , google photo

        // recent file with all folder access
        val galleryIntent = Intent(Intent.ACTION_GET_CONTENT)
        galleryIntent.type = "image/*"
        resultLauncher.launch(galleryIntent)
    }

    override fun onCreateView(
        inflater: LayoutInflater, container: ViewGroup?,
        savedInstanceState: Bundle?
    ): View {
        _binding = FragmentFirstBinding.inflate(inflater, container, false)
        binding.addImage.visibility= View.VISIBLE
        binding.encodeAndCopy.visibility = View.GONE
        binding.tvAddImage.visibility = View.GONE
//        binding.imageView.scaleType = ImageView.ScaleType.CENTER_CROP
        binding.imageView.setImageResource(R.drawable.logo_black_n_white)
        return binding.root
    }

    //    @RequiresApi(Build.VERSION_CODES.N)
    override fun onViewCreated(view: View, savedInstanceState: Bundle?) {
        super.onViewCreated(view, savedInstanceState)
        observer()
        listener()
    }

    private fun observer() {
        viewModel.inputURI.observe(viewLifecycleOwner) { imageUri ->
            Glide.with(requireContext())
                .load(imageUri)
                .into(binding.imageView)
            binding.encodeAndCopy.visibility = View.VISIBLE
            binding.addImage.visibility = View.GONE
            binding.tvAddImage.visibility = View.VISIBLE
        }
        viewModel.inputString.observe(viewLifecycleOwner) {
            binding.progressBar.visibility = View.INVISIBLE
//                    binding.text.text = imageString
            copyToClipBoard()
        }
    }

    private fun listener() {
        binding.addImage.setOnClickListener(copyClickListener)
        binding.tvAddImage.setOnClickListener(copyClickListener)
        binding.encodeAndCopy.setOnClickListener {
            binding.progressBar.visibility = View.VISIBLE
            viewModel.inputBitmap.value =
                MediaStore.Images.Media.getBitmap(
                    activity?.contentResolver,
                    viewModel.inputURI.value
                )
            viewModel.generateString()
        }
    }

    private fun copyToClipBoard() {
        val clipBoard =
            activity?.getSystemService(Context.CLIPBOARD_SERVICE) as ClipboardManager
        val clipData = ClipData.newPlainText("image", viewModel.inputString.value)
        clipBoard.setPrimaryClip(clipData)
        Toast.makeText(requireContext(), "Copied as Text", Toast.LENGTH_SHORT).show()
    }

}