package io.github.skincanorg.skincan.ui.diseases

import android.content.res.AssetManager
import androidx.appcompat.app.AppCompatActivity
import android.os.Bundle
import android.util.Log
import android.view.View
import android.widget.AdapterView
import android.widget.ArrayAdapter
import android.widget.Button
import android.widget.Toast
import androidx.appcompat.widget.AppCompatEditText
import androidx.appcompat.widget.AppCompatSpinner
import by.kirich1409.viewbindingdelegate.CreateMethod
import by.kirich1409.viewbindingdelegate.viewBinding
import com.google.android.material.textfield.TextInputEditText
import io.github.skincanorg.skincan.R
import io.github.skincanorg.skincan.databinding.ActivityCvdDiseaseBinding
import io.github.skincanorg.skincan.ml.HeartDiseaseModel
import org.tensorflow.lite.DataType
import org.tensorflow.lite.Interpreter
import org.tensorflow.lite.support.tensorbuffer.TensorBuffer
import java.io.FileInputStream
import java.io.IOException
import java.nio.ByteBuffer
import java.nio.ByteOrder
import java.nio.MappedByteBuffer
import java.nio.channels.FileChannel

class CvdDiseaseActivity : AppCompatActivity() {
     private val binding: ActivityCvdDiseaseBinding by viewBinding(CreateMethod.INFLATE)
    private lateinit var sexSpinner: AppCompatSpinner
    private lateinit var chestPainSpinner: AppCompatSpinner
    private lateinit var fbsSpinner: AppCompatSpinner
    private lateinit var exangSpinner: AppCompatSpinner

    private lateinit var etAge: AppCompatEditText
    private lateinit var etTrestbps: AppCompatEditText
    private lateinit var etChol: AppCompatEditText
    private lateinit var etRestecg: AppCompatEditText
    private lateinit var etThalach: AppCompatEditText
    private lateinit var etOldpeak: AppCompatEditText
    private lateinit var etSlope: AppCompatEditText
    private lateinit var etCa: AppCompatEditText
    private lateinit var etThal: AppCompatEditText
    private lateinit var btnCheckup: Button

    private var valueAge: String = ""
    private var valueTrestbps: String = ""
    private var valueChol: String = ""
    private var valueRestecg: String = ""
    private var valueThalach: String = ""
    private var valueOldpeak: String = ""
    private var valueSlope: String = ""
    private var valueCa: String = ""
    private var valueThal: String = ""


    private var sexValue: Int = 0
    private var chestPainValue: Int = 0
    private var fbsValue: Int = 0
    private var exangValue: Int = 0
    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        setContentView(binding.root)
        setupCoreFunctions()
        initializeSpinners()
       initializeViews()

    }


    private fun setupCoreFunctions() {
        binding.apply {

            binding.apply {
                toolbar2.title = "Heart Disease"
                setSupportActionBar(toolbar2)
                supportActionBar?.apply {
                    setDisplayHomeAsUpEnabled(true)
                    setHomeButtonEnabled(true)
                }
                toolbar2.setNavigationOnClickListener { finish() }
            }

        }
    }

    private fun initializeSpinners(){
        sexSpinner = binding.haSexSpinner
        chestPainSpinner = binding.haChestpainSpinner
        fbsSpinner = binding.haFbsSpinner
        exangSpinner = binding.haExangSpinner

        val adapter = ArrayAdapter.createFromResource(this, R.array.sex_list_item, R.layout.spinner_item_list)
        adapter.setDropDownViewResource(android.R.layout.simple_spinner_dropdown_item)

        binding.haSexSpinner.adapter= adapter
        sexSpinner.onItemSelectedListener = object : AdapterView.OnItemSelectedListener {
            override fun onItemSelected(
                adapterView: AdapterView<*>?,
                view: View?,
                i: Int,
                l: Long
            ) {
                sexValue = i
            }

            override fun onNothingSelected(adapterView: AdapterView<*>?) {
                // Handle case where nothing is selected
            }
        }


        val adapter2 = ArrayAdapter.createFromResource(this, R.array.chestpain_list_item, R.layout.spinner_item_list)
        adapter2.setDropDownViewResource(android.R.layout.simple_spinner_dropdown_item)

        binding.haChestpainSpinner.adapter= adapter2
        chestPainSpinner.onItemSelectedListener = object : AdapterView.OnItemSelectedListener {
            override fun onItemSelected(
                adapterView: AdapterView<*>?,
                view: View?,
                i: Int,
                l: Long
            ) {
                chestPainValue = i
            }

            override fun onNothingSelected(adapterView: AdapterView<*>?) {
                // Handle case where nothing is selected
            }
        }

        val adapter3 = ArrayAdapter.createFromResource(this, R.array.fbs_list_item, R.layout.spinner_item_list)
       adapter3.setDropDownViewResource(android.R.layout.simple_spinner_dropdown_item)

        binding.haFbsSpinner.adapter= adapter3
        fbsSpinner.onItemSelectedListener = object :AdapterView.OnItemSelectedListener{
            override fun onItemSelected(
                parent: AdapterView<*>?,
                view: View?,
                i: Int,
                l: Long
            ) {
                fbsValue=i
            }

            override fun onNothingSelected(parent: AdapterView<*>?) {
                TODO("Not yet implemented")
            }
        }


        val adapter4 = ArrayAdapter.createFromResource(this, R.array.exang_list_item, R.layout.spinner_item_list)
        adapter4.setDropDownViewResource(android.R.layout.simple_spinner_dropdown_item)

        binding.haExangSpinner.adapter= adapter4
        exangSpinner.onItemSelectedListener = object :AdapterView.OnItemSelectedListener{
            override fun onItemSelected(
                parent: AdapterView<*>?,
                view: View?,
                i: Int,
                l: Long
            ) {
                exangValue=i
            }

            override fun onNothingSelected(parent: AdapterView<*>?) {
                TODO("Not yet implemented")
            }
        }


    }

    private fun initializeViews() {
        etAge = binding.etHaAge
        etTrestbps = binding.etHaTrestbps
        etChol = binding.etHaChol
        etRestecg = binding.etHaRestecg
        etThalach = binding.etHaThalach
        etOldpeak = binding.etHaOldpeak
        etSlope = binding.etHaSlope
        etCa = binding.etHaCa
        etThal = binding.etHaThal
        btnCheckup = binding.btnHaCheckup


        btnCheckup.setOnClickListener {
            valueAge = etAge.text.toString()
            valueTrestbps = etTrestbps.text.toString()
            valueChol = etChol.text.toString()
            valueRestecg = etRestecg.text.toString()
            valueThalach = etThalach.text.toString()
            valueOldpeak = etOldpeak.text.toString()
            valueSlope = etSlope.text.toString()
            valueCa = etCa.text.toString()
            valueThal = etThal.text.toString()

            // Perform validation and show error messages as needed
            if (valueAge.isEmpty()) {
                etAge.error = "Please enter the patient's age"
            }
            if (valueTrestbps.isEmpty()) {
                etTrestbps.error = "Please enter the patient's Trestbps"
            }
            if (valueChol.isEmpty()) {
                etChol.error = "Please enter the patient's Chol"
            }
            if (valueRestecg.isEmpty()) {
                etRestecg.error = "Please enter the patient's Restecg"
            }
            if (valueThalach.isEmpty()) {
                etThalach.error = "Please enter the patient's Thalach"
            }
            if (valueOldpeak.isEmpty()) {
                etOldpeak.error = "Please enter the patient's Old Peak"
            }
            if (valueSlope.isEmpty()) {
                etSlope.error = "Please enter the patient's Slope"
            }
            if (valueCa.isEmpty()) {
                etCa.error = "Please enter the patient's Ca"
            }
            if (valueThal.isEmpty()) {
                etThal.error = "Please enter the patient's Thal"
            }

           //  Check if any field is empty
            if (checkIsEmpty(valueAge) || checkIsEmpty(valueTrestbps) || checkIsEmpty(valueChol) || checkIsEmpty(valueRestecg) || checkIsEmpty(valueThalach) ||
                checkIsEmpty(valueOldpeak) || checkIsEmpty(valueSlope) || checkIsEmpty(valueCa) || checkIsEmpty(valueThal)) {
                Toast.makeText(this, "Please enter all the information!", Toast.LENGTH_SHORT).show()
            } else{
                // Perform inference with the TFLite model
                val result = performInference()

                // Display the result in cvd_result TextView
                binding.cvdResult.text = "$result"
            }



        }
    }

    private fun checkIsEmpty(value: String): Boolean {
        return value.isEmpty()
    }

    private fun performInference(): String {
        try {
            val tflite = HeartDiseaseModel.newInstance(this) // Replace "this" with your context

            // Prepare input data
            val inputBuffer = ByteBuffer.allocateDirect(4 * 13).order(ByteOrder.nativeOrder())

            // Assuming the model expects the data in the following order: int, int, int, int, int, int, int, int, int, float, int, int, int
            inputBuffer.putFloat(valueAge.toFloat())       // age
            inputBuffer.putInt(sexValue)               // sex
            inputBuffer.putInt(chestPainValue)         // cp
            inputBuffer.putFloat(valueTrestbps.toFloat())  // trestbps
            inputBuffer.putFloat(valueChol.toFloat())      // chol
            inputBuffer.putInt(fbsValue)              // fbs
            inputBuffer.putFloat(valueRestecg.toFloat())   // restecg
            inputBuffer.putFloat(valueThalach.toFloat())  // thalach
            inputBuffer.putInt(exangValue)            // exang
            inputBuffer.putFloat(valueOldpeak.toFloat()) // oldpeak
            inputBuffer.putFloat(valueSlope.toFloat())      // slope
            inputBuffer.putFloat(valueCa.toFloat())          // ca
            inputBuffer.putFloat(valueThal.toFloat())        // thal

            // Reset the buffer position for reading
           inputBuffer.rewind()

            // Create a TensorBuffer from the inputBuffer
            val inputFeature0 = TensorBuffer.createFixedSize(intArrayOf(1, 13), DataType.FLOAT32)
            inputFeature0.loadBuffer(inputBuffer)
            Log.d("InputValues", "Age: $valueAge, Sex: $sexValue, ChestPain: $chestPainValue, ...")

            // Runs model inference and gets result
            val outputs = tflite.process(inputFeature0)
            val outputFeature0 = outputs.outputFeature0AsTensorBuffer

            // Get the model's output
            val modelResult = outputFeature0.floatArray[0]*100 // Assuming it's a single float result

            // Close the TFLite interpreter
            tflite.close()

            val roundedResult = String.format("%.2f", modelResult)
            return "Predicted probability for heart disease $roundedResult %"
        } catch (ex: Exception) {
            ex.printStackTrace()
            return "Error: ${ex.message}"
        }
    }


}










