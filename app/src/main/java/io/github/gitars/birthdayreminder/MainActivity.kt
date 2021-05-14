package io.github.gitars.birthdayreminder

import android.app.NotificationChannel
import android.app.NotificationManager
import android.content.Context
import android.os.Build
import androidx.appcompat.app.AppCompatActivity
import android.os.Bundle
import android.util.Log
import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import android.widget.Toast
import androidx.core.app.NotificationCompat
import androidx.core.app.NotificationManagerCompat
import androidx.recyclerview.widget.LinearLayoutManager
import androidx.recyclerview.widget.RecyclerView
import io.github.gitars.birthdayreminder.databinding.ActivityMainBinding
import io.github.gitars.birthdayreminder.databinding.ViewFriendsBirthdayListItemBinding
import java.util.*
import kotlin.random.Random

class MainActivity : AppCompatActivity() {


    open class Person(val name: String, val age: Int) {
        open fun getData() = "Person is named $name"
    }

    class Superhero(name: String, age: Int, val power: String): Person(name, age) {
        override fun getData() = "Super is really cool and powerful and also named $name"
    }


    private lateinit var binding: ActivityMainBinding
    private lateinit var adapter: FriendsBirthdayAdapter

    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)

        createNotificationChannel()

        val person = Person("afifa", 99)
        person.getData()

        val superhero = Superhero("afifa", 99, "coding")
        superhero.getData()


        binding = ActivityMainBinding.inflate(layoutInflater)
        setContentView(binding.root)

        binding.addBirthday.setOnClickListener {
            Log.e("XXX", "we just clicked the button")
            Toast.makeText(this, "clicked the button! with update!", Toast.LENGTH_SHORT).show()
            startActivity(AddBirthdayActivity.newIntent(this))
        }

        adapter = FriendsBirthdayAdapter()
        binding.birthdays.adapter = adapter
        binding.birthdays.layoutManager = LinearLayoutManager(this)
    }

    override fun onResume() {
        super.onResume()
        val prefs = getSharedPreferences("birthdays", Context. MODE_PRIVATE)
        val friends = (prefs.getStringSet("friends", emptySet()) ?: emptySet())
                .map { it.split("||") }
        Log.e("XXX", "we have $friends")
        adapter.setData(friends)
    }


    class FriendsBirthdayAdapter : RecyclerView.Adapter<FriendsBirthdayAdapter.ViewHolder>() {

        var friends: List<List<String>> = emptyList()

        fun setData(friends: List<List<String>>) {
            this.friends = friends
            notifyDataSetChanged()
        }

        /** RecyclerView.ViewHolder implementation for this adapter. */
        class ViewHolder(val binding: ViewFriendsBirthdayListItemBinding) : RecyclerView.ViewHolder(binding.root)

        override fun onCreateViewHolder(parent: ViewGroup, viewType: Int) =
            ViewHolder(ViewFriendsBirthdayListItemBinding.inflate(LayoutInflater.from(parent.context), parent, false))

        override fun onBindViewHolder(holder: ViewHolder, position: Int) {
            val (friendName, birthday) = friends[position]
            holder.binding.friendsName.text = friendName
            holder.binding.birthday.text = birthday
            holder.binding.root.setOnClickListener {
                Log.e("afifa","hellomyself")
                var builder = NotificationCompat.Builder(it.context, CHANNEL_ID)
                        .setSmallIcon(R.drawable.ic_launcher_foreground)
                        .setContentTitle("Birthday Reminder $friendName")
                        .setContentText("Birthday Reminder Time $birthday")
                        .setPriority(NotificationCompat.PRIORITY_DEFAULT)
                with(NotificationManagerCompat.from(it.context)) {
                    // notificationId is a unique int for each notification that you must define
                    notify(Random.nextInt(), builder.build())
                }
            }
        }

        override fun getItemCount() = friends.size
    }

    private fun createNotificationChannel() {
        // Create the NotificationChannel, but only on API 26+ because
        // the NotificationChannel class is new and not in the support library
        if (Build.VERSION.SDK_INT >= Build.VERSION_CODES.O) {
            val notificationManager: NotificationManager =
                    getSystemService(Context.NOTIFICATION_SERVICE) as NotificationManager

            if (notificationManager.getNotificationChannel(CHANNEL_ID) != null) {
                return
            }

            val name = "Reminders"
            val descriptionText = "Birthday reminders"
            val importance = NotificationManager.IMPORTANCE_DEFAULT
            val channel = NotificationChannel(CHANNEL_ID, name, importance).apply {
                description = descriptionText
            }
            // Register the channel with the system
            notificationManager.createNotificationChannel(channel)
        }
    }

    companion object {
        private const val CHANNEL_ID = "0"
    }
}
