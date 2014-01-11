/*
 * Consultas.java
 *   Encargado de hacer consultas al servidor de la base de datos
 *   Todos los metodos son static
 * Parte de proyecto: SADAA
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
import java.sql.Types;
import javax.swing.table.DefaultTableModel;
import operaciones.Datos;

/**  Clase que proporciona metodos (static) para realizar consultas en la base de datos.
 * 
 * @author Pedro Cardoso Rodríguez
 */
public class Consultas {
    
    /**Para enviar a ejecuta sentencias sql en la bd*/
    private static Statement sentencia;
    /**Guarda el resultado de una consulta realizada*/
    private static ResultSet resp;
    /**Guarda los metadatos de un resultset de una consulta realizada*/
    private static ResultSetMetaData respMetaData;
    /**Descripcion del ultimo error ocurrido*/
    private static String error="";
    
    /** Realiza una consulta y regresa el resulset convertido a un arreglo de string donde los 
     *   campos son concatenados y cada fila del resultset es un elemento del arreglo
     * @param parm Sentencia sql de la consulta a realizar
     * @param considerar_aut Booleano que indica si se debe tomar en cuenta la solicitud de 
     *   autentificacion pidiendo password de seguridad (si la opcione esta establecida para consultas)
     * @return La lista obtenida en la consulta, null si hubo error o una lista de un solo elemento null si no hubo resultados
     */
    public static String[] consultaLista(String parm, Boolean considerar_aut){
        String[] resultado = new String[1];        
        java.util.ArrayList<String> lista = new java.util.ArrayList<String>();
        resultado[0]=null;
        int columnas=0;
        int tipo;
        if(Conexion.autCons&&considerar_aut)
            if(!Conexion.tienePermiso("Consulta de datos")){
                error="No tiene permiso para realizar la acción solicitada!";
                return null;
            }
        try{
            sentencia = Conexion.conecBd.createStatement();
            resp = sentencia.executeQuery(parm);
            respMetaData = resp.getMetaData();
            columnas=respMetaData.getColumnCount();
            while(resp.next()){
                resultado[0]="";
                for(int j=0;j<columnas;j++){
                    tipo=respMetaData.getColumnType(j+1);
                    if(resp.getObject(j+1)==null){ resultado[0]+="null "; continue; }
                    if(tipo==Types.VARCHAR || tipo==Types.CHAR)
                        resultado[0] += resp.getString(j+1)+" ";
                    else if(tipo==Types.DATE)
                        resultado[0] += Datos.transformatFcha(resp.getDate(j+1).toString())+" ";
                    else if(tipo==Types.DECIMAL)
                        resultado[0] += ""+resp.getFloat(j+1)+" ";
                    else if(tipo==Types.TINYINT || tipo==Types.SMALLINT || tipo==Types.INTEGER)
                        resultado[0] += ""+resp.getInt(j+1)+" ";
                    else if(tipo==Types.TIMESTAMP)
                        resultado[0] += Datos.transformatFchaHora(resp.getTimestamp(j+1).toString(),true)+" ";
                    else if(tipo==Types.BIT)
                        resultado[0] += resp.getBoolean(j+1);
                    else resultado[j] = ""+resp.getObject(j+1);
                }                     
                lista.add(resultado[0]);
            }
            if(lista.size()>0){
                resultado = new String[lista.size()];
                for(int j=0;j<lista.size();j++) resultado[j]=lista.get(j);
            }
            resp.close();
            sentencia.close();
        }
        catch(SQLException excSqlUpd){
            error=excSqlUpd.getMessage();
            return null;
        }
        catch(NullPointerException npExc){
            error="Verifique la conexión con el servidor!";
            return null;
        }
        return resultado;
    }
    
    /** Realiza una consulta y regresa el resulset convertido a un array de string que equivale a la tabla obtenida
     * @param parm Sentencia sql de la consulta a realizar
     * @param considerar_aut Booleano que indica si se debe tomar en cuenta la solicitud de 
     *   autentificacion pidiendo password de seguridad (si la opcione esta establecida para consultas)
     * @return La tabla obtenida en la consulta, null si hubo error o un array de 1x1 donde el unico elemento es null si no hubo resultados
     */
    public static String[][] consultaDatos(String parm, Boolean considerar_aut){
        String[][] resultado = new String[1][1];
        resultado[0][0]=null;
        java.util.ArrayList<String[]> lista=new java.util.ArrayList<String[]>();
        int columnas=0;
        int tipo;
        if(Conexion.autCons&&considerar_aut)
            if(!Conexion.tienePermiso("Consulta de datos")){
                error="No tiene permiso para realizar la acción solicitada!";
                return null;
            }
        try{
            sentencia = Conexion.conecBd.createStatement();
            resp = sentencia.executeQuery(parm);
            respMetaData = resp.getMetaData();
            columnas=respMetaData.getColumnCount();
            while(resp.next()){
                resultado=new String[1][columnas];
                for(int j=0;j<columnas;j++){
                    tipo=respMetaData.getColumnType(j+1);
                    if(resp.getObject(j+1)==null)
                        resultado[0][j]= null;
                    else if(tipo==Types.VARCHAR || tipo==Types.CHAR)
                        resultado[0][j]= resp.getString(j+1);
                    else if(tipo==Types.DATE)
                        resultado[0][j]= Datos.transformatFcha(resp.getDate(j+1).toString());
                    else if(tipo==Types.DECIMAL)
                        resultado[0][j]= ""+resp.getFloat(j+1);
                    else if(tipo==Types.TINYINT || tipo==Types.SMALLINT || tipo==Types.INTEGER)
                        resultado[0][j]=""+resp.getInt(j+1);
                    else if(tipo==Types.TIMESTAMP)
                        resultado[0][j]= Datos.transformatFchaHora(resp.getTimestamp(j+1).toString(),true);
                    else if(tipo==Types.BIT)
                        resultado[0][j]=""+resp.getBoolean(j+1);
                    else resultado[j][j]=""+resp.getObject(j+1);
                }                     
                lista.add(resultado[0]);
            }
            if(lista.size()>0){
                resultado = new String[lista.size()][columnas];
                for(int j=0;j<lista.size();j++) resultado[j]=lista.get(j);
            }
            resp.close();
            sentencia.close();
        }
        catch(SQLException excSqlUpd){
            error=excSqlUpd.getMessage();
            return null;
        }
        catch(NullPointerException npExc){
            error="Verifique la conexión con el servidor!";
            return null;
        }
        return resultado;
    }
    
    /** Realiza una consulta y regresa el resulset convertido a un arreglo de enteros 
     *   la sentencia sql debe estar hecha para que cada fila del resultado debe ser un solo campo tipo entero
     * @param parm Sentencia sql de la consulta a realizar
     * @param considerar_aut Booleano que indica si se debe tomar en cuenta la solicitud de 
     *   autentificacion pidiendo password de seguridad (si la opcione esta establecida para consultas)
     * @return Si hay un error regresa null, si no hay resultados regresa un solo entero valor -1
     */
    public static int[] consultaEnteros(String parm, Boolean considerar_aut){
        int[] resultado = new int[1];
        java.util.ArrayList<Integer> lista = new java.util.ArrayList<Integer>();
        resultado[0]=-1;
        if(Conexion.autCons&&considerar_aut)
            if(!Conexion.tienePermiso("Consulta de datos")){
                error="No tiene permiso para realizar la acción solicitada!";
                return null;
            }
        try{
            sentencia = Conexion.conecBd.createStatement();
            resp = sentencia.executeQuery(parm);
            respMetaData = resp.getMetaData();
            while(resp.next()){
                resultado[0]=resp.getInt(1);
                lista.add(resultado[0]);
            }
            if(lista.size()>0){
                resultado = new int[lista.size()];
                for(int j=0;j<lista.size();j++) resultado[j]=lista.get(j);
            }
            resp.close();
            sentencia.close();
        }
        catch(SQLException excSqlUpd){
            error=excSqlUpd.getMessage();
            return null;
        }
        catch(NullPointerException npExc){
            error="Verifique la conexión con el servidor!";
            return null;
        }
        return resultado;
    }
    
    /** Realiza una consulta y regresa el resulset convertido a un arreglo de string donde los
     *   campos son los elementos del arreglo (pensado para consultas de un solo registro por medio del campo llave) 
     * @param parm Sentencia sql de la consulta a realizar
     * @param considerar_aut Booleano que indica si se debe tomar en cuenta la solicitud de 
     *   autentificacion pidiendo password de seguridad (si la opcione esta establecida para consultas)
     * @return La lista obtenida en la consulta, null si hubo error o una lista de un solo elemento null si no hubo resultados
     */
    public static String[] consultaUnCampo(String parm,boolean considerar_aut){
        String[] resultado = new String[1];
        int columnas=0;
        int tipo;
        resultado[0]=null;
        if(Conexion.autCons&&considerar_aut)
            if(!Conexion.tienePermiso("Consulta de datos")){
                error="No tiene permiso para realizar la acción solicitada!";
                return null;
            }
        try{
            sentencia = Conexion.conecBd.createStatement();
            resp = sentencia.executeQuery(parm);
            respMetaData = resp.getMetaData();
            columnas=respMetaData.getColumnCount();            
            if(resp.next()){
                resultado = new String[columnas];
                for(int j=0;j<columnas;j++){
                    tipo=respMetaData.getColumnType(j+1);
                    if(resp.getObject(j+1)==null){ resultado[j]=null; continue; }
                    if(tipo==Types.VARCHAR || tipo==Types.CHAR)
                        resultado[j] = resp.getString(j+1);
                    else if(tipo==Types.DATE)
                        resultado[j] = Datos.transformatFcha(resp.getDate(j+1).toString());
                    else if(tipo==Types.DECIMAL)
                        resultado[j] = ""+resp.getFloat(j+1);
                    else if(tipo==Types.TINYINT || tipo==Types.SMALLINT || tipo==Types.INTEGER)
                        resultado[j] = ""+resp.getInt(j+1);
                    else if(tipo==Types.TIMESTAMP)
                        resultado[j] = Datos.transformatFchaHora(resp.getTimestamp(j+1).toString(),true);
                    else if(tipo==Types.BIT)
                        resultado[j] = ""+resp.getBoolean(j+1);
                    else resultado[j] = ""+resp.getObject(j+1);
                }                     
            }
            resp.close();
            sentencia.close();
        }
        catch(SQLException excSqlUpd){
            error=excSqlUpd.getMessage();
            return null;
        }
        catch(NullPointerException npExc){
            error="Verifique la conexión con el servidor!";
            return null;
        }
        return resultado;
    }
    
    /** Realiza una consulta y regresa el resulset cargado en un DefaultTableModel 
     *   listo para cargar en un jtable
     * @param cons Sentencia sql de la consulta a realizar
     * @param considerar_aut Booleano que indica si se debe tomar en cuenta la solicitud de 
     *   autentificacion pidiendo password de seguridad (si la opcione esta establecida para consultas)
     * @return los resultados obtenidos en la consulta cargados en el defaultTableModel o null si hubo error
     */
    public static DefaultTableModel consTipoTable(String cons, boolean considerar_aut){
        int cols;
        int tipo;
        Object[] fila;
        if(Conexion.autCons&&considerar_aut)
            if(!Conexion.tienePermiso("Consulta de datos")){
                error="No tiene permiso para realizar la acción solicitada!";
                return null;
            }
        try{
            DefaultTableModel modeloTab = new DefaultTableModel();
            sentencia = Conexion.conecBd.createStatement();
            resp = sentencia.executeQuery(cons);
            respMetaData = resp.getMetaData();
            cols = respMetaData.getColumnCount();
            fila = new Object[cols];
            for(int i=0;i<cols;i++) fila[i] = respMetaData.getColumnLabel(i+1);
            modeloTab.setColumnIdentifiers(fila);
            while(modeloTab.getRowCount()>0) modeloTab.removeRow(0);
            while(resp.next()){
                for(int j=0;j<cols;j++){
                    tipo=respMetaData.getColumnType(j+1);
                    if(resp.getObject(j+1)==null){ fila[j]=null; continue; }
                    if(tipo==Types.VARCHAR || tipo==Types.CHAR)
                        fila[j] = resp.getString(j+1);
                    else if(tipo==Types.DATE)
                        fila[j] = Datos.transformatFcha(resp.getDate(j+1).toString());
                    else if(tipo==Types.DECIMAL)
                        fila[j] = resp.getFloat(j+1);
                    else if(tipo==Types.TINYINT  || tipo==Types.SMALLINT || tipo==Types.INTEGER)
                        fila[j] = resp.getInt(j+1);
                    else if(tipo==Types.TIMESTAMP )
                        fila[j] = Datos.transformatFchaHora(resp.getTimestamp(j+1).toString(),true);
                    else if(tipo==Types.BIT)
                        fila[j] = resp.getBoolean(j+1);
                    else fila[j] = ""+resp.getObject(j+1);
                }
                modeloTab.addRow(fila);
            }
            resp.close();
            sentencia.close();
            error=null;
            return modeloTab;
        }
        catch(SQLException excSqlCons){
            error = excSqlCons.getMessage();
            return null;
        }
        catch(NullPointerException npExc){
            error="Verifique la conexión con el servidor!";
            return null;
        }
    }
    
    /** Consultar un campo blob (para utilizar en la foto de alumno)
     * @param cons Sentencia sql de la consulta a realizar
     * @return El valor del campo obtenido en la consulta o si la consulta no regresa 
     *   nada o hay error regresa null (para diferenciar los casos el string error sera null cuando la consulta regreso vacia) 
     */
    public static byte[] consultaBts(String cons){
        try{
            byte[] datos;
            sentencia = Conexion.conecBd.createStatement();
            resp = sentencia.executeQuery(cons);
            if(resp.next()) datos=resp.getBytes(1);
            else datos=null;
            resp.close();
            sentencia.close();
            error=null;
            return datos;
        }
        catch(SQLException excSqlCons){
            error = excSqlCons.getMessage();
            return null;
        }
        catch(NullPointerException npExc){
            error="Verifique la conexión con el servidor!";
            return null;
        }
    }

    /** Obtiene la descripcion del ultimo error ocurrido en alguna operacion con 
     *   la base de datos desde los metodos de este paquete
     * @return la descripcion del ultimo error ocurrido en alguna operacion con 
     *   la base de datos desde los metodos de este paquete
     */
    public static String obtenError(){
        return error;
    } 
}
