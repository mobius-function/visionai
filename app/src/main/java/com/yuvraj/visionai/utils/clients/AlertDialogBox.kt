package com.yuvraj.visionai.utils.clients

import android.app.Activity
import android.content.Context
import android.view.LayoutInflater
import android.view.View
import android.widget.TextView
import androidx.appcompat.app.AlertDialog
import com.google.android.material.textfield.TextInputEditText
import com.google.android.material.textfield.TextInputLayout
import com.yuvraj.visionai.R
import com.yuvraj.visionai.utils.PowerAlgorithm.Companion.getDefaultFocalLength
import com.yuvraj.visionai.utils.PowerAlgorithm.Companion.getFrontCameraFocalLength
import com.yuvraj.visionai.utils.helpers.SharedPreferencesHelper.getFocalLength
import com.yuvraj.visionai.utils.helpers.SharedPreferencesHelper.setFocalLength


class AlertDialogBox {
    companion object{
        fun showInstructionDialogBox(context: Context, title: String, message: String) {
            val builder = AlertDialog.Builder(context)
            builder.setTitle(title)
            builder.setMessage(message)

            builder.setPositiveButton("OK") { dialog, _ ->
                dialog.cancel()
            }

            val dialog: AlertDialog = builder.create()
            dialog.show()
        }

        fun Activity.showInputBoxForFocalLength() {
            val builder = AlertDialog.Builder(this)
            builder.setTitle("Update Focal Length | Testing Purpose")

            // I'm using fragment here so I'm using getView() to provide ViewGroup
            // but you can provide here any other instance of ViewGroup from your Fragment / Activity
            // I'm using fragment here so I'm using getView() to provide ViewGroup
            // but you can provide here any other instance of ViewGroup from your Fragment / Activity
//            val viewInflated: View = LayoutInflater.from(this)
//                .inflate(R.layout.alert_dialog, getView() as ViewGroup?, false)
            val viewInflated: View = LayoutInflater.from(this)
                .inflate(R.layout.alert_dialogue_box_with_input_field, null, false)
            // Set up the input
            // Set up the input
                        // Specify the type of input expected; this, for example, sets the input as a password, and will mask the text
            // Specify the type of input expected; this, for example, sets the input as a password, and will mask the text
            builder.setView(viewInflated)

            // Set up the buttons
            val etInput : TextInputEditText = viewInflated.findViewById(R.id.etInput)
            val tilInput : TextInputLayout = viewInflated.findViewById(R.id.tilInput)

            val btnRestoreDefault : TextView = viewInflated.findViewById(R.id.btnRestoreDefault)
            val btnGetStoredFocalLength : TextView = viewInflated.findViewById(R.id.btnGetStoredFocalLength)

            btnRestoreDefault.setOnClickListener {
                etInput.setText(getDefaultFocalLength().toString())
            }

            btnGetStoredFocalLength.setOnClickListener {
                etInput.setText(getFrontCameraFocalLength().toString())
            }

            tilInput.hint = "Enter Focal Length"

            etInput.setText(getFocalLength().toString())
            // Set up the buttons
            builder.setPositiveButton("Confirm") { dialog, _ ->
                setFocalLength(etInput.text.toString().toDouble())
                dialog.dismiss()
            }
            builder.setNegativeButton(
                "Cancel"
            ) { dialog, _ -> dialog.cancel() }

            builder.show()
        }

    }
}