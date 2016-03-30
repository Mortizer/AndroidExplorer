package com.aprendeandroid.androidexplorer;

import android.app.Activity;
import android.content.Context;
import android.graphics.Bitmap;
import android.graphics.BitmapFactory;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.ArrayAdapter;
import android.widget.ImageView;
import android.widget.TextView;

import java.io.IOException;
import java.io.InputStream;
import java.util.List;


public class FileAdapter extends ArrayAdapter<DirectoryList> {

    Context context;
    int layoutRes;
    List<DirectoryList> pathlist;
    String iconPath;


    public FileAdapter(Context context, int LayoutID, List<DirectoryList> pathlist,String iconPath){
        super(context,LayoutID,pathlist);

        this.context = context;
        this.layoutRes = LayoutID;
        this.pathlist = pathlist;
        this.iconPath = iconPath;
    }

    @Override
    public View getView(int position, View convertView, ViewGroup parent){

        View row = convertView;
        String pathImg;
        Bitmap bmap;

        FileHolder holder;

        if (row == null) {
            LayoutInflater inflater = ((Activity) context).getLayoutInflater();
            row = inflater.inflate(layoutRes, parent,false);

            holder = new FileHolder();
            holder.imgDir = (ImageView) row.findViewById(R.id.imagerowlist);
            holder.textDir = (TextView) row.findViewById(R.id.textrowlist);
            row.setTag(holder);
        }else{
            holder = (FileHolder) row.getTag();
        }

        holder.textDir.setText(pathlist.get(position).getNameDir());

        //------- TRATAMIENTO PARA LA IMAGEN ---------

        Integer maxDimension = (int)context.getResources().getDimension(R.dimen.heightCustomList);
        String [] ext = context.getResources().getStringArray(R.array.exAllow);

        if (pathlist.get(position).getNameDir().lastIndexOf(".")>0){  //es un fichero

            if (EsImagen(pathlist.get(position).getNameDir().toLowerCase(),ext)){ //si es una imagen

                pathImg = pathlist.get(position).getTextDir().toLowerCase();
                bmap = ScaleBitmapImg(pathImg, maxDimension);

            }else {

                pathImg = iconPath + "/" + pathlist.get(position).getImgDir();
                bmap = ScaleBitmap(pathImg, maxDimension);
            }

        }
        else{

            pathImg = iconPath + "/" + pathlist.get(position).getImgDir();
            bmap = ScaleBitmap(pathImg, maxDimension);

        }


        //Escalamos la imagen para que se muestre de forma correcta
        if (bmap == null){
            holder.imgDir.setImageDrawable(context.getResources().getDrawable(R.drawable.no_image));
        }else {
            holder.imgDir.setImageBitmap(bmap);
        }

        return row;
    }

    private boolean EsImagen (String path, String[] ext){

        for (String anExt : ext) {
            if (path.endsWith(anExt)) { // si en nombre termina con la extension..
                return true;
            }
        }
        return false;
    }



    //Escalado de una imagen con origen Assets
    public Bitmap ScaleBitmap(String pathimg, int maxDimension){

        Bitmap scaleImg;
        InputStream is;
        BitmapFactory.Options options = new BitmapFactory.Options();

        options.inJustDecodeBounds = true;
        try {
            is= context.getAssets().open(pathimg);
            scaleImg = BitmapFactory.decodeStream(is);

            if ((maxDimension > options.outHeight) || (maxDimension >options.outWidth)){
                options.inSampleSize = Math.round(Math.max((float) options.outHeight / (float)maxDimension,
                        (float) options.outWidth / (float)maxDimension));
            }

            is.close();

        }catch (IOException e){
            e.printStackTrace();
            scaleImg = null;
        }

        options.inJustDecodeBounds = false;

        return scaleImg;
    }

    //Escalado de una imagen con origen un path.
    public Bitmap ScaleBitmapImg(String pathimg, int maxDimension){

        final BitmapFactory.Options options = new BitmapFactory.Options();
        BitmapFactory.decodeFile(pathimg, options);

        options.inJustDecodeBounds = true;

        options.inSampleSize = calculateInSampleSize(options,maxDimension,maxDimension);

        options.inJustDecodeBounds = false;

        return BitmapFactory.decodeFile(pathimg, options);
    }


    public static int calculateInSampleSize(
            BitmapFactory.Options options, int reqWidth, int reqHeight) {
        // Raw height and width of image
        final int height = options.outHeight;
        final int width = options.outWidth;
        int inSampleSize = 1;

        if (height > reqHeight || width > reqWidth) {

            final int halfHeight = height / 2;
            final int halfWidth = width / 2;

            // Calculate the largest inSampleSize value that is a power of 2 and keeps both
            // height and width larger than the requested height and width.
            while ((halfHeight / inSampleSize) > reqHeight
                    && (halfWidth / inSampleSize) > reqWidth) {
                inSampleSize *= 2;
            }
        }

        return inSampleSize;
    }


    static class FileHolder{

        ImageView imgDir;
        TextView textDir;

    }
}
