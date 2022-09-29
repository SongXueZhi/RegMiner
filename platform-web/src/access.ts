/**
 * @see https://umijs.org/zh-CN/plugins/plugin-access
 * */
export default function access(initialState: { currentUser?: API.CurrentUser | undefined }) {
  const { currentUser } = initialState || {};
  return {
    canReadFoo: true,
    canUpdateFoo: currentUser?.role === 'admin',
    canClickFoo: currentUser?.role === 'admin',
    canDeleteFoo: currentUser?.role === 'admin',
    // canAdmin: currentUser && currentUser.accountRight === 0,
  };
}
