package com.example.firebaseauth

import android.annotation.SuppressLint
import android.content.Context.INPUT_METHOD_SERVICE
import android.os.Bundle
import android.os.CountDownTimer
import android.util.Log
import androidx.fragment.app.Fragment
import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import android.view.inputmethod.EditorInfo
import android.view.inputmethod.InputMethodManager
import androidx.core.content.ContextCompat.getSystemService
import androidx.core.widget.addTextChangedListener
import androidx.navigation.Navigation
import androidx.navigation.fragment.findNavController
import com.example.firebaseauth.databinding.FragmentSecondBinding
import com.google.firebase.FirebaseException
import com.google.firebase.FirebaseTooManyRequestsException
import com.google.firebase.auth.*
import java.util.concurrent.TimeUnit

class SecondFragment : Fragment() {

    lateinit var binding: FragmentSecondBinding
    lateinit var auth: FirebaseAuth
    lateinit var storedVerificationId: String
    lateinit var resendToken: PhoneAuthProvider.ForceResendingToken
    private val TAG = "SecondFragment"
    private lateinit var phoneNumber: String
    var isActive = false
    override fun onCreateView(
        inflater: LayoutInflater, container: ViewGroup?,
        savedInstanceState: Bundle?
    ): View? {
        binding = FragmentSecondBinding.inflate(inflater)
        auth = FirebaseAuth.getInstance()
        phoneNumber = arguments?.getString("phone").toString()
        var p1 = phoneNumber.subSequence(4,6)
        var p2 = phoneNumber.subSequence(6,9)
        binding.tv2.setText("Bir martalik kod  (+998 $p1) $p2-**-**\nraqamiga yuborildi")
        sentVerificationCode(phoneNumber!!)

        binding.smsEt.setOnEditorActionListener { v, actionId, event ->
            if (actionId == EditorInfo.IME_ACTION_DONE){
                verifyCode()
                val view = requireActivity().currentFocus
                if (view != null){
                    val imm: InputMethodManager = requireActivity().getSystemService(INPUT_METHOD_SERVICE) as InputMethodManager
                    imm.hideSoftInputFromWindow(view.windowToken,0)
                }
            }
            true
        }
        binding.smsEt.addTextChangedListener {
            if (it.toString().length == 6) {
                verifyCode()
            }
        }

        binding.resendTv.setOnClickListener {
            if (isActive){
                resentVerificationCode(phoneNumber)
            }
        }
        return binding.root
    }


    fun sentVerificationCode(phoneNumber: String) {
        val options = PhoneAuthOptions.newBuilder(auth)
            .setPhoneNumber(phoneNumber)       // Phone number to verify
            .setTimeout(60L, TimeUnit.SECONDS) // Timeout and unit
            .setActivity(requireActivity())                 // Activity (for callback binding)
            .setCallbacks(callbacks)          // OnVerificationStateChangedCallbacks
            .build()
        PhoneAuthProvider.verifyPhoneNumber(options)
    }

    fun resentVerificationCode(phoneNumber: String) {
        val options = PhoneAuthOptions.newBuilder(auth)
            .setPhoneNumber(phoneNumber)       // Phone number to verify
            .setTimeout(60L, TimeUnit.SECONDS) // Timeout and unit
            .setActivity(requireActivity())                 // Activity (for callback binding)
            .setCallbacks(callbacks)          // OnVerificationStateChangedCallbacks
            .setForceResendingToken(resendToken)
            .build()
        PhoneAuthProvider.verifyPhoneNumber(options)
    }

    private var callbacks = object : PhoneAuthProvider.OnVerificationStateChangedCallbacks() {

        override fun onVerificationCompleted(credential: PhoneAuthCredential) {
            // This callback will be invoked in two situations:
            // 1 - Instant verification. In some cases the phone number can be instantly
            //     verified without needing to send or enter a verification code.
            // 2 - Auto-retrieval. On some devices Google Play services can automatically
            //     detect the incoming verification SMS and perform verification without
            //     user action.
            Log.d(TAG, "onVerificationCompleted:$credential")
            signInWithPhoneAuthCredential(credential)
        }

        override fun onVerificationFailed(e: FirebaseException) {
            // This callback is invoked in an invalid request for verification is made,
            // for instance if the the phone number format is not valid.
            Log.w(TAG, "onVerificationFailed", e)

            if (e is FirebaseAuthInvalidCredentialsException) {
                // Invalid request
            } else if (e is FirebaseTooManyRequestsException) {
                // The SMS quota for the project has been exceeded
            }

            // Show a message and update the UI
        }

        override fun onCodeSent(
            verificationId: String,
            token: PhoneAuthProvider.ForceResendingToken
        ) {
            // The SMS verification code has been sent to the provided phone number, we
            // now need to ask the user to enter the code and then construct a credential
            // by combining the code with a verification ID.
            Log.d(TAG, "onCodeSent:$verificationId")
            val timer = object: CountDownTimer(59000, 1000) {
                @SuppressLint("ResourceAsColor")
                override fun onTick(millisUntilFinished: Long) {
                    val sec = (millisUntilFinished / 1000).toInt()
                    val second = if (sec>9) "$sec" else "0$sec"
                    binding.tv4.setText("00:$second")
                    isActive = false
                    binding.resendTv.setHintTextColor(R.color.old_color)
                }

                @SuppressLint("ResourceAsColor")
                override fun onFinish() {
                    isActive = true
                    binding.resendTv.setHintTextColor(R.color.new_color)
                }
            }
            timer.start()
            // Save verification ID and resending token so we can use them later
            storedVerificationId = verificationId
            resendToken = token
        }
    }

    private fun signInWithPhoneAuthCredential(credential: PhoneAuthCredential) {
        auth.signInWithCredential(credential)
            .addOnCompleteListener(requireActivity()) { task ->
                if (task.isSuccessful) {

                    Log.d(TAG, "signInWithCredential:success")

                    val phone = task.result?.user?.phoneNumber
                    val bundle = Bundle()
                    bundle.putString("phone",phone)
                    val controller = Navigation.findNavController(binding.root)
                    controller.popBackStack(R.id.firthFragment, true)
                    controller.popBackStack(R.id.secondFragment, true)
                    controller.navigate(R.id.thirdFragment,bundle)
                } else {
                    // Sign in failed, display a message and update the UI
                    Log.w(TAG, "signInWithCredential:failure", task.exception)
                    if (task.exception is FirebaseAuthInvalidCredentialsException) {
                        // The verification code entered was invalid
                    }
                    // Update UI
                }
            }
    }

    private fun verifyCode() {
        val code = binding.smsEt.text.toString()
        if (code.length == 6) {
            val credential = PhoneAuthProvider.getCredential(storedVerificationId, code)
            signInWithPhoneAuthCredential(credential)
        }
    }
}