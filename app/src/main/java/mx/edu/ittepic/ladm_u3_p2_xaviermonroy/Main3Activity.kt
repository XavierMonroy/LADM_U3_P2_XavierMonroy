package mx.edu.ittepic.ladm_u3_p2_xaviermonroy

import androidx.appcompat.app.AppCompatActivity
import android.os.Bundle
import android.widget.Toast
import com.google.firebase.firestore.FirebaseFirestore
import kotlinx.android.synthetic.main.activity_main3.*

class Main3Activity : AppCompatActivity() {
    var id = ""
    var BD = FirebaseFirestore.getInstance()

    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        setContentView(R.layout.activity_main3)

        var extras = intent.extras
        id = extras!!.getString("id")!!
        txtNombre.setText(extras.getString("nombre"))
        txtDomicilio.setText(extras.getString("domicilio"))
        txtTelefono.setText(extras.getString("celular"))
        txtDescripcion.setText(extras.get("descripcion").toString())
        txtCantidad.setText(extras.get("cantidad").toString())
        txtPrecio.setText(extras.get("precio").toString())

        var estadoEntrega = extras.getString("entregado")!!.toBoolean()
        checkEntrega.isChecked= estadoEntrega

        btnActualizar.setOnClickListener {
            BD.collection("Restaurante").document(id)
                .update(
                    "nombre", txtNombre.text.toString(),
                    "domicilio", txtDomicilio.text.toString(),
                    "celular", txtTelefono.text.toString(),
                    "pedido.descripcion", txtDescripcion.text.toString(),
                    "pedido.cantidad", txtCantidad.text.toString().toInt(),
                    "pedido.precio", txtPrecio.text.toString().toDouble(),
                    "pedido.entregado", checkEntrega.isChecked
                )
                .addOnSuccessListener {
                    Toast.makeText(this, "Actualizado correctamente", Toast.LENGTH_LONG).show()
                    finish()
                }
                .addOnFailureListener {
                    Toast.makeText(this, "No se pudo actualizar", Toast.LENGTH_LONG).show()
                }
        }

        btnRegresar.setOnClickListener {
            finish()
        }
    }
}
