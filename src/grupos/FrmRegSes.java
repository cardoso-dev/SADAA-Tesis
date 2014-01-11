/*
 * FrmRegSes.java
 *   Encargado de manejar registros individuales de sesiones programas (calendario grupos)
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

import database.Consultas;
import database.Actualiza;
import definiciones.TipoMensaje;
import definiciones.TipoRespuesta;
import iconos.Iconos;
import java.awt.event.ActionEvent;
import java.awt.event.ItemEvent;

/**  Es una ventana interna (JInternalFrame) para manejar un registro individual de las
 *  sesiones programadas para un grupo.
 * 
 * @author Pedro Cardoso Rodríguez
 */
public class FrmRegSes extends sistema.ModeloFrameInterno{
    
    /**Referencia a la ventana ficha de grupo a la cual pertenece esta ventana*/
    private FrameCalendario refCal;
    /**La clave del grupo al que pertenece la sesion*/
    private String claveg;
    /**La clave de la sesion en la bd o -1 si no se ha guardado*/
    private int claveses;
    /**version del temario utilizado para los temas a programar para esta sesion*/
    private int temario;
    /**Temas que incluye la materia del grupo al que pertenece esta sesion*/
    private String[][] temas;
    /**Descripcion del ultimo error ocurrido*/
    private String error;
    /**Lista de controles de temas programados para esta sesion*/
    private java.util.ArrayList<Tema> listTemas;
    /**Lista de controles de objetivos programados para esta sesion*/
    private java.util.ArrayList<Objetivo> listObjs;
    
    /** Crea una nueva ventana FrmRegSes
     * @param ventana Referencia a la ventana principal contenedora (clase sistema.FramePrincipal)
     * @param refCal Referencia a la ventana ficha de grupo a la cual pertenece esta ventana
     * @param claveg La clave del grupo al que pertenece la sesion
     * @param versTem version del temario utilizado para los temas a programar para esta sesion
     */
    public FrmRegSes(sistema.FramePrincipal ventana, FrameCalendario refCal, String claveg, int versTem) {
        super(ventana,"frmcal.png");
        initComponents();
        this.refCal=refCal; this.claveg=claveg; temario=versTem;
        temas=null; claveses=-1;
        setTitle("Registro de sesión grupo: "+claveg);
        lblMens.setText("Datos de nueva sesion:");
        btnGuarda.setIcon(Iconos.getIcono("guardar.png"));
        lblNumTemario.setText("Corresponde a temario versión: "+versTem);
        listTemas=new java.util.ArrayList<Tema>();
        listObjs=new java.util.ArrayList<Objetivo>();
        cargaTemas();
        setCambios(false); escuchaCambios();
    }
   
    /** Carga el registro de una sesion incluyendo sus temas y objetivos programados
     * @param clave La clave de la sesion a programar
     * @return true si el registro se cargo correctamente false en caso contrario
     */
    public boolean cargaSesion(int clave){
        String sen="select sesiones.*,temario.*,vetema.porcenplan,objetivos.* from Sesiones left join vetema ";
        sen+="using(clvses) left join temario using(clvtem) left join busca using(clvses) left";
        sen+=" join objetivos using(clvobj) where Sesiones.ClvSes="+clave+" order by clvtem,clvobj;";
        String[][] dts;
        int ultTema=-1;
        int ultObj=-1;
        dts=Consultas.consultaDatos(sen,true);
        if(dts==null){
            muestraMensaje("Error al cargar datos de sesión",Consultas.obtenError(),TipoMensaje.ERROR);
            return false;
        }
        else if(dts[0][0]==null){
            muestraMensaje("Atención","No se encontro la sesión con clave "+clave,TipoMensaje.ERROR);
            return false;
        }
        claveses=clave;
        setTitle("Registro de sesión "+claveses+" grupo: "+claveg);
        lblMens.setText("Datos de sesión: "+claveses);
        txtFcha.setText(dts[0][1].substring(0,10));
        txtHorIni.setText(dts[0][1].substring(11,16));
        txtDur.setText(dts[0][2]);
        txtPlan.setText(dts[0][3]);
        txtComp.setText(dts[0][4]);
        txtMate.setText(dts[0][5]);
        txtObser.setText(dts[0][6]);
        for(int l=0;l<dts.length;l++){
            if(dts[l][7]!=null && Integer.parseInt(dts[l][7])>ultTema){
                Tema tem=new Tema();
                tem.setFinalClave(Integer.parseInt(dts[l][7]));
                tem.setPorcentaje(Integer.parseInt(dts[l][13]));
                ultTema=tem.getClaveTema();
                agregaTema(tem);
            }
            if(dts[l][14]!=null && !dts[l][14].equals("null") && Integer.parseInt(dts[l][14])>ultObj){
                Objetivo obj=new Objetivo(Integer.parseInt(dts[l][14]),dts[l][15]);
                ultObj=Integer.parseInt(dts[l][14]);
                agregaObjetivo(obj);
            }
        }
        setCambios(false);
        return true;
    }
 
    /** Carga desde la base de datos los temas que abarca la materia del grupo al 
     *   cual pertenece esta sesion (considerando la version del temario)
     * @return true si los datos se cargaron correctamente false en caso contrario
     */
    private boolean cargaTemas(){
        String sen="select temario.* from temario,cubre where version="+temario;
        sen+=" and temario.clvtem=cubre.clvtem and clvm='"+claveg.substring(3,6)+"';";
        temas=Consultas.consultaDatos(sen,false);
        if(temas==null){
            error=Consultas.obtenError();
            return false;
        }
        return true;
    }
    
    /** This method is called from within the constructor to
     * initialize the form.
     * WARNING: Do NOT modify this code. The content of this method is
     * always regenerated by the Form Editor.
     */
    // <editor-fold defaultstate="collapsed" desc="Generated Code">//GEN-BEGIN:initComponents
    private void initComponents() {
        java.awt.GridBagConstraints gridBagConstraints;

        lblMens = new javax.swing.JLabel();
        jLabel2 = new javax.swing.JLabel();
        txtFcha = new javax.swing.JTextField();
        jLabel3 = new javax.swing.JLabel();
        txtHorIni = new javax.swing.JTextField();
        jLabel4 = new javax.swing.JLabel();
        txtDur = new javax.swing.JTextField();
        jLabel6 = new javax.swing.JLabel();
        jLabel7 = new javax.swing.JLabel();
        jScrollPane1 = new javax.swing.JScrollPane();
        txtObser = new javax.swing.JTextArea();
        btnGuarda = new javax.swing.JButton();
        jLabel1 = new javax.swing.JLabel();
        btnAgregaT = new javax.swing.JButton();
        jScrollPane2 = new javax.swing.JScrollPane();
        pnlTemas = new javax.swing.JPanel();
        lblMensTemas = new javax.swing.JLabel();
        jLabel9 = new javax.swing.JLabel();
        btnAgregaO = new javax.swing.JButton();
        jScrollPane3 = new javax.swing.JScrollPane();
        pnlObjetivos = new javax.swing.JPanel();
        lblMesnObjs = new javax.swing.JLabel();
        jLabel5 = new javax.swing.JLabel();
        txtPlan = new javax.swing.JTextField();
        lblNumTemario = new javax.swing.JLabel();
        jLabel8 = new javax.swing.JLabel();
        jLabel10 = new javax.swing.JLabel();
        txtComp = new javax.swing.JTextField();
        txtMate = new javax.swing.JTextField();

        setClosable(true);
        setDefaultCloseOperation(javax.swing.WindowConstants.DO_NOTHING_ON_CLOSE);
        setIconifiable(true);
        setTitle("Registro de sesión grupo");

        lblMens.setHorizontalAlignment(javax.swing.SwingConstants.CENTER);
        lblMens.setText("Datos de nueva sesión:");

        jLabel2.setText("Fecha:");

        jLabel3.setText("*Hora inicial:");

        jLabel4.setText("Duración:");

        jLabel6.setText("Tema (s):");

        jLabel7.setText("Observaciones:");

        txtObser.setColumns(20);
        txtObser.setRows(5);
        jScrollPane1.setViewportView(txtObser);

        btnGuarda.setText("Guardar");
        btnGuarda.addActionListener(new java.awt.event.ActionListener() {
            public void actionPerformed(java.awt.event.ActionEvent evt) {
                btnGuardaActionPerformed(evt);
            }
        });

        jLabel1.setFont(new java.awt.Font("Tahoma", 2, 11));
        jLabel1.setText("minutos");

        btnAgregaT.setText("Agregar");
        btnAgregaT.addActionListener(new java.awt.event.ActionListener() {
            public void actionPerformed(java.awt.event.ActionEvent evt) {
                btnAgregaTActionPerformed(evt);
            }
        });

        pnlTemas.setLayout(new java.awt.GridBagLayout());

        lblMensTemas.setText("Seleccione tema e indique el porcentaje que planea cubrir en esta sesión.");
        gridBagConstraints = new java.awt.GridBagConstraints();
        gridBagConstraints.gridx = 0;
        gridBagConstraints.gridy = 0;
        gridBagConstraints.fill = java.awt.GridBagConstraints.HORIZONTAL;
        pnlTemas.add(lblMensTemas, gridBagConstraints);

        jScrollPane2.setViewportView(pnlTemas);

        jLabel9.setText("Objetivo (s):");

        btnAgregaO.setText("Agregar");
        btnAgregaO.addActionListener(new java.awt.event.ActionListener() {
            public void actionPerformed(java.awt.event.ActionEvent evt) {
                btnAgregaOActionPerformed(evt);
            }
        });

        pnlObjetivos.setLayout(new java.awt.GridBagLayout());

        lblMesnObjs.setText("Ingrese la descripcion del objetivo(s) que planea alcanzar en esta sesión");
        gridBagConstraints = new java.awt.GridBagConstraints();
        gridBagConstraints.gridx = 0;
        gridBagConstraints.gridy = 0;
        gridBagConstraints.fill = java.awt.GridBagConstraints.HORIZONTAL;
        pnlObjetivos.add(lblMesnObjs, gridBagConstraints);

        jScrollPane3.setViewportView(pnlObjetivos);

        jLabel5.setText("Plan:");

        lblNumTemario.setText("Corresponden a temario versión: XX");

        jLabel8.setText("Competencia:");

        jLabel10.setText("Material:");

        javax.swing.GroupLayout layout = new javax.swing.GroupLayout(getContentPane());
        getContentPane().setLayout(layout);
        layout.setHorizontalGroup(
            layout.createParallelGroup(javax.swing.GroupLayout.Alignment.LEADING)
            .addGroup(layout.createSequentialGroup()
                .addContainerGap()
                .addGroup(layout.createParallelGroup(javax.swing.GroupLayout.Alignment.LEADING)
                    .addGroup(layout.createSequentialGroup()
                        .addGap(10, 10, 10)
                        .addComponent(jLabel7))
                    .addGroup(layout.createSequentialGroup()
                        .addGap(10, 10, 10)
                        .addComponent(jLabel9)
                        .addPreferredGap(javax.swing.LayoutStyle.ComponentPlacement.UNRELATED)
                        .addComponent(btnAgregaO))
                    .addGroup(layout.createSequentialGroup()
                        .addGap(10, 10, 10)
                        .addComponent(jLabel6)
                        .addPreferredGap(javax.swing.LayoutStyle.ComponentPlacement.UNRELATED)
                        .addComponent(btnAgregaT)
                        .addGap(18, 18, 18)
                        .addComponent(lblNumTemario))
                    .addGroup(javax.swing.GroupLayout.Alignment.TRAILING, layout.createSequentialGroup()
                        .addComponent(lblMens, javax.swing.GroupLayout.DEFAULT_SIZE, 369, Short.MAX_VALUE)
                        .addPreferredGap(javax.swing.LayoutStyle.ComponentPlacement.RELATED)
                        .addComponent(btnGuarda))
                    .addGroup(layout.createSequentialGroup()
                        .addComponent(jLabel2)
                        .addPreferredGap(javax.swing.LayoutStyle.ComponentPlacement.RELATED)
                        .addComponent(txtFcha, javax.swing.GroupLayout.PREFERRED_SIZE, 87, javax.swing.GroupLayout.PREFERRED_SIZE)
                        .addPreferredGap(javax.swing.LayoutStyle.ComponentPlacement.RELATED)
                        .addComponent(jLabel3)
                        .addPreferredGap(javax.swing.LayoutStyle.ComponentPlacement.RELATED)
                        .addComponent(txtHorIni, javax.swing.GroupLayout.PREFERRED_SIZE, 50, javax.swing.GroupLayout.PREFERRED_SIZE)
                        .addPreferredGap(javax.swing.LayoutStyle.ComponentPlacement.RELATED)
                        .addComponent(jLabel4)
                        .addPreferredGap(javax.swing.LayoutStyle.ComponentPlacement.RELATED)
                        .addComponent(txtDur, javax.swing.GroupLayout.PREFERRED_SIZE, 44, javax.swing.GroupLayout.PREFERRED_SIZE)
                        .addPreferredGap(javax.swing.LayoutStyle.ComponentPlacement.RELATED)
                        .addComponent(jLabel1))
                    .addGroup(layout.createSequentialGroup()
                        .addComponent(jLabel5)
                        .addPreferredGap(javax.swing.LayoutStyle.ComponentPlacement.RELATED)
                        .addComponent(txtPlan, javax.swing.GroupLayout.PREFERRED_SIZE, 350, javax.swing.GroupLayout.PREFERRED_SIZE))
                    .addGroup(layout.createSequentialGroup()
                        .addComponent(jLabel8)
                        .addPreferredGap(javax.swing.LayoutStyle.ComponentPlacement.UNRELATED)
                        .addComponent(txtComp, javax.swing.GroupLayout.PREFERRED_SIZE, 302, javax.swing.GroupLayout.PREFERRED_SIZE))
                    .addGroup(layout.createSequentialGroup()
                        .addComponent(jLabel10)
                        .addPreferredGap(javax.swing.LayoutStyle.ComponentPlacement.UNRELATED)
                        .addComponent(txtMate, javax.swing.GroupLayout.PREFERRED_SIZE, 326, javax.swing.GroupLayout.PREFERRED_SIZE))
                    .addComponent(jScrollPane2, javax.swing.GroupLayout.DEFAULT_SIZE, 444, Short.MAX_VALUE)
                    .addComponent(jScrollPane3, javax.swing.GroupLayout.DEFAULT_SIZE, 444, Short.MAX_VALUE)
                    .addComponent(jScrollPane1, javax.swing.GroupLayout.DEFAULT_SIZE, 444, Short.MAX_VALUE))
                .addContainerGap())
        );
        layout.setVerticalGroup(
            layout.createParallelGroup(javax.swing.GroupLayout.Alignment.LEADING)
            .addGroup(layout.createSequentialGroup()
                .addContainerGap()
                .addGroup(layout.createParallelGroup(javax.swing.GroupLayout.Alignment.BASELINE)
                    .addComponent(lblMens)
                    .addComponent(btnGuarda))
                .addPreferredGap(javax.swing.LayoutStyle.ComponentPlacement.RELATED)
                .addGroup(layout.createParallelGroup(javax.swing.GroupLayout.Alignment.BASELINE)
                    .addComponent(jLabel2)
                    .addComponent(txtFcha, javax.swing.GroupLayout.PREFERRED_SIZE, javax.swing.GroupLayout.DEFAULT_SIZE, javax.swing.GroupLayout.PREFERRED_SIZE)
                    .addComponent(jLabel3)
                    .addComponent(txtHorIni, javax.swing.GroupLayout.PREFERRED_SIZE, javax.swing.GroupLayout.DEFAULT_SIZE, javax.swing.GroupLayout.PREFERRED_SIZE)
                    .addComponent(jLabel4)
                    .addComponent(txtDur, javax.swing.GroupLayout.PREFERRED_SIZE, javax.swing.GroupLayout.DEFAULT_SIZE, javax.swing.GroupLayout.PREFERRED_SIZE)
                    .addComponent(jLabel1))
                .addPreferredGap(javax.swing.LayoutStyle.ComponentPlacement.RELATED)
                .addGroup(layout.createParallelGroup(javax.swing.GroupLayout.Alignment.BASELINE)
                    .addComponent(jLabel5)
                    .addComponent(txtPlan, javax.swing.GroupLayout.PREFERRED_SIZE, javax.swing.GroupLayout.DEFAULT_SIZE, javax.swing.GroupLayout.PREFERRED_SIZE))
                .addPreferredGap(javax.swing.LayoutStyle.ComponentPlacement.RELATED)
                .addGroup(layout.createParallelGroup(javax.swing.GroupLayout.Alignment.BASELINE)
                    .addComponent(jLabel8)
                    .addComponent(txtComp, javax.swing.GroupLayout.PREFERRED_SIZE, javax.swing.GroupLayout.DEFAULT_SIZE, javax.swing.GroupLayout.PREFERRED_SIZE))
                .addPreferredGap(javax.swing.LayoutStyle.ComponentPlacement.RELATED)
                .addGroup(layout.createParallelGroup(javax.swing.GroupLayout.Alignment.BASELINE)
                    .addComponent(jLabel10)
                    .addComponent(txtMate, javax.swing.GroupLayout.PREFERRED_SIZE, javax.swing.GroupLayout.DEFAULT_SIZE, javax.swing.GroupLayout.PREFERRED_SIZE))
                .addPreferredGap(javax.swing.LayoutStyle.ComponentPlacement.RELATED)
                .addGroup(layout.createParallelGroup(javax.swing.GroupLayout.Alignment.BASELINE)
                    .addComponent(jLabel6)
                    .addComponent(btnAgregaT)
                    .addComponent(lblNumTemario))
                .addPreferredGap(javax.swing.LayoutStyle.ComponentPlacement.RELATED)
                .addComponent(jScrollPane2, javax.swing.GroupLayout.PREFERRED_SIZE, 75, javax.swing.GroupLayout.PREFERRED_SIZE)
                .addPreferredGap(javax.swing.LayoutStyle.ComponentPlacement.RELATED)
                .addGroup(layout.createParallelGroup(javax.swing.GroupLayout.Alignment.BASELINE)
                    .addComponent(jLabel9)
                    .addComponent(btnAgregaO))
                .addPreferredGap(javax.swing.LayoutStyle.ComponentPlacement.RELATED)
                .addComponent(jScrollPane3, javax.swing.GroupLayout.PREFERRED_SIZE, 75, javax.swing.GroupLayout.PREFERRED_SIZE)
                .addPreferredGap(javax.swing.LayoutStyle.ComponentPlacement.RELATED)
                .addComponent(jLabel7)
                .addPreferredGap(javax.swing.LayoutStyle.ComponentPlacement.RELATED)
                .addComponent(jScrollPane1, javax.swing.GroupLayout.PREFERRED_SIZE, javax.swing.GroupLayout.DEFAULT_SIZE, javax.swing.GroupLayout.PREFERRED_SIZE)
                .addContainerGap(19, Short.MAX_VALUE))
        );

        pack();
    }// </editor-fold>//GEN-END:initComponents
   
    /** Llama al metodo que guarda los datos del registro actual
     * @param evt El ActionEvent que genero el evento
     */
    private void btnGuardaActionPerformed(java.awt.event.ActionEvent evt) {//GEN-FIRST:event_btnGuardaActionPerformed
        if(guardaCambios()){
            muestraMensaje("Acción realizada","Se ha guardado el registro ",TipoMensaje.INFORMACION);
        }
    }//GEN-LAST:event_btnGuardaActionPerformed

    /** Valida y guarda los datos actuales en la base de datos si es registro nuevo crea el registro 
     *   si ya existia el registro actualiza los valores.
     * @return true si la operacion se realizo correctamente, false si hay datos invalidos o fallo al guardar en la bd
     */
    @Override
    public boolean guardaCambios(){
        String sen;
        String[] datos;
        java.util.ArrayList<String> trans=new java.util.ArrayList<String>();
        if(!sonDatosValidos()) return false;
        datos=new String[7];
        datos[0]=operaciones.Datos.transformatFcha(txtFcha.getText().trim())+" "+txtHorIni.getText().trim()+":00";
        datos[1]=txtDur.getText().trim();
        datos[2]=txtPlan.getText().trim();
        datos[3]=txtComp.getText().trim();
        datos[4]=txtMate.getText().trim();
        datos[5]=txtObser.getText().trim();
        datos[6]=claveg;
        if(claveses==-1){ // si clave no existe hacer un insert si no hacer update 
            if(Actualiza.nuevoRgSesProg(datos,true)){
                claveses=Actualiza.obtenClave();
                if(claveses==0){
                    muestraMensaje("No se pudo crear","Ya existe una sesión con la misma fecha y hora",TipoMensaje.INFORMACION);
                    return false;
                }
            }
            else{
                muestraMensaje("Acción fallida","<html>Ocurrio un error al guardar los datos<br>"+Actualiza.obtenError()+"</html>",TipoMensaje.ERROR);
                return false;
            }
        }
        else {
            sen="update Sesiones set FechaYHora='"+datos[0]+"', Duracion="+datos[1]+", Plan='"+datos[2];
            sen+="', competencia="+(datos[3].length()>0?"'"+datos[3]+"'":"null")+", material=";
            sen+=(datos[4].length()>0?"'"+datos[4]+"'":"null")+", observaciones=";
            sen+=(datos[5].length()>0?"'"+datos[5]+"'":"null")+" where ClvSes="+claveses+";";
            trans.add(sen);
        }
        for(int j=0;j<listObjs.size();j++){
            if(listObjs.get(j).getClaveObj()==-1){
                if(Actualiza.nuevoObjetivo(claveses,listObjs.get(j).getDescripcion(),false)){
                    listObjs.get(j).setClaveObj(Actualiza.obtenClave());
                    // la creacion del objetivo automaticamente agrega la relacion en busca
                }
                else{
                    muestraMensaje("Acción fallida","<html>Ocurrio un error al guardar los objetivos:<br>"+Actualiza.obtenError()+"</html>",TipoMensaje.ERROR);
                    return false;
                }
            }
            else{
                sen="update Objetivos set Descrip='"+listObjs.get(j).getDescripcion()+"' where ClvObj="+listObjs.get(j).getClaveObj()+";";
                trans.add(sen);
            }
        }
        for(int f=0;f<listTemas.size();f++){
            if(!listTemas.get(f).esEnBD()){
                trans.add("insert into VeTema values("+listTemas.get(f).getClaveTema()+","+claveses+","+listTemas.get(f).getPorcentaje()+");");
            }
            else{
                sen="update VeTema set porcenplan="+listTemas.get(f).getPorcentaje()+" where clvtem="+listTemas.get(f).getClaveTema()+" and ";
                sen+="clvses="+claveses+";";
                trans.add(sen);
            }
        }
        if(!Actualiza.transaccion(trans,false,false)){
            muestraMensaje("Acción fallida","<html>Ocurrio un error al guardar los datos:<br>"+Actualiza.obtenError()+"</html>",TipoMensaje.ERROR);
            return false;
        }
        for(int f=0;f<listTemas.size();f++){
            listTemas.get(f).setFinalClave(listTemas.get(f).getClaveTema());
        }
        setTitle("Registro de sesión "+claveses+" grupo: "+claveg);
        lblMens.setText("Datos de sesion: "+claveses);
        if(refCal!=null) refCal.actualiza();
        setCambios(false);
        return true;
    }
  
    /** Agrega un control Tema en el jpanel pnlTemas para programar un tema para esta sesion
     * @param evt El ActionEvent que genero el evento
     */
    private void btnAgregaTActionPerformed(java.awt.event.ActionEvent evt) {//GEN-FIRST:event_btnAgregaTActionPerformed
        if(temas==null){
            if(!cargaTemas()){
                muestraMensaje("Error","<html>Error al tratar de cargar los temas<br>"+error+"</html>",TipoMensaje.ERROR);
                return;
            }
        }
        if(temas[0][0]==null){
            muestraMensaje("No existes temas","No existen temas actualmente para la versión del temario",TipoMensaje.INFORMACION);
            return;
        }
        agregaTema(new Tema());
    }//GEN-LAST:event_btnAgregaTActionPerformed

    /** Agrega un control Objetivo en el jpanel pnlObjetivos para programar un objetivo para esta sesion
     * @param evt El ActionEvent que genero el evento
     */
    private void btnAgregaOActionPerformed(java.awt.event.ActionEvent evt) {//GEN-FIRST:event_btnAgregaOActionPerformed
        agregaObjetivo(new Objetivo());
    }//GEN-LAST:event_btnAgregaOActionPerformed
 
    /** Agrega un control tema (cargado con sus datos) al panel de temas programados para esta sesion
     * @param tema El tema a agregar
     */
    private void agregaTema(Tema tema){
        java.awt.GridBagLayout gbl=((java.awt.GridBagLayout)pnlTemas.getLayout());
        java.awt.GridBagConstraints gbc=new java.awt.GridBagConstraints();
        gbc.gridx=0;
        gbc.gridy=pnlTemas.getComponentCount();
        gbc.fill=java.awt.GridBagConstraints.HORIZONTAL;
        gbc.insets=new java.awt.Insets(2,0,0,3);
        gbl.setConstraints(tema, gbc);
        pnlTemas.add(tema);
        listTemas.add(tema);
        setCambios(true);
    }
  
    /** Quita un tema del panel de temas programados para esta sesion
     * @param tem El tema a quitar
     */
    private void quitaTema(Tema tem){
        int band=-1;
        java.awt.GridBagLayout gbl=((java.awt.GridBagLayout)pnlTemas.getLayout());
        java.awt.GridBagConstraints gbc=new java.awt.GridBagConstraints();
        pnlTemas.removeAll();
        gbc.gridx=0; gbc.gridy=0;
        gbc.fill=java.awt.GridBagConstraints.HORIZONTAL;
        gbc.insets=new java.awt.Insets(2,0,0,3);
        gbl.setConstraints(lblMensTemas, gbc);
        pnlTemas.setVisible(false);
        pnlTemas.add(lblMensTemas);
        for(int g=0;g<listTemas.size();g++){
            if(listTemas.get(g)!=tem){
                gbc.gridy=(g+1);
                gbl.setConstraints(listTemas.get(g),gbc);
                pnlTemas.add(listTemas.get(g));
            }
            else{
                band=g;
            }
        }
        pnlTemas.setVisible(true);
        listTemas.remove(band);
    }
  
    /** Agrega un control objetivo (cargado con sus datos) al panel de objetivos programados para esta sesion
     * @param obj el objetivo a agregar
     */
    private void agregaObjetivo(Objetivo obj){
        java.awt.GridBagLayout gbl=((java.awt.GridBagLayout)pnlObjetivos.getLayout());
        java.awt.GridBagConstraints gbc=new java.awt.GridBagConstraints();
        gbc.gridy=pnlObjetivos.getComponentCount();
        gbc.fill=java.awt.GridBagConstraints.HORIZONTAL;
        gbc.insets=new java.awt.Insets(2,0,0,3);
        gbl.setConstraints(obj, gbc);
        pnlObjetivos.add(obj);
        listObjs.add(obj);
        setCambios(true);
    }
   
    /** Quita un objetivo del panel de objetivos programados para esta sesion
     * @param obj El objetivo a quitar
     */
    private void quitaObjetivo(Objetivo obj){
        int band=-1;
        java.awt.GridBagLayout gbl=((java.awt.GridBagLayout)pnlObjetivos.getLayout());
        java.awt.GridBagConstraints gbc=new java.awt.GridBagConstraints();
        pnlObjetivos.removeAll();
        gbc.gridx=0; gbc.gridy=0;
        gbc.fill=java.awt.GridBagConstraints.HORIZONTAL;
        gbc.insets=new java.awt.Insets(2,0,0,3);
        gbl.setConstraints(lblMesnObjs, gbc);
        pnlObjetivos.setVisible(false);
        pnlObjetivos.add(lblMesnObjs);
        for(int g=0;g<listObjs.size();g++){
            if(listObjs.get(g)!=obj){
                gbc.gridy=(g+1);
                gbl.setConstraints(listObjs.get(g),gbc);
                pnlObjetivos.add(listObjs.get(g));
            }
            else{
                band=g;
            }
        }
        pnlObjetivos.setVisible(true);
        listObjs.remove(band);
    }
    
    /** Valida los datos en los controles
     * @return true si los datos son validos false si al menos un dato es invalido
     */
    private boolean sonDatosValidos(){
        int aux=0;
        boolean bandera=false;
        if(!operaciones.Datos.valFecha(txtFcha.getText().trim())){ // Fecha que tenga formato valido
            muestraMensaje("Error en los datos", "Fecha invalida",TipoMensaje.ERROR);
            return false;
        }
        if(!operaciones.Datos.valHorarioSimple(txtHorIni.getText().trim())){ // hora inicial que sea formato hora simple
            muestraMensaje("Error en los datos", "Hora inicial invalida",TipoMensaje.ERROR);
            return false;
        }
        try{ aux=Integer.parseInt(txtDur.getText().trim()); } // Duracion que sea numero mayor a 0 y menor a 255
        catch(NumberFormatException nfbExc){ bandera=true; }
        if(bandera || aux<0 || aux>255){
            muestraMensaje("Error en los datos", "Duración invalida",TipoMensaje.ERROR);
            return false;
        }
        if(txtPlan.getText().trim().equals("") || txtPlan.getText().trim().length()>85){ // plan no debe ser nulo ni pasar una longitud de 85
            muestraMensaje("Error en los datos", "Plan invalido",TipoMensaje.ERROR);
            return false;
        }
        if(txtComp.getText().trim().length()>255){ // Copetencia no debe tener una logitud mayor a 255
            muestraMensaje("Error en los datos", "Competencia invalida",TipoMensaje.ERROR);
            return false;
        }
        if(txtMate.getText().trim().length()>255){ // Material no debe tener una logitud mayor a 255
            muestraMensaje("Error en los datos", "Material invalido",TipoMensaje.ERROR);
            return false;
        }
        if(txtObser.getText().trim().length()>255){ // Observaciones no debe tener una logitud mayor a 255
            muestraMensaje("Error en los datos", "Observaciones invalidas",TipoMensaje.ERROR);
            return false;
        }
        for(int g=0;g<listTemas.size();g++){
            if(!listTemas.get(g).esValido()){
                muestraMensaje("Error en los datos", "Tema invalido (tema "+(g+1)+" de la lista)",TipoMensaje.ERROR);
                return false;
            }
        }
        for(int g=0;g<listObjs.size();g++){
            if(!listObjs.get(g).esValido()){
                muestraMensaje("Error en los datos", "Objetivo invalido (objetivo "+(g+1)+" de la lista)",TipoMensaje.ERROR);
                return false;
            }
        }
        return true;
    }
   
    /** Crea y asigna un listener en los controles de texto para escuchar si hay cambios en su contenido*/
    private void escuchaCambios(){
        ListenChanges listenCh=new ListenChanges();
        txtPlan.getDocument().addDocumentListener(listenCh);
        txtFcha.getDocument().addDocumentListener(listenCh);
        txtHorIni.getDocument().addDocumentListener(listenCh);
        txtDur.getDocument().addDocumentListener(listenCh);
        txtComp.getDocument().addDocumentListener(listenCh);
        txtMate.getDocument().addDocumentListener(listenCh);
        txtObser.getDocument().addDocumentListener(listenCh);
    }
    
    // Variables declaration - do not modify//GEN-BEGIN:variables
    private javax.swing.JButton btnAgregaO;
    private javax.swing.JButton btnAgregaT;
    private javax.swing.JButton btnGuarda;
    private javax.swing.JLabel jLabel1;
    private javax.swing.JLabel jLabel10;
    private javax.swing.JLabel jLabel2;
    private javax.swing.JLabel jLabel3;
    private javax.swing.JLabel jLabel4;
    private javax.swing.JLabel jLabel5;
    private javax.swing.JLabel jLabel6;
    private javax.swing.JLabel jLabel7;
    private javax.swing.JLabel jLabel8;
    private javax.swing.JLabel jLabel9;
    private javax.swing.JScrollPane jScrollPane1;
    private javax.swing.JScrollPane jScrollPane2;
    private javax.swing.JScrollPane jScrollPane3;
    private javax.swing.JLabel lblMens;
    private javax.swing.JLabel lblMensTemas;
    private javax.swing.JLabel lblMesnObjs;
    private javax.swing.JLabel lblNumTemario;
    private javax.swing.JPanel pnlObjetivos;
    private javax.swing.JPanel pnlTemas;
    private javax.swing.JTextField txtComp;
    private javax.swing.JTextField txtDur;
    private javax.swing.JTextField txtFcha;
    private javax.swing.JTextField txtHorIni;
    private javax.swing.JTextField txtMate;
    private javax.swing.JTextArea txtObser;
    private javax.swing.JTextField txtPlan;
    // End of variables declaration//GEN-END:variables

    /** Crea un control (hereda de jpanel) para manejar los datos de un tema programado para una sesion */
    private class Tema extends javax.swing.JPanel implements java.awt.event.ActionListener{
    
        /** La clave del tema actual */
        private int clvTem;
        /** Depliega la lista de temas disponibles*/
        private javax.swing.JComboBox jcbTema;
        /** Despliega el numero y descripcion del tema actual*/
        private javax.swing.JTextField txtTema;
        /** Despliega el porcentaje planeado a cubrir del tema en una sesion*/
        private javax.swing.JTextField txtPorcen;
        /** Boton para quitar este tema de los temas programados*/
        private javax.swing.JButton btnQuita;
        /** Cuando es true indica que se debe ignorar el itemevento del control jcbTema*/
        public boolean bandera=false;
        /** Indica si el tema actual ya esta registrado en la bd (como programado para esta sesion)*/
        public boolean enBD;
        
        /** Crea un nuevo control Tema*/
        public Tema(){
            jcbTema=new javax.swing.JComboBox();
            jcbTema.addItemListener(new java.awt.event.ItemListener(){
                public void itemStateChanged(ItemEvent e) {
                    if(bandera) return;
                    setClaveTema(Integer.parseInt(temas[jcbTema.getSelectedIndex()][0]));
                    setCambios(true);
                }
            });
            txtPorcen=new javax.swing.JTextField(4);
            btnQuita=new javax.swing.JButton("Quitar");
            clvTem=-1; enBD=false;
            btnQuita.addActionListener(this);
            setLayout(new java.awt.BorderLayout(6,2));
            actualizaTemas();
            add(jcbTema,java.awt.BorderLayout.WEST);
            add(txtPorcen,java.awt.BorderLayout.CENTER);
            add(btnQuita,java.awt.BorderLayout.EAST);
            txtPorcen.getDocument().addDocumentListener(new ListenChanges());
        }
    
        /** Vuelve a cargar la lista del control jcbTema*/
        public void actualizaTemas(){
            jcbTema.removeAllItems();
            for(int i=0;i<temas.length;i++){
                jcbTema.addItem(temas[i][3]+") "+temas[i][4]);
            }
            bandera=true;
            jcbTema.setSelectedIndex(-1);
            bandera=false;
            clvTem=-1;
        }
        
        /** Obtiene la clave del tema actual
         * @return la clave del tema actual
         */
        public int getClaveTema(){
            return clvTem;
        }
     
        /** Establece la clave del tema actual
         * @param clv la clave a asignar al tema actual
         */
        public void setClaveTema(int clv){
            for(int i=0;i<temas.length;i++){
                if(temas[i][0].equals(""+clv)){
                    bandera=true;
                    jcbTema.setSelectedIndex(i);
                    bandera=false;
                    clvTem=clv;
                    return;
                }
            }
            clvTem=-1;
        }
   
        /** Establece una clave al tema actual e indica que es definitiva
         * @param c la clave a asignar al tema actual
         */
        public void setFinalClave(int c){
            setClaveTema(c);
            txtTema=new javax.swing.JTextField(30);
            txtTema.setText(""+jcbTema.getSelectedItem());
            remove(jcbTema);
            txtTema.setEditable(false);
            add(txtTema,java.awt.BorderLayout.WEST);
            enBD=true;
        }

        /** Regresa un boolean que indica si los datos actuales son validos
         * @return un boolean que indica si los datos actuales son validos
         */
        public boolean esValido(){
            int aux;
            try{ aux=Integer.parseInt(txtPorcen.getText().trim()); }
            catch(NumberFormatException nbfExc){aux=-1;}
            if(jcbTema.getSelectedIndex()<0 || aux<1 || aux>100){
                return false;
            }
            return true;
        }
     
        /** Obtiene el porcentaje planeado para el tema actual
         * @return el porcentaje planeado para el tema actual
         */
        public String getPorcentaje(){
            return txtPorcen.getText();
        }
   
        /** Establece el porcentaje planeado para el tema actual
         * @param por el porcentaje planeado para el tema actual
         */
        public void setPorcentaje(int por){
            txtPorcen.setText(""+por);
        }
  
        /** Obtiene un boolean que indica si el tema actual esta registrado en la 
         *   base de datos (como programado para esta sesion)
         * @return un boolean que indica si el tema actual esta registrado en la 
         *   base de datos (como programado para esta sesion
         */
        public boolean esEnBD(){ return enBD; }
       
        /** Borra el registro del tema actual (como programado para esta sesion de la bd)
         * @param e El ActionEvent que genero el evento
         */
        public void actionPerformed(ActionEvent e) {
            TipoRespuesta res;
            if(enBD){
                res=pideDesicion("Atención","<html>Si quita este tema se borraran los posibles avances registrados<br>Esta acción no se podra deshacer<br>¿Desea continuar?</html>");
                if(res.getTipo()==TipoRespuesta.ACEPTAR.getTipo()){
                    if(!Actualiza.actualiza("delete from vetema where clvtem="+clvTem+" and clvses="+claveses+";",false,true)){
                        muestraMensaje("Error al quitar el tema",Actualiza.obtenError(),TipoMensaje.ERROR);
                        return;
                    }
                    if(refCal!=null) refCal.actualiza();
                }
                else return;
            }
            quitaTema(this);
        }
    }
    
    /** Crea un control (hereda de jpanel) para manejar los datos de un objetivo programado para una sesion */
    private class Objetivo extends javax.swing.JPanel implements java.awt.event.ActionListener{
        
        /**Clave del objetivo actual*/
        private int clvObj;
        /**Descripcion del objetivo actual*/
        private javax.swing.JTextField txtDesc;
        /** Boton para quitar este objetivo de los objetivos programados*/
        private javax.swing.JButton btnQuita;
        
        /** Crea un nuevo control objetivo*/
        public Objetivo(){
            txtDesc=new javax.swing.JTextField(30);
            btnQuita=new javax.swing.JButton("Quitar");
            clvObj=-1;
            btnQuita.addActionListener(this);
            setLayout(new java.awt.BorderLayout(6,2));
            add(txtDesc,java.awt.BorderLayout.CENTER);
            add(btnQuita,java.awt.BorderLayout.EAST);
            txtDesc.getDocument().addDocumentListener(new ListenChanges());
        }
        
        /** Crea un nuevo control objetivo
         * @param clave La clave del ojetivo
         * @param descrip La descripcion del objetivo
         */
        public Objetivo(int clave,String descrip){
            txtDesc=new javax.swing.JTextField(30);
            txtDesc.setText(descrip);
            btnQuita=new javax.swing.JButton("Quitar");
            clvObj=clave;
            btnQuita.addActionListener(this);
            setLayout(new java.awt.BorderLayout(6,2));
            add(txtDesc,java.awt.BorderLayout.CENTER);
            add(btnQuita,java.awt.BorderLayout.EAST);
            txtDesc.getDocument().addDocumentListener(new ListenChanges());
        }
      
        /** Obtiene la clave del objetivo actual
         * @return la clave del objetivo actual
         */
        public int getClaveObj(){ return clvObj; }
        
        /** Establece la clave del objetivo actual
         * @param clv la clave del objetivo actual
         */
        public void setClaveObj(int clv){ clvObj=clv; }

        /** Obtiene un boolean que indica si los datos del objetivo actual son validos
         * @return un boolean que indica si los datos del objetivo actual son validos
         */
        public boolean esValido(){
            if(txtDesc.getText().length()<1 || txtDesc.getText().length()>255){
                return false;
            }
            return true;
        }
      
        /** Obtiene la descripcion del objetivo actual
         * @return la descripcion del objetivo actual
         */
        public String getDescripcion(){
            return txtDesc.getText().trim();
        }
     
        /** Elimina el objetivo actual de los objetivos programados para la sesion actual
         * @param e El ActionCommand que genero el evento
         */
        public void actionPerformed(ActionEvent e) {
            TipoRespuesta res;
            if(clvObj!=-1){
                res=pideDesicion("Atención","<html>Si quita este objetivo se borraran los posibles avances registrados<br>Esta acción no se podra deshacer<br>¿Desea continuar?</html>");
                if(res.getTipo()==TipoRespuesta.ACEPTAR.getTipo()){
                    if(!Actualiza.actualiza("delete from objetivos where clvobj="+clvObj+";",false,true)){
                        muestraMensaje("Error al quitar el objetivo",Actualiza.obtenError(),TipoMensaje.ERROR);
                        return;
                    }
                    if(refCal!=null) refCal.actualiza();
                }
                else return;
            }
            quitaObjetivo(this);
        }
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
         * y avisa al FrmRegSes que hay cambios sin guardar
         * @param e El DocumentEvent que genero el evento
         */
        public void insertUpdate(javax.swing.event.DocumentEvent e){ 
            setCambios(true);
        }
        /** Metodo de la interfaz DocumentListener
         * detecta si se quito contenido al documento (contenido del control de texto)
         * y avisa al FrmRegSes que hay cambios sin guardar
         * @param e El DocumentEvent que genero el evento
         */
        public void removeUpdate(javax.swing.event.DocumentEvent e){ 
            setCambios(true);
        }
        /** Metodo de la interfaz DocumentListener
         * detecta si cambio el contenido del documento (contenido del control de texto)
         * @param e El DocumentEvent que genero el evento
         */
        public void changedUpdate(javax.swing.event.DocumentEvent e){}
    }
}
