package com.example.pa_bai5

import android.content.Intent
import android.os.Bundle
import android.widget.Button
import androidx.appcompat.app.AppCompatActivity
import androidx.recyclerview.widget.LinearLayoutManager
import androidx.recyclerview.widget.RecyclerView
import com.google.firebase.auth.FirebaseAuth
import com.google.firebase.firestore.FirebaseFirestore
import com.google.firebase.firestore.FirebaseFirestoreSettings
import com.google.firebase.firestore.ListenerRegistration
import com.google.firebase.firestore.Query

class MainActivity : AppCompatActivity() {

    private lateinit var nutThem: Button
    private lateinit var hienThiDanhSach: RecyclerView
    private val danhSachGiaoDich = mutableListOf<GiaoDich>()
    private lateinit var giaoDichAdapter: GiaoDichAdapter

    private lateinit var auth: FirebaseAuth
    private var registration: ListenerRegistration? = null

    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        setContentView(R.layout.activity_main)

        auth = FirebaseAuth.getInstance()

        nutThem = findViewById(R.id.nutThem)
        hienThiDanhSach = findViewById(R.id.hienThiDanhSach)

        giaoDichAdapter = GiaoDichAdapter(danhSachGiaoDich)
        hienThiDanhSach.layoutManager = LinearLayoutManager(this)
        hienThiDanhSach.adapter = giaoDichAdapter

        nutThem.setOnClickListener {
            startActivity(Intent(this, ThemGiaoDichActivity::class.java))
        }

        val settings = FirebaseFirestoreSettings.Builder()
            .setPersistenceEnabled(true)
            .build()
        FirebaseFirestore.getInstance().firestoreSettings = settings
    }

    override fun onStart() {
        super.onStart()
        val user = auth.currentUser ?: return

        registration = FirebaseFirestore.getInstance()
            .collection("users").document(user.uid)
            .collection("transactions")
            .orderBy("createdAt", Query.Direction.DESCENDING)
            .addSnapshotListener { snap, e ->
                if (e != null) return@addSnapshotListener
                danhSachGiaoDich.clear()
                snap?.documents?.forEach { doc ->
                    val gd = GiaoDich(
                        soTien = doc.getDouble("soTien") ?: 0.0,
                        moTa = doc.getString("moTa") ?: "",
                        loai = doc.getString("loai") ?: "",
                        danhMuc = doc.getString("danhMuc") ?: "",
                        ngay = doc.getString("ngay") ?: ""
                    )
                    danhSachGiaoDich.add(gd)
                }
                giaoDichAdapter.notifyDataSetChanged()
                if (danhSachGiaoDich.isNotEmpty()) {
                    hienThiDanhSach.scrollToPosition(0)
                }
            }
    }

    override fun onStop() {
        super.onStop()
        registration?.remove()
        registration = null
    }
}
