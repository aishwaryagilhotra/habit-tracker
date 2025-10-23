package com.example.habit_tracker_3

import DatabaseHelper
import android.os.Bundle
import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import android.widget.*
import androidx.appcompat.app.AlertDialog
import androidx.fragment.app.Fragment

class ForgotPasswordFragment : Fragment() {

    private lateinit var emailEditText: EditText
    private lateinit var resetButton: Button
    private lateinit var backToLogin: TextView
    private lateinit var dbHelper: DatabaseHelper

    override fun onCreateView(
        inflater: LayoutInflater, container: ViewGroup?,
        savedInstanceState: Bundle?
    ): View? {
        val view = inflater.inflate(R.layout.fragment_forgot_password, container, false)

        dbHelper = DatabaseHelper(requireContext())

        emailEditText = view.findViewById(R.id.editTextEmail)
        resetButton = view.findViewById(R.id.buttonReset)
        backToLogin = view.findViewById(R.id.textBackToLogin)

        setupClickListeners()

        return view
    }

    private fun setupClickListeners() {
        resetButton.setOnClickListener {
            handlePasswordReset()
        }

        backToLogin.setOnClickListener {
            navigateToLogin()
        }
    }

    private fun handlePasswordReset() {
        val email = emailEditText.text.toString().trim()

        if (email.isEmpty()) {
            showToast("Please enter your email")
            return
        }

        val user = dbHelper.getUserByEmail(email)
        if (user != null) {
            // Show current password (since we don't have email sending)
            showPasswordDialog(user)
        } else {
            showToast("Email not found")
        }
    }

    private fun showPasswordDialog(user: User) {
        AlertDialog.Builder(requireContext())
            .setTitle("Password Recovery")
            .setMessage("Your password is: ${user.password}\n\nPlease change it after logging in for security.")
            .setPositiveButton("OK") { dialog, _ ->
                navigateToLogin()
                dialog.dismiss()
            }
            .show()
    }

    private fun navigateToLogin() {
        parentFragmentManager.beginTransaction()
            .replace(R.id.fragmentContainer, LoginFragment())
            .commit()
    }

    private fun showToast(message: String) {
        Toast.makeText(requireContext(), message, Toast.LENGTH_SHORT).show()
    }
}