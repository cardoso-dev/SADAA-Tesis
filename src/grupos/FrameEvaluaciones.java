/*
 * FrameEvaluaciones.java
 *  Ventana para mostrar rubros programados para calificar a los alumnos de un grupo 
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

package grupos;

import database.Actualiza;
import database.Consultas;
import definiciones.TipoMensaje;
import definiciones.TipoRespuesta;
import iconos.Iconos;
import javax.swing.table.DefaultTableModel;

/**  Es una ventana interna (JInternalFrame) para manejar los rubros de evaluacion 
 *   programadas de un grupo, proporciona los controles y metodos para agregar, modificar
 *   o eliminar rubros de evaluacion programadas
 * 
 * @author Pedro Cardoso Rodríguez
 */
public class FrameEvaluaciones extends sistema.ModeloFrameInterno implements java.awt.event.ActionListener{
    
    /**Clave del grupo actual*/
    private String clave;
    /**Materia del grupo actual*/
    private String materia;
    /**PopUpMenu para el control jtRubros para acciones sobre los rubros programados*/
    private javax.swing.JPopupMenu pmnuRub;
    
    /** Crea una nueva ventana FrameEvaluaciones
     * @param ventana Referencia a la ventana principal contenedora (clase sistema.FramePrincipal)
     */
    public FrameEvaluaciones(sistema.FramePrincipal ventana){
        super(ventana,"frmcalif.png");
        initComponents();
        btnCarga.setIcon(Iconos.getIcono("cargar.png"));       
        btnProgSes.setIcon(Iconos.getIcono("frmcal.png"));   
        btnProgSes.setText("");
        btnVerFicha.setIcon(Iconos.getIcono("frmgru.png"));   
        btnVerFicha.setText("");
        btnAgrega.setIcon(Iconos.getIcono("nuevo.png"));
        btnQuitar.setIcon(Iconos.getIcono("elimina.png"));   
        btnMod.setIcon(Iconos.getIcono("modifica.png"));   
        crearPopMenu();
        clave=null;
    }
    
    /** This method is called from within the constructor to
     * initialize the form.
     * WARNING: Do NOT modify this code. The content of this method is
     * always regenerated by the Form Editor.
     */
    // <editor-fold defaultstate="collapsed" desc="Generated Code">//GEN-BEGIN:initComponents
    private void initComponents() {

        jToolBar1 = new javax.swing.JToolBar();
        btnVerFicha = new javax.swing.JButton();
        btnProgSes = new javax.swing.JButton();
        jPanel2 = new javax.swing.JPanel();
        jLabel3 = new javax.swing.JLabel();
        txtClv = new javax.swing.JTextField();
        btnCarga = new javax.swing.JButton();
        jPanel3 = new javax.swing.JPanel();
        jLabel9 = new javax.swing.JLabel();
        lblDescGrupo = new javax.swing.JLabel();
        txtClave = new javax.swing.JTextField();
        jLabel4 = new javax.swing.JLabel();
        lblTitSes = new javax.swing.JLabel();
        btnAgrega = new javax.swing.JButton();
        btnMod = new javax.swing.JButton();
        btnQuitar = new javax.swing.JButton();
        jScrollPane3 = new javax.swing.JScrollPane();
        jtRubros = new javax.swing.JTable();

        setClosable(true);
        setDefaultCloseOperation(javax.swing.WindowConstants.DO_NOTHING_ON_CLOSE);
        setIconifiable(true);
        setMaximizable(true);
        setResizable(true);
        setTitle("Rubros de evaluación para grupo");
        setToolTipText("");

        jToolBar1.setFloatable(false);
        jToolBar1.setRollover(true);

        btnVerFicha.setText("fich");
        btnVerFicha.setToolTipText("Ficha de grupo");
        btnVerFicha.setEnabled(false);
        btnVerFicha.setFocusable(false);
        btnVerFicha.setHorizontalTextPosition(javax.swing.SwingConstants.CENTER);
        btnVerFicha.setVerticalTextPosition(javax.swing.SwingConstants.BOTTOM);
        btnVerFicha.addActionListener(new java.awt.event.ActionListener() {
            public void actionPerformed(java.awt.event.ActionEvent evt) {
                btnVerFichaActionPerformed(evt);
            }
        });
        jToolBar1.add(btnVerFicha);

        btnProgSes.setText("pses");
        btnProgSes.setToolTipText("Calendario de sesiones");
        btnProgSes.setEnabled(false);
        btnProgSes.setFocusable(false);
        btnProgSes.setHorizontalTextPosition(javax.swing.SwingConstants.CENTER);
        btnProgSes.setVerticalTextPosition(javax.swing.SwingConstants.BOTTOM);
        btnProgSes.addActionListener(new java.awt.event.ActionListener() {
            public void actionPerformed(java.awt.event.ActionEvent evt) {
                btnProgSesActionPerformed(evt);
            }
        });
        jToolBar1.add(btnProgSes);

        jPanel2.setBorder(javax.swing.BorderFactory.createBevelBorder(javax.swing.border.BevelBorder.RAISED));

        jLabel3.setText("Buscar Clave:");

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
                .addContainerGap(271, Short.MAX_VALUE)
                .addComponent(jLabel3)
                .addPreferredGap(javax.swing.LayoutStyle.ComponentPlacement.RELATED)
                .addComponent(txtClv, javax.swing.GroupLayout.PREFERRED_SIZE, 142, javax.swing.GroupLayout.PREFERRED_SIZE)
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

        jLabel9.setText("Grupo clave:");

        lblDescGrupo.setText("Descripción: GradoGrupo Materia [en Aula]");

        txtClave.setEditable(false);

        jLabel4.setHorizontalAlignment(javax.swing.SwingConstants.CENTER);
        jLabel4.setText("Datos del grupo: ");

        lblTitSes.setText("Rubros de evaluación programados para el grupo:");

        btnAgrega.setMnemonic('G');
        btnAgrega.setText("Agregar");
        btnAgrega.setToolTipText("Agregar nueva sesion");
        btnAgrega.setEnabled(false);
        btnAgrega.addActionListener(new java.awt.event.ActionListener() {
            public void actionPerformed(java.awt.event.ActionEvent evt) {
                btnAgregaActionPerformed(evt);
            }
        });

        btnMod.setText("Modificar");
        btnMod.setToolTipText("Modificar sesion");
        btnMod.setEnabled(false);
        btnMod.addActionListener(new java.awt.event.ActionListener() {
            public void actionPerformed(java.awt.event.ActionEvent evt) {
                btnModActionPerformed(evt);
            }
        });

        btnQuitar.setMnemonic('Q');
        btnQuitar.setText("Quitar");
        btnQuitar.setToolTipText("Quitar sesion");
        btnQuitar.setEnabled(false);
        btnQuitar.addActionListener(new java.awt.event.ActionListener() {
            public void actionPerformed(java.awt.event.ActionEvent evt) {
                btnQuitarActionPerformed(evt);
            }
        });

        jtRubros.setAutoCreateRowSorter(true);
        jtRubros.setModel(new javax.swing.table.DefaultTableModel(
            new Object [][] {

            },
            new String [] {
                "Clave", "Tipo", "Descripcion", "Fecha en que solicita", "Fecha en que califica"
            }
        ) {
            boolean[] canEdit = new boolean [] {
                false, false, false, false, false
            };

            public boolean isCellEditable(int rowIndex, int columnIndex) {
                return canEdit [columnIndex];
            }
        });
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
        jScrollPane3.setViewportView(jtRubros);
        jtRubros.getColumnModel().getColumn(0).setPreferredWidth(50);
        jtRubros.getColumnModel().getColumn(1).setPreferredWidth(140);
        jtRubros.getColumnModel().getColumn(2).setPreferredWidth(200);
        jtRubros.getColumnModel().getColumn(3).setPreferredWidth(130);
        jtRubros.getColumnModel().getColumn(4).setPreferredWidth(130);

        javax.swing.GroupLayout jPanel3Layout = new javax.swing.GroupLayout(jPanel3);
        jPanel3.setLayout(jPanel3Layout);
        jPanel3Layout.setHorizontalGroup(
            jPanel3Layout.createParallelGroup(javax.swing.GroupLayout.Alignment.LEADING)
            .addGroup(jPanel3Layout.createSequentialGroup()
                .addContainerGap()
                .addGroup(jPanel3Layout.createParallelGroup(javax.swing.GroupLayout.Alignment.LEADING)
                    .addGroup(jPanel3Layout.createSequentialGroup()
                        .addGap(10, 10, 10)
                        .addComponent(lblTitSes)
                        .addPreferredGap(javax.swing.LayoutStyle.ComponentPlacement.RELATED, 131, Short.MAX_VALUE)
                        .addComponent(btnAgrega)
                        .addPreferredGap(javax.swing.LayoutStyle.ComponentPlacement.RELATED)
                        .addComponent(btnMod)
                        .addPreferredGap(javax.swing.LayoutStyle.ComponentPlacement.RELATED)
                        .addComponent(btnQuitar))
                    .addGroup(jPanel3Layout.createSequentialGroup()
                        .addComponent(jLabel9)
                        .addPreferredGap(javax.swing.LayoutStyle.ComponentPlacement.RELATED)
                        .addComponent(txtClave, javax.swing.GroupLayout.PREFERRED_SIZE, 163, javax.swing.GroupLayout.PREFERRED_SIZE)
                        .addPreferredGap(javax.swing.LayoutStyle.ComponentPlacement.UNRELATED)
                        .addComponent(lblDescGrupo))
                    .addComponent(jScrollPane3, javax.swing.GroupLayout.DEFAULT_SIZE, 602, Short.MAX_VALUE)
                    .addComponent(jLabel4, javax.swing.GroupLayout.Alignment.TRAILING, javax.swing.GroupLayout.DEFAULT_SIZE, 602, Short.MAX_VALUE))
                .addContainerGap())
        );
        jPanel3Layout.setVerticalGroup(
            jPanel3Layout.createParallelGroup(javax.swing.GroupLayout.Alignment.LEADING)
            .addGroup(jPanel3Layout.createSequentialGroup()
                .addComponent(jLabel4)
                .addPreferredGap(javax.swing.LayoutStyle.ComponentPlacement.RELATED)
                .addGroup(jPanel3Layout.createParallelGroup(javax.swing.GroupLayout.Alignment.BASELINE)
                    .addComponent(jLabel9)
                    .addComponent(txtClave, javax.swing.GroupLayout.PREFERRED_SIZE, javax.swing.GroupLayout.DEFAULT_SIZE, javax.swing.GroupLayout.PREFERRED_SIZE)
                    .addComponent(lblDescGrupo))
                .addPreferredGap(javax.swing.LayoutStyle.ComponentPlacement.RELATED)
                .addGroup(jPanel3Layout.createParallelGroup(javax.swing.GroupLayout.Alignment.BASELINE)
                    .addComponent(lblTitSes)
                    .addComponent(btnAgrega)
                    .addComponent(btnMod)
                    .addComponent(btnQuitar))
                .addPreferredGap(javax.swing.LayoutStyle.ComponentPlacement.RELATED)
                .addComponent(jScrollPane3, javax.swing.GroupLayout.DEFAULT_SIZE, 331, Short.MAX_VALUE)
                .addContainerGap())
        );

        getContentPane().add(jPanel3, java.awt.BorderLayout.CENTER);

        pack();
    }// </editor-fold>//GEN-END:initComponents

    /** Llama al metodo que muestra el popupmenu para la tabla de rubros (control jtRubros)
     * @param evt El MouseEvent que genero el evento
     */
    private void jtRubrosMouseClicked(java.awt.event.MouseEvent evt) {//GEN-FIRST:event_jtRubrosMouseClicked
        llamaMenu(evt);
}//GEN-LAST:event_jtRubrosMouseClicked

    /** Llama al metodo que muestra el popupmenu para la tabla de rubros (control jtRubros)
     * @param evt El MouseEvent que genero el evento
     */
    private void jtRubrosMousePressed(java.awt.event.MouseEvent evt) {//GEN-FIRST:event_jtRubrosMousePressed
        llamaMenu(evt);
}//GEN-LAST:event_jtRubrosMousePressed

    /** Llama al metodo que muestra el popupmenu para la tabla de rubros (control jtRubros)
     * @param evt El MouseEvent que genero el evento
     */
    private void jtRubrosMouseReleased(java.awt.event.MouseEvent evt) {//GEN-FIRST:event_jtRubrosMouseReleased
        llamaMenu(evt);
}//GEN-LAST:event_jtRubrosMouseReleased

    /** Llama al metodo mostrarPopupMenu solo si el click hecho sobre el control jtRubros
     *   fue hecho sobre el area de celdas validas
     * @param evt El MouseEvent que se genero al hacer click sobre jtRubros
     */
    private void llamaMenu(java.awt.event.MouseEvent evt){
        int col=jtRubros.columnAtPoint(evt.getPoint());
        int fila=jtRubros.rowAtPoint(evt.getPoint());
        int filaClave=0;
        for(int i=0;i<jtRubros.getColumnCount();i++)
            if(jtRubros.getColumnName(i).equals("Clave")){
                filaClave=i; break;
            }
        if (fila>-1 && col>-1 && jtRubros.getValueAt(fila,filaClave)!=null)
            mostrarPopupMenu(evt,""+jtRubros.getValueAt(fila,filaClave),fila);
    }

    /** Abre el registro (ventana clase FrmRegEval) de un rubro perteneciente al grupo actual
     *   para dar la opcion de modificar/actualizar sus datos
     * @param claverub La clave del rubro a abrir
     */
    private void modificaRubro(int claverub){
        DefaultTableModel modelo=(DefaultTableModel)jtRubros.getModel();        
        FrmRegEval newRegInd;
        if(!existeVentana("Registro de rubro "+claverub+" para grupo "+clave, true)){
            String[] datos = new String[modelo.getColumnCount()];
            for(int g=0;g<modelo.getRowCount();g++){
                if(Integer.parseInt(""+modelo.getValueAt(g,0))==claverub){
                    for(int j=0;j<datos.length;j++)
                        if(modelo.getValueAt(g,j)!=null) datos[j]=""+modelo.getValueAt(g,j);
                        else datos[j]=null;
                    break;
                }
            }            
            newRegInd = new FrmRegEval(getPrincipalVnt(),this,null,null,clave,claverub,datos,0);
            agregaVentana(newRegInd);
        } 
    }

    /** Pide la clave de un rubro perteneciente al grupo actual y si existe envia al metodo para eliminar su registro
     * @param evt El ActionEvent que genero el evento
     */
    private void btnQuitarActionPerformed(java.awt.event.ActionEvent evt) {//GEN-FIRST:event_btnQuitarActionPerformed
        DefaultTableModel modelo=(DefaultTableModel)jtRubros.getModel();
        int tmp;
        boolean bandera=false;
        int claverub=pideEntero("Eliminar un rubro", "¿Clave del rubro que dese eliminar?");
        for(int i=0;i<modelo.getRowCount();i++){
            tmp = Integer.parseInt(""+modelo.getValueAt(i,0));
            if(tmp==claverub){
                quitarRubro(claverub);
                bandera=true;
                break;
            }
        }
        if(!bandera){
            muestraMensaje("No se pudo realizar la accion", "No se encontro rubro con la clave: "+claverub,TipoMensaje.ERROR);
        }
    }//GEN-LAST:event_btnQuitarActionPerformed

    /** Elimina el registro de un rubro perteneciente al grupo actual
     * @param claverub La clave del rubro a eliminar
     */
    private void quitarRubro(int claverub){
        TipoRespuesta tmp=pideDesicion("Confirme la accion", "En verdad desea eliminar completamente el rubro: "+claverub);
        if(tmp.getTipo()!=TipoRespuesta.ACEPTAR.getTipo()) return;
        if(!Actualiza.actualiza("delete from RubrosCalif where ClvRU="+claverub+";",false,true))
            muestraMensaje("No se pudo realizar la accion", Actualiza.obtenError(),TipoMensaje.ERROR);
        else{
            muestraMensaje("Accion realizada", "Se ha eliminado completamente el registro",TipoMensaje.INFORMACION);
            actualiza();
        }
    }

    /** Vuelve a cargar todos los registros desde la base de datos para el grupo actual */
    public void actualiza(){        
        cargaDatosGrupo(clave);        
        if(existeVentana("Ficha de grupo "+clave,false)){
            FrameGrupo fich=((FrameGrupo)obtenVentana("Ficha de grupo "+clave));
            fich.cargaDatosGrupo(clave);
        }
    }
   
    /** Abre una ventana (clase FrmRegEval) para crear el registro de un nueva rubro de evaluacion
     * @param evt El ActionEvent que genero el evento
     */
    private void btnAgregaActionPerformed(java.awt.event.ActionEvent evt) {//GEN-FIRST:event_btnAgregaActionPerformed
        FrmRegEval newRegInd;
        if(existeVentana("Registro de rubro para grupo "+clave, true)) return;
        newRegInd = new FrmRegEval(getPrincipalVnt(),this,null,null,clave,-1,null,0);
        agregaVentana(newRegInd);
    }//GEN-LAST:event_btnAgregaActionPerformed

    /** Pide la clave de un rubro de evaluacion perteneciente al grupo actual y si existe abre 
     *  su registro (ventana clase FrmRegEval) para dar la opcione de modificar/actualizar sus datos
     * @param evt El ActionEvent que genero el evento
     */
    private void btnModActionPerformed(java.awt.event.ActionEvent evt) {//GEN-FIRST:event_btnModActionPerformed
        DefaultTableModel modelo=(DefaultTableModel)jtRubros.getModel();
        boolean bandera=false;
        int claverub=pideEntero("Modificar datos de un rubro", "¿Clave del rubro que dese modificar?");
        for(int i=0;i<modelo.getRowCount();i++){
            if(Integer.parseInt(""+modelo.getValueAt(i,0))==claverub){
                modificaRubro(claverub);
                bandera=true;
                break;
            }
        }
        if(!bandera){
            muestraMensaje("No se pudo realizar la accion", "No se encontro registro con la clave: "+claverub,TipoMensaje.INFORMACION);
        }
    }//GEN-LAST:event_btnModActionPerformed

    /** Abre la ventana con la ficha del grupo actual
     * @param evt El ActionEvent que genero el evento
     */
    private void btnVerFichaActionPerformed(java.awt.event.ActionEvent evt) {//GEN-FIRST:event_btnVerFichaActionPerformed
        getPrincipalVnt().agregaVentanaGrupo(txtClave.getText(),1);
    }//GEN-LAST:event_btnVerFichaActionPerformed

    /** Valida la clave ingresada en el control txtClv y busca en la base de datos un grupo con esa clave
     * @param evt El ActionEvent que genero el evento
     */
    private void btnCargaActionPerformed(java.awt.event.ActionEvent evt) {//GEN-FIRST:event_btnCargaActionPerformed
        String tmpClv=txtClv.getText().trim().toUpperCase();
        if(tmpClv.equals("")) {
            muestraMensaje("Error en el parametro", "Clave invalida",TipoMensaje.ERROR);
            return;
        }
        if(getTitle().equals("Rubros de evaluacion grupo "+tmpClv))
            actualiza();
        else if(existeVentana("Rubros de evaluacion grupo "+tmpClv,true)){
            txtClv.setText(tmpClv);
            return;
        } else cargaDatosGrupo(tmpClv);
    }//GEN-LAST:event_btnCargaActionPerformed

    /** Busca en la base de datos el registro de un grupo y si lo encuentra carga 
     *   todos los registros relaciondos con los controles de esta ventana
     * @param newClave La clave del grupo a cargar
     * @return true si encontro el grupo y cargo la informacion false en caso de 
     *   que el grupo no exista o halla ocurrido algun error al cargar los datos.
     */
    public boolean cargaDatosGrupo(String newClave){
        String[] tipos={"Tarea","Investigacion","Exposicion","Proyecto","Practica","Examen parcial",
        "Examen final","Examen ordinario","Examen extraordinario","Calificacion final","Otro"};
        String[] dts = Consultas.consultaUnCampo("select grupos.*,nombre from grupos,imparte,materias where grupos.clvg='"+newClave+"' and grupos.clvg=imparte.clvg and materias.clvm=imparte.clvm;",true);
        DefaultTableModel modelo;
        DefaultTableModel modelPrevio;
        String sentencia;
        if(dts==null){
            muestraMensaje("Error al hacer la busqueda", Consultas.obtenError(),TipoMensaje.ERROR);
            return false;
        }
        else if(dts[0]!=null){
            setTitle("Rubros de evaluación para grupo "+newClave);
            txtClave.setText(""+newClave); materia=dts[6];
            lblDescGrupo.setText(dts[1]+dts[2]+" "+materia+(dts[5]!=null?" en Aula "+dts[5]:""));
            btnAgrega.setEnabled(true); btnVerFicha.setEnabled(true);            
            btnProgSes.setEnabled(true);
            clave=newClave;
            sentencia="select RubrosCalif.ClvRU as 'Clave',Tipo,Descripcion,FchaSol as 'Fecha solicita',";
            sentencia+="FchaCal as 'Fecha califica' from RubrosCalif,CalificaCon where RubrosCalif.ClvRU=";
            sentencia+="CalificaCon.ClvRU and CalificaCon.ClvG='"+newClave+"' order by FchaCal;";
            modelo=Consultas.consTipoTable(sentencia,false);                        
            if(modelo==null){
                muestraMensaje("Error al consultar los rubros de evaluacion", Consultas.obtenError(),TipoMensaje.ERROR);
                return false;
            }
            modelPrevio=((DefaultTableModel)jtRubros.getModel());
            while(modelPrevio.getRowCount()>0){modelPrevio.removeRow(0);}
            if(modelo.getRowCount()>0){
                for(int f=0;f<modelo.getRowCount();f++){
                    Object[] row=new Object[modelo.getColumnCount()];
                    int auxt=Integer.parseInt(""+modelo.getValueAt(f,1));
                    for(int i=0;i<row.length;i++){
                        if(i==0) row[i]=Integer.parseInt(""+modelo.getValueAt(f,i));
                        else if(modelo.getValueAt(f,i)!=null) row[i]=""+modelo.getValueAt(f,i);
                        else row[i]=null;
                    }
                    row[1]=tipos[auxt-1];
                    modelPrevio.addRow(row);
                    
                }
                btnQuitar.setEnabled(true); btnMod.setEnabled(true);
            }
            lblTitSes.setText("Rubros de evaluacion programados para el grupo: "+jtRubros.getRowCount());
            return true;
        }
        else{
            muestraMensaje("Error", "No se encontraron datos para esa clave",TipoMensaje.ERROR);
            return false;
        }
    }
 
    /** Crea el popupmenu con la accion quitar para los registros de la tabla jtbRubros */
    private void crearPopMenu(){ 
        pmnuRub=new javax.swing.JPopupMenu();
        javax.swing.JMenuItem mnua=new javax.swing.JMenuItem("Modificar datos");
        mnua.setActionCommand("Modificar datos");
        mnua.addActionListener(this);
        javax.swing.JMenuItem mnuq=new javax.swing.JMenuItem("Quitar rubro");
        mnuq.setActionCommand("Quitar rubro");
        mnuq.addActionListener(this);
        pmnuRub.add(mnua);
        pmnuRub.add(mnuq);
    }
    
    /** Muestra el popupmenu sobre la tabla jtRubros solo si el click hecho sobre el control jtRubros
     *   ese el disparador de popup segun el so sobre el que se ejecuta el sistema
     * @param evt El MouseEvent que se genero al hacer click sobre jtRubros
     * @param txClv El texto de la fila sobre la que se hizo click
     * @param fila El numero de fila sobre la que se hizo click
     */
    private void mostrarPopupMenu(java.awt.event.MouseEvent evt, String txClv,int fila){
        java.awt.Component[] mns;
        if (evt.isPopupTrigger()){
            mns =pmnuRub.getComponents();
            for(int y=0;y<mns.length;y++){
                String tmp=((javax.swing.JMenuItem)mns[y]).getText();
                ((javax.swing.JMenuItem)mns[y]).setActionCommand(tmp+":"+txClv+":"+fila);
            }
            jtRubros.setRowSelectionInterval(fila, fila);
            pmnuRub.show(evt.getComponent(),evt.getX(),evt.getY());
        }
    }
   
    /** Abre la ventana con el calendario de sesiones del grupo actual
     * @param evt El ActionEvent que genero el evento
     */
    private void btnProgSesActionPerformed(java.awt.event.ActionEvent evt) {//GEN-FIRST:event_btnProgSesActionPerformed
        getPrincipalVnt().agregaVentanaGrupo(txtClave.getText(),2);
    }//GEN-LAST:event_btnProgSesActionPerformed
   
    /** Es la implementacion del action command de los menus del menu emergente 
     *   (control pmnuRub) de acuerdo al ActionCommand de evento llama al metodo 
     *   de la accion en la cual se hizo click (modificar o quitar)
     * @param evt El ActionEvent que genero el evento
     */
    public void actionPerformed(java.awt.event.ActionEvent evt){
        java.util.StringTokenizer toks=new java.util.StringTokenizer(evt.getActionCommand(),":");
        String accion=toks.nextToken();
        int claveRub=Integer.parseInt(toks.nextToken());     
        //int fila=Integer.parseInt(toks.nextToken());
        if(accion.equals("Modificar datos")) modificaRubro(claveRub);
        else quitarRubro(claveRub);
        pmnuRub.setVisible(false);
    }
    
    // Variables declaration - do not modify//GEN-BEGIN:variables
    private javax.swing.JButton btnAgrega;
    private javax.swing.JButton btnCarga;
    private javax.swing.JButton btnMod;
    private javax.swing.JButton btnProgSes;
    private javax.swing.JButton btnQuitar;
    private javax.swing.JButton btnVerFicha;
    private javax.swing.JLabel jLabel3;
    private javax.swing.JLabel jLabel4;
    private javax.swing.JLabel jLabel9;
    private javax.swing.JPanel jPanel2;
    private javax.swing.JPanel jPanel3;
    private javax.swing.JScrollPane jScrollPane3;
    private javax.swing.JToolBar jToolBar1;
    private javax.swing.JTable jtRubros;
    private javax.swing.JLabel lblDescGrupo;
    private javax.swing.JLabel lblTitSes;
    private javax.swing.JTextField txtClave;
    private javax.swing.JTextField txtClv;
    // End of variables declaration//GEN-END:variables
    
}