package com.juanko.core.data.utils;

import java.io.File;
import java.io.FileInputStream;
import java.io.IOException;
import java.util.ArrayList;
import java.util.Arrays;
import java.util.List;
import java.util.Observable;
import java.util.Observer;
import java.util.Properties;
import java.util.Timer;
import java.util.TimerTask;
import org.apache.log4j.Logger;

/**
 * Clase encargada de manejar los accesos a las propiedades del archivo de
 * recursos de la aplicación
 *
 * @author glucero
 */
public final class ResourcesManager extends Observable {

    private static final Logger logger = Logger.getLogger("seagal");

    /**
     * Nombre del archivo de porpiedades que se encuentra en el classpath
     */
    private static final String PROPERTY_FILE_NAME = "etc" + System.getProperty("file.separator") + "business-${TSENV}-${TSNODE}.properties";

    /**
     * Separador de los valores dentro de una propiedad
     */
    private static final String SEPARATOR = ";";

    /**
     * Caracter que delimita el inicio de una lista de valores
     */
    private static final String BEGIN_PROPERTY = "[";

    /**
     * Caracter que delimita el fin de una lista de valores
     */
    private static final String END_PROPERTY = "]";

    /**
     * Separador de cada atributo dentro de una key de propiedad
     */
    private static final String INLINE_SEPARATOR = ".";

    /**
     * En esta propiedad se especifica el tiempo en milisegundos de cada cuanto
     * se chequea si el archivo de propiedaes ha sido modificado
     */
    private static final String SLEEP_WATCHER = "file.watcherSleep";

    private static Properties properties;
    private static final ResourcesManager instance = new ResourcesManager();

    public static String concatProperties(String... properties) {
        StringBuilder result = new StringBuilder();
        for (String property : properties) {
            result.append(property);
            result.append(INLINE_SEPARATOR);
        }
        return result.substring(0, result.lastIndexOf(INLINE_SEPARATOR));
    }

    public ResourcesManager() {
        loadProperties();
    }

    /**
     * Metodo que carga las propiedades de la aplicación
     */
    private void loadProperties() {
        try {
            properties = new Properties();
            String fileName = PROPERTY_FILE_NAME
                    .replace("${TSENV}", System.getenv("TSENV"))
                    .replace("${TSNODE}", System.getenv("TSNODE"));
            properties.load(new FileInputStream(new File(fileName)));
            updateResourceListener(fileName);
        } catch (IOException e) {
            logger.error("Error al cargar propiedades de la aplicación ", e);
        }
    }

    /**
     * Metodo que dada una key retorna el value asociado en el archivo de
     * propiedades. Si la clave no exista se lanza una excepción
     *
     * @param key Clave a buscar
     * @return Valor asociado a la key
     */
    public static String getPropertyValue(String key) {
        return properties.getProperty(key).trim();
    }

    public static Integer getIntegerValue(String key) {
        return Integer.valueOf(getPropertyValue(key));
    }

    /**
     * Metodo que dada una key retorna el value asociado en el archivo de
     * propiedades en forma de lista de String. Si la clave no exista se lanza
     * una excepción
     *
     * @param key Clave a buscar
     * @return Valor asociado a la key
     */
    public static List<String> getPropertyAsList(String key) {
        List<String> result;
        String listProperty = properties.getProperty(key).trim();
        if (listProperty.startsWith(BEGIN_PROPERTY) && listProperty.endsWith(END_PROPERTY)) {
            listProperty = listProperty.substring(1, listProperty.length() - 1);
            result = Arrays.asList(listProperty.split(SEPARATOR));
        } else {
            result = new ArrayList<>();
        }
        return result;
    }

    /**
     * Metodo encargado de monitorear constantemente el archivo de propiedades
     * para realizar las actualizaciones en caliente cuando el archivo se
     * modifique para los lugares donde sea posible. Para que las
     * actualizaciones se efectuen correctamente en los lugares necesarios se
     * tiene que notificar a todos los observers que estan registrados. Por
     * ejemplo, si se cambia el nivel de logeo, deberia notificar a
     * LoggerManager
     *
     */
    public void updateResourceListener(String fileName) {

        final File f = new File(fileName);
        Timer timer = new Timer();
        TimerTask task = new TimerTask() {
            @Override
            public void run() {
                long initialTime = f.lastModified();
                while (true) {
                    try {
                        //cada 2 minutos verifica si el archivo de propiedades ha sido modificadp
                        Thread.sleep(Long.valueOf(ResourcesManager.getPropertyValue(SLEEP_WATCHER)));
                        if (initialTime < f.lastModified()) {
                            initialTime = f.lastModified();
                            //Recarga las propiedades y notifica a los observers que estan regsitrados
                            properties.load(new FileInputStream(fileName));

                            setChanged();
                            notifyObservers("Cambio en las properties");
                        }
                    } catch (IOException | InterruptedException ex) {
                        logger.error("");
                    }
                }
            }
        };
        timer.schedule(task, 5000);
    }

    public static void addResourceObserver(Observer o) {
        instance.addObserver(o);
    }

    @Override
    public synchronized void addObserver(Observer o) {
        super.addObserver(o);
    }

}
