import { Routes } from '@angular/router';

const routes: Routes = [
  {
    path: 'authority',
    data: { pageTitle: 'roomApp.adminAuthority.home.title' },
    loadChildren: () => import('./admin/authority/authority.routes'),
  },
  {
    path: 'perfil-usuario',
    data: { pageTitle: 'roomApp.perfilUsuario.home.title' },
    loadChildren: () => import('./perfil-usuario/perfil-usuario.routes'),
  },
  {
    path: 'documento-usuario',
    data: { pageTitle: 'roomApp.documentoUsuario.home.title' },
    loadChildren: () => import('./documento-usuario/documento-usuario.routes'),
  },
  {
    path: 'inmueble',
    data: { pageTitle: 'roomApp.inmueble.home.title' },
    loadChildren: () => import('./inmueble/inmueble.routes'),
  },
  {
    path: 'publicacion-inmueble',
    data: { pageTitle: 'roomApp.publicacionInmueble.home.title' },
    loadChildren: () => import('./publicacion-inmueble/publicacion-inmueble.routes'),
  },
  {
    path: 'multimedia-inmueble',
    data: { pageTitle: 'roomApp.multimediaInmueble.home.title' },
    loadChildren: () => import('./multimedia-inmueble/multimedia-inmueble.routes'),
  },
  {
    path: 'solicitud-arriendo',
    data: { pageTitle: 'roomApp.solicitudArriendo.home.title' },
    loadChildren: () => import('./solicitud-arriendo/solicitud-arriendo.routes'),
  },
  {
    path: 'visita-programada',
    data: { pageTitle: 'roomApp.visitaProgramada.home.title' },
    loadChildren: () => import('./visita-programada/visita-programada.routes'),
  },
  {
    path: 'contrato-arriendo',
    data: { pageTitle: 'roomApp.contratoArriendo.home.title' },
    loadChildren: () => import('./contrato-arriendo/contrato-arriendo.routes'),
  },
  {
    path: 'publicacion-roomie',
    data: { pageTitle: 'roomApp.publicacionRoomie.home.title' },
    loadChildren: () => import('./publicacion-roomie/publicacion-roomie.routes'),
  },
  {
    path: 'solicitud-roomie',
    data: { pageTitle: 'roomApp.solicitudRoomie.home.title' },
    loadChildren: () => import('./solicitud-roomie/solicitud-roomie.routes'),
  },
  {
    path: 'calificacion',
    data: { pageTitle: 'roomApp.calificacion.home.title' },
    loadChildren: () => import('./calificacion/calificacion.routes'),
  },
  {
    path: 'user-management',
    data: { pageTitle: 'userManagement.home.title' },
    loadChildren: () => import('./admin/user-management/user-management.routes'),
  },
  /* jhipster-needle-add-entity-route - JHipster will add entity modules routes here */
];

export default routes;
