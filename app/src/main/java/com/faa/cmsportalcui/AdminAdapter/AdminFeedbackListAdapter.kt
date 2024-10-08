package com.faa.cmsportalcui.AdminAdapter


import android.content.Context
import android.content.Intent
import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import android.widget.Button
import android.widget.TextView
import androidx.recyclerview.widget.RecyclerView
import com.bumptech.glide.Glide
import com.faa.cmsportalcui.AdminModel.AdminFeedbackList
import com.faa.cmsportalcui.AdminSide.FeedbackActivity
import com.faa.cmsportalcui.R
import com.google.firebase.firestore.FirebaseFirestore
import de.hdodenhof.circleimageview.CircleImageView

class AdminFeedbackListAdapter(
    private val context: Context,
    private val feedbackList: List<AdminFeedbackList>,
    private val onItemClickListener: (AdminFeedbackList) -> Unit
) : RecyclerView.Adapter<AdminFeedbackListAdapter.FeedbackViewHolder>() {

    private val db = FirebaseFirestore.getInstance()

    override fun onCreateViewHolder(parent: ViewGroup, viewType: Int): FeedbackViewHolder {
        val view = LayoutInflater.from(context).inflate(R.layout.item_feedback_request, parent, false)
        return FeedbackViewHolder(view)
    }

    override fun onBindViewHolder(holder: FeedbackViewHolder, position: Int) {
        val feedback = feedbackList[position]

        holder.requestId.text = feedback.assignedTaskId
        holder.requestTitle.text = feedback.title

        // Fetch staff profile image
        loadStaffProfileImage(feedback.staffId, holder.profileImage)

        // Handle the view button click
        holder.viewButton.setOnClickListener {
            // Pass the feedback details to FeedbackActivity
            val intent = Intent(context, FeedbackActivity::class.java).apply {
                putExtra("id", feedback.id)
                putExtra("assignedBy", feedback.assignedBy) // Assuming this field exists in AdminFeedbackList
                putExtra("review", feedback.review)
            }
            context.startActivity(intent)
        }
    }

    override fun getItemCount(): Int {
        return feedbackList.size
    }

    inner class FeedbackViewHolder(itemView: View) : RecyclerView.ViewHolder(itemView) {
        val requestId: TextView = itemView.findViewById(R.id.request_id)
        val requestTitle: TextView = itemView.findViewById(R.id.request_title)
        val profileImage: CircleImageView = itemView.findViewById(R.id.ProfileImage)
        val viewButton: Button = itemView.findViewById(R.id.view_button)
    }

    private fun loadStaffProfileImage(staffId: String, profileImageView: CircleImageView) {
        // Get the staff's profile picture from Firestore
        db.collection("staff").document(staffId)
            .get()
            .addOnSuccessListener { document ->
                if (document.exists()) {
                    val imageUrl = document.getString("profileImageUrl") // Assuming profileImageUrl is the field name
                    if (!imageUrl.isNullOrEmpty()) {
                        // Use Glide or any other image loading library to load the image into the CircleImageView
                        Glide.with(context)
                            .load(imageUrl)
                            .placeholder(R.drawable.account) // Placeholder image
                            .into(profileImageView)
                    }
                }
            }
            .addOnFailureListener { e ->
                // Handle failure to load the profile image
                profileImageView.setImageResource(R.drawable.account) // Fallback image
            }
    }
}
