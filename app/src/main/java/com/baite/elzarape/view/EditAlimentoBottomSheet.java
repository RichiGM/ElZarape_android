package com.baite.elzarape.view;

import static com.baite.elzarape.commons.ZarapeCommons.SERVER_URL;

import android.content.Intent;
import android.content.SharedPreferences;
import android.graphics.Bitmap;
import android.net.Uri;
import android.os.Bundle;
import android.provider.MediaStore;
import android.util.Base64;
import android.util.Log;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.ArrayAdapter;
import android.widget.Button;
import android.widget.ImageButton;
import android.widget.ImageView;
import android.widget.TextView;
import android.widget.Toast;

import androidx.annotation.NonNull;
import androidx.annotation.Nullable;
import androidx.fragment.app.DialogFragment;

import com.android.volley.Request;
import com.android.volley.toolbox.JsonArrayRequest;
import com.android.volley.toolbox.JsonObjectRequest;
import com.android.volley.toolbox.Volley;
import com.baite.elzarape.R;
import com.baite.elzarape.commons.ZarapeCommons;
import com.google.android.material.textfield.MaterialAutoCompleteTextView;
import com.google.android.material.textfield.TextInputEditText;

import org.json.JSONException;
import org.json.JSONObject;
import org.utl.dsm.zarape.model.Alimento;
import org.utl.dsm.zarape.model.Categoria;

import java.io.ByteArrayOutputStream;
import java.io.IOException;
import java.util.ArrayList;
import java.util.HashMap;
import java.util.List;
import java.util.Map;

public class EditAlimentoBottomSheet extends DialogFragment {

    private TextView tvTitulo;
    private ImageView imvAlimento;
    private Button btnCargarImagen;
    private TextInputEditText etNombre, etDescripcion, etPrecio;
    private MaterialAutoCompleteTextView actvCategoria;
    private ImageButton btnAgregar, btnLimpiar;

    private Alimento alimento; // Alimento a editar
    private List<Categoria> categorias;
    private int categoriaSeleccionadaId;
    private String imagenBase64;
    private static final int PICK_IMAGE = 1;

    public EditAlimentoBottomSheet(Alimento alimento) {
        this.alimento = alimento; // Recibir el alimento a editar
    }

    @Nullable
    @Override
    public View onCreateView(@NonNull LayoutInflater inflater, @Nullable ViewGroup container, @Nullable Bundle savedInstanceState) {
        View view = inflater.inflate(R.layout.bottom_sheet_add_alimento, container, false);

        // Inicializar vistas
        tvTitulo = view.findViewById(R.id.tvTitulo);
        imvAlimento = view.findViewById(R.id.imvAlimento);
        btnCargarImagen = view.findViewById(R.id.btnCargarImagen);
        etNombre = view.findViewById(R.id.etNombre);
        etDescripcion = view.findViewById(R.id.etDescripcion);
        etPrecio = view.findViewById(R.id.etPrecio);
        actvCategoria = view.findViewById(R.id.actvCategoria);
        btnAgregar = view.findViewById(R.id.btnAgregar);
        btnLimpiar = view.findViewById(R.id.btnLimpiar);

        // Cambiar el título
        tvTitulo.setText("Modificar Alimento");

        // Precargar datos del alimento
        if (alimento != null) {
            etNombre.setText(alimento.getProducto().getNombre());
            etDescripcion.setText(alimento.getProducto().getDescripcion());
            etPrecio.setText(String.valueOf(alimento.getProducto().getPrecio()));
            categoriaSeleccionadaId = alimento.getProducto().getCategoria().getIdCategoria(); // Precargar ID de categoría
            actvCategoria.setText(alimento.getProducto().getCategoria().getDescripcion(), false); // Mostrar descripción
            // Cargar la imagen desde Base64
            if (alimento.getProducto().getFoto() != null && alimento.getProducto().getFoto().trim().length() > 64) {
                String b64 = alimento.getProducto().getFoto();
                b64 = b64.substring(b64.indexOf(",") + 1); // Quitar el prefijo "data:image/jpeg;base64,"
                imvAlimento.setImageBitmap(ZarapeCommons.procesarImagen(b64));
                imagenBase64 = b64; // Guardar la imagen actual en Base64
            } else {
                imvAlimento.setImageResource(R.drawable.logo_zarape); // Imagen por defecto
            }
        }

        // Configurar el botón para cargar imagen
        btnCargarImagen.setOnClickListener(v -> {
            Intent intent = new Intent(Intent.ACTION_PICK, MediaStore.Images.Media.EXTERNAL_CONTENT_URI);
            startActivityForResult(intent, PICK_IMAGE);
        });

        // Configurar el botón de agregar
        btnAgregar.setOnClickListener(v -> actualizarAlimento());

        // Configurar el botón de limpiar
        btnLimpiar.setOnClickListener(v -> limpiarFormulario());

        // Cargar las categorías
        cargarCategorias();

        return view;
    }

    private void actualizarAlimento() {
        String nombre = etNombre.getText().toString().trim();
        String descripcion = etDescripcion.getText().toString().trim();
        String precioStr = etPrecio.getText().toString().trim();

        // Validar campos
        if (nombre.isEmpty() || descripcion.isEmpty() || precioStr.isEmpty() || categoriaSeleccionadaId == 0) {
            showToast("Por favor, completa todos los campos");
            return;
        }

        double precio;
        try {
            precio = Double.parseDouble(precioStr);
        } catch (NumberFormatException e) {
            showToast("El precio debe ser un número válido");
            return;
        }

        // Construir el JSON para la API
        JSONObject jsonObject = new JSONObject();
        try {
            jsonObject.put("idProducto", alimento.getProducto().getIdProducto()); // Incluir el ID del producto
            jsonObject.put("nombre", nombre);
            jsonObject.put("descripcion", descripcion);
            jsonObject.put("precio", precio);
            jsonObject.put("foto", imagenBase64 != null ? "data:image/jpeg;base64," + imagenBase64 : alimento.getProducto().getFoto()); // Usar nueva imagen o la existente
            JSONObject categoria = new JSONObject();
            categoria.put("idCategoria", categoriaSeleccionadaId);
            jsonObject.put("categoria", categoria);
        } catch (JSONException e) {
            e.printStackTrace();
            showToast("Error al crear el JSON: " + e.getMessage());
            return;
        }

        String url = SERVER_URL + "api/alimento/update"; // Asegúrate de que esta sea la URL correcta
        SharedPreferences sharedPreferences = getActivity().getSharedPreferences("MyAppPrefs", getContext().MODE_PRIVATE);
        String token = sharedPreferences.getString("lastToken", null);

        Log.d("JSON Request", jsonObject.toString()); // Log del JSON enviado

        JsonObjectRequest jsonObjectRequest = new JsonObjectRequest(Request.Method.POST, url, jsonObject,
                response -> {
                    Log.d("API Response", response.toString());
                    showToast("Alimento actualizado correctamente");
                    dismiss(); // Cerrar el BottomSheet
                },
                error -> {
                    Log.e("API Error", error.toString());
                    if (error.networkResponse != null) {
                        Log.e("API Error", "Código: " + error.networkResponse.statusCode);
                        Log.e("API Error", "Respuesta: " + new String(error.networkResponse.data));
                    }
                    showToast("Error al actualizar el alimento: " + error.getMessage());
                }) {
            @Override
            public Map<String, String> getHeaders() {
                Map<String, String> headers = new HashMap<>();
                headers.put("Content-Type", "application/json");
                headers.put("Authorization", "Bearer " + token);
                return headers;
            }
        };

        Volley.newRequestQueue(getContext()).add(jsonObjectRequest);
    }

    private void limpiarFormulario() {
        etNombre.setText("");
        etDescripcion.setText("");
        etPrecio.setText("");
        actvCategoria.setText("");
        imvAlimento.setImageResource(R.drawable.ic_launcher_background);
        imagenBase64 = null;
    }

    private void cargarCategorias() {
        String url = SERVER_URL + "api/categoria/getall/alimentos";
        SharedPreferences sharedPreferences = getActivity().getSharedPreferences("MyAppPrefs", getContext().MODE_PRIVATE);
        String token = sharedPreferences.getString("lastToken", null);

        JsonArrayRequest jsonArrayRequest = new JsonArrayRequest(Request.Method.GET, url, null,
                response -> {
                    categorias = new ArrayList<>();
                    try {
                        for (int i = 0; i < response.length(); i++) {
                            JSONObject jsonObject = response.getJSONObject(i);
                            int id = jsonObject.getInt("idCategoria");
                            String descripcion = jsonObject.getString("descripcion");
                            String tipo = jsonObject.getString("tipo");
                            int activo = jsonObject.getInt("activo");
                            categorias.add(new Categoria(id, descripcion, tipo, activo));
                            Log.d("Categoria " + i, "ID: " + id + ", Descripción: " + descripcion);
                        }

                        if (categorias.isEmpty()) {
                            showToast("No se encontraron categorías");
                            return;
                        }

                        ArrayAdapter<Categoria> adapter = new ArrayAdapter<Categoria>(getContext(),
                                android.R.layout.simple_dropdown_item_1line, categorias) {
                            @NonNull
                            @Override
                            public View getView(int position, @Nullable View convertView, @NonNull ViewGroup parent) {
                                View view = super.getView(position, convertView, parent);
                                TextView textView = view.findViewById(android.R.id.text1);
                                textView.setText(getItem(position).getDescripcion());
                                return view;
                            }

                            @Override
                            public View getDropDownView(int position, @Nullable View convertView, @NonNull ViewGroup parent) {
                                View view = super.getDropDownView(position, convertView, parent);
                                TextView textView = view.findViewById(android.R.id.text1);
                                textView.setText(getItem(position).getDescripcion());
                                return view;
                            }
                        };

                        actvCategoria.setAdapter(adapter);
                        Log.d("Adaptador", "Adaptador configurado con " + categorias.size() + " categorías");

                        actvCategoria.setOnItemClickListener((parent, view, position, id) -> {
                            Categoria selectedCategoria = adapter.getItem(position);
                            if (selectedCategoria != null) {
                                categoriaSeleccionadaId = selectedCategoria.getIdCategoria();
                                actvCategoria.setText(selectedCategoria.getDescripcion(), false);
                            }
                        });

                    } catch (JSONException e) {
                        e.printStackTrace();
                        showToast("Error al parsear categorías: " + e.getMessage());
                    }
                },
                error -> {
                    Log.e("Volley Error", error.toString());
                    showToast("Error al cargar categorías: " + error.getMessage());
                }) {
            @Override
            public Map<String, String> getHeaders() {
                Map<String, String> headers = new HashMap<>();
                headers.put("Authorization", "Bearer " + token);
                return headers;
            }
        };

        Volley.newRequestQueue(getContext()).add(jsonArrayRequest);
    }

    @Override
    public void onActivityResult(int requestCode, int resultCode, @Nullable Intent data) {
        super.onActivityResult(requestCode, resultCode, data);
        if (requestCode == PICK_IMAGE && resultCode == getActivity().RESULT_OK && data != null) {
            Uri selectedImage = data.getData();
            imvAlimento.setImageURI(selectedImage); // Mostrar la imagen seleccionada
            imagenBase64 = convertirImagenABase64(selectedImage); // Convertir a Base64
        }
    }

    private String convertirImagenABase64(Uri uri) {
        try {
            Bitmap bitmap = MediaStore.Images.Media.getBitmap(getActivity().getContentResolver(), uri);
            ByteArrayOutputStream byteArrayOutputStream = new ByteArrayOutputStream();
            bitmap.compress(Bitmap.CompressFormat.JPEG, 100, byteArrayOutputStream);
            return Base64.encodeToString(byteArrayOutputStream.toByteArray(), Base64.DEFAULT);
        } catch (IOException e) {
            e.printStackTrace();
            showToast("Error al convertir la imagen: " + e.getMessage());
            return null;
        }
    }

    private void showToast(String message) {
        if (getContext() != null) {
            Toast.makeText(getContext(), message, Toast.LENGTH_SHORT).show();
        }
    }
}