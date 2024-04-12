package com.example.m08_p4_mapsapp.firebase

import android.net.Uri
import android.util.Log
import com.example.m08_p4_mapsapp.model.Marker
import com.example.m08_p4_mapsapp.model.User
import com.google.firebase.firestore.CollectionReference
import com.google.firebase.firestore.DocumentReference
import com.google.firebase.firestore.FirebaseFirestore
import com.google.firebase.storage.FirebaseStorage
import java.text.SimpleDateFormat
import java.util.Date
import java.util.Locale

class Repository {
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

    fun addMarker(marker: Marker) {
        database.collection("markers").add(
            hashMapOf(
                "id" to marker.id,
                "name" to marker.name,
                "latitude" to marker.markerState.position.latitude,
                "longitude" to marker.markerState.position.longitude,
                "url" to marker.url
            )
        )
    }

    fun removeMarker(marker:Marker) {
        database.collection("markers").document(marker.id).delete()
    }

    // SELECT
    fun getUsers(): CollectionReference {
        return database.collection("users")
    }

    fun getUser(userId: String): DocumentReference {
        return database.collection("users").document(userId)
    }

    fun getMarkersFromDatabase(): CollectionReference {
        return database.collection("markers")
    }

    fun uploadImage(imageUri: Uri, marker: Marker) {
        val formatter = SimpleDateFormat("yyyy_MM_dd_HH_mm_ss", Locale.getDefault())
        val now = Date()
        val fileName = formatter.format(now)
        val storage = FirebaseStorage.getInstance().getReference("images/$fileName")
        storage.putFile(imageUri)
            .addOnSuccessListener {
                Log.i("IMAGE UPLOADED", "Image uploaded successfully")
                storage.downloadUrl.addOnSuccessListener {
                    Log.i("IMAGEN",it.toString())
                    marker.url = it.toString()
                }
            }
            .addOnCanceledListener {
                Log.i("IMAGE UPLOAD CANCELED", "Image upload canceled")
            }
    }
}