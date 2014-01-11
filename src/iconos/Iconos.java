/*
 * Iconos.java
 *  Clase con metodos estaticos para obtener un icono por su nombre existente en el paquete iconos
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

package iconos;

/** Clase para obtener iconos del paquete iconos
 * @author Pedro CardosoRodriguez
 */
public class Iconos {

    /** Obtiene un ImageIcon existente en este paquete
     * @param nombre El nombre del icono a obtener
     * @return el icono obtenido
     */
    public static javax.swing.ImageIcon getIcono(String nombre){
        Iconos iTemp=new Iconos();
        return iTemp.getIt(nombre);
    }
    
    /** Obtiene un ImageIcon existente en este paquete
     * @param nom El nombre del icono a obtener
     * @return el icono obtenido
     */
    private javax.swing.ImageIcon getIt(String nom){
        javax.swing.ImageIcon icon=null;
        icon=new javax.swing.ImageIcon(getClass().getResource(nom));
        return icon;
    }
}
