/*
 * Tiempo.java
 *   Encargado de datos y proceso usando el tiempo/fecha desde el so
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
import java.util.*;

/** Encargado de datos y proceso usando el tiempo/fecha desde el so
 * @author Pedro Cardoso Rodríguez
 */
public class Tiempo {
 
    /**Referencia a calendario gregoriano*/
   private static GregorianCalendar calendario;
   /** Inicializa el calendario */
   static{ calendario = new GregorianCalendar(); }

   /** Obtiene una descripcion larga de la fecha actual en formato
    *  dia, DD de mes del ANIO donde DD son los digitos del dia y ANIO los del anio
    * @return una descripcion larga de la fecha actual
    */
  public static String getFecha(){
    int dia = calendario.get(Calendar.DAY_OF_WEEK)-1;
    int mes = calendario.get(Calendar.MONTH);
    String[] diaNom={"Domingo","Lunes","Martes","Miercoles","Jueves","Viernes","Sabado"};
    String[] mesNom={"Enero","Febrero","Marzo","Abril","Mayo","Junio","Julio","Agosto","Septiembre","Octubre","Noviembre","Diciembre"};
    String fechaAct = " "+diaNom[dia]+", "+(calendario.get(Calendar.DAY_OF_MONTH))+" de ";
    fechaAct += mesNom[mes]+" del "+calendario.get(Calendar.YEAR);
    return fechaAct;
  }

  /** Una descripcion de la fecha actual en formato AAAA-MM-DD
   * @return descripcion de la fecha actual en formato AAAA-MM-DD
   */
  public static String getFechaAMD(){
    int dia = calendario.get(Calendar.DAY_OF_MONTH);
    int mes = calendario.get(Calendar.MONTH)+1;
    String fechaAct=calendario.get(Calendar.YEAR)+"-"+(mes<10?"0":"")+mes+"-"+(dia<10?"0":"")+dia;
    return fechaAct;
  }
  
  /** Una descripcion de la fecha actual en formato DD-MM-AAAA
   * @return descripcion de la fecha actual en formato DD-MM-AAAA
   */
  public static String getFechaFormatoNums(){
    int dia = calendario.get(Calendar.DAY_OF_MONTH);
    int mes = calendario.get(Calendar.MONTH)+1;
    return (dia<9?"0":"")+dia+(mes<9?"-0":"-")+mes+"-"+calendario.get(Calendar.YEAR);
  }
  
  /** Obtiene el dia actual
   * @return el dia actual
   */
  public static int getDia(){ return calendario.get(Calendar.DAY_OF_MONTH); }
 
  /** Obtiene el mes actual
   * @return el mes actual
   */
  public static int getMes(){ return calendario.get(Calendar.MONTH)+1; }
  
  /** Obtiene el anio actual
   * @return el anio actual
   */
  public static int getAnyo(){ return calendario.get(Calendar.YEAR); }

  /** Obtiene la descripcion de la hora actual en formato: HH:MM:SS AM o PM
   * @return la descripcion de la hora actual en formato: HH:MM:SS AM o PM
   */
  public static String getHora(){
    calendario.setTime(new Date());
    int h = calendario.get(Calendar.HOUR);
    int m = calendario.get(Calendar.MINUTE);
    int s = calendario.get(Calendar.SECOND);
    int apm = calendario.get(Calendar.AM_PM);
    h=(h==0?12:h);
    return (" "+(h<10?"0":"")+h+":"+(m<10?"0":"")+m+":"+(s<10?"0":"")+s+" "+(apm==0?"AM ":"PM "));
  }
  
  /** Obtiene una descripcion de la hora en formato: HHMMSS
   * @return una descripcion de la hora en formato: HHMMSS
   */
  public static String getHoraNoFormato(){
    calendario.setTime(new Date());
    int h = calendario.get(Calendar.HOUR);
    int m = calendario.get(Calendar.MINUTE);
    int s = calendario.get(Calendar.SECOND);
    return ((h<10?"0":"")+h+(m<10?"0":"")+m+(s<10?"0":"")+s);
  }

  /** Obtiene las fechas comprendidas entre un rango de dos fechas que ocurren en un determinado dia de la semana
   * @param fechaIni fecha inicial en formato dd-mm-aaaa debe ser anterior a fechaIni
   * @param fechaFin fecha final en formato dd-mm-aaaa debe ser posterior a fechaIni
   * @param diaSema dia de la semana de lunes a sabado indicado por un entero desde 0 a 5
   * @return Las fechas correspondientes (formato aaaa-mm-dd) a un determinado dia de la semana que ocurren entre el 
   *   rango comprendido desde fechaIni hasta fechaFin
   */
  public static ArrayList<String> getFechasEnDiaSema(String fechaIni,String fechaFin,int diaSema){
      ArrayList<String> fechas=new ArrayList<String>();
      ArrayList<String> aux=new ArrayList<String>();
      int dia1=Integer.parseInt(fechaIni.substring(0,2));
      int mes1=Integer.parseInt(fechaIni.substring(3,5));
      int year1=Integer.parseInt(fechaIni.substring(6));
      int dia2=Integer.parseInt(fechaFin.substring(0,2));
      int mes2=Integer.parseInt(fechaFin.substring(3,5));
      int year2=Integer.parseInt(fechaFin.substring(6));
      do{
          aux=getFechasEnDiaSema(mes1,diaSema,dia1,(mes1==mes2&&year1==year2?dia2:-1),year1);
          for(String str:aux){
              fechas.add(str);
          }
          dia1=1; mes1++;
          if(mes1>12){ mes1=1; year1++; }
      }while(year1<year2||mes1<=mes2);
      return fechas;
  }
  
  /** Obtiene las fechas comprendidas dentro de un rango de dias en un mes que ocurren en un determinado dia de la semana
   * @param mes El numero de mes del 1 al 12
   * @param diaSema el dia de la semana del 0 al 5 indica lunes a sabado
   * @param desdeDia dia inicial del mes a considerar
   * @param hastaDia dia final del mes a considerar o -1 para todos los dias del mes
   * @param year anyo del mes
   * @return lista de fechas en formato aaaa-mm-dd
   */
  private static ArrayList<String> getFechasEnDiaSema(int mes,int diaSema,int desdeDia,int hastaDia,int year){
      GregorianCalendar caltemp = new GregorianCalendar(year,mes-1,desdeDia);
      ArrayList<String> dias=new ArrayList<String>();
      int[] diasMes={31,28,31,30,31,30,31,31,30,31,30,31};  
      int aux=desdeDia;
      if(caltemp.isLeapYear(year)) diasMes[1]=29;
      if(hastaDia==-1) hastaDia=diasMes[mes-1];
      while(caltemp.get(Calendar.DAY_OF_WEEK)!=diaSema+2){
          aux++;
          caltemp.set(year,mes-1,aux);
      }
      do{
          caltemp.set(year,mes-1,aux);
          desdeDia=caltemp.get(Calendar.DATE);
          dias.add(""+year+"-"+(mes<10?"0":"")+mes+"-"+(desdeDia<10?"0":"")+desdeDia);
          aux+=7;
      }while(aux<=hastaDia);
      return dias;
  }
  
}
