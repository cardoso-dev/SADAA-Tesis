create table Alumno
 ( Matricula char(8) not null,
   ApPat varchar(25) not null,
   ApMat varchar(25) not null,
   Nom varchar(25) not null,
   FNac date not null,
   FIng date not null,
   Dom varchar(64) not null,
   Mun varchar(32) not null,
   TCasa char(10),
   TMovil char(10),
   CorreoE varchar(25) not null,
   Expectativas varchar(255),
   Pasatiempos varchar(255),
   MatPref varchar(255),
   MatNPref varchar(255),
   Observaciones varchar(255),
   foto Mediumblob,
   primary key(Matricula) )]

create table DatCo
 ( ClvDC int unsigned not null auto_increment,
   NomPad varchar(75),
   NomMad varchar(75),
   EscPadre varchar(25),
   EscMadre varchar(25),
   Emp varchar(25),
   NTHjs tinyint unsigned,
   NHrnas tinyint unsigned,
   NHrnos tinyint unsigned,
   POrdNac tinyint unsigned,
   MunOr varchar(25),
   DirPadres varchar(85),
   TPadres char(10),
   Prim varchar(85),
   PPrim decimal(4,2) unsigned,
   Sec varchar(85),
   PSec decimal(4,2) unsigned,
   Bachiller varchar(85),
   PromBach decimal(4,2) unsigned,
   OtCur varchar(255),
   primary key(ClvDC) )]

create table Tiene
 ( ClvDC int unsigned not null,
   Matricula char(8) not null,
   primary key(ClvDC, Matricula) )]

create table DesAca
 ( ClvDA int unsigned auto_increment not null,
   Materia varchar(45) not null,
   Periodo varchar(12) not null,
   Docente varchar(75) not null,
   Grado tinyint unsigned not null,
   Grupo varchar(4) not null,
   CalF decimal(4,2) not null,
   primary key(ClvDA) )]

create table Muestra
 ( ClvDA int unsigned not null,
   Matricula char(8) not null,
   primary key(ClvDA, Matricula) )]

create table Grupos
 ( ClvG varchar(19) not null,
   Grado tinyint unsigned not null,
   Grupo char(1) not null,
   PerIni date not null,
   PerFin date not null,
   Aula varchar(20),
   primary key(ClvG ) )]

create table Pertenece
 ( ClvPer int unsigned auto_increment not null,
   ClvG varchar(19) not null,
   Matricula char(8) not null,
   primary key(ClvPer) )]

create table Sesiones
 (  ClvSes integer unsigned auto_increment not null,
    FechaYHora datetime unique not null,
    Duracion smallint not null,
    Plan varchar(85) not null,
    Competencia varchar(255),
    Material varchar(255),
    Observaciones varchar(255),
    primary key(ClvSes) )]

create table ProgSes
 (  ClvSes int unsigned not null,
    ClvG varchar(19) not null,
    primary key(ClvSes,ClvG) )]

create table Asistencias
 (  ClvPer int unsigned not null,
    ClvSes int unsigned not null,
    Valor char(1) not null,
    primary key(ClvPer,ClvSes) )]

create table RubrosCalif
 ( ClvRU int unsigned auto_increment not null,
   Tipo tinyint not null,
   Descripcion varchar(65) not null,
   FchaSol date,
   FchaCal date not null,
   primary key(ClvRU)  )]

create table Realiza
 ( ClvPer int unsigned not null,
   ClvRU int unsigned not null,
   Calif decimal(4,2),
   ValorP tinyint,
   Observaciones varchar(255),
   primary key(ClvPer, ClvRU) )]

create table CalificaCon
 ( ClvRU int unsigned not null,
   ClvG varchar(19) not null )]

create table Tesis
 ( ClvT smallint unsigned auto_increment not null,
   Titulo varchar(95) not null,
   Tipo varchar(45) not null,
   FechaIni date not null,
   Colaboradores varchar(255),
   Sinodales varchar(255),
   primary key(ClvT) )]

create table SeTitula
 ( Matricula char(8) not null,
   ClvT smallint unsigned not null,
   primary key(Matricula,ClvT) )]

create table SesTesis
 ( ClvSesT int unsigned auto_increment not null,
   FechaYHora datetime unique not null,
   Tema varchar(105) not null,
   Lugar varchar(26),
   Observaciones varchar(255),
   primary key(ClvSesT) )]

create table ProgSesT
 ( Matricula char(8) not null,
   ClvT smallint unsigned not null,
   ClvSesT int unsigned not null,
   primary key(Matricula,ClvT,ClvSesT) )]

create table Materias
 ( ClvM varchar(3) not null,
   nombre varchar(45) not null,
   CalMin numeric(4,2) not null,
   primary key(ClvM) )]

create table Imparte
 ( ClvM varchar(3) not null,
   ClvG varchar(19) not null,
   VersTem int not null,
   primary key(ClvM,ClvG) )]

create table DatosDoc
 ( nombre varchar(32) not null,
   titulo varchar(65) not null )]
insert into DatosDoc values('Docente','Titulo')]

create table DatosSeg
 ( Altas boolean not null,
   Bajas boolean not null,
   Cambios boolean not null,
   Consultas boolean not null,
   Pass varchar(32) not null )]
insert into DatosSeg values(false,false,false,false,md5('password'))]

create table DtsPref
  ( Mostrar boolean not null,
    Asesorias boolean not null,
    DiasAse smallint not null,
    Clases boolean not null,
    DiasClase smallint not null )]
insert into DtsPref values(false,false,0,false,0)]

create table DatosInst
 ( NomInst varchar(65) not null,
   NomUnEsc varchar(65) not null,
   AreaProg varchar(65) not null )]
insert into DatosInst values('Institucion','Unidad academica','Area o programa')]

create table Temario
 ( ClvTem int unsigned not null auto_increment,
   version int not null,
   orden int not null,
   NumTem varchar(8) not null,
   TitTem varchar(65) not null,
   Conts varchar(255),
   primary key(ClvTem) )]

create table cubre
   ( ClvM varchar(3) not null,
     ClvTem int unsigned not null,
     primary key(ClvM,ClvTem) )]

create table veTema
   ( ClvTem int unsigned not null,
     ClvSes int unsigned not null,
     PorcenPlan int not null,
     primary key(ClvTem,ClvSes) )]

create table Objetivos
  ( ClvObj int unsigned not null auto_increment,
    Descrip varchar(255) not null,
    primary key(ClvObj) )]

create table busca
   ( ClvSes int unsigned not null,
     ClvObj int unsigned not null,
     primary key(ClvSes,ClvObj) )]

create table Resultados
  ( ClvRes int unsigned not null auto_increment,
    FechaYHora datetime not null,
    Duracion int not null,
    PorcenCub int not null,
    primary key(ClvRes) )]

create table obtiene
   ( ClvRes int unsigned not null,
     ClvObj int unsigned not null,
     primary key(ClvRes,ClvObj) )]

create table visto
   ( ClvRes int unsigned not null,
     ClvTem int unsigned not null,
     ClvSes int unsigned not null,
     primary key(ClvRes,ClvTem,ClvSes) )]

create table ActivPorDia
   ( ClvAD int unsigned not null auto_increment,
     HoraIni varchar(5) not null,
     HoraFin varchar(5) not null,
     dia smallint not null,
     Activ varchar(255) not null,
     primary key(ClvAD) )]

create table DiasGrupo
   ( ClvG varchar(19) not null,
     ClvAD int unsigned not null,
     primary key(ClvG,ClvAD) )]

create table DiasTesis
   ( ClvT int unsigned not null,
     ClvAD int unsigned not null,
     primary key(ClvT,ClvAD) )]

create table FBibl
 (  ClvFBib int unsigned not null auto_increment,
    Titulo varchar(125) not null,
    Autor varchar(75) not null,
    Clasificacion varchar(125) not null,
    Prologista varchar(75),
    Compilador varchar(75),
    Traductor varchar(75),
    NumEdit smallint unsigned not null,
    NomEdit varchar(75) not null,
    FchaEdit varchar(15),
    LugImp varchar(75),
    ColSer varchar(125),
    VolTom smallint unsigned,
    NumPgs int unsigned,
    RefLugCon varchar(255) not null,
    Resumen varchar(450),
    KeyWords varchar(125),
    primary key(ClvFBib) )]

create table FHeme
 (  ClvHem int unsigned not null auto_increment,
    Titulo varchar(125) not null,
    NomDir varchar(75) not null,
    Per varchar(75) not null,
    LugEd varchar(75) not null,
    Comentario varchar(450),
    KeyWords varchar(125),
    primary key(ClvHem) )]

create table FHemAn
 (  ClvHmAn int unsigned not null auto_increment,
    Titulo varchar(125) not null,
    Autor varchar(75) not null,
    Clasificacion varchar(125) not null,
    NomPub varchar(75) not null,
    Paginas varchar(15) not null,
    Numero smallint unsigned not null,
    Fcha varchar(15) not null,
    YearPub varchar(7) not null,
    NumTomVol int unsigned,
    Resumen varchar(450),
    primary key(ClvHmAn) )]

create procedure nuevoAlumno (in mat char(8), appat varchar(25), apmat varchar(25),
  nom varchar(25), fna date, fin date, dom varchar(64), mun varchar(32), tca char(10),
  tmo char(10), ce varchar(25), exp varchar(255), pas varchar(255), mpre varchar(255),
  mnpr varchar(255), obs varchar(255), fto Mediumblob, nmp varchar(75), nmm varchar(75),
  escp varchar(25), escm varchar(25), emp varchar(25), nhjs int, nhas int, nhos int,
  onac int, muno varchar(25), dirp varchar(85), telp char(10), prim varchar(85),
  pprim decimal(4,2), sec varchar(85), psec decimal(4,2), bach varchar(85),
  pbach decimal(4,2), ocur varchar(255))
begin
  declare bandera boolean default true;
  declare auxInt int default -1;
  start transaction;
  insert Alumno values(mat,appat,apmat,nom,fna,fin,dom,mun,tca,tmo,ce,exp,pas,mpre,mnpr,obs,fto);
  select row_count() into auxInt;
  if auxInt<>1 then
    set bandera=false;
  end if;
  insert DatCo values(DEFAULT,nmp,nmm,escp,escm,emp,nhjs,nhas,nhos,onac,muno,dirp,telp,prim,pprim,
    sec,psec,bach,pbach,ocur);
  select row_count() into auxInt;
  if auxInt<>1 then
    set bandera=false;
   else
    select last_insert_id() into auxInt;
    if auxInt<>0 then
      insert tiene values(auxInt,mat);
      select row_count() into auxInt;
      if auxInt<>1 then
        set bandera=false;
      end if;
     else
      set bandera=false;
    end if;
  end if;
  if bandera then
    commit;
    else
    rollback;
  end if;
end;]

create procedure actuRegAlumno (in mat char(8), appat varchar(25), apmat varchar(25),
  nom varchar(25), fna date, fin date, dom varchar(64), mun varchar(32), tca char(10),
  tmo char(10), ce varchar(25), exp varchar(255), pas varchar(255), mpre varchar(255),
  mnpr varchar(255), obs varchar(255), fto mediumblob, nmp varchar(75), nmm varchar(75),
  escp varchar(25), escm varchar(25), emp varchar(25), nhjs int, nhas int, nhos int,
  onac int, muno varchar(25), dirp varchar(85), telp char(10), prim varchar(85),
  pprim decimal(4,2), sec varchar(85), psec decimal(4,2), bach varchar(85),
  pbach decimal(4,2), ocur varchar(255), clavedc int, cmf boolean)
begin
  declare bandera boolean default true;
  declare auxInt int default -1;
  start transaction;
  if cmf=true then
    update Alumno set ApPat=appat, ApMat=apmat, Nom=nom, FNac=fna,FIng=fin, Dom=dom, Mun=mun, TCasa=tca,
      TMovil=tmo, CorreoE=ce, Expectativas=exp, Pasatiempos=pas, MatPref=mpre, MatNPref=mnpr,
      Observaciones=obs, foto=fto where matricula=mat;
    else
    update Alumno set ApPat=appat, ApMat=apmat, Nom=nom, FNac=fna,FIng=fin, Dom=dom, Mun=mun, TCasa=tca,
      TMovil=tmo, CorreoE=ce, Expectativas=exp, Pasatiempos=pas, MatPref=mpre, MatNPref=mnpr,
      Observaciones=obs where matricula=mat;
  end if;
  select row_count() into auxInt;
  if auxInt<>1 then
    set bandera=false;
  end if;
  update DatCo set NomPad=nmp, NomMad=nmm, EscPadre=escp, EscMadre=escm, Emp=emp, NTHjs=nhjs, NHrnas=nhas,
    NHrnos=nhos, POrdNac=onac, MunOr=muno, DirPadres=dirp, TPadres=telp, Prim=prim, PPrim=pprim,
    Sec=sec, PSec=psec, Bachiller=bach, PromBach=pbach, OtCur=ocur where ClvDC=clavedc;
  select row_count() into auxInt;
  if auxInt<>1 then
    set bandera=false;
  end if;
  if bandera then
    commit;
    else
    rollback;
  end if;
end;]

create procedure newRgDesAca (in matr char(8), mate varchar(45), per varchar(12), doc varchar(75),
    grad int, gru varchar(4), cal decimal(4,2), out clave int)
begin
  declare bandera boolean default true;
  declare auxInt int;
  start transaction;
  insert DesAca values(DEFAULT,mate,per,doc,grad,gru,cal);
  select row_count() into auxInt;
  if auxInt<>1 then
    set bandera=false;
    set clave=0;
   else
    select last_insert_id() into clave;
    insert muestra values(clave,matr);
    select row_count() into auxInt;
    if auxInt<>1 then
      set bandera=false;
      set clave=0;
    end if;
  end if;
  if bandera then
    commit;
   else
    rollback;
  end if;
end;]

create procedure nuevaSesion(in fch datetime, dur smallint, pln varchar(25),
  comp varchar(255), mat varchar(255), obs varchar(255), clg varchar(19), out clave int)
begin
  declare auxInt int;
  declare previo int;
  select count(FechaYHora) from Sesiones where FechaYHora=fch into previo;
  if previo>0 then
    set clave=0;
  else
    start transaction;
    insert Sesiones values(DEFAULT,fch,dur,pln,comp,mat,obs);
    select row_count() into auxInt;
    if auxInt<>1 then
      set clave=-1;
    else
      select last_insert_id() into clave;
      insert into ProgSes values(clave,clg);
      select row_count() into auxInt;
      if auxInt<>1 then
        set clave=-1;
      end if;
    end if;
    if clave<>-1 then
      commit;
    else
      rollback;
    end if;
  end if;
end;]

create procedure agAluGrupo(in mat varchar(8), clg varchar(19), out res int)
begin
  declare auxInt int;
  declare auxStr char(8);
  set res=1;
  select clvper into auxInt from Pertenece where ClvG=clg and Matricula=mat;
  if auxInt is not null then
    set res=0;
  end if;
  select matricula into auxStr from alumno where matricula=mat;
  if auxStr is null then
    set res=-1;
  end if;
  select grupo into auxStr from grupos where clvg=clg;
  if auxStr is null then
    set res=-2;
  end if;
  if res=1 then
    insert into Pertenece values(DEFAULT,clg,mat);
    select row_count() into auxInt;
    if auxInt<=0 then
      set res=-1;
    else
      set res=1;
    end if;
  end if;
end;]

create procedure newRgRubCalif (tp int, des varchar(65), clg varchar(19), fchas date, fchac date, out clave int)
begin
  declare bandera boolean default true;
  declare auxInt int;
  start transaction;
  insert RubrosCalif values(DEFAULT,tp,des,fchas,fchac);
  select row_count() into auxInt;
  if auxInt<>1 then
    set bandera=false;
    set clave=0;
   else
    select last_insert_id() into clave;
    insert into CalificaCon values(clave,clg);
  end if;
  if bandera then
    commit;
   else
    rollback;
  end if;
end;]

create procedure newTesis (in matr char(8), tit varchar(95), tp varchar(45), fcha date,
  col varchar(255), sin varchar(255), out clave int)
begin
  declare bandera boolean default true;
  declare auxInt int;
  start transaction;
  insert Tesis values(DEFAULT,tit,tp,fcha,col,sin);
  select row_count() into auxInt;
  if auxInt<>1 then
    set bandera=false;
    set clave=0;
   else
    select last_insert_id() into clave;
    insert SeTitula values(matr,clave);
    select row_count() into auxInt;
    if auxInt<>1 then
      set bandera=false;
      set clave=0;
    end if;
  end if;
  if bandera then
    commit;
   else
    rollback;
  end if;
end;]

create procedure newRgSesT(in matr char(8), clt int, fh datetime, tma varchar(105),
  lgr varchar(26), obs varchar(255), out clave int)
begin
  declare bandera boolean default true;
  declare auxInt int;
  declare previo int;
  select count(fechayhora) from sestesis where fechayhora=fh into previo;
  if previo>0 then
    set clave=0;
  else
    start transaction;
    insert SesTesis values(DEFAULT,fh,tma,lgr,obs);
    select row_count() into auxInt;
    if auxInt<>1 then
      set bandera=false;
      set clave=-1;
     else
     select last_insert_id() into clave;
      insert ProgSesT values(matr,clt,clave);
      select row_count() into auxInt;
      if auxInt<>1 then
        set bandera=false;
        set clave=-1;
      end if;
    end if;
    if bandera then
      commit;
     else
      rollback;
    end if;
  end if;
end;]

create procedure cambiaPreSeg (in alt boolean, baj boolean, cam boolean, con boolean,
  pss varchar(32), psn varchar(32), out res int)
begin
  declare auxInt int;
  select count(pass) into auxInt from datosseg where pass=md5(pss);
  if auxInt>0 then
    update datosseg set altas=alt, bajas=baj, cambios=cam, consultas=con, pass=md5(psn);
    set res=1;
  else
    set res=0;
  end if;
end;]

create procedure nuevaFichaBibGen(in tit varchar(125), aut varchar(75), cla varchar(125), pro varchar(75),
  cmp varchar(75), trd varchar(75), nued int, noed varchar(75), fch varchar(15), lim varchar(75), csr varchar(125),
  vtm int, npg int, lgc varchar(255), res varchar(450), kwd varchar(125), out clave int)
begin
  declare auxInt int;
  start transaction;
  insert FBibl values(DEFAULT,tit,aut,cla,pro,cmp,trd,nued,noed,fch,lim,csr,vtm,npg,lgc,res,kwd);
  select row_count() into auxInt;
  if auxInt<>1 then
    set clave=0;
   else
    select last_insert_id() into clave;
  end if;
  if clave<>0 then
    commit;
   else
    rollback;
  end if;
end;]

create procedure nuevaFichaHemGen(in tit varchar(125), dir varchar(75), per varchar(75), led varchar(75),
  com varchar(450), kwd varchar(125), out clave int)
begin
  declare auxInt int;
  start transaction;
  insert FHeme values(DEFAULT,tit,dir,per,led,com,kwd);
  select row_count() into auxInt;
  if auxInt<>1 then
    set clave=0;
   else
    select last_insert_id() into clave;
  end if;
  if clave<>0 then
    commit;
   else
    rollback;
  end if;
end;]

create procedure nuevaFichaHemAn(in tit varchar(125), aut varchar(75), cla varchar(125), nomp varchar(75), pgs varchar(15),
  num int, fch varchar(15), yep varchar(7), nvt int, res varchar(450), out clave int)
begin
  declare auxInt int;
  start transaction;
  insert FHemAn values(DEFAULT,tit,aut,cla,nomp,pgs,num,fch,yep,nvt,res);
  select row_count() into auxInt;
  if auxInt<>1 then
    set clave=0;
   else
    select last_insert_id() into clave;
  end if;
  if clave<>0 then
    commit;
   else
    rollback;
  end if;
end;]

create procedure nuevoTema(in clm varchar(3), ver int, ord int, ntm varchar(5), ttm varchar(65), con varchar(255), out clave int)
begin
  declare auxInt int;
  start transaction;
  insert Temario values(DEFAULT,ver,ord,ntm,ttm,con);
  select row_count() into auxInt;
  if auxInt<>1 then
    set clave=0;
   else
    select last_insert_id() into clave;
    insert into cubre values(clm,clave);
    select row_count() into auxInt;
    if auxInt<>1 then
      set clave=0;
    end if;
  end if;
  if clave<>0 then
    commit;
   else
    rollback;
  end if;
end;]

create procedure nuevoObjetivo(in cls int, descr varchar(255), out clave int)
begin
  declare auxInt int;
  start transaction;
  insert Objetivos values(DEFAULT,descr);
  select row_count() into auxInt;
  if auxInt<>1 then
    set clave=0;
   else
    select last_insert_id() into clave;
    insert into busca values(cls,clave);
    select row_count() into auxInt;
    if auxInt<>1 then
      set clave=0;
    end if;
  end if;
  if clave<>0 then
    commit;
   else
    rollback;
  end if;
end;]

create procedure nuevoResultado(in cl1 int, cl2 int, fch datetime, dur int, pcu int, out clave int)
begin
  declare auxInt int;
  start transaction;
  insert Resultados values(DEFAULT,fch,dur,pcu);
  select row_count() into auxInt;
  if auxInt<>1 then
    set clave=0;
   else
    select last_insert_id() into clave;
    if cl2=0 then
      insert into obtiene values(clave,cl1);
    else
      insert into Visto values(clave,cl1,cl2);
    end if;
    select row_count() into auxInt;
    if auxInt<>1 then
      set clave=0;
    end if;
  end if;
  if clave<>0 then
    commit;
   else
    rollback;
  end if;
end;]

create procedure nuevaActivDia(in clg varchar(19), clt int, hini varchar(5), hfin varchar(5), dia int, act varchar(255), out clave int)
begin
  declare auxInt int;
  start transaction;
  insert ActivPorDia values(DEFAULT,hini,hfin,dia,act);
  select row_count() into auxInt;
  if auxInt<>1 then
    set clave=0;
   else
    select last_insert_id() into clave;
    if clg is not null then
      insert into DiasGrupo values(clg,clave);
      select row_count() into auxInt;
    end if;
    if clt<>0 then
      insert into DiasTesis values(clt,clave);
      select row_count() into auxInt;
    end if;
    if auxInt<>1 then
      set clave=0;
    end if;
  end if;
  if clave<>0 then
    commit;
   else
    rollback;
  end if;
end;]

create trigger nuevoPertenece after insert
  on Pertenece for each row
  begin
    declare claveper int;
    declare claveses int;
    declare claverubcal int;
    declare hecho int default 0;
    declare hecho2 int default 0;
    declare crsrSes cursor for select Sesiones.ClvSes from Sesiones,ProgSes
      where Sesiones.ClvSes = ProgSes.ClvSes and ProgSes.ClvG = NEW.ClvG;
    declare crsrRub cursor for select RubrosCalif.ClvRU from RubrosCalif,CalificaCon
      where RubrosCalif.ClvRU = CalificaCon.ClvRU and CalificaCon.ClvG = NEW.ClvG;
    declare continue handler for sqlstate '02000' set hecho = 1;
    select ClvPer from Pertenece where Pertenece.ClvG = new.ClvG
      and Pertenece.Matricula = new.Matricula into claveper;
    open crsrSes;
    repeat
      fetch crsrSes into claveses;
      if not hecho then
        insert Asistencias values(claveper,claveses,'_');
      end if;
    until hecho
    end repeat;
    close crsrSes;
    set hecho=0;
    open crsrRub;
    repeat
      fetch crsrRub into claverubcal;
      if not hecho then
        insert Realiza values(claveper,claverubcal,0.0,0,null);
      end if;
    until hecho
    end repeat;
    close crsrRub;
  end;]

create trigger nuevoProgSes after insert
  on ProgSes for each row
  begin
    declare hecho int default 0;
    declare claveper int;
    declare crsrPer cursor for select ClvPer from Pertenece
      where Pertenece.ClvG = NEW.ClvG;
    declare continue handler for sqlstate '02000' set hecho = 1;
    open crsrPer;
    repeat
      fetch crsrPer into claveper;
      if not hecho then
        insert Asistencias values(claveper,new.ClvSes,'_');
      end if;
    until hecho
    end repeat;
    close crsrPer;
  end;]

create trigger nuevoCalifCon after insert
  on CalificaCon for each row
  begin
    declare hecho int default 0;
    declare claveper int;
    declare crsrPer cursor for select ClvPer from Pertenece
      where Pertenece.ClvG = NEW.ClvG;
    declare continue handler for sqlstate '02000' set hecho = 1;
    open crsrPer;
    repeat
      fetch crsrPer into claveper;
      if not hecho then
        insert Realiza values(claveper,new.ClvRU,0.0,0,null);
      end if;
    until hecho
    end repeat;
    close crsrPer;
  end;]

create trigger eliminaSesion after delete
  on Sesiones for each row
  begin
    declare hecho int default 0;
    declare claveobj int;
    declare crsrObj cursor for select ClvObj from Busca
      where Busca.ClvSes = OLD.ClvSes;
    declare continue handler for sqlstate '02000' set hecho = 1;
    open crsrObj;
    repeat
      fetch crsrObj into claveobj;
      if not hecho then
        delete from Objetivos where ClvObj=claveobj;
      end if;
    until hecho
    end repeat;
    close crsrObj;
    delete from Asistencias where Asistencias.ClvSes=old.ClvSes;
    delete from ProgSes where ProgSes.ClvSes=old.ClvSes;
    delete from VeTema where VeTema.ClvSes=old.ClvSes;
  end;]

create trigger eliminaRubroCalif after delete
  on RubrosCalif for each row
  begin
    delete from Realiza where Realiza.ClvRU=old.ClvRU;
    delete from CalificaCon where CalificaCon.ClvRU=old.ClvRU;
  end;]

create trigger eliminaPertenece after delete
  on Pertenece for each row
  begin
    delete from Realiza where clvper=old.clvper;
    delete from Asistencias where clvper=old.clvper;
  end;]

create trigger eliminaGrupo after delete
  on Grupos for each row
  begin
    declare hecho int default 0;
    declare clavead int;
    declare crsrAD cursor for select ClvAD from DiasGrupo
      where DiasGrupo.ClvG = OLD.ClvG;
    declare continue handler for sqlstate '02000' set hecho = 1;
    open crsrAD;
    repeat
      fetch crsrAD into clavead;
      if not hecho then
        delete from ActivPorDia where ClvAD=clavead;
      end if;
    until hecho
    end repeat;
    close crsrAD;
    delete from Pertenece where clvg=old.clvg;
    delete from Imparte where clvg=old.clvg;
  end;]

create trigger eliminaSeTitula after delete
  on SeTitula for each row
  begin
    delete from Tesis where clvt=old.clvt;
    delete ProgSesT,SesTesis from ProgSesT,SesTesis where
      ProgSesT.matricula=old.matricula and ProgSesT.Clvt=old.ClvT
      and SesTesis.ClvSesT=ProgSesT.ClvSesT;
  end;]

create trigger eliminaObj after delete
  on Objetivos for each row
  begin
    declare hecho int default 0;
    declare claveres int;
    declare crsrObr cursor for select ClvRes from obtiene
      where obtiene.ClvObj = OLD.ClvObj;
    declare continue handler for sqlstate '02000' set hecho = 1;
    open crsrObr;
    repeat
      fetch crsrObr into claveres;
      if not hecho then
        delete from Resultados where Resultados.ClvRes=claveres;
      end if;
    until hecho
    end repeat;
    close crsrObr;
    delete from busca where busca.ClvObj=old.ClvObj;
  end;]

create trigger eliminaVeTema after delete
  on VeTema for each row
  begin
    declare hecho int default 0;
    declare claveres int;
    declare crsrObr cursor for select ClvRes from Visto
      where Visto.ClvTem = OLD.ClvTem and Visto.ClvSes = OLD.ClvSes;
    declare continue handler for sqlstate '02000' set hecho = 1;
    open crsrObr;
    repeat
      fetch crsrObr into claveres;
      if not hecho then
        delete from Resultados where Resultados.ClvRes=claveres;
      end if;
    until hecho
    end repeat;
    close crsrObr;
  end;]

create trigger eliminaResultado after delete
  on Resultados for each row
  begin
    delete from obtiene where obtiene.ClvRes=old.ClvRes;
    delete from Visto where visto.ClvRes=old.ClvRes;
  end;]

create trigger elimninaMate after delete
  on Materias for each row
  begin
    declare hecho int default 0;
    declare clavetem int;
    declare crsrTem cursor for select ClvTem from cubre
      where cubre.ClvM = OLD.ClvM;
    declare continue handler for sqlstate '02000' set hecho = 1;
    open crsrTem;
    repeat
      fetch crsrTem into clavetem;
      if not hecho then
        delete from Temario where Temario.ClvTem=clavetem;
      end if;
    until hecho
    end repeat;
    close crsrTem;
    delete from imparte where imparte.ClvM=old.ClvM;
  end;]

create trigger eliminaTema after delete
  on Temario for each row
  begin
    delete from cubre where cubre.ClvTem=old.ClvTem;
    delete from veTema where veTema.ClvTem=old.ClvTem;
  end;]

create trigger eliminaTesis after delete
  on Tesis for each row
  begin
    declare hecho int default 0;
    declare clavead int;
    declare crsrAD cursor for select ClvAD from DiasTesis
      where DiasTesis.ClvT = OLD.ClvT;
    declare continue handler for sqlstate '02000' set hecho = 1;
    open crsrAD;
    repeat
      fetch crsrAD into clavead;
      if not hecho then
        delete from ActivPorDia where ClvAD=clavead;
      end if;
    until hecho
    end repeat;
    close crsrAD;
  end;]

create trigger eliminaActivPorDia after delete
  on ActivPorDia for each row
  begin
    delete from DiasTesis where DiasTesis.ClvAD=OLD.ClvAD;
    delete from DiasGrupo where DiasGrupo.ClvAD=OLD.ClvAD;
  end;]