package com.riadsafowan.pixt

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

class SecondFragment : Fragment() {

    private var _binding: FragmentSecondBinding? = null
    private val viewModel: MainViewModel by activityViewModels()

    // This property is only valid between onCreateView and
    // onDestroyView.
    private val binding get() = _binding!!

    override fun onCreateView(
        inflater: LayoutInflater, container: ViewGroup?,
        savedInstanceState: Bundle?
    ): View {
        _binding = FragmentSecondBinding.inflate(inflater, container, false)
        return binding.root
    }

    override fun onViewCreated(view: View, savedInstanceState: Bundle?) {
        super.onViewCreated(view, savedInstanceState)

        viewModel.outputString.observe(viewLifecycleOwner){
            val imageBytes = Base64.decode(it, Base64.DEFAULT)
            val decodedImage = BitmapFactory.decodeByteArray(imageBytes, 0, imageBytes.size)
            binding.imageView.setImageBitmap(decodedImage)
            binding.btnDownload.visibility=View.VISIBLE
            
        }

        binding.btnSee.setOnClickListener {
            val clipboard =
                activity?.getSystemService(Context.CLIPBOARD_SERVICE) as ClipboardManager
            viewModel.outputString.value = clipboard.primaryClip?.getItemAt(0)?.text.toString()
//            binding.editText.setText(item)
//            val clipData = ClipData.newPlainText("image", null)
//            clipboard.setPrimaryClip(clipData)
        }
        
        binding.btnDownload.setOnClickListener {
            Toast.makeText(requireContext(), "Download started", Toast.LENGTH_SHORT).show()
        }
    }

    override fun onDestroyView() {
        super.onDestroyView()
        _binding = null
    }
}