package com.example.m08_p4_mapsapp.firebase

import com.example.m08_p4_mapsapp.model.User
import com.google.firebase.firestore.CollectionReference
import com.google.firebase.firestore.DocumentReference
import com.google.firebase.firestore.FirebaseFirestore

class repository {
    private val database = FirebaseFirestore.getInstance()

    // INSERT
    fun addUser(user: User) {
        database.collection("users").add(
            hashMapOf(
                "userName" to user.userName,
                "age" to user.age,
                "profilePicture" to user.profilePicture
            )
        )
    }

    // UPDATE
    fun editUser(editedUser: User) {
        database.collection("users").document(editedUser.userId!!).set(
            hashMapOf(
                "userName" to editedUser.userName,
                "age" to editedUser.age,
                "profilePicture" to editedUser.profilePicture
            )
        )
    }

    // DELETE
    fun deleteUser(userId: String) {
        database.collection("users").document(userId).delete()
    }

    // SELECT
    fun getUsers(): CollectionReference {
        return database.collection("users")
    }

    fun getUser(userId:String): DocumentReference {
        return database.collection("users").document(userId)
    }
}