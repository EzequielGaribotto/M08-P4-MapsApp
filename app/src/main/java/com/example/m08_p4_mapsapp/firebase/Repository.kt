package com.example.m08_p4_mapsapp.firebase

import android.net.Uri
import android.util.Log
import com.example.m08_p4_mapsapp.model.Marker
import com.example.m08_p4_mapsapp.model.User
import com.google.firebase.auth.FirebaseAuth
import com.google.firebase.firestore.CollectionReference
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
                "avatarUrl" to newUser.avatarUrl,
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
                            "avatarUrl" to user.avatarUrl,
                            "nombre" to user.nombre,
                            "apellido" to user.apellido,
                            "ciudad" to user.ciudad,
                            "owner" to user.owner,
                        )
                    )
                }
                Log.d("User", "User updated successfully, new user name: ${user.nombre} ${user.apellido}")
            }
            .addOnFailureListener { exception ->
                Log.w("ERROR", "Error getting documents: ", exception)
            }
    }
    fun deleteUserAuth() {
        val user = FirebaseAuth.getInstance().currentUser
        user?.delete()?.addOnCompleteListener { task ->
            if (task.isSuccessful) {
                Log.d("User Account", "User account deleted.")
            }
        }
    }
    // DELETE
    fun removeUser(user: User) {
        database.collection("user")
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
                "url" to marker.url,
                "categoria" to marker.categoria
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
                            "url" to marker.url,
                            "categoria" to marker.categoria
                        )
                    )
                }
            }
            .addOnFailureListener { exception ->
                Log.w("ERROR", "Error getting documents: ", exception)
            }
    }

    fun removeMarker(marker: Marker) {
        println("Nom del marcador a borrar: ${marker.name}")
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

    // SELECT
    fun getUsers(): CollectionReference {
        return database.collection("user")
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
                Log.i("Image upload", "Image uploaded correctly")
                storage.downloadUrl.addOnSuccessListener {
                    marker.url = it.toString()
                    editMarker(marker)
                    Log.i("Image upload", it.toString())
                }
                    .addOnFailureListener { Log.e("Image upload", "Image upload failed") }
            }
    }

    fun deletePhoto(uri: Uri) {
        val storage = FirebaseStorage.getInstance().getReferenceFromUrl(uri.toString())
        storage.delete()
            .addOnSuccessListener {
                Log.i("Image delete", "Image deleted correctly")
            }
            .addOnFailureListener { Log.e("Image delete", "Image delete failed") }
    }

    fun uploadPfp(imageUri: Uri, user: User) {
        val formatter = SimpleDateFormat("yyyy_MM_dd_HH_mm_ss", Locale.getDefault())
        val now = Date()
        val fileName = formatter.format(now)
        val storage = FirebaseStorage.getInstance().getReference("images/$fileName")
        storage.putFile(imageUri)
            .addOnSuccessListener {
                Log.i("Image upload", "Image uploaded correctly")
                storage.downloadUrl.addOnSuccessListener {
                    user.avatarUrl = it.toString()
                    editUser(user)
                    Log.i("Image upload", it.toString())
                }
                    .addOnFailureListener { Log.e("Image upload", "Image upload failed") }
            }
    }
}