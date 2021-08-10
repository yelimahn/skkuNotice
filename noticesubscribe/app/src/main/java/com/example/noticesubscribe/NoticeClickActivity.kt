package com.example.noticesubscribe

import android.content.Intent
import androidx.appcompat.app.AppCompatActivity
import android.os.Bundle
import android.renderscript.ScriptGroup
import android.util.Log
import com.example.noticesubscribe.databinding.ActivityKeywordEditBinding
import com.example.noticesubscribe.databinding.ActivityNoticeClickBinding

class NoticeClickActivity : AppCompatActivity() {

    private var noticeClickBinding: ActivityNoticeClickBinding? = null
    private val binding get() = noticeClickBinding!!

    //intent를 keywordadapter.kt로부터 전달받기 위한 코드 - 0808작성
//    var dateOfaNotice = intent.getStringExtra("data")
//    var visitedOfaNotice = intent.getStringExtra("visited")
//    var linkOfaNotice = intent.getStringExtra("link")
//    var contentOfaNotice = intent.getStringExtra("content")



    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        noticeClickBinding = ActivityNoticeClickBinding.inflate(layoutInflater)
        setContentView(binding.root)
        // 밑의 코드는 단순 확인용
//        if (titleOfaNotice != null){
//            Log.d("notnull", "눌이 아니다")
//        } else{
//            Log.d("notnull", "눌이다")
//
//        }
        var titleOfaNotice = intent?.getStringExtra("title")
        var contentOfaNotice = intent?.getStringExtra("Ncontent")
        binding.NoticeTitle.text = titleOfaNotice
        binding.NoticeContent.text = contentOfaNotice
        //차후에 내용 받아오면 이거 지우면 된다
        //binding.NoticeContent = contentOfaNotice

    }


}