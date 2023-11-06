package com.lauovalle.taller_03_lauraovalle

import android.content.ActivityNotFoundException
import android.content.Context
import android.content.Intent
import android.content.pm.PackageManager
import android.net.Uri
import androidx.appcompat.app.AppCompatActivity
import android.os.Bundle
import android.os.Environment
import android.provider.MediaStore
import android.view.View
import android.widget.ImageView
import android.widget.Toast
import androidx.activity.result.contract.ActivityResultContracts
import androidx.appcompat.app.AlertDialog
import androidx.core.content.ContextCompat
import androidx.core.content.FileProvider
import com.google.android.material.snackbar.Snackbar
import com.google.firebase.auth.FirebaseAuth
import com.google.firebase.database.DatabaseReference
import com.google.firebase.database.FirebaseDatabase
import com.google.firebase.storage.FirebaseStorage
import com.lauovalle.taller_03_lauraovalle.FirebaseModel.Model
import com.lauovalle.taller_03_lauraovalle.FirebaseModel.User
import com.lauovalle.taller_03_lauraovalle.databinding.ActivityAuthBinding
import java.io.File
import java.io.IOException
import java.text.DateFormat
import java.util.Date
import java.util.logging.Logger

class AuthActivity : AppCompatActivity() {
    private lateinit var binding: ActivityAuthBinding

    // Database
    private lateinit var dbRef: DatabaseReference
    private lateinit var user: User

    // Storage
    private var firebaseStorage: FirebaseStorage? = null

    companion object {
        val TAG: String = AuthActivity::class.java.name
    }

    private val logger = Logger.getLogger(TAG)

    // Permission handler
    private val getSimplePermission = registerForActivityResult(
        ActivityResultContracts.RequestPermission()) {
        updateUI(it)
    }

    private var pictureImagePath: Uri? = null

    // Create ActivityResultLauncher instances
    private val cameraActivityResultLauncher = registerForActivityResult(
        ActivityResultContracts.StartActivityForResult()) { result ->
        if (result.resultCode == RESULT_OK) {
            // Handle camera result
            binding.ProfilePhoto.setImageURI(pictureImagePath)
            binding.ProfilePhoto.scaleType = ImageView.ScaleType.FIT_CENTER
            binding.ProfilePhoto.adjustViewBounds = true
            logger.info("Image capture successfully.")
        } else {
            logger.warning("Image capture failed.")
        }
    }

    private val pickMedia = registerForActivityResult(ActivityResultContracts.StartActivityForResult()) { result ->
        if (result.resultCode == RESULT_OK) {
            val data: Intent? = result.data
            pictureImagePath = data?.data

            binding.ProfilePhoto.setImageURI(pictureImagePath)
        }
    }

    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)

        // --------------------  BINDING
        binding = ActivityAuthBinding.inflate(layoutInflater)
        // llama al método getRoot() para obtener una referencia a la vista raíz
        val view: View = binding.root
        setContentView(view)

        // --------------------- DATA BASE REFERENCE
        dbRef = FirebaseDatabase.getInstance().getReference("Usuarios")
        firebaseStorage = FirebaseStorage.getInstance()

        setup()
    }

    private fun setup() {
        title = "Atenticación"

        // ----------------------- CARGAR FOTO
        // Pick Image from gallery
        binding.galleryBtn.setOnClickListener{
            val pickImageIntent = Intent(Intent.ACTION_PICK, MediaStore.Images.Media.EXTERNAL_CONTENT_URI)
            pickMedia.launch(pickImageIntent)
        }


        // Take photo
        binding.cameraBtn.setOnClickListener {
            // Pedir el permiso cuando la aplicación inicie
            logger.info("Se va a solicitar el permiso")
            verifyPermissions(this, android.Manifest.permission.CAMERA, "El permiso es requerido para...")
        }

        // ----------------------- REGISTRARSE
        binding.SingUpBtn.setOnClickListener{
            if(binding.EmailAddress.text.isNotEmpty() && binding.Password.text.isNotEmpty())
            {
                FirebaseAuth.getInstance().createUserWithEmailAndPassword(binding.EmailAddress.text.toString(),binding.Password.text.toString()).addOnCompleteListener{
                    if(it.isSuccessful) {
                        // Guardar la información del usuario
                        saveUserData()
                        // Intent para ver si los datos se guardan bien
                        val homeIntent = Intent(this, HomeActivity::class.java)
                        homeIntent.putExtra("email",binding.EmailAddress.text.toString())
                        homeIntent.putExtra("password",binding.Password.text.toString())
                        startActivity(homeIntent)
                    } else {
                        showAlert()
                    }
                }
            }
        }


        // ------------------------- INICIAR SESIÓN
        binding.LogInBtn.setOnClickListener{
            if(binding.EmailAddress.text.isNotEmpty() && binding.Password.text.isNotEmpty())
            {
                FirebaseAuth.getInstance().signInWithEmailAndPassword(binding.EmailAddress.text.toString(),binding.Password.text.toString()).addOnCompleteListener{
                    if(it.isSuccessful) {
                        val homeIntent = Intent(this, HomeActivity::class.java)
                        homeIntent.putExtra("email",binding.EmailAddress.text.toString())
                        homeIntent.putExtra("password",binding.Password.text.toString())
                        startActivity(homeIntent)

                    } else {
                        showAlert()
                    }
                }
            }
        }

    }

    private fun saveUserData() {
        if(binding.EmailAddress.text.isEmpty() || binding.Name.text.isEmpty() || binding.LastName.text.isEmpty() || binding.Password.text.isEmpty() || binding.Phone.text.isEmpty() || binding.Identification.text.isEmpty()) {
            // SnackBar pidiendo que se llenen todos los datos
            TODO()
        }
        else {
            user = User()
            val userId = dbRef.push().key!!
            user.key = userId
            user.nombre = binding.Name.text.toString()
            user.apellido = binding.LastName.text.toString()
            user.phone = binding.Phone.text.toString()
            user.nroId = binding.Identification.text.toString()

            dbRef.child(userId).setValue(user).addOnCompleteListener{
                Toast.makeText(this,"Datos guardados correctamente", Toast.LENGTH_LONG).show()
            }.addOnFailureListener {err ->
                Toast.makeText(this,"Error: ${err.message}", Toast.LENGTH_LONG).show()
            }

            // Guardar la foto en storage
            val reference = firebaseStorage!!.reference.child("Images").child(userId)
            reference.putFile(pictureImagePath!!).addOnSuccessListener {
                reference.downloadUrl.addOnSuccessListener {
                    val model = Model()
                    model.image = pictureImagePath.toString()
                    dbRef.child("Imagenes").push().setValue(model).addOnSuccessListener {
                        finish()
                    }.addOnFailureListener{
                        Toast.makeText(this, "Error al subir la imagen", Toast.LENGTH_LONG).show()
                    }
                }
            }

            TODO("Incluir la latitud y la longitud")
        }
    }

    private fun verifyPermissions(context: Context, permission: String, rationale: String) {
        when {
            ContextCompat.checkSelfPermission(context, permission) == PackageManager.PERMISSION_GRANTED -> {
                Snackbar.make(binding.root, "Ya tengo los permisos 😜", Snackbar.LENGTH_LONG).show()
                updateUI(true)
            }
            shouldShowRequestPermissionRationale(permission) -> {
                // We display a snackbar with the justification for the permission, and once it disappears, we request it again.
                val snackbar = Snackbar.make(binding.root, rationale, Snackbar.LENGTH_LONG)
                snackbar.addCallback(object : Snackbar.Callback() {
                    override fun onDismissed(snackbar: Snackbar, event: Int) {
                        if (event == DISMISS_EVENT_TIMEOUT) {
                            getSimplePermission.launch(permission)
                        }
                    }
                })
                snackbar.show()
            }
            else -> {
                getSimplePermission.launch(permission)
            }
        }
    }



    private fun updateUI(permission: Boolean) {
        if (permission) {
            //granted
            dipatchTakePictureIntent()
        } else {
            logger.warning("Permission denied")
        }
    }

    private fun dipatchTakePictureIntent() {
        val takePictureIntent = Intent(MediaStore.ACTION_IMAGE_CAPTURE)
        // Crear el archivo donde debería ir la foto
        var imageFile: File? = null
        try {
            imageFile = createImageFile()
        } catch (ex: IOException) {
            logger.warning(ex.message)
        }
        // Continua si el archivo ha sido creado exitosamente
        if (imageFile != null) {
            // Guardar un archivo: Ruta para usar con ACTION_VIEW intents
            pictureImagePath = FileProvider.getUriForFile(this,"com.example.android.fileprovider", imageFile)
            logger.info("Ruta: $pictureImagePath")
            takePictureIntent.putExtra(MediaStore.EXTRA_OUTPUT, pictureImagePath)
            try {
                cameraActivityResultLauncher.launch(takePictureIntent)
            } catch (e: ActivityNotFoundException) {
                logger.warning("Camera app not found.")
            }
        }
    }

    @Throws(IOException::class)
    private fun createImageFile(): File {
        //Crear un nombre de archivo de imagen
        val timeStamp: String = DateFormat.getDateInstance().format(Date())
        val imageFileName = "${timeStamp}.jpg"
        return File(getExternalFilesDir(Environment.DIRECTORY_PICTURES), imageFileName)
    }


    private fun showAlert() {
        val builder = AlertDialog.Builder(this)
        builder.setTitle("ERROR")
        builder.setMessage("Se ha producido un error autenticando al usuario")
        builder.setPositiveButton("Aceptar",null)
        val dialog: AlertDialog = builder.create()
        dialog.show()
    }
}