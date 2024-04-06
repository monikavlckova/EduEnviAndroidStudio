package com.example.eduenvi

import android.content.Intent
import android.os.Bundle
import androidx.appcompat.app.AppCompatActivity
import com.example.eduenvi.adapters.ImageGalleryAdapter
import com.example.eduenvi.databinding.ActivityImageGalleryBinding
import com.example.eduenvi.models.Image
import kotlinx.coroutines.CoroutineScope
import kotlinx.coroutines.Dispatchers
import kotlinx.coroutines.launch
import kotlinx.coroutines.withContext

class ImageGalleryActivity : AppCompatActivity() {

    lateinit var binding: ActivityImageGalleryBinding
    private var images: MutableList<Image>? = null
    private lateinit var adapter: ImageGalleryAdapter
    private val myContext = this
    private val IMAGE_REQUEST_CODE = 100
    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        binding = ActivityImageGalleryBinding.inflate(layoutInflater)
        setContentView(binding.root)

        CoroutineScope(Dispatchers.IO).launch {
            images = ApiHelper.getAllImages() as MutableList<Image>

            withContext(Dispatchers.Main) {
                if (images != null) {
                    adapter = ImageGalleryAdapter(myContext, images!!)
                    binding.imagesLayout.adapter = adapter
                }
            }
        }

        binding.backButton.setOnClickListener {
            val intent = Intent(this, ProfileActivity::class.java) //TODO zmen na predchadzajucu
            startActivity(intent)
        }

        binding.addButton.setOnClickListener {
        //TODO nahrat vlastny z galerie, pridat ho do db a nastavit ho do Constant.Image nacitat
        // predchadzajuce a nastavit ten obrazok
        }
    }

    private fun pickImageGallery() {
        val intent = Intent(Intent.ACTION_PICK)
        intent.type = "image/*"
        startActivityForResult(intent, IMAGE_REQUEST_CODE)
    }

    override fun onActivityResult(requestCode: Int, resultCode: Int, data: Intent?) {
        super.onActivityResult(requestCode, resultCode, data)
        if (requestCode == IMAGE_REQUEST_CODE && resultCode == RESULT_OK) {
            //binding.profileImageEditPanel.setImageURI(data?.data) TODO uloz obrazok do databazy a zobra ho v gride
        }
    }
}