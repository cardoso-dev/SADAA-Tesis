/*
 * FrameImprimir.java
 *  JDialog para enviar a imprimir reportes generados con ireport
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

package sistema;

import java.io.File;
import java.util.ArrayList;
import java.util.Map;
import javax.print.PrintService;
import javax.print.PrintServiceLookup;
import javax.print.attribute.HashPrintRequestAttributeSet;
import javax.print.attribute.PrintRequestAttributeSet;
import javax.print.attribute.standard.MediaPrintableArea;
import javax.print.attribute.standard.MediaSize;
import javax.print.attribute.standard.OrientationRequested;
import net.sf.jasperreports.engine.JREmptyDataSource;
import net.sf.jasperreports.engine.JRExporterParameter;
import net.sf.jasperreports.engine.JasperFillManager;
import net.sf.jasperreports.engine.JasperPrint;
import net.sf.jasperreports.engine.data.JRBeanCollectionDataSource;
import net.sf.jasperreports.engine.export.JRPrintServiceExporter;
import net.sf.jasperreports.engine.export.JRPrintServiceExporterParameter;

/** JDialog para enviar a imprimir reportes generados con ireport
 * los reportes que puede enviar son:
 *  1=lista de alumnos definido en ListaAlumnos.jrxml
 *  2=lista de creditos (calificacion o porcentaje de asistencia) definido en ListaCreditos.jrxml
 *  3=calendario de sesiones de grupo definido en CalSesionesGrp.jrxml
 *  4=ficha de Registro desempeño academico de alumno definido en FichaDesAca.jrxml
 *  5=ficha de Registro desempeño en grupo de alumno definido en FichaDesGru.jrxml
 *  6=Ficha bibliografica definido en FichaBib.jrxml
 *  7=ficha hemeroteca general definido en FichaHemg.jrxml
 *  8=ficha hemeroteca analitica definido en FichaHema.jrxml
 *  9=reporte de datos de tesista (datos y calendarios de sesiones) definido en FichaTesista.jrxml
 *  10=horario por dias de semana definido en HorarioSem.jrxml
 *  11=Temario definido en ListaTemas.jrxml
 * 
 * @author  Pedro Cardoso Rodriguez
 */
public class FrameImprimir extends javax.swing.JDialog {
    
    /**Lista de impresoras detectadas en el pc que ejecuta el sistema*/
    private PrintService[] impresoras;
    /**Objeto que representa un documento a imprimir*/
    private JasperPrint impresor;
    /**Descripcion del ultimo error ocurrido*/
    private String error;
    /**Tipo de reporte a enviar a imprimir donde: 1=lista de alumnos, 2=lista de creditos, 3=calendario de sesiones de grupo,
     *  4=ficha de Registro desempeño academico de alumno, 5=ficha de Registro desempeño en grupo de alumno, 6=Ficha bibliografica,
     *  7=ficha hemeroteca general, 8=ficha hemeroteca analitica, 9=reporte de datos de tesista, 10=horario por dias de semana, 11=Temario
     */
    private int tipoDoc;
    /**Resultado del ultimo envio a impresion donde: -1=fallo, 0=cancelo, 1=ok*/
    private int resultado;
    
    /** Crea un nuevo FrameImprimir
     * @param parent La ventana propietaria de este dialog
     * @param modal Indica si de debe desplegar en forma modal
     */
    public FrameImprimir(java.awt.Frame parent, boolean modal) {
        super(parent, modal);
        initComponents();
        setTitle("Imprimir reporte");
        detectaImpresoras();
    }
    
    /** Detecta las impresoras instaladas en la pc en la que se ejecuta el sistema y carga la lista 
     *   en el control jcbImpresoras para que el usuario eliga en cual desea imprimir el reporte
     */
    private void detectaImpresoras(){
        impresoras = PrintServiceLookup.lookupPrintServices(null, null);
        if(impresoras.length==0)
            jcbImpresoras.addItem("<No se encontro impresora>");
        else
            for(int h=0;h<impresoras.length;h++)
                jcbImpresoras.addItem(""+impresoras[h].getName());
    }
    
    /** Establece los datos de la impresion actual
     * @param tit Titulo del reporte a imprimir
     * @param tpDoc Tipo de reporte a imprimir
     *   donde: 1=lista de alumnos, 2=lista de creditos (calificacion o porcentaje de asistencia)
     *   3=calendario de sesiones de grupo 4=ficha de Registro desempeño academico de alumno
     *   5=ficha de Registro desempeño en grupo de alumno 6=Ficha bibliografica
     *   7=ficha hemeroteca general 8=ficha hemeroteca analitica 
     *   9=reporte de datos de tesista (datos y calendarios de sesiones)
     *   10=horario por dias de semana, 11=Temario
     * @param params parametros del documento a imprimir
     * @param lista Fuente de datos a imprimir (para los campos detail)
     * @return true si se pudo establecer los datos de impresion false en caso contrario
     */
    public boolean setImpresion(String tit,int tpDoc,Map<String,String> params,ArrayList lista,String logo){
        JRBeanCollectionDataSource campos;
        String[] formularios={"ListaAlumnos","ListaCreditos","CalSesionesGrp","FichaDesAca","FichaDesGru","FichaBib","FichaHemg","FichaHema","FichaTesista","HorarioSem","ListaTemas"};
        String dirBase=System.getProperty("user.dir");
        lblTitulo.setText(tit);
        tipoDoc=tpDoc;
        try{
            campos = new JRBeanCollectionDataSource(lista);
            params.put("LOGO",logo);
            if(lista!=null)
                impresor = JasperFillManager.fillReport(dirBase+File.separator+"formReportes"+File.separator+formularios[tipoDoc-1]+".jasper",params,campos);
            else
                impresor = JasperFillManager.fillReport(dirBase+File.separator+"formReportes"+File.separator+formularios[tipoDoc-1]+".jasper",params,new JREmptyDataSource());
            lblNumCopias.setText("Total paginas del reporte: "+((ArrayList)impresor.getPages()).size());
            txtPagIni.setText("1"); txtCopias.setText("1");
            txtPagFin.setText(""+((ArrayList)impresor.getPages()).size());
            resultado=0;
            return true;
        }
        catch(Exception exc){
            error=exc.getMessage();
            return false;
        }
    }
    
    /** This method is called from within the constructor to
     * initialize the form.
     * WARNING: Do NOT modify this code. The content of this method is
     * always regenerated by the Form Editor.
     */
    // <editor-fold defaultstate="collapsed" desc="Generated Code">//GEN-BEGIN:initComponents
    private void initComponents() {

        jLabel1 = new javax.swing.JLabel();
        jcbImpresoras = new javax.swing.JComboBox();
        jPanel1 = new javax.swing.JPanel();
        lblNumCopias = new javax.swing.JLabel();
        jLabel3 = new javax.swing.JLabel();
        txtPagIni = new javax.swing.JTextField();
        jLabel4 = new javax.swing.JLabel();
        txtPagFin = new javax.swing.JTextField();
        jLabel5 = new javax.swing.JLabel();
        txtCopias = new javax.swing.JTextField();
        jLabel6 = new javax.swing.JLabel();
        btnCancel = new javax.swing.JButton();
        btnAcepta = new javax.swing.JButton();
        lblTitulo = new javax.swing.JLabel();

        setDefaultCloseOperation(javax.swing.WindowConstants.DISPOSE_ON_CLOSE);

        jLabel1.setText("Seleccione impresora:");

        jPanel1.setBorder(javax.swing.BorderFactory.createTitledBorder("Opciones"));

        lblNumCopias.setText("Num de paginas calculadas");

        jLabel3.setText("Imprimir desde:");

        jLabel4.setText("Hasta:");

        jLabel5.setText("Imprimir:");

        txtCopias.setText("1");

        jLabel6.setText("copias:");

        javax.swing.GroupLayout jPanel1Layout = new javax.swing.GroupLayout(jPanel1);
        jPanel1.setLayout(jPanel1Layout);
        jPanel1Layout.setHorizontalGroup(
            jPanel1Layout.createParallelGroup(javax.swing.GroupLayout.Alignment.LEADING)
            .addGroup(jPanel1Layout.createSequentialGroup()
                .addContainerGap()
                .addGroup(jPanel1Layout.createParallelGroup(javax.swing.GroupLayout.Alignment.LEADING)
                    .addComponent(lblNumCopias)
                    .addGroup(jPanel1Layout.createSequentialGroup()
                        .addComponent(jLabel3)
                        .addPreferredGap(javax.swing.LayoutStyle.ComponentPlacement.RELATED)
                        .addComponent(txtPagIni, javax.swing.GroupLayout.PREFERRED_SIZE, 40, javax.swing.GroupLayout.PREFERRED_SIZE)
                        .addPreferredGap(javax.swing.LayoutStyle.ComponentPlacement.RELATED)
                        .addComponent(jLabel4)
                        .addPreferredGap(javax.swing.LayoutStyle.ComponentPlacement.RELATED)
                        .addComponent(txtPagFin, javax.swing.GroupLayout.PREFERRED_SIZE, 40, javax.swing.GroupLayout.PREFERRED_SIZE))
                    .addGroup(jPanel1Layout.createSequentialGroup()
                        .addComponent(jLabel5)
                        .addPreferredGap(javax.swing.LayoutStyle.ComponentPlacement.RELATED)
                        .addComponent(txtCopias, javax.swing.GroupLayout.PREFERRED_SIZE, 40, javax.swing.GroupLayout.PREFERRED_SIZE)
                        .addPreferredGap(javax.swing.LayoutStyle.ComponentPlacement.RELATED)
                        .addComponent(jLabel6)))
                .addContainerGap(javax.swing.GroupLayout.DEFAULT_SIZE, Short.MAX_VALUE))
        );
        jPanel1Layout.setVerticalGroup(
            jPanel1Layout.createParallelGroup(javax.swing.GroupLayout.Alignment.LEADING)
            .addGroup(jPanel1Layout.createSequentialGroup()
                .addContainerGap(javax.swing.GroupLayout.DEFAULT_SIZE, Short.MAX_VALUE)
                .addComponent(lblNumCopias)
                .addGap(18, 18, 18)
                .addGroup(jPanel1Layout.createParallelGroup(javax.swing.GroupLayout.Alignment.BASELINE)
                    .addComponent(jLabel3)
                    .addComponent(txtPagIni, javax.swing.GroupLayout.PREFERRED_SIZE, javax.swing.GroupLayout.DEFAULT_SIZE, javax.swing.GroupLayout.PREFERRED_SIZE)
                    .addComponent(jLabel4)
                    .addComponent(txtPagFin, javax.swing.GroupLayout.PREFERRED_SIZE, javax.swing.GroupLayout.DEFAULT_SIZE, javax.swing.GroupLayout.PREFERRED_SIZE))
                .addGap(18, 18, 18)
                .addGroup(jPanel1Layout.createParallelGroup(javax.swing.GroupLayout.Alignment.BASELINE)
                    .addComponent(jLabel5)
                    .addComponent(txtCopias, javax.swing.GroupLayout.PREFERRED_SIZE, javax.swing.GroupLayout.DEFAULT_SIZE, javax.swing.GroupLayout.PREFERRED_SIZE)
                    .addComponent(jLabel6)))
        );

        btnCancel.setText("Cancelar");
        btnCancel.addActionListener(new java.awt.event.ActionListener() {
            public void actionPerformed(java.awt.event.ActionEvent evt) {
                btnCancelActionPerformed(evt);
            }
        });

        btnAcepta.setText("Aceptar");
        btnAcepta.addActionListener(new java.awt.event.ActionListener() {
            public void actionPerformed(java.awt.event.ActionEvent evt) {
                btnAceptaActionPerformed(evt);
            }
        });

        lblTitulo.setFont(new java.awt.Font("Tahoma", 1, 11));
        lblTitulo.setHorizontalAlignment(javax.swing.SwingConstants.CENTER);
        lblTitulo.setText("Titulo");

        javax.swing.GroupLayout layout = new javax.swing.GroupLayout(getContentPane());
        getContentPane().setLayout(layout);
        layout.setHorizontalGroup(
            layout.createParallelGroup(javax.swing.GroupLayout.Alignment.LEADING)
            .addGroup(layout.createSequentialGroup()
                .addContainerGap()
                .addGroup(layout.createParallelGroup(javax.swing.GroupLayout.Alignment.TRAILING, false)
                    .addComponent(lblTitulo, javax.swing.GroupLayout.Alignment.LEADING, javax.swing.GroupLayout.DEFAULT_SIZE, javax.swing.GroupLayout.DEFAULT_SIZE, Short.MAX_VALUE)
                    .addGroup(javax.swing.GroupLayout.Alignment.LEADING, layout.createParallelGroup(javax.swing.GroupLayout.Alignment.LEADING)
                        .addGroup(layout.createParallelGroup(javax.swing.GroupLayout.Alignment.TRAILING, false)
                            .addComponent(jcbImpresoras, javax.swing.GroupLayout.Alignment.LEADING, 0, 281, Short.MAX_VALUE)
                            .addComponent(jLabel1, javax.swing.GroupLayout.Alignment.LEADING))
                        .addGroup(layout.createSequentialGroup()
                            .addGap(20, 20, 20)
                            .addComponent(jPanel1, javax.swing.GroupLayout.PREFERRED_SIZE, javax.swing.GroupLayout.DEFAULT_SIZE, javax.swing.GroupLayout.PREFERRED_SIZE))
                        .addGroup(javax.swing.GroupLayout.Alignment.TRAILING, layout.createSequentialGroup()
                            .addPreferredGap(javax.swing.LayoutStyle.ComponentPlacement.RELATED, 129, javax.swing.GroupLayout.PREFERRED_SIZE)
                            .addComponent(btnAcepta)
                            .addPreferredGap(javax.swing.LayoutStyle.ComponentPlacement.RELATED)
                            .addComponent(btnCancel))))
                .addContainerGap(javax.swing.GroupLayout.DEFAULT_SIZE, Short.MAX_VALUE))
        );
        layout.setVerticalGroup(
            layout.createParallelGroup(javax.swing.GroupLayout.Alignment.LEADING)
            .addGroup(layout.createSequentialGroup()
                .addContainerGap()
                .addComponent(lblTitulo)
                .addGap(18, 18, 18)
                .addComponent(jLabel1)
                .addPreferredGap(javax.swing.LayoutStyle.ComponentPlacement.RELATED)
                .addComponent(jcbImpresoras, javax.swing.GroupLayout.PREFERRED_SIZE, javax.swing.GroupLayout.DEFAULT_SIZE, javax.swing.GroupLayout.PREFERRED_SIZE)
                .addPreferredGap(javax.swing.LayoutStyle.ComponentPlacement.RELATED)
                .addComponent(jPanel1, javax.swing.GroupLayout.PREFERRED_SIZE, javax.swing.GroupLayout.DEFAULT_SIZE, javax.swing.GroupLayout.PREFERRED_SIZE)
                .addPreferredGap(javax.swing.LayoutStyle.ComponentPlacement.RELATED)
                .addGroup(layout.createParallelGroup(javax.swing.GroupLayout.Alignment.BASELINE)
                    .addComponent(btnCancel)
                    .addComponent(btnAcepta))
                .addContainerGap(47, Short.MAX_VALUE))
        );

        pack();
    }// </editor-fold>//GEN-END:initComponents

    /** Cierra el dialog
     * @param evt El ActionEvent que genro el evento
     */
    private void btnCancelActionPerformed(java.awt.event.ActionEvent evt) {//GEN-FIRST:event_btnCancelActionPerformed
        setVisible(false);
    }//GEN-LAST:event_btnCancelActionPerformed

    /** Envia a imprimir el reporte actual con las opciones elegidas (paginas y numero de copias)
     * @param evt El ActionEvent que genero el evento
     */
    private void btnAceptaActionPerformed(java.awt.event.ActionEvent evt) {//GEN-FIRST:event_btnAceptaActionPerformed
        PrintRequestAttributeSet atribImp=new HashPrintRequestAttributeSet();
        JRPrintServiceExporter exporter = new JRPrintServiceExporter();
        OrientationRequested orientation;
        float base=8.5f; 
        float alto=11f;
        int pagini, pagfin, copias;
        try{
            pagini=Integer.parseInt(txtPagIni.getText());
            pagfin=Integer.parseInt(txtPagFin.getText());
            copias=Integer.parseInt(txtCopias.getText());
            if(copias<1){
                resultado=-1; error="Numero de copias invalido";
                setVisible(false); return;
            }
            if(tipoDoc!=6&&tipoDoc!=7&&tipoDoc!=8){
                atribImp.add(MediaSize.findMedia(base,alto,MediaSize.INCH)); 
                if(tipoDoc==10 || tipoDoc==3){
                    orientation = OrientationRequested.LANDSCAPE;
                }
                else orientation = OrientationRequested.PORTRAIT;
            }
            else{
                atribImp.add(MediaSize.findMedia(125f,75f,MediaSize.MM));
                orientation = OrientationRequested.LANDSCAPE;
            }
            atribImp.add(new MediaPrintableArea(0,0,base,alto,MediaSize.INCH));
            atribImp.add(orientation);
            exporter.setParameter(JRExporterParameter.JASPER_PRINT, impresor);
            exporter.setParameter(JRExporterParameter.START_PAGE_INDEX,pagini-1);
            exporter.setParameter(JRExporterParameter.END_PAGE_INDEX,pagfin-1);
            exporter.setParameter(JRPrintServiceExporterParameter.DISPLAY_PRINT_DIALOG, Boolean.FALSE);
            exporter.setParameter(JRPrintServiceExporterParameter.PRINT_SERVICE, impresoras[jcbImpresoras.getSelectedIndex()]);
            exporter.setParameter(JRPrintServiceExporterParameter.PRINT_REQUEST_ATTRIBUTE_SET,atribImp);
            for(int t=0;t<copias;t++) exporter.exportReport();
            setVisible(false);
        }
        catch(Exception exc){
            resultado=-1; error=exc.getMessage();
            setVisible(false); return;
        }
        resultado=1;
    }//GEN-LAST:event_btnAceptaActionPerformed
    
    /** Obtiene la descripcion del ultimo error ocurrido
     * @return la descripcion del ultimo error ocurrido
     */
    public String getError(){ return error; }
    
    /** Obtiene el indicador del resultado del ultimo envio a imprimir
     * @return indicador del resultado del ultimo envio a imprimir donde: -1=fallo, 0=cancelo, 1=ok
     */
    public int getResultado(){ return resultado;}
            
    // Variables declaration - do not modify//GEN-BEGIN:variables
    private javax.swing.JButton btnAcepta;
    private javax.swing.JButton btnCancel;
    private javax.swing.JLabel jLabel1;
    private javax.swing.JLabel jLabel3;
    private javax.swing.JLabel jLabel4;
    private javax.swing.JLabel jLabel5;
    private javax.swing.JLabel jLabel6;
    private javax.swing.JPanel jPanel1;
    private javax.swing.JComboBox jcbImpresoras;
    private javax.swing.JLabel lblNumCopias;
    private javax.swing.JLabel lblTitulo;
    private javax.swing.JTextField txtCopias;
    private javax.swing.JTextField txtPagFin;
    private javax.swing.JTextField txtPagIni;
    // End of variables declaration//GEN-END:variables
    
}
