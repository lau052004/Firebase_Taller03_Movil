package com.lauovalle.taller_03_lauraovalle.Fragments

import android.annotation.SuppressLint
import android.content.ActivityNotFoundException
import android.content.Context
import android.content.Intent
import android.content.pm.PackageManager
import android.location.Location
import android.net.Uri
import android.os.Bundle
import android.os.Environment
import android.provider.MediaStore
import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import android.widget.ImageView
import android.widget.Toast
import androidx.activity.result.contract.ActivityResultContracts
import androidx.appcompat.app.AlertDialog
import androidx.appcompat.app.AppCompatActivity
import androidx.core.content.ContextCompat
import androidx.core.content.FileProvider
import androidx.fragment.app.Fragment
import com.google.android.gms.location.FusedLocationProviderClient
import com.google.android.gms.location.LocationCallback
import com.google.android.gms.location.LocationServices
import com.google.android.gms.maps.CameraUpdateFactory
import com.google.android.gms.maps.model.LatLng
import com.google.android.gms.maps.model.MarkerOptions
import com.google.android.gms.tasks.OnSuccessListener
import com.google.android.gms.tasks.Task
import com.google.android.material.snackbar.Snackbar
import com.google.firebase.auth.AuthResult
import com.google.firebase.auth.FirebaseAuth
import com.google.firebase.auth.ktx.auth
import com.google.firebase.database.DatabaseReference
import com.google.firebase.database.FirebaseDatabase
import com.google.firebase.ktx.Firebase
import com.google.firebase.storage.FirebaseStorage
import com.lauovalle.taller_03_lauraovalle.AuthActivity
import com.lauovalle.taller_03_lauraovalle.FirebaseModel.Model
import com.lauovalle.taller_03_lauraovalle.FirebaseModel.User
import com.lauovalle.taller_03_lauraovalle.HomeActivity
import com.lauovalle.taller_03_lauraovalle.databinding.FragmentSignUpBinding
import java.io.File
import java.io.IOException
import java.text.DateFormat
import java.util.Date
import java.util.logging.Logger


class SignUpFragment : Fragment() {
    private lateinit var binding: FragmentSignUpBinding
    private lateinit var permission: String

    companion object {
        val TAG: String = AuthActivity::class.java.name
    }
    private val logger = Logger.getLogger(TAG)

    // Permission handler


    private val getSimplePermission = registerForActivityResult(ActivityResultContracts.RequestPermission()) { isPermissionGranted ->
        val requestCode = if (isPermissionGranted) {
            when (permission) {
                android.Manifest.permission.CAMERA -> 1
                android.Manifest.permission.ACCESS_FINE_LOCATION -> 2
                else -> 0 // Valor predeterminado en caso de que no se reconozca el permiso
            }
        } else {
            -1 // Permiso denegado
        }
        updateUI(requestCode)
    }

    private var pictureImagePath: Uri? = null
    private var imageViewContainer: ImageView? = null

    private lateinit var mAuth: FirebaseAuth
    private lateinit var dbRef: DatabaseReference
    private var firebaseStorage: FirebaseStorage? = null

    private lateinit var user: User

    // Create ActivityResultLauncher instances
    private val cameraActivityResultLauncher = registerForActivityResult(
        ActivityResultContracts.StartActivityForResult()) { result ->
        if (result.resultCode == AppCompatActivity.RESULT_OK) {
            // Handle camera result
            imageViewContainer!!.setImageURI(pictureImagePath)
            imageViewContainer!!.scaleType = ImageView.ScaleType.FIT_CENTER
            imageViewContainer!!.adjustViewBounds = true
            logger.info("Image capture successfully.")
        } else {
            logger.warning("Image capture failed.")
        }
    }

    private val galleryActivityResultLauncher = registerForActivityResult(
        ActivityResultContracts.StartActivityForResult()) { result ->
        if (result.resultCode == AppCompatActivity.RESULT_OK) {
            // Handle gallery result
            val imageUri: Uri? = result.data!!.data
            pictureImagePath = imageUri
            imageViewContainer!!.setImageURI(imageUri)
            logger.info("Image loaded successfully")
        }
    }

    override fun onCreateView(
        inflater: LayoutInflater, container: ViewGroup?,
        savedInstanceState: Bundle?
    ): View {
        // Inflate the layout for this fragment
        binding = FragmentSignUpBinding.inflate(inflater, container, false)
        return binding.root
    }

    override fun onViewCreated(view: View, savedInstanceState: Bundle?) {
        super.onViewCreated(view, savedInstanceState)

        imageViewContainer = binding.ProfilePhoto

        // Acceder a la latitud y la longitud
        permission = android.Manifest.permission.ACCESS_FINE_LOCATION
        verifyPermissions(requireContext(), android.Manifest.permission.ACCESS_FINE_LOCATION, "El permiso es requerido para acceder a la latitud y longitud")

        binding.cameraBtn.setOnClickListener {
            permission = android.Manifest.permission.CAMERA
            verifyPermissions(requireContext(), android.Manifest.permission.CAMERA, "El permiso es requerido para...")
        }
        binding.galleryBtn.setOnClickListener {
            val pickGalleryImage = Intent(Intent.ACTION_PICK)
            pickGalleryImage.type = "image/*"
            galleryActivityResultLauncher.launch(pickGalleryImage)
        }

        mAuth = Firebase.auth
        dbRef = FirebaseDatabase.getInstance().getReference("Usuarios")
        firebaseStorage = FirebaseStorage.getInstance()

        setup()
    }

    private fun setup() {
        binding.SignupBtn.setOnClickListener{
            if(binding.EmailAddress.text.isNotEmpty() && binding.Password.text.isNotEmpty()) {
                createUser()
            } else {
                Snackbar.make(requireActivity().findViewById(android.R.id.content), "Por favor, llene todos los campos", Snackbar.LENGTH_LONG).show()
            }
        }
    }

    private fun createUser() {
        val email = binding.EmailAddress.text.toString()
        val password = binding.Password.text.toString()
        if (binding.EmailAddress.text.isEmpty() || binding.Name.text.isEmpty() || binding.LastName.text.isEmpty() || binding.Password.text.isEmpty() || binding.Phone.text.isEmpty() || binding.Identification.text.isEmpty() || binding.longitud.text.isEmpty() || binding.longitud.text.isEmpty()) {
            Snackbar.make(requireActivity().findViewById(android.R.id.content), "Por favor, llene todos los campos", Snackbar.LENGTH_LONG).show()
            return
        } else if (pictureImagePath == null) {
            Snackbar.make(requireActivity().findViewById(android.R.id.content), "Por favor, seleccione una imagen", Snackbar.LENGTH_LONG).show()
            return
        }

        mAuth.createUserWithEmailAndPassword(email, password).addOnCompleteListener {
            if (it.isSuccessful) {
                // Guardar la información del usuario
                logger.info("Usuario creado correctamente")
                saveUserData {
                    // Ir a la pantalla de inicio
                    val homeIntent = Intent(requireContext(), HomeActivity::class.java)
                    Snackbar.make(requireActivity().findViewById(android.R.id.content), "Usuario creado con éxito", Snackbar.LENGTH_LONG).show()
                    homeIntent.putExtra("email", email)
                    homeIntent.putExtra("password", password)
                    startActivity(homeIntent)
                }
            } else {
                showAlert()
            }
        }
    }


    private fun saveUserData(onSuccessListener: OnSuccessListener<Void>? = null) {
        user = User()
        if (mAuth.currentUser == null) {
            showAlert()
            return
        }
        val userId = mAuth.currentUser?.uid.toString()
        user.key = userId
        user.nombre = binding.Name.text.toString()
        user.correo = binding.EmailAddress.text.toString()
        user.apellido = binding.LastName.text.toString()
        user.phone = binding.Phone.text.toString().toLong()
        user.nroId = binding.Identification.text.toString().toLong()
        user.latitud = binding.latitud.text.toString().toDouble()
        user.longitud = binding.longitud.text.toString().toDouble()

        dbRef.child(userId).setValue(user).addOnCompleteListener {
            guardarImagen(userId, onSuccessListener)
        }.addOnFailureListener {err ->
            Toast.makeText(requireContext(),"Error: ${err.message}", Toast.LENGTH_LONG).show()
        }
    }

    private fun guardarImagen(userId: String, onSuccessListener: OnSuccessListener<Void>? = null) {
        // Guardar la foto en storage
        val reference = firebaseStorage!!.reference.child("Images").child(userId)
        reference.putFile(pictureImagePath!!).addOnSuccessListener {
            reference.downloadUrl.addOnSuccessListener {
                val model = Model()
                model.image = pictureImagePath.toString()
                dbRef.child("Imagenes").push().setValue(model).addOnSuccessListener {
                    requireActivity().finish()
                    onSuccessListener?.onSuccess(null)
                }.addOnFailureListener{
                    Toast.makeText(requireContext(), "Error al subir la imagen", Toast.LENGTH_LONG).show()
                }
            }
        }
    }

    // Verify permission to access contacts info
    private fun verifyPermissions(context: Context, permission: String, rationale: String) {
        when {
            ContextCompat.checkSelfPermission(context, permission) == PackageManager.PERMISSION_GRANTED -> {
                Snackbar.make(requireView(), "Ya tengo los permisos 😜", Snackbar.LENGTH_LONG).show()
                if(permission == android.Manifest.permission.ACCESS_FINE_LOCATION) {
                    updateUI(2)
                } else {
                    updateUI(1)
                }
            }
            shouldShowRequestPermissionRationale(permission) -> {
                // We display a snackbar with the justification for the permission, and once it disappears, we request it again.
                val snackbar = Snackbar.make(requireActivity().findViewById(android.R.id.content), rationale, Snackbar.LENGTH_LONG)
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

    // Update activity behavior and actions according to result of permission request
    @SuppressLint("MissingPermission")
    private fun updateUI(permission : Int) {
        if (permission == 0) {
            logger.info("Permission granted but not recognized")
        }
        if (permission == 1) {
            //granted
            logger.info("Permission granted")
            dispatchTakePictureIntent()
        }
        else if(permission == 2) {
            logger.info("Permission granted")
            var fusedLocationClient: FusedLocationProviderClient = LocationServices.getFusedLocationProviderClient(requireContext())

            fusedLocationClient.lastLocation.addOnSuccessListener { location: Location? ->
                if (location != null) {
                    val ubicacion = LatLng(location.latitude, location.longitude)

                    binding.latitud.text = ubicacion.latitude.toString()
                    binding.longitud.text = ubicacion.longitude.toString()
                }
            }
        } else {
            logger.warning("Permission denied")
        }
    }

    private fun dispatchTakePictureIntent() {
        val takePictureIntent = Intent(MediaStore.ACTION_IMAGE_CAPTURE)
        // Crear el archivo donde debería ir la foto
        var imageFile: File? = null
        pictureImagePath = null
        imageViewContainer!!.setImageURI(null)
        try {
            imageFile = createImageFile()
        } catch (ex: IOException) {
            logger.warning(ex.message)
        }
        // Continua si el archivo ha sido creado exitosamente
        if (imageFile != null) {
            // Guardar un archivo: Ruta para usar con ACTION_VIEW intents
            pictureImagePath = FileProvider.getUriForFile(requireContext(),"com.taller3.fileprovider", imageFile)
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
        val imageFile = File(activity?.getExternalFilesDir(Environment.DIRECTORY_PICTURES), imageFileName)
        return imageFile
    }

    private fun showAlert() {
        val builder = AlertDialog.Builder(requireContext())
        builder.setTitle("ERROR")
        builder.setMessage("Se ha producido un error autenticando al usuario")
        builder.setPositiveButton("Aceptar",null)
        val dialog: AlertDialog = builder.create()
        dialog.show()
    }
}