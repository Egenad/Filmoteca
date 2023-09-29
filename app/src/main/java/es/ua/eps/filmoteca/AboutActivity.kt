package es.ua.eps.filmoteca

import android.content.Intent
import android.net.Uri
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
            val webPageIntent = Intent(Intent.ACTION_VIEW,
                Uri.parse(resources.getString(R.string.webPage)))
            if (checkResolveNull(webPageIntent)) {
                startActivity(webPageIntent)
            }
        }

        binding.support.setOnClickListener{
            val emailIntent = Intent(Intent.ACTION_SENDTO,
                Uri.parse("mailto:" + resources.getString(R.string.email_to)))
            if (checkResolveNull(emailIntent)) {
                startActivity(emailIntent)
            }
        }

        binding.back.setOnClickListener{
            finish()
        }
    }

    private fun checkResolveNull(customIntent : Intent) : Boolean {
        return customIntent.resolveActivity(packageManager) != null
    }
}