package es.ciudadescolar.util;

import org.xml.sax.ErrorHandler;
import org.xml.sax.SAXException;
import org.xml.sax.SAXParseException;

/*
    Clase donde podemos personalizar la gestión de los errores 
    durante el parseo del xml
*/
public class AlumnoErrorhandler implements ErrorHandler {

    @Override
    public void warning(SAXParseException exception) throws SAXException {
        System.out.println("Warning durante el parseo del xml: "+ exception.getMessage());
    }

    // si el XML está bien formado pero se indica que se valida contra un DTD o XSD
    //  pero no cumple las reglas de ese DTD o XSD
    @Override
    public void error(SAXParseException exception) throws SAXException {
        System.err.println("Error durante el parseo del xml: "+ exception.getMessage());
        throw exception;
    }

    // si XML no está bien formado (elementos que no se cierran o etiquetas incorrectas)
    @Override
    public void fatalError(SAXParseException exception) throws SAXException {
        System.err.println("Error fatal durante el parseo del xml: "+ exception.getMessage());
        throw exception;
    }

}
