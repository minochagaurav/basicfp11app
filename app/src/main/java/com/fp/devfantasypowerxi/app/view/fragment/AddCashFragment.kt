package com.fp.devfantasypowerxi.app.view.fragment

import android.annotation.SuppressLint
import android.app.Activity
import android.content.Intent
import android.graphics.Bitmap
import android.graphics.BitmapFactory
import android.net.Uri
import android.os.Bundle
import android.os.Environment
import android.provider.MediaStore
import android.util.Log
import android.view.LayoutInflater
import android.view.Menu
import android.view.View
import android.view.ViewGroup
import android.widget.Toast
import androidx.appcompat.app.AppCompatActivity
import androidx.databinding.DataBindingUtil
import androidx.fragment.app.Fragment
import androidx.fragment.app.FragmentManager
import androidx.fragment.app.FragmentStatePagerAdapter
import com.fp.devfantasypowerxi.MyApplication
import com.fp.devfantasypowerxi.R
import com.fp.devfantasypowerxi.app.api.request.BaseRequest
import com.fp.devfantasypowerxi.app.api.request.JoinContestRequest
import com.fp.devfantasypowerxi.app.api.response.ImageUploadResponse
import com.fp.devfantasypowerxi.app.api.response.NormalResponse
import com.fp.devfantasypowerxi.app.api.service.OAuthRestService
import com.fp.devfantasypowerxi.app.utils.AppUtils
import com.fp.devfantasypowerxi.app.view.activity.PersonalDetailsActivity
import com.fp.devfantasypowerxi.app.view.activity.VerifyAccountActivity
import com.fp.devfantasypowerxi.app.view.viewmodel.TeamViewModel
import com.fp.devfantasypowerxi.common.api.ApiException
import com.fp.devfantasypowerxi.common.api.CustomCallAdapter
import com.fp.devfantasypowerxi.common.api.Resource
import com.fp.devfantasypowerxi.common.utils.Constants
import com.fp.devfantasypowerxi.databinding.FragmentAddCashBinding
import okhttp3.MediaType.Companion.toMediaTypeOrNull
import okhttp3.MultipartBody
import okhttp3.RequestBody
import retrofit2.Response
import java.io.File
import java.io.FileInputStream
import java.io.FileOutputStream
import java.io.IOException
import java.text.SimpleDateFormat
import java.util.*
import javax.inject.Inject

// made by Gaurav Minocha
class AddCashFragment : Fragment() {
    lateinit var mainBinding: FragmentAddCashBinding
    lateinit var mAdapter: TabAdapter
    lateinit var teamViewModel: TeamViewModel

    @Inject
    lateinit var oAuthRestService: OAuthRestService
    private var fileImage = ""
    private var fileName = ""
    private var currentPhotoPath = ""
    private val GALLERY_REQUEST_CODE = 100
    private val CAMERA_REQUEST_CODE = 101

    override fun onCreateView(
        inflater: LayoutInflater, container: ViewGroup?,
        savedInstanceState: Bundle?,
    ): View {
        mainBinding =
            DataBindingUtil.inflate(inflater, R.layout.fragment_add_cash, container, false)
        mainBinding.ivUserProfile.setImageURI(MyApplication.preferenceDB!!.getString(Constants.SHARED_PREFERENCE_USER_PIC))
        mainBinding.tvUserName.text =
            MyApplication.preferenceDB!!.getString(Constants.SHARED_PREFERENCE_USER_NAME)
        mAdapter = TabAdapter(fragmentManager)
        /* mAdapter.addFragment(BalanceFragment(), "Balance")*/
        mAdapter.addFragment(PlayingHistoryFragment(), "Playing History")
        mAdapter.addFragment(TransactionsFragment(), "Transactions")
        mainBinding.viewPager.adapter = mAdapter
        mainBinding.tabLayout.setupWithViewPager(mainBinding.viewPager)
        mainBinding.btnEditProfile.setOnClickListener {
            startActivity(Intent(activity, PersonalDetailsActivity::class.java))
        }
        mainBinding.btnVerifyAccount.setOnClickListener {
            startActivity(Intent(activity, VerifyAccountActivity::class.java))
        }
        mainBinding.fcCoinsImage.setOnClickListener {
            AppUtils.showToolTip(
                requireContext(),
                Constants.FANTASY_COINS,
                mainBinding.fcCoinsLayout,
                requireActivity().resources.getColor(R.color.green_color)
            )
        }
        mainBinding.ivUserProfile.setOnClickListener {
            /*      val alertLayout: View = layoutInflater.inflate(R.layout.layout_pic_upload, null)
                  val tvGallery = alertLayout.findViewById<View>(R.id.tv_gallery) as TextView
                  val tvCamera = alertLayout.findViewById<View>(R.id.tv_camera) as TextView
                  val tvCancel = alertLayout.findViewById<View>(R.id.tv_cancel) as TextView
                  val builder = AlertDialog.Builder(
                      requireContext()
                  )
                  builder.setView(alertLayout)
                  val alert = builder.create()
                  tvGallery.setOnClickListener {
                      alert.dismiss()
                      openGallery()
                  }
                  tvCamera.setOnClickListener {
                      alert.dismiss()
                      openCamera()
                  }
                  tvCancel.setOnClickListener { alert.dismiss() }
                  alert.show()*/
        }

        checkBalance()
        return mainBinding.root
    }


    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        MyApplication.getAppComponent()!!.inject(this@AddCashFragment)
        teamViewModel = TeamViewModel().create(this@AddCashFragment)
        MyApplication.getAppComponent()!!.inject(teamViewModel)
    }

    /*   private fun openGallery() {
           val intent = Intent(Intent.ACTION_PICK, MediaStore.Images.Media.EXTERNAL_CONTENT_URI)
           startActivityForResult(intent, GALLERY_REQUEST_CODE)
       }

       private fun openCamera() {
           dispatchTakePictureIntent()
       }*/

    // setup tabs
    class TabAdapter internal constructor(fm: FragmentManager?) :
        FragmentStatePagerAdapter(fm!!) {
        private val mFragmentList: MutableList<Fragment> = ArrayList()
        private val mFragmentTitleList: MutableList<String> = ArrayList()
        override fun getItem(position: Int): Fragment {
            return mFragmentList[position]
        }

        fun addFragment(fragment: Fragment, title: String) {
            mFragmentList.add(fragment)
            mFragmentTitleList.add(title)
        }

        override fun getPageTitle(position: Int): CharSequence {
            return mFragmentTitleList[position]
        }

        override fun getCount(): Int {
            return mFragmentList.size
        }
    }

    @SuppressLint("SimpleDateFormat")
    @Throws(IOException::class)
    private fun createImageFile(): File? {
        // Create an image file name
        val timeStamp = SimpleDateFormat("yyyyMMdd_HHmmss").format(Date())
        val imageFileName = "JPEG_" + timeStamp + "_"
        val storageDir = requireContext().getExternalFilesDir(Environment.DIRECTORY_PICTURES)
        val image = File.createTempFile(
            imageFileName,  /* prefix */
            ".jpg",  /* suffix */
            storageDir /* directory */
        )

        // Save a file: path for use with ACTION_VIEW intents
        currentPhotoPath = image.absolutePath
        return image
    }

    private fun getPath(uri: Uri): String? {
        val projection = arrayOf(MediaStore.Images.Media.DATA)
        val cursor = requireActivity().contentResolver.query(uri, projection, null, null, null)
            ?: return null
        val columnIndex = cursor.getColumnIndexOrThrow(MediaStore.Images.Media.DATA)
        cursor.moveToFirst()
        val s = cursor.getString(columnIndex)
        cursor.close()
        return s
    }


    fun saveBitmapToFile(file: File): File? {
        return try {

            // BitmapFactory options to downsize the image
            val o = BitmapFactory.Options()
            o.inJustDecodeBounds = true
            o.inSampleSize = 6
            // factor of downsizing the image
            var inputStream = FileInputStream(file)
            //Bitmap selectedBitmap = null;
            BitmapFactory.decodeStream(inputStream, null, o)
            inputStream.close()

            // The new size we want to scale to
            val REQUIRED_SIZE = 75

            // Find the correct scale value. It should be the power of 2.
            var scale = 1
            while (o.outWidth / scale / 2 >= REQUIRED_SIZE &&
                o.outHeight / scale / 2 >= REQUIRED_SIZE
            ) {
                scale *= 2
            }
            val o2 = BitmapFactory.Options()
            o2.inSampleSize = scale
            inputStream = FileInputStream(file)
            val selectedBitmap = BitmapFactory.decodeStream(inputStream, null, o2)
            inputStream.close()

            // here i override the original image file
            file.createNewFile()
            val outputStream = FileOutputStream(file)
            selectedBitmap!!.compress(Bitmap.CompressFormat.JPEG, 100, outputStream)
            file
        } catch (e: Exception) {
            null
        }
    }

    fun uploadUserImage() {
        mainBinding.refreshing = true
        val userId: String =
            MyApplication.preferenceDB!!.getString(Constants.SHARED_PREFERENCE_USER_ID)!!
        val requestBodyUserId =
            RequestBody.create("multipart/form-data".toMediaTypeOrNull(), userId)
        var file = File(fileImage)
        val length = file.length() / (1024 * 1024)
        if (length > 1) {
            file = saveBitmapToFile(file)!!
        }
        val requestFile = RequestBody.create("multipart/form-data".toMediaTypeOrNull(), file)
        val uploadPic: MultipartBody.Part =
            MultipartBody.Part.createFormData("file", file.name, requestFile)
        val imageUploadResponseCustomCall: CustomCallAdapter.CustomCall<ImageUploadResponse> =
            oAuthRestService.uploadUserImage(requestBodyUserId, uploadPic)
        imageUploadResponseCustomCall.enqueue(object :
            CustomCallAdapter.CustomCallback<ImageUploadResponse> {
            override fun success(response: Response<ImageUploadResponse>) {
                mainBinding.refreshing = false
                val imageUploadResponse: ImageUploadResponse =
                    response.body() ?: ImageUploadResponse()
                if (imageUploadResponse.status == 1) {
                    if (imageUploadResponse.result[0].status == "1") {
                        fileName = imageUploadResponse.result[0].image
                        MyApplication.preferenceDB!!.putString(
                            Constants.SHARED_PREFERENCE_USER_PIC,
                            fileName
                        )
                        mainBinding.ivUserProfile.setImageURI(
                            MyApplication.preferenceDB!!.getString(
                                Constants.SHARED_PREFERENCE_USER_PIC
                            )
                        )
                        AppUtils.showError(activity as AppCompatActivity, "Image uploded.")
                    } else {
                        AppUtils.showError(
                            activity as AppCompatActivity,
                            "Error in image uploading."
                        )
                    }
                } else {
                    AppUtils.showError(
                        activity as AppCompatActivity,
                        imageUploadResponse.message
                    )
                }
            }

            override fun failure(e: ApiException?) {
                mainBinding.refreshing = false
                e!!.printStackTrace()
                if (e.response != null) {
                    if (e.response.code() in 400..403) {
                        logout()
                    }
                }
            }
        })
    }

    fun logout() {
        mainBinding.refreshing = true
        val baseRequest = BaseRequest()
        baseRequest.user_id =
            MyApplication.preferenceDB!!.getString(Constants.SHARED_PREFERENCE_USER_ID)!!
        val userFullDetailsResponseCustomCall: CustomCallAdapter.CustomCall<NormalResponse> =
            oAuthRestService.logout(baseRequest)
        userFullDetailsResponseCustomCall.enqueue(object :
            CustomCallAdapter.CustomCallback<NormalResponse> {
            override fun success(response: Response<NormalResponse>) {
                mainBinding.refreshing = false
                val updateProfileResponse: NormalResponse = response.body()!!
                val activity = activity
                if (activity != null) {
                    if (updateProfileResponse.status == 1 || updateProfileResponse.status == 0) {
                        MyApplication.logout(requireActivity())
                    } else {
                        AppUtils.showError(
                            context as AppCompatActivity,
                            updateProfileResponse.message
                        )
                    }
                }
            }

            override fun failure(e: ApiException?) {
                mainBinding.refreshing = false
            }
        })
    }

    override fun onActivityResult(requestCode: Int, resultCode: Int, data: Intent?) {
        super.onActivityResult(requestCode, resultCode, data)
        var flag = false
        if (requestCode == GALLERY_REQUEST_CODE) {
            if (resultCode == Activity.RESULT_OK) {
                val image = data!!.data
                flag = true
                fileImage = getPath(image!!)!!
            }
        } else if (requestCode == CAMERA_REQUEST_CODE) {
            if (resultCode == Activity.RESULT_OK) {
                flag = true
                fileImage = currentPhotoPath
            }
        }
        if (flag) {
            uploadUserImage()
        }
    }

    /*  private fun dispatchTakePictureIntent() {
          val takePictureIntent = Intent(MediaStore.ACTION_IMAGE_CAPTURE)
          // Ensure that there's a camera activity to handle the intent
          if (takePictureIntent.resolveActivity(requireContext().packageManager) != null) {
              // Create the File where the photo should go
              var photoFile: File? = null
              try {
                  photoFile = createImageFile()
              } catch (ex: IOException) {
                  // Error occurred while creating the File
              }
              // Continue only if the File was successfully created
              if (photoFile != null) {
                  val photoURI = FileProvider.getUriForFile(
                      requireContext(),
                      "com.img.fantasypowerxi.provider",
                      photoFile
                  )
                  takePictureIntent.putExtra(MediaStore.EXTRA_OUTPUT, photoURI)
                  startActivityForResult(takePictureIntent, CAMERA_REQUEST_CODE)
              }
          }
      }*/

    override fun onPrepareOptionsMenu(menu: Menu) {
        val itemNotification = menu.findItem(R.id.navigation_notification)
        if (itemNotification != null) itemNotification.isVisible = false
    }


    private fun checkBalance() {
        val request = JoinContestRequest()
        request.user_id =
            MyApplication.preferenceDB!!.getString(Constants.SHARED_PREFERENCE_USER_ID)!!
        teamViewModel.loadBalanceRequest(request)
        teamViewModel.getBalanceData().observe(viewLifecycleOwner) { arrayListResource ->
            Log.d("Status ", "" + arrayListResource.status)
            when (arrayListResource.status) {
                Resource.Status.LOADING -> {
                    // mainBinding.refreshing = true
                }
                Resource.Status.ERROR -> {
                    //   mainBinding.refreshing = false
                    Toast.makeText(
                        MyApplication.appContext,
                        arrayListResource.exception!!.getErrorModel().errorMessage,
                        Toast.LENGTH_SHORT
                    ).show()
                }
                Resource.Status.SUCCESS -> {
                    // mainBinding.refreshing = false
                    if (arrayListResource.data!!.status == 1
                    ) {
                        mainBinding.tvCoinsAvailable.text =
                            arrayListResource.data.result.usertotalbalance


                    } else {
                        Toast.makeText(
                            MyApplication.appContext,
                            arrayListResource.data.message,
                            Toast.LENGTH_SHORT
                        ).show()
                    }
                }
            }
        }
    }
}