/*
 * Conexion.java
 *   Encargado de conectar al servidor de la base de datos
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

/**  Clase que proporciona metodos (static) para realizar y revisar la conexion con la base da datos.
 * 
 * @author Pedro Cardoso Rodríguez
 */
public class Conexion {

    /**Establece y mantiene la conexion con la base de datos*/
    public static Connection conecBd;
    /**Ip del host en el cual se encuentra el servidor de bd*/
    private static String ip = null;
    /**Numero de puerto del servidor mysql con el cual hay que conectarse*/
    private static int puerto = 0;  
    /**Usuario a utilizar para conectarse a la base de datos*/
    private static String user = null;
    /**Password a utilizar para conectarse a la base de datos*/
    private static String pass = null;
    /**Descripcion del ultimo error ocurrido*/
    private static String error = null;
    /**Indica si hay que realizar autentificacion al hacer operacion de altas de registros*/
    public static boolean autAltas;
    /**Indica si hay que realizar autentificacion al hacer operacion de bajass de registros*/
    public static boolean autBajas;
    /**Indica si hay que realizar autentificacion al hacer operacion de cambios/updates de registros*/
    public static boolean autCambs;
    /**Indica si hay que realizar autentificacion al hacer consultas de registros*/
    public static boolean autCons;
    /**Referencia a la ventana principal del sistema*/
    private static sistema.FramePrincipal refPrinc=null;
    
    /** Establece los parametros de conexion al servidor de bases de datos
     * @param parms los parametros de conexion al servidor de bases de datos
     *   en orde: IP, puerto, usuario, password
     */
    public static void estableceParms(String[] parms){
        ip=parms[0];
        puerto=Integer.parseInt(parms[1]);
        user=parms[2];
        pass=parms[3];
    }
    
    /** Obtiene los parametros de conexion al servidor de bases de datos
     * @return los parametros de conexion al servidor de bases de datos
     *   en orde: IP, puerto, usuario, password
     */
    public static String[] obtenParms(){
        String[] pr = new String[3];
        pr[0]=ip;
        pr[1]=""+puerto;
        pr[2]=user;
        return pr;
    }
    
    /** Establece la conexion al servidor de la base de datos (de acuerdo a los parametros actuales)
     * @return true si la conexion se establecio con exito false en caso contrario
     */
    public static boolean conecta(){
        try{
            DriverManager.registerDriver(new org.gjt.mm.mysql.Driver());
            /* se tuvo que especificar el parametro: noAccessToProcedureBodies=true
             * para poder utilizar procedimientos almacenados
             */
            conecBd = DriverManager.getConnection("jdbc:mysql://"+ip+":"+puerto+"/Docente?noAccessToProcedureBodies=true",user,pass);
        }
        catch(SQLException excSql){
            error=""+excSql.getMessage();
            return false;
        }
        cargaPrefSeg();
        return true;
    }
    
    /** Establece la referencia la la ventana principal del sistema
     * @param rp la referencia la la ventana principal del sistema
     */
    public static void setRefPrinc(sistema.FramePrincipal rp){
        refPrinc=rp;
    }
 
    /** Carga desde la bd las opciones de seguridad establecidas
     */
    public static void cargaPrefSeg(){
        String[] datosper=database.Consultas.consultaUnCampo("select Altas,Bajas,Cambios,Consultas from DatosSeg;",false);
        if(datosper!=null){
            autAltas=datosper[0].equals("true");
            autBajas=datosper[1].equals("true");
            autCambs=datosper[2].equals("true");
            autCons=datosper[3].equals("true");
        }
        else{
            autAltas=autBajas=autCambs=autCons=false;
        }
    }
    
    /** Pide el password de seguridad a traves de un dialogo al usuario (establecido para realizar acciones sobre registros)
     * @param acc Descripcion de la accion que se pretende realizar
     * @return true si el usuario autentifico correctamente el password de seguridad false en caso contrario
     */
    public static boolean tienePermiso(String acc){
        String pss=refPrinc.pidePssSeg(acc);
        String[] cmp=database.Consultas.consultaUnCampo("select * from datosseg where pass=md5('"+pss+"')",false);
        if(cmp==null || cmp[0]==null) return false;
        return true;
    }
   
    /** Cierra la conexion con el servidor de la base de datos
     */
    public static void desconecta(){
        try{ conecBd.close(); }
        catch(SQLException excSql){ error=""+excSql.getMessage(); }
        catch(NullPointerException excNullPE){ error=""+excNullPE.getMessage(); }
    }
    
    /** Indica si hay conexion actualemnte con la base de datos
     * @return true si hay conexion actualemnte con la base de datos 
     *   false en caso contrario
     */
    public static boolean hayConexion(){
        try{ return !conecBd.isClosed(); }
        catch(SQLException excSql){ return false; }
        catch(NullPointerException excNullPE){ return false; }
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
