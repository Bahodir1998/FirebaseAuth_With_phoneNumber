package com.example.firebaseauth

import android.os.Bundle
import androidx.fragment.app.Fragment
import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import android.widget.Toast
import androidx.navigation.fragment.findNavController
import com.example.firebaseauth.databinding.FragmentFirthBinding


class FirthFragment : Fragment() {

    lateinit var binding: FragmentFirthBinding

    override fun onCreateView(
        inflater: LayoutInflater, container: ViewGroup?,
        savedInstanceState: Bundle?
    ): View? {
        binding = FragmentFirthBinding.inflate(inflater)

        binding.button.setOnClickListener {
            val phone = binding.phoneEt.text.toString()
            if (!phone.isNullOrBlank()){
                val bundle = Bundle()
                bundle.putString("phone",phone)
                findNavController().navigate(R.id.secondFragment,bundle)
            }else{
                Toast.makeText(requireContext(), "Iltimos raqam kiriting!", Toast.LENGTH_SHORT).show()
            }

        }


        return binding.root
    }

}