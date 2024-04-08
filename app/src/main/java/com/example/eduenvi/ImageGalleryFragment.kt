package com.example.eduenvi

import android.os.Bundle
import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import android.widget.GridView
import android.widget.ImageButton
import androidx.fragment.app.Fragment
import androidx.lifecycle.ViewModelProvider
import com.example.eduenvi.adapters.ImageGalleryAdapter
import com.example.eduenvi.models.Image
import kotlinx.coroutines.CoroutineScope
import kotlinx.coroutines.Dispatchers
import kotlinx.coroutines.launch
import kotlinx.coroutines.withContext

class ImageGalleryFragment : Fragment() {

    private lateinit var imagesLayout: GridView
    private lateinit var add: ImageButton
    private var images: MutableList<Image>? = null
    private lateinit var adapter: ImageGalleryAdapter
    lateinit var viewModel: ImageViewModel
    override fun onCreateView(
        inflater: LayoutInflater, container: ViewGroup?, savedInstanceState: Bundle?
    ): View? {
        viewModel = ViewModelProvider(requireActivity())[ImageViewModel::class.java]
        return inflater.inflate(R.layout.fragment_image_gallery, container, false)
    }

    override fun onViewCreated(view: View, savedInstanceState: Bundle?) {
        super.onViewCreated(view, savedInstanceState)
        imagesLayout = view.findViewById(R.id.imagesLayout)
        add = view.findViewById(R.id.addButton)

        add.setOnClickListener {
            //TODO nahrat vlastny z galerie, pridat ho do db a nastavit ho do Constant.Image nacitat
            // predchadzajuce a nastavit ten obrazok
        }

        CoroutineScope(Dispatchers.IO).launch {
            images = ApiHelper.getAllImages() as MutableList<Image>

            withContext(Dispatchers.Main) {
                if (images != null) {
                    adapter = ImageGalleryAdapter(requireActivity(), images!!)
                    imagesLayout.adapter = adapter
                }
            }
        }
    }
}