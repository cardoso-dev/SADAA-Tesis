/*
 * RepsUnGrupo.java
 *  Crea el conjunto de reportes de un grupo registrado
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

import database.Consultas;
import java.awt.Color;
import java.util.ArrayList;
import operaciones.Tiempo;
import org.jfree.chart.ChartFactory;
import org.jfree.chart.ChartPanel;
import org.jfree.chart.JFreeChart;
import org.jfree.chart.plot.CategoryPlot;
import org.jfree.chart.plot.PlotOrientation;
import org.jfree.data.category.DefaultCategoryDataset;

/** Crea el conjunto de reportes de un grupo registrado
 * Los reportes que crea son:
 * 1-. Reporte de temas vistos
 * 2-. Reporte de gasto de tiempo en temas vistos
 * 3-. Reportes de calificacion de grupo
 * 4-. Reportes de objetivos cumplidos
 * 
 * @author Pedro Cardoso Rodriguez
 */
public class RepsUnGrupo {

    /** Lista de los reportes actuales */
    private ArrayList<Reporte> reportes;
    /**Descripcion del ultimo error ocurrido*/
    private String error;
    
    /** Crea un nuevo objeto RepsUnGrupo */
    public RepsUnGrupo(){ error=null; }
    
    /** Obtiene la descripcion del ultimo error ocurrido
     * @return la descripcion del ultimo error ocurrido
     */
    public String getError(){ return error; }
    
    /** Genera y obtiene la lista de reportes para un grupo
     * @param clvg La clave del grupo del cual generar los reportes
     * @return La lista de los reportes generados (null en caso de ocurrir un error) los cuales son:
     *   1-. Reporte de temas vistos
     *   2-. Reporte de gasto de tiempo en temas vistos
     *   3-. Reportes de calificacion de grupo
     *   4-. Reportes de objetivos cumplidos
     */
    public ArrayList<Reporte> getReportes(String clvg){
        Reporte repo;
        String[] verif=Consultas.consultaUnCampo("select count(*) from grupos where clvg='"+clvg+"';",true);
        if(verif==null){
            error=Consultas.obtenError();
            return null;
        }
        else if(verif[0].equals("0")){
            error="No existe grupo con clave "+clvg;
            return null;
        }
        reportes=new ArrayList<Reporte>();
        repo=generaRepTemas(clvg);
        if(repo==null) return null;
        reportes.add(repo);
        repo=generaRepTemasTiempo(clvg);
        if(repo==null) return null;
        reportes.add(repo);
        repo=generaRepCalifGrupo(clvg);
        if(repo==null) return null;
        reportes.add(repo);
        repo=generaRepObjetivos(clvg);
        if(repo==null) return null;
        reportes.add(repo);
        return reportes;
    }
    
    /** Genera y obtiene un reporte de temas vistos por un grupo
     * @param clvg La clave del grupo
     * @return reporte de temas vistos por un grupo con porcentaje visto por cada tema
     */
    private Reporte generaRepTemas(String clvg){
        Reporte reporte;
        JFreeChart grafica;
        ChartPanel panel;
        DefaultCategoryDataset datos;
        CategoryPlot plotCat;
        String[][] datosBd;
        int[] auxs=new int[3]; // donde indice indica: 
          //0=#temas vistos menos del 50%, 1=#temas vistos mas del 50% 2=#temas vistos al 100%
        double porcentaje;
        String mens;
        String sen="select vetema.*,resultados.*,numtem,tittem from sesiones,";
        sen+="progses,temario,vetema left join visto on (visto.clvtem=vetema.";
        sen+="clvtem and visto.clvses=vetema.clvses) left join resultados on ";
        sen+="(visto.clvres=resultados.clvres) where vetema.clvses=sesiones.";
        sen+="clvses and sesiones.clvses=progses.clvses and temario.clvtem=";
        sen+="vetema.clvtem and progses.clvg='"+clvg+"' order by vetema.clvses,vetema.clvtem;";
        datosBd=Consultas.consultaDatos(sen,true);
        if(datosBd==null){
            error=Consultas.obtenError();
            return null;
        }
        else if(datosBd[0][0]==null){
            reporte=new Reporte(null,"No existen datos para calcular este reporte","","Temas vistos");
            return reporte;
        }
        datos=new DefaultCategoryDataset();
        sen="\n\n***********************\nPorcentaje de cada tema";
        for(int j=0;j<datosBd.length;j++){
            porcentaje=0.0;
            if(datosBd[j][3]!=null){
                porcentaje=Double.parseDouble(datosBd[j][6]);
                while( j+1<datosBd.length && datosBd[j][1].equals(datosBd[j+1][1])
                        &&datosBd[j][0].equals(datosBd[j+1][0]) ){
                    porcentaje+=Double.parseDouble(datosBd[++j][6]);
                }
            }
            sen+="\n"+datosBd[j][7]+"- "+datosBd[j][8]+": "+porcentaje+"% visto";
            datos.addValue(porcentaje,(porcentaje<50?"Menos del 50%":porcentaje<100?"Mas del 50%":"El 100%"),datosBd[j][7]+"- "+datosBd[j][8]);
            auxs[(porcentaje<50?0:(porcentaje<100?1:2))]++;
        }
        mens="Reporte generado:"+Tiempo.getFecha()+"\nPorcentaje visto de cada tema en grupo "+clvg;
        mens+="\n"+(auxs[0]+auxs[1]+auxs[2])+" Temas a cubrir durante el curso\n"+auxs[0]+" Temas vistos menos del ";
        mens+="50%\n"+auxs[1]+" Temas vistos mas del 50%\n"+auxs[2]+" Temas vistos completamente"+sen;
        grafica = ChartFactory.createBarChart("","Tema","Porcentaje",datos,PlotOrientation.HORIZONTAL,false,true,true);
        plotCat=grafica.getCategoryPlot();
        plotCat.getRenderer().setSeriesPaint(0,new Color(0,0,255));
        plotCat.getRenderer().setSeriesPaint(1,new Color(0,255,0));
        plotCat.getRenderer().setSeriesPaint(2,new Color(255,0,0));
        panel = new ChartPanel(grafica);
        reporte=new Reporte(panel,mens,"Porcentaje visto por tema en curso "+clvg,"Temas vistos");
        return reporte;
    }
  
    /** Genera y obtiene un reporte de gasto por tema visto en un grupo
     * @param clvg La clave del grupo
     * @return reporte de gasto por tema visto en un grupo
     */
    private Reporte generaRepTemasTiempo(String clvg){
        Reporte reporte;
        JFreeChart grafica;
        ChartPanel panel;
        DefaultCategoryDataset datos;
        CategoryPlot plotCat;
        String[][] datosBd;
        int[] auxs=new int[2]; // donde indice indica: 
          //0=#temas vistos menos del 100%, 1=#temas vistos al 100%
        double porcentaje;
        double tiempo;
        String mens;
        String sen="select vetema.*,resultados.*,numtem,tittem from sesiones,";
        sen+="progses,temario,vetema left join visto on (visto.clvtem=vetema.";
        sen+="clvtem and visto.clvses=vetema.clvses) left join resultados on ";
        sen+="(visto.clvres=resultados.clvres) where vetema.clvses=sesiones.";
        sen+="clvses and sesiones.clvses=progses.clvses and temario.clvtem=";
        sen+="vetema.clvtem and progses.clvg='"+clvg+"' order by vetema.clvses,vetema.clvtem;";
        datosBd=Consultas.consultaDatos(sen,true);
        if(datosBd==null){
            error=Consultas.obtenError();
            return null;
        }
        else if(datosBd[0][0]==null){
            reporte=new Reporte(null,"No existen datos para calcular este reporte","","Tiempo por tema");
            return reporte;
        }
        datos=new DefaultCategoryDataset();
        sen="\n\n**********************\nMinutos de gasto por cada tema";
        for(int j=0;j<datosBd.length;j++){
            porcentaje=0.0;
            tiempo=0.0;
            if(datosBd[j][3]!=null){
                porcentaje=Double.parseDouble(datosBd[j][6]);
                tiempo=Double.parseDouble(datosBd[j][5]);
                while( j+1<datosBd.length && datosBd[j][1].equals(datosBd[j+1][1])
                        &&datosBd[j][0].equals(datosBd[j+1][0]) ){
                    porcentaje+=Double.parseDouble(datosBd[j+1][6]);
                    tiempo+=Double.parseDouble(datosBd[++j][5]);
                }
            }
            sen+="\n"+datosBd[j][7]+"- "+datosBd[j][8]+": "+tiempo+" minutos";
            datos.addValue(tiempo,(porcentaje<100?"Incompleto":"Completo"),datosBd[j][7]+"- "+datosBd[j][8]);
            auxs[(porcentaje<100?0:1)]++;
        }
        mens="Reporte generado:"+Tiempo.getFecha()+"\nGasto de tiempo por tema en grupo "+clvg;
        mens+="\n"+(auxs[0]+auxs[1])+" Temas a cubrir durante el curso\n"+auxs[0]+" Temas vistos parcialmente";
        mens+="\n"+auxs[1]+" Temas vistos completamente"+sen;
        grafica = ChartFactory.createBarChart("","Tema","Minutos",datos,PlotOrientation.HORIZONTAL,false,true,true);
        plotCat=grafica.getCategoryPlot();
        plotCat.getRenderer().setSeriesPaint(0,new Color(0,0,255));
        plotCat.getRenderer().setSeriesPaint(1,new Color(0,255,0));
        plotCat.getRenderer().setSeriesPaint(2,new Color(255,0,0));
        panel = new ChartPanel(grafica);
        reporte=new Reporte(panel,mens,"Gasto de tiempo por tema en curso "+clvg,"Tiempo por tema");
        return reporte;
    }
    
    /** Genera y obtiene un reporte de calificacion de un grupo para sus rubros de evaluacion
     * @param clvg La clave del grupo
     * @return reporte de calificacion de un grupo para sus rubros de evaluacion
     */
    private Reporte generaRepCalifGrupo(String clvg){
        Reporte reporte;
        JFreeChart grafica;
        ChartPanel panel;
        DefaultCategoryDataset datos;
        CategoryPlot plotCat;
        String[][] datosBd;
        int[] auxs=new int[3]; // donde indice indica:
          // 0=#rubros con menos del 7, 1=#rubros con mas del 7 2=#rubros con el 10
        double calif;
        String mens;
        String sen="select descripcion,avg(calif) from rubroscalif,realiza,";
        sen+="calificacon where rubroscalif.clvru=calificacon.clvru and realiza.";
        sen+="clvru=rubroscalif.clvru and calificacon.clvg='"+clvg+"' group by ";
        sen+="descripcion order by fchacal;";
        datosBd=Consultas.consultaDatos(sen,true);
        if(datosBd==null){
            error=Consultas.obtenError();
            return null;
        }
        else if(datosBd[0][0]==null){
            reporte=new Reporte(null,"No existen datos para calcular este reporte","","Calificacion de grupo");
            return reporte;
        }
        datos=new DefaultCategoryDataset();
        sen="\n\n**********************\nCalificacion de grupo por cada rubro";
        for(int j=0;j<datosBd.length;j++){
            calif=Double.parseDouble(datosBd[j][1]);
            sen+="\n"+datosBd[j][0]+": "+calif;
            datos.addValue(calif,(calif<7?"Menos de 7":calif<10?"Mas de 7":"Diez"),datosBd[j][0]);
            auxs[(calif<7?0:(calif<10?1:2))]++;
        }
        mens="Reporte generado:"+Tiempo.getFecha()+"\nCalificaion para cada rubro programado en grupo "+clvg;
        mens+="\n"+(auxs[0]+auxs[1]+auxs[2])+" Rubros programados para el curso\n"+auxs[0]+" Rubros con menos del ";
        mens+="7.0\n"+auxs[1]+" Rubros con mas del 7.0\n"+auxs[2]+" Rubros con el 10.0"+sen;
        grafica = ChartFactory.createBarChart("","Rubro","Calificacion",datos,PlotOrientation.HORIZONTAL,false,true,true);
        plotCat=grafica.getCategoryPlot();
        plotCat.getRenderer().setSeriesPaint(0,new Color(0,0,255));
        plotCat.getRenderer().setSeriesPaint(1,new Color(0,255,0));
        plotCat.getRenderer().setSeriesPaint(2,new Color(255,0,0));
        panel = new ChartPanel(grafica);
        reporte=new Reporte(panel,mens,"Calificacion por rubro para grupo "+clvg,"Calificacion de grupo");
        return reporte;
    }
    
    /** Genera y obtiene un reporte de objetivos cumplidos para un grupo
     * @param clvg La clave del grupo
     * @return reporte de objetivos cumplidos para un grupo
     */
    private Reporte generaRepObjetivos(String clvg){
        Reporte reporte;
        JFreeChart grafica;
        ChartPanel panel;
        DefaultCategoryDataset datos;
        String[][] datosBd;
        int[] auxs=new int[3];
        double porcentaje;
        String mens;
        String sen="select objetivos.*,resultados.* from busca,progses,objetivos ";
        sen+="left join obtiene on objetivos.clvobj=obtiene.clvobj left join ";
        sen+="resultados on resultados.clvres=obtiene.clvres where progses.";
        sen+="clvses=busca.clvses and objetivos.clvobj=busca.clvobj and ";
        sen+="clvg='"+clvg+"' order by objetivos.clvobj;";
        datosBd=Consultas.consultaDatos(sen,true);
        if(datosBd==null){
            error=Consultas.obtenError();
            return null;
        }
        else if(datosBd[0][0]==null){
            reporte=new Reporte(null,"No existen datos para calcular este reporte","","Avance en objetivos");
            return reporte;
        }
        datos=new DefaultCategoryDataset();
        sen="\n\n***********************\nPorcentaje por cada objetivo";
        for(int j=0;j<datosBd.length;j++){
            porcentaje=0.0;
            if(datosBd[j][2]!=null){
                porcentaje=Double.parseDouble(datosBd[j][5]);
                while(j+1<datosBd.length && datosBd[j][0].equals(datosBd[j+1][0])){
                    porcentaje+=Double.parseDouble(datosBd[++j][5]);
                }
            }
            sen+="\n"+datosBd[j][1]+": "+porcentaje+"%";
            datos.addValue(porcentaje,(porcentaje<50?"Menos del 50%":porcentaje<100?"Mas del 50%":"El 100%"),datosBd[j][1]);
            auxs[(porcentaje<50?0:(porcentaje<100?1:2))]++;
        }
        mens="Reporte generado:"+Tiempo.getFecha()+"\nPorcentaje de avance en objetivos planeados para grupo "+clvg;
        mens+="\n"+(auxs[0]+auxs[1]+auxs[2])+" Objetivos planeados para el grupo\n"+auxs[0]+" Objetivos con menos del ";
        mens+="50% de avance\n"+auxs[1]+" Objetivos con mas del 50% de avance\n"+auxs[2]+" Objetivos cumplidos al 100"+sen;
        grafica = ChartFactory.createBarChart("","Objetivo","Porcentaje",datos,PlotOrientation.HORIZONTAL,false,true,true);
        panel = new ChartPanel(grafica);
        reporte=new Reporte(panel,mens,"Porcentaje de avance en objetivos planeados grupo "+clvg,"Avance en objetivos");
        return reporte;
    }
    
}
