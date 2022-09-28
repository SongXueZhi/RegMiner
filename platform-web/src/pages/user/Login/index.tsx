import { LockOutlined, UserOutlined } from '@ant-design/icons';
import { Alert, Tabs, Image } from 'antd';
import React, { useState } from 'react';
import ProForm, { ProFormText } from '@ant-design/pro-form';
import { useIntl, history, FormattedMessage, SelectLang, useModel, Link } from 'umi';
import Footer from '@/components/Footer';
import { login } from '@/services/ant-design-pro/login';

import styles from './style.less';

const RegisterPath = '/user/register';

const LoginMessage: React.FC<{
  content: string;
}> = ({ content }) => (
  <Alert
    style={{
      marginBottom: 24,
    }}
    message={content}
    type="error"
    showIcon
  />
);

/** 此方法会跳转到 redirect 参数所在的位置 */
const goto = () => {
  if (!history) return;
  setTimeout(() => {
    history.push('/');
  }, 10);
};

const Login: React.FC = () => {
  const [submitting, setSubmitting] = useState(false);
  const [status, setStatus] = useState('');
  // const [type, setType] = useState<string>('account');
  const { initialState, setInitialState } = useModel('@@initialState');

  const intl = useIntl();

  const fetchUserInfo = async (user: API.CurrentUser) => {
    const userInfo = await initialState?.fetchUserInfo?.(user);
    console.log(userInfo);
    if (userInfo) {
      await setInitialState((s: any) => ({
        ...s,
        currentUser: userInfo,
      }));
    }
  };

  const handleSubmit = async (values: API.LoginParams) => {
    setSubmitting(true);
    try {
      // 登录
      const user: API.LoginResult = await login({ ...values });
      if (user) {
        await fetchUserInfo(user);
        goto();
        return;
      }
      setStatus('error');
    } catch (error) {}
    setSubmitting(false);
  };

  return (
    <div className={styles.container}>
      <div className={styles.lang}>{SelectLang && <SelectLang />}</div>
      <div className={styles.content}>
        <div className={styles.top}>
          <div className={styles.header}>
            <Link to="/">
              <Image alt="logo" src="/favicon.ico" className={styles.logo} />
              <span className={styles.title}>RegMiner Data Annotations</span>
            </Link>
          </div>
          <div className={styles.desc}>RegMiner 数据标注平台</div>
        </div>
        {JSON.stringify(initialState)}
        <div className={styles.main}>
          <ProForm
            initialValues={{
              autoLogin: false,
            }}
            submitter={{
              searchConfig: {
                submitText: intl.formatMessage({
                  id: 'pages.login.submit',
                  defaultMessage: 'Login',
                }),
              },
              render: (_, dom) => dom.pop(),
              submitButtonProps: {
                loading: submitting,
                size: 'large',
                style: {
                  width: '100%',
                },
              },
            }}
            onFinish={async (values) => {
              handleSubmit(values as API.LoginParams);
            }}
          >
            <Tabs activeKey="account">
              <Tabs.TabPane
                key="account"
                tab={intl.formatMessage({
                  id: 'pages.login.accountLogin.tab',
                  defaultMessage: 'Account Login',
                })}
              />
            </Tabs>
            {status === 'error' && (
              <LoginMessage
                content={intl.formatMessage({
                  id: 'pages.login.accountLogin.errorMessage',
                  defaultMessage: 'Incorrect username/password',
                })}
              />
            )}
            <ProFormText
              name="accountName"
              fieldProps={{
                size: 'large',
                prefix: <UserOutlined className={styles.prefixIcon} />,
              }}
              placeholder={intl.formatMessage({
                id: 'pages.login.username.placeholder',
                defaultMessage: 'Username',
              })}
              rules={[
                {
                  required: true,
                  message: (
                    <FormattedMessage
                      id="pages.login.username.required"
                      defaultMessage="Please input your username!"
                    />
                  ),
                },
              ]}
            />
            <ProFormText.Password
              name="password"
              fieldProps={{
                size: 'large',
                prefix: <LockOutlined className={styles.prefixIcon} />,
              }}
              placeholder={intl.formatMessage({
                id: 'pages.login.password.placeholder',
                defaultMessage: 'Password: ',
              })}
              rules={[
                {
                  required: true,
                  message: (
                    <FormattedMessage
                      id="pages.login.password.required"
                      defaultMessage="Please input your password!"
                    />
                  ),
                },
              ]}
            />
            <div>
              <a
                style={{
                  float: 'left',
                  marginBottom: 10,
                }}
                href={RegisterPath}
              >
                {/* <ProFormCheckbox noStyle name="autoLogin">
                <FormattedMessage id="pages.login.rememberMe" defaultMessage="自动登录" />
              </ProFormCheckbox> */}
                <FormattedMessage
                  id="pages.login.registerAccount"
                  defaultMessage="Register Account"
                />
              </a>
              <a
                style={{
                  float: 'right',
                  marginBottom: 10,
                }}
                onClick={() => console.log('forgot')}
              >
                <FormattedMessage
                  id="pages.login.forgotPassword"
                  defaultMessage="Forgot Password ?"
                />
              </a>
            </div>
          </ProForm>
        </div>
      </div>
      <Footer />
    </div>
  );
};

export default Login;
