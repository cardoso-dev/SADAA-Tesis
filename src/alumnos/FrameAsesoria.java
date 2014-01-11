/*
 * FrameAsesoria.java
 *  Ventana para mostrar datos de alumnos tesistas asesorados por el docente
 *    Calendarizacion y seguimientos de sesiones de asesorias
 * Parte de proyecto: SADAA
 * Author: Pedro Cardoso Rdz
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

import database.Actualiza;
import reportes.ImpRegTesis;
import definiciones.TipoMensaje;
import definiciones.TipoRespuesta;
import iconos.Iconos;
import java.util.ArrayList;
import java.util.HashMap;
import java.util.Map;
import java.util.StringTokenizer;
import javax.swing.table.DefaultTableModel;
import operaciones.Datos;
import sistema.ControlDiasSemana;

/**  Es una ventana interna (JInternalFrame) la cual sirve para la creación
 *  y manejo de registros de alumnos tesistas. La ventana tiene tres secciones: datos
 *  de la tesis, sesiones de asesoria programadas y horario semanal.
 * 
 * @author Pedro Cardoso Rodríguez
 */
public class FrameAsesoria extends sistema.ModeloFrameInterno implements java.awt.event.ActionListener {
    
    /**Clave de la tesis en la base de datos (tabla tesis) o -1 si no existe*/
    private int clvTesis;
    /**Matricula actual del alumno del registre que muestre la ventana en un momento dado null si no muestra ninguno*/
    private String matActual;
    /**Menu pop up para la tabla de sesiones de asesoria programadas con opciones para modificar o quitar registros*/
    private javax.swing.JPopupMenu pmnuOpcs;
    /** Referencia al objeto para detectar cambios en el contenido de los controles de texto */
    private ListenChanges listenCh;
    /** Referencia al control de horario semanal para detectar manejar horario semanal de asesorias programadas*/
    private ControlDiasSemana horarioSemanal;
    /** Fecha inicial de asesorias al alumno */
    private String fechaIni;
    /** Guarda la descripcion del ultimo error ocurrido al procesar registros (guardar, actualizar, borrar) */
    private String error;
    
    /** Crea una nueva ventana FrameAsesoria
     * @param ventana Referencia a la ventana principal contenedora 
     *   (sistema.FramePrincipal)
     */
    public FrameAsesoria(sistema.FramePrincipal ventana){
        super(ventana,"frmasesorias.png");
        try {
            initComponents();
            btnGuardar.setIcon(Iconos.getIcono("guardar.png"));
            btnGuardar.setText("");
            btnRgper.setIcon(Iconos.getIcono("frmregistro.png"));
            btnRgper.setText("");
            btnRgDG.setIcon(Iconos.getIcono("frmregdesgru.png"));
            btnRgDG.setText("");
            btnRgDA.setIcon(Iconos.getIcono("frmregdesaca.png"));
            btnRgDA.setText("");
            btnElim.setIcon(Iconos.getIcono("elimina.png"));
            btnElim.setText("");
            btnImprimir.setIcon(Iconos.getIcono("impresora.png"));
            btnImprimir.setText("");
            btnCarga.setIcon(Iconos.getIcono("cargar.png"));
            btnAgreSes.setIcon(Iconos.getIcono("nuevo.png"));
            btnModSes.setIcon(Iconos.getIcono("modifica.png"));
            btnElimSes.setIcon(Iconos.getIcono("elimina.png"));
            horarioSemanal = new ControlDiasSemana("Distribucion de horario semanal", "Sesion", this);
            jtbSecciones.setComponentAt(2, horarioSemanal);
            crearPopMenu();
            matActual = null;
            clvTesis = -1;
            fechaIni=null;
            setCambios(false);
            listenCh = new ListenChanges();
            escuchaCambios();
        }
        catch (ControlDiasSemana.RefSuperiorInvalida ex) {}
    }
    
    /** Consulta en la base de datos el registro de un alumno y si existe carga sus datos 
     * personales matricula y nombre, busca si existe registro de tesis y si es asi lo carga
     * @param matricula La matricula del alumno a cargar
     * @return true si se cargaron los datos del alumno false en caso de que no exista
     *   alumno para la matricula o hayan ocurrido errores al procesar en la bd
     */
    public boolean cargaDatosAlumno(String matricula){
        String[] datos;
        DefaultTableModel modelo;
        String sentencia="select nom,appat,apmat from alumno where alumno.matricula='"+matricula+"';";
        datos=database.Consultas.consultaUnCampo(sentencia,true);
        if(datos==null){
            muestraMensaje("Error al hacer la busqueda de alumno", database.Consultas.obtenError(),TipoMensaje.ERROR);
            return false;
        }
        else if(datos[0]==null){
            muestraMensaje("Resultados","No se encontraron datos para esa matricula",TipoMensaje.INFORMACION);
            return false;
        }
        txtNomAl.setText(datos[0]+" "+datos[1]+" "+datos[2]);
        setTitle("Ficha de asesorías alumno "+matricula);
        btnGuardar.setEnabled(true); btnRgDG.setEnabled(true);
        btnRgDA.setEnabled(true); btnRgper.setEnabled(true);
        btnElim.setEnabled(true); matActual=matricula; 
        btnImprimir.setEnabled(true);
        // buscar datos de tesis
        sentencia="select tesis.* from setitula,tesis where tesis.clvt=setitula.clvt";
        sentencia+=" and setitula.matricula='"+matActual+"';";
        datos=database.Consultas.consultaUnCampo(sentencia,false);
        if(datos==null){
            muestraMensaje("Error al hacer la busqueda de tesis", database.Consultas.obtenError(),TipoMensaje.ERROR);
            return false;
        }
        if(datos[0]==null){
            muestraMensaje("Resultados","No se encontraron datos de tesis para ese alumno",TipoMensaje.INFORMACION);
            btnImprimir.setEnabled(false);
            txtTitTesis.setText(""); jcbTipo.setSelectedIndex(0);
            txtFchaIni.setText(""); txtColab.setText("");
            txtSino.setText(""); clvTesis=-1; btnAgreSes.setEnabled(false); 
            btnModSes.setEnabled(false); btnElimSes.setEnabled(false);
            modelo=(DefaultTableModel)jtSesiones.getModel();
            while(modelo.getRowCount()>0) modelo.removeRow(0);
            jtSesiones.setModel(modelo);
            setCambios(false);
            return true;
        }
        txtTitTesis.setText(datos[1]); jcbTipo.setSelectedItem(datos[2]);
        txtFchaIni.setText(datos[3]); fechaIni=datos[3]; txtColab.setText(datos[4]);
        txtSino.setText(datos[5]); clvTesis=Integer.parseInt(datos[0]);
        btnAgreSes.setEnabled(true); btnModSes.setEnabled(true);
        btnElimSes.setEnabled(true);
        if(!horarioSemanal.cargaDatos()){
            muestraMensaje("Error al consultar horario semanal",horarioSemanal.obtenError(),TipoMensaje.ERROR);
            return false;
        }
        horarioSemanal.setTitulo("Horario semanal asesorias tesis alumno: "+matActual);
        // buscar datos de sesiones programadas
        sentencia="select sestesis.clvsest as 'Clave', fechayhora as 'Fecha y Hora',Tema,Lugar,Observaciones from";
        sentencia+=" progsest,sestesis where matricula='"+matActual+"' and ";
        sentencia+="clvt='"+clvTesis+"' and progsest.clvsest=sestesis.clvsest order by fechayhora;";
        modelo=database.Consultas.consTipoTable(sentencia,false);
        if(modelo==null){
            muestraMensaje("Error al realizar la busqueda de sesiones",database.Consultas.obtenError(),TipoMensaje.ERROR);
            return false;
        }
        jtSesiones.setModel(modelo);
        jtSesiones.getColumn("Clave").setPreferredWidth(45);
        jtSesiones.getColumn("Fecha y Hora").setPreferredWidth(112);
        jtSesiones.getColumn("Tema").setPreferredWidth(142);
        jtSesiones.getColumn("Observaciones").setPreferredWidth(162);
        setCambios(false);
        return true;
    }
    
    /** Si hay datos de un alumno vuelve a cargar desde la bd su registro (datos personales y de tesis)
     */
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
        btnGuardar = new javax.swing.JButton();
        btnRgper = new javax.swing.JButton();
        btnRgDG = new javax.swing.JButton();
        btnRgDA = new javax.swing.JButton();
        btnElim = new javax.swing.JButton();
        btnImprimir = new javax.swing.JButton();
        jPanel2 = new javax.swing.JPanel();
        jLabel3 = new javax.swing.JLabel();
        txtClv = new javax.swing.JTextField();
        btnCarga = new javax.swing.JButton();
        jtbSecciones = new javax.swing.JTabbedPane();
        pnlDTTesis = new javax.swing.JPanel();
        jScrollPane1 = new javax.swing.JScrollPane();
        txtColab = new javax.swing.JTextArea();
        jLabel6 = new javax.swing.JLabel();
        jScrollPane2 = new javax.swing.JScrollPane();
        txtSino = new javax.swing.JTextArea();
        jLabel7 = new javax.swing.JLabel();
        jLabel4 = new javax.swing.JLabel();
        jcbTipo = new javax.swing.JComboBox();
        txtTitTesis = new javax.swing.JTextField();
        jLabel5 = new javax.swing.JLabel();
        txtFchaIni = new javax.swing.JTextField();
        jLabel9 = new javax.swing.JLabel();
        jLabel1 = new javax.swing.JLabel();
        txtNomAl = new javax.swing.JTextField();
        pnlDsesT = new javax.swing.JPanel();
        btnAgreSes = new javax.swing.JButton();
        btnModSes = new javax.swing.JButton();
        btnElimSes = new javax.swing.JButton();
        jScrollPane3 = new javax.swing.JScrollPane();
        jtSesiones = new javax.swing.JTable();
        jLabel8 = new javax.swing.JLabel();
        jPanel1 = new javax.swing.JPanel();

        setClosable(true);
        setDefaultCloseOperation(javax.swing.WindowConstants.DO_NOTHING_ON_CLOSE);
        setIconifiable(true);
        setMaximizable(true);
        setResizable(true);
        setTitle("Ficha de asesorías");

        jToolBar1.setFloatable(false);
        jToolBar1.setRollover(true);

        btnGuardar.setText("Grd");
        btnGuardar.setToolTipText("Guarda cambios en datos de tesis");
        btnGuardar.setEnabled(false);
        btnGuardar.setFocusable(false);
        btnGuardar.setHorizontalTextPosition(javax.swing.SwingConstants.CENTER);
        btnGuardar.setVerticalTextPosition(javax.swing.SwingConstants.BOTTOM);
        btnGuardar.addActionListener(new java.awt.event.ActionListener() {
            public void actionPerformed(java.awt.event.ActionEvent evt) {
                btnGuardarActionPerformed(evt);
            }
        });
        jToolBar1.add(btnGuardar);

        btnRgper.setText("rgPer");
        btnRgper.setToolTipText("Ver registro personal");
        btnRgper.setEnabled(false);
        btnRgper.setFocusable(false);
        btnRgper.setHorizontalTextPosition(javax.swing.SwingConstants.CENTER);
        btnRgper.setVerticalTextPosition(javax.swing.SwingConstants.BOTTOM);
        btnRgper.addActionListener(new java.awt.event.ActionListener() {
            public void actionPerformed(java.awt.event.ActionEvent evt) {
                btnRgperActionPerformed(evt);
            }
        });
        jToolBar1.add(btnRgper);

        btnRgDG.setText("rgDG");
        btnRgDG.setToolTipText("Ver registro de desempeño en grupos");
        btnRgDG.setEnabled(false);
        btnRgDG.setFocusable(false);
        btnRgDG.setHorizontalTextPosition(javax.swing.SwingConstants.CENTER);
        btnRgDG.setVerticalTextPosition(javax.swing.SwingConstants.BOTTOM);
        btnRgDG.addActionListener(new java.awt.event.ActionListener() {
            public void actionPerformed(java.awt.event.ActionEvent evt) {
                btnRgDGActionPerformed(evt);
            }
        });
        jToolBar1.add(btnRgDG);

        btnRgDA.setText("rgDA");
        btnRgDA.setToolTipText("Ver registros de desempeño academico");
        btnRgDA.setEnabled(false);
        btnRgDA.setFocusable(false);
        btnRgDA.setHorizontalTextPosition(javax.swing.SwingConstants.CENTER);
        btnRgDA.setVerticalTextPosition(javax.swing.SwingConstants.BOTTOM);
        btnRgDA.addActionListener(new java.awt.event.ActionListener() {
            public void actionPerformed(java.awt.event.ActionEvent evt) {
                btnRgDAActionPerformed(evt);
            }
        });
        jToolBar1.add(btnRgDA);

        btnElim.setText("el");
        btnElim.setToolTipText("Eliminar datos de tesis");
        btnElim.setEnabled(false);
        btnElim.setFocusable(false);
        btnElim.setHorizontalTextPosition(javax.swing.SwingConstants.CENTER);
        btnElim.setVerticalTextPosition(javax.swing.SwingConstants.BOTTOM);
        btnElim.addActionListener(new java.awt.event.ActionListener() {
            public void actionPerformed(java.awt.event.ActionEvent evt) {
                btnElimActionPerformed(evt);
            }
        });
        jToolBar1.add(btnElim);

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

        javax.swing.GroupLayout jPanel2Layout = new javax.swing.GroupLayout(jPanel2);
        jPanel2.setLayout(jPanel2Layout);
        jPanel2Layout.setHorizontalGroup(
            jPanel2Layout.createParallelGroup(javax.swing.GroupLayout.Alignment.LEADING)
            .addGroup(javax.swing.GroupLayout.Alignment.TRAILING, jPanel2Layout.createSequentialGroup()
                .addContainerGap(125, Short.MAX_VALUE)
                .addComponent(jLabel3)
                .addPreferredGap(javax.swing.LayoutStyle.ComponentPlacement.UNRELATED)
                .addComponent(txtClv, javax.swing.GroupLayout.PREFERRED_SIZE, 93, javax.swing.GroupLayout.PREFERRED_SIZE)
                .addPreferredGap(javax.swing.LayoutStyle.ComponentPlacement.RELATED)
                .addComponent(btnCarga)
                .addContainerGap())
        );
        jPanel2Layout.setVerticalGroup(
            jPanel2Layout.createParallelGroup(javax.swing.GroupLayout.Alignment.LEADING)
            .addGroup(jPanel2Layout.createParallelGroup(javax.swing.GroupLayout.Alignment.BASELINE)
                .addComponent(btnCarga)
                .addComponent(txtClv, javax.swing.GroupLayout.PREFERRED_SIZE, javax.swing.GroupLayout.DEFAULT_SIZE, javax.swing.GroupLayout.PREFERRED_SIZE)
                .addComponent(jLabel3))
        );

        jToolBar1.add(jPanel2);

        getContentPane().add(jToolBar1, java.awt.BorderLayout.NORTH);

        jtbSecciones.setTabLayoutPolicy(javax.swing.JTabbedPane.SCROLL_TAB_LAYOUT);

        txtColab.setColumns(20);
        txtColab.setRows(5);
        jScrollPane1.setViewportView(txtColab);

        jLabel6.setText("Colaboradores:");

        txtSino.setColumns(20);
        txtSino.setRows(5);
        jScrollPane2.setViewportView(txtSino);

        jLabel7.setText("Sinodales:");

        jLabel4.setText("Tipo:");

        jcbTipo.setEditable(true);
        jcbTipo.setModel(new javax.swing.DefaultComboBoxModel(new String[] { "Tesis", "Tesina", "Tesis experimental", "Experiencia laboral", "Seminario de autoformacion", "(Otro escribir directamente)" }));
        jcbTipo.addItemListener(new java.awt.event.ItemListener() {
            public void itemStateChanged(java.awt.event.ItemEvent evt) {
                jcbTipoItemStateChanged(evt);
            }
        });

        jLabel5.setText("Fecha de inicio:");

        jLabel9.setText("Titulo:");

        jLabel1.setHorizontalAlignment(javax.swing.SwingConstants.RIGHT);
        jLabel1.setText("Datos de tesis del alumno:");

        txtNomAl.setEditable(false);

        javax.swing.GroupLayout pnlDTTesisLayout = new javax.swing.GroupLayout(pnlDTTesis);
        pnlDTTesis.setLayout(pnlDTTesisLayout);
        pnlDTTesisLayout.setHorizontalGroup(
            pnlDTTesisLayout.createParallelGroup(javax.swing.GroupLayout.Alignment.LEADING)
            .addGroup(pnlDTTesisLayout.createSequentialGroup()
                .addContainerGap()
                .addGroup(pnlDTTesisLayout.createParallelGroup(javax.swing.GroupLayout.Alignment.LEADING)
                    .addGroup(pnlDTTesisLayout.createSequentialGroup()
                        .addComponent(jLabel1)
                        .addPreferredGap(javax.swing.LayoutStyle.ComponentPlacement.RELATED)
                        .addComponent(txtNomAl, javax.swing.GroupLayout.PREFERRED_SIZE, 358, javax.swing.GroupLayout.PREFERRED_SIZE))
                    .addGroup(pnlDTTesisLayout.createSequentialGroup()
                        .addComponent(jLabel9)
                        .addPreferredGap(javax.swing.LayoutStyle.ComponentPlacement.UNRELATED)
                        .addComponent(txtTitTesis, javax.swing.GroupLayout.PREFERRED_SIZE, 448, javax.swing.GroupLayout.PREFERRED_SIZE))
                    .addGroup(pnlDTTesisLayout.createSequentialGroup()
                        .addComponent(jLabel4)
                        .addPreferredGap(javax.swing.LayoutStyle.ComponentPlacement.UNRELATED)
                        .addComponent(jcbTipo, javax.swing.GroupLayout.PREFERRED_SIZE, 210, javax.swing.GroupLayout.PREFERRED_SIZE)
                        .addPreferredGap(javax.swing.LayoutStyle.ComponentPlacement.UNRELATED)
                        .addComponent(jLabel5)
                        .addPreferredGap(javax.swing.LayoutStyle.ComponentPlacement.RELATED)
                        .addComponent(txtFchaIni, javax.swing.GroupLayout.PREFERRED_SIZE, 155, javax.swing.GroupLayout.PREFERRED_SIZE))
                    .addComponent(jLabel6)
                    .addComponent(jScrollPane1, javax.swing.GroupLayout.PREFERRED_SIZE, 488, javax.swing.GroupLayout.PREFERRED_SIZE)
                    .addComponent(jLabel7)
                    .addComponent(jScrollPane2, javax.swing.GroupLayout.PREFERRED_SIZE, 488, javax.swing.GroupLayout.PREFERRED_SIZE))
                .addContainerGap(50, Short.MAX_VALUE))
        );
        pnlDTTesisLayout.setVerticalGroup(
            pnlDTTesisLayout.createParallelGroup(javax.swing.GroupLayout.Alignment.LEADING)
            .addGroup(pnlDTTesisLayout.createSequentialGroup()
                .addContainerGap()
                .addGroup(pnlDTTesisLayout.createParallelGroup(javax.swing.GroupLayout.Alignment.BASELINE)
                    .addComponent(jLabel1)
                    .addComponent(txtNomAl, javax.swing.GroupLayout.PREFERRED_SIZE, javax.swing.GroupLayout.DEFAULT_SIZE, javax.swing.GroupLayout.PREFERRED_SIZE))
                .addPreferredGap(javax.swing.LayoutStyle.ComponentPlacement.RELATED)
                .addGroup(pnlDTTesisLayout.createParallelGroup(javax.swing.GroupLayout.Alignment.BASELINE)
                    .addComponent(jLabel9)
                    .addComponent(txtTitTesis, javax.swing.GroupLayout.PREFERRED_SIZE, javax.swing.GroupLayout.DEFAULT_SIZE, javax.swing.GroupLayout.PREFERRED_SIZE))
                .addPreferredGap(javax.swing.LayoutStyle.ComponentPlacement.RELATED)
                .addGroup(pnlDTTesisLayout.createParallelGroup(javax.swing.GroupLayout.Alignment.BASELINE)
                    .addComponent(jLabel4)
                    .addComponent(jcbTipo, javax.swing.GroupLayout.PREFERRED_SIZE, javax.swing.GroupLayout.DEFAULT_SIZE, javax.swing.GroupLayout.PREFERRED_SIZE)
                    .addComponent(jLabel5)
                    .addComponent(txtFchaIni, javax.swing.GroupLayout.PREFERRED_SIZE, javax.swing.GroupLayout.DEFAULT_SIZE, javax.swing.GroupLayout.PREFERRED_SIZE))
                .addPreferredGap(javax.swing.LayoutStyle.ComponentPlacement.RELATED)
                .addComponent(jLabel6)
                .addPreferredGap(javax.swing.LayoutStyle.ComponentPlacement.RELATED)
                .addComponent(jScrollPane1, javax.swing.GroupLayout.PREFERRED_SIZE, 79, javax.swing.GroupLayout.PREFERRED_SIZE)
                .addPreferredGap(javax.swing.LayoutStyle.ComponentPlacement.RELATED)
                .addComponent(jLabel7)
                .addPreferredGap(javax.swing.LayoutStyle.ComponentPlacement.RELATED)
                .addComponent(jScrollPane2, javax.swing.GroupLayout.PREFERRED_SIZE, javax.swing.GroupLayout.DEFAULT_SIZE, javax.swing.GroupLayout.PREFERRED_SIZE)
                .addContainerGap(javax.swing.GroupLayout.DEFAULT_SIZE, Short.MAX_VALUE))
        );

        jtbSecciones.addTab("Datos de la tesis", pnlDTTesis);

        btnAgreSes.setText("Agregar");
        btnAgreSes.setEnabled(false);
        btnAgreSes.addActionListener(new java.awt.event.ActionListener() {
            public void actionPerformed(java.awt.event.ActionEvent evt) {
                btnAgreSesActionPerformed(evt);
            }
        });

        btnModSes.setText("Modificar");
        btnModSes.setEnabled(false);
        btnModSes.addActionListener(new java.awt.event.ActionListener() {
            public void actionPerformed(java.awt.event.ActionEvent evt) {
                btnModSesActionPerformed(evt);
            }
        });

        btnElimSes.setText("Quitar");
        btnElimSes.setEnabled(false);
        btnElimSes.addActionListener(new java.awt.event.ActionListener() {
            public void actionPerformed(java.awt.event.ActionEvent evt) {
                btnElimSesActionPerformed(evt);
            }
        });

        jScrollPane3.setPreferredSize(new java.awt.Dimension(0, 0));

        jtSesiones.setAutoCreateRowSorter(true);
        jtSesiones.setModel(new javax.swing.table.DefaultTableModel(
            new Object [][] {

            },
            new String [] {
                "Clave", "FechaYHora", "Tema", "Lugar", "Observaciones"
            }
        ) {
            boolean[] canEdit = new boolean [] {
                false, false, false, false, false
            };

            public boolean isCellEditable(int rowIndex, int columnIndex) {
                return canEdit [columnIndex];
            }
        });
        jtSesiones.setAutoResizeMode(javax.swing.JTable.AUTO_RESIZE_OFF);
        jtSesiones.setEnabled(false);
        jtSesiones.addMouseListener(new java.awt.event.MouseAdapter() {
            public void mouseClicked(java.awt.event.MouseEvent evt) {
                jtSesionesMouseClicked(evt);
            }
            public void mousePressed(java.awt.event.MouseEvent evt) {
                jtSesionesMousePressed(evt);
            }
            public void mouseReleased(java.awt.event.MouseEvent evt) {
                jtSesionesMouseReleased(evt);
            }
        });
        jScrollPane3.setViewportView(jtSesiones);

        jLabel8.setText("Programación de sesiones para asesorías:");

        javax.swing.GroupLayout pnlDsesTLayout = new javax.swing.GroupLayout(pnlDsesT);
        pnlDsesT.setLayout(pnlDsesTLayout);
        pnlDsesTLayout.setHorizontalGroup(
            pnlDsesTLayout.createParallelGroup(javax.swing.GroupLayout.Alignment.LEADING)
            .addGroup(pnlDsesTLayout.createSequentialGroup()
                .addContainerGap()
                .addGroup(pnlDsesTLayout.createParallelGroup(javax.swing.GroupLayout.Alignment.LEADING)
                    .addGroup(pnlDsesTLayout.createSequentialGroup()
                        .addComponent(jLabel8)
                        .addGap(18, 18, 18)
                        .addComponent(btnAgreSes)
                        .addPreferredGap(javax.swing.LayoutStyle.ComponentPlacement.RELATED)
                        .addComponent(btnModSes)
                        .addPreferredGap(javax.swing.LayoutStyle.ComponentPlacement.RELATED)
                        .addComponent(btnElimSes))
                    .addComponent(jScrollPane3, javax.swing.GroupLayout.DEFAULT_SIZE, 528, Short.MAX_VALUE))
                .addContainerGap())
        );
        pnlDsesTLayout.setVerticalGroup(
            pnlDsesTLayout.createParallelGroup(javax.swing.GroupLayout.Alignment.LEADING)
            .addGroup(pnlDsesTLayout.createSequentialGroup()
                .addContainerGap()
                .addGroup(pnlDsesTLayout.createParallelGroup(javax.swing.GroupLayout.Alignment.BASELINE)
                    .addComponent(jLabel8)
                    .addComponent(btnAgreSes)
                    .addComponent(btnModSes)
                    .addComponent(btnElimSes))
                .addPreferredGap(javax.swing.LayoutStyle.ComponentPlacement.RELATED)
                .addComponent(jScrollPane3, javax.swing.GroupLayout.DEFAULT_SIZE, 250, Short.MAX_VALUE)
                .addContainerGap())
        );

        jtbSecciones.addTab("Sesiones de asesoría programadas", pnlDsesT);

        javax.swing.GroupLayout jPanel1Layout = new javax.swing.GroupLayout(jPanel1);
        jPanel1.setLayout(jPanel1Layout);
        jPanel1Layout.setHorizontalGroup(
            jPanel1Layout.createParallelGroup(javax.swing.GroupLayout.Alignment.LEADING)
            .addGap(0, 548, Short.MAX_VALUE)
        );
        jPanel1Layout.setVerticalGroup(
            jPanel1Layout.createParallelGroup(javax.swing.GroupLayout.Alignment.LEADING)
            .addGap(0, 301, Short.MAX_VALUE)
        );

        jtbSecciones.addTab("Horario semanal", jPanel1);

        getContentPane().add(jtbSecciones, java.awt.BorderLayout.CENTER);

        pack();
    }// </editor-fold>//GEN-END:initComponents

    /** Valida la matricula ingresada en el control txtClv y si es valida carga los datos del alumno al que corresponda
     * @param evt El ActionEvent que genero el evento
     */
    private void btnCargaActionPerformed(java.awt.event.ActionEvent evt) {//GEN-FIRST:event_btnCargaActionPerformed
        String tmpClv=txtClv.getText().trim();
        if(!operaciones.Datos.valMatricula(tmpClv)) {
            muestraMensaje("Error en el parametro", "Matricula invalida",TipoMensaje.ERROR);
            return;
        }
        if(hayCambios()){
            TipoRespuesta opc=pideDesicion(getTitle(),"Hay cambios sin guardar, ¿Desea ignorarlos?");
            if(opc.getTipo()!=TipoRespuesta.ACEPTAR.getTipo()) return;
        }
        if(existeVentana("Ficha de asesorias alumno "+tmpClv,true) && !txtClv.equals(matActual)){ 
            txtClv.setText(""); return;
        }
        else cargaDatosAlumno(tmpClv);
    }//GEN-LAST:event_btnCargaActionPerformed

    /** Carga el registro personal del alumno actual
     * @param evt El ActionEvent que genero el evento
     */
    private void btnRgperActionPerformed(java.awt.event.ActionEvent evt) {//GEN-FIRST:event_btnRgperActionPerformed
        getPrincipalVnt().agregaVentanaAlumno(matActual,1);
    }//GEN-LAST:event_btnRgperActionPerformed

    /** Carga el registro de desempeño dentro de grupos del alumno actual
     * @param evt El ActionEvent que genero el evento
     */
    private void btnRgDGActionPerformed(java.awt.event.ActionEvent evt) {//GEN-FIRST:event_btnRgDGActionPerformed
        getPrincipalVnt().agregaVentanaAlumno(matActual,3);
    }//GEN-LAST:event_btnRgDGActionPerformed

    /** Carga el registro de desempeño academico del alumno actual
     * @param evt El ActionEvent que genero el evento
     */
    private void btnRgDAActionPerformed(java.awt.event.ActionEvent evt) {//GEN-FIRST:event_btnRgDAActionPerformed
        getPrincipalVnt().agregaVentanaAlumno(matActual,2);
    }//GEN-LAST:event_btnRgDAActionPerformed

    /** Llama al metodo guardaCambios
     * @param evt El ActionEvent que genero el evento
     */
    private void btnGuardarActionPerformed(java.awt.event.ActionEvent evt) {//GEN-FIRST:event_btnGuardarActionPerformed
        guardaCambios();
    }//GEN-LAST:event_btnGuardarActionPerformed

    /** Guarda los cambios realizados en el registro de tesis del alumno actual 
     * (si es la primera vez que se edita se crea el registro)
     * @return true si el registro se creo/guardo correctamente. 
     *   false si no se pudo crear/guardar el registro en cuyo caso muestra una mensaje 
     *   indicando el error ocurrido (Comunicacion con la bd, datos invalidos, etc...).
     */
    @Override
    public boolean guardaCambios(){
        String[] datos=obtenDatosValidos(); 
        String sentencia;
        if(datos!=null){
            if(clvTesis==-1){ // caso no existe y hay que guardar como nueva
                if(database.Actualiza.nuevaTesis(datos,true)){
                    clvTesis=database.Actualiza.obtenClave();
                    muestraMensaje("Accion realizada","Se ha agregado la nueva tesis",TipoMensaje.INFORMACION); 
                    setTitle("Ficha de asesorías alumno "+matActual);
                    btnAgreSes.setEnabled(true); btnModSes.setEnabled(true);
                    btnElimSes.setEnabled(true); btnImprimir.setEnabled(true);
                    setCambios(false);
                }
                else{
                    muestraMensaje("Accion fallida",database.Actualiza.obtenError(),TipoMensaje.ERROR);
                    return false;
                }
                return true;
            } // caso ya existe hay que hacer un update
            sentencia="update tesis set titulo='"+datos[1]+"', tipo='"+datos[2]+"', fechaini='";
            sentencia+=datos[3]+"', colaboradores="+(datos[4].equals("")?"null":"'"+datos[4]+"'");
            sentencia+=", sinodales="+(datos[5].equals("")?"null":"'"+datos[5]+"'")+" where ";
            sentencia+="clvt="+clvTesis+";";
            if(database.Actualiza.actualiza(sentencia,true,true)){
                muestraMensaje("Accion realizada","Se han actualizado los datos",TipoMensaje.INFORMACION); 
                setCambios(false);
                return true;
            }
            else{
                muestraMensaje("Accion fallida",database.Actualiza.obtenError(),TipoMensaje.ERROR);
                return false;
            }
        }
        return false;
    }
    
    /** Muestra una ventana interna (clase FrmRegAse) para agregar una nueva sesion 
     *   de asesoria al alumno actual
     * @param evt El ActionEvent que genero el evento
     */
    private void btnAgreSesActionPerformed(java.awt.event.ActionEvent evt) {//GEN-FIRST:event_btnAgreSesActionPerformed
        if(existeVentana("Registro de sesion Asesoria a alumno: "+matActual, true)) return;
        FrmRegAse ase=new FrmRegAse(getPrincipalVnt(),getTitle(),matActual,-1,clvTesis,null);
        agregaVentana(ase);
    }//GEN-LAST:event_btnAgreSesActionPerformed

    /** Pide la clave de registro de una sesion de asesoria para modificar/actualizar sus datos
     * @param evt El ActionEvent que genero el evento
     */
    private void btnModSesActionPerformed(java.awt.event.ActionEvent evt) {//GEN-FIRST:event_btnModSesActionPerformed
        DefaultTableModel modelo=(DefaultTableModel)jtSesiones.getModel();        
        int filas=modelo.getRowCount();
        boolean bandera=false;
        if(filas==0) return;
        int clave=pideEntero("Modificar un registro de sesion", "¿Clave del registro que deseas modificar?");
        for(int i=0;i<filas;i++){            
            if(Integer.parseInt(""+modelo.getValueAt(i,0))==clave){                
                modificaRegistro(clave);
                bandera=true; break;
            }
        }
        if(!bandera)
            muestraMensaje("No se pudo realizar la accion", "No se encontro registro con la clave: "+clave,TipoMensaje.INFORMACION);
    }//GEN-LAST:event_btnModSesActionPerformed

    /** Pide la clave de registro de una sesion de asesoria para llamar al metodo que elimina un registro de asesoria
     * @param evt El ActionEvent que genero el evento
     */
    private void btnElimSesActionPerformed(java.awt.event.ActionEvent evt) {//GEN-FIRST:event_btnElimSesActionPerformed
        DefaultTableModel modelo=(DefaultTableModel)jtSesiones.getModel();
        int clave;
        int filas=modelo.getRowCount();
        boolean bandera=false;
        if(filas==0) return;
        clave=pideEntero("Eliminar un registro de sesion de asesoria", "Ingresa la clave del registro que deseas eliminar");
        for(int i=0;i<filas;i++){
            if(Integer.parseInt(""+modelo.getValueAt(i,0))==clave){
                quitarRegistro(clave);
                bandera=true; break;
            }
        }
        if(!bandera){
            muestraMensaje("No se pudo realizar la accion", "No se encontro registro con la clave: "+clave,TipoMensaje.INFORMACION);
        }
    }//GEN-LAST:event_btnElimSesActionPerformed

    /** Abre un PopMenu (control pmnuOpcs) si es el evento popUpTrigger en el control jtSesiones 
     *   para dar opiones de modificar o quitar el registro sobre el que se hizo click
     * @param evt El MouseEvent que genero el evento
     */
    private void jtSesionesMouseClicked(java.awt.event.MouseEvent evt) {//GEN-FIRST:event_jtSesionesMouseClicked
        int col=jtSesiones.columnAtPoint(evt.getPoint());
        int fila=jtSesiones.rowAtPoint(evt.getPoint());       
        int filaClave=0;
        for(int i=0;i<jtSesiones.getColumnCount();i++)
            if(jtSesiones.getColumnName(i).equals("Clave")){
                filaClave=i; break;
            }
        if (fila>-1 && col>-1 && jtSesiones.getValueAt(fila,filaClave)!=null)
            mostrarPopupMenu(evt,fila,""+jtSesiones.getValueAt(fila,filaClave));
    }//GEN-LAST:event_jtSesionesMouseClicked

    /** Abre un PopMenu (control pmnuOpcs) si es el evento popUpTrigger en el control jtSesiones 
     *   para dar opiones de modificar o quitar el registro sobre el que se hizo click
     * @param evt El MouseEvent que genero el evento
     */
    private void jtSesionesMousePressed(java.awt.event.MouseEvent evt) {//GEN-FIRST:event_jtSesionesMousePressed
        int col=jtSesiones.columnAtPoint(evt.getPoint());
        int fila=jtSesiones.rowAtPoint(evt.getPoint());       
        int filaClave=0;
        for(int i=0;i<jtSesiones.getColumnCount();i++)
            if(jtSesiones.getColumnName(i).equals("Clave")){
                filaClave=i; break;
            }
        if (fila>-1 && col>-1 && jtSesiones.getValueAt(fila,filaClave)!=null)
            mostrarPopupMenu(evt,fila,""+jtSesiones.getValueAt(fila,filaClave));
    }//GEN-LAST:event_jtSesionesMousePressed

    /** Abre un PopMenu (control pmnuOpcs) si es el evento popUpTrigger en el control jtSesiones 
     *   para dar opiones de modificar o quitar el registro sobre el que se hizo click
     * @param evt El MouseEvent que genero el evento
     */
    private void jtSesionesMouseReleased(java.awt.event.MouseEvent evt) {//GEN-FIRST:event_jtSesionesMouseReleased
        int col=jtSesiones.columnAtPoint(evt.getPoint());
        int fila=jtSesiones.rowAtPoint(evt.getPoint());       
        int filaClave=0;
        for(int i=0;i<jtSesiones.getColumnCount();i++)
            if(jtSesiones.getColumnName(i).equals("Clave")){
                filaClave=i; break;
            }
        if (fila>-1 && col>-1 && jtSesiones.getValueAt(fila,filaClave)!=null)
            mostrarPopupMenu(evt,fila,""+jtSesiones.getValueAt(fila,filaClave));
    }//GEN-LAST:event_jtSesionesMouseReleased

    /** Borra el registro de tesis de la bd del alumno actual (pregunta antes si esta seguro
     *  y en caso de estar configurado pedira el password de seguridad)
     * @param evt El ActionEvent que genero el evento
     */
    private void btnElimActionPerformed(java.awt.event.ActionEvent evt) {//GEN-FIRST:event_btnElimActionPerformed
        TipoRespuesta aux=pideDesicion("Confirme accion","En verdad desea eliminar esta tesis y sus sesiones");
        if(aux.getTipo()!=TipoRespuesta.ACEPTAR.getTipo()) return;        
        if(!database.Actualiza.actualiza("delete from SeTitula where matricula='"+matActual+"'",false,true))
            muestraMensaje("No se pudo realizar la accion", database.Actualiza.obtenError(),TipoMensaje.ERROR);
        else{
            muestraMensaje("Se ha realizado la accion", "Se elimino completamente la tesis",TipoMensaje.INFORMACION);
            cerrarVentana(getTitle());
            dispose();
        }
    }//GEN-LAST:event_btnElimActionPerformed

    /** Actualiza la bandera interna de hayCambios (cambios sin guardar) a true
     *  (solo si hay un registro de alumno cargado)
     * @param evt El ItemEvent que genero el evento
     */
    private void jcbTipoItemStateChanged(java.awt.event.ItemEvent evt) {//GEN-FIRST:event_jcbTipoItemStateChanged
        setCambios(matActual!=null);
    }//GEN-LAST:event_jcbTipoItemStateChanged

    /** Prepara un reporte con los datos del registro actual y abre el cuadro de 
     *   dialogo para enviarlo a imprimir
     * @param evt El ActionEvent que genero el evento
     */
    private void btnImprimirActionPerformed(java.awt.event.ActionEvent evt) {//GEN-FIRST:event_btnImprimirActionPerformed
        Map<String,String> parametros = new HashMap<String,String>();
        ArrayList campos=new ArrayList();
        ImpRegTesis campo;
        DefaultTableModel modelo=(DefaultTableModel)jtSesiones.getModel();
        String[] datos;
        if(hayCambios()){
            muestraMensaje("No se puede imprimir","Guarde los cambios hechos para poder imprimir",TipoMensaje.INFORMACION);
            return;
        }
        datos=database.Consultas.consultaUnCampo("select * from datosinst;",false);
        if(datos==null){
            muestraMensaje("Error al consultar datos de cabecera de reporte",database.Consultas.obtenError(),TipoMensaje.ERROR);
            return;
        }
        parametros.put("NOMINST",datos[0]);
        parametros.put("UNIACAESC",datos[1]);
        parametros.put("AREAPROG",datos[2]);
        parametros.put("MATRICULA",matActual);
        parametros.put("ALUMNO",txtNomAl.getText());
        parametros.put("TITULO",txtTitTesis.getText());
        parametros.put("TIPO",""+jcbTipo.getSelectedItem());
        parametros.put("FCHAINICIO",""+txtFchaIni.getText());
        parametros.put("SINODALES",""+txtSino.getText());
        parametros.put("COLABORADORES",""+txtColab.getText());
        parametros.put("LOGO",null);
        for(int s=0;s<modelo.getRowCount();s++){
            campo=new ImpRegTesis(""+modelo.getValueAt(s,0),""+modelo.getValueAt(s,1),""+modelo.getValueAt(s,2),""+modelo.getValueAt(s,3));
            campos.add(campo);
        }
        if(campos.size()==0) campos=null;
        enviarImpresion("Registro tesis alumno "+matActual,9,parametros,campos);
    }//GEN-LAST:event_btnImprimirActionPerformed
    
    /** Elimina un registro de asesoria de la tesis actual (pregunta antes si esta seguro
     *  y en caso de estar configurado pedira el password de seguridad)
     * @param clave La clave del registro a eliminar
     */
    private void quitarRegistro(int clave){
        TipoRespuesta tmp=pideDesicion("Confirme la accion", "En verdad desea eliminar completamente el registro: "+clave);
        if(tmp.getTipo()!=TipoRespuesta.ACEPTAR.getTipo()) return;
        String sentencia="delete ProgSesT,SesTesis from ProgSesT,SesTesis where ProgSesT.matricula='"+matActual;
        sentencia+="' and ProgSesT.ClvT="+clvTesis+" and ProgSesT.ClvSesT="+clave+" and SesTesis.ClvSesT="+clave;
        if(!database.Actualiza.actualiza(sentencia,false,true))
            muestraMensaje("No se pudo realizar la accion", database.Actualiza.obtenError(),TipoMensaje.ERROR);
        else{
            muestraMensaje("Accion realizada", "Se ha eliminado completamente el registro",TipoMensaje.INFORMACION);
            actualiza();
        }
    }
    
    /** Verifica si existe un registro de asesoria por su calve y en caso de existir 
     *   llam al metodo que lo muestra en su propia ventana
     * @param clave La clave del registro a mostrar
     */
    public void muestraRegistro(String clave){
        DefaultTableModel modelo=(DefaultTableModel)jtSesiones.getModel();
        boolean bandera=false;
        for(int j=0;j<modelo.getRowCount();j++) 
            if(clave.equals(""+modelo.getValueAt(j,0))){
                bandera=true;
                break;
            }
        if(bandera) modificaRegistro(Integer.parseInt(clave));
        else muestraMensaje("Error","No existe la sesion con clave "+clave,TipoMensaje.ERROR);
    }
    
    /** Abre una ventana con el registro de una sesion de asesoria para modificarlo/actualizarlo
     * @param clave La clave del registro a modificar
     */
    private void modificaRegistro(int clave){
        DefaultTableModel modelo=(DefaultTableModel)jtSesiones.getModel();
        FrmRegAse newRegAse;
        if(!existeVentana("Registro de sesion "+clave+" Asesoria a alumno: "+matActual,true)){
            String[] datos = new String[modelo.getColumnCount()];
            for(int g=0;g<modelo.getRowCount();g++){
                if(Integer.parseInt(""+modelo.getValueAt(g,0))==clave){
                    for(int j=1;j<datos.length;j++) 
                        datos[j-1]=(modelo.getValueAt(g,j)!=null?""+modelo.getValueAt(g,j):null);
                    break;
                }
            }
            newRegAse = new FrmRegAse(getPrincipalVnt(),getTitle(),matActual,clave,clvTesis,datos);
            agregaVentana(newRegAse);
        }
    }
    
    /** Valida todos los datos ingresados en los controles (area dato de tesis) y los regresa en un arreglo de String
     *  en el orden: matricula del alumno actual, titulo de la tesis, tipo de tesis, fecha de inicio de asesoria, 
     *  colaboradores, sinodales.
     * @return La lista de datos o null si hay al menos un dato invalido en cuyo caso muestra un mensaje indicando
     *  el error encontrado.
     */
    private String[] obtenDatosValidos(){
        String[] datos=new String[6];
        datos[0]=matActual;
        datos[1]=txtTitTesis.getText().trim();
        datos[2]=""+jcbTipo.getSelectedItem();
        datos[3]=txtFchaIni.getText().trim();
        datos[4]=txtColab.getText().trim();
        datos[5]=txtSino.getText().trim();
        // validar que el titulo no sea nulo ni sobrepase 95 caracteres
        if(datos[1].equals("")||datos[1].length()>95){
            muestraMensaje("Error en los datos","Titulo invalido",TipoMensaje.ERROR); return null; 
        }
        // validar que el tipo no sea nulo ni sobrepase 45 caracteres
        if(datos[2].equals("")||datos[2].length()>45){
            muestraMensaje("Error en los datos","Tipo invalido",TipoMensaje.ERROR); return null; 
        }
        // validar el formato de la fecha inicia
        if(!operaciones.Datos.valFecha(datos[3])){
            muestraMensaje("Error en los datos","Fecha inicial invalida",TipoMensaje.ERROR); return null; 
        }
        datos[3]=operaciones.Datos.transformatFcha(datos[3]);
        // validar que los colaboradores no sobrepase 255 caracteres
        if(datos[4].length()>255){
            muestraMensaje("Error en los datos","Colaboradores invalido",TipoMensaje.ERROR); return null; 
        }
        // validar que sinodales no sea nulo ni sobrepase 255 caracteres
        if(datos[5].equals("")||datos[5].length()>255){
            muestraMensaje("Error en los datos","Sinodales invalido",TipoMensaje.ERROR); return null; 
        }
        return datos;
    }
    
    /** Crea el popmenu control (pmnuOpcs) con las opciones a desplegar
     * Modificar y Quitar para aplicar a la tabla de sesiones de asesoria programada
     */
    private void crearPopMenu(){        
        pmnuOpcs=new javax.swing.JPopupMenu();
        javax.swing.JMenuItem mnu1 = new javax.swing.JMenuItem("Modificar");
        mnu1.setActionCommand("Modificar");
        mnu1.addActionListener(this);
        javax.swing.JMenuItem mnu2 = new javax.swing.JMenuItem("Quitar");
        mnu2.setActionCommand("Quitar");
        mnu2.addActionListener(this);
        pmnuOpcs.add(mnu1);
        pmnuOpcs.add(mnu2);
    }
    
    /** Verifica si el evento es un popUpTrigger y en que fila (tabla jtSesiones) se hizo click
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
            jtSesiones.setRowSelectionInterval(fila, fila);
            pmnuOpcs.show(evt.getComponent(),evt.getX(),evt.getY());
        }
    }

    /** Es la implementacion del action command de los menus del menu emergente 
     *   (control pmnuOpcs) de acuerdo al ActionCommand del evento llama al metodo 
     *   de la accion en la cual se hizo click (modificar o quitar un registro)
     * @param evt El ActionEvent que genero el evento
     */
    public void actionPerformed(java.awt.event.ActionEvent evt){
        java.util.StringTokenizer toks=new java.util.StringTokenizer(evt.getActionCommand(),":");
        String accion=toks.nextToken();
        int clave=Integer.parseInt(toks.nextToken());
        //int fila=Integer.parseInt(toks.nextToken());
        if(accion.equals("Modificar")) modificaRegistro(clave);
        else quitarRegistro(clave);
        pmnuOpcs.setVisible(false);
    }
    
    /** Crea un objeto que implementa el DocumentListener y lo agrega a los controles de texto
     * para detectar si un campo es editado y por lo tanto saber si hay cambios sin guardar.
     */
    private void escuchaCambios(){
        txtTitTesis.getDocument().addDocumentListener(listenCh);
        txtFchaIni.getDocument().addDocumentListener(listenCh);
        txtColab.getDocument().addDocumentListener(listenCh);
        txtSino.getDocument().addDocumentListener(listenCh);
    }
    
    /** Regresa la clave del registro de tesis actual (-1 si no hay registro cargado)
     * @return la clave del registro de tesis actual (-1 si no hay registro cargado)
     */
    public int getClaveTesis(){ return clvTesis; }
    
    /** Regresa la matricula del alumno de actual (null si no hay alumno cargado)
     * @return la matricula del alumno de actual (null si no hay alumno cargado)
     */
    public String getMatriculaAlumno(){ return matActual; }
    
    /** Pide una fecha a considerar como final para las asesorias al alumno del registro actual y
     *   lo regresa en vector junto con la fecha inicial para considerar como rango de vigencia de asesoria
     *   (se utiliza para la generacion de calendario semanal)
     * @return vector de dos elementos fecha inicial y fecha final de vigencia de asesorias al alumno del
     *   registro actual
     */
    public String[] getFechasVigencia(){
        String[] vigencia=new String[2];
        String aux;
        vigencia[0]=fechaIni;
        aux="<html>Para generar automaticamente sesiones<br>se debe considerar una rango de fechas.<br>";
        aux+="La fecha inicial es "+fechaIni+"<br>Ingrese la fecha a considerar como final para el rango";
        aux+="<br><i>(Debe tener el formato dd-mm-aaaa)</i></html>";
        aux=pideString("Se requiere rango de fechas",aux);
        if(aux==null) return null;
        if(!Datos.valFecha(aux)){
            muestraMensaje("Fecha invalida","<html>La fecha proporcionada es invalida<br><i>(Debe tener el formato dd-mm-aaaa)</i></html>",TipoMensaje.ERROR);
            return null;
        }
        if(Datos.compareFecha(fechaIni,aux)!=-1){
            muestraMensaje("Fecha invalida","<html>La fecha proporcionada debe ser posterior a la fecha inicial</html>",TipoMensaje.ERROR);
            return null;
        }
        vigencia[1]=aux;
        return vigencia;
    }
   
    /** Genera n sesiones de asesoria usando una fecha dada
     * (se utiliza para generar registrros de asesorias desde un control de horario semanal
     * para los dias de la semana que apliquen y dentro de un rango de fechas)
     * Al final muestra un dialog con un mensaje indicando cuantas sesiones se calcularon 
     * cuantos registros se generaron, cuanto fallo la generacion y cuantos existian previamente
     * @param fechas Lista de fechas a generar registro de sesion de asesoria
     *  cada fecha debe tener el formato: aaaa-mm-dd hh:mm+DD donde DD es la duracion
     *  en minutos (sera ignorada para el registro)
     */
    public void generaSesiones(ArrayList<String> fechas){
        int[] resultados=new int[3]; //indice 0=numYaExistentes, 1=numCreados, 2=numFallas
        StringTokenizer tk;
        String[] dts=new String[6];
        int aux;
        resultados[0]=resultados[1]=resultados[2]=0;
        for(String str:fechas){ 
            tk=new StringTokenizer(str);
            dts[0]=getMatriculaAlumno(); dts[1]=""+getClaveTesis();
            dts[2]=tk.nextToken("+")+":00";
            dts[3]="No Especificado!"; dts[4]=null; dts[5]=null;
            if(Actualiza.nuevoRgSesT(dts,true)){
                aux=Actualiza.obtenClave();
                if(aux==0) resultados[0]++;
                else resultados[1]++;
            }
            else{
                error=Actualiza.obtenError();
                resultados[2]++;
            }
        }
        actualiza();
        dts[0]="<html>Se ejecuto la generacion automatica de sesiones<br>";
        dts[0]+=fechas.size()+" sesiones calculadas de acuerdo a la tabla<br>Se generaron ";
        dts[0]+=resultados[1]+" sesiones nuevas<br>"+resultados[0]+" Existian previamente<br>Ocurrieron ";
        dts[0]+=resultados[2]+" errores";
        if(resultados[2]>0){
            dts[0]+="<br>El ultimo error detectado fue:<br>"+error;
        }
        muestraMensaje("Generacion de sesiones",dts[0]+"</html>",TipoMensaje.INFORMACION);
    }
    
    // Variables declaration - do not modify//GEN-BEGIN:variables
    private javax.swing.JButton btnAgreSes;
    private javax.swing.JButton btnCarga;
    private javax.swing.JButton btnElim;
    private javax.swing.JButton btnElimSes;
    private javax.swing.JButton btnGuardar;
    private javax.swing.JButton btnImprimir;
    private javax.swing.JButton btnModSes;
    private javax.swing.JButton btnRgDA;
    private javax.swing.JButton btnRgDG;
    private javax.swing.JButton btnRgper;
    private javax.swing.JLabel jLabel1;
    private javax.swing.JLabel jLabel3;
    private javax.swing.JLabel jLabel4;
    private javax.swing.JLabel jLabel5;
    private javax.swing.JLabel jLabel6;
    private javax.swing.JLabel jLabel7;
    private javax.swing.JLabel jLabel8;
    private javax.swing.JLabel jLabel9;
    private javax.swing.JPanel jPanel1;
    private javax.swing.JPanel jPanel2;
    private javax.swing.JScrollPane jScrollPane1;
    private javax.swing.JScrollPane jScrollPane2;
    private javax.swing.JScrollPane jScrollPane3;
    private javax.swing.JToolBar jToolBar1;
    private javax.swing.JComboBox jcbTipo;
    private javax.swing.JTable jtSesiones;
    private javax.swing.JTabbedPane jtbSecciones;
    private javax.swing.JPanel pnlDTTesis;
    private javax.swing.JPanel pnlDsesT;
    private javax.swing.JTextField txtClv;
    private javax.swing.JTextArea txtColab;
    private javax.swing.JTextField txtFchaIni;
    private javax.swing.JTextField txtNomAl;
    private javax.swing.JTextArea txtSino;
    private javax.swing.JTextField txtTitTesis;
    // End of variables declaration//GEN-END:variables

    /** Clase que implementa la interfaz DocumentListener 
     * para detectar si hay edicion en los controles de texto y saber si hay 
     * cambios sin guardar en el registro
     */
    private class ListenChanges implements javax.swing.event.DocumentListener{
        /** Crea un nuevo objeto ListenChanges */
        public ListenChanges(){}
        /** Metodo de la interfaz DocumentListener
         * detecta si se inserto contenido al documento (contenido del control de texto)
         * y avisa al FrameAsesoria que hay cambios sin guardar
         * @param e El DocumentEvent que genero el evento
         */
        public void insertUpdate(javax.swing.event.DocumentEvent e){ 
            setCambios(matActual!=null);
        }
        /** Metodo de la interfaz DocumentListener
         * detecta si se quito contenido al documento (contenido del control de texto)
         * y avisa al FrameAsesoria que hay cambios sin guardar
         * @param e El DocumentEvent que genero el evento
         */
        public void removeUpdate(javax.swing.event.DocumentEvent e){ 
            setCambios(matActual!=null);
        }
        /** Metodo de la interfaz DocumentListener
         * detecta si cambio el contenido del documento (contenido del control de texto)
         * @param e El DocumentEvent que genero el evento
         */
        public void changedUpdate(javax.swing.event.DocumentEvent e){}
    }
}

