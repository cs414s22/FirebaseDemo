package com.example.firebasedemo


import androidx.appcompat.app.AppCompatActivity
import android.os.Bundle
import com.google.firebase.firestore.FirebaseFirestore
import android.app.AlertDialog
import android.util.Log
import android.view.View
import android.widget.Toast
import com.google.firebase.firestore.ktx.toObjects
import kotlinx.android.synthetic.main.activity_main.*

class MainActivity : AppCompatActivity() {

    private val TAG = "MainActivity"

    private lateinit var fireBaseDb: FirebaseFirestore

    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        setContentView(R.layout.activity_main)

        // Get a Cloud Firestore instance
        fireBaseDb = FirebaseFirestore.getInstance()
    }

    // Alternative-1 --> uses a hashMap to structure the data to be inserted into the db
    // Add  a record to db
    fun addButton(view: View) {

        // Get an instance of our collection
        val contacts = fireBaseDb.collection("contacts")

        // Map or Dictionary objects is used to represent your document
        val contact = hashMapOf(
            "id" to text_id.text.toString().toInt(),
            "name" to text_name.text.toString(),
            "email" to text_email.text.toString()
        )

        // Get an auto generated id for a document that you want to insert
        val documentId = contacts.document().id

        // Add data
        contacts.document(documentId).set(contact)

        // Clear texts and inform the user
        clearEditTexts()
        showDialog("Success", "Contact has been added.")

    }



    // Alternative-1
    // Read all the records from the database
    fun viewAllDataButton(view: View) {

        // Get data using addOnSuccessListener
        fireBaseDb.collection("contacts")
            .orderBy("id")  // Here you can also use orderBy to sort the results based on a field such as id
            //.orderBy("id", Query.Direction.DESCENDING)  // this would be used to orderBy in DESCENDING order
            .get()
            .addOnSuccessListener { documents ->

                val buffer = StringBuffer()

                // The result (documents) contains all the records in db, each of them is a document
                for (document in documents) {

                    Log.d(TAG, "${document.id} => ${document.data}")

                    Log.d(TAG, "contact: ${document.get("id")}, ${document.get("name")}, ${document.get("email")}")

                    // Create a string buffer (i.e., concatenate all the fields into one string)
                    buffer.append("ID : ${document.get("id")}" + "\n")
                    buffer.append("NAME : ${document.get("name")}" + "\n")
                    buffer.append("EMAIL :  ${document.get("email")}" + "\n\n")
                }

                // show all the records as a string in a dialog
                showDialog("Data Listing", buffer.toString())
            }
            .addOnFailureListener {
                Log.d(TAG, "Error getting documents")
                showDialog("Error", "Error getting documents")
            }
    }



    // Delete a contact based on its id
    fun deleteButton(view: View) {

        // get the id from the user
        val id = text_id.text.toString()

        if (id.isNotEmpty()) {

            // To delete the contact based on id, we first execute a query to get a reference to
            // document to be deleted, then loop over matching documents and finally delete each
            // document based on its reference
            fireBaseDb.collection("contacts")
                .whereEqualTo("id", id.toInt())
                .get()
                .addOnSuccessListener {documents->

                    for (document in documents) {
                        if (document != null) {
                            Log.d(TAG, "${document.id} => ${document.data}")
                            // delete the document
                            document.reference.delete()

                            clearEditTexts()
                            showToast("Contact has been deleted.")
                            // Assuming there is only one document we want to delete so break the loop
                            break
                        } else {
                            Log.d(TAG, "No such document")
                        }
                    }
                }

        } else {
            showToast("Enter an id")
        }
    }


    // Alternative-1 --> uses map to represent contact data to updated
    // Update a contact using id
    fun updateButton(view: View) {

        // get the id from the user
        val id = text_id.text.toString()

        if (id.isNotEmpty()) {

            // Create hashMap with updated contact data: name and email
            val updatedContact = mapOf(
                "name" to text_name.text.toString(),
                "email" to text_email.text.toString()
            )


            // To update the contact based on id, we first execute a query to get a reference to
            // document to be updated, then loop over matching documents and finally update each
            // document based on its reference
            fireBaseDb.collection("contacts")
                .whereEqualTo("id", id.toInt())
                .get()
                .addOnSuccessListener { documents ->

                    for (document in documents) {
                        if (document != null) {
                            Log.d(TAG, "${document.id} => ${document.data}")

                            // update the document
                            document.reference.update(updatedContact)

                            clearEditTexts()
                            showToast("Contact has been updated.")
                            // Assuming there is only one document we want to update so break the loop
                            break
                        } else {
                            Log.d(TAG, "No such document")
                        }
                    }
                }

        } else {
            showToast("Enter an id")
        }
    }


    /**
     * -----------------------------------------------------------------------
     * ########################################################################
     *
     *
     * The functions below function as the above functions using a custom class
     *
     *
     * ########################################################################
     * ------------------------------------------------------------------------
     */


    // Alternative-2 --> uses a custom class to represent the data to be inserted into the db
    // Add  a record to db
    fun addButtonWithCustomClass(view: View) {

        // Get an instance of our collection
        val contacts = fireBaseDb.collection("contacts")

        // Custom class is used to represent your document
        // it is recommended to have a custom class to represent the data
        val contact = Contact(
            text_id.text.toString().toInt(),
            text_name.text.toString(),
            text_email.text.toString()
        )

        // Get an auto generated id for a document that you want to insert
        val id = contacts.document().id

        // Add data
        contacts.document(id).set(contact)

        // Clear texts and inform the user
        clearEditTexts()
        showDialog("Success", "Contact has been added.")


    }

    // Alternative-2 --> Uses custom objects (i.e., Contact data class)
    // Read all the records from the database
    fun viewAllDataButtonWithCustomClass(view: View) {

        // Get data using addOnSuccessListener
        fireBaseDb.collection("contacts")
            .orderBy("id")
            .get()
            .addOnSuccessListener { documents ->

                val buffer = StringBuffer()

                // Turn your document(s) to Contact object
                val contacts = documents.toObjects<Contact>()

                for (contact in contacts) {

                    Log.d(TAG, "contact: ${contact}")

                    // Create a string buffer (i.e., concatenate all the fields into one string)
                    buffer.append("ID : ${contact.id}" + "\n")
                    buffer.append("NAME : ${contact.name}" + "\n")
                    buffer.append("EMAIL :  ${contact.email}" + "\n\n")
                }

                // show all the records as a string in a dialog
                showDialog("Data Listing", buffer.toString())
            }
            .addOnFailureListener {
                Log.d(TAG, "Error getting documents")
                showDialog("Error", "Error getting documents")
            }
    }





    // Alternative-2 --> uses a custom class to represent the data to be update in the db
    // Update a contact using id
    fun updateButtonWithCustomClass(view: View) {

        // get the id from the user
        val id = text_id.text.toString()

        if (id.isNotEmpty()) {


            // Create hashMap with updated contact data: name and email
            val updatedContact = Contact(
                id.toInt(),
                text_name.text.toString(),
                text_email.text.toString()
            )


            // To update the contact based on id, we first execute a query to get a reference to
            // document to be updated, then loop over matching documents and finally update each
            // document based on its reference
            fireBaseDb.collection("contacts")
                .whereEqualTo("id", id.toInt())
                .get()
                .addOnSuccessListener { documents ->

                    for (document in documents) {
                        if (document != null) {
                            Log.d(TAG, "${document.id} => ${document.data}")

                            // update the document
                            document.reference.set(updatedContact)

                            clearEditTexts()
                            showToast("Contact has been updated.")
                            // Assuming there is only one document we want to delete so break the loop
                            break
                        } else {
                            Log.d(TAG, "No such document")
                        }
                    }
                }

        } else {
            showToast("Enter an id")
        }
    }

    /**
     * ######################### End of Alternative-2 functions ###################################
     */

    // Gets realtime updates whenever the data on the server is updated
    fun realtimeUpdateButton(view: View) {

        // Get real time update
        fireBaseDb.collection("contacts")
            .addSnapshotListener{ snapshots, e ->
                if (e != null) {
                    Log.w(TAG, "Listen failed.", e)
                    return@addSnapshotListener
                }


                if (snapshots != null) {

                    // This will be called every time a document is updated
                    Log.d(TAG, "onEvent: -----------------------------")

                    val contacts = snapshots.toObjects<Contact>()
                    for (contact in contacts) {
                        Log.d(TAG, "Current data: ${contact}")
                        showData(contact)
                    }
                } else {
                    Log.d(TAG, "Current data: null")
                }
            }
    }

    /**
     * A helper function to show contact data in the textViews
     */
    private fun showData(contact: Contact){
        text_id.setText(contact.id.toString())
        text_name.setText(contact.name.toString())
        text_email.setText(contact.email.toString())
    }

    /**
     * A helper function to show Toast message
     */
    private fun showToast(text: String){
        Toast.makeText(this, text, Toast.LENGTH_LONG).show()
    }

    /**
     * show an alert dialog with data dialog.
     */
    private fun showDialog(title : String,Message : String){
        val builder = AlertDialog.Builder(this)
        builder.setCancelable(true)
        builder.setTitle(title)
        builder.setMessage(Message)
        builder.show()
    }


    /**
     * A helper function to clear our edittexts
     */
    private fun clearEditTexts(){
        text_email.text.clear()
        text_id.text.clear()
        text_name.text.clear()
    }
}