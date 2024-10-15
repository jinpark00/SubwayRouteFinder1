package com.example.subwayroutefinder

import android.os.Bundle
import android.widget.TextView
import androidx.appcompat.app.AppCompatActivity

class SearchResultActivity : AppCompatActivity() {

    private lateinit var resultTextView: TextView

    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        setContentView(R.layout.activity_search_result)

        // 결과를 표시할 TextView 초기화
        resultTextView = findViewById(R.id.resultTextView)

        // Intent에서 전달된 데이터를 가져와서 출력
        val result = intent.getStringExtra("searchResult")
        resultTextView.text = result
    }
}