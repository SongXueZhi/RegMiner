/**
 * @see https://umijs.org/zh-CN/plugins/plugin-access
 * */
export default function access(initialState: { currentUser?: API.CurrentUser | undefined }) {
  const { currentUser } = initialState || {};
  return {
    canReadFoo: true,
    canUpdateFoo: currentUser?.accountRight === 0 || currentUser,
    canClickFoo: currentUser?.accountRight === 0 || currentUser,
    canDeleteFoo: currentUser?.accountRight === 0 || currentUser,
    // canAdmin: currentUser && currentUser.accountRight === 0,
  };
}
