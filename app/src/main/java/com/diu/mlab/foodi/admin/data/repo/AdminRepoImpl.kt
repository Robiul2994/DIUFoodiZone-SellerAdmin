package com.diu.mlab.foodi.admin.data.repo

import android.util.Log
import com.diu.mlab.foodi.admin.domain.model.SuperUser
import com.diu.mlab.foodi.admin.domain.repo.AdminRepo
import com.google.firebase.firestore.FirebaseFirestore
import com.google.firebase.firestore.ktx.toObject

class AdminRepoImpl(
    private val firestore: FirebaseFirestore
) : AdminRepo {
    override fun getSuperUserList(
        type: String,
        success: (superUserList: List<SuperUser>) -> Unit,
        failed: (msg: String) -> Unit
    ) {
        val userList = mutableListOf<SuperUser>()
        firestore.collection("superUserProfiles")
            .get()
            .addOnSuccessListener { result ->
                for (document in result) {
                    Log.d("TAG", "${document.id} => ${document.data}")
                    val user = document.toObject<SuperUser>()
                    if(user.status==type){
                        userList.add(user)
                    }
                }
                success.invoke(userList)
            }
            .addOnFailureListener { exception ->
                Log.w("TAG", "Error getting documents.", exception)
                failed.invoke("Something went wrong")
            }
    }

    override fun getMyProfile(
        email: String,
        success: (superUser: SuperUser) -> Unit,
        failed: (msg: String) -> Unit
    ) {
        firestore.collection("superUserProfiles").document(email)
            .get()
            .addOnSuccessListener { document ->
                if (document != null) {
                    Log.d("TAG", "DocumentSnapshot data: ${document.data}")
                    val superUser = document.toObject(SuperUser::class.java)!!
                    success.invoke(superUser)
                } else {
                    Log.d("TAG", "No such document")
                    failed.invoke("User doesn't exist")
                }
            }
            .addOnFailureListener { exception ->
                Log.w("TAG", "Error getting documents.", exception)
                failed.invoke("Something went wrong")
            }    }


}