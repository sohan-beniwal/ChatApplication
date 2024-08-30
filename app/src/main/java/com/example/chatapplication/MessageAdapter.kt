package com.example.chatapplication

import android.content.Context
import android.media.MediaScannerConnection
import android.os.Environment
import android.util.Log
import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import android.widget.ImageView
import android.widget.TextView
import android.widget.Toast
import androidx.core.app.NotificationCompat.MessagingStyle.Message
import androidx.recyclerview.widget.RecyclerView
import com.google.firebase.Firebase
import com.google.firebase.auth.FirebaseAuth
import com.google.firebase.storage.storage
import java.io.File

class MessageAdapter(val context : Context, val MessageList : ArrayList<message>):
    RecyclerView.Adapter<RecyclerView.ViewHolder>() {

    val ITEM_SENT=2;
    val ITEM_RECEIVE=1;

    override fun onCreateViewHolder(parent: ViewGroup, viewType: Int): RecyclerView.ViewHolder {


        if(viewType == 1){
            val view : View = LayoutInflater.from(context).inflate(R.layout.receive,parent,false)
            return receiveViewHolder(view)
        }
        else if (viewType == 2){
            val view : View = LayoutInflater.from(context).inflate(R.layout.sent,parent,false)
            return sentViewHolder(view)
        }
        else if(viewType == 0){
            val view : View = LayoutInflater.from(context).inflate(R.layout.sent_pdf,parent,false)
            return PdfMessageViewHolderSent(view)
        }
        else {
            val view : View = LayoutInflater.from(context).inflate(R.layout.receive_pdf,parent,false)
            return PdfMessageViewHolderRecv(view)
        }
    }

    override fun getItemViewType(position: Int): Int {
        val currerntMessage = MessageList[position]
        if(FirebaseAuth.getInstance().currentUser?.uid.equals(currerntMessage.senderID))
        {
            if(currerntMessage.pdfUrl!=null) return 0;
            else return ITEM_SENT
        }
        else{
            if(currerntMessage.pdfUrl!=null) return 3;
            else return ITEM_RECEIVE
        }
    }
    override fun getItemCount(): Int {
        return MessageList.size
    }

    override fun onBindViewHolder(holder: RecyclerView.ViewHolder, position: Int) {
        val currentMessage = MessageList[position]
       if(holder.javaClass == sentViewHolder::class.java) {
           val viewHolder = holder as sentViewHolder
           holder.sentMessage.text=currentMessage.message
       }
       else if (holder.javaClass == receiveViewHolder::class.java){
           val viewHolder = holder as receiveViewHolder
           holder.receiveMessage.text = currentMessage.message
       }
        else if(holder.javaClass == PdfMessageViewHolderSent::class.java){
            val viewHolder = holder as PdfMessageViewHolderSent
           holder.bind(currentMessage.pdfName,currentMessage.pdfUrl)
       }
       else {
           val viewHolder = holder as PdfMessageViewHolderRecv
           holder.bind(currentMessage.pdfName,currentMessage.pdfUrl)
       }
    }


    class sentViewHolder(itemView: View) : RecyclerView.ViewHolder(itemView){
        val sentMessage = itemView.findViewById<TextView>(R.id.sent_message)

    }
    class PdfMessageViewHolderSent(itemView: View) : RecyclerView.ViewHolder(itemView) {
        fun bind(pdfName: String?, pdfUrl: String?) {
            // Set PDF name text
            itemView.findViewById<TextView>(R.id.pdfNameSent).text = pdfName

            // Set click listener for download button
            itemView.findViewById<ImageView>(R.id.downloadbtnSent).setOnClickListener {
                // Implement download logic here
                // You can use the PDF URL to download the file
                downloadFile(pdfName)
            }
    }

        private fun downloadFile(url: String?) {
            if (url.isNullOrEmpty()) {
                Log.e("Download", "URL is null or empty")
                return
            }

            val storageRef = Firebase.storage.reference
            val httpsReference = storageRef.child("pdfs").child(url)

            val localFile = File.createTempFile("pdf", "pdf")
            httpsReference.getFile(localFile).addOnSuccessListener {
                // File downloaded successfully
                Log.d("Download", "File downloaded successfully")
                // You can now open or use the downloaded file
                val downloadsDirectory = Environment.getExternalStoragePublicDirectory(Environment.DIRECTORY_DOWNLOADS)
                val destFile = File(downloadsDirectory, url)
                localFile.copyTo(destFile, true)

            }.addOnFailureListener { exception ->
                // Handle any errors
                Log.e("Download", "Error downloading file: ${exception.message}")
            }
        }
        }
}
    class PdfMessageViewHolderRecv(itemView: View) : RecyclerView.ViewHolder(itemView) {
        fun bind(pdfName: String?, pdfUrl: String?) {
            // Set PDF name text
            itemView.findViewById<TextView>(R.id.pdfName).text = pdfName

            // Set click listener for download button
            itemView.findViewById<ImageView>(R.id.downloadbtn).setOnClickListener {
                downloadFile(pdfUrl)
            }
    }

        private fun downloadFile(url: String?) {
            if (url.isNullOrEmpty()) {
                Log.e("Download", "URL is null or empty")
                return
            }

            val storageRef = Firebase.storage.reference
            val httpsReference = storageRef.child("pdfs").child(url)

            val localFile = File.createTempFile("pdf", "pdf")
            httpsReference.getFile(localFile).addOnSuccessListener {
                // File downloaded successfully
                Log.d("Download", "File downloaded successfully")
                // You can now open or use the downloaded file
                val downloadsDirectory = Environment.getExternalStoragePublicDirectory(Environment.DIRECTORY_DOWNLOADS)
                val destFile = File(downloadsDirectory, url)
                localFile.copyTo(destFile, true)

            }.addOnFailureListener { exception ->
                // Handle any errors
                Log.e("Download", "Error downloading file: ${exception.message}")
            }
        }
    }

    class receiveViewHolder(itemView: View) : RecyclerView.ViewHolder(itemView){
        val receiveMessage = itemView.findViewById<TextView>(R.id.receive_message)
    }
