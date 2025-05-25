//package com.example.artistsearch
//
//import android.app.Dialog
//import android.content.Context
//import android.graphics.drawable.GradientDrawable
//import android.os.Bundle
//import android.view.Window
//import android.view.WindowManager
//import coil.load
//import com.example.artistsearch.databinding.DialogCategoryBinding
//import kotlinx.coroutines.CoroutineScope
//import kotlinx.coroutines.Dispatchers
//import kotlinx.coroutines.launch
//import kotlinx.coroutines.withContext
//import retrofit2.Retrofit
//import retrofit2.converter.gson.GsonConverterFactory
//
//class CategoryDialog(context: Context, private val artworkId: String) : Dialog(context) {
//
//    private lateinit var binding: DialogCategoryBinding
//    private var categories: List<Category> = emptyList()
//    private var currentIndex = 0
//
//    override fun onCreate(savedInstanceState: Bundle?) {
//        super.onCreate(savedInstanceState)
//        requestWindowFeature(Window.FEATURE_NO_TITLE)
//
//        binding = DialogCategoryBinding.inflate(layoutInflater)
//        setContentView(binding.root)
//
//        // Window style
//        window?.apply {
//            setBackgroundDrawableResource(android.R.color.transparent)
//            setLayout(
//                (context.resources.displayMetrics.widthPixels * 0.9).toInt(),
//                WindowManager.LayoutParams.WRAP_CONTENT
//            )
//        }
//
//        // Rounded background
//        binding.root.background = GradientDrawable().apply {
//            shape = GradientDrawable.RECTANGLE
//            cornerRadius = 48f
//            setColor(context.getColor(android.R.color.white))
//        }
//
//        binding.closeButton.setOnClickListener {
//            dismiss()
//        }
//
//        binding.prevButton.setOnClickListener {
//            if (categories.isNotEmpty()) {
//                currentIndex = if (currentIndex - 1 < 0) categories.size - 1 else currentIndex - 1
//                updateCategory()
//            }
//        }
//
//        binding.nextButton.setOnClickListener {
//            if (categories.isNotEmpty()) {
//                currentIndex = (currentIndex + 1) % categories.size
//                updateCategory()
//            }
//        }
//
//        fetchCategories()
//    }
//
//    private fun fetchCategories() {
//        val retrofit = Retrofit.Builder()
//            .baseUrl("https://android-backend-458823.wl.r.appspot.com/")
//            .addConverterFactory(GsonConverterFactory.create())
//            .build()
//
//        val apiService = retrofit.create(ApiService::class.java)
//
//        CoroutineScope(Dispatchers.IO).launch {
//            try {
//                val response = apiService.getCategories(artworkId)
//                withContext(Dispatchers.Main) {
//                    categories = response.embedded.genes
//                    if (categories.isNotEmpty()) {
//                        currentIndex = 0
//                        updateCategory()
//                    } else {
//                        showNoCategory()
//                    }
//                }
//            } catch (e: Exception) {
//                withContext(Dispatchers.Main) {
//                    showNoCategory()
//                }
//            }
//        }
//    }
//
//    private fun updateCategory() {
//        val category = categories[currentIndex]
//
//        binding.categoryName.text = category.name
//        binding.categoryDescription.text = category.description ?: "No description available"
//
//        val imageUrl = category.links.thumbnail?.href
//        if (!imageUrl.isNullOrEmpty()) {
//            binding.categoryImage.load(imageUrl) {
//                placeholder(R.drawable.ic_launcher_foreground)
//                error(R.drawable.ic_launcher_foreground)
//                crossfade(true)
//            }
//        } else {
//            binding.categoryImage.setImageResource(R.drawable.ic_launcher_foreground)
//        }
//    }
//
//    private fun showNoCategory() {
//        binding.categoryName.text = "No categories"
//        binding.categoryDescription.text = ""
//        binding.categoryImage.setImageResource(R.drawable.ic_launcher_foreground)
//    }
//}

package com.example.artistsearch

import android.app.Dialog
import android.content.Context
import android.graphics.drawable.GradientDrawable
import android.os.Bundle
import android.view.View
import android.view.Window
import android.view.WindowManager
import coil.load
import com.example.artistsearch.databinding.DialogCategoryBinding
import kotlinx.coroutines.*
import retrofit2.Retrofit
import retrofit2.converter.gson.GsonConverterFactory
import androidx.core.text.HtmlCompat


class CategoryDialog(context: Context, private val artworkId: String) : Dialog(context) {

    private lateinit var binding: DialogCategoryBinding
    private var categories: List<Category> = emptyList()
    private var currentIndex = 0

    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        requestWindowFeature(Window.FEATURE_NO_TITLE)

        binding = DialogCategoryBinding.inflate(layoutInflater)
        setContentView(binding.root)

        window?.apply {
            setBackgroundDrawableResource(android.R.color.transparent)
            setLayout(
                (context.resources.displayMetrics.widthPixels * 0.85).toInt(), // narrow width
                (context.resources.displayMetrics.heightPixels * 0.7).toInt() // tall height
            )
            setGravity(android.view.Gravity.CENTER)
        }

        binding.root.background = GradientDrawable().apply {
            shape = GradientDrawable.RECTANGLE
            cornerRadius = 48f
            setColor(context.getColor(R.color.screenBack))
        }

        binding.closeButton.setOnClickListener { dismiss() }

        binding.prevButton.setOnClickListener {
            if (categories.isNotEmpty()) {
                currentIndex = if (currentIndex - 1 < 0) categories.size - 1 else currentIndex - 1
                updateCategory()
            }
        }

        binding.nextButton.setOnClickListener {
            if (categories.isNotEmpty()) {
                currentIndex = (currentIndex + 1) % categories.size
                updateCategory()
            }
        }

        fetchCategories()
    }

    private fun fetchCategories() {
        showLoading(true)

        val retrofit = Retrofit.Builder()
            .baseUrl("https://android-backend-458823.wl.r.appspot.com/")
            .addConverterFactory(GsonConverterFactory.create())
            .build()

        val apiService = retrofit.create(ApiService::class.java)

        CoroutineScope(Dispatchers.IO).launch {
            try {
                val response = apiService.getCategories(artworkId)
                withContext(Dispatchers.Main) {
                    categories = response.embedded.genes
                    showLoading(false)
                    if (categories.isNotEmpty()) {
                        currentIndex = 0
                        showCategoryViews(true)
                        updateCategory()
                    } else {
                        showNoCategory()
                    }
                }
            } catch (e: Exception) {
                withContext(Dispatchers.Main) {
                    showLoading(false)
                    showNoCategory()
                }
            }
        }
    }

    private fun updateCategory() {
        val category = categories[currentIndex]

        binding.categoryName.text = category.name

        binding.categoryDescription.text = HtmlCompat.fromHtml(
            category.description ?: "No description available",
            HtmlCompat.FROM_HTML_MODE_LEGACY
        )

        val imageUrl = category.links.thumbnail?.href
        if (!imageUrl.isNullOrEmpty()) {
            binding.categoryImage.load(imageUrl) {
                placeholder(R.drawable.ic_launcher_foreground)
                error(R.drawable.ic_launcher_foreground)
                crossfade(true)
            }
        } else {
            binding.categoryImage.setImageResource(R.drawable.ic_launcher_foreground)
        }
    }

    private fun showNoCategory() {
        showCategoryViews(true)
        binding.categoryName.text = "No categories"
        binding.categoryDescription.text = ""
        binding.categoryImage.setImageResource(R.drawable.ic_launcher_foreground)
    }

    private fun showLoading(loading: Boolean) {
        binding.categoryLoading.visibility = if (loading) View.VISIBLE else View.GONE
        showCategoryViews(!loading)
    }

    private fun showCategoryViews(visible: Boolean) {
        val visibility = if (visible) View.VISIBLE else View.GONE
        binding.categoryCard.visibility = visibility
        binding.categoryName.visibility = visibility
        binding.categoryDescription.visibility = visibility
        binding.categoryImage.visibility = visibility
        binding.prevButton.visibility = visibility
        binding.nextButton.visibility = visibility
    }
}

