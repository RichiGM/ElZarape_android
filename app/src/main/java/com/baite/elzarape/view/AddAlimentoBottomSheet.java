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
import android.widget.EditText;
import android.widget.ImageButton;
import android.widget.ImageView;
import android.widget.Button;
import android.widget.TextView;
import android.widget.Toast;
import androidx.annotation.NonNull;
import androidx.annotation.Nullable;

import com.android.volley.toolbox.JsonArrayRequest;
import com.baite.elzarape.R;
import org.json.JSONException;
import org.json.JSONObject;
import org.utl.dsm.zarape.model.Categoria;
import com.google.android.material.bottomsheet.BottomSheetDialogFragment;
import com.google.android.material.textfield.MaterialAutoCompleteTextView;
import com.android.volley.Request;
import com.android.volley.Response;
import com.android.volley.VolleyError;
import com.android.volley.toolbox.JsonObjectRequest;
import com.android.volley.toolbox.Volley;

import java.io.ByteArrayOutputStream;
import java.io.IOException;
import java.util.ArrayList;
import java.util.HashMap;
import java.util.List;
import java.util.Map;

public class AddAlimentoBottomSheet extends BottomSheetDialogFragment {

    private EditText etNombre, etDescripcion, etPrecio;
    private MaterialAutoCompleteTextView actvCategoria;
    private ImageView imvAlimento;
    private Button btnCargarImagen;
    private ImageButton btnAgregar, btnLimpiar;

    private static final int PICK_IMAGE = 1;
    private String imagenBase64;
    private List<Categoria> categorias;
    private int categoriaSeleccionadaId;

    @Nullable
    @Override
    public View onCreateView(@NonNull LayoutInflater inflater, @Nullable ViewGroup container, @Nullable Bundle savedInstanceState) {
        View view = inflater.inflate(R.layout.bottom_sheet_add_alimento, container, false);

        etNombre = view.findViewById(R.id.etNombre);
        etDescripcion = view.findViewById(R.id.etDescripcion);
        etPrecio = view.findViewById(R.id.etPrecio);
        actvCategoria = view.findViewById(R.id.actvCategoria);
        imvAlimento = view.findViewById(R.id.imvAlimento);
        btnCargarImagen = view.findViewById(R.id.btnCargarImagen);
        btnAgregar = view.findViewById(R.id.btnAgregar);
        btnLimpiar = view.findViewById(R.id.btnLimpiar);

        // Cargar categorías
        cargarCategorias();

        btnCargarImagen.setOnClickListener(v -> {
            Intent intent = new Intent(Intent.ACTION_PICK, android.provider.MediaStore.Images.Media.EXTERNAL_CONTENT_URI);
            startActivityForResult(intent, PICK_IMAGE);
        });

        btnAgregar.setOnClickListener(v -> {
            // Lógica para agregar el alimento
            agregarAlimento();
            dismiss(); // Cerrar el BottomSheet
        });

        btnLimpiar.setOnClickListener(v -> {
            // Lógica para limpiar el formulario
            limpiarFormulario();
        });

        return view;
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
                                textView.setText(getItem(position).getDescripcion()); // Forzar descripción en dropdown
                                return view;
                            }
                        };

                        actvCategoria.setAdapter(adapter);
                        Log.d("Adaptador", "Adaptador personalizado configurado con " + categorias.size() + " categorías");

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

    private void agregarAlimento() {
        String nombre = etNombre.getText().toString();
        String descripcion = etDescripcion.getText().toString();
        String precio = etPrecio.getText().toString();

        String categoriaSeleccionadaTexto = actvCategoria.getText().toString();
        Categoria categoriaSeleccionada = null;

        JSONObject jsonObject = new JSONObject();
        try {
            JSONObject producto = new JSONObject();
            producto.put("nombre", nombre);
            producto.put("descripcion", descripcion);
            producto.put("precio", Double.parseDouble(precio));
            producto.put("foto", imagenBase64);
            JSONObject categoria = new JSONObject();
            categoria.put("idCategoria", categoriaSeleccionadaId);
            producto.put("categoria", categoria);
            jsonObject.put("producto", producto);
        } catch (JSONException e) {
            e.printStackTrace();
        }

        String url = SERVER_URL + "api/alimento/insert"; // URL de la API para insertar alimento
        SharedPreferences sharedPreferences = getActivity().getSharedPreferences("MyAppPrefs", getContext().MODE_PRIVATE);
        String token = sharedPreferences.getString("lastToken", null);

        Log.d("JSON Request", jsonObject.toString()); // Log del JSON

        JsonObjectRequest jsonObjectRequest = new JsonObjectRequest(Request.Method.POST, url, jsonObject,
                response -> {
                    Log.d("API Response", response.toString()); // Log de la respuesta
                    showToast("Alimento agregado correctamente");
                    // Aquí puedes llamar a cargarAlimentos() y limpiar() si es necesario
                },
                error -> {
                    Log.e("API Error", error.toString()); // Log del error
                    showToast("Error al agregar alimento: " + error.getMessage());
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
        actvCategoria.setText(""); // Limpiar el AutoCompleteTextView
        imvAlimento.setImageResource(R.drawable.ic_launcher_background); // Restablecer la imagen
    }

    @Override
    public void onActivityResult(int requestCode, int resultCode, @Nullable Intent data) {
        super.onActivityResult(requestCode, resultCode, data);
        if (requestCode == PICK_IMAGE && resultCode == getActivity().RESULT_OK && data != null) {
            Uri selectedImage = data.getData();
            imvAlimento.setImageURI(selectedImage);
            imagenBase64 = convertirImagenABase64(selectedImage);
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
            return null;
        }
    }

    private void showToast(String message) {
        if (getContext() != null) {
            Toast.makeText(getContext(), message, Toast.LENGTH_SHORT).show();
        }
    }
}