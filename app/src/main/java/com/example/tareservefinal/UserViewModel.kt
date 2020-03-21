package com.example.tareservefinal

import android.app.Application
import androidx.lifecycle.AndroidViewModel

class UserViewModel(application: Application): AndroidViewModel(application){
    val userId = "UserID1"
    var isTA = "0"
}