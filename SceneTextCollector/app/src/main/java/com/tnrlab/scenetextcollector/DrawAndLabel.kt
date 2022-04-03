package com.tnrlab.scenetextcollector

import android.annotation.SuppressLint
import android.content.Intent
import android.graphics.*
import android.net.Uri
import androidx.appcompat.app.AppCompatActivity
import android.os.Bundle
import android.view.MotionEvent
import android.view.View
import android.widget.Button
import android.widget.ImageView
import android.widget.TextView
import android.widget.Toast
import java.io.FileNotFoundException
import java.io.FileWriter
import java.net.URI


/*
* This Activity Loads an Image from the device storage and let us draw boxes.
* There are two Image Views, for one we have Canvas Master and Bitmap Master.
* For another extra Image View we have Canvas Drawing Pane and Bitmap Drawing Pane.
* The primary drawing is done on the extra overlaid Image View and then drawn into
* the Canvas Master as the user finishes the drawing.
* */

class DrawAndLabel : AppCompatActivity() {

    private lateinit var bitmapMaster: Bitmap // Main bitmap
    private lateinit var canvasMaster: Canvas // Main Canvas
    private lateinit var bitmapDrawingPane: Bitmap // extra bitmap
    private lateinit var canvasDrawingPane: Canvas // extra canvas
    private lateinit var startPt: projectPt
    private var startX: Int = 0
    private var startY: Int = 0
    private var endX: Int = 0
    private var endY: Int = 0
    private var labeling: Boolean = false


    private lateinit var imageView: ImageView // Main ImageView where the picture is displayed
    private lateinit var loadImage: Button
    private lateinit var titleTv: TextView
    private lateinit var textSource: TextView
    private lateinit var imageDrawingPane: ImageView // Where initial drawing happens
    private lateinit var startPtTv: TextView // Shows starting points
    private lateinit var endPtTv: TextView // Shows ending points
    private lateinit var addLabel: Button
    private lateinit var printTv: TextView // For testing purposes
    private lateinit var resetBtn: Button
    private lateinit var testBtn: Button
    private lateinit var nextBtn: Button
    private lateinit var singleObject: drawnObject // Class for a drawn object
    private var objectList = mutableListOf<drawnObject>() // For keeping drawn objects
    private lateinit var objectCountTv: TextView // Displays total drawn objects
    private var objectCount: Int = 0
    private var labelCount: Int = 0




    @SuppressLint("ClickableViewAccessibility")
    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        setContentView(R.layout.activity_draw_and_label)

        val imageUri = intent.getParcelableExtra<Uri>("ImageUri")
        val textUri = intent.getParcelableExtra<Uri>("TextUri")

        imageView = findViewById<ImageView>(R.id.imageView)
        titleTv = findViewById<TextView>(R.id.titleTV)
        textSource = findViewById<TextView>(R.id.sourceUri)
        imageDrawingPane = findViewById<ImageView>(R.id.drawingPaneIv)
        addLabel = findViewById<Button>(R.id.addLabel)
        printTv = findViewById<TextView>(R.id.printTv)
        resetBtn = findViewById<Button>(R.id.resetBtn)
        testBtn = findViewById<Button>(R.id.testBtn)
        nextBtn= findViewById<Button>(R.id.nextBtn)
        objectCountTv = findViewById<TextView>(R.id.objectCountTv)
        printTv.text = "Status: Drawing"
        objectCountTv.text = "Total Objects: $objectCount"


        init()


        addLabel.setOnClickListener() {

            if(objectCount==0) {
                Toast.makeText(this, "No object is drawn!", Toast.LENGTH_SHORT).show()
            } else {

                if (addLabel.text == "Add Labels") {
                    addLabel.text = "Confirm Labels"
                    labeling = true
                    printTv.text = "Status: Labeling"
                    addLabel.setBackgroundColor(resources.getColor(R.color.GoogleLogin))
                    println("Labelling ${labeling}")
                    //touchBox()

                } else {
                    addLabel.text = "Add Labels"
                    labeling = false
                    printTv.text = "Status: Drawing"
                    addLabel.setBackgroundColor(resources.getColor(R.color.Button1))
                    println("Labelling ${labeling}")
                }
            }
        }

        resetBtn.setOnClickListener() {
            reset()
        }


        testBtn.setOnClickListener() {
            testAFunction()
        }


        nextBtn.setOnClickListener() {
//            if(labeling==false) {
//                Toast.makeText(
//                    this,
//                    "Please confirm labels first",
//                    Toast.LENGTH_SHORT
//                ).show()
//            }
            if ((textUri != null) && (objectCount != 0) && (labelCount == objectCount) && (labeling==false)) {
                writeToTextFile()
                var intent = Intent(this, ImageAnnotation::class.java)
                intent.putExtra("ImageUri", imageUri)
                intent.putExtra("TextUri", textUri)
                startActivity(intent)
                finish()
            } else {
                if(textUri == null) {
                    Toast.makeText(
                        this,
                        "Found textUri is null in DrawAndLabel",
                        Toast.LENGTH_SHORT
                    ).show()
                }
                if(objectCount == 0) {
                    Toast.makeText(
                        this,
                        "No object is annotated!",
                        Toast.LENGTH_SHORT
                    ).show()
                }
                if(labelCount != objectCount) {
                    Toast.makeText(
                        this,
                        "Must label all the drawn objects!",
                        Toast.LENGTH_SHORT
                    ).show()
                }
            }

        }





        imageView.setOnTouchListener(View.OnTouchListener { v, event ->
            val action = event.action
            val x = event.x.toInt()
            val y = event.y.toInt()
            if (!labeling) {

                when (action) {
                    MotionEvent.ACTION_DOWN -> {
                        textSource.text = "ACTION_DOWN- $x : $y"
                        startX = x
                        startY = y
                        startPt = projectXY(v as ImageView, bitmapMaster, x, y)!!
                    }
                    MotionEvent.ACTION_MOVE -> {
                        textSource.text = "ACTION_MOVE- $x : $y"
                        drawOnRectProjectedBitMap(v as ImageView, bitmapMaster, x, y)
                    }
                    MotionEvent.ACTION_UP -> {
                        textSource.text = "ACTION_UP- $x : $y"
                        drawOnRectProjectedBitMap(v as ImageView, bitmapMaster, x, y)
                        endX = x
                        endY = y
                        finalizeDrawing()
                    }
                }
            } else {

                when (action) {
                    MotionEvent.ACTION_DOWN -> {
                        whereTouched(x, y)
                    }

                }
            }
            /*
        * Return 'true' to indicate that the event have been consumed.
        * If auto-generated 'false', your code can detect ACTION_DOWN only,
        * cannot detect ACTION_MOVE and ACTION_UP.
        */true
        }) //

    } // onCreate

    class projectPt(var x: Int, var y: Int)

    class drawnObject(var startx: Int, var starty: Int, var endx: Int, var endy:Int, var label: String)

    private fun projectXY(iv: ImageView, bm: Bitmap, x: Int, y: Int): projectPt? {
        if (x < 0 || y < 0 || x > iv.width || y > iv.height) {
            //outside ImageView
            return null
        } else {
            val projectedX = (x.toDouble() * (bm.width.toDouble() / iv.width.toDouble())).toInt()
            val projectedY = (y.toDouble() * (bm.height.toDouble() / iv.height.toDouble())).toInt()
            return projectPt(projectedX, projectedY)
        }
    }

    private fun drawOnRectProjectedBitMap(iv: ImageView, bm: Bitmap, x: Int, y: Int) {
        if (x < 0 || y < 0 || x > iv.width || y > iv.height) {
            //outside ImageView
            return
        } else {
            val projectedX = (x.toDouble() * (bm.width.toDouble() / iv.width.toDouble())).toInt()
            val projectedY = (y.toDouble() * (bm.height.toDouble() / iv.height.toDouble())).toInt()

            //clear canvasDrawingPane
            canvasDrawingPane.drawColor(Color.TRANSPARENT, PorterDuff.Mode.CLEAR)
            val paint = Paint()
            paint.style = Paint.Style.FILL_AND_STROKE
            paint.color = Color.argb(80, 129, 178, 154)
            paint.strokeWidth = 10f
            canvasDrawingPane.drawRect(
                startPt.x.toFloat(),
                startPt.y.toFloat(),
                projectedX.toFloat(),
                projectedY.toFloat(),
                paint
            )

            imageDrawingPane.invalidate()
            textSource.text = x.toString() + ":" + y + "/" + iv.width + " : " + iv.height + "\n" +
                    projectedX + " : " + projectedY + "/" + bm.width + " : " + bm.height
        }
    }

    private fun finalizeDrawing() {
        canvasMaster.drawBitmap(bitmapDrawingPane, 0f, 0f, null)
        singleObject = drawnObject(startX, startY, endX, endY, "Null")
        objectList.add(singleObject)
        objectCount++
        objectCountTv.text = " Total objects: $objectCount"

    }

    // Resets the canvas and list
    private fun reset() {
        init()
        startX = 0 // Clearing last coordinates
        startY = 0
        endX = 0
        endY = 0
        objectCount = 0 // Clearing object count
        labelCount = 0 // Clearing label count
        objectList.clear() // Clearing objects list
        objectCountTv.text = "Total Objects: $objectCount"
        textSource.text = "Nothing"

        if (addLabel.text == "Confirm Labels") {
            addLabel.text = "Add Labels"
            labeling = false
            printTv.text = "Status: Drawing"
            addLabel.setBackgroundColor(resources.getColor(R.color.Button1))
            println(labeling)
            //touchBox()

        }
        // also need to clean the object coordinates
    }

    // Checks if the user tapped an object or not
    private fun whereTouched(X: Int, Y:Int) {

        // anObject is an object which iterates thtough the objectList
        for (anObject in objectList) {
            println("start: ${anObject.startx}, ${anObject.starty}")
            println("end: ${anObject.endx}, ${anObject.endy}")
            println("Touched: ${X}, ${Y} ")
            if(X >= anObject.startx && Y >= anObject.starty && X <= anObject.endx && Y <= anObject.endy) {
                textSource.text = " Touched an object!: ${anObject.label}"
                callLabellingFragment(anObject)
                if(textSource.text != "Null") {
                    labelCount++
                }
                break
            } else {
                textSource.text = " Object not found "
                continue
            }
        }
    }

    // Just to test things
    private fun testAFunction() {
        var listSize = objectList.size
        Toast.makeText(this, "list size: $listSize", Toast.LENGTH_SHORT).show()
        for(anObject in objectList) {
            println("start: ${anObject.startx}, ${anObject.starty}")
            println("end: ${anObject.endx}, ${anObject.endy}")
            println("label: ${anObject.label}")
        }
    }

    // This invokes the fragment as the user touches an drawn object
    // touchedObject is a single drawn object sent by whereTouched function
    private fun callLabellingFragment(touchedObject: drawnObject) {
        var dialog = labellingFragment(touchedObject)

        dialog.show(supportFragmentManager,"customDialog")
    }

    /*
    This function initializes the activity by loading the image,
    building canvases and bitmaps.
     */
    private fun init() {
        try {

            // We will now try loading from SD card
            lateinit var tempBitmap: Bitmap

            /*if(ContextCompat.checkSelfPermission(this, Manifest.permission.READ_EXTERNAL_STORAGE) == PackageManager.PERMISSION_GRANTED) {
               val imgFile = File("/mnt/sdcard/Download/catflower.jpg")
                println(imgFile.absolutePath)
                if (imgFile.exists()) {
                    tempBitmap = BitmapFactory.decodeFile(imgFile.absolutePath)

                }
            }
            else {
                // Request For Permission
                ActivityCompat.requestPermissions(this, arrayOf(Manifest.permission.READ_EXTERNAL_STORAGE), 121)
            } */

            tempBitmap = picture!!




            // THE CODES BELOW ARE FINE DON'T TOUCH
            var config: Bitmap.Config

            if(tempBitmap.config != null) {
                config = tempBitmap.config
            } else {
                config = Bitmap.Config.ARGB_8888
            }

            bitmapMaster = Bitmap.createBitmap(
                tempBitmap.width,
                tempBitmap.height,
                config
            )

            canvasMaster = Canvas(bitmapMaster)
            canvasMaster.drawBitmap(tempBitmap, 0f, 0f, null)

            // Accurate imageView.setImageBitmap(bitmapMaster)
            imageView.setImageBitmap(bitmapMaster)

            bitmapDrawingPane = Bitmap.createBitmap(
                tempBitmap.width,
                tempBitmap.height,
                Bitmap.Config.ARGB_8888
            )

            canvasDrawingPane = Canvas(bitmapDrawingPane)
            imageDrawingPane.setImageBitmap(bitmapDrawingPane)

        } catch (e: FileNotFoundException) {
            e.printStackTrace()
        }
    } // init()

    private fun writeToTextFile() {

            val fileWriter = FileWriter(textFile)
            for(anObject in objectList) {
                println("start: ${anObject.startx}, ${anObject.starty}")
                println("end: ${anObject.endx}, ${anObject.endy}")
                println("label: ${anObject.label}")

                fileWriter.write("label: ${anObject.label}, " +
                        "start: ${anObject.startx}, ${anObject.starty}, " +
                        "end: ${anObject.endx}, ${anObject.endy}" +
                        "\n")
            }

            fileWriter.flush()
    }


}