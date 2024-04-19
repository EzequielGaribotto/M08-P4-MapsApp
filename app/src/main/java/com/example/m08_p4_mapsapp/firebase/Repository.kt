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
    fun addUser(newUser: User) {
        database.collection("user").add(
            hashMapOf(
                "nombre" to newUser.nombre,
                "apellido" to newUser.apellido,
                "ciudad" to newUser.ciudad,
                "owner" to newUser.owner,
            )
        )
    }


    // UPDATE
    fun editUser(user: User) {
        database.collection("user")
            .whereEqualTo("owner", user.owner)
            .get()
            .addOnSuccessListener { documents ->
                for (document in documents) {
                    database.collection("user").document(document.id).set(
                        hashMapOf(
                            "userName" to user.nombre,
                            "age" to user.apellido,
                            "ciudad" to user.ciudad,
                            "profilePicture" to user.owner,
                        )
                    )
                }
            }
            .addOnFailureListener { exception ->
                Log.w("ERROR", "Error getting documents: ", exception)
            }
    }

    // DELETE
    fun removeUser(user: User) {
        database.collection("users")
            .whereEqualTo("owner", user.owner)
            .get()
            .addOnSuccessListener { documents ->
                for (document in documents) {
                    database.collection("user").document(document.id).delete()
                }
            }
            .addOnFailureListener { exception ->
                Log.w("ERROR", "Error getting documents: ", exception)
            }
    }

    fun addMarker(marker: Marker) {
        database.collection("markers").add(
            hashMapOf(
                "owner" to marker.owner,
                "id" to marker.id,
                "name" to marker.name,
                "latitude" to marker.markerState.position.latitude,
                "longitude" to marker.markerState.position.longitude,
                "url" to marker.url
            )
        )
    }

    fun editMarker(marker: Marker) {
        database.collection("markers")
            .whereEqualTo("id", marker.id)
            .get()
            .addOnSuccessListener { documents ->
                for (document in documents) {
                    database.collection("markers").document(document.id).set(
                        hashMapOf(
                            "owner" to marker.owner,
                            "id" to marker.id,
                            "name" to marker.name,
                            "latitude" to marker.markerState.position.latitude,
                            "longitude" to marker.markerState.position.longitude,
                            "url" to marker.url
                        )
                    )
                }
            }
            .addOnFailureListener { exception ->
                Log.w("ERROR", "Error getting documents: ", exception)
            }
    }

    fun removeMarker(marker: Marker) {
        database.collection("markers")
            .whereEqualTo("id", marker.id)
            .get()
            .addOnSuccessListener { documents ->
                for (document in documents) {
                    database.collection("markers").document(document.id).delete()
                }
            }.addOnFailureListener { exception ->
                Log.w("ERROR", "Error getting documents: ", exception)
            }
    }

    fun getUserImageUri(): CollectionReference {
        return database.collection("user")
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
                storage.downloadUrl.addOnSuccessListener { uri ->
                    Log.i("IMAGEN", uri.toString())
                    marker.url = uri.toString()
                }
            }
            .addOnCanceledListener {
                Log.i("IMAGE UPLOAD CANCELED", "Image upload canceled")
            }
    }
}