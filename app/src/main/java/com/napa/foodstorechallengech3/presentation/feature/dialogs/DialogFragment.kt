package com.napa.foodstorechallengech3.presentation.feature.dialogs

import android.app.Dialog
import android.os.Bundle
import androidx.appcompat.app.AlertDialog
import androidx.fragment.app.DialogFragment

class SuccessDialogFragment : DialogFragment() {
    override fun onCreateDialog(savedInstanceState: Bundle?): Dialog {
        return AlertDialog.Builder(requireContext())
            .setTitle("Pesanan Berhasil")
            .setMessage("Terima kasih! Pesanan Anda Sedang Kami Proses.")
            .setPositiveButton("OK") { dialog, which ->
                dialog.dismiss()
            }
            .create()
    }
}
