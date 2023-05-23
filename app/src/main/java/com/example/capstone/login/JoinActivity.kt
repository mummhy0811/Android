package com.example.capstone.login

import android.content.Intent
import android.content.SharedPreferences
import android.os.Build
import android.os.Bundle
import android.os.PersistableBundle
import android.telephony.PhoneNumberFormattingTextWatcher
import android.text.Editable
import android.text.TextWatcher
import android.util.Log
import android.view.View
import android.widget.EditText
import android.widget.Toast
import androidx.annotation.RequiresApi
import androidx.appcompat.app.AppCompatActivity
import androidx.core.view.isVisible
import com.example.capstone.CheckedInfo
import com.example.capstone.R
import com.example.capstone.UserPhone
import com.example.capstone.databinding.ActivityJoinBinding
import com.example.capstone.retrofit.API
import com.example.capstone.retrofit.API.BASE_URL
import com.example.capstone.retrofit.IRetrofit
import com.example.capstone.retrofit.RetrofitClient
import okhttp3.MultipartBody
import okhttp3.OkHttp
import okhttp3.OkHttpClient
import okhttp3.Request
import okhttp3.RequestBody
import org.json.JSONException
import org.json.JSONObject
import retrofit2.Call
import retrofit2.Callback
import retrofit2.Response
import java.io.IOException
import java.sql.Array

class JoinActivity : AppCompatActivity(){
    private lateinit var binding : ActivityJoinBinding
    private var userKeyword : MutableList<String> = mutableListOf("", "", "")
    private var checked = false
    private var validchecked = true
    private var userPhone : String = ""
    private var userPW : String =  ""
    private var userPwCheck : String = ""
    private var userGender : String = "M"
    private var birthYear : String = ""
    private var birthMonth : String = ""
    private var birthDay : String = ""
    private var userBirth : String = ""
    private var userName : String = ""
    private lateinit var joiningInfo : SharedPreferences

    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        binding = ActivityJoinBinding.inflate(layoutInflater)
        setContentView(binding.root)

        // 회원가입 화면에서 backBtn을 누를 경우 login page로 돌아감
        binding.joinBackBtn.setOnClickListener {
            val intent = Intent(this, LoginActivity::class.java)
            startActivity(intent)
        }

        // 전화번호 입력시 자동 하이픈 추가
        binding.userPhNum.addTextChangedListener(PhoneNumberFormattingTextWatcher())

        binding.userPhNum.addTextChangedListener(object: TextWatcher{
            override fun afterTextChanged(p0: Editable?) {
                userPhone = p0.toString()
            }

            override fun beforeTextChanged(p0: CharSequence?, p1: Int, p2: Int, p3: Int) {

            }

            override fun onTextChanged(p0: CharSequence?, p1: Int, p2: Int, p3: Int) {
                binding.idEmptyTxt.visibility=View.GONE
            }
        })

        binding.userPw.addTextChangedListener(object: TextWatcher{
            override fun afterTextChanged(p0: Editable?) {
                userPW = p0.toString()
            }

            override fun beforeTextChanged(p0: CharSequence?, p1: Int, p2: Int, p3: Int) {

            }

            override fun onTextChanged(p0: CharSequence?, p1: Int, p2: Int, p3: Int) {
                binding.pwEmptyTxt.visibility=View.GONE
            }
        })

        binding.userPwCheck.addTextChangedListener(object: TextWatcher{
            override fun afterTextChanged(p0: Editable?) {
                userPwCheck = p0.toString()
                if(userPW.equals(userPwCheck)){
                    binding.pwNotMatchTxt.visibility = View.GONE
                }
            }

            override fun beforeTextChanged(p0: CharSequence?, p1: Int, p2: Int, p3: Int) {

            }

            override fun onTextChanged(p0: CharSequence?, p1: Int, p2: Int, p3: Int) {
                if(!userPW.equals(userPwCheck)){
                    binding.pwNotMatchTxt.visibility = View.VISIBLE
                }

            }
        })

        binding.birthYear.addTextChangedListener(object: TextWatcher{
            override fun afterTextChanged(p0: Editable?) {
                birthYear = p0.toString()
                userBirth = "${birthYear}-${birthMonth}-${birthDay}"
            }

            override fun beforeTextChanged(p0: CharSequence?, p1: Int, p2: Int, p3: Int) {

            }

            override fun onTextChanged(p0: CharSequence?, p1: Int, p2: Int, p3: Int) {
                binding.birthEmptyTxt.visibility=View.GONE
            }
        })

        binding.birthMonth.addTextChangedListener(object: TextWatcher{
            override fun afterTextChanged(p0: Editable?) {
                birthMonth = p0.toString()
                userBirth = "${birthYear}-${birthMonth}-${birthDay}"
            }

            override fun beforeTextChanged(p0: CharSequence?, p1: Int, p2: Int, p3: Int) {

            }

            override fun onTextChanged(p0: CharSequence?, p1: Int, p2: Int, p3: Int) {
                binding.birthEmptyTxt.visibility=View.GONE
            }
        })

        binding.birthDay.addTextChangedListener(object: TextWatcher{
            override fun afterTextChanged(p0: Editable?) {
                birthDay = p0.toString()
                userBirth = "${birthYear}-${birthMonth}-${birthDay}"
            }

            override fun beforeTextChanged(p0: CharSequence?, p1: Int, p2: Int, p3: Int) {

            }

            override fun onTextChanged(p0: CharSequence?, p1: Int, p2: Int, p3: Int) {
                binding.birthEmptyTxt.visibility=View.GONE
            }
        })

        binding.userName.addTextChangedListener(object: TextWatcher{
            override fun afterTextChanged(p0: Editable?) {
                userName = p0.toString()
            }

            override fun beforeTextChanged(p0: CharSequence?, p1: Int, p2: Int, p3: Int) {

            }

            override fun onTextChanged(p0: CharSequence?, p1: Int, p2: Int, p3: Int) {
                binding.nameEmptyTxt.visibility=View.GONE
            }
        })

        binding.genderCheck.setOnCheckedChangeListener { _, checkedId ->
            when (checkedId) {
                R.id.male -> {
                    userGender = "M"
                }

                R.id.female -> {
                    userGender = "F"
                }
            }
        }

        binding.userChipGroup1.setOnCheckedStateChangeListener { _, checkedIds ->
            if(checkedIds.size!=0){
                when(checkedIds[0]){
                    binding.userKeyword1.id -> userKeyword[0] = binding.userKeyword1.text.toString().replace("#","")
                    binding.userKeyword2.id -> userKeyword[0] = binding.userKeyword2.text.toString().replace("#","")
                    binding.userKeyword3.id -> userKeyword[0] = binding.userKeyword3.text.toString().replace("#","")
                    binding.userKeyword4.id -> userKeyword[0] = binding.userKeyword4.text.toString().replace("#","")
                    binding.userKeyword5.id -> userKeyword[0] = binding.userKeyword5.text.toString().replace("#","")
                    binding.userKeyword6.id -> userKeyword[0] = binding.userKeyword6.text.toString().replace("#","")
                    binding.userKeyword7.id -> userKeyword[0] = binding.userKeyword7.text.toString().replace("#","")
                }
            }
            else{
                userKeyword[0] = ""
            }
        }

        binding.userChipGroup2.setOnCheckedStateChangeListener { _, checkedIds ->
            if(checkedIds.size!=0){
                when(checkedIds[0]){
                    binding.userKeyword8.id -> userKeyword[1] = binding.userKeyword8.text.toString().replace("#","")
                    binding.userKeyword9.id -> userKeyword[1] = binding.userKeyword9.text.toString().replace("#","")
                    binding.userKeyword10.id -> userKeyword[1] = binding.userKeyword10.text.toString().replace("#","")
                    binding.userKeyword11.id -> userKeyword[1] = binding.userKeyword11.text.toString().replace("#","")
                    binding.userKeyword12.id -> userKeyword[1] = binding.userKeyword12.text.toString().replace("#","")
                    binding.userKeyword13.id -> userKeyword[1] = binding.userKeyword13.text.toString().replace("#","")
                    binding.userKeyword14.id -> userKeyword[1] = binding.userKeyword14.text.toString().replace("#","")
                    binding.userKeyword15.id -> userKeyword[1] = binding.userKeyword15.text.toString().replace("#","")
                    binding.userKeyword16.id -> userKeyword[1] = binding.userKeyword16.text.toString().replace("#","")
                }
            }
            else{
                userKeyword[1] = ""
            }
        }

        binding.userChipGroup3.setOnCheckedStateChangeListener { _, checkedIds ->
            if(checkedIds.size!=0){
                when(checkedIds[0]){
                    binding.userKeyword17.id -> userKeyword[2] = binding.userKeyword17.text.toString().replace("#","")
                    binding.userKeyword18.id -> userKeyword[2] = binding.userKeyword18.text.toString().replace("#","")
                    binding.userKeyword19.id -> userKeyword[2] = binding.userKeyword19.text.toString().replace("#","")
                    binding.userKeyword20.id -> userKeyword[2] = binding.userKeyword20.text.toString().replace("#","")
                    binding.userKeyword21.id -> userKeyword[2] = binding.userKeyword21.text.toString().replace("#","")
                    binding.userKeyword22.id -> userKeyword[2] = binding.userKeyword22.text.toString().replace("#","")
                }
            }else{
                userKeyword[2] = ""
            }
        }

        //id 중복확인 체크
        binding.doubleCheckBtn.setOnClickListener {
            if(!userPhone.isNullOrEmpty()){
                joiningInfo = this.getSharedPreferences("joiningInfo", MODE_PRIVATE)
                val userPhone = this.getSharedPreferences("joiningInfo", AppCompatActivity.MODE_PRIVATE).getString("userPhone", userPhone).toString()
                checkId(UserPhone(userPhone))
            }
        }


        // 완료 버튼을 누를 경우 회원가입 api에 정보 전송하도록
        // 유효성 검사
        binding.submitBtn.setOnClickListener {
            validchecked = true

            if(userPhone.isNullOrEmpty()){
                binding.idEmptyTxt.visibility=View.VISIBLE
                validchecked = false
            }

            if(userPW.isNullOrEmpty()){
                binding.pwEmptyTxt.visibility=View.VISIBLE
                validchecked = false
            }

            if(userPwCheck.isNullOrEmpty()){
                binding.pwNotMatchTxt.visibility=View.VISIBLE
                validchecked = false
            }

            if(birthYear.isNullOrEmpty()){
                binding.birthEmptyTxt.visibility=View.VISIBLE
                validchecked = false
            }

            if(birthMonth.isNullOrEmpty()){
                binding.birthEmptyTxt.visibility=View.VISIBLE
                validchecked = false
            }

            if(birthDay.isNullOrEmpty()){
                binding.birthEmptyTxt.visibility=View.VISIBLE
                validchecked = false
            }

            if(userName.isNullOrEmpty()){
                binding.nameEmptyTxt.visibility=View.VISIBLE
                validchecked = false
            }

            if(userKeyword[0] == ""){
                Toast.makeText(this, "음식 / 가격 키워드를 입력해주세요", Toast.LENGTH_SHORT).show()
                validchecked = false
            }

            if(userKeyword[1] == ""){
                Toast.makeText(this, "분위기 키워드를 입력해주세요", Toast.LENGTH_SHORT).show()
                validchecked = false
            }

            if(userKeyword[2] == ""){
                Toast.makeText(this, "편의 시설 키워드를 입력해주세요", Toast.LENGTH_SHORT).show()
                validchecked = false
            }

            if(validchecked){

                val requestBody : RequestBody = MultipartBody.Builder().setType(MultipartBody.FORM)
                    .addFormDataPart("userPhone", userPhone)
                    .addFormDataPart("userPW", userPW)
                    .addFormDataPart("userGender", userGender)
                    .addFormDataPart("userBirth", userBirth)
                    .addFormDataPart("userName", userName)
                    .addFormDataPart("keyword", userKeyword.toString())
                    .build()
                join(requestBody)

                Log.d("retrofit", "$requestBody")
            }
        }
    }

    private fun checkId(userPhone: UserPhone){
        val iRetrofit : IRetrofit? = RetrofitClient.getClient(API.BASE_URL)?.create(IRetrofit::class.java)
        val call = iRetrofit?.checkId(userPhone) ?:return

        call.enqueue(object : Callback<CheckedInfo> {
            override fun onResponse(call: Call<CheckedInfo>, response: Response<CheckedInfo>) {
                Log.d("retrofit", "아이디 중복 체크 - 성공 / t : ${response.raw()} ${response.body()}")
                if(response.body()!!.result){
                    Toast.makeText(this@JoinActivity, "중복된 아이디가 존재합니다", Toast.LENGTH_SHORT)
                    binding.userPhNum.text = null
                    checked = false
                }else{
                    Toast.makeText(this@JoinActivity, "사용 가능한 아이디입니다", Toast.LENGTH_SHORT)
                    checked = true
                }
            }

            override fun onFailure(call: Call<CheckedInfo>, t: Throwable) {
                Log.d("retrofit", "아이디 중복 체크 - 실패 / t: $t")
            }
        })
    }

    private fun join(requestBody: RequestBody){
        val client = OkHttpClient()

        val request = Request.Builder()
            .url(BASE_URL)
            .post(requestBody)
            .build()

        Log.d("retrofit", requestBody.javaClass.toString())

        client.newCall(request).enqueue(object: okhttp3.Callback{
            override fun onResponse(call: okhttp3.Call, response: okhttp3.Response) {
                val responseData = response.body?.string()

                runOnUiThread {
                    try {
                        //val jsonObject = JSONObject(responseData!!)
                        Log.d("retrofit", "")
                        /*val userPhone = jsonObject.getString("userPhone")
                        val userPW = jsonObject.getString("userPW")
                        val userGender = jsonObject.getString("userGender")
                        val userBirth = jsonObject.getString("userBirth")
                        val userName = jsonObject.getString("userName")
                        val keyword = jsonObject.getString("keyword")*/
                        //Log.d("retrofit", "userPhone: $userPhone\n userPW : $userPW\n userGender $userGender\n userBirth : $userBirth\n userName : $userName\n userKeyword : ${keyword.toString()}")

                    }catch (e : JSONException) {
                        e.printStackTrace()
                        Log.d("retrofit", "JSON 파싱 오류: ${e.message}")
                        println(e.message)
                    }
                }
            }

            override fun onFailure(call: okhttp3.Call, e: IOException) {
                runOnUiThread {
                    Log.d("retrofit fail","업로드 실패: ${e.message}")
                    Toast.makeText(this@JoinActivity, "[오류 발생]\n잠시 후 다시 시도해주세요.", Toast.LENGTH_LONG).show()
                }
            }
        })
    }

}