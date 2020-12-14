package org.jglrxavpok.todo.userinfo

import android.Manifest
import android.app.AlertDialog
import android.content.ContentResolver
import android.content.pm.PackageManager
import android.graphics.Bitmap
import android.net.Uri
import android.os.Build
import androidx.appcompat.app.AppCompatActivity
import android.os.Bundle
import android.provider.MediaStore.Files.getContentUri
import android.widget.Toast
import androidx.activity.result.contract.ActivityResultContracts
import androidx.activity.viewModels
import androidx.annotation.RequiresApi
import androidx.core.content.ContextCompat
import androidx.core.content.FileProvider
import androidx.core.net.toFile
import androidx.core.net.toUri
import androidx.databinding.DataBindingUtil
import androidx.lifecycle.lifecycleScope
import kotlinx.coroutines.launch
import okhttp3.MultipartBody
import okhttp3.RequestBody.Companion.asRequestBody
import okhttp3.RequestBody.Companion.toRequestBody
import org.jglrxavpok.todo.BuildConfig
import org.jglrxavpok.todo.R
import org.jglrxavpok.todo.databinding.ActivityUserInfoBinding
import org.jglrxavpok.todo.network.Api
import org.jglrxavpok.todo.network.UserInfo
import java.io.File

class UserInfoActivity : AppCompatActivity() {

    private lateinit var binding: ActivityUserInfoBinding
    private val viewModel : UserInfoViewModel by viewModels()

    private val requestPermissionLauncher =
        registerForActivityResult(ActivityResultContracts.RequestPermission()) { isGranted: Boolean ->
            if (isGranted) openCamera()
            else showExplanationDialog()
        }

    private fun requestCameraPermission() =
        requestPermissionLauncher.launch(Manifest.permission.CAMERA)

    @RequiresApi(Build.VERSION_CODES.Q)
    private fun askCameraPermissionAndOpenCamera() {
        when {
            ContextCompat.checkSelfPermission(this, Manifest.permission.CAMERA)
                    == PackageManager.PERMISSION_GRANTED -> openCamera()
            shouldShowRequestPermissionRationale(Manifest.permission.CAMERA) -> showExplanationDialog()
            else -> requestCameraPermission()
        }
    }

    private fun showExplanationDialog() {
        AlertDialog.Builder(this).apply {
            setMessage("On a besoin de la caméra sivouplé ! 🥺")
            setPositiveButton("Bon, ok") { _, _ ->
                requestCameraPermission()
            }
            setCancelable(true)
            show()
        }
    }

    private val photoUri by lazy {
        FileProvider.getUriForFile(
            this,
            "org.jglrxavpok.todo.fileProvider",
            File.createTempFile("avatar", "jpeg")
        )
    }

    // intent pour la prise de photo
    @RequiresApi(Build.VERSION_CODES.Q)
    private val takePicture = registerForActivityResult(ActivityResultContracts.TakePicture()) { success ->
        if(success) {
            uploadNewAvatar(photoUri)
        } else {
            Toast.makeText(this, "Sans photo c'est pas ouf :'(", Toast.LENGTH_LONG).show()
        }
    }

    // intent pour récupérer dans la galerie
    @RequiresApi(Build.VERSION_CODES.Q)
    private val pickInGallery =
        registerForActivityResult(ActivityResultContracts.GetContent()) { uri ->
            uploadNewAvatar(uri)
        }

    @RequiresApi(Build.VERSION_CODES.Q)
    private fun openCamera() = takePicture.launch(photoUri)

    // convert file to HTTP body
    @RequiresApi(Build.VERSION_CODES.Q)
    private fun convertFileForHTTP(uri: Uri) = MultipartBody.Part.createFormData(
        name = "avatar",
        filename = "temp.jpeg",
        body = contentResolver.openInputStream(uri)!!.readBytes().toRequestBody()
    )

    @RequiresApi(Build.VERSION_CODES.Q)
    private fun uploadNewAvatar(image: Uri) {
        lifecycleScope.launch {
            binding.imageView.setImageURI(image)
            val part = convertFileForHTTP(image)
            Api.userWebService.updateAvatar(part)
            println("upload")
        }
    }

    @RequiresApi(Build.VERSION_CODES.Q)
    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        binding = DataBindingUtil.setContentView(this, R.layout.activity_user_info)

        binding.takePictureButton.setOnClickListener {
            askCameraPermissionAndOpenCamera()
        }
        binding.uploadImageButton.setOnClickListener {
            pickInGallery.launch("image/*")
        }
        viewModel.userInfo.observe(this) {
            binding.firstName.setText(it.firstName)
            binding.familyName.setText(it.lastName)
            binding.email.setText(it.email)
        }
        binding.editUserInfo.setOnClickListener {
            val user = UserInfo (binding.email.text.toString(), binding.firstName.text.toString(), binding.familyName.text.toString(), viewModel.userInfo.value?.avatarURL)
            viewModel.editUserInfo(user)
            viewModel.refreshUserInfo()
            finish()
        }
    }

    override fun onResume() {
        super.onResume()
        viewModel.refreshUserInfo()
    }
}