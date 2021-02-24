package br.com.acessandocamera

import android.app.Activity
import android.content.pm.PackageManager
import android.os.Build
import android.view.textclassifier.TextClassifierEvent
import android.widget.Toast
import androidx.appcompat.app.AlertDialog
import androidx.core.app.ActivityCompat
import androidx.core.content.ContextCompat

class PermissaoParaAcesso (private val activity: Activity,
                           private val sharedPreferencesHelper: SharedPreferencesHelper) {

    fun requerimentoDeCamera(permissao: String): Boolean{
        val garantido = ContextCompat.checkSelfPermission(activity, permissao)

        return if(garantido == PackageManager.PERMISSION_DENIED){
            ActivityCompat.requestPermissions(activity, arrayOf(permissao), 1)
            false
        }else{
            Toast.makeText(activity, "Permissão concedida: $permissao", Toast.LENGTH_LONG).show()
            true
        }
    }

    fun requerimentoDeGaleria(permissao: String){
        val permissoesNegadas = mutableListOf<String>()

        permissao.forEach {
            if(ContextCompat.checkSelfPermission(activity, it.toString()) == PackageManager.PERMISSION_DENIED
            ){
                permissoesNegadas.add(it.toString())
            }
        }

        if(permissao.isNotEmpty()){
            ActivityCompat.requestPermissions(
                    activity,
                    permissoesNegadas.toTypedArray(), 2
            )
        }else{
            Toast.makeText(activity, "Permissão para galeria concedida", Toast.LENGTH_LONG).show()
        }
    }

    fun resultadoPedidoDePermissao(
            requestCode: Int,
            permissions: Array<out String>,
            grantResults: IntArray){
        if(permissions.isEmpty()) return
        when(requestCode) {
            1 -> {
                if(perguntarMotivo(permissions[0])){
                    AlertDialog.Builder(activity)
                            .setTitle("Alerta de permissão importante")
                            .setMessage("Permita o acesso para uma melhor experiência no App")
                            .setPositiveButton("Ok"){dialog, _-> requerimentoDeCamera(permissions[0])
                            }.setNegativeButton("No"){dialog, _ -> dialog.dismiss() }.show()
                } else{
                    if(grantResults[0] == PackageManager.PERMISSION_DENIED){
                        sharedPreferencesHelper.inserirAlertaAoIniciar(true)
                    }
                }
            }
            else -> {

            }
        }
    }

    private fun perguntarMotivo(permissao: String) = Build.VERSION.SDK_INT >= Build.VERSION_CODES.M &&
            activity.shouldShowRequestPermissionRationale(permissao)

}