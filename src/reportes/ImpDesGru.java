/*
 * ImpDesGru.java
 *  Definicion de datos para imprimir reporte de desempeño en grupo de un alumno
 * Parte de proyecto: SADAA
 * Author: Pedro Cardoso Rdz
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

/** Crea un objeto que representa el desmpenio de un rubro de evaluacion  por un alumno
 *  con los campos requeridos para  generar un reporte de desempenio en grupo
 *  se utiliza para el reporte FichaDesGru.jrxml
 * 
 * @author Pedro Cardoso Rodriguez
 */
public class ImpDesGru {
    
    /**La clave del registro*/
    public String clv;
    /**El rubro de evaluacion*/
    public String rubro;
    /**La fecha de calificacion*/
    public String fchacal;
    /**La calificacion obtenida por el alumno*/
    public String calif;
    
    /** Crea un nuevo ImpDesGru(
     * @param cl La clave del registro
     * @param rb El rubro de evaluacion
     * @param fc La fecha de calificacion
     * @param cal La calificacion obtenida por el alumno
     */
    public ImpDesGru(String cl,String rb,String fc,String cal){
        clv=cl; rubro=rb; fchacal=fc; calif=cal;
    }
    
    /** Obtiene la clave del registro
     * @return la clave del registro
     */
    public String getCLV(){ return clv; }
    /** Obtiene el rubro de evaluacion
     * @return el rubro de evaluacion
     */
    public String getRUBRO(){ return rubro; }
    /** Obtiene la fecha de calificacion
     * @return la fecha de calificacion
     */
    public String getFCHACAL(){ return fchacal; }
    /** Obtiene la calificacion del alumno en el registro
     * @return la calificacion del alumno en el registro
     */
    public String getCALIF(){ return calif; }

}
