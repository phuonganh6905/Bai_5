package com.example.pa_bai5


import java.io.Serializable

data class GiaoDich(
    val soTien: Double,
    val moTa: String,
    val loai: String,
    val danhMuc: String,
    val ngay: String
) : Serializable