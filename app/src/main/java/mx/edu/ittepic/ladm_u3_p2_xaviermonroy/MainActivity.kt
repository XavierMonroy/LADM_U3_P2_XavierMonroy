package mx.edu.ittepic.ladm_u3_p2_xaviermonroy

import android.content.Intent
import androidx.appcompat.app.AppCompatActivity
import android.os.Bundle
import android.widget.Toast
import com.google.firebase.firestore.FirebaseFirestore
import kotlinx.android.synthetic.main.activity_main.*

class MainActivity : AppCompatActivity() {
    var BD = FirebaseFirestore.getInstance()
    var dataLista = ArrayList<String>()
    var listaID = ArrayList<String>()

    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        setContentView(R.layout.activity_main)

        btnGuardar.setOnClickListener {
            buscarRegistro()
        }
        btnConsultar.setOnClickListener {
            var vConsulta = Intent(this,Main2Activity::class.java)
            startActivity(vConsulta)
        }
    }

    private fun buscarRegistro() {
        BD.collection("Restaurante")
            .whereEqualTo("celular", txtTelefono.text.toString())
            .addSnapshotListener { querySnapshot, firebaseFirestoreException ->
                if (firebaseFirestoreException != null) {
                    //si hay error
                    Toast.makeText(this, "Error no se puede acceder", Toast.LENGTH_LONG)
                        .show()
                    return@addSnapshotListener
                }
                dataLista.clear()
                listaID.clear()

                for (document in querySnapshot!!) {
                    var cadena = "Nombre: " + document.getString("nombre") + "\n" +
                            "Domicilio: " + document.getString("domicilio") + "\n" +
                            "Celular: " + document.getString("celular") + "\n" +
                            "Datos del pedido: \n" +
                            "Descripción: " + document.getString("pedido.descripcion") + "\n" +
                            "Cantidad: " + document.get("pedido.cantidad") + "\n" +
                            "Entregado: " + document.get("pedido.entregado") + "\n" +
                            "Precio: $" + document.get("pedido.precio") + "\n"
                    dataLista.add(cadena)
                    listaID.add(document.id)
                }
            }

        if (dataLista.size > 0) {
            Toast.makeText(this, "Ya hay un registro asociado a ese numero de telefono. Verifique!", Toast.LENGTH_LONG).show()
        }
        else {
            insertarPedido()
        }
    }

    private fun insertarPedido() {
        var data = hashMapOf(
            "nombre" to txtNombre.text.toString(),
            "domicilio" to txtDomicilio.text.toString(),
            "celular" to txtTelefono.text.toString(),
            "pedido" to hashMapOf(
                "descripcion" to txtDescripcion.text.toString(),
                "precio" to txtPrecio.text.toString().toDouble(),
                "cantidad" to txtCantidad.text.toString().toInt(),
                "entregado" to checkEntrega.isChecked
            )
        )

        BD.collection("Restaurante")
            .add(data as Any)
            .addOnSuccessListener {
                Toast.makeText(this, "Se insertó correctamente", Toast.LENGTH_LONG)
                    .show()
            }//exito
            .addOnFailureListener {
                Toast.makeText(this, "No se pudo guardar", Toast.LENGTH_LONG)
                    .show()
            }//error
        limpiarCampos()
    }

    private fun limpiarCampos(){
        txtNombre.setText("")
        txtDomicilio.setText("")
        txtTelefono.setText("")
        txtDescripcion.setText("")
        txtPrecio.setText("")
        txtCantidad.setText("")
        checkEntrega.isChecked = false
    }
}
