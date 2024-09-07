package com.yuvraj.visionai.utils.clients

import android.app.Activity
import android.content.Context
import android.view.LayoutInflater
import android.view.View
import android.widget.TextView
import androidx.appcompat.app.AlertDialog
import com.google.android.material.button.MaterialButton
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
            val builder = AlertDialog.Builder(context).create()

            val viewInflated: View = LayoutInflater.from(context)
                .inflate(R.layout.instructions_alert_dialog_box_instructions, null, false)
            builder.setView(viewInflated)

            val tvTitle : TextView = viewInflated.findViewById(R.id.tvTitleBar)
            val tvMessage : TextView = viewInflated.findViewById(R.id.tvInstructions)
            val btnOk : MaterialButton = viewInflated.findViewById(R.id.btnOK)

            tvTitle.text = title
            tvMessage.text = message

            btnOk.setOnClickListener {
                builder.dismiss()
            }

            builder.show()
        }

        fun Activity.showInputBoxForFocalLength() {
            val builder = AlertDialog.Builder(this).create()

            // I'm using fragment here so I'm using getView() to provide ViewGroup
            // but you can provide here any other instance of ViewGroup from your Fragment / Activity
            val viewInflated: View = LayoutInflater.from(this)
                .inflate(R.layout.input_box_for_focal_length, null, false)

            // Set up the input
            // Specify the type of input expected; this, for example, sets the input as a password, and will mask the text
            builder.setView(viewInflated)

            // Set up the UI Components
            val etInput : TextInputEditText = viewInflated.findViewById(R.id.etInput)
            val tilInput : TextInputLayout = viewInflated.findViewById(R.id.tilInput)

            val btnRestoreDefault : TextView = viewInflated.findViewById(R.id.btnRestoreDefault)
            val btnGetStoredFocalLength : TextView = viewInflated.findViewById(R.id.btnGetStoredFocalLength)
            val btnConfirm : MaterialButton = viewInflated.findViewById(R.id.btnConfirm)
            val btnCancel : MaterialButton = viewInflated.findViewById(R.id.btnCancel)

            tilInput.hint = "Enter Testing Value of Focal Length"
            etInput.setText(getFocalLength().toString())

            btnRestoreDefault.setOnClickListener {
                etInput.setText(getDefaultFocalLength().toString())
            }

            btnGetStoredFocalLength.setOnClickListener {
                etInput.setText(getFrontCameraFocalLength().toString())
            }

            btnConfirm.setOnClickListener {
                setFocalLength(etInput.text.toString().toDouble())
                builder.dismiss()
            }

            btnCancel.setOnClickListener {
                builder.dismiss()
            }

            builder.show()
        }

    }
}