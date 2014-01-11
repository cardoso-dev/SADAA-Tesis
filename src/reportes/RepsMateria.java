/*
 * RepsMateria.java
 *  Crea el conjunto de reportes de una materia registrada
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
import org.jfree.data.general.DefaultPieDataset;

/** Crea el conjunto de reportes de una materia registrada
 * Los reportes que crea son: 
 * 1-. Reporte de alumnos con calificacion mas alto-bajo obtenida (sobre los que han cursado la materia)
 * 2-. Reporte con el porcentaje de alumnos (sobre los que han cursado la materia) que han aprobado-reprobado-repetido curso
 * 
 * @author Pedro Cardoso Rodriguez
 */
public class RepsMateria {

    /** Lista de los reportes actuales */
    private ArrayList<Reporte> reportes;
    /**Descripcion del ultimo error ocurrido*/
    private String error;
    
    /** Crea un nuevo objeto RepsMateria */
    public RepsMateria(){ error=null; }
    
    /** Obtiene la descripcion del ultimo error ocurrido
     * @return la descripcion del ultimo error ocurrido
     */
    public String getError(){ return error; }
    
    /** Genera y obtiene la lista de reportes para una materia
     * @param clvm La clave de la materia de la cual de desea generar los reportes
     * @return La lista de los reportes generados (null en caso de ocurrir un error) los cuales son: 
     *   1-. Reporte de alumnos con calificacion mas alto-bajo obtenida (sobre los que han cursado la materia)
     *   2-. Reporte con el porcentaje de alumnos (sobre los que han cursado la materia) que han aprobado-reprobado-repetido curso
     */
    public ArrayList<Reporte> getReportes(String clvm){
        Reporte repo;
        String[] verif=Consultas.consultaUnCampo("select count(*) from materias where clvm='"+clvm+"';",true);
        if(verif==null){
            error=Consultas.obtenError();
            return null;
        }
        else if(verif[0].equals("0")){
            error="No existe materia con clave "+clvm;
            return null;
        }
        reportes=new ArrayList<Reporte>();
        repo=generaRepDesemAlums(clvm);
        if(repo==null) return null;
        reportes.add(repo);
        repo=generaRepAproAlums(clvm);
        if(repo==null) return null;
        reportes.add(repo);
        return reportes;
    }
    
    /** Genera y obtiene un reporte de desempenio de alumnos en una materia
     * @param clvm La clave de la materia del reporte a generar
     * @return un reporte de alumnos con calificacion mas alto-
     *  bajo obtenida (sobre los que han cursado la materia) o null si hay error
     */
    private Reporte generaRepDesemAlums(String clvm){
        Reporte reporte;
        JFreeChart grafica;
        ChartPanel panel;
        DefaultCategoryDataset datos;
        CategoryPlot plotCat;
        String[][] datosBd;
        int[] califs=new int[7]; // donde indice indica:
         // 0=9.5-10, 1=9-9.5, 2=8.5-9, 3=8-8.5, 4=7.5-8, 5=7-7.5, 6=menor a 7
        double promedio;
        double val;
        java.text.DecimalFormat formateo=new java.text.DecimalFormat("##.###");
        String mens;
        String sen="select calif from realiza, rubroscalif, imparte, calificacon, grupos";
        sen+=" where realiza.clvru=rubroscalif.clvru and rubroscalif.tipo=10 and calificacon.";
        sen+="clvru=rubroscalif.clvru and calificacon.clvg=grupos.clvg and now()>grupos.";
        sen+="perfin and grupos.clvg=imparte.clvg and imparte.clvm='poo' order by calif desc";
        datosBd=Consultas.consultaDatos(sen,true);
        if(datosBd==null){
            error=Consultas.obtenError();
            return null;
        }
        else if(datosBd[0][0]==null){
            reporte=new Reporte(null,"No existen datos para calcular este reporte","","Calificaciones de alumnos");
            return reporte;
        }
        datos=new DefaultCategoryDataset();
        promedio=0.0;
        for(int j=0;j<datosBd.length;j++){
            val=Double.parseDouble(datosBd[j][0]);
            promedio+=val;
            if(val<7) califs[6]++;
            else if(val>=7 && val <=7.5) califs[5]++;
            else if(val>7.5 && val <=8) califs[4]++; 
            else if(val>8 && val <=8.5) califs[3]++;
            else if(val>8.5 && val <=9) califs[2]++; 
            else if(val>9 && val <=9.5) califs[1]++;
            else if(val>9.5) califs[0]++;
        }
        promedio/=datosBd.length;
        datos.addValue((califs[0]/(datosBd.length/100.0)),"","Rango 9.5 - 10");
        datos.addValue((califs[1]/(datosBd.length/100.0)),"","Rango 9 - 9.5");
        datos.addValue((califs[2]/(datosBd.length/100.0)),"","Rango 8.5 - 9");
        datos.addValue((califs[3]/(datosBd.length/100.0)),"","Rango 8 - 8.5");
        datos.addValue((califs[4]/(datosBd.length/100.0)),"","Rango 7.5 - 8");
        datos.addValue((califs[5]/(datosBd.length/100.0)),"","Rango 7 - 7.5");
        datos.addValue((califs[6]/(datosBd.length/100.0)),"","Menor a 7");
        mens="Reporte generado:"+Tiempo.getFecha()+"\nPorcentaje de calificaciones obtenidas materia "+clvm;
        mens+="\n"+datosBd.length+" Calificaciones registradas\nLa calificación promedio es "+formateo.format(promedio);
        mens+="\n************\nNumero de alumnos por rango de calificación\n";
        mens+=califs[0]+" Alumnos obtuvieron 9.5 a 10\n"+califs[1]+" Alumnos obtuvieron 9 a 9.5\n";
        mens+=califs[2]+" Alumnos obtuvieron 8.5 a 9\n"+califs[3]+" Alumnos obtuvieron 8 a 8.5\n";
        mens+=califs[4]+" Alumnos obtuvieron 7.5 a 8\n"+califs[5]+" Alumnos obtuvieron 7 a 7.5\n";
        mens+=califs[6]+" Alumnos obtuvieron menos de 7";
        grafica = ChartFactory.createBarChart("","Calificación","porcentaje",datos,PlotOrientation.HORIZONTAL,false,true,true);
        plotCat=grafica.getCategoryPlot();
        plotCat.getRenderer().setSeriesPaint(0,new Color(0,0,255));
        plotCat.getRenderer().setSeriesPaint(1,new Color(0,255,0));
        plotCat.getRenderer().setSeriesPaint(2,new Color(255,0,0));
        panel = new ChartPanel(grafica);
        reporte=new Reporte(panel,mens,"Porcentaje de calificaciones obtenidas materia: "+clvm,"Indice calificaciones");
        return reporte;
    }

    /** Genera y obtiene un reporte de indice de aprobacion en una materia
     * @param clvm La clave de la materia del reporte a generar
     * @return un reporte con el porcentaje de alumnos (sobre los que 
     *   han cursado la materia) que han aprobado-reprobado-repetido curso
     */
    private Reporte generaRepAproAlums(String clvm){
        Reporte reporte;
        JFreeChart grafica;
        ChartPanel panel;
        DefaultPieDataset data;
        String[][] datosBd=new String[1][1];
        int[] estado=new int[3]; // donde indice indica:
         // 0=reprobados, 1=aprobados, 2=repetidores
        double calif;
        double minCal;
        String matricula;
        String mens;
        String sen="select calmin from materias where clvm='"+clvm+"';";
        datosBd[0]=Consultas.consultaUnCampo(sen,true);
        if(datosBd==null){
            error=Consultas.obtenError();
            return null;
        }
        else if(datosBd[0][0]==null){
            reporte=new Reporte(null,"No existen datos para calcular este reporte","","Aprobación alumnos");
            return reporte;
        }
        minCal=Double.parseDouble(datosBd[0][0]);
        sen="select pertenece.matricula,pertenece.clvg,realiza.calif from pertenece, realiza, grupos, ";
        sen+="imparte, rubroscalif where realiza.clvper=pertenece.clvper and pertenece.clvg=grupos.clvg";
        sen+=" and now()>grupos.perfin and realiza.clvru=rubroscalif.clvru and rubroscalif.tipo=10 and ";
        sen+="imparte.clvg=grupos.clvg and imparte.clvm='poo' order by pertenece.matricula,pertenece.clvg;";
        datosBd=Consultas.consultaDatos(sen,false);
        if(datosBd==null){
            error=Consultas.obtenError();
            return null;
        }
        else if(datosBd[0][0]==null){
            reporte=new Reporte(null,"No existen datos para calcular este reporte","","Aprobación alumnos");
            return reporte;
        }
        data = new DefaultPieDataset();
        for(int j=0;j<datosBd.length;j++){
            matricula=datosBd[j][0];
            calif=Double.parseDouble(datosBd[j][2]);
            if(calif<minCal) estado[0]++;
            else estado[1]++;
            while(j+1<datosBd.length && matricula.equals(datosBd[j+1][0])){
                calif=Double.parseDouble(datosBd[++j][2]);
                estado[2]++;
            }
        }
        data.setValue("Reprobados",(estado[0]/(datosBd.length/100.0)));
        data.setValue("Aprobados",(estado[1]/(datosBd.length/100.0)));
        data.setValue("Repetidores",(estado[2]/(datosBd.length/100.0)));
        mens="Reporte generado:"+Tiempo.getFecha()+"\nPorcentaje de aprobación de alumnos materia "+clvm;
        mens+="\n"+datosBd.length+" Alumnos registrados\n************\nNumero de alumnos por estado\n";
        mens+=estado[1]+" Alumnos Aprobados\n"+estado[2]+" Alumnos repetidores\n";
        mens+=estado[0]+" Alumnos reprobados";
        grafica = ChartFactory.createPieChart("",data,true,true,true);
        panel = new ChartPanel(grafica);
        reporte=new Reporte(panel,mens,"Porcentaje de aprobación de alumnos materia: "+clvm,"Aprobación alumnos");
        return reporte;
    }
}
