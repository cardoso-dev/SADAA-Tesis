/*
 * ImpCalGrupo.java
 *  Definicion de datos para imprimir reporte de calendario de sesiones de un grupo
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

/** Crea un objeto que representa una sesion con los campos requeridos para 
 * generar un reporte de calendario de sesiones de un grupo 
 * se utiliza para el reporte CalSesionesGrp.jrxml
 * 
 * @author Pedro Cardoso Rodriguez
 */
public class ImpCalGrupo {
   
    /**La fecha de la sesion*/
    private String fecha;
    /**La hora de las sesion*/
    private String hora;
    /**El plan de la sesion*/
    private String plan;
    /**El tema de la sesion*/
    private String tema;
    /**Competencia de la sesion*/
    private String competencia;
    /**Material a usar para la sesion*/
    private String material;
    
    /** Crea un nuevo objeto ImpCalGrupo con los campos de una sesion
     * @param fh La fecha de la sesion
     * @param hr La hora de las sesion
     * @param pl El plan de la sesion
     * @param tm El tema de la sesion
     * @param cmp Competencia de la sesion
     * @param mat Material a usar para la sesion
     */
    public ImpCalGrupo(String fh, String hr, String pl, String tm, String cmp, String mat){
        fecha=fh; hora=hr; plan=pl; tema=tm; competencia=cmp; material=mat;
    }
    
    /** Obtiene la fecha de la sesion
     * @return la fecha de la sesio
     */
    public String getFECHA(){ return fecha; }
    /** Obtiene la hora de la sesion
     * @return la hora de la sesion
     */
    public String getHORA(){ return hora; }
    /** Obtiene el plan de la sesion 
     * @return el plan de la sesio
     */
    public String getPLAN(){ return plan; }
    /** Obtiene el tema de la sesion
     * @return el tema de la sesion
     */
    public String getTEMA(){ return tema; }
    /** Obtiene la competencia de la sesion
     * @return la competencia de la sesion
     */
    public String getCOMPETENCIA(){ return competencia; }
    /** Obtiene descripcion del material de la sesion
     * @return descripcion del material de la sesion
     */
    public String getMATERIAL(){ return material; }

}
