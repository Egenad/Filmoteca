package es.ua.eps.filmoteca

import androidx.appcompat.app.AppCompatActivity
import android.os.Bundle
import android.widget.Toast
import es.ua.eps.filmoteca.databinding.ActivityAboutBinding

class AboutActivity : AppCompatActivity() {

    private lateinit var binding : ActivityAboutBinding
    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        setContentView(R.layout.activity_about)

        binding = ActivityAboutBinding.inflate(layoutInflater)
        setContentView(binding.root)

        binding.webpage.setOnClickListener{
            Toast.makeText(this, R.string.functionality, Toast.LENGTH_LONG).show()
        }

        binding.support.setOnClickListener{
            Toast.makeText(this, R.string.functionality, Toast.LENGTH_LONG).show()
        }

        binding.back.setOnClickListener{
            Toast.makeText(this, R.string.functionality, Toast.LENGTH_LONG).show()
        }
    }
}