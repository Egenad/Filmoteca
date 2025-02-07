package es.ua.eps.filmoteca.activity

import android.content.Intent
import android.net.Uri
import androidx.appcompat.app.AppCompatActivity
import android.os.Bundle
import android.view.MenuItem
import es.ua.eps.filmoteca.R
import es.ua.eps.filmoteca.databinding.ActivityAboutBinding

class AboutActivity : AppCompatActivity() {

    private lateinit var binding : ActivityAboutBinding
    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)

        binding = ActivityAboutBinding.inflate(layoutInflater)
        setContentView(binding.root)

        setSupportActionBar(binding.includeAppbar.toolbar)
        supportActionBar?.setHomeButtonEnabled(true)
        supportActionBar?.setDisplayHomeAsUpEnabled(true)

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

        binding.back.setOnClickListener{ returnButton() }
    }

    private fun checkResolveNull(customIntent : Intent) : Boolean {
        return customIntent.resolveActivity(packageManager) != null
    }

    override fun onOptionsItemSelected(item: MenuItem): Boolean {
        super.onOptionsItemSelected(item)

        when(item.itemId){
            android.R.id.home -> returnButton()
        }

        return false
    }

    private fun returnButton(){
        startActivity(Intent(this@AboutActivity, MainActivity::class.java)
        .setFlags(Intent.FLAG_ACTIVITY_CLEAR_TOP))
    }
}