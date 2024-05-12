package com.example.eduenvi

import android.content.Intent
import android.os.Bundle
import androidx.appcompat.app.AppCompatActivity
import com.example.eduenvi.api.ApiHelper
import com.example.eduenvi.databinding.ActivityMainBinding
import com.example.eduenvi.models.Image
import kotlinx.coroutines.CoroutineScope
import kotlinx.coroutines.Dispatchers
import kotlinx.coroutines.launch
class MainActivity : AppCompatActivity() {
//TODO vyhadzovat upozornenie ked nie je spojenie s internetom, namiesto errorov ze nespravny login alebo heslo, ked je to z dovodu nepripojenia k db
    private lateinit var binding: ActivityMainBinding

    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        binding = ActivityMainBinding.inflate(layoutInflater)
        setContentView(binding.root)

        binding.teacherLoginButton.setOnClickListener {
            val intent = Intent(this, LoginActivity::class.java)
            startActivity(intent)
        }

        binding.studentLoginButton.setOnClickListener {
            val intent = Intent(this, LoginStudentActivity::class.java)
            startActivity(intent)
        }
    }

    fun createImages() { // to ked odstranim db tak aby som mala hned obrazky
        val urls = mutableListOf(
            "bow-and-arrow", "music-note", "sea", "bush", "flower", "flower1", "duckie",
            "gardening", "cookies", "crackers", "birdhouse", "albatross", "bohemian-waxwing",
            "bullfinch", "capercaillie", "cardinal", "common-cuckoo", "condor", "crane",
            "crossbill", "crow", "duck", "eagle", "falcon", "flamingo", "goose", "gull", "hawk",
            "hen", "heron", "hoope", "hornbill", "hummingbird", "jay", "kiwi", "kiwi2", "magpie",
            "mandarin-duck", "ostrich", "paradise-tanager", "parrot", "parrot2", "peacock",
            "pelican", "penguin", "pheasant", "pigeon", "pin-tailed-manakin", "quail", "sparrow",
            "spoonbill", "starling", "stork", "swallow", "swan", "thrush", "tit", "toucan",
            "turkey", "vulture", "woodpecker" )
        for (url in urls) {
            CoroutineScope(Dispatchers.IO).launch {
                val image =  Image(0, "https://davinci.fmph.uniba.sk/~vlckova66/images/$url.png")
                ApiHelper.createImage(image)
            }
        }
    }
}