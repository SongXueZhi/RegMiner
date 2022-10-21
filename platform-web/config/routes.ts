export default [
  {
    path: '/user',
    layout: false,
    routes: [
      {
        path: '/user',
        routes: [
          {
            name: 'login',
            path: '/user/login',
            component: './user/Login',
          },
          {
            name: 'register',
            path: '/user/register',
            component: './user/Register',
          },
          {
            name: 'resetPassword',
            path: '/user/resetPassword',
            component: './user/ResetPassword',
          },
        ],
      },
    ],
  },
  {
    key: 'regression',
    name: 'regression',
    icon: 'table',
    path: '/regression',
    component: './regression',
  },
  {
    key: 'dashboard',
    name: 'dashboard',
    icon: 'Sync', 
    path: '/dashboard',
    component: './dashboard',
    access: 'userRouteFilter',
  },
  {
    key: 'deltaDebugging',
    name: 'deltaDebugging',
    icon: 'Control',
    path: '/deltaDebugging',
    component: './deltaDebugging',
    access: 'adminRouteFilter',
  },
  {
    key: 'editor',
    // name: 'editor',
    path: '/editor',
    // icon: 'smile',
    component: './editor',
  },
  // {
  //   key: 'detail',
  //   name: 'editor',
  //   path: '/detail',
  //   icon: 'smile',
  //   component: './detail',
  // },
  // {
  //   name: 'code',
  //   path: '/code',
  //   icon: 'smile',
  //   component: './diff',
  // },
  {
    path: '/',
    redirect: '/regression',
  },
  {
    component: './404',
  },
];
