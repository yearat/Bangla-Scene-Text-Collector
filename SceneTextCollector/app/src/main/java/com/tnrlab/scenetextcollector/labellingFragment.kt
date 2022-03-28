package com.tnrlab.scenetextcollector

import android.os.Bundle
import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import android.widget.Button
import android.widget.EditText
import android.widget.TextView
import android.widget.Toast
import androidx.fragment.app.DialogFragment


/*
    class labellingFragment takes touchedObject as an input.
    var touchedObject is an drawn object which user tapped for labelling.
    drawnObject is a reference which is inside objectList[].
    touchedObject is sent from fun callLabellingFragment which received it
    from fun whereTouched.

    This Fragment contains a cancel button, submit button and edit text field.
    As user taps submit and the text field is not null, the text is assigned to
    touchedObject's label field. As touchedObject is a reference from the main objectList
    it assigns values to the main list without any issue.

 */

class labellingFragment(var touchedObject: DrawAndLabel.drawnObject) : DialogFragment() {


    override fun onCreateView(
        inflater: LayoutInflater,
        container: ViewGroup?,
        savedInstanceState: Bundle?
    ): View? {
        var rootView: View = inflater.inflate(R.layout.fragment_labelling, container, false)

        val labellingFragmentTv = rootView.findViewById<TextView>(R.id.labellingFragmentTv)
       // val cancelLabelBtn = rootView.findViewById<Button>(R.id.cancelLabelBtn)
        val submitLabelBtn = rootView.findViewById<Button>(R.id.submitLabelBtn)
        val labelFieldEt = rootView.findViewById<EditText>(R.id.labelFieldEt)

        labellingFragmentTv.text = "${touchedObject.label}"

       /* cancelLabelBtn.setOnClickListener() {
            dismiss()
        } */

        submitLabelBtn.setOnClickListener() {
            if(labelFieldEt.getText().toString().isEmpty()) {
                Toast.makeText(context, "Label can't be empty", Toast.LENGTH_SHORT).show()
            } else {
                Toast.makeText(context, "${labelFieldEt.getText().toString()}", Toast.LENGTH_SHORT).show()
                touchedObject.label = labelFieldEt.getText().toString()

                dismiss()
            }
        }

        return rootView
    }

}