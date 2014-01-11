/*
 * Reporte.java
 *  Guarda y despliega los datos de un reporte grafico
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

import java.awt.event.MouseEvent;
import java.awt.event.MouseListener;
import org.jfree.chart.ChartPanel;

/** Guarda y despliega los datos de un reporte grafico
 * 
 * @author Pedro Cardoso Rodriguz
 */
public class Reporte extends javax.swing.JPanel implements MouseListener{

    /**El grafico del reporte que se guarda*/
    private ChartPanel grafica;
    /**La descripcion del reporte que se guarda*/
    private String desc;
    /**El titulo del reporte que se guarda*/
    private String titulo;
    /**Titulo breve del reporte que se guarda (modo vista mini)*/
    private String miniTit;
    /**Control para el titulo a mostrar del reporte que se guarda*/
    private javax.swing.JLabel lblTit;
    /**Referencia de la ventana a la cual pertenece el reporte*/
    private FrameReporte refSup;
    
    /** Crea un nuevo objeto reporte
     * @param graf El grafico del reporte
     * @param de La descripcion del reporte
     * @param tit El titulo del del reporte
     * @param minTit El titulo breve del reporte
     */
    public Reporte(ChartPanel graf, String de,String tit,String minTit){
        grafica=graf;
        desc=de;
        titulo=tit;
        miniTit=minTit;
        lblTit=new javax.swing.JLabel(miniTit);
        addMouseListener(this);
        refSup=null;
        setLayout(new java.awt.BorderLayout(3,3));
        setPreferredSize(new java.awt.Dimension(350,185));
        setMinimumSize(new java.awt.Dimension(350,185));
        setBorder(javax.swing.BorderFactory.createEtchedBorder());
        actualiza();
    }
    
    /**Actualiza el despliege del reporte guardado*/
    private void actualiza(){
        removeAll();
        if(grafica!=null){
            grafica.getChart().setTitle("");
            add(grafica,java.awt.BorderLayout.CENTER);
        }
        else {
            add(new javax.swing.JLabel("X",javax.swing.JLabel.CENTER),java.awt.BorderLayout.CENTER);
        }
        lblTit=new javax.swing.JLabel(miniTit,javax.swing.JLabel.CENTER);
        add(lblTit,java.awt.BorderLayout.NORTH);
    }

    /** Obtiene la descripcion del reporte actual
     * @return la descripcion del reporte actual
     */
    public String getDesc() {
        return desc;
    }

    /** Establece la descripcion del reporte actual
     * @param desc la descripcion del reporte actual
     */
    public void setDesc(String desc) {
        this.desc = desc;
        actualiza();
    }

    /** Obtiene la grafica del reporte actual
     * @return la grafica del reporte actual
     */
    public ChartPanel getGrafica() {
        return grafica;
    }

    /** Establece la grafica del reporte actual
     * @param grafica la grafica del reporte actual
     */
    public void setGrafica(ChartPanel grafica) {
        this.grafica = grafica;
        actualiza();
    }

    /** Obtiene el titulo del reporte actual
     * @return el titulo del reporte actual
     */
    public String getTitulo() {
        return titulo;
    }

    /** Establece el titulo del reporte actual
     * @param titulo el titulo del reporte actual
     */
    public void setTitulo(String titulo) {
        this.titulo = titulo;
        actualiza();
    }

    /** Establece la ventana a la que pertenece el reporte actual
     * @param refSup la ventana a la que pertenece el reporte actual
     */
    public void setFrameReporte(FrameReporte refSup) {
        this.refSup = refSup;
    }

    /** Establece el reporte actual como principal en la ventana superior que contiene este reporte
     * @param e El ActionEvent que genero el evento
     */
    public void mouseClicked(MouseEvent e) {
        refSup.setGrafica(this);
    }
    public void mousePressed(MouseEvent e) {}
    public void mouseReleased(MouseEvent e) {}
    /** Muestra el texto del control lblTit subrayado y azul para simular efecto link cuando el mouse entra dentro del area del control
     * @param e El ActionEvent que genero el evento
     */
    public void mouseEntered(MouseEvent e) {
        lblTit.setForeground(new java.awt.Color(0,0,255));
        lblTit.setText("<html><u>"+miniTit+"</u></html>");
        validate();
    }
    /** Quita del texto del control lblTit el subrayado y color azul para quitar efecto link cuando el mouse sale del area del control
     * @param e El ActionEvent que genero el evento
     */
    public void mouseExited(MouseEvent e) {
        lblTit.setForeground(new java.awt.Color(0,0,0));
        lblTit.setText(miniTit);
        validate();
    }
    
}
