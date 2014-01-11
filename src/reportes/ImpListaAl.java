/*
 * ImpListaAl.java
 *  Definicion de datos para imprimir reporte con lista de alumnos o lista de creditos
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
 * generar un reporte de lista de alumnos, lista de calificacion o de porcentaje de asistencias
 * se utiliza para el reporte ListaAlumnos.jrxml o con ListaCreditos.jrxml
 * 
 * @author Pedro Cardoso Rodriguez
 */
public class ImpListaAl {
    /**La matricula del alumno del registro*/
    private String matricula;
    /**El nombre del alumno del registro*/
    private String alumno;
    /**El numero del alumno en la lista de alumnos*/
    private String numero;
    /**El credito obtenido por el alumno (puede porcentaje en caso de tratarse de lista de asistencias)*/
    private String calificacion;
    
    /** Crea un nuevo objeto ImpListaAl
     * @param mat La matricula del alumno del registro
     * @param al El nombre del alumno del registro
     * @param num El numero del alumno en la lista de alumnos
     * @param cal El credito obtenido por el alumno (puede porcentaje en caso de tratarse de lista de asistencias)
     */
    public ImpListaAl(String mat, String al,int num, String cal){
        matricula=mat; alumno=al; numero=""+num; calificacion=cal;
    }
    
    /** Obtiene la matricula del alumno
     * @return la matricula del alumno
     */
    public String getMATRICULA(){ return matricula; }
    /** Obtiene el nombre del alumno
     * @return el nombre del alumno
     */
    public String getALUMNO(){ return alumno; }
    /** Obtiene el numero del alumno en la lista de alumnos
     * @return el numero del alumno en la lista de alumnos
     */
    public String getNUMERO(){ return numero; }
    /** Obtiene la calificacion del alumno
     * @return la calificacion del alumno
     */
    public String getCALIFICACION(){ return calificacion; }
}
