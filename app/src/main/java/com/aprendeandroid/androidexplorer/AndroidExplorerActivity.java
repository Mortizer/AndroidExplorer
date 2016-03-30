package com.aprendeandroid.androidexplorer;

import android.app.AlertDialog;
import android.content.DialogInterface;
import android.content.res.Configuration;
import android.os.Bundle;
import android.support.v4.app.FragmentActivity;
import android.support.v4.app.FragmentManager;
import android.support.v4.app.FragmentTransaction;
import android.widget.TextView;

import com.aprendeandroid.androidexplorer.SupportListFragment.ListItemSelectedListener;

import java.io.File;
import java.io.FileFilter;
import java.util.ArrayList;
import java.util.List;

public class AndroidExplorerActivity extends FragmentActivity implements ListItemSelectedListener {

    //Fragments
    private SupportListFragment listFrag;


    //Para el sistema de directorios y archivos
    private List<DirectoryList> paths = null;
    private String root = "/"; // directorio raiz
    private String currentDir = root;

    //Para elementos de la vista, muestra el path actual
    private TextView myPath;

    //Para filtrar los archivos por extensiones, si se queda null, los muestra todos
    public static String[] extension = null;

    //Por si la orientacion cambia
    private boolean orientationChange = false;

    private FileAdapter fadapt;


    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_android_explorer);


        //INSERTAMOS EL FRAGMENT CONFIGURADO CON BUNDLE PARA SU PROPIO LAYOUT
        listFrag = new SupportListFragment();

        Bundle parametros = new Bundle();
        parametros.putInt("listLayoutId", R.layout.list_fragment);
        listFrag.setArguments(parametros);

        FragmentManager fm = getSupportFragmentManager();
        FragmentTransaction ft = fm.beginTransaction();

        ft.add(R.id.listPlace, listFrag, "list");
        ft.commit();


        //Recogemos una referencia al campo de texto superior para informar al usuario del directorio actual
        myPath = (TextView) findViewById(R.id.path);


        if (savedInstanceState != null && savedInstanceState.containsKey("currentDir")) {
            currentDir = savedInstanceState.getString("currentDir");
        }



        //llamamos a este metodo y le pasamos el path actual para que lo recorra y saque archivos o carpetas
        getDir(currentDir);

    }


    @Override
    protected void onSaveInstanceState(Bundle outState) {
        super.onSaveInstanceState(outState);
        outState.putString("currentDir", currentDir);
    }

    @Override
    public void onConfigurationChanged(Configuration newConfig) {
        super.onConfigurationChanged(newConfig);
        orientationChange = true;
    }


    /**
     * Recibe el path de un directorio e inserta en el Adapter del ListView
     * los nombres de los ficheros que tengan las extensiones del array extension,
     * los subdirectorios, el directorio raiz y el directorio padre si existe.
     *
     * @param dirPath el path del directorio
     */
    private void getDir(String dirPath) {

        myPath.setText(getResources().getString(R.string.cabeceraText) + dirPath); // muestra el path actual en el textView

        paths = new ArrayList<DirectoryList>(); // se crea la lista de paths

        // se crea el objeto File, que corresponde a un directorio
        File f = new File(dirPath);

        // se carga la lista de ficheros con las extensiones o directorios
        File[] files = f.listFiles(new ProjectFilter());

        String iconDir = getResources().getString(R.string.icon_directory);
        String iconFile = getResources().getString(R.string.icon_file);
        String parentName = getResources().getString(R.string.parent_name);


        if (!dirPath.equals(root)) { // si no es el directorio raiz, colocamos para ir a raiz o al directorio anterior
            paths.add(new DirectoryList(root, root, iconDir)); //anade root "/"
            paths.add(new DirectoryList(f.getParent(), parentName, iconDir)); //anade directorio padre "//"
        }


        for (int i = 0; i < files.length; i++) {
            File file = files[i];

            if (file.isFile()) {
                paths.add(new DirectoryList(file.getPath(), file.getName(), iconFile)); //Se guardan todos los ficheros de igual forma.
            }
            if (file.isDirectory()) {
                paths.add(new DirectoryList(file.getPath(), file.getName(), iconDir)); //Se guardan todos los directorios deigual forma.
            }
        }

        // Creamos el custom ArrayAdapter creando un nuevo onbjeto de la clase FileAdapter, donde le pasamos el layout,
        // el array con todos los files y la direccion del assets.
        fadapt = new FileAdapter(this, R.layout.custom_listpath, paths, getResources().getString(R.string.icon_asset_dir));
        listFrag.setListAdapter(fadapt);
    }



    /**
     * Esta clase se usa para saber si un path (fichero o directorio)
     * se va a mostrar en la lista del ListView, o sea, si es un
     * directorio o un fichero con las extensiones requeridas
     * <p/>
     * Se podria establecer cualquier otra condicion
     */
    private static class ProjectFilter implements FileFilter {

        public ProjectFilter() {

        }

        //No se filtra ningún fichero, ya que se desea que salgan todos para mostrarlos.
        @Override
        public boolean accept(File pathname) {

            if (pathname.isDirectory() || extension == null) { // si es directorio o no se han declarado extensiones
                return true; // devuelve verdadero
            }

            // nombre del fichero en minusculas, para que reconozca extensiones
            String name = pathname.getName().toLowerCase();

            // se recorre la lista de extensiones requeridas
            for (String anExt : extension) {
                if (name.endsWith(anExt)) { // si en nombre termina con la extension...
                    return true; //...devuelve verdadero
                }
            }
            return false;
        }
    }


    //-----------------------------------------------SI PULSAMOS ALGUN ELEMENTO DE LA LISTA interfaz de SupporListFragment
    @Override
    public void onListItemSelected(int position) {

        if (orientationChange) {
            orientationChange = false;
            return;
        }


        // crea un objeto File con el path del item del ListView pulsado
        File file = new File(paths.get(position).getTextDir());

        if (file.isDirectory()) { // si es un directorio

            if (file.canRead()) {// ES UN DIRECTORIO READ
                currentDir = paths.get(position).getTextDir();
                getDir(currentDir);
            } else {  //ES UN DIRECTORIO NO-READ, mostramos un AlertDialog para informar

                new AlertDialog.Builder(this).setIcon(R.drawable.ic_launcher).setTitle("[" + file.getName() + "] folder can't be read!")
                        .setPositiveButton("OK", new DialogInterface.OnClickListener() {

                            @Override
                            public void onClick(DialogInterface dialog, int which) {
                            }// este metodo no necesita hacer nada pero debe estar
                        }).show();
            }
        } else {
            // TODO dependiendo de la extension (tipo MIME)
        }

    }


}
