/*
 * FramePrincipal.java
 *   Ventana principal del sistema
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
import database.Consultas;
import java.awt.Dimension;
import java.awt.event.*;
import java.util.ArrayList;
import java.util.Map;
import javax.swing.JFileChooser;
import javax.swing.JOptionPane;
import javax.swing.JRootPane;
import definiciones.TipoMensaje;
import definiciones.TipoRespuesta;
import iconos.Iconos;
import java.io.File;
import operaciones.Archivos;
import operaciones.Datos;
import operaciones.Tiempo;
import reportes.FrameReporte;

/** Ventana principal del sistema
 * 
 * @author Pedro Cardoso Rodríguez
 */
public class FramePrincipal extends javax.swing.JFrame implements ActionListener{
    
    /**Indica la version actual del sistema*/
    public final float SIS_VER=0.75f;
    /**hilo utilizado para crear un objeto ActuHoraStatConx*/
    private Thread hilo;
    /**Referencia a una ventana de tipo FrameBusquedas*/
    private FrameBusquedas resbus;
    /**Referencia a una ventana de tipo FrameAyuda*/
    private FrameAyuda ayuda;
    /**Referencia a una ventana de tipo FrameImprimir*/
    private FrameImprimir impres;
    
    /**Crea un nuevo FramePrincipal*/
    public FramePrincipal() {
        Thread.setDefaultUncaughtExceptionHandler(new catchUnhandledErrors(this));
        String[] prefEst=cargaPrefEst();
        boolean aux;
        if(prefEst!=null) aux=prefEst[0].equals("true");
        else aux=true;
        setUndecorated(aux);
        javax.swing.JFrame.setDefaultLookAndFeelDecorated(aux);
        javax.swing.JDialog.setDefaultLookAndFeelDecorated(aux);
        if(aux) getRootPane().setWindowDecorationStyle(JRootPane.FRAME);
        else
            try{ javax.swing.UIManager.setLookAndFeel(javax.swing.UIManager.getSystemLookAndFeelClassName()); }
            catch(Exception exc){}
        initComponents();                                
        setIconImage(Iconos.getIcono("aplicacion.png").getImage());
        lblCon.setText("");
        lblAvisos.setText("");
        lblAvisos.setIcon(Iconos.getIcono("avisos.png"));
        hilo = new ActuHoraStatConx();
        hilo.start();
        conecServidor();
        database.Conexion.setRefPrinc(this);
        if(prefEst!=null){
            setSize(Integer.parseInt(prefEst[1]), Integer.parseInt(prefEst[2]));
            setLocation(Integer.parseInt(prefEst[3]), Integer.parseInt(prefEst[4]));
        }
        else{
            setSize(800,650); setLocationRelativeTo(null);
        }
        setVisible(true);
        if(database.Conexion.hayConexion()) muestraAvisos(false);
        ayuda=null; impres=null;
    }
    
    /** Revisa si existe el archivo prmscnx (parametros de conexion) y si 
     *   lo encuentra lo lee y trata de conectarse al servidor 
     */
    private void conecServidor(){        
        String dirBase=System.getProperty("user.dir");
        String[] prms = operaciones.Archivos.leeBin(dirBase+File.separator+"lib"+File.separator+"prmscnx",4);
        if(prms==null)
            mensaje("Error al intentar conectar al servidor",operaciones.Archivos.obtenError(),TipoMensaje.ERROR);
        else if(prms[0]!=null){
            database.Conexion.estableceParms(prms);
            if(!database.Conexion.conecta()) 
                mensaje("Error al intentar conectar al servidor",database.Conexion.obtenError(),TipoMensaje.ERROR);
        }
    }
  
    /** Carga los resultados de una busqueda que regreso multiples registros
     * @param modTabla El model obtenido en la consulta a la base de datos
     * @param titulo El titulo de la busqueda
     * @param desc La descripcion de la busqueda
     * @param acols El ancho preferido de las columnas de la tabla a mostrar
     * @param mnus las acciones del popupmenu emergente de la tabla a mostrar
     * @param isAl Indica si los registros encontrados son de alumnos
     */
    public void cargaBusqueda(javax.swing.table.DefaultTableModel modTabla, String titulo, String desc,String[] acols,String[] mnus,boolean isAl){
        if(!existeVentana("Resultado de búsquedas",true)){
          resbus = new FrameBusquedas(this);          
          jdpEscritorio.add(resbus);
          resbus.setVisible(true);
          resbus.setLocation(0,0);          
          resbus.empaqueta();
          javax.swing.JMenuItem mnuTmp = new javax.swing.JMenuItem("Resultado de búsquedas");
          mnuTmp.setActionCommand("Resultado de búsquedas");
          mnuTmp.addActionListener(this);
          mnuVentana.add(mnuTmp);
          mnuVentana.setEnabled(true);
        }
        resbus.cargaResultados(modTabla,titulo,desc,acols,mnus,isAl);
    }
    
    /** Carga y regresa los parametros de preferencia de la ventana (estilo, size y location)
     * @return los parametros de preferencia de la ventana (estilo, size y location)
     */
    public String[] cargaPrefEst(){
        String dirBase=System.getProperty("user.dir");
        String[] prms = Archivos.leeBin(dirBase+File.separator+"lib"+File.separator+"prmsprf",6);
        if(prms==null)
            mensaje("Error al intentar cargar las preferencias",Archivos.obtenError(),TipoMensaje.ERROR);
        if(prms[0]==null) return null;
        return prms;
    }
    
    /** Guarda los parametros de preferencia elegidos
     * @param prefsEst Preferencia del estilo o recibe prefEst="ignora" cuando debe conservar el del archivo
     * @param rutaLogo Ruta al logo a utilizar en impresion de reportes
     * @return true si los datos se guardaron corectamente false en caso contrario
     */
    public boolean guardaPrefEst(String prefsEst,String rutaLogo){
        String[] prefs=cargaPrefEst();
        String dirBase=System.getProperty("user.dir");
        Dimension dim=getSize();
        java.awt.geom.Point2D lugar = getLocation();
        if(prefs==null){
            prefs=new String[6];
            prefs[0]=""+this.isUndecorated();
            prefs[5]="";
        }
        if(!prefsEst.equals("ignora")) prefs[0]=prefsEst;
        if(!rutaLogo.equals("")) prefs[5]=rutaLogo;
        prefs[1]=""+dim.width;
        prefs[2]=""+dim.height;
        prefs[3]=""+(int)lugar.getX();
        prefs[4]=""+(int)lugar.getY();
        if(Archivos.escribeBin(dirBase+File.separator+"lib"+File.separator+"prmsprf", prefs)) return true;        
        return false;
    }
    
    /** Consulta en la bd si hay avisos o recordatorios para mostrar 
     * @param forzar Indica que se debe mostrar la ventana de avisos inmediatamente 
     *   aun cuando este configurado que no se debe mostrar
     */
    private void muestraAvisos(boolean forzar){
        String[] prefs=Consultas.consultaUnCampo("select * from dtspref;",false);
        if(prefs==null){ 
            mensaje("Error al consultar la base de datos",Consultas.obtenError(),TipoMensaje.ERROR);
            return;
        }
        if(prefs[0].equals("false")&&!forzar) return;
        FrameAvisos avis=(FrameAvisos)obtenVentana("Avisos del sistema");        
        javax.swing.JMenuItem mnuTmp;
        String fecha;        
        String sentencia;
        javax.swing.table.DefaultTableModel consAvisos;
        java.util.ArrayList<String> lista = new java.util.ArrayList<String>();
        if(prefs==null){
            mensaje("Error","No hay parametros especificados para buscar avisos",TipoMensaje.ERROR);
            return;
        }        
        fecha=Tiempo.getFechaFormatoNums();
        fecha=Datos.transformatFcha(fecha);
        // buscar si hay sesorias programadas para hoy o en los dos dias siguientes
        if(prefs[1].equals("true")){
            sentencia="select appat, apmat, nom, fechayhora, alumno.matricula, ";
            sentencia+=" sestesis.clvsest from alumno, setitula, progsest, sestesis ";
            sentencia+="where datediff(fechayhora,'"+fecha+"')>=0 and datediff(fechayhora,'"+fecha+"')<="+Integer.parseInt(prefs[2]);
            sentencia+=" and sestesis.clvsest=progsest.clvsest and progsest.matricula=setitula.matricula ";
            sentencia+="and setitula.matricula=alumno.matricula order by fechayhora;";
            consAvisos=Consultas.consTipoTable(sentencia,false);
            if(consAvisos!=null && consAvisos.getRowCount()>0){
                lista.add(" &gt;&gt; Proximas sesiones de aseroria con alumnos tesistas");
                for(int y=0;y<consAvisos.getRowCount();y++){
                    sentencia=""+consAvisos.getValueAt(y,0)+" "+consAvisos.getValueAt(y,1)+" ";
                    sentencia+=consAvisos.getValueAt(y,2)+". Fecha y hora: "+consAvisos.getValueAt(y,3);
                    sentencia+="%ASE%"+consAvisos.getValueAt(y,4)+"%"+consAvisos.getValueAt(y,5);
                    lista.add(sentencia);
                }
            }
        }
        // buscar si hay clases programadas para hoy o en los dos dias siguientes
        if(prefs[3].equals("true")){
            sentencia="select grado, grupo, nombre, fechayhora, grupos.clvg, ";
            sentencia+="sesiones.clvses from sesiones, progses, grupos, ";
            sentencia+="imparte, materias where datediff(fechayhora,'"+fecha+"')<="+Integer.parseInt(prefs[4]);
            sentencia+=" and datediff(fechayhora,'"+fecha+"')>=0 and sesiones.clvses=";
            sentencia+="progses.clvses and progses.clvg=grupos.clvg and grupos.clvg=";
            sentencia+="imparte.clvg and imparte.clvm=materias.clvm order by fechayhora;";
            consAvisos=Consultas.consTipoTable(sentencia,false);
            if(consAvisos!=null && consAvisos.getRowCount()>0){
                lista.add(" &gt;&gt; Proximas clases programadas");
                for(int y=0;y<consAvisos.getRowCount();y++){
                    sentencia=""+consAvisos.getValueAt(y,0)+consAvisos.getValueAt(y,1);
                    sentencia+=" "+consAvisos.getValueAt(y,2)+". Fecha y hora: "+consAvisos.getValueAt(y,3);
                    sentencia+="%CLA%"+consAvisos.getValueAt(y,4)+"%"+consAvisos.getValueAt(y,5);
                    lista.add(sentencia);
                }
            }
        }
        if(avis==null) avis = new FrameAvisos(this);
        avis.cargaAvisos(lista);
        if(this.existeVentana("Avisos del sistema",true)) return;
        jdpEscritorio.add(avis);
        avis.setVisible(true);       
        mnuTmp = new javax.swing.JMenuItem(avis.getTitle());
        mnuTmp.setActionCommand(avis.getTitle());
        mnuTmp.addActionListener(this);
        mnuVentana.add(mnuTmp);
        mnuVentana.setEnabled(true);
    }
    
    /** Despliega un mensaje
     * @param titulo El titulo del mensaje
     * @param mensaje El texto de mensaje
     * @param tp El tipo de mensaje
     */
    public void mensaje(String titulo, String mensaje, TipoMensaje tp){
        JOptionPane.showMessageDialog(this, mensaje, titulo, tp.getTipo());
    }
    
    /** Pide un valor entero por medio de un joptionpane
     * @param titulo El titulo a mostrar en el cuadro de dialogo
     * @param mensaje El mensaje a mostrar en el cuadro de dialogo
     * @return El entero ingresado si se ingresan caracteres regresa 0
     */
    public int pideEntero(String titulo, String mensaje){
        int valor=0;
        String cad=JOptionPane.showInputDialog(this,mensaje,titulo,JOptionPane.QUESTION_MESSAGE);        
        try{
            valor=Integer.parseInt(cad);
        }
        catch(NumberFormatException nbfExc){}
        return valor;
    }
    
    /** Pide una string por medio de un joptionpane
     * @param titulo El titulo a mostrar en el cuadro de dialogo
     * @param mensaje El mensaje a mostrar en el cuadro de dialogo
     * @return La cadena ingresada por el usuario
     */
    public String pideString(String titulo, String mensaje){
        String cad=JOptionPane.showInputDialog(this,mensaje,titulo,JOptionPane.QUESTION_MESSAGE);        
        return cad;
    }
    
    /** Pide una desicion al usuario (Si, No, Cancelar)
     * @param titulo El titulo a mostrar en el cuadro de dialogo
     * @param mensaje El mensaje a mostrar en el cuadro de dialogo
     * @return El TipoRespuesta con la desicion tomada por el usuario
     */
    public TipoRespuesta pideDesicion(String titulo, String mensaje){
        int res=JOptionPane.showConfirmDialog(this,mensaje,titulo,JOptionPane.YES_NO_CANCEL_OPTION);
        if(res==JOptionPane.YES_OPTION) return TipoRespuesta.ACEPTAR;
        else if(res==JOptionPane.NO_OPTION) return TipoRespuesta.RECHAZAR;
        return TipoRespuesta.CANCELAR;
    }
    
    /** Pide un password al usuario (para el password de seguridad en acciones que lo requieran)
     * @param acc La descripcion de la accion que lo solicita
     * @return El password ingresado por el usuario
     */
    public String pidePssSeg(String acc){
        DialogoAutentifica aut = new DialogoAutentifica(this,true,acc);
        aut.setVisible(true);
        return aut.getPsg();
    }
    
    /** This method is called from within the constructor to
     * initialize the form.
     * WARNING: Do NOT modify this code. The content of this method is
     * always regenerated by the Form Editor.
     */
    // <editor-fold defaultstate="collapsed" desc="Generated Code">//GEN-BEGIN:initComponents
    private void initComponents() {

        statusBar = new javax.swing.JPanel();
        lblMens = new javax.swing.JLabel();
        lblFcha = new javax.swing.JLabel();
        jPanel1 = new javax.swing.JPanel();
        lblHora = new javax.swing.JLabel();
        lblCon = new javax.swing.JLabel();
        lblAvisos = new javax.swing.JLabel();
        jdpEscritorio = new javax.swing.JDesktopPane();
        jMenuBar1 = new javax.swing.JMenuBar();
        mnuSistema = new javax.swing.JMenu();
        mnuConex = new javax.swing.JMenuItem();
        mnuSeguridad = new javax.swing.JMenuItem();
        mnuDtsDoc = new javax.swing.JMenuItem();
        mnuHoraSem = new javax.swing.JMenuItem();
        mnuPref = new javax.swing.JMenuItem();
        mnuSalir = new javax.swing.JMenuItem();
        mnuAlumnos = new javax.swing.JMenu();
        mnuGrupos = new javax.swing.JMenu();
        mnuNewGrupo = new javax.swing.JMenuItem();
        mnuBuscGrupo = new javax.swing.JMenuItem();
        mnuFichGru = new javax.swing.JMenuItem();
        mnuCal = new javax.swing.JMenuItem();
        mnuRubEval = new javax.swing.JMenuItem();
        mnuAlumnos2 = new javax.swing.JMenu();
        mnuAltaAl = new javax.swing.JMenuItem();
        mnuBusAl = new javax.swing.JMenuItem();
        mnuRegPer = new javax.swing.JMenuItem();
        mnuRegDA = new javax.swing.JMenuItem();
        mnuRegDG = new javax.swing.JMenuItem();
        mnuAsesorias = new javax.swing.JMenuItem();
        mnuReportes = new javax.swing.JMenu();
        mnuRepGrupo = new javax.swing.JMenuItem();
        mnuRepMateria = new javax.swing.JMenuItem();
        mnuBiblioteca = new javax.swing.JMenu();
        mnuNuevaFichaBib = new javax.swing.JMenuItem();
        mnuBuscaFichBib = new javax.swing.JMenuItem();
        mnuVentana = new javax.swing.JMenu();
        mnuAyuda = new javax.swing.JMenu();
        mnuManual = new javax.swing.JMenuItem();
        mnuAcercaDe = new javax.swing.JMenuItem();

        setDefaultCloseOperation(javax.swing.WindowConstants.DO_NOTHING_ON_CLOSE);
        setTitle("SADAA");
        setCursor(new java.awt.Cursor(java.awt.Cursor.DEFAULT_CURSOR));
        addWindowListener(new java.awt.event.WindowAdapter() {
            public void windowClosing(java.awt.event.WindowEvent evt) {
                formWindowClosing(evt);
            }
        });

        statusBar.setLayout(new java.awt.BorderLayout());

        lblMens.setHorizontalAlignment(javax.swing.SwingConstants.CENTER);
        lblMens.setText("Bienvenido al sistema");
        statusBar.add(lblMens, java.awt.BorderLayout.CENTER);

        lblFcha.setText("Fecha");
        statusBar.add(lblFcha, java.awt.BorderLayout.LINE_START);

        jPanel1.setLayout(new java.awt.BorderLayout(12, 0));

        lblHora.setText("Hora");
        jPanel1.add(lblHora, java.awt.BorderLayout.EAST);

        lblCon.setText("Conexion");
        lblCon.addMouseListener(new java.awt.event.MouseAdapter() {
            public void mouseClicked(java.awt.event.MouseEvent evt) {
                lblConMouseClicked(evt);
            }
        });
        jPanel1.add(lblCon, java.awt.BorderLayout.CENTER);

        lblAvisos.setText("Avisos");
        lblAvisos.setToolTipText("Avisos del sistema");
        lblAvisos.addMouseListener(new java.awt.event.MouseAdapter() {
            public void mouseClicked(java.awt.event.MouseEvent evt) {
                lblAvisosMouseClicked(evt);
            }
        });
        jPanel1.add(lblAvisos, java.awt.BorderLayout.WEST);

        statusBar.add(jPanel1, java.awt.BorderLayout.LINE_END);

        getContentPane().add(statusBar, java.awt.BorderLayout.PAGE_END);

        jdpEscritorio.setBackground(javax.swing.UIManager.getDefaults().getColor("tab_sel_fill"));
        getContentPane().add(jdpEscritorio, java.awt.BorderLayout.CENTER);

        mnuSistema.setMnemonic('S');
        mnuSistema.setText("Sistema");

        mnuConex.setMnemonic('C');
        mnuConex.setText("Conexión al servidor");
        mnuConex.addActionListener(new java.awt.event.ActionListener() {
            public void actionPerformed(java.awt.event.ActionEvent evt) {
                mnuConexActionPerformed(evt);
            }
        });
        mnuSistema.add(mnuConex);

        mnuSeguridad.setMnemonic('R');
        mnuSeguridad.setText("Seguridad");
        mnuSeguridad.addActionListener(new java.awt.event.ActionListener() {
            public void actionPerformed(java.awt.event.ActionEvent evt) {
                mnuSeguridadActionPerformed(evt);
            }
        });
        mnuSistema.add(mnuSeguridad);

        mnuDtsDoc.setMnemonic('D');
        mnuDtsDoc.setText("Datos del docente");
        mnuDtsDoc.addActionListener(new java.awt.event.ActionListener() {
            public void actionPerformed(java.awt.event.ActionEvent evt) {
                mnuDtsDocActionPerformed(evt);
            }
        });
        mnuSistema.add(mnuDtsDoc);

        mnuHoraSem.setText("Horario semanal vigente");
        mnuHoraSem.addActionListener(new java.awt.event.ActionListener() {
            public void actionPerformed(java.awt.event.ActionEvent evt) {
                mnuHoraSemActionPerformed(evt);
            }
        });
        mnuSistema.add(mnuHoraSem);

        mnuPref.setMnemonic('E');
        mnuPref.setText("Preferencias");
        mnuPref.addActionListener(new java.awt.event.ActionListener() {
            public void actionPerformed(java.awt.event.ActionEvent evt) {
                mnuPrefActionPerformed(evt);
            }
        });
        mnuSistema.add(mnuPref);

        mnuSalir.setAccelerator(javax.swing.KeyStroke.getKeyStroke(java.awt.event.KeyEvent.VK_S, java.awt.event.InputEvent.CTRL_MASK));
        mnuSalir.setMnemonic('S');
        mnuSalir.setText("Salir");
        mnuSalir.addActionListener(new java.awt.event.ActionListener() {
            public void actionPerformed(java.awt.event.ActionEvent evt) {
                mnuSalirActionPerformed(evt);
            }
        });
        mnuSistema.add(mnuSalir);

        jMenuBar1.add(mnuSistema);

        mnuAlumnos.setMnemonic('A');
        mnuAlumnos.setText("Alumnos");

        mnuGrupos.setMnemonic('G');
        mnuGrupos.setText("Grupos");

        mnuNewGrupo.setMnemonic('n');
        mnuNewGrupo.setText("Nuevo grupo");
        mnuNewGrupo.addActionListener(new java.awt.event.ActionListener() {
            public void actionPerformed(java.awt.event.ActionEvent evt) {
                mnuNewGrupoActionPerformed(evt);
            }
        });
        mnuGrupos.add(mnuNewGrupo);

        mnuBuscGrupo.setAccelerator(javax.swing.KeyStroke.getKeyStroke(java.awt.event.KeyEvent.VK_U, java.awt.event.InputEvent.CTRL_MASK));
        mnuBuscGrupo.setMnemonic('b');
        mnuBuscGrupo.setText("Buscar");
        mnuBuscGrupo.addActionListener(new java.awt.event.ActionListener() {
            public void actionPerformed(java.awt.event.ActionEvent evt) {
                mnuBuscGrupoActionPerformed(evt);
            }
        });
        mnuGrupos.add(mnuBuscGrupo);

        mnuFichGru.setAccelerator(javax.swing.KeyStroke.getKeyStroke(java.awt.event.KeyEvent.VK_G, java.awt.event.InputEvent.CTRL_MASK));
        mnuFichGru.setMnemonic('F');
        mnuFichGru.setText("Ficha de grupo");
        mnuFichGru.addActionListener(new java.awt.event.ActionListener() {
            public void actionPerformed(java.awt.event.ActionEvent evt) {
                mnuFichGruActionPerformed(evt);
            }
        });
        mnuGrupos.add(mnuFichGru);

        mnuCal.setAccelerator(javax.swing.KeyStroke.getKeyStroke(java.awt.event.KeyEvent.VK_M, java.awt.event.InputEvent.CTRL_MASK));
        mnuCal.setMnemonic('C');
        mnuCal.setText("Calendario de grupo");
        mnuCal.addActionListener(new java.awt.event.ActionListener() {
            public void actionPerformed(java.awt.event.ActionEvent evt) {
                mnuCalActionPerformed(evt);
            }
        });
        mnuGrupos.add(mnuCal);

        mnuRubEval.setMnemonic('R');
        mnuRubEval.setText("Rubros de evaluación");
        mnuRubEval.addActionListener(new java.awt.event.ActionListener() {
            public void actionPerformed(java.awt.event.ActionEvent evt) {
                mnuRubEvalActionPerformed(evt);
            }
        });
        mnuGrupos.add(mnuRubEval);

        mnuAlumnos.add(mnuGrupos);

        mnuAlumnos2.setMnemonic('L');
        mnuAlumnos2.setText("Alumnos");

        mnuAltaAl.setMnemonic('T');
        mnuAltaAl.setText("Alta de alumno");
        mnuAltaAl.addActionListener(new java.awt.event.ActionListener() {
            public void actionPerformed(java.awt.event.ActionEvent evt) {
                mnuAltaAlActionPerformed(evt);
            }
        });
        mnuAlumnos2.add(mnuAltaAl);

        mnuBusAl.setAccelerator(javax.swing.KeyStroke.getKeyStroke(java.awt.event.KeyEvent.VK_B, java.awt.event.InputEvent.CTRL_MASK));
        mnuBusAl.setMnemonic('b');
        mnuBusAl.setText("Buscar");
        mnuBusAl.addActionListener(new java.awt.event.ActionListener() {
            public void actionPerformed(java.awt.event.ActionEvent evt) {
                mnuBusAlActionPerformed(evt);
            }
        });
        mnuAlumnos2.add(mnuBusAl);

        mnuRegPer.setAccelerator(javax.swing.KeyStroke.getKeyStroke(java.awt.event.KeyEvent.VK_R, java.awt.event.InputEvent.CTRL_MASK));
        mnuRegPer.setMnemonic('P');
        mnuRegPer.setText("Registro personal de alumno");
        mnuRegPer.addActionListener(new java.awt.event.ActionListener() {
            public void actionPerformed(java.awt.event.ActionEvent evt) {
                mnuRegPerActionPerformed(evt);
            }
        });
        mnuAlumnos2.add(mnuRegPer);

        mnuRegDA.setMnemonic('c');
        mnuRegDA.setText("Registro desempeño académico");
        mnuRegDA.addActionListener(new java.awt.event.ActionListener() {
            public void actionPerformed(java.awt.event.ActionEvent evt) {
                mnuRegDAActionPerformed(evt);
            }
        });
        mnuAlumnos2.add(mnuRegDA);

        mnuRegDG.setAccelerator(javax.swing.KeyStroke.getKeyStroke(java.awt.event.KeyEvent.VK_D, java.awt.event.InputEvent.CTRL_MASK));
        mnuRegDG.setMnemonic('g');
        mnuRegDG.setText("Registro desempeño dentro de un grupo");
        mnuRegDG.addActionListener(new java.awt.event.ActionListener() {
            public void actionPerformed(java.awt.event.ActionEvent evt) {
                mnuRegDGActionPerformed(evt);
            }
        });
        mnuAlumnos2.add(mnuRegDG);

        mnuAlumnos.add(mnuAlumnos2);

        mnuAsesorias.setAccelerator(javax.swing.KeyStroke.getKeyStroke(java.awt.event.KeyEvent.VK_O, java.awt.event.InputEvent.CTRL_MASK));
        mnuAsesorias.setMnemonic('S');
        mnuAsesorias.setText("Asesorías");
        mnuAsesorias.addActionListener(new java.awt.event.ActionListener() {
            public void actionPerformed(java.awt.event.ActionEvent evt) {
                mnuAsesoriasActionPerformed(evt);
            }
        });
        mnuAlumnos.add(mnuAsesorias);

        jMenuBar1.add(mnuAlumnos);

        mnuReportes.setMnemonic('R');
        mnuReportes.setText("Reportes");

        mnuRepGrupo.setText("Por grupo");
        mnuRepGrupo.addActionListener(new java.awt.event.ActionListener() {
            public void actionPerformed(java.awt.event.ActionEvent evt) {
                mnuRepGrupoActionPerformed(evt);
            }
        });
        mnuReportes.add(mnuRepGrupo);

        mnuRepMateria.setText("Por materia");
        mnuRepMateria.addActionListener(new java.awt.event.ActionListener() {
            public void actionPerformed(java.awt.event.ActionEvent evt) {
                mnuRepMateriaActionPerformed(evt);
            }
        });
        mnuReportes.add(mnuRepMateria);

        jMenuBar1.add(mnuReportes);

        mnuBiblioteca.setMnemonic('B');
        mnuBiblioteca.setText("Catalogo bibliográfico");

        mnuNuevaFichaBib.setMnemonic('N');
        mnuNuevaFichaBib.setText("Nueva Ficha");
        mnuNuevaFichaBib.addActionListener(new java.awt.event.ActionListener() {
            public void actionPerformed(java.awt.event.ActionEvent evt) {
                mnuNuevaFichaBibActionPerformed(evt);
            }
        });
        mnuBiblioteca.add(mnuNuevaFichaBib);

        mnuBuscaFichBib.setAccelerator(javax.swing.KeyStroke.getKeyStroke(java.awt.event.KeyEvent.VK_L, java.awt.event.InputEvent.CTRL_MASK));
        mnuBuscaFichBib.setMnemonic('B');
        mnuBuscaFichBib.setText("Buscar Ficha");
        mnuBuscaFichBib.addActionListener(new java.awt.event.ActionListener() {
            public void actionPerformed(java.awt.event.ActionEvent evt) {
                mnuBuscaFichBibActionPerformed(evt);
            }
        });
        mnuBiblioteca.add(mnuBuscaFichBib);

        jMenuBar1.add(mnuBiblioteca);

        mnuVentana.setMnemonic('V');
        mnuVentana.setText("Ventana");
        mnuVentana.setEnabled(false);
        jMenuBar1.add(mnuVentana);

        mnuAyuda.setMnemonic('Y');
        mnuAyuda.setText("Ayuda");

        mnuManual.setAccelerator(javax.swing.KeyStroke.getKeyStroke(java.awt.event.KeyEvent.VK_F1, 0));
        mnuManual.setMnemonic('U');
        mnuManual.setText("Manual de usuario");
        mnuManual.addActionListener(new java.awt.event.ActionListener() {
            public void actionPerformed(java.awt.event.ActionEvent evt) {
                mnuManualActionPerformed(evt);
            }
        });
        mnuAyuda.add(mnuManual);

        mnuAcercaDe.setMnemonic('D');
        mnuAcercaDe.setText("Acerca de ");
        mnuAcercaDe.addActionListener(new java.awt.event.ActionListener() {
            public void actionPerformed(java.awt.event.ActionEvent evt) {
                mnuAcercaDeActionPerformed(evt);
            }
        });
        mnuAyuda.add(mnuAcercaDe);

        jMenuBar1.add(mnuAyuda);

        setJMenuBar(jMenuBar1);

        pack();
    }// </editor-fold>//GEN-END:initComponents

    /** Llama al evento que abre la ventana para registrar nuevos alumnos
     * @param evt El ActionEvent que genero el evento
     */
    private void mnuAltaAlActionPerformed(java.awt.event.ActionEvent evt) {//GEN-FIRST:event_mnuAltaAlActionPerformed
        if(!existeVentana("Alta de nuevos alumnos",true)){
            agregaVentana(new alumnos.FrameAltas(this));
        }
    }//GEN-LAST:event_mnuAltaAlActionPerformed

    /** Abre la ventana para ver el registro personal de un alumno
     * @param evt El ActionEvent que genero el evento
     */
    private void mnuRegPerActionPerformed(java.awt.event.ActionEvent evt) {//GEN-FIRST:event_mnuRegPerActionPerformed
        agregaVentanaAlumno(null,1);
}//GEN-LAST:event_mnuRegPerActionPerformed

    /** Carga una ventana con una ficha bibliografica
     * @param clave la clave de la ficha bibliografica o 0 para nueva
     * @param tipo el tipo de la ficha a cargar donde: 
     *   0=nueva, 1=ficha general, 2=ficha hemeroteca, 3=ficha hemeroteca analitica
     */
    public void agregaFichaBiliografica(int clave, int tipo){
        ModeloFrameInterno ventana=null;
        String titulo;
        if(tipo==1){ titulo="Ficha bibliográfica general"+(clave!=0?" "+clave:""); }
        else if(tipo==2){ titulo="Ficha hemeroteca general"+(clave!=0?" "+clave:""); }
        else if(tipo==3){ titulo="Ficha hemeroteca analítica"+(clave!=0?" "+clave:""); }
        else{ titulo="Nueva ficha bibliográfica"; }
        if(!existeVentana(titulo,true)){
            ventana=new bibliografia.FrameFicha(this);
            if(clave!=0){
                if(!((bibliografia.FrameFicha)ventana).cargaDatos(clave, tipo)){
                    return;
                }
            }
            agregaVentana(ventana);
        }
    }
    
    /** Agrega una Frame de datos de un grupo
     * @param clave la clave del grupo a cargar o null para ventana sin datos
     * @param tipo el tipo de frame donde: 
     *   1=ficha de grupo, 2=calendario de grupo y 3=rubros de evaluacion de grupo
     */
    public void agregaVentanaGrupo(String clave, int tipo){
        ModeloFrameInterno ventana=null;
        String titulo="";
        if(tipo==1){ titulo="Ficha de grupo"+(clave!=null?" "+clave:""); }
        else if(tipo==2){ titulo="Calendario de grupo"+(clave!=null?" "+clave:""); }
        else if(tipo==3){ titulo="Rubros de evaluación para grupo"+(clave!=null?" "+clave:""); }
        if(!existeVentana(titulo,true)){
            if(tipo==1){
                ventana=new grupos.FrameGrupo(this);
                if(clave!=null){
                    if(!((grupos.FrameGrupo)ventana).cargaDatosGrupo(clave)){
                        return;
                    }
                }
            }
            else if(tipo==2){
                ventana=new grupos.FrameCalendario(this);
                if(clave!=null){
                    if(!((grupos.FrameCalendario)ventana).cargaDatosGrupo(clave)){
                        return;
                    }
                }
            }
            else if(tipo==3){
                ventana=new grupos.FrameEvaluaciones(this);
                if(clave!=null){
                    if(!((grupos.FrameEvaluaciones)ventana).cargaDatosGrupo(clave)){
                        return;
                    }
                }
            }
            agregaVentana(ventana);
        }        
    }
    
    /** Agrega una frame de datos de alumno
     * @param matricula la matricula del alumno o null para ventana sin datos
     * @param tipo el tipo de ventana donde:
     *   1=registro personal de alumno, 2=registro personal de desempeño academico
     *   3=registro de desempeño en grupo, 4=asesorias
     */
    public void agregaVentanaAlumno(String matricula, int tipo){
        ModeloFrameInterno ventana=null;
        String titulo="";
        if(tipo==1){ titulo="Registro personal de alumno"+(matricula!=null?" "+matricula:""); }
        else if(tipo==2){ titulo="Registro personal de desempeño académico"+(matricula!=null?" alumno "+matricula:""); }
        else if(tipo==3){ titulo="Registro personal de desempeño en grupo"+(matricula!=null?" alumno "+matricula:""); }
        else if(tipo==4){ titulo="Ficha de asesorías"+(matricula!=null?" alumno "+matricula:""); }
        if(!existeVentana(titulo,true)){
            if(tipo==1){
                ventana=new alumnos.FramePersonal(this);
                if(matricula!=null){
                    if(!((alumnos.FramePersonal)ventana).cargaDatosAlumno(matricula)){
                        return;
                    }
                }
            }
            else if(tipo==2){
                ventana=new alumnos.FrameDesAca(this);
                if(matricula!=null){
                    if(!((alumnos.FrameDesAca)ventana).cargaDatosAlumno(matricula)){
                        return;
                    }
                }
            }
            else if(tipo==3){
                ventana=new alumnos.FrameDesGru(this);
                if(matricula!=null){
                    if(!((alumnos.FrameDesGru)ventana).cargaDatosAlumno(matricula)){
                        return;
                    }
                }
            }
            else if(tipo==4){
                ventana=new alumnos.FrameAsesoria(this);
                if(matricula!=null){ 
                    if(!((alumnos.FrameAsesoria)ventana).cargaDatosAlumno(matricula)){
                        return;
                    }
                }
            }
            agregaVentana(ventana);
        }        
    }
    
    /** Agrega una frame de reportes
     * @param tipo el tipo de reporte donde:
     *   1=de un grupo, 2=de una materia
     * @param clave si es de un grupo o materia su clave o null para general.
     */
    public void agregaVentanaReporte(int tipo, String clave){
        FrameReporte reporte=null;
        String titulo="";
        if(tipo==1){ titulo="Reportes de grupo"+(clave!=null?" "+clave:""); }
        else if(tipo==2){ titulo="Reportes de materia"+(clave!=null?" "+clave:""); }
        if(!existeVentana(titulo,true)){
            reporte=new FrameReporte(this,tipo);
            if(clave!=null){
                if(tipo==1 && !reporte.cargaReportesGrupo(clave)){
                    return;
                }
                else if(tipo==2 && !reporte.cargaReportesMateria(clave)){
                    return;
                }
            }
            else if(reporte.getError()!=null){
                return;
            }
            agregaVentana(reporte);
        }        
    }
    
    /** Agrega una ventana interna
     * @param ventana La ventana a agregar
     */
    public void agregaVentana(ModeloFrameInterno ventana){
        javax.swing.JMenuItem mnu=ventana.getMenu();
        mnu.setActionCommand(ventana.getTitle());
        mnu.addActionListener(this);
        mnuVentana.add(mnu);
        mnuVentana.setEnabled(true);
        jdpEscritorio.add(ventana);
        ventana.setVisible(true);
        ventana.empaqueta();
    }

    /** Abre la ventana para busqueda de alumnos
     * @param evt El ActionEvent que genero el evento
     */
    private void mnuBusAlActionPerformed(java.awt.event.ActionEvent evt) {//GEN-FIRST:event_mnuBusAlActionPerformed
        if(!existeVentana("Búsqueda de alumnos",true)){
            agregaVentana(new alumnos.FrameBusca(this));
        }
    }//GEN-LAST:event_mnuBusAlActionPerformed

    /** Llama al metodo para terminar la aplicacion
     * @param evt El ActionEvent que genero el evento
     */
    private void mnuSalirActionPerformed(java.awt.event.ActionEvent evt) {//GEN-FIRST:event_mnuSalirActionPerformed
        cerrarAplicacion();
    }//GEN-LAST:event_mnuSalirActionPerformed

    /** Llama al metodo para terminar la aplicacion
     * @param evt El ActionEvent que genero el evento
     */
    private void formWindowClosing(java.awt.event.WindowEvent evt) {//GEN-FIRST:event_formWindowClosing
        cerrarAplicacion();        
    }//GEN-LAST:event_formWindowClosing

    /** Llama al metodo que agrea una ventana de desepenio academico de un alumno
     * @param evt El ActionEvent que genero el evento
     */
    private void mnuRegDAActionPerformed(java.awt.event.ActionEvent evt) {//GEN-FIRST:event_mnuRegDAActionPerformed
        agregaVentanaAlumno(null,2);
}//GEN-LAST:event_mnuRegDAActionPerformed

    /** Llama al metodo que agrea una ventana de desempenio en grupo de un alumno
     * @param evt El ActionEvent que genero el evento
     */
    private void mnuRegDGActionPerformed(java.awt.event.ActionEvent evt) {//GEN-FIRST:event_mnuRegDGActionPerformed
        agregaVentanaAlumno(null,3);
}//GEN-LAST:event_mnuRegDGActionPerformed

    /** Abre la ventan de conexion al servidor
     * @param evt El ActionEvent que genero el evento
     */
    private void mnuConexActionPerformed(java.awt.event.ActionEvent evt) {//GEN-FIRST:event_mnuConexActionPerformed
        if(!existeVentana("Conexión al servidor",true)){
            agregaVentana(new FrameConexion(this));
        }
    }//GEN-LAST:event_mnuConexActionPerformed

    /** Abre la ventana para registrar nuevos grupos
     * @param evt El ActionEvent que genero el evento
     */
    private void mnuNewGrupoActionPerformed(java.awt.event.ActionEvent evt) {//GEN-FIRST:event_mnuNewGrupoActionPerformed
        if(!existeVentana("Crear nuevo grupo",true)){
            agregaVentana(new grupos.FrameAltas(this));
        }
    }//GEN-LAST:event_mnuNewGrupoActionPerformed

    /** Abre la ventana acerca de
     * @param evt El ActionEvent que genero el evento
     */
    private void mnuAcercaDeActionPerformed(java.awt.event.ActionEvent evt) {//GEN-FIRST:event_mnuAcercaDeActionPerformed
        if(!existeVentana("Acerca de",true)){
            agregaVentana(new FrameAcerca(this));
        }
}//GEN-LAST:event_mnuAcercaDeActionPerformed

    /** Llama al evento que abre una ventana de ficha de grupo
     * @param evt El ActionEvent que genero el evento
     */
    private void mnuFichGruActionPerformed(java.awt.event.ActionEvent evt) {//GEN-FIRST:event_mnuFichGruActionPerformed
        agregaVentanaGrupo(null,1);
    }//GEN-LAST:event_mnuFichGruActionPerformed

    /** Abre la ventana de busqueda de grupos
     * @param evt El ActionEvent que genero el evento
     */
    private void mnuBuscGrupoActionPerformed(java.awt.event.ActionEvent evt) {//GEN-FIRST:event_mnuBuscGrupoActionPerformed
        if(!existeVentana("Búsqueda de grupos",true)){
            agregaVentana(new grupos.FrameBusca(this));
        }
    }//GEN-LAST:event_mnuBuscGrupoActionPerformed

    /** Abre la ventan de preferencias del sistema
     * @param evt El ActionEvent que genero el evento
     */
    private void mnuPrefActionPerformed(java.awt.event.ActionEvent evt) {//GEN-FIRST:event_mnuPrefActionPerformed
        if(!existeVentana("Preferencias",true)){
            agregaVentana(new FramePreferencias(this));
        }
    }//GEN-LAST:event_mnuPrefActionPerformed

    /** Abre la ventan de conexion al servidor
     * @param evt El ActionEvent que genero el evento
     */
    private void lblConMouseClicked(java.awt.event.MouseEvent evt) {//GEN-FIRST:event_lblConMouseClicked
        if(!existeVentana("Conexión al servidor",true)){
            agregaVentana(new FrameConexion(this));
        }
    }//GEN-LAST:event_lblConMouseClicked

    /** Llama al evento que abre la ventana de calendario de un grupo
     * @param evt El ActionEvent que genero el evento
     */
    private void mnuCalActionPerformed(java.awt.event.ActionEvent evt) {//GEN-FIRST:event_mnuCalActionPerformed
        agregaVentanaGrupo(null,2);
    }//GEN-LAST:event_mnuCalActionPerformed

    /**  Llama al evento que abre la ventana de asesorias de un alumno tesista
     * @param evt El ActionEvent que genero el evento
     */
    private void mnuAsesoriasActionPerformed(java.awt.event.ActionEvent evt) {//GEN-FIRST:event_mnuAsesoriasActionPerformed
        agregaVentanaAlumno(null,4);
    }//GEN-LAST:event_mnuAsesoriasActionPerformed

    /** Abre la ventana de datos del docente
     * @param evt El ActionEvent que genero el evento
     */
    private void mnuDtsDocActionPerformed(java.awt.event.ActionEvent evt) {//GEN-FIRST:event_mnuDtsDocActionPerformed
        if(!existeVentana("Datos del docente",true)){
            agregaVentana(new FrameDocente(this));
        }
}//GEN-LAST:event_mnuDtsDocActionPerformed

    /** Abre la ventana de opciones de seguridad del sistema
     * @param evt El ActionEvent que genero el evento
     */
    private void mnuSeguridadActionPerformed(java.awt.event.ActionEvent evt) {//GEN-FIRST:event_mnuSeguridadActionPerformed
        if(!existeVentana("Seguridad",true)){
            agregaVentana(new FrameSeguridad(this));
        }
    }//GEN-LAST:event_mnuSeguridadActionPerformed

    /** Abre la ventana de ayuda del sistema
     * @param evt El ActionEvent que genero el evento
     */
    private void mnuManualActionPerformed(java.awt.event.ActionEvent evt) {//GEN-FIRST:event_mnuManualActionPerformed
        if(ayuda==null) ayuda = new FrameAyuda();
        if(ayuda.getExtendedState()==javax.swing.JFrame.ICONIFIED) 
            ayuda.setExtendedState(javax.swing.JFrame.NORMAL);
        ayuda.setVisible(true);
    }//GEN-LAST:event_mnuManualActionPerformed

    /** Llama al metodo que abre la ventana de avisos del sistema
     * @param evt El MouseEvent que genero el evento
     */
    private void lblAvisosMouseClicked(java.awt.event.MouseEvent evt) {//GEN-FIRST:event_lblAvisosMouseClicked
        muestraAvisos(true);
    }//GEN-LAST:event_lblAvisosMouseClicked

    /** Llama el evento que abre una ventana de ficha bibliografica nueva
     * @param evt El ActionEvent que genero el evento
     */
    private void mnuNuevaFichaBibActionPerformed(java.awt.event.ActionEvent evt) {//GEN-FIRST:event_mnuNuevaFichaBibActionPerformed
        agregaFichaBiliografica(0,0);
    }//GEN-LAST:event_mnuNuevaFichaBibActionPerformed

    /** Abre la ventana de busqueda de fichas bibliograficas
     * @param evt El ActionEvent que genero el evento
     */
    private void mnuBuscaFichBibActionPerformed(java.awt.event.ActionEvent evt) {//GEN-FIRST:event_mnuBuscaFichBibActionPerformed
        if(!existeVentana("Buscar ficha bibliográfica",true)){
            agregaVentana(new bibliografia.FrameBusca(this));
        }
    }//GEN-LAST:event_mnuBuscaFichBibActionPerformed

    /** Llama al evento que abre una ventana de evaluaciones programadas de un grupo
     * @param evt El ActionEvent que genero el evento
     */
    private void mnuRubEvalActionPerformed(java.awt.event.ActionEvent evt) {//GEN-FIRST:event_mnuRubEvalActionPerformed
        agregaVentanaGrupo(null,3);
}//GEN-LAST:event_mnuRubEvalActionPerformed

    /** Llama el evento que abre una ventana de reportes de un grupo
     * @param evt El ActionEvent que genero el evento
     */
    private void mnuRepGrupoActionPerformed(java.awt.event.ActionEvent evt) {//GEN-FIRST:event_mnuRepGrupoActionPerformed
         agregaVentanaReporte(1,null);
    }//GEN-LAST:event_mnuRepGrupoActionPerformed

    /** Abre la ventana de horario semanal vigente
     * @param evt El ActionEvent que genero el evento
     */
    private void mnuHoraSemActionPerformed(java.awt.event.ActionEvent evt) {//GEN-FIRST:event_mnuHoraSemActionPerformed
        if(!existeVentana("Horario semanal vigente",true)){
            agregaVentana(new FrameHorarioCiclo(this));
        }
}//GEN-LAST:event_mnuHoraSemActionPerformed

    /** Llama el evento que abre una ventana de reportes de una materia
     * @param evt El ActionEvent que genero el evento
     */
    private void mnuRepMateriaActionPerformed(java.awt.event.ActionEvent evt) {//GEN-FIRST:event_mnuRepMateriaActionPerformed
        agregaVentanaReporte(2,null);
    }//GEN-LAST:event_mnuRepMateriaActionPerformed
            
    /** indica si el jdesktoppane tiene acutalmente una determinada ventana hija
     * @param tit Titulo de la ventana a buscar 
     * @param actual Si es true y si existe la ventana buscada la trae al frente y la hace actual
     * @return true si la ventana buscada existe false en caso contrario
     */
    public boolean existeVentana(String tit, boolean actual){
        boolean bandera=false;
        javax.swing.JInternalFrame[] ventanas = jdpEscritorio.getAllFrames();
        for(int i=0;i<ventanas.length;i++)
            if(ventanas[i].getTitle().equals(tit)){
                if(actual){
                    try{
                        ventanas[i].setMaximum(true);
                        ventanas[i].pack();
                        ((ModeloFrameInterno)ventanas[i]).empaqueta();
                        ventanas[i].moveToFront();
                        ventanas[i].setSelected(true);
                    }
                    catch(java.beans.PropertyVetoException ex1){}
                }                
                bandera=true;
                break;
            }
        return bandera;
    }
      
    /** Obtiene una referencia a una ventana hija
     * @param tit El titulo de la ventana a obtener
     * @return la referencia a la ventana obtenida o null si no existe
     */
    public ModeloFrameInterno obtenVentana(String tit){
        javax.swing.JInternalFrame[] ventanas = jdpEscritorio.getAllFrames();
        ModeloFrameInterno ventana=null;
        for(int i=0;i<ventanas.length;i++)
            if(ventanas[i].getTitle().equals(tit)){
                ventana=(ModeloFrameInterno)ventanas[i];
                break;
            }
        return ventana;
    }
    
    /** Cierra una ventana hija
     * @param tit El titulo de la ventana a cerrar
     */
    public void cerrarVentana(String tit){
        for(int i=0;i<mnuVentana.getItemCount();i++){
            javax.swing.JMenuItem jmnuTmp = mnuVentana.getItem(i);
            if(jmnuTmp.getText().equals(tit)){
                mnuVentana.remove(jmnuTmp);
                break;
            }
        }
        mnuVentana.setEnabled(mnuVentana.getItemCount()!=0);
    }
    
    /** Envia la impresion recibida al dialogo de impresion
     * @param tit Titulo del reporte a imprimir
     * @param tipoDoc Tipo de reporte a imprimir
     *   donde: 1=lista de alumnos, 2=lista de creditos (calificacion o porcentaje de asistencia)
     *   3=calendario de sesiones de grupo 4=ficha de Registro desempeño academico de alumno
     *   5=ficha de Registro desempeño en grupo de alumno 6=Ficha bibliografica
     *   7=ficha hemeroteca general 8=ficha hemeroteca analitica 
     *   9=reporte de datos de tesista (datos y calendarios de sesiones)
     *   10=horario por dias de semana, 11=Temario
     * @param params parametros del documento a imprimir
     * @param lista Fuente de datos a imprimir (para los campos detail del formulario)
     */
    public void enviarImpresion(String tit,int tipoDoc,Map<String,String> params,ArrayList lista){
        String[] prefs=cargaPrefEst();
        String logo=null;
        if(prefs!=null&&!prefs[5].equals("")) logo=prefs[5];
        if(impres==null) impres=new FrameImprimir(this,true);
        if(impres.setImpresion(tit, tipoDoc, params, lista,logo)){
            impres.setLocationRelativeTo(this);
            impres.setVisible(true);
            if(impres.getResultado()==-1)
                mensaje("No se pudo imprimir","Ocurrio un error: "+impres.getError(),TipoMensaje.INFORMACION);
        }
        else
            mensaje("No se pudo imprimir","Ocurrio un error: "+impres.getError(),TipoMensaje.INFORMACION);
    }
  
    /** Obtiene el size del jdesktop de esta ventana
     * @return el size del jdesktop de esta ventana
     */
    public java.awt.Dimension getTamJDesktop(){
        return jdpEscritorio.getSize();
    }
    
    /** Implementa el action de los jmenus con los titulos de las ventanas internas actuales la accion 
     *   siempre es hacer actual la ventana cuyo titulo corresponda al jmenu en cual se hizo click
     * @param actEve El ActionEvent que genero el eevento
     */
    public void actionPerformed(ActionEvent actEve){
        String com = actEve.getActionCommand();
        existeVentana(com, true);
    }
  
    /** Revisa si hay ventanas internas con cambios sin guardar y en caso de ser asi
     *  pregunta si se desean los cambios, al guardarse o descartarse los cambios o 
     *  en caso de no haberlos cierra la ventana y termina la ejecucion del sistema
     */
    private void cerrarAplicacion(){  
        javax.swing.JInternalFrame[] ventanas = jdpEscritorio.getAllFrames();
        ModeloFrameInterno ventana;
        TipoRespuesta tpr;
        for(int i=0;i<ventanas.length;i++){
            ventana=((ModeloFrameInterno)ventanas[i]);
            if(ventana.hayCambios()){
                tpr=pideDesicion(ventana.getTitle(),"<html>Hay cambios sin guardar<br>¿Desea guardarlos antes de salir?</html>");
                if(tpr.getTipo()==TipoRespuesta.CANCELAR.getTipo()){
                    return;
                }
                else if(tpr.getTipo()==TipoRespuesta.ACEPTAR.getTipo()){
                    if(!ventana.guardaCambios()){
                        return;
                    }
                }
            }
        }
        if(ayuda!=null) ayuda.dispose();
        database.Conexion.desconecta();
        guardaPrefEst("ignora","");
        System.exit(0);
    }
   
    /** Obtiene la version actual del sistema ejecutandose
     * @return la version actual del sistema ejecutandose
     */
    public float getVersion(){ return SIS_VER; }
    
    /** Inicia la ejecucion del sistema
     * @param args los argumentos de la linea de comandos
     */
    public static void main(String args[]) {
        java.awt.EventQueue.invokeLater(new Runnable() { 
            public void run() {
                FramePrincipal principal = new FramePrincipal();
            }
        });
    }
    
    /** Clase que hereda de Thread e implementa un metodo para actualizar la hora del 
     *  reloj del sistema cada segundo y revisar la conexion al servidor
     */
    private class ActuHoraStatConx extends Thread{
        @Override
        public void run(){            
            while(true){
                lblHora.setText(Tiempo.getHora()+" ");
                lblFcha.setText(Tiempo.getFecha()+" ");
                lblCon.setIcon(iconos.Iconos.getIcono((database.Conexion.hayConexion()?"":"des")+"conecta.png"));
                lblCon.setToolTipText("Conexion al servidor "+(database.Conexion.hayConexion()?"":"in")+"activa");
                try{ Thread.sleep(1000); }
                catch(InterruptedException e){}
            }
        }
    }
    
    // Variables declaration - do not modify//GEN-BEGIN:variables
    private javax.swing.JMenuBar jMenuBar1;
    private javax.swing.JPanel jPanel1;
    private javax.swing.JDesktopPane jdpEscritorio;
    private javax.swing.JLabel lblAvisos;
    private javax.swing.JLabel lblCon;
    private javax.swing.JLabel lblFcha;
    private javax.swing.JLabel lblHora;
    private javax.swing.JLabel lblMens;
    private javax.swing.JMenuItem mnuAcercaDe;
    private javax.swing.JMenuItem mnuAltaAl;
    private javax.swing.JMenu mnuAlumnos;
    private javax.swing.JMenu mnuAlumnos2;
    private javax.swing.JMenuItem mnuAsesorias;
    private javax.swing.JMenu mnuAyuda;
    private javax.swing.JMenu mnuBiblioteca;
    private javax.swing.JMenuItem mnuBusAl;
    private javax.swing.JMenuItem mnuBuscGrupo;
    private javax.swing.JMenuItem mnuBuscaFichBib;
    private javax.swing.JMenuItem mnuCal;
    private javax.swing.JMenuItem mnuConex;
    private javax.swing.JMenuItem mnuDtsDoc;
    private javax.swing.JMenuItem mnuFichGru;
    private javax.swing.JMenu mnuGrupos;
    private javax.swing.JMenuItem mnuHoraSem;
    private javax.swing.JMenuItem mnuManual;
    private javax.swing.JMenuItem mnuNewGrupo;
    private javax.swing.JMenuItem mnuNuevaFichaBib;
    private javax.swing.JMenuItem mnuPref;
    private javax.swing.JMenuItem mnuRegDA;
    private javax.swing.JMenuItem mnuRegDG;
    private javax.swing.JMenuItem mnuRegPer;
    private javax.swing.JMenuItem mnuRepGrupo;
    private javax.swing.JMenuItem mnuRepMateria;
    private javax.swing.JMenu mnuReportes;
    private javax.swing.JMenuItem mnuRubEval;
    private javax.swing.JMenuItem mnuSalir;
    private javax.swing.JMenuItem mnuSeguridad;
    private javax.swing.JMenu mnuSistema;
    private javax.swing.JMenu mnuVentana;
    private javax.swing.JPanel statusBar;
    // End of variables declaration//GEN-END:variables
    
    /** Clase para atrapar excepciones inesperadas en tiempo de ejecucion*/
    private class catchUnhandledErrors implements Thread.UncaughtExceptionHandler{
        
        /**Referencia a la ventana principal del sistema*/
        private FramePrincipal father;
        
        /** Creaun nuevo catchUnhandledErrors
         * @param f Referencia a la ventana principal del sistema
         */
        public catchUnhandledErrors(FramePrincipal f){ father=f; }
       
        /** En caso de ocurrir una excepcion inesperada la atrapa y da la opcion de generar 
         *   un archivo con el informe de la misma
         * @param trd El thread donde ocurrio la excepcion
         * @param exc La excepcion ocurrida
         */
        public void uncaughtException(Thread trd, Throwable exc) {
            JFileChooser cajaArchivo;
            java.io.File archivo;
            TipoRespuesta res;
            String aux="<html>Ha ocurrido un error inesperado!!<br>Puede informar de errores inesperados";
            aux+="enviando un archivo de informe<br>a la direccion de correo electronico que aparece en Acerca de.";
            aux+="<br>¿Desea crear un archivo de informe de errores?</html>";
            res=pideDesicion("Error Inesperado!",aux);
            if(res.getTipo()!=TipoRespuesta.ACEPTAR.getTipo()) return;
            cajaArchivo= new JFileChooser();
            do{
                if(cajaArchivo.showSaveDialog(father)!=JFileChooser.APPROVE_OPTION) return;
                archivo=cajaArchivo.getSelectedFile();
                aux="<html>El archivo: "+archivo.getName()+" ya existe.<br>¿Desea sobreescribir el archivo?</html>";
            }while(archivo.isFile()&&pideDesicion("Atencion!",aux).getTipo()==TipoRespuesta.CANCELAR.getTipo());
            try{
                java.io.FileWriter arsale = new java.io.FileWriter(archivo);
                java.io.BufferedWriter bufw = new java.io.BufferedWriter(arsale);
                java.lang.StackTraceElement[] elementospila;
                elementospila=exc.getStackTrace();
                bufw.write(" <<- SisAdminDocente (V"+SIS_VER+") log de error ->>\n\tOcurrido en:\n");
                bufw.write(""+Tiempo.getFecha()+Tiempo.getHora());
                bufw.write("\nOcurrio un error definido: "+exc.toString()+"\n\tTrazado de pila:\n");
                for(int j=0;j<elementospila.length;j++) 
                    bufw.write(elementospila[j]+"\n");
                bufw.close();
                arsale.close();
                aux="El archivo de informe ha sido creado";
            }
            catch(Exception excLocal){
                aux="<html>No se pudo crear el archivo de informe<br>";
                aux+="Error: "+excLocal.getMessage()+"</html>";
            }
            mensaje("Atencion!",aux,TipoMensaje.INFORMACION);
         }
    }
    
}
