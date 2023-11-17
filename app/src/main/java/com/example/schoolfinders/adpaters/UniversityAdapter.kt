package com.example.schoolfinders.adpaters

import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import android.widget.TextView
import androidx.recyclerview.widget.RecyclerView
import com.example.schoolfinders.R
import com.example.schoolfinders.models.university

class UniversityAdapter : RecyclerView.Adapter<UniversityAdapter.UniversityViewHolder>() {
    private var universities: List<university> = emptyList()

    override fun onCreateViewHolder(parent: ViewGroup, viewType: Int): UniversityViewHolder {
        val view = LayoutInflater.from(parent.context).inflate(R.layout.item_layout, parent, false)
        return UniversityViewHolder(view)
    }

    override fun onBindViewHolder(holder: UniversityViewHolder, position: Int) {
        val university = universities[position]
        holder.bind(university)
    }

    override fun getItemCount(): Int {
        return universities.size
    }

    fun setUniversities(universities: List<university>) {
        this.universities = universities
        notifyDataSetChanged()
    }

    class UniversityViewHolder(itemView: View) : RecyclerView.ViewHolder(itemView) {
        private val tvUniversity: TextView = itemView.findViewById(R.id.tvUniversity)
        private val tvCourse: TextView = itemView.findViewById(R.id.tvCourse)
        private val tvStatus: TextView = itemView.findViewById(R.id.tvStatus)

        fun bind(university: university) {
            tvUniversity.text = "University: ${university.universityCourse}"
            tvCourse.text = "Course: ${university.course}"
            tvStatus.text = "Status: ${university.status}"
        }
    }
}
