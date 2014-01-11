/*
 * FrameDesAca.java
 *  Ventana para dar seguimiento al desempeño academico de un alumno
 * Parte de proyecto: SADAA
 * Author: Pedro Cardoso Rodríguez
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

package alumnos;

import reportes.ImpDesAca;
import definiciones.TipoMensaje;
import definiciones.TipoRespuesta;
import iconos.Iconos;
import java.util.ArrayList;
import java.util.HashMap;
import java.util.Map;
import javax.swing.table.DefaultTableModel;
import operaciones.Datos;

/**  Es una ventana interna (JInternalFrame) la cual sirve para mostrar 
 *  el desempeño academico de un alumno, muestra una tabla con las materias 
 *  cursadas y aprobadas por el alumno estilo hoja kardex, puede registrar 
 *  incluso materias de otros docentes.
 * 
 * @author Pedro Cardoso Rodríguez
 */
public class FrameDesAca extends sistema.ModeloFrameInterno implements java.awt.event.ActionListener {
    
    /** La matricula del alumno del cual se muestran los registros actualmente (vacio para ninguno) */
    private String matActual;
    /**Menu pop up para la tabla de materias con opciones para modificar o quitar registros*/
    private javax.swing.JPopupMenu pmnuOpcs;
    /** Menu (parte de pmnuOpcs) para la opcion de modificar registro de tabla de materias */
    private javax.swing.JMenuItem mnu1;
    /** Menu (parte de pmnuOpcs) para la opcion de quitar registro de tabla de materias */
    private javax.swing.JMenuItem mnu2;
    /** Menu (parte de pmnuOpcs) para la opcion de verregistro registro de tabla de 
     *   materias (solo para materias cursadas y registradas dentro del sistema)*/
    private javax.swing.JMenuItem mnu3;
    
    /** Crea una nueva ventana FrameDesAca
     * @param ventana Referencia a la ventana principal contenedora 
     *   (sistema.FramePrincipal)
     */
    public FrameDesAca(sistema.FramePrincipal ventana){
        super(ventana,"frmregdesaca.png");
        initComponents();
        btnAbRegPer.setIcon(Iconos.getIcono("frmregistro.png"));
        btnAbRegDesG.setIcon(Iconos.getIcono("frmregdesgru.png"));
        btnAbAse.setIcon(Iconos.getIcono("frmasesorias.png"));
        btnAbRegPer.setText("");
        btnAbRegDesG.setText("");
        btnAbAse.setText("");
        btnImprimir.setIcon(Iconos.getIcono("impresora.png"));
        btnImprimir.setText("");
        btnCarga.setIcon(Iconos.getIcono("cargar.png"));       
        btnAgreReg.setIcon(Iconos.getIcono("nuevo.png"));
        btnModReg.setIcon(Iconos.getIcono("modifica.png"));
        btnEliReg.setIcon(Iconos.getIcono("elimina.png"));
        crearPopMenu();
        matActual="";
    }
    
    /** Carga los registros de desempeño academico de un alumno por su matricula
     * @param matricula La matricula del alumno del cual se desean cargar los registros
     * @return true si se cargaron los registros, false si ocurrio un error al consultar la
     *   base de datos o si no existe alumno con la matricula recibida en el parametro
     */
    public boolean cargaDatosAlumno(String matricula){
        DefaultTableModel modelo;
        DefaultTableModel modelo2;
        String[] nombre;
        String sentencia="select DesAca.ClvDA as 'Clave', DesAca.Materia, DesAca.Periodo, DesAca.Docente, DesAca.Grado, ";
        sentencia+="DesAca.Grupo, DesAca.CalF as 'Calificacion' from DesAca,Muestra where";
        int materias;
        float prom=0f;
        java.text.DecimalFormat formato = new java.text.DecimalFormat("##.##");           
        nombre=database.Consultas.consultaLista("select nom,ApPat,ApMat from Alumno where matricula='"+matricula+"'",true);
        if(nombre==null){
            muestraMensaje("Error al realizar la búsqueda",database.Consultas.obtenError(),TipoMensaje.ERROR);
            return false;
        }
        if(nombre[0]==null){
            muestraMensaje("Resultado de la búsqueda","No se encontraron datos para esa matricula",TipoMensaje.ERROR);
            return false;
        }
        modelo=database.Consultas.consTipoTable(sentencia+" Muestra.Matricula='"+matricula+"' and DesAca.ClvDA=Muestra.ClvDA",false);
        if(modelo==null){
            muestraMensaje("Error al realizar la búsqueda",database.Consultas.obtenError(),TipoMensaje.ERROR);
            return false;
        }
        sentencia="select grupos.clvg,materias.nombre,perini,perfin,datosdoc.nombre,grado,grupo,calif ";
        sentencia+="from materias,grupos,imparte,pertenece,datosdoc,realiza,rubroscalif where materias";
        sentencia+=".clvm=imparte.clvm and imparte.clvg=grupos.clvg and pertenece.clvg=grupos.clvg and ";
        sentencia+=" realiza.clvper=pertenece.clvper and realiza.clvru=rubroscalif.clvru and tipo=10 and";
        sentencia+=" calif>=calmin and matricula='"+matricula+"'";
        modelo2=database.Consultas.consTipoTable(sentencia,false);
        if(modelo2!=null&&modelo2.getRowCount()>0){
            Object[] fila=new Object[modelo2.getColumnCount()-1];
            for(int h=0;h<modelo2.getRowCount();h++){
                for(int d=0;d<modelo2.getColumnCount();d++) {
                    if(d<2) fila[d]=modelo2.getValueAt(h,d);
                    else if(d==2) fila[d]=Datos.transFechasDescPer(""+modelo2.getValueAt(h,d++),""+modelo2.getValueAt(h,d));
                    else fila[d-1]=modelo2.getValueAt(h,d);
                }
                modelo.addRow(fila);
            }
        }
        matActual=matricula;
        setTitle("Registro personal de desempeño académico alumno "+matricula);
        txtNomAl.setText(nombre[0]);                
        materias=modelo.getRowCount();
        for(int h=0;h<materias;h++) prom+=Float.parseFloat((String)(""+modelo.getValueAt(h, 6)));
        prom=(materias==0?0:prom/materias);
        lblNMats.setText("Se muestran "+materias+" materias registradas. Con un promedio general de:");
        txtPromG.setText(formato.format(prom));
        jtRegs.setModel(modelo); jtRegs.getColumn("Clave").setPreferredWidth(50);
        jtRegs.getColumn("Materia").setPreferredWidth(160); jtRegs.getColumn("Periodo").setPreferredWidth(100);
        jtRegs.getColumn("Docente").setPreferredWidth(140); jtRegs.getColumn("Grado").setPreferredWidth(50);
        jtRegs.getColumn("Grupo").setPreferredWidth(50); jtRegs.getColumn("Calificacion").setPreferredWidth(80);        
        btnAbRegPer.setEnabled(true); btnAbRegDesG.setEnabled(true); 
        btnAbAse.setEnabled(true); btnAgreReg.setEnabled(true);
        btnModReg.setEnabled(true); btnEliReg.setEnabled(true);
        btnImprimir.setEnabled(true);
        return true;
    }
    
    /** Recarga (refresca desde la base de datos) los registros del alumno actual */
    public void actualiza(){
        cargaDatosAlumno(matActual);
    }
       
    /** This method is called from within the constructor to
     * initialize the form.
     * WARNING: Do NOT modify this code. The content of this method is
     * always regenerated by the Form Editor.
     */
    // <editor-fold defaultstate="collapsed" desc="Generated Code">//GEN-BEGIN:initComponents
    private void initComponents() {

        jToolBar1 = new javax.swing.JToolBar();
        btnAbRegPer = new javax.swing.JButton();
        btnAbRegDesG = new javax.swing.JButton();
        btnAbAse = new javax.swing.JButton();
        btnImprimir = new javax.swing.JButton();
        jPanel2 = new javax.swing.JPanel();
        jLabel3 = new javax.swing.JLabel();
        txtMat = new javax.swing.JTextField();
        btnCarga = new javax.swing.JButton();
        jPanel1 = new javax.swing.JPanel();
        jPanel3 = new javax.swing.JPanel();
        jLabel1 = new javax.swing.JLabel();
        txtNomAl = new javax.swing.JTextField();
        lblNMats = new javax.swing.JLabel();
        txtPromG = new javax.swing.JTextField();
        pnlRegs = new javax.swing.JPanel();
        jScrollPane3 = new javax.swing.JScrollPane();
        jtRegs = new javax.swing.JTable();
        btnAgreReg = new javax.swing.JButton();
        btnModReg = new javax.swing.JButton();
        btnEliReg = new javax.swing.JButton();

        setClosable(true);
        setDefaultCloseOperation(javax.swing.WindowConstants.DO_NOTHING_ON_CLOSE);
        setIconifiable(true);
        setMaximizable(true);
        setResizable(true);
        setTitle("Registro personal de desempeño académico");

        jToolBar1.setFloatable(false);
        jToolBar1.setRollover(true);

        btnAbRegPer.setText("regp");
        btnAbRegPer.setToolTipText("Abrir registro personal de alumno");
        btnAbRegPer.setEnabled(false);
        btnAbRegPer.setFocusable(false);
        btnAbRegPer.setHorizontalTextPosition(javax.swing.SwingConstants.CENTER);
        btnAbRegPer.setVerticalTextPosition(javax.swing.SwingConstants.BOTTOM);
        btnAbRegPer.addActionListener(new java.awt.event.ActionListener() {
            public void actionPerformed(java.awt.event.ActionEvent evt) {
                btnAbRegPerActionPerformed(evt);
            }
        });
        jToolBar1.add(btnAbRegPer);

        btnAbRegDesG.setText("regdg");
        btnAbRegDesG.setToolTipText("Abrir registro de desempeño en grupo del alumno");
        btnAbRegDesG.setEnabled(false);
        btnAbRegDesG.setFocusable(false);
        btnAbRegDesG.setHorizontalTextPosition(javax.swing.SwingConstants.CENTER);
        btnAbRegDesG.setVerticalTextPosition(javax.swing.SwingConstants.BOTTOM);
        btnAbRegDesG.addActionListener(new java.awt.event.ActionListener() {
            public void actionPerformed(java.awt.event.ActionEvent evt) {
                btnAbRegDesGActionPerformed(evt);
            }
        });
        jToolBar1.add(btnAbRegDesG);

        btnAbAse.setText("ase");
        btnAbAse.setToolTipText("Asesorias");
        btnAbAse.setEnabled(false);
        btnAbAse.setFocusable(false);
        btnAbAse.setHorizontalTextPosition(javax.swing.SwingConstants.CENTER);
        btnAbAse.setVerticalTextPosition(javax.swing.SwingConstants.BOTTOM);
        btnAbAse.addActionListener(new java.awt.event.ActionListener() {
            public void actionPerformed(java.awt.event.ActionEvent evt) {
                btnAbAseActionPerformed(evt);
            }
        });
        jToolBar1.add(btnAbAse);

        btnImprimir.setText("imp");
        btnImprimir.setToolTipText("Imprimir un reporte");
        btnImprimir.setEnabled(false);
        btnImprimir.setFocusable(false);
        btnImprimir.setHorizontalTextPosition(javax.swing.SwingConstants.CENTER);
        btnImprimir.setVerticalTextPosition(javax.swing.SwingConstants.BOTTOM);
        btnImprimir.addActionListener(new java.awt.event.ActionListener() {
            public void actionPerformed(java.awt.event.ActionEvent evt) {
                btnImprimirActionPerformed(evt);
            }
        });
        jToolBar1.add(btnImprimir);

        jPanel2.setBorder(javax.swing.BorderFactory.createBevelBorder(javax.swing.border.BevelBorder.RAISED));

        jLabel3.setText("Buscar Matricula:");

        btnCarga.setMnemonic('C');
        btnCarga.setText("Cargar");
        btnCarga.addActionListener(new java.awt.event.ActionListener() {
            public void actionPerformed(java.awt.event.ActionEvent evt) {
                btnCargaActionPerformed(evt);
            }
        });

        org.jdesktop.layout.GroupLayout jPanel2Layout = new org.jdesktop.layout.GroupLayout(jPanel2);
        jPanel2.setLayout(jPanel2Layout);
        jPanel2Layout.setHorizontalGroup(
            jPanel2Layout.createParallelGroup(org.jdesktop.layout.GroupLayout.LEADING)
            .add(org.jdesktop.layout.GroupLayout.TRAILING, jPanel2Layout.createSequentialGroup()
                .addContainerGap(229, Short.MAX_VALUE)
                .add(jLabel3)
                .addPreferredGap(org.jdesktop.layout.LayoutStyle.UNRELATED)
                .add(txtMat, org.jdesktop.layout.GroupLayout.PREFERRED_SIZE, 93, org.jdesktop.layout.GroupLayout.PREFERRED_SIZE)
                .addPreferredGap(org.jdesktop.layout.LayoutStyle.RELATED)
                .add(btnCarga)
                .addContainerGap())
        );
        jPanel2Layout.setVerticalGroup(
            jPanel2Layout.createParallelGroup(org.jdesktop.layout.GroupLayout.LEADING)
            .add(jPanel2Layout.createParallelGroup(org.jdesktop.layout.GroupLayout.BASELINE)
                .add(btnCarga)
                .add(txtMat, org.jdesktop.layout.GroupLayout.PREFERRED_SIZE, org.jdesktop.layout.GroupLayout.DEFAULT_SIZE, org.jdesktop.layout.GroupLayout.PREFERRED_SIZE)
                .add(jLabel3))
        );

        jToolBar1.add(jPanel2);

        getContentPane().add(jToolBar1, java.awt.BorderLayout.NORTH);

        jPanel1.setLayout(new java.awt.BorderLayout());

        jLabel1.setText("Registro de desempeño académico del alumno:");

        txtNomAl.setEditable(false);

        lblNMats.setText("Se muestran N materias registradas con un promedio general de:");

        txtPromG.setEditable(false);

        org.jdesktop.layout.GroupLayout jPanel3Layout = new org.jdesktop.layout.GroupLayout(jPanel3);
        jPanel3.setLayout(jPanel3Layout);
        jPanel3Layout.setHorizontalGroup(
            jPanel3Layout.createParallelGroup(org.jdesktop.layout.GroupLayout.LEADING)
            .add(jPanel3Layout.createSequentialGroup()
                .addContainerGap()
                .add(jPanel3Layout.createParallelGroup(org.jdesktop.layout.GroupLayout.LEADING)
                    .add(jPanel3Layout.createSequentialGroup()
                        .add(jLabel1)
                        .addPreferredGap(org.jdesktop.layout.LayoutStyle.RELATED)
                        .add(txtNomAl, org.jdesktop.layout.GroupLayout.PREFERRED_SIZE, 255, org.jdesktop.layout.GroupLayout.PREFERRED_SIZE))
                    .add(jPanel3Layout.createSequentialGroup()
                        .add(lblNMats)
                        .addPreferredGap(org.jdesktop.layout.LayoutStyle.RELATED)
                        .add(txtPromG, org.jdesktop.layout.GroupLayout.PREFERRED_SIZE, 40, org.jdesktop.layout.GroupLayout.PREFERRED_SIZE)))
                .addContainerGap(118, Short.MAX_VALUE))
        );
        jPanel3Layout.setVerticalGroup(
            jPanel3Layout.createParallelGroup(org.jdesktop.layout.GroupLayout.LEADING)
            .add(jPanel3Layout.createSequentialGroup()
                .add(jPanel3Layout.createParallelGroup(org.jdesktop.layout.GroupLayout.BASELINE)
                    .add(jLabel1)
                    .add(txtNomAl, org.jdesktop.layout.GroupLayout.PREFERRED_SIZE, org.jdesktop.layout.GroupLayout.DEFAULT_SIZE, org.jdesktop.layout.GroupLayout.PREFERRED_SIZE))
                .addPreferredGap(org.jdesktop.layout.LayoutStyle.RELATED)
                .add(jPanel3Layout.createParallelGroup(org.jdesktop.layout.GroupLayout.BASELINE)
                    .add(lblNMats)
                    .add(txtPromG, org.jdesktop.layout.GroupLayout.PREFERRED_SIZE, org.jdesktop.layout.GroupLayout.DEFAULT_SIZE, org.jdesktop.layout.GroupLayout.PREFERRED_SIZE)))
        );

        jPanel1.add(jPanel3, java.awt.BorderLayout.NORTH);

        jtRegs.setAutoCreateRowSorter(true);
        jtRegs.setModel(new javax.swing.table.DefaultTableModel(
            new Object [][] {

            },
            new String [] {
                "Clave", "Materia", "Periodo", "Docente", "Grado", "Grupo", "Calificacion"
            }
        ) {
            boolean[] canEdit = new boolean [] {
                false, false, false, false, false, false, false
            };

            public boolean isCellEditable(int rowIndex, int columnIndex) {
                return canEdit [columnIndex];
            }
        });
        jtRegs.setAutoResizeMode(javax.swing.JTable.AUTO_RESIZE_OFF);
        jtRegs.setEnabled(false);
        jtRegs.addMouseListener(new java.awt.event.MouseAdapter() {
            public void mouseClicked(java.awt.event.MouseEvent evt) {
                jtRegsMouseClicked(evt);
            }
            public void mousePressed(java.awt.event.MouseEvent evt) {
                jtRegsMousePressed(evt);
            }
            public void mouseReleased(java.awt.event.MouseEvent evt) {
                jtRegsMouseReleased(evt);
            }
        });
        jScrollPane3.setViewportView(jtRegs);
        jtRegs.getColumnModel().getColumn(0).setPreferredWidth(100);

        btnAgreReg.setMnemonic('A');
        btnAgreReg.setText("Agregar");
        btnAgreReg.setToolTipText("");
        btnAgreReg.setEnabled(false);
        btnAgreReg.addActionListener(new java.awt.event.ActionListener() {
            public void actionPerformed(java.awt.event.ActionEvent evt) {
                btnAgreRegActionPerformed(evt);
            }
        });

        btnModReg.setMnemonic('M');
        btnModReg.setText("Modificar");
        btnModReg.setToolTipText("");
        btnModReg.setEnabled(false);
        btnModReg.addActionListener(new java.awt.event.ActionListener() {
            public void actionPerformed(java.awt.event.ActionEvent evt) {
                btnModRegActionPerformed(evt);
            }
        });

        btnEliReg.setMnemonic('E');
        btnEliReg.setText("Eliminar");
        btnEliReg.setToolTipText("");
        btnEliReg.setEnabled(false);
        btnEliReg.addActionListener(new java.awt.event.ActionListener() {
            public void actionPerformed(java.awt.event.ActionEvent evt) {
                btnEliRegActionPerformed(evt);
            }
        });

        org.jdesktop.layout.GroupLayout pnlRegsLayout = new org.jdesktop.layout.GroupLayout(pnlRegs);
        pnlRegs.setLayout(pnlRegsLayout);
        pnlRegsLayout.setHorizontalGroup(
            pnlRegsLayout.createParallelGroup(org.jdesktop.layout.GroupLayout.LEADING)
            .add(pnlRegsLayout.createSequentialGroup()
                .addContainerGap()
                .add(pnlRegsLayout.createParallelGroup(org.jdesktop.layout.GroupLayout.LEADING)
                    .add(jScrollPane3, org.jdesktop.layout.GroupLayout.DEFAULT_SIZE, 591, Short.MAX_VALUE)
                    .add(pnlRegsLayout.createSequentialGroup()
                        .add(btnAgreReg)
                        .addPreferredGap(org.jdesktop.layout.LayoutStyle.UNRELATED)
                        .add(btnModReg)
                        .addPreferredGap(org.jdesktop.layout.LayoutStyle.UNRELATED)
                        .add(btnEliReg)))
                .addContainerGap())
        );
        pnlRegsLayout.setVerticalGroup(
            pnlRegsLayout.createParallelGroup(org.jdesktop.layout.GroupLayout.LEADING)
            .add(pnlRegsLayout.createSequentialGroup()
                .addContainerGap()
                .add(pnlRegsLayout.createParallelGroup(org.jdesktop.layout.GroupLayout.LEADING)
                    .add(btnAgreReg)
                    .add(pnlRegsLayout.createParallelGroup(org.jdesktop.layout.GroupLayout.BASELINE)
                        .add(btnModReg)
                        .add(btnEliReg)))
                .addPreferredGap(org.jdesktop.layout.LayoutStyle.RELATED)
                .add(jScrollPane3, org.jdesktop.layout.GroupLayout.DEFAULT_SIZE, 324, Short.MAX_VALUE)
                .addContainerGap())
        );

        jPanel1.add(pnlRegs, java.awt.BorderLayout.CENTER);

        getContentPane().add(jPanel1, java.awt.BorderLayout.CENTER);

        pack();
    }// </editor-fold>//GEN-END:initComponents
     
    /** Llama a la ventana (ver alumno.FrmRegDesAca) para agregar un nuevo registro de materia cursada
     * @param evt El ActionEvent que genero el evento
     */
    private void btnAgreRegActionPerformed(java.awt.event.ActionEvent evt) {//GEN-FIRST:event_btnAgreRegActionPerformed
        alumnos.FrmRegDesAca newRegInd;
        if(existeVentana("Registro de curso alumno: "+matActual, true)) return;
        newRegInd = new alumnos.FrmRegDesAca(getPrincipalVnt(),getTitle(),matActual,-1,null);
        agregaVentana(newRegInd);
    }//GEN-LAST:event_btnAgreRegActionPerformed

    /** Valida la matricula ingresada en el campo Buscar Matricula (control txtMat)
     *   y si es valida (8 digitos) llama al metodo cargaDatosAlumno para buscar y 
     *   cargar los registros del alumno correspondiente
     * @param evt El ActionEvent que genero el evento
     */
    private void btnCargaActionPerformed(java.awt.event.ActionEvent evt) {//GEN-FIRST:event_btnCargaActionPerformed
        String mat=txtMat.getText();
        if(!Datos.valMatricula(mat)){ // validar la matricula ingresada
            muestraMensaje("Error en el parámetro","Matricula invalida",TipoMensaje.ERROR);
            return;
        }
        if(getTitle().equals("Registro personal de desempeño academico alumno "+mat))
            actualiza();
        else if(existeVentana("Registro personal de desempeño academico alumno "+mat,true)){ 
            txtMat.setText(matActual);
            return;
        }        
        else cargaDatosAlumno(mat);
    }//GEN-LAST:event_btnCargaActionPerformed

    /** Llama al metodo agregaVentanaAlumno de la ventana principal (sistema.FramePrincipal)
     *   con los parametros adecuados para abrir la ventana con el registro personal 
     *   correspondiente al alumno actual
     * @param evt El ActionEvent que genero el evento
     */
    private void btnAbRegPerActionPerformed(java.awt.event.ActionEvent evt) {//GEN-FIRST:event_btnAbRegPerActionPerformed
        getPrincipalVnt().agregaVentanaAlumno(matActual,1);
    }//GEN-LAST:event_btnAbRegPerActionPerformed

    /** Pide la clave de un registro de la tabla de materias para llamar la ventana
     *   alumno.FrmRegDesAca y cargarlo para modificar sus datos
     * @param evt El ActionEvent que genero el evento
     */
    private void btnModRegActionPerformed(java.awt.event.ActionEvent evt) {//GEN-FIRST:event_btnModRegActionPerformed
        DefaultTableModel modelo=(DefaultTableModel)jtRegs.getModel();
        int clave;
        int filas=modelo.getRowCount();
        boolean bandera=false;
        if(filas==0) return;
        clave=pideEntero("Modificar un registro de desempeño academico", "¿Clave del registro que deseas modificar?");        
        for(int i=0;i<filas;i++){
            try{
                if(Integer.parseInt(""+modelo.getValueAt(i,0))==clave){
                    modificaRegistro(clave);
                    bandera=true; break;
                }
            }
            catch(NumberFormatException nbfExc){ continue; }
        }
        if(!bandera)
            muestraMensaje("No se pudo realizar la acción", "No se encontro registro con la clave: "+clave,TipoMensaje.INFORMACION);
    }//GEN-LAST:event_btnModRegActionPerformed

    /** Reciba la clave de un registro materia de desempeño para cargarlo en su propia ventana
     *   alumno.FrmRegDesAca y poder modificarlo (solo aplica a materias cursadas no registradas en el sistema)
     * @param clave La clave del registro de materia a modificar
     */
    private void modificaRegistro(int clave){
        DefaultTableModel modelo=(DefaultTableModel)jtRegs.getModel();
        alumnos.FrmRegDesAca newRegInd;
        if(!existeVentana("Registro de curso "+clave+" alumno: "+matActual,true)){
            String[] datos = new String[modelo.getColumnCount()];
            for(int g=0;g<modelo.getRowCount();g++){
                if(Integer.parseInt(""+modelo.getValueAt(g,0))==clave){
                    for(int j=1;j<datos.length;j++) 
                        datos[j-1]=""+modelo.getValueAt(g,j);
                    break;
                }
            }
            newRegInd = new alumnos.FrmRegDesAca(getPrincipalVnt(),getTitle(),matActual,clave,datos);
            agregaVentana(newRegInd);
        }
    }
    
    /** Pide la clave de un registro de la tabla de materias para eliminarlo
     * @param evt El ActionEvent que genero el evento
     */
    private void btnEliRegActionPerformed(java.awt.event.ActionEvent evt) {//GEN-FIRST:event_btnEliRegActionPerformed
        DefaultTableModel modelo=(DefaultTableModel)jtRegs.getModel();
        int clave;
        int filas=modelo.getRowCount();
        boolean bandera=false;
        if(filas==0) return;
        clave=pideEntero("Eliminar un registro de desempeño academico", "Ingresa la clave del registro que deseas eliminar");        
        for(int i=0;i<filas;i++){
            try{
                if(Integer.parseInt(""+modelo.getValueAt(i,0))==clave){
                    quitarRegistro(clave);
                    bandera=true; break;
                }
            }
            catch(NumberFormatException nbfExc){ continue; }
        }
        if(!bandera){
            muestraMensaje("No se pudo realizar la acción", "No se encontro registro con la clave: "+clave,TipoMensaje.INFORMACION);
        }
    }//GEN-LAST:event_btnEliRegActionPerformed

    /** Reciba la clave de un registro materia de desempeño para eliminarlo
     *   (si esta configurado pide password de seguridad antes de eliminarlo)
     * @param clave La clave del registro de materia a eliminar
     */
    private void quitarRegistro(int clave){
        TipoRespuesta tmp=pideDesicion("Confirme la accion", "En verdad desea eliminar completamente el registro: "+clave);
        if(tmp.getTipo()!=TipoRespuesta.ACEPTAR.getTipo()) return;
        String sentencia="delete DesAca,Muestra from DesAca,Muestra where Muestra.ClvDA="+clave+" and DesAca.ClvDA="+clave+";";
        if(!database.Actualiza.actualiza(sentencia,false,true))
            muestraMensaje("No se pudo realizar la acción", database.Actualiza.obtenError(),TipoMensaje.ERROR);
        else{
            muestraMensaje("Acción realizada", "Se ha eliminado completamente el registro",TipoMensaje.INFORMACION);
            actualiza();
        }
    }
    
    /** Llama al metodo agregaVentanaAlumno de la ventana principal (sistema.FramePrincipal)
     *   con los parametros adecuados para abrir la ventana con el registro de desempeño
     *   en grupos (grupos a los que pertenesca registrados en el sistema) correspondiente al alumno actual
     * @param evt El ActionEvent que genero el evento
     */
    private void btnAbRegDesGActionPerformed(java.awt.event.ActionEvent evt) {//GEN-FIRST:event_btnAbRegDesGActionPerformed
        getPrincipalVnt().agregaVentanaAlumno(matActual,3);
    }//GEN-LAST:event_btnAbRegDesGActionPerformed

    /** Llama al metodo agregaVentanaAlumno de la ventana principal (sistema.FramePrincipal)
     *   con los parametros adecuados para abrir la ventana con el registro de asesorias
     *   de tesis correspondiente al alumno actual
     * @param evt El ActionEvent que genero el evento
     */
    private void btnAbAseActionPerformed(java.awt.event.ActionEvent evt) {//GEN-FIRST:event_btnAbAseActionPerformed
        getPrincipalVnt().agregaVentanaAlumno(matActual,4);
    }//GEN-LAST:event_btnAbAseActionPerformed

    /** Abre un PopMenu (control pmnuOpcs) si es el evento popUpTrigger en el control jtRegs
     *   para dar opiones de modificar, quitar o ver registro (segun aplique) del registro 
     *   sobre el que se hizo click
     * @param evt El MouseEvent que genero el evento
     */
    private void jtRegsMouseClicked(java.awt.event.MouseEvent evt) {//GEN-FIRST:event_jtRegsMouseClicked
        int col=jtRegs.columnAtPoint(evt.getPoint());
        int fila=jtRegs.rowAtPoint(evt.getPoint());       
        int filaClave=0;
        for(int i=0;i<jtRegs.getColumnCount();i++)
            if(jtRegs.getColumnName(i).equals("Clave")){
                filaClave=i; break;
            }
        if (fila>-1 && col>-1 && jtRegs.getValueAt(fila,filaClave)!=null)
            mostrarPopupMenu(evt,fila,""+jtRegs.getValueAt(fila,filaClave));
    }//GEN-LAST:event_jtRegsMouseClicked

    /** Abre un PopMenu (control pmnuOpcs) si es el evento popUpTrigger en el control jtRegs
     *   para dar opiones de modificar, quitar o ver registro (segun aplique) del registro 
     *   sobre el que se hizo click
     * @param evt El MouseEvent que genero el evento
     */
    private void jtRegsMousePressed(java.awt.event.MouseEvent evt) {//GEN-FIRST:event_jtRegsMousePressed
        int col=jtRegs.columnAtPoint(evt.getPoint());
        int fila=jtRegs.rowAtPoint(evt.getPoint());       
        int filaClave=0;
        for(int i=0;i<jtRegs.getColumnCount();i++)
            if(jtRegs.getColumnName(i).equals("Clave")){
                filaClave=i; break;
            }
        if (fila>-1 && col>-1 && jtRegs.getValueAt(fila,filaClave)!=null)
            mostrarPopupMenu(evt,fila,""+jtRegs.getValueAt(fila,filaClave));
    }//GEN-LAST:event_jtRegsMousePressed

    /** Abre un PopMenu (control pmnuOpcs) si es el evento popUpTrigger en el control jtRegs
     *   para dar opiones de modificar, quitar o ver registro (segun aplique) del registro 
     *   sobre el que se hizo click
     * @param evt El MouseEvent que genero el evento
     */
    private void jtRegsMouseReleased(java.awt.event.MouseEvent evt) {//GEN-FIRST:event_jtRegsMouseReleased
        int col=jtRegs.columnAtPoint(evt.getPoint());
        int fila=jtRegs.rowAtPoint(evt.getPoint());       
        int filaClave=0;
        for(int i=0;i<jtRegs.getColumnCount();i++)
            if(jtRegs.getColumnName(i).equals("Clave")){
                filaClave=i; break;
            }
        if (fila>-1 && col>-1 && jtRegs.getValueAt(fila,filaClave)!=null)
            mostrarPopupMenu(evt,fila,""+jtRegs.getValueAt(fila,filaClave));
    }//GEN-LAST:event_jtRegsMouseReleased

    /** Prepara un reporte con los datos del registro actual y abre el cuadro de 
     *   dialogo para enviarlo a imprimir
     * @param evt El ActionEvent que genero el evento
     */
    private void btnImprimirActionPerformed(java.awt.event.ActionEvent evt) {//GEN-FIRST:event_btnImprimirActionPerformed
        Map<String,String> parametros = new HashMap<String,String>();
        ArrayList campos=new ArrayList();
        ImpDesAca campo;
        DefaultTableModel modelo=(DefaultTableModel)jtRegs.getModel();
        if(modelo.getRowCount()<1){
            muestraMensaje("No se puede imprimir","No hay registros para el alumno",TipoMensaje.INFORMACION);
            return;
        }
        String[] datos=database.Consultas.consultaUnCampo("select datosinst.*,fing from alumno,datosinst where matricula='"+matActual+"';",false);
        if(datos==null){
            muestraMensaje("Error al consultar datos de cabecera de reporte",database.Consultas.obtenError(),TipoMensaje.ERROR);
            return;
        }
        parametros.put("NOMINST",datos[0]);
        parametros.put("UNIACAESC",datos[1]);
        parametros.put("AREAPROG",datos[2]);
        parametros.put("ALUMNO",txtNomAl.getText());
        parametros.put("MATRICULA",matActual);
        parametros.put("FCHAING",datos[3]);
        parametros.put("TOTMATERIAS",""+jtRegs.getRowCount());
        parametros.put("PROMGRL",txtPromG.getText());
        parametros.put("LOGO",null);
        for(int s=0;s<modelo.getRowCount();s++){
            String grupo=""+modelo.getValueAt(s,4)+modelo.getValueAt(s,5)+" "+modelo.getValueAt(s,2);
            campo=new ImpDesAca(""+modelo.getValueAt(s,0),""+modelo.getValueAt(s,1),grupo,""+modelo.getValueAt(s,6));
            campos.add(campo);
        }
        enviarImpresion("Registro academico alumno"+matActual,4,parametros,campos);
    }//GEN-LAST:event_btnImprimirActionPerformed

    /** Crea el popmenu control (pmnuOpcs) con las opciones a desplegar
     * Modificar, Quitar o Ver registro para aplicar a la tabla de materias registradas
     */
    private void crearPopMenu(){        
        pmnuOpcs=new javax.swing.JPopupMenu();
        mnu1 = new javax.swing.JMenuItem("Modificar");
        mnu1.setActionCommand("Modificar");
        mnu1.addActionListener(this);
        mnu2 = new javax.swing.JMenuItem("Quitar");
        mnu2.setActionCommand("Quitar");
        mnu2.addActionListener(this);
        mnu3 = new javax.swing.JMenuItem("Ver registro");
        mnu3.setActionCommand("Ver registro");
        mnu3.addActionListener(this);
        pmnuOpcs.add(mnu1);
        pmnuOpcs.add(mnu2);
        pmnuOpcs.add(mnu3);
    }
    
    /** Verifica si el evento es un popUpTrigger y en que fila (tabla jtRegs) se hizo click
     *   en caso de ser popUpTrigger y una fila valida posiciona y muestra un menu emergente
     * @param evt El MouseEvent que genero el evento
     * @param fila La fila en la que se hizo click
     * @param tx El texto de la fila en al que se hizo click
     */
    private void mostrarPopupMenu(java.awt.event.MouseEvent evt, int fila, String tx){
        java.awt.Component[] mns;
        boolean bandera=true;
        if (evt.isPopupTrigger()){
            mns =pmnuOpcs.getComponents();
            for(int y=0;y<mns.length;y++){
                String tmp=((javax.swing.JMenuItem)mns[y]).getText();                
                ((javax.swing.JMenuItem)mns[y]).setActionCommand(tmp+":"+tx+":"+fila);
            }
            try{ Integer.parseInt(tx); bandera=true; }
            catch(NumberFormatException nbfExc){ bandera=false; }
            mnu1.setVisible(bandera);
            mnu2.setVisible(bandera);
            mnu3.setVisible(!bandera);
            jtRegs.setRowSelectionInterval(fila, fila);
            pmnuOpcs.show(evt.getComponent(),evt.getX(),evt.getY());
        }
    }

    /** Es la implementacion del action command de los menus del menu emergente 
     *   (control pmnuOpcs) de acuerdo al ActionCommand de evento llama al metodo 
     *   de la accion en la cual se hizo click (modificar, quitar o ver registro)
     * @param evt El ActionEvent que genero el evento
     */
    public void actionPerformed(java.awt.event.ActionEvent evt){
        java.util.StringTokenizer toks=new java.util.StringTokenizer(evt.getActionCommand(),":");
        String accion=toks.nextToken();
        String clavetxt=toks.nextToken();
        int clave=0; //, fila=0;
        try{ clave=Integer.parseInt(clavetxt); }
            //fila=Integer.parseInt(toks.nextToken()); }
        catch(NumberFormatException nbfExc){}
        if(accion.equals("Modificar")) modificaRegistro(clave);
        else if(accion.equals("Quitar")) quitarRegistro(clave);
        else{
            getPrincipalVnt().agregaVentanaAlumno(matActual,3);
            alumnos.FrameDesGru rgd=((alumnos.FrameDesGru)obtenVentana("Registro personal de desempeño en grupo alumno "+matActual));
            rgd.cargaDatosDesGru(clavetxt);
        }
        pmnuOpcs.setVisible(false);
    }
    
    // Variables declaration - do not modify//GEN-BEGIN:variables
    private javax.swing.JButton btnAbAse;
    private javax.swing.JButton btnAbRegDesG;
    private javax.swing.JButton btnAbRegPer;
    private javax.swing.JButton btnAgreReg;
    private javax.swing.JButton btnCarga;
    private javax.swing.JButton btnEliReg;
    private javax.swing.JButton btnImprimir;
    private javax.swing.JButton btnModReg;
    private javax.swing.JLabel jLabel1;
    private javax.swing.JLabel jLabel3;
    private javax.swing.JPanel jPanel1;
    private javax.swing.JPanel jPanel2;
    private javax.swing.JPanel jPanel3;
    private javax.swing.JScrollPane jScrollPane3;
    private javax.swing.JToolBar jToolBar1;
    private javax.swing.JTable jtRegs;
    private javax.swing.JLabel lblNMats;
    private javax.swing.JPanel pnlRegs;
    private javax.swing.JTextField txtMat;
    private javax.swing.JTextField txtNomAl;
    private javax.swing.JTextField txtPromG;
    // End of variables declaration//GEN-END:variables
    
}
