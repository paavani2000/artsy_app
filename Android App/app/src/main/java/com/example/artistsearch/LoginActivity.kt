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
//import com.example.artistsearch.network.LoginRequest
//import com.example.artistsearch.network.LoginResponse
//import com.google.android.material.snackbar.Snackbar
//import com.google.android.material.textfield.TextInputEditText
//import com.google.android.material.textfield.TextInputLayout
//import retrofit2.Call
//import retrofit2.Callback
//import retrofit2.Response
//import android.os.Handler
//import android.os.Looper
//
//
//
//class LoginActivity : AppCompatActivity() {
//
//    private lateinit var emailLayout: TextInputLayout
//    private lateinit var passwordLayout: TextInputLayout
//    private lateinit var emailField: TextInputEditText
//    private lateinit var passwordField: TextInputEditText
//    private lateinit var loginButton: Button
//    private lateinit var progressBar: ProgressBar
//    private lateinit var loginError: TextView
//    private lateinit var registerText: TextView
//
//
//    override fun onCreate(savedInstanceState: Bundle?) {
//        super.onCreate(savedInstanceState)
//        setContentView(R.layout.activity_login)
//
//        emailLayout = findViewById(R.id.emailInputLayout)
//        passwordLayout = findViewById(R.id.passwordInputLayout)
//        emailField = findViewById(R.id.editTextTextEmailAddress)
//        passwordField = findViewById(R.id.editTextTextPassword)
//        loginButton = findViewById(R.id.loginButton)
//        progressBar = findViewById(R.id.loginProgress)
//        loginError = findViewById(R.id.loginError)
//        registerText = findViewById(R.id.textView)
//
//        loginError.visibility = View.GONE
//        progressBar.visibility = View.GONE
//
//        findViewById<ImageButton>(R.id.searchIcon).setOnClickListener {
//            finish()
//        }
//
//        setupClickableRegister()
//        setupValidation()
//    }
//
//    private fun setupValidation() {
//        emailField.addTextChangedListener {
//            emailLayout.error = null
//        }
//
//        passwordField.addTextChangedListener {
//            passwordLayout.error = null
//        }
//
//        loginButton.setOnClickListener {
//            val email = emailField.text.toString().trim()
//            val password = passwordField.text.toString()
//
//            var valid = true
//
//            if (email.isEmpty()) {
//                emailLayout.error = "Email cannot be empty"
//                valid = false
//            } else if (!Patterns.EMAIL_ADDRESS.matcher(email).matches()) {
//                emailLayout.error = "Invalid email format"
//                valid = false
//            }
//
//            if (password.isEmpty()) {
//                passwordLayout.error = "Password cannot be empty"
//                valid = false
//            }
//
//            if (valid) {
//                loginButton.isEnabled = false
//                progressBar.visibility = View.VISIBLE
//                loginError.visibility = View.GONE
//
//                val request = LoginRequest(email, password)
//                val authService = ApiClient.getAuthService(this)
//
//                hideKeyboard()
//                authService.login(request).enqueue(object : Callback<LoginResponse> {
//                    override fun onResponse(call: Call<LoginResponse>, response: Response<LoginResponse>) {
//                        loginButton.isEnabled = true
//                        progressBar.visibility = View.GONE
//
//                        if (response.isSuccessful) {
//                            Toast.makeText(this@LoginActivity, "Logged in successfully", Toast.LENGTH_SHORT).show()
//
//                            val intent = Intent(this@LoginActivity, MainActivity::class.java)
//                            intent.flags = Intent.FLAG_ACTIVITY_NEW_TASK or Intent.FLAG_ACTIVITY_CLEAR_TASK
//                            startActivity(intent)
//                        } else {
//                            loginError.text = "Username or password is incorrect"
//                            loginError.visibility = View.VISIBLE
//                        }
//                    }
//
//
//                    override fun onFailure(call: Call<LoginResponse>, t: Throwable) {
//                        loginButton.isEnabled = true
//                        progressBar.visibility = View.GONE
//                        loginError.text = "Network error: ${t.message}"
//                        loginError.visibility = View.VISIBLE
//                    }
//                })
//            }
//        }
//    }
//
//    private fun setupClickableRegister() {
//        val fullText = "Don't have an account yet? Register"
//        val spannable = SpannableString(fullText)
//        val start = fullText.indexOf("Register")
//        val end = start + "Register".length
//
//        val clickableSpan = object : ClickableSpan() {
//            override fun onClick(widget: View) {
//                val intent = Intent(this@LoginActivity, RegisterActivity::class.java)
//                intent.addFlags(Intent.FLAG_ACTIVITY_CLEAR_TOP or Intent.FLAG_ACTIVITY_NEW_TASK)
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
//        registerText.text = spannable
//        registerText.movementMethod = LinkMovementMethod.getInstance()
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
import com.example.artistsearch.network.LoginRequest
import com.example.artistsearch.network.LoginResponse
import retrofit2.Call
import retrofit2.Callback
import retrofit2.Response
import com.google.android.material.textfield.TextInputEditText
import com.google.android.material.textfield.TextInputLayout

class LoginActivity : AppCompatActivity() {

    private lateinit var emailInput: TextInputEditText
    private lateinit var passwordInput: TextInputEditText
    private lateinit var emailLayout: TextInputLayout
    private lateinit var passwordLayout: TextInputLayout
    private lateinit var loginButton: Button
    private lateinit var loginProgress: ProgressBar
    private lateinit var errorMessage: TextView
    private lateinit var registerRedirect: TextView

    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        setContentView(R.layout.activity_login)

        emailLayout = findViewById(R.id.emailInputLayout)
        passwordLayout = findViewById(R.id.passwordInputLayout)
        emailInput = findViewById(R.id.editTextTextEmailAddress)
        passwordInput = findViewById(R.id.editTextTextPassword)
        loginButton = findViewById(R.id.loginButton)
        loginProgress = findViewById(R.id.loginProgress)
        errorMessage = findViewById(R.id.loginError)
        registerRedirect = findViewById(R.id.textView)

        errorMessage.visibility = View.GONE
        loginProgress.visibility = View.GONE

        findViewById<ImageButton>(R.id.searchIcon).setOnClickListener {
            finish()
        }

        setupClickableRegister()
        setupValidation()
    }

    private fun setupValidation() {
        // Clear errors on text change
        emailInput.addTextChangedListener { emailLayout.error = null }
        passwordInput.addTextChangedListener { passwordLayout.error = null }

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

        loginButton.setOnClickListener {
            val email = emailInput.text.toString().trim()
            val password = passwordInput.text.toString()

            var valid = true
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
                loginButton.isEnabled = false
                loginProgress.visibility = View.VISIBLE
                errorMessage.visibility = View.GONE

                val request = LoginRequest(email, password)
                val authService = ApiClient.getAuthService(this)

                authService.login(request).enqueue(object : Callback<LoginResponse> {
                    override fun onResponse(call: Call<LoginResponse>, response: Response<LoginResponse>) {
                        loginButton.isEnabled = true
                        loginProgress.visibility = View.GONE

                        if (response.isSuccessful) {
                            Toast.makeText(this@LoginActivity, "Logged in successfully", Toast.LENGTH_SHORT).show()

                            val intent = Intent(this@LoginActivity, MainActivity::class.java)
                            intent.flags = Intent.FLAG_ACTIVITY_NEW_TASK or Intent.FLAG_ACTIVITY_CLEAR_TASK
                            startActivity(intent)
                        } else {
                            errorMessage.text = "Username or password is incorrect"
                            errorMessage.visibility = View.VISIBLE
                        }
                    }

                    override fun onFailure(call: Call<LoginResponse>, t: Throwable) {
                        loginButton.isEnabled = true
                        loginProgress.visibility = View.GONE
                        errorMessage.text = "Network error: ${t.message}"
                        errorMessage.visibility = View.VISIBLE
                    }
                })
            }
        }
    }

    private fun setupClickableRegister() {
        val fullText = "Don't have an account yet? Register"
        val spannable = SpannableString(fullText)
        val start = fullText.indexOf("Register")
        val end = start + "Register".length

        val clickableSpan = object : ClickableSpan() {
            override fun onClick(widget: View) {
                val intent = Intent(this@LoginActivity, RegisterActivity::class.java)
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
        registerRedirect.text = spannable
        registerRedirect.movementMethod = LinkMovementMethod.getInstance()
    }

    private fun hideKeyboard() {
        val view = currentFocus
        if (view != null) {
            val imm = getSystemService(INPUT_METHOD_SERVICE) as android.view.inputmethod.InputMethodManager
            imm.hideSoftInputFromWindow(view.windowToken, 0)
        }
    }
}
