package com.example.suitmedia_intern

import android.content.Intent
import android.os.Bundle
import androidx.activity.enableEdgeToEdge
import androidx.appcompat.app.AppCompatActivity
import androidx.core.view.ViewCompat
import androidx.core.view.WindowInsetsCompat
import com.example.suitmedia_intern.databinding.ActivityMainBinding

class MainActivity : AppCompatActivity() {

    private lateinit var binding: ActivityMainBinding

    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        enableEdgeToEdge()

        binding = ActivityMainBinding.inflate(layoutInflater)
        setContentView(binding.root)

        ViewCompat.setOnApplyWindowInsetsListener(binding.main) { v, insets ->
            val systemBars = insets.getInsets(WindowInsetsCompat.Type.systemBars())
            v.setPadding(systemBars.left, systemBars.top, systemBars.right, systemBars.bottom)
            insets
        }

        binding.checkButton.setOnClickListener {
            val palindromeText = binding.palindromeEditText.text.toString()
            if (isPalindrome(palindromeText)) {
                showDialog("isPalindrome")
            } else {
                showDialog("not palindrome")
            }
        }

        binding.nextButton.setOnClickListener {
            val name = binding.nameEditText.text.toString()
            val intent = Intent(this, MainActivity2::class.java).apply {
                putExtra("USERNAME", name)
            }
            startActivity(intent)
        }
    }

    private fun isPalindrome(text: String): Boolean {
        val cleanText = text.replace("\\s".toRegex(), "").lowercase()
        return cleanText == cleanText.reversed()
    }

    private fun showDialog(message: String) {
        androidx.appcompat.app.AlertDialog.Builder(this)
            .setMessage(message)
            .setPositiveButton("OK") { dialog, _ ->
                dialog.dismiss()
            }
            .show()
    }
}
