/*
 * ImpHorarioSem.java
 *  Definicion de datos para imprimir reporte horario semanal
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

/** Crea un objeto que representa una semana con los campos requeridos para 
 * generar un reporte de horario semanal de un grupo, asesorias o general
 * se utiliza para el reporte HorarioSem.jrxml
 * 
 * @author Pedro Cardoso Rodriguez
 */
public class ImpHorarioSem {
   
    /**La hora que aplica este registro es una representacion que indica la hora 
     *   desde y hasta en forma 00:00 00:00*/
    public String hora;
    /**La actividad para el dia lunes para hora del registro null si no hay ninguna*/
    public String lunes;
    /**La actividad para el dia martes para hora del registro null si no hay ninguna*/
    public String martes;
    /**La actividad para el dia miercoles para hora del registro null si no hay ninguna*/
    public String miercoles;
    /**La actividad para el dia jueves para hora del registro null si no hay ninguna*/
    public String jueves;
    /**La actividad para el dia viernes para hora del registro null si no hay ninguna*/
    public String viernes;
    /**La actividad para el dia sabado para hora del registro null si no hay ninguna*/
    public String sabado;
    
    /** Crea un nuevo objeto ImpHorarioSem
     * @param h La hora que aplica este registro es una representacion que indica la hora desde y hasta en forma 00:00 00:00
     * @param l La actividad para el dia lunes para hora del registro null si no hay ninguna
     * @param ma La actividad para el dia martes para hora del registro null si no hay ninguna
     * @param mi La actividad para el dia miercoles para hora del registro null si no hay ninguna
     * @param j La actividad para el dia jueves para hora del registro null si no hay ninguna
     * @param v La actividad para el dia viernes para hora del registro null si no hay ninguna
     * @param s La actividad para el dia sabado para hora del registro null si no hay ninguna
     */
    public ImpHorarioSem(String h,String l,String ma,String mi,String j,String v,String s){
        hora=h;
        lunes=l;
        martes=ma;
        miercoles=mi;
        jueves=j;
        viernes=v;
        sabado=s;
    }    
    
    /** Obtiene la hora que aplica a este registro
     * @return la hora que aplica a este registro
     */
    public String getHORA(){ return hora; }
    /** Obtiene la actividad para el lunes que aplica a la hora del registro
     * @return la actividad para el lunes que aplica a la hora del registro
     */
    public String getLUNES(){ return lunes; }
    /** Obtiene la actividad para el martes que aplica a la hora del registro
     * @return la actividad para el martes que aplica a la hora del registro
     */
    public String getMARTES(){ return martes; }
    /** Obtiene la actividad para el miercoles que aplica a la hora del registro
     * @return la actividad para el miercoles que aplica a la hora del registro
     */
    public String getMIERCOLES(){ return miercoles; }
    /** Obtiene la actividad para el jueves que aplica a la hora del registro
     * @return la actividad para el jueves que aplica a la hora del registro
     */
    public String getJUEVES(){ return jueves; }
    /** Obtiene la actividad para el viernes que aplica a la hora del registro
     * @return la actividad para el viernes que aplica a la hora del registro
     */
    public String getVIERNES(){ return viernes; }
    /** Obtiene la actividad para el sabado que aplica a la hora del registro
     * @return la actividad para el sabado que aplica a la hora del registro
     */
    public String getSABADO(){ return sabado; }
}
