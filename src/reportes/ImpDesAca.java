/*
 * ImpDesAca.java
 *  Definicion de datos para imprimir reporte de desempeño academico de un alumno
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

package reportes;

/** Crea un objeto que representa un registro con los campos requeridos para 
 * generar un reporte de desempenio academico de un alumno
 * se utiliza para el reporte FichaDesAca.jrxml
 * @author Pedro Cardoso Rodriguez
 */
public class ImpDesAca {
    
    /**La clave del registro*/
    private String clv;
    /**La materia del registro*/
    private String materia;
    /**El grupo del registro*/
    private String grupo;
    /**La calificacion del registro*/
    private String calif;
    
    /** Crea un nuevo objeto ImpDesAca
     * @param cl La clave del registro
     * @param mate La materia del registro
     * @param grup El grupo del registro
     * @param cal La calificacion del registro
     */
    public ImpDesAca(String cl,String mate,String grup,String cal){
        clv=cl; materia=mate; grupo=grup; calif=cal;
    }
    
    /** Obtiene la clave del registro
     * @return la clave del registro
     */
    public String getCLV(){ return clv; }
    /** Obtiene la materia del registro
     * @return la materia del registro
     */
    public String getMATERIA(){ return materia; }
    /** Obtiene el grupo del registro
     * @return el grupo del registro
     */
    public String getGRUPO(){ return grupo; }
    /** Obtiene la calificacion del registro
     * @return la calificacion del registro
     */
    public String getCALIF(){ return calif; }

}
