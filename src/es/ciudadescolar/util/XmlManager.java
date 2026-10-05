package es.ciudadescolar.util;

import java.io.File;
import java.io.FileWriter;
import java.io.IOException;
import java.util.ArrayList;
import java.util.List;

import javax.xml.XMLConstants;
import javax.xml.parsers.DocumentBuilder;
import javax.xml.parsers.DocumentBuilderFactory;
import javax.xml.parsers.ParserConfigurationException;
import javax.xml.transform.OutputKeys;
import javax.xml.transform.Transformer;
import javax.xml.transform.TransformerConfigurationException;
import javax.xml.transform.TransformerException;
import javax.xml.transform.TransformerFactory;
import javax.xml.transform.dom.DOMSource;
import javax.xml.transform.stream.StreamResult;
import javax.xml.validation.Schema;
import javax.xml.validation.SchemaFactory;

import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.w3c.dom.Document;
import org.w3c.dom.Element;
import org.w3c.dom.Node;
import org.w3c.dom.NodeList;
import org.xml.sax.SAXException;

import es.ciudadescolar.modelo.Alumno;

public class XmlManager {

      private static final Logger LOG = LoggerFactory.getLogger(XmlManager.class);
    /**
     * Método para recuperar los alumnos del xml alumnos.xml
     * @throws SAXException 
     */
    public static List<Alumno> procesarXmlAlumnos (File xml, File xsd)
    {
        List<Alumno> listaAlumnos = null;
        Alumno alumno = null;

        DocumentBuilderFactory dbf = DocumentBuilderFactory.newInstance();

        // como nuestro xml NO se valida contra un dtd (sino contra un XSD) podemos añadir esto:
        dbf.setValidating(false);

        // Como vamos a validar contra un XSD (schema) necesitamos considerar los namespaces
        dbf.setNamespaceAware(true);


        // ignorar los nodos con espacios no productivos.
        // ignora los nodos de texto que contienen únicamente espacios en blanco usados para formatear el XML: 
        // saltos de línea, tabulaciones y espacios de indentación.
        // Para que funcione correctamente, el parser debe saber qué espacios son realmente prescindibles 
        // y para eso normalmente necesitas validación contra una DTD o un esquema.
        dbf.setIgnoringElementContentWhitespace(true);

        SchemaFactory sf = SchemaFactory.newInstance(XMLConstants.W3C_XML_SCHEMA_NS_URI);

        Schema schema = null;

        try 
        {
            schema = sf.newSchema(xsd);

            dbf.setSchema(schema);
            
            DocumentBuilder db = dbf.newDocumentBuilder();
           
            // cargamos el XML en la estructura DOM (en memoria)
            
            // Vamos a personalizar el tratamiento de errores durante el parseo
            db.setErrorHandler(new AlumnoErrorhandler());

            Document documento = db.parse(xml);

            // Recuperamos el elemento raiz del XML
            Element elementoRaiz = documento.getDocumentElement();

            LOG.trace("el nodo raiz del xml es: "+elementoRaiz.getNodeName());

            // opción1: recuperar todos los hijos del elemento raiz (sin saber qué elementos concretos son)
            //NodeList listaNodosAlumno = elementoRaiz.getChildNodes();

            //opción2: recuperar los descendientes con un determinado nombre
            NodeList listaNodosAlumno2 = elementoRaiz.getElementsByTagName("alumno");

            listaAlumnos = new ArrayList<Alumno>();

            LOG.trace("se han recuperado total de ["+listaNodosAlumno2.getLength()+"] alumnos");

            for (int i=0; i<listaNodosAlumno2.getLength();i++)
            {
                Node nodoAlumno = listaNodosAlumno2.item(i);

                if (nodoAlumno.getNodeType() == Node.ELEMENT_NODE)
                {
                    Element elementoAlumno = (Element) nodoAlumno;
                    
                    Node nodoExpediente = elementoAlumno.getFirstChild();
                    Node nodoNombre = nodoExpediente.getNextSibling();
                    Node nodoEdad = elementoAlumno.getLastChild();
                    
                    alumno = new Alumno(nodoNombre.getTextContent(), nodoExpediente.getTextContent(), Integer.parseInt(nodoEdad.getTextContent()));
                    LOG.trace("Nuevo alumno creado: "+alumno.toString());
                    listaAlumnos.add(alumno);
                    LOG.trace("Añadido alumno a la colección");
                }

            }

        } catch (SAXException e) 
        {
             LOG.error("Error durante el parseo de xml: "+ e.getMessage());
       
        } catch (IOException e) {
            LOG.error("Error de I/O durante el parseo del xml: "+e.getMessage());         
        }
        catch (ParserConfigurationException e) 
        {
            LOG.error("Error durante el parseo de xml: "+ e.getMessage());

        }
    
        return listaAlumnos;
        
    }

    public static void generarNuevoXml(List<Alumno> alumnos, File xmlSalida, File dtdValidor)
    {
        DocumentBuilderFactory dbf = null;

        DocumentBuilder db = null;

        Document doc = null;

        Element elementoAlumno = null;
        Element elementoEdad = null;

        try 
        {
            dbf = DocumentBuilderFactory.newInstance();
            db  = dbf.newDocumentBuilder();

            // Aquí no tiene sentido fijar un ErrorHandler porque no estamos parseando ningún xml
            
            // vamos a crear una estructura DOM nueva
            doc = db.newDocument();

            Element raiz=doc.createElement("estudiantes");
            doc.appendChild(raiz);

            for (Alumno al:alumnos)
            {
                    elementoAlumno = doc.createElement("alumno");
                    LOG.trace("Creando elemento alumno");
                    elementoAlumno.setAttribute("exp", al.getExpediente());
                    elementoAlumno.setAttribute("nom",al.getNombre());
                    elementoAlumno.setAttribute("edad", al.getEdad().toString());
                    LOG.trace("Añadidos los atributos al elemento alumno");
                    elementoEdad = doc.createElement("edad");
                    elementoEdad.setTextContent(al.getEdad().toString());
                    elementoAlumno.appendChild(elementoEdad);
                    LOG.trace("Añadido hijo edad al elemento alumno");


                    // Recordad crear los elementos y AÑADIRLOS al DOM
                    raiz.appendChild(elementoAlumno);
                    LOG.trace("Añadido elemento alumno al elemento raiz del DOM");
            }

            LOG.trace("Creada estuctura DOM para ser volcada a fichero");
            // Hasta aquí ya tengo montada la estructura DOM definitiva. Solo queda volcarla a fichero xml

            TransformerFactory tf = null;
            Transformer t = null;
            DOMSource ds = null;
            StreamResult sr = null;

            if (xmlSalida.exists())
            {
                xmlSalida.delete();
                LOG.warn("El fichero de salida ya existía. Se procede a borrar");
            }
            
            tf = TransformerFactory.newInstance();
            t = tf.newTransformer();
            ds = new DOMSource(doc);

            FileWriter fw = new FileWriter(xmlSalida);
            sr = new StreamResult(fw);

            // nuestro XML de salida queremos que se valide contra el DTD recibido como parámetro (dtdValidator)
            t.setOutputProperty(OutputKeys.DOCTYPE_SYSTEM, dtdValidor.getName());

            t.setOutputProperty(OutputKeys.METHOD, "xml");
            t.setOutputProperty(OutputKeys.VERSION, "1.0");
            t.setOutputProperty(OutputKeys.ENCODING, "UTF-8");
            t.setOutputProperty(OutputKeys.INDENT, "yes");
            
            t.transform(ds, sr);
            LOG.trace("Generado fichero XML a partir de la estructura DOM");
            
        } catch (ParserConfigurationException e) 
        {
            LOG.error("Error durante la generación del xml ["+xmlSalida.getName()+"]"+ e.getMessage());
        } catch (TransformerConfigurationException e) {
            LOG.error("Error durante la generación del xml ["+xmlSalida.getName()+"]"+ e.getMessage());
        } catch (IOException e) {
            LOG.error("Error durante la generación del xml ["+xmlSalida.getName()+"]"+ e.getMessage());            
        } catch (TransformerException e) {
            LOG.error("Error durante la generación del xml ["+xmlSalida.getName()+"]"+ e.getMessage());

        }


    }

}
