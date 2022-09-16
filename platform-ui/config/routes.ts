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
            name: 'register-result',
            path: '/user/register-result',
            component: './user/Register-result',
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
    icon: 'smile',
    path: '/dashboard',
    component: './dashboard',
  },
  {
    key: 'editor',
    // name: 'editor',
    path: '/editor',
    // icon: 'smile',
    component: './editor',
  },
  {
    key: 'detail',
    // name: 'editor',
    path: '/detail',
    // icon: 'smile',
    component: './detail',
  },
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
