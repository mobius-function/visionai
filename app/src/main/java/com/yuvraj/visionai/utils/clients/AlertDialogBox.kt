package com.yuvraj.visionai.utils.clients

import android.content.Context
import androidx.appcompat.app.AlertDialog

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
    }
}