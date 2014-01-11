/*
 * Archivos.java
 *  Proporciona metodos para realizar operaciones sobre archivos
 * Parte de proyecto: SADAA
 * Author: Pedro Cardoso Rodriguez
 * Mail: ingpedro@live.com
 * Place: Zacatecas Mexico
 * 
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

package operaciones;

/** Proporciona metodos para realizar operaciones sobre archivos
 * @author Pedro Cardoso Rodríguez
 */
public class Archivos {
    
    /** La descripcion del ultimo error ocurrido*/
    private static String error;

    /** Lee un archivo formato binario con contenido en formato de n cadenas 
     * @param nomAr Nombre del archivo a leer
     * @param nCads Numero de cadenas a leer
     * @return el arreglo de las cadenas leidas si hay un error regresa null, si 
     *   el archivo no existe regresa una sola cadena null
     */
    public static String[] leeBin(String nomAr, int nCads){
        java.io.File ar = new java.io.File(nomAr);
        boolean bandera=true;
        String[] cont;
        if(ar.isFile()){
            cont = new String[nCads];
            try{
                java.io.FileInputStream arSrc = new java.io.FileInputStream(nomAr);
                java.io.ObjectInputStream dtSrc = new java.io.ObjectInputStream(arSrc);
                for(int t=0;t<nCads;t++) cont[t] = (String)dtSrc.readObject();
                dtSrc.close();         
            }
            catch(java.io.IOException ioExc){ error=ioExc.getMessage(); bandera=false; }
            catch(ClassNotFoundException cnfExc){ error=cnfExc.getMessage(); bandera=false; }
            catch(NullPointerException npExc){ error=npExc.getMessage(); bandera=false; }
            if(!bandera) return null;            
            return cont;
        }
        else{
            cont = new String[1];
            cont[0]=null;
            return cont;
        }
    }
    
    /** Escribe un archivo formato binario con contenido en formato de n cadenas 
     *   si el archivo no existe lo crea de lo contrario lo sobreescribe
     * @param nomAr El nombre del archivo a escribir
     * @param cads Las cadenas a escribir
     * @return true si la operacion se realizo con exito, false caso contrario
     */
    public static boolean escribeBin(String nomAr, String[] cads){
        java.io.File ar;
        java.io.ObjectOutputStream artOut;
        try{
            ar = new java.io.File(nomAr);
            artOut = new java.io.ObjectOutputStream(new java.io.FileOutputStream(ar));
            for(int g=0;g<cads.length;g++) artOut.writeObject(cads[g]);
            artOut.close();
            return true;
        }
        catch(java.io.IOException ioExc){
            error=ioExc.getMessage();
            return false;
        }            
    }
    
    /** Obtiene la descripcion del ultimo error ocurrido
     * @return la descripcion del ultimo error ocurrido
     */
    public static String obtenError(){ return error; }
}
