package br.com.acessandocamera

import android.Manifest
import android.app.Activity
import android.content.Intent
import android.content.pm.PackageManager
import android.graphics.Bitmap
import android.net.Uri
import androidx.appcompat.app.AppCompatActivity
import android.os.Bundle
import android.provider.ContactsContract.Intents.Insert.ACTION
import android.provider.MediaStore
import android.widget.ImageView
import androidx.appcompat.app.AlertDialog
import androidx.core.app.ActivityCompat
import androidx.core.content.ContextCompat
import com.google.android.material.floatingactionbutton.FloatingActionButton

class MainActivity : AppCompatActivity() {

    lateinit var permissaoParaAcesso: PermissaoParaAcesso
    lateinit var sharedPreferencesHelper: SharedPreferencesHelper
    private val imagem by lazy { findViewById<ImageView>(R.id.imagem) }

    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        setContentView(R.layout.activity_main)

        initViews()

    }

    override fun onStart() {
        super.onStart()

        if(perdirPermissaoAoUsuario()) {
            AlertDialog.Builder(this)
                    .setTitle("Alerta de utilização do sistema")
                    .setMessage("Vá até as suas configurações para dar as permissões")
                    .setPositiveButton("Obrigado!"){dialog, _ -> dialog.dismiss() }.show()
        }
    }

    private fun perdirPermissaoAoUsuario(): Boolean = sharedPreferencesHelper.pegarAlertaAoIniciar()

    private fun initViews() {
        val botaoFoto = findViewById<FloatingActionButton>(R.id.botao_foto)
        val botaoGaleria = findViewById<FloatingActionButton>(R.id.botao_galeria)

        botaoFoto.setOnClickListener {
            val permissao = Manifest.permission.CAMERA
            requerimentoDeCamera(permissao)
            abrirGaleria()
        }

        botaoGaleria.setOnClickListener {
            requerimentoDeGaleria()
        }
    }

    private fun abrirGaleria(){
        val listaIntents = mutableListOf<Intent>()
        val tirarFotoDaIntent = Intent(MediaStore.ACTION_IMAGE_CAPTURE)

        val escolhaIntent = Intent()
        escolhaIntent.type = "image/*"
        escolhaIntent.action = Intent.ACTION_GET_CONTENT

        listaIntents.add(escolhaIntent)
        listaIntents.add(tirarFotoDaIntent)

        val galeriaIntent = Intent.createChooser(listaIntents[0], "Escolha como tirar a foto: ")
        galeriaIntent.putExtra(
                Intent.EXTRA_INITIAL_INTENTS,
                listaIntents.toTypedArray()
        )
        startActivityForResult(galeriaIntent, 200)
    }

    override fun onActivityResult(requestCode: Int, resultCode: Int, data: Intent?) {
        super.onActivityResult(requestCode, resultCode, data)
        if(requestCode == 200 && resultCode == Activity.RESULT_OK && intent?.data !=null){
            val foto = intent.data as Uri
            imagem.setImageURI(foto)
        } else if(intent?.extras != null) {
            val foto = intent.extras?.get("data") as Bitmap
            imagem.setImageBitmap(foto)
        }
    }

    private fun requerimentoDeCamera(permissao: String){
        permissaoParaAcesso.requerimentoDeCamera(permissao)
    }

    private fun requerimentoDeGaleria(){
        val permissoes =listOf(Manifest.permission.WRITE_EXTERNAL_STORAGE, Manifest.permission.CAMERA)
        permissaoParaAcesso.requerimentoDeGaleria(permissoes)
    }

    override fun onRequestPermissionsResult(
            requestCode: Int,
            permissions: Array<out String>,
            grantResults: IntArray) {
        super.onRequestPermissionsResult(requestCode, permissions, grantResults)
        permissaoParaAcesso.resultadoPedidoDePermissao(
                requestCode,
                permissions,
                grantResults
        )
    }
}
