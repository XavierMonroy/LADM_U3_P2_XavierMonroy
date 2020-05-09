package mx.edu.ittepic.ladm_u3_p2_xaviermonroy

import android.content.Intent
import androidx.appcompat.app.AppCompatActivity
import android.os.Bundle
import android.text.Editable
import android.text.TextWatcher
import android.widget.ArrayAdapter
import android.widget.Toast
import androidx.appcompat.app.AlertDialog
import com.google.firebase.firestore.FirebaseFirestore
import kotlinx.android.synthetic.main.activity_main2.*

class Main2Activity : AppCompatActivity() {
    var dataLista = ArrayList<String>()
    var listaID = ArrayList<String>()
    var BD = FirebaseFirestore.getInstance()

    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        setContentView(R.layout.activity_main2)

        mostrarTodos()
        listaPedidos.setOnItemClickListener { parent, view, position, id ->
            if (listaID.size == 0) {
                return@setOnItemClickListener
            }
            alerta(position)
        }

        telefono.addTextChangedListener(object : TextWatcher {
            override fun beforeTextChanged(s: CharSequence, start: Int, count: Int, after: Int) {}

            override fun onTextChanged(s: CharSequence, start: Int, before: Int, count: Int) {
                if (s.toString() == "")
                    mostrarTodos()
                else buscarTelefono(s.toString())
            }

            override fun afterTextChanged(s: Editable) {}
        })
    }

    private fun alerta(position: Int) {
        AlertDialog.Builder(this).setTitle("PEDIDO")
            .setMessage("${dataLista[position]}")
            .setPositiveButton("Eliminar") { d, w ->
                eliminarPedido(listaID[position])
            }
            .setNegativeButton("Actualizar") { d, w ->
                actualizarPedido(listaID[position])
            }
            .setNeutralButton("Cancelar") { d, w -> }
            .show()
    }

    private fun eliminarPedido(idEliminar: String) {
        BD.collection("Restaurante").document(idEliminar).delete()
            .addOnSuccessListener {
                Toast.makeText(this, "Se eliminó con exito", Toast.LENGTH_LONG)
                    .show()
                mostrarTodos()
            }
            .addOnFailureListener {
                Toast.makeText(this, "No se pudo eliminar", Toast.LENGTH_LONG)
                    .show()
            }
    }

    private fun actualizarPedido(idActualizar: String) {
        BD.collection("Restaurante")
            .document(idActualizar)
            .get()
            .addOnSuccessListener {
                var v = Intent(this, Main3Activity::class.java)
                v.putExtra("id", idActualizar)
                v.putExtra("nombre", it.getString("nombre"))
                v.putExtra("domicilio", it.getString("domicilio"))
                v.putExtra("celular", it.getString("celular"))
                v.putExtra("descripcion", it.get("pedido.descripcion").toString())
                v.putExtra("precio", it.get("pedido.precio").toString())
                v.putExtra("cantidad", it.get("pedido.cantidad").toString())
                v.putExtra("entregado", it.get("pedido.entregado").toString())
                startActivity(v)
            }
            .addOnFailureListener {
                Toast.makeText(this, "No hay conexión", Toast.LENGTH_LONG).show()
            }
    }

    private fun mostrarTodos() {
        BD.collection("Restaurante")
            .addSnapshotListener { querySnapshot, firebaseFirestoreException ->
                if (firebaseFirestoreException != null) {
                    //si hay error
                    Toast.makeText(this, "No se puede acceder", Toast.LENGTH_LONG)
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
                            "   Descripción: " + document.getString("pedido.descripcion") + "\n" +
                            "   Cantidad: " + document.get("pedido.cantidad") + "\n" +
                            "   Entregado: " + document.get("pedido.entregado") + "\n" +
                            "   Precio: $" + document.get("pedido.precio") + "\n"

                    dataLista.add(cadena)
                    listaID.add(document.id)
                }
                if (dataLista.size == 0) {
                    dataLista.add("NO HAY PEDIDOS")
                }
                var adaptador = ArrayAdapter<String>(this, android.R.layout.simple_list_item_1, dataLista)
                listaPedidos.adapter = adaptador
            }
    }

    private fun buscarTelefono(s: String) {
        BD.collection("Restaurante").orderBy("celular").startAt(s).endAt(s+"\uf8ff")
            .addSnapshotListener { querySnapshot, firebaseFirestoreException ->
                if (firebaseFirestoreException != null) {
                    //si hay error
                    Toast.makeText(this, "No se puede acceder", Toast.LENGTH_LONG).show()
                    return@addSnapshotListener
                }

                dataLista.clear()
                listaID.clear()
                for (document in querySnapshot!!) {
                    var cadena = "Nombre: " + document.getString("nombre") + "\n" +
                            "Domicilio: " + document.getString("domicilio") + "\n" +
                            "Celular: " + document.getString("celular") + "\n" +
                            "Datos del pedido: \n" +
                            "   Descripcion: " + document.getString("pedido.descripcion") + "\n" +
                            "   Cantidad: " + document.get("pedido.cantidad") + "\n" +
                            "   Entregado: " + document.get("pedido.entregado") + "\n" +
                            "   Precio: $" + document.get("pedido.precio") + "\n"
                    dataLista.add(cadena)
                    listaID.add(document.id)
                }

                if (dataLista.size == 0) {
                    dataLista.add("NO HAY COINCIDENCIAS")
                }

                var adaptador = ArrayAdapter<String>(this, android.R.layout.simple_list_item_1, dataLista)
                listaPedidos.adapter = adaptador
            }
    }
}
