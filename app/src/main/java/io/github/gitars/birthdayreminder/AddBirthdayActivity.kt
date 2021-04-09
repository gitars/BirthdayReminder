package io.github.gitars.birthdayreminder

import android.app.Activity
import android.content.Context
import android.content.Intent
import android.content.SharedPreferences
import android.os.Bundle
import android.util.Log
import android.widget.Toast
import com.google.android.material.floatingactionbutton.FloatingActionButton
import com.google.android.material.snackbar.Snackbar
import androidx.appcompat.app.AppCompatActivity
import io.github.gitars.birthdayreminder.databinding.ActivityAddBirthdayBinding
import io.github.gitars.birthdayreminder.databinding.ActivityMainBinding

class AddBirthdayActivity : AppCompatActivity() {

    private lateinit var binding: ActivityAddBirthdayBinding

    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        binding = ActivityAddBirthdayBinding.inflate(layoutInflater)
        setContentView(binding.root)
        setSupportActionBar(binding.toolbar)

        binding.submit.setOnClickListener {
            val name = binding.friendsName.text
            val birthday = binding.birthday.text
            if (name.isNullOrBlank() || birthday.isNullOrBlank()) {
                Toast.makeText(this, "Fill out all the fields!", Toast.LENGTH_SHORT).show()
                return@setOnClickListener
            }
            val prefs = getSharedPreferences("birthdays", Context. MODE_PRIVATE)
            val friends = (prefs.getStringSet("friends", emptySet()) ?: emptySet()).toMutableSet()
            friends.add("$name||$birthday")
            prefs.edit().apply {
                putStringSet("friends", friends)
                apply()
            }
            finish()
        }
    }

    companion object {
        fun newIntent(parent: Activity) = Intent(parent, AddBirthdayActivity::class.java)
    }
}
