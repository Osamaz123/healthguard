package io.com.example.healthguard.ui.vitalsigns

import androidx.appcompat.app.AppCompatActivity
import android.os.Bundle
import android.widget.Toast
import com.github.mikephil.charting.charts.LineChart
import com.google.firebase.firestore.FirebaseFirestore
import io.com.example.healthguard.R
import io.com.example.healthguard.databinding.ActivityVitalSignsBinding
import android.graphics.Color

import com.github.mikephil.charting.components.XAxis
import com.github.mikephil.charting.components.YAxis
import com.github.mikephil.charting.data.Entry
import com.github.mikephil.charting.data.LineData
import com.github.mikephil.charting.data.LineDataSet
import com.google.firebase.database.DataSnapshot
import com.google.firebase.database.DatabaseError
import com.google.firebase.database.FirebaseDatabase
import com.google.firebase.database.ValueEventListener

class VitalSignsActivity : AppCompatActivity() {

    private val database = FirebaseDatabase.getInstance()
    private val ecgRef = database.getReference("ESP32_Device/ECG/int")

    // Initialize Firestore
    private val db = FirebaseFirestore.getInstance()
    // Declare View Binding
    private lateinit var binding: ActivityVitalSignsBinding
    private lateinit var lineChartECG: LineChart

    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        binding = ActivityVitalSignsBinding.inflate(layoutInflater)
        setContentView(binding.root)

        // Initialize lineChartECG
        lineChartECG = findViewById(R.id.lineChartECG)
        fetchDataFromFirebase()
        setupECGChart()

        fetchDataFromFirestore()
        listenForDocumentChanges()



    }

    private fun fetchDataFromFirestore() {
        // Reference to the document containing vital signs
        val docRef = db.collection("House").document("VITALS")

        // Fetch document
        docRef.get()
            .addOnSuccessListener { document ->
                if (document != null && document.exists()) {
                    // Parse data and set TextViews
                    val heartRate = document.getDouble("heartRate")?.toInt() ?: -1
                    val temperature = document.getDouble("temperature") ?: -1.0
                    val spo2 = document.getDouble("spo")?.toInt() ?: -1

                    // Update TextViews
                    binding.tvHeartRateValue.text = "$heartRate bpm"
                    binding.tvBodyTemperatureValue.text = "${String.format("%.2f", temperature)} °C"
                    binding.tvSPO2Value.text = "$spo2 SpO2"
                } else {
                    Toast.makeText(this, "No such document", Toast.LENGTH_SHORT).show()
                }
            }
            .addOnFailureListener { exception ->
                // Show error message in Toast
                Toast.makeText(this, "Failed to get data: ${exception.message}", Toast.LENGTH_SHORT).show()
            }
    }

    private fun listenForDocumentChanges() {
        // Reference to the document containing vital signs
        val docRef = db.collection("House").document("VITALS")

        // Listen for changes to the document
        docRef.addSnapshotListener { snapshot, exception ->
            if (exception != null) {
                // Show error message in Toast
                Toast.makeText(this, "Failed to listen for changes: ${exception.message}", Toast.LENGTH_SHORT).show()
                return@addSnapshotListener
            }

            if (snapshot != null && snapshot.exists()) {
                // Parse data and update TextViews
                val heartRate = snapshot.getDouble("heartRate")?.toInt() ?: -1
                val temperature = snapshot.getDouble("temperature") ?: -1.0

                // Update TextViews
                binding.tvHeartRateValue.text = "$heartRate bpm"
                binding.tvBodyTemperatureValue.text = "${String.format("%.2f", temperature)} °C"
            } else {
                Toast.makeText(this, "No such document", Toast.LENGTH_SHORT).show()
            }
        }
    }

    private fun fetchDataFromFirebase() {
        ecgRef.addValueEventListener(object : ValueEventListener {
            override fun onDataChange(snapshot: DataSnapshot) {
                val ecgDataList = ArrayList<Entry>()

                // Iterate through the dataSnapshot to fetch ECG data
                for (data in snapshot.children) {
                    val value = data.getValue(Long::class.java)
                    ecgDataList.add(Entry(data.key?.toFloat() ?: 0f, value?.toFloat() ?: 0f))
                }

                // Update ECG chart with fetched data
                updateECGChart(ecgDataList)
            }

            override fun onCancelled(error: DatabaseError) {
                // Handle database error
                Toast.makeText(this@VitalSignsActivity, "Failed to read ECG data: ${error.message}", Toast.LENGTH_SHORT).show()
            }
        })
    }

    private fun setupECGChart() {
        // Customize chart appearance
        lineChartECG.description.isEnabled = false
        lineChartECG.xAxis.position = XAxis.XAxisPosition.BOTTOM
        lineChartECG.xAxis.setDrawGridLines(false)
        lineChartECG.axisRight.isEnabled = false
        val leftAxis: YAxis = lineChartECG.axisLeft
        leftAxis.setDrawGridLines(false)
    }

    private fun updateECGChart(ecgDataList: List<Entry>) {
        // Create a dataset from the entries
        val dataSet = LineDataSet(ecgDataList, "ECG Data")
        dataSet.color = Color.BLUE
        dataSet.lineWidth = 2f
        dataSet.setDrawCircles(false)

        // Create a LineData object with the dataset
        val lineData = LineData(dataSet)
        lineChartECG.data = lineData

        // Refresh the chart
        lineChartECG.invalidate()
    }

}
