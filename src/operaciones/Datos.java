/*
 * Datos.java
 *   Encargado de operaciones sobre datos
 *   Todos los metodos son static
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

package operaciones;

import java.util.regex.Matcher;
import java.util.regex.Pattern;

/** Proporciona metodos, todos son static,  para realizar operaciones sobre datos
 * @author Pedro Cardoso Rodríguez
 */
public class Datos {
  
    /**Para realizar operaciones con regex*/
    private static Matcher matcher;
    
    /** Cambia el formato de una fecha recibida como parametro el cambio es del 
     *   formato yyyy-mm-dd : dd-mm-yyyy y viceversa (para interaccion con el servidor que maneja el formato yyyy-mm-dd) 
     * @param fcha La fecha original
     * @return la fecha con el cambio de formato
     */
    public static String transformatFcha(String fcha){
        java.util.StringTokenizer toks = new java.util.StringTokenizer(fcha,"-");
        String tk1 = toks.nextToken();
        String tk2 = toks.nextToken();
        String tk3 = toks.nextToken();
        return tk3+"-"+tk2+"-"+tk1;
    } 
    
    /** Obtiene el numero de mes de una cadena con formato de fecha yyyy-mm-dd o dd-mm-yyyy 
     * @param fcha la fecha a procesar
     * @return El numero de mes de la fecha
     */
    public static int obtenMesDeFecha(String fcha){
        java.util.StringTokenizer toks = new java.util.StringTokenizer(fcha,"-");
        String tk = toks.nextToken();
        tk = toks.nextToken();
        return Integer.parseInt(tk);
    }
    
    /** Transforma dos fechas (formato dd-mm-yyyy) a una descripcion de periodo 
     * @param fcha1 la fecha inicial del rango
     * @param fcha2 la fecha final del rango
     * @return la descripcion del periodo entre las dos fechas
     */
    public static String transFechasDescPer(String fcha1, String fcha2){
        String[] meses={"ENE","FEB","MAR","ABR","MAY","JUN","JUL","AGO","SEP","OCT","NOV","DIC"};
        String per;
        java.util.StringTokenizer toks = new java.util.StringTokenizer(fcha1,"-");
        toks.nextToken();
        per=meses[Integer.parseInt(toks.nextToken())-1]+toks.nextToken().substring(2)+"-";
        toks = new java.util.StringTokenizer(fcha2,"-");
        toks.nextToken();
        per+=meses[Integer.parseInt(toks.nextToken())-1]+toks.nextToken().substring(2);
        return per;
    }
    
    /** Compara dos fechas en formato dd-mm-aaaa para ver cual sucede antes
     * @param fecha1 La 1ra fecha a comparar
     * @param fecha2 La 2da fecha a comparar
     * @return -1 si fecha1 es menor a fecha2; 1 si fecha2 es menor a fecha1; 0 si son iguales 
     */
    public static int compareFecha(String fecha1, String fecha2){
        java.util.StringTokenizer toks = new java.util.StringTokenizer(fecha1,"-");
        int dia1=Integer.parseInt(toks.nextToken());
        int mes1=Integer.parseInt(toks.nextToken());
        int year1=Integer.parseInt(toks.nextToken());
        toks = new java.util.StringTokenizer(fecha2,"-");
        int dia2=Integer.parseInt(toks.nextToken());
        int mes2=Integer.parseInt(toks.nextToken());
        int year2=Integer.parseInt(toks.nextToken());
        if(year1<year2) return -1;
        else if(year1>year2) return 1;
        else if(mes1<mes2) return -1;
        else if(mes1>mes2) return 1;
        else if(dia1<dia2) return -1;
        else if(dia1>dia2) return 1;
        return 0;
    }
    
    /** Cambia el formato de una fecha el cambio es del 
     *   formato yyyy-mm-dd : dd-mm-yyyy y viceversa (para interaccion con el servidor 
     *   que maneja el formato yyyy-mm-dd)
     * @param fchahor La fecha a transformar
     * @param quitaSegundos A la parte de la hora (tipo datetime) le quita los segundos
     *   (cadena :00)si el parametro quitaSegundos es true, de lo contrario se los agrega
     * @return la fecha transformada
     */
    public static String transformatFchaHora(String fchahor, boolean quitaSegundos){
        java.util.StringTokenizer toks = new java.util.StringTokenizer(fchahor," ");
        java.util.StringTokenizer toksFcha = new java.util.StringTokenizer(toks.nextToken(),"-");
        String tk1 = toksFcha.nextToken();
        String tk2 = toksFcha.nextToken();
        String tk3 = toksFcha.nextToken();
        String hora=toks.nextToken();
        fchahor=tk3+"-"+tk2+"-"+tk1+" ";
        if(quitaSegundos){
            toks = new java.util.StringTokenizer(hora,":");
            fchahor+=toks.nextToken()+":"+toks.nextToken();
        }
        else fchahor+=hora+":00";
        return fchahor;
    } 
    
    /** valida una cadena como matricula debe tener 8 caracteres todos digitos
     * @param mat la cadena a validar
     * @return true si mat es valida false en caso contrario
     */
    public static boolean valMatricula(String mat){
        Pattern ptrMat  = Pattern.compile("[0-9]{8}");
        matcher = ptrMat.matcher(mat);
        return matcher.matches();
    }

    /** valida una fecha que tenga el formato dd-mm-aaaa
     * @param fcha la fecha a validar 
     * @return true si la fecha tiene formato valido false caso contrario
     */
    public static boolean valFecha(String fcha){
        Pattern ptrFecha  = Pattern.compile("[0-9]{2}-[0-9]{2}-[0-9]{4}");
        matcher = ptrFecha.matcher(fcha);
        return matcher.matches();
    }
    
    /** Valida una cadena que tenag formato de direccion de correo electronico
     * @param mail la cadena a validar
     * @return true si la cadena es valida false en caso contrario
     */
    public static boolean valMail(String mail){
        Pattern ptrMail  = Pattern.compile("([a-zA-Z0-9_-]+){1}(\\.[a-zA-Z0-9_-]+)*@([a-zA-Z0-9]+){1}(\\.[a-zA-Z0-9]+)*");
        matcher = ptrMail.matcher(mail);
        return matcher.matches();
    }

    /** Valida que una cadena que sea un numero telefonico de diez digitos (lada + numero)
     * @param tel la cadena a validar
     * @return true si lña cadena tiene formato correcto false en caso contrario
     */
    public static boolean valTel(String tel){
        Pattern ptrTel  = Pattern.compile("[0-9]{0,10}");
        matcher = ptrTel.matcher(tel);
        return matcher.matches();
    }
    
    /** Valida que una cadena tenga el formato de un periodo mmmaa-mmmaa (3 letras del mes 2 digitos finales del anyo)
     * @param per al cadena a validar
     * @return true si la cadena tiene formato valido false en caso contrario
     */
    public static boolean valPeriodo(String per){
        Pattern ptrPed  = Pattern.compile("((ENE)|(FEB)|(MAR)|(ABR)|(MAY)|(JUN)|(JUL)|(AGO)|(SEP)|(OCT)|(NOV)|(DIC))[0-9]{2}-((ENE)|(FEB)|(MAR)|(ABR)|(MAY)|(JUN)|(JUL)|(AGO)|(SEP)|(OCT)|(NOV)|(DIC))[0-9]{2}");
        matcher = ptrPed.matcher(per);
        return matcher.matches();
    }
    
    /** Valida rangos de horarios que esten en formato de 24 horas (ejemplo 02:23-03:57)
     * @param hor el horario a validar
     * @return true si el horario es valido false en caso contrario
     */
    public static boolean valHorario(String hor){
        Pattern ptrPhr  = Pattern.compile("(([0-1][0-9])|(2[0-3])):([0-5][0-9])-(([0-1][0-9])|(2[0-3])):([0-5][0-9])");
        matcher = ptrPhr.matcher(hor);
        return matcher.matches();
    }
    
    /** Valida horarios simples (ejemplo 14:30)
     * @param hor el horario a validar
     * @return true si el horario es valido false en caso contrario
     */
    public static boolean valHorarioSimple(String hor){
        Pattern ptrPhr  = Pattern.compile("(([0-1][0-9])|(2[0-3])):([0-5][0-9])");
        matcher = ptrPhr.matcher(hor);
        return matcher.matches();
    }
    
    /** Valida que sea una direccion ip valida
     * @param dirIp la direccion a validar
     * @return true si al ip es valida false en caso contrario
     */
    public static boolean vaDirlIP(String dirIp){
        Pattern ptrIp  = Pattern.compile("[0-9]{1,3}\\.[0-9]{1,3}\\.[0-9]{1,3}\\.[0-9]{1,3}");
        matcher = ptrIp.matcher(dirIp);
        return matcher.matches();
    }
    
    /** Obtiene la clave de una materia de una cadena con formato clave de grupo
     * @param clvmat cadena con formato clave de grupo
     * @return la clave de una materia de una cadena con formato clave de grupo
     */
    public static String obtenClvMat(String clvmat){
        java.util.StringTokenizer toks = new java.util.StringTokenizer(clvmat,"+");
        toks.nextToken();
        return toks.nextToken();
    }
    
    /** Valida una cadena que solo contenga caracteres de numeracion romana
     * @param year cadena a validar
     * @return true si la cadena es valida false en caso contrario
     */
    public static boolean valRomanYear(String year){
        Pattern ptrMat  = Pattern.compile("[IVXLCDM]*");
        matcher = ptrMat.matcher(year);
        return matcher.matches();
    }
 
    /** Forma una condicion para un where de una sentencia sql
     * @param campo el campo (columna) de la tabla en la cual se condiciona
     * @param val el valor a buscar (sera divido en tokens si se usa operador like)
     * @param opLike booleano que indica si se debe usar operador like
     * @return la condicion envuelta es sus propios parentesis 
     *   lista para usar en un where de consulta sql
     */
    public static String formaCondicion(String campo, String val, boolean opLike){
        String condicion;
        java.util.StringTokenizer tokens = new java.util.StringTokenizer(val);
        if(opLike){
            condicion="("+campo;
            while(tokens.hasMoreTokens()){
                condicion+=" like '%"+tokens.nextToken()+"%'";
                if(tokens.hasMoreTokens()) condicion+=" or "+campo;
            }
            condicion+=")";
        }
        else condicion="("+campo+"='"+val+"')";
        return condicion;
    }

}
