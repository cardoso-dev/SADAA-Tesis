/*
 * ImpTemas.java
 *  Definicion de datos para imprimir reporte de un temario de una materia
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

/** Crea un objeto que representa un registro de un tema para generar un reporte de temario
 * de una materia generar se utiliza para el reporte ListaTemas.jrxml
 * 
 * @author Pedro Cardoso Rodriguez
 */
public class ImpTemas {
    
    /**Numero de tema del registro*/
    public String num;
    /**Titulo del tema del registro*/
    public String tema;
    /**Contenidos del tema del registro*/
    public String cont;
    
    /** Crea un nuevo objeto ImpTemas
     * @param n Numero de tema del registro
     * @param t Titulo del tema del registro
     * @param c Contenidos del tema del registro
     */
    public ImpTemas(String n,String t,String c){
        num=n; tema=t; cont=c;
    }
    
    /** Obtiene el contenido de tema del registro
     * @return el contenido de tema del registro
     */
    public String getCONT(){ return cont; }
    /** Obtiene el titulo del tema del registro
     * @return el titulo del tema del registro
     */
    public String getTEMA(){ return tema; }
    /** Obtiene el numero de tema del registro
     * @return el numero de tema del registro
     */
    public String getNUM(){ return num; }

}
