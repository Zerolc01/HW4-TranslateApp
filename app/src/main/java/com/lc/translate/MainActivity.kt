package com.lc.translate

import android.annotation.SuppressLint
import androidx.appcompat.app.AppCompatActivity
import android.os.Bundle
import android.widget.Button
import android.widget.EditText
import android.widget.TextView
import com.google.gson.GsonBuilder
import com.lc.translate.api.JsonRootBean
import com.lc.translate.interceptor.TimeConsumeInterceptor
import okhttp3.*
import java.io.IOException


class MainActivity : AppCompatActivity() {

    var requestBtn: Button? = null
    var showText: TextView? = null

    val okhttpListener = object : EventListener() {
        override fun dnsStart(call: Call, domainName: String) {
            super.dnsStart(call, domainName)
            showText?.text = showText?.text.toString() + "\nDns Search:" + domainName
        }

        override fun responseBodyStart(call: Call) {
            super.responseBodyStart(call)
            showText?.text = showText?.text.toString() + "\nResponse Start"
        }
    }

    val client: OkHttpClient = OkHttpClient
        .Builder()
        .addInterceptor(TimeConsumeInterceptor())
        .eventListener(okhttpListener).build()

    val gson = GsonBuilder().create()

    fun request(url: String, callback: Callback) {
        val request: Request = Request.Builder()
            .url(url)
            .header("User-Agent", "Sjtu-Android-OKHttp")
            .build()
        client.newCall(request).enqueue(callback)
    }

    fun click() {
        val editText = findViewById<EditText>(R.id.edit)
        var string = editText.text.toString()

        if (string == null || string.isEmpty()){
            showText?.text = "麻烦输入一点东西吧！\n别逗弟弟玩了！ \n作业好难啊QAQ"
        }
        else {
            val url = "https://fanyi.youdao.com/translate?&doctype=json&type=AUTO&i=$string"
            request(url, object : Callback {
                override fun onFailure(call: Call, e: IOException) {
                    showText?.text = e.message
                }

                override fun onResponse(call: Call, response: Response) {
                    val bodyString = response.body?.string()
                    val transBean = gson.fromJson(bodyString, JsonRootBean::class.java)
                    showText?.text = "${transBean.translateResult[0][0].tgt}"
                }
            })
        }
    }

    

    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        setContentView(R.layout.activity_main)
        requestBtn = findViewById(R.id.trans_button)
        showText = findViewById(R.id.show_text)

        requestBtn?.setOnClickListener {
            showText?.text = ""
            click()
        }
    }
}