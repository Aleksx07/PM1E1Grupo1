package com.example.PM1E1Grupo1;
import android.content.Context;
import android.content.Intent;
import android.graphics.Bitmap;
import android.graphics.BitmapFactory;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.BaseAdapter;
import android.widget.ImageView;
import android.widget.TextView;
import com.example.PM1E1Grupo1.transacciones.Contactos;
import java.util.ArrayList;
import java.util.List;

public class CustomBaseAdapter extends BaseAdapter {


    //Adaptador personalizado para la lista de contactos.

    Context context;
    LayoutInflater inflater;
    private List<Contactos> contactos;

    //Constructor del adaptador personalizado.
    public  CustomBaseAdapter(Context context, ArrayList<Contactos> contactos)
    {
        this.context = context;
        this.contactos = contactos;
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
            // Inflar el diseño de la fila si es nulo
            convertView = LayoutInflater.from(context).inflate(R.layout.activity_custom_list_view, parent, false);
        }

        ImageView imagenImageView = convertView.findViewById(R.id.imageIcom);
        TextView nombreTextView = convertView.findViewById(R.id.txtnombre);

        Contactos contacto = contactos.get(position);
        Bitmap b = BitmapFactory.decodeFile(contacto.getImagen());
        imagenImageView.setImageBitmap(b);
     //  imagenImageView.setImageResource(contacto.getImagen());
        nombreTextView.setText(contacto.getNombre());
        convertView.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                Intent intent = new Intent(context, ActivityView.class);
                intent.putExtra("id", contacto.getId());
                intent.putExtra("nombre",contacto.getNombre());
                intent.putExtra("numero",contacto.getNumero());
                intent.putExtra("latitud",contacto.getLatitud());
                intent.putExtra("longitud",contacto.getLongitud());
                intent.putExtra("imagen", contacto.getImagen());
                context.startActivity(intent);
            }
        });

        return convertView;
    }


}