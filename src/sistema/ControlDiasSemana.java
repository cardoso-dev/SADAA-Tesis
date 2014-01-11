/*
 * ControlDiasSemana.java
 *  Crea un control con una tabla para manejar un horario semanal
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

import alumnos.FrameAsesoria;
import database.Actualiza;
import database.Consultas;
import definiciones.TipoMensaje;
import grupos.FrameCalendario;
import java.awt.event.ActionEvent;
import java.awt.event.WindowEvent;
import javax.swing.table.DefaultTableModel;
import operaciones.Datos;
import definiciones.TipoRespuesta;
import java.util.ArrayList;
import java.util.HashMap;
import java.util.Map;
import operaciones.Tiempo;
import reportes.ImpHorarioSem;

/** Crea un control con una tabla para manejar un horario semanal
 *   se utiliza en las ventana de calendario de grupo (FrameCalendario)
 *   de asesoria de alumnos (FrameAsesoria) 
 *   y de horario actual (FrameHorarioCiclo
 * 
 * @author  Pedro Cardoso Rodriguez
 */
public class ControlDiasSemana extends javax.swing.JPanel {
    
    /**Nombre generico de una celda de la tabla de horario*/
    private String nomCelda;
    /**Referencia al calendario de grupo al que pertenece o null si no pertenece a un calendario de grupo*/
    private FrameCalendario grupo;
    /**Referencia a la ventana de asesoria a la que pertenece o null si no pertenece a una ventana de asesoria*/
    private FrameAsesoria asesoria;
    /**Referencia a la ventana horario de ciclo a la que pertenece o null si no pertenece a una ventana horario de ciclo*/
    private FrameHorarioCiclo ciclo;
    /**popupmenu para las celda de la tabla de horario*/
    private javax.swing.JPopupMenu pmnuSes;
    /**Meu item para el popupmenu de la tabla de horario*/
    javax.swing.JMenuItem mnuq;
    /**Descripcion del ultimo error ocurrido*/
    private String error;
    
    /** Crea un nuevo ControlDiasSemana
     * @param titulo El titulo del control (se desplegara visualmente)
     * @param nomCelda El nombre generico de las celdas de la tablas de horario
     * @param refSuperior La referencia a la jinternalframe superior que contiene este control
     *   debe ser de uno de los siguientes tipos: FrameCalendario, FrameAsesoria o FrameHorarioCiclo
     * @throws sistema.ControlDiasSemana.RefSuperiorInvalida
     *   Lanza la excepcion si refSuprerior es de un tipo invalido
     */
    public ControlDiasSemana(String titulo,String nomCelda,Object refSuperior) throws ControlDiasSemana.RefSuperiorInvalida{
        initComponents();
        this.nomCelda=nomCelda;
        lblTitulo.setText(titulo);
        btnAgregaCelda.setText("Agregar "+nomCelda);
        btnImp.setEnabled(false);
        btnImp.setText("");
        btnImp.setIcon(iconos.Iconos.getIcono("impresora.png"));
        pnlBtns.setBorder(null);
        grupo=null; asesoria=null; ciclo=null;
        if(refSuperior instanceof FrameCalendario){
            grupo=(FrameCalendario)refSuperior;
        }
        else if(refSuperior instanceof FrameAsesoria){
            asesoria=(FrameAsesoria)refSuperior;
        }
        else if(refSuperior instanceof FrameHorarioCiclo){
            ciclo=(FrameHorarioCiclo)refSuperior;
            btnCreaDias.setEnabled(false);
            btnCreaDias.setVisible(false);
        }
        else{
            throw new RefSuperiorInvalida();
        }
        crearPopMenu();
        error=null;
    }
    
    /**Crea el control pmnuSes que se utiliza como popupmenu para las celdas de la tabla de horarios*/
    private void crearPopMenu(){ 
        pmnuSes=new javax.swing.JPopupMenu();
        mnuq=new javax.swing.JMenuItem("Quitar");
        mnuq.setActionCommand("Quitar");
        mnuq.addActionListener(new java.awt.event.ActionListener(){
            public void actionPerformed(ActionEvent e){
                quitaCelda(e.getActionCommand());
            }
        });
        pmnuSes.add(mnuq);
    }
    
    /** Establece el titulo del control
     * @param titulo el nuevo actual del control
     */
    public void setTitulo(String titulo){ lblTitulo.setText(titulo); }
    
    /** Carga los datos de horarios desde la base de datos segun el tipo de horarios a desplegar
     *   sesiones de un grupo, de asesorias o de un ciclo
     * @return true si los datos se cargaron correctamente false en caso contrario
     */
    public boolean cargaDatos(){
        String[][] datos;
        String sen=null;
        error=null;
        if(grupo!=null){
            sen="select activpordia.* from activpordia,diasgrupo where activpordia.clvad";
            sen+="=diasgrupo.clvad and clvg='"+grupo.getClavegrupo()+"';";
        }
        else if(asesoria!=null){
            sen="select activpordia.* from activpordia,diastesis where activpordia.clvad";
            sen+="=diastesis.clvad and clvt="+asesoria.getClaveTesis()+";";
        }
        else if(ciclo!=null){
            return cargaVigentesEnDia(Tiempo.getFechaAMD());
        }
        datos=Consultas.consultaDatos(sen,true);
        if(datos==null){
            error=Consultas.obtenError();
            return false;
        }
        else if(datos[0][0]==null){
            return true;
        }
        cargaDatos(datos);
        return true;
    }
    
    /** Carga datos de horarios en la tabla de horarios
     * @param datos Los datos a cargar en la tabla de horarios
     */
    private void cargaDatos(String[][] datos){
        DefaultTableModel modelo=(DefaultTableModel)jtbDias.getModel();
        Celda celda;
        int aux;
        while(modelo.getRowCount()>0){ modelo.removeRow(0); }
        for(int i=0;i<datos.length;i++){
            celda=new Celda(datos[i][4],Integer.parseInt(datos[i][0]));
            aux=existe(datos[i][1],datos[i][2]);
            if(aux>-1 && modelo.getValueAt(aux,Integer.parseInt(datos[i][3])+1)==null){
                modelo.setValueAt(celda,aux,Integer.parseInt(datos[i][3])+1);
            }
            else{
                Object[] row=new Object[7];
                row[0]="<html>"+datos[i][1]+"<br>"+datos[i][2]+"</html>";
                row[1]=row[2]=row[3]=row[4]=row[5]=row[6]=null;
                row[Integer.parseInt(datos[i][3])+1]=celda;
                modelo.addRow(row);
            }
        }
        ordena();
        btnAgregaCelda.setEnabled(true);
        btnCreaDias.setEnabled(true);
        btnImp.setEnabled(true);
    }
    
    /** Carga horarios de asesorias a tesistas cuyo periodo programado de sesiones abarque una fecha en particular
     * @param dia La fecha que debe abarcar el periodo programado de las sesiones a considerar
     * @return true si se cargaron los datos correctamente false en caso contrario
     */
    private boolean cargaVigentesEnDia(String dia){
        String sen="select clvt from Tesis;";
        int[] tesis=Consultas.consultaEnteros(sen,true);
        String[] dts;
        String[][] datos;
        boolean hayTesis=false;
        if(tesis==null){
            error=Consultas.obtenError();
            return false;
        }
        else if(tesis[0]!=-1){
            for(int h=0;h<tesis.length;h++){
                sen="select min(fechayhora),max(fechayhora) from sestesis,progsest";
                sen+=" where progsest.clvsest=sestesis.clvsest and progsest.clvt="+tesis[h]+";";
                dts=Consultas.consultaUnCampo(sen,false);
                if(dts==null){
                    error=Consultas.obtenError();
                    return false;
                }
                else if(dts[0]!=null && dts[1]!=null){
                    if(Datos.compareFecha(dts[0].substring(0,10),Datos.transformatFcha(dia))>0
                            || Datos.compareFecha(Datos.transformatFcha(dia),dts[1].substring(0,10))>0){
                        tesis[h]=-1;
                    }
                    else{ hayTesis=true; }
                }
                else{ tesis[h]=-1; }
            }
        }
        sen="select distinct(activpordia.clvad),horaini,horafin,dia,activ from activpordia,diasgrupo";
        sen+=(hayTesis?",diastesis":"")+" where (activpordia.clvad=diasgrupo.clvad and ";
        sen+="clvg in (select ClvG from Grupos where '"+dia+"'>=PerIni && '"+dia+"'<=PerFin))";
        if(hayTesis){
            sen+=" or (activpordia.clvad=diastesis.clvad and (";
            for(int g=0;g<tesis.length;g++){
                if(tesis[g]!=-1){
                    sen+=(g>0?"":" or ")+"clvt="+tesis[g];
                }
            }
            sen+="))";
        }
        sen+="or(activpordia.clvad not in(select clvAD from diasgrupo) and activpordia.clvad not in(select clvAD from diastesis)) order by activpordia.clvad;";
        datos=Consultas.consultaDatos(sen,true);
        if(datos==null){
            error=Consultas.obtenError();
            return false;
        }
        else if(datos[0][0]==null){
            return true;
        }
        cargaDatos(datos);
        return true;
    }
    
    /** This method is called from within the constructor to
     * initialize the form.
     * WARNING: Do NOT modify this code. The content of this method is
     * always regenerated by the Form Editor.
     */
    // <editor-fold defaultstate="collapsed" desc="Generated Code">//GEN-BEGIN:initComponents
    private void initComponents() {

        lblTitulo = new javax.swing.JLabel();
        pnlBtns = new javax.swing.JPanel();
        btnAgregaCelda = new javax.swing.JButton();
        btnImp = new javax.swing.JButton();
        btnCreaDias = new javax.swing.JButton();
        jScrollPane1 = new javax.swing.JScrollPane();
        jtbDias = new javax.swing.JTable();

        lblTitulo.setFont(new java.awt.Font("Tahoma", 1, 12));
        lblTitulo.setHorizontalAlignment(javax.swing.SwingConstants.CENTER);
        lblTitulo.setText("Titulo");

        pnlBtns.setBorder(javax.swing.BorderFactory.createEtchedBorder());
        pnlBtns.setLayout(new java.awt.BorderLayout());

        btnAgregaCelda.setText("Agregar Celda");
        btnAgregaCelda.addActionListener(new java.awt.event.ActionListener() {
            public void actionPerformed(java.awt.event.ActionEvent evt) {
                btnAgregaCeldaActionPerformed(evt);
            }
        });
        pnlBtns.add(btnAgregaCelda, java.awt.BorderLayout.WEST);

        btnImp.setText("imp");
        btnImp.setToolTipText("Imprimir horario");
        btnImp.addActionListener(new java.awt.event.ActionListener() {
            public void actionPerformed(java.awt.event.ActionEvent evt) {
                btnImpActionPerformed(evt);
            }
        });
        pnlBtns.add(btnImp, java.awt.BorderLayout.EAST);

        btnCreaDias.setText("Crear sesiones");
        btnCreaDias.setToolTipText("<html>Crea sesiones automaticamente en<br>las fechas de los dias correspondientes<br>dentro del periodo de vigencia</html>");
        btnCreaDias.addActionListener(new java.awt.event.ActionListener() {
            public void actionPerformed(java.awt.event.ActionEvent evt) {
                btnCreaDiasActionPerformed(evt);
            }
        });
        pnlBtns.add(btnCreaDias, java.awt.BorderLayout.CENTER);

        jtbDias.setModel(new javax.swing.table.DefaultTableModel(
            new Object [][] {

            },
            new String [] {
                "Hora", "Lunes", "Martes", "Miercoles", "Jueves", "Viernes", "Sabado"
            }
        ) {
            boolean[] canEdit = new boolean [] {
                false, false, false, false, false, false, false
            };

            public boolean isCellEditable(int rowIndex, int columnIndex) {
                return canEdit [columnIndex];
            }
        });
        jtbDias.setAutoResizeMode(javax.swing.JTable.AUTO_RESIZE_OFF);
        jtbDias.setRowHeight(48);
        jtbDias.getTableHeader().setReorderingAllowed(false);
        jtbDias.addMouseListener(new java.awt.event.MouseAdapter() {
            public void mouseClicked(java.awt.event.MouseEvent evt) {
                jtbDiasMouseClicked(evt);
            }
            public void mousePressed(java.awt.event.MouseEvent evt) {
                jtbDiasMousePressed(evt);
            }
            public void mouseReleased(java.awt.event.MouseEvent evt) {
                jtbDiasMouseReleased(evt);
            }
        });
        jScrollPane1.setViewportView(jtbDias);
        jtbDias.getColumnModel().getColumn(0).setPreferredWidth(40);
        jtbDias.getColumnModel().getColumn(1).setPreferredWidth(150);
        jtbDias.getColumnModel().getColumn(2).setPreferredWidth(150);
        jtbDias.getColumnModel().getColumn(3).setPreferredWidth(150);
        jtbDias.getColumnModel().getColumn(4).setPreferredWidth(150);
        jtbDias.getColumnModel().getColumn(5).setPreferredWidth(150);
        jtbDias.getColumnModel().getColumn(6).setPreferredWidth(150);

        javax.swing.GroupLayout layout = new javax.swing.GroupLayout(this);
        this.setLayout(layout);
        layout.setHorizontalGroup(
            layout.createParallelGroup(javax.swing.GroupLayout.Alignment.LEADING)
            .addGroup(layout.createSequentialGroup()
                .addContainerGap()
                .addGroup(layout.createParallelGroup(javax.swing.GroupLayout.Alignment.LEADING)
                    .addComponent(jScrollPane1, javax.swing.GroupLayout.Alignment.TRAILING, javax.swing.GroupLayout.DEFAULT_SIZE, 953, Short.MAX_VALUE)
                    .addGroup(layout.createSequentialGroup()
                        .addComponent(lblTitulo, javax.swing.GroupLayout.DEFAULT_SIZE, 692, Short.MAX_VALUE)
                        .addPreferredGap(javax.swing.LayoutStyle.ComponentPlacement.RELATED)
                        .addComponent(pnlBtns, javax.swing.GroupLayout.PREFERRED_SIZE, javax.swing.GroupLayout.DEFAULT_SIZE, javax.swing.GroupLayout.PREFERRED_SIZE)))
                .addContainerGap())
        );
        layout.setVerticalGroup(
            layout.createParallelGroup(javax.swing.GroupLayout.Alignment.LEADING)
            .addGroup(layout.createSequentialGroup()
                .addContainerGap()
                .addGroup(layout.createParallelGroup(javax.swing.GroupLayout.Alignment.LEADING)
                    .addComponent(pnlBtns, javax.swing.GroupLayout.PREFERRED_SIZE, javax.swing.GroupLayout.DEFAULT_SIZE, javax.swing.GroupLayout.PREFERRED_SIZE)
                    .addComponent(lblTitulo))
                .addPreferredGap(javax.swing.LayoutStyle.ComponentPlacement.RELATED)
                .addComponent(jScrollPane1, javax.swing.GroupLayout.DEFAULT_SIZE, 325, Short.MAX_VALUE)
                .addContainerGap())
        );
    }// </editor-fold>//GEN-END:initComponents

    /** Llama un jdialog con el cual solicita datos para crear una celda en la tabla de horarios
     *  la celda debe ser consistente con los datos que se muestren en el momento en el control
     *  asesorias a alumnos, sesiones de un grupo o general por ciclo
     * @param evt El ActionEvent que genero el evento
     */
    private void btnAgregaCeldaActionPerformed(java.awt.event.ActionEvent evt) {//GEN-FIRST:event_btnAgregaCeldaActionPerformed
        DialogoCreaCelda dccNueva;
        DefaultTableModel modelo;
        Object[] row;
        String[] datos=null;
        String activ;
        boolean band;
        int aux;
        Celda celda=null;
        do{
            dccNueva=new DialogoCreaCelda((javax.swing.JFrame)getTopLevelAncestor(),true,"Crear "+nomCelda,datos);
            if(dccNueva.cancelo) return;
            datos=dccNueva.getDatos();
            band=sonValidos(datos);
            if(!band){
                mensaje("No se puede crear",error,TipoMensaje.ERROR);
            }
        }while(!band);
        if(existe(datos[1],Integer.parseInt(datos[0]))){
            mensaje("No se puede crear","Ya existe actividad en el dia y hora solicitado",TipoMensaje.INFORMACION);
            return;
        }
        try{
            if(grupo!=null){
                celda=new Celda("Sesion grupo:\n"+grupo.getClavegrupo(),grupo.getClavegrupo(),0,datos[1],datos[2],Integer.parseInt(datos[0]));
            }
            else if(asesoria!=null){
                celda=new Celda("Asesoria tesis\nAlumno: "+asesoria.getMatriculaAlumno(),null,asesoria.getClaveTesis(),datos[1],datos[2],Integer.parseInt(datos[0]));
            }
            else if(ciclo!=null){
                activ=ciclo.pideString("Actividad","Indique actividad a realizar");
                if(activ==null || activ.length()==0){
                    return;
                }
                else if(activ.length()>255){
                    mensaje("No se puede crear","Actividad invalida (numero de caracteres debe ser 255 o menor)",TipoMensaje.ERROR);
                    return;
                }
                celda=new Celda(activ,null,0,datos[1],datos[2],Integer.parseInt(datos[0]));
            }
        }
        catch(Exception exc){
            mensaje("No se puede crear","Ocurrio el error: "+exc.getMessage(),TipoMensaje.INFORMACION);
            return;
        }
        modelo=(DefaultTableModel)jtbDias.getModel();
        aux=existe(datos[1],datos[2]);
        if(aux>-1){
            modelo.setValueAt(celda,aux,Integer.parseInt(datos[0])+1);
        }
        else{
            row=new Object[7];
            row[0]="<html>"+datos[1]+"<br>"+datos[2]+"</html>";
            row[1]=row[2]=row[3]=row[4]=row[5]=row[6]=null;
            row[Integer.parseInt(datos[0])+1]=celda;
            modelo.addRow(row);
        }
        ordena();
        btnImp.setEnabled(true);
    }//GEN-LAST:event_btnAgregaCeldaActionPerformed

    /** Llama el metodo que muestra el popupmenu de las celdas de la tabla de horarios
     * @param evt El MouseEvent que genero el evento
     */
    private void jtbDiasMouseClicked(java.awt.event.MouseEvent evt) {//GEN-FIRST:event_jtbDiasMouseClicked
        llamaMenu(evt);
    }//GEN-LAST:event_jtbDiasMouseClicked

    /** Llama el metodo que muestra el popupmenu de las celdas de la tabla de horarios
     * @param evt El MouseEvent que genero el evento
     */
    private void jtbDiasMousePressed(java.awt.event.MouseEvent evt) {//GEN-FIRST:event_jtbDiasMousePressed
        llamaMenu(evt);
    }//GEN-LAST:event_jtbDiasMousePressed

    /** Llama el metodo que muestra el popupmenu de las celdas de la tabla de horarios
     * @param evt El MouseEvent que genero el evento
     */
    private void jtbDiasMouseReleased(java.awt.event.MouseEvent evt) {//GEN-FIRST:event_jtbDiasMouseReleased
        llamaMenu(evt);
    }//GEN-LAST:event_jtbDiasMouseReleased

    /** Genera una lista de fechas, las cuales estan dentro de un rango delimitado de tiempo, y
     *   donde cada una es un dia de la semana para el cual existe una celda en la tabla de 
     *   horario actual, la lista se envia a la ventana que contiene el control para que 
     *   genere automaticamente sesiones programadas con esas fechas (sesiones de grupo 
     *   o asesorias a tesistas)
     * @param evt El ActionEvent que genero el evento
     */
    private void btnCreaDiasActionPerformed(java.awt.event.ActionEvent evt) {//GEN-FIRST:event_btnCreaDiasActionPerformed
        ArrayList<String> sesiones;
          //formato=fecha horaini+duracion Donde fecha=aaaa-mm-dd, horaini=hh:mm, duracion=N minutos (N>0)
        java.util.ArrayList<String> aux;
        String[] rango=null;
        if(jtbDias.getRowCount()==0){
            mensaje("No se puede generar","No existe ningun horario en la tabla",TipoMensaje.INFORMACION);
            return;
        }
        sesiones=new ArrayList<String>();
        if(grupo!=null){
            rango=grupo.getFechasVigencia();
        }
        else if(asesoria!=null){
            rango=asesoria.getFechasVigencia();
            if(rango==null) return;
        }
        for(int col=1;col<jtbDias.getColumnCount();col++){
            for(int fila=0;fila<jtbDias.getRowCount();fila++){
                if(jtbDias.getValueAt(fila, col)==null) continue;
                aux=Tiempo.getFechasEnDiaSema(rango[0],rango[1],col-1);
                if(aux.size()>0){
                    for(String str:aux){
                        sesiones.add(str+" "+(""+jtbDias.getValueAt(fila,0)).substring(6,11)+"+"+getDuracion(fila));
                    }
                }
            }
        }
        if(grupo!=null){
            grupo.generaSesiones(sesiones);
        }
        else if(asesoria!=null){
            asesoria.generaSesiones(sesiones);
        }
    }//GEN-LAST:event_btnCreaDiasActionPerformed

    /** Genera un reporte con la tabla de horarios actual y abre un 
     *   dialogo para enviarlo a la impresora
     * @param evt El ActionEvent que genero el evento
     */
    private void btnImpActionPerformed(java.awt.event.ActionEvent evt) {//GEN-FIRST:event_btnImpActionPerformed
        Map<String,String> parametros = new HashMap<String,String>();
        ArrayList campos=new ArrayList();
        ImpHorarioSem campo;
        DefaultTableModel modelo=(DefaultTableModel)jtbDias.getModel();
        if(modelo.getRowCount()<1){
            mensaje("No se puede imprimir","No hay horarios programados",TipoMensaje.ERROR);
            return;
        }
        String[] datosDoc=Consultas.consultaUnCampo("select * from datosdoc,datosinst;",false);
        if(datosDoc==null){
            mensaje("Error al consultar datos de cabecera de reporte",Consultas.obtenError(),TipoMensaje.ERROR);
            return;
        }
        parametros.put("NOMINST",datosDoc[2]);
        parametros.put("UNIACAESC",datosDoc[3]);
        parametros.put("AREAPROG",datosDoc[4]);
        parametros.put("NOMDOC",datosDoc[0]);
        parametros.put("NOMHORARIO",lblTitulo.getText());
        for(int s=0;s<modelo.getRowCount();s++){
            String hora=(""+jtbDias.getValueAt(s,0));
            String lun=(""+(jtbDias.getValueAt(s,1)!=null?jtbDias.getValueAt(s,1):""));
            String mar=(""+(jtbDias.getValueAt(s,2)!=null?jtbDias.getValueAt(s,2):""));
            String mie=(""+(jtbDias.getValueAt(s,3)!=null?jtbDias.getValueAt(s,3):""));
            String jue=(""+(jtbDias.getValueAt(s,4)!=null?jtbDias.getValueAt(s,4):""));
            String vie=(""+(jtbDias.getValueAt(s,5)!=null?jtbDias.getValueAt(s,5):""));
            String sab=(""+(jtbDias.getValueAt(s,6)!=null?jtbDias.getValueAt(s,6):""));
            campo=new ImpHorarioSem(hora,lun,mar,mie,jue,vie,sab);
            campos.add(campo);
        }
        if(grupo!=null){
            grupo.enviarImpresion(lblTitulo.getText(),10,parametros,campos);
        }
        else if(asesoria!=null){
            asesoria.enviarImpresion(lblTitulo.getText(),10,parametros,campos);
        }
        else if(ciclo!=null){
            ciclo.enviarImpresion(lblTitulo.getText(),10,parametros,campos);
        }
    }//GEN-LAST:event_btnImpActionPerformed
  
    /** Recibe y valida datos de horario (hora inicial y final de una fila de la tabla de horarios)
     * @param dts Los datos a validar hora inicial y final en un vector de tamanio 2
     * @return true si los datos son validos flase en caso contrario
     */
    private boolean sonValidos(String[] dts){
        java.util.StringTokenizer tk1;
        java.util.StringTokenizer tk2;
        int h1, h2;
        int m1, m2;
        error=null;
        if(!Datos.valHorarioSimple(dts[1])){
            error="Hora inicial invalida";
        }
        else if(!Datos.valHorarioSimple(dts[2])){
            error="Hora final invalida";
        }
        else{
            tk1=new java.util.StringTokenizer(dts[1]);
            tk2=new java.util.StringTokenizer(dts[2]);
            h1=Integer.parseInt(tk1.nextToken(":")); h2=Integer.parseInt(tk2.nextToken(":"));
            m1=Integer.parseInt(tk1.nextToken()); m2=Integer.parseInt(tk2.nextToken());
            if(h1>h2 || (h1==h2 && m1>=m2)){
                error="Hora final debe ser mayor a la hora inicial";
            }
        }
        return (error==null);
    }
  
    /** Muestra un mensaje al usuario por medio de un dialogo
     * @param tit El titulo del mensaje
     * @param mens El texto del mensaje
     * @param tm El tipo de mensaje
     */
    private void mensaje(String tit,String mens,TipoMensaje tm){
        if(grupo!=null){
            grupo.muestraMensaje(tit,mens,tm);
        }
        else if(asesoria!=null){
            asesoria.muestraMensaje(tit,mens,tm);
        }
        else if(ciclo!=null){
            ciclo.muestraMensaje(tit,mens,tm);
        }
    }
   
    /** Obtiene un tipo de respuesta del usuario al mostrarle un mensaje o pregunta
     * @param tit El titulo del mensaje o pregunta
     * @param mens El texto del mensaje o pregunta
     * @return El tipo de respuesta elegido por el usuario
     */
    private TipoRespuesta pideDesicion(String tit, String mens){
        TipoRespuesta tmp=null;
        if(grupo!=null) tmp=grupo.pideDesicion(tit,mens);
        else if(asesoria!=null) tmp=asesoria.pideDesicion(tit,mens);
        else if(ciclo!=null) tmp=ciclo.pideDesicion(tit,mens);
        return tmp;
    }
  
    /** Obtiene un booleano que indica si existe una actividad para un dia en un determinado horario
     * @param hora El horario a considerar (formato HH:MM)
     * @param dia El dia de la semana a considerar (0 a 5 para luneas a sabado)
     * @return true si existe una actividad para el dia y el horario, false en caso contrario
     */
    private boolean existe(String hora, int dia){
        String hrTemp;
        for(int g=0;g<jtbDias.getRowCount();g++){
            hrTemp=""+jtbDias.getValueAt(g,0);
            hrTemp=hrTemp.substring(6,11);
            if(hora.equals(hrTemp) && jtbDias.getValueAt(g,dia+1)!=null){
                return true;
            }
        }
        return false;
    }
   
    /** Obtiene un booleano que indica si existe una fila con una determinada hora inicial y final
     * @param h1 La hora inicial a considerar (formato: HH:MM)
     * @param h2 La hora final a considerar (formato: HH:MM)
     * @return true si existe una fila con hora inicial y final indicadas false en caso contrario
     */
    private int existe(String h1, String h2){
        String hrTemp1;
        String hrTemp2;
        for(int g=0;g<jtbDias.getRowCount();g++){
            hrTemp1=""+jtbDias.getValueAt(g,0);
            hrTemp2=hrTemp1.substring(15,20);
            hrTemp1=hrTemp1.substring(6,11);
            if(h1.equals(hrTemp1) && h2.equals(hrTemp2)){
                return g;
            }
        }
        return -1;
    }
    
    /** Obtiene la duracion en minutos para las actividades de una fila de la tabla de horarios
     * @param fila el indice de la fila de la tabla de horarios a considerar
     * @return la duracion en minutos para las actividades de la fila indicada de la tabla de horarios
     */
    private int getDuracion(int fila){
        String rep=""+jtbDias.getValueAt(fila,0);
        int dura;
        int hora1=Integer.parseInt(rep.substring(6,8));
        int min1=Integer.parseInt(rep.substring(9,11));
        int hora2=Integer.parseInt(rep.substring(15,17));
        int min2=Integer.parseInt(rep.substring(18,20));
        if(hora1==hora2) return min2-min1;
        dura=60-min1+min2;
        while(hora1<hora2-1){
            dura+=60;
            hora1++;
        }
        return dura;
    } 
    
    /** Ordena las filas de la tabla de horarios por hora inicial
     */
    private void ordena(){
        int hrTemp1;
        int hrTemp2;
        Object tmp;
        DefaultTableModel mod=(DefaultTableModel)jtbDias.getModel();
        for(int g=0;g<mod.getRowCount()-1;g++){
            hrTemp1=Integer.parseInt((""+jtbDias.getValueAt(g,0)).substring(6,8));
            hrTemp2=Integer.parseInt((""+jtbDias.getValueAt(g+1,0)).substring(6,8));
            if(hrTemp2<hrTemp1){
                for(int f=0;f<mod.getColumnCount();f++){
                    tmp=mod.getValueAt(g,f);
                    mod.setValueAt(mod.getValueAt(g+1,f),g,f);
                    mod.setValueAt(tmp,g+1,f);
                }
                g=-1;
            }
        }
    }
  
    /** Si se hizo click en una area de celdas de la tabla de horarios llama al metodo que muestra el popupmenu
     * @param evt El MouseEvent que se genero al hacer click en la tabal de horarios
     */
    private void llamaMenu(java.awt.event.MouseEvent evt){
        int col=jtbDias.columnAtPoint(evt.getPoint());
        int fila=jtbDias.rowAtPoint(evt.getPoint());        
        Celda celda;
        if (fila<0 || col<1) return;
        if(jtbDias.getValueAt(fila, col)!=null){
            celda=(Celda)jtbDias.getValueAt(fila, col);
            mostrarPopupMenu(evt,""+celda.getClave()+":"+fila+":"+col);
        }
    }
    
    /** Si se hizo click con el evento disparador de popupmenues se muestra el popupmenu
     * @param evt El MouseEvent que se genero al hacer click en la tabal de horarios
     * @param txtDesc El texto de la celda sobre la cual se hizo click
     */
    private void mostrarPopupMenu(java.awt.event.MouseEvent evt, String txtDesc){
        if (evt.isPopupTrigger()){
            mnuq.setActionCommand(txtDesc);
            pmnuSes.show(evt.getComponent(),evt.getX(),evt.getY());
        }
    }
 
    /** Borra una celda y borra la respectiva actividad de la base de datos
     * @param desc La descripcion de la celda en formato: clave:fila:col
     *  donde clave es la clave de la actividad (tabla activpordia); fila es el 
     *  indice de la fila en la cual se hizo click y col es el indice de la
     *  columna en la cual se hizo click
     */
    private void quitaCelda(String desc){
        // en desc viene: clave:fila:col
        java.util.StringTokenizer tk=new java.util.StringTokenizer(desc,":");
        int clave=Integer.parseInt(tk.nextToken());
        int fila=Integer.parseInt(tk.nextToken());
        int col=Integer.parseInt(tk.nextToken());
        DefaultTableModel mod;
        if(pideDesicion("Atencion","<html>Esta accion no se podra deshacer<br>¿Desea continuar?</html>").getTipo()!=TipoRespuesta.ACEPTAR.getTipo()){
            return;
        }
        if(Actualiza.actualiza("delete from ActivPorDia where ClvAD="+clave+";",false,true)){
            mod=(DefaultTableModel)jtbDias.getModel();
            mod.setValueAt(null,fila,col);
            for(int g=1;g<mod.getColumnCount();g++){
                if(mod.getValueAt(fila,g)!=null){
                    return;
                }
            }
            mod.removeRow(fila);
        }
        else{
            mensaje("No se pudo quitar","Error: "+Actualiza.obtenError(),TipoMensaje.ERROR);
        }
    }
   
    /** Obtiene la descripcion del ultimo error ocurrido
     * @return la descripcion del ultimo error ocurrido
     */
    public String obtenError(){ return error; }
    
    // Variables declaration - do not modify//GEN-BEGIN:variables
    private javax.swing.JButton btnAgregaCelda;
    private javax.swing.JButton btnCreaDias;
    private javax.swing.JButton btnImp;
    private javax.swing.JScrollPane jScrollPane1;
    private javax.swing.JTable jtbDias;
    private javax.swing.JLabel lblTitulo;
    private javax.swing.JPanel pnlBtns;
    // End of variables declaration//GEN-END:variables
 
    /**Objeto que guarda los datos de una celda de la tabla de horarios*/
    private class Celda{
        
        /**El texto de la celda*/
        private String txt;
        /**La clave de la actividad de la celda*/
        private int clave;
        
        /** Crea una nueva Celda y guardar los datos en un nuevo registro en la base de datos 
         * @param txt El texto de la celda
         * @param clvg La clave del grupo si la actividad pertenece a un grupo
         * @param clv La clave de tesis si la actividad pertenece a una asesoria de tesista
         * @param h1 El horario inicial de la actividad
         * @param h2 El horario final de la actividad
         * @param dia El dia de la actividad (0 a 5 luneas a sabado)
         * @throws java.lang.Exception
         *  una excepcion indicando en su menssage el error que se produjo al 
         *   tratar de crear el registro de la actividad en la base de datos
         */
        public Celda(String txt,String clvg,int clv,String h1,String h2,int dia)throws Exception{
            if(Actualiza.nuevaActivDia(clvg,clv,h1,h2,dia,txt,true)){
                clave=Actualiza.obtenClave();
                this.txt=txt;
            }
            else{
                throw new Exception(Actualiza.obtenError());
            }
        }
        
        /** Crea una nueva celda recibiendo datos que ya deben existir en la base de datos
         * @param txt El texto de la celda
         * @param clv La clave de la actividad de la celda
         */
        public Celda(String txt,int clv){
            this.txt=txt; clave=clv;
        }
       
        /** Obtiene la clave de la actividad de la celda
         * @return la clave de la actividad de la celda
         */
        public int getClave(){ return clave; }
        
        /** Obtiene la reprentacion string del contenido de la celda
         * @return la reprentacion string del contenido de la celda
         */
        @Override
        public String toString(){
            String rep="";
            for(int j=0;j<txt.length();j++){
                if(txt.charAt(j)=='\n'){
                    rep+="<br>";
                }
                else{
                    rep+=txt.charAt(j);
                }
            }
            return "<html>"+rep+"</html>";
        }
    }
    
    /**Crea un jdialog para obtener los datos para crear una nueva celda en la tabla de horarios*/
    private class DialogoCreaCelda extends javax.swing.JDialog implements java.awt.event.ActionListener, java.awt.event.WindowListener{
        
        /**Boton para cancelar y cerra el jdialog*/
        private javax.swing.JButton btnCancela;
        /**Boton crear la nueva actividad*/
        private javax.swing.JButton btnCrea;
        private javax.swing.JLabel jLabel1;
        private javax.swing.JLabel jLabel2;
        private javax.swing.JLabel jLabel3;
        /**Lista desplegable para dar la opcion de los dias de la semana disponibles para la actividad a crear*/
        private javax.swing.JComboBox jcbDias;
        /**Caja de texto para la hora final de la actividad a crear*/
        private javax.swing.JTextField txtHoraFin;
        /**Caja de texto para la hora inicial de la actividad a crear*/
        private javax.swing.JTextField txtHoraIni;
        /**Bandera que indica si al cerrar el dialogo si el usuario cancelo la accion*/
        private boolean cancelo;
        
        /** Crea una nueva DialogoCelda
         * @param parent La ventana propietaria del dialogo
         * @param modal Si se desea que el dialogo se muestre en forma modal
         * @param tit El titulo del dialogo
         * @param datos Los datos precargados de la actividad a crea hora inicial y final
         */
        public DialogoCreaCelda(java.awt.Frame parent, boolean modal, String tit,String[] datos) {
            super(parent, modal);
            initComponents();
            setTitle(tit); cancelo=false;
            btnCancela.addActionListener(this);
            btnCrea.addActionListener(this);
            btnCancela.addActionListener(this);
            addWindowListener(this);
            btnCrea.addActionListener(this);
            if(datos!=null){
                txtHoraIni.setText(datos[1]);
                txtHoraFin.setText(datos[2]);
            }
            setLocationRelativeTo(parent);
            setVisible(true);
        }
        
        /** Booleano que indica si al cerrar el dialogo si el usuario cancelo la accion
         * @return booleano que indica si al cerrar el dialogo si el usuario cancelo la accion
         */
        public boolean cancelo(){ return cancelo; }
        
        /** Obtiene los datos ingresados en los controles por el usuario
         * @return un vector con los datos en orden: indice de dia de la semana; 
         *   hora inicial; hora final
         */
        public String[] getDatos(){
            String[] datos=new String[3];
            datos[0]=""+jcbDias.getSelectedIndex();
            datos[1]=txtHoraIni.getText();
            datos[2]=txtHoraFin.getText();
            return datos;
        }
        
        /**Inicializa los controles del dialogo*/
        private void initComponents(){
             jLabel1 = new javax.swing.JLabel("Dia:");
             jcbDias = new javax.swing.JComboBox();
             jLabel2 = new javax.swing.JLabel("Hora inicial:");
             jLabel3 = new javax.swing.JLabel("Hora final:");
             txtHoraIni = new javax.swing.JTextField();
             txtHoraFin = new javax.swing.JTextField();
             btnCrea = new javax.swing.JButton("Crear");
             btnCancela = new javax.swing.JButton("Cancelar");
             setDefaultCloseOperation(javax.swing.WindowConstants.DISPOSE_ON_CLOSE);
             jcbDias.setModel(new javax.swing.DefaultComboBoxModel(new String[] { "Lunes", "Martes", "MIercoles", "Jueves", "Viernes", "Sabado" }));
             javax.swing.GroupLayout layout = new javax.swing.GroupLayout(getContentPane());
             getContentPane().setLayout(layout);
             layout.setHorizontalGroup(
                     layout.createParallelGroup(javax.swing.GroupLayout.Alignment.LEADING)
                     .addGroup(layout.createSequentialGroup()
                     .addContainerGap()
                     .addGroup(layout.createParallelGroup(javax.swing.GroupLayout.Alignment.LEADING)
                     .addComponent(jLabel1, javax.swing.GroupLayout.Alignment.TRAILING)
                     .addComponent(jLabel2, javax.swing.GroupLayout.Alignment.TRAILING)
                     .addComponent(jLabel3, javax.swing.GroupLayout.Alignment.TRAILING))
                     .addPreferredGap(javax.swing.LayoutStyle.ComponentPlacement.RELATED)
                     .addGroup(layout.createParallelGroup(javax.swing.GroupLayout.Alignment.LEADING)
                     .addComponent(jcbDias, javax.swing.GroupLayout.PREFERRED_SIZE, javax.swing.GroupLayout.DEFAULT_SIZE, javax.swing.GroupLayout.PREFERRED_SIZE)
                     .addComponent(txtHoraIni, javax.swing.GroupLayout.PREFERRED_SIZE, 100, javax.swing.GroupLayout.PREFERRED_SIZE)
                     .addComponent(txtHoraFin, javax.swing.GroupLayout.PREFERRED_SIZE, 100, javax.swing.GroupLayout.PREFERRED_SIZE))
                     .addContainerGap(50, Short.MAX_VALUE))
                     .addGroup(javax.swing.GroupLayout.Alignment.TRAILING, layout.createSequentialGroup()
                     .addContainerGap(69, Short.MAX_VALUE)
                     .addComponent(btnCrea)
                     .addPreferredGap(javax.swing.LayoutStyle.ComponentPlacement.RELATED)
                     .addComponent(btnCancela)
                     .addContainerGap())
                     );
             layout.setVerticalGroup(
                     layout.createParallelGroup(javax.swing.GroupLayout.Alignment.LEADING)
                     .addGroup(layout.createSequentialGroup()
                     .addContainerGap()
                     .addGroup(layout.createParallelGroup(javax.swing.GroupLayout.Alignment.BASELINE)
                     .addComponent(jLabel1)
                     .addComponent(jcbDias, javax.swing.GroupLayout.PREFERRED_SIZE, javax.swing.GroupLayout.DEFAULT_SIZE, javax.swing.GroupLayout.PREFERRED_SIZE))
                     .addPreferredGap(javax.swing.LayoutStyle.ComponentPlacement.RELATED)
                     .addGroup(layout.createParallelGroup(javax.swing.GroupLayout.Alignment.BASELINE)
                     .addComponent(jLabel2)
                     .addComponent(txtHoraIni, javax.swing.GroupLayout.PREFERRED_SIZE, javax.swing.GroupLayout.DEFAULT_SIZE, javax.swing.GroupLayout.PREFERRED_SIZE))
                     .addPreferredGap(javax.swing.LayoutStyle.ComponentPlacement.RELATED)
                     .addGroup(layout.createParallelGroup(javax.swing.GroupLayout.Alignment.BASELINE)
                     .addComponent(jLabel3)
                     .addComponent(txtHoraFin, javax.swing.GroupLayout.PREFERRED_SIZE, javax.swing.GroupLayout.DEFAULT_SIZE, javax.swing.GroupLayout.PREFERRED_SIZE))
                     .addPreferredGap(javax.swing.LayoutStyle.ComponentPlacement.UNRELATED)
                     .addGroup(layout.createParallelGroup(javax.swing.GroupLayout.Alignment.BASELINE)
                     .addComponent(btnCrea)
                     .addComponent(btnCancela))
                     .addContainerGap(20, Short.MAX_VALUE))
                     );
             pack();
         }

        /** Implementacion de las acciones de los botones del dialogo
         * @param e El ActionEvent que genero el evetno
         */
        public void actionPerformed(ActionEvent e) {
            javax.swing.JButton btn=(javax.swing.JButton)e.getSource();
            cancelo=(btn==btnCancela);
            dispose();
        }

        /** Implementacion del cierre del dialogo
         * @param e El WindowEvent que genero el evento
         */
        public void windowClosing(WindowEvent e){ cancelo=true; }
        public void windowOpened(WindowEvent e){}
        public void windowClosed(WindowEvent e){}
        public void windowIconified(WindowEvent e){}
        public void windowDeiconified(WindowEvent e){}
        public void windowActivated(WindowEvent e){}
        public void windowDeactivated(WindowEvent e){}
    }
    
    /**Definicion de excepcion que se lanza cuando al construir un control ControlDiasSemana se le envia una referencia invalida de ventana contenedora*/
    public class RefSuperiorInvalida extends Exception{
        
        /** Construye una nueva RefSuperiorInvalida */
        public RefSuperiorInvalida(){
            super("Instancia de referencia superior invalida");
        }
    }
    
}