/*
 * FrameDesGru.java
 *  Ventana para dar seguimiento al desempeño de un alumno dentro de un grupo
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

import reportes.ImpDesGru;
import definiciones.TipoMensaje;
import grupos.FrameGrupo;
import grupos.FrmRegEval;
import iconos.Iconos;
import java.util.ArrayList;
import java.util.HashMap;
import java.util.Map;
import javax.swing.table.DefaultTableModel;
import operaciones.Datos;

/**  Es una ventana interna (JInternalFrame) la cual sirve para mostrar 
 *  el desempeño de un alumno, dentro del grupo al cual pertenesca, muestra 
 *  los resultados obtenidos para cada rubro de evaluacion programado en ese grupo
 * 
 * @author Pedro Cardoso Rodríguez
 */
public class FrameDesGru extends sistema.ModeloFrameInterno implements java.awt.event.ActionListener {
    
     /** La matricula del alumno del cual se muestran los registros actualmente (vacio para ninguno) */
    private String matActual;
     /** La clave del grupo del cual se muestran los registros actualmente (vacio para ninguno) */
    private String clvgActual;
     /** El nombre de la materia correspondiente al grupo actual (vacio para ninguno) */
    private String nomMatActual;
    /** Clave de pertenencia del alumno al grupo actual (base de datos tabla: Pertenece) */
    private int clvper;
     /** Vector con las claves de los grupos en los cuales esta registrado el alumno */
    private String[] clvsMats;
    /** Menu pop up para la tabla de rubros de evaluacion con opciones para modificar registros */
    private javax.swing.JPopupMenu pmnuOpcs;
    
    /** Crea una nueva ventana FrameDesGru
     * @param ventana Referencia a la ventana principal contenedora 
     *   (sistema.FramePrincipal)
     */
    public FrameDesGru(sistema.FramePrincipal ventana){
        super(ventana,"frmregdesgru.png");
        initComponents();
        btnCarga.setIcon(Iconos.getIcono("cargar.png")); 
        btnCarGrupo.setIcon(Iconos.getIcono("cargar.png")); 
        btnAbRegPer.setIcon(Iconos.getIcono("frmregistro.png")); 
        btnAbRegPer.setText("");
        btnDesAca.setIcon(Iconos.getIcono("frmregdesaca.png")); 
        btnDesAca.setText("");
        btnImprimir.setIcon(Iconos.getIcono("impresora.png"));
        btnImprimir.setText("");
        btnAsesorias.setIcon(Iconos.getIcono("frmasesorias.png")); 
        btnAsesorias.setText("");
        btnModReg.setIcon(Iconos.getIcono("modifica.png"));
        crearPopMenu();
        matActual=""; clvgActual=""; nomMatActual="Grupo:";
    }
    
    /** Carga los datos (nombre y matricula) en los cuales esta registrado un alumno
     *   (si el alumno existe y se carga se llama al metodo que carga la lista de grupos en los cuales esta registrado)
     * @param newMat La matricula del alumno a cargar
     * @return true si se cargaron los datos, false si ocurrio un error al consultar la
     *   base de datos o si no existe alumno con la matricula recibida en el parametro
     */
    public boolean cargaDatosAlumno(String newMat){        
        String sentencia;
        String[] datos; 
        sentencia="select nom,appat,apmat from alumno where alumno.matricula='"+newMat+"'";        
        datos=database.Consultas.consultaUnCampo(sentencia,true);
        if(datos==null){            
            muestraMensaje("Error al realizar la búsqueda",database.Consultas.obtenError(),TipoMensaje.ERROR);
            return false;
        }
        if(datos[0]==null){            
            muestraMensaje("Resultado de la búsqueda","No se encontraron datos para esa matricula",TipoMensaje.ERROR);
            return false;
        }        
        txtNomAl.setText(datos[0]+" "+datos[1]+" "+datos[2]);
        matActual=newMat;
        btnAbRegPer.setEnabled(true); btnDesAca.setEnabled(true);
        btnAsesorias.setEnabled(true);
        lblGrupoActual.setText("Grupo:"); txtPcentCalFin.setText(""); txtPcentAsis.setText("");
        btnModReg.setEnabled(false); nomMatActual="Grupo:";
        setTitle("Registro personal de desempeño en grupo alumno "+matActual);
        cargaListaGrupos();
        return true;
    }
    
    /** Carga la lista de grupos en los cuales esta registrado un alumno actual */
    private void cargaListaGrupos(){
        String sentencia="select grupos.clvg,grado,grupo,nombre from pertenece,grupos,imparte,materias";
        sentencia+=" where pertenece.matricula='"+matActual+"' and grupos.clvg=pertenece.clvg";
        sentencia+=" and imparte.clvg=grupos.clvg and materias.clvm=imparte.clvm order by perini desc;";
        String[] datos=database.Consultas.consultaLista(sentencia,false);
        jcbGrupos.removeAllItems();
        jcbGrupos.setEnabled(false); 
        btnCarGrupo.setEnabled(false);
        if(datos==null){            
            muestraMensaje("Error al realizar la búsqueda de grupos",database.Consultas.obtenError(),TipoMensaje.ERROR);
            setEnabled(true); return;
        }
        if(datos[0]==null){            
            muestraMensaje("Resultado de la búsqueda","El alumno "+matActual+" no esta registrado en ningun grupo",TipoMensaje.ERROR);
            setEnabled(true); return;
        }
        clvsMats=new String[datos.length];
        for(int k=0;k<datos.length;k++){
            java.util.StringTokenizer toks= new java.util.StringTokenizer(datos[k]);
            clvsMats[k]=toks.nextToken();
            jcbGrupos.addItem(toks.nextToken()+toks.nextToken()+" "+toks.nextToken("\0"));
        }        
        jcbGrupos.setSelectedIndex(0);
        jcbGrupos.setEnabled(true);
        btnCarGrupo.setEnabled(true);
    }
    
    /** Recarga (refresca desde la base de datos) los datos del alumno actual */
    public void actualiza(){
        FrameGrupo ficha;
        cargaDatosAlumno(matActual);
        if(!clvgActual.equals("")){ 
            cargaDatosDesGru(clvgActual);
            ficha=((FrameGrupo)obtenVentana("Ficha de grupo "+clvgActual));
            if(ficha!=null) ficha.actualiza();
        }
    }
    
    /** This method is called from within the constructor to
     * initialize the form.
     * WARNING: Do NOT modify this code. The content of this method is
     * always regenerated by the Form Editor.
     */
    // <editor-fold defaultstate="collapsed" desc="Generated Code">//GEN-BEGIN:initComponents
    private void initComponents() {

        jPanel2 = new javax.swing.JPanel();
        jToolBar1 = new javax.swing.JToolBar();
        btnAbRegPer = new javax.swing.JButton();
        btnDesAca = new javax.swing.JButton();
        btnAsesorias = new javax.swing.JButton();
        btnImprimir = new javax.swing.JButton();
        jPanel1 = new javax.swing.JPanel();
        jLabel1 = new javax.swing.JLabel();
        txtMat = new javax.swing.JTextField();
        btnCarga = new javax.swing.JButton();
        jPanel3 = new javax.swing.JPanel();
        lblIdAlumno = new javax.swing.JLabel();
        jLabel2 = new javax.swing.JLabel();
        jcbGrupos = new javax.swing.JComboBox();
        btnCarGrupo = new javax.swing.JButton();
        txtNomAl = new javax.swing.JTextField();
        jPanel4 = new javax.swing.JPanel();
        jLabel3 = new javax.swing.JLabel();
        jLabel4 = new javax.swing.JLabel();
        jLabel5 = new javax.swing.JLabel();
        jLabel6 = new javax.swing.JLabel();
        jScrollPane2 = new javax.swing.JScrollPane();
        jtRubros = new javax.swing.JTable();
        btnModReg = new javax.swing.JButton();
        txtPcentCalFin = new javax.swing.JTextField();
        txtPcentAsis = new javax.swing.JTextField();
        jLabel7 = new javax.swing.JLabel();
        lblGrupoActual = new javax.swing.JLabel();

        setClosable(true);
        setDefaultCloseOperation(javax.swing.WindowConstants.DO_NOTHING_ON_CLOSE);
        setIconifiable(true);
        setMaximizable(true);
        setResizable(true);
        setTitle("Registro personal de desempeño en grupo");

        jPanel2.setLayout(new java.awt.BorderLayout());

        jToolBar1.setFloatable(false);
        jToolBar1.setRollover(true);

        btnAbRegPer.setText("rp");
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

        btnDesAca.setText("da");
        btnDesAca.setToolTipText("Desempeño academico");
        btnDesAca.setEnabled(false);
        btnDesAca.setFocusable(false);
        btnDesAca.setHorizontalTextPosition(javax.swing.SwingConstants.CENTER);
        btnDesAca.setVerticalTextPosition(javax.swing.SwingConstants.BOTTOM);
        btnDesAca.addActionListener(new java.awt.event.ActionListener() {
            public void actionPerformed(java.awt.event.ActionEvent evt) {
                btnDesAcaActionPerformed(evt);
            }
        });
        jToolBar1.add(btnDesAca);

        btnAsesorias.setText("as");
        btnAsesorias.setToolTipText("Asesorias");
        btnAsesorias.setEnabled(false);
        btnAsesorias.setFocusable(false);
        btnAsesorias.setHorizontalTextPosition(javax.swing.SwingConstants.CENTER);
        btnAsesorias.setVerticalTextPosition(javax.swing.SwingConstants.BOTTOM);
        btnAsesorias.addActionListener(new java.awt.event.ActionListener() {
            public void actionPerformed(java.awt.event.ActionEvent evt) {
                btnAsesoriasActionPerformed(evt);
            }
        });
        jToolBar1.add(btnAsesorias);

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

        jPanel1.setBorder(javax.swing.BorderFactory.createBevelBorder(javax.swing.border.BevelBorder.RAISED));

        jLabel1.setText("Buscar Matricula:");

        btnCarga.setMnemonic('C');
        btnCarga.setText("Cargar");
        btnCarga.addActionListener(new java.awt.event.ActionListener() {
            public void actionPerformed(java.awt.event.ActionEvent evt) {
                btnCargaActionPerformed(evt);
            }
        });

        org.jdesktop.layout.GroupLayout jPanel1Layout = new org.jdesktop.layout.GroupLayout(jPanel1);
        jPanel1.setLayout(jPanel1Layout);
        jPanel1Layout.setHorizontalGroup(
            jPanel1Layout.createParallelGroup(org.jdesktop.layout.GroupLayout.LEADING)
            .add(org.jdesktop.layout.GroupLayout.TRAILING, jPanel1Layout.createSequentialGroup()
                .addContainerGap(207, Short.MAX_VALUE)
                .add(jLabel1)
                .addPreferredGap(org.jdesktop.layout.LayoutStyle.UNRELATED)
                .add(txtMat, org.jdesktop.layout.GroupLayout.PREFERRED_SIZE, 93, org.jdesktop.layout.GroupLayout.PREFERRED_SIZE)
                .addPreferredGap(org.jdesktop.layout.LayoutStyle.RELATED)
                .add(btnCarga, org.jdesktop.layout.GroupLayout.PREFERRED_SIZE, 104, org.jdesktop.layout.GroupLayout.PREFERRED_SIZE)
                .addContainerGap())
        );
        jPanel1Layout.setVerticalGroup(
            jPanel1Layout.createParallelGroup(org.jdesktop.layout.GroupLayout.LEADING)
            .add(jPanel1Layout.createParallelGroup(org.jdesktop.layout.GroupLayout.BASELINE)
                .add(txtMat, org.jdesktop.layout.GroupLayout.PREFERRED_SIZE, org.jdesktop.layout.GroupLayout.DEFAULT_SIZE, org.jdesktop.layout.GroupLayout.PREFERRED_SIZE)
                .add(jLabel1)
                .add(btnCarga))
        );

        jToolBar1.add(jPanel1);

        jPanel2.add(jToolBar1, java.awt.BorderLayout.NORTH);

        lblIdAlumno.setHorizontalAlignment(javax.swing.SwingConstants.RIGHT);
        lblIdAlumno.setText("Registro personal desempeño en grupo de alumno:");

        jLabel2.setText("Grupos: ");

        jcbGrupos.setEnabled(false);

        btnCarGrupo.setText("Ver");
        btnCarGrupo.setToolTipText("Ver rubros del grupo seleccionado");
        btnCarGrupo.setEnabled(false);
        btnCarGrupo.addActionListener(new java.awt.event.ActionListener() {
            public void actionPerformed(java.awt.event.ActionEvent evt) {
                btnCarGrupoActionPerformed(evt);
            }
        });

        txtNomAl.setEditable(false);

        org.jdesktop.layout.GroupLayout jPanel3Layout = new org.jdesktop.layout.GroupLayout(jPanel3);
        jPanel3.setLayout(jPanel3Layout);
        jPanel3Layout.setHorizontalGroup(
            jPanel3Layout.createParallelGroup(org.jdesktop.layout.GroupLayout.LEADING)
            .add(jPanel3Layout.createSequentialGroup()
                .addContainerGap()
                .add(jPanel3Layout.createParallelGroup(org.jdesktop.layout.GroupLayout.LEADING)
                    .add(jPanel3Layout.createSequentialGroup()
                        .add(lblIdAlumno)
                        .addPreferredGap(org.jdesktop.layout.LayoutStyle.RELATED)
                        .add(txtNomAl, org.jdesktop.layout.GroupLayout.PREFERRED_SIZE, 255, org.jdesktop.layout.GroupLayout.PREFERRED_SIZE))
                    .add(jPanel3Layout.createSequentialGroup()
                        .add(10, 10, 10)
                        .add(jLabel2)
                        .addPreferredGap(org.jdesktop.layout.LayoutStyle.RELATED)
                        .add(jcbGrupos, org.jdesktop.layout.GroupLayout.PREFERRED_SIZE, 374, org.jdesktop.layout.GroupLayout.PREFERRED_SIZE)
                        .addPreferredGap(org.jdesktop.layout.LayoutStyle.RELATED)
                        .add(btnCarGrupo)))
                .addContainerGap(81, Short.MAX_VALUE))
        );
        jPanel3Layout.setVerticalGroup(
            jPanel3Layout.createParallelGroup(org.jdesktop.layout.GroupLayout.LEADING)
            .add(jPanel3Layout.createSequentialGroup()
                .addContainerGap(org.jdesktop.layout.GroupLayout.DEFAULT_SIZE, Short.MAX_VALUE)
                .add(jPanel3Layout.createParallelGroup(org.jdesktop.layout.GroupLayout.BASELINE)
                    .add(lblIdAlumno)
                    .add(txtNomAl, org.jdesktop.layout.GroupLayout.PREFERRED_SIZE, org.jdesktop.layout.GroupLayout.DEFAULT_SIZE, org.jdesktop.layout.GroupLayout.PREFERRED_SIZE))
                .addPreferredGap(org.jdesktop.layout.LayoutStyle.RELATED)
                .add(jPanel3Layout.createParallelGroup(org.jdesktop.layout.GroupLayout.BASELINE)
                    .add(jLabel2)
                    .add(jcbGrupos, org.jdesktop.layout.GroupLayout.PREFERRED_SIZE, org.jdesktop.layout.GroupLayout.DEFAULT_SIZE, org.jdesktop.layout.GroupLayout.PREFERRED_SIZE)
                    .add(btnCarGrupo)))
        );

        jPanel2.add(jPanel3, java.awt.BorderLayout.CENTER);

        getContentPane().add(jPanel2, java.awt.BorderLayout.NORTH);

        jLabel3.setText("Porcentaje de calificación final obtenido: ");

        jLabel4.setText("%");

        jLabel5.setText("Porcentaje de asistencias: ");

        jLabel6.setText("%");

        jtRubros.setAutoCreateRowSorter(true);
        jtRubros.setModel(new javax.swing.table.DefaultTableModel(
            new Object [][] {

            },
            new String [] {
                "Clave", "Tipo", "Descripcion", "Fecha solicita", "Fecha califica", "Calificacion", "Valor Porcentual", "Observaciones"
            }
        ));
        jtRubros.setAutoResizeMode(javax.swing.JTable.AUTO_RESIZE_OFF);
        jtRubros.setEnabled(false);
        jtRubros.addMouseListener(new java.awt.event.MouseAdapter() {
            public void mouseClicked(java.awt.event.MouseEvent evt) {
                jtRubrosMouseClicked(evt);
            }
            public void mousePressed(java.awt.event.MouseEvent evt) {
                jtRubrosMousePressed(evt);
            }
            public void mouseReleased(java.awt.event.MouseEvent evt) {
                jtRubrosMouseReleased(evt);
            }
        });
        jScrollPane2.setViewportView(jtRubros);

        btnModReg.setMnemonic('M');
        btnModReg.setText("Modificar");
        btnModReg.setEnabled(false);
        btnModReg.addActionListener(new java.awt.event.ActionListener() {
            public void actionPerformed(java.awt.event.ActionEvent evt) {
                btnModRegActionPerformed(evt);
            }
        });

        txtPcentCalFin.setEditable(false);

        txtPcentAsis.setEditable(false);

        jLabel7.setText("Lista de rubros por los que se califica");

        lblGrupoActual.setHorizontalAlignment(javax.swing.SwingConstants.CENTER);
        lblGrupoActual.setText("Grupo:");
        lblGrupoActual.setToolTipText("Click para abrir la ficha del grupo");
        lblGrupoActual.addMouseListener(new java.awt.event.MouseAdapter() {
            public void mouseClicked(java.awt.event.MouseEvent evt) {
                lblGrupoActualMouseClicked(evt);
            }
            public void mouseEntered(java.awt.event.MouseEvent evt) {
                lblGrupoActualMouseEntered(evt);
            }
            public void mouseExited(java.awt.event.MouseEvent evt) {
                lblGrupoActualMouseExited(evt);
            }
        });

        org.jdesktop.layout.GroupLayout jPanel4Layout = new org.jdesktop.layout.GroupLayout(jPanel4);
        jPanel4.setLayout(jPanel4Layout);
        jPanel4Layout.setHorizontalGroup(
            jPanel4Layout.createParallelGroup(org.jdesktop.layout.GroupLayout.LEADING)
            .add(org.jdesktop.layout.GroupLayout.TRAILING, jPanel4Layout.createSequentialGroup()
                .addContainerGap()
                .add(jPanel4Layout.createParallelGroup(org.jdesktop.layout.GroupLayout.TRAILING)
                    .add(org.jdesktop.layout.GroupLayout.LEADING, jScrollPane2, org.jdesktop.layout.GroupLayout.DEFAULT_SIZE, 574, Short.MAX_VALUE)
                    .add(org.jdesktop.layout.GroupLayout.LEADING, lblGrupoActual, org.jdesktop.layout.GroupLayout.DEFAULT_SIZE, 574, Short.MAX_VALUE)
                    .add(org.jdesktop.layout.GroupLayout.LEADING, jPanel4Layout.createParallelGroup(org.jdesktop.layout.GroupLayout.TRAILING, false)
                        .add(org.jdesktop.layout.GroupLayout.LEADING, jPanel4Layout.createSequentialGroup()
                            .add(10, 10, 10)
                            .add(jLabel7)
                            .addPreferredGap(org.jdesktop.layout.LayoutStyle.RELATED, org.jdesktop.layout.GroupLayout.DEFAULT_SIZE, Short.MAX_VALUE)
                            .add(btnModReg))
                        .add(org.jdesktop.layout.GroupLayout.LEADING, jPanel4Layout.createSequentialGroup()
                            .add(jLabel3)
                            .addPreferredGap(org.jdesktop.layout.LayoutStyle.RELATED)
                            .add(txtPcentCalFin, org.jdesktop.layout.GroupLayout.PREFERRED_SIZE, 50, org.jdesktop.layout.GroupLayout.PREFERRED_SIZE)
                            .addPreferredGap(org.jdesktop.layout.LayoutStyle.RELATED)
                            .add(jLabel6)
                            .addPreferredGap(org.jdesktop.layout.LayoutStyle.RELATED)
                            .add(jLabel5)
                            .addPreferredGap(org.jdesktop.layout.LayoutStyle.RELATED)
                            .add(txtPcentAsis, org.jdesktop.layout.GroupLayout.PREFERRED_SIZE, 50, org.jdesktop.layout.GroupLayout.PREFERRED_SIZE)
                            .addPreferredGap(org.jdesktop.layout.LayoutStyle.RELATED)
                            .add(jLabel4))))
                .addContainerGap())
        );
        jPanel4Layout.setVerticalGroup(
            jPanel4Layout.createParallelGroup(org.jdesktop.layout.GroupLayout.LEADING)
            .add(jPanel4Layout.createSequentialGroup()
                .addContainerGap()
                .add(lblGrupoActual)
                .addPreferredGap(org.jdesktop.layout.LayoutStyle.RELATED)
                .add(jPanel4Layout.createParallelGroup(org.jdesktop.layout.GroupLayout.BASELINE)
                    .add(jLabel3)
                    .add(txtPcentCalFin, org.jdesktop.layout.GroupLayout.PREFERRED_SIZE, org.jdesktop.layout.GroupLayout.DEFAULT_SIZE, org.jdesktop.layout.GroupLayout.PREFERRED_SIZE)
                    .add(jLabel6)
                    .add(jLabel5)
                    .add(txtPcentAsis, org.jdesktop.layout.GroupLayout.PREFERRED_SIZE, org.jdesktop.layout.GroupLayout.DEFAULT_SIZE, org.jdesktop.layout.GroupLayout.PREFERRED_SIZE)
                    .add(jLabel4))
                .addPreferredGap(org.jdesktop.layout.LayoutStyle.RELATED)
                .add(jPanel4Layout.createParallelGroup(org.jdesktop.layout.GroupLayout.BASELINE)
                    .add(jLabel7)
                    .add(btnModReg))
                .addPreferredGap(org.jdesktop.layout.LayoutStyle.RELATED)
                .add(jScrollPane2, org.jdesktop.layout.GroupLayout.DEFAULT_SIZE, 207, Short.MAX_VALUE)
                .addContainerGap())
        );

        getContentPane().add(jPanel4, java.awt.BorderLayout.CENTER);

        pack();
    }// </editor-fold>//GEN-END:initComponents

    /** Valida la matricula ingresada en el campo Buscar Matricula (control txtMat)
     *   y si es valida (8 digitos) llama al metodo cargaDatosAlumno para buscar y 
     *   cargar los datos del alumno correspondiente
     * @param evt El ActionEvent que genero el evento
     */
    private void btnCargaActionPerformed(java.awt.event.ActionEvent evt) {//GEN-FIRST:event_btnCargaActionPerformed
        String mat=txtMat.getText();
        if(!Datos.valMatricula(mat)){ // validar la matricula ingresada
            muestraMensaje("Error en el parámetro","Matricula invalida",TipoMensaje.ERROR);
            return;
        }
        jtRubros.setModel(new javax.swing.table.DefaultTableModel());
        btnImprimir.setEnabled(false);
        if(getTitle().equals("Registro personal de desempeño en grupo alumno "+mat)){
            clvgActual=""; actualiza(); 
        }
        else if(existeVentana("Registro personal de desempeño en grupo alumno "+mat,true)){ 
            txtMat.setText(matActual); return; 
        }
        else{ clvgActual=""; cargaDatosAlumno(mat); }
    }//GEN-LAST:event_btnCargaActionPerformed

    /** Llama al metodo agregaVentanaAlumno de la ventana principal (sistema.FramePrincipal)
     *   con los parametros adecuados para abrir la ventana con el registro personal 
     *   correspondiente al alumno actual
     * @param evt El ActionEvent que genero el evento
     */
    private void btnAbRegPerActionPerformed(java.awt.event.ActionEvent evt) {//GEN-FIRST:event_btnAbRegPerActionPerformed
        getPrincipalVnt().agregaVentanaAlumno(matActual,1);
    }//GEN-LAST:event_btnAbRegPerActionPerformed

    /** Llama al metodo agregaVentanaAlumno de la ventana principal (sistema.FramePrincipal)
     *   con los parametros adecuados para abrir la ventana con el registro de asesorias
     *   de tesis correspondiente al alumno actual
     * @param evt El ActionEvent que genero el evento
     */
    private void btnAsesoriasActionPerformed(java.awt.event.ActionEvent evt) {//GEN-FIRST:event_btnAsesoriasActionPerformed
        getPrincipalVnt().agregaVentanaAlumno(matActual,4);
    }//GEN-LAST:event_btnAsesoriasActionPerformed

    /** Llama al metodo agregaVentanaAlumno de la ventana principal (sistema.FramePrincipal)
     *   con los parametros adecuados para abrir la ventana con el registro de desempeño
     *   academico correspondiente al alumno actual
     * @param evt El ActionEvent que genero el evento
     */
    private void btnDesAcaActionPerformed(java.awt.event.ActionEvent evt) {//GEN-FIRST:event_btnDesAcaActionPerformed
        getPrincipalVnt().agregaVentanaAlumno(matActual,2);
    }//GEN-LAST:event_btnDesAcaActionPerformed

    /** Llama al metodo que carga los rubros de evaluacion de un grupo pasandole el 
     *   parametro para que carge los datos del grupos seleccionado en la 
     *   lista Grupos (control jcbGrupos)
     * @param evt El ActionEvent que genero el evento
     */
    private void btnCarGrupoActionPerformed(java.awt.event.ActionEvent evt) {//GEN-FIRST:event_btnCarGrupoActionPerformed
        cargaDatosDesGru(clvsMats[jcbGrupos.getSelectedIndex()]);
    }//GEN-LAST:event_btnCarGrupoActionPerformed

    /** Carga la lista de rubros de evaluacion programados para un grupo con los datos de
     *   desempeño para cada rubro obtenidos por el alumno actual
     * @param clvg clave del grupo a del cual se desea cargar los rubros
     */
    public void cargaDatosDesGru(String clvg){
        DefaultTableModel modelo;
        String[] datos;
        int sesiones;
        float aux=0f;
        int prcCalif=0;
        String sentencia;
        java.text.DecimalFormat dc=new java.text.DecimalFormat("##.##");
        String[] tipos={"Tarea","Investigacion","Exposicion","Proyecto","Practica","Examen parcial",
          "Examen final","Examen ordinario","Examen extraordinario","Calificacion final","Otro"};
        datos=database.Consultas.consultaUnCampo("select clvper from pertenece where matricula='"+matActual+"' and clvg='"+clvg+"';",true);
        if(datos==null){
            muestraMensaje("Error al realizar la búsqueda de datos",database.Consultas.obtenError(),TipoMensaje.ERROR);
            return;
        }
        clvper=Integer.parseInt(datos[0]);
        sentencia="select valor from pertenece,asistencias,sesiones where matricula='"+matActual;
        sentencia+="' and clvg='"+clvg+"' and pertenece.clvper=asistencias.clvper and ";
        sentencia+="asistencias.clvses=sesiones.clvses and timediff(now(),sesiones.fechayhora)>'0000-00-00 00:00:00';";
        // consultar porcentaje de asistencias
        modelo=database.Consultas.consTipoTable(sentencia,false);
        if(modelo==null)
            muestraMensaje("Error al realizar la búsqueda de asistencias",database.Consultas.obtenError(),TipoMensaje.ERROR);
        else if(modelo.getRowCount()>0){
            sesiones=modelo.getRowCount();
            for(int y=0;y<sesiones;y++){                 
                char valor=(""+modelo.getValueAt(y,0)).charAt(0);
                if(valor!='I'&&valor!='_') aux++; 
            }
            if(aux>0) txtPcentAsis.setText(dc.format(aux/((float)sesiones/100f)));
            else txtPcentAsis.setText("0.00");
        }
        else txtPcentAsis.setText("");
        // consultar rubros por los cuales se califica
        sentencia="select rubroscalif.clvru as Clave,Tipo,Descripcion,FchaSol as 'Fecha solicita',FchaCal as";
        sentencia+=" 'Fecha califica',calif as Calificacion, valorp as 'Valor porcentual',Observaciones from ";
        sentencia+="realiza,rubroscalif,pertenece where rubroscalif.clvru=realiza.clvru and realiza.clvper=";
        sentencia+="pertenece.clvper and matricula='"+matActual+"' and clvg='"+clvg+"' order by FchaCal;";
        modelo=database.Consultas.consTipoTable(sentencia,false);
        if(modelo==null){
            muestraMensaje("Error al realizar la búsqueda de rubros",database.Consultas.obtenError(),TipoMensaje.ERROR);
            return;
        }        
        jtRubros.setModel(modelo); 
        for(int f=0;f<jtRubros.getRowCount();f++){
            int auxt=Integer.parseInt(""+jtRubros.getValueAt(f,1));
            jtRubros.setValueAt(tipos[auxt-1],f,1);
            prcCalif+=Integer.parseInt(""+jtRubros.getValueAt(f,6));
        }
        btnImprimir.setEnabled(modelo.getRowCount()>0);
        jtRubros.getColumn("Clave").setPreferredWidth(40); 
        jtRubros.getColumn("Tipo").setPreferredWidth(110);
        jtRubros.getColumn("Descripcion").setPreferredWidth(205);
        jtRubros.getColumn("Calificacion").setPreferredWidth(70); 
        jtRubros.getColumn("Fecha solicita").setPreferredWidth(130);
        jtRubros.getColumn("Fecha califica").setPreferredWidth(130);
        jtRubros.getColumn("Valor porcentual").setPreferredWidth(90);
        jtRubros.getColumn("Observaciones").setPreferredWidth(100);
        txtPcentCalFin.setText(""+prcCalif); btnModReg.setEnabled(true);
        for(int j=0;j<clvsMats.length;j++)
            if(clvsMats[j].equals(clvg)){
                jcbGrupos.setSelectedIndex(j);
                lblGrupoActual.setText(""+jcbGrupos.getSelectedItem());
                nomMatActual=lblGrupoActual.getText();
                break;
            }        
        clvgActual=clvg;
    }
    
    /** Resalta el texto de la etiqueta de grupo actual (control lblGrupoActual) al entrar el puntero 
     *   del mouse dentro de su area para mostrarlo como enlace subrayado y en color de texto azul 
     *   para indicar que al hacer click sobre el se abre el registro del grupo correspondiente
     * @param evt El MouseEvent que genero el evento
     */
    private void lblGrupoActualMouseEntered(java.awt.event.MouseEvent evt) {//GEN-FIRST:event_lblGrupoActualMouseEntered
        lblGrupoActual.setForeground(new java.awt.Color(0, 0, 255));
        lblGrupoActual.setText("<html><u> "+nomMatActual+"</u></html>");
    }//GEN-LAST:event_lblGrupoActualMouseEntered

    /** Quita el resalta del texto de la etiqueta de grupo actual (control lblGrupoActual) 
     *   al salir el puntero del mouse dentro de su area 
     * @param evt El MouseEvent que genero el evento
     */
    private void lblGrupoActualMouseExited(java.awt.event.MouseEvent evt) {//GEN-FIRST:event_lblGrupoActualMouseExited
        lblGrupoActual.setForeground(new java.awt.Color(0, 0, 0));
        lblGrupoActual.setText(nomMatActual);
    }//GEN-LAST:event_lblGrupoActualMouseExited

    /** Llama al metodo agregaVentanaGrupo de la ventana principal (sistema.FramePrincipal)
     *   con los parametros adecuados para abrir la ventana con el registro del grupo
     *   correspondiente al grupo mostrado actual
     * @param evt El MouseEvent que genero el evento
     */
    private void lblGrupoActualMouseClicked(java.awt.event.MouseEvent evt) {//GEN-FIRST:event_lblGrupoActualMouseClicked
        if(clvgActual.equals("")) return;
        getPrincipalVnt().agregaVentanaGrupo(clvgActual,1);
    }//GEN-LAST:event_lblGrupoActualMouseClicked

    /** Pide la clave de un registro de la tabla de rubros para llamar al metodo 
     *   modificaRegistro para cargar y modificar sus datos
     * @param evt El ActionEvent que genero el evento
     */
    private void btnModRegActionPerformed(java.awt.event.ActionEvent evt) {//GEN-FIRST:event_btnModRegActionPerformed
        DefaultTableModel modelo=(DefaultTableModel)jtRubros.getModel();
        int clave;
        int filas=modelo.getRowCount();
        boolean bandera=false;
        if(filas==0) return;
        clave=pideEntero("Modificar un rubro de desempeño en grupo", "Ingrese al clave del registro que deseas modificar");
        for(int i=0;i<filas;i++){            
            if(Integer.parseInt(""+modelo.getValueAt(i,0))==clave){                
                modificaRegistro(clave);
                bandera=true; break;
            }
        }
        if(!bandera)
            muestraMensaje("No se pudo realizar la acción", "No se encontro registro con la clave: "+clave,TipoMensaje.INFORMACION);
    }//GEN-LAST:event_btnModRegActionPerformed

    /** Abre un PopMenu (control pmnuOpcs) si es el evento popUpTrigger en el control jtRubros
     *   para dar opiones de modificar el registro sobre el que se hizo click
     * @param evt El MouseEvent que genero el evento
     */
    private void jtRubrosMouseClicked(java.awt.event.MouseEvent evt) {//GEN-FIRST:event_jtRubrosMouseClicked
        int col=jtRubros.columnAtPoint(evt.getPoint());
        int fila=jtRubros.rowAtPoint(evt.getPoint());       
        int filaClave=0;
        for(int i=0;i<jtRubros.getColumnCount();i++)
            if(jtRubros.getColumnName(i).equals("Clave")){
                filaClave=i; break;
            }
        if (fila>-1 && col>-1 && jtRubros.getValueAt(fila,filaClave)!=null)
            mostrarPopupMenu(evt,fila,""+jtRubros.getValueAt(fila,filaClave));
    }//GEN-LAST:event_jtRubrosMouseClicked

    /** Abre un PopMenu (control pmnuOpcs) si es el evento popUpTrigger en el control jtRubros
     *   para dar opiones de modificar el registro sobre el que se hizo click
     * @param evt El MouseEvent que genero el evento
     */
    private void jtRubrosMousePressed(java.awt.event.MouseEvent evt) {//GEN-FIRST:event_jtRubrosMousePressed
        int col=jtRubros.columnAtPoint(evt.getPoint());
        int fila=jtRubros.rowAtPoint(evt.getPoint());       
        int filaClave=0;
        for(int i=0;i<jtRubros.getColumnCount();i++)
            if(jtRubros.getColumnName(i).equals("Clave")){
                filaClave=i; break;
            }
        if (fila>-1 && col>-1 && jtRubros.getValueAt(fila,filaClave)!=null)
            mostrarPopupMenu(evt,fila,""+jtRubros.getValueAt(fila,filaClave));
    }//GEN-LAST:event_jtRubrosMousePressed

    /** Abre un PopMenu (control pmnuOpcs) si es el evento popUpTrigger en el control jtRubros
     *   para dar opiones de modificar el registro sobre el que se hizo click
     * @param evt El MouseEvent que genero el evento
     */
    private void jtRubrosMouseReleased(java.awt.event.MouseEvent evt) {//GEN-FIRST:event_jtRubrosMouseReleased
        int col=jtRubros.columnAtPoint(evt.getPoint());
        int fila=jtRubros.rowAtPoint(evt.getPoint());       
        int filaClave=0;
        for(int i=0;i<jtRubros.getColumnCount();i++)
            if(jtRubros.getColumnName(i).equals("Clave")){
                filaClave=i; break;
            }
        if (fila>-1 && col>-1 && jtRubros.getValueAt(fila,filaClave)!=null)
            mostrarPopupMenu(evt,fila,""+jtRubros.getValueAt(fila,filaClave));
    }//GEN-LAST:event_jtRubrosMouseReleased

    /** Prepara un reporte con los datos listado actualmente y abre el cuadro de 
     *   dialogo para enviarlo a imprimir
     * @param evt El ActionEvent que genero el evento
     */
    private void btnImprimirActionPerformed(java.awt.event.ActionEvent evt) {//GEN-FIRST:event_btnImprimirActionPerformed
        Map<String,String> parametros = new HashMap<String,String>();
        ArrayList campos=new ArrayList();
        ImpDesGru campo;
        DefaultTableModel modelo=(DefaultTableModel)jtRubros.getModel();
        if(modelo.getRowCount()<1){
            muestraMensaje("No se puede imprimir","No hay registros para el alumno",TipoMensaje.INFORMACION);
            return;
        }
        String[] datos=database.Consultas.consultaUnCampo("select * from datosinst;",false);
        if(datos==null){
            muestraMensaje("Error al consultar datos de cabecera de reporte",database.Consultas.obtenError(),TipoMensaje.ERROR);
            return;
        }
        parametros.put("NOMINST",datos[0]);
        parametros.put("UNIACAESC",datos[1]);
        parametros.put("AREAPROG",datos[2]);
        parametros.put("ALUMNO",txtNomAl.getText());
        parametros.put("MATRICULA",matActual);
        parametros.put("GRUPO",nomMatActual+" "+clvgActual.substring(7));
        parametros.put("PORASIS",""+txtPcentAsis.getText());
        parametros.put("LOGO",null);
        for(int s=0;s<modelo.getRowCount();s++){
            String calif=""+modelo.getValueAt(s,5);
            calif=(calif.equals("0.0")?"NP":calif);
            campo=new ImpDesGru(""+modelo.getValueAt(s,0),""+modelo.getValueAt(s,2),""+modelo.getValueAt(s,4),calif);
            campos.add(campo);
        }
        enviarImpresion("Desempeño en grupo alumno"+matActual,5,parametros,campos);
    }//GEN-LAST:event_btnImprimirActionPerformed

    /** Recibe la clave de un registro de rubro de evaluacion para cargarlo en su propia ventana
     *   grupos.FrmRegEval y poder modificarlo (solo datos del desempeño del alumno actual)
     * @param clave La clave del registro de rubro a modificar
     */
    private void modificaRegistro(int clave){        
        DefaultTableModel modelo=(DefaultTableModel)jtRubros.getModel();
        FrmRegEval newRegInd;
        if(!existeVentana("Registro de rubro "+clave+" para alumno "+matActual+" en grupo "+clvgActual,true)){
            String[] datos = new String[modelo.getColumnCount()];
            for(int g=0;g<modelo.getRowCount();g++){
                if(Integer.parseInt(""+modelo.getValueAt(g,0))==clave){
                    for(int j=0;j<datos.length;j++) 
                        datos[j]=""+(modelo.getValueAt(g,j)!=null?modelo.getValueAt(g,j):"");
                    break;
                }
            }
            newRegInd = new FrmRegEval(getPrincipalVnt(),null,this,matActual,clvgActual,clave,datos,clvper);
            agregaVentana(newRegInd);
        }
    }
    
    /** Crea el popmenu control (pmnuOpcs) con la opcion Modificar para aplicar al regsitro sobre el que hizo click */
    private void crearPopMenu(){        
        pmnuOpcs=new javax.swing.JPopupMenu();
        javax.swing.JMenuItem mnu1 = new javax.swing.JMenuItem("Modificar");
        mnu1.setActionCommand("Modificar");
        mnu1.addActionListener(this);
        pmnuOpcs.add(mnu1);
    }
    
    /** Verifica si el evento es un popUpTrigger y en que fila (tabla jtRubros) se hizo click
     *   en caso de ser popUpTrigger y una fila valida posiciona y muestra un menu emergente
     * @param evt El MouseEvent que genero el evento
     * @param fila La fila en la que se hizo click
     * @param tx El texto de la fila en al que se hizo click
     */
    private void mostrarPopupMenu(java.awt.event.MouseEvent evt, int fila, String tx){
        java.awt.Component[] mns;
        if (evt.isPopupTrigger()){
            mns =pmnuOpcs.getComponents();
            for(int y=0;y<mns.length;y++){
                String tmp=((javax.swing.JMenuItem)mns[y]).getText();
                ((javax.swing.JMenuItem)mns[y]).setActionCommand(tmp+":"+tx+":"+fila);
            }
            jtRubros.setRowSelectionInterval(fila, fila);
            pmnuOpcs.show(evt.getComponent(),evt.getX(),evt.getY());
        }
    }

    /** Es la implementacion del action command del menu del menu emergente 
     *   (control pmnuOpcs) llama al metodo modificaRegistro para el registro sobre el que se hizo click
     * @param evt El ActionEvent que genero el evento
     */
    public void actionPerformed(java.awt.event.ActionEvent evt){
        java.util.StringTokenizer toks=new java.util.StringTokenizer(evt.getActionCommand(),":");
        String accion=toks.nextToken();
        int clave=Integer.parseInt(toks.nextToken());
        //int fila=Integer.parseInt(toks.nextToken());
        if(accion.equals("Modificar")) modificaRegistro(clave);
        pmnuOpcs.setVisible(false);
    }
    
    // Variables declaration - do not modify//GEN-BEGIN:variables
    private javax.swing.JButton btnAbRegPer;
    private javax.swing.JButton btnAsesorias;
    private javax.swing.JButton btnCarGrupo;
    private javax.swing.JButton btnCarga;
    private javax.swing.JButton btnDesAca;
    private javax.swing.JButton btnImprimir;
    private javax.swing.JButton btnModReg;
    private javax.swing.JLabel jLabel1;
    private javax.swing.JLabel jLabel2;
    private javax.swing.JLabel jLabel3;
    private javax.swing.JLabel jLabel4;
    private javax.swing.JLabel jLabel5;
    private javax.swing.JLabel jLabel6;
    private javax.swing.JLabel jLabel7;
    private javax.swing.JPanel jPanel1;
    private javax.swing.JPanel jPanel2;
    private javax.swing.JPanel jPanel3;
    private javax.swing.JPanel jPanel4;
    private javax.swing.JScrollPane jScrollPane2;
    private javax.swing.JToolBar jToolBar1;
    private javax.swing.JComboBox jcbGrupos;
    private javax.swing.JTable jtRubros;
    private javax.swing.JLabel lblGrupoActual;
    private javax.swing.JLabel lblIdAlumno;
    private javax.swing.JTextField txtMat;
    private javax.swing.JTextField txtNomAl;
    private javax.swing.JTextField txtPcentAsis;
    private javax.swing.JTextField txtPcentCalFin;
    // End of variables declaration//GEN-END:variables

}
