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
//TODO spravit panel, co bude na vybratie servera - vyber z existujucich, moznost vytvorit dalsi
class MainActivity : AppCompatActivity() {

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
//TODO zistit, ci je typ1activity v db ak nie, vytvorit ju
    fun createImages() { // to ked odstranim db tak aby som mala hned obrazky
        val urls = listOf(
            "https://davinci.fmph.uniba.sk/~vlckova66/images/bow-and-arrow.png",
            "https://davinci.fmph.uniba.sk/~vlckova66/images/music-note.png",
            "https://davinci.fmph.uniba.sk/~vlckova66/images/sea.png",
            "https://davinci.fmph.uniba.sk/~vlckova66/images/bush.png",
            "https://davinci.fmph.uniba.sk/~vlckova66/images/flower.png",
            "https://davinci.fmph.uniba.sk/~vlckova66/images/flower1.png",
            "https://davinci.fmph.uniba.sk/~vlckova66/images/duckie.png",
            "https://davinci.fmph.uniba.sk/~vlckova66/images/gardening.png",
            "https://davinci.fmph.uniba.sk/~vlckova66/images/cookies.png",
            "https://davinci.fmph.uniba.sk/~vlckova66/images/crackers.png",
            "https://davinci.fmph.uniba.sk/~vlckova66/images/birdhouse.png",
            "https://davinci.fmph.uniba.sk/~vlckova66/images/albatross.png",
            "https://davinci.fmph.uniba.sk/~vlckova66/images/bohemian-waxwing.png",
            "https://davinci.fmph.uniba.sk/~vlckova66/images/bullfinch.png",
            "https://davinci.fmph.uniba.sk/~vlckova66/images/capercaillie.png",
            "https://davinci.fmph.uniba.sk/~vlckova66/images/cardinal.png",
            "https://davinci.fmph.uniba.sk/~vlckova66/images/common-cuckoo.png",
            "https://davinci.fmph.uniba.sk/~vlckova66/images/condor.png",
            "https://davinci.fmph.uniba.sk/~vlckova66/images/crane.png",
            "https://davinci.fmph.uniba.sk/~vlckova66/images/crossbill.png",
            "https://davinci.fmph.uniba.sk/~vlckova66/images/crow.png",
            "https://davinci.fmph.uniba.sk/~vlckova66/images/duck.png",
            "https://davinci.fmph.uniba.sk/~vlckova66/images/eagle.png",
            "https://davinci.fmph.uniba.sk/~vlckova66/images/falcon.png",
            "https://davinci.fmph.uniba.sk/~vlckova66/images/flamingo.png",
            "https://davinci.fmph.uniba.sk/~vlckova66/images/goose.png",
            "https://davinci.fmph.uniba.sk/~vlckova66/images/gull.png",
            "https://davinci.fmph.uniba.sk/~vlckova66/images/hawk.png",
            "https://davinci.fmph.uniba.sk/~vlckova66/images/hen.png",
            "https://davinci.fmph.uniba.sk/~vlckova66/images/heron.png",
            "https://davinci.fmph.uniba.sk/~vlckova66/images/hoope.png",
            "https://davinci.fmph.uniba.sk/~vlckova66/images/hornbill.png",
            "https://davinci.fmph.uniba.sk/~vlckova66/images/hummingbird.png",
            "https://davinci.fmph.uniba.sk/~vlckova66/images/jay.png",
            "https://davinci.fmph.uniba.sk/~vlckova66/images/kiwi.png",
            "https://davinci.fmph.uniba.sk/~vlckova66/images/kiwi2.png",
            "https://davinci.fmph.uniba.sk/~vlckova66/images/magpie.png",
            "https://davinci.fmph.uniba.sk/~vlckova66/images/mandarin-duck.png",
            "https://davinci.fmph.uniba.sk/~vlckova66/images/ostrich.png",
            "https://davinci.fmph.uniba.sk/~vlckova66/images/paradise-tanager.png",
            "https://davinci.fmph.uniba.sk/~vlckova66/images/parrot.png",
            "https://davinci.fmph.uniba.sk/~vlckova66/images/parrot2.png",
            "https://davinci.fmph.uniba.sk/~vlckova66/images/peacock.png",
            "https://davinci.fmph.uniba.sk/~vlckova66/images/pelican.png",
            "https://davinci.fmph.uniba.sk/~vlckova66/images/penguin.png",
            "https://davinci.fmph.uniba.sk/~vlckova66/images/pheasant.png",
            "https://davinci.fmph.uniba.sk/~vlckova66/images/pigeon.png",
            "https://davinci.fmph.uniba.sk/~vlckova66/images/pin-tailed-manakin.png",
            "https://davinci.fmph.uniba.sk/~vlckova66/images/quail.png",
            "https://davinci.fmph.uniba.sk/~vlckova66/images/sparrow.png",
            "https://davinci.fmph.uniba.sk/~vlckova66/images/spoonbill.png",
            "https://davinci.fmph.uniba.sk/~vlckova66/images/starling.png",
            "https://davinci.fmph.uniba.sk/~vlckova66/images/stork.png",
            "https://davinci.fmph.uniba.sk/~vlckova66/images/swallow.png",
            "https://davinci.fmph.uniba.sk/~vlckova66/images/swan.png",
            "https://davinci.fmph.uniba.sk/~vlckova66/images/thrush.png",
            "https://davinci.fmph.uniba.sk/~vlckova66/images/tit.png",
            "https://davinci.fmph.uniba.sk/~vlckova66/images/toucan.png",
            "https://davinci.fmph.uniba.sk/~vlckova66/images/turkey.png",
            "https://davinci.fmph.uniba.sk/~vlckova66/images/vulture.png",
            "https://davinci.fmph.uniba.sk/~vlckova66/images/woodpecker.png")
        for (url in urls){
            CoroutineScope(Dispatchers.IO).launch {
                ApiHelper.createImage(Image(0, url))
            }
        }
    }
}