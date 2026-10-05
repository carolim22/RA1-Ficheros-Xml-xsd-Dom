import java.io.File;
import java.util.List;

import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

import es.ciudadescolar.modelo.Alumno;
import es.ciudadescolar.util.XmlManager;

public class App {

    private static final Logger LOG = LoggerFactory.getLogger(App.class);
    
    public static void main(String[] args) throws Exception 
    {
       
        File xmlFile, xsdFile = null;

        // System.out.println("num parametros: "+ args.length);
        LOG.trace("El número de parámetros pasados al main es: "+args.length);

        LOG.info("Comenzando aplicación...");
        if(args.length != 4)
        {
            LOG.error("Invocación incorrecta. Se debe pasar 4 parámetros");
            LOG.error("Parámetro1:  nombre del xml de entrada");
            LOG.error("Parámetro2:  nombre del xsd de entrada");
            LOG.error("Parámetro3:  nombre del xml de salida");
            LOG.error("Parámetro4:  nombre del xsd con el que validar el xml de salida");
        }
        else
        {
            xmlFile = new File(args[0]);
            xsdFile = new File(args[1]);
            if (xmlFile.exists() && xmlFile.canRead())
            {
                    LOG.trace("Validación completada sobre fichero ["+xmlFile.getName()+"]: localizado y legible");
                    // hemos creado un método de clase (static) en lugar de método de instancia
                    if(xsdFile.exists() && xsdFile.canRead())
                    {
                        LOG.trace("Procediendo a parsear el fichero xml en cuestión");
                        List<Alumno> alumnos = XmlManager.procesarXmlAlumnos(xmlFile,xsdFile);
                        LOG.trace("Completado parseo del fichero xml");
                        if (alumnos !=null)
                        {
                            if (alumnos.isEmpty())
                            {
                                LOG.warn("Ojo que no se ha recuerado ningún alumno");
                            }
                            else
                            {
                                LOG.info("La lista de alumnos recuperada es:");
                                for (Alumno al:alumnos)
                                    LOG.info(al.toString());

                               /*  LOG.trace("Procediendo a generar el fichero xml de salida");
                                File xmlSalida = new File(args[2]);
                                LOG.trace("Fichero xml de salida: "+ xmlSalida.toString());

                                File xsdSalida = new File(args[3]);
                                LOG.trace("Fichero dtd contra el que validar el xml de salida: "+ xsdSalida.toString());
                                XmlManager.generarNuevoXml(alumnos, xmlSalida, xsdSalida);
                                LOG.trace("Completada generación del fichero xml de salida");*/
                            }
                        }
                        }
                        else
                        {
                            LOG.error("XSD alumnos inaccesible: "+xsdFile.getAbsolutePath());
                        }
            }
            else
                LOG.error("XML alumnos inaccesible: "+xmlFile.getAbsolutePath());
        }

        LOG.info("Finalizando aplicación..."+ App.class.getSimpleName());
    }
}

