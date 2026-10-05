# 📝 Ficheros XML/XSD - DOM

👨‍🏫 Asignatura: Acceso a Datos

🧑‍💻 Profesor: José Sala Gutiérrez

📆 Curso: [2026/2027]

---

Este proyecto de Java demuestra el tratamiento de ficheros XMLs para la lectura (*parser*) y escritura mediante la API DOM (org.w3c.dom). En ambos casos, el fichero de entrada y de salida se validan contra un XSD.

El objetivo es **familiarizarse con el procesamiento de ficheros XML con la API DOM** y aprender tanto a obtener información almacenada en dichos ficheros como a crear nuestros propios ficheros XML con la información solicitada. Habitualmente, la generación de un XML vendrá supeditado a un fichero validador que servirá como *acuerdo de interfaz* con otras aplicaciones o sistemas. En este caso concreto, dispondremos de un XSD para asgurar el formato de la información del XML de salida.

Se ha aprovechado el proyecto para introducir **la gestión de trazas en aplicaciones backend**  usando la api de fachada `slf4j` junto a la implementación `logback`. Gracias a slf4j nuestro código se independiza de cualquier implementación de logs usada y así a futuro podemos cambiarla sin tocar nuestro código.

## ✅ Características

- Se utilizan librerías externas para la gestión de logs: slf4j + logback.
- Se utiliza una clase Alumno para la demostración del proceso de recuperación de información y volcado de la misma.
- Se deben pasar 3 parámetros durante la ejecución de la aplicación: xml a parsear, xml a generar y dtd con el que validar xml de salida.

## 📁 Estructura del proyecto

```text

RA1-XML-DOM-CLASE-2/
├── lib/                # librerías externas para la gestión de logs
├── bin/                # Carpeta donde se ubican .class
├── src/
│   ├──es
│   │   └──ciudadescolar       
│   │      ├──util
│   │      │  ├──XmlManager.java           # clase interacción con ficheros XML (parsea y genera) 
│   │      │  └──AlumnoErrorHandler.java   # clase gestora de errores durante parseo/generación de XML.
|   │      ├──modelo
│   │      │  └──Alumno.java
│   ├──Logback.xml                         # Fichero de configuración del log                
│   └──App.java                            # Clase principal 
└── README.md                              # Este documento

````

## ▶️ ¿Cómo probar la funcionalidad?

Debes ubicar el xml "alumnos3.xml" a parsear y el XSD "alumnos3.xsd" que lo valida en el directorio de trabajo del proyecto. Así mismo, debes ubicar el XSD "alumno4.xsd" que permitirá validar el fichero de salida que genera la aplicación "alumnos4.xml"
La aplicación precisa de 3 parámetros pasados al main:

- opción 1: desde VSCode *debugeando* con fichero **launch.json**
- opción 2: exportada la app en jar (ra1-xml-dom-clase.jar), se invocaría:  `java -jar ra1-xml-dom-clase_2.jar <xml_entrada> <xml_salida> <xsd_salida>`

Tras ejecutar el programa principal de este mismo repositorio, se mostrarán los alumnos por consola y se generá un xml de salida "alumnos4.xml" validable con el XSD proporcionado "alumnos4.xsd". El fichero de salida se sobreescribe con cada ejecución.
