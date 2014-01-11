/*
 * TipoMensaje.java
 *   Enum que indica los tipos de mensajes usado por el sistema al enviar un dialog al usuario
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

/** Enum que indica los tipos de mensajes usado por el sistema al enviar un dialog al usuario
 * @author Pedro Cardoso Rodriguez
 */
public enum TipoMensaje{
    
    /**Tipo error para mensajes de error*/
    ERROR (javax.swing.JOptionPane.ERROR_MESSAGE),
    /**Tipo informacion para mensajes informativos*/
    INFORMACION (javax.swing.JOptionPane.INFORMATION_MESSAGE);
    /**Indica el tipo actual para una referencia en el momento que cree un objeto*/
    private final int tipo;

    /** Crea un nuevo objeto de TipoMensaje
     * @param tpMens El valor del tipo segun las opciones de este enum
     */
    private TipoMensaje(int tpMens){
        tipo=tpMens;
    }
    
    /** Obtiene el tipo de mensaje actual
     * @return el tipo de mensaje actual
     */
    public int getTipo(){
        return tipo;
    }
}
