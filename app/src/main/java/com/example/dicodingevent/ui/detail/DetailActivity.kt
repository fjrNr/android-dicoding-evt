package com.example.dicodingevent.ui.detail

import android.content.Intent
import android.net.Uri
import android.os.Bundle
import android.view.Menu
import android.view.MenuItem
import android.view.View
import android.widget.Toast
import androidx.activity.viewModels
import androidx.appcompat.app.AppCompatActivity
import androidx.core.text.HtmlCompat
import com.bumptech.glide.Glide
import com.example.dicodingevent.R
import com.example.dicodingevent.data.remote.response.EventItem
import com.example.dicodingevent.databinding.ActivityDetailBinding
import com.example.dicodingevent.ui.ViewModelRepositoryFactory

class DetailActivity : AppCompatActivity() {
    private lateinit var binding: ActivityDetailBinding
    private val viewModel: DetailViewModel by viewModels {
        ViewModelRepositoryFactory.getInstance(this)
    }

    companion object {
        const val EXTRA_INDEX = "extra_index"
        const val EXTRA_TITLE = "extra_title"
    }

    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        binding = ActivityDetailBinding.inflate(layoutInflater)
        setContentView(binding.root)

        supportActionBar?.setDisplayHomeAsUpEnabled(true)
        supportActionBar?.title = intent.getStringExtra(EXTRA_TITLE)

        val eventId = intent.getIntExtra(EXTRA_INDEX,0)

        if (savedInstanceState == null) {
            viewModel.getDetailEvent(eventId)
        }

        viewModel.eventItem.observe(this) {
            it?.let(::setDetailData)
        }

        viewModel.isLoading.observe(this) {
            showLoading(it)
        }

        viewModel.isShowMessage.observe(this) {
            showMessage(it)
        }

        viewModel.isShowButtonRetry.observe(this) {
            showButtonRetry(it)
        }

        binding.includeMessage.btnRetry.setOnClickListener{
            viewModel.getDetailEvent(intent.getIntExtra(EXTRA_INDEX,0))
        }
    }

    override fun onSupportNavigateUp(): Boolean {
        finish()
        return true
    }

    override fun onCreateOptionsMenu(menu: Menu?): Boolean {
        menuInflater.inflate(R.menu.detail_menu, menu)
        return super.onCreateOptionsMenu(menu)
    }

    override fun onOptionsItemSelected(item: MenuItem): Boolean {
        if (item.itemId == R.id.action_favorite) {
            val event = viewModel.eventItem.value
            if (viewModel.isFavoriteEvent.value == false) {
                item.setIcon(R.drawable.baseline_favorite_24)
                event?.let { viewModel.addFavoriteEvent(it) }
            }else{
                item.setIcon(R.drawable.baseline_favorite_border_24)
                event?.let { viewModel.removeFavoriteEvent(it.id?: 0) }
            }
        }
        return super.onOptionsItemSelected(item)
    }

    override fun onPrepareOptionsMenu(menu: Menu?): Boolean {
        val iconFavorite = menu?.findItem(R.id.action_favorite)
        iconFavorite?.isVisible = binding.includeDetail.root.visibility == View.VISIBLE

        viewModel.isFavoriteEvent.observe(this){
            if(it) {
                iconFavorite?.setIcon(R.drawable.baseline_favorite_24)
            }else{
                iconFavorite?.setIcon(R.drawable.baseline_favorite_border_24)
            }
            Toast.makeText(this, "Event is favorite: $it", Toast.LENGTH_SHORT).show()
        }
        return super.onPrepareOptionsMenu(menu)
    }

    private fun setDetailData(eventItem: EventItem) {
        val quotaRemaining = (eventItem.quota?: 0) - (eventItem.registrants?: 0)

        with(binding.includeDetail) {
            tvEventName.text = eventItem.name
            tvOwnerName.text = eventItem.ownerName
            tvEventTime.text = eventItem.beginTime
            tvQuotaRemaining.text =
                getString(R.string.quota_remaining, quotaRemaining.toString(), eventItem.quota.toString())
            tvDescription.text = HtmlCompat.fromHtml(
                eventItem.description.toString(),
                HtmlCompat.FROM_HTML_MODE_LEGACY
            )
        }

        Glide.with(this@DetailActivity)
            .load(eventItem.mediaCover)
            .into(binding.ivPicture)

        binding.btnRegister.setOnClickListener {
            val webIntent = Intent(Intent.ACTION_VIEW, Uri.parse(eventItem.link))
            startActivity(webIntent)
        }
        showDetail(true)
        invalidateOptionsMenu()
    }

    private fun showLoading(isLoading: Boolean) {
        if (isLoading) {
            binding.progressBar.visibility = View.VISIBLE
            showDetail(false)
        } else {
            binding.progressBar.visibility = View.GONE
        }
    }

    private fun showButtonRetry(isShowing: Boolean) {
        binding.includeMessage.btnRetry.visibility = if (isShowing) View.VISIBLE else View.GONE
    }

    private fun showMessage(isShowing: Boolean) {
        with(binding.includeMessage) {
            root.visibility = if (isShowing) View.VISIBLE else View.GONE
            tvMessage.visibility = if (isShowing) View.VISIBLE else View.GONE
            tvMessage.text = viewModel.message.value
        }
    }

    private fun showDetail(isShowing: Boolean) {
        with(binding) {
            ivPicture.visibility = if(isShowing) View.VISIBLE else View.INVISIBLE
            includeDetail.root.visibility = if(isShowing) View.VISIBLE else View.INVISIBLE
            btnRegister.visibility = if(isShowing) View.VISIBLE else View.INVISIBLE
        }
    }
}