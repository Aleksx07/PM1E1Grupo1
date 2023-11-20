package com.example.PM1E1Grupo1;
import android.content.Context;
import android.content.Intent;
import android.graphics.Bitmap;
import android.graphics.BitmapFactory;
import android.util.Log;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.BaseAdapter;
import android.widget.Filter;
import android.widget.Filterable;
import android.widget.ImageView;
import android.widget.TextView;
import com.example.PM1E1Grupo1.transacciones.Contactos;
import java.util.ArrayList;
import java.util.List;
import java.util.Locale;

public class CustomBaseAdapter extends BaseAdapter{
    Context context;
    LayoutInflater inflater;
    private List<Contactos> contactos; // Lista para elementos filtrados

    // Constructor del adaptador personalizado
    public CustomBaseAdapter(Context context, ArrayList<Contactos> contactos) {
        this.context = context;
        this.contactos = new ArrayList<>(contactos); // Crear una nueva lista basada en contactos
        inflater = LayoutInflater.from(context);
    }

    @Override
    public int getCount() {
        return contactos.size();
    }

    @Override
    public Object getItem(int position) {
        return contactos.get(position);
    }

    @Override
    public long getItemId(int position) {
        return position;
    }

    @Override
    public View getView(int position, View convertView, ViewGroup parent) {
        if (convertView == null) {
            convertView = inflater.inflate(R.layout.activity_custom_list_view, parent, false);
        }

        // Inicialización de los componentes de la vista
        ImageView imagenImageView = convertView.findViewById(R.id.imageIcom);
        TextView nombreTextView = convertView.findViewById(R.id.txtnombre);

        Contactos contacto = contactos.get(position);
        Bitmap b = BitmapFactory.decodeFile(contacto.getImagen());
        imagenImageView.setImageBitmap(b);
        nombreTextView.setText(contacto.getNombre());

        // Evento onClick
        convertView.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {

            }
        });

        return convertView;
    }
    // Método para actualizar el conjunto de datos
    public void updateDataSet(List<Contactos> newContactos) {
        contactos.clear();
        contactos.addAll(newContactos);
        notifyDataSetChanged();
    }

    /* Para buscar
    @Override
    public Filter getFilter() {
        return new Filter() {
            @Override
            protected FilterResults performFiltering(CharSequence constraint) {
                FilterResults results = new FilterResults();

                if (constraint != null && constraint.length() > 0) {
                    String filterPattern = constraint.toString().toLowerCase().trim();
                    Log.d("Este es el filtrado", filterPattern);
                    List<Contactos> filteredList = new ArrayList<>();

                    for (Contactos item : contactosTemporal) { // Cambio a contactosTemporal
                        Log.d("HOLA", "HOLA");
                        if (item.getNombre().toLowerCase().contains(filterPattern)) {
                            filteredList.add(item);
                        }
                    }

                    results.count = filteredList.size();
                    results.values = filteredList;
                } else {
                    results.count = contactosTemporal.size();
                    results.values = new ArrayList<>(contactosTemporal); // Crear una copia para evitar la modificación directa de la lista original
                }

                return results;
            }
            @Override
            protected void publishResults(CharSequence constraint, FilterResults results) {
                if (results != null && results.values != null) {
                    contactos.clear();
                    contactos.addAll((List<Contactos>) results.values);
                    notifyDataSetChanged();
                }
            }
        };
    }*/
}