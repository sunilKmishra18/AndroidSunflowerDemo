package com.sunilmishra.android.androidsunflowerdemo

import android.content.Intent
import android.os.Build
import android.os.Bundle
import android.view.LayoutInflater
import android.view.Menu
import android.view.MenuInflater
import android.view.MenuItem
import android.view.View
import android.view.ViewGroup
import androidx.core.app.ShareCompat
import androidx.databinding.DataBindingUtil
import androidx.fragment.app.Fragment
import androidx.lifecycle.Observer
import androidx.lifecycle.ViewModelProviders
import com.google.android.material.snackbar.Snackbar
import com.sunilmishra.android.androidsunflowerdemo.databinding.FragmentPlantDetailBinding
import com.sunilmishra.android.androidsunflowerdemo.utilities.InjectorUtils
import com.sunilmishra.android.androidsunflowerdemo.viewmodels.PlantDetailViewModel

/**
 * A fragment representing a single Plant detail screen.
 */
class PlantDetailFragment : Fragment() {

    private lateinit var shareText: String

    override fun onCreateView(
        inflater: LayoutInflater,
        container: ViewGroup?,
        savedInstanceState: Bundle?
    ): View? {
        val plantId = PlantDetailFragmentArgs.fromBundle(arguments).plantId

        val factory = InjectorUtils.providePlantDetailViewModelFactory(requireActivity(), plantId)
        val plantDetailViewModel = ViewModelProviders.of(this, factory)
                .get(PlantDetailViewModel::class.java)

        val binding = DataBindingUtil.inflate<FragmentPlantDetailBinding>(
                inflater, R.layout.fragment_plant_detail, container, false).apply {
            viewModel = plantDetailViewModel
            setLifecycleOwner(this@PlantDetailFragment)
            fab.setOnClickListener { view ->
                plantDetailViewModel.addPlantToGarden()
                Snackbar.make(view, R.string.added_plant_to_garden, Snackbar.LENGTH_LONG).show()
            }
        }

        plantDetailViewModel.plant.observe(this, Observer { plant ->
            shareText = if (plant == null) {
                ""
            } else {
                getString(R.string.share_text_plant, plant.name)
            }
        })

        setHasOptionsMenu(true)

        return binding.root
    }

    override fun onCreateOptionsMenu(menu: Menu?, inflater: MenuInflater?) {
        inflater?.inflate(R.menu.menu_plant_detail, menu)
        super.onCreateOptionsMenu(menu, inflater)
    }

    @Suppress("DEPRECATION")
    override fun onOptionsItemSelected(item: MenuItem?): Boolean {
        return when (item?.itemId) {
            R.id.action_share -> {
                val shareIntent = ShareCompat.IntentBuilder.from(activity)
                    .setText(shareText)
                    .setType("text/plain")
                    .createChooserIntent()
                    .apply {
                        // https://android-developers.googleblog.com/2012/02/share-with-intents.html
                        if (Build.VERSION.SDK_INT >= Build.VERSION_CODES.LOLLIPOP) {
                            // If we're on Lollipop, we can open the intent as a document
                            addFlags(Intent.FLAG_ACTIVITY_NEW_DOCUMENT or Intent.FLAG_ACTIVITY_MULTIPLE_TASK)
                        } else {
                            // Else, we will use the old CLEAR_WHEN_TASK_RESET flag
                            addFlags(Intent.FLAG_ACTIVITY_CLEAR_WHEN_TASK_RESET)
                        }
                    }
                startActivity(shareIntent)
                return true
            }
            else -> super.onOptionsItemSelected(item)
        }
    }
}
