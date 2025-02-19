package es.ua.eps.filmoteca.activity

import android.content.Intent
import android.net.Uri
import android.os.Bundle
import androidx.activity.enableEdgeToEdge
import androidx.appcompat.app.AppCompatActivity
import androidx.core.view.ViewCompat
import androidx.core.view.WindowInsetsCompat
import es.ua.eps.filmoteca.ClientManager
import es.ua.eps.filmoteca.R
import es.ua.eps.filmoteca.USER_NAME
import es.ua.eps.filmoteca.USER_PHOTO_URL
import es.ua.eps.filmoteca.databinding.ActivityLoginBinding
import es.ua.eps.filmoteca.persistence.UserData

class LoginActivity : AppCompatActivity() {

    private lateinit var binding : ActivityLoginBinding

    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        enableEdgeToEdge()

        binding = ActivityLoginBinding.inflate(layoutInflater)
        setContentView(binding.root)

        setSupportActionBar(binding.includeAppbar.toolbar)

        ViewCompat.setOnApplyWindowInsetsListener(findViewById(R.id.main)) { v, insets ->
            val systemBars = insets.getInsets(WindowInsetsCompat.Type.systemBars())
            v.setPadding(systemBars.left, systemBars.top, systemBars.right, systemBars.bottom)
            insets
        }

        val clientManager = ClientManager(this)

        binding.signInButton.setOnClickListener {
            clientManager.startGoogleSignInFlow()
        }

        // Check if an user already singed in
        if(UserData.userSignedIn(this)){
            startActivity(Intent(this, MainActivity::class.java))
            finish()
        }
    }
}