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
import androidx.annotation.RequiresApi
import androidx.core.content.ContextCompat
import androidx.core.net.toFile
import androidx.core.net.toUri
import androidx.databinding.DataBindingUtil
import androidx.lifecycle.lifecycleScope
import kotlinx.coroutines.launch
import okhttp3.MultipartBody
import okhttp3.RequestBody.Companion.toRequestBody
import org.jglrxavpok.todo.R
import org.jglrxavpok.todo.databinding.ActivityUserInfoBinding
import org.jglrxavpok.todo.network.Api
import java.io.File

class UserInfoActivity : AppCompatActivity() {

    private lateinit var binding: ActivityUserInfoBinding

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

    // intent pour la prise de photo
    @RequiresApi(Build.VERSION_CODES.Q)
    private val takePicture = registerForActivityResult(ActivityResultContracts.TakePicturePreview()) { picture ->
        val tmpFile = File.createTempFile("avatar", "jpeg")
        tmpFile.outputStream().use {
            picture.compress(Bitmap.CompressFormat.JPEG, 100, it)
        }
        uploadNewAvatar(tmpFile.toUri())
    }

    // intent pour récupérer dans la galerie
    @RequiresApi(Build.VERSION_CODES.Q)
    private val pickInGallery =
        registerForActivityResult(ActivityResultContracts.GetContent()) { uri ->
            uploadNewAvatar(uri)
        }

    @RequiresApi(Build.VERSION_CODES.Q)
    private fun openCamera() = takePicture.launch(null)

    // convert file to HTTP body
    @RequiresApi(Build.VERSION_CODES.Q)
    private fun convertFileForHTTP(uri: Uri): MultipartBody.Part {
        println("URI is $uri")
        return MultipartBody.Part.create(contentResolver.openInputStream(uri)!!.readBytes().toRequestBody())
    }

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
    }
}