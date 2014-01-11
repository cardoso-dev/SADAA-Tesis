/*
 * ControlMateria.java
 *   Ficha de datos de una materia registrada
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

import database.Actualiza;
import database.Consultas;
import definiciones.TipoMensaje;
import definiciones.TipoRespuesta;
import iconos.Iconos;
import java.util.ArrayList;
import java.util.HashMap;
import java.util.Map;
import javax.swing.event.TableModelEvent;
import javax.swing.event.TableModelListener;
import javax.swing.table.DefaultTableModel;
import reportes.ImpTemas;

/** Crea un control para manejar los datos de materias impartidas por el docente
 *   maneja los datos generales de la materia asi como lista de temas que cubre
 *   se utiliza en la ventana de datos del docente (FrameDocente)
 * 
 * @author  Pedro Cardoso Rodriguez
 */
public class ControlMateria extends javax.swing.JPanel implements TableModelListener{
    
    /**La clave de la materia actual*/
    private String clave;
    /**El nombre de la materia actual*/
    private String nom;
    /**Indica si hay cambios en los datos generales de la materia*/
    private boolean hayCambiosMat;
    /**Indica si hay cambios en los temarios de la materia*/
    private boolean hayCambiosTem;
    /**Descripcion del ultimo error ocurrido*/
    private String error;
    /**Referencia a la ventana de la cual depende este control*/
    private FrameDocente datosdoc;
    /**Indica cuando se debe procesar y cuando ignorar el ItemStateChanged en caso de ser llamado (como un semaforo)*/
    private boolean bandera;
    /**Version actual del temario*/
    private int verActu;
    
    /** Crea un nuevo ControlMateria
     * @param datosdoc Referencia a la ventana de la cual depende este control
     */
    public ControlMateria(FrameDocente datosdoc) {
        ModeloTemario tems=new ModeloTemario();
        this.datosdoc=datosdoc;
        initComponents();
        clave=null; nom=null; verActu=0;
        error=null; hayCambiosMat=hayCambiosTem=false;
        btnUp.setText(""); btnUp.setIcon(Iconos.getIcono("up.png"));
        btnDown.setText(""); btnDown.setIcon(Iconos.getIcono("down.png"));
        btnPrint.setText(""); btnPrint.setIcon(Iconos.getIcono("impresora.png"));
        tems.addTableModelListener(this);
        jtbTemario.setModel(tems);
        organizaColumnas();
        jtbTemario.setSelectionMode(javax.swing.ListSelectionModel.SINGLE_SELECTION);
        bandera=true;
        escuchaCambios();
    }
    
    /** Carga una materia desde la base de datos
     * @param clave La clave la materia a cargar
     * @param version La version del temario a cargar o -1 para cargar la version mas reciente
     * @return true si se cargo todo correctamente false en caso contrario
     */
    public boolean cargaMateria(String clave, int version){
        String[] datos=Consultas.consultaUnCampo("Select * from Materias where ClvM='"+clave+"';",false);
        javax.swing.table.DefaultTableModel temas;
        ModeloTemario tems;
        java.util.Vector cols=new java.util.Vector();
        int[] versiones;
        if(datos==null){ error=Consultas.obtenError(); return false; }
        else if(datos[0]==null){ error="La materia con clave "+clave+" no existe"; return false; }
        else{
            this.clave=clave; txtClvm.setText(this.clave); txtClvm.setEditable(false);
            txtNombre.setText(datos[1]); nom=datos[1]; txtCalMin.setText(datos[2]);
            datos[0]="Select distinct(temario.version) from temario,cubre where temario.clvtem=cubre.clvtem and cubre.clvm='"+clave+"' order by version;";
            versiones=Consultas.consultaEnteros(datos[0],false);
            if(versiones==null){ error=Consultas.obtenError(); return false; }
            else if(versiones[0]!=-1){
                bandera=false;
                jcbVersiones.removeAllItems();
                for(int s=0;s<versiones.length;s++){
                    jcbVersiones.addItem(""+versiones[s]);
                }
                if(version==-1){
                    version=versiones[versiones.length-1];
                }
                jcbVersiones.setSelectedItem(""+version);
                verActu=version;
                bandera=true;
                datos[0]="Select temario.* from temario,cubre where temario.clvtem=cubre.clvtem and ";
                datos[0]+="cubre.clvm='"+clave+"' and version="+version+" order by orden;";
                temas=Consultas.consTipoTable(datos[0],false);
                if(temas==null){ error=Consultas.obtenError(); return false; }
                cols.add("ClvTem"); cols.add("Version"); cols.add("Orden");
                cols.add("Numero"); cols.add("Titulo"); cols.add("Contenido");
                tems=new ModeloTemario(temas,cols,version);
                tems.addTableModelListener(this);
                jtbTemario.setModel(tems);
                btnAgregaT.setEnabled(true);
                btnQuitaT.setEnabled(true);
                btnUp.setEnabled(true);
                btnDown.setEnabled(true);
                btnPrint.setEnabled(true);
                organizaColumnas();
            }
        }
        setCambios(false,false);
        error=null; return true;
    }
    
    /** Genera y obtiene una lista de comandos sql para realizar como transaccion
     * para guardar los datos del registro de materia actual y su temario
     * @return Lista de senetencias sql para actualizar datos del registro o null en caso de error al generar sentencias
     */
    public java.util.ArrayList<String> getTransGuardar(){
        int clv;
        String sen;
        java.util.ArrayList<String> trans;
        java.util.ArrayList<Integer> aQuitar;
        if(clave==null && !guardaMateria()){
            return null;
        }
        if(!sonDatosValidos()){
            return null;
        }
        trans=new java.util.ArrayList<String>();
        trans.add("update materias set nombre='"+txtNombre.getText()+"', calmin="+txtCalMin.getText()+" where clvm='"+txtClvm.getText()+"';");
        for(int fila=0;fila<jtbTemario.getRowCount();fila++){
            clv=Integer.parseInt(""+jtbTemario.getValueAt(fila,0));
            if(clv==-1){
                clv=guardaFila(fila);
                if(clv==-1){
                    error="Error al intentar guardar fila "+(fila+1)+"\n"+error;
                    return null;
                }
                jtbTemario.setValueAt(""+clv,fila,0);
            }
            else{
                sen="update Temario set version="+jtbTemario.getValueAt(fila,1)+", orden=";
                sen+=""+(fila+1)+", NumTem='"+jtbTemario.getValueAt(fila,3)+"', TitTem=";
                if((""+jtbTemario.getValueAt(fila,4)).length()>0){
                    sen+="'"+jtbTemario.getValueAt(fila,4)+"'";
                }
                else{
                    sen+="null";
                }
                sen+=", Conts=";
                jtbTemario.setValueAt(""+(fila+1),fila,2);
                if((""+jtbTemario.getValueAt(fila,5)).length()>0){
                    sen+="'"+jtbTemario.getValueAt(fila,5)+"'";
                }
                else{
                    sen+="null";
                }
                sen+=" where ClvTem="+jtbTemario.getValueAt(fila,0)+";";
                trans.add(sen);
            }
        }
        aQuitar=((ModeloTemario)jtbTemario.getModel()).getQuitados();
        for(int d=0;d<aQuitar.size();d++){
            trans.add("delete from Temario where clvtem="+aQuitar.get(d)+";");
        }
        return trans;
    }
    
    /** Guarda el registro de una fila de la tabla de temas
     * @param fila El indice de la fila a guardar
     * @return true si los datos se guardaron correctamente false en caso contrario
     */
    private int guardaFila(int fila){
        String[] datos=new String[6];
        datos[0]=clave;
        datos[1]=""+jtbTemario.getValueAt(fila,1);
        datos[2]=""+(fila+1);
        jtbTemario.setValueAt(""+(fila+1),fila,2);
        datos[3]=""+jtbTemario.getValueAt(fila,3);
        datos[4]=""+jtbTemario.getValueAt(fila,4);
        if(jtbTemario.getValueAt(fila,5)!=null){
            datos[5]=""+jtbTemario.getValueAt(fila,5);
        }
        else{
            datos[5]=null;
        }
        if(Actualiza.nuevoTema(datos,true)){
            return Actualiza.obtenClave();
        }
        else {
            error=Actualiza.obtenError();
            return -1;
        }
    }
    
    /** Guarda los datos generales de la materia en un nuevo registro
     * @return true si los datos se guardaron correctamente false en caso contrario
     */
    private boolean guardaMateria(){
        String sen="insert into materias values('";
        float calMin=-1;
        if(txtClvm.getText().length()!=3){
            error="Clave de materia invalida";
            return false;
        }
        if(txtNombre.getText().trim().length()<1 || txtNombre.getText().length()>45){
            error="Nombre de materia invalida";
            return false;
        }
        try{ calMin=Float.parseFloat(txtCalMin.getText()); }
        catch(NumberFormatException nfbExc){ calMin=-1; }
        if(calMin<0 || calMin>10){
            error="Calificacion minima invalida";
            return false;
        }
        sen+=txtClvm.getText().toUpperCase()+"','"+txtNombre.getText()+"',"+txtCalMin.getText()+");";
        if(Actualiza.actualiza(sen,false,false)){
            clave=txtClvm.getText().toUpperCase();
            nom=txtNombre.getText();
            txtClvm.setText(clave);
            return true;
        }
        else{
            error=Actualiza.obtenError();
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

        btnCreaVer = new javax.swing.JButton();
        jcbVersiones = new javax.swing.JComboBox();
        jLabel5 = new javax.swing.JLabel();
        btnQuitaT = new javax.swing.JButton();
        jLabel4 = new javax.swing.JLabel();
        btnAgregaT = new javax.swing.JButton();
        jScrollPane1 = new javax.swing.JScrollPane();
        jtbTemario = new javax.swing.JTable();
        txtCalMin = new javax.swing.JTextField();
        jLabel3 = new javax.swing.JLabel();
        txtNombre = new javax.swing.JTextField();
        jLabel2 = new javax.swing.JLabel();
        txtClvm = new javax.swing.JTextField();
        jLabel1 = new javax.swing.JLabel();
        jLabel6 = new javax.swing.JLabel();
        btnUp = new javax.swing.JButton();
        btnDown = new javax.swing.JButton();
        btnPrint = new javax.swing.JButton();

        btnCreaVer.setText("Crear nueva versión");
        btnCreaVer.addActionListener(new java.awt.event.ActionListener() {
            public void actionPerformed(java.awt.event.ActionEvent evt) {
                btnCreaVerActionPerformed(evt);
            }
        });

        jcbVersiones.addItemListener(new java.awt.event.ItemListener() {
            public void itemStateChanged(java.awt.event.ItemEvent evt) {
                jcbVersionesItemStateChanged(evt);
            }
        });

        jLabel5.setText("Temario versión:");

        btnQuitaT.setText("Quitar");
        btnQuitaT.setEnabled(false);
        btnQuitaT.addActionListener(new java.awt.event.ActionListener() {
            public void actionPerformed(java.awt.event.ActionEvent evt) {
                btnQuitaTActionPerformed(evt);
            }
        });

        jLabel4.setText("Otras acciones:");

        btnAgregaT.setText("Agregar");
        btnAgregaT.setEnabled(false);
        btnAgregaT.addActionListener(new java.awt.event.ActionListener() {
            public void actionPerformed(java.awt.event.ActionEvent evt) {
                btnAgregaTActionPerformed(evt);
            }
        });

        jtbTemario.setModel(new javax.swing.table.DefaultTableModel(
            new Object [][] {

            },
            new String [] {
                "ClvTem", "Version", "Orden", "Numero", "Titulo", "Contenido"
            }
        ));
        jtbTemario.setAutoResizeMode(javax.swing.JTable.AUTO_RESIZE_OFF);
        jtbTemario.getTableHeader().setReorderingAllowed(false);
        jScrollPane1.setViewportView(jtbTemario);

        jLabel3.setText("Calificación mínima aprobatoria:");

        jLabel2.setText("Nombre:");

        jLabel1.setText("Clave:");

        jLabel6.setFont(new java.awt.Font("Tahoma", 1, 12));
        jLabel6.setHorizontalAlignment(javax.swing.SwingConstants.CENTER);
        jLabel6.setText("Temario planeado");

        btnUp.setText("UP");
        btnUp.setToolTipText("Mover el tema seleccionado arriba");
        btnUp.setEnabled(false);
        btnUp.addActionListener(new java.awt.event.ActionListener() {
            public void actionPerformed(java.awt.event.ActionEvent evt) {
                btnUpActionPerformed(evt);
            }
        });

        btnDown.setText("DN");
        btnDown.setToolTipText("Mover el tema seleccionado abajo");
        btnDown.setEnabled(false);
        btnDown.addActionListener(new java.awt.event.ActionListener() {
            public void actionPerformed(java.awt.event.ActionEvent evt) {
                btnDownActionPerformed(evt);
            }
        });

        btnPrint.setText("PT");
        btnPrint.setToolTipText("Imprimir temario actual");
        btnPrint.setEnabled(false);
        btnPrint.addActionListener(new java.awt.event.ActionListener() {
            public void actionPerformed(java.awt.event.ActionEvent evt) {
                btnPrintActionPerformed(evt);
            }
        });

        javax.swing.GroupLayout layout = new javax.swing.GroupLayout(this);
        this.setLayout(layout);
        layout.setHorizontalGroup(
            layout.createParallelGroup(javax.swing.GroupLayout.Alignment.LEADING)
            .addGroup(javax.swing.GroupLayout.Alignment.TRAILING, layout.createSequentialGroup()
                .addContainerGap()
                .addGroup(layout.createParallelGroup(javax.swing.GroupLayout.Alignment.TRAILING)
                    .addComponent(jScrollPane1, javax.swing.GroupLayout.Alignment.LEADING, javax.swing.GroupLayout.DEFAULT_SIZE, 691, Short.MAX_VALUE)
                    .addGroup(javax.swing.GroupLayout.Alignment.LEADING, layout.createSequentialGroup()
                        .addComponent(jLabel1)
                        .addGap(10, 10, 10)
                        .addComponent(txtClvm, javax.swing.GroupLayout.PREFERRED_SIZE, 40, javax.swing.GroupLayout.PREFERRED_SIZE)
                        .addGap(10, 10, 10)
                        .addComponent(jLabel2)
                        .addPreferredGap(javax.swing.LayoutStyle.ComponentPlacement.RELATED)
                        .addComponent(txtNombre, javax.swing.GroupLayout.PREFERRED_SIZE, 300, javax.swing.GroupLayout.PREFERRED_SIZE)
                        .addPreferredGap(javax.swing.LayoutStyle.ComponentPlacement.RELATED)
                        .addComponent(jLabel3)
                        .addPreferredGap(javax.swing.LayoutStyle.ComponentPlacement.RELATED)
                        .addComponent(txtCalMin, javax.swing.GroupLayout.PREFERRED_SIZE, 50, javax.swing.GroupLayout.PREFERRED_SIZE))
                    .addComponent(jLabel6, javax.swing.GroupLayout.Alignment.LEADING, javax.swing.GroupLayout.DEFAULT_SIZE, 691, Short.MAX_VALUE)
                    .addGroup(javax.swing.GroupLayout.Alignment.LEADING, layout.createSequentialGroup()
                        .addGap(10, 10, 10)
                        .addComponent(jLabel5)
                        .addPreferredGap(javax.swing.LayoutStyle.ComponentPlacement.RELATED)
                        .addComponent(jcbVersiones, javax.swing.GroupLayout.PREFERRED_SIZE, 50, javax.swing.GroupLayout.PREFERRED_SIZE)
                        .addPreferredGap(javax.swing.LayoutStyle.ComponentPlacement.RELATED)
                        .addComponent(btnCreaVer)
                        .addPreferredGap(javax.swing.LayoutStyle.ComponentPlacement.UNRELATED)
                        .addComponent(jLabel4)
                        .addPreferredGap(javax.swing.LayoutStyle.ComponentPlacement.UNRELATED)
                        .addComponent(btnAgregaT)
                        .addPreferredGap(javax.swing.LayoutStyle.ComponentPlacement.RELATED)
                        .addComponent(btnQuitaT)
                        .addPreferredGap(javax.swing.LayoutStyle.ComponentPlacement.RELATED)
                        .addComponent(btnUp)
                        .addPreferredGap(javax.swing.LayoutStyle.ComponentPlacement.RELATED)
                        .addComponent(btnDown)
                        .addPreferredGap(javax.swing.LayoutStyle.ComponentPlacement.RELATED)
                        .addComponent(btnPrint)))
                .addContainerGap())
        );
        layout.setVerticalGroup(
            layout.createParallelGroup(javax.swing.GroupLayout.Alignment.LEADING)
            .addGroup(layout.createSequentialGroup()
                .addContainerGap()
                .addGroup(layout.createParallelGroup(javax.swing.GroupLayout.Alignment.BASELINE)
                    .addComponent(jLabel1)
                    .addComponent(txtClvm, javax.swing.GroupLayout.PREFERRED_SIZE, javax.swing.GroupLayout.DEFAULT_SIZE, javax.swing.GroupLayout.PREFERRED_SIZE)
                    .addComponent(jLabel2)
                    .addComponent(txtNombre, javax.swing.GroupLayout.PREFERRED_SIZE, javax.swing.GroupLayout.DEFAULT_SIZE, javax.swing.GroupLayout.PREFERRED_SIZE)
                    .addComponent(jLabel3)
                    .addComponent(txtCalMin, javax.swing.GroupLayout.PREFERRED_SIZE, javax.swing.GroupLayout.DEFAULT_SIZE, javax.swing.GroupLayout.PREFERRED_SIZE))
                .addPreferredGap(javax.swing.LayoutStyle.ComponentPlacement.RELATED)
                .addComponent(jLabel6)
                .addPreferredGap(javax.swing.LayoutStyle.ComponentPlacement.RELATED)
                .addGroup(layout.createParallelGroup(javax.swing.GroupLayout.Alignment.BASELINE)
                    .addComponent(jLabel5)
                    .addComponent(jcbVersiones, javax.swing.GroupLayout.PREFERRED_SIZE, javax.swing.GroupLayout.DEFAULT_SIZE, javax.swing.GroupLayout.PREFERRED_SIZE)
                    .addComponent(btnCreaVer)
                    .addComponent(jLabel4)
                    .addComponent(btnAgregaT)
                    .addComponent(btnQuitaT)
                    .addComponent(btnUp)
                    .addComponent(btnDown)
                    .addComponent(btnPrint))
                .addPreferredGap(javax.swing.LayoutStyle.ComponentPlacement.RELATED)
                .addComponent(jScrollPane1, javax.swing.GroupLayout.DEFAULT_SIZE, 215, Short.MAX_VALUE)
                .addContainerGap())
        );
    }// </editor-fold>//GEN-END:initComponents

    /** Crea una nueva version de temario
     * @param evt El ActionEvent que genero el evento
     */
    private void btnCreaVerActionPerformed(java.awt.event.ActionEvent evt) {//GEN-FIRST:event_btnCreaVerActionPerformed
        TipoRespuesta res;
        int aux;
        if(hayCambiosTem){
            res=datosdoc.pideDesicion(datosdoc.getTitle(),"Hay cambios sin guardar, ¿Desea continuar y perder los cambios?");
            if(res.getTipo()!=TipoRespuesta.ACEPTAR.getTipo()){
                return;
            }
        }
        res=datosdoc.pideDesicion("Crear nueva versión de temario","Limpiar tabla actual");
        if(res.getTipo()==TipoRespuesta.CANCELAR.getTipo()){
            return;
        }
        if(res.getTipo()==TipoRespuesta.ACEPTAR.getTipo()){
            while(((ModeloTemario)jtbTemario.getModel()).getRowCount()>0){
                ((ModeloTemario)jtbTemario.getModel()).removeRow(0);
            }
        }
        aux=jcbVersiones.getItemCount()+1;
        bandera=false;
        jcbVersiones.addItem(""+aux);
        jcbVersiones.setSelectedItem(""+aux);
        verActu=aux;
        bandera=true;
        ((ModeloTemario)jtbTemario.getModel()).setVersion(aux);
        btnAgregaT.setEnabled(true);
        btnQuitaT.setEnabled(true);
        btnUp.setEnabled(true);
        btnDown.setEnabled(true);
        btnPrint.setEnabled(true);
        setCambios(hayCambiosMat,true);
    }//GEN-LAST:event_btnCreaVerActionPerformed

    /** Agrega una fila a la tabla de tetas para crear un nuevo tema
     * @param evt El ActionEvent que genero el evento
     */
    private void btnAgregaTActionPerformed(java.awt.event.ActionEvent evt) {//GEN-FIRST:event_btnAgregaTActionPerformed
        ((ModeloTemario)jtbTemario.getModel()).agregaFila();
        setCambios(hayCambiosMat,true);
    }//GEN-LAST:event_btnAgregaTActionPerformed

    /** Cambia la posicion del tema seleccionado en la tabla de temas subiendola una pocision
     * @param evt El ActionEvent que genero el evento
     */
    private void btnUpActionPerformed(java.awt.event.ActionEvent evt) {//GEN-FIRST:event_btnUpActionPerformed
        int fila=jtbTemario.getSelectedRow();
        if(fila==-1) return;
        ((ModeloTemario)jtbTemario.getModel()).filaUp(fila);
        setCambios(hayCambiosMat,true);
    }//GEN-LAST:event_btnUpActionPerformed

    /** Cambia la posicion del tema seleccionado en la tabla de temas bajandola una pocision
     * @param evt El ActionEvent que genero el evento
     */
    private void btnDownActionPerformed(java.awt.event.ActionEvent evt) {//GEN-FIRST:event_btnDownActionPerformed
        int fila=jtbTemario.getSelectedRow();
        if(fila==-1) return;
        ((ModeloTemario)jtbTemario.getModel()).filaDown(fila);
        setCambios(hayCambiosMat,true);
    }//GEN-LAST:event_btnDownActionPerformed

    /** Quita el tema seleccionado en la tabla de temas
     * @param evt El ActionEvent que genero el evento
     */
    private void btnQuitaTActionPerformed(java.awt.event.ActionEvent evt) {//GEN-FIRST:event_btnQuitaTActionPerformed
        int fila=jtbTemario.getSelectedRow();
        TipoRespuesta aux;
        if(fila==-1) return;
        aux=datosdoc.pideDesicion("Quitar una tema","<html>¿En verdad desea quitar el tema seleccionado?<br>Esta acción no se podra deshacer</html>");
        if(aux.getTipo()!=TipoRespuesta.ACEPTAR.getTipo()) return;
        ((ModeloTemario)jtbTemario.getModel()).quitaFila(fila);
        setCambios(hayCambiosMat,true);
    }//GEN-LAST:event_btnQuitaTActionPerformed

    /** Cambia la version de la tabla de temas (si existe mas de una)
     * @param evt El ActionEvent que genero el evento
     */
    private void jcbVersionesItemStateChanged(java.awt.event.ItemEvent evt) {//GEN-FIRST:event_jcbVersionesItemStateChanged
        TipoRespuesta opc;
        if(!bandera) return;
        if(hayCambiosTem){
            opc=datosdoc.pideDesicion(datosdoc.getTitle(),"Hay cambios sin guardar, ¿Desea continuar y perder los cambios?");
            if(opc.getTipo()!=TipoRespuesta.ACEPTAR.getTipo()){
                bandera=false;
                jcbVersiones.setSelectedItem(""+verActu);
                bandera=true;
                return;
            }
        }
        cargaMateria(txtClvm.getText(),Integer.parseInt(""+jcbVersiones.getSelectedItem()));
    }//GEN-LAST:event_jcbVersionesItemStateChanged

    /** Genera un reporte de datos generales de la materia y el temario y abre un dialog para enviarlo a la impresora
     * @param evt El ActionEvent que genero el evento
     */
    private void btnPrintActionPerformed(java.awt.event.ActionEvent evt) {//GEN-FIRST:event_btnPrintActionPerformed
        Map<String,String> parametros = new HashMap<String,String>();
        ArrayList campos=new ArrayList();
        ImpTemas campo;
        DefaultTableModel modelo=(DefaultTableModel)jtbTemario.getModel();
        if(modelo.getRowCount()<1){
            datosdoc.muestraMensaje("No se puede imprimir","No hay temas registrados",TipoMensaje.ERROR);
            return;
        }
        String[] datosDoc=Consultas.consultaUnCampo("select * from datosdoc,datosinst;",false);
        if(datosDoc==null){
            datosdoc.muestraMensaje("Error al consultar datos de cabecera de reporte",Consultas.obtenError(),TipoMensaje.ERROR);
            return;
        }
        parametros.put("NOMINST",datosDoc[2]);
        parametros.put("UNIACAESC",datosDoc[3]);
        parametros.put("AREAPROG",datosDoc[4]);
        parametros.put("NOMDOC",datosDoc[0]);
        parametros.put("NOMMATE",nom);
        parametros.put("TEMVER",""+jcbVersiones.getSelectedItem());
        for(int s=0;s<modelo.getRowCount();s++){
            campo=new ImpTemas(""+modelo.getValueAt(s,3),""+modelo.getValueAt(s,4),(modelo.getValueAt(s,5)!=null?""+modelo.getValueAt(s,5):null));
            campos.add(campo);
        }
        datosdoc.enviarImpresion("Temario materia: "+nom,11,parametros,campos);
}//GEN-LAST:event_btnPrintActionPerformed
    
    /** Valida el contenido de las filas de la tabla de temas
     * @return true si todas las filas son validas false en caso contrario
     */
    private boolean sonDatosValidos(){
        String aux;
        for(int j=0;j<jtbTemario.getRowCount();j++){
            aux=""+jtbTemario.getValueAt(j,3);
            if(aux.length()<1||aux.length()>8){
                error="Numero invalido en fila: "+(j+1);
                return false;
            }
            aux=""+jtbTemario.getValueAt(j,4);
            if(aux.length()<1||aux.length()>65){
                error="Titulo invalido en fila: "+(j+1);
                return false;
            }
        }
        return true;
    }
    
    /**Establece el ancho de las columnas y cuales son invisibles de la tabla de temas*/
    private void organizaColumnas(){
        jtbTemario.getColumn("Numero").setPreferredWidth(80);
        jtbTemario.getColumn("Titulo").setPreferredWidth(300);
        jtbTemario.getColumn("Contenido").setPreferredWidth(400);
        ocultaColumna("ClvTem");
        ocultaColumna("Version");
        ocultaColumna("Orden");
    }
   
    /** Oculta una columna de la tabla de temas
     * @param col El nombre de la columna a ocultar
     */
    private void ocultaColumna(String col){
        jtbTemario.getColumn(col).setPreferredWidth(0);
        jtbTemario.getColumn(col).setMinWidth(0);
        jtbTemario.getColumn(col).setMaxWidth(0);
        jtbTemario.getColumn(col).setResizable(false);
    }

    /** Obtiene un booleano que indica si hay cambios sin guardar en los datos
     * @return un booleano que indica si hay cambios sin guardar en los datos
     */
    public boolean hayCambios(){
        return (hayCambiosMat||hayCambiosTem);
    }
   
    /** Establece si hay cambios sin guardar en los datos
     * @param materia indica si hay cambios en los datos de la materia
     * @param temario indica si hay cambios en los datos del temario
     */
    public void setCambios(boolean materia, boolean temario){
        hayCambiosMat=materia;
        hayCambiosTem=temario;
        datosdoc.actualizaCambiosMaterias();
    }
    
    /** Obtiene el nombre de la materia actual
     * @return el nombre de la materia actual
     */
    public String getNombre(){ return nom; }
    
    /** Obtiene la clave de la materia actual
     * @return la clave de la materia actual
     */
    public String getClave(){ return clave; }
    
    /** Obtiene la descripcion del ultimo error ocurrido
     * @return la descripcion del ultimo error ocurrido
     */
    public String getError(){ return error; }
  
    /** Crea un listener para detectar is ocurren cambios en los datos de la materia */
    private void escuchaCambios(){
        ListenChanges listenCh= new ListenChanges();
        txtNombre.getDocument().addDocumentListener(listenCh);
        txtCalMin.getDocument().addDocumentListener(listenCh);
    }
    
    // Variables declaration - do not modify//GEN-BEGIN:variables
    private javax.swing.JButton btnAgregaT;
    private javax.swing.JButton btnCreaVer;
    private javax.swing.JButton btnDown;
    private javax.swing.JButton btnPrint;
    private javax.swing.JButton btnQuitaT;
    private javax.swing.JButton btnUp;
    private javax.swing.JLabel jLabel1;
    private javax.swing.JLabel jLabel2;
    private javax.swing.JLabel jLabel3;
    private javax.swing.JLabel jLabel4;
    private javax.swing.JLabel jLabel5;
    private javax.swing.JLabel jLabel6;
    private javax.swing.JScrollPane jScrollPane1;
    private javax.swing.JComboBox jcbVersiones;
    private javax.swing.JTable jtbTemario;
    private javax.swing.JTextField txtCalMin;
    private javax.swing.JTextField txtClvm;
    private javax.swing.JTextField txtNombre;
    // End of variables declaration//GEN-END:variables

    /** Clase con la definicion del modelo de datos de la tabla de temas*/
    private class ModeloTemario extends DefaultTableModel{
        
        /**version del temario usada*/
        private int version;
        /**Lista de temas quitados pero aun sin eliminar de la base de datos*/
        private java.util.ArrayList<Integer> quitados;
        
        /** Crea un nuevo ModeloTemario */
        public ModeloTemario(){
            super(new Object[][] {},new String[]{"ClvTem", "Version", "Orden", "Numero", "Titulo", "Contenido"});
            quitados=new java.util.ArrayList<Integer>();
        }
        
        /** Crea un nuevo ModeloTemario
         * @param m Modelo previo con datos de temas cargados desde la base de datos (tabla temario)
         * @param cols Nombres de las columna del modelo
         * @param ver version del temario
         */
        public ModeloTemario(DefaultTableModel m,java.util.Vector cols, int ver){
            super(m.getDataVector(),cols);
            version=ver;
            quitados=new java.util.ArrayList<Integer>();
            for(int j=0;j<getRowCount();j++){
                if((""+getValueAt(j,5)).equals("null")){
                    setValueAt("",j,5);
                }
            }
        }
        
        /** Obtiene un booleano que indica si una celda de la tabla es editable
         * @param row el numero de fila de la celda
         * @param col el numero de columna de la celda
         * @return un booleano que indica si una celda de la tabla es editable
         */
        @Override
        public boolean isCellEditable(int row, int col){
            return (col>2);
        }
        
        /** Agrega una fila vacia al modelo (y por consiguiente a la tabla) */
        public void agregaFila(){
            addRow(new Object[]{"-1",""+version,"","","",""});
        }
       
        /** Quita una fila del modelo (solo la quita pero no borra el registro 
         *   a menos que posteriormente guarden los cambios)         * 
         * @param fila El indice de la fila a quitar
         */
        public void quitaFila(int fila){
            quitados.add(Integer.parseInt(""+getValueAt(fila,0)));
            removeRow(fila);
        }
     
        /** Establece la version del temario actual
         * @param version la version del temario actual
         */
        public void setVersion(int version){
            this.version=version;
            for(int f=0;f<getRowCount();f++){
                setValueAt("-1",f,0);
                setValueAt(""+version,f,1);
            }
            quitados.clear();
        }
       
        /** Obtiene la lista de registros de temas quitados de la tabla
         * @return la lista de registros de temas quitados de la tabla
         */
        public java.util.ArrayList<Integer> getQuitados(){
            return quitados;
        }
        
        /** Mueve una fila una posicion hacia arriba
         * @param fila el indice de la fila a mover
         */
        public void filaUp(int fila){
            Object[] vals;
            if(fila==0) return;
            vals=new Object[getColumnCount()];
            for(int j=0;j<getColumnCount();j++){
                vals[j]=getValueAt(fila,j);
            }
            removeRow(fila);
            insertRow(fila-1, vals);
            jtbTemario.setRowSelectionInterval(fila-1, fila-1);
        }
        
        /** Mueve una fila una posicion hacia abajo
         * @param fila el indice de la fila a mover
         */
        public void filaDown(int fila){
            Object[] vals;
            if(fila==getRowCount()-1) return;
            vals=new Object[getColumnCount()];
            for(int j=0;j<getColumnCount();j++){
                vals[j]=getValueAt(fila,j);
            }
            removeRow(fila);
            insertRow(fila+1, vals);
            jtbTemario.setRowSelectionInterval(fila+1, fila+1);
        }
        
        /** Obtiene el nuemro de version actual
         * @return el nuemro de version actual
         */
        public int getVersion(){ return version; }
    }

    /** Clase que implementa la interfaz DocumentListener 
     * para detectar si hay edicion en los controles de texto y saber si hay 
     * cambios sin guardar en el registro
     */
    private class ListenChanges implements javax.swing.event.DocumentListener{
       /** Crea un nuevo objeto ListenChanges */
        public ListenChanges(){}
        /** Metodo de la interfaz DocumentListener
         * detecta si se inserto contenido al documento (contenido del control de texto)
         * y avisa al ControlMateria que hay cambios sin guardar
         * @param e El DocumentEvent que genero el evento
         */
        public void insertUpdate(javax.swing.event.DocumentEvent e){ 
            setCambios(true,hayCambiosTem);
        }
        /** Metodo de la interfaz DocumentListener
         * detecta si se quito contenido al documento (contenido del control de texto)
         * y avisa al ControlMateria que hay cambios sin guardar
         * @param e El DocumentEvent que genero el evento
         */
        public void removeUpdate(javax.swing.event.DocumentEvent e){ 
            setCambios(true,hayCambiosTem);
        }
        /** Metodo de la interfaz DocumentListener
         * detecta si cambio el contenido del documento (contenido del control de texto)
         * @param e El DocumentEvent que genero el evento
         */
        public void changedUpdate(javax.swing.event.DocumentEvent e){}
    }
    
    /**  Establece que hay cambios sin guardar al editar alguna celda de la tabla de temas
     * @param e El TableModelEvent que genero el evento
     */
    public void tableChanged(TableModelEvent e) {
        setCambios(hayCambiosMat,true);
    }
}
