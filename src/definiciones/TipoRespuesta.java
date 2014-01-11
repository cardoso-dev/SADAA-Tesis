/*
 * TipoRespuesta.java
 *   Enum que indica los tipos de respuesta usado por el sistema al enviar un dialog 
      con alguna peticion al usuario
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

package definiciones;

/** Enum que indica los tipos de respuesta usado por el sistema al enviar un dialog 
      con alguna peticion al usuario
 * @author Pedro Cardoso Rodriguez
 */
public enum TipoRespuesta {

    /**Tipo rechazar cuando el usuario rechaza una peticion*/
    RECHAZAR (-1),
    /**Tipo cancelar cuando el usuario cancela una peticion*/
    CANCELAR (0),
    /**Tipo aceptar cuando el usuario acepta una peticion*/
    ACEPTAR (1);
    /**Indica el tipo actual para una referencia en el momento que cree un objeto*/
    private final int tipo;
    
    /** Crea un nuevo objeto de TipoRespuesta
     * @param tp El valor del tipo segun las opciones de este enum
     */
    private TipoRespuesta(int tp){
        tipo=tp;
    }
    
    /** Obtiene el tipo de respuesta actual
     * @return el tipo de respuesta actual
     */
    public int getTipo(){
        return tipo;
    }

}
