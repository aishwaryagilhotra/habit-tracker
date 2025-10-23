package com.example.habit_tracker_3

import DatabaseHelper
import SignUpFragment
import android.content.Intent
import android.os.Bundle
import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import android.widget.*
import androidx.appcompat.app.AlertDialog
import androidx.fragment.app.Fragment

class LoginFragment : Fragment() {

    private lateinit var emailEditText: EditText
    private lateinit var passwordEditText: EditText
    private lateinit var loginButton: Button
    private lateinit var signUpTextView: TextView
    private lateinit var forgotPasswordText: TextView
    private lateinit var dbHelper: DatabaseHelper

    override fun onCreateView(
        inflater: LayoutInflater, container: ViewGroup?,
        savedInstanceState: Bundle?
    ): View? {
        val view = inflater.inflate(R.layout.fragment_login_, container, false)

        dbHelper = DatabaseHelper(requireContext())

        // Initialize views
        emailEditText = view.findViewById(R.id.editTextEmail)
        passwordEditText = view.findViewById(R.id.editTextPassword)
        loginButton = view.findViewById(R.id.buttonLogin)
        signUpTextView = view.findViewById(R.id.textViewnewuser)
        forgotPasswordText = view.findViewById(R.id.textViewForgot)

        // Hide the name field since we don't need it for login
        val nameEditText = view.findViewById<EditText>(R.id.editTextName)
        nameEditText.visibility = View.GONE

        // Check if user is already logged in
        val currentUser = dbHelper.getCurrentUser(requireContext())
        if (currentUser != null) {
            navigateToHome()
            return view
        }

        setupClickListeners()

        return view
    }

    private fun setupClickListeners() {
        // Sign Up click listener
        signUpTextView.setOnClickListener {
            navigateToSignUp()
        }

        // Login button click listener
        loginButton.setOnClickListener {
            handleLogin()
        }

        // Forgot password click listener - IMPLEMENTED!
        forgotPasswordText.setOnClickListener {
            handleForgotPassword()
        }
    }

    private fun handleForgotPassword() {
        val email = emailEditText.text.toString().trim()

        if (email.isEmpty()) {
            showToast("Please enter your email first")
            return
        }

        val user = dbHelper.getUserByEmail(email)
        if (user != null) {
            // Show password recovery dialog
            showPasswordRecoveryDialog(user)
        } else {
            showToast("Email not found in our system")
        }
    }

    private fun showPasswordRecoveryDialog(user: User) {
        AlertDialog.Builder(requireContext())
            .setTitle("Password Recovery")
            .setMessage("Your password is: ${user.password}\n\nFor security reasons, please change your password after logging in.")
            .setPositiveButton("OK") { dialog, _ ->
                // Auto-fill the password field for convenience
                passwordEditText.setText(user.password)
                dialog.dismiss()
            }
            .setNegativeButton("Cancel") { dialog, _ ->
                dialog.dismiss()
            }
            .show()
    }

    private fun handleLogin() {
        val email = emailEditText.text.toString().trim()
        val password = passwordEditText.text.toString().trim()

        if (email.isEmpty() || password.isEmpty()) {
            showToast("Please fill all fields")
            return
        }

        val user = dbHelper.getUserByEmail(email)
        if (user != null && user.password == password) {
            dbHelper.saveCurrentUser(user, requireContext())
            showToast("Login successful!")
            navigateToHome()
        } else {
            showToast("Invalid credentials")
        }
    }

    private fun navigateToHome() {
        val intent = Intent(requireContext(), HomeActivity::class.java).apply {
            flags = Intent.FLAG_ACTIVITY_CLEAR_TASK or Intent.FLAG_ACTIVITY_NEW_TASK
        }
        startActivity(intent)
        requireActivity().finish()
    }

    private fun navigateToSignUp() {
        parentFragmentManager.beginTransaction()
            .replace(R.id.fragmentContainer, SignUpFragment())
            .addToBackStack(null)
            .commit()
    }

    private fun showToast(message: String) {
        Toast.makeText(requireContext(), message, Toast.LENGTH_SHORT).show()
    }
}