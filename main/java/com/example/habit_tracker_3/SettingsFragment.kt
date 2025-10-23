package com.example.habit_tracker_3

import DatabaseHelper
import android.content.Intent
import android.graphics.Bitmap
import android.graphics.BitmapFactory
import android.net.Uri
import android.os.Bundle
import android.util.Log
import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import android.widget.*
import androidx.activity.result.contract.ActivityResultContracts
import androidx.fragment.app.Fragment
import androidx.appcompat.app.AlertDialog
import java.io.IOException

class SettingsFragment(
    private val userId: Int,
    private val dbHelper: DatabaseHelper,
    private val habitList: MutableList<Habit>?
) : Fragment() {

    private lateinit var imageProfile: ImageView
    private lateinit var textUsername: TextView
    private lateinit var textGender: TextView
    private lateinit var btnChangeProfile: LinearLayout
    private lateinit var btnClearHabits: LinearLayout
    private lateinit var btnAbout: LinearLayout
    private lateinit var btnLogout: LinearLayout

    private var selectedImageBitmap: Bitmap? = null

    // Image picker for profile picture
    private val imagePicker = registerForActivityResult(
        ActivityResultContracts.GetContent()
    ) { uri: Uri? ->
        uri?.let {
            try {
                val inputStream = requireContext().contentResolver.openInputStream(uri)
                selectedImageBitmap = BitmapFactory.decodeStream(inputStream)
                // Resize image
                selectedImageBitmap = resizeBitmap(selectedImageBitmap, 300, 300)

                // Update the profile image in settings
                imageProfile.setImageBitmap(selectedImageBitmap)

                // Try to save the updated profile picture to database
                try {
                    val success = dbHelper.updateUserProfilePic(userId, selectedImageBitmap)
                    if (success) {
                        showToast("Profile picture updated!")
                    } else {
                        showToast("Failed to save profile picture")
                    }
                } catch (e: Exception) {
                    Log.e("SettingsFragment", "updateUserProfilePic not available: ${e.message}")
                    showToast("Profile picture updated (not saved to database)")
                }

                inputStream?.close()
            } catch (e: IOException) {
                e.printStackTrace()
                showToast("Failed to load image")
            }
        }
    }

    override fun onCreateView(
        inflater: LayoutInflater, container: ViewGroup?,
        savedInstanceState: Bundle?
    ): View? {
        val view = inflater.inflate(R.layout.fragment_settings, container, false)
        Log.d("SettingsFragment", "onCreateView called")

        initializeViews(view)
        loadUserData()
        setupClickListeners()

        return view
    }

    private fun initializeViews(view: View) {
        try {
            imageProfile = view.findViewById(R.id.imageProfile)
            textUsername = view.findViewById(R.id.textUsername)
            textGender = view.findViewById(R.id.textGender)
            btnChangeProfile = view.findViewById(R.id.btnChangeProfile)
            btnClearHabits = view.findViewById(R.id.btnClearHabits)
            btnAbout = view.findViewById(R.id.btnAbout)
            btnLogout = view.findViewById(R.id.btnLogout)

            Log.d("SettingsFragment", "Views initialized successfully")
        } catch (e: Exception) {
            Log.e("SettingsFragment", "Error initializing views: ${e.message}")
            e.printStackTrace()
        }
    }

    private fun loadUserData() {
        try {
            Log.d("SettingsFragment", "Loading user data...")
            val currentUser = dbHelper.getCurrentUser(requireContext())

            if (currentUser != null) {
                Log.d("SettingsFragment", "User found: ${currentUser.username}")
                textUsername.text = currentUser.username
                textGender.text = "${currentUser.gender} • ${currentUser.email}"

                // Set profile picture if available
                if (currentUser.profilePic != null) {
                    imageProfile.setImageBitmap(currentUser.profilePic)
                } else {
                    imageProfile.setImageResource(R.drawable.user)
                }
            } else {
                Log.e("SettingsFragment", "No user found in database")
                showToast("User not found")
                textUsername.text = "Unknown User"
                textGender.text = "Not set • No email"
            }
        } catch (e: Exception) {
            Log.e("SettingsFragment", "Error loading user data: ${e.message}")
            showToast("Error loading user data")
        }
    }

    private fun setupClickListeners() {
        Log.d("SettingsFragment", "Setting up click listeners")

        btnChangeProfile.setOnClickListener {
            Log.d("SettingsFragment", "Change Profile clicked")
            setupProfileEditing()
        }

        btnClearHabits.setOnClickListener {
            Log.d("SettingsFragment", "Clear Habits clicked")
            setupClearHabits()
        }

        btnAbout.setOnClickListener {
            Log.d("SettingsFragment", "About clicked")
            setupAbout()
        }

        btnLogout.setOnClickListener {
            Log.d("SettingsFragment", "Logout clicked")
            setupLogout()
        }

        // Make profile image clickable too
        imageProfile.setOnClickListener {
            showToast("Tap 'Edit Profile' to change picture")
        }
    }

    private fun setupProfileEditing() {
        val currentUser = dbHelper.getCurrentUser(requireContext())
        val dialogView = layoutInflater.inflate(R.layout.dialog_edit_profile, null)

        val editUsername = dialogView.findViewById<EditText>(R.id.editUsername)
        val editGender = dialogView.findViewById<EditText>(R.id.editGender)
        val btnChangePic = dialogView.findViewById<Button>(R.id.btnChangePic)

        // Pre-fill with current data
        editUsername.setText(currentUser?.username ?: "")
        editGender.setText(currentUser?.gender ?: "")

        val dialog = AlertDialog.Builder(requireContext())
            .setTitle("Edit Profile")
            .setView(dialogView)
            .setPositiveButton("Save") { _, _ ->
                val newUsername = editUsername.text.toString().trim()
                val newGender = editGender.text.toString().trim()

                if (newUsername.isEmpty() || newGender.isEmpty()) {
                    showToast("Please fill all fields")
                    return@setPositiveButton
                }

                // Update UI
                textUsername.text = newUsername
                textGender.text = "$newGender • ${currentUser?.email ?: ""}"

                // Try to update user data in database
                try {
                    val success = dbHelper.updateUserData(userId, newUsername, newGender)
                    if (success) {
                        showToast("Profile updated successfully!")
                    } else {
                        showToast("Failed to update profile")
                    }
                } catch (e: Exception) {
                    Log.e("SettingsFragment", "updateUserData not available: ${e.message}")
                    showToast("Profile updated (UI only)")
                }
            }
            .setNegativeButton("Cancel", null)
            .create()

        dialog.show()

        // Handle profile picture change in dialog
        btnChangePic.setOnClickListener {
            openImagePicker()
        }
    }

    private fun openImagePicker() {
        imagePicker.launch("image/*")
    }

    private fun resizeBitmap(bitmap: Bitmap?, maxWidth: Int, maxHeight: Int): Bitmap? {
        if (bitmap == null) return null

        var width = bitmap.width
        var height = bitmap.height

        if (width > maxWidth || height > maxHeight) {
            val ratio = width.toFloat() / height.toFloat()

            if (ratio > 1) {
                width = maxWidth
                height = (width / ratio).toInt()
            } else {
                height = maxHeight
                width = (height * ratio).toInt()
            }

            return Bitmap.createScaledBitmap(bitmap, width, height, true)
        }

        return bitmap
    }

    private fun setupClearHabits() {
        if (!habitList.isNullOrEmpty()) {
            AlertDialog.Builder(requireContext())
                .setTitle("Clear All Habits")
                .setMessage("Are you sure you want to delete all habits?")
                .setPositiveButton("Yes") { _, _ ->
                    habitList.forEach { dbHelper.deleteHabit(it.id) }
                    habitList.clear()
                    showToast("All habits cleared")
                }
                .setNegativeButton("No", null)
                .show()
        } else {
            showToast("No habits to clear")
        }
    }

    private fun setupAbout() {
        AlertDialog.Builder(requireContext())
            .setTitle("About")
            .setMessage("Habit Tracker App\nVersion 1.0\nCreated by You")
            .setPositiveButton("OK", null)
            .show()
    }

    private fun setupLogout() {
        AlertDialog.Builder(requireContext())
            .setTitle("Logout")
            .setMessage("Are you sure you want to logout?")
            .setPositiveButton("Yes") { _, _ ->
                dbHelper.logout(requireContext())
                val intent = Intent(requireContext(), MainActivity::class.java)
                intent.flags = Intent.FLAG_ACTIVITY_CLEAR_TASK or Intent.FLAG_ACTIVITY_NEW_TASK
                startActivity(intent)
                requireActivity().finish()
            }
            .setNegativeButton("No", null)
            .show()
    }

    private fun showToast(message: String) {
        Toast.makeText(requireContext(), message, Toast.LENGTH_SHORT).show()
    }
}