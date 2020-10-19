package com.example.covidapp

import android.content.Context
import android.os.Bundle
import android.util.Log
import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import android.widget.*
import androidx.appcompat.app.AppCompatActivity
import androidx.core.widget.NestedScrollView
import androidx.recyclerview.widget.LinearLayoutManager
import androidx.recyclerview.widget.RecyclerView
import com.google.android.material.button.MaterialButton
import com.google.android.material.floatingactionbutton.FloatingActionButton
import okhttp3.OkHttpClient
import retrofit2.Call
import retrofit2.Callback
import retrofit2.Response
import retrofit2.Retrofit
import retrofit2.converter.gson.GsonConverterFactory
import java.text.SimpleDateFormat
import java.util.*

class MainActivity : AppCompatActivity() {
    private lateinit var editText: EditText
    private lateinit var sendBtn: FloatingActionButton
    private lateinit var recyclerView: RecyclerView
    private val USER = 0
    private val BOT = 1

    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)

        setContentView(R.layout.activity_main)
        val chatScrollView = findViewById<NestedScrollView>(R.id.chatScrollView)

        editText = findViewById(R.id.edittext_chatbox)
        chatScrollView.post { chatScrollView.fullScroll(ScrollView.FOCUS_DOWN) }
        sendBtn = findViewById(R.id.send_button)

        sendBtn.setOnClickListener {
            val msg: String = editText.text.toString().trim()
            sendMessage(msg)
        }


    }

    fun sendMessage(message: String) {
        val date = Date(System.currentTimeMillis())
        val okHttpClient = OkHttpClient()
        val retrofit = Retrofit.Builder()
            .baseUrl("http://IP-Address:Port/webhooks/rest/")
            .client(okHttpClient)
            .addConverterFactory(GsonConverterFactory.create())
            .build()
        val userMessage = UserMessage()
        if (message.trim().isEmpty())
            Toast.makeText(this, "Please enter your query", Toast.LENGTH_SHORT).show()
        else {
            Log.e("MSg", "msssage: $message")
            userMessage.UserMessage("User", message)
            showTextView(message, USER, date.toString())

        }
        val messageSender = retrofit.create(MessageSender::class.java)
        val response =
            messageSender.sendMessage(userMessage)

        response.enqueue(object : Callback<List<BotResponse>> {
            override fun onResponse(
                call: Call<List<BotResponse>>,
                response: Response<List<BotResponse>>
            ) {
                if (response.body() == null || response.body()!!.size == 0) {
                    val botMessage = "Sorry didn't understand"
                    showTextView(botMessage, BOT, date.toString())
                } else {
                    val botResponse = response.body()!![0]
                    showTextView(botResponse.text, BOT, date.toString())
                    if (botResponse.buttons != null) {

                        val buttonRecyclerView =
                            ButtonRecyclerView(this@MainActivity, botResponse.buttons)
                        recyclerView = findViewById(R.id.button_list)
                        recyclerView.adapter = buttonRecyclerView
                        val layoutManager = LinearLayoutManager(this@MainActivity)
                        layoutManager.orientation = LinearLayoutManager.HORIZONTAL
                        recyclerView.layoutManager = layoutManager
                        Log.e("Button c", "${botResponse.buttons.size}")
                    }
                }
            }

            override fun onFailure(call: Call<List<BotResponse>>, t: Throwable) {
                val botMessage = "Check your network connection"
                showTextView(botMessage, BOT, date.toString())
                t.printStackTrace()
                Toast.makeText(this@MainActivity, "" + t.message, Toast.LENGTH_SHORT).show()
            }
        })

    }

    fun showTextView(message: String, type: Int, date: String) {
        var frameLayout: FrameLayout? = null
        val linearLayout = findViewById<LinearLayout>(R.id.chat_layout)
        when (type) {
            USER -> {
                frameLayout = getUserLayout()
            }
            BOT -> {
                frameLayout = getBotLayout()
            }
            else -> {
                frameLayout = getBotLayout()
            }
        }
        frameLayout?.isFocusableInTouchMode = true
        linearLayout.addView(frameLayout)
        val messageTextView = frameLayout?.findViewById<TextView>(R.id.chat_msg)
        messageTextView?.setText(message)
        frameLayout?.requestFocus()
        editText.requestFocus()
        val currentDateTime = Date(System.currentTimeMillis())
        val dateNew = Date(date)
        val dateFormat = SimpleDateFormat("dd-MM-YYYY", Locale.ENGLISH)
        val currentDate = dateFormat.format(currentDateTime)
        val providedDate = dateFormat.format(dateNew)
        var time = ""
        if (currentDate.equals(providedDate)) {
            val timeFormat = SimpleDateFormat(
                "hh:mm aa",
                Locale.ENGLISH
            )
            time = timeFormat.format(dateNew)
        } else {
            val dateTimeFormat = SimpleDateFormat(
                "dd-MM-yy hh:mm aa",
                Locale.ENGLISH
            )
            time = dateTimeFormat.format(dateNew)
        }
        val timeTextView = frameLayout?.findViewById<TextView>(R.id.message_time)
        timeTextView?.setText(time.toString())


    }

    fun getUserLayout(): FrameLayout? {
        val inflater: LayoutInflater = LayoutInflater.from(this)
        return inflater.inflate(R.layout.user_message_box, null) as FrameLayout?
    }

    fun getBotLayout(): FrameLayout? {
        val inflater: LayoutInflater = LayoutInflater.from(this)
        return inflater.inflate(R.layout.bot_message_box, null) as FrameLayout?
    }


    inner class ButtonRecyclerView(var context: Context, var buttons: List<BotResponse.Buttons>) :
        RecyclerView.Adapter<ButtonRecyclerView.ButtonViewHolder>() {


        override fun onCreateViewHolder(parent: ViewGroup, viewType: Int): ButtonViewHolder {
            val view =
                LayoutInflater.from(context).inflate(R.layout.button_list_item, parent, false)
            return ButtonViewHolder(view)

        }

        override fun onBindViewHolder(holder: ButtonViewHolder, position: Int) {
            val payload_button = buttons[position]
            holder.button.text = payload_button.title
            holder.button.setOnClickListener {
                sendMessage(payload_button.payload)
            }
        }

        override fun getItemCount(): Int {
            buttons.isEmpty() ?: return -1
            return buttons.size

        }

        inner class ButtonViewHolder(view: View) : RecyclerView.ViewHolder(view) {
            val button = view.findViewById<MaterialButton>(R.id.payload_button)
        }
    }
}
