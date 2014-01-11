/*
 * ImpRegTesis.java
 *  Definicion de datos para imprimir reporte de tesis de un alumno
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
 * generar un reporte de calendario de sesiones de asesorias a un alumno tesista
 * se utiliza para el reporte FichaTesista.jrxml
 * @author Pedro Cardoso Rodriguez
 */
public class ImpRegTesis {
    
    /**La clave de la sesion del registro*/
    private String clv;
    /**La fecha y hora de la sesion del registro*/
    private String fchahora;
    /**El tema de la sesion del registro*/
    private String tema;
    /**El lugar de la sesion del registro*/
    private String lugar;
    
    /** Crea un nuevo objeto ImpRegTesis
     * @param cl La clave de la sesion del registro
     * @param fh La fecha y hora de la sesion del registro
     * @param tm El tema de la sesion del registro
     * @param lg El lugar de la sesion del registro
     */
    public ImpRegTesis(String cl,String fh,String tm,String lg){
        clv=cl; fchahora=fh; tema=tm; lugar=lg;
    }
    
    /** Obtiene la clave de la sesion del registro
     * @return la clave de la sesion del registro
     */
    public String getCLV(){ return clv; }
    /** Obtiene la fecha y hora de la sesion del registro
     * @return la fecha y hora de la sesion del registro
     */
    public String getFCHAHORA(){ return fchahora; }
    /** Obtiene el tema de la sesion del registro
     * @return el tema de la sesion del registro
     */
    public String getTEMA(){ return tema; }
    /** Obtiene el lugar de la sesion del registro
     * @return el lugar de la sesion del registro
     */
    public String getLUGAR(){ return lugar; }

}
