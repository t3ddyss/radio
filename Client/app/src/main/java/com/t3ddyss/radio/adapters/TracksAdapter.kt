package com.t3ddyss.radio.adapters

import android.view.LayoutInflater
import android.view.ViewGroup
import androidx.core.graphics.toColor
import androidx.recyclerview.widget.RecyclerView
import com.t3ddyss.radio.R
import com.t3ddyss.radio.databinding.ListItemTrackBinding
import com.t3ddyss.radio.models.domain.Track
import com.t3ddyss.radio.utilities.getThemeColor
import com.t3ddyss.radio.utilities.toColorFilter

class TracksAdapter(
    val tracks: List<Track>,
    val clickListener: (Track) -> Unit
) : RecyclerView.Adapter<TracksAdapter.TrackViewHolder>() {

    override fun onCreateViewHolder(parent: ViewGroup, viewType: Int): TrackViewHolder {
        return TrackViewHolder(
            binding = ListItemTrackBinding.inflate(
                LayoutInflater.from(parent.context),
                parent,
                false
            )
        )
    }

    override fun onBindViewHolder(holder: TrackViewHolder, position: Int) {
        holder.bind(tracks[position])
    }

    override fun getItemCount() = tracks.size

    inner class TrackViewHolder(
        private val binding: ListItemTrackBinding
    ) : RecyclerView.ViewHolder(binding.root) {

        init {
            with (binding) {
                root.setOnClickListener {
                    clickListener.invoke(tracks[adapterPosition])
                    audioIcon.colorFilter = root.context.getThemeColor(R.attr.colorPrimary).toColorFilter()
                }
            }
        }

        fun bind(track: Track) = with (binding) {
                textViewArtist.text = track.artist
                textViewTitle.text = track.title
                textViewLength.text = track.length
            }
    }
}