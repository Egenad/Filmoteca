package es.ua.eps.filmoteca.activity

import android.Manifest
import android.content.Intent
import android.content.pm.PackageManager
import android.os.Build
import androidx.appcompat.app.AppCompatActivity
import android.os.Bundle
import android.util.Log
import android.view.Menu
import android.view.MenuItem
import android.widget.FrameLayout
import android.widget.Toast
import androidx.activity.result.contract.ActivityResultContracts
import androidx.appcompat.app.AlertDialog
import androidx.core.content.ContextCompat
import androidx.credentials.ClearCredentialStateRequest
import androidx.credentials.CredentialManagerCallback
import androidx.credentials.exceptions.ClearCredentialException
import es.ua.eps.filmoteca.ClientManager
import es.ua.eps.filmoteca.FilmDataSource
import es.ua.eps.filmoteca.R
import es.ua.eps.filmoteca.databinding.ActivityMainBinding
import es.ua.eps.filmoteca.fragment.EXTRA_FILM_POSITION
import es.ua.eps.filmoteca.fragment.FilmDataFragment
import es.ua.eps.filmoteca.fragment.FilmListFragment
import es.ua.eps.filmoteca.persistence.UserData
import es.ua.eps.filmoteca.service.FirebaseMessagingService
import kotlinx.coroutines.CoroutineScope
import kotlinx.coroutines.Dispatchers
import kotlinx.coroutines.launch

const val PREFERENCES_NAME = "FilmotecaPreferences"

class MainActivity : AppCompatActivity(),
            FilmListFragment.OnItemSelectedListener,
            FilmListFragment.OnDeleteItemListener,
            FilmDataFragment.OnDataEditListener,
            FilmDataFragment.OnReturnListener{

    private val requestPermissionLauncher = registerForActivityResult(
        ActivityResultContracts.RequestPermission(),
    ) { isGranted: Boolean ->
        if (isGranted) {
            initFireCloudMessaging()
        } else {
            Log.d("FCM", "Permiso denegado: la app no mostrará notificaciones")
        }
    }

    private var fireBaseService: FirebaseMessagingService? = null

    companion object {
        const val ID_ADD_FILM = Menu.FIRST
        const val ID_CLOSE_SESSION = Menu.FIRST + 1
        const val ID_ABOUT = Menu.FIRST + 2
        const val ID_GROUP = Menu.FIRST
    }

    private lateinit var mainBinding : ActivityMainBinding

    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)

        mainBinding = ActivityMainBinding.inflate(layoutInflater)
        setContentView(mainBinding.root)
        setSupportActionBar(mainBinding.includeAppbar.toolbar)

        if(mainBinding.fragmentContainer != null)
            if(savedInstanceState == null)
                initDynamicFragment()
            else
                toListFragment()

        askNotificationPermission()
    }

    private fun initFireCloudMessaging(){
        fireBaseService = FirebaseMessagingService()
        fireBaseService?.askToken(this)
    }

    override fun onCreateOptionsMenu(menu: Menu?): Boolean {
        super.onCreateOptionsMenu(menu)

        menu?.add(ID_GROUP, ID_ADD_FILM, Menu.NONE, resources.getString(R.string.menu_add_film))
        menu?.add(ID_GROUP, ID_CLOSE_SESSION, Menu.NONE, resources.getString(R.string.menu_close_session))
        menu?.add(ID_GROUP, ID_ABOUT, Menu.NONE, resources.getString(R.string.menu_about))

        return true
    }

    override fun onOptionsItemSelected(item: MenuItem): Boolean {
        super.onOptionsItemSelected(item)

        when(item.itemId){
            ID_ABOUT -> startActivity(Intent(this@MainActivity, AboutActivity::class.java))
            ID_CLOSE_SESSION -> logout()
            ID_ADD_FILM ->{
                FilmDataSource.addDefaultFilm()

                val listFragmentStatic = supportFragmentManager.findFragmentById(R.id.list_fragment) as? FilmListFragment
                val listFragmentDynamic = supportFragmentManager.findFragmentById(R.id.fragment_container) as? FilmListFragment

                listFragmentStatic?.notifyItemInserted()
                listFragmentDynamic?.notifyItemInserted()
            }
            android.R.id.home -> toListFragment()
        }

        return false
    }

    private fun initDynamicFragment(){
        val listFragment = FilmListFragment()
        listFragment.arguments = intent.extras
        supportFragmentManager.beginTransaction().add(R.id.fragment_container, listFragment).commit()
    }

    override fun onItemSelected(position: Int) {

        var dataFragment = supportFragmentManager.findFragmentById(R.id.data_fragment) as? FilmDataFragment

        if (findViewById<FrameLayout>(R.id.fragment_container) == null) {

            supportActionBar?.setHomeButtonEnabled(false)
            supportActionBar?.setDisplayHomeAsUpEnabled(false)

            dataFragment?.updateInterfaceByPositionId(position)

        } else {

            supportActionBar?.setHomeButtonEnabled(true)
            supportActionBar?.setDisplayHomeAsUpEnabled(true)

            dataFragment = FilmDataFragment()
            val args = Bundle()
            args.putInt(EXTRA_FILM_POSITION, position)
            dataFragment.arguments = args

            val t = supportFragmentManager.beginTransaction()
            t.replace(R.id.fragment_container, dataFragment)
            t.addToBackStack(null)
            t.commit()

        }
    }

    override fun onReturn() {
        toListFragment()
    }

    override fun onDeleteItem() {
        val dataFragment = supportFragmentManager.findFragmentById(R.id.data_fragment) as? FilmDataFragment
        dataFragment?.updateInterface(FilmDataSource.getFilmByTitle(dataFragment.getActualFilmTitle()))
    }

    override fun onDataEdit(position: Int) {
        val listFragment = supportFragmentManager.findFragmentById(R.id.list_fragment) as? FilmListFragment
        listFragment?.notifyItemModified(position)
    }

    private fun toListFragment(){

        if(findViewById<FrameLayout>(R.id.fragment_container) != null) {

            supportActionBar?.setHomeButtonEnabled(false)
            supportActionBar?.setDisplayHomeAsUpEnabled(false)

            supportFragmentManager.popBackStack()

            val listFragment = FilmListFragment()
            val t = supportFragmentManager.beginTransaction()
            t.replace(R.id.fragment_container, listFragment)
            t.commit()
        }
    }

    private fun askNotificationPermission() {
        if (Build.VERSION.SDK_INT >= Build.VERSION_CODES.TIRAMISU) {
            if (ContextCompat.checkSelfPermission(this, Manifest.permission.POST_NOTIFICATIONS) ==
                PackageManager.PERMISSION_GRANTED
            ) {
                Log.d("FCM", "Permiso ya concedido")
                initFireCloudMessaging()
            } else if (shouldShowRequestPermissionRationale(Manifest.permission.POST_NOTIFICATIONS)) {
                showPermissionRationaleDialog()
            } else {
                requestPermissionLauncher.launch(Manifest.permission.POST_NOTIFICATIONS)
            }
        }
    }

    private fun showPermissionRationaleDialog() {
        if (Build.VERSION.SDK_INT >= Build.VERSION_CODES.TIRAMISU)
            AlertDialog.Builder(this)
                .setTitle(getString(R.string.permissions_needed))
                .setMessage(getString(R.string.permissions_ask))
                .setPositiveButton(getString(R.string.accept)) { _, _ ->
                    requestPermissionLauncher.launch(Manifest.permission.POST_NOTIFICATIONS)
                }
                .setNegativeButton(getString(R.string.no_thanks)) { dialog, _ ->
                    dialog.dismiss()
                    Log.d("FCM", "Rejected")
                }
                .show()
    }

    private fun logout() {

        val request = ClearCredentialStateRequest()
        UserData.clear()

        try {
            ClientManager.getCredentialManager().clearCredentialStateAsync(
                request,
                null,
                Runnable::run,
                object : CredentialManagerCallback<Void?, ClearCredentialException> {
                    override fun onResult(result: Void?) {
                        Log.d("Logout", "Credentials cleared")
                        CoroutineScope(Dispatchers.Main).launch {
                            val intent = Intent(this@MainActivity, LoginActivity::class.java)
                            startActivity(intent)
                            finish()
                        }
                    }
                    override fun onError(e: ClearCredentialException) {
                        Log.e("Logout", "Error clearing credentials", e)
                        CoroutineScope(Dispatchers.Main).launch {
                            Toast.makeText(
                                this@MainActivity,
                                "Error clearing credentials",
                                Toast.LENGTH_LONG
                            ).show()
                        }
                    }
                }
            )
        }catch (ex: Exception){
            println(ex.printStackTrace())
        }
    }
}