package com.example.noticesubscribe.fragments

import android.content.Context
import android.os.Bundle
import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import androidx.fragment.app.Fragment
import androidx.recyclerview.widget.LinearLayoutManager
import androidx.recyclerview.widget.RecyclerView
import com.example.noticesubscribe.databinding.FragmentHomeBinding
import android.content.Intent
import android.net.Uri
import android.util.Log
import android.widget.*
import androidx.recyclerview.widget.GridLayoutManager
import com.example.noticesubscribe.*
import com.google.firebase.firestore.DocumentSnapshot
import com.google.firebase.firestore.FirebaseFirestore
import com.google.firebase.firestore.Query
import com.google.firebase.ktx.Firebase

class HomeFragment : Fragment() {

    //홈
    //mbinding을 통해 네비게이션 바를 이용해서 이동할 수 있는 fragment를 만듦
    private var mBinding: FragmentHomeBinding? = null
    val db=FirebaseFirestore.getInstance()
    //var notice_list = ArrayList<Notice>()
    val keyList = arrayListOf<Keyword>()//첫번째 리스트 아이템 배열(구독키워드)
    val keyadapter = KeyWordAdapter(keyList)//첫번째 리사이클러뷰 어댑터 부르기(구독키워드)
    //val delete_btn = view?.findViewById< ImageView>(R.id.btn_delete2)
    var mDocuments: List<DocumentSnapshot>? = null
    override fun onCreateView(
        inflater: LayoutInflater,
        container: ViewGroup?,
        savedInstanceState: Bundle?
    ): View? {

        val binding = FragmentHomeBinding.inflate(inflater, container, false)

        mBinding = binding

        return mBinding?.root
    }

    override fun onDestroyView() {
        mBinding = null
        super.onDestroyView()
    }

    //검색 내용에 해당하는 공지들이 표시될 recyclerlist 넣기
    override fun onViewCreated(view: View, savedInstanceState: Bundle?) {
        super.onViewCreated(view, savedInstanceState)
        val keywordeditbutton = view.findViewById<Button>(R.id.bt_editKeyword)
        keywordeditbutton.setOnClickListener {
            val intent = Intent(getActivity(), KeywordEditActivity::class.java)
            startActivity(intent)
        }

        val recyclerView = view.findViewById<RecyclerView>(R.id.rv_homenotice)
        val notice_list = arrayListOf<Notice>()

//        db.collection("Contacts")
//            .orderBy("timestamp", Query.Direction.DESCENDING)
//            .get()
//            .addOnSuccessListener { documents ->
//                for (document in documents) {
//                    val item = document.toObject(Keyword::class.java)
//                    keyList.add(item)
//                }
//                keyadapter.notifyDataSetChanged()
//            }.addOnFailureListener { exception ->
//                Log.w("MainActivity", "Error getting documents: $exception")
//            }
        val gridLayoutManager = GridLayoutManager(view.context, 4)
        mBinding?.rvKeyword?.layoutManager = gridLayoutManager
        mBinding?.rvKeyword?.adapter = keyadapter



//        mBinding?.more?.setOnClickListener {
//            db.collection("Contacts")
//                .orderBy("timestamp", Query.Direction.DESCENDING)
//                .get()
//                .addOnSuccessListener { documents ->
//                    for (document in documents) {
//                        val item = document.toObject(Keyword::class.java)
//                        keyList.add(item)
//                    }
//                    keyadapter.notifyDataSetChanged()
//                }.addOnFailureListener { exception ->
//                    Log.w("MainActivity", "Error getting documents: $exception")
//                }
//            mBinding?.rvKeyword2?.layoutManager = gridLayoutManager
//            mBinding?.rvKeyword2?.adapter = keyadapter
//        }

        //현재의 구독키워드 보여줌
        (mBinding?.rvKeyword?.adapter as KeyWordAdapter).getDataFromFirestore()
        //키워드 삭제
        keyadapter.deleteClick = object : KeyWordAdapter.ItemClick {
            override fun onClick(view: View, pos: Int) {
                when(view.id){
                    R.id.btn_delete2->itemDelete(mDocuments!!.get(pos))
                }
            }
        }
        //키워드 해당하는 공지사항 찾기
        keyadapter.itemClick = object : KeyWordAdapter.ItemClick {
            override fun onClick(view: View, pos: Int) {
                val key: TextView = view.findViewById(R.id.keywordbox)
                val searchOption = "title"//현재는 Notice에 키워드가 포함되어 있지 않아서 제목을 통해서 관련공지사항을 수집함. 추후에 키워드가 생겨나면 "keyword" 로 대체
                (mBinding?.rvHomenotice?.adapter as NoticeAdapter).search(key.text.toString(), searchOption)
            }
        }
        //새로고침 화면(일단은 전체 공지사항으로 대체)
        mBinding?.btnRefresh?.setOnClickListener{
            (mBinding?.rvHomenotice?.adapter as NoticeAdapter).load()
            (mBinding?.rvKeyword?.adapter as KeyWordAdapter).load2()
        }

        recyclerView.layoutManager = LinearLayoutManager(view.context, LinearLayoutManager.VERTICAL, false)
        recyclerView.setHasFixedSize(true)
        recyclerView.adapter = NoticeAdapter(view.context, notice_list)

        //첫화면 추천사항(일단은 전체 공지사항으로 대체)
        (mBinding?.rvHomenotice?.adapter as NoticeAdapter).load()
    }

    //키워드 클릭시 관련공지사항 나옴
    fun NoticeAdapter.search(key:String,option: String){
        db.collection("total")   // 작업할 컬렉션
            .orderBy("date", Query.Direction.DESCENDING)
            .get()      // 문서 가져오기
            .addOnSuccessListener { result ->
                // 성공할 경우
                noticeList.clear()
                for (document in result) {  // 가져온 문서들은 result에 들어감
                    if (document.getString(option)!!.contains(key)) {
                        val item =  Notice(document["title"] as String, document["date"] as String,document["visited"] as String,document["link"] as String)
                        noticeList.add(item)
                    }
                }
                    notifyDataSetChanged()  // 리사이클러 뷰 갱신
            }
            .addOnFailureListener { exception ->
                // 실패할 경우
                Log.w("MainActivity", "Error getting documents: $exception")
            }
}
    //추천키워드 첫화면 및 새로고침 화면, 일단 전체공지로 대체(공지관련)
    fun NoticeAdapter.load(){
        db.collection("total")
            .orderBy("date", Query.Direction.DESCENDING)
            .get()
            .addOnSuccessListener { documents ->
                for (document in documents) {
                    val item = document.toObject(Notice::class.java)
                    noticeList.add(item)
                }
                notifyDataSetChanged()
            }.addOnFailureListener {exception->
                Log.w("MainActivity", "Error getting documents: $exception")
            }
    }
   //추천키워드 첫화면 및 새로고침 화면, 일단 전체공지로 대체(키워드 관련)
    fun KeyWordAdapter.load2(){
        db.collection("Contacts")
            .orderBy("date", Query.Direction.DESCENDING)
            .get()
            .addOnSuccessListener { documents ->
                for (document in documents) {
                    val item = document.toObject(Keyword::class.java)
                    keyList.add(item)
                }
                notifyDataSetChanged()
            }.addOnFailureListener {exception->
                Log.w("MainActivity", "Error getting documents: $exception")
            }
    }

    //삭제관련 데이터(파이어베이스상의 키워드의 위치를 알기위해)
    fun KeyWordAdapter.getDataFromFirestore() {
        db.collection("Contacts")
            .orderBy("timestamp", Query.Direction.DESCENDING)
            .addSnapshotListener{ snapshot, exception ->
                if (exception != null) {
                }
                else {
                    if (snapshot != null) {
                        if (!snapshot.isEmpty) {
                            keyList.clear()
                            mDocuments = snapshot.documents
                            val documents = snapshot.documents
                            for (document in documents) {
                                val item = Keyword(document["key"] as String)
                                keyList.add(item)
                            }
                            notifyDataSetChanged()
                        }
                    }
                }
            }
    }
    //키워드삭제
    fun itemDelete(doc: DocumentSnapshot){
        db.collection("Contacts").document(doc.id)
            .delete()

    }

}
