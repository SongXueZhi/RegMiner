/**
 * @see https://umijs.org/zh-CN/plugins/plugin-access
 * */
export default function access(initialState: { currentUser?: API.CurrentUser | undefined }) {
  const {currentUser} = initialState || {};
  return {
    // noneUserFoo: true,
    // canUpdateFoo: currentUser?.role === 'admin',
    // canClickFoo: currentUser?.role === 'admin',
    // canDeleteFoo: currentUser?.role === 'admin',
    noneUserFoo: true,
    allUsersFoo: currentUser?.role === 'admin' || currentUser?.role === 'user',
    onlyAdminFoo: currentUser?.role === 'admin',
    adminRouteFilter: () => currentUser?.role === 'admin',
    userRouteFilter: () => currentUser?.role === 'admin' || currentUser?.role === 'user',
    // canAdmin: currentUser && currentUser.accountRight === 0,
  };
}
