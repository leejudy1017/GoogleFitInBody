package com.inbody.googlefit.googlefitinbody

import android.Manifest
import android.content.Intent
import android.content.pm.PackageManager
import android.graphics.Color
import android.os.Bundle
import com.inbody.googlefit.common.logger.Log
import android.view.View
import android.widget.AdapterView
import android.widget.ArrayAdapter
import androidx.appcompat.app.AppCompatActivity
import androidx.core.app.ActivityCompat
import androidx.core.widget.TextViewCompat
import com.google.android.gms.auth.api.signin.GoogleSignIn
import com.google.android.gms.fitness.Fitness
import com.google.android.gms.fitness.FitnessOptions
import com.google.android.gms.fitness.data.*
import com.google.android.gms.fitness.request.DataReadRequest
import com.google.android.gms.fitness.request.SessionReadRequest
import com.google.android.gms.fitness.result.DataReadResponse
import com.google.android.gms.fitness.result.SessionReadResponse
import com.google.android.gms.tasks.Task
import com.google.android.material.snackbar.Snackbar
import com.inbody.googlefit.common.logger.LogView
import com.inbody.googlefit.common.logger.LogWrapper
import com.inbody.googlefit.common.logger.MessageOnlyLogFilter
import com.inbody.googlefit.test.R
import com.inbody.googlefit.test.databinding.ActivityMainBinding
import kotlinx.android.synthetic.main.activity_main.*
import java.text.DateFormat
import java.time.Instant
import java.time.LocalDate
import java.time.LocalDateTime
import java.time.ZoneId
import java.time.format.DateTimeFormatter
import java.util.*
import java.util.concurrent.TimeUnit
import kotlin.math.*


const val TAG = "GoogleFitInBody"

class MainActivity : AppCompatActivity() {

    private val dateFormat = DateFormat.getDateInstance()
    private var endTime : Long = 0
    private var startTime : Long = 0

    private lateinit var binding : ActivityMainBinding


    private val fitnessOptions = FitnessOptions.builder()
            // ACTIVITY DATA
        .addDataType(DataType.TYPE_CALORIES_EXPENDED)
        .addDataType(DataType.TYPE_WEIGHT)
        .addDataType(DataType.TYPE_POWER_SAMPLE)
        .addDataType(DataType.TYPE_NUTRITION)
        .addDataType(DataType.TYPE_HEIGHT)
        .addDataType(DataType.TYPE_BODY_FAT_PERCENTAGE)
        .addDataType(DataType.TYPE_HEART_RATE_BPM)
        .addDataType(DataType.TYPE_ACTIVITY_SEGMENT)
        .addDataType(DataType.TYPE_BASAL_METABOLIC_RATE)
        .addDataType(DataType.AGGREGATE_STEP_COUNT_DELTA)
        .addDataType(DataType.TYPE_HYDRATION)
        .addDataType(DataType.TYPE_LOCATION_SAMPLE)
        .addDataType(DataType.TYPE_HEART_POINTS)
        .addDataType(DataType.TYPE_SLEEP_SEGMENT)
        .addDataType(DataType.TYPE_STEP_COUNT_CUMULATIVE)
        .addDataType(DataType.TYPE_STEP_COUNT_DELTA)
        .addDataType(DataType.TYPE_CYCLING_PEDALING_CADENCE)
        .addDataType(DataType.TYPE_CYCLING_WHEEL_REVOLUTION)
        .addDataType(DataType.TYPE_CYCLING_WHEEL_RPM)
        .addDataType(DataType.TYPE_MOVE_MINUTES)
        .addDataType(DataType.TYPE_SPEED)
        .addDataType(DataType.TYPE_STEP_COUNT_CADENCE)
        .addDataType(DataType.TYPE_WORKOUT_EXERCISE)
            // HEALTH DATA
        .addDataType(HealthDataTypes.TYPE_BLOOD_GLUCOSE)
        .addDataType(HealthDataTypes.TYPE_BLOOD_PRESSURE)
        .addDataType(HealthDataTypes.TYPE_BODY_TEMPERATURE)
        .addDataType(HealthDataTypes.TYPE_CERVICAL_MUCUS)
        .addDataType(HealthDataTypes.TYPE_CERVICAL_POSITION)
        .addDataType(HealthDataTypes.TYPE_MENSTRUATION)
        .addDataType(HealthDataTypes.TYPE_OVULATION_TEST)
        .addDataType(HealthDataTypes.TYPE_OXYGEN_SATURATION)
        .addDataType(HealthDataTypes.TYPE_VAGINAL_SPOTTING)
        .build()

    enum class FitActionRequestCode {
        //Activity Data
        TYPE_CALORIES_EXPENDED, TYPE_WEIGHT, TYPE_POWER_SAMPLE,TYPE_NUTRITION,TYPE_HEIGHT,TYPE_BODY_FAT_PERCENTAGE
        ,TYPE_HEART_RATE_BPM,TYPE_ACTIVITY_SEGMENT,TYPE_BASAL_METABOLIC_RATE,TYPE_DISTANCE_DELTA,TYPE_HYDRATION,TYPE_LOCATION_SAMPLE
        ,TYPE_HEART_POINTS,TYPE_SLEEP_SEGMENT,TYPE_STEP_COUNT_CUMULATIVE,TYPE_STEP_COUNT_DELTA,TYPE_CYCLING_PEDALING_CADENCE
        ,TYPE_CYCLING_WHEEL_REVOLUTION,TYPE_CYCLING_WHEEL_RPM,TYPE_MOVE_MINUTES,TYPE_SPEED,TYPE_STEP_COUNT_CADENCE,TYPE_WORKOUT_EXERCISE

        //Health Data
        ,TYPE_BLOOD_GLUCOSE,TYPE_BLOOD_PRESSURE,TYPE_BODY_TEMPERATURE,TYPE_CERVICAL_MUCUS,TYPE_CERVICAL_POSITION,TYPE_MENSTRUATION
        ,TYPE_OVULATION_TEST,TYPE_OXYGEN_SATURATION,TYPE_VAGINAL_SPOTTING
    }

    private val requestCodeArray = arrayListOf(
        //Activity Data
        FitActionRequestCode.TYPE_CALORIES_EXPENDED,
        FitActionRequestCode.TYPE_WEIGHT,
        FitActionRequestCode.TYPE_POWER_SAMPLE,
        FitActionRequestCode.TYPE_NUTRITION,
        FitActionRequestCode.TYPE_HEIGHT,
        FitActionRequestCode.TYPE_BODY_FAT_PERCENTAGE
        ,
        FitActionRequestCode.TYPE_HEART_RATE_BPM,
        FitActionRequestCode.TYPE_ACTIVITY_SEGMENT,
        FitActionRequestCode.TYPE_BASAL_METABOLIC_RATE,
        FitActionRequestCode.TYPE_DISTANCE_DELTA,
        FitActionRequestCode.TYPE_HYDRATION,
        FitActionRequestCode.TYPE_LOCATION_SAMPLE
        ,
        FitActionRequestCode.TYPE_HEART_POINTS,
        FitActionRequestCode.TYPE_SLEEP_SEGMENT,
        FitActionRequestCode.TYPE_STEP_COUNT_CUMULATIVE,
        FitActionRequestCode.TYPE_STEP_COUNT_DELTA,
        FitActionRequestCode.TYPE_CYCLING_PEDALING_CADENCE
        ,
        FitActionRequestCode.TYPE_CYCLING_WHEEL_REVOLUTION,
        FitActionRequestCode.TYPE_CYCLING_WHEEL_RPM,
        FitActionRequestCode.TYPE_MOVE_MINUTES,
        FitActionRequestCode.TYPE_SPEED,
        FitActionRequestCode.TYPE_STEP_COUNT_CADENCE,
        FitActionRequestCode.TYPE_WORKOUT_EXERCISE

        //Health Data
        ,
        FitActionRequestCode.TYPE_BLOOD_GLUCOSE,
        FitActionRequestCode.TYPE_BLOOD_PRESSURE,
        FitActionRequestCode.TYPE_BODY_TEMPERATURE,
        FitActionRequestCode.TYPE_CERVICAL_MUCUS,
        FitActionRequestCode.TYPE_CERVICAL_POSITION,
        FitActionRequestCode.TYPE_MENSTRUATION
        ,
        FitActionRequestCode.TYPE_OVULATION_TEST,
        FitActionRequestCode.TYPE_OXYGEN_SATURATION,
        FitActionRequestCode.TYPE_VAGINAL_SPOTTING
    )

    var requestCode = requestCodeArray[0]

    private val runningQOrLater = android.os.Build.VERSION.SDK_INT >= android.os.Build.VERSION_CODES.Q

    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        binding = ActivityMainBinding.inflate(layoutInflater)
        val view = binding.root
        setContentView(view)


        //search Type (spinner)
        var adapter = ArrayAdapter(this,android.R.layout.simple_spinner_dropdown_item,requestCodeArray)
        binding.spinner.adapter = adapter

        binding.spinner.onItemSelectedListener = object : AdapterView.OnItemSelectedListener {
            override fun onItemSelected(parent: AdapterView<*>?, view: View?, position: Int, id: Long) {
                requestCode = requestCodeArray[position]
            }

            override fun onNothingSelected(p0: AdapterView<*>?) {
                requestCode = requestCodeArray[0]
            }
        }

        //range
        val calendar = Calendar.getInstance(TimeZone.getTimeZone("UTC"))
        val now = Date()
        calendar.time = now

        endTime = calendar.timeInMillis
        calendar.add(Calendar.WEEK_OF_YEAR, -1)
        startTime = calendar.timeInMillis


        binding.btn.setOnClickListener{
            Log.i(TAG, "Start Date: ${dateFormat.format(startTime)}")
            Log.i(TAG, "End Date: ${dateFormat.format(endTime)}")

            clearLogView()
            initializeLogging()
            checkPermissionsAndRun(fitActionRequestCode = requestCode)
        }

       /*

       val calendar = Calendar.getInstance(TimeZone.getTimeZone("UTC"))
       val now = Date()
       calendar.time = now


       binding.btn.setOnClickListener{

           //end 설정
           calendar.set(Calendar.DAY_OF_MONTH,binding.datePicker.dayOfMonth)
           calendar.set(Calendar.MONTH,binding.datePicker.month-1)
           calendar.set(Calendar.YEAR,binding.datePicker.year)
           calendar.set(Calendar.HOUR,binding.timePicker.currentHour)
           calendar.set(Calendar.MINUTE,binding.timePicker.currentMinute)

           endTime = binding.datePicker.year*31557600000 + binding.datePicker.monthbinding.timePicker.currentHour*3600000 + binding.timePicker.currentMinute*600000
           calendar.add(Calendar.WEEK_OF_YEAR, -1)
           startTime = calendar.timeInMillis

           Log.i(TAG, "Start Date: ${dateFormat.format(startTime)}")
           Log.i(TAG, "End Date: ${dateFormat.format(endTime)}")

           checkPermissionsAndRun(fitActionRequestCode = FitActionRequestCode.TYPE_CALORIES_EXPENDED)



       }

        */

    }


    /** CHECK PERMISSION */

    private fun checkPermissionsAndRun(fitActionRequestCode: FitActionRequestCode) {
        if (permissionApproved()) {
            fitSignIn(fitActionRequestCode)
        } else {
            requestRuntimePermissions(fitActionRequestCode)
        }
    }

    private fun permissionApproved(): Boolean {
        return if (runningQOrLater) {
            PackageManager.PERMISSION_GRANTED == ActivityCompat.checkSelfPermission(this, Manifest.permission.ACTIVITY_RECOGNITION)
            PackageManager.PERMISSION_GRANTED == ActivityCompat.checkSelfPermission(this, Manifest.permission.ACCESS_COARSE_LOCATION)
            PackageManager.PERMISSION_GRANTED == ActivityCompat.checkSelfPermission(this, Manifest.permission.ACCESS_FINE_LOCATION)
        } else {
            true
        }
    }

    private fun requestRuntimePermissions(requestCode: FitActionRequestCode) {
        val shouldProvideRationale =
            ActivityCompat.shouldShowRequestPermissionRationale(this, Manifest.permission.ACTIVITY_RECOGNITION)
            ActivityCompat.shouldShowRequestPermissionRationale(this, Manifest.permission.ACCESS_COARSE_LOCATION)
            ActivityCompat.shouldShowRequestPermissionRationale(this, Manifest.permission.ACCESS_FINE_LOCATION)

        requestCode.let {
            if (shouldProvideRationale) {
                Log.i(TAG, "Displaying permission rationale to provide additional context.")
                Snackbar.make(findViewById(R.id.main_activity_view),
                    R.string.permission_rationale, Snackbar.LENGTH_INDEFINITE)
                    .setAction(R.string.ok) {
                        ActivityCompat.requestPermissions(this, arrayOf(Manifest.permission.ACTIVITY_RECOGNITION,Manifest.permission.ACCESS_COARSE_LOCATION,Manifest.permission.ACCESS_FINE_LOCATION), requestCode.ordinal)
                    }
                    .show()
            } else {
                Log.i(TAG, "Requesting permission")
                ActivityCompat.requestPermissions(this, arrayOf(Manifest.permission.ACTIVITY_RECOGNITION,Manifest.permission.ACCESS_COARSE_LOCATION,Manifest.permission.ACCESS_FINE_LOCATION), requestCode.ordinal)
            }

            fitSignIn(requestCode)
        }

    }

    private fun fitSignIn(requestCode: FitActionRequestCode) {
        if (oAuthPermissionsApproved()) {
            performActionForRequestCode(requestCode)
        } else {
            requestCode.let {
                GoogleSignIn.requestPermissions(this, it.ordinal, getGoogleAccount(), fitnessOptions)
            }
        }
    }

    private fun oAuthPermissionsApproved() = GoogleSignIn.hasPermissions(getGoogleAccount(), fitnessOptions)
    private fun getGoogleAccount() = GoogleSignIn.getAccountForExtension(this, fitnessOptions)

    override fun onActivityResult(requestCode: Int, resultCode: Int, data: Intent?) {
        super.onActivityResult(requestCode, resultCode, data)

        when (resultCode) {
            RESULT_OK -> {
                val postSignInAction = FitActionRequestCode.values()[requestCode]
                performActionForRequestCode(postSignInAction)
            }
            else -> oAuthErrorMsg(requestCode, resultCode)
        }
    }

    private fun oAuthErrorMsg(requestCode: Int, resultCode: Int) {
        val message = """
            oAuth ERROR MESSAGE
            Request code was: $requestCode
            Result code was: $resultCode
        """.trimIndent()
        Log.e(TAG, message)
    }

    private fun performActionForRequestCode(requestCode: FitActionRequestCode) = when (requestCode) {

        FitActionRequestCode.TYPE_CALORIES_EXPENDED -> typeCaloriesExpended()
        FitActionRequestCode.TYPE_WEIGHT -> typeWeight()
        FitActionRequestCode.TYPE_POWER_SAMPLE -> typePowerSample()
        FitActionRequestCode.TYPE_NUTRITION -> typeNutrition()
        FitActionRequestCode.TYPE_HEIGHT -> typeHeight()
        FitActionRequestCode.TYPE_BODY_FAT_PERCENTAGE -> typeBodyFat()
        FitActionRequestCode.TYPE_HEART_RATE_BPM -> typeHeartRate()
        FitActionRequestCode.TYPE_ACTIVITY_SEGMENT -> typeActivity()
        FitActionRequestCode.TYPE_BASAL_METABOLIC_RATE -> typeBasalMetabolicRate()
        FitActionRequestCode.TYPE_DISTANCE_DELTA -> typeDistance()
        FitActionRequestCode.TYPE_HYDRATION -> typeHydration()
        FitActionRequestCode.TYPE_LOCATION_SAMPLE -> typeLocation()
        FitActionRequestCode.TYPE_HEART_POINTS -> typeHeartPoints()
        FitActionRequestCode.TYPE_SLEEP_SEGMENT -> typeSleep()
        FitActionRequestCode.TYPE_STEP_COUNT_CUMULATIVE -> typeStepCountCumulative()
        FitActionRequestCode.TYPE_STEP_COUNT_DELTA -> typeStepCount()
        FitActionRequestCode.TYPE_CYCLING_PEDALING_CADENCE -> typeCyclingPedalingCadence()
        FitActionRequestCode.TYPE_CYCLING_WHEEL_REVOLUTION -> typeCyclingWheelRevolution()
        FitActionRequestCode.TYPE_CYCLING_WHEEL_RPM -> typeCyclingWheelRPM()
        FitActionRequestCode.TYPE_MOVE_MINUTES -> typeMoveMinutes()
        FitActionRequestCode.TYPE_SPEED -> typeSpeed()
        FitActionRequestCode.TYPE_STEP_COUNT_CADENCE -> typeStepCountCadence()
        FitActionRequestCode.TYPE_WORKOUT_EXERCISE -> typeWorkoutExercise()

        FitActionRequestCode.TYPE_BLOOD_GLUCOSE -> typeBloodGlucose()
        FitActionRequestCode.TYPE_BLOOD_PRESSURE -> typeBloodPressure()
        FitActionRequestCode.TYPE_BODY_TEMPERATURE -> typeBodyTemperature()
        FitActionRequestCode.TYPE_CERVICAL_MUCUS -> typeCervicalMucus()
        FitActionRequestCode.TYPE_CERVICAL_POSITION -> typeCervicalPostion()
        FitActionRequestCode.TYPE_MENSTRUATION -> typeMenstration()
        FitActionRequestCode.TYPE_OVULATION_TEST -> typeOvulationTest()
        FitActionRequestCode.TYPE_OXYGEN_SATURATION -> typeOxygenSaturation()
        FitActionRequestCode.TYPE_VAGINAL_SPOTTING -> typeVaginalSpotting()

        else -> {}
    }

    /** DATA */

    fun readData( readRequest : DataReadRequest ) : Task<DataReadResponse>{
        return Fitness.getHistoryClient(this, GoogleSignIn.getAccountForExtension(this, fitnessOptions))
            .readData(readRequest)
            .addOnSuccessListener { response ->
                if (response.buckets.isNotEmpty()) {
                    Log.i(TAG, "Number of returned buckets of DataSets is: " + response.buckets.size)
                    for (bucket in response.buckets) {
                        bucket.dataSets.forEach { dumpDataSet(it) }
                    }
                } else if (response.dataSets.isNotEmpty()) {
                    Log.i(TAG, "Number of returned DataSets is: " + response.dataSets.size)
                    response.dataSets.forEach { dumpDataSet(it) }
                }
            }
            .addOnFailureListener { e ->
                Log.w(TAG,"There was an error reading data from Google Fit", e)
            }
    }

    fun dumpDataSet(dataSet: DataSet) {
        Log.i(TAG,"---------------------------------------------")
        Log.i(TAG, "Data returned for Data type: ${dataSet.dataType.name}")
        Log.i(TAG, "returned Data Points count: ${dataSet.dataPoints.size}")

        for (dp in dataSet.dataPoints) {
            Log.i(TAG, "Data point:")
            Log.i(TAG, "\tType: ${dp.dataType.name}")
            Log.i(TAG, "\tStart: ${dateFormat.format(dp.getStartTime(TimeUnit.MILLISECONDS))} | ${dp.getStartTimeString()}")
            Log.i(TAG, "\tEnd: ${dateFormat.format(dp.getEndTime(TimeUnit.MILLISECONDS))} | ${dp.getEndTimeString()}")
            for (field in dp.dataType.fields) {
                Log.i(TAG, "\tField: ${field.name} |  Value: ${dp.getValue(field)}")
            }
        }
    }


    fun DataPoint.getStartTimeString(): String = DateFormat.getTimeInstance().format(this.getStartTime(TimeUnit.MILLISECONDS))

    fun DataPoint.getEndTimeString(): String = DateFormat.getTimeInstance().format(this.getEndTime(TimeUnit.MILLISECONDS))


    /** Calories Expended */
    private fun typeCaloriesExpended(): Task<DataReadResponse>{

        val readRequest = DataReadRequest.Builder()
            .aggregate(DataType.TYPE_CALORIES_EXPENDED)
            .bucketByTime(1, TimeUnit.DAYS)
            .setTimeRange(startTime, endTime, TimeUnit.MILLISECONDS)
            .build()

        return readData(readRequest)
    }

    /** Weight */
    private fun typeWeight(): Task<DataReadResponse>{

        val readRequest = DataReadRequest.Builder()
            .aggregate(DataType.TYPE_WEIGHT)
            .bucketByTime(1, TimeUnit.DAYS)
            .setTimeRange(startTime, endTime, TimeUnit.MILLISECONDS)
            .build()

        return readData(readRequest)
    }

    /** Power */
    private fun typePowerSample(): Task<DataReadResponse>{

        val readRequest = DataReadRequest.Builder()
            .aggregate(DataType.TYPE_POWER_SAMPLE)
            .read(DataType.AGGREGATE_POWER_SUMMARY)
            .bucketByTime(1, TimeUnit.DAYS)
            .setTimeRange(startTime, endTime, TimeUnit.MILLISECONDS)
            .build()

        return readData(readRequest)
    }

    /** Nutrition */
    private fun typeNutrition(): Task<DataReadResponse>{

        val readRequest = DataReadRequest.Builder()
            .aggregate(DataType.TYPE_NUTRITION)
            .bucketByTime(1, TimeUnit.DAYS)
            .setTimeRange(startTime, endTime, TimeUnit.MILLISECONDS)
            .build()

        return readData(readRequest)
    }

    /** Height */
    private fun typeHeight(): Task<DataReadResponse>{

        val readRequest = DataReadRequest.Builder()
            .aggregate(DataType.TYPE_HEIGHT)
            .bucketByTime(1, TimeUnit.DAYS)
            .setTimeRange(startTime, endTime, TimeUnit.MILLISECONDS)
            .build()

        return readData(readRequest)
    }

    /** Body Fat Percentage */
    private fun typeBodyFat(): Task<DataReadResponse>{

        val readRequest = DataReadRequest.Builder()
            .aggregate(DataType.TYPE_BODY_FAT_PERCENTAGE)
            .bucketByTime(1, TimeUnit.DAYS)
            .setTimeRange(startTime, endTime, TimeUnit.MILLISECONDS)
            .build()

        return readData(readRequest)
    }

    /** Heart Rate */
    private fun typeHeartRate(): Task<DataReadResponse>{

        val readRequest = DataReadRequest.Builder()
            .aggregate(DataType.TYPE_HEART_RATE_BPM)
            .bucketByTime(1, TimeUnit.DAYS)
            .setTimeRange(startTime, endTime, TimeUnit.MILLISECONDS)
            .build()

        return readData(readRequest)
    }

    /** Activity Segment */
    private fun typeActivity(): Task<DataReadResponse>{

        val readRequest = DataReadRequest.Builder()
            .aggregate(DataType.TYPE_ACTIVITY_SEGMENT)
            .bucketByTime(1, TimeUnit.DAYS)
            .setTimeRange(startTime, endTime, TimeUnit.MILLISECONDS)
            .build()

        return readData(readRequest)
    }

    /** Basal Metabolic Rate */
    private fun typeBasalMetabolicRate(): Task<DataReadResponse>{

        val readRequest = DataReadRequest.Builder()
            .aggregate(DataType.TYPE_BASAL_METABOLIC_RATE)
            .bucketByTime(1, TimeUnit.DAYS)
            .setTimeRange(startTime, endTime, TimeUnit.MILLISECONDS)
            .build()

        return readData(readRequest)
    }

    /** Distance */
    private fun typeDistance(): Task<DataReadResponse>{

        val readRequest = DataReadRequest.Builder()
            .aggregate(DataType.TYPE_DISTANCE_DELTA)
            .bucketByTime(1, TimeUnit.DAYS)
            .setTimeRange(startTime, endTime, TimeUnit.MILLISECONDS)
            .build()

        return readData(readRequest)
    }

    /** Hydration */
    private fun typeHydration(): Task<DataReadResponse>{

        val readRequest = DataReadRequest.Builder()
            .aggregate(DataType.TYPE_HYDRATION)
            .bucketByTime(1, TimeUnit.DAYS)
            .setTimeRange(startTime, endTime, TimeUnit.MILLISECONDS)
            .build()

        return readData(readRequest)
    }

    /** Location */
    private fun typeLocation(): Task<DataReadResponse>{

        val readRequest = DataReadRequest.Builder()
            .aggregate(DataType.TYPE_LOCATION_SAMPLE)
            .bucketByTime(1, TimeUnit.DAYS)
            .setTimeRange(startTime, endTime, TimeUnit.MILLISECONDS)
            .build()

        return readData(readRequest)
    }

    /** Heart Points */
    private fun typeHeartPoints(): Task<DataReadResponse>{

        val readRequest = DataReadRequest.Builder()
            .aggregate(DataType.TYPE_HEART_POINTS)
            .bucketByTime(1, TimeUnit.DAYS)
            .setTimeRange(startTime, endTime, TimeUnit.MILLISECONDS)
            .build()

        return readData(readRequest)
    }

    /** Sleep Segment */
    private fun typeSleep(): Task<DataReadResponse>{

        val readRequest = DataReadRequest.Builder()
            .read(DataType.TYPE_SLEEP_SEGMENT)
            .bucketByTime(1, TimeUnit.DAYS)
            .setTimeRange(startTime, endTime, TimeUnit.MILLISECONDS)
            .build()

        return readData(readRequest)
    }

    /** Step Count Cumulative */
    private fun typeStepCountCumulative(): Task<DataReadResponse>{

        val readRequest = DataReadRequest.Builder()
            .read(DataType.TYPE_STEP_COUNT_CUMULATIVE)
            .bucketByTime(1, TimeUnit.DAYS)
            .setTimeRange(startTime, endTime, TimeUnit.MILLISECONDS)
            .build()

        return readData(readRequest)
    }

    /** Step Count */
    private fun typeStepCount(): Task<DataReadResponse>{

        val readRequest = DataReadRequest.Builder()
            .aggregate(DataType.TYPE_STEP_COUNT_DELTA)
            .bucketByTime(1, TimeUnit.DAYS)
            .setTimeRange(startTime, endTime, TimeUnit.MILLISECONDS)
            .build()

        return readData(readRequest)
    }

    /** Cycling Pedal Cadence */
    private fun typeCyclingPedalingCadence(): Task<DataReadResponse>{

        val readRequest = DataReadRequest.Builder()
            .read(DataType.TYPE_CYCLING_PEDALING_CADENCE)
            .bucketByTime(1, TimeUnit.DAYS)
            .setTimeRange(startTime, endTime, TimeUnit.MILLISECONDS)
            .build()

        return readData(readRequest)
    }

    /** Cycling Wheel Revolution */
    private fun typeCyclingWheelRevolution(): Task<DataReadResponse>{

        val readRequest = DataReadRequest.Builder()
            .read(DataType.TYPE_CYCLING_WHEEL_REVOLUTION)
            .bucketByTime(1, TimeUnit.DAYS)
            .setTimeRange(startTime, endTime, TimeUnit.MILLISECONDS)
            .build()

        return readData(readRequest)
    }

    /** Cycling Wheel RPM */
    private fun typeCyclingWheelRPM(): Task<DataReadResponse>{

        val readRequest = DataReadRequest.Builder()
            .read(DataType.TYPE_CYCLING_WHEEL_RPM)
            .bucketByTime(1, TimeUnit.DAYS)
            .setTimeRange(startTime, endTime, TimeUnit.MILLISECONDS)
            .build()

        return readData(readRequest)
    }

    /** Move Minutes */
    private fun typeMoveMinutes(): Task<DataReadResponse>{

        val readRequest = DataReadRequest.Builder()
            .aggregate(DataType.TYPE_MOVE_MINUTES)
            .bucketByTime(1, TimeUnit.DAYS)
            .setTimeRange(startTime, endTime, TimeUnit.MILLISECONDS)
            .build()

        return readData(readRequest)
    }

    /** Speed */
    private fun typeSpeed(): Task<DataReadResponse>{

        val readRequest = DataReadRequest.Builder()
            .aggregate(DataType.TYPE_SPEED)
            .bucketByTime(1, TimeUnit.DAYS)
            .setTimeRange(startTime, endTime, TimeUnit.MILLISECONDS)
            .build()

        return readData(readRequest)
    }

    /** Step Count Cadence */
    private fun typeStepCountCadence(): Task<DataReadResponse>{

        val readRequest = DataReadRequest.Builder()
            .read(DataType.TYPE_STEP_COUNT_CADENCE)
            .bucketByTime(1, TimeUnit.DAYS)
            .setTimeRange(startTime, endTime, TimeUnit.MILLISECONDS)
            .build()

        return readData(readRequest)
    }

    /** Workout Exercise */
    private fun typeWorkoutExercise(): Task<DataReadResponse>{

        val readRequest = DataReadRequest.Builder()
            .read(DataType.TYPE_WORKOUT_EXERCISE)
            .bucketByTime(1, TimeUnit.DAYS)
            .setTimeRange(startTime, endTime, TimeUnit.MILLISECONDS)
            .build()

        return readData(readRequest)
    }

    /** Blood Glucose */
    private fun typeBloodGlucose(): Task<DataReadResponse>{

        val readRequest = DataReadRequest.Builder()
            .aggregate(HealthDataTypes.TYPE_BLOOD_GLUCOSE)
            .bucketByTime(1, TimeUnit.DAYS)
            .setTimeRange(startTime, endTime, TimeUnit.MILLISECONDS)
            .build()

        return readData(readRequest)
    }

    /** Blood Pressure */
    private fun typeBloodPressure(): Task<DataReadResponse>{

        val readRequest = DataReadRequest.Builder()
            .aggregate(HealthDataTypes.TYPE_BLOOD_PRESSURE)
            .bucketByTime(1, TimeUnit.DAYS)
            .setTimeRange(startTime, endTime, TimeUnit.MILLISECONDS)
            .build()

        return readData(readRequest)
    }

    /** Body Temperature */
    private fun typeBodyTemperature(): Task<DataReadResponse>{

        val readRequest = DataReadRequest.Builder()
            .aggregate(HealthDataTypes.TYPE_BODY_TEMPERATURE)
            .bucketByTime(1, TimeUnit.DAYS)
            .setTimeRange(startTime, endTime, TimeUnit.MILLISECONDS)
            .build()

        return readData(readRequest)
    }

    /** Cervical Mucus */
    private fun typeCervicalMucus(): Task<DataReadResponse>{

        val readRequest = DataReadRequest.Builder()
            .read(HealthDataTypes.TYPE_CERVICAL_MUCUS)
            .bucketByTime(1, TimeUnit.DAYS)
            .setTimeRange(startTime, endTime, TimeUnit.MILLISECONDS)
            .build()

        return readData(readRequest)
    }

    /** Cervical Position */
    private fun typeCervicalPostion(): Task<DataReadResponse>{

        val readRequest = DataReadRequest.Builder()
            .read(HealthDataTypes.TYPE_CERVICAL_POSITION)
            .bucketByTime(1, TimeUnit.DAYS)
            .setTimeRange(startTime, endTime, TimeUnit.MILLISECONDS)
            .build()

        return readData(readRequest)
    }

    /** Menstration */
    private fun typeMenstration(): Task<DataReadResponse>{

        val readRequest = DataReadRequest.Builder()
            .read(HealthDataTypes.TYPE_MENSTRUATION)
            .bucketByTime(1, TimeUnit.DAYS)
            .setTimeRange(startTime, endTime, TimeUnit.MILLISECONDS)
            .build()

        return readData(readRequest)
    }

    /** Ovulation Test */
    private fun typeOvulationTest(): Task<DataReadResponse>{

        val readRequest = DataReadRequest.Builder()
            .read(HealthDataTypes.TYPE_OVULATION_TEST)
            .bucketByTime(1, TimeUnit.DAYS)
            .setTimeRange(startTime, endTime, TimeUnit.MILLISECONDS)
            .build()

        return readData(readRequest)
    }

    /** Oxygen Saturation */
    private fun typeOxygenSaturation(): Task<DataReadResponse>{

        val readRequest = DataReadRequest.Builder()
            .aggregate(HealthDataTypes.TYPE_OXYGEN_SATURATION)
            .bucketByTime(1, TimeUnit.DAYS)
            .setTimeRange(startTime, endTime, TimeUnit.MILLISECONDS)
            .build()

        return readData(readRequest)
    }

    /** Vaginal Spotting */
    private fun typeVaginalSpotting(): Task<DataReadResponse>{

        val readRequest = DataReadRequest.Builder()
            .read(HealthDataTypes.TYPE_VAGINAL_SPOTTING)
            .bucketByTime(1, TimeUnit.DAYS)
            .setTimeRange(startTime, endTime, TimeUnit.MILLISECONDS)
            .build()

        return readData(readRequest)
    }

    /** Clears all the logging message in the LogView.  */
    private fun clearLogView() {
        binding.logView.text = ""
    }

    /** Log View */
    private fun initializeLogging(){

        val logWrapper = LogWrapper()
        Log.setLogNode(logWrapper)

        val msgFilter = MessageOnlyLogFilter()
        logWrapper.next = msgFilter

        TextViewCompat.setTextAppearance(binding.logView, R.style.Log)
        binding.logView.setBackgroundColor(Color.WHITE)
        msgFilter.next = logView
        Log.i(TAG, "Ready.")

    }

}