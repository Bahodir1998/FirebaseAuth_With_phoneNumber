package com.example.firebaseauth

import android.os.Bundle
import androidx.fragment.app.Fragment
import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import com.example.firebaseauth.databinding.FragmentThirdBinding

class ThirdFragment : Fragment() {

    lateinit var binding: FragmentThirdBinding
    override fun onCreateView(
        inflater: LayoutInflater, container: ViewGroup?,
        savedInstanceState: Bundle?
    ): View? {
        binding = FragmentThirdBinding.inflate(inflater)

        val phone = arguments?.getString("phone")
        val p1 = phone?.subSequence(4,6)
        val p2 = phone?.subSequence(6,9)
        val p3 = phone?.subSequence(9,11)
        val p4 = phone?.subSequence(11,13)
        binding.phoneNumber.setText("(+998 $p1) $p2-$p3-$p4")
        return binding.root
    }

}