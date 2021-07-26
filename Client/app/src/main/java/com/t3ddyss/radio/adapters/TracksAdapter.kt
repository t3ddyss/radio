package com.t3ddyss.radio.adapters

import android.view.LayoutInflater
import android.view.ViewGroup
import androidx.recyclerview.widget.RecyclerView
import com.t3ddyss.radio.R
import com.t3ddyss.radio.databinding.ListItemTrackBinding
import com.t3ddyss.radio.models.domain.Track
import com.t3ddyss.radio.utilities.getThemeColor

class TracksAdapter(
    private val tracks: List<Track>,
    val clickListener: (Int) -> Unit
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

    fun resetPlayingTrack() {
        tracks
            .withIndex()
            .find {
                it.value.isPlaying
            }?.let {
                it.value.isPlaying = false
                notifyItemChanged(it.index)
            }
    }

    fun setPlayingTrack(trackId: Int) {
        resetPlayingTrack()

        tracks
            .withIndex()
            .find {
                it.value.id == trackId
            }?.let {
                it.value.isPlaying = true
                notifyItemChanged(it.index)
            }
    }

    inner class TrackViewHolder(
        private val binding: ListItemTrackBinding
    ) : RecyclerView.ViewHolder(binding.root) {

        init {
            binding.root.setOnClickListener {
                clickListener.invoke(adapterPosition)
            }
        }

        fun bind(track: Track) = with (binding) {
            textViewArtist.text = track.artist
            textViewTitle.text = track.title
            textViewLength.text = track.length

            if (track.isPlaying) {
                textViewTitle.setTextColor(root.context.getThemeColor(R.attr.colorSecondary))
                textViewArtist.setTextColor(root.context.getThemeColor(R.attr.colorSecondary))
                textViewLength.setTextColor(root.context.getThemeColor(R.attr.colorSecondary))
            }

            else {
                textViewTitle.setTextColor(root.context.getThemeColor(R.attr.colorOnPrimary))
                textViewArtist.setTextColor(root.context.getThemeColor(R.attr.colorOnPrimary))
                textViewLength.setTextColor(root.context.getThemeColor(R.attr.colorOnPrimary))
            }
        }
    }
}