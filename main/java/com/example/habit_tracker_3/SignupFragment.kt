import android.content.Intent
import android.graphics.Bitmap
import android.graphics.BitmapFactory
import android.net.Uri
import android.os.Bundle
import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import android.widget.Button
import android.widget.EditText
import android.widget.ImageView
import android.widget.RadioGroup
import android.widget.TextView
import android.widget.Toast
import androidx.activity.result.contract.ActivityResultContracts
import androidx.fragment.app.Fragment
import com.example.habit_tracker_3.HomeActivity
import com.example.habit_tracker_3.LoginFragment
import com.example.habit_tracker_3.R
import com.example.habit_tracker_3.User
import java.io.IOException

class SignUpFragment : Fragment() {

    private lateinit var usernameEdit: EditText
    private lateinit var emailEdit: EditText
    private lateinit var passwordEdit: EditText
    private lateinit var signUpButton: Button
    private lateinit var loginTextView: TextView
    private lateinit var genderGroup: RadioGroup
    private lateinit var imagePreview: ImageView
    private lateinit var uploadButton: Button
    private lateinit var dbHelper: DatabaseHelper

    private var selectedImageBitmap: Bitmap? = null

    // Image picker
    private val imagePicker = registerForActivityResult(
        ActivityResultContracts.GetContent()
    ) { uri: Uri? ->
        uri?.let {
            try {
                val inputStream = requireContext().contentResolver.openInputStream(uri)
                selectedImageBitmap = BitmapFactory.decodeStream(inputStream)
                selectedImageBitmap = resizeBitmap(selectedImageBitmap, 300, 300)
                imagePreview.setImageBitmap(selectedImageBitmap)
                inputStream?.close()
                Toast.makeText(requireContext(), "Picture selected!", Toast.LENGTH_SHORT).show()
            } catch (e: IOException) {
                e.printStackTrace()
                Toast.makeText(requireContext(), "Failed to load image", Toast.LENGTH_SHORT).show()
            }
        }
    }

    override fun onCreateView(
        inflater: LayoutInflater, container: ViewGroup?,
        savedInstanceState: Bundle?
    ): View? {
        val view = inflater.inflate(R.layout.fragment_signup, container, false)

        dbHelper = DatabaseHelper(requireContext())


        usernameEdit = view.findViewById(R.id.editUsername)
        emailEdit = view.findViewById(R.id.editTextEmail)
        passwordEdit = view.findViewById(R.id.editTextPassword)
        signUpButton = view.findViewById(R.id.buttonSignup)
        loginTextView = view.findViewById(R.id.textViewLogin)
        genderGroup = view.findViewById(R.id.genderGroup)
        imagePreview = view.findViewById(R.id.imagePreview)
        uploadButton = view.findViewById(R.id.btnUpload)

        setupClickListeners()

        return view
    }

    private fun setupClickListeners() {
        signUpButton.setOnClickListener {
            handleSignUp()
        }

        loginTextView.setOnClickListener {
            navigateToLogin()
        }

        uploadButton.setOnClickListener {
            openImagePicker()
        }

        imagePreview.setOnClickListener {
            openImagePicker()
        }
    }

    private fun openImagePicker() {
        imagePicker.launch("image/*")
    }

    private fun handleSignUp() {
        val username = usernameEdit.text.toString().trim()
        val email = emailEdit.text.toString().trim()
        val password = passwordEdit.text.toString().trim()

        val selectedGenderId = genderGroup.checkedRadioButtonId
        if (selectedGenderId == -1) {
            Toast.makeText(requireContext(), "Please select gender", Toast.LENGTH_SHORT).show()
            return
        }

        val gender = when (selectedGenderId) {
            R.id.radioMale -> "Male"
            R.id.radioFemale -> "Female"
            else -> return
        }

        if (username.isEmpty() || email.isEmpty() || password.isEmpty()) {
            Toast.makeText(requireContext(), "Please fill all fields", Toast.LENGTH_SHORT).show()
            return
        }

        val existing = dbHelper.getUserByEmail(email)
        if (existing != null) {
            Toast.makeText(requireContext(), "Email already registered", Toast.LENGTH_SHORT).show()
            return
        }

        val user = User(
            id = 0,
            username = username,
            email = email,
            password = password,
            gender = gender,
            profilePic = selectedImageBitmap
        )

        val id = dbHelper.addUser(user)
        if (id != -1L) {
            val newUser = user.copy(id = id.toInt())
            dbHelper.saveCurrentUser(newUser, requireContext())
            Toast.makeText(requireContext(), "Registration successful!", Toast.LENGTH_SHORT).show()
            navigateToHome()
        } else {
            Toast.makeText(requireContext(), "Registration failed", Toast.LENGTH_SHORT).show()
        }
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

    private fun navigateToHome() {
        val intent = Intent(requireContext(), HomeActivity::class.java).apply {
            flags = Intent.FLAG_ACTIVITY_CLEAR_TASK or Intent.FLAG_ACTIVITY_NEW_TASK
        }
        startActivity(intent)
        requireActivity().finish()
    }

    private fun navigateToLogin() {
        parentFragmentManager.beginTransaction()
            .replace(R.id.fragmentContainer, LoginFragment())
            .addToBackStack(null)
            .commit()
    }
}