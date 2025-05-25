//package com.example.artistsearch
//
//import android.content.Intent
//import android.graphics.Color
//import android.os.Bundle
//import android.text.*
//import android.text.method.LinkMovementMethod
//import android.text.style.ClickableSpan
//import android.text.TextPaint
//import android.util.Patterns
//import android.view.View
//import android.widget.*
//import androidx.appcompat.app.AppCompatActivity
//import androidx.core.widget.addTextChangedListener
//import com.example.artistsearch.network.ApiClient
//import com.example.artistsearch.network.RegisterRequest
//import com.google.android.material.snackbar.Snackbar
//import com.google.android.material.textfield.TextInputEditText
//import com.google.android.material.textfield.TextInputLayout
//import retrofit2.Call
//import retrofit2.Callback
//import retrofit2.Response
//
//class RegisterActivity : AppCompatActivity() {
//
//    private lateinit var nameInput: TextInputEditText
//    private lateinit var emailInput: TextInputEditText
//    private lateinit var passwordInput: TextInputEditText
//    private lateinit var nameLayout: TextInputLayout
//    private lateinit var emailLayout: TextInputLayout
//    private lateinit var passwordLayout: TextInputLayout
//    private lateinit var registerButton: Button
//    private lateinit var registerProgress: ProgressBar
//    private lateinit var errorMessage: TextView
//    private lateinit var loginRedirect: TextView
//
//    override fun onCreate(savedInstanceState: Bundle?) {
//        super.onCreate(savedInstanceState)
//        setContentView(R.layout.activity_register)
//
//        nameLayout = findViewById(R.id.nameInputLayout)
//        emailLayout = findViewById(R.id.emailInputLayout)
//        passwordLayout = findViewById(R.id.passwordInputLayout)
//        nameInput = findViewById(R.id.editTextFullname)
//        emailInput = findViewById(R.id.editTextTextEmailAddress)
//        passwordInput = findViewById(R.id.editTextTextPassword)
//        registerButton = findViewById(R.id.button)
//        registerProgress = findViewById(R.id.registerProgress)
//        errorMessage = findViewById(R.id.errorMessageText)
//        loginRedirect = findViewById(R.id.textView)
//
//        errorMessage.visibility = View.GONE
//        registerProgress.visibility = View.GONE
//
//        findViewById<ImageButton>(R.id.searchIcon).setOnClickListener {
//            finish()
//        }
//
//        setupClickableLogin()
//        setupValidation()
//    }
//
//    private fun setupValidation() {
//        nameInput.addTextChangedListener { nameLayout.error = null }
//        emailInput.addTextChangedListener { emailLayout.error = null }
//        passwordInput.addTextChangedListener { passwordLayout.error = null }
//
//        registerButton.setOnClickListener {
//            val name = nameInput.text.toString().trim()
//            val email = emailInput.text.toString().trim()
//            val password = passwordInput.text.toString()
//
//            var valid = true
//            if (name.isEmpty()) {
//                nameLayout.error = "Name cannot be empty"
//                valid = false
//            }
//            if (email.isEmpty()) {
//                emailLayout.error = "Email cannot be empty"
//                valid = false
//            } else if (!Patterns.EMAIL_ADDRESS.matcher(email).matches()) {
//                emailLayout.error = "Invalid email format"
//                valid = false
//            }
//            if (password.isEmpty()) {
//                passwordLayout.error = "Password cannot be empty"
//                valid = false
//            }
//
//            if (valid) {
//                registerButton.isEnabled = false
//                registerProgress.visibility = View.VISIBLE
//                errorMessage.visibility = View.GONE
//
//                val request = RegisterRequest(name, email, password)
//                val authService = ApiClient.getAuthService(this)
//
//                authService.register(request).enqueue(object : Callback<Void> {
//                    override fun onResponse(call: Call<Void>, response: Response<Void>) {
//                        registerButton.isEnabled = true
//                        registerProgress.visibility = View.GONE
//
//                        if (response.isSuccessful) {
//                            Toast.makeText(this@RegisterActivity, "Registered successfully", Toast.LENGTH_SHORT).show()
//
//                            val intent = Intent(this@RegisterActivity, MainActivity::class.java)
//                            intent.flags = Intent.FLAG_ACTIVITY_NEW_TASK or Intent.FLAG_ACTIVITY_CLEAR_TASK
//                            startActivity(intent)
//                        } else {
//                            if (response.code() == 400 || response.code() == 409) {
//                                errorMessage.text = "Email already exists"
//                            } else {
//                                errorMessage.text = "Registration failed. Try again."
//                            }
//                            errorMessage.visibility = View.VISIBLE
//                        }
//                    }
//
//                    override fun onFailure(call: Call<Void>, t: Throwable) {
//                        registerButton.isEnabled = true
//                        registerProgress.visibility = View.GONE
//                        errorMessage.text = "Network error: ${t.message}"
//                        errorMessage.visibility = View.VISIBLE
//                    }
//                })
//            }
//        }
//    }
//
//    private fun setupClickableLogin() {
//        val fullText = "Already have an account? Login"
//        val spannable = SpannableString(fullText)
//        val start = fullText.indexOf("Login")
//        val end = start + "Login".length
//
//        val clickableSpan = object : ClickableSpan() {
//            override fun onClick(widget: View) {
//                val intent = Intent(this@RegisterActivity, LoginActivity::class.java)
//                intent.flags = Intent.FLAG_ACTIVITY_CLEAR_TOP or Intent.FLAG_ACTIVITY_NEW_TASK
//                startActivity(intent)
//                finish()
//            }
//
//            override fun updateDrawState(ds: TextPaint) {
//                super.updateDrawState(ds)
//                ds.color = Color.parseColor("#3366CC")
//                ds.isUnderlineText = true
//            }
//        }
//
//        spannable.setSpan(clickableSpan, start, end, Spanned.SPAN_EXCLUSIVE_EXCLUSIVE)
//        loginRedirect.text = spannable
//        loginRedirect.movementMethod = LinkMovementMethod.getInstance()
//    }
//
//    private fun hideKeyboard() {
//        val view = currentFocus
//        if (view != null) {
//            val imm = getSystemService(INPUT_METHOD_SERVICE) as android.view.inputmethod.InputMethodManager
//            imm.hideSoftInputFromWindow(view.windowToken, 0)
//        }
//    }
//
//}
//
package com.example.artistsearch

import android.content.Intent
import android.graphics.Color
import android.os.Bundle
import android.text.*
import android.text.method.LinkMovementMethod
import android.text.style.ClickableSpan
import android.text.TextPaint
import android.util.Patterns
import android.view.View
import android.widget.*
import androidx.appcompat.app.AppCompatActivity
import androidx.core.widget.addTextChangedListener
import com.example.artistsearch.network.ApiClient
import com.example.artistsearch.network.RegisterRequest
import com.google.android.material.snackbar.Snackbar
import com.google.android.material.textfield.TextInputEditText
import com.google.android.material.textfield.TextInputLayout
import retrofit2.Call
import retrofit2.Callback
import retrofit2.Response

class RegisterActivity : AppCompatActivity() {

    private lateinit var nameInput: TextInputEditText
    private lateinit var emailInput: TextInputEditText
    private lateinit var passwordInput: TextInputEditText
    private lateinit var nameLayout: TextInputLayout
    private lateinit var emailLayout: TextInputLayout
    private lateinit var passwordLayout: TextInputLayout
    private lateinit var registerButton: Button
    private lateinit var registerProgress: ProgressBar
    private lateinit var errorMessage: TextView
    private lateinit var loginRedirect: TextView

    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        setContentView(R.layout.activity_register)

        nameLayout = findViewById(R.id.nameInputLayout)
        emailLayout = findViewById(R.id.emailInputLayout)
        passwordLayout = findViewById(R.id.passwordInputLayout)
        nameInput = findViewById(R.id.editTextFullname)
        emailInput = findViewById(R.id.editTextTextEmailAddress)
        passwordInput = findViewById(R.id.editTextTextPassword)
        registerButton = findViewById(R.id.button)
        registerProgress = findViewById(R.id.registerProgress)
        errorMessage = findViewById(R.id.errorMessageText)
        loginRedirect = findViewById(R.id.textView)

        errorMessage.visibility = View.GONE
        registerProgress.visibility = View.GONE

        findViewById<ImageButton>(R.id.searchIcon).setOnClickListener {
            finish()
        }

        setupClickableLogin()
        setupValidation()
    }

    private fun setupValidation() {
        // Clear errors on text change
        nameInput.addTextChangedListener { nameLayout.error = null }
        emailInput.addTextChangedListener { emailLayout.error = null }
        passwordInput.addTextChangedListener { passwordLayout.error = null }

        // Validate name field when it gains focus
        nameInput.setOnFocusChangeListener { _, hasFocus ->
            if (hasFocus) {
                val name = nameInput.text.toString().trim()
                if (name.isEmpty()) {
                    nameLayout.error = "Name cannot be empty"
                } else {
                    nameLayout.error = null
                }
            }
        }

        // Validate email field when it gains focus
        emailInput.setOnFocusChangeListener { _, hasFocus ->
            if (hasFocus) {
                val email = emailInput.text.toString().trim()
                if (email.isEmpty()) {
                    emailLayout.error = "Email cannot be empty"
                } else if (!Patterns.EMAIL_ADDRESS.matcher(email).matches()) {
                    emailLayout.error = "Invalid email format"
                } else {
                    emailLayout.error = null
                }
            }
        }

        // Validate password field when it gains focus
        passwordInput.setOnFocusChangeListener { _, hasFocus ->
            if (hasFocus) {
                val password = passwordInput.text.toString()
                if (password.isEmpty()) {
                    passwordLayout.error = "Password cannot be empty"
                } else {
                    passwordLayout.error = null
                }
            }
        }

        registerButton.setOnClickListener {
            val name = nameInput.text.toString().trim()
            val email = emailInput.text.toString().trim()
            val password = passwordInput.text.toString()

            var valid = true
            if (name.isEmpty()) {
                nameLayout.error = "Name cannot be empty"
                valid = false
            }
            if (email.isEmpty()) {
                emailLayout.error = "Email cannot be empty"
                valid = false
            } else if (!Patterns.EMAIL_ADDRESS.matcher(email).matches()) {
                emailLayout.error = "Invalid email format"
                valid = false
            }
            if (password.isEmpty()) {
                passwordLayout.error = "Password cannot be empty"
                valid = false
            }

            if (valid) {
                registerButton.isEnabled = false
                registerProgress.visibility = View.VISIBLE
                errorMessage.visibility = View.GONE

                val request = RegisterRequest(name, email, password)
                val authService = ApiClient.getAuthService(this)

                authService.register(request).enqueue(object : Callback<Void> {
                    override fun onResponse(call: Call<Void>, response: Response<Void>) {
                        registerButton.isEnabled = true
                        registerProgress.visibility = View.GONE

                        if (response.isSuccessful) {
                            Toast.makeText(this@RegisterActivity, "Registered successfully", Toast.LENGTH_SHORT).show()

                            val intent = Intent(this@RegisterActivity, MainActivity::class.java)
                            intent.flags = Intent.FLAG_ACTIVITY_NEW_TASK or Intent.FLAG_ACTIVITY_CLEAR_TASK
                            startActivity(intent)
                        } else {
                            if (response.code() == 400 || response.code() == 409) {
                                errorMessage.text = "Email already exists"
                            } else {
                                errorMessage.text = "Registration failed. Try again."
                            }
                            errorMessage.visibility = View.VISIBLE
                        }
                    }

                    override fun onFailure(call: Call<Void>, t: Throwable) {
                        registerButton.isEnabled = true
                        registerProgress.visibility = View.GONE
                        errorMessage.text = "Network error: ${t.message}"
                        errorMessage.visibility = View.VISIBLE
                    }
                })
            }
        }
    }

    private fun setupClickableLogin() {
        val fullText = "Already have an account? Login"
        val spannable = SpannableString(fullText)
        val start = fullText.indexOf("Login")
        val end = start + "Login".length

        val clickableSpan = object : ClickableSpan() {
            override fun onClick(widget: View) {
                val intent = Intent(this@RegisterActivity, LoginActivity::class.java)
                intent.flags = Intent.FLAG_ACTIVITY_CLEAR_TOP or Intent.FLAG_ACTIVITY_NEW_TASK
                startActivity(intent)
                finish()
            }

            override fun updateDrawState(ds: TextPaint) {
                super.updateDrawState(ds)
                ds.color = Color.parseColor("#3366CC")
                ds.isUnderlineText = true
            }
        }

        spannable.setSpan(clickableSpan, start, end, Spanned.SPAN_EXCLUSIVE_EXCLUSIVE)
        loginRedirect.text = spannable
        loginRedirect.movementMethod = LinkMovementMethod.getInstance()
    }

    private fun hideKeyboard() {
        val view = currentFocus
        if (view != null) {
            val imm = getSystemService(INPUT_METHOD_SERVICE) as android.view.inputmethod.InputMethodManager
            imm.hideSoftInputFromWindow(view.windowToken, 0)
        }
    }
}
