import React, { useCallback } from 'react';
import { LogoutOutlined } from '@ant-design/icons';
import { Avatar, Menu, Spin } from 'antd';
import { history } from 'umi';
import HeaderDropdown from '../HeaderDropdown';
import styles from './index.less';
import type { MenuInfo } from 'rc-menu/lib/interface';
import { useModel } from '@/.umi/plugin-model/useModel';

export type GlobalHeaderRightProps = {
  menu?: boolean;
};

/**
 * 退出登录，并且将当前的 url 保存
 */
const loginOut = async () => {
  // Note: There may be security issues, please note
  if (window.location.pathname !== '/user/login') {
    history.replace({
      pathname: '/user/login',
    });
  }
};

const AvatarDropdown: React.FC<GlobalHeaderRightProps> = ({ menu }) => {
  const { initialState, setInitialState } = useModel('@@initialState');

  const onMenuClick = useCallback(
    async (event: MenuInfo) => {
      const { key } = event;
      if (key === 'logout') {
        await initialState?.fetchUserInfo?.({
          accountId: 0,
          accountName: '',
          role: '',
        });
        setInitialState((s) => ({ ...s, currentUser: undefined }));
        loginOut();
        return;
      }
      history.push(`/user/login`);
    },
    [setInitialState],
  );
  const menuHeaderDropdown = (
    <Menu className={styles.menu} selectedKeys={[]} onClick={onMenuClick}>
      {/* {menu && (
        <Menu.Item key="center">
        <UserOutlined />
        个人中心
        </Menu.Item>
        )}
        {menu && (
          <Menu.Item key="settings">
          <SettingOutlined />
          个人设置
          </Menu.Item>
        )} */}
      <Menu.Item key="logout">
        <LogoutOutlined />
        {!initialState || !initialState.currentUser || !initialState.currentUser.accountName
          ? 'Sign in'
          : 'Logout'}
      </Menu.Item>
    </Menu>
  );
  const loading = (
    <HeaderDropdown overlay={menuHeaderDropdown}>
      <span className={`${styles.action} ${styles.account}`}>
        <Spin
          size="small"
          style={{
            marginLeft: 8,
            marginRight: 8,
          }}
        />
      </span>
    </HeaderDropdown>
  );
  if (!initialState) {
    return loading;
  }
  const { currentUser } = initialState;
  if (!currentUser || !currentUser.accountName) {
    return loading;
  }
  return (
    <HeaderDropdown overlay={menuHeaderDropdown}>
      <span className={`${styles.action} ${styles.account}`}>
        <Avatar
          size="large"
          className={styles.avatar}
          src={currentUser.avatar !== '' && currentUser.avatar ? currentUser.avatar : './user.png'}
          alt="avatar"
        />
        <span className={`${styles.name} anticon`}>{currentUser.accountName}</span>
      </span>
    </HeaderDropdown>
  );
};

export default AvatarDropdown;
