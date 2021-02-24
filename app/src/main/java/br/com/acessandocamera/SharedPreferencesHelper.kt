package br.com.acessandocamera

import android.app.Activity
import android.content.Context
import androidx.core.content.edit

class SharedPreferencesHelper (context: Context) {

    companion object{
        const val permission_preferences = "PERMISSION_PREFS"
        const val show_permission_alert = "show_permission_alert"
    }
    private val preferences = context.getSharedPreferences(permission_preferences, Context.MODE_PRIVATE)

    fun salvarPermissao(permissao: String, garantido: Boolean){
        preferences.edit().putBoolean(permissao, garantido).apply()
    }

    fun obterEstadoDaPermissao(permissao: String) = preferences.getBoolean(permissao, false)

    fun inserirAlertaAoIniciar(show: Boolean){
        preferences.edit().putBoolean(show_permission_alert, show).apply()
    }

    fun pegarAlertaAoIniciar(): Boolean{
        return preferences.getBoolean(show_permission_alert, false)
    }

}
