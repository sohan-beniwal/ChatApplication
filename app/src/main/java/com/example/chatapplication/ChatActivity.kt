package com.example.chatapplication

import android.app.Activity
import android.content.BroadcastReceiver
import android.content.Intent
import android.net.Uri
import android.os.Build
import android.os.Bundle
import android.os.Environment
import android.provider.Settings
import android.util.Base64
import android.util.Log
import android.widget.EditText
import android.widget.ImageView
import androidx.activity.result.contract.ActivityResultContracts
import androidx.appcompat.app.AppCompatActivity
import androidx.recyclerview.widget.LinearLayoutManager
import androidx.recyclerview.widget.RecyclerView
import com.google.firebase.Firebase
import com.google.firebase.auth.FirebaseAuth
import com.google.firebase.database.DataSnapshot
import com.google.firebase.database.DatabaseError
import com.google.firebase.database.DatabaseReference
import com.google.firebase.database.FirebaseDatabase
import com.google.firebase.database.ValueEventListener
import com.google.firebase.storage.FirebaseStorage
import com.google.firebase.storage.StorageReference
import com.google.firebase.storage.storage
import javax.crypto.Cipher
import javax.crypto.SecretKey
import javax.crypto.spec.SecretKeySpec


class ChatActivity : AppCompatActivity() {

    companion object {
        private const val TAG = "PDF_SEND"
    }

    private lateinit var messageRecyclerView : RecyclerView
    private lateinit var messageBox : EditText
    private lateinit var sendButton : ImageView
    private lateinit var messageAdapter: MessageAdapter
    private lateinit var messageList: ArrayList<message>
    private lateinit var mDbRef : DatabaseReference
    lateinit var key : SecretKey
    private lateinit var pdfbtn : ImageView
    var receiverRoom : String? = null
    var senderRoom : String?= null
    var senderUid : String? = null
    var receiverUid : String? = null

    override fun onCreate(savedInstanceState: Bundle?) {

        super.onCreate(savedInstanceState)
        setContentView(R.layout.activity_chat)


        val name = intent.getStringExtra("name")
        receiverUid = intent.getStringExtra("uid")
        senderUid = FirebaseAuth.getInstance().currentUser?.uid
        supportActionBar?.title = name
        //val keygen = KeyGenerator.getInstance("AES")
        key = SecretKeySpec("Sohan12345612345".toByteArray(),"AES")
        mDbRef = FirebaseDatabase.getInstance().getReference()

        senderRoom = receiverUid+senderUid
        receiverRoom = senderUid+receiverUid


        messageRecyclerView=findViewById(R.id.ChatRecycler)
        messageBox = findViewById(R.id.messagebox)
        sendButton = findViewById(R.id.sent_btn)
        messageList=ArrayList()
        messageAdapter=MessageAdapter(this,messageList)
        pdfbtn = findViewById(R.id.pdf_btn)

        messageRecyclerView.layoutManager = LinearLayoutManager(this)
        messageRecyclerView.adapter=messageAdapter

        mDbRef.child("Chats").child(senderRoom!!).child("Messages")
            .addValueEventListener(object : ValueEventListener{
                override fun onDataChange(snapshot: DataSnapshot) {

                    messageList.clear()
                    for(postsSnapshot in  snapshot.children){

                        val messageobj = postsSnapshot.getValue(message::class.java)
                        if(messageobj?.pdfUrl!=null){
                            val url = decrypt(messageobj?.pdfUrl)
                            messageobj?.pdfUrl=url
                            messageList.add(messageobj)
                        }
                        else {
                        val messagedec = decrypt(messageobj?.message)
                        messageobj?.message = messagedec
                        messageList.add(messageobj!!)
                        }
                    }
                    messageAdapter.notifyDataSetChanged()
                }

                override fun onCancelled(error: DatabaseError) {
                    TODO("Not yet implemented")
                }

            })

        sendButton.setOnClickListener{
            val Message1 = messageBox.text.toString()
            var Message = encrypt(Message1)
            val messageObject = message(Message,senderUid,null,null)

            mDbRef.child("Chats").child(senderRoom!!).child("Messages").push().setValue(messageObject)
                .addOnSuccessListener {
                    mDbRef.child("Chats").child(receiverRoom!!).child("Messages").push().setValue(messageObject)
                }
            messageBox.setText("")
        }

        pdfbtn.setOnClickListener{
            if(checkStoragePermission()){
                pickPdfFromGallery()
            }
            else{
                requestStoragePermission()
            }
        }
    }

    private fun pickPdfFromGallery() {
        Log.d(TAG, "pickPdfFromGallery")
        val intent = Intent(Intent.ACTION_GET_CONTENT)
        intent.type = "application/pdf"
        pdfGalleryActivityResultLauncher.launch(intent)
    }

    private val pdfGalleryActivityResultLauncher =
        registerForActivityResult(ActivityResultContracts.StartActivityForResult()) { result ->
            Log.d(TAG, "PdfPicking")
            if (result.resultCode == Activity.RESULT_OK) {
                val data = result.data
                if (data != null) {
                    val pdfUri = data.data
                    if (pdfUri != null) {
                        // Handle the selected PDF URI here
                        Log.d(TAG, "Selected PDF URI: $pdfUri")
                        // Now you can upload the PDF file to Firebase or perform any other operation
                        uploadPdfToFirebase(pdfUri)
                    } else {
                        Log.e(TAG, "Intent data is null")
                    }
                }
            } else {
                Log.d(TAG, "Cancelled")
            }
        }

    private fun encrypt(strToEncrypt: String): String {
        val plainText = strToEncrypt.toByteArray(Charsets.UTF_8)
        var cipher = Cipher.getInstance("AES/ECB/PKCS5Padding")
        cipher.init(Cipher.ENCRYPT_MODE, key)
        var cipherText = cipher.doFinal(plainText)
        return Base64.encodeToString(cipherText,Base64.DEFAULT)
    }
    private fun decrypt(dataToDecrypt: String?): String {
        var cipher = Cipher.getInstance("AES/ECB/PKCS5Padding")
        cipher.init(Cipher.DECRYPT_MODE, key)
        var cipherText = cipher.doFinal(Base64.decode(dataToDecrypt,Base64.DEFAULT))
        return String(cipherText,Charsets.UTF_8)
    }

    private fun checkStoragePermission(): Boolean {
        Log.d(TAG, "checkstoragepermission")
        return if (Build.VERSION.SDK_INT >= Build.VERSION_CODES.R) {
            Environment.isExternalStorageManager()
        } else {
            true // For versions below Android 11, assume permission is granted
        }
    }

    private fun requestStoragePermission() {
        Log.d(TAG, "requeststoragepermission")
        try {
            val intent = Intent(Settings.ACTION_MANAGE_APP_ALL_FILES_ACCESS_PERMISSION)
            intent.data = Uri.parse("package:" + this.packageName)
            pdfGalleryActivityResultLauncher.launch(intent)
        } catch (e: Exception) {
            Log.e(TAG, "Error launching permission intent: ${e.message}")
        }
    }

    private fun uploadPdfToFirebase(pdfUri: Uri) {
        val fileName = "pdf_${System.currentTimeMillis()}.pdf"
        val storageRef = Firebase.storage.reference.child("pdfs").child(fileName)
        storageRef.putFile(pdfUri)
            .addOnSuccessListener { taskSnapshot ->
                // File uploaded successfully, get the download URL
                storageRef.downloadUrl.addOnSuccessListener { uri ->
                    val downloadUrl = uri.toString()
                    val Message1 = downloadUrl
                    var Message = encrypt(Message1)
                    val messageObject = message(senderUid,Message,fileName)
                    //messageList.add(messageObject)

                    mDbRef.child("Chats").child(senderRoom!!).child("Messages").push().setValue(messageObject)
                        .addOnSuccessListener {
                            mDbRef.child("Chats").child(receiverRoom!!).child("Messages").push().setValue(messageObject)
                        }

                    messageAdapter.notifyDataSetChanged()
                }
            }
            .addOnFailureListener { exception ->
                // Handle any errors
                Log.e(TAG, "Error uploading PDF: ${exception.message}")
            }
    }
}