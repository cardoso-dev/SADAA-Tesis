/*
 * Actualiza.java
 *   Encargado de operaciones insert y update en la base de datos
 *   Todos los metodos son static
 * Parte de proyecto: SADAAA
 * Author: Pedro Cardoso Rodriguez
 * Mail: ingpedro@live.com
 * Place: Zacatecas Mexico
 * 
    Copyright © 2010 Pedro Cardoso Rodriguez

    SADAA is free software: you can redistribute it and/or modify
    it under the terms of the GNU General Public License as published by
    the Free Software Foundation, either version 3 of the License, or any 
    later version.

    SADAA is distributed in the hope that it will be useful,
    but WITHOUT ANY WARRANTY; without even the implied warranty of
    MERCHANTABILITY or FITNESS FOR A PARTICULAR PURPOSE.  See the
    GNU General Public License for more details.

    You should have received a copy of the GNU General Public License
    along with SADAA.  If not, see <http://www.gnu.org/licenses/>
 */

package database;
import java.sql.*;
import java.io.FileInputStream;

/**  Clase que proporciona metodos (static) para realizar operaciones de
 *  naturaleza update e insert de registros en la base de datos.
 * 
 * @author Pedro Cardoso Rodríguez
 */
public class Actualiza {
  
    /**Para enviar a ejecuta sentencias sql en la bd*/
    private static Statement sentencia;
    /**Para enviar a ejecuta procedimientos almacenados en la bd*/
    private static CallableStatement callProcedure;
    /**Descripcion del ultimo error ocurrido*/
    private static String error;
    /**Ultima clave generada en inserciones en registros con campo clave auto_increment*/
    private static int clave;
    
    /** Crea un registro de nuevo alumno en la base de datos llamando al procedimiento almacenado nuevoAlumno
     * @param datos Los datos del nuevo alumno en el orden: matricula, apellido paterno, apellido materno,
     *  nombre, fecha nacimiento, fecha ingreso, domicilio, municipio, telefono casa, telefono movil, correo electronico, 
     *  expectativas, pasatiempos, materias preferidas, materias no preferidas, observaciones, nombre del padre, 
     *   nombre de la madre, escolaridad padre, escolaridad madre, empleo del padre, numero de hijos, numero de hermanas,
     *   numero de hermanos, posicion en el orden de nacimiento, municipio de origen, direccion de los padres,
     *   telefono de los padres, escuela primaria, promedio primaria, escuela secundaria, promedio secundaria, 
     *   escuela bachiller, promedio bachiller, otros cursos
     * @param fto Archivo con la foto del alumno null si no se dispone de ella
     * @param btsFto Tamanio en bites del archivo de la foto 0 si no se dispone de ella
     * @param considerar_aut Booleano que indica si se debe tomar en cuenta la solicitud de 
     *   autentificacion pidiendo password de seguridad (si la opcione esta establecida para operaciones insert)
     * @return true si el registro se creo correctamente false en caso contrario
     */
    public static boolean nuevoAl(String[] datos, FileInputStream fto, long btsFto, boolean considerar_aut){
        if(Conexion.autAltas&&considerar_aut)
            if(!Conexion.tienePermiso("Alta de alumno")){
                error="No tiene permiso para realizar la acción solicitada!";
                return false;
            }
        try{            
            String aux="";
            for(int j=0;j<=datos.length;j++) aux+="?"+(j<datos.length?",":"");
            callProcedure = Conexion.conecBd.prepareCall("{call nuevoAlumno("+aux+")}");
            for(int j=0,k=0;j<=datos.length;j++,k++){
                try{
                    if(j==4 || j==5) callProcedure.setString(j+1, operaciones.Datos.transformatFcha(datos[k]));
                    else if(j==16){ callProcedure.setBinaryStream(j+1,fto,btsFto); k--; }
                    else if(j>=22 && j<=25) callProcedure.setInt(j+1, Integer.parseInt(datos[k]));
                    else if(j==30 || j==32 || j==34) callProcedure.setFloat(j+1, Float.parseFloat(datos[k]));
                    else callProcedure.setString(j+1, datos[k]);
                }
                catch(NumberFormatException nfExc){
                    if(j>=22 && j<=25) callProcedure.setInt(j+1,0);
                    else if(j==30 || j==32 || j==34) callProcedure.setFloat(j+1,0);
                }
            }
            callProcedure.executeUpdate();
            callProcedure.close();
        }
        catch(SQLException excSqlUpd){
            error=excSqlUpd.getMessage();
            return false;
        }
        catch(NullPointerException npExc){
            error="Verifique que haya conexión con el servidor!";
            return false;
        }
        return true;
    }
    
    /** Actualiza un registro de alumno en la base de datos llamando al procedimiento almacenado actuRegAlumno
     * @param datos Los nuevos datos del alumno en el orden: matricula, apellido paterno, apellido materno,
     *  nombre, fecha nacimiento, fecha ingreso, domicilio, municipio, telefono casa, telefono movil, correo electronico, 
     *  expectativas, pasatiempos, materias preferidas, materias no preferidas, observaciones, nombre del padre, 
     *   nombre de la madre, escolaridad padre, escolaridad madre, empleo del padre, numero de hijos, numero de hermanas,
     *   numero de hermanos, posicion en el orden de nacimiento, municipio de origen, direccion de los padres,
     *   telefono de los padres, escuela primaria, promedio primaria, escuela secundaria, promedio secundaria, 
     *   escuela bachiller, promedio bachiller, otros cursos
     * @param clavedc Clave de registro de datos complemntarios (tabla DatCo de la bd)
     * @param fto Archivo con la foto del alumno o null para vacio
     * @param btsFto Tamanio en bites del archivo de la foto 0 para vacio
     * @param cambiaFto Booleano que indica si debe actualiza el campo foto
     * @param considerar_aut Booleano que indica si se debe tomar en cuenta la solicitud de 
     *   autentificacion pidiendo password de seguridad (si la opcione esta establecida para operaciones insert)
     * @return true si el registro se creo correctamente false en caso contrario
     */
    public static boolean actualizaRegAl(String[] datos, int clavedc, FileInputStream fto, long btsFto, boolean cambiaFto, boolean considerar_aut){
        if(Conexion.autCambs&&considerar_aut)
            if(!Conexion.tienePermiso("Modificacion datos de alumno")){
                error="No tiene permiso para realizar la acción solicitada!";
                return false;
            }
        try{               
            String aux="?,?,?,";
            for(int j=0;j<datos.length;j++) aux+="?"+(j<datos.length-1?",":"");
            callProcedure = Conexion.conecBd.prepareCall("{call actuRegAlumno("+aux+")}");
            for(int j=0, k=0;j<=datos.length;j++,k++){
                try{
                    if(j==4 || j==5) callProcedure.setString(j+1, operaciones.Datos.transformatFcha(datos[k]));
                    else if(j==16){ callProcedure.setBinaryStream(j+1, fto, btsFto); k--; }
                    else if(j>=22 && j<=25) callProcedure.setInt(j+1, Integer.parseInt(datos[k]));
                    else if(j==30 || j==32 || j==34) callProcedure.setFloat(j+1, Float.parseFloat(datos[k]));
                    else callProcedure.setString(j+1, datos[k]);
                }
                catch(NumberFormatException nbfExc){
                    if(j>=22 && j<=25) callProcedure.setInt(j+1,0);
                    else if(j==30 || j==32 || j==34) callProcedure.setFloat(j+1,0);
                }
            }
            callProcedure.setInt(datos.length+2, clavedc);
            callProcedure.setBoolean(datos.length+3, cambiaFto);
            callProcedure.executeUpdate();
            callProcedure.close();    
        }
        catch(SQLException excSqlUpd){
            error=excSqlUpd.getMessage();
            return false;
        }
        catch(NullPointerException npExc){
            error="Verifique que haya conexión con el servidor!";
            return false;
        }
        return true;
    }
    
    /** Crea un nuevo registro de desempeño academico en la base de datos llamando al procedimiento almacenado newRgDesAca
     * @param datos Los datos del nuevo registro en el orden: matricula (del alumno a que corresponde), materia, periodo, 
     *   docente, grado, grupo, calificacion
     * @param considerar_aut Booleano que indica si se debe tomar en cuenta la solicitud de 
     *   autentificacion pidiendo password de seguridad (si la opcione esta establecida para operaciones insert)
     * @return true si el registro se creo correctamente (en cuyo caso la clave generada con el metodo obtenClave()) 
     *   false en caso contrario
     */
    public static boolean nuevoRgDesAca(String[] datos, boolean considerar_aut){
        if(Conexion.autAltas&&considerar_aut)
            if(!Conexion.tienePermiso("Alta registro academico")){
                error="No tiene permiso para realizar la acción solicitada!";
                return false;
            }
        try{            
            String aux="?,";
            for(int j=0;j<datos.length;j++) aux+="?"+(j<datos.length-1?",":"");
            callProcedure = Conexion.conecBd.prepareCall("{call newRgDesAca("+aux+")}");
            for(int j=0;j<datos.length;j++){
                try{
                    if(j==4) callProcedure.setInt(j+1, Integer.parseInt(datos[j]));
                    else if(j==6) callProcedure.setFloat(j+1, Float.parseFloat(datos[j]));
                    else callProcedure.setString(j+1, datos[j]);
                }
                catch(NumberFormatException nbfExc){
                    error=nbfExc.getMessage();
                    return false;
                }
            }
            callProcedure.registerOutParameter(datos.length+1, Types.INTEGER);
            callProcedure.executeUpdate();
            clave=Integer.parseInt(""+callProcedure.getObject(datos.length+1));
            callProcedure.close();
            if(clave==0){ 
                error="No se pudo realizar la acción";
                return false;
            }
                    
        }
        catch(SQLException excSqlUpd){
            error=excSqlUpd.getMessage();
            return false;
        }
        catch(NullPointerException npExc){
            error="Verifique que haya conexión con el servidor!";
            return false;
        }
        return true;
    }
    
    /** Crea un nuevo registro de tesis en la base de datos llamando al procedimiento almacenado newTesis
     * @param datos Los datos del nuevo registro en el orden: matricula (del alumno a que corresponde), titulo, 
     *   tipo, fecha (de inicio de asesorias), colaboradores, sinodales
     * @param considerar_aut Booleano que indica si se debe tomar en cuenta la solicitud de 
     *   autentificacion pidiendo password de seguridad (si la opcione esta establecida para operaciones insert)
     * @return true si el registro se creo correctamente (en cuyo caso la clave generada con el metodo obtenClave()) 
     *   false en caso contrario
     */
    public static boolean nuevaTesis(String[] datos, boolean considerar_aut){
        if(Conexion.autAltas&&considerar_aut)
            if(!Conexion.tienePermiso("Alta de tesis")){
                error="No tiene permiso para realizar la acción solicitada!";
                return false;
            }
        try{            
            String aux="?,";
            for(int j=0;j<datos.length;j++) aux+="?"+(j<datos.length-1?",":"");
            callProcedure = Conexion.conecBd.prepareCall("{call newTesis("+aux+")}");
            for(int j=0;j<datos.length;j++) callProcedure.setString(j+1, datos[j]);                
            callProcedure.registerOutParameter(datos.length+1, Types.INTEGER);
            callProcedure.executeUpdate();
            clave=Integer.parseInt(""+callProcedure.getObject(datos.length+1));
            callProcedure.close();
            if(clave==0){ 
                error="No se pudo realizar la acción";
                return false;
            }
                    
        }
        catch(SQLException excSqlUpd){
            error=excSqlUpd.getMessage();
            return false;
        }
        catch(NullPointerException npExc){
            error="Verifique que haya conexión con el servidor!";
            return false;
        }
        return true;
    }
    
    /** Crea un nuevo registro de sesion de asesoria para tesista en la bd llamando al procedimiento almacenado newRgSesT
     * @param datos Los datos del nuevo registro en el orden: matricula (del alumno a que corresponde), clave de tesis, 
     *   fecha, tema, lugar, observaciones
     * @param considerar_aut Booleano que indica si se debe tomar en cuenta la solicitud de 
     *   autentificacion pidiendo password de seguridad (si la opcione esta establecida para operaciones insert)
     * @return true si el registro se creo correctamente (en cuyo caso la clave generada con el metodo obtenClave()) 
     *   false en caso contrario
     */
    public static boolean nuevoRgSesT(String[] datos, boolean considerar_aut){
        if(Conexion.autAltas&&considerar_aut)
            if(!Conexion.tienePermiso("Alta sesion de asesoria")){
                error="No tiene permiso para realizar la acción solicitada!";
                return false;
            }
        try{            
            String aux="?,";
            for(int j=0;j<datos.length;j++) aux+="?"+(j<datos.length-1?",":"");
            callProcedure = Conexion.conecBd.prepareCall("{call newRgSesT("+aux+")}");
            for(int j=0;j<datos.length;j++){
                try{
                    if(j==1) callProcedure.setInt(j+1, Integer.parseInt(datos[j]));
                    else callProcedure.setString(j+1, datos[j]);
                }
                catch(NumberFormatException nbfExc){
                    error=nbfExc.getMessage();
                    return false;
                }
            }
            callProcedure.registerOutParameter(datos.length+1, Types.INTEGER);
            callProcedure.executeUpdate();
            clave=Integer.parseInt(""+callProcedure.getObject(datos.length+1));
            callProcedure.close();
            if(clave==-1){ 
                error="No se pudo realizar la acción";
                return false;
            }
                    
        }
        catch(SQLException excSqlUpd){
            error=excSqlUpd.getMessage();
            return false;
        }
        catch(NullPointerException npExc){
            error="Verifique que haya conexión con el servidor!";
            return false;
        }
        return true;
    }
    
    /** Crea un nuevo registro de rubro de evaluacion para grupo en la bd llamando al procedimiento almacenado newRgRubCalif
     * @param datos Los datos del nuevo registro en el orden: tipo (1=Tarea, 2=Investigacion, 3=Exposicion, 4=Proyecto, 5=Practica,
     *   6=Examen parcial, 7=Examen final, 8=Examen ordinario, 9=Examen extraordinario,  10=Calificacion final, 11=Otro);
     *   descripcion, clave de grupo, fecha solicita, fecha califica
     * @param considerar_aut Booleano que indica si se debe tomar en cuenta la solicitud de 
     *   autentificacion pidiendo password de seguridad (si la opcione esta establecida para operaciones insert)
     * @return true si el registro se creo correctamente (en cuyo caso la clave generada con el metodo obtenClave()) 
     *   false en caso contrario
     */
    public static boolean newRgRubCalif(String[] datos, boolean considerar_aut){
        if(Conexion.autAltas&&considerar_aut)
            if(!Conexion.tienePermiso("Registro de rubro de evaluación")){
                error="No tiene permiso para realizar la acción solicitada!";
                return false;
            }
        try{            
            String aux="?,";
            for(int j=0;j<datos.length;j++) aux+="?"+(j<datos.length-1?",":"");
            callProcedure = Conexion.conecBd.prepareCall("{call newRgRubCalif("+aux+")}");
            for(int j=0;j<datos.length;j++){
                try{
                    if(j==0) callProcedure.setInt(j+1, Integer.parseInt(datos[j]));
                    else callProcedure.setString(j+1, datos[j]);
                }
                catch(NumberFormatException nbfExc){
                    error=nbfExc.getMessage();
                    return false;
                }
            }
            callProcedure.registerOutParameter(datos.length+1, Types.INTEGER);
            callProcedure.executeUpdate();
            clave=Integer.parseInt(""+callProcedure.getObject(datos.length+1));
            callProcedure.close();
            if(clave==0){ 
                error="No se pudo realizar la acción";
                return false;
            }
                    
        }
        catch(SQLException excSqlUpd){
            error=excSqlUpd.getMessage();
            return false;
        }
        catch(NullPointerException npExc){
            error="Verifique que haya conexión con el servidor!";
            return false;
        }
        return true;
    }
    
    /** Crea u obtiene (en caso de existir) la clave de relacion entre un 
     *   alumno y un grupo en la base de datos (tabla pertenece)
     * @param mat Matricula del alumno
     * @param clvgrupo Clave del grupo
     * @param considerar_aut Booleano que indica si se debe tomar en cuenta la solicitud de 
     *   autentificacion pidiendo password de seguridad (si la opcione esta establecida para operaciones insert)
     * @return true si la relacion se creo correctamente (en cuyo caso la clave generada con el metodo obtenClave()) 
     *   false en caso contrario
     */
    public static boolean agregaAluGrupo(String mat, String clvgrupo, boolean considerar_aut){
        if(Conexion.autCambs&&considerar_aut)
            if(!Conexion.tienePermiso("Agregar alumno a grupo")){
                error="No tiene permiso para realizar la acción solicitada!";
                return false;
            }
        try{            
            String aux="?,?,?";
            callProcedure = Conexion.conecBd.prepareCall("{call agAluGrupo("+aux+")}");
            callProcedure.setString(1,mat);
            callProcedure.setString(2,clvgrupo);
            callProcedure.registerOutParameter(3, Types.INTEGER);
            callProcedure.executeUpdate();
            clave=Integer.parseInt(""+callProcedure.getObject(3));
            callProcedure.close();            
            if(clave<=0){ 
                error=(clave==-2?"No existe el grupo":(clave==-1?"No existe el alumno":"El alumno ya esta en el grupo"));
                return false;
            }                    
        }
        catch(SQLException excSqlUpd){
            error=excSqlUpd.getMessage();
            return false;
        }
        catch(NullPointerException npExc){
            error="Verifique que haya conexión con el servidor!";            
            return false;
        }
        return true;
    }
    
    /** Crea un nuevo registro de sesion programada para grupo en la bd llamando al procedimiento almacenado nuevaSesion
     * @param datos Los datos del nuevo registro en el orden: fecha y hora, duracion (en minutos), plan, competencias,
     *   material, observaciones, clave de grupo
     * @param considerar_aut Booleano que indica si se debe tomar en cuenta la solicitud de 
     *   autentificacion pidiendo password de seguridad (si la opcione esta establecida para operaciones insert)
     * @return true si el registro se creo correctamente (en cuyo caso la clave generada con el metodo obtenClave()) 
     *   false en caso contrario
     */
    public static boolean nuevoRgSesProg(String[] datos, boolean considerar_aut){
        if(Conexion.autAltas&&considerar_aut)
            if(!Conexion.tienePermiso("Alta sesión para grupo")){
                error="No tiene permiso para realizar la acción solicitada!";
                return false;
            }
        try{            
            String aux="?,";
            for(int j=0;j<datos.length;j++) aux+="?"+(j<datos.length-1?",":"");
            callProcedure = Conexion.conecBd.prepareCall("{call nuevaSesion("+aux+")}");
            for(int j=0;j<datos.length;j++){
                try{
                    if(j==1) callProcedure.setInt(j+1, Integer.parseInt(datos[j]));                    
                    else callProcedure.setString(j+1, datos[j]);
                }
                catch(NumberFormatException nbfExc){
                    error=nbfExc.getMessage();
                    return false;
                }
            }
            callProcedure.registerOutParameter(datos.length+1, Types.INTEGER);
            callProcedure.executeUpdate();
            clave=Integer.parseInt(""+callProcedure.getObject(datos.length+1));
            callProcedure.close();
            if(clave==-1){ 
                error="No se pudo realizar la acción";
                return false;
            }
                    
        }
        catch(SQLException excSqlUpd){
            error=excSqlUpd.getMessage();
            return false;
        }
        catch(NullPointerException npExc){
            error="Verifique que haya conexión con el servidor!";
            return false;
        }
        return true;
    }
    
    /** Crea un nuevo registro de ficha bibliografica general en la bd llamando al procedimiento almacenado nuevaFichaBibGen
     * @param datos Los datos del nuevo registro en el orden: titulo, autor, clasificacion, prologista,
     *  compilador, traductor, numero de edicion, nombre del editor, fecha de edicion, lugar impresion, coleccion o serie,
     *  volumen o tomo, numero de paginas, referncia lugar consulta, resumen, palabras clave
     * @param considerar_aut Booleano que indica si se debe tomar en cuenta la solicitud de 
     *   autentificacion pidiendo password de seguridad (si la opcione esta establecida para operaciones insert)
     * @return true si el registro se creo correctamente (en cuyo caso la clave generada con el metodo obtenClave()) 
     *   false en caso contrario
     */
    public static boolean nuevaFichaBibGen(String[] datos, boolean considerar_aut){
        if(Conexion.autAltas&&considerar_aut)
            if(!Conexion.tienePermiso("Alta ficha bibliográfica")){
                error="No tiene permiso para realizar la acción solicitada!";
                return false;
            }
        try{            
            String aux="?,";
            for(int j=0;j<datos.length;j++) aux+="?"+(j<datos.length-1?",":"");
            callProcedure = Conexion.conecBd.prepareCall("{call nuevaFichaBibGen("+aux+")}");
            for(int j=0;j<datos.length;j++){ 
                if(j!=6&&j!=11&&j!=12) callProcedure.setString(j+1, datos[j]);
                else callProcedure.setInt(j+1, Integer.parseInt(datos[j]));
            }                
            callProcedure.registerOutParameter(datos.length+1, Types.INTEGER);
            callProcedure.executeUpdate();
            clave=Integer.parseInt(""+callProcedure.getObject(datos.length+1));
            callProcedure.close();
            if(clave==0){ 
                error="No se pudo realizar la acción";
                return false;
            }
                    
        }
        catch(SQLException excSqlUpd){
            error=excSqlUpd.getMessage();
            return false;
        }
        catch(NullPointerException npExc){
            error="Verifique que haya conexión con el servidor!";
            return false;
        }
        return true;
    }
    
    /** Crea un nuevo registro de ficha hemeroteca general en la bd llamando al procedimiento almacenado nuevaFichaHemGen
     * @param datos Los datos del nuevo registro en el orden: titulo, nombre del director, perioricidad (de la publicacion), 
     *   lugar de edicion, comentario, palabras clave
     * @param considerar_aut Booleano que indica si se debe tomar en cuenta la solicitud de 
     *   autentificacion pidiendo password de seguridad (si la opcione esta establecida para operaciones insert)
     * @return true si el registro se creo correctamente (en cuyo caso la clave generada con el metodo obtenClave()) 
     *   false en caso contrario
     */
    public static boolean nuevaFichaHemGen(String[] datos, boolean considerar_aut){
        if(Conexion.autAltas&&considerar_aut)
            if(!Conexion.tienePermiso("Alta ficha hemeroteca")){
                error="No tiene permiso para realizar la acción solicitada!";
                return false;
            }
        try{            
            String aux="?,";
            for(int j=0;j<datos.length;j++) aux+="?"+(j<datos.length-1?",":"");
            callProcedure = Conexion.conecBd.prepareCall("{call nuevaFichaHemGen("+aux+")}");
            for(int j=0;j<datos.length;j++) callProcedure.setString(j+1, datos[j]);
            callProcedure.registerOutParameter(datos.length+1, Types.INTEGER);
            callProcedure.executeUpdate();
            clave=Integer.parseInt(""+callProcedure.getObject(datos.length+1));
            callProcedure.close();
            if(clave==0){ 
                error="No se pudo realizar la acción";
                return false;
            }
                    
        }
        catch(SQLException excSqlUpd){
            error=excSqlUpd.getMessage();
            return false;
        }
        catch(NullPointerException npExc){
            error="Verifique que haya conexión con el servidor!";
            return false;
        }
        return true;
    }
    
    /** Crea un nuevo registro de ficha hemeroteca analitica en la bd llamando al procedimiento almacenado nuevaFichaHemGen
     * @param datos Los datos del nuevo registro en el orden: titulo, autor, clasificacion, nombre (de la publicacion), 
     *   paginas, numero (de la publicacion), fecha, anio de publicacion, numero de volumen/tomo, resumen
     * @param considerar_aut Booleano que indica si se debe tomar en cuenta la solicitud de 
     *   autentificacion pidiendo password de seguridad (si la opcione esta establecida para operaciones insert)
     * @return true si el registro se creo correctamente (en cuyo caso la clave generada con el metodo obtenClave()) 
     *   false en caso contrario
     */
    public static boolean nuevaFichaHemAn(String[] datos, boolean considerar_aut){
        if(Conexion.autAltas&&considerar_aut)
            if(!Conexion.tienePermiso("Alta ficha H analítica")){
                error="No tiene permiso para realizar la acción solicitada!";
                return false;
            }
        try{            
            String aux="?,";
            for(int j=0;j<datos.length;j++) aux+="?"+(j<datos.length-1?",":"");
            callProcedure = Conexion.conecBd.prepareCall("{call nuevaFichaHemAn("+aux+")}");
            for(int j=0;j<datos.length;j++){ 
                if(j!=8) callProcedure.setString(j+1, datos[j]);
                else callProcedure.setInt(j+1, Integer.parseInt(datos[j]));
            }
            callProcedure.registerOutParameter(datos.length+1, Types.INTEGER);
            callProcedure.executeUpdate();
            clave=Integer.parseInt(""+callProcedure.getObject(datos.length+1));
            callProcedure.close();
            if(clave==0){ 
                error="No se pudo realizar la acción";
                return false;
            }
                    
        }
        catch(SQLException excSqlUpd){
            error=excSqlUpd.getMessage();
            return false;
        }
        catch(NullPointerException npExc){
            error="Verifique que haya conexión con el servidor!";
            return false;
        }
        return true;
    }
    
    /** Crea un nuevo registro de tema (de materia) en la bd llamando al procedimiento almacenado nuevoTema
     * @param datos Los datos del nuevo registro en el orden: clave de la materia (a la que aplica), version, 
     *   orden, numero de tema, titulo de tema, contenido
     * @param considerar_aut Booleano que indica si se debe tomar en cuenta la solicitud de 
     *   autentificacion pidiendo password de seguridad (si la opcione esta establecida para operaciones insert)
     * @return true si el registro se creo correctamente (en cuyo caso la clave generada con el metodo obtenClave()) 
     *   false en caso contrario
     */
    public static boolean nuevoTema(String[] datos, boolean considerar_aut){
        if(Conexion.autAltas&&considerar_aut)
            if(!Conexion.tienePermiso("Alta nuevo Tema")){
                error="No tiene permiso para realizar la acción solicitada!";
                return false;
            }
        try{            
            String aux="?,";
            for(int j=0;j<datos.length;j++) aux+="?"+(j<datos.length-1?",":"");
            callProcedure = Conexion.conecBd.prepareCall("{call nuevoTema("+aux+")}");
            for(int j=0;j<datos.length;j++){ 
                if(j==1||j==2) callProcedure.setInt(j+1, Integer.parseInt(datos[j]));
                else callProcedure.setString(j+1, datos[j]);
            }
            callProcedure.registerOutParameter(datos.length+1, Types.INTEGER);
            callProcedure.executeUpdate();
            clave=Integer.parseInt(""+callProcedure.getObject(datos.length+1));
            callProcedure.close();
            if(clave==0){ 
                error="No se pudo realizar la acción";
                return false;
            }
                    
        }
        catch(SQLException excSqlUpd){
            error=excSqlUpd.getMessage();
            return false;
        }
        catch(NullPointerException npExc){
            error="Verifique que haya conexión con el servidor!";
            return false;
        }
        return true;
    }
    
    /** Crea un nuevo registro de objetivo para una sesion en la bd llamando al procedimiento almacenado nuevoObjetivo
     * @param clvSes Clave de la sesion
     * @param descr Descripcion del objetivo
     * @param considerar_aut Booleano que indica si se debe tomar en cuenta la solicitud de 
     *   autentificacion pidiendo password de seguridad (si la opcione esta establecida para operaciones insert)
     * @return true si el registro se creo correctamente (en cuyo caso la clave generada con el metodo obtenClave()) 
     *   false en caso contrario
     */
    public static boolean nuevoObjetivo(int clvSes, String descr, boolean considerar_aut){
        if(Conexion.autAltas&&considerar_aut)
            if(!Conexion.tienePermiso("Alta objetivo de sesión")){
                error="No tiene permiso para realizar la acción solicitada!";
                return false;
            }
        try{            
            callProcedure = Conexion.conecBd.prepareCall("{call nuevoObjetivo(?,?,?)}");
            callProcedure.setInt(1,clvSes);
            callProcedure.setString(2,descr);
            callProcedure.registerOutParameter(3,Types.INTEGER);
            callProcedure.executeUpdate();
            clave=Integer.parseInt(""+callProcedure.getObject(3));
            callProcedure.close();
            if(clave==0){ 
                error="No se pudo realizar la acción";
                return false;
            }
                    
        }
        catch(SQLException excSqlUpd){
            error=excSqlUpd.getMessage();
            return false;
        }
        catch(NullPointerException npExc){
            error="Verifique que haya conexión con el servidor!";
            return false;
        }
        return true;
    }
    
    /** Crea un nuevo registro de actividad por dia de semana para grupo, tesis o ciclo 
     *   en la bd llamando al procedimiento almacenado nuevaActivDia
     * @param cg Clave del grupo (si aplica a grupo null si no)
     * @param ct Clave del tesis (si aplica asesori de tesis 0 si no)
     * @param h1 Hora inicial
     * @param h2 Hora final
     * @param dia Dia de la semana por valor 0 a 5 (lunes a sabado)
     * @param activ Descripcion de la actividad
     * @param considerar_aut Booleano que indica si se debe tomar en cuenta la solicitud de 
     *   autentificacion pidiendo password de seguridad (si la opcione esta establecida para operaciones insert)
     * @return true si el registro se creo correctamente (en cuyo caso la clave generada con el metodo obtenClave()) 
     *   false en caso contrario
     */
    public static boolean nuevaActivDia(String cg,int ct,String h1,String h2,int dia,String activ,boolean considerar_aut){
        if(Conexion.autAltas&&considerar_aut)
            if(!Conexion.tienePermiso("Alta actividad en horario semanal")){
                error="No tiene permiso para realizar la acción solicitada!";
                return false;
            }
        try{            
            callProcedure = Conexion.conecBd.prepareCall("{call nuevaActivDia(?,?,?,?,?,?,?)}");
            callProcedure.setString(1,cg);
            callProcedure.setInt(2,ct);
            callProcedure.setString(3,h1);
            callProcedure.setString(4,h2);
            callProcedure.setInt(5,dia);
            callProcedure.setString(6,activ);
            callProcedure.registerOutParameter(7,Types.INTEGER);
            callProcedure.executeUpdate();
            clave=Integer.parseInt(""+callProcedure.getObject(7));
            callProcedure.close();
            if(clave==0){ 
                error="No se pudo realizar la acción";
                return false;
            }
                    
        }
        catch(SQLException excSqlUpd){
            error=excSqlUpd.getMessage();
            return false;
        }
        catch(NullPointerException npExc){
            error="Verifique que haya conexión con el servidor!";
            return false;
        }
        return true;
    }
        
    /** Crea un nuevo registro de resultado de un relacionado a un objetivo planeado
     *   en la bd llamando al procedimiento almacenado nuevoResultado
     * @param datos Los datos del nuevo registro en el orden: clave de objetivo o tema,
     *   si el anterior es clave de tema clave de sesion, si no 0, fecha, duracion, porcentaje cubierto
     * @param considerar_aut Booleano que indica si se debe tomar en cuenta la solicitud de 
     *   autentificacion pidiendo password de seguridad (si la opcione esta establecida para operaciones insert)
     * @return true si el registro se creo correctamente (en cuyo caso la clave generada con el metodo obtenClave()) 
     *   false en caso contrario
     */
    public static boolean nuevoResultado(String[] datos, boolean considerar_aut){
        if(Conexion.autAltas&&considerar_aut)
            if(!Conexion.tienePermiso("Alta nuevo avance")){
                error="No tiene permiso para realizar la acción solicitada!";
                return false;
            }
        try{            
            String aux="?,";
            for(int j=0;j<datos.length;j++) aux+="?"+(j<datos.length-1?",":"");
            callProcedure = Conexion.conecBd.prepareCall("{call nuevoResultado("+aux+")}");
            for(int j=0;j<datos.length;j++){ 
                if(j==2) callProcedure.setString(j+1, datos[j]);
                else callProcedure.setInt(j+1, Integer.parseInt(datos[j]));
            }
            callProcedure.registerOutParameter(datos.length+1, Types.INTEGER);
            callProcedure.executeUpdate();
            clave=Integer.parseInt(""+callProcedure.getObject(datos.length+1));
            callProcedure.close();
            if(clave==0){ 
                error="No se pudo realizar la acción";
                return false;
            }
                    
        }
        catch(SQLException excSqlUpd){
            error=excSqlUpd.getMessage();
            return false;
        }
        catch(NullPointerException npExc){
            error="Verifique que haya conexión con el servidor!";
            return false;
        }
        return true;
    }
    
    /** Cambia los datos de opciones de seguridad tabla datosseg en la bd 
     *   llamando al procedimiento almacenado cambiaPreSeg
     * @param datos Los nuevos datos en orden: altas (booleano), bajas (booleano),
     *   cambios (booleano), consultas (booleano), password de seguridad
     * @return true si el registro se actualizo correctamente false en caso contrario
     */
    public static boolean actuSelSeg(String[] datos){
        try{            
            String aux="?,";
            for(int j=0;j<datos.length;j++) aux+="?"+(j<datos.length-1?",":"");
            callProcedure = Conexion.conecBd.prepareCall("{call cambiaPreSeg("+aux+")}");
            for(int j=0;j<datos.length;j++){
                if(j<4) callProcedure.setBoolean(j+1, datos[j].equals("true"));
                else callProcedure.setString(j+1, datos[j]);
            }            
            callProcedure.registerOutParameter(datos.length+1, Types.INTEGER);
            callProcedure.executeUpdate();
            clave=Integer.parseInt(""+callProcedure.getObject(datos.length+1));
            callProcedure.close();
            if(clave==0){ 
                error="Password actual incorrecto";
                return false;
            }
                    
        }
        catch(SQLException excSqlUpd){
            error=excSqlUpd.getMessage();
            return false;
        }
        catch(NullPointerException npExc){
            error="Verifique que haya conexión con el servidor!";
            return false;
        }
        return true;
    }
    
    /** Ejecuta una sentencia para actualizar registros (update, delete) en la bd 
     * @param com La sentencia a ejecutar
     * @param isUpd Indica si es update, si no se considera delete
     * @param considerar_aut Booleano que indica si se debe tomar en cuenta la solicitud de 
     *   autentificacion pidiendo password de seguridad (si la opcione esta establecida para operaciones insert)
     * @return true si el registro se creo correctamente (en cuyo caso la clave generada con el metodo obtenClave()) 
     *   false en caso contrario
     */
    public static boolean actualiza(String com, boolean isUpd, boolean considerar_aut){
        if(((isUpd&&Conexion.autCambs)||(!isUpd&&Conexion.autBajas))&&considerar_aut)
            if(!Conexion.tienePermiso(isUpd?"Modificación de datos":"Eliminación de datos")){
                error="No tiene permiso para realizar la acción solicitada!";
                return false;
            }
        try{            
            sentencia = Conexion.conecBd.createStatement();
            sentencia.execute(com);            
        }
        catch(SQLException excSqlUpd){
            error=excSqlUpd.getMessage();
            return false;
        }
        catch(NullPointerException npExc){
            error="Verifique que haya conexión con el servidor!";
            return false;
        }
        return true;
    }
    
    /** Ejecuta una transaccion, en la base de datos 
     * @param comandos Lista de sentencias que incluye la transaccion
     * @param isUpd Indica si es update, si no se considera delete
     * @param considerar_aut Booleano que indica si se debe tomar en cuenta la solicitud de 
     *   autentificacion pidiendo password de seguridad (si la opcione esta establecida para operaciones insert)
     * @return true si el registro se creo correctamente (en cuyo caso la clave generada con el metodo obtenClave()) 
     *   false en caso contrario
     */
    public static boolean transaccion(java.util.ArrayList<String> comandos, boolean isUpd, boolean considerar_aut){
        if(((isUpd&&Conexion.autCambs)||(!isUpd&&Conexion.autBajas))&&considerar_aut)
            if(!Conexion.tienePermiso("Transacción (aplica a varios datos)")){
                error="No tiene permiso para realizar la acción solicitada!";
                return false;
            }
        try{
            sentencia = Conexion.conecBd.createStatement();
            sentencia.execute("start transaction;");
            for(int k=0;k<comandos.size();k++) sentencia.execute(comandos.get(k));
            sentencia.execute("commit;");
        }
        catch(SQLException excSqlUpd){
            error=excSqlUpd.getMessage();
            try{ sentencia.execute("rollback;"); }
            catch(SQLException excSqlUpd2){}
            return false;
        }
        catch(NullPointerException npExc){
            error="Verifique que haya conexión con el servidor!";
            try{ sentencia.execute("rollback;"); }
            catch(SQLException excSqlUpd2){}
            return false;
        }
        return true;
    }
    
    /** Obtiene la descripcion del ultimo error ocurrido en alguna operacion con 
     *   la base de datos desde los metodos de este paquete
     * @return la descripcion del ultimo error ocurrido en alguna operacion con 
     *   la base de datos desde los metodos de este paquete
     */
    public static String obtenError(){
        return error;
    }

    /** Obtiene la ultima clave generada en inserciones en registros con campo clave auto_increment
     * @return la ultima clave generada en inserciones en registros con campo clave auto_increment
     */
    public static int obtenClave(){
        return clave;
    }
}
